package io.jans.ca.server.service;

import io.jans.ca.common.ExpiredObject;
import io.jans.ca.common.ExpiredObjectType;
import io.jans.ca.server.Utils;
import io.jans.ca.server.persistence.service.PersistenceService;
import io.jans.ca.server.persistence.service.PersistenceServiceImpl;
import io.jans.ca.server.service.auth.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author Yuriy Zabrovarnyy
 */
@ApplicationScoped
public class StateService {

    private static final Logger LOG = LoggerFactory.getLogger(StateService.class);
    @Inject
    PersistenceServiceImpl persistenceService;
    @Inject
    ConfigurationService configurationService;

    private final SecureRandom random = new SecureRandom();

    public String generateState() {
        return putState(generateSecureString());
    }

    public String generateNonce() {
        return putNonce(generateSecureString());
    }

    public String generateSecureString() {
        return new BigInteger(130, random).toString(32);
    }

    public boolean isExpiredObjectPresent(String key) {
        return persistenceService.isExpiredObjectPresent(key);
    }

    public void deleteExpiredObjectsByKey(String key) {
        persistenceService.deleteExpiredObjectsByKey(key);
    }

    public String putState(String state) {
        persistenceService.createExpiredObject(new ExpiredObject(state, state, ExpiredObjectType.STATE, configurationService.findConf().getDynamicConf().getStateExpirationInMinutes()));
        return state;
    }

    public String putNonce(String nonce) {
        persistenceService.createExpiredObject(new ExpiredObject(nonce, nonce, ExpiredObjectType.NONCE, configurationService.findConf().getDynamicConf().getNonceExpirationInMinutes()));
        return nonce;
    }

    public String encodeExpiredObject(String expiredObject, ExpiredObjectType type) throws UnsupportedEncodingException {
        if (type == ExpiredObjectType.STATE && configurationService.findConf().getDynamicConf().getEncodeStateFromRequestParameter()) {
            return Utils.encode(expiredObject);
        }

        if (type == ExpiredObjectType.NONCE && configurationService.findConf().getDynamicConf().getEncodeNonceFromRequestParameter()) {
            return Utils.encode(expiredObject);
        }

        return expiredObject;
    }
}
