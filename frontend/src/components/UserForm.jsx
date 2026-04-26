import React, { useState } from 'react';
import './UserForm.css';

const UserForm = ({ onSubmit }) => {
  const [formData, setFormData] = useState({
    teamName: '',
    teamLeadName: '',
    teamLeadEmail: '',
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    if (!formData.teamName.trim()) {
      newErrors.teamName = 'Team name is required';
    }

    if (!formData.teamLeadName.trim()) {
      newErrors.teamLeadName = 'Team lead name is required';
    }

    if (!formData.teamLeadEmail.trim()) {
      newErrors.teamLeadEmail = 'Team lead email is required';
    } else if (!formData.teamLeadEmail.toLowerCase().endsWith('@htcinc.com')) {
      newErrors.teamLeadEmail = 'Email must end with @htcinc.com';
    } else {
      const emailRegex = /^[^\s@]+@htcinc\.com$/i;
      if (!emailRegex.test(formData.teamLeadEmail)) {
        newErrors.teamLeadEmail = 'Invalid email format';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: '',
      }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <form className="user-form" onSubmit={handleSubmit}>
      <h2>Team Details</h2>
      <p className="form-subtitle">Provide your team information</p>

      <div className="form-group">
        <label htmlFor="teamName">Team Name *</label>
        <input
          type="text"
          id="teamName"
          name="teamName"
          value={formData.teamName}
          onChange={handleChange}
          placeholder="Enter your team name"
          className={errors.teamName ? 'input-error' : ''}
        />
        {errors.teamName && <span className="error-message">{errors.teamName}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="teamLeadName">Team Lead Name *</label>
        <input
          type="text"
          id="teamLeadName"
          name="teamLeadName"
          value={formData.teamLeadName}
          onChange={handleChange}
          placeholder="Enter team lead's full name"
          className={errors.teamLeadName ? 'input-error' : ''}
        />
        {errors.teamLeadName && <span className="error-message">{errors.teamLeadName}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="teamLeadEmail">Team Lead Email (HTC Inc) *</label>
        <input
          type="email"
          id="teamLeadEmail"
          name="teamLeadEmail"
          value={formData.teamLeadEmail}
          onChange={handleChange}
          placeholder="teamlead@htcinc.com"
          className={errors.teamLeadEmail ? 'input-error' : ''}
        />
        {errors.teamLeadEmail && <span className="error-message">{errors.teamLeadEmail}</span>}
        <small className="hint-text">Must be an HTC Inc email (@htcinc.com)</small>
      </div>

      <div className="form-group">
        <button type="submit" className="btn-next">
          Next: Select Problem Statement →
        </button>
      </div>
    </form>
  );
};

export default UserForm;
