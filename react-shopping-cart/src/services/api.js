// Enhanced API functions with robust error handling, retry, and loading state management

const MAX_RETRIES = 3;
const RETRY_DELAY = 1000; // ms

const handleResponse = async (response) => {
  if (!response.ok) {
    let errorMsg = `HTTP error! Status: ${response.status}`;
    try {
      const errorData = await response.json();
      errorMsg = errorData.message || errorMsg;
    } catch {}
    throw new Error(errorMsg);
  }
  return response.json();
};

const delay = (ms) => new Promise(res => setTimeout(res, ms));

export const fetchProducts = async () => {
  let attempt = 0;
  let lastError = null;
  while (attempt < MAX_RETRIES) {
    try {
      const response = await fetch('/api/products');
      const data = await handleResponse(response);
      return { data, loading: false, error: null };
    } catch (error) {
      lastError = error;
      attempt++;
      if (attempt < MAX_RETRIES) await delay(RETRY_DELAY);
    }
  }
  return {
    data: null,
    loading: false,
    error: lastError?.message || 'Failed to fetch products. Please try again later.'
  };
};

export const processPayment = async (paymentData) => {
  let attempt = 0;
  let lastError = null;
  while (attempt < MAX_RETRIES) {
    try {
      const response = await fetch('/api/payment', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(paymentData)
      });
      const data = await handleResponse(response);
      return { data, loading: false, error: null };
    } catch (error) {
      lastError = error;
      attempt++;
      if (attempt < MAX_RETRIES) await delay(RETRY_DELAY);
    }
  }
  return {
    data: null,
    loading: false,
    error: lastError?.message || 'Payment failed. Please try again later.'
  };
}; 