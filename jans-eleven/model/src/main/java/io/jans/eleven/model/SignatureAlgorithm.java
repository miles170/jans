/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.eleven.model;

/**
 * @author Javier Rojas Blum
 * @version July 20, 2022
 */
public interface SignatureAlgorithm {

    String NONE = "none";

    String HS256 = "HS256";
    String HS384 = "HS384";
    String HS512 = "HS512";

    String RS256 = "RS256";
    String RS384 = "RS384";
    String RS512 = "RS512";

    String ES256 = "ES256";
    String ES384 = "ES384";
    String ES512 = "ES512";

    String PS256 = "PS256";
    String PS384 = "PS384";
    String PS512 = "PS512";
}
