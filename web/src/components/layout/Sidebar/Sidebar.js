// components/layout/Sidebar/Sidebar.jsx
import { Link, useLocation } from 'react-router-dom';
import './Sidebar.css'

const Sidebar = () => {
  const location = useLocation();

  const menuItems = [
    { 
      name: 'Home', 
      path: '/',
      icon: '🏠'
    },
    { 
      name: 'Search', 
      path: '/search',
      icon: '🔍'
    },
    { 
      name: 'Explore', 
      path: '/explore',
      icon: '🌎'
    },
    { 
      name: 'Reels', 
      path: '/reels',
      icon: '🎬'
    },
    { 
      name: 'Messages', 
      path: '/message',
      icon: '💬'
    },
    { 
      name: 'Notifications', 
      path: '/notifications',
      icon: '🔔'
    },
    { 
      name: 'Create', 
      path: '/create-post',
      icon: '➕'
    },
    { 
      name: 'Dashboard', 
      path: '/dashboard',
      icon: '📊'
    },
    { 
      name: 'Profile', 
      path: '/profile',
      icon: '👤'
    }
  ];

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h1 className="sidebar-title">Noxtragram</h1>
      </div>
      
      <nav className="sidebar-nav">
        {menuItems.map((item) => (
          <Link 
            key={item.name} 
            to={item.path}
            className={`sidebar-menu-item ${isActive(item.path) ? 'active' : ''}`}
          >
            <div className="sidebar-icon">
              {item.icon}
            </div>
            <span className="sidebar-menu-text">{item.name}</span>
          </Link>
        ))}
      </nav>
    </div>
  );
};

export default Sidebar;
