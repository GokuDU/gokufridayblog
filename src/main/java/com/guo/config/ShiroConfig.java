package com.guo.config;

import cn.hutool.core.map.MapUtil;
import com.guo.shiro.AccountRealm;
import com.guo.shiro.AuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShiroConfig {

//    @Bean
//    public SecurityManager securityManager(AccountRealm accountRealm) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(accountRealm);
//
//        log.info("---------------------->securityManager注入成功");
//
//        return securityManager;
//    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager(AccountRealm accountRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);

        log.info("------------------>securityManager注入成功");

        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        // 过滤器工厂
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url 和 登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        // 配置自定义过滤器
        filterFactoryBean.setFilters(MapUtil.of("myAuth", authFilter()));

        Map<String, String> hashMap = new LinkedHashMap<>();
        // 登录不需要权限
        hashMap.put("/login", "anon");
        hashMap.put("/webSocket", "anon");
        hashMap.put("/res/**", "anon");

        // 需要登录
        hashMap.put("/user/home", "myAuth");
        hashMap.put("/user/set", "myAuth");
        hashMap.put("/user/upload", "myAuth");

        hashMap.put("/user/index", "myAuth");
        hashMap.put("/user/publish", "myAuth");
        hashMap.put("/user/collect", "myAuth");
        hashMap.put("/user/message", "myAuth");
        hashMap.put("/message/remove", "myAuth");
        hashMap.put("/message/nums", "myAuth");

        hashMap.put("/collection/find", "myAuth");
        hashMap.put("/collection/add", "myAuth");
        hashMap.put("/collection/remove", "myAuth");

        hashMap.put("/post/edit", "myAuth");
        hashMap.put("/post/submit", "myAuth");
        hashMap.put("/post/delete", "myAuth");
        hashMap.put("/post/reply/", "myAuth");
        hashMap.put("/post/zanReply/", "myAuth");

        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;
    }

    // 注入 自定义过滤器 AuthFilter
    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }
}
