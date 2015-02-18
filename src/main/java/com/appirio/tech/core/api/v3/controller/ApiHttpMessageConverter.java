/**
 * 
 */
package com.appirio.tech.core.api.v3.controller;

import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

import com.appirio.tech.core.api.v3.request.FieldSelector;
import com.appirio.tech.core.api.v3.response.ApiFieldSelectorResponse;

/**
 * Custom {@link HttpMessageConverter} to handle /api/v2 request and response format.
 * see api spec doc for details on format.
 * 
 * @author sudo
 *
 */
public class ApiHttpMessageConverter extends MappingJacksonHttpMessageConverter {

	/**
	 * Override method to register api response.
	 * The response JSON will only have properties specified in {@link FieldSelector}
	 * 
	 */
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		if((object instanceof ApiFieldSelectorResponse)) {
			JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
			JsonGenerator jsonGenerator =
					this.getObjectMapper().getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
			
			ApiBeanSerializeFilter customFilter = new ApiBeanSerializeFilter();
			customFilter.setObjectFieldSerializeMap(((ApiFieldSelectorResponse)object).getFieldSelectionMap());
			FilterProvider filters = new SimpleFilterProvider().addFilter("ApiResponseFilter",
					customFilter);
			try {
				this.getObjectMapper().writer(filters).writeValue(jsonGenerator, object);
			}
			catch (JsonProcessingException ex) {
				throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
			}
		} else {
			super.writeInternal(object, outputMessage);
		}
	}

}
