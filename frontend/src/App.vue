<template>
  <div class="app-container">
    <header class="header">
      <h1>物流派单系统</h1>
    </header>
    <nav class="nav">
      <router-link to="/couriers" class="nav-item">快递员列表</router-link>
      <router-link to="/order-create" class="nav-item">创建订单</router-link>
      <router-link to="/orders" class="nav-item">订单列表</router-link>
      <router-link to="/logs" class="nav-item">派单日志</router-link>
    </nav>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.app-container {
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
  margin: 0;
  font-size: 1.5rem;
}

.nav {
  background: white;
  padding: 0 2rem;
  display: flex;
  gap: 0.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.nav-item {
  padding: 1rem 1.5rem;
  text-decoration: none;
  color: #666;
  font-weight: 500;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #667eea;
}

.nav-item.router-link-active {
  color: #667eea;
  border-bottom-color: #667eea;
}

.main {
  padding: 2rem;
}
</style>
