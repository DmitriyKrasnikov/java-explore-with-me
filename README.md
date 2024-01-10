# ExploreWithMe

## Используемые технологии
- Spring Boot
- Hibernate
- Docker
- Git
- Maven
- Lombok
- Postman
- Postgresql
- H2

## Описание
“ExploreWithMe” - это приложение, которое позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них. Оно помогает пользователям оптимально использовать свое свободное время, планируя различные мероприятия.

## Сервисы
Приложение состоит из двух сервисов:
1. Основной сервис, который содержит все необходимое для работы продукта.
2. Сервис статистики, который хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.

## Спецификация API
Оба сервиса выполнены согласно данным спецификациям API:
- спецификация основного сервиса: [ewm-main-service-spec.json;](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json)
- спецификация сервиса статистики: [ewm-stats-service.json.](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json)

## Аутентификация и авторизация
С внешним миром сервисы связывает сетевой шлюз. Он контактирует с системой аутентификации и авторизации, а затем перенаправляет запрос в сервисы. То есть, если шлюз пропустил запрос к закрытой или административной части API, значит, этот запрос успешно прошел аутентификацию и авторизацию.

API основного сервиса разделено на три части:
- Публичная часть API, доступная без регистрации любому пользователю сети. Она предоставляет возможности поиска и фильтрации событий.
- Закрытая часть API, доступная только авторизованным пользователям. Она реализует возможности зарегистрированных пользователей продукта, такие как добавление новых мероприятий, редактирование их и просмотр после добавления, а также подача заявок на участие в интересующих мероприятиях.
- Административная часть API, доступная для администраторов сервиса. Она предоставляет возможности настройки и поддержки работы сервиса, такие как добавление, изменение и удаление категорий для событий, добавление, удаление и закрепление на главной странице подборок мероприятий, модерация событий, размещённых пользователями, и управление пользователями .

Жизненный цикл события включает несколько этапов: создание, ожидание публикации, публикация и отмена публикации. Это обеспечивает гибкость и контроль над процессом публикации событий.

## Сервис статистики
Второй сервис — сервис статистики. Он собирает информацию о количестве обращений пользователей к спискам событий и о количестве запросов к подробной информации о событии. На основе этой информации формируется статистика о работе приложения. Функционал сервиса статистики включает запись информации о том, что был обработан запрос к эндпоинту API, и предоставление статистики за выбранные даты по выбранному эндпоинту.

## Модули
Проект многомодульный, состоит из двух отдельно запускаемых модулей: основного сервиса и сервиса статистики. Сервис статистики состоит из подмодулей HTTP-сервиса и HTTP-клиента. Механизм взаимодействия сервиса и клиента предполагает, что они используют одни и те же объекты для запросов и ответов. Исходя из этого, выделен еще один подмодуль, в котором размещены общие классы DTO.

Оба сервиса и экземпляр базы данных PostgreSQL для каждого из сервисов запускаются в отдельных Docker-контейнерах, и их взаимодействие настроено через Docker Compose.

Также написаны Postman тесты для одной из функциональностей.

