import React, { useState } from 'react';
import axios from 'axios';
import './AdminDashboard.css';
import { buildApiUrl } from '../config/api';

const AdminDashboard = ({ admin, onLogout }) => {
  const [uploadFile, setUploadFile] = useState(null);
  const [uploadMessage, setUploadMessage] = useState('');
  const [uploadLoading, setUploadLoading] = useState(false);
  const [downloadLoading, setDownloadLoading] = useState('');
  const [activeTab, setActiveTab] = useState('dashboard');

  const handleFileChange = (e) => {
    setUploadFile(e.target.files[0]);
    setUploadMessage('');
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadFile) {
      setUploadMessage('Please select a file');
      return;
    }

    setUploadLoading(true);
    setUploadMessage('');

    try {
      const formData = new FormData();
      formData.append('file', uploadFile);

      const response = await axios.post(
        buildApiUrl('/admin/upload-problems'),
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        }
      );

      setUploadMessage(response.data.message || 'Upload successful!');
      setUploadFile(null);
      document.getElementById('fileInput').value = '';
    } catch (error) {
      setUploadMessage(error.response?.data?.message || 'Upload failed');
    } finally {
      setUploadLoading(false);
    }
  };

  const handleDownload = async (endpoint, filename) => {
    setDownloadLoading(filename);
    try {
      const response = await axios.get(buildApiUrl(`/admin/${endpoint}`), {
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (error) {
      alert('Download failed: ' + (error.response?.data?.message || error.message));
    } finally {
      setDownloadLoading('');
    }
  };

  return (
    <div className="admin-dashboard-container1">
      <div className="admin-dashboard">
        <div className="admin-header">
          <div className="admin-header-content">
            <h1>Admin Dashboard</h1>
            <p>Welcome, {admin.fullName}</p>
          </div>
          <button className="logout-button" onClick={onLogout}>
            Logout
          </button>
        </div>

        <div className="admin-tabs">
          <button
            className={`tab ${activeTab === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActiveTab('dashboard')}
          >
            📊 Dashboard
          </button>
          <button
            className={`tab ${activeTab === 'upload' ? 'active' : ''}`}
            onClick={() => setActiveTab('upload')}
          >
            📤 Upload Problems
          </button>
          <button
            className={`tab ${activeTab === 'download' ? 'active' : ''}`}
            onClick={() => setActiveTab('download')}
          >
            📥 Download Reports
          </button>
        </div>

        <div className="admin-content">
          {/* Dashboard Tab */}
          {activeTab === 'dashboard' && (
            <div className="dashboard-section">
              <div className="section-title">Welcome to Admin Dashboard</div>
              <p className="dashboard-message">Use the tabs above to manage problem statements, upload problems, and download reports.</p>
            </div>
          )}

          {/* Upload Tab */}
          {activeTab === 'upload' && (
            <div className="upload-section">
              <div className="section-title">Upload Problem Statements</div>
              <form onSubmit={handleUpload} className="upload-form">
                <div className="upload-area">
                  <label htmlFor="fileInput" className="file-label">
                    <div className="upload-icon">📁</div>
                    <div className="upload-text">
                      <p className="upload-title">Choose Excel File</p>
                      <p className="upload-subtitle">Click to select or drag and drop</p>
                    </div>
                    <input
                      id="fileInput"
                      type="file"
                      accept=".xlsx,.xls"
                      onChange={handleFileChange}
                      hidden
                    />
                  </label>
                </div>

                {uploadFile && (
                  <div className="file-selected">
                    <span>✓ {uploadFile.name}</span>
                  </div>
                )}

                {uploadMessage && (
                  <div className={`upload-message ${uploadMessage.includes('successful') ? 'success' : 'error'}`}>
                    {uploadMessage}
                  </div>
                )}

                <button
                  type="submit"
                  className="upload-button"
                  disabled={!uploadFile || uploadLoading}
                >
                  {uploadLoading ? 'Uploading...' : 'Upload'}
                </button>
              </form>

              <div className="upload-instructions">
                <h3>Instructions:</h3>
                <ol>
                  <li>Prepare your Excel file with problem statements</li>
                  <li>Ensure all required columns are present</li>
                  <li>Click "Upload" to add problems to the system</li>
                  <li>Existing problems will be updated if IDs match</li>
                </ol>
              </div>
            </div>
          )}

          {/* Download Tab */}
          {activeTab === 'download' && (
            <div className="download-section">
              <div className="section-title">Download Reports</div>
              <div className="download-grid">
                <div className="download-card">
                  <div className="download-icon">📋</div>
                  <h3>Problem Statements</h3>
                  <p>Download all problem statements with current status</p>
                  <button
                    className="download-btn"
                    onClick={() =>
                      handleDownload('download-problems', 'problem_statements.xlsx')
                    }
                    disabled={downloadLoading === 'problem_statements.xlsx'}
                  >
                    {downloadLoading === 'problem_statements.xlsx'
                      ? 'Downloading...'
                      : 'Download'}
                  </button>
                </div>

                <div className="download-card">
                  <div className="download-icon">👥</div>
                  <h3>User Selections</h3>
                  <p>Download all team registrations and problem selections</p>
                  <button
                    className="download-btn"
                    onClick={() =>
                      handleDownload('download-selections', 'user_selections.xlsx')
                    }
                    disabled={downloadLoading === 'user_selections.xlsx'}
                  >
                    {downloadLoading === 'user_selections.xlsx'
                      ? 'Downloading...'
                      : 'Download'}
                  </button>
                </div>

                <div className="download-card">
                  <div className="download-icon">📊</div>
                  <h3>Complete Report</h3>
                  <p>Download comprehensive report with all details and summary</p>
                  <button
                    className="download-btn btn-primary"
                    onClick={() =>
                      handleDownload('download-combined-report', 'hackathon_complete_report.xlsx')
                    }
                    disabled={downloadLoading === 'hackathon_complete_report.xlsx'}
                  >
                    {downloadLoading === 'hackathon_complete_report.xlsx'
                      ? 'Downloading...'
                      : 'Download'}
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
