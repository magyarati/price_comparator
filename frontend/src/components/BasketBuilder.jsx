import React, { useState } from 'react';
import axios from 'axios';

export default function BasketBuilder({ productNames, products, onResult, date }) {
  const [lines, setLines] = useState([{ name: '', qty: '' }]);
  const [loading, setLoading] = useState(false);

  const addLine = () => setLines(prev => [...prev, { name: '', qty: '' }]);

  // Remove line by index
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
    setLoading(true);
    axios
      .post('/api/basket/optimize', { items, date })
      .then(response => onResult(response.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  // NEW: Optimize Split Basket
  const optimizeSplit = () => {
    const items = lines.map(line => ({
      productName: line.name,
      quantity: parseFloat(line.qty) || 0,
    }));
    setLoading(true);
    axios
      .post('/api/basket/optimize-split', { items, date })
      .then(response => onResult(response.data))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  return (
    <div className="mb-4">
      {lines.map((line, i) => {
        return (
          <div key={i} className="flex items-center space-x-2 mb-2">
            <select
              className="border p-1 flex-1"
              value={line.name}
              onChange={e => updateLine(i, 'name', e.target.value)}
            >
              <option value="">Select product</option>
              {productNames.map(name => (
                <option key={name} value={name}>
                  {name}
                </option>
              ))}
            </select>
            <input
              type="number"
              className="border p-1 w-20"
              placeholder="Quantity"
              value={line.qty}
              onChange={e => updateLine(i, 'qty', e.target.value)}
            />
            {/* Remove button (hide if only 1 line) */}
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
      <button
        onClick={optimize}
        className="px-3 py-1 bg-green-600 text-white rounded mr-2"
        disabled={loading}
      >
        Optimize Basket
      </button>
      <button
        onClick={optimizeSplit}
        className="px-3 py-1 bg-green-700 text-white rounded"
        disabled={loading}
      >
        Optimize Split Basket
      </button>
    </div>
  );
}
