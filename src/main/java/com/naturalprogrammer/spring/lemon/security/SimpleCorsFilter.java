package com.naturalprogrammer.spring.lemon.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.naturalprogrammer.spring.lemon.LemonProperties;

/**
 * If you want to disable this, e.g. while testing or in pure REST APIs,
 * in your application.properties, use
 * 
 * lemon.cors.enabled: false
 * 
 * https://spring.io/guides/gs/rest-service-cors/
 * 
 * @author skpat_000
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name="lemon.enabled.cors", matchIfMissing=true)
public class SimpleCorsFilter implements Filter {

	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	LemonProperties properties;
	
	public void doFilter(ServletRequest req,
			ServletResponse res,
			FilterChain chain)
	throws IOException, ServletException {
		
		log.debug("Inside SimpleCorsFilter");
		
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin",
			properties.getApplicationUrl()); // "*" does not work when $httpProvider.defaults.withCredentials = true;
		response.setHeader("Access-Control-Allow-Methods",
			"GET, POST, PUT, PATCH, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age",
			"3600");
		response.setHeader("Access-Control-Allow-Headers",
			"x-requested-with,origin,content-type,accept,X-XSRF-TOKEN");
		response.setHeader("Access-Control-Allow-Credentials", "true"); // needed when $httpProvider.defaults.withCredentials = true;
		
		HttpServletRequest request =  (HttpServletRequest) req;
		
		if (!request.getMethod().equals("OPTIONS"))
		   chain.doFilter(req, res);
	}

	public void init(FilterConfig filterConfig) {}

	public void destroy() {}
}
