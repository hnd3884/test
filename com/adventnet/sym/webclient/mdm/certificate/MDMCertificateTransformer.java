package com.adventnet.sym.webclient.mdm.certificate;

import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMCertificateTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMCertificateTransformer() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = false;
        if (reportType != 4) {
            isExport = true;
        }
        if (!columnalias.equalsIgnoreCase("checkbox_column") && !columnalias.equalsIgnoreCase("CredentialCertificateInfo.CERTIFICATE_ID") && !columnalias.equalsIgnoreCase("Checkbox") && !columnalias.equalsIgnoreCase("SCEPConfigurations.SCEP_CONFIG_ID")) {
            return super.checkIfColumnRendererable(tableContext);
        }
        if (isExport) {
            return false;
        }
        final boolean hasCertificateWritePrivillage = request.isUserInRole("MDM_CertMgmt_Admin") && TransformerUtil.hasUserAllDeviceScopeGroup(vc, false);
        return hasCertificateWritePrivillage;
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMCertificateTransformer renderHeader().....");
        super.renderHeader(tableContext);
        final ViewContext viewCtx = tableContext.getViewContext();
        final int renderType = viewCtx.getRenderType();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String head = tableContext.getDisplayName();
        final int reportType = tableContext.getViewContext().getRenderType();
        boolean isExport = false;
        if (reportType != 4) {
            isExport = true;
        }
        if (head.equals("Action") && isExport) {
            headerProperties.put("VALUE", "");
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering MDMCertificateTransformer renderCell...");
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            this.logger.log(Level.FINE, "Columnalais : ", columnalais);
            final int reportType = tableContext.getViewContext().getRenderType();
            final String viewname = tableContext.getViewContext().getUniqueId();
            boolean isExport = false;
            if (reportType != 4) {
                isExport = true;
            }
            if (columnalais.equalsIgnoreCase("CredentialCertificateInfo.CERTIFICATE_DISPLAY_NAME") || columnalais.equalsIgnoreCase("PROFILE_COUNT") || columnalais.equalsIgnoreCase("DEVICE_COUNT") || columnalais.equalsIgnoreCase("SCEPConfigurations.SCEP_CONFIGURATION_NAME")) {
                String viewContent = "";
                long cellIdforCheck = 0L;
                if (viewname.equalsIgnoreCase("mdmCertDetails")) {
                    cellIdforCheck = (long)tableContext.getAssociatedPropertyValue("CredentialCertificateInfo.CERTIFICATE_ID");
                }
                else if (viewname.equalsIgnoreCase("mdmTemplateDetails")) {
                    cellIdforCheck = (long)tableContext.getAssociatedPropertyValue("SCEPConfigurations.SCEP_CONFIG_ID");
                }
                if (columnalais.equalsIgnoreCase("CredentialCertificateInfo.CERTIFICATE_DISPLAY_NAME") || columnalais.equalsIgnoreCase("SCEPConfigurations.SCEP_CONFIGURATION_NAME")) {
                    viewContent = "details";
                }
                else if (columnalais.equalsIgnoreCase("PROFILE_COUNT")) {
                    final HashMap hashMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("ASSOCIATED_PROFILES");
                    final Long certID = (Long)tableContext.getAssociatedPropertyValue("Certificates.CERTIFICATE_RESOURCE_ID");
                    Integer count = hashMap.get(certID);
                    if (count == null) {
                        count = 0;
                    }
                    data = count;
                    viewContent = "profile";
                }
                else if (columnalais.equalsIgnoreCase("DEVICE_COUNT")) {
                    final HashMap hashMap = (HashMap)tableContext.getViewContext().getRequest().getAttribute("ASSOCIATED_DEVICES");
                    final Long certID = (Long)tableContext.getAssociatedPropertyValue("CredentialCertificateInfo.CERTIFICATE_ID");
                    Integer count = hashMap.get(certID);
                    if (count == null) {
                        count = 0;
                    }
                    data = count;
                    viewContent = "device";
                }
                if (!isExport) {
                    final JSONObject payloadData = new JSONObject();
                    if (viewname.equalsIgnoreCase("mdmTemplateDetails")) {
                        final long serverId = (long)tableContext.getAssociatedPropertyValue("SCEPServers.SERVER_ID");
                        payloadData.put("serverId", (Object)String.valueOf(serverId));
                    }
                    payloadData.put("viewContent", (Object)viewContent);
                    payloadData.put("cellId", (Object)String.valueOf(cellIdforCheck));
                    payloadData.put("cellValue", data);
                    columnProperties.put("PAYLOAD", payloadData);
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
            if (columnalais.equalsIgnoreCase("CredentialCertificateInfo.CERTIFICATE_ID")) {
                final JSONObject payloadData2 = new JSONObject();
                final String displayName = (String)tableContext.getAssociatedPropertyValue("CredentialCertificateInfo.CERTIFICATE_DISPLAY_NAME");
                payloadData2.put("certificateId", (Object)String.valueOf(data));
                payloadData2.put("certificateName", (Object)displayName);
                columnProperties.put("PAYLOAD", payloadData2);
            }
            if (columnalais.equalsIgnoreCase("ASSOCIATED_PAYLOADS")) {
                final List associatedPayloadList = new ArrayList();
                final Integer wifiPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("wifi.payload_count");
                final Integer vpnPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("vpn.payload_count");
                final Integer ssoPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("sso.payload_count");
                final Integer emailPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("email.payload_count");
                final Integer exchangePayloadCount = (Integer)tableContext.getAssociatedPropertyValue("exchange.payload_count");
                final Integer scepPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("scep.payload_count");
                final Integer certPayloadCount = (Integer)tableContext.getAssociatedPropertyValue("cert.payload_count");
                if (wifiPayloadCount != null && wifiPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.wi_fi", new Object[0]));
                }
                if (vpnPayloadCount != null && vpnPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.allow_vpn", new Object[0]));
                }
                if (ssoPayloadCount != null && ssoPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("mdm.profile.ssoPolicy", new Object[0]));
                }
                if (emailPayloadCount != null && emailPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.email", new Object[0]));
                }
                if (exchangePayloadCount != null && exchangePayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.exchange_activesync", new Object[0]));
                }
                if (scepPayloadCount != null && scepPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.scep", new Object[0]));
                }
                if (certPayloadCount != null && certPayloadCount != 0) {
                    associatedPayloadList.add(I18N.getMsg("dc.mdm.device_mgmt.certificate", new Object[0]));
                }
                final String delim = ", ";
                String associatedPayload = "--";
                if (associatedPayloadList.size() != 0) {
                    associatedPayload = String.join(delim, associatedPayloadList);
                }
                columnProperties.put("VALUE", associatedPayload);
            }
            if (columnalais.equalsIgnoreCase("SCEPConfigurations.CHALLENGE_TYPE")) {
                String value = "--";
                if (data != null && data != "") {
                    final Integer dataValue = Integer.parseInt(String.valueOf(data));
                    if (dataValue == 0) {
                        value = I18N.getMsg("mdm.common.none", new Object[0]);
                    }
                    else if (dataValue == 1) {
                        value = I18N.getMsg("dc.admin.cg.group_static", new Object[0]);
                    }
                    else if (dataValue == 2) {
                        value = I18N.getMsg("mdm.common.dynamic", new Object[0]);
                    }
                }
                columnProperties.put("VALUE", value);
            }
            if (columnalais.equalsIgnoreCase("SCEPConfigurations.SCEP_CONFIG_ID")) {
                final JSONObject payloadData2 = new JSONObject();
                final String displayName = (String)tableContext.getAssociatedPropertyValue("SCEPConfigurations.SCEP_CONFIGURATION_NAME");
                final long serverId2 = (long)tableContext.getAssociatedPropertyValue("SCEPServers.SERVER_ID");
                payloadData2.put("templateId", (Object)String.valueOf(data));
                payloadData2.put("serverId", (Object)String.valueOf(serverId2));
                payloadData2.put("templateName", (Object)displayName);
                columnProperties.put("PAYLOAD", payloadData2);
            }
            if (columnalais.equalsIgnoreCase("SCEPConfigurations.SUBJECT") || columnalais.equalsIgnoreCase("SCEPConfigurations.CHALLENGE_ENCRYPTED") || columnalais.equalsIgnoreCase("SCEPConfigurations.SUBJECT_ALTNAME_VALUE") || columnalais.equalsIgnoreCase("SCEPConfigurations.NT_PRINCIPAL") || columnalais.equalsIgnoreCase("SCEPConfigurations.CA_FINGER_PRINT")) {
                if (MDMStringUtils.isEmpty(data.toString())) {
                    columnProperties.put("VALUE", "--");
                }
                else {
                    columnProperties.put("VALUE", data);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
