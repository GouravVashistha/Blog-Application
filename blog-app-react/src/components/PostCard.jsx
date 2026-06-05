import React from 'react';
import { Link } from 'react-router-dom';
import { getImageUrl } from '../services/postService';

const PostCard = ({ post, showActions = false, onDelete = null }) => {
  const postDate = post.addDate || post.date;
  const formattedDate = postDate ? new Date(postDate).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }) : 'Unknown Date';

  // Extract text excerpt without html or double spaces
  const getExcerpt = (content) => {
    if (!content) return '';
    const plainText = content.replace(/<[^>]*>/g, '');
    return plainText.substring(0, 120) + (plainText.length > 120 ? '...' : '');
  };

  return (
    <article className="post-card glass-panel">
      <img
        src={getImageUrl(post.imageName)}
        alt={post.title}
        className="post-card-img"
        onError={(e) => {
          e.target.src = 'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=500&auto=format&fit=crop&q=60';
        }}
      />
      <div className="post-card-content">
        <div>
          <div className="post-meta">
            {post.category && (
              <span className="category-tag">{post.category.categoryTitle}</span>
            )}
            <span>•</span>
            <span>{formattedDate}</span>
          </div>

          <Link to={`/posts/${post.postId}`} className="post-title-link">
            <h3>{post.title}</h3>
          </Link>

          <p className="post-excerpt">{getExcerpt(post.content)}</p>
        </div>

        <div className="post-card-footer">
          <div className="author-info">
            <div className="author-avatar">
              {post.user?.name ? post.user.name[0].toUpperCase() : 'U'}
            </div>
            <span>By {post.user?.name || 'Anonymous'}</span>
          </div>

          {showActions && (
            <div style={{ display: 'flex', gap: '10px' }}>
              <Link to={`/edit-post/${post.postId}`} className="btn btn-secondary btn-sm" style={{ padding: '4px 8px' }}>
                ✏️ Edit
              </Link>
              {onDelete && (
                <button
                  onClick={() => onDelete(post.postId)}
                  className="btn btn-danger btn-sm"
                  style={{ padding: '4px 8px' }}
                >
                  🗑️ Delete
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </article>
  );
};

export default PostCard;
