<!--游戏范围确定组件，用于辅助动态计算出正方形的真正游戏区域-->
<template>
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
import GameMap from "./GameMap.vue"
export default {
    components: {
        GameMap,
    }
}
</script>

<style scoped>
div.playground {
    width: 60vw;
    height: 70vh;
    margin: 30px auto;
}
</style>