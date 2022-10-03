package com.adventnet.sym.server.mdm.certificates.scep;

import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMCollectionNotApplicableListener;

public class DynamicScepNotApplicableHandler implements MDMCollectionNotApplicableListener
{
    Logger logger;
    
    public DynamicScepNotApplicableHandler() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public List<Long> getNotApplicableDeviceList(final List resources, final Long collectionId, final List configIds, final long customerId) {
        final List<Long> resourceList = new ArrayList<Long>(resources);
        final List<Long> notApplicableResources = new ArrayList<Long>();
        Set<Long> scepIds;
        try {
            final Set<Long> identityCerts = IdentityCertificateDBHandler.getIdentityCertsBelongingToCollection(collectionId);
            scepIds = IdentityCertificateDBHandler.filterScepIds(identityCerts);
        }
        catch (final Exception e) {
            final String exMessage = "Exception while getting scep ids for the collection: " + collectionId;
            this.logger.log(Level.SEVERE, exMessage, e);
            return new ArrayList<Long>();
        }
        try {
            this.logger.log(Level.INFO, "Getting not applicable devices for dynamic scep: Collection {0}, resources : {1}", new Object[] { collectionId, resources });
            for (final Long scepId : scepIds) {
                this.logger.log(Level.INFO, "Getting password for scep : {0}", new Object[] { scepId });
                final ScepServer scepServer = DynamicScepServer.getScepServerForScepId(scepId);
                if (scepServer != null && DynamicScepServer.isPasscodeRetrievalAllowed(scepServer)) {
                    this.logger.log(Level.INFO, "Scep server is eligible for getting passwords. scep : {0}", new Object[] { scepId });
                    final List<Long> resourcesAllowedForThisScep = new ArrayList<Long>(resourceList);
                    resourcesAllowedForThisScep.removeAll(notApplicableResources);
                    this.logger.log(Level.INFO, "Resources allowed for this scep : {0}, Resources: {1}", new Object[] { scepId, resourcesAllowedForThisScep });
                    final List<Long> notApplicableResourcesOfThisScep = DynamicScepServer.obtainAndStorePasswords(collectionId, resourcesAllowedForThisScep, scepId, scepServer);
                    this.logger.log(Level.INFO, "Not applicable resources for this scep : {0}, Resources: {1}", new Object[] { scepId, notApplicableResourcesOfThisScep });
                    notApplicableResources.addAll(notApplicableResourcesOfThisScep);
                }
            }
            this.logger.log(Level.INFO, "Overall Not applicable resources : {0}, collection: {1}", new Object[] { notApplicableResources, collectionId });
            return notApplicableResources;
        }
        catch (final DataAccessException e2) {
            final String eMessage = "Exception in dynamic scep not applicable handler: " + collectionId;
            this.logger.log(Level.SEVERE, eMessage, (Throwable)e2);
            DynamicScepRemarks.updateFailureRemarks(resourceList, collectionId, PasswordRequestStatus.UNKNOWN_ERROR);
            return resourceList;
        }
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
    }
}
