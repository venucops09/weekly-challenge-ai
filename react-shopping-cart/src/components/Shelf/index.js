// Found in the actual codebase - State mutation bug
// FIXED: addProduct now uses immutable update patterns and avoids mutating props or state
const addProduct = (product) => {
  setProducts(prevProducts => {
    const productAlreadyInCart = prevProducts.find(p => p.id === product.id);
    if (productAlreadyInCart) {
      // Return a new array with the updated product (immutably)
      return prevProducts.map(p =>
        p.id === product.id
          ? { ...p, quantity: p.quantity + 1 }
          : p
      );
    } else {
      // Add a new product object (do not mutate the original)
      return [...prevProducts, { ...product, quantity: 1 }];
    }
  });
};
    