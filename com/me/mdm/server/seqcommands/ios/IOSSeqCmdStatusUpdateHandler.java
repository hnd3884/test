package com.me.mdm.server.seqcommands.ios;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.Properties;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import java.util.List;
import com.me.mdm.server.seqcommands.BaseSeqCmdStatusUpdateHandler;

public class IOSSeqCmdStatusUpdateHandler extends BaseSeqCmdStatusUpdateHandler
{
    @Override
    public void makeStatusUpdateforSubCommand(final List<SequentialSubCommand> cmdList) throws Exception {
        this.getRemoveCommandList(cmdList);
        final List collectionList = DeviceCommandRepository.getInstance().getCollectionIDListForCmdID(SeqCmdUtils.getInstance().getCommandList(cmdList));
        if (!collectionList.isEmpty()) {
            this.makeStatusUpdatesForCollectionCommands(cmdList);
        }
    }
    
    private void makeStatusUpdatesForCollectionCommands(final List<SequentialSubCommand> cmdList) throws Exception {
        final DataObject dataObject = this.getStatusUpdateDO(cmdList);
        final DataObject baseObjectDO = this.getBaseObjectDO(SeqCmdUtils.getInstance().getSeqList(cmdList));
        final List profileList = this.getProfileListForUpdate(dataObject);
        final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
        profileAssociateDataHandler.init(SeqCmdUtils.getInstance().getResList(cmdList), profileList);
        final Iterator iterator = cmdList.iterator();
        Boolean profileUpdated = Boolean.FALSE;
        while (iterator.hasNext()) {
            final SequentialSubCommand sequentialSubCommand = iterator.next();
            final Row row = dataObject.getRow("MdCollectionCommand", new Criteria(Column.getColumn("MdCollectionCommand", "COMMAND_ID"), (Object)sequentialSubCommand.CommandID, 0));
            if (row != null) {
                final Long collectionID = (Long)row.get("COLLECTION_ID");
                final Row profileRow = dataObject.getRow("ProfileToCollection", new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                final Criteria resourceCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0);
                final Criteria seqCriteria = new Criteria(Column.getColumn("SequentialCommandParams", "RESOURCE_ID"), (Object)sequentialSubCommand.resourceID, 0);
                final Row seqRow = dataObject.getRow("SequentialCommandParams", seqCriteria.and(resourceCriteria));
                final String paramString = (String)seqRow.get("PARAMS");
                final Row baseIDRow = dataObject.getRow("MdCommandToSequentialCommand", new Criteria(Column.getColumn("MdCommandToSequentialCommand", "SEQUENTIAL_COMMAND_ID"), (Object)sequentialSubCommand.SequentialCommandID, 0));
                final Long baseID = (Long)baseIDRow.get("COMMAND_ID");
                JSONObject seqParams = new JSONObject();
                if (paramString != null) {
                    seqParams = new JSONObject(paramString);
                }
                final Row appRow = dataObject.getRow("AppGroupToCollection", new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                Boolean isApp = Boolean.FALSE;
                if (appRow != null) {
                    isApp = Boolean.TRUE;
                }
                final JSONObject initialParams = seqParams.optJSONObject("initialParams");
                final Properties params = new Properties();
                final Long baseProfileID = this.getBaseProfileID(baseObjectDO, baseID);
                final Long baseCollnID = this.getBaseCollectionID(baseObjectDO, baseID);
                final Long userID = SeqCmdUtils.getInstance().getAssociatedUserFromParamsFrombaseID(initialParams, baseProfileID);
                final List resourceList = new ArrayList();
                resourceList.add(sequentialSubCommand.resourceID);
                ((Hashtable<String, Long>)params).put("UserId", userID);
                ((Hashtable<String, List>)params).put("resourceList", resourceList);
                ((Hashtable<String, Long>)params).put("profileID", profileID);
                ((Hashtable<String, Long>)params).put("collectionId", collectionID);
                ((Hashtable<String, Boolean>)params).put("profileOrigin", false);
                ((Hashtable<String, Boolean>)params).put("isAppConfig", isApp);
                ((Hashtable<String, Boolean>)params).put("baseCmd", baseCollnID.equals(collectionID));
                profileAssociateDataHandler.associateProfileFromSequencialCmd(params);
                profileUpdated = Boolean.TRUE;
            }
        }
        if (profileUpdated) {
            profileAssociateDataHandler.commitChangestoDB();
        }
    }
}
