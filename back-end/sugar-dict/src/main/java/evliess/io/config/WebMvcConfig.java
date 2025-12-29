package evliess.io.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${audio.dir}")
  private String audioDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/public/v1/audio/**")
      .addResourceLocations("file:" + audioDir)
      .setCachePeriod(3600)
      .resourceChain(true);

    String custom = "custom/";
    registry.addResourceHandler("/public/v1/audio/custom/**")
      .addResourceLocations("file:" + audioDir + File.separator + custom)
      .setCachePeriod(3600).resourceChain(true);

  }
}
