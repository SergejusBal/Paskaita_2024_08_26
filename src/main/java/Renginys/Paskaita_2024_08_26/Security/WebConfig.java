package Renginys.Paskaita_2024_08_26.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://127.0.0.1:5500",
                                "http://localhost:7778",
                                "http://127.0.0.1:7778"
                        ) // List specific origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow more HTTP methods as needed
                        .allowedHeaders("*") // Allow all headers
                        .allowCredentials(false); // Allow credentials if necessary
            }
        };
    }
}