import React, { useState } from 'react';
import axios from 'axios';
import './AdminLogin.css';
import { buildApiUrl } from '../config/api';

const AdminLogin = ({ onLoginSuccess, onSwitchToUser }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await axios.post(buildApiUrl('/admin/login'), {
        username,
        password,
      });

      if (response.data.success) {
        localStorage.setItem('adminUser', JSON.stringify(response.data));
        onLoginSuccess(response.data);
      } else {
        setError(response.data.message || 'Login failed');
      }
    } catch (err) {
      setError('Invalid credentials. Please try again.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-login-container">
      <div className="admin-login-box">
        <div className="admin-login-header">
          <h1>Admin Portal</h1>
          <p>Vibeathon Management System</p>
        </div>

        <form onSubmit={handleLogin} className="admin-login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              placeholder="Enter username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>

        {/* <div className="login-footer">
          <p>Default credentials:</p>
          <p>Username: <strong>admin</strong></p>
          <p>Password: <strong>admin@123</strong></p>
        </div> */}

        <button
          className="switch-button"
          onClick={onSwitchToUser}
        >
          ← Back to User Registration
        </button>
      </div>
    </div>
  );
};

export default AdminLogin;
