import React, { useState } from 'react';
import { 
  User, Lock, Eye, Bell, Smartphone, LogOut, 
  Trash2, Moon, Sun, Globe, Save, Camera,
  ChevronRight, Shield, Users, MessageCircle,
  Mail, Key, Smartphone as Device, Download
} from 'lucide-react';
import './Settings.css';

const Settings = () => {
  const [activeSection, setActiveSection] = useState('profile');
  const [theme, setTheme] = useState('light');
  const [privacySettings, setPrivacySettings] = useState({
    isPrivate: false,
    hideOnlineStatus: true,
    allowFollowRequests: 'everyone',
    allowComments: 'everyone',
    allowMessages: 'followers'
  });
  const [notifications, setNotifications] = useState({
    push: true,
    email: false,
    sms: false
  });

  // Menu items
  const menuItems = [
    { id: 'profile', icon: User, label: 'Cài đặt hồ sơ', color: '#3B82F6' },
    { id: 'security', icon: Lock, label: 'Bảo mật & Mật khẩu', color: '#EF4444' },
    { id: 'privacy', icon: Eye, label: 'Quyền riêng tư', color: '#8B5CF6' },
    { id: 'notifications', icon: Bell, label: 'Thông báo', color: '#F59E0B' },
    { id: 'preferences', icon: Smartphone, label: 'Tuỳ chọn ứng dụng', color: '#10B981' },
    { id: 'account', icon: Shield, label: 'Quản lý tài khoản', color: '#6B7280' }
  ];

  // Render Sections
  const renderProfileSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <User size={24} />
        <h2>Cài đặt hồ sơ</h2>
      </div>
      
      <div className="avatar-section">
        <div className="avatar-upload">
          <img 
            src="/default-avatar.png" 
            alt="Avatar"
            className="avatar-preview"
          />
          <label className="upload-btn">
            <Camera size={16} />
            Thay đổi ảnh
            <input type="file" accept="image/*" hidden />
          </label>
        </div>
      </div>

      <div className="form-grid">
        <div className="form-group">
          <label>Họ tên *</label>
          <input type="text" defaultValue="Nguyễn Văn A" />
        </div>

        <div className="form-group">
          <label>Username *</label>
          <input type="text" defaultValue="nguyenvana" />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input type="email" defaultValue="nguyenvana@email.com" disabled />
          <small>Email không thể thay đổi</small>
        </div>

        <div className="form-group">
          <label>Số điện thoại</label>
          <input type="tel" defaultValue="+84 123 456 789" />
        </div>

        <div className="form-group">
          <label>Giới tính</label>
          <select defaultValue="male">
            <option value="male">Nam</option>
            <option value="female">Nữ</option>
            <option value="other">Khác</option>
          </select>
        </div>

        <div className="form-group">
          <label>Ngày sinh</label>
          <input type="date" defaultValue="1990-01-01" />
        </div>
      </div>

      <div className="form-group">
        <label>Bio</label>
        <textarea 
          rows="3" 
          placeholder="Giới thiệu về bản thân..."
          defaultValue="Xin chào! Tôi là Nguyễn Văn A 👋"
          maxLength="150"
        />
        <small>0/150 ký tự</small>
      </div>

      <button className="btn-primary">
        <Save size={16} />
        Cập nhật thông tin
      </button>
    </div>
  );

  const renderSecuritySettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <Lock size={24} />
        <h2>Bảo mật & Mật khẩu</h2>
      </div>

      <div className="security-cards">
        <div className="security-card">
          <div className="card-header">
            <Key size={20} />
            <h3>Thay đổi mật khẩu</h3>
          </div>
          <div className="form-group">
            <label>Mật khẩu hiện tại</label>
            <input type="password" placeholder="Nhập mật khẩu hiện tại" />
          </div>
          <div className="form-group">
            <label>Mật khẩu mới</label>
            <input type="password" placeholder="Nhập mật khẩu mới" />
          </div>
          <div className="form-group">
            <label>Xác nhận mật khẩu mới</label>
            <input type="password" placeholder="Nhập lại mật khẩu mới" />
          </div>
          <button className="btn-primary">Cập nhật mật khẩu</button>
        </div>

        <div className="security-card">
          <div className="card-header">
            <Device size={20} />
            <h3>Thiết bị đang đăng nhập</h3>
          </div>
          <div className="device-list">
            <div className="device-item active">
              <Smartphone size={16} />
              <div className="device-info">
                <span>iPhone 14 Pro - Hiện tại</span>
                <small>Hà Nội, Vietnam • Chrome</small>
              </div>
            </div>
            <div className="device-item">
              <Smartphone size={16} />
              <div className="device-info">
                <span>MacBook Pro</span>
                <small>TP.HCM, Vietnam • Safari</small>
              </div>
            </div>
          </div>
          <button className="btn-secondary">Đăng xuất tất cả thiết bị</button>
        </div>

        <div className="security-card">
          <div className="card-header">
            <Shield size={20} />
            <h3>Xác thực 2 lớp (2FA)</h3>
          </div>
          <p>Bảo vệ tài khoản của bạn bằng xác thực 2 lớp</p>
          <button className="btn-secondary">Thiết lập 2FA</button>
        </div>
      </div>
    </div>
  );

  const renderPrivacySettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <Eye size={24} />
        <h2>Quyền riêng tư</h2>
      </div>

      <div className="privacy-settings">
        <div className="privacy-item">
          <div className="privacy-info">
            <Users size={20} />
            <div>
              <h3>Tài khoản riêng tư</h3>
              <p>Chỉ người theo dõi có thể xem bài viết của bạn</p>
            </div>
          </div>
          <label className="toggle">
            <input 
              type="checkbox" 
              checked={privacySettings.isPrivate}
              onChange={(e) => setPrivacySettings(prev => ({
                ...prev, isPrivate: e.target.checked
              }))}
            />
            <span className="slider"></span>
          </label>
        </div>

        <div className="privacy-item">
          <div className="privacy-info">
            <Globe size={20} />
            <div>
              <h3>Ẩn trạng thái hoạt động</h3>
              <p>Không hiển thị khi bạn đang online</p>
            </div>
          </div>
          <label className="toggle">
            <input 
              type="checkbox" 
              checked={privacySettings.hideOnlineStatus}
              onChange={(e) => setPrivacySettings(prev => ({
                ...prev, hideOnlineStatus: e.target.checked
              }))}
            />
            <span className="slider"></span>
          </label>
        </div>

        <div className="privacy-item">
          <div className="privacy-info">
            <User size={20} />
            <div>
              <h3>Ai có thể theo dõi bạn</h3>
              <p>Kiểm soát ai có thể gửi lời mời theo dõi</p>
            </div>
          </div>
          <select 
            value={privacySettings.allowFollowRequests}
            onChange={(e) => setPrivacySettings(prev => ({
              ...prev, allowFollowRequests: e.target.value
            }))}
          >
            <option value="everyone">Mọi người</option>
            <option value="friends">Bạn của bạn bè</option>
            <option value="none">Không ai</option>
          </select>
        </div>

        <div className="privacy-item">
          <div className="privacy-info">
            <MessageCircle size={20} />
            <div>
              <h3>Ai có thể bình luận</h3>
              <p>Kiểm soát ai có thể bình luận bài viết của bạn</p>
            </div>
          </div>
          <select 
            value={privacySettings.allowComments}
            onChange={(e) => setPrivacySettings(prev => ({
              ...prev, allowComments: e.target.value
            }))}
          >
            <option value="everyone">Mọi người</option>
            <option value="followers">Người theo dõi</option>
            <option value="following">Người bạn theo dõi</option>
          </select>
        </div>

        <div className="privacy-item">
          <div className="privacy-info">
            <Mail size={20} />
            <div>
              <h3>Ai có thể nhắn tin</h3>
              <p>Kiểm soát ai có thể gửi tin nhắn cho bạn</p>
            </div>
          </div>
          <select 
            value={privacySettings.allowMessages}
            onChange={(e) => setPrivacySettings(prev => ({
              ...prev, allowMessages: e.target.value
            }))}
          >
            <option value="everyone">Mọi người</option>
            <option value="followers">Người theo dõi</option>
            <option value="none">Không ai</option>
          </select>
        </div>
      </div>

      <button className="btn-primary">Lưu cài đặt</button>
    </div>
  );

  const renderNotificationSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <Bell size={24} />
        <h2>Cài đặt thông báo</h2>
      </div>

      <div className="notification-settings">
        <div className="notification-category">
          <h3>Đẩy thông báo</h3>
          <div className="notification-item">
            <div className="notification-info">
              <h4>Thông báo đẩy</h4>
              <p>Nhận thông báo trên thiết bị</p>
            </div>
            <label className="toggle">
              <input 
                type="checkbox" 
                checked={notifications.push}
                onChange={(e) => setNotifications(prev => ({
                  ...prev, push: e.target.checked
                }))}
              />
              <span className="slider"></span>
            </label>
          </div>
        </div>

        <div className="notification-category">
          <h3>Email</h3>
          <div className="notification-item">
            <div className="notification-info">
              <h4>Thông báo qua email</h4>
              <p>Nhận thông báo qua email</p>
            </div>
            <label className="toggle">
              <input 
                type="checkbox" 
                checked={notifications.email}
                onChange={(e) => setNotifications(prev => ({
                  ...prev, email: e.target.checked
                }))}
              />
              <span className="slider"></span>
            </label>
          </div>
        </div>

        <div className="notification-category">
          <h3>SMS</h3>
          <div className="notification-item">
            <div className="notification-info">
              <h4>Thông báo SMS</h4>
              <p>Nhận thông báo qua tin nhắn</p>
            </div>
            <label className="toggle">
              <input 
                type="checkbox" 
                checked={notifications.sms}
                onChange={(e) => setNotifications(prev => ({
                  ...prev, sms: e.target.checked
                }))}
              />
              <span className="slider"></span>
            </label>
          </div>
        </div>
      </div>
    </div>
  );

  const renderPreferences = () => (
    <div className="settings-section">
      <div className="section-header">
        <Smartphone size={24} />
        <h2>Tuỳ chọn ứng dụng</h2>
      </div>

      <div className="preference-settings">
        <div className="preference-item">
          <div className="preference-info">
            <Moon size={20} />
            <div>
              <h3>Chế độ tối</h3>
              <p>Chuyển đổi giữa chế độ sáng và tối</p>
            </div>
          </div>
          <div className="theme-toggle">
            <button 
              className={`theme-option ${theme === 'light' ? 'active' : ''}`}
              onClick={() => setTheme('light')}
            >
              <Sun size={16} />
              Sáng
            </button>
            <button 
              className={`theme-option ${theme === 'dark' ? 'active' : ''}`}
              onClick={() => setTheme('dark')}
            >
              <Moon size={16} />
              Tối
            </button>
          </div>
        </div>

        <div className="preference-item">
          <div className="preference-info">
            <Globe size={20} />
            <div>
              <h3>Ngôn ngữ</h3>
              <p>Ngôn ngữ hiển thị ứng dụng</p>
            </div>
          </div>
          <select defaultValue="vi">
            <option value="vi">Tiếng Việt</option>
            <option value="en">English</option>
            <option value="zh">中文</option>
          </select>
        </div>

        <div className="preference-item">
          <div className="preference-info">
            <Download size={20} />
            <div>
              <h3>Tự động phát video</h3>
              <p>Tự động phát video khi cuộn</p>
            </div>
          </div>
          <label className="toggle">
            <input type="checkbox" defaultChecked />
            <span className="slider"></span>
          </label>
        </div>
      </div>
    </div>
  );

  const renderAccountManagement = () => (
    <div className="settings-section">
      <div className="section-header">
        <Shield size={24} />
        <h2>Quản lý tài khoản</h2>
      </div>

      <div className="account-actions">
        <div className="account-action">
          <div className="action-info">
            <LogOut size={20} />
            <div>
              <h3>Đăng xuất</h3>
              <p>Đăng xuất khỏi tài khoản hiện tại</p>
            </div>
          </div>
          <button className="btn-secondary">Đăng xuất</button>
        </div>

        <div className="account-action">
          <div className="action-info">
            <Eye size={20} />
            <div>
              <h3>Vô hiệu hóa tài khoản</h3>
              <p>Tạm thời ẩn tài khoản của bạn</p>
            </div>
          </div>
          <button className="btn-warning">Vô hiệu hóa</button>
        </div>

        <div className="account-action danger">
          <div className="action-info">
            <Trash2 size={20} />
            <div>
              <h3>Xóa tài khoản</h3>
              <p>Xóa vĩnh viễn tài khoản và dữ liệu</p>
            </div>
          </div>
          <button className="btn-danger">Xóa tài khoản</button>
        </div>
      </div>
    </div>
  );

  const renderContent = () => {
    switch (activeSection) {
      case 'profile': return renderProfileSettings();
      case 'security': return renderSecuritySettings();
      case 'privacy': return renderPrivacySettings();
      case 'notifications': return renderNotificationSettings();
      case 'preferences': return renderPreferences();
      case 'account': return renderAccountManagement();
      default: return renderProfileSettings();
    }
  };

  return (
    <div className="settings-page">
      <div className="settings-container">
        {/* Sidebar Menu */}
        <div className="settings-sidebar">
          <div className="sidebar-header">
            <h2>Cài đặt</h2>
          </div>
          <nav className="sidebar-nav">
            {menuItems.map(item => {
              const Icon = item.icon;
              return (
                <button
                  key={item.id}
                  className={`nav-item ${activeSection === item.id ? 'active' : ''}`}
                  onClick={() => setActiveSection(item.id)}
                >
                  <div className="nav-icon" style={{ backgroundColor: item.color }}>
                    <Icon size={18} />
                  </div>
                  <span>{item.label}</span>
                  <ChevronRight size={16} className="nav-arrow" />
                </button>
              );
            })}
          </nav>
        </div>

        {/* Main Content */}
        <div className="settings-main">
          {renderContent()}
        </div>
      </div>
    </div>
  );
};

export default Settings;
