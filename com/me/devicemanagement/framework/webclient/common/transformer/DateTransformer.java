package com.me.devicemanagement.framework.webclient.common.transformer;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import java.sql.Timestamp;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class DateTransformer extends RolecheckerTransformer
{
    public Logger logger;
    
    public DateTransformer() {
        this.logger = Logger.getLogger(DateTransformer.class.getName());
    }
    
    @Override
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final String columnalias = tableContext.getPropertyName();
            final String viewName = tableContext.getViewContext().getUniqueId();
            if (viewName.equalsIgnoreCase("CQListView") && columnalias.equalsIgnoreCase("CRSaveViewDetails.LAST_MODIFIED_TIME")) {
                CustomerInfoUtil.getInstance();
                if (CustomerInfoUtil.isSAS()) {
                    return false;
                }
            }
        }
        catch (final Exception e) {
            this.logger.severe("Issue " + e);
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final Object propsValue = tableContext.getPropertyValue();
            final ViewContext viewContext = tableContext.getViewContext();
            final HttpServletRequest request = viewContext.getRequest();
            final String isExport = request.getParameter("isExport");
            if (propsValue != null) {
                String value = "--";
                this.logger.log(Level.FINE, "propsValue value and columnalais value is {0},{1} ", new Object[] { propsValue.toString(), columnalais });
                if (propsValue instanceof Timestamp) {
                    columnProperties.clear();
                    if (isExport != null && isExport.equalsIgnoreCase("true")) {
                        value = Utils.getTime(((Timestamp)propsValue).getTime());
                    }
                    else {
                        value = Utils.getEventTime((Timestamp)propsValue);
                    }
                    columnProperties.put("VALUE", value);
                }
                else if ((long)propsValue <= 0L) {
                    columnProperties.put("VALUE", "--");
                }
                else if (this.getDateColumnAliasName().contains(columnalais)) {
                    if (isExport != null && isExport.equalsIgnoreCase("true")) {
                        value = Utils.getDate((Long)propsValue);
                    }
                    else {
                        value = Utils.getEventDate((Long)propsValue);
                    }
                    columnProperties.put("VALUE", value);
                }
                else if ("PackageDownloadStatus.DOWNLOAD_TIME".equalsIgnoreCase(columnalais)) {
                    if (isExport != null && isExport.equalsIgnoreCase("true")) {
                        value = Utils.getTime((long)propsValue * 1000L);
                    }
                    else {
                        value = Utils.getEventTime((long)propsValue * 1000L);
                    }
                    columnProperties.put("VALUE", value);
                }
                else {
                    if (isExport != null && isExport.equalsIgnoreCase("true")) {
                        value = Utils.getTime((Long)propsValue);
                    }
                    else {
                        value = Utils.getEventTime((Long)propsValue);
                    }
                    columnProperties.put("VALUE", value);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while render cell....", e);
        }
    }
    
    public List getDateColumnAliasName() {
        final List dateColumn = new ArrayList();
        dateColumn.add("InvSWInstalled.INSTALLED_DATE");
        dateColumn.add("InvComputerExtn.SHIPPING_DATE");
        dateColumn.add("InvComputerExtn.WARRANTY_EXPIRY_DATE");
        dateColumn.add("LICENSESUBQUERY.PURCHASE_DATE");
        dateColumn.add("LICENSESUBQUERY.EXPIRY_DATE");
        dateColumn.add("InvSWLicenseDetails.PURCHASE_DATE");
        dateColumn.add("InvSWLicenseDetails.EXPIRY_DATE");
        dateColumn.add("dc.common.RELEASE_DATE");
        dateColumn.add("pd.RELEASEDTIME");
        dateColumn.add("PatchDetails.RELEASEDTIME");
        dateColumn.add("Vulnerability.PUBLISHEDTIME");
        dateColumn.add("Vulnerability.UPDATEDTIME");
        dateColumn.add("Vulnerability.SUPPORTEDTIME");
        dateColumn.add("SupportedEolDetails.EOLDATE");
        dateColumn.add("Hardening.PUBLISHEDDATE");
        dateColumn.add("Hardening.UPDATEDDATE");
        dateColumn.add("TechVulDiscoverAge.DISCOVERED_AGE");
        dateColumn.add("InvBios.RELEASE_DATE");
        dateColumn.add("ToolsRemoteSoftware.SW_INSTALLED_DATE");
        dateColumn.add("pd.SUPPORTEDTIME");
        dateColumn.add("PatchDetails.SUPPORTEDTIME");
        return dateColumn;
    }
}
