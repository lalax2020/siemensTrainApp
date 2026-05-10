import { useState } from 'react';
import api from '../../api/axios';
import Navbar from '../../components/Navbar';

export default function DelayPage() {
  const [form, setForm]     = useState({ routeStopId: '', actualTime: '' });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const [error, setError]   = useState('');

  const set = key => e => setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setSuccess('');
    setError('');
    try {
      await api.put(
        `/admin/route-stops/${form.routeStopId}/actual-time`,
        null,
        { params: { actualTime: form.actualTime } }
      );
      setSuccess('Actual arrival time recorded. Delay notifications sent to affected passengers.');
      setForm({ routeStopId: '', actualTime: '' });
    } catch (err) {
      setError(err.response?.data || 'Update failed. Check the route stop ID.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-md mx-auto py-10 px-4">
        <h2 className="text-2xl font-bold text-gray-800 mb-2">Record Arrival Delay</h2>
        <p className="text-sm text-gray-500 mb-6">
          Enter the actual arrival time for a route stop. If the train is late, affected passengers
          will automatically receive an email notification.
        </p>

        <div className="card p-6">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">Route Stop ID</label>
              <input required type="number" min="1" className="input"
                placeholder="e.g. 3"
                value={form.routeStopId} onChange={set('routeStopId')} />
            </div>
            <div>
              <label className="label">Actual Arrival Time</label>
              <input required type="time" step="60" className="input"
                value={form.actualTime} onChange={set('actualTime')} />
            </div>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>
            )}
            {success && (
              <p className="text-sm text-green-700 bg-green-50 rounded-lg px-3 py-2">{success}</p>
            )}

            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              {loading ? 'Recording…' : 'Record Actual Time'}
            </button>
          </form>
        </div>

        <div className="mt-6 card p-4 text-xs text-gray-500 space-y-1">
          <p className="font-semibold text-gray-700 mb-2">How it works</p>
          <p>1. Enter the route stop ID from the database.</p>
          <p>2. Enter when the train actually arrived at that stop.</p>
          <p>3. Delay = actual − expected. If &gt; 0 minutes, emails are sent to all passengers booked on affected schedules running today.</p>
        </div>
      </div>
    </div>
  );
}
