FROM maven:latest
WORKDIR /backend
COPY pom.xml .
RUN mvn clean install
COPY . .
EXPOSE 8000 8080
CMD mvn spring-boot:run