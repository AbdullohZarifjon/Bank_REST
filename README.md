Тестовый проект на Java 17 / Spring Boot, представляющий систему управления банковскими картами, пользователями и транзакциями.
Приложение реализует безопасный REST API с аутентификацией через JWT и ролевой моделью доступа (ADMIN / USER).

Проект содержит роли ROLE_ADMIN и ROLE_USER. При первом запуске они автоматически добавляются в базу данных.

Вход как админ: username="admin", password="adminchik"

Вход как пользователь: username="string", password="stringst"

Если хотите запустить проект, измените настройки базы данных в application.properties, а также используйте Dockerfile и docker-compose.yml.

Если возникнут вопросы, можно написать @AbdullohZarif в Telegram или на почту zarifabdullox@gmail.com
 — постараюсь помочь в любое время 😄.

Технологии

Java 17+

Spring Boot 3+

Spring Security + JWT

Spring Data JPA (Hibernate)

PostgreSQL

Liquibase — миграции БД

Swagger / OpenAPI

Docker + Docker Compose

После запуска:

API доступно по адресу: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

🔑 API Основные эндпоинты
Аутентификация

POST /api/auth/sign-up — регистрация

POST /api/auth/sign-in — вход (JWT)

POST /api/auth/refresh-token — обновление токена

POST /api/auth/sign-out — выход

Пользователи

GET /api/users/{id} — получить пользователя

PUT /api/users/{id} — обновить пользователя

GET /api/users/admin — список всех пользователей (только ADMIN)

PUT /api/users/{id}/status — изменить статус пользователя (ADMIN)

Карты

POST /api/cards/me — создать карту

GET /api/cards/me — список карт пользователя

PUT /api/cards/{id}/block — блокировка карты (ADMIN)

PUT /api/cards/{id}/activate — активация карты (ADMIN)

Транзакции

POST /api/transactions/me/transfer — перевод между своими картами

GET /api/transactions/me — история операций

GET /api/transactions — все транзакции (ADMIN)

🧾 Миграции БД

Все изменения схемы БД управляются через Liquibase

Основной changelog: src/main/resources/config/master.yaml

Если возникнут вопросы, можно написать @AbdullohZarif в Telegram или на почту zarifabdullox@gmail.com
 — постараюсь помочь в любое время 😄.

