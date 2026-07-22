import { Navigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import Layout from './components/Layout'
import Login from './pages/Login'
import Articles from './pages/Articles'
import ArticleStock from './pages/ArticleStock'
import Devis from './pages/Devis'
import Inventory from './pages/Inventory'

function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return children
}

function PublicRoute({ children }) {
  const { isAuthenticated } = useAuth()
  if (isAuthenticated) return <Navigate to="/" replace />
  return children
}

export const routes = [
  { path: '/login', element: <PublicRoute><Login /></PublicRoute> },
  {
    path: '/',
    element: <ProtectedRoute><Layout /></ProtectedRoute>,
    children: [
      { index: true, element: <Articles /> },
      { path: 'stock/:id/:name?', element: <ArticleStock /> },
      { path: 'devis', element: <Devis /> },
      { path: 'devis/:ref', element: <Devis /> },
      { path: 'inventory', element: <Inventory /> },
    ]
  },
  { path: '*', element: <Navigate to="/" replace /> }
]
