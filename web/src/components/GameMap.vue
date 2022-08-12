<template>
    <!-- 在这里添加ref,实现和组件绑定 -->
    <!--  parent变量用来获取游戏区域外面的辅助边界div在不同浏览器大小下的的实际大小 -->
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas> <!-- tabindex = "0" 后可以去设置绑定用户的键盘输入事件 -->
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap'
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex';
export default {
    setup() {
        const store = useStore();
        let parent = ref(null);
        let canvas = ref(null);

        //onMounted能够在挂载完组件后执行
        onMounted(() => {
            //初始化一个GameMap对象
            //传入canvas画布和辅助区域div，那么就可以在类中去计算了
            store.commit(
                "updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store));
        });

        return {
            parent,
            canvas,
        }
    }
}
</script>

<style scoped>
div.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>