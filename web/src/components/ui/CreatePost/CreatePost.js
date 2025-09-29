// components/CreatePost/CreatePost.js
import React, { useState, useRef, useEffect } from 'react';
import { useAuth } from '../../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import postService from '../../../services/postService';
import './CreatePost.css';

const CreatePost = () => {
    const { user, isAuthenticated } = useAuth();
    const navigate = useNavigate();
    
    // State chính
    const [caption, setCaption] = useState('');
    const [location, setLocation] = useState('');
    const [hashtags, setHashtags] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [currentStep, setCurrentStep] = useState(1); // 1: Upload, 2: Edit, 3: Details
    
    // State cho media
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [previewUrls, setPreviewUrls] = useState([]);
    const [currentMediaIndex, setCurrentMediaIndex] = useState(0);
    
    // State cho filter và edit
    const [filters, setFilters] = useState([]);
    const [selectedFilter, setSelectedFilter] = useState('normal');
    const [cropData, setCropData] = useState(null);
    
    // Refs
    const fileInputRef = useRef(null);
    const captionTextareaRef = useRef(null);

    // Danh sách filters
    const availableFilters = [
        { id: 'normal', name: 'Normal', className: 'filter-normal' },
        { id: 'clarendon', name: 'Clarendon', className: 'filter-clarendon' },
        { id: 'gingham', name: 'Gingham', className: 'filter-gingham' },
        { id: 'moon', name: 'Moon', className: 'filter-moon' },
        { id: 'lark', name: 'Lark', className: 'filter-lark' },
        { id: 'reyes', name: 'Reyes', className: 'filter-reyes' },
        { id: 'juno', name: 'Juno', className: 'filter-juno' },
        { id: 'slumber', name: 'Slumber', className: 'filter-slumber' },
        { id: 'crema', name: 'Crema', className: 'filter-crema' },
        { id: 'ludwig', name: 'Ludwig', className: 'filter-ludwig' },
        { id: 'aden', name: 'Aden', className: 'filter-aden' },
        { id: 'perpetua', name: 'Perpetua', className: 'filter-perpetua' }
    ];

    // Xử lý chọn file
    const handleFileSelect = (event) => {
        const files = Array.from(event.target.files);
        if (files.length === 0) return;

        // Validate file types và size
        const validFiles = files.filter(file => {
            const isValidType = file.type.startsWith('image/') || file.type.startsWith('video/');
            const isValidSize = file.size <= 50 * 1024 * 1024; // 50MB
            return isValidType && isValidSize;
        });

        if (validFiles.length === 0) {
            alert('Vui lòng chọn file ảnh hoặc video hợp lệ (tối đa 50MB)');
            return;
        }

        setSelectedFiles(validFiles);
        
        // Tạo preview URLs
        const urls = validFiles.map(file => URL.createObjectURL(file));
        setPreviewUrls(urls);
        setCurrentStep(2); // Chuyển đến step edit
    };

    // Xử lý kéo thả file
    const handleDrop = (event) => {
        event.preventDefault();
        const files = Array.from(event.dataTransfer.files);
        if (files.length > 0) {
            const inputEvent = { target: { files } };
            handleFileSelect(inputEvent);
        }
    };

    const handleDragOver = (event) => {
        event.preventDefault();
    };

    // Chuyển đổi giữa các media
    const nextMedia = () => {
        if (currentMediaIndex < previewUrls.length - 1) {
            setCurrentMediaIndex(currentMediaIndex + 1);
        }
    };

    const prevMedia = () => {
        if (currentMediaIndex > 0) {
            setCurrentMediaIndex(currentMediaIndex - 1);
        }
    };

    // Xử lý hashtag input
    const handleHashtagInput = (e) => {
        const value = e.target.value;
        setHashtags(value);
        
        // Auto-add space sau khi gõ xong hashtag
        if (value.endsWith('#')) {
            setHashtags(value + ' ');
        }
    };

    // Đếm ký tự caption
    const captionLength = caption.length;
    const maxCaptionLength = 2200;

    // Xử lý submit post
    const handleSubmit = async () => {
        if (!caption.trim() || selectedFiles.length === 0) {
            alert('Vui lòng thêm caption và ít nhất một ảnh/video');
            return;
        }

        if (caption.length > maxCaptionLength) {
            alert(`Caption không được vượt quá ${maxCaptionLength} ký tự`);
            return;
        }

        setIsLoading(true);

        try {
            const formData = new FormData();
            formData.append('caption', caption);
            formData.append('location', location);
            formData.append('hashtags', hashtags);

            // Thêm tất cả files
            selectedFiles.forEach(file => {
                formData.append('files', file);
            });

            const response = await postService.createPost(formData);
            
            // Thành công
            navigate('/');
        } catch (error) {
            console.error('Error creating post:', error);
            alert('Có lỗi xảy ra khi đăng bài. Vui lòng thử lại.');
        } finally {
            setIsLoading(false);
        }
    };

    // Cleanup preview URLs
    useEffect(() => {
        return () => {
            previewUrls.forEach(url => URL.revokeObjectURL(url));
        };
    }, [previewUrls]);

    // Auto-resize caption textarea
    useEffect(() => {
        if (captionTextareaRef.current) {
            captionTextareaRef.current.style.height = 'auto';
            captionTextareaRef.current.style.height = captionTextareaRef.current.scrollHeight + 'px';
        }
    }, [caption]);

    if (!isAuthenticated) {
        return (
            <div className="create-post-container">
                <div className="login-prompt">
                    <h2>Tạo Bài Đăng</h2>
                    <p>Vui lòng đăng nhập để tạo bài đăng mới</p>
                    <button onClick={() => navigate('/login')} className="login-btn">
                        Đăng Nhập
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="create-post-container">
            {/* Header */}
            <header className="create-post-header">
                <div className="header-content">
                    <button 
                        className="back-btn"
                        onClick={() => currentStep > 1 ? setCurrentStep(currentStep - 1) : navigate(-1)}
                    >
                        <span className="icon">←</span>
                    </button>
                    <h1 className="header-title">
                        {currentStep === 1 && 'Tạo bài đăng mới'}
                        {currentStep === 2 && 'Chỉnh sửa'}
                        {currentStep === 3 && 'Thông tin bài đăng'}
                    </h1>
                    {currentStep === 3 && (
                        <button 
                            className="share-btn"
                            onClick={handleSubmit}
                            disabled={isLoading}
                        >
                            {isLoading ? 'Đang đăng...' : 'Chia sẻ'}
                        </button>
                    )}
                </div>
            </header>

            <div className="create-post-content">
                {/* Step 1: Upload Media */}
                {currentStep === 1 && (
                    <div className="upload-step">
                        <div className="upload-container">
                            <div 
                                className="upload-area"
                                onDrop={handleDrop}
                                onDragOver={handleDragOver}
                                onClick={() => fileInputRef.current?.click()}
                            >
                                <div className="upload-icon">
                                    <span className="icon">📷</span>
                                </div>
                                <h3>Kéo ảnh và video vào đây</h3>
                                <p>hoặc chọn từ thiết bị của bạn</p>
                                <button className="select-files-btn">
                                    Chọn từ máy tính
                                </button>
                                
                                <input
                                    ref={fileInputRef}
                                    type="file"
                                    multiple
                                    accept="image/*,video/*"
                                    onChange={handleFileSelect}
                                    style={{ display: 'none' }}
                                />
                            </div>
                            
                            <div className="upload-tips">
                                <h4>Mẹo đăng bài:</h4>
                                <ul>
                                    <li>✓ Ảnh tỷ lệ 1:1, 4:5 hoặc 16:9 cho kết quả tốt nhất</li>
                                    <li>✓ Video tối đa 60 giây</li>
                                    <li>✓ Kích thước file tối đa 50MB</li>
                                    <li>✓ Hỗ trợ JPEG, PNG, MP4, MOV</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                )}

                {/* Step 2: Edit Media */}
                {currentStep === 2 && previewUrls.length > 0 && (
                    <div className="edit-step">
                        <div className="edit-container">
                            {/* Media Preview */}
                            <div className="media-preview">
                                <div className="media-container">
                                    {previewUrls.map((url, index) => (
                                        <div
                                            key={index}
                                            className={`media-item ${index === currentMediaIndex ? 'active' : ''}`}
                                            style={{ display: index === currentMediaIndex ? 'block' : 'none' }}
                                        >
                                            <img 
                                                src={url} 
                                                alt={`Preview ${index + 1}`}
                                                className={`media-image ${selectedFilter}`}
                                            />
                                            
                                            {/* Media Navigation */}
                                            {previewUrls.length > 1 && (
                                                <>
                                                    <button 
                                                        className="nav-btn prev-btn"
                                                        onClick={prevMedia}
                                                        disabled={currentMediaIndex === 0}
                                                    >
                                                        ←
                                                    </button>
                                                    <button 
                                                        className="nav-btn next-btn"
                                                        onClick={nextMedia}
                                                        disabled={currentMediaIndex === previewUrls.length - 1}
                                                    >
                                                        →
                                                    </button>
                                                    <div className="media-indicator">
                                                        {currentMediaIndex + 1} / {previewUrls.length}
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Edit Controls */}
                            <div className="edit-controls">
                                <div className="filter-section">
                                    <h4>Bộ lọc</h4>
                                    <div className="filter-grid">
                                        {availableFilters.map(filter => (
                                            <div
                                                key={filter.id}
                                                className={`filter-option ${selectedFilter === filter.id ? 'active' : ''}`}
                                                onClick={() => setSelectedFilter(filter.id)}
                                            >
                                                <div className={`filter-preview ${filter.className}`}>
                                                    <div className="filter-sample"></div>
                                                </div>
                                                <span className="filter-name">{filter.name}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="edit-actions">
                                    <button 
                                        className="edit-btn"
                                        onClick={() => setCurrentStep(3)}
                                    >
                                        Tiếp theo
                                    </button>
                                    <button 
                                        className="cancel-edit-btn"
                                        onClick={() => setCurrentStep(1)}
                                    >
                                        Quay lại
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* Step 3: Post Details */}
                {currentStep === 3 && previewUrls.length > 0 && (
                    <div className="details-step">
                        <div className="details-container">
                            {/* Media Thumbnail */}
                            <div className="media-thumbnail">
                                <img 
                                    src={previewUrls[currentMediaIndex]} 
                                    alt="Post thumbnail"
                                    className={`thumbnail-image ${selectedFilter}`}
                                />
                            </div>

                            {/* Post Details Form */}
                            <div className="details-form">
                                <div className="user-info">
                                    <img 
                                        src={user?.profilePicture || 'https://i.pravatar.cc/150'} 
                                        alt={user?.username}
                                        className="user-avatar"
                                    />
                                    <span className="username">{user?.username}</span>
                                </div>

                                {/* Caption Input */}
                                <div className="form-group">
                                    <label htmlFor="caption">Caption</label>
                                    <textarea
                                        ref={captionTextareaRef}
                                        id="caption"
                                        value={caption}
                                        onChange={(e) => setCaption(e.target.value)}
                                        placeholder="Viết caption..."
                                        className="caption-input"
                                        maxLength={maxCaptionLength}
                                    />
                                    <div className="character-count">
                                        {captionLength} / {maxCaptionLength}
                                    </div>
                                </div>

                                {/* Location Input */}
                                <div className="form-group">
                                    <label htmlFor="location">
                                        <span className="icon">📍</span> Thêm địa điểm
                                    </label>
                                    <input
                                        type="text"
                                        id="location"
                                        value={location}
                                        onChange={(e) => setLocation(e.target.value)}
                                        placeholder="Thêm địa điểm"
                                        className="location-input"
                                    />
                                </div>

                                {/* Hashtags Input */}
                                <div className="form-group">
                                    <label htmlFor="hashtags">
                                        <span className="icon">#</span> Hashtags
                                    </label>
                                    <textarea
                                        id="hashtags"
                                        value={hashtags}
                                        onChange={handleHashtagInput}
                                        placeholder="#example #tags #here"
                                        className="hashtags-input"
                                        rows="2"
                                    />
                                    <div className="hashtags-tip">
                                        Mỗi hashtag cách nhau bằng dấu cách
                                    </div>
                                </div>

                                {/* Advanced Options */}
                                <div className="advanced-options">
                                    <h4>Tùy chọn nâng cao</h4>
                                    <div className="option-item">
                                        <label className="switch">
                                            <input type="checkbox" />
                                            <span className="slider"></span>
                                        </label>
                                        <span>Tắt bình luận</span>
                                    </div>
                                    <div className="option-item">
                                        <label className="switch">
                                            <input type="checkbox" />
                                            <span className="slider"></span>
                                        </label>
                                        <span>Ẩn số lượt thích</span>
                                    </div>
                                </div>

                                {/* Action Buttons */}
                                <div className="action-buttons">
                                    <button 
                                        className="post-btn"
                                        onClick={handleSubmit}
                                        disabled={isLoading || !caption.trim()}
                                    >
                                        {isLoading ? (
                                            <>
                                                <span className="spinner"></span>
                                                Đang đăng...
                                            </>
                                        ) : (
                                            'Đăng bài'
                                        )}
                                    </button>
                                    <button 
                                        className="draft-btn"
                                        onClick={() => navigate('/')}
                                    >
                                        Lưu bản nháp
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default CreatePost;
