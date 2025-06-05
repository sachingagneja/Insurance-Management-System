import { createApp } from 'vue'
import './style.css'
import router from './router'
// import 'bootstrap/dist/css/bootstrap.min.css';
// import 'bootstrap/dist/js/bootstrap.bundle.min.js';

import App from "./App.vue";

createApp(App).use(router).mount('#app')
