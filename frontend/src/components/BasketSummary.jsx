import React from 'react';

export default function BasketSummary({ data }) {
  const { totalCost, storeLists } = data;

  return (
    <div className="mb-6">
      <h2 className="text-lg font-semibold mb-2">Basket Optimization Summary</h2>
      <p className="mb-4">
        Total Estimated Cost:&nbsp;
        <strong>{totalCost.toFixed(2)}</strong>
      </p>

      {Object.entries(storeLists).map(([store, items]) => (
        <div key={store} className="mb-4">
          <h3 className="font-medium underline capitalize">{store}</h3>
          <table className="w-full table-auto border-collapse mb-2">
            <thead>
              <tr className="bg-gray-100">
                <th className="border p-1">Product</th>
                <th className="border p-1">Qty</th>
                <th className="border p-1">Unit Price</th>
                <th className="border p-1">Cost</th>
              </tr>
            </thead>
            <tbody>
              {items.map((it, idx) => (
                <tr key={idx}>
                  <td className="border p-1">{it.name}</td>
                  <td className="border p-1">{it.quantity}</td>
                  <td className="border p-1">{it.unitPrice.toFixed(2)}</td>
                  <td className="border p-1">{it.cost.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
}