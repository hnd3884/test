package com.me.devicemanagement.framework.server.common;

import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;
import com.me.devicemanagement.framework.server.api.ADGeneralAPI;

public abstract class ADFrameworkImpl implements ADGeneralAPI
{
    @Override
    public abstract void generateMetaData(final Long p0, final String p1);
    
    @Override
    public abstract void updateHasManagedResourceInHash(final String p0, final Long p1, final Boolean p2);
    
    @Override
    public abstract Properties deleteManagedResource(final String p0);
    
    @Override
    public abstract void removeHasManagedResourceFromHash(final String p0, final Long p1);
    
    @Override
    public abstract void postAddDomainActions(final Properties p0, final Properties p1);
    
    @Override
    public abstract Criteria getManagedResourceCriteriaForPasswordChangedDomain();
    
    @Override
    public List getPasswordChangedManagedDomainNames(final Long customerID) throws SyMException {
        return SoMADUtil.getInstance().getPasswordChangedManagedDomainNames(customerID);
    }
    
    @Override
    public Criteria getCriteriaForDomainWithoutCredential() {
        return SoMADUtil.getInstance().getCriteriaForDomainWithoutCredential();
    }
}
