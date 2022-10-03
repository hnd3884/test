package com.me.mdm.webclient.directory;

import com.adventnet.persistence.Row;
import java.util.List;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.sync.asynch.DirectorySequenceAsynchImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.util.DMDomainSyncDetailsDataHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.simple.JSONObject;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class MDMAllDomainsViewTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final ViewContext viewCtx = tableContext.getViewContext();
            final HttpServletRequest request = viewCtx.getRequest();
            final String columnalias = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            if (columnalias.equalsIgnoreCase("DMDOMAIN.DOMAIN_ID") && reportType != 4) {
                return Boolean.FALSE;
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void getDomainType(final Object data, final HashMap columnProperties) throws Exception {
        String domainTypeStr = "---";
        if (data != null) {
            final int domainType = (int)data;
            if (domainType == 3) {
                domainTypeStr = I18N.getMsg("desktopcentral.common.aad.Azure_AD", new Object[0]);
            }
            else if (domainType == 4) {
                domainTypeStr = I18N.getMsg("desktopcentral.common.aad.Hybrid_AD", new Object[0]);
            }
            else if (domainType == 2) {
                domainTypeStr = I18N.getMsg("desktopcentral.common.aad.Onpremise_AD", new Object[0]);
            }
            else if (domainType == 101) {
                domainTypeStr = I18N.getMsg("GSuite Directory", new Object[0]);
            }
            else if (domainType == 201) {
                domainTypeStr = "Zoho Directory";
            }
            else if (domainType == 301) {
                domainTypeStr = I18N.getMsg("mdm.ad.okta.directory", new Object[0]);
            }
            columnProperties.put("VALUE", domainTypeStr);
        }
    }
    
    private Integer extractValue(final JSONObject directorySyncSummary, final String key) {
        Integer ret = null;
        if (directorySyncSummary != null && directorySyncSummary.containsKey((Object)key)) {
            try {
                ret = MDMUtil.getInstance().getIntVal(directorySyncSummary.get((Object)key));
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.WARNING, null, ex);
            }
        }
        if (ret == null) {
            ret = new Integer(-1);
        }
        return ret;
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            Integer fetchedCount = 0;
            Integer syncStatusID = 0;
            super.renderCell(tableContext);
            String overlibText = "";
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equalsIgnoreCase("DMDOMAIN.DOMAIN_ID")) {
                final Long dmDomainID = (Long)data;
                final int type = (int)tableContext.getAssociatedPropertyValue("DMDomain.CLIENT_ID");
                int syncStatus = -1;
                try {
                    syncStatus = Integer.valueOf(String.valueOf(tableContext.getAssociatedPropertyValue("DMDomainSyncDetails.FETCH_STATUS")));
                }
                catch (final Exception ex4) {}
                boolean isGsuite = false;
                boolean enableAction = true;
                boolean groupSyncEnabled = false;
                boolean showZDopADUserOption = false;
                boolean zdOPadUserSyncEnabled = false;
                if (data != null) {
                    if (syncStatus != 941 && syncStatus != 951) {
                        final List<Integer> syncObjects = DMDomainSyncDetailsDataHandler.getInstance().getObjectTypesToBeSynced(dmDomainID);
                        if (!syncObjects.isEmpty() && syncObjects.contains(7)) {
                            groupSyncEnabled = true;
                        }
                        if (type == 101) {
                            isGsuite = true;
                        }
                        if (type == 201) {
                            final Long custID = Long.valueOf(String.valueOf(tableContext.getAssociatedPropertyValue("DMDomain.CUSTOMER_ID")));
                            final int zdOPcount = DirectoryUtil.getInstance().getZDopCount((long)custID);
                            if (zdOPcount > 0) {
                                showZDopADUserOption = true;
                            }
                            if (!syncObjects.isEmpty() && syncObjects.contains(1000)) {
                                zdOPadUserSyncEnabled = true;
                            }
                        }
                    }
                    else {
                        overlibText = I18N.getMsg("mdm.ad.disabled.while.syncing", new Object[0]);
                        enableAction = false;
                    }
                }
                final JSONObject payload = new JSONObject();
                payload.put((Object)"client_id", (Object)type);
                payload.put((Object)"isGsuite", (Object)isGsuite);
                payload.put((Object)"dmDomainID", (Object)dmDomainID);
                payload.put((Object)"overlibText", (Object)overlibText);
                payload.put((Object)"enableAction", (Object)enableAction);
                payload.put((Object)"groupSyncEnabled", (Object)groupSyncEnabled);
                payload.put((Object)"showZDopADUserOption", (Object)showZDopADUserOption);
                payload.put((Object)"zdOPadUserSyncEnabled", (Object)zdOPadUserSyncEnabled);
                columnProperties.put("PAYLOAD", payload);
            }
            if (columnalais.equalsIgnoreCase("DMDOMAIN.NAME")) {
                String dmDomainName = (String)data;
                final Integer type2 = (Integer)tableContext.getAssociatedPropertyValue("DMDomain.CLIENT_ID");
                if (type2 != null && type2 == 101) {
                    dmDomainName = "G Suite";
                }
                columnProperties.put("VALUE", dmDomainName);
            }
            if (columnalais.equalsIgnoreCase("DMDOMAIN.CLIENT_ID")) {
                this.getDomainType(data, columnProperties);
            }
            if (columnalais.equalsIgnoreCase("DMDomainSyncDetails.LAST_SYNC_INITIATED") || columnalais.equalsIgnoreCase("DMDomainSyncDetails.LAST_SUCCESSFUL_SYNC")) {
                String value = "--";
                if (data != null) {
                    try {
                        if (!data.toString().equals("-1") && data != null) {
                            value = Utils.getTime((Long)data);
                        }
                    }
                    catch (final Exception ex) {
                        IDPSlogger.ERR.log(Level.WARNING, "Exception occoured in Agent applied time", ex);
                    }
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equals("DMDomainSyncDetails.FETCH_STATUS")) {
                Integer collatedStatus = 0;
                String failText = "";
                String failURL = "false";
                String statusLabel = "---";
                if (data != null) {
                    try {
                        syncStatusID = Integer.valueOf(String.valueOf(data));
                        final Row configStatusRow = DBUtil.getRowFromDB("ConfigStatusDefn", "STATUS_ID", (Object)syncStatusID);
                        statusLabel = (String)configStatusRow.get("LABEL");
                        final String statusImage = (String)configStatusRow.get("IMAGE_NAME");
                        final Long dmDomainID2 = (Long)tableContext.getAssociatedPropertyValue("DMDomain.DOMAIN_ID");
                        final Integer domainType = (Integer)tableContext.getAssociatedPropertyValue("DMDomain.CLIENT_ID");
                        final JSONObject directorySyncSummary = DirectorySequenceAsynchImpl.getInstance().getDirectorySyncSummary(dmDomainID2);
                        if (syncStatusID == 901) {
                            final String remarks = (String)tableContext.getAssociatedPropertyValue("DMDomainSyncDetails.REMARKS");
                            if (!SyMUtil.isStringValid(remarks)) {
                                failURL = "/kb/unable-to-add-domain.html";
                                failText = MDMI18N.getI18Nmsg("mdm.appmgmt.read_more");
                            }
                            else if (domainType == 3) {
                                failURL = remarks;
                                if (remarks.contains("login.microsoftonline.com/common/oauth2/v2.0/authorize")) {
                                    failText = MDMI18N.getI18Nmsg("idps.oauth.reauthenticate");
                                    overlibText = MDMI18N.getI18Nmsg("idps.re.oauth.desc");
                                }
                                else if (remarks.contains("webclient#/uems/mdm/enrollment/activeDirectory/integrateAzureOauth")) {
                                    failText = MDMI18N.getI18Nmsg("idps.oauth.update");
                                    overlibText = MDMI18N.getI18Nmsg("idps.oauth.view.update.desc");
                                }
                                else if (remarks.contains("webclient#/uems/mdm/enrollment/activeDirectory/list/oauth")) {
                                    failText = MDMI18N.getI18Nmsg("idps.oauth.invalid.client");
                                    overlibText = MDMI18N.getI18Nmsg("idps.oauth.view.client.update.desc");
                                }
                            }
                            else if (domainType == 101 && remarks.equalsIgnoreCase("/webclient#/uems/mdm/enrollment/activeDirectory/integrateGsuite")) {
                                failText = MDMI18N.getI18Nmsg("idps.oauth.reauthenticate");
                                failURL = remarks;
                            }
                        }
                        if (syncStatusID == 941) {
                            collatedStatus = this.extractValue(directorySyncSummary, "STATUS_ID");
                            fetchedCount = this.extractValue(directorySyncSummary, "PRE_PROCESSED_COUNT");
                            if (collatedStatus == 951) {
                                overlibText = MDMI18N.getI18Nmsg("mdm.ad.sync.initate");
                            }
                            if (collatedStatus == 921) {
                                overlibText = MDMI18N.getI18Nmsg("mdm.ad.sync.queue");
                            }
                            else if (collatedStatus == 931 || collatedStatus == 911) {
                                overlibText = (String)tableContext.getAssociatedPropertyValue("DMDomainSyncDetails.SYNC_STATUS");
                            }
                            else if (collatedStatus == 941 && fetchedCount > 0) {
                                overlibText = MDMI18N.getI18Nmsg("mdm.ad.sync.fetched", new Object[] { String.valueOf(fetchedCount) });
                            }
                        }
                        else if (syncStatusID == 921) {
                            overlibText = (String)tableContext.getAssociatedPropertyValue("DMDomainSyncDetails.SYNC_STATUS");
                        }
                        if (reportType == 4) {
                            final org.json.JSONObject payload2 = new org.json.JSONObject();
                            payload2.put("data", data);
                            payload2.put("failURL", (Object)failURL);
                            payload2.put("failText", (Object)failText);
                            payload2.put("statusImage", (Object)statusImage);
                            payload2.put("overlibText", (Object)overlibText);
                            payload2.put("fetchedCount", (Object)fetchedCount);
                            payload2.put("status", (Object)I18N.getMsg(statusLabel, new Object[0]));
                            columnProperties.put("PAYLOAD", payload2);
                        }
                        else {
                            columnProperties.put("VALUE", I18N.getMsg(statusLabel, new Object[0]));
                        }
                    }
                    catch (final Exception ex2) {
                        IDPSlogger.ERR.log(Level.WARNING, null, ex2);
                    }
                }
            }
            if (columnalais.equals("DMManagedDomain.DC_NAME")) {
                final Integer dmDomainClient = (Integer)tableContext.getAssociatedPropertyValue("DMDomain.CLIENT_ID");
                if (dmDomainClient != null && dmDomainClient == 101) {
                    columnProperties.put("VALUE", "GSuite");
                }
            }
        }
        catch (final Exception ex3) {
            IDPSlogger.ERR.log(Level.WARNING, "Exception occured while rendering cell value in MDMAllDomainsViewTransformer", ex3);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
}
