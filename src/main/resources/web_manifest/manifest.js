const staticCacheName = "portal";

self.addEventListener('fetch', function(event){
    event.respondWith(
        caches.match(event.request).then(function (response) {
            let cacheable = event.request.method.toUpperCase() === 'GET' &&
                (  /^(.*node_modules.*)\.(jpg|jpeg|png|gif|ico|ttf|eot|svg|woff|woff2|css|js)$/.test(event.request.url) )  
            if (response !== undefined){
                return response;
            }else if (cacheable){
                return fetch(event.request).then(function(response){
                    let responseClone = response.clone();
                    caches.open(staticCacheName).then(function (cache) {
                      cache.put(event.request, responseClone);
                    });
                    return response;
                });
            }else {
                return fetch(event.request);
            }
        })
    );
});

self.addEventListener('activate', function(event){
    //event.waitUntil(caches.delete(staticCacheName));
    event.waitUntil(clients.claim());
});



self.addEventListener('install', event => {
  console.log('Attempting to install service worker and cache static assets');
});

var deferredPrompt;
self.addEventListener('beforeinstallprompt', (e) => {
  // Prevent Chrome 67 and earlier from automatically showing the prompt
  e.preventDefault();
  // Stash the event so it can be triggered later.
  deferredPrompt = e;
  // Update UI notify the user they can add to home screen
  btnAdd.style.display = 'block';
});



self.addEventListener('push', function (event) {
  console.log('[Service Worker] Push Received.');
  console.log(`[Service Worker] Push had this data: "${event.data.text()}"`);
  var payload = event.data.json().notification;
  event.waitUntil(self.registration.showNotification(payload.title, payload));
});

self.addEventListener('notificationclick', function (event) {
  console.log('[Service Worker] Notification click Received.');
  var payload = event.notification;
  event.notification.close();
  event.waitUntil(clients.openWindow(payload.data.click_url));
});
