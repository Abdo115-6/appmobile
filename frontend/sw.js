const CACHE = 'mallzellij-v1'
const ASSETS = ['/', '/index.html']

self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE).then((c) => c.addAll(ASSETS))
  )
  self.skipWaiting()
})

self.addEventListener('activate', () => self.clients.claim())

self.addEventListener('fetch', (e) => {
  if (e.request.mode === 'navigate') {
    e.respondWith(caches.match('/').then((r) => r || fetch(e.request)))
    return
  }
  e.respondWith(
    caches.match(e.request).then((r) => r || fetch(e.request))
  )
})
