import { useState } from 'react'
import { api } from '../api.js'
import { ErrorMessage, PageHeader } from '../components/Common.jsx'

const actions = [
  { title: 'BST vs AVL search', description: 'Compare height, comparisons, rotations, and balance.', endpoint: (f) => `/algorithms/tree-comparison/${f.catalogId || 5}`, fields: [['catalogId', 'Catalog ID', '5']] },
  { title: 'BFS knowledge discovery', description: 'Traverse related books level by level.', endpoint: (f) => `/algorithms/bfs/${f.start || 0}`, fields: [['start', 'Start catalog ID', '0']] },
  { title: 'Dijkstra shortest path', description: 'Find the minimum-cost path between two books.', endpoint: (f) => `/algorithms/shortest-path?source=${f.source || 0}&target=${f.target || 11}`, fields: [['source', 'Source', '0'], ['target', 'Target', '11']] },
  { title: 'Kruskal MST', description: 'Build a minimum-cost knowledge network.', endpoint: () => '/algorithms/mst', fields: [] },
  { title: 'Catalog sorting', description: 'Run Merge, Quick, Heap, Counting, or Radix Sort.', endpoint: (f) => `/algorithms/sort?algorithm=${f.algorithm || 'merge'}`, fields: [['algorithm', 'Algorithm', 'merge']] },
  { title: 'B+ Tree year range', description: 'Retrieve publications within a year range.', endpoint: (f) => `/algorithms/publication-range?startYear=${f.startYear || 2019}&endYear=${f.endYear || 2025}`, fields: [['startYear', 'Start year', '2019'], ['endYear', 'End year', '2025']] },
  { title: 'Greedy & DP demonstration', description: 'Knapsack, LIS, coin change, and LCS.', endpoint: () => '/algorithms/optimization-demo', fields: [] },
]

export default function Algorithms() {
  const [forms, setForms] = useState({})
  const [output, setOutput] = useState(null)
  const [active, setActive] = useState('')
  const [error, setError] = useState('')

  const run = async (action) => {
    try { setActive(action.title); setOutput(await api.get(action.endpoint(forms[action.title] || {}))); setError('') }
    catch (e) { setError(e.message) }
  }
  const change = (action, key, value) => setForms({ ...forms, [action.title]: { ...(forms[action.title] || {}), [key]: value } })

  return <>
    <PageHeader title="DSA Laboratory" subtitle="Run the original Java algorithms through REST endpoints backed by PostgreSQL data." />
    <ErrorMessage message={error} />
    <div className="algorithm-layout"><div className="algorithm-grid">{actions.map((action) => <article className="algorithm-card" key={action.title}><span className="algorithm-number">{String(actions.indexOf(action) + 1).padStart(2, '0')}</span><h3>{action.title}</h3><p>{action.description}</p><div className="mini-fields">{action.fields.map(([key, label, initial]) => <label key={key}>{label}<input value={forms[action.title]?.[key] ?? initial} onChange={(e) => change(action, key, e.target.value)} /></label>)}</div><button className="primary-button" onClick={() => run(action)}>Run algorithm</button></article>)}</div>
      <aside className="output-panel"><div className="output-heading"><span>API OUTPUT</span><strong>{active || 'Select an algorithm'}</strong></div><pre>{output ? JSON.stringify(output, null, 2) : '// Results will appear here'}</pre></aside>
    </div>
  </>
}
