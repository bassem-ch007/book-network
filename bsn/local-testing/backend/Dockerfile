#   Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

#   Runtime stage
FROM amazoncorretto:17
ARG APP_VERSION=1.0.3
ARG PROFILE=prod
ARG EMAIL_HOSTNAME=smtp.gmail.com
ARG EMAIL_USER_NAME=contact.bassem587@gmail.com
ARG DB_URL=jdbc:postgresql://postgres-sql-bsn:5432/book_social_network

WORKDIR /app
COPY --from=build /build/target/bsn-*.jar /app/
EXPOSE 8088
ENV PROFILE=${PROFILE}
ENV DB_URL=${DB_URL}
ENV APP_VERSION=${APP_VERSION}
ENV ACTIVE_PROFILE=${PROFILE}
ENV EMAIL_HOSTNAME=${EMAIL_HOSTNAME}
ENV EMAIL_USER_NAME=${EMAIL_USER_NAME}
# Run the app
ENTRYPOINT ["sh", "-c", "java -jar bsn-$APP_VERSION.jar"]
#docker hub password :dckr_pat_S-Czck2AGzwd2gPzugW2Ps9VaJQ
