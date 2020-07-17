if (typeof (tio) == "undefined") {  // 如果tio未定义，声明其为一个类
    tio = {};
}

tio.ws = {};    // 内部类
tio.ws = function ($,layim) {  // 构造方法 传过来的参数： layui.jquery 、 layim

    this.heartbeatTimeout = 3000; // 心跳超时时间，单位：毫秒
    this.heartbeatSendInterval = this.heartbeatTimeout / 2; // 心跳超时半值

    var self = this; // 定义一个全局名称

    // 建立连接
    this.connect = function () {
        // var url = "ws://localhost:9981?userId=" + self.userId;
        var url = "ws://112.74.110.84:9981?userId=" + self.userId;
        // var socket = new WebSocket(url);

        // 设置全局 socket
        self.socket = socket;

        socket.onopen = function () {
            console.log("tio ws 启动--------->>>>")

            // 设置 最后发送消息的时间
            self.lastInteractionTime(new Date().getTime());

            // 建立心跳
            self.ping();
        };

        socket.onclose = function () {
            console.log("tio ws 关闭--------->>>>")

            //尝试重连
            self.reconn();
        };

        // 传过来的消息
        socket.onmessage = function (res) {
            console.log("接收到了消息========>>");
            console.log(res);

            var msgBody = eval('('+ res.data +')');   // json数据转为对象
            if (msgBody.emit === 'chatMessage') {
                layim.getMessage(msgBody.data);
            }

            // 更新 最后发送消息的时间
            self.lastInteractionTime(new Date().getTime());
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

                self.userId = self.mine.id;
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
        // 清除缓存
        localStorage.clear();
        // 获取个人、群聊信息
        $.ajax({
            url: "/chat/getGroupHistoryMsg",
            success: function (res) {
                var data = res.data;
                if (data.length < 1) {
                    return;
                }

                for (var i in data) {
                    layim.getMessage(data[i]);
                }
            }
        });
    };

    // ------------- 心跳机制 ------------------

    // 最后发送消息的时间
    this.lastInteractionTime = function () {
        // debugger;
        if (arguments.length == 1) {
            this.lastInteractionTimeValue = arguments[0]
        }
        return this.lastInteractionTimeValue
    }

    this.ping = function () {
        console.log("------------->准备心跳中~");


        //建立一个定时器，定时心跳
        self.pingIntervalId = setInterval(function () {
            var intervalTimeValue = new Date().getTime() - self.lastInteractionTime(); // 已经多久没发消息了

            // debugger;

            // 单位：秒
            if ((self.heartbeatSendInterval + intervalTimeValue) >= self.heartbeatTimeout) {
                self.socket.send(JSON.stringify({
                    type: 'pingMessage'
                    ,data: 'ping'
                }))
                console.log("------------->心跳中~")
            }
        }, self.heartbeatSendInterval)
    };

    this.reconn = function () {
        // 先删除心跳定时器
        clearInterval(self.pingIntervalId);

        // 然后尝试重连
        self.connect();
    };

};
