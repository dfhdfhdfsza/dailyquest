# Dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
# 로컬에 빌드된 jar 경로에 맞춰 파일명 수정 (예: build/libs/app-0.0.1-SNAPSHOT.jar)
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]