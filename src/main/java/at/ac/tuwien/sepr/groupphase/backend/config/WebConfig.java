package at.ac.tuwien.sepr.groupphase.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS mappings...");
        registry.addMapping("/**")
            .allowedOrigins(
                "https://sepm-frontend.vercel.app",
                "http://localhost:4200",
                "https://sepm-backend-6xd0.onrender.com"
            )
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
            .maxAge(3600);
        logger.info("CORS mappings configured successfully");
    }
} 