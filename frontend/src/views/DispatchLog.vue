<template>
  <div class="page-container">
    <div class="page-header">
      <h2>派单日志</h2>
      <button class="btn-primary" @click="refreshLogs">刷新</button>
    </div>
    
    <div class="tabs">
      <button :class="['tab-btn', { active: activeTab === 'dispatch' }]" @click="activeTab = 'dispatch'">派单日志</button>
      <button :class="['tab-btn', { active: activeTab === 'status' }]" @click="activeTab = 'status'">状态流转日志</button>
    </div>
    
    <div v-if="activeTab === 'dispatch'" class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>时间</th>
          <th>订单号</th>
          <th>快递员</th>
          <th>操作类型</th>
          <th>说明</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="log in dispatchLogs" :key="log.id">
          <td class="time">{{ formatTime(log.createdAt) }}</td>
          <td class="order-no">{{ log.orderNo }}</td>
          <td>{{ log.courierName || '-' }}</td>
          <td>
            <span :class="['action-tag', getActionTagClass(log.action)]">
              {{ log.action }}
            </span>
          </td>
          <td class="reason">{{ log.reason }}</td>
        </tr>
        <tr v-if="dispatchLogs.length === 0">
          <td colspan="5" class="empty-text">暂无日志</td>
        </tr>
        </tbody>
      </table>
    </div>
    
    <div v-if="activeTab === 'status'" class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>时间</th>
          <th>订单号</th>
          <th>从状态</th>
          <th>到状态</th>
          <th>操作人</th>
          <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="log in statusLogs" :key="log.id">
          <td class="time">{{ formatTime(log.createdAt) }}</td>
          <td class="order-no">{{ log.orderNo }}</td>
          <td>
            <span :class="['status-tag', 'status-' + log.fromStatus]" v-if="log.fromStatus">
              {{ getStatusText(log.fromStatus) }}
            </span>
            <span v-else>-</span>
          </td>
          <td>
            <span :class="['status-tag', 'status-' + log.toStatus]">
              {{ getStatusText(log.toStatus) }}
            </span>
          </td>
          <td>{{ log.operator }}</td>
          <td class="remark">{{ log.remark }}</td>
        </tr>
        <tr v-if="statusLogs.length === 0">
          <td colspan="6" class="empty-text">暂无日志</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import { logApi } from '../api'

export default {
  name: 'DispatchLog',
  data() {
    return {
      activeTab: 'dispatch',
      dispatchLogs: [],
      statusLogs: []
    }
  },
  mounted() {
    this.refreshLogs()
  },
  methods: {
    async refreshLogs() {
      try {
        const res = await logApi.all()
        if (res.code === 200) {
          this.dispatchLogs = res.data.dispatchLogs || []
          this.statusLogs = res.data.statusLogs || []
        }
      } catch (error) {
        console.error('加载日志失败:', error)
      }
    },
    formatTime(time) {
      if (!time) return '-'
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    getStatusText(status) {
      const map = {
        pending_assign: '待分配',
        assigned: '已分配',
        accepted: '已接单',
        picking_up: '取件中',
        delivering: '配送中',
        signed: '已签收',
        failed: '配送失败'
      }
      return map[status] || status
    },
    getActionTagClass(action) {
      if (action === '签收') return 'action-success'
      if (action === '失败') return 'action-failed'
      if (action === '待分配') return 'action-warning'
      if (action === '派单' || action === '接单' || action === '重新派单') return 'action-info'
      return ''
    }
  }
}
</script>

<style scoped>
.page-container {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  border-bottom: 2px solid #eee;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  background: none;
  border: none;
  font-size: 0.95rem;
  cursor: pointer;
  color: #666;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: #667eea;
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: opacity 0.2s;
}

.btn-primary:hover {
  opacity: 0.9;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.data-table th,
.data-table td {
  padding: 0.75rem 0.5rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background: #f9f9f9;
  font-weight: 600;
  color: #333;
}

.data-table tr:hover {
  background: #f9f9f9;
}

.time {
  font-family: monospace;
  font-size: 0.8rem;
  white-space: nowrap;
}

.order-no {
  font-family: monospace;
  font-size: 0.85rem;
}

.reason,
.remark {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-tag {
  display: inline-block;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.action-success {
  background: #d1fae5;
  color: #065f46;
}

.action-failed {
  background: #fee2e2;
  color: #dc2626;
}

.action-warning {
  background: #fef3c7;
  color: #92400e;
}

.action-info {
  background: #dbeafe;
  color: #1e40af;
}

.status-tag {
  display: inline-block;
  padding: 0.25rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-pending_assign {
  background: #fef3c7;
  color: #92400e;
}

.status-assigned {
  background: #dbeafe;
  color: #1e40af;
}

.status-accepted {
  background: #d1fae5;
  color: #065f46;
}

.status-picking_up {
  background: #ede9fe;
  color: #5b21b6;
}

.status-delivering {
  background: #bae6fd;
  color: #0369a1;
}

.status-signed {
  background: #bbf7d0;
  color: #166534;
}

.status-failed {
  background: #fee2e2;
  color: #dc2626;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 2rem !important;
}
</style>
