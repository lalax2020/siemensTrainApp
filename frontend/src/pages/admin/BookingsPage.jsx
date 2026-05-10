import { useState } from 'react';
import api from '../../api/axios';
import Navbar from '../../components/Navbar';

export default function AdminBookingsPage() {
  const [form, setForm]       = useState({ scheduleId: '', date: '' });
  const [bookings, setBookings] = useState(null);
  const [loading, setLoading]  = useState(false);
  const [error, setError]      = useState('');

  const set = key => e => setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSearch = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const { data } = await api.get('/admin/bookings', {
        params: { scheduleId: form.scheduleId, date: form.date },
      });
      setBookings(data);
    } catch (err) {
      setError(err.response?.data || 'Failed to load bookings.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async id => {
    if (!window.confirm('Cancel this booking?')) return;
    try {
      await api.delete(`/bookings/${id}`);
      setBookings(bs => bs.map(b => b.id === id ? { ...b, status: 'CANCELLED' } : b));
    } catch {
      setError('Cancellation failed.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto py-10 px-4">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Bookings</h2>

        <form onSubmit={handleSearch} className="card p-5 flex flex-wrap gap-4 mb-8 items-end">
          <div className="flex-1 min-w-[160px]">
            <label className="label">Schedule ID</label>
            <input required type="number" className="input"
              value={form.scheduleId} onChange={set('scheduleId')} />
          </div>
          <div className="flex-1 min-w-[160px]">
            <label className="label">Travel Date</label>
            <input required type="date" className="input"
              value={form.date} onChange={set('date')} />
          </div>
          <button type="submit" disabled={loading} className="btn-primary px-8 py-2">
            {loading ? 'Loading…' : 'Search'}
          </button>
        </form>

        {error && <p className="text-red-600 text-sm mb-4">{error}</p>}

        {bookings && (
          <div className="card overflow-hidden">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr className="text-left text-xs font-semibold text-gray-500 uppercase tracking-wide">
                  <th className="px-4 py-3">ID</th>
                  <th className="px-4 py-3">Customer</th>
                  <th className="px-4 py-3">Route</th>
                  <th className="px-4 py-3">Seats</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-50">
                {bookings.map(b => (
                  <tr key={b.id} className="hover:bg-gray-50 transition">
                    <td className="px-4 py-3 text-gray-400 tabular-nums">{b.id}</td>
                    <td className="px-4 py-3">
                      <p className="font-medium text-gray-800">{b.customerName}</p>
                      <p className="text-xs text-gray-400">{b.customerEmail}</p>
                    </td>
                    <td className="px-4 py-3 text-gray-600">{b.fromStation} → {b.toStation}</td>
                    <td className="px-4 py-3 text-gray-600">{b.numberOfSeats}</td>
                    <td className="px-4 py-3">
                      <span className={b.status === 'ACTIVE' ? 'badge-active' : 'badge-cancelled'}>
                        {b.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      {b.status === 'ACTIVE' && (
                        <button onClick={() => handleCancel(b.id)}
                          className="text-red-500 hover:underline text-xs font-medium">
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {bookings.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-10 text-center text-gray-400">
                      No bookings found for this schedule and date.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
