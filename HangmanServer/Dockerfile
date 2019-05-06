FROM maven:3.6-jdk-13-alpine
WORKDIR /app
RUN ls
COPY . .
RUN mvn package

FROM openjdk:13-slim
WORKDIR /app
COPY --from=0 /app/target/HangmanServer.jar ./HangmanServer.jar
CMD java -jar /app/HangmanServer.jar