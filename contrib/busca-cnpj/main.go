package main

import (
	"fmt"
	"io"
	"io/fs"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"regexp"
	"strings"
	"time"
)

const parallelRequests = 512

var (
	dir       = filepath.Join("orcamento-secreto", "data-cnpj")
	cnpj      = regexp.MustCompile(`\d{2}\.?\d{3}\.?\d{3}/?\d{4}-?\d{2}`)
	nonDigits = regexp.MustCompile(`\D`)
)

func findRepoRoot() (string, error) {
	root := "."
	var err error
	for {
		root, err = filepath.Abs(root)
		if err != nil {
			return "", fmt.Errorf("error getting absolute path for %s: %w", root, err)
		}
		_, err = os.Open(filepath.Join(root, ".git"))
		if os.IsNotExist(err) {
			root = filepath.Dir(root)
			continue
		}
		if err != nil {
			return "", fmt.Errorf("error finding the repository root: %w", err)
		}
		break
	}
	return root, nil
}

func findJSONs(path string) ([]string, error) {
	var ls []string
	filepath.WalkDir(path, func(p string, d fs.DirEntry, err error) error {
		if !strings.Contains(p, "data-cnpj") && filepath.Ext(p) == ".json" {
			ls = append(ls, p)
		}
		return nil
	})
	if len(ls) == 0 {
		return []string{}, fmt.Errorf("no json file found in %s", path)
	}
	return ls, nil
}

func skipCNPJ(path string) bool {
	f, err := os.Open(path)
	if os.IsNotExist(err) {
		return false
	}
	if err != nil {
		return false
	}
	i, err := f.Stat()
	if err != nil {
		return false
	}
	return i.ModTime().After(time.Now().Add(-24 * 30 * time.Hour))
}

func findCNPJs(ls []string) ([]string, error) {
	cnpjs := make(map[string]struct{})
	q := make(chan string)
	errs := make(chan error)
	results := make(chan string)
	done := make(chan struct{})
	defer func() {
		close(q)
		close(errs)
		close(results)
		close(done)
	}()
	for _, json := range ls {
		go func(j string) {
			defer func() { done <- struct{}{} }()
			txt, err := os.ReadFile(j)
			if err != nil {
				errs <- fmt.Errorf("error opening %s: %w", j, err)
				return
			}
			for _, c := range cnpj.FindAllString(string(txt), -1) {
				results <- c
			}
		}(json)
	}
	err := func() error {
		var t int
		for {
			select {
			case err := <-errs:
				return err
			case c := <-results:
				cnpjs[c] = struct{}{}
			case <-done:
				t += 1
				if t == len(ls) {
					return nil
				}
			}
		}
	}()
	if err != nil {
		return []string{}, err
	}
	var r []string
	for k := range cnpjs {
		r = append(r, k)
	}
	return r, nil
}

func pathForCNPJ(root, c string) string {
	c = nonDigits.ReplaceAllString(c, "")
	return filepath.Join(root, dir, c+".json")
}

func saveCNPJ(root string, c string) error {
	p := pathForCNPJ(root, c)
	r, err := http.Get("https://minhareceita.org/" + c)
	if err != nil {
		return fmt.Errorf("error getting data for %s: %w", c, err)
	}
	defer r.Body.Close()
	switch r.StatusCode {
	case http.StatusOK:
		break
	case http.StatusNotFound, http.StatusBadRequest:
		return nil
	default:
		return fmt.Errorf("got http status %d for %s: %w", r.StatusCode, c, err)
	}
	f, err := os.Create(p)
	if err != nil {
		return fmt.Errorf("error creating %s: %w", p, err)
	}
	defer f.Close()
	if _, err = io.Copy(f, r.Body); err != nil {
		return fmt.Errorf("error writing to %s: %w", p, err)
	}
	return nil
}

func worker(root string, q <-chan string, errs chan<- error) {
	for c := range q {
		errs <- saveCNPJ(root, c)
	}
}

func main() {
	root, err := findRepoRoot()
	if err != nil {
		log.Fatal(err)
	}
	log.Output(2, fmt.Sprintf("Buscando por arquivos JSON em %s…", root))
	jsons, err := findJSONs(root)
	if err != nil {
		log.Fatal(err)
	}
	log.Output(2, fmt.Sprintf("%d arquivos JSON encontrados", len(jsons)))
	cnpjs, err := findCNPJs(jsons)
	if err != nil {
		log.Fatal(err)
	}
	log.Output(2, fmt.Sprintf("%d CNPJs únicos encontrados", len(cnpjs)))
	var pending []string
	for _, c := range cnpjs {
		if skipCNPJ(pathForCNPJ(root, c)) {
			pending = append(pending, c)
		}
	}
	log.Output(2, fmt.Sprintf("%d novos CNPJs encontrados", len(pending)))
	if err := os.MkdirAll(dir, 0755); err != nil {
		log.Fatal("could not create directory cnpj")
	}
	log.Output(2, "Criando arquivos JSON para cara CNPJ…")
	errs := make(chan error)
	q := make(chan string)
	defer func() {
		close(errs)
		close(q)
	}()
	for i := 0; i < parallelRequests; i++ {
		go worker(root, q, errs)
	}
	for _, c := range pending {
		go func(c string) { q <- c }(c)
	}
	var t uint
	for range pending {
		if err := <-errs; err != nil {
			log.Fatal(err)
		}
		t += 1
		fmt.Printf("\r%d CNPJs salvos…", t)
	}
	fmt.Printf("\r")
	log.Output(2, "Feito!")
}
