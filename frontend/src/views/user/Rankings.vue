<template>
  <div class="rankings">
    <h2 class="page-title">积分排行榜</h2>

    <el-card shadow="never" class="ranking-card">
      <!-- Top 3 Podium -->
      <div class="podium" v-if="rankings.length >= 3">
        <div class="podium-item second">
          <div class="podium-avatar">
            <el-avatar :size="64" :src="rankings[1]?.avatar">
              <el-icon :size="28"><User /></el-icon>
            </el-avatar>
            <div class="medal silver">2</div>
          </div>
          <div class="podium-name">{{ rankings[1]?.nickname || '匿名' }}</div>
          <div class="podium-points">{{ rankings[1]?.points || 0 }} 积分</div>
        </div>
        <div class="podium-item first">
          <div class="crown">👑</div>
          <div class="podium-avatar">
            <el-avatar :size="80" :src="rankings[0]?.avatar">
              <el-icon :size="36"><User /></el-icon>
            </el-avatar>
            <div class="medal gold">1</div>
          </div>
          <div class="podium-name">{{ rankings[0]?.nickname || '匿名' }}</div>
          <div class="podium-points">{{ rankings[0]?.points || 0 }} 积分</div>
        </div>
        <div class="podium-item third">
          <div class="podium-avatar">
            <el-avatar :size="64" :src="rankings[2]?.avatar">
              <el-icon :size="28"><User /></el-icon>
            </el-avatar>
            <div class="medal bronze">3</div>
          </div>
          <div class="podium-name">{{ rankings[2]?.nickname || '匿名' }}</div>
          <div class="podium-points">{{ rankings[2]?.points || 0 }} 积分</div>
        </div>
      </div>

      <!-- Full Ranking Table -->
      <el-table
        :data="rankings"
        v-loading="loading"
        stripe
        style="width: 100%; margin-top: 24px"
        :row-class-name="tableRowClassName"
      >
        <el-table-column label="排名" width="80" align="center">
          <template #default="{ $index }">
            <span v-if="$index === 0" class="rank-badge rank-gold">🥇</span>
            <span v-else-if="$index === 1" class="rank-badge rank-silver">🥈</span>
            <span v-else-if="$index === 2" class="rank-badge rank-bronze">🥉</span>
            <span v-else class="rank-number">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" :src="row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-nickname">{{ row.nickname || '匿名' }}</span>
              <el-tag
                v-if="userStore.user && row.id === userStore.user.id"
                size="small"
                type="success"
              >
                我
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="积分" width="150" align="center">
          <template #default="{ row }">
            <span class="points-value">{{ row.points || 0 }}</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && rankings.length === 0" description="暂无排行数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const userStore = useUserStore()
const rankings = ref([])
const loading = ref(false)

const tableRowClassName = ({ row }) => {
  if (userStore.user && row.id === userStore.user.id) {
    return 'current-user-row'
  }
  return ''
}

const fetchRankings = async () => {
  loading.value = true
  try {
    const res = await api.points.getRanking()
    rankings.value = res.data || []
  } catch (e) {
    // silent
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRankings()
})
</script>

<style scoped>
.rankings {
  padding-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.ranking-card {
  border-radius: 12px;
}

/* Podium styles */
.podium {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 24px;
  padding: 40px 0 20px;
  background: linear-gradient(180deg, #f0f5ff 0%, #fff 100%);
  border-radius: 12px;
  margin-bottom: 8px;
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.podium-item.first {
  order: 2;
}

.podium-item.second {
  order: 1;
}

.podium-item.third {
  order: 3;
}

.crown {
  font-size: 28px;
  margin-bottom: -4px;
}

.podium-avatar {
  position: relative;
}

.medal {
  position: absolute;
  bottom: -4px;
  right: -4px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
}

.medal.gold {
  background: linear-gradient(135deg, #f6d365, #fda085);
  box-shadow: 0 2px 8px rgba(246, 211, 101, 0.5);
}

.medal.silver {
  background: linear-gradient(135deg, #a8c0ff, #3f2b96);
  box-shadow: 0 2px 8px rgba(168, 192, 255, 0.5);
}

.medal.bronze {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  box-shadow: 0 2px 8px rgba(240, 147, 251, 0.5);
}

.podium-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.podium-points {
  font-size: 14px;
  color: #909399;
}

/* Table styles */
.rank-badge {
  font-size: 22px;
}

.rank-number {
  font-size: 16px;
  font-weight: 600;
  color: #909399;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-nickname {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.points-value {
  font-size: 18px;
  font-weight: 700;
  color: #E6A23C;
}

:deep(.current-user-row) {
  background-color: #ecf5ff !important;
}
</style>
