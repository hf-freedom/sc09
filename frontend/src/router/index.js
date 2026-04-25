import { createRouter, createWebHistory } from 'vue-router'
import CourierList from '../views/CourierList.vue'
import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import DispatchLog from '../views/DispatchLog.vue'

const routes = [
  {
    path: '/',
    redirect: '/couriers'
  },
  {
    path: '/couriers',
    name: 'CourierList',
    component: CourierList
  },
  {
    path: '/order-create',
    name: 'OrderCreate',
    component: OrderCreate
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: OrderList
  },
  {
    path: '/logs',
    name: 'DispatchLog',
    component: DispatchLog
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
