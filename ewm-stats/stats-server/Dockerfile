FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats_server.jar
ENTRYPOINT ["java","-jar","/stats_server.jar"]
