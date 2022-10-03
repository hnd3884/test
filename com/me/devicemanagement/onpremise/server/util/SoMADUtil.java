package com.me.devicemanagement.onpremise.server.util;

import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import java.util.logging.Logger;

public class SoMADUtil extends com.me.devicemanagement.framework.server.util.SoMADUtil
{
    private Logger logger;
    private static SoMADUtil sUtil;
    
    protected SoMADUtil() {
        this.logger = Logger.getLogger("SoMLogger");
    }
    
    public static synchronized SoMADUtil getInstance() {
        if (SoMADUtil.sUtil == null) {
            SoMADUtil.sUtil = new SoMADUtil();
        }
        return SoMADUtil.sUtil;
    }
    
    public List getManagedDomainNamesWithoutCredential(final Long customerID) throws SyMException {
        Criteria criteria = ApiFactoryProvider.getADImpl().getCriteriaForDomainWithoutCredential();
        criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        return this.getManagedDomainNames(criteria);
    }
    
    public void setADAuthMsgStatus() {
        try {
            final List adDomainList = this.getADManagedDomainNames();
            if (adDomainList.isEmpty()) {
                MessageProvider.getInstance().unhideMessage("AD_CREDENTIAL_NOT_SPECIFIED");
            }
            else {
                MessageProvider.getInstance().hideMessage("AD_CREDENTIAL_NOT_SPECIFIED");
            }
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception while closing ad credential not specified message", (Throwable)ex);
        }
    }
    
    static {
        SoMADUtil.sUtil = null;
    }
}
