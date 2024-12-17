# Используем базовый образ Maven с JDK 17 для сборки бэкенда
FROM maven:3.8.4-openjdk-17 AS build

# Копируем все файлы проекта в контейнер
COPY / /usr/src/app

# Переходим в директорию проекта
WORKDIR /usr/src/app

# Собираем проект
RUN mvn clean package -DskipTests

# Используем минимальный образ JDK 17 для запуска бэкенда
FROM openjdk:17-slim

# Копируем JAR-файл из предыдущего этапа сборки бэкенда
COPY --from=build /usr/src/app/target/AST-0.0.1-SNAPSHOT.jar /app/ast-promo.jar


# Указываем порт, который будет использоваться приложением
EXPOSE 8080

# Команда для запуска приложения (бэкенда)
CMD ["java", "-jar", "/app/ast-promo.jar"]