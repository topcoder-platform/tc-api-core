package com.appirio.tech.core.auth;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.server.model.AnnotatedMethod;

import com.appirio.tech.core.api.v3.request.annotation.AllowAnonymous;

public class AllowAnonymousFeature implements DynamicFeature {

    private final ContainerRequestFilter authFilter;

    public AllowAnonymousFeature(ContainerRequestFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        final AnnotatedMethod am = new AnnotatedMethod(resourceInfo.getResourceMethod());
        if (am.isAnnotationPresent(AllowAnonymous.class)) {
            context.register(authFilter);
        }
    }

}
