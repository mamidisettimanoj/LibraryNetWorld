import { NavLink } from 'react-router-dom'

const items = [
  ['/', '▦', 'Dashboard'],
  ['/books', '▤', 'Books'],
  ['/members', '♙', 'Members'],
  ['/loans', '⇄', 'Borrowing'],
  ['/publications', '▥', 'Publications'],
  ['/discover', '⌕', 'Discover'],
  ['/algorithms', '◇', 'DSA Lab'],
]

export default function Layout({ children }) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">LN</span>
          <div><strong>LibraryNet</strong><small>Knowledge Retrieval</small></div>
        </div>
        <nav>
          {items.map(([path, icon, label]) => (
            <NavLink key={path} to={path} end={path === '/'} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">{icon}</span>{label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-note">
          <strong>Full-stack edition</strong>
          <span>React + Spring Boot + PostgreSQL</span>
        </div>
      </aside>
      <main className="main-area">
        <header className="topbar">
          <div>
            <p className="eyebrow">INTELLIGENT DIGITAL LIBRARY</p>
            <h1>LibraryNet</h1>
          </div>
          <span className="status-pill"><i /> Backend API</span>
        </header>
        <section className="page-content">{children}</section>
      </main>
    </div>
  )
}
