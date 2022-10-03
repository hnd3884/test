package com.me.idps.core.util;

import java.util.Hashtable;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.oauth.OauthException;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.me.idps.core.crud.DMDomainDataHandler;
import org.json.simple.parser.JSONParser;
import com.me.idps.core.factory.TransactionExecutionImpl;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.Properties;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public abstract class DirQueue extends DCQueueDataProcessor
{
    protected abstract void processDirTask(final String p0, final String p1, final Long p2, final Long p3, final Integer p4, final JSONObject p5) throws Exception;
    
    protected Properties getDomainProps(final Long dmDomainID, final String dmDomainName, final Integer dmDomainClient, final Long customerID) {
        final Properties dmDomainProps = new Properties();
        ((Hashtable<String, String>)dmDomainProps).put("NAME", dmDomainName);
        ((Hashtable<String, Long>)dmDomainProps).put("DOMAIN_ID", dmDomainID);
        ((Hashtable<String, Long>)dmDomainProps).put("CUSTOMER_ID", customerID);
        ((Hashtable<String, Integer>)dmDomainProps).put("CLIENT_ID", dmDomainClient);
        return dmDomainProps;
    }
    
    public void processData(final DCQueueData qData) {
        JSONObject qNode = null;
        String domainName = null;
        Integer dmDomainClient = null;
        Long dmDomainID = null;
        Long customerID = null;
        final Long consumeStartTime = System.currentTimeMillis();
        try {
            TransactionExecutionImpl.getInstance().clearActiveTransactionsIfAny();
            qNode = (JSONObject)new JSONParser().parse((String)qData.queueData);
            final String taskType = (String)qNode.get((Object)"TASK_TYPE");
            domainName = (String)qNode.getOrDefault((Object)"NAME", (Object)null);
            final Object domainIDobj = qNode.getOrDefault((Object)"DOMAIN_ID", (Object)null);
            final Object clientIDobj = qNode.getOrDefault((Object)"CLIENT_ID", (Object)null);
            final Object customerIDObj = qNode.getOrDefault((Object)"CUSTOMER_ID", (Object)null);
            if (domainIDobj != null && (customerIDObj == null || IdpsUtil.isStringEmpty(domainName) || clientIDobj == null)) {
                dmDomainID = Long.valueOf(String.valueOf(domainIDobj));
                final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainById(dmDomainID);
                domainName = ((Hashtable<K, String>)dmDomainProps).get("NAME");
                customerID = ((Hashtable<K, Long>)dmDomainProps).get("CUSTOMER_ID");
                dmDomainClient = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
            }
            else if (!IdpsUtil.isStringEmpty(domainName) && customerIDObj != null && clientIDobj != null && domainIDobj == null) {
                customerID = Long.valueOf(String.valueOf(customerIDObj));
                dmDomainClient = Integer.parseInt(String.valueOf(clientIDobj));
                final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID, dmDomainClient);
                dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
            }
            else if (!IdpsUtil.isStringEmpty(domainName) && customerIDObj != null && (domainIDobj == null || clientIDobj == null)) {
                customerID = Long.valueOf(String.valueOf(customerIDObj));
                final Properties dmDomainProps = DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID);
                dmDomainID = ((Hashtable<K, Long>)dmDomainProps).get("DOMAIN_ID");
                dmDomainClient = ((Hashtable<K, Integer>)dmDomainProps).get("CLIENT_ID");
            }
            else if (domainIDobj != null && customerIDObj != null && clientIDobj != null) {
                dmDomainID = Long.valueOf(String.valueOf(domainIDobj));
                customerID = Long.valueOf(String.valueOf(customerIDObj));
                dmDomainClient = Integer.valueOf(String.valueOf(clientIDobj));
            }
            dmDomainID = (Long)((dmDomainID != null) ? dmDomainID : domainIDobj);
            customerID = (Long)((customerID != null) ? customerID : customerIDObj);
            dmDomainClient = ((dmDomainClient != null) ? dmDomainClient : ((clientIDobj != null) ? Integer.valueOf(String.valueOf(clientIDobj)) : null));
            qNode.put((Object)"NAME", (Object)domainName);
            qNode.put((Object)"DOMAIN_ID", (Object)dmDomainID);
            qNode.put((Object)"CUSTOMER_ID", (Object)customerID);
            qNode.put((Object)"CLIENT_ID", (Object)dmDomainClient);
            final boolean isSyncTokenValid = DirectorySequenceAsynchImpl.getInstance().isSyncTokenValid(dmDomainID, qNode);
            final String queueName = (String)qNode.get((Object)"QUEUE_NAME");
            final Logger loggerForQueue = DirectoryUtil.getInstance().getLoggerForQueue(queueName);
            this.logStart(qData, queueName, loggerForQueue, domainName, customerID, dmDomainID, taskType, isSyncTokenValid, dmDomainClient, consumeStartTime);
            if (isSyncTokenValid) {
                this.processDirTask(taskType, domainName, customerID, dmDomainID, dmDomainClient, qNode);
            }
            this.logEnd(qData, queueName, dmDomainID, taskType, consumeStartTime);
        }
        catch (final OauthException e) {
            IDPSlogger.ERR.log(Level.SEVERE, null, e);
            if (dmDomainID != null) {
                DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e, "DOMAIN_EMPTYUSERSLIST_ERROR");
            }
        }
        catch (final Exception e2) {
            IDPSlogger.ERR.log(Level.SEVERE, "exception in processing {0}", new Object[] { String.valueOf(qData.queueData) });
            final String eMsg = e2.getMessage();
            if (!SyMUtil.isStringEmpty(eMsg)) {
                if (eMsg.equalsIgnoreCase("INTERNAL_ERROR")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "INTERNAL_ERROR");
                }
                else if (eMsg.contains("INPUT_USER_NAME_DUPL_ERROR")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "INPUT_USER_NAME_DUPL_ERROR");
                }
                else if (eMsg.contains("domain duplication error;")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DOMAIN_DUPLICATION_COUNT");
                }
                else if (eMsg.contains(" is not reachable")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DOMAIN_UNREACHABLE_ERROR");
                }
                else if (eMsg.contains(" exception from adapter")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DIR_ADAPTER_ERR");
                }
                else if (eMsg.contains(" is already ") && eMsg.contains(" and still again being incremented by !?")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DIR_ADAPTER_ERR");
                }
                else if (eMsg.contains(" should take values only o or 1, but is being set to ")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DIR_ADAPTER_ERR");
                }
                else if (eMsg.startsWith("DOMAIN_UNREACHABLE_ERROR:")) {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "DOMAIN_UNREACHABLE_ERROR");
                }
                else if (eMsg.equalsIgnoreCase("COM0032")) {
                    IDPSlogger.ERR.log(Level.SEVERE, "details are not found", e2);
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, "AZURE_OAUTH_ERROR");
                }
                else {
                    DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, null);
                }
            }
            else {
                DirectorySyncErrorHandler.getInstance().handleError(dmDomainID, customerID, domainName, dmDomainClient, e2, null);
            }
        }
    }
    
    private String getTimeStr(final long timeInMilllis) {
        String timeStr = null;
        try {
            timeStr = DirectoryUtil.getInstance().longdateToString(timeInMilllis);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.WARNING, "exception in getting postTimeStr");
        }
        return timeStr;
    }
    
    private String getDuration(final Long startTime, final Long endTime) {
        try {
            if (startTime != null && endTime != null) {
                return DirectoryUtil.getInstance().formatDurationMS(endTime - startTime);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.WARNING, "exception in getting duration");
        }
        return "NA";
    }
    
    private void logStart(final DCQueueData qData, final String queueName, final Logger loggerForQueue, final String domainName, final Long customerID, final Long dmDomainID, final String taskType, final boolean isSyncTokenValid, final Integer dmDomainClient, final Long consumeStartTime) {
        loggerForQueue.log(Level.INFO, "------------------------");
        loggerForQueue.log(Level.INFO, "|domainName:{0}|custID:{1}|domainID:{2}|task:{3}|isSyncTokenValid:{4}|clientID:{5}|", new Object[] { domainName, String.valueOf(customerID), String.valueOf(dmDomainID), taskType, isSyncTokenValid, String.valueOf(dmDomainClient) });
        final String prodTimeStr = this.getTimeStr(qData.postTime);
        final String consumStrtTimeStr = this.getTimeStr(consumeStartTime);
        final String startDuration = this.getDuration(qData.postTime, consumeStartTime);
        IDPSlogger.QUEUE.log(Level.INFO, "|qNme:{0}|dmId:{1}|task:{2}|consumQTID:{3}|pT:{4}|consumStrtAt:{5}|strtDur:{6}|", new Object[] { queueName, String.valueOf(dmDomainID), taskType, qData.fileName, prodTimeStr, consumStrtTimeStr, startDuration });
    }
    
    private void logEnd(final DCQueueData qData, final String queueName, final Long dmDomainID, final String taskType, final Long consumeStartTime) {
        final Long consumptionEndedTime = System.currentTimeMillis();
        final String prodTimeStr = this.getTimeStr(qData.postTime);
        final String consumStrtTime = this.getTimeStr(consumeStartTime);
        final String consumEndTime = this.getTimeStr(consumptionEndedTime);
        final String startDur = this.getDuration(qData.postTime, consumeStartTime);
        final String totalDur = this.getDuration(qData.postTime, consumptionEndedTime);
        final String consumDur = this.getDuration(consumeStartTime, consumptionEndedTime);
        IDPSlogger.QUEUE.log(Level.INFO, "|qNme:{0}|dmId:{1}|task:{2}|consumQTID:{3}|pT:{4}|consumStrtAt:{5}|strtDur:{6}|consumEndAt:{7}|consumDur:{8},totalDur:{9}", new Object[] { queueName, String.valueOf(dmDomainID), taskType, qData.fileName, prodTimeStr, consumStrtTime, startDur, consumEndTime, consumDur, totalDur });
    }
}
