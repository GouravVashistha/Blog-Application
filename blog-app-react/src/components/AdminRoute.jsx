import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const AdminRoute = ({ children }) => {
  const { isLoggedIn, user, loading } = useContext(AuthContext);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '50vh', color: 'var(--text-muted)' }}>
        <h3>Loading session...</h3>
      </div>
    );
  }

  const isAdmin = user && user.roles && user.roles.some((r) => r.name === 'ADMIN_USER');

  return isLoggedIn && isAdmin ? children : <Navigate to="/login" replace />;
};

export default AdminRoute;
