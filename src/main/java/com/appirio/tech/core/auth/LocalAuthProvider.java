package com.appirio.tech.core.auth;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jsymons
 * @version 1.0
 * @since 6/13/15
 */
public class LocalAuthProvider implements InjectableProvider<Auth, Parameter> {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthProvider.class);
    private AuthUser authUser;

    public LocalAuthProvider(AuthUser authUser) {
        this.authUser = authUser;
    }

    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    public Injectable<?> getInjectable(ComponentContext ic, Auth a, com.sun.jersey.api.model.Parameter c) {
        return new LocalAuthProvider.LocalAuthInjectable(authUser);
    }

    protected static class LocalAuthInjectable extends AbstractHttpContextInjectable<AuthUser> {
        private AuthUser authUser;

        protected LocalAuthInjectable(AuthUser authUser) {
            this.authUser = authUser;
        }

        public AuthUser getValue(HttpContext c) {
            return authUser;
        }
    }
}
