FROM openjdk:11
COPY target/userauthenticationmanagement.jar userauthenticationmanagement.jar
ENTRYPOINT [ "java", "-jar" , "/userauthenticationmanagement.jar" ]