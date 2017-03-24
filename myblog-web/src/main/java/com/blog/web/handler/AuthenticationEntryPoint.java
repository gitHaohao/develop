package com.blog.web.handler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;


public class AuthenticationEntryPoint extends CasAuthenticationEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationEntryPoint.class);
	
	private String noLoginPage = null;

	/**
	 * ���������/login/loginStatus.*,��׷��&noLoginPage=true��֪ͨsso�����ɵ�¼ҳ��
	 */
	protected String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
		String serviceUrl = super.createServiceUrl(request, response);
		String origTarget = request.getRequestURI();
		LOGGER.debug("origTarget is:" + origTarget);
		if (origTarget != null && origTarget.startsWith("/login/loginStatus.")) {
			this.noLoginPage = "&noLoginPage=true";
		}else{
			this.noLoginPage = null;
		}
		return serviceUrl;
	}
	
	protected String createRedirectUrl(final String serviceUrl) {
		String redirectUrl = super.createRedirectUrl(serviceUrl);
		if(this.noLoginPage!=null){
			return redirectUrl + this.noLoginPage;
		}else{
			return redirectUrl;
		}
	}
}
