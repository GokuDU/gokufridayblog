<#include "/inc/layout.ftl"/>

<@layout "我的消息">

  <div class="layui-container fly-marginTop fly-user-main">

    <@centerLeft level=3></@centerLeft>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>


    <div class="fly-panel fly-panel-user" pad20>
        <div class="layui-tab layui-tab-brief" lay-filter="user" id="LAY_msg" style="margin-top: 15px;">
          <button class="layui-btn layui-btn-danger" id="LAY_delallmsg">清空全部消息</button>
          <div  id="LAY_minemsg" style="margin-top: 10px;">
          <!--<div class="fly-none">您暂时没有最新消息</div>-->
          <ul class="mine-msg">

            <#list messagePageData.records as message>

                <li data-id="${message.id}">
                  <blockquote class="layui-elem-quote">

                  <#if message.type == 0>
                     系统消息：${message.content}
                  </#if>

                  <#if message.type == 1>
                    ${message.fromUserName}
                    -->评论了你的文章：<a href="/post/${message.postId}"> ${message.postTitle} </a>
                    --> 内容为：${message.content}
                  </#if>

                  <#if message.type == 2>
                    ${message.fromUserName}
                    -->回复了你：${message.content}
                    -->文章是：<a href="/post/${message.postId}"> ${message.postTitle} </a>
                  </#if>

                  </blockquote>
                  <p>
                    <span>${timeAgo(message.created)}</span>
                    <a href="javascript:;" class="layui-btn layui-btn-small layui-btn-danger fly-delete">删除</a>
                  </p>
                </li>

            </#list>

            <#if messagePageData.total != 0>
              <@paging messagePageData></@paging>
            </#if>



          </ul>
        </div>
        </div>
      </div>

  </div>


<script>
  layui.cache.page = 'user';
</script>

</@layout>