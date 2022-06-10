/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.rest;

import io.jans.eleven.model.GenerateKeyResponseParam;
import io.jans.eleven.model.SignatureAlgorithm;
import io.jans.eleven.model.SignatureAlgorithmFamily;
import io.jans.eleven.service.ConfigurationFactory;
import io.jans.eleven.util.Base64Util;
import io.jans.eleven.util.StringUtils;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Javier Rojas Blum
 * @version June 9, 2022
 */
@Path("/")
public class GenerateKeyRestServiceImpl implements GenerateKeyRestService {

    @Inject
    private Logger log;

    @Inject
    @Named("configurationFactory")
    private ConfigurationFactory configurationFactory;

    public Response generateKey(String sigAlg, Long expirationTime) {
        Response.ResponseBuilder builder = Response.ok();

        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.fromName(sigAlg);

            if (signatureAlgorithm == null) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the server does not support the provided signatureAlgorithm parameter."
                ));
            } else if (expirationTime == null) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The request asked for an operation that cannot be supported because the expiration time parameter is mandatory."
                ));
            } else if (signatureAlgorithm == SignatureAlgorithm.NONE || signatureAlgorithm.getFamily().equals(SignatureAlgorithmFamily.HMAC)) {
                builder = Response.status(Response.Status.BAD_REQUEST);
                builder.entity(StringUtils.getErrorResponse(
                        "invalid_request",
                        "The provided signature algorithm parameter is not supported."
                ));
            } else {
                String dnName = configurationFactory.getConfiguration().getDnName();
                String alias = configurationFactory.getPkcs11Service().generateKey(dnName, signatureAlgorithm, expirationTime);
                PublicKey publicKey = configurationFactory.getPkcs11Service().getPublicKey(alias);
                Certificate certificate = configurationFactory.getPkcs11Service().getCertificate(alias);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(GenerateKeyResponseParam.KEY_ID, alias);
                jsonObject.put(GenerateKeyResponseParam.KEY_TYPE, signatureAlgorithm.getFamily());
                jsonObject.put(GenerateKeyResponseParam.KEY_USE, "sig");
                jsonObject.put(GenerateKeyResponseParam.ALGORITHM, signatureAlgorithm.getName());
                jsonObject.put(GenerateKeyResponseParam.EXPIRATION_TIME, expirationTime);
                if (SignatureAlgorithmFamily.RSA.equals(signatureAlgorithm.getFamily())) {
                    RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
                    jsonObject.put(GenerateKeyResponseParam.MODULUS, Base64Util.base64UrlEncode(rsaPublicKey.getModulus().toByteArray()));
                    jsonObject.put(GenerateKeyResponseParam.EXPONENT, Base64Util.base64UrlEncode(rsaPublicKey.getPublicExponent().toByteArray()));
                } else if (SignatureAlgorithmFamily.EC.equals(signatureAlgorithm.getFamily())) {
                    ECPublicKey ecPublicKey = (ECPublicKey) publicKey;
                    jsonObject.put(GenerateKeyResponseParam.CURVE, signatureAlgorithm.getCurve());
                    jsonObject.put(GenerateKeyResponseParam.X, Base64Util.base64UrlEncode(ecPublicKey.getW().getAffineX().toByteArray()));
                    jsonObject.put(GenerateKeyResponseParam.Y, Base64Util.base64UrlEncode(ecPublicKey.getW().getAffineY().toByteArray()));
                }
                JSONArray x5c = new JSONArray();
                x5c.put(Base64.encodeBase64String(certificate.getEncoded()));
                jsonObject.put(GenerateKeyResponseParam.CERTIFICATE_CHAIN, x5c);

                builder.entity(jsonObject.toString());
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
