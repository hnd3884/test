package com.me.devicemanagement.onpremise.server.admin;

import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.onpremise.server.util.SoMADUtil;
import com.me.devicemanagement.framework.server.admin.SoMEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.admin.DomainListener;

public class MessageDomainListener implements DomainListener
{
    private static Logger out;
    
    public void domainsAdded(final SoMEvent[] somEventArr) {
        this.setSoMMsg(somEventArr);
    }
    
    public void domainsDeleted(final SoMEvent[] somEventArr) {
        this.setSoMMsg(somEventArr);
    }
    
    public void domainsUpdated(final SoMEvent[] somEventArr) {
        this.setSoMMsg(somEventArr);
    }
    
    public void domainsManaged(final SoMEvent[] somEventArr) {
        this.setSoMMsg(somEventArr);
    }
    
    public void domainsNotManaged(final SoMEvent[] somEventArr) {
        this.setSoMMsg(somEventArr);
    }
    
    private void setSoMMsg(final SoMEvent[] soMEvents) {
        this.setNoCredentialMsgStatus(soMEvents);
        this.setPasswordChangedMsg(soMEvents);
    }
    
    private void setNoCredentialMsgStatus(final SoMEvent[] soMEvents) {
        for (final SoMEvent soMEvent : soMEvents) {
            final Long customerID = soMEvent.customerID;
            try {
                final List domainWithOutCredential = SoMADUtil.getInstance().getManagedDomainNamesWithoutCredential(customerID);
                if (domainWithOutCredential.size() > 0) {
                    MessageProvider.getInstance().unhideMessage("NO_CREDENTIAL_SPECIFIED", customerID);
                }
                else {
                    MessageProvider.getInstance().hideMessage("NO_CREDENTIAL_SPECIFIED", customerID);
                }
            }
            catch (final SyMException ex) {
                MessageDomainListener.out.log(Level.SEVERE, "Exception while closing no credential message", (Throwable)ex);
            }
        }
    }
    
    private void setPasswordChangedMsg(final SoMEvent[] soMEvents) {
        for (final SoMEvent soMEvent : soMEvents) {
            try {
                final Long customerID = soMEvent.customerID;
                final List domainPwdChanged = SoMADUtil.getInstance().getPasswordChangedManagedDomainNames(customerID);
                if (domainPwdChanged.size() > 0) {
                    MessageProvider.getInstance().unhideMessage("DOMAIN_PASSWORD_CHANGED", customerID);
                }
                else {
                    MessageProvider.getInstance().hideMessage("DOMAIN_PASSWORD_CHANGED", customerID);
                }
            }
            catch (final SyMException ex) {
                MessageDomainListener.out.log(Level.SEVERE, "Exception while closing password changed message", (Throwable)ex);
            }
        }
    }
    
    static {
        MessageDomainListener.out = Logger.getLogger("SoMLogger");
    }
}
