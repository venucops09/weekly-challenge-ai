import formatPrice from 'utils/formatPrice';
import { ICartProduct } from 'models';
import React, { useState, useEffect } from 'react';
import { useCart } from 'contexts/cart-context';
import * as S from './style';

interface IProps {
  product: ICartProduct;
}
const FALLBACK_IMAGE = '/static/img/fallback.png';

const CartProduct = React.memo(({ product }: IProps) => {
  const { removeProduct, increaseProductQuantity, decreaseProductQuantity } = useCart();
  if (!product || !product.sku || !product.title) {
    return <div style={{ color: 'red' }}>Invalid product data.</div>;
  }
  const {
    sku,
    title,
    price,
    style,
    currencyId,
    currencyFormat,
    availableSizes,
    quantity,
  } = product;

  const [imgUrl, setImgUrl] = useState<string>("");

  useEffect(() => {
    // Try to load the main cart product image
    const img = new window.Image();
    const url = require(`static/products/${sku}-1-cart.webp`);
    img.src = url;
    img.onload = () => setImgUrl(url);
    img.onerror = () => setImgUrl(FALLBACK_IMAGE);
  }, [sku]);

  const handleRemoveProduct = () => removeProduct(product);
  const handleIncreaseProductQuantity = () => increaseProductQuantity(product);
  const handleDecreaseProductQuantity = () => decreaseProductQuantity(product);

  return (
    <S.Container>
      <S.DeleteButton
        onClick={handleRemoveProduct}
        title="remove product from cart"
      />
      <S.Image
        src={imgUrl}
        alt={title}
      />
      <S.Details>
        <S.Title>{title}</S.Title>
        <S.Desc>
          {`${availableSizes[0]} | ${style}`} <br />
          Quantity: {quantity}
        </S.Desc>
      </S.Details>
      <S.Price>
        <p>{`${currencyFormat}  ${formatPrice(price, currencyId)}`}</p>
        <div>
          <S.ChangeQuantity
            onClick={handleDecreaseProductQuantity}
            disabled={quantity === 1 ? true : false}
          >
            -
          </S.ChangeQuantity>
          <S.ChangeQuantity onClick={handleIncreaseProductQuantity}>
            +
          </S.ChangeQuantity>
        </div>
      </S.Price>
    </S.Container>
  );
});

export default CartProduct;
