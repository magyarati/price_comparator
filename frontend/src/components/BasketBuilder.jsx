import React, { useState } from 'react';
import axios from 'axios';

export default function BasketBuilder({ productNames, onResult }) {
  const [lines, setLines] = useState([{ name: '', qty: '' }]);

  const addLine = () => setLines(prev => [...prev, { name: '', qty: '' }]);

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
      .post('/api/basket/optimize', { items })
      .then(response => onResult(response.data))
      .catch(console.error);
  };

  return (
    <div className="mb-4">
      {lines.map((line, i) => (
        <div key={i} className="flex space-x-2 mb-2">
          <select
            className="border p-1 flex-1"
            value={line.name}
            onChange={e => updateLine(i, 'name', e.target.value)}
          >
            <option value="">Select product</option>
            {productNames.map(name => (
              <option key={name} value={name}>{name}</option>
            ))}
          </select>
          <input
            type="number"
            className="border p-1 w-20"
            placeholder="Qty"
            value={line.qty}
            onChange={e => updateLine(i, 'qty', e.target.value)}
          />
        </div>
      ))}
      <button onClick={addLine} className="mr-2 px-3 py-1 bg-blue-600 text-white rounded">
        + Line
      </button>
      <button onClick={optimize} className="px-3 py-1 bg-green-600 text-white rounded">
        Optimize Basket
      </button>
    </div>
  );
}