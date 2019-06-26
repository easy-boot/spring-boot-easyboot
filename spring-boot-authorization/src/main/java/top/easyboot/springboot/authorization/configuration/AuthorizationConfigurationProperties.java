package top.easyboot.springboot.authorization.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.easyboot.springboot.authorization.property.RestfulApiAuthProperties;

@Configuration
@EnableConfigurationProperties(RestfulApiAuthProperties.class)
public class AuthorizationConfigurationProperties{

}
