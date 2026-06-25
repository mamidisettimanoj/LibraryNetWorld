import { useEffect, useMemo, useState } from 'react'
import { api } from '../api.js'
import { ErrorMessage, PageHeader, SuccessMessage } from '../components/Common.jsx'

export default function Loans() {
  const [loans, setLoans] = useState([])
  const [books, setBooks] = useState([])
  const [members, setMembers] = useState([])
  const [memberId, setMemberId] = useState('')
  const [bookId, setBookId] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const load = async () => {
    try {
      const [loanData, bookData, memberData] = await Promise.all([api.get('/loans'), api.get('/books'), api.get('/members')])
      setLoans(loanData); setBooks(bookData); setMembers(memberData); setError('')
    } catch (e) { setError(e.message) }
  }
  useEffect(() => { load() }, [])
  const availableBooks = useMemo(() => books.filter((book) => book.available), [books])

  const borrow = async (event) => {
    event.preventDefault()
    try {
      await api.post('/loans/borrow', { memberId: Number(memberId), bookId: Number(bookId) })
      setSuccess('Book borrowed successfully. Due date is 14 days from today.'); setMemberId(''); setBookId(''); load()
    } catch (e) { setError(e.message) }
  }
  const returnBook = async (id) => {
    try { await api.put(`/loans/${id}/return`); setSuccess('Book returned successfully.'); load() } catch (e) { setError(e.message) }
  }

  return <>
    <PageHeader title="Borrowing" subtitle="Issue and return books using transactional Spring services." />
    <ErrorMessage message={error} /><SuccessMessage message={success} />
    <article className="panel compact-panel"><h3>Issue a book</h3><form className="inline-form" onSubmit={borrow}>
      <select required value={memberId} onChange={(e) => setMemberId(e.target.value)}><option value="">Select member</option>{members.filter((m) => m.active).map((m) => <option key={m.id} value={m.id}>{m.memberCode} — {m.name}</option>)}</select>
      <select required value={bookId} onChange={(e) => setBookId(e.target.value)}><option value="">Select available book</option>{availableBooks.map((b) => <option key={b.id} value={b.id}>#{b.catalogId} — {b.title}</option>)}</select>
      <button className="primary-button">Borrow book</button>
    </form></article>
    <div className="table-card"><table><thead><tr><th>ID</th><th>Member</th><th>Book</th><th>Borrowed</th><th>Due</th><th>Status</th><th /></tr></thead><tbody>
      {loans.map((loan) => <tr key={loan.id}><td>#{loan.id}</td><td>{loan.memberName}<small className="cell-note">{loan.memberCode}</small></td><td>{loan.bookTitle}<small className="cell-note">Catalog #{loan.catalogId}</small></td><td>{loan.borrowedAt}</td><td>{loan.dueAt}</td><td><span className={loan.status === 'RETURNED' ? 'badge neutral-badge' : loan.overdue ? 'badge danger-badge' : 'badge warning-badge'}>{loan.status === 'RETURNED' ? 'Returned' : loan.overdue ? 'Overdue' : 'Borrowed'}</span></td><td>{loan.status === 'BORROWED' && <button className="secondary-button small" onClick={() => returnBook(loan.id)}>Return</button>}</td></tr>)}
    </tbody></table></div>
  </>
}
