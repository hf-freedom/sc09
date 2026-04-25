<template>
  <div class="page-container">
    <div class="page-header">
      <h2>快递员列表</h2>
      <button class="btn-primary" @click="showAddModal = true">添加快递员</button>
    </div>
    
    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th>ID</th>
          <th>姓名</th>
          <th>所属区域</th>
          <th>当前任务数</th>
          <th>最大任务数</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="courier in couriers" :key="courier.id">
          <td>{{ courier.id }}</td>
          <td>{{ courier.name }}</td>
          <td>{{ courier.region }}</td>
          <td>
            <span :class="courier.currentTaskCount >= courier.maxTaskCount ? 'task-count-highlight' : ''">
              {{ courier.currentTaskCount }}
            </span>
          </td>
          <td>{{ courier.maxTaskCount }}</td>
          <td>
            <span :class="['status-tag', 'status-' + courier.status]">
              {{ getStatusText(courier.status) }}
            </span>
          </td>
          <td>
            <button class="btn-sm btn-primary" @click="editCourier(courier)">编辑</button>
            <button class="btn-sm btn-danger" @click="deleteCourier(courier.id)">删除</button>
          </td>
        </tr>
        <tr v-if="couriers.length === 0">
          <td colspan="7" class="empty-text">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showAddModal ? '添加快递员' : '编辑快递员' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>姓名</label>
            <input v-model="form.name" type="text" placeholder="请输入姓名" />
          </div>
          <div class="form-group">
            <label>所属区域</label>
            <input v-model="form.region" type="text" placeholder="请输入区域" />
          </div>
          <div class="form-group">
            <label>最大任务数</label>
            <input v-model.number="form.maxTaskCount" type="number" placeholder="请输入最大任务数" />
          </div>
          <div class="form-group" v-if="showEditModal">
            <label>状态</label>
            <select v-model="form.status">
              <option value="idle">空闲</option>
              <option value="busy">忙碌</option>
              <option value="offline">离线</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-default" @click="closeModal">取消</button>
          <button class="btn-primary" @click="submitForm">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { courierApi } from '../api'

export default {
  name: 'CourierList',
  data() {
    return {
      couriers: [],
      showAddModal: false,
      showEditModal: false,
      editingId: null,
      form: {
        name: '',
        region: '',
        maxTaskCount: 5,
        status: 'idle'
      }
    }
  },
  mounted() {
    this.loadCouriers()
  },
  methods: {
    async loadCouriers() {
      try {
        const res = await courierApi.list()
        if (res.code === 200) {
          this.couriers = res.data
        }
      } catch (error) {
        console.error('加载快递员列表失败:', error)
      }
    },
    getStatusText(status) {
      const map = {
        idle: '空闲',
        busy: '忙碌',
        offline: '离线'
      }
      return map[status] || status
    },
    editCourier(courier) {
      this.editingId = courier.id
      this.form = {
        name: courier.name,
        region: courier.region,
        maxTaskCount: courier.maxTaskCount,
        status: courier.status
      }
      this.showEditModal = true
    },
    async deleteCourier(id) {
      if (!confirm('确定要删除该快递员吗？')) return
      try {
        const res = await courierApi.delete(id)
        if (res.code === 200) {
          this.loadCouriers()
        } else {
          alert(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除失败:', error)
        alert('删除失败')
      }
    },
    closeModal() {
      this.showAddModal = false
      this.showEditModal = false
      this.editingId = null
      this.resetForm()
    },
    resetForm() {
      this.form = {
        name: '',
        region: '',
        maxTaskCount: 5,
        status: 'idle'
      }
    },
    async submitForm() {
      if (!this.form.name || !this.form.region) {
        alert('请填写完整信息')
        return
      }
      try {
        let res
        if (this.showAddModal) {
          res = await courierApi.create(this.form)
        } else {
          res = await courierApi.update(this.editingId, this.form)
        }
        if (res.code === 200) {
          this.closeModal()
          this.loadCouriers()
        } else {
          alert(res.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
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
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: opacity 0.2s;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-sm {
  padding: 0.3rem 0.8rem;
  font-size: 0.8rem;
  margin-right: 0.5rem;
}

.btn-danger {
  background: #ef4444;
  color: white;
  border: none;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: opacity 0.2s;
}

.btn-danger:hover {
  opacity: 0.9;
}

.btn-default {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.2s;
}

.btn-default:hover {
  background: #e5e5e5;
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 0.75rem 1rem;
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

.status-tag {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
}

.status-idle {
  background: #d1fae5;
  color: #065f46;
}

.status-busy {
  background: #fef3c7;
  color: #92400e;
}

.status-offline {
  background: #fee2e2;
  color: #991b1b;
}

.task-count-highlight {
  color: #ef4444;
  font-weight: 600;
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
  width: 500px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
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
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}
</style>
