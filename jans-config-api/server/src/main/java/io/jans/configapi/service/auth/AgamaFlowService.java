
package io.jans.configapi.service.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;

import org.slf4j.Logger;

import io.jans.orm.PersistenceEntryManager;
import io.jans.util.StringHelper;
import io.jans.agama.model.Flow;

@ApplicationScoped
public class AgamaFlowService  implements Serializable {

    private static final long serialVersionUID = 7912416439116338984L;
    
    @Inject
    private transient Logger logger;

    @Inject
    private transient PersistenceEntryManager persistenceEntryManager;

    public void addAgamaFlow(Flow flow) {
        logger.error("Added Agama Flow:{}", flow);
        persistenceEntryManager.persist(flow);
    }
    
    public void updateClient(Flow flow) {
        logger.error("Update Agama Flow:{}", flow);
        persistenceEntryManager.merge(flow);
    }
    
    public void removeAgamaFlow(Flow flow) {
        logger.error("Remove Agama Flow:{}", flow);
        persistenceEntryManager.removeRecursively(flow.getDn(), Flow.class);
    }
    
    public String getDnForClient(String inum) {
        String orgDn = organizationService.getDnForOrganization();
        if (StringHelper.isEmpty(inum)) {
            return String.format("ou=clients,%s", orgDn);
        }
        return String.format("inum=%s,ou=clients,%s", inum, orgDn);
    }


}