library(dplyr)

### Tratamento de dados

cartao_corporativo <- read.csv(file = "../data/cartao_corporativo_bolsonaro.csv", sep = ",")

cartao_corporativo$VALOR <- as.double(cartao_corporativo$VALOR)
cartao_corporativo$SUBELEMENTO.DE.DESPESA <- as.factor(cartao_corporativo$SUBELEMENTO.DE.DESPESA)
cartao_corporativo$CPF.CNPJ.FORNECEDOR <- as.factor(cartao_corporativo$CPF.CNPJ.FORNECEDOR)
cartao_corporativo$CPF.SERVIDOR <- as.factor(cartao_corporativo$CPF.SERVIDOR)
cartao_corporativo$NOME.FORNECEDOR <- as.factor(cartao_corporativo$NOME.FORNECEDOR)
cartao_corporativo$DATA.PGTO <- as.Date(cartao_corporativo$DATA.PGTO)

save(cartao_corporativo, file = "../data/cartao_corporativo_bolsonaro.RData")
