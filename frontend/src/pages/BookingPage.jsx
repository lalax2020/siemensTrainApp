import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function BookingPage() {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state?.journey) {
    navigate('/search', { replace: true });
    return null;
  }

  const { journey, departure, arrival, date } = state;
  const firstLeg = journey.legs[0];

  const [form, setForm] = useState({
    customerName: '',
    customerEmail: '',
    numberOfSeats: 1,
    scheduleId: firstLeg.scheduleId ?? '',
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState('');

  const set = key => e => setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const { data } = await api.post('/bookings', {
        customerName:  form.customerName,
        customerEmail: form.customerEmail,
        scheduleId:    Number(form.scheduleId),
        numberOfSeats: Number(form.numberOfSeats),
        travelDate:    date,
        fromStation:   departure,
        toStation:     arrival,
      });
      setSuccess(data);
    } catch (err) {
      setError(err.response?.data || 'Booking failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="max-w-md mx-auto py-20 px-4 text-center">
          <div className="card p-8">
            <div className="w-14 h-14 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-7 h-7 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-1">Booking Confirmed</h2>
            <p className="text-gray-500 text-sm mb-4">Booking ID: <span className="font-mono font-semibold text-gray-700">#{success.id}</span></p>
            <div className="bg-gray-50 rounded-xl p-4 text-sm text-gray-700 space-y-1 text-left mb-6">
              <p><span className="text-gray-400">Route:</span> {success.fromStation} → {success.toStation}</p>
              <p><span className="text-gray-400">Date:</span> {success.travelDate}</p>
              <p><span className="text-gray-400">Seats:</span> {success.numberOfSeats}</p>
              <p><span className="text-gray-400">Status:</span> <span className="badge-active">ACTIVE</span></p>
            </div>
            <p className="text-xs text-gray-400 mb-6">
              A confirmation email was sent to {success.customerEmail}.
            </p>
            <button onClick={() => navigate('/search')} className="btn-primary px-8 py-2">
              Search Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-lg mx-auto py-10 px-4">
        <button onClick={() => navigate(-1)} className="text-blue-600 text-sm mb-4 hover:underline">
          ← Back to results
        </button>
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Complete Your Booking</h2>

        <div className="bg-blue-50 border border-blue-100 rounded-xl p-4 mb-6">
          <p className="text-sm font-semibold text-blue-800 mb-1">
            {departure} → {arrival} · {date}
          </p>
          {journey.legs.map((leg, i) => (
            <p key={i} className="text-sm text-blue-700">
              {leg.trainName}: {leg.departure} – {leg.arrival}
              {journey.legs.length > 1 && i < journey.legs.length - 1 && (
                <span className="text-blue-400"> (changeover at {leg.toStation})</span>
              )}
            </p>
          ))}
        </div>

        <form onSubmit={handleSubmit} className="card p-6 space-y-4">
          <div>
            <label className="label">Full Name</label>
            <input required className="input" value={form.customerName} onChange={set('customerName')} />
          </div>
          <div>
            <label className="label">Email Address</label>
            <input required type="email" className="input" value={form.customerEmail} onChange={set('customerEmail')} />
          </div>
          <div>
            <label className="label">Number of Seats</label>
            <input required type="number" min="1" max="10" className="input"
              value={form.numberOfSeats} onChange={set('numberOfSeats')} />
          </div>
          <div>
            <label className="label">Schedule ID</label>
            <input required type="number" className="input"
              value={form.scheduleId} onChange={set('scheduleId')} />
            <p className="text-xs text-gray-400 mt-1">
              Pre-filled from search results.
              {journey.legs.length > 1 && ' For changeover journeys, this books the first leg.'}
            </p>
          </div>

          {error && (
            <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>
          )}

          <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
            {loading ? 'Confirming…' : 'Confirm Booking'}
          </button>
        </form>
      </div>
    </div>
  );
}
