import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { EmptyState, ErrorMessage, Modal, PageHeader, SuccessMessage } from '../components/Common.jsx'

const empty = { memberCode: '', name: '', email: '', department: '', maxBorrowLimit: 3, active: true }

export default function Members() {
  const [members, setMembers] = useState([])
  const [form, setForm] = useState(empty)
  const [editing, setEditing] = useState(null)
  const [open, setOpen] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const load = () => api.get('/members').then(setMembers).catch((e) => setError(e.message))
  useEffect(() => { load() }, [])

  const show = (member = null) => {
    setEditing(member)
    setForm(member ? { memberCode: member.memberCode, name: member.name, email: member.email, department: member.department, maxBorrowLimit: member.maxBorrowLimit, active: member.active } : empty)
    setOpen(true); setError('')
  }
  const submit = async (event) => {
    event.preventDefault()
    try {
      if (editing) await api.put(`/members/${editing.id}`, { ...form, maxBorrowLimit: Number(form.maxBorrowLimit) })
      else await api.post('/members', { ...form, maxBorrowLimit: Number(form.maxBorrowLimit) })
      setSuccess(editing ? 'Member updated.' : 'Member registered.'); setOpen(false); load()
    } catch (e) { setError(e.message) }
  }
  const remove = async (member) => {
    if (!window.confirm(`Delete ${member.name}?`)) return
    try { await api.delete(`/members/${member.id}`); setSuccess('Member deleted.'); load() } catch (e) { setError(e.message) }
  }

  return <>
    <PageHeader title="Members" subtitle="Register library members and control borrowing limits." actions={<button className="primary-button" onClick={() => show()}>+ Register member</button>} />
    <ErrorMessage message={error} /><SuccessMessage message={success} />
    <div className="table-card">{members.length === 0 ? <EmptyState /> : <table><thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Department</th><th>Limit</th><th>Status</th><th /></tr></thead><tbody>
      {members.map((member) => <tr key={member.id}><td><b>{member.memberCode}</b></td><td>{member.name}</td><td>{member.email}</td><td>{member.department}</td><td>{member.maxBorrowLimit}</td><td><span className={member.active ? 'badge success-badge' : 'badge warning-badge'}>{member.active ? 'Active' : 'Inactive'}</span></td><td className="row-actions"><button onClick={() => show(member)}>Edit</button><button className="danger-link" onClick={() => remove(member)}>Delete</button></td></tr>)}
    </tbody></table>}</div>
    {open && <Modal title={editing ? 'Edit member' : 'Register member'} onClose={() => setOpen(false)}><form className="form-grid" onSubmit={submit}>
      <label>Member code<input required value={form.memberCode} onChange={(e) => setForm({ ...form, memberCode: e.target.value })} /></label>
      <label>Borrow limit<input type="number" min="1" max="10" required value={form.maxBorrowLimit} onChange={(e) => setForm({ ...form, maxBorrowLimit: e.target.value })} /></label>
      <label className="full">Name<input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></label>
      <label>Email<input type="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label>Department<input required value={form.department} onChange={(e) => setForm({ ...form, department: e.target.value })} /></label>
      <label className="checkbox full"><input type="checkbox" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} /> Active account</label>
      <div className="form-actions full"><button type="button" className="secondary-button" onClick={() => setOpen(false)}>Cancel</button><button className="primary-button">Save member</button></div>
    </form></Modal>}
  </>
}
