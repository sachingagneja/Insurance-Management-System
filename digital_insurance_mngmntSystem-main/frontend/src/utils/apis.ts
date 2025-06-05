import axios from 'axios';
import router from '../router';
import { authService } from '../services/auth'; 

const API = axios.create({
  baseURL: 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
  },
});


API.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

API.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      console.error('Authentication error:', error);
      authService.logout(); 
      if (router.currentRoute.value.name !== 'Login') {
        localStorage.setItem('intendedRoute', router.currentRoute.value.fullPath);
      }
      router.push('/login'); 
    }
    return Promise.reject(error);
  }
);

export default API;