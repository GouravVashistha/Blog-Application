import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { getAllPosts, getPostsByCategory, searchPosts } from '../services/postService';
import PostCard from '../components/PostCard';
import Sidebar from '../components/Sidebar';

const Home = () => {
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Pagination State (only used for get all posts)
  const [pageInfo, setPageInfo] = useState({
    pageNumber: 0,
    pageSize: 5,
    totalElements: 0,
    totalPages: 0,
    lastpage: true,
  });

  const [activeCategory, setActiveCategory] = useState(null);
  const location = useLocation();

  // Extract search query from URL
  const queryParams = new URLSearchParams(location.search);
  const searchQuery = queryParams.get('search') || '';

  useEffect(() => {
    loadPosts(0);
  }, [activeCategory, searchQuery]);

  const loadPosts = (page = 0) => {
    setLoading(true);
    setError('');

    if (searchQuery) {
      // Search mode
      setActiveCategory(null); // Clear category filter if searching
      searchPosts(searchQuery)
        .then((data) => {
          setPosts(data);
          setLoading(false);
        })
        .catch((err) => {
          console.error(err);
          setError('Failed to fetch search results.');
          setLoading(false);
        });
    } else if (activeCategory !== null) {
      // Category filter mode
      getPostsByCategory(activeCategory)
        .then((data) => {
          setPosts(data);
          setLoading(false);
        })
        .catch((err) => {
          console.error(err);
          setError('Failed to fetch category posts.');
          setLoading(false);
        });
    } else {
      // Default Paginated Feed
      getAllPosts(page, 5, 'postId', 'desc')
        .then((data) => {
          setPosts(data.content || []);
          setPageInfo({
            pageNumber: data.pageNumber,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastpage: data.lastpage,
          });
          setLoading(false);
        })
        .catch((err) => {
          console.error(err);
          setError('Failed to fetch blog posts.');
          setLoading(false);
        });
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pageInfo.totalPages) {
      loadPosts(newPage);
    }
  };

  const handleCategorySelect = (categoryId) => {
    // Clear URL search params if filtering by category
    if (location.search) {
      window.history.pushState({}, '', '/');
    }
    setActiveCategory(categoryId);
  };

  // Determine if pagination controls should be rendered
  const showPagination = !searchQuery && activeCategory === null;

  return (
    <div className="home-layout">
      {/* Left Column: Post Feed */}
      <main className="post-feed">
        <h2 style={{ fontSize: '1.8rem', marginBottom: '10px' }} className="gradient-text">
          {searchQuery ? `Search Results for "${searchQuery}"` : activeCategory ? 'Filtered Category Feed' : 'Latest Stories'}
        </h2>

        {error && <div className="alert alert-danger">{error}</div>}

        {loading ? (
          <div style={{ padding: '40px 0', textAlign: 'center', color: 'var(--text-muted)' }}>
            <h3>Loading stories...</h3>
          </div>
        ) : posts.length === 0 ? (
          <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
            <p style={{ fontSize: '1.2rem' }}>No blog posts found.</p>
          </div>
        ) : (
          <>
            {posts.map((post) => (
              <PostCard key={post.postId} post={post} />
            ))}

            {/* Pagination Controls */}
            {showPagination && pageInfo.totalPages > 1 && (
              <div className="pagination">
                <button
                  onClick={() => handlePageChange(pageInfo.pageNumber - 1)}
                  disabled={pageInfo.pageNumber === 0}
                  className="page-btn"
                >
                  ◀
                </button>
                
                {Array.from({ length: pageInfo.totalPages }).map((_, idx) => (
                  <button
                    key={idx}
                    onClick={() => handlePageChange(idx)}
                    className={`page-btn ${pageInfo.pageNumber === idx ? 'active' : ''}`}
                  >
                    {idx + 1}
                  </button>
                ))}

                <button
                  onClick={() => handlePageChange(pageInfo.pageNumber + 1)}
                  disabled={pageInfo.lastpage}
                  className="page-btn"
                >
                  ▶
                </button>
              </div>
            )}
          </>
        )}
      </main>

      {/* Right Column: Sidebar Widgets */}
      <Sidebar activeCategory={activeCategory} onCategorySelect={handleCategorySelect} />
    </div>
  );
};

export default Home;
