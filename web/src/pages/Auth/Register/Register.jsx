// components/Register/Register.js
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import './Register.css';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    fullName: ''
  });
  const [validationErrors, setValidationErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  
  const { register, error, clearError, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Redirect nếu đã đăng nhập
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  // Clear error khi component unmount
  useEffect(() => {
    return () => clearError();
  }, [clearError]);

  // Tạo bubbles animation
  const renderBubbles = () => {
    return (
      <>
        <div className="bubble"></div>
        <div className="bubble"></div>
        <div className="bubble"></div>
        <div className="bubble"></div>
      </>
    );
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear validation error khi user bắt đầu nhập
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
    
    if (error) clearError();
  };

  const validateForm = () => {
    const errors = {};

    if (!formData.username.trim()) {
      errors.username = 'Username là bắt buộc';
    } else if (formData.username.length < 3) {
      errors.username = 'Username phải có ít nhất 3 ký tự';
    }

    if (!formData.email.trim()) {
      errors.email = 'Email là bắt buộc';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      errors.email = 'Email không hợp lệ';
    }

    if (!formData.password) {
      errors.password = 'Mật khẩu là bắt buộc';
    } else if (formData.password.length < 6) {
      errors.password = 'Mật khẩu phải có ít nhất 6 ký tự';
    }

    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Mật khẩu xác nhận không khớp';
    }

    if (!formData.fullName.trim()) {
      errors.fullName = 'Họ và tên là bắt buộc';
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);

    const userData = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      fullName: formData.fullName
    };

    const result = await register(userData);
    
    if (result.success) {
      navigate('/', { replace: true });
    }
    
    setIsLoading(false);
  };

  return (
    <div className="register-container">
      {renderBubbles()}
      
      <div className="register-card">
        <div className="register-header">
          <h1 className="register-title">Noxtragram</h1>
          <p className="register-subtitle">Đăng ký để bắt đầu chia sẻ</p>
        </div>

        <form onSubmit={handleSubmit} className="register-form">
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <div className="form-group">
            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Họ và tên"
              className={`form-input ${validationErrors.fullName ? 'error' : ''}`}
              required
              disabled={isLoading}
            />
            {validationErrors.fullName && (
              <span className="field-error">{validationErrors.fullName}</span>
            )}
          </div>

          <div className="form-group">
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              placeholder="Username"
              className={`form-input ${validationErrors.username ? 'error' : ''}`}
              required
              disabled={isLoading}
            />
            {validationErrors.username && (
              <span className="field-error">{validationErrors.username}</span>
            )}
          </div>

          <div className="form-group">
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Email"
              className={`form-input ${validationErrors.email ? 'error' : ''}`}
              required
              disabled={isLoading}
            />
            {validationErrors.email && (
              <span className="field-error">{validationErrors.email}</span>
            )}
          </div>

          <div className="form-group">
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Mật khẩu"
              className={`form-input ${validationErrors.password ? 'error' : ''}`}
              required
              disabled={isLoading}
            />
            {validationErrors.password && (
              <span className="field-error">{validationErrors.password}</span>
            )}
          </div>

          <div className="form-group">
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Xác nhận mật khẩu"
              className={`form-input ${validationErrors.confirmPassword ? 'error' : ''}`}
              required
              disabled={isLoading}
            />
            {validationErrors.confirmPassword && (
              <span className="field-error">{validationErrors.confirmPassword}</span>
            )}
          </div>

          <button 
            type="submit" 
            className="register-button"
            disabled={isLoading}
          >
            {isLoading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>

          <div className="terms-notice">
            <p>
              Bằng cách đăng ký, bạn đồng ý với 
              <Link to="/terms" className="terms-link"> Điều khoản</Link> và 
              <Link to="/privacy" className="terms-link"> Chính sách bảo mật</Link> của chúng tôi.
            </p>
          </div>
        </form>
      </div>

      <div className="register-footer">
        <p>
          Đã có tài khoản? 
          <Link to="/login" className="login-link"> Đăng nhập</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
