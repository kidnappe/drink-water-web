self.addEventListener('install', function() { self.skipWaiting(); });
self.addEventListener('activate', function(e) { e.waitUntil(clients.claim().then(function() { return caches.keys().then(function(names) { return Promise.all(names.map(function(n) { return caches.delete(n); })); }); })); });
