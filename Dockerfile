# --- Build Stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 필요한 도구 (dos2unix)
RUN apt-get update && apt-get install -y --no-install-recommends dos2unix && rm -rf /var/lib/apt/lists/*

# Gradle wrapper/설정 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 줄바꿈 교정 + 권한
RUN dos2unix gradlew && chmod +x gradlew

# 캐시 워밍업
RUN bash gradlew --version

# 소스 전체 복사
COPY . .

# (중요) 다시 줄바꿈 교정 + 권한 (COPY . . 이후 권한이 초기화될 수 있음)
RUN dos2unix gradlew && chmod +x gradlew

# 빌드 (exec 비트 문제 우회 위해 bash로 실행)
RUN bash gradlew clean bootJar -x test

# --- Run Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
CMD ["sh","-c","java -jar -Dserver.port=$PORT app.jar"]
