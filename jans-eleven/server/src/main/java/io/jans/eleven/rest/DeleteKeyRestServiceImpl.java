/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.rest;

import com.google.common.base.Strings;
import io.jans.eleven.service.ConfigurationFactory;
import io.jans.eleven.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.security.PublicKey;

import static io.jans.eleven.model.DeleteKeyResponseParam.DELETED;

/**
 * @author Javier Rojas Blum
 * @version June 9, 2022
 */
@Path("/")
public class DeleteKeyRestServiceImpl implements DeleteKeyRestService {

    @Inject
    private Logger log;

    @Inject
    @Named("configurationFactory")
    private ConfigurationFactory configurationFactory;

    public Response deleteKey(String alias) {
        Response.ResponseBuilder builder = Response.ok();

        try {
            if (Strings.isNullOrEmpty(alias)) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the alias parameter is mandatory."
                ));
            } else {
                PublicKey publicKey = configurationFactory.getPkcs11Service().getPublicKey(alias);
                if (publicKey == null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(DELETED, false);

                    builder.entity(jsonObject.toString());
                } else {
                    configurationFactory.getPkcs11Service().deleteKey(alias);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(DELETED, true);

                    builder.entity(jsonObject.toString());
                }
            }
        } catch (Exception e) {
            builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            log.error(e.getMessage(), e);
        }

        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoTransform(false);
        cacheControl.setNoStore(true);
        builder.cacheControl(cacheControl);
        builder.header("Pragma", "no-cache");
        return builder.build();
    }
}
