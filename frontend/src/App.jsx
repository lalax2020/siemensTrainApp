import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import SearchPage from './pages/SearchPage';
import BookingPage from './pages/BookingPage';
import TrainsPage from './pages/admin/TrainsPage';
import AdminBookingsPage from './pages/admin/BookingsPage';
import DelayPage from './pages/admin/DelayPage';

export default function App() {
  const { token, isAdmin } = useAuth();
  const home = token ? (isAdmin ? '/admin/trains' : '/search') : '/login';

  return (
    <Routes>
      <Route path="/login" element={
        token ? <Navigate to={home} replace /> : <LoginPage />
      } />

      <Route path="/search" element={
        <ProtectedRoute><SearchPage /></ProtectedRoute>
      } />
      <Route path="/book" element={
        <ProtectedRoute><BookingPage /></ProtectedRoute>
      } />

      <Route path="/admin/trains" element={
        <ProtectedRoute adminOnly><TrainsPage /></ProtectedRoute>
      } />
      <Route path="/admin/bookings" element={
        <ProtectedRoute adminOnly><AdminBookingsPage /></ProtectedRoute>
      } />
      <Route path="/admin/delays" element={
        <ProtectedRoute adminOnly><DelayPage /></ProtectedRoute>
      } />

      <Route path="*" element={<Navigate to={home} replace />} />
    </Routes>
  );
}
