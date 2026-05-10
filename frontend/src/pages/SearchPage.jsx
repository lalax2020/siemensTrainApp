import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

export default function SearchPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ departure: '', arrival: '', date: '' });
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const set = key => e => setForm(f => ({ ...f, [key]: e.target.value }));

  const handleSearch = async e => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResults(null);
    try {
      const { data } = await api.get('/search', { params: form });
      setResults(data);
    } catch (err) {
      setError(err.response?.data || 'No journeys found for this route and date.');
    } finally {
      setLoading(false);
    }
  };

  const handleBook = journey => {
    navigate('/book', { state: { journey, ...form } });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-3xl mx-auto py-10 px-4">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Find a Journey</h2>

        <form onSubmit={handleSearch} className="card p-6 grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
          <div>
            <label className="label">From</label>
            <input required className="input" placeholder="e.g. Bucharest"
              value={form.departure} onChange={set('departure')} />
          </div>
          <div>
            <label className="label">To</label>
            <input required className="input" placeholder="e.g. Cluj"
              value={form.arrival} onChange={set('arrival')} />
          </div>
          <div>
            <label className="label">Date</label>
            <input required type="date" className="input"
              value={form.date} onChange={set('date')} />
          </div>
          <div className="sm:col-span-3">
            <button type="submit" disabled={loading} className="btn-primary w-full py-2.5">
              {loading ? 'Searching…' : 'Search'}
            </button>
          </div>
        </form>

        {error && (
          <p className="text-center text-gray-500 py-8">{error}</p>
        )}

        {results?.length === 0 && (
          <p className="text-center text-gray-400 py-8">No journeys available for this route and date.</p>
        )}

        <div className="space-y-4">
          {results?.map((journey, i) => (
            <div key={i} className="card p-5">
              <div className="flex items-center justify-between mb-3">
                <div>
                  <span className="text-lg font-semibold text-gray-900">
                    {journey.totalDeparture}
                  </span>
                  <span className="text-gray-400 mx-2">→</span>
                  <span className="text-lg font-semibold text-gray-900">
                    {journey.totalArrival}
                  </span>
                  {journey.legs.length > 1 && (
                    <span className="ml-3 text-xs text-amber-600 font-medium bg-amber-50 px-2 py-0.5 rounded-full">
                      1 changeover
                    </span>
                  )}
                </div>
                <button onClick={() => handleBook(journey)} className="btn-primary px-5">
                  Book
                </button>
              </div>

              <div className="border-t border-gray-100 pt-3 space-y-1">
                {journey.legs.map((leg, j) => (
                  <div key={j} className="flex items-center gap-2 text-sm text-gray-600">
                    <span className="font-semibold text-blue-600 w-24 shrink-0">{leg.trainName}</span>
                    <span>{leg.fromStation}</span>
                    <span className="text-gray-300">→</span>
                    <span>{leg.toStation}</span>
                    <span className="ml-auto text-gray-400 tabular-nums">
                      {leg.departure} – {leg.arrival}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
