package com.appirio.tech.core.api.v3.util.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.appirio.tech.core.api.v3.util.jwt.JWTToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger logger = (Logger)LoggerFactory.getLogger(LoggingFilter.class);
	
	protected static final String MDC_KEY_USER = "MDC_KEY_USER";
	protected static final String MDC_KEY_REQUESTBODY = "MDC_KEY_REQUESTBODY";
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		MDC.put(MDC_KEY_USER, getUserInfo(request));
		MDC.put(MDC_KEY_REQUESTBODY, getRequestBody(request));
		return request;
	}

	@Override
	public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
		try {
			logger.info(generateLog(request, response));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return response;
	}

	protected static String LOG_TEMPLATE = "[%s] [%s] [/%s] [%s] [%d] [%s]"; 
	/*
	 * Template: "[1] [2] [3] [4] [5] [6]"
	 * 1: username,userId (e.g. johndoe,1234567)
	 * 2: request method  (e.g. POST)
	 * 3: request path    (e.g. /authorizations)
	 * 4: request body    (e.g. handle=jdoe&password=xxxxxx, {{"handle":"jdoe","password":"xxxxxx"}})
	 * 5: response status (e.g. 200)
	 * 6: response body   (e.g. {{"handle":"jdoe","email":"jdoe@topcoder.com", ...}})
	 */
	protected String generateLog(ContainerRequest request, ContainerResponse response) {
		try {
			return String.format(LOG_TEMPLATE,
									MDC.get(MDC_KEY_USER),
									request.getMethod(),
									maskPassword(getPath(request)),
									maskPassword(MDC.get(MDC_KEY_REQUESTBODY)),
									response.getStatus(),
									maskPassword(getResponseBody(response)));
		} finally {
			MDC.clear();
		}
	}
	
	protected String getPath(ContainerRequest request) {
		if(request==null)
			return null;
		String reqUrl = request.getRequestUri()!=null ? request.getRequestUri().toString() : null;
		if(reqUrl==null)
			return request.getPath();
		
		String baseUrl = request.getBaseUri()!=null ? request.getBaseUri().toString() : null;
		return baseUrl!=null ? reqUrl.substring(baseUrl.length()) : reqUrl;
	}
	
	protected String getUserInfo(ContainerRequest request) {
		if(request==null)
			return "";
		String header = request.getHeaderValue(HttpHeaders.AUTHORIZATION);
		if(header==null)
			return "";
		
		final int space = header.indexOf(' ');
		if (space < 0)
			return "";
		
		final String type = header.substring(0, space);
		if (!"bearer".equalsIgnoreCase(type))
			return "";

		String cred = header.substring(space + 1);
		try {
			JWTToken token = new JWTToken();
			token.apply(cred);
			return String.format("%s,%s", token.handle, token.userId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}
	
	protected String getRequestBody(ContainerRequest request) {
		if(request==null)
			return "";
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = request.getEntityInputStream();
		try {
			ReaderWriter.writeTo(in, out);
			byte[] buff = out.toByteArray();
			request.setEntityInputStream(new ByteArrayInputStream(buff));
			return buff.length == 0 ? "" : new String(buff).replaceAll("\\n", "");
		} catch (Exception e) {
			String err = String.format("Failed to get request body. (%s: %s)", e.getClass().getName(), e.getMessage());
			logger.error(err, e);
			return err;
		}
    }
	
	protected String getResponseBody(ContainerResponse response) {
		if(response==null)
			return "";
		Object entity = response.getEntity();
		try {
			return entity==null ? "" : mapper.writeValueAsString(entity);
		} catch (Exception e) {
			logger.error(String.format("Failed to get response body. (%s: %s)", e.getClass().getName(), e.getMessage()), e);
			return entity==null ? "" : entity.toString();
		}
	}
	
	protected static final Pattern PATTERN_PASSWORD_QUERY = Pattern.compile("(?i)(password[\\s\\t]*=[\\s\\t]*([^&]+))", Pattern.DOTALL);
	protected static final Pattern PATTERN_PASSWORD_JSON= Pattern.compile("(?i)(\"?password\"?[\\s\\t]*:[\\s\\t]*\"([^\"]+))", Pattern.DOTALL);

	protected String maskPassword(String text) {
		return maskPassword(maskPassword(text, PATTERN_PASSWORD_QUERY), PATTERN_PASSWORD_JSON);
	}
	
	protected String maskPassword(String text, Pattern p) {
		if(text==null || text.trim().length()==0)
			return text;
		
		int group = 2;
		try {
			Matcher m = p.matcher(text);
			String t = text;
			while(m.find()) {
				if(m.groupCount()==group) {
					String sub = StringUtils.repeat("*", m.end(group)-m.start(group));
					t = t.substring(0, m.start(group))+sub+t.substring(m.end(group));
				}
			}
			return t;
		} catch (Exception e) {
			logger.error("Error occurred in processing text.", e);
			return text;
		}
	}
}
