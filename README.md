# Price Comparator

A Java-based backend application that compares grocery product prices across major Romanian supermarket chains (Lidl, Kaufland, Profi). It helps users find the best deals, track price history, and optimize their shopping baskets.

---

## ✨ Features

- 🛒 **Basket Optimizer**  
  Splits a shopping list across stores to minimize total cost. 
  Highlights value per unit (e.g., price per kg/liter) to surface the best deals, even across different package sizes.
- 📉 **Best Discounts**  
  View products with the highest current percentage discounts across all tracked stores for any given day.
- 🆕 **New Discounts**  
  See all discounts newly introduced in the last 24 hours.
- 📈 **Dynamic Price History Graphs**  
  Track price trends over time for each product, with the ability to filter by store, product category, or brand.

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
│   │   ├── index.html
│   │   ├── best-discounts.html
│   │   ├── history.html
│   │   ├── new-discounts.html
│   │   ├── price-chart.html
│   │   └── split-basket.html
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
- Node.js & npm

### ▶️ Run the Backend

```bash
from project root folder
cd ~/price_comparator/
./gradlew clean build 
./gradlew :backend:bootRun
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

## 📊 API Overview

| Method | Endpoint                                  | Description                                               |
|--------|-------------------------------------------|-----------------------------------------------------------|
| GET    | `/api/products`                           | List all products                                         |
| GET    | `/api/products/history?name=PRODUCT_NAME` | View price history for a given product                    |
| GET    | `/api/discounts`                          | List all discounts                                        |
| POST   | `/api/basket/optimize`                    | Optimize basket for the cheapest option in a single store |
| POST   | `/api/basket/optimize?allStores=true`     | Optimize basket for all stores, show best/winner store    |
| POST   | `/api/basket/optimize-split`              | Optimize split basket (can divide list across multiple    |
|        |                                           | stores for lowest total cost)                             |
| GET    | `/api/discounts/best`                     | View products with the best (highest %) discounts overall |
|        |                                           | or by date                                                |
| GET    | `/api/discounts/new`                      | View new discounts added in the last 24 hours             |
| GET    | `/api/alerts`                             | Get all price alerts                                      |
| POST   | `/api/alerts`                             | Create a new price alert                                  |
| PUT    | `/api/alerts{id}`                         | Edit/update a price alert                                 |
| DELETE | `/api/alerts{id}`                         | Delete a price alert                                      |
| GET    | `/api/alerts/triggered`                   | Get currently triggered price alerts                      |
| POST   | `/api/admin/reload`                       | Reload the backend data from CSVs                         |

---

## 📁 Sample Data Files

Place CSVs in the `backend/data/` directory. Supported formats include:

- `lidl_2025-05-08.csv`
- `kaufland_discounts_2025-05-08.csv`
- `profi_2025-05-08.csv`

**Product price list format:**  
`product_id;product_name;category;brand;grammage;unit;price;currency`

**Discount list format:**  
`product_id;product_name;brand;package_quantity;package_unit;product_category;from_date;to_date;percentage_of_discount`

---

## 📌 Assumptions

- Product names are normalized across stores for accurate comparison.
- Price history is reconstructed from daily CSV snapshots.
- Discounts are matched by product name.
- Unit price comparisons (price per kg/liter/etc.) enable best-value recommendations.

---

## 🔖 License

MIT License. Free to use, modify, and extend.

---

## 👤 Author

Developed by [Attila Magyar] – for demo, testing, and portfolio purposes.

---

*Feel free to contribute, open issues, or fork for your own projects!*
