<#include "/inc/layout.ftl"/>

<@layout "用户中心">

  <div class="layui-container fly-marginTop fly-user-main">

    <@centerLeft level=1></@centerLeft>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>

    <div class="site-tree-mobile layui-hide">
      <i class="layui-icon">&#xe602;</i>
    </div>
    <div class="site-mobile-shade"></div>


    <div class="fly-panel fly-panel-user" pad20>


      <div class="layui-tab layui-tab-brief" lay-filter="user">
        <ul class="layui-tab-title" id="LAY_mine">
          <li data-type="mine-jie" lay-id="index" class="layui-this">我发的帖（<span>89</span>）</li>
          <li data-type="collection" data-url="/collection/find/" lay-id="collection">我收藏的帖（<span>16</span>）</li>
        </ul>
        <div class="layui-tab-content" style="padding: 20px 0;">
          <div class="layui-tab-item layui-show">

            <#-- 发布的文章 -->
            <ul class="mine-view jie-row" id="publishFlow">
              <script id="tpl-publish" type="text/html">
                <li>
                  <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{d.title}}</a>
                  <i>{{layui.util.toDateString(d.created, 'yyyy-MM-dd HH:mm:ss')}}</i>
                  <a class="mine-edit" href="/post/edit?id={{d.id}}">编辑</a>
                  <em>{{d.viewCount}}阅/{{d.commentCount}}答</em>
                </li>
              </script>
            </ul>

            <div id="LAY_page"></div>
          </div>
          <div class="layui-tab-item">

            <#-- 收藏的文章 -->
            <ul class="mine-view jie-row" id="collectFlow">
              <script id="tpl-collect" type="text/html">
                <li>
                  <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{d.title}}</a>
                  <i>收藏于{{layui.util.timeAgo(d.created, true)}}前</i>
                </li>
              </script>
            </ul>

            <div id="LAY_page1"></div>
          </div>
        </div>
      </div>
    </div>
  </div>


<script>
  layui.cache.page = 'user';

  layui.use(['laytpl','flow','util'], function(){
      var $ = layui.jquery;
      var laytpl = layui.laytpl;
      var flow = layui.flow;
      var util = layui.util;

      // 流加载 --> {发布的文章}
      flow.load({
        elem: '#publishFlow' //指定列表容器
        ,isAuto: false     // 不自动加载
        ,done: function(page, next){ //到达临界点（默认滚动触发），触发下一页
          var lis = [];

          //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
          $.get('/user/publish?pn='+page, function(res){
            //假设你的列表返回在data集合中    item 存放数据
            layui.each(res.data.records, function(index, item){

               var getTpl = $("#tpl-publish").html();
               // laytpl 和 item的数据 进行渲染得到 html
               laytpl(getTpl).render(item,function (html) {
                 $("#publishFlow .layui-flow-more").before(html);
               });
            });

            //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
            //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
            next(lis.join(''), page < res.data.pages);
          });
        }
      });


    // 流加载 --> {收藏的文章}
    flow.load({
      elem: '#collectFlow' //指定列表容器
      ,isAuto: false     // 不自动加载
      ,done: function(page, next){ //到达临界点（默认滚动触发），触发下一页
        var lis = [];

        //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
        $.get('/user/collect?pn='+page, function(res){
          //假设你的列表返回在data集合中    item 存放数据
          layui.each(res.data.records, function(index, item){

            var getTpl = $("#tpl-collect").html();
            // laytpl 和 item的数据 进行渲染得到 html
            laytpl(getTpl).render(item,function (html) {
              $("#collectFlow .layui-flow-more").before(html);
            });
          });

          //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
          //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
          next(lis.join(''), page < res.data.pages);
        });
      }
    });

  });

</script>

</@layout>