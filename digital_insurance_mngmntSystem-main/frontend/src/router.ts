import { createRouter, createWebHistory}  from 'vue-router'
import type { RouteRecordRaw } from 'vue-router';
import Home from './pages/HomePage.vue'
import Login from './pages/auth/LoginPage.vue'
import Register from './pages/auth/RegisterPage.vue'
import PolicyCatalog from './pages/policy/PolicyCatalog.vue'
import SupportForm from './pages/support/SupportForm.vue'
import UserPurchasedPolicy from './pages/policy/UserPurchasedPolicy.vue'
import SubmitClaim from './pages/claims/SubmitClaim.vue';
import ClaimList from './pages/claims/ClaimList.vue';
import AdminClaimsPage from './pages/claims/AdminClaims.vue'; // Renamed to avoid conflict
import PolicyRenewList from './pages/policy/PolicyRenewList.vue';
import RenewPolicy from './pages/policy/RenewPolicyPage.vue'
import TicketList from './pages/support/TicketList.vue'
import AdminTicketList from './pages/support/AdminTicketList.vue'
import UnauthorizedPage from './pages/UnauthorizedPage.vue'
import NotFoundPage from './pages/NotFoundPage.vue' // Assuming you have this
import { authService } from './services/auth';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/policies',
    name: 'PolicyCatalog',
    component: PolicyCatalog,
    meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] }
  },
  {
    path: '/support',
    name: 'Support',
    component: SupportForm,
    meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] }
  },
  {
    path: '/my-policies',
    name: 'MyPolicies',
    component: UserPurchasedPolicy,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/submit-claim',
    name: 'SubmitClaim',
    component: SubmitClaim,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/claims',
    name: 'ClaimList',
    component: ClaimList,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/admin/claims',
    name: 'AdminClaims',
    component: AdminClaimsPage, // Use renamed import
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/policy-renew',
    name: 'PolicyRenewList',
    component: PolicyRenewList,
    meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] }
  },
  {
    path: '/renew-policy', 
    name: 'RenewPolicy',   
    component: RenewPolicy,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/support/user',
    name: 'MyTickets', 
    component: TicketList,
    meta: { requiresAuth: true, roles: ['USER'] }
  },
  {
    path: '/support/admin',
    name: 'AdminTickets', 
    component: AdminTicketList,
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  {
    path: '/unauthorized',
    name: 'Unauthorized',
    component: UnauthorizedPage
  },
  {
    path: '/:catchAll(.*)*',
    name: 'NotFound',
    component: NotFoundPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, _from , next) => {
  const loggedIn = !!authService.getToken();
  const user = authService.getCurrentUser();
  const userRole = user?.role;

  

  if (to.meta.requiresAuth) {
    if (!loggedIn) {
      
      localStorage.setItem('intendedRoute', to.fullPath);
      next({ name: 'Login' });
    } else {
      if (to.meta.roles && Array.isArray(to.meta.roles) && to.meta.roles.length > 0) {
        if (userRole && to.meta.roles.includes(userRole)) {
          next(); 
        } else {
          next({ name: 'Unauthorized' }); 
        }
      } else {
        next(); 
      }
    }
  } else {
    next(); 
  }
});

export default router;