# Phonebook Scala

Простая телефонная книга на Scala с Http4s, Cats IO и Doobie.

## Варианты запуска

Мной предусмотрено несколько вариантов запуска.


### Готовый контейнер Docker

Готовый образ находится в репозитории Docker Hub: [denisnovac/scala-phonebook](https://hub.docker.com/repository/docker/denisnovac/scala-phonebook). Проще всего запустить его, используя docker-compose и конфиг из данного репозитория.


### Сборка JAR

Сборка толстого JAR со всеми зависимостями осуществляется командой `sbt assembly`. Полученный JAR готов к запуску. Для его конфигурации необходим файл `application.conf`, без него не получится подсоединиться к БД.


### Сборка контейнера Docker

Собрать контейнер можно последовательностью команд `sbt assembly` и `docker build -t phonebook ./`. После этого контейнер можно запускать через `docker run` или `docker-compose up -d`. В репозитории приложен конфиг `docker-compose.yml` с готовыми настройками для поднятия вместе с БД. Для конфигурации программы все ещё необходим `application.conf`, его можно "прокинуть" в контейнер (в файле compose он уже прокинут).


## Конфигурационный файл application.conf

Формат файла:

```
persistent = true
app-host = "172.18.1.2"
app-port = "9000"
db-host = "172.18.1.10"
db-port = "5432"
db = "postgres"
db-user = "postgres"
db-password = "P@ssw0rd"
```

Если `persistent = true`, то ещё до запуска Blaze сервера и бинда на адрес приложение начнёт ждать БД. Если соединение не будет получено - будет брошен эксепшен `SQLTimeoutException`.

Если пользоваться контейнером, то файл можно прокинуть откуда угодно через композ или соответствующий параметр:

```
./application.conf:/opt/app/application.conf
```

JAR-приложение ждёт именно файл ./application.conf в месте своего запуска.