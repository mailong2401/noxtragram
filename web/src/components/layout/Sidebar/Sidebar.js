import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext'; // Import auth context
import './Sidebar.css';

// import icons
import homeIcon from '../../../assets/icons/home.png';
import searchIcon from '../../../assets/icons/search.png';
import exploreIcon from '../../../assets/icons/explore.png';
import reelsIcon from '../../../assets/icons/reels.png';
import messageIcon from '../../../assets/icons/message.png';
import notificationsIcon from '../../../assets/icons/notifications.png';
import createIcon from '../../../assets/icons/create.png';
import dashboardIcon from '../../../assets/icons/dashboard.png';
import profileIcon from '../../../assets/icons/profile.png';
import settingIcon from '../../../assets/icons/setting.png';
import logoutIcon from '../../../assets/icons/logout.png';

const Sidebar = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout } = useAuth(); // Get logout function from auth context

  const menuItems = [
    { name: 'Home', path: '/', icon: homeIcon },
    { name: 'Search', path: '/search', icon: searchIcon },
    { name: 'Explore', path: '/explore', icon: exploreIcon },
    { name: 'Reels', path: '/reels', icon: reelsIcon },
    { name: 'Messages', path: '/message', icon: messageIcon },
    { name: 'Notifications', path: '/notifications', icon: notificationsIcon },
    { name: 'Create', path: '/create-post', icon: createIcon },
    { name: 'Dashboard', path: '/dashboard', icon: dashboardIcon },
    { name: 'Profile', path: '/profile', icon: profileIcon },
    { name: 'Setting', path: '/setting', icon: settingIcon },
  ];

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    logout();
    navigate('/login');
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
              <img src={item.icon} alt={`${item.name} icon`} />
            </div>
            <span className="sidebar-menu-text">{item.name}</span>
          </Link>
        ))}
        
        {/* Logout button with custom handler */}
        <button 
          onClick={handleLogout}
          className="sidebar-menu-item"
          style={{ background: 'none', border: 'none', width: '100%', textAlign: 'left' }}
        >
          <div className="sidebar-icon">
            <img src={logoutIcon} alt="Logout icon" />
          </div>
          <span className="sidebar-menu-text">Logout</span>
        </button>
      </nav>
    </div>
  );
};

export default Sidebar;
