import React from 'react';

export default function ProductList({ products, onShowHistory }) {
  return (
    <table className="w-full table-auto border-collapse">
      <thead>
        <tr className="bg-gray-200">
          <th className="border p-2">Store</th>
          <th className="border p-2">Name</th>
          <th className="border p-2">Category</th>
          <th className="border p-2">Brand</th>
          <th className="border p-2">Qty</th>
          <th className="border p-2">Unit</th>
          <th className="border p-2">Price</th>
          <th className="border p-2">Currency</th>
          <th className="border p-2">Discount%</th>
          <th className="border p-2">Price w/ Discount</th>
          <th className="border p-2">History</th>
        </tr>
      </thead>
      <tbody>
        {products.map((p, i) => {
          const discountedPrice = (p.price * (1 - p.discount / 100)).toFixed(2);
          return (
            <tr key={i}>
              <td className="border p-1">{p.store}</td>
              <td className="border p-1">{p.name}</td>
              <td className="border p-1">{p.category}</td>
              <td className="border p-1">{p.brand}</td>
              <td className="border p-1">{p.grammage}</td>
              <td className="border p-1">{p.unit}</td>
              <td className="border p-1">{p.price}</td>
              <td className="border p-1">{p.currency}</td>
              <td className="border p-1">{p.discount}</td>
              <td className="border p-1">{discountedPrice}</td>
              <td className="border p-1">
                <button
                  className="px-2 py-1 bg-indigo-500 text-white rounded"
                  onClick={() => onShowHistory(p)}
                >
                  History
                </button>
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}