// App.js
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import MainLayout from './layouts/MainLayout';
import Home from './pages/Home/Home';
import Profile from './pages/Profile/Profile';
import Message from './pages/Messages/Messages';
import CreatePost from './components/ui/CreatePost/CreatePost';
import Login from './pages/Auth/Login/Login';
import Register from './pages/Auth/Register/Register';
import PostDetail from './pages/PostDetail/PostDetail';
import Search from './pages/Search/Search'
import Settings from './pages/Settings/Settings'
import './App.css';

// Protected Route Component với Layout
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) return <div>Loading...</div>;
  
  return isAuthenticated ? (
    <MainLayout>{children}</MainLayout>
  ) : (
    <Navigate to="/login" replace />
  );
};

// Public Route Component (không có Layout)
const PublicRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) return <div>Loading...</div>;
  
  return !isAuthenticated ? children : <Navigate to="/" replace />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public Routes (không có Sidebar) */}
            <Route path="/login" element={<PublicRoute><Login /></PublicRoute>} />
            <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

            {/* Protected Routes (có Sidebar thông qua MainLayout) */}
            <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
            <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
            <Route path="/message" element={<ProtectedRoute><Message /></ProtectedRoute>} />
            <Route path="/create-post" element={<ProtectedRoute><CreatePost /></ProtectedRoute>} />
            <Route path="/search" element={<ProtectedRoute><Search /></ProtectedRoute>} />
            <Route path="/settings" element={<ProtectedRoute><Settings /></ProtectedRoute>} />

            {/* Thêm các route khác nếu cần */}
            <Route path="/explore" element={<ProtectedRoute><div>Explore Page</div></ProtectedRoute>} />
            <Route path="/reels" element={<ProtectedRoute><div>Reels Page</div></ProtectedRoute>} />
            <Route path="/notifications" element={<ProtectedRoute><div>Notifications Page</div></ProtectedRoute>} />
            <Route path="/dashboard" element={<ProtectedRoute><div>Dashboard Page</div></ProtectedRoute>} />
            <Route path="/post/:postId" element={<ProtectedRoute><PostDetail /></ProtectedRoute>} />

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
