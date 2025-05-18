import React from 'react';

export default function Filters({
  store, setStore,
  selectedName, setSelectedName,
  dateFilter, setDateFilter,
  productNames
}) {
  return (
    <div className="mb-4 flex space-x-4">
      <select value={store} onChange={e => setStore(e.target.value)} className="border rounded p-1">
        <option value="">All Stores</option>
        <option value="lidl">Lidl</option>
        <option value="kaufland">Kaufland</option>
        <option value="profi">Profi</option>
      </select>
      <select value={selectedName} onChange={e => setSelectedName(e.target.value)} className="border rounded p-1">
        <option value="">All Products</option>
        {productNames.map(n => <option key={n} value={n}>{n}</option>)}
      </select>
      <input type="date" value={dateFilter} onChange={e => setDateFilter(e.target.value)} className="border rounded p-1" />
    </div>
  );
}