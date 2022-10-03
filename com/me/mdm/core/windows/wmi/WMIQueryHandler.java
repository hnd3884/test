package com.me.mdm.core.windows.wmi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.mdm.framework.syncml.core.data.Location;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import org.json.JSONArray;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WMIQueryHandler
{
    private static WMIQueryHandler wmiQueryHandler;
    
    public static WMIQueryHandler getInstance() {
        if (WMIQueryHandler.wmiQueryHandler == null) {
            WMIQueryHandler.wmiQueryHandler = new WMIQueryHandler();
        }
        return WMIQueryHandler.wmiQueryHandler;
    }
    
    public WMIQuery getWMIQueryObject(final String wmiCommandName) {
        WMIQuery wmiQuery = null;
        if (wmiCommandName.contains("ComputerSystemProduct")) {
            wmiQuery = new ComputerSystemProductWMIQuery();
        }
        else if (wmiCommandName.contains("ComputerSystem")) {
            wmiQuery = new ComputerSystemWMIQuery();
        }
        else if (wmiCommandName.contains("NetworkAdapterConfig")) {
            wmiQuery = new NetworkAdapterConfigWMIQuery();
        }
        else if (wmiCommandName.contains("Bios")) {
            wmiQuery = new BiosWMIQuery();
        }
        return wmiQuery;
    }
    
    public SyncMLMessage getWMIInstanceQuery(final SyncMLMessage responseSyncML, final JSONObject jsonObject) throws Exception {
        final JSONArray wmiCommandNames = jsonObject.optJSONArray("WmiClasses");
        final String commandName = String.valueOf(jsonObject.get("commandName"));
        final SequenceRequestCommand seqRequestCommand = new SequenceRequestCommand();
        seqRequestCommand.setRequestCmdId(commandName);
        for (int wmiClassIndexLoop = 0; wmiCommandNames != null && wmiClassIndexLoop < wmiCommandNames.length(); ++wmiClassIndexLoop) {
            final String wmiCommandName = String.valueOf(wmiCommandNames.get(wmiClassIndexLoop));
            final WMIQuery wmiQuery = this.getWMIQueryObject(wmiCommandName);
            if (wmiQuery != null) {
                final GetRequestCommand wmiClassIntanceRequestCmd = new GetRequestCommand();
                wmiClassIntanceRequestCmd.setRequestCmdId("WmiQuery;" + wmiCommandName);
                wmiClassIntanceRequestCmd.addRequestItem(this.createCommandItemTagElement(wmiQuery.getFullyQualifiedWmiClassName()));
                seqRequestCommand.addRequestCmd(wmiClassIntanceRequestCmd);
            }
        }
        responseSyncML.getSyncBody().addRequestCmd(seqRequestCommand);
        return responseSyncML;
    }
    
    public SyncMLMessage getWMIInstancePropertiesQuery(final SyncMLMessage responseSyncML, final ResultsResponseCommand resultsResponseCommand) throws Exception {
        final GetRequestCommand wmiClassInstanceCommand = this.getWMIInstancePropertyCommand(resultsResponseCommand);
        responseSyncML.getSyncBody().addRequestCmd(wmiClassInstanceCommand);
        return responseSyncML;
    }
    
    private GetRequestCommand getWMIInstancePropertyCommand(final ResultsResponseCommand resultsResponseCommand) throws Exception {
        final WMIQuery wmiQuery = this.getWMIQueryObject(resultsResponseCommand.getCmdRef());
        final Item responseItem = resultsResponseCommand.getResponseItems().get(0);
        final GetRequestCommand wmiInstancePropsCommand = new GetRequestCommand();
        wmiInstancePropsCommand.setRequestCmdId("WmiInstancePropsQuery;" + wmiQuery.getWmiCommandName());
        if (responseItem.getSource().getLocUri().contains(wmiQuery.getWmiClassName())) {
            final String[] wmiInstances = responseItem.getData().toString().split("/");
            final List<String> wmiChildProperties = wmiQuery.getWmiClassProperties();
            for (final String wmiInstance : wmiInstances) {
                for (final String wmiChildProperty : wmiChildProperties) {
                    final String locationUri = wmiQuery.getFullyQualifiedWmiClassName() + "/" + this.getEncodedInstanceName(wmiInstance) + "/" + wmiChildProperty;
                    wmiInstancePropsCommand.addRequestItem(this.createCommandItemTagElement(locationUri));
                }
            }
            wmiQuery.modifyChildPropertyQuery(wmiInstancePropsCommand, wmiInstances);
        }
        return wmiInstancePropsCommand;
    }
    
    private Item createCommandItemTagElement(final String sLocationURI) {
        return this.createCommandItemTagElement(sLocationURI, null);
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        if (sValue != null) {
            commandItem.setData(sValue);
        }
        return commandItem;
    }
    
    private String getEncodedInstanceName(String wmiInstance) throws UnsupportedEncodingException {
        wmiInstance = URLEncoder.encode(wmiInstance, "UTF-8");
        wmiInstance = wmiInstance.replaceAll("\\+", "%20");
        return wmiInstance;
    }
    
    static {
        WMIQueryHandler.wmiQueryHandler = null;
    }
}
