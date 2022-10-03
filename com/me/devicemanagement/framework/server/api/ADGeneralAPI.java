package com.me.devicemanagement.framework.server.api;

import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;

public interface ADGeneralAPI
{
    void generateMetaData(final Long p0, final String p1);
    
    void updateHasManagedResourceInHash(final String p0, final Long p1, final Boolean p2);
    
    void postAddDomainActions(final Properties p0, final Properties p1);
    
    Properties deleteManagedResource(final String p0);
    
    void removeHasManagedResourceFromHash(final String p0, final Long p1);
    
    Criteria getManagedResourceCriteriaForPasswordChangedDomain();
    
    List getPasswordChangedManagedDomainNames(final Long p0) throws SyMException;
    
    Criteria getCriteriaForDomainWithoutCredential();
}
