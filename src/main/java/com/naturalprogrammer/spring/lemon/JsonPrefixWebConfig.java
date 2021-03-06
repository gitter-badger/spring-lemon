package com.naturalprogrammer.spring.lemon;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * If you want to disable this, e.g. while testing or in pure REST APIs,
 * in your application.properties, use
 * 
 * lemon.jsonprefix.enabled: false
 *
 * https://docs.angularjs.org/api/ng/service/$http
 * http://stackoverflow.com/questions/26384930/how-to-add-n-before-each-spring-json-response-to-prevent-common-vulnerab
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web
 * @author skpat_000
 *
 */
@Configuration
@ConditionalOnProperty(name="lemon.enabled.jsonprefix", matchIfMissing=true)
public class JsonPrefixWebConfig extends WebMvcConfigurerAdapter {
	
	private final Log log = LogFactory.getLog(getClass());
	
	public final static String JSON_PREFIX = ")]}',\n";
	
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setJsonPrefix(JSON_PREFIX);
        converters.add(converter);
        
        log.info("Configured prefixing JSON with )]}... for JSON vulnerability");
    }
    
}