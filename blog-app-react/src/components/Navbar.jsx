import React, { useContext, useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
  const { isLoggedIn, user, logoutUser } = useContext(AuthContext);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  const isAdmin = user && user.roles && user.roles.some((r) => r.name === 'ADMIN_USER');

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/?search=${encodeURIComponent(searchQuery.trim())}`);
    } else {
      navigate('/');
    }
  };

  const handleLogout = () => {
    logoutUser();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="logo">
          <span style={{ color: 'var(--primary)' }}>🚀</span>
          <span className="gradient-text">BlogSpace</span>
        </Link>

        {/* Search Bar */}
        <form onSubmit={handleSearchSubmit} className="search-bar">
          <input
            type="text"
            placeholder="Search posts..."
            className="search-input"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit" style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}>
            🔍
          </button>
        </form>

        <ul className="nav-menu">
          <li>
            <Link to="/" className={`nav-item ${isActive('/') ? 'active' : ''}`}>
              Home
            </Link>
          </li>
          
          {isLoggedIn ? (
            <>
              <li>
                <Link to="/create-post" className={`nav-item ${isActive('/create-post') ? 'active' : ''}`}>
                  Create Post
                </Link>
              </li>
              <li>
                <Link to="/profile" className={`nav-item ${isActive('/profile') ? 'active' : ''}`}>
                  Dashboard
                </Link>
              </li>
              <li className="nav-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span className="author-avatar" style={{ width: '24px', height: '24px', fontSize: '0.75rem' }}>
                  {user?.name ? user.name[0].toUpperCase() : 'U'}
                </span>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  Hi, {user?.name || 'User'}
                  {isAdmin && <span style={{ fontSize: '0.7rem', background: 'rgba(244, 63, 94, 0.15)', color: 'var(--accent)', padding: '1px 5px', borderRadius: '4px', border: '1px solid rgba(244, 63, 94, 0.3)' }}>Admin</span>}
                </span>
              </li>
              <li>
                <button onClick={handleLogout} className="btn btn-secondary btn-sm">
                  Logout
                </button>
              </li>
            </>
          ) : (
            <>
              <li>
                <Link to="/login" className="btn btn-secondary btn-sm">
                  Login
                </Link>
              </li>
              <li>
                <Link to="/register" className="btn btn-primary btn-sm">
                  Register
                </Link>
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
