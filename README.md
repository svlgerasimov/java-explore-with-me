# Explore With Me

***Учебный проект Яндекс Практикума.***

Backend приложения-афиши, позволяющего пользователям делиться информацией 
об интересных событиях и находить компанию для участия в них.

---
### Стек технологий

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20Boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

---

### Функционал и архитектура

Приложение состоит из двух сервисов:
- **Основной сервис** - содержит весь основной функционал
- **Сервис статистики** - хранит количество просмотров и позволяет делать выборки для анализа работы приложения

Основной сервис обращается к сервису статистики через REST с использованием WebClient. 

#### Основной сервис
API разделено на публичную, закрытую и административную части.

Функционал **публичного API**:
- Поиск и фильтрация событий с отображением краткой информации о них
- Просмотр подробной информации о событии
- Просмотр подборок событий

Функционал **закрытого API** авторизованных пользователей:
- Добавление, редактирование и просмотр событий
- Возможность оставлять заявки на участие в событиях, размещенных другими пользователями
- Возможность подтверждать и отклонять заявки на участие в размещенных пользователем событиях


Функционал **API администратора**:
- Управление пользователями
- Управление категориями событий
- Модерация событий, размещенных пользователями, с возможностью оставить комментарий с замечаниями для инициатора события
- Формирование подборок событий с возможностью закреплять их на главной странице


#### Сервис статистики

Основной функционал:
- Запись информации об обработке запроса к эндпойнту API основного сервиса
- Предоставление статистики о запросах к выбранным эндпойнтам за определенные даты

---

### Спецификация API
<a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json">
API основного сервиса
</a>
<br>
<a href="https://petstore.swagger.io/?url=https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-stats-service-spec.json">
API сервиса статистики
</a>

---
### Хранение данных
Основной сервис
![Схема основного сервиса](/img/main-service-schema.png)


Сервис статистики
![Схема сервиса статистики](/img/stats-service-schema.png)
