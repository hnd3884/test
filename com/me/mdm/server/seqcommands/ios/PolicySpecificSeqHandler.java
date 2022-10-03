package com.me.mdm.server.seqcommands.ios;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.me.mdm.server.config.MDMConfigUtil;
import org.json.JSONArray;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class PolicySpecificSeqHandler
{
    public static final String SEQUENTIAL_CMD_ARRAY = "sequentialCmdArray";
    public static final String META_DATA_LIST = "metaDataList";
    public static final String COLLECTION = "collection";
    public static final String SECURITY = "security";
    public static final String SEQUENTIAL = "sequential";
    public static final String COLLECTION_ARRAY = "collectionArray";
    public static final String SECURITY_ARRAY = "securityArray";
    public static final String COMMAND_ARRAY = "commandArray";
    public static final String KEYLIST = "keylist";
    private static Logger logger;
    private static Logger configLogger;
    
    public JSONObject getPolicyCommand(final List keyList, final JSONObject policyCmdParams, final HashMap<String, String> classHashMap, final String[] priortizedPolicy) throws Exception {
        final JSONObject policyCmdJSON = new JSONObject();
        final JSONArray commandArray = new JSONArray();
        final JSONObject policySeqParams = new JSONObject();
        try {
            final Long collectionID = policyCmdParams.optLong("COLLECTION_ID");
            final Long customerId = policyCmdParams.optLong("CUSTOMER_ID");
            int installCommandOrder = policyCmdParams.optInt("order");
            final JSONObject params = policyCmdParams.optJSONObject("params");
            final String[] configArray = this.priotizePolicyCommands(keyList, priortizedPolicy);
            final List configDoList = MDMConfigUtil.getConfigurationDataItems(collectionID);
            final List metaDataList = new ArrayList();
            final JSONArray collectionArray = new JSONArray();
            final JSONArray securityCommandArray = new JSONArray();
            for (int i = 0; i < configArray.length; ++i) {
                final String key = configArray[i];
                final String className = classHashMap.get(key);
                if (!MDMStringUtils.isEmpty(className)) {
                    final PolicySpecificSeqCommand command = (PolicySpecificSeqCommand)Class.forName(className).newInstance();
                    PolicySpecificSeqHandler.configLogger.log(Level.INFO, "Getting the iOS policy specific sequential command for collection:{0} and configType:{1}", new Object[] { collectionID, key });
                    final JSONObject policyParams = new JSONObject();
                    policyParams.put("COLLECTION_ID", (Object)collectionID);
                    policyParams.put("CUSTOMER_ID", (Object)customerId);
                    policyParams.put("order", installCommandOrder);
                    policyParams.put("params", (Object)params);
                    policyParams.put("keylist", (Object)keyList);
                    final JSONObject seqCmdJSON = command.getSequentialCommandForPolicy(configDoList, policyParams);
                    installCommandOrder = this.processPolicySpecificJSON(seqCmdJSON, metaDataList, collectionArray, securityCommandArray, commandArray, installCommandOrder, policySeqParams);
                    PolicySpecificSeqHandler.configLogger.log(Level.INFO, "Completed policy specific sequential command for collection:{0}", new Object[] { collectionID });
                }
            }
            this.addCollectionArrayToCommandArray(commandArray, collectionArray, metaDataList, collectionID);
            this.addSecurityArrayToCommandArray(commandArray, securityCommandArray);
            PolicySpecificSeqHandler.configLogger.log(Level.FINE, "Final array of sequential command:{0}", new Object[] { commandArray.toString() });
        }
        catch (final Exception e) {
            PolicySpecificSeqHandler.logger.log(Level.SEVERE, "Exception while creating sequential command in priority", e);
            throw new Exception(e.getMessage());
        }
        policyCmdJSON.put("commandArray", (Object)commandArray);
        policyCmdJSON.put("params", (Object)policySeqParams);
        return policyCmdJSON;
    }
    
    private String[] priotizePolicyCommands(final List configIDList, final String[] priortizedPolicy) {
        final Set configSet = new HashSet(configIDList);
        final String[] policyCommand = new String[configSet.size()];
        final List newConfigIDList = new ArrayList(configSet);
        int policyNo = 0;
        for (int i = 0; i < priortizedPolicy.length; ++i) {
            final String policy = priortizedPolicy[i];
            if (newConfigIDList.contains(policy)) {
                newConfigIDList.remove(policyCommand[policyNo++] = policy);
            }
        }
        if (newConfigIDList.size() != 0) {
            for (int i = 0; i < newConfigIDList.size(); ++i) {
                final String policy = newConfigIDList.get(i);
                policyCommand[policyNo++] = policy;
            }
        }
        return policyCommand;
    }
    
    private int addListToSeqCmdArray(final List seqList, int installCommandOrder, final JSONArray commandArray) {
        final Iterator seqIterator = seqList.iterator();
        while (seqIterator.hasNext()) {
            commandArray.put(seqIterator.next());
            ++installCommandOrder;
        }
        return installCommandOrder;
    }
    
    private int processPolicySpecificJSON(final JSONObject policyJSON, final List metaDataList, final JSONArray collectionArray, final JSONArray securityCommandArray, final JSONArray seqCmdArray, int installOrder, final JSONObject policySeqParams) throws JSONException {
        if (policyJSON.length() > 0) {
            final JSONObject collectionObject = policyJSON.optJSONObject("collection");
            if (collectionObject != null && collectionObject.length() > 0) {
                final JSONArray metaDataJSON = collectionObject.optJSONArray("metaDataList");
                final JSONObject policyParams = collectionObject.optJSONObject("params");
                if (policyParams != null && policyParams.length() > 0) {
                    JSONUtil.putAll(policySeqParams, policyParams);
                }
                final List tempMetaDataList = JSONUtil.getInstance().convertJSONArrayTOList(metaDataJSON);
                metaDataList.addAll(tempMetaDataList);
                final JSONArray policyCollectionArray = collectionObject.optJSONArray("collectionArray");
                if (policyCollectionArray != null && policyCollectionArray.length() > 0) {
                    JSONUtil.putAll(collectionArray, policyCollectionArray);
                    installOrder += policyCollectionArray.length();
                }
            }
            final JSONObject securityObject = policyJSON.optJSONObject("security");
            if (securityObject != null && securityObject.length() > 0) {
                final JSONArray securityArray = securityObject.optJSONArray("securityArray");
                final JSONObject policyParams2 = securityObject.optJSONObject("params");
                if (policyParams2 != null && policyParams2.length() > 0) {
                    JSONUtil.putAll(policySeqParams, policyParams2);
                }
                if (securityArray != null && securityArray.length() > 0) {
                    JSONUtil.putAll(securityCommandArray, securityArray);
                    installOrder += securityArray.length();
                }
            }
            final JSONObject sequentialObject = policyJSON.optJSONObject("sequential");
            if (sequentialObject != null && sequentialObject.length() > 0) {
                final JSONArray sequentialArray = sequentialObject.optJSONArray("sequentialCmdArray");
                final JSONObject policyParams3 = sequentialObject.optJSONObject("params");
                if (policyParams3 != null && policyParams3.length() > 0) {
                    JSONUtil.putAll(policySeqParams, policyParams3);
                }
                if (sequentialArray != null && sequentialArray.length() > 0) {
                    JSONUtil.putAll(seqCmdArray, sequentialArray);
                    installOrder += sequentialArray.length();
                }
            }
        }
        return installOrder;
    }
    
    private void addCollectionArrayToCommandArray(final JSONArray commandArray, final JSONArray collectionArray, final List metaDataList, final Long collectionId) throws Exception {
        try {
            DeviceCommandRepository.getInstance().addCollectionCommand(collectionId, metaDataList);
            final HashMap commandMap = DeviceCommandRepository.getInstance().getCommandIdsForCollection(collectionId);
            for (int i = 0; i < collectionArray.length(); ++i) {
                final JSONObject collectionObject = collectionArray.getJSONObject(i);
                final String commandUUID = collectionObject.optString("COMMAND_UUID");
                final Long commandID = commandMap.get(commandUUID);
                final int order = collectionObject.getInt("order");
                final String handler = collectionObject.optString("handler", "com.me.mdm.server.seqcommands.BaseSeqCmdResponseHandler");
                final JSONObject commandObject = new JSONObject();
                commandObject.put("cmd_id", (Object)commandID);
                commandObject.put("order", order);
                commandObject.put("handler", (Object)handler);
                commandArray.put((Object)commandObject);
            }
        }
        catch (final DataAccessException e) {
            PolicySpecificSeqHandler.logger.log(Level.SEVERE, "Exception in adding collection array to command array", (Throwable)e);
            throw e;
        }
        catch (final JSONException e2) {
            PolicySpecificSeqHandler.logger.log(Level.SEVERE, "Exception in adding collection array to command array", (Throwable)e2);
            throw e2;
        }
    }
    
    private void addSecurityArrayToCommandArray(final JSONArray commandArray, final JSONArray securityArray) {
    }
    
    static {
        PolicySpecificSeqHandler.logger = Logger.getLogger("MDMSequentialCommandsLogger");
        PolicySpecificSeqHandler.configLogger = Logger.getLogger("MDMConfigLogger");
    }
}
