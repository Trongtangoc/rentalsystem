package com.codegym.projectmodule5.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static resources from classpath
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);

        // Serve uploaded files from 'uploads' directory (relative to project root)
        // Ensure the 'uploads' directory is located correctly on your system (outside of src folder)
        String uploadPath = new File("uploads").getAbsolutePath() + File.separator;

        // Serve all files under '/uploads/' with the actual file path on the system
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600);

        // Specifically for images
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + uploadPath + "images" + File.separator)
                .setCachePeriod(3600);

        // Add common image extensions for proper handling (e.g., for .jpg, .png)
        registry.addResourceHandler("/uploads/images/*.jpg", "/uploads/images/*.jpeg",
                        "/uploads/images/*.png", "/uploads/images/*.gif", "/uploads/images/*.webp")
                .addResourceLocations("file:" + uploadPath + "images" + File.separator)
                .setCachePeriod(3600);
    }
}
