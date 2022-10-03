package com.adventnet.sym.server.mdm.certificates.scep;

import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class DynamicScepRemarks
{
    private static final Logger LOGGER;
    
    private DynamicScepRemarks() {
    }
    
    public static void updateFailureRemarks(final List<Long> resourceList, final long collectionId, final PasswordRequestStatus status) {
        try {
            DynamicScepRemarks.LOGGER.log(Level.INFO, "Updating failure remarks for resources: {0}", new Object[] { status.getRemarkString() });
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceList, collectionId, 7, status.getRemarkString());
        }
        catch (final DataAccessException e) {
            final String eMessage = "Exception while updating the collection Status for remark: " + status.getRemarkString() + ", collection id: " + collectionId;
            DynamicScepRemarks.LOGGER.log(Level.SEVERE, eMessage, (Throwable)e);
        }
    }
    
    public static void updateCustomRemarks(final long collectionId, final Map<Long, PasswordResponse> resourceToPasswordMap) {
        try {
            final PasswordRequestStatus[] passwordRequestStatuses = PasswordRequestStatus.values();
            final List<Long> unknownErroreDevices = new ArrayList<Long>(resourceToPasswordMap.keySet());
            DynamicScepRemarks.LOGGER.log(Level.INFO, "Updating custom failure remarks for resources: {0}, Collection: {1}", new Object[] { unknownErroreDevices, collectionId });
            for (final PasswordRequestStatus passwordRequestStatus : passwordRequestStatuses) {
                if (!passwordRequestStatus.getRemarkString().equals("mdm.scep.password_request_success")) {
                    DynamicScepRemarks.LOGGER.log(Level.INFO, "Getting resources matching the password request status: {0}, Collection: {1}", new Object[] { passwordRequestStatus, collectionId });
                    final List<Long> resourcesMatchingThisPasswordRequestStatus = getResourcesMatchingThisPasswordRequestStatus(resourceToPasswordMap, passwordRequestStatus);
                    if (!resourcesMatchingThisPasswordRequestStatus.isEmpty()) {
                        DynamicScepRemarks.LOGGER.log(Level.INFO, "Updating remarks for not applicable devices: {0}, Collection: {1}", new Object[] { passwordRequestStatus, collectionId });
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourcesMatchingThisPasswordRequestStatus, collectionId, 7, passwordRequestStatus.getRemarkString());
                        MDMCollectionStatusUpdate.getInstance().updateCollnToResListErrorCode(resourcesMatchingThisPasswordRequestStatus, collectionId, null);
                        unknownErroreDevices.removeAll(resourcesMatchingThisPasswordRequestStatus);
                    }
                }
            }
            DynamicScepRemarks.LOGGER.log(Level.INFO, "Updating unknown error remark for not applicable devices: {0}, Collection: {1}", new Object[] { unknownErroreDevices, collectionId });
            updateFailureRemarks(unknownErroreDevices, collectionId, PasswordRequestStatus.UNKNOWN_ERROR);
        }
        catch (final DataAccessException e) {
            final String eMessage = "Exception while updating the collection Status collection id: " + collectionId;
            DynamicScepRemarks.LOGGER.log(Level.SEVERE, eMessage, (Throwable)e);
        }
    }
    
    private static List<Long> getResourcesMatchingThisPasswordRequestStatus(final Map<Long, PasswordResponse> resourceToPasswordMap, final PasswordRequestStatus passwordRequestStatus) {
        final List<Long> resourcesMatchingThisStatus = new ArrayList<Long>();
        final Set<Map.Entry<Long, PasswordResponse>> passwordResponses = resourceToPasswordMap.entrySet();
        for (final Map.Entry<Long, PasswordResponse> passwordResponse : passwordResponses) {
            final PasswordResponse passwordResponseValue = passwordResponse.getValue();
            if (passwordResponseValue != null && passwordResponseValue.getPasswordRequestStatus() != null && passwordResponseValue.getPasswordRequestStatus() == passwordRequestStatus) {
                resourcesMatchingThisStatus.add(passwordResponse.getKey());
            }
        }
        DynamicScepRemarks.LOGGER.log(Level.INFO, "Resources matching the given password request status: {0}, Resources: {1}", new Object[] { passwordRequestStatus, resourcesMatchingThisStatus });
        return resourcesMatchingThisStatus;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
