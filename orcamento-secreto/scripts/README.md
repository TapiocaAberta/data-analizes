Scripts para auxiliar o ETL
--


Nesse diretório você encontra scripts para auxiliar o processo de ETL ou corrigir falhas. 

Scripts até o momento:

* EmendasSemDocumentos.java: Lista arquivos de emendas que estão sem documentos. O processo de baixar documentos é lento e as vezes há falhas. Esse script lista todos arquivos de emenda sem documentos. A saída é salva em `emendas_sem_documentos.txt`
* BaixaDocumentoDeEmenda.java: Baixa os documentos para uma emenda específica. Como argumento recebe o caminho de um arquivo da emenda. Ele funciona com o EmendasSemDocumentos, onde um arquivo que lista emenda sem documento é gerado, assim é possível chamar esse script usando todos os cores da máquina:
```
<emendas_sem_documentos.txt xargs -P 0 -I % ./BaixaDocumentoDeEmenda.java %
```



