import { mount } from '@vue/test-utils'
import { describe, it, expect, beforeEach } from 'vitest'
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../../../../src/views/Home.vue'

describe('Home.vue', () => {
  let wrapper: any
  let router: any

  beforeEach(() => {
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/login', name: 'Login', component: { template: '<div></div>' } },
        { path: '/signup', name: 'Signup', component: { template: '<div></div>' } },
      ],
    })
    wrapper = mount(Home, {
      global: {
        plugins: [router],
      },
    })
  })

  it('renders home page content', () => {
    expect(wrapper.find('h1').text()).toBe('Welcome to Achievix')
    expect(wrapper.find('p').exists()).toBe(true)
    expect(wrapper.find('img[alt="Achievix Logo"]').exists()).toBe(true)
  })

  it('has login and signup buttons', () => {
    const links = wrapper.findAll('a')
    expect(links.length).toBe(2)
    expect(links[0].text()).toBe('Login')
    expect(links[1].text()).toBe('Sign Up')
  })
})
