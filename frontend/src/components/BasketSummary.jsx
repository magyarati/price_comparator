import React from 'react';

export default function BasketSummary({ data }) {
  const { totalCost, storeLists } = data || {};
  if (!storeLists) return null;

  // If you want to show "at <store>", get the store name (always 1 in normal optimize mode)
  const storeKeys = Object.keys(storeLists);

  return (
    <div className="mb-6">
      <h2 className="text-lg font-semibold mb-2">Basket Optimization Summary</h2>
      <p className="mb-4 text-gray-700" style={{ maxWidth: 600 }}>
        This section summarizes your basket optimization results.
        You can compare estimated total costs and itemized details per store, with unit prices normalized (per kg or per liter where appropriate).
        <br />
        Switch between <b>Optimize Basket</b> (cheapest single-store basket) and <b>Optimize Split Basket</b> (cheapest item-by-item, possibly across multiple stores) to see the different strategies.
      </p>
      <p className="mb-4">
        Total Estimated Cost:&nbsp;
        <strong>
          {totalCost?.toFixed(2)} {storeKeys.length > 0 ? storeLists[storeKeys[0]][0]?.currency || '' : ''}
        </strong>
        {/* Show 'at <store>' if only one store */}
        {storeKeys.length === 1 &&
          <span> at <span className="font-bold capitalize">{storeKeys[0]}</span></span>
        }
      </p>
      {Object.entries(storeLists).map(([store, items]) => {
        const storeTotal = items.reduce((sum, it) => sum + (it.cost || 0), 0);
        return (
          <div key={store} className="mb-4">
            <h3 className="font-medium underline capitalize">{store}</h3>
            <table className="w-full table-auto border-collapse mb-2">
              <thead>
                <tr className="bg-gray-100">
                  <th className="border p-1">Product</th>
                  <th className="border p-1">Category</th>
                  <th className="border p-1">Brand</th>
                  <th className="border p-1">Quantity</th>
                  <th className="border p-1">Package Quantity</th>
                  <th className="border p-1">Package Unit</th>
                  <th className="border p-1">Package Price</th>
                  <th className="border p-1">Unit Price</th>
                  <th className="border p-1">Unit</th>
                  <th className="border p-1">Cost</th>
                  <th className="border p-1">Currency</th>
                </tr>
              </thead>
              <tbody>
                {items.map((it, idx) => (
                  <tr key={idx}>
                    <td className="border p-1">{it.name}</td>
                    <td className="border p-1">{it.category || ''}</td>
                    <td className="border p-1">{it.brand || ''}</td>
                    <td className="border p-1">{it.quantity}</td>
                    <td className="border p-1">{it.packageQuantity}</td>
                    <td className="border p-1">{it.packageUnit}</td>
                    <td className="border p-1">{Number(it.packagePrice).toFixed(2)}</td>
                    <td className="border p-1">{Number(it.unitPrice).toFixed(2)}</td>
                    <td className="border p-1">{it.unit}</td>
                    <td className="border p-1">{Number(it.cost).toFixed(2)}</td>
                    <td className="border p-1">{it.currency}</td>
                  </tr>
                ))}
                <tr>
                  <td colSpan={9} className="border p-1 font-bold text-right">
                    Total for {store}:
                  </td>
                  <td className="border p-1 font-bold">{storeTotal.toFixed(2)}</td>
                  <td className="border p-1 font-bold">{items[0]?.currency || 'RON'}</td>
                </tr>
              </tbody>
            </table>
          </div>
        );
      })}
    </div>
  );
}
