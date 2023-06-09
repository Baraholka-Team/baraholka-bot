FROM maven:3.8.5-eclipse-temurin-17-alpine as maven

COPY ./pom.xml ./pom.xml

COPY ./tg-bot/pom.xml ./tg-bot/pom.xml

COPY ./tg-bot/src ./tg-bot/src

WORKDIR /tg-bot

RUN mvn clean compile assembly:single

FROM openjdk:17-alpine

WORKDIR /baraholka-app

COPY --from=maven ./tg-bot/target/tg-bot-*.jar ./tg-bot/baraholka-bot.jar

CMD ["java", "-jar", "./tg-bot/baraholka-bot.jar"]