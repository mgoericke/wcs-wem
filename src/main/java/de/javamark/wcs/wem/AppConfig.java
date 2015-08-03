package de.javamark.wcs.wem;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.fatwire.wem.sso.SSOFilter;

@Component
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class AppConfig {


	@Bean
	public Filter ssofilter(){
		return new SSOFilter();
	}
}
