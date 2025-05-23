import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Filters from './components/Filters';
import BasketBuilder from './components/BasketBuilder';
import BasketSummary from './components/BasketSummary';
import ProductList from './components/ProductList';

export default function App() {
  const [products, setProducts] = useState([]);
  const [store, setStore] = useState('');
  const [selectedName, setSelectedName] = useState('');
  const [brandFilter, setBrandFilter] = useState('');
  const [dateFilter, setDateFilter] = useState('');
  const [basketResult, setBasketResult] = useState(null);

  useEffect(() => {
    const params = new URLSearchParams();
    if (store) params.append('store', store);
    if (dateFilter) params.append('date', dateFilter);

    axios
      .get(`/api/products?${params.toString()}`)
      .then(res => setProducts(res.data))
      .catch(console.error);
  }, [store, dateFilter]);

  function handleShowHistory(product) {
    window.location.href = `/history.html?name=${encodeURIComponent(product.name)}&store=${encodeURIComponent(product.store)}`;
  }

  const productNames = Array.from(new Set(products.map(p => p.name))).sort();
  const brandNames = Array.from(new Set(products.map(p => p.brand))).sort();

  // Apply all filters
  const filtered = products.filter(p =>
    (!selectedName || p.name === selectedName) &&
    (!brandFilter || p.brand === brandFilter)
  );

  function getAdjustedDate(base, offsetDays) {
    const date = base ? new Date(base) : new Date();
    date.setDate(date.getDate() + offsetDays);
    return date.toISOString().slice(0, 10);
  }

  return (
    <div className="p-4">
      <h1 className="text-xl mb-4">Price Comparator</h1>

      {/* Basket Section */}
      <h2 className="text-lg font-semibold mb-2">Basket</h2>
      <BasketBuilder
        productNames={productNames}
        products={products}
        onResult={setBasketResult}
        date={dateFilter}
      />
      {basketResult && <BasketSummary data={basketResult} />}

      {/* Price List Section */}
      <h2 className="text-lg font-semibold mb-2">Price List</h2>

      {/* Unified filter bar */}
      <div className="flex flex-wrap items-center gap-2 my-2">
        <select
          className="border px-2 py-1 rounded"
          value={store}
          onChange={e => setStore(e.target.value)}
        >
          <option value="">All stores</option>
          {Array.from(new Set(products.map(p => p.store))).sort().map(storeName => (
            <option key={storeName} value={storeName}>{storeName}</option>
          ))}
        </select>

        <select
          className="border px-2 py-1 rounded"
          value={selectedName}
          onChange={e => setSelectedName(e.target.value)}
        >
          <option value="">All products</option>
          {productNames.map(name => (
            <option key={name} value={name}>{name}</option>
          ))}
        </select>

        <select
          className="border px-2 py-1 rounded"
          value={brandFilter}
          onChange={e => setBrandFilter(e.target.value)}
        >
          <option value="">All brands</option>
          {brandNames.map(brand => (
            <option key={brand} value={brand}>{brand}</option>
          ))}
        </select>

        <button
          className="px-2 py-1 rounded bg-gray-200 hover:bg-gray-300"
          onClick={() => setDateFilter(getAdjustedDate(dateFilter, -1))}
          title="Previous Day"
        >
          &#8592;
        </button>
        <input
          type="date"
          className="border px-2 py-1 rounded"
          value={dateFilter}
          onChange={e => setDateFilter(e.target.value)}
        />
        <button
          className="px-2 py-1 rounded bg-gray-200 hover:bg-gray-300"
          onClick={() => setDateFilter(getAdjustedDate(dateFilter, 1))}
          title="Next Day"
        >
          &#8594;
        </button>
      </div>

      <ProductList products={filtered} onShowHistory={handleShowHistory} />
    </div>
  );
}
