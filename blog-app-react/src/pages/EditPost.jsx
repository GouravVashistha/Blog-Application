import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getPostById, updatePost, uploadPostImage, getImageUrl } from '../services/postService';
import { getCategories } from '../services/categoryService';
import { AuthContext } from '../context/AuthContext';

const EditPost = () => {
  const { postId } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    categoryId: '',
    imageName: '',
  });

  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    // Load categories first
    getCategories()
      .then((catData) => {
        setCategories(catData);
        // Then load post details
        return getPostById(postId);
      })
      .then((postData) => {
        // Security check: Verify post owner matches logged-in user OR user is Admin
        const isOwner = postData.user && user && postData.user.id === user.id;
        const isAdmin = user && user.roles && user.roles.some((r) => r.name === 'ADMIN_USER');

        if (!isOwner && !isAdmin) {
          setError('Unauthorized: You can only edit your own posts.');
          setLoading(false);
          return;
        }

        setFormData({
          title: postData.title,
          content: postData.content,
          categoryId: postData.category ? postData.category.categoryId.toString() : '',
          imageName: postData.imageName || '',
        });

        if (postData.imageName) {
          setImagePreview(getImageUrl(postData.imageName));
        }
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to fetch post or category details.');
        setLoading(false);
      });
  }, [postId, user]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { title, content, categoryId, imageName } = formData;

    if (!title.trim() || !content.trim() || !categoryId) {
      setError('Please fill in all details.');
      return;
    }

    setError('');
    setSuccess('');
    setUpdating(true);

    try {
      // Step 1: Update post content
      const payload = {
        title: title.trim(),
        content: content.trim(),
        imageName: imageName,
      };

      await updatePost(payload, postId);

      // Step 2: Upload new cover image if file selected
      if (imageFile) {
        await uploadPostImage(imageFile, postId);
      }

      setSuccess('Post updated successfully!');
      setUpdating(false);
      
      setTimeout(() => {
        navigate('/profile');
      }, 1500);
    } catch (err) {
      setUpdating(false);
      console.error(err);
      setError('Failed to update post.');
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '60px 0', textAlign: 'center', color: 'var(--text-muted)' }}>
        <h2>Fetching post details...</h2>
      </div>
    );
  }

  return (
    <div className="form-wrapper glass-panel">
      <div className="form-header">
        <h2 className="gradient-text" style={{ fontSize: '2rem' }}>Edit Your Story</h2>
        <p style={{ color: 'var(--text-muted)' }}>Modify details and refresh your blog post</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      {!error && (
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Post Title</label>
            <input
              type="text"
              name="title"
              className="form-control"
              value={formData.title}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Category</label>
            <select
              name="categoryId"
              className="form-control"
              value={formData.categoryId}
              onChange={handleInputChange}
              required
              disabled // Category is usually fixed during creation in some apps, but disabled fits standard edit flow if only editing title/content/image. If you want to enable, categoryId must be associated with backend change.
              style={{ appearance: 'none', background: 'rgba(15, 23, 42, 0.4)' }}
            >
              {categories.map((cat) => (
                <option key={cat.categoryId} value={cat.categoryId}>
                  {cat.categoryTitle}
                </option>
              ))}
            </select>
          </div>

          {/* Cover Image Replacement */}
          <div className="form-group">
            <label className="form-label">Cover Image</label>
            <div className="file-upload-wrapper">
              <input
                type="file"
                accept="image/*"
                className="file-upload-input"
                onChange={handleImageChange}
              />
              {imagePreview ? (
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px' }}>
                  <img src={imagePreview} alt="Preview" style={{ maxWidth: '100%', maxHeight: '150px', borderRadius: '4px' }} />
                  <span style={{ color: 'var(--primary)', fontSize: '0.85rem', fontWeight: 600 }}>Change Cover Image</span>
                </div>
              ) : (
                <div>
                  <span style={{ fontSize: '2rem' }}>🖼️</span>
                  <p style={{ margin: '8px 0', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                    Click to select cover image to upload
                  </p>
                </div>
              )}
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Post Content</label>
            <textarea
              name="content"
              className="form-control"
              value={formData.content}
              onChange={handleInputChange}
              required
              style={{ minHeight: '220px' }}
            />
          </div>

          <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
            <button type="submit" className="btn btn-primary" style={{ flex: 1 }} disabled={updating}>
              {updating ? 'Updating...' : 'Save Changes'}
            </button>
            <button type="button" onClick={() => navigate('/profile')} className="btn btn-secondary" style={{ width: '120px' }}>
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
};

export default EditPost;
