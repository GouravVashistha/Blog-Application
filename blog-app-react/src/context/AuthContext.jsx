import React, { createContext, useState, useEffect } from 'react';
import { login as apiLogin, register as apiRegister, logout as apiLogout, getCurrentUserProfile, getStoredUser } from '../services/authService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');
    const storedUserProfile = getStoredUser();

    if (storedToken && storedUsername) {
      setToken(storedToken);
      if (storedUserProfile) {
        setUser(storedUserProfile);
      } else {
        // Fetch from API if details are missing
        getCurrentUserProfile(storedUsername)
          .then((profile) => setUser(profile))
          .catch(() => logoutUser());
      }
    }
    setLoading(false);
  }, []);

  const loginUser = async (username, password) => {
    setLoading(true);
    try {
      const data = await apiLogin({ username, password });
      setToken(data.token);
      
      // Load user profile detail
      const profile = await getCurrentUserProfile(username);
      setUser(profile);
      setLoading(false);
      return profile;
    } catch (error) {
      setLoading(false);
      throw error;
    }
  };

  const registerUser = async (userData) => {
    try {
      return await apiRegister(userData);
    } catch (error) {
      throw error;
    }
  };

  const logoutUser = () => {
    apiLogout();
    setUser(null);
    setToken(null);
  };

  const refreshProfile = async () => {
    if (user && user.email) {
      try {
        const updatedProfile = await getCurrentUserProfile(user.email);
        setUser(updatedProfile);
        return updatedProfile;
      } catch (e) {
        console.error("Failed to refresh user profile data", e);
      }
    }
  };

  const isLoggedIn = !!token;

  return (
    <AuthContext.Provider value={{ user, token, isLoggedIn, loginUser, registerUser, logoutUser, refreshProfile, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
