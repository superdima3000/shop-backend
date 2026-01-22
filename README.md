# 👔 Clothing Store API

REST API для управления интернет-магазином одежды с продвинутой системой фильтрации, управлением остатками и аналитикой продаж.
***

## 📋 Описание

Полнофункциональное Spring Boot приложение для магазина одежды с поддержкой множественных магазинов, глобальных складов и детальной статистики по товарам.
## ✨ Возможности

- 🛍️ CRUD операции для товаров
- 🔍 Расширенная фильтрация товаров (категория, цена, пол, размер, рейтинг)
- 📦 Управление остатками на глобальном складе
- 🏪 Управление остатками в конкретных магазинах (с поддержкой размеров)
- 📊 Аналитика продаж (топ по продажам, доходу, чистой прибыли)
- 🔎 Полнотекстовый поиск товаров
- 📄 Пагинация результатов
- 🖼️ Поддержка изображений товаров
- 🧯 Централизованная обработка исключений с кастомными ошибками

***

## 🛠️ Технологии

- **Java** + **Spring Boot**
- **Spring Web** (REST API)
- **Spring Data JPA** (с поддержкой пагинации)
- **PostgreSQL**
- **Maven** / **Gradle**
- **Lombok**

***

## 🚀 Быстрый старт

### Требования

- Java 17+
- PostgreSQL
- Gradle

### Установка

1. Клонируй репозиторий
2. Настрой подключение к PostgreSQL в `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clothing_store_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Запусти приложение:

```bash
./gradlew bootRun
```

Приложение запустится на `http://localhost:8080`

***

## 📚 API эндпоинты

### 🛍️ Товары

#### Получить список товаров с фильтрацией

**GET** `/api/products`

Параметры запроса:
- `categoryId` — фильтр по категории
- `minPrice` — минимальная цена
- `maxPrice` — максимальная цена
- `gender` — пол (male/female/unisex)
- `sizeValues` — фильтр по размерам (можно передать несколько)
- `search` — поиск по названию
- `inStock` — только товары в наличии
- `storeId` — фильтр по магазину
- `rating` — минимальный рейтинг
- `page` — номер страницы (по умолчанию 0)
- `pageSize` — размер страницы (по умолчанию 20)

**Пример:**

```bash
curl "http://localhost:8080/api/products?gender=male&minPrice=1000&maxPrice=5000&sizeValues=M&sizeValues=L&page=0&pageSize=10"
```

**Ответ:**

```json
{
  "content": [
    {
      "id": 1,
      "name": "Куртка зимняя",
      "article": "WJ-2024-001",
      "price": 3500,
      "weight": 800,
      "description": "Теплая зимняя куртка с утеплителем",
      "gender": "male",
      "rating": 4.5,
      "categoryId": 2,
      "primaryImageUrl": "https://example.com/images/jacket-main.jpg",
      "images": [
        {
          "id": 1,
          "url": "https://example.com/images/jacket-1.jpg",
          "isPrimary": true
        },
        {
          "id": 2,
          "url": "https://example.com/images/jacket-2.jpg",
          "isPrimary": false
        }
      ],
      "availableSizes": ["M", "L", "XL"],
      "totalQuantity": 45
    }
  ],
  "totalElements": 45,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

#### Получить товар по ID

**GET** `/api/products/{id}`

```bash
curl http://localhost:8080/api/products/1
```

**Ответ:**

```json
{
  "id": 1,
  "name": "Куртка зимняя",
  "article": "WJ-2024-001",
  "price": 3500,
  "weight": 800,
  "description": "Теплая зимняя куртка с утеплителем",
  "gender": "male",
  "rating": 4.5,
  "categoryId": 2,
  "primaryImageUrl": "https://example.com/images/jacket-main.jpg",
  "images": [...],
  "availableSizes": ["M", "L", "XL"],
  "totalQuantity": 45
}
```

#### Создать товар

**POST** `/api/products`

```json
{
  "name": "Футболка базовая",
  "article": "TS-2024-050",
  "price": 1200,
  "weight": 200,
  "gender": "unisex",
  "categoryId": 1,
  "description": "Классическая хлопковая футболка"
}
```

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Футболка базовая","article":"TS-2024-050","price":1200,"weight":200,"gender":"unisex","categoryId":1}'
```

#### Обновить товар

**PUT** `/api/products/{id}`

```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Куртка зимняя обновленная","article":"WJ-2024-001","price":3800,"weight":850,"gender":"male","categoryId":2}'
```

#### Удалить товар

**DELETE** `/api/products/{id}`

```bash
curl -X DELETE http://localhost:8080/api/products/1
```

***

### 📦 Глобальный склад

#### Получить остаток на глобальном складе

**GET** `/api/products/{id}/global-stock`

```bash
curl http://localhost:8080/api/products/1/global-stock
```

**Ответ:**

```json
{
  "productId": 1,
  "quantity": 500,
  "price": 3500.0
}
```

#### Создать запись на глобальном складе

**POST** `/api/products/{id}/global-stock`

```json
{
  "productId": 1,
  "quantity": 500,
  "price": 3500.0
}
```

```bash
curl -X POST http://localhost:8080/api/products/1/global-stock \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":500,"price":3500.0}'
```

#### Обновить остаток на глобальном складе

**PUT** `/api/products/{id}/global-stock`

```bash
curl -X PUT http://localhost:8080/api/products/1/global-stock \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":450,"price":3500.0}'
```

#### Удалить запись с глобального склада

**DELETE** `/api/products/{id}/global-stock`

***

### 🏪 Управление остатками в магазинах

