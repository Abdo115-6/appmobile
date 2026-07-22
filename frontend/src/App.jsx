import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { BrowserRouter, useRoutes } from 'react-router-dom'
import { AuthProvider } from './AuthContext'
import { routes } from './routes'

const queryClient = new QueryClient({
  defaultOptions: { queries: { staleTime: 30000, retry: 1 } }
})

function AppRoutes() {
  return useRoutes(routes)
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
    </QueryClientProvider>
  )
}
