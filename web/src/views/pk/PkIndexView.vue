<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !== 'none'" />

    <div class="user-color-lb" v-if="$store.state.pk.status === 'playing' &&
    parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">
        己方颜色：蓝色
    </div>

    <div class="user-color-ro" v-if="$store.state.pk.status === 'playing' &&
    parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">
        己方颜色：红色
    </div>

    <!-- <div class=" user-color-lb" v-if="$store.state.pk.status === 'playing' &&
    parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">左下角</div>
    <div class="user-color-ro" v-if="$store.state.pk.status === 'playing' &&
    parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">右上角</div> -->

</template>

<script>
import PlayGround from "@/components/PlayGround.vue";
import MatchGround from "@/components/MatchGround.vue";
import ResultBoard from "@/components/ResultBoard.vue"
import { onMounted, onUnmounted } from "vue";
import { useStore } from 'vuex'

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },

    setup() {
        const store = useStore();
        const socketUrl = `wss://kob.yuanheci.top/websocket/${store.state.user.token}/`;

        let socket = null;

        store.commit("updateLoser", "none");
        store.commit("updateIsRecord", false);   //改回不是录像

        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("connected!");
                store.commit("updateSocket", socket)  //s
            }
            socket.onmessage = msg => {
                //spring中的JSON传输中，数据放在data中
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching") {  //匹配成功
                    store.commit("updateOpponent", {
                        username: data.opponent_usernme,
                        photo: data.opponent_photo,
                    })
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 200);
                    store.commit("updateGame", data.game);
                } else if (data.event === "move") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;
                    snake0.set_direction(data.a_direction);
                    snake1.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    console.log(data);
                    const game = store.state.pk.gameObject;
                    const [snake0, snake1] = game.snakes;

                    if (data.loser === "all" || data.loser === "A") {
                        snake0.status = "die";
                    }
                    if (data.loser === "all" || data.loser === "B") {
                        snake1.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            }

            socket.onclose = () => {    //表示链接已经关闭时触发
                console.log("disconnected!");
                store.commit("updateStatus", "matching");
            }
        })

        onUnmounted(() => {
            socket.close();      //手动关闭链接
        })
    }
};
</script>

<style scoped>
div.user-color-lb {
    height: 60px;
    width: 530px;
    background-color: #4876EC;

    text-align: center;
    font-size: 30px;
    font-weight: bold;
    color: white;
    line-height: 60px;

    margin: 0 auto;
}

div.user-color-ro {
    height: 60px;
    width: 530px;
    background-color: #F94848;

    text-align: center;
    color: white;
    font-size: 30px;
    font-weight: bold;
    line-height: 60px;

    margin: 0 auto;
}
</style>