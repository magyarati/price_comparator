import React, { useState } from 'react';
import axios from 'axios';

export default function BasketBuilder({ productNames, products, onResult, date }) {
  const [lines, setLines] = useState([{ name: '', qty: '' }]);

  const addLine = () => setLines(prev => [...prev, { name: '', qty: '' }]);

  // New: Remove line by index
  const removeLine = (index) => {
    setLines(prev => prev.length > 1 ? prev.filter((_, i) => i !== index) : prev);
  };

  const updateLine = (index, field, value) => {
    const updated = [...lines];
    updated[index][field] = value;
    setLines(updated);
  };

  const optimize = () => {
    const items = lines.map(line => ({
      productName: line.name,
      quantity: parseFloat(line.qty) || 0,
    }));
    axios
      .post('/api/basket/optimize', { items, date })
      .then(response => onResult(response.data))
      .catch(console.error);
  };

  return (
    <div className="mb-4">
      {lines.map((line, i) => {
        const prod = products.find(p => p.name === line.name);
        const unit = prod ? prod.unit : '';
        return (
          <div key={i} className="flex items-center space-x-2 mb-2">
            <select
              className="border p-1 flex-1"
              value={line.name}
              onChange={e => updateLine(i, 'name', e.target.value)}
            >
              <option value="">Select product</option>
              {productNames.map(name => {
                const p = products.find(pr => pr.name === name);
                const optUnit = p ? p.unit : '';
                return (
                  <option key={name} value={name}>
                    {name}{optUnit ? ` (${optUnit})` : ''}
                  </option>
                );
              })}
            </select>
            <input
              type="number"
              className="border p-1 w-20"
              placeholder="Qty"
              value={line.qty}
              onChange={e => updateLine(i, 'qty', e.target.value)}
            />
            <span className="text-gray-700 min-w-[40px]">
              {prod ? `buc ${prod.grammage} ${prod.unit}` : ''}
            </span>
            {/* Add remove button (hide if only 1 line) */}
            {lines.length > 1 && (
              <button
                onClick={() => removeLine(i)}
                className="text-red-600 font-bold px-2"
                title="Remove line"
                type="button"
              >
                &times;
              </button>
            )}
          </div>
        );
      })}
      <button onClick={addLine} className="mr-2 px-3 py-1 bg-blue-600 text-white rounded">
        + Line
      </button>
      <button onClick={optimize} className="px-3 py-1 bg-green-600 text-white rounded">
        Optimize Basket
      </button>
    </div>
  );
}
