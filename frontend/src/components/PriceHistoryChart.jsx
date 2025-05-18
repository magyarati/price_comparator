import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts';

export default function PriceHistoryChart({ product }) {
  const [history, setHistory] = useState([]);

  useEffect(() => {
    if (!product) return;
    axios
      .get('/api/products/history', {
        params: { store: product.store, name: product.name },
      })
      .then(res => {
        const sorted = res.data.sort((a, b) => new Date(a.date) - new Date(b.date));
        setHistory(sorted);
      })
      .catch(console.error);
  }, [product]);

  if (!product) return null;

  return (
    <div className="mt-6">
      <h2 className="text-lg font-semibold mb-2">
        Price History for {product.name} at {product.store}
      </h2>
      {history.length === 0 ? (
        <p>No historical data available.</p>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={history} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="date" />
            <YAxis />
            <Tooltip />
            <Line type="monotone" dataKey="price" dot={{ r: 3 }} />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}