import { renderWithThemeProvider } from 'utils/test/test-utils';
import { CartProvider } from 'contexts/cart-context';
import { mockCartProducts } from 'utils/test/mocks';
import React from 'react';
import { render, screen } from '@testing-library/react';

import CartProduct from './CartProduct';

describe('[components] - CartProduct', () => {
  const setup = (props = {}) => {
    return renderWithThemeProvider(
      <CartProvider>
        <CartProduct product={mockCartProducts[0]} {...props} />
      </CartProvider>
    );
  };

  test('should render correctly', () => {
    const view = setup();
    expect(view).toMatchSnapshot();
  });

  it('renders error for invalid product', () => {
    // @ts-expect-error purposely passing invalid
    render(<CartProduct product={null} />);
    expect(screen.getByText(/invalid product data/i)).toBeInTheDocument();
  });

  it('renders product title', () => {
    const product = {
      id: 1,
      sku: 1,
      title: 'Test Product',
      price: 10,
      style: 'Red',
      currencyId: 'USD',
      currencyFormat: '$',
      availableSizes: ['M'],
      quantity: 1,
      description: '',
      installments: 0,
      isFreeShipping: false,
    };
    render(<CartProduct product={product} />);
    expect(screen.getByText(/test product/i)).toBeInTheDocument();
  });
});
