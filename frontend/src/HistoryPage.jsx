import React from 'react';
import PriceHistoryChart from './components/PriceHistoryChart';

export default function HistoryPage() {
  const params = new URLSearchParams(window.location.search);
  const name = params.get('name');
  const store = params.get('store');
  const product = name && store ? { name, store } : null;

  return (
    <div style={{ padding: 24 }}>
      <h1>Dynamic Price History Graphs</h1>
      {product ? (
        <PriceHistoryChart product={product} />
      ) : (
        <div style={{ color: 'red' }}>No product selected.</div>
      )}
      <a href="index.html">&larr; Back to Product List</a>
    </div>
  );
}
