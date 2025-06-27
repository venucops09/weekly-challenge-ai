import React, { useMemo, Profiler } from 'react';
import PropTypes from 'prop-types';

const ProductList = ({ products, filters, loading, error }) => {
  // Default products to an empty array if undefined
  const safeProducts = Array.isArray(products) ? products : [];

  // Memoize expensive filtering
  const filteredProducts = useMemo(() => {
    return safeProducts.filter(
      (product) =>
        filters.sizes.every((size) => product.availableSizes.includes(size)) &&
        product.price <= filters.maxPrice,
    );
  }, [safeProducts, filters.sizes, filters.maxPrice]);

  // Performance monitoring callback
  const onRenderCallback = (
    id,
    phase,
    actualDuration,
    baseDuration,
    startTime,
    commitTime,
    interactions,
  ) => {
    // You can log or send these metrics to an analytics endpoint
    console.log(`[Profiler][${id}]`, {
      phase,
      actualDuration,
      baseDuration,
      startTime,
      commitTime,
      interactions,
    });
  };

  // Memoized ProductItem (assuming it is globally available)
  const MemoizedProductItem = React.memo(ProductItem);

  // Error and loading handling
  if (error) return <div className="error">Error loading products.</div>;
  if (loading) return <div className="loading">Loading products...</div>;
  if (!filteredProducts.length) return <div>No products found.</div>;

  return (
    <Profiler id="ProductList" onRender={onRenderCallback}>
      <div>
        {filteredProducts.map((product) => (
          <MemoizedProductItem key={product.id} product={product} />
        ))}
      </div>
    </Profiler>
  );
};

// PropTypes for better type safety
ProductList.propTypes = {
  products: PropTypes.array,
  filters: PropTypes.shape({
    sizes: PropTypes.array.isRequired,
    maxPrice: PropTypes.number.isRequired,
  }).isRequired,
  loading: PropTypes.bool,
  error: PropTypes.any,
};

export default ProductList;
