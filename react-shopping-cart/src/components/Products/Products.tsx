// Best Practice: Memoize Products for performance and handle empty product lists gracefully.
import { IProduct } from 'models';
import Product from './Product';
import React from 'react';
import * as S from './style';

interface IProps {
  products: IProduct[];
}

const Products = React.memo(({ products }: IProps) => {
  if (!products || products.length === 0) {
    return <div>No products available.</div>;
  }
  return (
    <S.Container>
      {products.map((p) => (
        <Product product={p} key={p.sku} />
      ))}
    </S.Container>
  );
});

export default Products;
