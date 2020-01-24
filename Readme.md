# Phonebook Scala

Простая телефонная книга на Scala http4s.

## Сборка контейнера Docker

- Собрать "толстый" JAR со всеми зависимостями: `sbt assembly`;
- В зависимости от вида запуска:
  - Запустить `docker-compose up -d` для билда и запуска вместе со Swagger Editor;
  - Запустить `docker build -t phonebook ./` для билда образа и запуска вручную.
  
