# 1. 자바 17 버전이 설치된 깨끗한 컴퓨터(리눅스)를 한 대 빌려옴
FROM eclipse-temurin:26-jdk-jammy

# 2. 그 컴퓨터 안에서 작업할 폴더를 만듦
WORKDIR /app

# 3. 질문자님의 깃허브에 있는 모든 코드를 그 폴더로 복사
COPY . .

# 4. 그래들에 실행 권한을 주고 빌드(포장)를 진행
RUN chmod +x gradlew
RUN ./gradlew build -x test

# 5. 서버를 실행
CMD ["./gradlew", "run"]