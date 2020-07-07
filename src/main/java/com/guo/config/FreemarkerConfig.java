package com.guo.config;

import com.guo.template.HotsTemplate;
import com.guo.template.PostTemplate;
import com.guo.template.TimeAgo;
import com.jagregory.shiro.freemarker.ShiroTags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;

    @Autowired
    PostTemplate postTemplate;

    @Autowired
    HotsTemplate hotsTemplate;

    @PostConstruct
    public void setUp() {
        configuration.setSharedVariable("timeAgo",new TimeAgo());
        configuration.setSharedVariable("posts",postTemplate);
        configuration.setSharedVariable("hots",hotsTemplate);
        configuration.setSharedVariable("shiro",new ShiroTags());
    }
}
