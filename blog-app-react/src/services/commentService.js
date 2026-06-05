import api from './api';

export const createComment = async (commentData, postId, userId) => {
  const response = await api.post(`/post/${postId}/comments/${userId}`, commentData);
  return response.data;
};

export const deleteComment = async (commentId) => {
  const response = await api.delete(`/comments/${commentId}`);
  return response.data;
};
