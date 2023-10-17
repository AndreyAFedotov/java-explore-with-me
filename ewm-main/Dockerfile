FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main_service.jar
ENTRYPOINT ["java","-jar","/main_service.jar"]
