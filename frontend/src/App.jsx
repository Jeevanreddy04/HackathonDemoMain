import React, { useState, useEffect } from 'react';
import HackathonForm from './components/HackathonForm';
import AdminLogin from './components/AdminLogin';
import AdminDashboard from './components/AdminDashboard';
import vibethonImg from './assets/vibethon.jpg';
import htcLogo from './assets/logo.png';
import './App.css';

function App() {
  const [mode, setMode] = useState('user'); // 'user', 'admin-login', 'admin-dashboard'
  const [adminUser, setAdminUser] = useState(null);

  // Load admin session from localStorage on app start
  useEffect(() => {
    const savedAdmin = localStorage.getItem('adminUser');
    if (savedAdmin) {
      try {
        const admin = JSON.parse(savedAdmin);
        setAdminUser(admin);
        setMode('admin-dashboard');
      } catch (e) {
        localStorage.removeItem('adminUser');
      }
    }
  }, []);

  const handleAdminLoginSuccess = (admin) => {
    setAdminUser(admin);
    setMode('admin-dashboard');
  };

  const handleAdminLogout = () => {
    localStorage.removeItem('adminUser');
    setAdminUser(null);
    setMode('user');
  };

  const handleSwitchToAdminLogin = () => {
    setMode('admin-login');
  };

  const handleSwitchToUser = () => {
    setMode('user');
  };

  return (
    <div className="App">
      {mode === 'user' && (
        <div className="main-container">
          <header className="app-header">
            <img src={vibethonImg} alt="Vibethon" className="vibethon-banner" />
          </header>
          
          <main className="app-content">
            <div className="user-content">
              <HackathonForm />
              <button 
                className="admin-access-button"
                onClick={handleSwitchToAdminLogin}
                title="Admin Panel"
              >
                🔐 Admin
              </button>
            </div>
          </main>

          <footer className="app-footer">
            <img src={htcLogo} alt="HTC Logo" className="htc-footer-logo" />
          </footer>
        </div>
      )}

      {mode === 'admin-login' && (
        <AdminLogin 
          onLoginSuccess={handleAdminLoginSuccess}
          onSwitchToUser={handleSwitchToUser}
        />
      )}

      {mode === 'admin-dashboard' && adminUser && (
        <AdminDashboard 
          admin={adminUser}
          onLogout={handleAdminLogout}
        />
      )}
    </div>
  );
}

export default App;
