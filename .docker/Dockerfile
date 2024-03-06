FROM amazoncorretto:17-alpine

# https://stackoverflow.com/a/68515645
RUN apk add --no-cache fontconfig ttf-dejavu

RUN echo "Copy files..."

COPY settings.gradle.kts /app-build/
COPY build.gradle.kts /app-build/
COPY gradle/ /app-build/gradle
COPY gradlew /app-build/
COPY src/ /app-build/src
COPY config_credentials/ /app-build/config_credentials

RUN echo "Display /app-build/ folder ..."
RUN cd /app-build/ \
    && echo $(pwd) && echo $(ls -la)

RUN echo "Gradle build..."
RUN cd /app-build/ \
    && ./gradlew clean bootJar --no-daemon

RUN echo "Display /app-build/ folder ..."
RUN cd /app-build/ \
    && echo $(pwd) && echo $(ls -la)
