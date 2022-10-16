# Programa auxiliar para buscar dados do CNPJ

Esse programa gera os dados do diretório `orcamento-secreto/data-cnpj/` com a seguinte lógica:

1. Procura todos os arquivos JSON no diretório do repositório (ignorando `orcamento-secreto/data-cnpj/`)
2. Dentro de cada arquivo JSON, busca todos os CNPJs utilizando uma expressão regular
3. Faz requisições para a [Minha Receita](http://docs.minhareceita.org) para baixar os dados de cada CNPJ (o programa ignora CNPJs já salvos em  `orcamento-secreto/data-cnpj/` a não ser se o arquivo existente tiver sido criado há mais de um mês)
4. Salva esses dados em arquivos JSON no novo `orcamento-secreto/data-cnpj/`

## Modo de usar

Com o terminal aberto nesse diretório `contrib/busca-cnpj/`:

```console
$ go run main.go
```
