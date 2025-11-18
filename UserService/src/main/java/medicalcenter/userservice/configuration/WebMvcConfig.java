package medicalcenter.userservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Конфигурация для обслуживания статических ресурсов (загруженные файлы чата)
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get("uploads/chat").toAbsolutePath().toString();
        registry.addResourceHandler("/uploads/chat/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}

