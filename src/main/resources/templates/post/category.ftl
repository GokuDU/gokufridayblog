<#include "/inc/layout.ftl"/>

<@layout "博客分类">

<#include "/inc/header-panel.ftl"/>

<div class="layui-container">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md8">
            <div class="fly-panel" style="margin-bottom: 0;">

                <div class="fly-panel-title fly-filter">
                    <a href="" class="layui-this">综合</a>
                    <span class="fly-mid"></span>
                </div>


                    <ul class="fly-list">
                        <#list categoryData.records as post>

                            <@postlisting post></@postlisting>

                        </#list>
                    </ul>

                <@paging categoryData></@paging>


            </div>
        </div>

        <#include "/inc/right.ftl"/>
    </div>
</div>

<script>
    layui.cache.page = 'jie';
</script>

</@layout>