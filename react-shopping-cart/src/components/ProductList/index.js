import React, { useMemo, Profiler } from 'react';

const ProductList = ({ products, filters }) => { 
    // Memoize expensive filtering
    const filteredProducts = useMemo(() => {
        return products.filter(product => 
            filters.sizes.every(size => product.availableSizes.includes(size)) &&
            product.price <= filters.maxPrice
        );
    }, [products, filters.sizes, filters.maxPrice]);

    // Performance monitoring callback
    const onRenderCallback = (
        id, phase, actualDuration, baseDuration, startTime, commitTime, interactions
    ) => {
        // You can log or send these metrics to an analytics endpoint
        console.log(`[Profiler][${id}]`, { phase, actualDuration, baseDuration, startTime, commitTime, interactions });
    };

    // Memoized ProductItem (assuming it is globally available)
    const MemoizedProductItem = React.memo(ProductItem);

    return (
        <Profiler id="ProductList" onRender={onRenderCallback}>
            <div> 
                {filteredProducts.map(product => ( 
                    <MemoizedProductItem key={product.id} product={product} /> 
                ))} 
            </div> 
        </Profiler>
    ); 
}; 
    