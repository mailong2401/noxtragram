import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Link, useNavigate } from 'react-router-dom';
import PostList from '../../components/ui/PostList/PostList';
import './Home.css';

const Home = () => {
    const { user, isAuthenticated, logout } = useAuth();
    const [posts, setPosts] = useState([]);
    const [stories, setStories] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        // Mock data for stories
        const mockStories = [
            { id: 1, username: 'john_doe', avatar: 'https://i.pravatar.cc/150?img=1', hasNew: true },
            { id: 2, username: 'jane_smith', avatar: 'https://i.pravatar.cc/150?img=2', hasNew: true },
            { id: 3, username: 'mike_wilson', avatar: 'https://i.pravatar.cc/150?img=3', hasNew: false },
            { id: 4, username: 'sarah_j', avatar: 'https://i.pravatar.cc/150?img=4', hasNew: true },
            { id: 5, username: 'tom_cruise', avatar: 'https://i.pravatar.cc/150?img=5', hasNew: false }
        ];

        setStories(mockStories);
        
        // Posts sẽ được fetch bởi PostList component
        setIsLoading(false);
    }, []);

    const handlePostsUpdate = (updatedPosts) => {
        setPosts(updatedPosts);
    };

    const handleLogout = () => {
        logout();
    };

    if (!isAuthenticated) {
        return (
            <div className="home-container">
                <div className="login-prompt">
                    <h2>Welcome to Instagram Clone</h2>
                    <p>Please log in to see the content</p>
                    <Link to="/login" className="login-btn">Log In</Link>
                </div>
            </div>
        );
    }

    return (
        <div className="home-container">
            {/* Stories Section */}
            <div className="stories-container">
                <div className="stories">
                    {stories.map(story => (
                        <div key={story.id} className="story">
                            <div className={`story-avatar ${story.hasNew ? 'has-new' : ''}`}>
                                <img src={story.avatar} alt={story.username} />
                            </div>
                            <span className="story-username">{story.username}</span>
                        </div>
                    ))}
                </div>
            </div>

            {/* Posts Section với PostList */}
            <PostList 
                onPostsUpdate={handlePostsUpdate}
                showUserInfo={true}
                enableInteraction={true}
                emptyMessage="No posts available. Follow more users to see posts in your feed!"
            />
        </div>
    );
};

export default Home;
