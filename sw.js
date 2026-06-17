self.addEventListener('install', function() { self.skipWaiting(); });
self.addEventListener('activate', function(e) {
  e.waitUntil(
    Promise.all([
      clients.claim(),
      caches.keys().then(function(keys) {
        return Promise.all(keys.map(function(k) { return caches.delete(k); }));
      })
    ])
  );
});
self.addEventListener('fetch', function(e) {
  /* 页面请求：如果网络失败，不拦截，让浏览器自行处理 */
  if (e.request.mode === 'navigate') return;
  e.respondWith(
    fetch(e.request).catch(function() { return new Response('', { status: 200 }); })
  );
});
