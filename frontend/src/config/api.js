const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const buildApiUrl = (path) => {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${apiBaseUrl}${normalizedPath}`;
};

export default apiBaseUrl;
