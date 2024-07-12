FROM maven:3.9.8-eclipse-temurin-21 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk21-temurin

RUN apt-get update && \
    apt-get install -y python3 python3-pip

RUN pip3 install markovify numpy unidecode psycopg[binary]

COPY --from=builder /app/target/ecommerce-demo.war /usr/local/tomcat/webapps/

COPY database/mock_data /usr/local/tomcat/mock_data

EXPOSE 8080

CMD [ "catalina.sh", "run" ]