package com.me.idps.core.sync.product;

import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.ArrayList;
import java.util.List;

public abstract class DirectoryProductAPI
{
    public abstract void handleProductOps(final DirProdImplRequest p0) throws Exception;
    
    public abstract void memberAdded(final DirProdImplRequest p0);
    
    public abstract void memberRemoved(final DirProdImplRequest p0);
    
    public abstract void userModified(final DirProdImplRequest p0);
    
    public abstract void groupModified(final DirProdImplRequest p0);
    
    public abstract void userDeleted(final DirProdImplRequest p0);
    
    public abstract void userActivated(final DirProdImplRequest p0);
    
    public abstract void userDirDisabled(final DirProdImplRequest p0);
    
    public abstract void userSyncDisabled(final DirProdImplRequest p0);
    
    public abstract void groupDeleted(final DirProdImplRequest p0);
    
    public abstract void groupActivated(final DirProdImplRequest p0);
    
    public abstract void groupDirDisabled(final DirProdImplRequest p0);
    
    public abstract void groupSyncDisabled(final DirProdImplRequest p0);
    
    public abstract void approveDomainDeletion(final DirProdImplRequest p0);
    
    public Object customHandling(final DirProdImplRequest dirProdImplRequest) throws Exception {
        return new Object();
    }
    
    public abstract void postSyncOPS(final DirProdImplRequest p0) throws Exception;
    
    public void resolveResourceDuplicates(final DirProdImplRequest dirProdImplRequest) throws Exception {
    }
    
    public void bindRes(final DirProdImplRequest dirProdImplRequest) throws Exception {
    }
    
    public void unBindDeletedRes(final DirProdImplRequest dirProdImplRequest) throws Exception {
    }
    
    public List<String> getProdSpecificMeTrackingKeys(final DirProdImplRequest dirProdImplRequest) {
        return new ArrayList<String>();
    }
    
    public String getDefulatCustPhoneNumber(final DirProdImplRequest dirProdImplRequest) throws SyMException {
        return "---";
    }
    
    public JSONObject getUserIDFdetails(final Long resourceID) throws Exception {
        return null;
    }
    
    public JSONObject doProdSpecificMEtracking(final Long customerID, final JSONObject input) throws Exception {
        return input;
    }
    
    public abstract Boolean isFeatureAvailable(final String p0);
    
    public abstract void updateFeatureAvailability(final String p0, final String p1);
    
    public abstract void throwExcepForErrResp(final DirProdImplRequest p0);
    
    public ArrayList<String> getAutoVAdisabledTables(final DirProdImplRequest dirProdImplRequest) {
        return null;
    }
    
    public void adjustSchedulerSpread(final DirProdImplRequest dirProdImplRequest) {
    }
    
    public void setADIntegConsent(final DirProdImplRequest dirProdImplRequest) {
    }
    
    public void handleUpgrade(final DirProdImplRequest dirProdImplRequest) throws Exception {
    }
}
