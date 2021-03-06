package com.naturalprogrammer.spring.lemon.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.naturalprogrammer.spring.lemon.LemonProperties;
import com.naturalprogrammer.spring.lemon.domain.AbstractUser;
import com.naturalprogrammer.spring.lemon.domain.AbstractUserRepository;
import com.naturalprogrammer.spring.lemon.util.LemonUtil;

/**
 * Reference
 *   http://www.captaindebug.com/2011/07/writng-jsr-303-custom-constraint_26.html#.VIVhqjGUd8E
 *   http://www.captechconsulting.com/blog/jens-alm/versioned-validated-and-secured-rest-services-spring-40-2?_ga=1.71504976.2113127005.1416833905
 * 
 * @author skpat_000
 *
 */
@Component
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private static class ResponseData {
		
		private boolean success;
		
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public Collection<String> getErrorCodes() {
			return errorCodes;
		}
		public void setErrorCodes(Collection<String> errorCodes) {
			this.errorCodes = errorCodes;
		}
	}
	
	@Autowired
	private LemonProperties properties;

	@Resource
	private RestTemplate restTemplate;
	
	@Override
	public boolean isValid(String captchaResponse, ConstraintValidatorContext context) {
		
	    
		/**
	     * Refer http://www.journaldev.com/7133/how-to-integrate-google-recaptcha-in-java-web-application  
	     */
		
		if (!properties.getRecaptcha().isEnabled()) { // e.g. while testing
			log.debug("Captcha validation not done, as it is disabled in application properties.");
			return true;
		}
		
		if (captchaResponse == null || "".equals(captchaResponse))
	         return false;
	        
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>(2);
		formData.add("response", captchaResponse);
		formData.add("secret", properties.getRecaptcha().getSecretkey());
		
		try {

			// This also works:
			//	ResponseData responseData = restTemplate.postForObject(
			//	   "https://www.google.com/recaptcha/api/siteverify?response={0}&secret={1}",
			//	    null, ResponseData.class, captchaResponse, reCaptchaSecretKey);

			ResponseData responseData = restTemplate.postForObject(
			   "https://www.google.com/recaptcha/api/siteverify",
			   formData, ResponseData.class);
			
			if (responseData.success) {
				log.debug("Captcha validation succeeded.");
				return true;
			}
			
			log.info("Captcha validation failed.");
			return false;
			
		} catch (Throwable t) {
			log.error(ExceptionUtils.getStackTrace(t));
			return false;
		}
		
	}

	@Override
	public void initialize(Captcha constraintAnnotation) {
		log.debug("Captcha validator initialized.");
	}

}
