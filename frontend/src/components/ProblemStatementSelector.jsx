import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Pagination from './Pagination';
import './ProblemStatementSelector.css';
import { buildApiUrl } from '../config/api';

const ProblemStatementSelector = ({ onSelectProblem, selectedProblem }) => {
  const [problems, setProblems] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const ITEMS_PER_PAGE = 9;

  useEffect(() => {
    fetchProblems();
  }, [currentPage]);

  const fetchProblems = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await axios.get(buildApiUrl('/problem-statements'), {
        params: {
          page: currentPage - 1, // Backend expects 0-indexed pages
          size: ITEMS_PER_PAGE,
        },
      });

      setProblems(response.data.content);
      setTotalItems(response.data.totalElements);
    } catch (err) {
      setError('Failed to load problem statements. Please try again.');
      console.error('Error fetching problems:', err);
    } finally {
      setLoading(false);
    }
  };

  const getSelectionStatus = (problem) => {
    const count = problem.selectionCount || 0;
    const maxCount = 2;
    const isDisabled = count >= maxCount;

    return {
      count,
      maxCount,
      isDisabled,
      isFull: count === maxCount,
    };
  };

  const handleSelectProblem = (problemId, isDisabled) => {
    if (!isDisabled) {
      onSelectProblem(problemId);
    }
  };

  const totalPages = Math.ceil(totalItems / ITEMS_PER_PAGE);

  return (
    <div className="problem-statement-selector">
      <h2>Step 2: Select a Problem Statement</h2>
      <p className="instruction">
        Choose one problem statement for your team. Each problem statement can be selected by a maximum of 2 teams.
      </p>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {loading ? (
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading problem statements...</p>
        </div>
      ) : (
        <>
          <div className="problems-grid">
            {problems.map((problem) => {
              const status = getSelectionStatus(problem);
              const isSelected = selectedProblem === problem.id;

              return (
                <div
                  key={problem.id}
                  className={`problem-card ${isSelected ? 'selected' : ''} ${status.isDisabled ? 'disabled' : ''
                    }`}
                  onClick={() => handleSelectProblem(problem.id, status.isDisabled)}
                >
                  <div className="problem-header">
                    <span className="problem-id">#{problem.id}</span>
                    <div className="selection-indicator">
                      <span className={`count-badge ${status.isFull ? 'full' : 'available'}`}>
                        {status.count}/{status.maxCount}
                      </span>
                      {status.isDisabled && (
                        <span className="disabled-badge">FULL</span>
                      )}
                    </div>
                  </div>

                  <div className="problem-content">
                    <h3>{problem.title}</h3>
                    <p className="description">{problem.description}</p>
                  </div>

                  <div className="problem-footer">
                    {isSelected && (
                      <span className="selected-indicator">✓ Selected</span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {problems.length === 0 && (
            <div className="no-data">
              <p>No problem statements available</p>
            </div>
          )}

          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setCurrentPage}
            />
          )}
        </>
      )}

      {selectedProblem && (
        <div className="selection-summary">
          <h3>✓ Your Selection</h3>
          {problems.find((p) => p.id === selectedProblem) && (
            <p>
              Selected: <strong>{problems.find((p) => p.id === selectedProblem)?.title}</strong>
            </p>
          )}
        </div>
      )}
    </div>
  );
};

export default ProblemStatementSelector;
