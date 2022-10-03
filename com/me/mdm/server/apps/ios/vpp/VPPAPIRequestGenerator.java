package com.me.mdm.server.apps.ios.vpp;

import java.util.List;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppAssignCommand;
import java.util.logging.Logger;

public class VPPAPIRequestGenerator
{
    public Logger logger;
    public String sToken;
    private static VPPAPIRequestGenerator vppapiRequestGenerator;
    
    public VPPAPIRequestGenerator(final String sToken) {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
        this.sToken = null;
        this.sToken = sToken;
    }
    
    public String getVPPClientConfigCommand() throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getVPPClientConfigCommand(final String clientContextString) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        if (clientContextString != null) {
            command.setClientContext(clientContextString);
        }
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getVPPAssetsCommand() throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setIncludeLicenseCounts(true);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getVPPLicenseCommand(final Integer appStoreId, final Long customerId, final String batchToken, final String sinceModifiedToken, final Long businessStoreID, final Boolean assignedOnly) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        if (appStoreId != null) {
            command.setAdamId(appStoreId);
        }
        if (batchToken != null) {
            command.setBatchToken(batchToken);
        }
        if (sinceModifiedToken != null) {
            command.setSinceModifiedToken(sinceModifiedToken);
        }
        command.setAssignedOnlyKey(assignedOnly);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getDisassociateAdamIdFromDevicesCommand(final Long businessStoreID, final int adamId, final String pricingParam, final List serialNumbers, final Boolean notifyDisassociation) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setAdamIdStr(adamId);
        command.setPricingParam(pricingParam);
        command.setDeviceDisassociations(serialNumbers);
        command.setNotifyDisassociations(notifyDisassociation);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getDisassociateAdamIdFromUsersCommand(final Long businessStoreID, final int adamId, final String pricingParam, final List clientUserIds, final Boolean notifyDisassociation) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setAdamIdStr(adamId);
        command.setPricingParam(pricingParam);
        command.setUserDisassociations(clientUserIds);
        command.setNotifyDisassociations(notifyDisassociation);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getRegisterUserCommand(final String clientUserIdStr, final Long customerId) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setClientUserIdStr(clientUserIdStr);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getAssociateAdamIdToUsersCommand(final Long customerId, final int adamId, final String pricingParam, final List userIds) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setAdamIdStr(adamId);
        command.setPricingParam(pricingParam);
        command.setUserAssociations(userIds);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getAssociateAdamIdToDevicesCommand(final Long customerId, final int adamId, final String pricingParam, final List serialNumbers) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setAdamIdStr(adamId);
        command.setPricingParam(pricingParam);
        command.setDeviceAssociations(serialNumbers);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getRetireUserCommand(final Long vppUserId) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setUserId(vppUserId);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getVPPUsersCommand(final String batchToken, final String sinceModifiedToken) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        if (batchToken != null) {
            command.setBatchToken(batchToken);
        }
        if (sinceModifiedToken != null) {
            command.setSinceModifiedToken(sinceModifiedToken);
        }
        requestCommand = command.toString();
        return requestCommand;
    }
    
    public String getVPPUserCommand(final Long vppUserId) throws JSONException {
        String requestCommand = "";
        final VPPAppAssignCommand command = new VPPAppAssignCommand();
        command.setSToken(this.sToken);
        command.setUserId(vppUserId);
        requestCommand = command.toString();
        return requestCommand;
    }
    
    static {
        VPPAPIRequestGenerator.vppapiRequestGenerator = null;
    }
}
