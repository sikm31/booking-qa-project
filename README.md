# booking-qa-project

Фреймворк автотестов для UI и API на Java: **Playwright + REST Assured + JUnit 6 + Allure**.

Тестируемые демо-приложения:
- UI: [SauceDemo](https://www.saucedemo.com), интернет-магазин
- API: [Restful-Booker](https://restful-booker.herokuapp.com/apidoc/index.html), CRUD бронирований

## Стек

| Задача | Инструмент |
|---|---|
| Язык и сборка | Java 17+, Maven |
| UI | Playwright for Java (системный Google Chrome) |
| API | REST Assured + Jackson (модели на records) |
| Раннер | JUnit 6 (параллельный запуск, параметризованные тесты) |
| Проверки | AssertJ, web-first assertions Playwright |
| Тестовые данные | Datafaker, CSV для data-driven |
| Моки | WireMock |
| БД | PostgreSQL в Docker Compose, Spring Boot (`JdbcTemplate`) |
| Конфиги | YAML по окружениям |
| Отчёты | Allure (шаги, скриншоты, Playwright trace, HTTP-логи) |
| CI | GitHub Actions + GitHub Pages |

## Архитектура

```
src/main/java/io/github/qa/
├── core/      Config (YAML), BrowserManager, PlaywrightExtension, BaseUiTest
├── pages/     Page Objects: LoginPage, InventoryPage, CartPage, CheckoutPage
├── api/       BaseClient, AuthClient, BookingClient + models/
├── steps/     бизнес-шаги с @Step: ShopSteps, BookingSteps
└── data/      фабрика уникальных тестовых данных (TestData)

src/test/java/io/github/qa/
├── ui/        UI-тесты (smoke, e2e, data-driven по CSV)
├── api/       CRUD, идемпотентность PUT, 403 без токена
├── mock/      проверки клиента на WireMock
└── db/        PostgreSQL (docker-compose.yml)

src/test/resources/
├── config/application-dev.yaml, application-stage.yaml
├── data/login_negative.csv
├── application.yaml              Spring Boot: подключение к PostgreSQL
└── junit-platform.properties     параллельность, случайный порядок
```

Слои: **tests → steps → pages / api clients → core**. Тесты не содержат локаторов и HTTP-деталей.

## Запуск

```bash
mvn test                                  # всё, кроме карантина (env=dev)
mvn test -Dgroups=smoke                   # только smoke
mvn test -Dgroups=api                     # только API
mvn test -Denv=stage -Dthreads=8          # другое окружение и число потоков
mvn test -Dbrowser.headless=false         # с видимым браузером
mvn test -Dbrowser.name=firefox           # другой браузер (сначала установите его, см. ниже)

mvn allure:serve                          # открыть отчёт в браузере
```

По умолчанию используется установленный **Google Chrome**, и браузеры Playwright скачивать не нужно.
Для `chromium`, `firefox` или `webkit` сначала выполните:
```bash
mvn exec:java -Dexec.args="install firefox"
```

Тесты в `db/` работают с PostgreSQL из `docker-compose.yml`. Перед запуском поднимите базу:
```bash
docker compose up -d --wait               # PostgreSQL 17 на localhost:55432 (booking / qa / qa)
mvn test -Dgroups=db
docker compose down                       # остановить
```
Тесты БД запускаются через `@SpringBootTest`: параметры подключения задаются в `src/test/resources/application.yaml` (`spring.datasource.*`) и переопределяются переменными `DB_URL`, `DB_USER`, `DB_PASSWORD`.
Если порт 55432 занят: `DB_PORT=55433 docker compose up -d --wait` и `DB_URL=jdbc:postgresql://localhost:55433/booking mvn test -Dgroups=db`.

## Конфигурация

Файл `config/application-{env}.yaml`, ключи читаются через точку по вложенности (`ui.baseUrl`, `browser.headless`).
Приоритет значений:
1. Системное свойство: `-Dui.baseUrl=...`
2. Переменная окружения: `UI_BASEURL`, `API_PASSWORD`
3. YAML-файл

Секреты в репозиторий не коммитятся: в CI они приходят из GitHub Secrets.

## Теги

| Тег | Что входит |
|---|---|
| `smoke` | Критичные пути, запускаются на каждый PR |
| `regression` | Полная регрессия, запускается ночью |
| `ui`, `api`, `mock`, `db` | По типу тестов |
| `quarantine` | Нестабильные тесты, исключены по умолчанию |

## Параллельность и стабильность

- JUnit 6 запускает тесты параллельно (по умолчанию 4 потока, настраивается через `-Dthreads`).
- Playwright не потокобезопасен: у каждого потока свой `Browser`, а у каждого теста новый `BrowserContext`.
- REST Assured настраивается на каждый запрос, без статического состояния.
- Каждый тест создаёт свои уникальные данные и удаляет их за собой.
- Методы выполняются в случайном порядке, это помогает ловить скрытые зависимости между тестами.
- Ожидания только явные (auto-wait Playwright), `Thread.sleep` не используется, анимации отключены (`reducedMotion`).
- При падении в Allure прикладываются скриншот, URL и Playwright trace (`npx playwright show-trace file.zip`).

## CI

- `.github/workflows/pr-smoke.yml`: smoke-тесты на каждый PR и push в `main`, результаты Allure сохраняются как artifact.
- `.github/workflows/nightly-regression.yml`: ночная регрессия и ручной запуск с выбором окружения. Отчёт Allure с историей публикуется в GitHub Pages.

Чтобы отчёт публиковался, включите в настройках репозитория Settings → Pages → Source: ветка `gh-pages`.

## Отчёт

Последний Allure-отчёт ночной регрессии: https://sikm31.github.io/booking-qa-project/
