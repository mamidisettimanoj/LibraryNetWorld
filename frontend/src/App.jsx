import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Books from './pages/Books.jsx'
import Members from './pages/Members.jsx'
import Loans from './pages/Loans.jsx'
import Publications from './pages/Publications.jsx'
import Discover from './pages/Discover.jsx'
import Algorithms from './pages/Algorithms.jsx'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/books" element={<Books />} />
        <Route path="/members" element={<Members />} />
        <Route path="/loans" element={<Loans />} />
        <Route path="/publications" element={<Publications />} />
        <Route path="/discover" element={<Discover />} />
        <Route path="/algorithms" element={<Algorithms />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  )
}
