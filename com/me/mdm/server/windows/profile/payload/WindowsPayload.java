package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AddRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.DeleteRequestCommand;

public class WindowsPayload
{
    public static final String INSTALL_CONFIG_PAYLOAD = "InstallConfigPayload";
    public static final String REMOVE_CONFIG_PAYLOAD = "RemoveConfigPayload";
    public static final String INSTALL_APP_SILENT_PAYLOAD = "InstallAppSilentPayload";
    public static final String UPDATE_CONFIG_PAYLOAD = "UpdateConfigPayload";
    private DeleteRequestCommand nonAtomicDeleteReqCommand;
    private ReplaceRequestCommand replaceReqCommand;
    private DeleteRequestCommand delReqCommand;
    private AddRequestCommand addRequestCommand;
    private ExecRequestCommand execRequestCommand;
    private String payloadName;
    
    public WindowsPayload() {
        this.nonAtomicDeleteReqCommand = null;
        this.replaceReqCommand = null;
        this.delReqCommand = null;
        this.addRequestCommand = null;
        this.execRequestCommand = null;
        this.payloadName = null;
        this.nonAtomicDeleteReqCommand = new DeleteRequestCommand();
        this.delReqCommand = new DeleteRequestCommand();
        this.replaceReqCommand = new ReplaceRequestCommand();
        this.addRequestCommand = new AddRequestCommand();
        this.execRequestCommand = new ExecRequestCommand();
    }
    
    public void setPayloadType(final String payloadName) {
        this.payloadName = payloadName;
    }
    
    public String getPayloadType() {
        return this.payloadName;
    }
    
    public ReplaceRequestCommand getReplacePayloadCommand() {
        return this.replaceReqCommand;
    }
    
    public DeleteRequestCommand getDeletePayloadCommand() {
        return this.delReqCommand;
    }
    
    public DeleteRequestCommand getNonAtomicDeletePayloadCommand() {
        return this.nonAtomicDeleteReqCommand;
    }
    
    public AddRequestCommand getAddPayloadCommand() {
        return this.addRequestCommand;
    }
    
    public ExecRequestCommand getExecPayloadCommand() {
        return this.execRequestCommand;
    }
    
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        return null;
    }
    
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        return null;
    }
    
    public WindowsConfigurationPayload getOSSpecificUpdatePayload(final WindowsConfigurationPayload winConfigPayload) {
        return null;
    }
    
    public void setCommandUUID(final String commandUUID) {
        this.getReplacePayloadCommand().setRequestCmdId(commandUUID + ";Type=Atomic");
        this.getAddPayloadCommand().setRequestCmdId(commandUUID + ";Type=Atomic");
        this.getDeletePayloadCommand().setRequestCmdId(commandUUID + ";Type=Atomic");
        this.getNonAtomicDeletePayloadCommand().setRequestCmdId(commandUUID + ";Type=NonAtomic");
    }
    
    public void addReplacePayloadCommand(final String locationUri, final String itemData, final String sMetaFormat) {
        final Item item = this.createCommandItemTagElement(locationUri, itemData, sMetaFormat);
        this.getReplacePayloadCommand().addRequestItem(item);
    }
    
    public void addReplacePayloadCommand(final String locationUri) {
        final Item item = this.createTargetItemTagElement(locationUri);
        this.getReplacePayloadCommand().addRequestItem(item);
    }
    
    public void addExecPayloadCommand(final String locationUri) {
        final Item item = this.createTargetItemTagElement(locationUri);
        this.getExecPayloadCommand().addRequestItem(item);
    }
    
    public void addAddPayloadCommand(final String locationUri, final String itemData, final String sMetaFormat) {
        final Item item = this.createCommandItemTagElement(locationUri, itemData, sMetaFormat);
        this.getAddPayloadCommand().addRequestItem(item);
    }
    
    public void addAddPayloadCommand(final String locationUri) {
        final Item item = this.createTargetItemTagElement(locationUri);
        this.getAddPayloadCommand().addRequestItem(item);
    }
    
    public Item createCommandItemTagElement(final String locationUri, final String itemData, final String sMetaFormat) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat(sMetaFormat);
        item.setMeta(meta);
        item.setData(itemData);
        return item;
    }
    
    public Item createCommandItemTagElemetWithoutMeta(final String locationUri, final String itemData) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        item.setData(itemData);
        return item;
    }
    
    public Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
