package com.appirio.tech.core.api.v3.util.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.appirio.tech.core.api.v3.util.jwt.JWTToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.glassfish.jersey.message.internal.ReaderWriter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger logger = (Logger)LoggerFactory.getLogger(LoggingFilter.class);
	
	protected static final String MDC_KEY_USER = "MDC_KEY_USER";
	protected static final String MDC_KEY_REQUESTBODY = "MDC_KEY_REQUESTBODY";
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		MDC.put(MDC_KEY_USER, getUserInfo(request));
		MDC.put(MDC_KEY_REQUESTBODY, getRequestBody(request));
	}
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		try {
			logger.info(generateLog(request, response));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
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
	protected String generateLog(ContainerRequestContext request, ContainerResponseContext response) {
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
	
	protected String getPath(ContainerRequestContext request) {
		if(request==null || request.getUriInfo()==null)
			return null;
		
		UriInfo uri = request.getUriInfo();
		String reqUrl = uri.getRequestUri()!=null ? uri.getRequestUri().toString() : null;
		if(reqUrl==null)
			return request.getUriInfo().getPath();
		
		String baseUrl = uri.getBaseUri()!=null ? uri.getBaseUri().toString() : null;
		return baseUrl!=null ? reqUrl.substring(baseUrl.length()) : reqUrl;
	}
	
	protected String getUserInfo(ContainerRequestContext request) {
		if(request==null)
			return "";
		String header = getAuthorizationHeader(request);
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

	protected String getAuthorizationHeader(ContainerRequestContext request) {
		return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	}
	
	protected String getRequestBody(ContainerRequestContext request) {
		if(request==null)
			return "";
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = request.getEntityStream();
		try {
			ReaderWriter.writeTo(in, out);
			byte[] buff = out.toByteArray();
			request.setEntityStream(new ByteArrayInputStream(buff));
			return buff.length == 0 ? "" : new String(buff).replaceAll("\\n", "");
		} catch (Exception e) {
			String err = String.format("Failed to get request body. (%s: %s)", e.getClass().getName(), e.getMessage());
			logger.error(err, e);
			return err;
		}
    }
	
	protected String getResponseBody(ContainerResponseContext response) {
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
