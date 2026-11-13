# Etapa 1: compila o projeto JSP/Servlet para WAR.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: somente Tomcat para publicar o frontend.
FROM tomcat:10.1-jdk17-temurin
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /workspace/target/SugestFilmes-Front-JSP.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
