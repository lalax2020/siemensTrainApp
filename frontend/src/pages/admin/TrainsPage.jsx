import { useEffect, useState } from 'react';
import api from '../../api/axios';
import Navbar from '../../components/Navbar';

const EMPTY = { name: '', capacity: '', routeId: '' };

export default function TrainsPage() {
  const [trains, setTrains]   = useState([]);
  const [form, setForm]       = useState(EMPTY);
  const [editingId, setEditing] = useState(null);
  const [error, setError]     = useState('');

  const set = key => e => setForm(f => ({ ...f, [key]: e.target.value }));

  const load = async () => {
    const { data } = await api.get('/trains');
    setTrains(data);
  };

  useEffect(() => { load(); }, []);

  const startEdit = t => {
    setEditing(t.id);
    setForm({ name: t.name, capacity: String(t.capacity), routeId: t.routeId ?? '' });
  };

  const cancelEdit = () => { setEditing(null); setForm(EMPTY); setError(''); };

  const handleSave = async e => {
    e.preventDefault();
    setError('');
    const payload = {
      id:       editingId,
      name:     form.name,
      capacity: Number(form.capacity),
      routeId:  form.routeId ? Number(form.routeId) : null,
    };
    try {
      if (editingId) {
        await api.put(`/trains/${editingId}`, payload);
      } else {
        await api.post('/trains', payload);
      }
      cancelEdit();
      load();
    } catch (err) {
      setError(err.response?.data || 'Save failed.');
    }
  };

  const handleDelete = async id => {
    if (!window.confirm('Delete this train?')) return;
    try {
      await api.delete(`/trains/${id}`);
      load();
    } catch {
      setError('Delete failed.');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto py-10 px-4">
        <h2 className="text-2xl font-bold text-gray-800 mb-6">Trains</h2>

        <form onSubmit={handleSave} className="card p-5 mb-8 grid grid-cols-1 sm:grid-cols-4 gap-4">
          <div>
            <label className="label">Name</label>
            <input required className="input" placeholder="IC 123"
              value={form.name} onChange={set('name')} />
          </div>
          <div>
            <label className="label">Capacity</label>
            <input required type="number" min="1" className="input"
              value={form.capacity} onChange={set('capacity')} />
          </div>
          <div>
            <label className="label">Route ID</label>
            <input type="number" className="input" placeholder="optional"
              value={form.routeId} onChange={set('routeId')} />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary flex-1">
              {editingId ? 'Save' : 'Add'}
            </button>
            {editingId && (
              <button type="button" onClick={cancelEdit} className="btn-secondary px-3">✕</button>
            )}
          </div>
          {error && <p className="sm:col-span-4 text-sm text-red-600">{error}</p>}
        </form>

        <div className="card overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr className="text-left text-xs font-semibold text-gray-500 uppercase tracking-wide">
                <th className="px-4 py-3">ID</th>
                <th className="px-4 py-3">Name</th>
                <th className="px-4 py-3">Capacity</th>
                <th className="px-4 py-3">Route</th>
                <th className="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {trains.map(t => (
                <tr key={t.id} className={`hover:bg-gray-50 transition ${editingId === t.id ? 'bg-blue-50' : ''}`}>
                  <td className="px-4 py-3 text-gray-400 tabular-nums">{t.id}</td>
                  <td className="px-4 py-3 font-medium text-gray-800">{t.name}</td>
                  <td className="px-4 py-3 text-gray-600">{t.capacity}</td>
                  <td className="px-4 py-3 text-gray-600">{t.routeId ?? '—'}</td>
                  <td className="px-4 py-3 text-right space-x-3">
                    <button onClick={() => startEdit(t)}
                      className="text-blue-600 hover:underline text-xs font-medium">Edit</button>
                    <button onClick={() => handleDelete(t.id)}
                      className="text-red-500 hover:underline text-xs font-medium">Delete</button>
                  </td>
                </tr>
              ))}
              {trains.length === 0 && (
                <tr>
                  <td colSpan={5} className="px-4 py-10 text-center text-gray-400">
                    No trains yet. Add one above.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
