import api from './api';

export const register = async (userData) => {
  const response = await api.post('/api/v1/auth/register', userData);
  return response.data;
};

export const login = async (loginData) => {
  // loginData has { username, password }
  const response = await api.post('/api/v1/auth/login', loginData);
  if (response.data && response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('username', response.data.username);
  }
  return response.data;
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  localStorage.removeItem('user');
};

export const getCurrentUserProfile = async (email) => {
  const response = await api.get('/api/users/AllUsers');
  const allUsers = response.data;
  const user = allUsers.find((u) => u.email === email);
  if (user) {
    localStorage.setItem('user', JSON.stringify(user));
  }
  return user;
};

export const getStoredUser = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (e) {
      return null;
    }
  }
  return null;
};
