package com.guo.shiro;

import cn.hutool.json.JSONUtil;
import com.guo.common.lang.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *  自定义 shiro过滤器 判断请求是否是 ajax 请求
 *  以及对不同类型请求的不同处理
 */
public class AuthFilter extends UserFilter {

    // 如果用户未登录 进行一些只有登陆后才能操作的动作 --》
    //    如果是 ajax 请求 --》 弹窗显示未登录
    //    如果是 web 请求 --》 重定向到登录界面
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 如果是 ajax 请求 --》 弹窗显示未登录
        String header = httpServletRequest.getHeader("X-Requested-With");
        if (header != null && "XMLHttpRequest".equals(header)) {
            // 判断当前用户是否登录(认证)
            boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
            if (!authenticated) {
                // 如果是未登录[认证]  返回 json 数据
                // 设置 JSON数据格式  设置编码
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONUtil.toJsonStr(Result.fail("请您先登录!")));
            }
        } else {
            // 如果是 web 请求 --》 重定向到登录界面
            super.redirectToLogin(request, response);
        }

    }
}
