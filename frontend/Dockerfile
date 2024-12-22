# Используем образ Node.js на основе Alpine
FROM node:23.4.0-alpine3.20 AS frontend-build

# Устанавливаем рабочую директорию
WORKDIR /usr/src/app/frontend

# Устанавливаем git
RUN apk add --no-cache git

# Клонируем репозиторий фронтенда (если директория пуста)
RUN git clone https://github.com/BaronGalileo/Act-books . || true

# Переключаемся на нужную ветку (например, main)
RUN git checkout main

# Обновляем репозиторий до последней версии
RUN git pull

# Устанавливаем зависимости
RUN npm install

# Собираем приложение
RUN npm run build

# Используем Nginx для обслуживания статических файлов приложения
FROM nginx:latest

# Копируем собранные файлы из предыдущего этапа в директорию Nginx
COPY --from=frontend-build /usr/src/app/frontend/build /usr/share/nginx/html

# Указываем порт, который будет использоваться приложением (по умолчанию это порт 80)
EXPOSE 80

# Команда для запуска Nginx (по умолчанию)
CMD ["nginx", "-g", "daemon off;"]