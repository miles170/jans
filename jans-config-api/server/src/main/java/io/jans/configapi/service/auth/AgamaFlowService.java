
package io.jans.configapi.service.auth;

import io.jans.agama.model.Flow;
import static io.jans.as.model.util.Util.escapeLog;
import io.jans.orm.PersistenceEntryManager;
import io.jans.orm.search.filter.Filter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;

@ApplicationScoped
public class AgamaFlowService implements Serializable {

    private static final long serialVersionUID = 7912416439116338984L;

    private static final String AGAMA_BASE = "ou=agama,o=jans";
    public static final String AGAMA_FLOWS_BASE = "ou=flows," + AGAMA_BASE;

    @Inject
    private transient Logger logger;

    @Inject
    private transient PersistenceEntryManager persistenceEntryManager;

    public Flow getClientByInum(String inum) {
        Flow result = null;
        try {
            result = persistenceEntryManager.find(Flow.class, getFlowByDn(inum));
        } catch (Exception ex) {
            logger.error("Failed to load client entry", ex);
        }
        return result;
    }

    public List<Flow> searchAgamaFlows(String pattern, int sizeLimit) {
        if (logger.isDebugEnabled()) {
            logger.debug("Search Agama Flow with pattern:{}, sizeLimit:{} ", escapeLog(pattern), escapeLog(sizeLimit));
        }

        String[] targetArray = new String[] { pattern };
        Filter searchFilter = Filter.createORFilter(
                Filter.createSubstringFilter(Flow.ATTR_NAMES.QNAME, null, targetArray, null),
                Filter.createSubstringFilter(Flow.ATTR_NAMES.META, null, targetArray, null));

        logger.debug("Agama Flows with matching searchFilter:{}", searchFilter);
        return persistenceEntryManager.findEntries(getAgamaFlowDn(), Flow.class, searchFilter, sizeLimit);
    }

    public List<Flow> searchAgamaFlows(String pattern, int sizeLimit, boolean enabled) {
        if (logger.isDebugEnabled()) {
            logger.debug("Search Agama Flow with pattern:{}, sizeLimit:{}, enabled:{} ", escapeLog(pattern),
                    escapeLog(sizeLimit), escapeLog(enabled));
        }

        String[] targetArray = new String[] { pattern };
        Filter searchFilter = Filter.createORFilter(
                Filter.createSubstringFilter(Flow.ATTR_NAMES.QNAME, null, targetArray, null),
                Filter.createSubstringFilter(Flow.ATTR_NAMES.META, null, targetArray, null),
                Filter.createEqualityFilter("jansEnabled", enabled));

        logger.debug("Agama Flows with searchFilter:{}", searchFilter);
        return persistenceEntryManager.findEntries(getAgamaFlowDn(), Flow.class, searchFilter, sizeLimit);

    }

    public List<Flow> getAllAgamaFlows(int sizeLimit) {
        return persistenceEntryManager.findEntries(getAgamaFlowDn(), Flow.class, null, sizeLimit);
    }

    public List<Flow> getAllFlows() {
        return persistenceEntryManager.findEntries(getAgamaFlowDn(), Flow.class, null);
    }

    public Flow getFlowByName(String flowName) {
        List<Flow> flows = persistenceEntryManager.findEntries(getAgamaFlowDn(), Flow.class,
                Filter.createEqualityFilter(Flow.ATTR_NAMES.QNAME, flowName), 1);
        logger.debug("Agama Flow with flowName:{} flows:{}", flowName, flows);
        if (!flows.isEmpty()) {
            return flows.get(0);
        }
        return null;
    }

    public Flow getFlowByDn(String dn) {
        try {
            return persistenceEntryManager.find(Flow.class, dn);
        } catch (Exception e) {
            logger.warn("", e);
            return null;
        }
    }

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

    public String getAgamaFlowDn() {
        return AGAMA_FLOWS_BASE;
    }

}