#### Получить список магазинов с товаром

**GET** `/api/products/{id}/stores`

```bash
curl http://localhost:8080/api/products/1/stores
```

**Ответ:**

```json
[
  {
    "id": 1,
    "address": "Невский проспект, 28",
    "phone": "+7 (812) 123-45-67",
    "rent": 150000.00,
    "rating": 4.8
  },
  {
    "id": 3,
    "address": "ТЦ Галерея, 3 этаж",
    "phone": "+7 (812) 987-65-43",
    "rent": 180000.00,
    "rating": 4.6
  }
]
```

#### Получить остаток товара в конкретном магазине

**GET** `/api/products/{productId}/stores/{storeId}/stock`

```bash
curl http://localhost:8080/api/products/1/stores/2/stock
```

**Ответ:**

```json
{
  "productId": 1,
  "storeId": 2
}
```

#### Создать остаток в магазине

**POST** `/api/products/{productId}/stores/{storeId}/stock`

```json
{
  "productId": 1,
  "storeId": 2,
  "quantity": 25
}
```

```bash
curl -X POST http://localhost:8080/api/products/1/stores/2/stock \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"storeId":2,"quantity":25}'
```

#### Обновить остаток в магазине

**PUT** `/api/products/{productId}/stores/{storeId}/stock`

```bash
curl -X PUT http://localhost:8080/api/products/1/stores/2/stock \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"storeId":2,"quantity":18}'
```

#### Удалить остаток из магазина

**DELETE** `/api/products/{productId}/stores/{storeId}/stock`

***

### 📊 Аналитика и статистика

#### Топ самых продаваемых товаров

**GET** `/api/products/top-selling`
```bash
curl "http://localhost:8080/api/products/top-selling?limit=5"
```

**Ответ:**

```json
[
  {
    "productId": 15,
    "name": "Джинсы классические",
    "article": "JN-2024-015",
    "price": 2500,
    "rating": 4.7,
    "totalSold": 320
  },
  {
    "productId": 8,
    "name": "Футболка базовая белая",
    "article": "TS-2024-008",
    "price": 800,
    "rating": 4.5,
    "totalSold": 285
  }
]
```

#### Топ товаров по валовому доходу

**GET** `/api/products/top-income`

```bash
curl "http://localhost:8080/api/products/top-income?limit=10"
```

**Ответ:**

```json
[
  {
    "productId": 1,
    "name": "Куртка зимняя",
    "rating": 4.5,
    "cleanIncome": 450000,
    "totalIncome": 800000,
    "totalSold": 230
  }
]
```

#### Топ товаров по чистой прибыли

**GET** `/api/products/top-clean-income`

```bash
curl "http://localhost:8080/api/products/top-clean-income?limit=10"
```

**Ответ:**

```json
[
  {
    "productId": 5,
    "name": "Пальто шерстяное",
    "rating": 4.9,
    "cleanIncome": 520000,
    "totalIncome": 950000,
    "totalSold": 95
  }
]
```

#### Общая статистика по товарам

**GET** `/api/products/stats`

Параметры:
- `limit` — количество товаров (по умолчанию 10)
- `orderBy` — сортировка: `total_sold`, `total_income`, `clean_income`

```bash
curl "http://localhost:8080/api/products/stats?limit=20&orderBy=total_income"
```

**Ответ:**
```json
[
  {
    "productId": 1,
    "name": "Куртка зимняя",
    "rating": 4.5,
    "cleanIncome": 450000,
    "totalIncome": 800000,
    "totalSold": 230
  },
  {
    "productId": 5,
    "name": "Пальто шерстяное",
    "rating": 4.9,
    "cleanIncome": 520000,
    "totalIncome": 950000,
    "totalSold": 95
  }
]
```

***

### 🔍 Дополнительные операции

#### Поиск товаров по названию

**GET** `/api/products/search`

```bash
curl "http://localhost:8080/api/products/search?query=куртка"
```

**Ответ:** список `ProductDto`

#### Получить доступные размеры товара

**GET** `/api/products/{id}/sizes`

```bash
curl http://localhost:8080/api/products/1/sizes
```

**Ответ:**

```json
["S", "M", "L", "XL"]
```

#### Получить общее количество товара во всех магазинах

**GET** `/api/products/{id}/total-quantity

```bash
curl http://localhost:8080/api/products/1/total-quantity
```

**Ответ:** `48`

#### Получить количество товара в конкретном магазине

**GET** `/api/products/{id}/store/{storeId}/quantity`

```bash
curl http://localhost:8080/api/products/1/store/2/quantity
```

**Ответ:** `15`

#### Проверить наличие товара в магазине

**GET** `/api/products/{id}/store/{storeId}/in-stock`

```bash
curl http://localhost:8080/api/products/1/store/2/in-stock
```

**Ответ:** `true` или `false`

***

## 🧯 Обработка ошибок

Все исключения в API — кастомные. Централизованный обработчик возвращает структурированные ответы.

### Формат ошибки

```json
{
  "timestamp": "2026-01-22T23:48:00",
  "status": 404,
  "error": "PRODUCT_NOT_FOUND",
  "message": "Товар с ID 999 не найден",
  "path": "/api/products/999"
}
```

### Типы ошибок

- `400` — Невалидные данные запроса
- `404` — Ресурс не найден
- `409` — Конфликт данных (например, дублирование)
- `500` — Внутренняя ошибка сервера

***

### Слои приложения

- **Entity** — модели базы данных
- **Repository** — доступ к данным через JPA
- **Service** — бизнес-логика и валидация
- **Controller** — обработка HTTP запросов

***
