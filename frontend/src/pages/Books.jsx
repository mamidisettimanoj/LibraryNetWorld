import { useEffect, useMemo, useState } from 'react'
import { api } from '../api.js'
import { EmptyState, ErrorMessage, Modal, PageHeader, SuccessMessage } from '../components/Common.jsx'

const emptyForm = { catalogId: '', title: '', author: '', category: '', publicationYear: '', isbn: '' }

export default function Books() {
  const [books, setBooks] = useState([])
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [editing, setEditing] = useState(null)
  const [open, setOpen] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const load = async () => {
    try { setBooks(await api.get('/books')); setError('') } catch (e) { setError(e.message) }
  }
  useEffect(() => { load() }, [])

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase()
    if (!q) return books
    return books.filter((book) => [book.title, book.author, book.category, String(book.catalogId)].some((value) => value?.toLowerCase().includes(q)))
  }, [books, query])

  const showCreate = () => { setEditing(null); setForm(emptyForm); setOpen(true); setError('') }
  const showEdit = (book) => {
    setEditing(book)
    setForm({ catalogId: book.catalogId, title: book.title, author: book.author, category: book.category,
      publicationYear: book.publicationYear || '', isbn: book.isbn || '' })
    setOpen(true); setError('')
  }

  const submit = async (event) => {
    event.preventDefault()
    const payload = { ...form, catalogId: Number(form.catalogId), publicationYear: form.publicationYear ? Number(form.publicationYear) : null }
    try {
      if (editing) await api.put(`/books/${editing.id}`, payload)
      else await api.post('/books', payload)
      setSuccess(editing ? 'Book updated successfully.' : 'Book added successfully.')
      setOpen(false); await load()
    } catch (e) { setError(e.message) }
  }

  const remove = async (book) => {
    if (!window.confirm(`Delete “${book.title}”?`)) return
    try { await api.delete(`/books/${book.id}`); setSuccess('Book deleted.'); await load() } catch (e) { setError(e.message) }
  }

  return <>
    <PageHeader title="Book Catalog" subtitle="Manage books stored in PostgreSQL and indexed by the DSA service."
      actions={<button className="primary-button" onClick={showCreate}>+ Add book</button>} />
    <ErrorMessage message={error} /><SuccessMessage message={success} />
    <div className="toolbar"><input className="search-input" value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search by title, author, category, or ID…" /><span>{filtered.length} records</span></div>
    <div className="table-card">
      {filtered.length === 0 ? <EmptyState /> : <table><thead><tr><th>Catalog ID</th><th>Title</th><th>Author</th><th>Category</th><th>Year</th><th>Status</th><th /></tr></thead>
        <tbody>{filtered.map((book) => <tr key={book.id}><td><b>#{book.catalogId}</b></td><td>{book.title}</td><td>{book.author}</td><td><span className="tag">{book.category}</span></td><td>{book.publicationYear || '—'}</td><td><span className={book.available ? 'badge success-badge' : 'badge warning-badge'}>{book.available ? 'Available' : 'Borrowed'}</span></td><td className="row-actions"><button onClick={() => showEdit(book)}>Edit</button><button className="danger-link" onClick={() => remove(book)}>Delete</button></td></tr>)}</tbody></table>}
    </div>

    {open && <Modal title={editing ? 'Edit book' : 'Add a new book'} onClose={() => setOpen(false)}>
      <form className="form-grid" onSubmit={submit}>
        <label>Catalog ID<input type="number" min="0" required value={form.catalogId} onChange={(e) => setForm({ ...form, catalogId: e.target.value })} /></label>
        <label>Publication year<input type="number" min="1000" max="2100" value={form.publicationYear} onChange={(e) => setForm({ ...form, publicationYear: e.target.value })} /></label>
        <label className="full">Title<input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></label>
        <label>Author<input required value={form.author} onChange={(e) => setForm({ ...form, author: e.target.value })} /></label>
        <label>Category<input required value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} /></label>
        <label className="full">ISBN<input value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} /></label>
        <div className="form-actions full"><button type="button" className="secondary-button" onClick={() => setOpen(false)}>Cancel</button><button className="primary-button">{editing ? 'Save changes' : 'Add book'}</button></div>
      </form>
    </Modal>}
  </>
}
