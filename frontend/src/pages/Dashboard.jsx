import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { ErrorMessage, Loading, PageHeader } from '../components/Common.jsx'

const cards = [
  ['bookCount', 'Books', '▤'],
  ['availableBooks', 'Available', '✓'],
  ['memberCount', 'Members', '♙'],
  ['activeLoans', 'Active loans', '⇄'],
  ['overdueLoans', 'Overdue', '!'],
  ['publicationCount', 'Publications', '▥'],
]

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/analytics/dashboard').then(setData).catch((e) => setError(e.message))
  }, [])

  if (!data && !error) return <Loading />

  const maxMonth = Math.max(1, ...(data?.monthlyLoans || []).map((item) => Number(item.count)))
  const maxCategory = Math.max(1, ...(data?.categoryDistribution || []).map((item) => Number(item.count)))

  return <>
    <PageHeader title="Dashboard" subtitle="A live overview of catalog, circulation, and academic resources." />
    <ErrorMessage message={error} />
    {data && <>
      <div className="stat-grid">
        {cards.map(([key, label, icon]) => <article className="stat-card" key={key}>
          <span className="stat-icon">{icon}</span>
          <div><strong>{data[key] ?? 0}</strong><span>{label}</span></div>
        </article>)}
      </div>

      <div className="two-column">
        <article className="panel">
          <div className="panel-heading"><div><h3>Borrowing activity</h3><p>Last six months</p></div></div>
          <div className="bar-chart">
            {(data.monthlyLoans || []).map((item) => <div className="bar-column" key={item.month}>
              <span className="bar-value">{item.count}</span>
              <div className="bar" style={{ height: `${Math.max(8, (Number(item.count) / maxMonth) * 140)}px` }} />
              <small>{item.month}</small>
            </div>)}
          </div>
        </article>

        <article className="panel">
          <div className="panel-heading"><div><h3>Catalog categories</h3><p>Books by subject</p></div></div>
          <div className="progress-list">
            {(data.categoryDistribution || []).map((item) => <div className="progress-row" key={item.category}>
              <div><span>{item.category}</span><strong>{item.count}</strong></div>
              <div className="progress-track"><i style={{ width: `${(Number(item.count) / maxCategory) * 100}%` }} /></div>
            </div>)}
          </div>
        </article>
      </div>

      <article className="panel">
        <div className="panel-heading"><div><h3>Most borrowed books</h3><p>Popularity is updated by real transactions.</p></div></div>
        <div className="popular-list">
          {(data.popularBooks || []).map((book, index) => <div key={book.catalogId} className="popular-item">
            <span className="rank">{index + 1}</span>
            <div><strong>{book.title}</strong><small>Catalog ID {book.catalogId}</small></div>
            <b>{book.borrowCount} borrows</b>
          </div>)}
        </div>
      </article>
    </>}
  </>
}
