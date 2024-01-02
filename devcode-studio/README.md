## devcode-studio
개발을 도와주는 Java Swing 기반의 UI 프로그램


### 기능
1. Java Proerties 내용을 조회하는 기능
2. System.getEnv 내용을 조회하는 기능
3. UUID 문자열 생성기능
4. 랜덤 문자열 생성기능


### 개발환경
* JDK 11
* Maven 3.8


### 빌드 및 실행
```shell
git clone https://github.com/wyleedp/devcode.git
cd devcode\devcode-studio
mvnw clean package
java -jar target\devcode-studio.jar
```


### 스크린샷
Java Proerties

![Java Properties](./doc/image/01_JavaProerties.jpg)

OS Environment

![Java Properties](./doc/image/02_OsEnvironment.jpg)

UUID 문자열 생성

![Java Properties](./doc/image/03_UUID.jpg)


### 사용한 오픈소스
* [FlatLaf](https://github.com/JFormDesigner/FlatLaf) - Swing Look and Feel
* [MigLayout](https://github.com/JFormDesigner/miglayout) - Swing Layout
* [Apache Common IO](https://github.com/apache/commons-io)
* [Apache Common Lang](https://github.com/apache/commons-lang)
* [Apache Logging log4j2](https://github.com/apache/logging-log4j2)