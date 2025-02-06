# Usar uma imagem base do OpenJDK 17
FROM openjdk:17-jdk-alpine

# Definir o diretório de trabalho dentro do container
WORKDIR /app

# Copiar o arquivo JAR gerado pelo Gradle para o container
COPY build/libs/Order-Management-0.0.1-SNAPSHOT.jar app.jar

# Expor a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]