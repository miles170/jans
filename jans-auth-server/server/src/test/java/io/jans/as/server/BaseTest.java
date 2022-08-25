/*
 * Janssen Project software is available under the Apache License (2004). See http://www.apache.org/licenses/ for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.as.server;

import io.jans.as.model.config.Conf;
import io.jans.as.model.config.Constants;
import io.jans.as.model.jwk.JSONWebKey;
import io.jans.as.model.util.StringUtils;
import io.jans.as.model.util.Util;
import io.jans.as.server.model.config.ConfigurationFactory;
import io.jans.as.server.util.TestUtil;
import io.jans.orm.PersistenceEntryManager;
import io.jans.service.cdi.util.CdiUtil;
import org.json.JSONObject;
import org.testng.Assert;

import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Javier Rojas
 * @author Yuriy Movchan Date: 10.10.2011
 */
public abstract class BaseTest extends ConfigurableTest {

    private static HashMap<String, String> HM_WEB_KEYS = new HashMap<>();
    private static String KEYSTORE_FILE;
    private static String KEYSTORE_SECRET;

    public static void showTitle(String title) {
        title = "TEST: " + title;

        System.out.println("#######################################################");
        System.out.println(title);
        System.out.println("#######################################################");
    }

    public void showResponse(String title, Response response) {
        showResponse(title, response, null);
    }

    public static void showResponse(String title, Response response, Object entity) {
        System.out.println(" ");
        System.out.println("RESPONSE FOR: " + title);
        System.out.println(response.getStatus());
        for (Entry<String, List<Object>> headers : response.getHeaders().entrySet()) {
            String headerName = headers.getKey();
            System.out.println(headerName + ": " + headers.getValue());
        }

        if (entity != null) {
            System.out.println(entity.toString().replace("\\n", "\n"));
        }
        System.out.println(" ");
        System.out.println("Status message: " + response.getStatus());
    }

    public static void fails(Throwable e) {
        Assert.fail(e.getMessage(), e);
    }

    public static void output(String msg) {
        System.out.println(msg);
    }

    public static String getApiTagetURL(URI uriArquillianTestServer) {
        if (TestUtil.testWithExternalApiUrl()) {
            return TestUtil.readExternalApiUrl();
        } else if (uriArquillianTestServer != null) {
            return uriArquillianTestServer.toString();
        } else {
            return null;
        }
    }

    public static URI getApiTagetURI(URI uriArquillianTestServer) {
        String url = getApiTagetURL(uriArquillianTestServer);
        if (url != null) {
            try {
                return new URI(url);
            } catch (Exception e) {
                System.out.println("Parsing URI getApiTagetURI : " + url + " - " + e.getMessage());
            }
        }
        return null;
    }

    public static String getApiTargetPath(URI uriArquillianTestServer, String endpointPath) {
        if (TestUtil.testWithExternalApiUrl()) {
            String url = TestUtil.readExternalApiUrl() + endpointPath;
            try {
                URI testURI = new URI(url);
                return testURI.getPath();
            } catch (Exception e) {
                System.out.println("Parsing URI getApiTargetPath : " + e.getMessage());
            }
        } else if (uriArquillianTestServer != null) {
            return uriArquillianTestServer.getPath();
        }
        return null;
    }

    public static String getWebKeyId(String keyAlg) {
        if (HM_WEB_KEYS.isEmpty()) {
            readWebKeys();
        }
        return HM_WEB_KEYS.get(keyAlg);
    }

    public static String getKeystoreFile() {
        if (Util.isNullOrEmpty(KEYSTORE_FILE)) {
            readKeystoreInfo();
        }
        return KEYSTORE_FILE;
    }

    public static String getKeystoreSecret() {
        if (Util.isNullOrEmpty(KEYSTORE_SECRET)) {
            readKeystoreInfo();
        }
        return KEYSTORE_SECRET;
    }

    public static void readWebKeys() {
        ConfigurationFactory configurationFactory = CdiUtil.bean(ConfigurationFactory.class);
        PersistenceEntryManager ldapEntryManager = CdiUtil.bean(PersistenceEntryManager.class);
        String dn = configurationFactory.getBaseConfiguration().getString(Constants.SERVER_KEY_OF_CONFIGURATION_ENTRY);
        Conf conf = ldapEntryManager.find(Conf.class, dn);
        for (JSONWebKey jwk : conf.getWebKeys().getKeys()) {
            String nameTestParam = jwk.getAlg().name().replaceAll("-", "_") + "_keyId";
            HM_WEB_KEYS.put(nameTestParam, jwk.getKid());
        }
    }

    public static void readKeystoreInfo() {
        ConfigurationFactory configurationFactory = CdiUtil.bean(ConfigurationFactory.class);
        PersistenceEntryManager ldapEntryManager = CdiUtil.bean(PersistenceEntryManager.class);
        String dn = configurationFactory.getBaseConfiguration().getString(Constants.SERVER_KEY_OF_CONFIGURATION_ENTRY);
        Conf conf = ldapEntryManager.find(Conf.class, dn);
        KEYSTORE_FILE = conf.getDynamic().getKeyStoreFile();
        KEYSTORE_SECRET = conf.getDynamic().getKeyStoreSecret();
    }

}