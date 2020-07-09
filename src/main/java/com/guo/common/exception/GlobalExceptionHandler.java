package com.guo.common.exception;

import cn.hutool.json.JSONUtil;
import com.guo.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 拦截所有异常
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handler(HttpServletRequest request, HttpServletResponse response,Exception e) throws IOException {
        //  ajax 请求
        String header = request.getHeader("X-Requested-With");
        if (header != null && "XMLHttpRequest".equals(header)) {
            // 判断当前用户是否登录(认证)
            boolean authenticated = SecurityUtils.getSubject().isAuthenticated();
            if (!authenticated) {
                // 如果是未登录[认证]  返回 json 数据
                // 设置 JSON数据格式  设置编码
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONUtil.toJsonStr(Result.fail(e.getMessage())));
                return null;
            }
        }

        // web 请求
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        return modelAndView;

    }
}
