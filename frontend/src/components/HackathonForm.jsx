import React, { useState, useEffect } from 'react';
import axios from 'axios';
import UserForm from './UserForm';
import ProblemStatementSelector from './ProblemStatementSelector';
import './HackathonForm.css';
import { buildApiUrl } from '../config/api';

const HackathonForm = () => {
  const [step, setStep] = useState(1); // Step 1: User details, Step 2: Problem selection
  const [formData, setFormData] = useState({
    teamName: '',
    teamLeadName: '',
    teamLeadEmail: '',
  });
  const [selectedProblem, setSelectedProblem] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleUserFormSubmit = (data) => {
    setFormData(data);
    setStep(2);
    setError('');
  };

  const handleProblemSelection = async (problemId) => {
    setSelectedProblem(problemId);
  };

  const downloadSelectionReport = async (email) => {
    const response = await axios.get(
      buildApiUrl('/user-selections/download-report'),
      {
        params: { email },
        responseType: 'blob',
      }
    );

    const blob = new Blob([response.data], {
      type: 'application/pdf',
    });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `selection_report_${email.replace(/[^a-zA-Z0-9]/g, '_')}.pdf`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  };

  const handleFinalSubmit = async () => {
    if (!selectedProblem) {
      setError('Please select a problem statement');
      return;
    }

    setLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      const payload = {
        ...formData,
        problemStatementId: selectedProblem,
      };

      await axios.post(
        buildApiUrl('/user-selections'),
        payload
      );

      await downloadSelectionReport(formData.teamLeadEmail);

      setSuccessMessage('✓ Registration successful! Your selection report has been downloaded.');

      // Reset form after success
      setTimeout(() => {
        setStep(1);
        setFormData({
          teamName: '',
          teamLeadName: '',
          teamLeadEmail: '',
        });
        setSelectedProblem(null);
        setSuccessMessage('');
      }, 2000);
    } catch (err) {
      const errorMsg =
        err.response?.data?.message ||
        err.message ||
        'Failed to submit the form. Please try again.';
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const goBack = () => {
    setStep(1);
    setSelectedProblem(null);
    setError('');
  };

  return (
    <div className="hackathon-form-container">
      {/* <div className="form-header">
        <h1>VIBEATHON 2026</h1>
        <p>Innovate, Create, and Transform the Future</p>
      </div> */}

      <div className="progress-bar">
        <div className={`progress-step ${step >= 1 ? 'active' : ''}`}>
          <span className="step-number">1</span>
          <span className="step-label">Team Details</span>
        </div>
        <div className={`progress-line ${step >= 2 ? 'active' : ''}`}></div>
        <div className={`progress-step ${step >= 2 ? 'active' : ''}`}>
          <span className="step-number">2</span>
          <span className="step-label">Select Problem</span>
        </div>
      </div>

      {error && (
        <div className="alert alert-error">
          <span>❌ {error}</span>
          <button onClick={() => setError('')} className="close-btn">×</button>
        </div>
      )}

      {successMessage && (
        <div className="alert alert-success">
          <span>{successMessage}</span>
        </div>
      )}

      <div className="form-content">
        {step === 1 ? (
          <UserForm onSubmit={handleUserFormSubmit} />
        ) : (
          <ProblemStatementSelector
            onSelectProblem={handleProblemSelection}
            selectedProblem={selectedProblem}
          />
        )}
      </div>

      <div className="form-actions">
        {step === 2 && (
          <button className="btn btn-secondary" onClick={goBack}>
            ← Back
          </button>
        )}
        {step === 2 && (
          <button
            className="btn btn-primary"
            onClick={handleFinalSubmit}
            disabled={!selectedProblem || loading}
          >
            {loading ? 'Submitting...' : 'Submit Registration'}
          </button>
        )}
      </div>
    </div>
  );
};

export default HackathonForm;
