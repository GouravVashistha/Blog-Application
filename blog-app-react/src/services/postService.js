import api, { BASE_URL } from './api';

export const getAllPosts = async (pageNumber = 0, pageSize = 5, sortBy = 'postId', sortDir = 'asc') => {
  const response = await api.get(`/api/allposts`, {
    params: { pageNumber, pageSize, sortBy, sortDir },
  });
  return response.data;
};

export const getPostById = async (postId) => {
  const response = await api.get(`/api/GetPost/${postId}`);
  return response.data;
};

export const createPost = async (postData, userId, categoryId) => {
  const response = await api.post(`/api/user/${userId}/category/${categoryId}/posts`, postData);
  return response.data;
};

export const updatePost = async (postData, postId) => {
  const response = await api.put(`/api/posts/${postId}`, postData);
  return response.data;
};

export const deletePost = async (postId) => {
  const response = await api.delete(`/api/deletePost/${postId}`);
  return response.data;
};

export const getPostsByCategory = async (categoryId) => {
  const response = await api.get(`/api/category/${categoryId}/posts`);
  return response.data;
};

export const getPostsByUser = async (userId) => {
  const response = await api.get(`/api/user/${userId}/posts`);
  return response.data;
};

export const searchPosts = async (keywords) => {
  const response = await api.get(`/api/post/search/${keywords}`);
  return response.data;
};

export const uploadPostImage = async (imageFile, postId) => {
  const formData = new FormData();
  formData.append('image', imageFile);
  const response = await api.post(`/api/post/image/upload/${postId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};

export const getImageUrl = (imageName) => {
  if (!imageName) return `${BASE_URL}/api/post/image/default.png`;
  return `${BASE_URL}/api/post/image/${imageName}`;
};
