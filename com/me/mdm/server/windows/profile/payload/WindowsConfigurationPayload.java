package com.me.mdm.server.windows.profile.payload;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsConfigurationPayload extends WindowsBaseConfigPayload
{
    private String cfgPayloadType;
    
    public WindowsConfigurationPayload() {
        this.cfgPayloadType = null;
    }
    
    public void setConfigurationPayloadType(final String cfgPayloadType) {
        this.cfgPayloadType = cfgPayloadType;
    }
    
    public String getConfigurationPayloadType() {
        return this.cfgPayloadType;
    }
    
    public void setPayloadContent(final SyncMLRequestCommand payload) {
        this.setAtomicPayloadContent(payload);
    }
    
    public void setAtomicPayloadContent(final SyncMLRequestCommand command) {
        if (command != null && command.getRequestItems() != null && !command.getRequestItems().isEmpty()) {
            this.getAtomicPayloadContent().addRequestCmd(command);
        }
    }
    
    public void setNonAtomicPayloadContent(final SyncMLRequestCommand command) {
        if (command != null && command.getRequestItems() != null && !command.getRequestItems().isEmpty()) {
            if (this.getNonAtomicPayloadContent().getRequestItems() == null || this.getNonAtomicPayloadContent().getRequestItems().isEmpty()) {
                this.getNonAtomicPayloadContent().setRequestItems(command.getRequestItems());
            }
            else {
                final List<Item> requestItems = command.getRequestItems();
                for (final Item requestItem : requestItems) {
                    this.getNonAtomicPayloadContent().addRequestItem(requestItem);
                }
            }
        }
    }
    
    public void setCommandUUID(final String commandUUID) {
        this.getPayloadContent().setRequestCmdId(commandUUID + ";Type=Root");
        this.getAtomicPayloadContent().setRequestCmdId(commandUUID + ";Type=AtomicChild");
        this.getNonAtomicPayloadContent().setRequestCmdId(commandUUID + ";Type=NonAtomicChild");
    }
}
