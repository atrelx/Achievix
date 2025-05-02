import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import AppLayout from '../components/AppLayout.vue'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import GoalDetails from '../views/GoalDetails.vue'
import TaskForm from '../views/TaskForm.vue'
import Signup from '../views/Signup.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login },
  { path: '/signup', name: 'Signup', component: Signup },
  {
    path: '/',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Home', component: Home },
      { path: 'dashboard', name: 'Dashboard', component: Dashboard },
      { path: 'goals/:id', name: 'GoalDetails', component: GoalDetails },
      { path: 'tasks/new', name: 'TaskCreate', component: TaskForm },
      { path: 'tasks/:id/edit', name: 'TaskEdit', component: TaskForm },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  if (!authStore.isAuthenticated) {
    await authStore.checkAuth()
  }

  if (to.name === 'Login' && authStore.isAuthenticated) {
    next({ name: 'Home' })
  } else if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router
