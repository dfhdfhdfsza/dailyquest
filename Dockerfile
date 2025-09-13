# --- Build Stage ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# wrapper/설정 복사
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# (중요) 윈도우 CRLF 방지 + 실행 권한 부여
# dos2unix 없이도 sed만으로 충분. 둘 다 쓰고 싶으면 dos2unix 설치해도 OK.
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Gradle 캐시 워밍업
RUN ./gradlew --version

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew clean bootJar -x test

# --- Run Stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
CMD ["sh","-c","java -jar -Dserver.port=$PORT app.jar"]
