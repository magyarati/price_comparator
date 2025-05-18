# Price Comparator

A Java-based backend application that compares grocery product prices across major Romanian supermarket chains (Lidl, Kaufland, Profi). It helps users find the best deals, track price history, and optimize their shopping baskets.

---

## ✨ Features

- 🛒 **Basket Optimizer**  
  Splits a shopping list across stores to minimize total cost.


---

## 🗂️ Project Structure

```text
price-comparator/
├── backend/                    ← Java Spring Boot backend
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew, gradlew.bat
│   ├── gradle/wrapper/
│   ├── .idea/                  ← IntelliJ project config
│   ├── data/                   ← Product & discount CSV files
│   │   ├── lidl_*.csv
│   │   ├── kaufland_*.csv
│   │   └── profi_*.csv
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/example/pricecomparator/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── model/
│   │   │   │   ├── util/
│   │   │   │   └── PriceComparatorApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/
│   │   │           ├── index.html
│   │   │           └── static/js/
│   ├── src/test/
│   │   └── java/
│   │       └── ProductServiceTest.java
│
├── frontend/                   ← React frontend (partial)
│   ├── package.json
│   ├── package-lock.json
│   ├── public/
│   │   └── index.html
│   └── src/
│       ├── index.js
│       ├── App.jsx
│       └── components/
│           ├── BasketSummary.jsx
│           ├── Filters.jsx
│           ├── BasketBuilder.jsx
│           ├── ProductList.jsx
│           └── PriceHistoryChart.jsx
│
├── scripts/
│   └── package_project.sh      ← Archive project with timestamp
├── build.gradle                ← Root Gradle (or frontend)
├── settings.gradle
├── gradlew, gradlew.bat
```

---

## 🛠️ Build & Run

### 🔧 Prerequisites

- Java 17+
- Gradle (or use the provided wrapper)

### ▶️ Run the Backend

```bash
from project root folder
./gradlew build
./gradlew bootRun
```


---

## 🧪 Run Tests

```bash
./gradlew test
```

---

## 📦 Packaging the Project

Use the helper script to generate a timestamped ZIP:

```bash
./scripts/package_project.sh
```

This creates:  
`price_comparator_YYYY-MM-DD_HH-MM-SS.zip`  
(excludes: `build/`, `node_modules/`, `.git/`, etc.)

---

## 📊 API Overview (Example)

| Method | Endpoint                   | Description                             |
|--------|----------------------------|-----------------------------------------|
| GET    | `/api/products`            | List all products                       |

---

## 📁 Sample Data Files

Place CSVs in the `backend/data/` directory. Supported formats include:

- `lidl_2025-05-08.csv`
- `kaufland_discounts_2025-05-08.csv`
- `profi_2025-05-08.csv`

> Format columns for price lists: `product_id;product_name;category;brand;grammage;unit;price;currency`
> Format columns for discounts: `product_id;product_name;brand;package_quantity;package_unit;product_category;from_date;to_date;percentage_of_discount`


---

## 📌 Assumptions

- Product names are normalized across stores for comparison.
- Price history is reconstructed from multiple daily CSV snapshots.
- Discounts are matched by `product_name`.

---

## 🔖 License

MIT License. Free to use, modify, and extend.

---

## 👤 Author

Developed by [Attila Magyar] – for demo, testing, and practical portfolio purposes.
