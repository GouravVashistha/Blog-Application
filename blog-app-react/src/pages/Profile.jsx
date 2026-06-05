import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { getPostsByUser, deletePost, getAllPosts } from '../services/postService';
import PostCard from '../components/PostCard';

const Profile = () => {
  const { user } = useContext(AuthContext);
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('my-posts'); // 'my-posts' or 'all-posts'

  const isAdmin = user && user.roles && user.roles.some((r) => r.name === 'ADMIN_USER');

  useEffect(() => {
    if (user && user.id) {
      if (activeTab === 'my-posts') {
        loadUserPosts();
      } else {
        loadAllSystemPosts();
      }
    }
  }, [user, activeTab]);

  const loadUserPosts = () => {
    setLoading(true);
    setError('');
    getPostsByUser(user.id)
      .then((data) => {
        setPosts(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to fetch your blog posts.');
        setLoading(false);
      });
  };

  const loadAllSystemPosts = () => {
    setLoading(true);
    setError('');
    // Fetch a large page size of posts to list system-wide content
    getAllPosts(0, 1000, 'postId', 'desc')
      .then((data) => {
        setPosts(data.content || []);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to fetch system-wide blog posts.');
        setLoading(false);
      });
  };

  const handleDeletePost = async (postId) => {
    if (window.confirm('Are you sure you want to delete this blog post? This action cannot be undone.')) {
      try {
        await deletePost(postId);
        // Remove from local feed state
        setPosts((prev) => prev.filter((p) => p.postId !== postId));
        alert('Post deleted successfully!');
      } catch (err) {
        console.error(err);
        alert('Failed to delete the post.');
      }
    }
  };

  if (!user) {
    return (
      <div style={{ padding: '60px 0', textAlign: 'center', color: 'var(--text-muted)' }}>
        <h2>Loading profile dashboard...</h2>
      </div>
    );
  }

  return (
    <div>
      {/* Profile Info panel */}
      <section className="profile-header glass-panel">
        <div className="profile-avatar">
          {user.name ? user.name[0].toUpperCase() : 'U'}
        </div>
        <div className="profile-details">
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '6px' }}>
            <h2 style={{ margin: 0 }}>{user.name}</h2>
            {isAdmin && (
              <span className="category-tag" style={{ background: 'rgba(244, 63, 94, 0.15)', color: 'var(--accent)', fontSize: '0.75rem', padding: '3px 10px', border: '1px solid rgba(244, 63, 94, 0.3)' }}>
                🛡️ Admin
              </span>
            )}
          </div>
          <p style={{ color: 'var(--primary)', fontWeight: 500, fontSize: '0.95rem', marginBottom: '8px' }}>
            📧 {user.email}
          </p>
          <p style={{ fontSize: '0.95rem' }}>{user.about}</p>
        </div>
      </section>

      {/* Admin Tabs */}
      {isAdmin && (
        <div style={{ display: 'flex', gap: '15px', marginBottom: '25px', borderBottom: '1px solid var(--border-glass)', paddingBottom: '10px' }}>
          <button
            className={`btn ${activeTab === 'my-posts' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('my-posts')}
            style={{ borderRadius: '20px', padding: '6px 16px', fontSize: '0.85rem' }}
          >
            My Posts
          </button>
          <button
            className={`btn ${activeTab === 'all-posts' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('all-posts')}
            style={{ borderRadius: '20px', padding: '6px 16px', fontSize: '0.85rem' }}
          >
            📁 All System Posts (Admin View)
          </button>
        </div>
      )}

      {/* User's personal blogs list */}
      <section>
        <h3 className="dashboard-title">
          {activeTab === 'my-posts' ? `My Published Stories (${posts.length})` : `All System Stories (${posts.length})`}
        </h3>

        {error && <div className="alert alert-danger">{error}</div>}

        {loading ? (
          <div style={{ padding: '20px 0', color: 'var(--text-muted)' }}>
            <p>Loading stories...</p>
          </div>
        ) : posts.length === 0 ? (
          <div className="glass-panel no-posts">
            <p style={{ fontSize: '1.1rem', marginBottom: '10px' }}>No stories found.</p>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Click "Create Post" in the navbar to start writing!</p>
          </div>
        ) : (
          <div className="post-feed">
            {posts.map((post) => (
              <PostCard
                key={post.postId}
                post={post}
                showActions={true}
                onDelete={handleDeletePost}
              />
            ))}
          </div>
        )}
      </section>
    </div>
  );
};

export default Profile;
