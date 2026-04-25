<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建配送订单</h2>
    </div>
    
    <div class="form-container">
      <form @submit.prevent="submitForm">
        <div class="form-row">
          <div class="form-group">
            <label>收货地址 <span class="required">*</span></label>
            <input v-model="form.receiverAddress" type="text" placeholder="请输入收货地址" />
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>区域 <span class="required">*</span></label>
            <select v-model="form.region">
              <option value="">请选择区域</option>
              <option v-for="region in regions" :key="region" :value="region">{{ region }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>重量 (kg)</label>
            <input v-model.number="form.weight" type="number" placeholder="请输入重量" step="0.01" />
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group checkbox-group">
            <label>
              <input v-model="form.urgent" type="checkbox" />
              <span>加急订单</span>
            </label>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn-default" @click="resetForm">重置</button>
          <button type="submit" class="btn-primary">提交订单</button>
        </div>
      </form>
    </div>

    <div v-if="showSuccess" class="success-message">
      <div class="success-icon">&check;</div>
      <h3>订单创建成功</h3>
      <p>订单号: {{ createdOrder?.orderNo }}</p>
      <p>状态: {{ getStatusText(createdOrder?.status) }}</p>
      <p v-if="createdOrder?.courierName">已分配快递员: {{ createdOrder.courierName }}</p>
      <div class="success-actions">
        <button class="btn-primary" @click="createAnother">继续创建</button>
        <router-link to="/orders" class="btn-default">查看订单列表</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { orderApi } from '../api'

export default {
  name: 'OrderCreate',
  data() {
    return {
      regions: ['朝阳区', '东城区', '西城区', '海淀区', '丰台区', '石景山区', '通州区', '大兴区', '亦庄开发区'],
      form: {
        receiverAddress: '',
        region: '',
        weight: 0,
        urgent: false
      },
      showSuccess: false,
      createdOrder: null
    }
  },
  methods: {
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
    resetForm() {
      this.form = {
        receiverAddress: '',
        region: '',
        weight: 0,
        urgent: false
      }
      this.showSuccess = false
      this.createdOrder = null
    },
    createAnother() {
      this.resetForm()
    },
    async submitForm() {
      if (!this.form.receiverAddress || !this.form.region) {
        alert('请填写必填项')
        return
      }
      
      try {
        const orderData = {
          receiverAddress: this.form.receiverAddress,
          region: this.form.region,
          weight: this.form.weight || 0,
          urgent: this.form.urgent
        }
        
        const res = await orderApi.create(orderData)
        if (res.code === 200) {
          this.createdOrder = res.data
          this.showSuccess = true
        } else {
          alert(res.message || '创建订单失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        alert('创建订单失败')
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
  margin-bottom: 1.5rem;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.form-container {
  max-width: 600px;
}

.form-row {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 0;
}

.form-row .form-group {
  flex: 1;
}

.form-group {
  margin-bottom: 1.25rem;
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

.required {
  color: #ef4444;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: normal;
  cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
  width: auto;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: opacity 0.2s;
}

.btn-primary:hover {
  opacity: 0.9;
}

.btn-default {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background 0.2s;
  text-decoration: none;
  display: inline-block;
}

.btn-default:hover {
  background: #e5e5e5;
}

.success-message {
  margin-top: 2rem;
  padding: 2rem;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  text-align: center;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #22c55e;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 1rem;
}

.success-message h3 {
  margin: 0 0 1rem;
  color: #166534;
}

.success-message p {
  margin: 0.5rem 0;
  color: #15803d;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1.5rem;
}
</style>
