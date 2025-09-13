# syntax=docker/dockerfile:1

### 1단계: 빌드
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper 및 설정 파일 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Gradle 캐시 warm-up
RUN ./gradlew --version

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean bootJar -x test

### 2단계: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app

# Render가 제공하는 포트 사용
ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=dev

# 빌드한 JAR만 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
CMD ["sh","-c","java -jar -Dserver.port=$PORT app.jar"]
