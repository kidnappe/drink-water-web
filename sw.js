/* 喝水记录 — Service Worker
 * 策略：网络优先，离线时回退到缓存
 * 更新：每次部署改 CANARY 值，SW 立即换缓存
 */
var CACHE_NAME = 'drink-water-v1';
var CANARY = '1';  /* 部署新版时改这个数字，旧缓存立即清空 */

self.addEventListener('install', function(e) {
  self.skipWaiting();
  e.waitUntil(
    caches.open(CACHE_NAME).then(function(cache) {
      return cache.addAll([
        './',
        './manifest.json',
        './icons/Icon-192.png',
        './icons/Icon-512.png',
        './icons/Icon-maskable-192.png',
        './icons/Icon-maskable-512.png'
      ]);
    })
  );
});

self.addEventListener('activate', function(e) {
  e.waitUntil(
    Promise.all([
      clients.claim(),
      caches.keys().then(function(keys) {
        return Promise.all(
          keys.filter(function(k) { return k !== CACHE_NAME; }).map(function(k) { return caches.delete(k); })
        );
      })
    ])
  );
});

self.addEventListener('fetch', function(e) {
  e.respondWith(
    fetch(e.request).then(function(resp) {
      /* 网络成功：更新缓存 */
      if (resp && resp.ok && e.request.method === 'GET') {
        var clone = resp.clone();
        caches.open(CACHE_NAME).then(function(cache) { cache.put(e.request, clone); });
      }
      return resp;
    }).catch(function() {
      /* 离线：从缓存取 */
      return caches.match(e.request);
    })
  );
});
