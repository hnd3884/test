package com.adventnet.sym.webclient.mdm.doc;

import java.util.ArrayList;
import com.me.mdm.webclient.transformer.TransformerUtil;
import java.text.DateFormat;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.List;
import com.adventnet.i18n.I18N;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMDocumentTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMDocumentTransformer() {
        this.logger = DocMgmt.logger;
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        if (columnalias.equalsIgnoreCase("checkbox") || columnalias.equalsIgnoreCase("checkbox_column") || columnalias.equalsIgnoreCase("DocumentDetails.DOC_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final String isExport = request.getParameter("isExport");
            final int reportType = tableContext.getViewContext().getRenderType();
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (export || (isExport != null && isExport.equalsIgnoreCase("true"))) {
                return false;
            }
        }
        if (columnalias.equalsIgnoreCase("DocumentDetails.DOC_ID")) {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            return request.isUserInRole("MDM_ContentMgmt_Admin");
        }
        if (!columnalias.equalsIgnoreCase("DocumentToDeviceGroup.CUSTOMGROUP_ID") && !columnalias.equals("DocumentManagedDeviceRel.MANAGEDDEVICE_ID")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        final int reportType2 = tableContext.getViewContext().getRenderType();
        boolean export2 = false;
        if (reportType2 != 4) {
            export2 = true;
        }
        if (export2) {
            return false;
        }
        final ViewContext vc2 = tableContext.getViewContext();
        final HttpServletRequest request2 = vc2.getRequest();
        return request2.isUserInRole("MDM_ContentMgmt_Write");
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        this.logger.log(Level.FINE, "Entering DocumentTransformer renderHeader().....");
        final ViewContext viewCtx = tableContext.getViewContext();
        final HttpServletRequest request = viewCtx.getRequest();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String head = tableContext.getDisplayName();
        try {
            if (head.equals("Action") && isExport != null) {
                headerProperties.put("VALUE", "");
            }
        }
        catch (final Exception ex) {}
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering DocumentTransformer...");
        try {
            final boolean isExport = false;
            super.renderCell(tableContext);
            final ViewContext vc = tableContext.getViewContext();
            final Object data = tableContext.getPropertyValue();
            final String columnAlias = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String viewName = tableContext.getViewContext().getUniqueId();
            final Long docIDObj = (Long)tableContext.getAssociatedPropertyValue("DocumentDetails.DOC_ID");
            if (viewName.equalsIgnoreCase("mdmDocGroups") && docIDObj == null) {
                return;
            }
            final long docId = docIDObj;
            final long docSize = (long)tableContext.getAssociatedPropertyValue("DocumentDetails.SIZE");
            final String docName = (String)tableContext.getAssociatedPropertyValue("DocumentDetails.DOC_NAME");
            final Integer docType = (Integer)tableContext.getAssociatedPropertyValue("DocumentDetails.DOC_TYPE");
            String description = (String)tableContext.getAssociatedPropertyValue("DocumentDetails.DESCRIPTION");
            Integer sharedGrpCount = (Integer)tableContext.getAssociatedPropertyValue("DocumentSummary.GROUP_COUNT");
            Integer sharedDeviceCount = (Integer)tableContext.getAssociatedPropertyValue("DocumentSummary.DEVICE_COUNT");
            Integer sharedUserCount = (Integer)tableContext.getAssociatedPropertyValue("DocumentSummary.USER_COUNT");
            boolean export = false;
            if (reportType != 4) {
                export = true;
            }
            if (columnAlias.equalsIgnoreCase("DocumentSummary.GROUP_COUNT")) {
                if (sharedGrpCount == null) {
                    sharedGrpCount = 0;
                }
                if (!export) {
                    final List<String> cgName = this.getAssociatedGroupNames(vc, docId);
                    final Iterator groups = cgName.iterator();
                    String grpStr = "";
                    while (groups.hasNext()) {
                        grpStr += groups.next();
                        if (groups.hasNext()) {
                            grpStr += ",<br>";
                        }
                    }
                    final JSONObject payload = new JSONObject();
                    payload.put("sharedGrpCount", (Object)sharedGrpCount);
                    payload.put("grpStr", (Object)grpStr);
                    columnProperties.put("PAYLOAD", payload);
                }
                else {
                    columnProperties.put("VALUE", sharedGrpCount);
                }
            }
            if (columnAlias.equals("DocumentSummary.DEVICE_COUNT")) {
                if (sharedDeviceCount == null) {
                    sharedDeviceCount = 0;
                }
                if (!export) {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("sharedDeviceCount", (Object)sharedDeviceCount);
                    payload2.put("docId", docId);
                    columnProperties.put("PAYLOAD", payload2);
                }
                else {
                    final String shareDevices = String.valueOf(sharedDeviceCount);
                    columnProperties.put("VALUE", shareDevices);
                }
            }
            if (columnAlias.equals("DocumentSummary.USER_COUNT")) {
                if (sharedUserCount == null) {
                    sharedUserCount = 0;
                }
                if (!export) {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("sharedUserCount", (Object)sharedUserCount);
                    payload2.put("docId", docId);
                    columnProperties.put("PAYLOAD", payload2);
                }
                else {
                    final String shareUsers = String.valueOf(sharedUserCount);
                    columnProperties.put("VALUE", shareUsers);
                }
            }
            if (columnAlias.equals("STATUS")) {
                if (docSize == 0L) {
                    if (!export) {
                        final Long resourceId = docId;
                        final JSONObject payload2 = new JSONObject();
                        payload2.put("resourceId", (Object)resourceId);
                        columnProperties.put("PAYLOAD", payload2);
                    }
                    else {
                        columnProperties.put("VALUE", "");
                    }
                }
                else {
                    columnProperties.put("VALUE", "active");
                }
            }
            if (columnAlias.equals("tags")) {
                int tagsStrlen = 0;
                final JSONObject payload2 = new JSONObject();
                String hoverTagsStr = "";
                final JSONArray tags = DocMgmtDataHandler.getInstance().getDocsTag(docId);
                String tagsStr = "";
                for (int i = 0; i < tags.length(); ++i) {
                    tagsStrlen += tags.get(i).toString().length();
                    if (i == tags.length() - 1) {
                        tagsStr += tags.get(i);
                    }
                    else {
                        tagsStr = tagsStr + tags.get(i) + ", ";
                    }
                }
                if (tagsStr == "") {
                    tagsStr = "--";
                }
                if (tagsStr.length() >= 8) {
                    hoverTagsStr = tagsStr;
                }
                if (viewName.equalsIgnoreCase("mdmDocument") || viewName.equalsIgnoreCase("mdmDeviceDocuments") || viewName.equalsIgnoreCase("mdmGroupDocument") || viewName.equalsIgnoreCase("mdmPolicyDocumentList")) {
                    if (export) {
                        columnProperties.put("VALUE", tagsStr);
                    }
                    else {
                        payload2.put("hoverText", (Object)hoverTagsStr);
                        payload2.put("data", (Object)tagsStr);
                        columnProperties.put("PAYLOAD", payload2);
                    }
                }
                else {
                    columnProperties.put("VALUE", tagsStr);
                }
            }
            if (columnAlias.equalsIgnoreCase("DocumentDetails.DESCRIPTION")) {
                if (SyMUtil.isStringEmpty(description)) {
                    description = "--";
                }
                columnProperties.put("VALUE", description);
            }
            if (columnAlias.equals("DocumentDetails.DOC_ID")) {
                if (!export) {
                    final Long resourceId = (Long)data;
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("resourceId", (Object)resourceId);
                    columnProperties.put("PAYLOAD", payload2);
                }
                else {
                    columnProperties.put("VALUE", "");
                }
            }
            if (columnAlias.equals("DOC_TYPE") || columnAlias.equals("DocumentDetails.DOC_TYPE")) {
                final String docTypeName = DocMgmtDataHandler.getInstance().getDocExtention(docType);
                if (viewName.equalsIgnoreCase("mdmDeviceDocuments") || viewName.equalsIgnoreCase("mdmDocument") || viewName.equalsIgnoreCase("mdmGroupDocument") || viewName.equalsIgnoreCase("mdmPolicyDocumentList")) {
                    if (export) {
                        columnProperties.put("VALUE", docTypeName);
                    }
                    else {
                        final JSONObject payload2 = new JSONObject();
                        payload2.put("docTypeName", (Object)docTypeName);
                        payload2.put("docType", (Object)docType);
                        payload2.put("docName", (Object)docName);
                        columnProperties.put("PAYLOAD", payload2);
                    }
                }
                else if (!isExport) {
                    final String DocImage = DocMgmtDataHandler.getInstance().getDocImage(docType);
                    String docTypeValue = "";
                    if (MDMUtil.isStringValid(docTypeName)) {
                        docTypeValue = "<img title=" + docTypeName.substring(1, docTypeName.length()) + " class=\"viewImage\" src=" + DocImage + " width=\"38px\" >";
                    }
                    else {
                        docTypeValue = "<img class=\"viewImage\" src=" + DocImage + " width=\"38px\" >";
                    }
                    columnProperties.put("VALUE", docTypeValue);
                }
                else {
                    columnProperties.put("VALUE", docTypeName);
                }
            }
            if (columnAlias.equals("DocumentDetails.SIZE")) {
                String sizeLong = null;
                if (1048576L < docSize) {
                    sizeLong = String.format("%.1f", docSize / 1048576.0f) + " MB";
                }
                else if (1024L < docSize) {
                    sizeLong = String.format("%.1f", docSize / 1024.0f) + " KB";
                }
                else {
                    sizeLong = String.format("%.1f", docSize / 1.0f) + " B";
                }
                columnProperties.put("VALUE", sizeLong);
            }
            if (columnAlias.equals("DocumentDetails.UPDATED_TIME")) {
                final Date currentDate = new Date((long)data);
                final DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
                columnProperties.put("VALUE", df.format(currentDate));
            }
            if (columnAlias.equals("DocumentToDeviceGroup.ASSOCIATED_AT")) {
                final Date currentDate = new Date((long)data);
                final DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
                columnProperties.put("VALUE", df.format(currentDate));
            }
            if (columnAlias.equals("DocumentManagedDeviceInfo.ASSOCIATED_AT")) {
                final Date currentDate = new Date((long)data);
                final DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
                columnProperties.put("VALUE", df.format(currentDate));
            }
            if (columnAlias.equals("DocumentDetails.ADDED_TIME")) {
                final Date currentDate = new Date((long)data);
                final DateFormat df = new SimpleDateFormat("MMM dd hh:mm a");
                columnProperties.put("VALUE", df.format(currentDate));
            }
            if (columnAlias.equals("ConfigStatusDefn.STATUS_ID")) {
                final String statusLabel = (String)tableContext.getAssociatedPropertyValue("ConfigStatusDefn.StatusLabel");
                if (viewName.equalsIgnoreCase("mdmDeviceDocuments") || viewName.equalsIgnoreCase("mdmGroupDocument") || viewName.equalsIgnoreCase("mdmDocGroups")) {
                    if (export) {
                        columnProperties.put("VALUE", I18N.getMsg(statusLabel, new Object[0]));
                    }
                    else {
                        final JSONObject payload2 = new JSONObject();
                        payload2.put("data", data);
                        payload2.put("statusLabel", (Object)I18N.getMsg(statusLabel, new Object[0]));
                        columnProperties.put("PAYLOAD", payload2);
                    }
                }
            }
            if (columnAlias.equals("DocumentToDeviceGroup.CUSTOMGROUP_ID") || columnAlias.equals("DocumentManagedDeviceRel.MANAGEDDEVICE_ID")) {
                final Long resourceId = (Long)data;
                if (viewName.equalsIgnoreCase("mdmDeviceDocuments") || viewName.equalsIgnoreCase("mdmGroupDocument")) {
                    final JSONObject payload2 = new JSONObject();
                    payload2.put("docId", docId);
                    payload2.put("resourceId", (Object)resourceId);
                    columnProperties.put("PAYLOAD", payload2);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
    
    private List getAssociatedGroupNames(final ViewContext vc, final Long docID) {
        final HashMap hashMap = (HashMap)TransformerUtil.getPreValuesForTransformer(vc, "ASSOCIATED_GROUP_NAMES");
        return (hashMap.get(docID) != null) ? ((List)hashMap.get(docID)) : new ArrayList();
    }
}
