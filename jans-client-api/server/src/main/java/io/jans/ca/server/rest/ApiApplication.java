/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.ca.server.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApiApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<Class<?>>();
                // General
        classes.add(ApiHealthCheck.class);
        classes.add(RpJwksResource.class);
        classes.add(GetDiscoveryResource.class);
        classes.add(SiteResource.class);
        classes.add(GetClientTokenResource.class);

        return classes;
    }
}
