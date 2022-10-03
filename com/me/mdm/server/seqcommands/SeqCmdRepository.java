package com.me.mdm.server.seqcommands;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.Properties;
import java.util.Iterator;
import com.me.mdm.server.seqcommands.data.SeqCmdDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class SeqCmdRepository
{
    public Logger logger;
    static SeqCmdRepository sequentialCommandRepository;
    
    public SeqCmdRepository() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    public static SeqCmdRepository getInstance() {
        if (SeqCmdRepository.sequentialCommandRepository == null) {
            SeqCmdRepository.sequentialCommandRepository = new SeqCmdRepository();
        }
        return SeqCmdRepository.sequentialCommandRepository;
    }
    
    public void executeSequentially(final List<Long> resourceList, final List<Long> commandList, final JSONObject params) throws Exception {
        this.logger.log(Level.INFO, "[Seq] [Init] Execute sequentially invoked with command set {0} and res lsit {1}", new Object[] { commandList, resourceList });
        final List seqCmdList = SeqCmdUtils.getInstance().getSequentialIDforBaseID(commandList);
        if (seqCmdList.size() == 0) {
            return;
        }
        final List resourcesRunningSeq = SeqCmdUtils.getInstance().getResourcesRunningSeqCmd(resourceList);
        final List resourcesNotRunningSeq = new ArrayList(resourceList);
        resourcesNotRunningSeq.removeAll(resourcesRunningSeq);
        final Long firstCommand = seqCmdList.remove(0);
        this.initialiseAndLoadFirstCommand(resourcesNotRunningSeq, firstCommand, params);
        SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourcesNotRunningSeq, 1, seqCmdList, 12);
        seqCmdList.add(firstCommand);
        SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourcesRunningSeq, 1, seqCmdList, 12);
        SeqCmdUtils.getInstance().setSeqCmdParams(resourcesNotRunningSeq, seqCmdList, params);
        SeqCmdUtils.getInstance().setSeqCmdParams(resourcesRunningSeq, seqCmdList, params);
        this.logger.log(Level.INFO, "[Seq] [Init] Sequential Commands Added for execution {0} {1} ", new Object[] { resourceList, seqCmdList });
    }
    
    protected void initialiseAndLoadFirstCommand(final List<Long> resourceList, final Long commandID, final JSONObject param) throws Exception {
        this.logger.log(Level.INFO, "[Seq] [Init] Seqcmd being initialised for resource : {0} Seq sub cmd id: {1} The params passed were {2}", new Object[] { resourceList, commandID, param });
        if (resourceList.size() == 0) {
            return;
        }
        final List commandList = new ArrayList();
        commandList.add(commandID);
        try {
            SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceList, 1, commandList, 190);
            SeqCmdUtils.getInstance().setSeqCmdParams(resourceList, commandList, param);
            final List seqList = new ArrayList();
            seqList.add(commandID);
            final List listofResourceLists = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
            final Iterator iterator = listofResourceLists.iterator();
            while (iterator.hasNext()) {
                final SeqCmdDataHandler seqCmdDataHandler = new SeqCmdDataHandler();
                final List<Long> sliptResList = iterator.next();
                this.logger.log(Level.INFO, "[Seq] [Init] chunk being processed reslist : {0}", resourceList);
                seqCmdDataHandler.initalise(sliptResList, seqList);
                for (final Long resourceID : sliptResList) {
                    this.logger.log(Level.INFO, "[Seq] [Init] resource being initialised  : {0}", resourceID);
                    SequentialSubCommand sequentialSubCommand = seqCmdDataHandler.getCurrentSeqCmdForResource(resourceID);
                    if (sequentialSubCommand != null) {
                        final String handler = sequentialSubCommand.Handler;
                        Object handlerClass = null;
                        handlerClass = Class.forName(handler).newInstance();
                        final SeqCmdResponseHandler sequentialCommandResponseHandler = (SeqCmdResponseHandler)handlerClass;
                        while (!sequentialCommandResponseHandler.subCommandPreProcessor(resourceID, sequentialSubCommand.CommandID, sequentialSubCommand)) {
                            sequentialSubCommand = seqCmdDataHandler.getNextCommand(sequentialSubCommand);
                            if (sequentialSubCommand == null) {
                                seqCmdDataHandler.cleanUpSeqCmdForResource(resourceID, sequentialSubCommand);
                                break;
                            }
                            seqCmdDataHandler.addorUpdateSeqStatusAndQueue(resourceID, sequentialSubCommand.order, commandID, 190);
                        }
                        this.logger.log(Level.INFO, "[Seq] [Init] Sequential Sub Command selected for loading  : {0}", sequentialSubCommand);
                        seqCmdDataHandler.updateParams(sequentialSubCommand);
                        if (sequentialSubCommand == null) {
                            continue;
                        }
                        seqCmdDataHandler.addorUpdateSeqStatusAndQueue(resourceID, sequentialSubCommand.order, commandID, 3);
                        seqCmdDataHandler.addToLoadQueue(sequentialSubCommand);
                    }
                    else {
                        this.logger.log(Level.WARNING, "[Seq] [Init] A Seq cmd was added for resource {0} but got suspended before initialsation", resourceID);
                    }
                }
                this.logger.log(Level.INFO, "[Seq] [Init] Going to commit changes  : {0}", seqCmdDataHandler);
                seqCmdDataHandler.loadAndCommitCommand();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "[Seq] [Init] Exception in intilising seq cmd so going to suspend resourcelist : {0} seq cmd list : {1} ", new Object[] { resourceList, commandList });
            this.logger.log(Level.SEVERE, "[Seq] [Init] Exception in intilising seq cmd so going to suspend ", e);
            SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceList, 1, commandList, 5);
            throw e;
        }
    }
    
    private boolean executeSequentially(final Long resourceID, final Long sequentialCommandID) throws Exception {
        final Long sequentialID = SeqCmdUtils.getInstance().getSequentialIDforBaseID(sequentialCommandID);
        if (SeqCmdUtils.getInstance().getCurrentSeqCmdOfResource(resourceID) == SeqCmdConstants.NO_SEQUENTIAL_COMMAND_EXECUTING) {
            this.initialiseAndLoadFirstCommand(resourceID, sequentialID);
        }
        else {
            SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, 1, sequentialID, 12);
        }
        return true;
    }
    
    private void initialiseAndLoadFirstCommand(final Long resourceID, final Long sequentialID) throws Exception {
        if (sequentialID == -1L) {
            return;
        }
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        this.initialiseAndLoadFirstCommand(resourceList, sequentialID, null);
    }
    
    public void processSeqCommand(final JSONObject response) {
        Long resourceID = null;
        final Boolean isNotify = response.optBoolean("isNotify", (boolean)Boolean.TRUE);
        try {
            this.logger.log(Level.INFO, "[Seq] [process] Data to Process Sequential Command From platform : {0}", response);
            final int action = response.getInt("action");
            resourceID = response.getLong("resourceID");
            final JSONObject params = response.getJSONObject("params");
            final String UUID = String.valueOf(response.get("commandUUID"));
            SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getCurrentSeqCmdOfResourceWithMeta(resourceID);
            SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 3);
            String handler = sequentialSubCommand.Handler;
            Object handlerClass = Class.forName(handler).newInstance();
            SeqCmdResponseHandler sequentialCommandResponseHandler = (SeqCmdResponseHandler)handlerClass;
            final JSONObject handlerParams = new JSONObject();
            handlerParams.put("resourceID", (Object)resourceID);
            handlerParams.put("commandUUID", (Object)UUID);
            handlerParams.put("CurCmdParam", (Object)params);
            handlerParams.put("cmdScopeParams", (Object)sequentialSubCommand.params.optJSONObject("cmdScopeParams"));
            handlerParams.put("initialParams", (Object)sequentialSubCommand.params.optJSONObject("initialParams"));
            handlerParams.put("PrevCmdParams", (Object)sequentialSubCommand.params.optJSONObject("CurCmdParam"));
            handlerParams.put("QueuedCommands", (Object)SeqCmdUtils.getInstance().getNextSubCommandsInQueueforResource(resourceID));
            handlerParams.put("CommandLevelParams", (Object)sequentialSubCommand.params.optJSONObject("CommandLevelParams"));
            this.logger.log(Level.INFO, "[Seq] [process] Params Being sent to Handler {0}", handlerParams);
            if (action == 1) {
                Long nextCmdID = sequentialCommandResponseHandler.onSuccess(handlerParams);
                if (nextCmdID.equals(SeqCmdConstants.NO_NEXT_COMMAND)) {
                    SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
                }
                else {
                    sequentialSubCommand = SeqCmdDBUtil.getInstance().getSequentialSubCommand(resourceID, nextCmdID);
                    if (sequentialSubCommand == SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
                        SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
                    }
                    else {
                        sequentialSubCommand.params = handlerParams;
                        while (!sequentialCommandResponseHandler.subCommandPreProcessor(resourceID, nextCmdID, sequentialSubCommand)) {
                            this.logger.log(Level.INFO, "[Seq] [process] Success Handler calling Net sub cmd resource : {0} command ID{1}", new Object[] { resourceID, nextCmdID });
                            sequentialSubCommand = SeqCmdDBUtil.getInstance().getNextSubCommand(resourceID);
                            if (sequentialSubCommand == SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
                                SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
                                break;
                            }
                            SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 3);
                            nextCmdID = sequentialSubCommand.CommandID;
                            sequentialSubCommand.params = handlerParams;
                            handler = sequentialSubCommand.Handler;
                            handlerClass = Class.forName(handler).newInstance();
                            sequentialCommandResponseHandler = (SeqCmdResponseHandler)handlerClass;
                        }
                        this.logger.log(Level.INFO, "[Seq] [process] updating seqCmd to {0}", sequentialSubCommand);
                        if (sequentialSubCommand != SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
                            SeqCmdUtils.getInstance().loadNextSubCommand(sequentialSubCommand.SequentialCommandID, nextCmdID, resourceID);
                            if (isNotify) {
                                SeqCmdUtils.getInstance().sendNotification(resourceID);
                            }
                            sequentialCommandResponseHandler.setParams(resourceID, handlerParams);
                        }
                    }
                }
            }
            else if (action == 2) {
                this.logger.log(Level.INFO, "[Seq] [process] Failure Handler calling Net sub cmd resource : {0} ", new Object[] { resourceID });
                final Long nextCmdID = sequentialCommandResponseHandler.onFailure(handlerParams);
                SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
                if (isNotify) {
                    SeqCmdUtils.getInstance().sendNotification(resourceID);
                }
                if (!nextCmdID.equals(SeqCmdConstants.ABORT_COMMAND)) {
                    this.executeSequentially(resourceID, nextCmdID);
                }
            }
            else if (action == 3) {
                Long nextCmdID = sequentialCommandResponseHandler.retry(handlerParams);
                sequentialSubCommand = SeqCmdDBUtil.getInstance().getSequentialSubCommand(resourceID, nextCmdID);
                sequentialSubCommand.params = handlerParams;
                while (!sequentialCommandResponseHandler.subCommandPreProcessor(resourceID, nextCmdID, sequentialSubCommand)) {
                    sequentialSubCommand = SeqCmdDBUtil.getInstance().getNextSubCommand(resourceID);
                    if (sequentialSubCommand == SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
                        SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
                        break;
                    }
                    SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 3);
                    nextCmdID = sequentialSubCommand.CommandID;
                    sequentialSubCommand.params = handlerParams;
                }
                if (sequentialSubCommand != SeqCmdConstants.NO_NEXT_SUB_COMMAND) {
                    SeqCmdUtils.getInstance().loadNextSubCommand(sequentialSubCommand.SequentialCommandID, nextCmdID, resourceID);
                    if (isNotify) {
                        SeqCmdUtils.getInstance().sendNotification(resourceID);
                    }
                    sequentialCommandResponseHandler.setParams(resourceID, handlerParams);
                }
            }
            else if (action == 4) {
                final JSONObject jsonObject = sequentialCommandResponseHandler.processLater(handlerParams);
                final String taskClass = String.valueOf(jsonObject.get("taskClass"));
                final Long timeOffset = jsonObject.getLong("timeOffset");
                final Properties taskProps = new Properties();
                ((Hashtable<String, String>)taskProps).put("params", handlerParams.toString());
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("taskName", "ProcessSequentialCommandScheduler");
                final HashMap hashMap = taskInfoMap;
                final String s = "schedulerTime";
                SyMUtil.getInstance();
                hashMap.put(s, SyMUtil.getCurrentTimeInMillis() + timeOffset);
                taskInfoMap.put("poolName", "mdmPool");
                SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 180);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay(taskClass, taskInfoMap, taskProps);
            }
            else if (action == 5) {
                SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 16);
                sequentialCommandResponseHandler.notNow(handlerParams);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e, () -> "Exception in processSeqCommand, The command is being aborted : " + jsonObject2 + " :");
            if (resourceID != null) {
                SeqCmdUtils.getInstance().sequentialCommandCleanUpforResource(resourceID, isNotify);
            }
        }
    }
    
    static {
        SeqCmdRepository.sequentialCommandRepository = null;
    }
}
