import * as bootstrap from 'bootstrap';
import './router.js'
import './routes.js'

(() => {
	'use strict';


window.onload = () => {
	'use strict';
	if ('serviceWorker' in navigator) {
		navigator.serviceWorker.register('./sw.js').then(function(registration) {
			console.log('ServiceWorker registration successful with scope:', registration.scope);
		}).catch(function(err) {
			console.log('ServiceWorker registration failed:', err);
		});
	}
};

document.addEventListener('DOMContentLoaded', () => {
	[...document.querySelectorAll('[data-bs-toggle="tooltip"]')].map(el => new bootstrap.Tooltip(el));
	[...document.querySelectorAll('[data-bs-toggle="popover"]')].map(el => new bootstrap.Popover(el));
});
})();