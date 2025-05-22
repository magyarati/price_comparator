import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Filters from './components/Filters';
import BasketBuilder from './components/BasketBuilder';
import BasketSummary from './components/BasketSummary';
import ProductList from './components/ProductList';
import PriceHistoryChart from './components/PriceHistoryChart';

export default function App() {
  const [products, setProducts] = useState([]);
  const [store, setStore] = useState('');
  const [selectedName, setSelectedName] = useState('');
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

    // Handler for History button - redirects to history.html with productId
    function handleShowHistory(product) {
      window.location.href = `/history.html?name=${encodeURIComponent(product.name)}&store=${encodeURIComponent(product.store)}`;
    }

  const productNames = Array.from(new Set(products.map(p => p.name))).sort();
  const filtered = products.filter(p => !selectedName || p.name === selectedName);

  return (
    <div className="p-4">
      <h1 className="text-xl mb-4">Price Comparator</h1>

      <BasketBuilder
        productNames={productNames}
        onResult={setBasketResult}
      />
      {basketResult && <BasketSummary data={basketResult} />}

      <Filters
        store={store}
        setStore={setStore}
        selectedName={selectedName}
        setSelectedName={setSelectedName}
        dateFilter={dateFilter}
        setDateFilter={setDateFilter}
        productNames={productNames}
      />

      <ProductList products={filtered} onShowHistory={handleShowHistory} />

    </div>
  );
}
