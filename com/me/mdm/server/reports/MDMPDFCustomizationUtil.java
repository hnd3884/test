package com.me.mdm.server.reports;

import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Chunk;
import com.me.mdm.webclient.reports.PDFUtil;
import com.adventnet.i18n.I18N;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.client.components.table.web.DefaultExportRedactHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.api.admin.security.ExportSettingFacade;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import com.lowagie.text.Document;

public class MDMPDFCustomizationUtil
{
    public void customizeMDMReport(final Document doc, final String reportId, final HttpServletRequest request) throws Exception {
        if (reportId != null) {
            switch (Integer.valueOf(reportId)) {
                case 40034:
                case 40037:
                case 41009:
                case 41010: {
                    final Long resourceId = Long.valueOf(request.getParameter("RESOURCE_ID"));
                    this.addDeviceDetails(doc, resourceId);
                    break;
                }
                case 40020:
                case 40076: {
                    final Long appGroupId = Long.valueOf(request.getParameter("APP_GROUP_ID"));
                    this.addAppDetails(doc, appGroupId);
                    break;
                }
            }
        }
    }
    
    private void addDeviceDetails(final Document doc, final Long resourceId) throws Exception {
        final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        final JSONObject privacyObject = new ExportSettingFacade().getUserExportRedactType(userID);
        if (privacyObject != null && privacyObject.optInt("export_type") != 2 && (privacyObject.optInt("export_type") != 4 || privacyObject.optInt("selected_option") != 2)) {
            final JSONObject deviceObject = InventoryUtil.getInstance().getDeviceDetails(resourceId);
            String deviceName = String.valueOf(deviceObject.get("NAME"));
            final JSONObject userJSON = deviceObject.getJSONObject("user");
            String emailAddress = String.valueOf(userJSON.get("EMAIL_ADDRESS"));
            String userName = String.valueOf(userJSON.get("NAME"));
            int exportType = privacyObject.optInt("export_type");
            exportType = ((exportType == 4) ? privacyObject.optInt("selected_option") : exportType);
            if (exportType == 1) {
                final DefaultExportRedactHandler defaultExportRedactHandler = new DefaultExportRedactHandler();
                userName = defaultExportRedactHandler.mask(userName);
                deviceName = defaultExportRedactHandler.mask(deviceName);
                emailAddress = defaultExportRedactHandler.email(emailAddress);
            }
            final PdfPTable userField = new PdfPTable(2);
            userField.setWidthPercentage(100.0f);
            final Phrase userName_title = new Phrase();
            userName_title.add((Object)new Chunk(I18N.getMsg("dc.windowslivetools.processes.USER_NAME", new Object[0]) + " : " + userName, PDFUtil.black_text_small));
            final PdfPCell userNameCell = new PdfPCell(userName_title);
            userNameCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            userNameCell.setBorder(0);
            final Phrase userEmail_title = new Phrase();
            userEmail_title.add((Object)new Chunk(I18N.getMsg("dc.common.email", new Object[0]) + " : " + emailAddress, PDFUtil.black_text_small));
            final PdfPCell userEmailField = new PdfPCell(userEmail_title);
            userEmailField.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            userEmailField.setBorder(0);
            userEmailField.setHorizontalAlignment(2);
            userField.addCell(userNameCell);
            userField.addCell(userEmailField);
            userField.setHorizontalAlignment(0);
            doc.add((Element)userField);
            final PdfPTable deviceNameField = new PdfPTable(1);
            deviceNameField.setWidthPercentage(100.0f);
            final Phrase deviceNamePhrase = new Phrase();
            deviceNamePhrase.add((Object)new Chunk(I18N.getMsg("dc.mdm.enroll.device_name", new Object[0]) + " : " + deviceName, PDFUtil.black_text_small));
            final PdfPCell deviceNameCell = new PdfPCell(deviceNamePhrase);
            deviceNameCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
            deviceNameCell.setBorder(0);
            deviceNameField.addCell(deviceNameCell);
            deviceNameField.setHorizontalAlignment(0);
            doc.add((Element)deviceNameField);
        }
    }
    
    private void addAppDetails(final Document doc, final Long appGroupId) throws Exception {
        final Row appGroupRow = DBUtil.getRowFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appGroupId);
        final String displayName = (String)appGroupRow.get("GROUP_DISPLAY_NAME");
        final String identifier = (String)appGroupRow.get("IDENTIFIER");
        final int platform = (int)appGroupRow.get("PLATFORM_TYPE");
        final PdfPTable appField = new PdfPTable(2);
        appField.setWidthPercentage(100.0f);
        final Phrase appName_title = new Phrase();
        appName_title.add((Object)new Chunk(I18N.getMsg("dc.mdm.reports.app_name", new Object[0]) + " : " + displayName, PDFUtil.black_text_small));
        final PdfPCell appNameCell = new PdfPCell(appName_title);
        appNameCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
        appNameCell.setBorder(0);
        final Phrase appIdentifier_title = new Phrase();
        appIdentifier_title.add((Object)new Chunk(I18N.getMsg("dc.mdm.actionlog.appmgmt.bundle_identifier", new Object[0]) + " : " + identifier, PDFUtil.black_text_small));
        final PdfPCell appIdentifierField = new PdfPCell(appIdentifier_title);
        appIdentifierField.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
        appIdentifierField.setBorder(0);
        appIdentifierField.setHorizontalAlignment(2);
        appField.addCell(appNameCell);
        appField.addCell(appIdentifierField);
        appField.setHorizontalAlignment(0);
        doc.add((Element)appField);
        final PdfPTable platformField = new PdfPTable(1);
        platformField.setWidthPercentage(100.0f);
        final Phrase platformNamePhrase = new Phrase();
        platformNamePhrase.add((Object)new Chunk(I18N.getMsg("dc.mdm.group.view.Platform_Type", new Object[0]) + " : " + MDMUtil.getInstance().getPlatformName(platform), PDFUtil.black_text_small));
        final PdfPCell platformNameCell = new PdfPCell(platformNamePhrase);
        platformNameCell.setBackgroundColor(PDFUtil.TAB_HEADER_BLUE);
        platformNameCell.setBorder(0);
        platformField.addCell(platformNameCell);
        platformField.setHorizontalAlignment(0);
        doc.add((Element)platformField);
    }
}
