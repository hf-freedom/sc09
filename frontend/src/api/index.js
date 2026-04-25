import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    return Promise.reject(error)
  }
)

export const courierApi = {
  list: () => api.get('/couriers'),
  get: (id) => api.get(`/couriers/${id}`),
  create: (data) => api.post('/couriers', data),
  update: (id, data) => api.put(`/couriers/${id}`, data),
  delete: (id) => api.delete(`/couriers/${id}`)
}

export const orderApi = {
  list: () => api.get('/orders'),
  get: (id) => api.get(`/orders/${id}`),
  create: (data) => api.post('/orders', data),
  accept: (id) => api.post(`/orders/${id}/accept`),
  pickUp: (id) => api.post(`/orders/${id}/pick-up`),
  startDelivery: (id) => api.post(`/orders/${id}/start-delivery`),
  sign: (id) => api.post(`/orders/${id}/sign`),
  fail: (id, reason) => api.post(`/orders/${id}/fail`, { reason }),
  redispatch: (id) => api.post(`/orders/${id}/redispatch`)
}

export const branchApi = {
  list: () => api.get('/branches'),
  get: (id) => api.get(`/branches/${id}`),
  create: (data) => api.post('/branches', data),
  update: (id, data) => api.put(`/branches/${id}`, data),
  delete: (id) => api.delete(`/branches/${id}`)
}

export const logApi = {
  dispatch: () => api.get('/logs/dispatch'),
  status: () => api.get('/logs/status'),
  all: () => api.get('/logs')
}

export default api
