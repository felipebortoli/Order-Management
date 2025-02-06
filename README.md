# Order Service - Gerenciamento de Pedidos
Este projeto é um serviço de gerenciamento de pedidos desenvolvido em Java com Spring Boot. 
Ele recebe pedidos de um sistema externo (Produto Externo A), processa os pedidos (calculando o valor total e gerenciando o status) 
e disponibiliza os pedidos processados para outro sistema externo (Produto Externo B). 
O sistema foi projetado para lidar com uma alta volumetria de pedidos (150 mil a 200 mil por dia) 
e utiliza tecnologias como Kafka, PostgreSQL, Docker e Swagger para garantir escalabilidade, resiliência e facilidade de uso.


## Tecnologias Utilizadas
Java 17: Linguagem de programação principal.

Spring Boot: Framework para desenvolvimento da aplicação.

Kafka: Sistema de mensageria para receber e enviar pedidos.

PostgreSQL: Banco de dados relacional para armazenar os pedidos.

Docker: Para containerização da aplicação e dependências.

Swagger: Documentação e teste da API.

Resilience4j: Para implementar Circuit Breaker e garantir resiliência.


## Como Rodar o Projeto
### Pré-requisitos
Docker e Docker Compose instalados.

Java 17 (opcional, apenas se quiser rodar localmente sem Docker).

### Passos para Execução

git clone https://github.com/seu-usuario/order-service.git
cd order-service

./gradlew clean build

docker-compose up

### Acessar a aplicação:

A aplicação estará disponível em: http://localhost:8080.

O Swagger UI estará disponível em: http://localhost:8080/swagger-ui/index.html#/

