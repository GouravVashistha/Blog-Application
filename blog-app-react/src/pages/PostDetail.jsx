import React, { useState, useEffect, useContext } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getPostById, getImageUrl, deletePost } from '../services/postService';
import { createComment, deleteComment } from '../services/commentService';
import { AuthContext } from '../context/AuthContext';

const PostDetail = () => {
  const { postId } = useParams();
  const { user, isLoggedIn } = useContext(AuthContext);
  const navigate = useNavigate();

  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Comment states
  const [commentContent, setCommentContent] = useState('');
  const [commentError, setCommentError] = useState('');
  const [commentSuccess, setCommentSuccess] = useState('');
  const [commentLoading, setCommentLoading] = useState(false);

  useEffect(() => {
    loadPost();
  }, [postId]);

  const loadPost = () => {
    setLoading(true);
    getPostById(postId)
      .then((data) => {
        setPost(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load blog post details.');
        setLoading(false);
      });
  };

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    if (!commentContent.trim()) return;

    setCommentError('');
    setCommentSuccess('');
    setCommentLoading(true);

    try {
      const newComment = await createComment(
        { content: commentContent.trim() },
        post.postId,
        user.id // Logged-in user's ID
      );
      
      setCommentSuccess('Comment added successfully!');
      setCommentContent('');
      setCommentLoading(false);
      
      // Update post state to include new comment locally
      setPost((prev) => ({
        ...prev,
        comments: [...(prev.comments || []), newComment],
      }));
    } catch (err) {
      setCommentLoading(false);
      console.error(err);
      setCommentError('Failed to publish comment. Please check your session.');
    }
  };

  const handleCommentDelete = async (commentId) => {
    if (window.confirm('Are you sure you want to delete this comment?')) {
      try {
        await deleteComment(commentId);
        // Remove from local state
        setPost((prev) => ({
          ...prev,
          comments: prev.comments.filter((c) => c.id !== commentId),
        }));
      } catch (err) {
        console.error(err);
        alert('Failed to delete comment.');
      }
    }
  };

  const handleDeletePost = async () => {
    if (window.confirm('Are you sure you want to delete this blog post? This action cannot be undone.')) {
      try {
        await deletePost(post.postId);
        alert('Post deleted successfully!');
        navigate('/');
      } catch (err) {
        console.error(err);
        alert('Failed to delete the post.');
      }
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '60px 0', textAlign: 'center', color: 'var(--text-muted)' }}>
        <h2>Loading post content...</h2>
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', maxWidth: '600px', margin: '40px auto' }}>
        <div className="alert alert-danger">{error || 'Post not found.'}</div>
        <Link to="/" className="btn btn-primary">Back to Home</Link>
      </div>
    );
  }

  const postDate = post.date || post.addDate;
  const formattedDate = postDate ? new Date(postDate).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }) : 'Unknown Date';

  const isPostOwner = post.user && user && post.user.id === user.id;
  const isAdmin = user && user.roles && user.roles.some((r) => r.name === 'ADMIN_USER');
  const showActions = isPostOwner || isAdmin;

  return (
    <article className="post-detail-layout">
      {/* Blog Image */}
      <img
        src={getImageUrl(post.imageName)}
        alt={post.title}
        className="post-detail-img"
        onError={(e) => {
          e.target.src = 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800&auto=format&fit=crop&q=80';
        }}
      />

      <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
        {post.category && (
          <span className="category-tag" style={{ fontSize: '0.85rem', padding: '4px 12px' }}>
            {post.category.categoryTitle}
          </span>
        )}
      </div>

      <h1 className="post-detail-title gradient-text">{post.title}</h1>

      <div className="post-detail-meta">
        <div className="author-info">
          <div className="author-avatar" style={{ width: '40px', height: '40px', fontSize: '1.2rem' }}>
            {post.user?.name ? post.user.name[0].toUpperCase() : 'U'}
          </div>
          <div>
            <div style={{ fontWeight: 600 }}>By {post.user?.name || 'Anonymous'}</div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{post.user?.about || 'Author'}</div>
          </div>
        </div>
        
        <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
          Published: {formattedDate}
        </div>
      </div>

      {showActions && (
        <div style={{ display: 'flex', gap: '15px', marginTop: '15px', marginBottom: '25px', borderBottom: '1px solid var(--border-glass)', paddingBottom: '15px' }}>
          <Link to={`/edit-post/${post.postId}`} className="btn btn-secondary btn-sm" style={{ padding: '6px 12px' }}>
            ✏️ Edit Story
          </Link>
          <button onClick={handleDeletePost} className="btn btn-danger btn-sm" style={{ padding: '6px 12px' }}>
            🗑️ Delete Story
          </button>
        </div>
      )}

      {/* Main Blog Body */}
      <div className="post-detail-body">{post.content}</div>

      {/* Comments Section */}
      <section className="comments-section">
        <h3 className="comments-title">Comments ({post.comments?.length || 0})</h3>

        {/* Comment input form */}
        {isLoggedIn ? (
          <form onSubmit={handleCommentSubmit} className="comment-form glass-panel" style={{ padding: '20px' }}>
            <h4 style={{ marginBottom: '12px' }}>Leave a Comment</h4>
            
            {commentError && <div className="alert alert-danger" style={{ padding: '8px 12px' }}>{commentError}</div>}
            {commentSuccess && <div className="alert alert-success" style={{ padding: '8px 12px' }}>{commentSuccess}</div>}

            <div className="form-group" style={{ marginBottom: '15px' }}>
              <textarea
                placeholder="Share your thoughts on this story..."
                className="form-control"
                style={{ minHeight: '80px' }}
                value={commentContent}
                onChange={(e) => setCommentContent(e.target.value)}
                required
              />
            </div>
            
            <button type="submit" className="btn btn-primary btn-sm" disabled={commentLoading}>
              {commentLoading ? 'Posting...' : 'Publish Comment'}
            </button>
          </form>
        ) : (
          <div className="glass-panel" style={{ padding: '20px', textAlign: 'center', marginBottom: '30px' }}>
            <p style={{ color: 'var(--text-muted)' }}>
              Please <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>Login</Link> to post comments.
            </p>
          </div>
        )}

        {/* Comments List */}
        {post.comments && post.comments.length > 0 ? (
          <ul className="comment-list">
            {post.comments.map((com) => (
              <li key={com.id} className="comment-card glass-panel">
                <div className="author-avatar" style={{ width: '32px', height: '32px', fontSize: '0.9rem' }}>
                  C
                </div>
                <div className="comment-content">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div className="comment-author">Reader</div>
                    {isLoggedIn && (
                      <button
                        onClick={() => handleCommentDelete(com.id)}
                        className="comment-delete-btn"
                      >
                        Delete
                      </button>
                    )}
                  </div>
                  <p className="comment-text">{com.content}</p>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>No comments yet. Be the first to express your thoughts!</p>
        )}
      </section>
    </article>
  );
};

export default PostDetail;
