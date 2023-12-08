FROM amazoncorretto:17

RUN echo "Copy files..."

COPY admin/ /app-build/admin
COPY datacenter/ /app-build/datacenter
COPY dataprovider/ /app-build/dataprovider
COPY restapi/ /app-build/restapi
COPY .mvn/ /app-build/.mvn
COPY pom.xml /app-build/
COPY mvnw* /app-build/
COPY config/ /app-build/config

RUN echo "Display /app-build/ folder ..."
RUN cd /app-build/ \
    && echo $(pwd) && echo $(ls -la)

RUN echo "Maven build..."
RUN cd /app-build/ \
    && ./mvnw clean install

RUN echo "Display /app-build/ folder ..."
RUN cd /app-build/ \
    && echo $(pwd) && echo $(ls -la)
