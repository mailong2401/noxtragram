// layouts/MainLayout.jsx
import Sidebar from '../components/layout/Sidebar/Sidebar';
import styles from './MainLayout.module.css';

const MainLayout = ({ children }) => {
  return (
    <div className={styles.layout}>
      <Sidebar />
      <main className={styles.main}>
        {children}
      </main>
    </div>
  );
};

export default MainLayout;
