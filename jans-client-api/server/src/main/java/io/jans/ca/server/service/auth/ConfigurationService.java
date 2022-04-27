/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.ca.server.service.auth;

import io.jans.as.common.service.common.ApplicationFactory;
import io.jans.as.persistence.model.configuration.GluuConfiguration;
import io.jans.ca.server.configuration.ApiAppConfiguration;
import io.jans.ca.server.configuration.ConfigurationFactory;
import io.jans.ca.server.configuration.model.ApiConf;
import io.jans.configapi.model.status.StatsData;
import io.jans.orm.PersistenceEntryManager;
import io.jans.util.StringHelper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import static io.jans.ca.server.configuration.ConfigurationFactory.CONFIGURATION_ENTRY_DN;

/**
 * @author Yuriy Zabrovarnyy
 */
@ApplicationScoped
public class ConfigurationService {

    @Inject
    @Named(ApplicationFactory.PERSISTENCE_ENTRY_MANAGER_NAME)
    PersistenceEntryManager persistenceManager;

    @Inject
    ConfigurationFactory configurationFactory;
    
    private StatsData statsData;

    public ApiConf findConf() {
        final String dn = configurationFactory.getConfigurationDn(CONFIGURATION_ENTRY_DN);
        return persistenceManager.find(dn, ApiConf.class, null);
    }

    public void merge(ApiConf conf) {
        conf.setRevision(conf.getRevision() + 1);
        persistenceManager.merge(conf);
    }

    public void merge(GluuConfiguration conf) {
        persistenceManager.merge(conf);
    }

    public ApiAppConfiguration find() {
        final ApiConf conf = findConf();
        return conf.getDynamicConf();
    }

    public GluuConfiguration findGluuConfiguration() {
        String configurationDn = findConf().getStaticConf().getBaseDn().getConfiguration();
        if (StringHelper.isEmpty(configurationDn)) {
            return null;
        }
        return persistenceManager.find(GluuConfiguration.class, configurationDn);
    }

    public String getPersistenceType() {
        return configurationFactory.getBaseConfiguration().getString("persistence.type");
    }

    public StatsData getStatsData() {
        return statsData;
    }

    public void setStatsData(StatsData statsData) {
        this.statsData = statsData;
    }    
  
}
