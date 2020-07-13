if (typeof (tio) == "undefined") {  // 如果tio未定义，声明其为一个类
    tio = {};
}

tio.ws = {};    // 内部类
tio.ws = function ($,layim) {  // 构造方法 传过来的参数： layui.jquery 、 layim

    var self = this; // 定义一个全局名称

    // 建立连接
    this.connect = function () {
        var url = "ws://localhost:9981";
        var socket = new WebSocket(url);

        // 设置全局 socket
        self.socket = socket;

        socket.onopen = function () {
            console.log("tio ws 启动--------->>>>")
        };

        socket.onclose = function () {
            console.log("tio ws 关闭--------->>>>")
        };

        // 传过来的消息
        socket.onmessage = function (res) {
            console.log("接收到了消息========>>");
            console.log(res);
        };
    };

    // 获取个人、群聊信息，并打开聊天窗口
    this.openChatWindow = function () {

        // 获取个人、群聊信息
        $.ajax({
            url: "/chat/getMineAndGroupData",
            async: false,
            success: function (res) {
                self.group = res.data.group;
                self.mine = res.data.mine;
            }
        });

        console.log(self.group);
        console.log(self.mine);

        // 获取缓存
        var cache =  layui.layim.cache();
        cache.mine = self.mine;

        // 打开聊天窗口
        layim.chat(self.group);
        // 收缩聊天面板
        layim.setChatMin();
    } ;
    
    // 监听发送消息
    this.sendChatMessage = function (res) {
        self.socket.send(JSON.stringify({
            type: 'chatMessage',  // 随便定义，用于在服务端区分消息类型
            data: res
        }));
    };

    // 历史聊天信息回显
    this.initHistoryMess = function () {

    };

};
