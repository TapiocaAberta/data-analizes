library("tidyverse") #pacote para manipulacao de dados
library("cluster") #algoritmo de cluster
library("dendextend") #compara dendogramas
library("factoextra") #algoritmo de cluster e visualizacao
library("fpc") #algoritmo de cluster e visualizacao
library("gridExtra") #para a funcao grid arrange

set.seed(42)
load(file = "../data/emendas_2020_2022.RData")

data <- emendas[, c("id", "empenhado")]
rownames(data) <- data$id
data <- data[, -c(1)]
data.padronized <- (scale(data))

fviz_nbclust(data.padronized, kmeans, method = "wss") +
  geom_vline(xintercept = 3, linetype = 2)

k3_ini <- kmeans(data.padronized, centers = 3)
fviz_cluster(k3_ini, data = data.padronized, main = "Cluster K3", geom = c("point"))

datafit <- data.frame(k3_ini$cluster)
emendas <-  cbind(emendas, datafit)

save(emendas, file = "../data/emendas_cluster.RData")
write.csv(emendas,"../data/emendas_cluster.csv", row.names = FALSE)

k1 <- emendas %>% dplyr::filter(k3_ini.cluster == "1")
summary(k1)

k2 <- emendas %>% dplyr::filter(k3_ini.cluster == "2")
summary(k2)

k3 <- emendas %>% dplyr::filter(k3_ini.cluster == "3")
summary(k3)
