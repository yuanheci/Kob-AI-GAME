<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%" />
                        <!-- <button style="margin-top: 10px" type="button" class="btn btn-secondary">修改头像</button> -->

                        <button style="margin: 10px 0 0 90px" type="button" class="btn btn-secondary"
                            data-bs-toggle="modal" data-bs-target="#updateImg">
                            修改头像
                        </button>


                        <!-- Modal -->
                        <div class="modal fade" id="updateImg" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="exampleModalLabel">修改头像</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="update-url-title" class="form-label">URL</label>
                                            <input v-model="image" type="text" class="form-control"
                                                id="update-url-title" placeholder="请输入URL" />
                                        </div>
                                        <div style="color: red">{{ updateImgErr }}</div>
                                    </div>

                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.error_message }}</div>
                                        <button type="button" class="btn btn-primary" @click="update_images">
                                            确认
                                        </button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            取消
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px">
                    <div class="card-header">
                        <span style="font-size: 130%">我的Bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal"
                            data-bs-target="#add_bot_btn">
                            创建Bot
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="add_bot_btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="exampleModalLabel">创建Bot</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">名称</label>
                                            <input v-model="botadd.title" type="email" class="form-control"
                                                id="add-bot-title" placeholder="请输入Bot名称" />
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">简介</label>
                                            <textarea v-model="botadd.description" class="form-control"
                                                id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">代码</label>
                                            <VAceEditor v-model:value="botadd.content" lang="c_cpp" theme="textmate"
                                                :options="{
                                                    fontSize: 18,
                                                    tabSize: 4,
                                                    fontFamily: 'consolas',
                                                    highlightActiveLine: true,
                                                }" style="height: 300px" />
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ botadd.error_message }}</div>
                                        <button type="button" class="btn btn-primary" @click="add_bot">
                                            创建
                                        </button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                            取消
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary" style="margin-right: 10px"
                                            data-bs-toggle="modal" :data-bs-target="'#update_bot_modal' + bot.id">
                                            修改
                                        </button>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">
                                            删除
                                        </button>

                                        <!-- Modal -->
                                        <div class="modal fade" :id="'update_bot_modal' + bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                    <div class="modal-header">
                                                        <h5 class="modal-title">创建Bot</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                            aria-label="Close"></button>
                                                    </div>
                                                    <div class="modal-body">
                                                        <div class="mb-3">
                                                            <label for="add-bot-title" class="form-label">名称</label>
                                                            <input v-model="bot.title" type="email" class="form-control"
                                                                id="add-bot-title" placeholder="请输入Bot名称" />
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="add-bot-description"
                                                                class="form-label">简介</label>
                                                            <textarea v-model="bot.description" class="form-control"
                                                                id="add-bot-description" rows="3"
                                                                placeholder="请输入Bot简介"></textarea>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="add-bot-code" class="form-label">代码</label>
                                                            <VAceEditor v-model:value="bot.content" lang="c_cpp"
                                                                theme="textmate" :options="{
                                                                    fontSize: 18,
                                                                    tabSize: 4,
                                                                    fontFamily: 'consolas',
                                                                    highlightActiveLine: true,
                                                                }" style="height: 300px" />
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <div class="error-message">
                                                            {{ botadd.error_message }}
                                                        </div>
                                                        <button type="button" class="btn btn-primary"
                                                            @click="update_bot(bot)">
                                                            保存修改
                                                        </button>
                                                        <button type="button" class="btn btn-secondary"
                                                            data-bs-dismiss="modal">
                                                            取消
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
//vue3中绑定一个变量一般用ref，绑定一个对象用reactive更方便
import { ref, reactive } from "vue";
import $ from "jquery";
import { useStore } from "vuex";
import { Modal } from "bootstrap/dist/js/bootstrap";
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

export default {
    components: {
        VAceEditor,
    },
    setup() {
        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" +
            require("ace-builds").version +
            "/src-noconflict/"
        );

        const store = useStore();
        let bots = ref([]);
        let image = ref('');
        let updateImgErr = ref('');

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });

        const refresh_bots = () => {
            $.ajax({
                url: "https://kob.yuanheci.top/api/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                },
            });
        };
        refresh_bots(); //执行一下

        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "https://kob.yuanheci.top/api/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#add_bot_btn").hide(); //根据Modal的id来关闭
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
            });
        };

        const remove_bot = (bot) => {
            $.ajax({
                url: "https://yuanheci.top/api/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                },
            });
        };

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "https://kob.yuanheci.top/api/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        Modal.getInstance("#update_bot_modal" + bot.id).hide(); //根据Modal的id来关闭
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },
            });
        };

        const update_images = () => {
            console.log(image.value);
            $.ajax({
                url: "https://kob.yuanheci.top/api/user/account/updateImg/",
                type: "post",
                contentType: "application/json",
                data: JSON.stringify({
                    url: image.value,
                }),
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        Modal.getInstance("#updateImg").hide(); //根据Modal的id来关闭
                        store.dispatch("getinfo", {  //更新头像，重新渲染到前端
                            success() {
                                console.log("success");
                            },
                            error() {
                                console.log("error");
                            }
                        });
                    } else {
                        updateImgErr.value = resp.error_message;
                    }
                },
            });
        };

        return {
            bots,
            botadd,
            image,
            updateImgErr,
            add_bot,
            remove_bot,
            update_bot,
            update_images,
        };
    },
};
</script>

<style scoped>
.error-message {
    color: red;
}
</style>
