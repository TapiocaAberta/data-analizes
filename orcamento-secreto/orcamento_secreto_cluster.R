install.packages("stringr")
library(dplyr)
library(stringr)

emendas <- read.csv(file = "data/emendas.csv", sep = ";")
emendas <- emendas[,1:9]
colnames(emendas) <- c("ano", "autor", "emenda", "localidade", "função", "subfunção", 
                       "empenhado", "liquido", "pago")

emendas$ano = as.factor(emendas$ano)
emendas$autor = as.factor(emendas$autor)
emendas$emenda = as.factor(emendas$emenda)
emendas$função = as.factor(emendas$função)
emendas$localidade = as.factor(emendas$localidade)
emendas$subfunção = as.factor(emendas$subfunção)

emendas$empenhado <- str_replace_all(emendas$empenhado, "\\.", "")
emendas$empenhado <- as.double(str_replace_all(emendas$empenhado, ",", "."))

emendas$liquido <- str_replace_all(emendas$liquido, "\\.", "")
emendas$liquido <- as.double(str_replace_all(emendas$liquido, ",", "."))

emendas$pago <- str_replace_all(emendas$pago, "\\.", "")
emendas$pago <- as.double(str_replace_all(emendas$pago, ",", "."))

save(emendas, file = "data/emendas_2020_2022.RData")
write.csv(emendas,"data/emendas_2020_2022.csv", row.names = FALSE)

summary(emendas)

