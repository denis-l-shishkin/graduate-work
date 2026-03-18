## Graduation Project - Платформа объявлений (Ads Platform)

Это дипломный проект для приложения объявлений, которое представляет собой RESTful API для управления объявлениями и профилями пользователей.

### Возможности

- Аутентификация и авторизация пользователей
- Управление объявлениями (операции CRUD)
- Система комментариев для объявлений
- Загрузка и управление аватарами пользователей
- Обработка изображений для объявлений и аватаров пользователей
- Контроль доступа на основе ролей (USER, ADMIN)

### Технологии

- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- MapStruct

### Требования

- Java 11
- PostgreSQL
- Maven
- Docker (для фронтенда)

### Структура проекта

src/main/java/ru/skypro/homework/
- config/ # Конфигурация безопасности и CORS
- controller/ # REST контроллеры
- dto/ # Data Transfer Objects
- entity/ # Сущности БД
- repository/ # Репозитории JPA
- service/ # Сервисные классы
- mapper/ # Мапперы для преобразования данных
- utils/ # Вспомогательные классы
- HomeworkApplication.java  # Точка входа

resources/
- application.properties  # Конфигурационные файлы
- db/changelog/ # Миграции Liquibase
- db.changelog-master.yaml
- - v1.0.0/
- - - 01-create-users-table.yaml
- - - 02-create-ads-table.yaml
- - - 03-create-comments-table.yaml
- - - 04-add-indexes.yaml

### Основные компоненты

- Контроллеры
- AdController - управление объявлениями
- CommentController - управление комментариями
- UserController - управление пользователями
- ImageController - работа с изображениями

### Сервисы

- AdService - бизнес-логика работы с объявлениями
- CommentService - бизнес-логика работы с комментариями
- UserService - бизнес-логика работы с пользователями
- ImageService - работа с изображениями

### Сущности

- AdEntity - объявление
- CommentEntity - комментарий
- UserEntity - пользователь

### Мапперы

- AdMapper - преобразование между сущностями и DTO для объявлений
- CommentMapper - преобразование между сущностями и DTO для комментариев
- UserMapper - преобразование между сущностями и DTO для пользователей

## Основные функции

### Управление объявлениями

- Создание, чтение, обновление и удаление объявлений
- Прикрепление изображений к объявлениям
- Получение списка всех объявлений или объявлений конкретного пользователя

### Управление комментариями

- Создание, чтение, обновление и удаление комментариев
- Получение комментариев к конкретному объявлению

### Управление пользователями

- Регистрация и авторизация пользователей
- Обновление профильной информации
- Управление аватарами пользователей
- Получение информации о текущем пользователе

### Работа с изображениями

- Загрузка и хранение изображений для объявлений и аватаров
- Получение изображений по их идентификатору
- Удаление изображений при удалении соответствующих сущностей

## Безопасность

- Basic Authentication (совместимость с фронтендом)
- Роли: USER и ADMIN
- Проверка прав: через @PreAuthorize и SecurityUtils
- Хранение пользователей: PostgreSQL (через JdbcUserDetailsManager)

## Настройка

Для запуска проекта необходимо:
- Создать БД PostgreSQL и настроить подключение к ней
  sql
  CREATE DATABASE ads_platform;
  CREATE USER user_ads_platform WITH PASSWORD 'password';
  GRANT ALL PRIVILEGES ON DATABASE ads_platform TO user_ads_platform;

- Настроить пути загрузки изображений в application.properties
  properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/ads_platform
  spring.datasource.username=user_ads_platform
  spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
spring.liquibase.enabled=true

upload.avatars.path=./uploads/avatars/
upload.ads.path=./uploads/ads/

spring.servlet.multipart.max-file-size=10MB

## Запус

### Сборка проекта
mvn clean install

### Запуск приложения
mvn spring-boot:run

### Запуск фронтенда (отдельный терминал)
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21

### Приложение будет доступно:
Backend: http://localhost:8080

Frontend: http://localhost:3000

## Тестирование
Инструменты для ручного тестирования

Postman

## Примечания
Фронтенд запускается на порту 3000 и ожидает бэкенд на 8080
Все запросы с фронтенда содержат Basic Authentication заголовки
Изображения сохраняются в файловую систему, в БД только пути
Администратор имеет полный доступ ко всем ресурсам

👥 Авторы

Разработчик: Shishkin D