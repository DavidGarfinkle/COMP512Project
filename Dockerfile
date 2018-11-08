FROM openjdk:latest

WORKDIR /COMP512Project

COPY . /COMP512Project

RUN mvn clean install

EXPOSE 1099

CMD []
