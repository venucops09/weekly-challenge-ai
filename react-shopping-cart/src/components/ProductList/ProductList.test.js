import React from 'react';
import { render, screen } from '@testing-library/react';
import ProductList from './index';

describe('ProductList', () => {
  const filters = { sizes: [], maxPrice: 1000 };
  it('renders loading state', () => {
    render(<ProductList products={[]} filters={filters} loading={true} />);
    expect(screen.getByText(/loading products/i)).toBeInTheDocument();
  });
  it('renders error state', () => {
    render(
      <ProductList products={[]} filters={filters} error={new Error('fail')} />,
    );
    expect(screen.getByText(/error loading products/i)).toBeInTheDocument();
  });
  it('renders empty state', () => {
    render(<ProductList products={[]} filters={filters} />);
    expect(screen.getByText(/no products found/i)).toBeInTheDocument();
  });
  it('renders products', () => {
    const products = [
      {
        id: 1,
        availableSizes: [],
        price: 10,
        name: 'Test',
        title: 'Test',
        sku: 1,
      },
    ];
    render(<ProductList products={products} filters={filters} />);
    expect(screen.getByText(/test/i)).toBeInTheDocument();
  });
});
