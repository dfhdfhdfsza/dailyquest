# --- Build Stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper 및 설정 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 윈도우 줄바꿈 제거 + 실행 권한
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Gradle 캐시 워밍업
RUN ./gradlew --version

# 소스 전체 복사
COPY . .

# (중요) 다시 권한 부여
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 빌드 실행
RUN ./gradlew clean bootJar -x test

# --- Run Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
CMD ["sh","-c","java -jar -Dserver.port=$PORT app.jar"]
