package org.cv.moa.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadDir = "file:///" + System.getProperty("user.dir") + "/uploads/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 요청이 오면 로컬 uploads 폴더 내용을 보여줌
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir);
    }
}
