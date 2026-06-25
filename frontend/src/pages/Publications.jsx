import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { ErrorMessage, Modal, PageHeader, SuccessMessage } from '../components/Common.jsx'

const empty = { publicationCode: '', title: '', author: '', publicationYear: '', type: 'JOURNAL', resourceUrl: '' }

export default function Publications() {
  const [items, setItems] = useState([])
  const [form, setForm] = useState(empty)
  const [open, setOpen] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const load = () => api.get('/publications').then(setItems).catch((e) => setError(e.message))
  useEffect(() => { load() }, [])
  const submit = async (event) => {
    event.preventDefault()
    try {
      await api.post('/publications', { ...form, publicationCode: Number(form.publicationCode), publicationYear: Number(form.publicationYear) })
      setSuccess('Publication added.'); setOpen(false); setForm(empty); load()
    } catch (e) { setError(e.message) }
  }
  const remove = async (item) => {
    if (!window.confirm(`Delete “${item.title}”?`)) return
    try { await api.delete(`/publications/${item.id}`); load() } catch (e) { setError(e.message) }
  }
  return <>
    <PageHeader title="Publications" subtitle="Journals, conference papers, e-books, and digital resources." actions={<button className="primary-button" onClick={() => setOpen(true)}>+ Add publication</button>} />
    <ErrorMessage message={error} /><SuccessMessage message={success} />
    <div className="publication-grid">{items.map((item) => <article className="publication-card" key={item.id}><span>{item.type}</span><h3>{item.title}</h3><p>{item.author}</p><div><b>{item.publicationYear}</b><small>Code {item.publicationCode}</small></div><footer>{item.resourceUrl ? <a href={item.resourceUrl} target="_blank" rel="noreferrer">Open resource</a> : <span>No URL</span>}<button onClick={() => remove(item)}>Delete</button></footer></article>)}</div>
    {open && <Modal title="Add publication" onClose={() => setOpen(false)}><form className="form-grid" onSubmit={submit}>
      <label>Publication code<input type="number" min="0" required value={form.publicationCode} onChange={(e) => setForm({ ...form, publicationCode: e.target.value })} /></label>
      <label>Year<input type="number" min="1000" max="2100" required value={form.publicationYear} onChange={(e) => setForm({ ...form, publicationYear: e.target.value })} /></label>
      <label className="full">Title<input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></label>
      <label>Author<input required value={form.author} onChange={(e) => setForm({ ...form, author: e.target.value })} /></label>
      <label>Type<select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}><option>JOURNAL</option><option>CONFERENCE</option><option>EBOOK</option><option>PDF</option><option>DATASET</option></select></label>
      <label className="full">Resource URL<input value={form.resourceUrl} onChange={(e) => setForm({ ...form, resourceUrl: e.target.value })} /></label>
      <div className="form-actions full"><button type="button" className="secondary-button" onClick={() => setOpen(false)}>Cancel</button><button className="primary-button">Add publication</button></div>
    </form></Modal>}
  </>
}
