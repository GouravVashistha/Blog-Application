import React, { useState, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Register = () => {
  const { registerUser } = useContext(AuthContext);
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    about: '',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const { name, email, password, about } = formData;

    if (!name || !email || !password || !about) {
      setError('Please fill in all details.');
      return;
    }

    if (name.length < 4) {
      setError('Name must be at least 4 characters.');
      return;
    }

    if (password.length < 3 || password.length > 10) {
      setError('Password must be between 3 and 10 characters.');
      return;
    }

    setError('');
    setSuccess('');
    setLoading(true);

    try {
      await registerUser(formData);
      setSuccess('Registration successful! Redirecting to login page...');
      setLoading(false);
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      setLoading(false);
      console.error(err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Registration failed. Email might already exist.');
      }
    }
  };

  return (
    <div className="auth-wrapper glass-panel" style={{ maxWidth: '500px', margin: '40px auto' }}>
      <div className="auth-header">
        <h2>Create Account</h2>
        <p>Join our premium writing community today</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <form onSubmit={handleSubmit} className="auth-form">
        <div className="form-group">
          <label className="form-label">Full Name</label>
          <input
            type="text"
            name="name"
            placeholder="John Doe"
            className="form-control"
            value={formData.name}
            onChange={handleInputChange}
            required
          />
          <small style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>Minimum 4 characters</small>
        </div>

        <div className="form-group">
          <label className="form-label">Email Address</label>
          <input
            type="email"
            name="email"
            placeholder="john@example.com"
            className="form-control"
            value={formData.email}
            onChange={handleInputChange}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Password</label>
          <input
            type="password"
            name="password"
            placeholder="••••••••"
            className="form-control"
            value={formData.password}
            onChange={handleInputChange}
            required
          />
          <small style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>Between 3 and 10 characters</small>
        </div>

        <div className="form-group">
          <label className="form-label">About / Bio</label>
          <textarea
            name="about"
            placeholder="Tell us a bit about yourself..."
            className="form-control"
            value={formData.about}
            onChange={handleInputChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '10px' }} disabled={loading}>
          {loading ? 'Creating Account...' : 'Register'}
        </button>
      </form>

      <div className="auth-footer">
        Already have an account?{' '}
        <Link to="/login" className="auth-link">
          Login Here
        </Link>
      </div>
    </div>
  );
};

export default Register;
