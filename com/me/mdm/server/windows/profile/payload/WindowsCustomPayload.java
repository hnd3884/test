package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;

public class WindowsCustomPayload extends WindowsPayload
{
    public static final int ADD_COMMAND = 0;
    public static final int REPLACE_COMMAND = 1;
    public static final int DELETE_COMMAND = 2;
    public static final int EXEC_COMMAND = 3;
    public static final int TYPE_INT = 0;
    public static final int TYPE_FLOAT = 1;
    public static final int TYPE_BOOL = 2;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_XML = 4;
    public static final int TYPE_B64 = 5;
    
    public void addNonAtomicDelete(final String locUri) {
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locUri));
    }
    
    public void addCommand(final String locUri, final int actionType, final int dataType, final String data) {
        if (actionType == 2) {
            this.addCommand(locUri, actionType);
            return;
        }
        if (actionType == 3 && (MDMStringUtils.isEmpty(data) || dataType == -1)) {
            this.addCommand(locUri, actionType);
            return;
        }
        this.getAppropriateRequestCommand(actionType).addRequestItem(this.createCommandItemTagElement(locUri, data, this.getAppropriateMeta(dataType)));
    }
    
    private void addCommand(final String locUri, final int actionType) {
        this.getAppropriateRequestCommand(actionType).addRequestItem(this.createTargetItemTagElement(locUri));
    }
    
    private SyncMLRequestCommand getAppropriateRequestCommand(final int actionType) {
        switch (actionType) {
            case 0: {
                return this.getAddPayloadCommand();
            }
            case 1: {
                return this.getReplacePayloadCommand();
            }
            case 2: {
                return this.getDeletePayloadCommand();
            }
            case 3: {
                return this.getExecPayloadCommand();
            }
            default: {
                return null;
            }
        }
    }
    
    private String getAppropriateMeta(final int dataType) {
        switch (dataType) {
            case 0: {
                return "int";
            }
            case 1: {
                return "float";
            }
            case 2: {
                return "bool";
            }
            case 4: {
                return "xml";
            }
            case 5: {
                return "b64";
            }
            default: {
                return "chr";
            }
        }
    }
}
