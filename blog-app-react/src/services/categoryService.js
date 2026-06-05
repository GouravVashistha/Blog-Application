import api from './api';

export const getCategories = async () => {
  const response = await api.get('/api/category');
  return response.data;
};

export const getCategoryById = async (categoryId) => {
  const response = await api.get(`/api/category/${categoryId}`);
  return response.data;
};

export const createCategory = async (categoryData) => {
  const response = await api.post('/api/category', categoryData);
  return response.data;
};

export const updateCategory = async (categoryData, categoryId) => {
  const response = await api.put(`/api/category/${categoryId}`, categoryData);
  return response.data;
};

export const deleteCategory = async (categoryId) => {
  const response = await api.delete(`/api/category/${categoryId}`);
  return response.data;
};
