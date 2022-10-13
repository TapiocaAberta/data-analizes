library(ggplot2)

set.seed(42)
load(file = "../data/emendas_2020_2022.RData")

ano_2020 <- emendas %>% dplyr::filter(ano == "2020")
ano_2021 <- emendas %>% dplyr::filter(ano == "2021")
ano_2022 <- emendas %>% dplyr::filter(ano == "2022")

s_2020 <- summary(ano_2020[c("localidade", "função", "subfunção", "empenhado", "liquido", "pago")], maxsum = 100)
summary(ano_2021[c("localidade", "função", "subfunção", "empenhado", "liquido", "pago")], maxsum = 100)
summary(ano_2022[c("localidade", "função", "subfunção", "empenhado", "liquido", "pago")], maxsum = 100)

##### GRAFICOS ######

localidades <- c("ALAGOAS", "AMAPÁ",  "CEARÁ", "Centro-Oeste", "GOIÁS", "MARANHÃO",          
  "MATO GROSSO",  "MATÕES DO NORTE",  "MINAS GERAIS", "Nacional", "Nordeste",          
  "Norte", "PARÁ", "PARAÍBA", "PERNAMBUCO", "PORTO VELHO",  "RIO GRANDE DO SUL", "SANTA CATARINA",    
  "Sudeste", "Sul",  "TIMON")

localidades_2020 <- data.frame(
  group=localidades,
  value=c(1,1,1,4,2,1,2,0,3,302,9,3,1,1,2,1,5,1,4,4,0)
)

ggplot(localidades_2020, aes(x="", y=value, fill=group)) +
  geom_bar(stat="identity", width=1, color="white") +
  coord_polar("y", start=0) +
  theme_void() # remove background, grid, numeric labels
