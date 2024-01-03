FROM openjdk:17
COPY target/userauthenticationmanagement.jar userauthenticationmanagement.jar
ENTRYPOINT [ "java", "-jar" , "/userauthenticationmanagement.jar" ]