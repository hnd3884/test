package com.me.mdm.onpremise.server.common;

import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import com.me.mdm.onpremise.server.admin.DomainUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.common.ADFrameworkImpl;

public class MDMADImpl extends ADFrameworkImpl
{
    private Logger logger;
    
    public MDMADImpl() {
        this.logger = Logger.getLogger("SoMLogger");
    }
    
    public void generateMetaData(final Long customerID, final String domainName) {
    }
    
    public void updateHasManagedResourceInHash(final String domainName, final Long customerID, final Boolean hasManagedComputers) {
    }
    
    public void postAddDomainActions(final Properties domainProps, final Properties addDomainProps) {
        try {
            if (!((Hashtable<K, Boolean>)addDomainProps).get("isDomainDetailsAvailableInDB")) {
                DomainUtil.getInstance();
                DomainUtil.writeSoMPropsInFile();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while creating custom groups...", ex);
        }
    }
    
    public Properties deleteManagedResource(final String domainName) {
        return null;
    }
    
    public void removeHasManagedResourceFromHash(final String domainName, final Long customerID) {
    }
    
    public Criteria getManagedResourceCriteriaForPasswordChangedDomain() {
        return null;
    }
}
