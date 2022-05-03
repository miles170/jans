package io.jans.ca.server.persistence.service;

import io.jans.ca.common.ExpiredObject;
import io.jans.ca.server.configuration.model.ApiConf;
import io.jans.ca.server.configuration.model.Rp;
import io.jans.ca.server.persistence.providers.H2PersistenceProvider;
import io.jans.ca.server.persistence.providers.SqlPersistenceProvider;
import io.jans.ca.server.service.auth.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;

/**
 * @author Yuriy Zabrovarnyy
 */
@ApplicationScoped
public class PersistenceServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceServiceImpl.class);
    @Inject
    ConfigurationService configurationService;
    private SqlPersistenceProvider sqlProvider;
    private PersistenceService persistenceService;

    public void create() {
        persistenceService = createServiceInstance();
        persistenceService.create();
    }

    private PersistenceService getPersistenceService() {
        if (persistenceService == null) {
            create();
        }
        return persistenceService;
    }

    private PersistenceService createServiceInstance() {
        ApiConf apiConf = this.configurationService.findConf();
        String storage = apiConf.getDynamicConf().getStorage();
        //Only jans_server persistence and h2(local file db) are supported
        switch (storage) {
            case "jans_server_configuration":
                return new JansPersistenceService(apiConf.getDynamicConf());
            case "h2":
                this.sqlProvider = new H2PersistenceProvider(this.configurationService);
                return new SqlPersistenceServiceImpl(this.sqlProvider, this.configurationService);
//            case "redis":
//                return new RedisPersistenceService(apiConf.getDynamicConf());
//            case "couchbase":
//                return new JansPersistenceService(apiConf.getDynamicConf(), storage);
        }
        throw new RuntimeException("Failed to create persistence provider. Unrecognized storage specified: " + storage + ", full configuration: " + this.configurationService.findConf());
    }

    public boolean create(Rp rp) {
        return getPersistenceService().create(rp);
    }

    public boolean createExpiredObject(ExpiredObject obj) {
        return getPersistenceService().createExpiredObject(obj);
    }

    public ExpiredObject getExpiredObject(String key) {
        return getPersistenceService().getExpiredObject(key);
    }

    public boolean isExpiredObjectPresent(String key) {
        return getPersistenceService().isExpiredObjectPresent(key);
    }

    public boolean update(Rp rp) {
        return getPersistenceService().update(rp);
    }

    public Rp getRp(String rpId) {
        return getPersistenceService().getRp(rpId);
    }

    public boolean removeAllRps() {
        return getPersistenceService().removeAllRps();
    }

    public Set<Rp> getRps() {
        return getPersistenceService().getRps();
    }

    public boolean deleteExpiredObjectsByKey(String key) {
        return getPersistenceService().deleteExpiredObjectsByKey(key);
    }

    public boolean deleteAllExpiredObjects() {
        return getPersistenceService().deleteAllExpiredObjects();
    }

    public void destroy() {
        getPersistenceService().destroy();
    }

    public boolean remove(String rpId) {
        return getPersistenceService().remove(rpId);
    }
}
