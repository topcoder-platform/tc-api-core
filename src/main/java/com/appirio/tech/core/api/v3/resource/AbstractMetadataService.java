/**
 * 
 */
package com.appirio.tech.core.api.v3.resource;

import javax.servlet.http.HttpServletRequest;

import com.appirio.tech.core.api.v3.metadata.Metadata;
import com.appirio.tech.core.api.v3.request.QueryParameter;
import com.appirio.tech.core.api.v3.resource.old.RESTMetadataService;

/**
 * @author sudo
 *
 */
abstract public class AbstractMetadataService implements RESTMetadataService {

	/* temporary removed for porting
	@Autowired
	private ApplicationContext context;
	*/

	/**
	 * Returns Metadata with default fields.
	 * Extend this method to return non-default Metadata information.
	 * @throws Exception 
	 */
	@Override
	public Metadata getMetadata(HttpServletRequest request, QueryParameter query) throws Exception {
		Metadata metadata = new Metadata();
		populateFieldInfo(metadata);
		return metadata;
	}

	/**
	 * This method looks into each of the response model object and populates field information based on annotation
	 * 
	 * @param metadata
	 * @throws Exception 
	 */
	protected void populateFieldInfo(Metadata metadata) throws Exception {
/*		ApiController controller = context.getBean(ApiController.class);
		Class<?> resource = controller.getResourceFactory().getResourceModel(getResourcePath());

		*//**
		 * Get all fields that is serialized via Jackson mapper.
		 * We would want to get our message converter bean from Spring's context, but as Spring
		 * won't inject converter, we'll create a new instance here. (ref: http://stackoverflow.com/questions/11970358)
		 *//*
		Set<String> visibleFields = new HashSet<String>();
		ApiHttpMessageConverter converter = new ApiHttpMessageConverter();
		JsonSchema schema = converter.getObjectMapper().generateJsonSchema(resource);
		JsonNode property = schema.getSchemaNode().findValue("properties");

		Iterator<String> i = property.getFieldNames();
		while(i.hasNext()) {
			visibleFields.add(i.next());
		}
		
		//Populate FieldInfo from Annotations
		Set<FieldInfo> fieldInfoSet = new HashSet<FieldInfo>();
		Method[] methods = resource.getMethods();
		for(Method method : methods) {
			String methodName = method.getName();
			if(methodName.startsWith("get") && method.getParameterTypes().length==0 &&
					visibleFields.contains(ResourceHelper.getApiFieldName(methodName))) {
				ApiMapping api = method.getAnnotation(ApiMapping.class);
				if(api==null || api.visible()) {
					FieldInfo info = new FieldInfo();
					info.setName(ResourceHelper.getApiFieldName(methodName));
					info.setType(method.getReturnType().getSimpleName());
					info.setDefault((api==null || api.queryDefault()) ? true : false);
					fieldInfoSet.add(info);
				}
			}
		}
		metadata.setFields(fieldInfoSet);*/
	}
}
