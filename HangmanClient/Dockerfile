FROM maven:3.6-jdk-13-alpine
WORKDIR /app
COPY . .
RUN mvn package

FROM ubuntu:18.04
WORKDIR /app
COPY --from=0 /app/target/HangmanClient.jar ./HangmanClient.jar
CMD java -jar /app/HangmanClient.jar
RUN dpkg --add-architecture i386
RUN apt-get update
RUN apt-get install -y default-jdk xorg
RUN apt-get clean; apt-get purge
