export default {
    state: {
        status: "matching",  //matching匹配界面，playing表示对战界面
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;  
        },
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    //异步修改state的函数放到actions中
    actions: {
        
    },
    modules: {
    }
}
