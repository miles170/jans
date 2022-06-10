/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.rest;

import com.google.common.base.Strings;
import io.jans.eleven.model.SignRequestParam;
import io.jans.eleven.model.SignResponseParam;
import io.jans.eleven.model.SignatureAlgorithm;
import io.jans.eleven.model.SignatureAlgorithmFamily;
import io.jans.eleven.service.ConfigurationFactory;
import io.jans.eleven.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.security.InvalidKeyException;

/**
 * @author Javier Rojas Blum
 * @version June 9, 2022
 */
@Path("/")
public class SignRestServiceImpl implements SignRestService {

    @Inject
    private Logger log;

    @Inject
    @Named("configurationFactory")
    private ConfigurationFactory configurationFactory;

    public Response sign(SignRequestParam signRequestParam) {
        Response.ResponseBuilder builder = Response.ok();

        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.fromName(signRequestParam.getSignatureAlgorithm());

            if (Strings.isNullOrEmpty(signRequestParam.getSigningInput())) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the signingInput parameter is mandatory."
                ));
            } else if (signatureAlgorithm == null) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the server does not support the provided signatureAlgorithm parameter."
                ));
            } else if (signatureAlgorithm != SignatureAlgorithm.NONE
                    && SignatureAlgorithmFamily.HMAC.equals(signatureAlgorithm.getFamily())
                    && Strings.isNullOrEmpty(signRequestParam.getSharedSecret())) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the shared secret parameter is mandatory."
                ));
            } else if (signatureAlgorithm != SignatureAlgorithm.NONE
                    && !SignatureAlgorithmFamily.HMAC.equals(signatureAlgorithm.getFamily()) // EC or RSA
                    && Strings.isNullOrEmpty(signRequestParam.getAlias())) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the alias parameter is mandatory."
                ));
            } else {
                String signature = configurationFactory.getPkcs11Service().getSignature(signRequestParam.getSigningInput().getBytes(),
                        signRequestParam.getAlias(), signRequestParam.getSharedSecret(), signatureAlgorithm);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(SignResponseParam.SIGNATURE, signature);

                builder.entity(jsonObject.toString());
            }
        } catch (InvalidKeyException e) {
            builder = Response.status(Response.Status.BAD_REQUEST);
            builder.entity(StringUtils.getErrorResponse(
                    "invalid_request",
                    "Invalid key, either the alias or signatureAlgorithm parameter is not valid."
            ));
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
