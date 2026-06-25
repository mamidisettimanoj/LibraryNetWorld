export function PageHeader({ title, subtitle, actions }) {
  return <div className="page-header"><div><h2>{title}</h2><p>{subtitle}</p></div><div className="page-actions">{actions}</div></div>
}

export function Loading() {
  return <div className="state-card">Loading LibraryNet data…</div>
}

export function ErrorMessage({ message }) {
  return message ? <div className="alert error">{message}</div> : null
}

export function SuccessMessage({ message }) {
  return message ? <div className="alert success">{message}</div> : null
}

export function Modal({ title, children, onClose }) {
  return <div className="modal-backdrop" onMouseDown={onClose}>
    <div className="modal" onMouseDown={(event) => event.stopPropagation()}>
      <div className="modal-header"><h3>{title}</h3><button className="icon-button" onClick={onClose}>×</button></div>
      {children}
    </div>
  </div>
}

export function EmptyState({ children = 'No records found.' }) {
  return <div className="empty-state">{children}</div>
}
