<#--分页-->
<#macro paging pageData>

    <div style="text-align: center">
        <div id="laypage-main">

        </div>

        <script>
            layui.use('laypage', function(){
                var laypage = layui.laypage;

                //执行一个laypage实例
                laypage.render({
                    elem: 'laypage-main' //注意，这里的 test1 是 ID，不用加 # 号
                    ,count: ${pageData.total} //数据总数，从服务端得到
                    ,curr: ${pageData.current}
                    ,limit: ${pageData.size}
                    ,jump: function(obj, first){
                        // 切换分页的回调
                        //obj包含了当前分页的所有参数，比如：
                        console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                        console.log(obj.limit); //得到每页显示的条数

                        //首次不执行 进入那一页就就不执行这个页的跳转
                        if(!first){
                            location.href = "?pn=" + obj.curr
                        }
                    }
                });
            });
        </script>
    </div>

</#macro>

<#macro postlisting post>
    <li>
        <a href="user/${post.authorId}" class="fly-avatar">
            <img src="${post.authorAvatar}" alt="${post.authorName}">
        </a>
        <h2>
            <a class="layui-badge">${post.categoryName}</a>
            <a href="post/${post.id}">${post.title}</a>
        </h2>
        <div class="fly-list-info">
            <a href="user/${post.authorId}" link>
                <cite>${post.authorName}</cite>
            </a>
            <#--<span>${post.created?string("yyyy-MM-dd")}</span>-->
            <span>${timeAgo(post.created)}</span>


            <span class="fly-list-nums">
                <i class="iconfont icon-pinglun1" title="回答"></i> ${post.commentCount}
            </span>

        </div>
        <div class="fly-list-badge">

            <#if post.recommend>
                <span class="layui-badge layui-bg-red">精帖</span>
            </#if>
            <#if post.level gt 0>
                <span class="layui-badge layui-bg-black">置顶</span>
            </#if>

        </div>
    </li>
</#macro>

<#-- 用户中心左边栏 参数 level： 0 、1 、2 、3     -->
<#macro centerLeft level>
    <ul class="layui-nav layui-nav-tree layui-inline" lay-filter="user">
        <li class="layui-nav-item <#if level == 0>layui-this</#if>">
            <a href="/user/home">
                <i class="layui-icon">&#xe609;</i>
                我的主页
            </a>
        </li>
        <li class="layui-nav-item <#if level == 1>layui-this</#if>">
            <a href="/user/index">
                <i class="layui-icon">&#xe612;</i>
                用户中心
            </a>
        </li>
        <li class="layui-nav-item <#if level == 2>layui-this</#if>">
            <a href="/user/set">
                <i class="layui-icon">&#xe620;</i>
                基本设置
            </a>
        </li>
        <li class="layui-nav-item <#if level == 3>layui-this</#if>">
            <a href="/user/message">
                <i class="layui-icon">&#xe611;</i>
                我的消息
            </a>
        </li>
    </ul>
</#macro>