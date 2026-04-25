<template>
  <div class="page-container">
    <div class="page-header">
      <h2>订单列表</h2>
      <div class="header-actions">
        <button class="btn-primary" @click="refreshOrders">刷新</button>
        <router-link to="/order-create" class="btn-primary">创建订单</router-link>
      </div>
    </div>
    
    <div class="filter-bar">
      <label>状态筛选:</label>
      <select v-model="filterStatus" @change="refreshOrders">
        <option value="">全部</option>
        <option value="pending_assign">待分配</option>
        <option value="assigned">已分配</option>
        <option value="accepted">已接单</option>
        <option value="picking_up">取件中</option>
        <option value="delivering">配送中</option>
        <option value="signed">已签收</option>
        <option value="failed">配送失败</option>
      </select>
    </div>
    
    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>订单号</th>
          <th>收货地址</th>
          <th>区域</th>
          <th>重量</th>
          <th>加急</th>
          <th>快递员</th>
          <th>状态</th>
          <th>失败原因</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="order in filteredOrders" :key="order.id">
          <td class="order-no">{{ order.orderNo }}</td>
          <td class="address">{{ order.receiverAddress }}</td>
          <td>{{ order.region }}</td>
          <td>{{ order.weight }} kg</td>
          <td>
            <span v-if="order.urgent" class="urgent-tag">加急</span>
            <span v-else class="normal-tag">普通</span>
          </td>
          <td>{{ order.courierName || '-' }}</td>
          <td>
            <span :class="['status-tag', 'status-' + order.status]">
              {{ getStatusText(order.status) }}
            </span>
          </td>
          <td class="fail-reason">{{ order.failReason || '-' }}</td>
          <td>
            <div class="action-buttons">
              <button v-if="order.status === 'assigned'" class="btn-sm btn-success" @click="acceptOrder(order.id)">接单</button>
              <button v-if="order.status === 'accepted'" class="btn-sm btn-warning" @click="pickUpOrder(order.id)">取件</button>
              <button v-if="order.status === 'picking_up'" class="btn-sm btn-primary" @click="startDelivery(order.id)">开始配送</button>
              <button v-if="order.status === 'delivering'" class="btn-sm btn-success" @click="signOrder(order.id)">签收</button>
              <button v-if="order.status === 'delivering'" class="btn-sm btn-danger" @click="showFailModal(order.id)">失败</button>
              <button v-if="order.status === 'failed' || order.status === 'pending_assign'" class="btn-sm btn-primary" @click="redispatchOrder(order.id)">重新派单</button>
            </div>
          </td>
        </tr>
        <tr v-if="filteredOrders.length === 0">
          <td colspan="9" class="empty-text">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="isShowFailModal" class="modal-overlay" @click.self="closeFailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>配送失败</h3>
          <button class="modal-close" @click="closeFailModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>失败原因 <span class="required">*</span></label>
            <textarea v-model="failReason" placeholder="请输入失败原因" rows="4"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-default" @click="closeFailModal">取消</button>
          <button class="btn-danger" @click="confirmFail">确认失败</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { orderApi } from '../api'

export default {
  name: 'OrderList',
  data() {
    return {
      orders: [],
      filterStatus: '',
      isShowFailModal: false,
      failingOrderId: null,
      failReason: ''
    }
  },
  computed: {
    filteredOrders() {
      if (!this.filterStatus) {
        return this.orders
      }
      return this.orders.filter(order => order.status === this.filterStatus)
    }
  },
  mounted() {
    this.refreshOrders()
  },
  methods: {
    async refreshOrders() {
      try {
        const res = await orderApi.list()
        if (res.code === 200) {
          this.orders = res.data
        }
      } catch (error) {
        console.error('加载订单列表失败:', error)
      }
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
    async acceptOrder(id) {
      if (!confirm('确认接单吗？')) return
      try {
        const res = await orderApi.accept(id)
        if (res.code === 200) {
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('接单失败:', error)
        alert('操作失败')
      }
    },
    async pickUpOrder(id) {
      if (!confirm('确认取件吗？')) return
      try {
        const res = await orderApi.pickUp(id)
        if (res.code === 200) {
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('取件失败:', error)
        alert('操作失败')
      }
    },
    async startDelivery(id) {
      if (!confirm('确认开始配送吗？')) return
      try {
        const res = await orderApi.startDelivery(id)
        if (res.code === 200) {
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('开始配送失败:', error)
        alert('操作失败')
      }
    },
    async signOrder(id) {
      if (!confirm('确认签收吗？')) return
      try {
        const res = await orderApi.sign(id)
        if (res.code === 200) {
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('签收失败:', error)
        alert('操作失败')
      }
    },
    showFailModal(id) {
      this.failingOrderId = id
      this.failReason = ''
      this.isShowFailModal = true
    },
    closeFailModal() {
      this.isShowFailModal = false
      this.failingOrderId = null
      this.failReason = ''
    },
    async confirmFail() {
      if (!this.failReason.trim()) {
        alert('请输入失败原因')
        return
      }
      try {
        const res = await orderApi.fail(this.failingOrderId, this.failReason)
        if (res.code === 200) {
          this.closeFailModal()
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('标记失败:', error)
        alert('操作失败')
      }
    },
    async redispatchOrder(id) {
      if (!confirm('确认重新派单吗？')) return
      try {
        const res = await orderApi.redispatch(id)
        if (res.code === 200) {
          this.refreshOrders()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('重新派单失败:', error)
        alert('操作失败')
      }
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

.header-actions {
  display: flex;
  gap: 0.75rem;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: #f9f9f9;
  border-radius: 6px;
}

.filter-bar select {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
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

.order-no {
  font-family: monospace;
  font-size: 0.85rem;
}

.address {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fail-reason {
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #ef4444;
}

.urgent-tag {
  display: inline-block;
  padding: 0.15rem 0.5rem;
  background: #fee2e2;
  color: #dc2626;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.normal-tag {
  display: inline-block;
  padding: 0.15rem 0.5rem;
  background: #e5e7eb;
  color: #6b7280;
  border-radius: 4px;
  font-size: 0.75rem;
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

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
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
  text-decoration: none;
  display: inline-block;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-sm {
  padding: 0.3rem 0.6rem;
  font-size: 0.75rem;
}

.btn-success {
  background: #22c55e;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-success:hover {
  opacity: 0.9;
}

.btn-warning {
  background: #f59e0b;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-warning:hover {
  opacity: 0.9;
}

.btn-danger {
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-danger:hover {
  opacity: 0.9;
}

.btn-default {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.2s;
}

.btn-default:hover {
  background: #e5e5e5;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 2rem !important;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 450px;
  max-width: 90%;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #eee;
}

.form-group {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  resize: vertical;
  font-family: inherit;
}

.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.required {
  color: #ef4444;
}
</style>
