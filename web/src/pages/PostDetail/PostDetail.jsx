// pages/PostDetail/PostDetail.jsx
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PostCard from '../../components/ui/PostCard/PostCard';
import postService from '../../services/postService';
import { useAuth } from '../../contexts/AuthContext';
import './PostDetail.css';

const PostDetail = () => {
    const { postId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchPost();
    }, [postId]);

    const fetchPost = async () => {
        try {
            setLoading(true);
            const postData = await postService.getPost(postId);
            setPost(postData);
        } catch (err) {
            setError(err.message);
            console.error('Error fetching post:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdatePost = (updatedPost) => {
        setPost(updatedPost);
    };

    const handleBack = () => {
        navigate(-1);
    };

    if (loading) {
        return (
            <div className="post-detail-container">
                <div className="post-detail-loading">
                    <div className="loading-spinner"></div>
                    <p>Loading post...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="post-detail-container">
                <div className="post-detail-error">
                    <h2>Error</h2>
                    <p>{error}</p>
                    <button onClick={handleBack} className="back-btn">
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    if (!post) {
        return (
            <div className="post-detail-container">
                <div className="post-detail-not-found">
                    <h2>Post Not Found</h2>
                    <p>The post you're looking for doesn't exist.</p>
                    <button onClick={handleBack} className="back-btn">
                        Go Back
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="post-detail-container">
            <div className="post-detail-header">
                <div className="post-detail-header-content">
                    <button className="back-button" onClick={handleBack}>
                        <span className="icon">←</span> Back
                    </button>
                    <h1 className="post-detail-title">Post</h1>
                </div>
            </div>
            
            <div className="post-detail-content">
                <PostCard 
                    post={post} 
                    onUpdate={handleUpdatePost}
                />
            </div>
        </div>
    );
};

export default PostDetail;
