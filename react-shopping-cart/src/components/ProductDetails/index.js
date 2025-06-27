// Security-hardened ProductDetails component
import DOMPurify from 'dompurify';
import React from 'react';

const isValidImageUrl = (url) => {
  // Basic validation: must be http(s) and image extension
  try {
    const parsed = new URL(url, window.location.origin);
    return (
      (parsed.protocol === 'http:' || parsed.protocol === 'https:') &&
      /\.(jpg|jpeg|png|gif|webp|svg)$/i.test(parsed.pathname)
    );
  } catch {
    return false;
  }
};

const isValidId = (id) => typeof id === 'string' || typeof id === 'number';

const ProductDetails = ({ product }) => {
  // Sanitize HTML to prevent XSS
  const safeDescription = DOMPurify.sanitize(product.description || '');
  // Validate image URL
  const imageUrl = isValidImageUrl(product.imageUrl) ? product.imageUrl : '/placeholder.png';
  // Validate product id for navigation
  const handleViewDetails = () => {
    if (isValidId(product.id)) {
      window.location.href = `/product/${encodeURIComponent(product.id)}`;
    } else {
      alert('Invalid product ID.');
    }
  };

  return (
    <div>
      <h2>{product.title}</h2>
      {/* Secure HTML rendering with sanitization */}
      <div dangerouslySetInnerHTML={{ __html: safeDescription }} />
      {/* Validated image source */}
      <img src={imageUrl} alt={product.title} onError={e => { e.target.src = '/placeholder.png'; }} />
      <button onClick={handleViewDetails}>
        View Details
      </button>
    </div>
  );
};

export default ProductDetails; 
    