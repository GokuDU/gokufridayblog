<#include "/inc/layout.ftl"/>

<@layout "博客详情">

    <#include "/inc/header-panel.ftl"/>

    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md8 content detail">
                <div class="fly-panel detail-box">
                    <h1>${post.title}</h1>
                    <div class="fly-detail-info">
                        <!-- <span class="layui-badge">审核中</span> -->
                        <span class="layui-badge layui-bg-green fly-detail-column">${post.categoryName}</span>

                        <#if post.level gt 0>
                            <span class="layui-badge layui-bg-black">置顶</span>
                        </#if>

                        <#if post.recommend>
                            <span class="layui-badge layui-bg-red">精帖</span>
                        </#if>

                        <div class="fly-admin-box" data-id="${post.id}">



                            <#-- 发布作者删除 -->
                            <#if post.userId == profile.id && profile.username != 'admin'>
                                <span class="layui-btn layui-btn-xs jie-admin" type="del">删除</span>
                            </#if>

                            <#-- 管理员删除、置顶、加精 -->
                            <@shiro.hasRole name="admin">

                                <span class="layui-btn layui-btn-xs jie-admin" type="set" field="delete" rank="1">删除</span>

                                <#-- post.level == 0  ===》Controller中  设置 post.setLevel(rank);  setLevel 为 1 -->
                                <#if post.level == 0>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set" field="stick" rank="1">置顶</span>
                                </#if>
                                <#-- post.level == 1  ===》Controller中  设置 post.setLevel(rank);   0 -->
                                <#if post.level gt 0>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set" field="stick" rank="0" style="background-color:#ccc;">取消置顶</span>
                                </#if>
                                <#-- js中   0 == false 、 1==true -->
                                <#if !post.recommend>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set" field="status" rank="1">加精</span>
                                </#if>
                                <#if post.recommend>
                                    <span class="layui-btn layui-btn-xs jie-admin" type="set" field="status" rank="0" style="background-color:#ccc;">取消加精</span>
                                </#if>


                            </@shiro.hasRole>


                        </div>
                        <span class="fly-list-nums">
                            <a href="#comment"><i class="iconfont" title="回答">&#xe60c;</i>${post.commentCount}</a>
                            <i class="iconfont" title="人气">&#xe60b;</i> ${post.viewCount}
                        </span>
                    </div>

                    <div class="detail-about">
                        <a class="fly-avatar" href="../user/${post.authorId}">
                            <img src="${post.authorAvatar}" alt="${post.authorName}">
                        </a>
                        <div class="fly-detail-user">
                            <a href="../user/${post.authorId}" class="fly-link">
                                <cite>${post.authorName}</cite>
                            </a>
                            <span>${timeAgo(post.created)}</span>
                        </div>
                        <div class="detail-hits" id="LAY_jieAdmin" data-id="${post.id}">
                            <span class="layui-btn layui-btn-xs jie-admin" type="edit"><a href="/post/edit?id=${post.id}">编辑此贴</a></span>
                        </div>
                    </div>

                    <div class="detail-body photos">
                        ${post.content}
                    </div>

                </div>


                <div class="fly-panel detail-box" id="flyReply">
                    <fieldset class="layui-elem-field layui-field-title" style="text-align: center;">
                        <legend>回帖</legend>
                    </fieldset>

                    <ul class="jieda" id="jieda">

                        <#list commentPageData.records as comment>

                            <li data-id="${comment.id}" class="jieda-daan">
                                <a name="${comment.id}"></a>
                                <div class="detail-about detail-about-reply">
                                    <a class="fly-avatar" href="user/${post.authorId}">
                                        <img src="${comment.authorAvatar}" alt="${comment.authorName}">
                                    </a>
                                    <div class="fly-detail-user">
                                        <a href="user/${comment.authorId}" class="fly-link">
                                            <cite>${comment.authorName}</cite>
                                        </a>

                                        <#if post.authorId == comment.userId>
                                            <span>(楼主)</span>
                                        </#if>

                                    </div>

                                    <div class="detail-hits">
                                        <span>${timeAgo(comment.created)}</span>
                                    </div>

                                </div>

                                <div class="detail-body jieda-body photos">
                                    ${comment.content}
                                </div>

                                <div class="jieda-reply">

                                  <span class="jieda-zan zanok" type="zan">
                                    <i class="iconfont icon-zan"></i>
                                    <em>${comment.voteUp}</em>
                                  </span>

                                   <span type="reply">
                                    <i class="iconfont icon-svgmoban53"></i>
                                    回复
                                  </span>

                                    <div class="jieda-admin">
                                        <#if profile.id == comment.userId>
                                            <span type="del">删除</span>
                                        </#if>

                                        <!-- <span class="jieda-accept" type="accept">采纳</span> -->
                                    </div>
                                </div>
                            </li>

                        </#list>
                    </ul>

                    <@paging commentPageData></@paging>

                    <div class="layui-form layui-form-pane">
                        <form action="/post/reply/" method="post">
                            <div class="layui-form-item layui-form-text">
                                <a name="comment"></a>
                                <div class="layui-input-block">
                                    <textarea id="L_content" name="content" required lay-verify="required" placeholder="请输入内容"  class="layui-textarea fly-editor" style="height: 150px;"></textarea>
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <input type="hidden" name="postId" value="${post.id}">
                                <button class="layui-btn" lay-filter="*" lay-submit>提交回复</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <#include "/inc/right.ftl"/>
        </div>
    </div>

<script>
    layui.cache.page = 'jie';

    $(function () {
        layui.use(['fly','face'],function () {
            var fly =layui.fly;

            //如果你是采用模版自带的编辑器，你需要开启以下语句来解析。
            $('.detail-body').each(function(){
                var othis = $(this), html = othis.html();
                othis.html(fly.content(html));
            });
        });
    });
</script>

</@layout>





