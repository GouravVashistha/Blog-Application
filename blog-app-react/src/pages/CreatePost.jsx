import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCategories } from '../services/categoryService';
import { createPost, uploadPostImage } from '../services/postService';
import { AuthContext } from '../context/AuthContext';

const CreatePost = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    categoryId: '',
  });

  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getCategories()
      .then((data) => {
        setCategories(data);
        if (data.length > 0) {
          setFormData((prev) => ({ ...prev, categoryId: data[0].categoryId.toString() }));
        }
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load categories list.');
      });
  }, []);

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
    const { title, content, categoryId } = formData;

    if (!title.trim() || !content.trim() || !categoryId) {
      setError('Please fill in all details.');
      return;
    }

    setError('');
    setSuccess('');
    setLoading(true);

    try {
      // Step 1: Create the post
      const payload = {
        title: title.trim(),
        content: content.trim(),
      };
      
      const newPost = await createPost(payload, user.id, parseInt(categoryId));
      
      // Step 2: Upload image if selected
      if (imageFile) {
        await uploadPostImage(imageFile, newPost.postId);
      }

      setSuccess('Blog post published successfully!');
      setLoading(false);
      
      setTimeout(() => {
        navigate('/profile');
      }, 1500);
    } catch (err) {
      setLoading(false);
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to create post. Please try again.');
      }
    }
  };

  return (
    <div className="form-wrapper glass-panel">
      <div className="form-header">
        <h2 className="gradient-text" style={{ fontSize: '2rem' }}>Write a New Story</h2>
        <p style={{ color: 'var(--text-muted)' }}>Share your ideas, stories, and expertise</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Post Title</label>
          <input
            type="text"
            name="title"
            placeholder="Enter an attention-grabbing title"
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
            style={{ appearance: 'none', background: 'rgba(15, 23, 42, 0.6) url("data:image/svg+xml;utf8,<svg fill=\'white\' height=\'24\' viewBox=\'0 0 24 24\' width=\'24\' xmlns=\'http://www.w3.org/2000/svg\'><path d=\'M7 10l5 5 5-5z\'/><path d=\'M0 0h24v24H0z\' fill=\'none\'/></svg>") no-repeat 95% center' }}
          >
            {categories.map((cat) => (
              <option key={cat.categoryId} value={cat.categoryId} style={{ background: 'var(--bg-secondary)', color: 'var(--text-main)' }}>
                {cat.categoryTitle}
              </option>
            ))}
          </select>
        </div>

        {/* Thumbnail Image Selection */}
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
                <span style={{ color: 'var(--primary)', fontSize: '0.85rem', fontWeight: 600 }}>Change Image</span>
              </div>
            ) : (
              <div>
                <span style={{ fontSize: '2rem' }}>🖼️</span>
                <p style={{ margin: '8px 0', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                  Drag and drop or click to upload cover image
                </p>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Supports PNG, JPG, JPEG</span>
              </div>
            )}
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Post Content</label>
          <textarea
            name="content"
            placeholder="Write your story here..."
            className="form-control"
            value={formData.content}
            onChange={handleInputChange}
            required
            style={{ minHeight: '220px' }}
          />
        </div>

        <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
          <button type="submit" className="btn btn-primary" style={{ flex: 1 }} disabled={loading}>
            {loading ? 'Publishing...' : 'Publish Post'}
          </button>
          <button type="button" onClick={() => navigate('/')} className="btn btn-secondary" style={{ width: '120px' }}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreatePost;
