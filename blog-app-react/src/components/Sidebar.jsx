import React, { useState, useEffect } from 'react';
import { getCategories } from '../services/categoryService';

const Sidebar = ({ activeCategory, onCategorySelect }) => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getCategories()
      .then((data) => {
        setCategories(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to load categories', err);
        setLoading(false);
      });
  }, []);

  return (
    <aside className="sidebar">
      <div className="sidebar-widget glass-panel">
        <h3 className="widget-title">Categories</h3>
        {loading ? (
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Loading categories...</p>
        ) : (
          <ul className="category-list">
            <li
              className={`category-item ${activeCategory === null ? 'active' : ''}`}
              onClick={() => onCategorySelect(null)}
            >
              <span>🌐 All Categories</span>
            </li>
            {categories.map((cat) => (
              <li
                key={cat.categoryId}
                className={`category-item ${activeCategory === cat.categoryId ? 'active' : ''}`}
                onClick={() => onCategorySelect(cat.categoryId)}
              >
                <span>📁 {cat.categoryTitle}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
      
      <div className="sidebar-widget glass-panel" style={{ padding: '20px' }}>
        <h3 className="widget-title">About BlogSpace</h3>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.6' }}>
          Welcome to BlogSpace, a premium writing community. Express yourself, share your knowledge, and connect with people from around the world.
        </p>
      </div>
    </aside>
  );
};

export default Sidebar;
