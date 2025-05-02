import { mount } from '@vue/test-utils'
import {describe, it, expect, beforeEach, vi} from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../../../../src/views/Login.vue'
import { useAuthStore } from '@/stores/auth.ts'

describe('Login.vue', () => {
  let wrapper: any
  let authStore: any
  let router: any

  beforeEach(() => {
    setActivePinia(createPinia())
    authStore = useAuthStore()
    router = createRouter({
      history: createWebHistory(),
      routes: [{ path: '/dashboard', name: 'Dashboard', component: { template: '<div></div>' } }],
    })
    wrapper = mount(Login, {
      global: {
        plugins: [router],
      },
    })
  })

  it('renders login form', () => {
    expect(wrapper.find('form').exists()).toBe(true)
    expect(wrapper.find('input[type="email"]').exists()).toBe(true)
    expect(wrapper.find('input[type="password"]').exists()).toBe(true)
    expect(wrapper.find('button[type="submit"]').text()).toContain('Login')
  })

  it('displays errors on invalid submission', async () => {
    authStore.login = vi.fn().mockRejectedValue(new Error('Invalid credentials'))
    await wrapper.find('form').trigger('submit.prevent')
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.text-error').exists()).toBe(true)
  })

  it('navigates to dashboard on successful login', async () => {
    authStore.login = vi.fn().mockResolvedValue(undefined)
    router.push = vi.fn()
    wrapper.find('input[type="email"]').setValue('test@example.com')
    wrapper.find('input[type="password"]').setValue('password')
    await wrapper.find('form').trigger('submit.prevent')
    await wrapper.vm.$nextTick()
    expect(router.push).toHaveBeenCalledWith('/dashboard')
  })
})
