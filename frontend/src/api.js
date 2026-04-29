import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

export const enterpriseApi = {
  getAll: () => api.get('/enterprises'),
  getById: (id) => api.get(`/enterprises/${id}`),
  create: (data) => api.post('/enterprises', data),
  update: (id, data) => api.put(`/enterprises/${id}`, data),
  delete: (id) => api.delete(`/enterprises/${id}`),
  getKeyEnterprises: () => api.get('/enterprises/key-enterprises')
}

export const meterApi = {
  getAll: () => api.get('/meters'),
  getById: (id) => api.get(`/meters/${id}`),
  create: (data) => api.post('/meters', data),
  update: (id, data) => api.put(`/meters/${id}`, data),
  delete: (id) => api.delete(`/meters/${id}`),
  getByEnterprise: (enterpriseId) => api.get(`/meters/enterprise/${enterpriseId}`),
  getActive: () => api.get('/meters/active')
}

export const meterReadingApi = {
  getAll: () => api.get('/meter-readings'),
  submit: (meterId, reading) => api.post('/meter-readings/submit', null, {
    params: { meterId, reading }
  }),
  getByMeter: (meterId) => api.get(`/meter-readings/meter/${meterId}`),
  getByEnterprise: (enterpriseId) => api.get(`/meter-readings/enterprise/${enterpriseId}`),
  getLatest: (meterId) => api.get(`/meter-readings/meter/${meterId}/latest`)
}

export const statisticsApi = {
  getAll: () => api.get('/statistics'),
  getDailyByEnterprise: (enterpriseId) => api.get(`/statistics/enterprise/${enterpriseId}/daily`),
  getMonthlyByEnterprise: (enterpriseId) => api.get(`/statistics/enterprise/${enterpriseId}/monthly`),
  getByMonth: (year, month) => api.get(`/statistics/month/${year}/${month}`)
}

export const alertApi = {
  getAll: () => api.get('/alerts'),
  getById: (id) => api.get(`/alerts/${id}`),
  getActive: () => api.get('/alerts/active'),
  getByEnterprise: (enterpriseId) => api.get(`/alerts/enterprise/${enterpriseId}`),
  resolve: (id, resolvedBy) => api.put(`/alerts/${id}/resolve`, null, {
    params: { resolvedBy }
  }),
  ignore: (id, ignoredBy) => api.put(`/alerts/${id}/ignore`, null, {
    params: { ignoredBy }
  }),
  generateDailyQuotas: () => api.post('/alerts/generate-daily'),
  generateMonthlyQuotas: (year, month) => api.post('/alerts/generate-monthly', null, {
    params: { year, month }
  }),
  generateAllQuotas: () => api.post('/alerts/generate-all')
}

export const billApi = {
  getAll: () => api.get('/bills'),
  getById: (id) => api.get(`/bills/${id}`),
  generate: (enterpriseId, year, month) => api.post(`/bills/generate/${enterpriseId}/${year}/${month}`),
  confirm: (id, confirmedBy) => api.put(`/bills/${id}/confirm`, null, {
    params: { confirmedBy }
  }),
  pay: (id, amount) => api.post(`/bills/${id}/pay`, null, {
    params: { amount }
  }),
  createAdjustment: (id, type, amount, reason, createdBy) => api.post(`/bills/${id}/adjustment`, null, {
    params: { type, amount, reason, createdBy }
  }),
  getByEnterprise: (enterpriseId) => api.get(`/bills/enterprise/${enterpriseId}`),
  getByMonth: (year, month) => api.get(`/bills/month/${year}/${month}`)
}

export const assessmentApi = {
  getAll: () => api.get('/assessments'),
  getById: (id) => api.get(`/assessments/${id}`),
  assess: (enterpriseId, year, month) => api.post(`/assessments/assess/${enterpriseId}/${year}/${month}`),
  assessAll: (year, month) => api.post(`/assessments/assess-all/${year}/${month}`),
  getByEnterprise: (enterpriseId) => api.get(`/assessments/enterprise/${enterpriseId}`),
  getByMonth: (year, month) => api.get(`/assessments/month/${year}/${month}`),
  getQualified: (year, month) => api.get(`/assessments/month/${year}/${month}/qualified`)
}

export const rankingApi = {
  getAll: () => api.get('/rankings'),
  generate: (year, month) => api.post(`/rankings/generate/${year}/${month}`),
  getByMonth: (year, month) => api.get(`/rankings/month/${year}/${month}`),
  getByMonthAndType: (year, month, type) => api.get(`/rankings/month/${year}/${month}/type/${type}`),
  getByEnterprise: (enterpriseId) => api.get(`/rankings/enterprise/${enterpriseId}`)
}

export const inspectionTaskApi = {
  getAll: () => api.get('/inspection-tasks'),
  getById: (id) => api.get(`/inspection-tasks/${id}`),
  create: (params) => api.post('/inspection-tasks', null, { params }),
  assign: (id, assignedTo) => api.put(`/inspection-tasks/${id}/assign`, null, {
    params: { assignedTo }
  }),
  start: (id) => api.put(`/inspection-tasks/${id}/start`),
  complete: (id, result) => api.put(`/inspection-tasks/${id}/complete`, null, {
    params: { result }
  }),
  cancel: (id, reason) => api.put(`/inspection-tasks/${id}/cancel`, null, {
    params: { reason }
  }),
  getPending: () => api.get('/inspection-tasks/pending'),
  getByEnterprise: (enterpriseId) => api.get(`/inspection-tasks/enterprise/${enterpriseId}`)
}

export const resampleTaskApi = {
  getAll: () => api.get('/resample-tasks'),
  getById: (id) => api.get(`/resample-tasks/${id}`),
  create: (params) => api.post('/resample-tasks', null, { params }),
  complete: (id) => api.put(`/resample-tasks/${id}/complete`),
  fail: (id) => api.put(`/resample-tasks/${id}/fail`),
  getPending: () => api.get('/resample-tasks/pending'),
  getByMeter: (meterId) => api.get(`/resample-tasks/meter/${meterId}`)
}

export default api
