import { KeyboardEvent, useState, useEffect } from 'react';

import formatPrice from 'utils/formatPrice';
import { IProduct } from 'models';

import { useCart } from 'contexts/cart-context';

import * as S from './style';

interface IProps {
  product: IProduct;
}

const FALLBACK_IMAGE = '/static/img/fallback.png';

const Product = ({ product }: IProps) => {
  const { openCart, addProduct } = useCart();
  if (!product || !product.sku || !product.title) {
    return <div style={{ color: 'red' }}>Invalid product data.</div>;
  }
  const {
    sku,
    title,
    price,
    installments,
    currencyId,
    currencyFormat,
    isFreeShipping
  } = product;

  const [imgError, setImgError] = useState(false);
  const [imgUrl, setImgUrl] = useState<string>("");

  useEffect(() => {
    // Try to load the main product image
    const img = new window.Image();
    const url = require(`static/products/${sku}-1-product.webp`);
    img.src = url;
    img.onload = () => setImgUrl(url);
    img.onerror = () => {
      setImgError(true);
      setImgUrl(FALLBACK_IMAGE);
    };
  }, [sku]);

  const formattedPrice = formatPrice(price, currencyId);
  let productInstallment;

  if (installments) {
    const installmentPrice = price / installments;

    productInstallment = (
      <S.Installment>
        <span>or {installments} x</span>
        <b>
          {currencyFormat}
          {formatPrice(installmentPrice, currencyId)}
        </b>
      </S.Installment>
    );
  }

  const handleAddProduct = () => {
    addProduct({ ...product, quantity: 1 });
    openCart();
  };

  const handleAddProductWhenEnter = (event: KeyboardEvent) => {
    if (event.key === 'Enter' || event.code === 'Space') {
      addProduct({ ...product, quantity: 1 });
      openCart();
    }
  };

  return (
    <S.Container onKeyUp={handleAddProductWhenEnter} sku={sku} tabIndex={1}>
      {isFreeShipping && <S.Stopper>Free shipping</S.Stopper>}
      <S.Image alt={title} style={{ backgroundImage: `url(${imgUrl})` }} />
      <S.Title>{title}</S.Title>
      <S.Price>
        <S.Val>
          <small>{currencyFormat}</small>
          <b>{formattedPrice.substring(0, formattedPrice.length - 3)}</b>
          <span>{formattedPrice.substring(formattedPrice.length - 3)}</span>
        </S.Val>
        {productInstallment}
      </S.Price>
      <S.BuyButton onClick={handleAddProduct} tabIndex={-1}>
        Add to cart
      </S.BuyButton>
    </S.Container>
  );
};

export default Product;
