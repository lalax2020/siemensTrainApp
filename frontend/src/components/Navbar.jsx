import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { role, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <nav className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-10">
      <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
        <div className="flex items-center gap-6">
          <Link
            to={isAdmin ? '/admin/trains' : '/search'}
            className="font-bold text-blue-700 text-lg tracking-tight"
          >
            Siemens Trains
          </Link>

          {isAdmin ? (
            <>
              <Link to="/admin/trains"   className="nav-link">Trains</Link>
              <Link to="/admin/bookings" className="nav-link">Bookings</Link>
              <Link to="/admin/delays"   className="nav-link">Delays</Link>
              <Link to="/search"         className="nav-link">Search</Link>
            </>
          ) : (
            <Link to="/search" className="nav-link">Search</Link>
          )}
        </div>

        <div className="flex items-center gap-3">
          <span className="text-xs text-gray-400 uppercase tracking-widest">{role}</span>
          <button
            onClick={handleLogout}
            className="text-sm text-gray-500 hover:text-red-500 transition"
          >
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
