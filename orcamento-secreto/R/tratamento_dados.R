install.packages("stringr")
library(dplyr)
library(stringr)

emendas <- read.csv(file = "../data/emendas.csv", sep = ";", colClasses =  "factor")
emendas <- emendas[,1:9]
colnames(emendas) <- c("ano", "autor", "numero_emenda", "localidade", "função", "subfunção", 
                       "empenhado", "liquido", "pago")

emendas$id <- as.factor(sample(nrow(emendas)))

emendas$empenhado <- str_replace_all(emendas$empenhado, "\\.", "")
emendas$empenhado <- as.double(str_replace_all(emendas$empenhado, ",", "."))

emendas$liquido <- str_replace_all(emendas$liquido, "\\.", "")
emendas$liquido <- as.double(str_replace_all(emendas$liquido, ",", "."))

emendas$pago <- str_replace_all(emendas$pago, "\\.", "")
emendas$pago <- as.double(str_replace_all(emendas$pago, ",", "."))

emendas$codigo_emenda <- as.factor(paste(emendas$ano, "8100", emendas$numero_emenda, sep = ""))

save(emendas, file = "../data/emendas_2020_2022.RData")
write.csv(emendas,"../data/emendas_2020_2022.csv", row.names = FALSE)

summary(emendas)

