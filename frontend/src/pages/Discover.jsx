import { useEffect, useState } from 'react'
import { api } from '../api.js'
import { ErrorMessage, PageHeader } from '../components/Common.jsx'

export default function Discover() {
  const [books, setBooks] = useState([])
  const [query, setQuery] = useState('AI')
  const [results, setResults] = useState([])
  const [catalogId, setCatalogId] = useState('5')
  const [recommendations, setRecommendations] = useState([])
  const [error, setError] = useState('')

  useEffect(() => { api.get('/books').then(setBooks).catch((e) => setError(e.message)) }, [])

  const search = async (event) => {
    event.preventDefault()
    try { setResults(await api.get(`/books?q=${encodeURIComponent(query)}`)); setError('') } catch (e) { setError(e.message) }
  }
  const recommend = async (event) => {
    event.preventDefault()
    try { setRecommendations(await api.get(`/discovery/recommendations/${catalogId}?limit=6`)); setError('') } catch (e) { setError(e.message) }
  }

  return <>
    <PageHeader title="Search & Discovery" subtitle="Keyword retrieval and explainable recommendations from catalog and graph data." />
    <ErrorMessage message={error} />
    <div className="two-column">
      <article className="panel">
        <h3>Keyword search</h3><p className="muted">Search title, author, or category.</p>
        <form className="stack-form" onSubmit={search}><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Example: AI, databases, Tanenbaum" /><button className="primary-button">Search catalog</button></form>
        <div className="result-list">{results.map((book) => <div className="result-card" key={book.id}><span>#{book.catalogId}</span><div><strong>{book.title}</strong><small>{book.author} · {book.category}</small></div></div>)}</div>
      </article>
      <article className="panel">
        <h3>Smart recommendations</h3><p className="muted">Scores use category, popularity, author, and knowledge links.</p>
        <form className="stack-form" onSubmit={recommend}><select value={catalogId} onChange={(e) => setCatalogId(e.target.value)}>{books.map((book) => <option key={book.id} value={book.catalogId}>#{book.catalogId} — {book.title}</option>)}</select><button className="primary-button">Generate recommendations</button></form>
        <div className="result-list">{recommendations.map((item) => <div className="recommendation-card" key={item.book.id}><span className="score">{item.score}</span><div><strong>{item.book.title}</strong><small>{item.reason}</small></div></div>)}</div>
      </article>
    </div>
  </>
}
