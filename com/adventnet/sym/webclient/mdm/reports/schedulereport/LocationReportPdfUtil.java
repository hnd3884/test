package com.adventnet.sym.webclient.mdm.reports.schedulereport;

import com.lowagie.text.FontFactory;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.lowagie.text.Image;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.adventnet.client.components.table.web.DefaultExportRedactHandler;
import com.adventnet.ds.query.Range;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import java.util.ArrayList;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;
import com.adventnet.i18n.I18N;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.adventnet.client.view.pdf.PDFTheme;
import com.adventnet.client.view.web.ViewContext;
import java.sql.Connection;
import javax.transaction.TransactionManager;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.export.pdf.TablePDFRenderer;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.common.ExportAuditModel;
import com.adventnet.db.api.RelationalAPI;
import java.util.List;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import javax.servlet.ServletContext;
import com.me.mdm.api.admin.security.ExportSettingFacade;
import java.io.OutputStream;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataAccess;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import java.io.FileOutputStream;
import com.adventnet.client.view.web.HttpReqWrapper;
import com.lowagie.text.Font;
import java.awt.Color;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.mdm.webclient.reports.PDFUtil;

public class LocationReportPdfUtil extends PDFUtil
{
    int intervalFilter;
    DCViewFilterCriteria dateCri;
    private static Logger logger;
    int exportType;
    public static Color table_header_color;
    public static Color even_row_color;
    public static Color odd_row_color;
    public static Color product_name_header_color;
    public static Font white_text_big;
    public static Font black_text_heading;
    public static Font black_text_medium;
    
    public void generateDeviceLocationPDFReport(final String viewName, final HttpReqWrapper reqWrapper, final FileOutputStream fos, final Long customerId) {
        TransactionManager txnMgr = null;
        Document doc = null;
        Connection connection = null;
        final long start = System.currentTimeMillis();
        final String landscapemode = reqWrapper.getParameter("landscape");
        if (landscapemode != null && landscapemode.equals("true")) {
            doc = new Document(PageSize.A4.rotate());
        }
        try {
            txnMgr = DataAccess.getTransactionManager();
            if (txnMgr.getTransaction() != null) {
                throw new Exception("A new transaction will be created for rendering PDF files in PDFUtil, therefore generatePDF method should not be invoked within transaction.");
            }
            txnMgr.setTransactionTimeout(86400000);
            txnMgr.begin();
            final ViewContext vc = com.adventnet.client.util.pdf.PDFUtil.getViewCtx((HttpServletRequest)reqWrapper, (Object)viewName);
            final PDFTheme theme = com.adventnet.client.util.pdf.PDFUtil.getThemeClass((HttpServletRequest)reqWrapper);
            if (doc == null) {
                doc = theme.getDocument(vc);
            }
            final PdfWriter writer = PdfWriter.getInstance(doc, (OutputStream)fos);
            doc.open();
            final JSONObject privacyObject = new ExportSettingFacade().getExportSettings().getJSONObject("scheduled_report_redact");
            if (privacyObject != null) {
                this.exportType = privacyObject.optInt("export_type");
                this.exportType = ((this.exportType == 4) ? privacyObject.optInt("selected_option") : this.exportType);
            }
            theme.startPDFDoc((ServletContext)null, vc, doc, writer);
            final SelectQuery selectQuery = new GeoLocationFacade().getLocationHistoryDetailsQuery(customerId);
            final String critJson = reqWrapper.getParameter("criteriaJSON");
            List<DCViewFilterCriteria> dcViewFilter = null;
            if (critJson != null) {
                dcViewFilter = DCViewFilter.dcViewFilterMapper(critJson).getDcViewFilterCriteriaList();
                for (int i = 0; i < dcViewFilter.size(); ++i) {
                    final DCViewFilterCriteria crit = dcViewFilter.get(i);
                    final String columnAlias = MDMUtil.getInstance().getColumnDetails(crit.getColumnID()).optString("columnName", (String)null);
                    if (columnAlias != null && columnAlias.equalsIgnoreCase("LOCATED_TIME_INTERVAL")) {
                        final List searchValue = crit.getSearchValue();
                        if (searchValue != null && !searchValue.isEmpty()) {
                            this.intervalFilter = Integer.valueOf(searchValue.get(0));
                        }
                        dcViewFilter.remove(crit);
                        --i;
                    }
                    else if (columnAlias != null && columnAlias.equalsIgnoreCase("LOCATED_TIME")) {
                        dcViewFilter.remove(this.dateCri = crit);
                        --i;
                    }
                }
                DCViewFilterUtil.getInstance().getDCViewFilterCriteria(selectQuery, (List)dcViewFilter);
            }
            final long count = this.getTotalLocationsCount(customerId);
            this.addProductNameHeader(doc);
            if (count > 5000L) {
                this.batchProcessByDevice(selectQuery, doc);
            }
            else {
                this.bulkProcess(selectQuery, doc, customerId);
            }
            theme.endPDFDoc((ServletContext)null, vc, doc, writer);
            connection = RelationalAPI.getInstance().getConnection();
            vc.setTransientState("CONNECTION", (Object)connection);
            vc.getViewModel(true);
            final ExportAuditModel exportInfo = new ExportAuditModel();
            exportInfo.setStartTime(start);
            exportInfo.setAccountID(WebClientUtil.getAccountId());
            exportInfo.setViewName((long)vc.getModel().getViewNameNo());
            exportInfo.setViewContext(vc);
            exportInfo.setExportedTime(System.currentTimeMillis());
            new TablePDFRenderer().auditExport(exportInfo);
            txnMgr.commit();
        }
        catch (final Exception e) {
            try {
                if (txnMgr.getTransaction() != null) {
                    txnMgr.rollback();
                }
            }
            catch (final Exception var11) {
                LocationReportPdfUtil.logger.log(Level.INFO, "Error while rollback : ", var11);
            }
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error genrating location report pdf", e);
        }
        finally {
            doc.close();
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (final Exception ex) {
                    LocationReportPdfUtil.logger.log(Level.SEVERE, "exception in closing connection", ex);
                }
            }
            LocationReportPdfUtil.logger.log(Level.INFO, "completed genrating location report pdf");
        }
    }
    
    private void addProductNameHeader(final Document doc) {
        try {
            final PdfPTable ptable = new PdfPTable(1);
            ptable.setWidthPercentage(100.0f);
            final PdfPCell productHeader = this.createLocationDataCell(I18N.getMsg("mdm.product.name", new Object[0]), PDFUtil.white_text_small, LocationReportPdfUtil.product_name_header_color);
            ptable.addCell(productHeader);
            doc.add((Element)ptable);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "exception while adding addProductNameHeader", e);
        }
    }
    
    private long getTotalLocationsCount(final Long customerId) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
        selectQuery.addJoin(new Join("MdDeviceLocationDetails", "Resource", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdDeviceLocationDetails", "ManagedDevice", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria crit = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        crit = crit.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        selectQuery.setCriteria(crit);
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID").count());
        int count = 0;
        try {
            count = DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error genrating location report pdf", e);
        }
        return count;
    }
    
    private void bulkProcess(final SelectQuery selectQuery, final Document doc, final long customerId) {
        try {
            final DataObject locationDetailsDO = DataAccess.get(selectQuery);
            final SelectQuery locationHistoryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            locationHistoryQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            locationHistoryQuery.addJoin(new Join("ManagedDevice", "MdDeviceLocationDetails", new String[] { "RESOURCE_ID" }, new String[] { "DEVICE_ID" }, 1));
            locationHistoryQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            locationHistoryQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_ADDRESS"));
            locationHistoryQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"), true));
            locationHistoryQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), true));
            final Criteria cxCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            locationHistoryQuery.setCriteria(cxCri.and(managedCri));
            if (this.dateCri != null) {
                final List<DCViewFilterCriteria> dcViewFilters = new ArrayList<DCViewFilterCriteria>();
                dcViewFilters.add(this.dateCri);
                DCViewFilterUtil.getInstance().getDCViewFilterCriteria(locationHistoryQuery, (List)dcViewFilters);
            }
            long lastLocatedTime = 0L;
            final long lastLocatedDevice = 0L;
            long interval = 0L;
            if (this.intervalFilter != 0) {
                interval = this.intervalFilter * 60000;
            }
            final DataObject locationHistoryDO = DataAccess.get(locationHistoryQuery);
            JSONArray deviceLocationHistory = new JSONArray();
            final Iterator deviceRows = locationDetailsDO.getRows("ManagedDevice");
            while (deviceRows.hasNext()) {
                final Row deviceRow = deviceRows.next();
                final long locatedDevice = (long)deviceRow.get("RESOURCE_ID");
                deviceLocationHistory = new JSONArray();
                this.setDeviceLocationHeader(doc, this.getDeviceDetails(locationDetailsDO, locatedDevice));
                lastLocatedTime = 0L;
                final Iterator rows = locationHistoryDO.getRows("MdDeviceLocationDetails", new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)locatedDevice, 0));
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final long locatedTimeInMilli = (long)row.get("LOCATED_TIME");
                    if (lastLocatedTime == 0L || locatedTimeInMilli - lastLocatedTime >= interval) {
                        final JSONObject deviceLocationDetails = new JSONObject();
                        deviceLocationDetails.put("located_time", (Object)Utils.getEventTime(Long.valueOf(locatedTimeInMilli)));
                        deviceLocationDetails.put("longitude", row.get("LONGITUDE"));
                        deviceLocationDetails.put("latitude", row.get("LATITUDE"));
                        deviceLocationDetails.put("address", row.get("LOCATION_ADDRESS"));
                        deviceLocationHistory.put((Object)deviceLocationDetails);
                        lastLocatedTime = locatedTimeInMilli;
                    }
                }
                this.setDeviceLocationData(doc, deviceLocationHistory);
            }
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error in bulk process", e);
        }
    }
    
    public void batchProcessByDevice(final SelectQuery selectQuery, final Document doc) {
        try {
            int startIndex = 0;
            JSONObject deviceDetails = null;
            DataObject locationDetailsDO;
            do {
                selectQuery.setRange(new Range(startIndex, 5000));
                startIndex += 5000;
                locationDetailsDO = DataAccess.get(selectQuery);
                final Iterator rows = locationDetailsDO.getRows("ManagedDevice");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final long locatedDeviceId = (long)row.get("RESOURCE_ID");
                    deviceDetails = this.getDeviceDetails(locationDetailsDO, locatedDeviceId);
                    this.setDeviceLocationHeader(doc, deviceDetails);
                    this.getAndSetDeviceLocationHistoryDetails(locatedDeviceId, doc);
                }
            } while (!locationDetailsDO.isEmpty());
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error in getDeviceLocationHistoryDetailsAsArray()-----", e);
        }
    }
    
    private JSONObject getDeviceDetails(final DataObject deviceDetailsDO, final long locatedDeviceId) {
        final JSONObject deviceDetails = new JSONObject();
        try {
            final long userId = (long)deviceDetailsDO.getRow("ManagedUserToDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)locatedDeviceId, 0)).get("MANAGED_USER_ID");
            long recentLocatedTime = 0L;
            final Row recentLocRow = deviceDetailsDO.getRow("MdDeviceLocationDetails", new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)locatedDeviceId, 0));
            if (recentLocRow != null) {
                recentLocatedTime = (long)recentLocRow.get("LOCATED_TIME");
            }
            final int platform = (int)deviceDetailsDO.getRow("ManagedDevice", new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)locatedDeviceId, 0)).get("PLATFORM_TYPE");
            final Row errCodeRow = deviceDetailsDO.getRow("MdDeviceLocationToErrCode", new Criteria(Column.getColumn("MdDeviceLocationToErrCode", "RESOURCE_ID"), (Object)locatedDeviceId, 0));
            String errorCode = null;
            if (errCodeRow != null) {
                errorCode = errCodeRow.get("ERROR_CODE").toString();
            }
            boolean locationEnabled = (boolean)deviceDetailsDO.getRow("LocationDeviceStatus", new Criteria(Column.getColumn("LocationDeviceStatus", "MANAGED_DEVICE_ID"), (Object)locatedDeviceId, 0)).get("IS_ENABLED");
            final Row lostStatusString = deviceDetailsDO.getRow("LostModeTrackInfo", new Criteria(Column.getColumn("LostModeTrackInfo", "RESOURCE_ID"), (Object)locatedDeviceId, 0));
            boolean isLost = false;
            if (lostStatusString != null) {
                final int lostStatus = Integer.parseInt(lostStatusString.get("TRACKING_STATUS").toString());
                isLost = (lostStatus != 0 && lostStatus != 5 && lostStatus != 3);
            }
            locationEnabled = (locationEnabled || isLost);
            deviceDetails.put("device_name", deviceDetailsDO.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)locatedDeviceId, 0)).get("NAME"));
            deviceDetails.put("user_name", deviceDetailsDO.getRow("USER_RESOURCE", new Criteria(Column.getColumn("USER_RESOURCE", "RESOURCE_ID"), (Object)userId, 0)).get("NAME"));
            deviceDetails.put("group_names", (Object)new GeoLocationFacade().getAssociatedGroupNames(deviceDetailsDO, userId, locatedDeviceId));
            deviceDetails.put("platform", platform);
            deviceDetails.put("geo_status", (Object)new GeoLocationFacade().getGeoStatus(errorCode, locationEnabled, locatedDeviceId, platform, recentLocatedTime));
            LocationReportPdfUtil.logger.log(Level.INFO, "goin to add location his for device {0}", locatedDeviceId);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error in get device details ", e);
        }
        return deviceDetails;
    }
    
    private void getAndSetDeviceLocationHistoryDetails(final Long deviceId, final Document doc) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_ADDRESS"));
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"), true));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), (Object)deviceId, 0));
            if (this.dateCri != null) {
                final List<DCViewFilterCriteria> dcViewFilters = new ArrayList<DCViewFilterCriteria>();
                dcViewFilters.add(this.dateCri);
                DCViewFilterUtil.getInstance().getDCViewFilterCriteria(selectQuery, (List)dcViewFilters);
            }
            long lastLocatedTime = 0L;
            long interval = 0L;
            if (this.intervalFilter != 0) {
                interval = this.intervalFilter * 60000;
            }
            int startIndex = 0;
            final JSONArray deviceLocationHistory = new JSONArray();
            DataObject locationDetailsDO;
            do {
                selectQuery.setRange(new Range(startIndex, 5000));
                startIndex += 5000;
                locationDetailsDO = DataAccess.get(selectQuery);
                final Iterator rows = locationDetailsDO.getRows("MdDeviceLocationDetails");
                while (rows.hasNext()) {
                    final Row row = rows.next();
                    final long locatedTimeInMilli = (long)row.get("LOCATED_TIME");
                    if (lastLocatedTime == 0L || locatedTimeInMilli - lastLocatedTime >= interval) {
                        final JSONObject deviceLocationDetails = new JSONObject();
                        deviceLocationDetails.put("located_time", (Object)Utils.getEventTime(Long.valueOf(locatedTimeInMilli)));
                        deviceLocationDetails.put("longitude", row.get("LONGITUDE"));
                        deviceLocationDetails.put("latitude", row.get("LATITUDE"));
                        deviceLocationDetails.put("address", row.get("LOCATION_ADDRESS"));
                        deviceLocationHistory.put((Object)deviceLocationDetails);
                        lastLocatedTime = locatedTimeInMilli;
                    }
                }
            } while (!locationDetailsDO.isEmpty());
            this.setDeviceLocationData(doc, deviceLocationHistory);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error populating history data", e);
        }
    }
    
    private void setDeviceLocationData(final Document doc, final JSONArray deviceLocationHistory) {
        try {
            doc.add((Element)this.createEmptySpace(1));
            final DefaultExportRedactHandler defaultExportRedactHandler = new DefaultExportRedactHandler();
            final PdfPTable pTable = new PdfPTable(4);
            pTable.setWidthPercentage(100.0f);
            final float[] columnWidth = { 50.0f, 12.5f, 12.5f, 25.0f };
            pTable.setWidths(columnWidth);
            pTable.addCell(this.createLocationDataCell(I18N.getMsg("mdm.geoLoc.located_at", new Object[0]), LocationReportPdfUtil.white_text_big, LocationReportPdfUtil.table_header_color));
            pTable.addCell(this.createLocationDataCell(I18N.getMsg("dc.mdm.geoLoc_latitude", new Object[0]), LocationReportPdfUtil.white_text_big, LocationReportPdfUtil.table_header_color));
            pTable.addCell(this.createLocationDataCell(I18N.getMsg("dc.mdm.geoLoc_longitude", new Object[0]), LocationReportPdfUtil.white_text_big, LocationReportPdfUtil.table_header_color));
            pTable.addCell(this.createLocationDataCell(I18N.getMsg("mdm.geoLoc.located_time", new Object[0]), LocationReportPdfUtil.white_text_big, LocationReportPdfUtil.table_header_color));
            if (deviceLocationHistory.length() < 1) {
                pTable.setComplete(true);
                doc.add((Element)pTable);
                final PdfPTable noDataTable = new PdfPTable(1);
                noDataTable.setWidthPercentage(100.0f);
                final PdfPCell noDataCell = this.createLocationDataCell(I18N.getMsg("dc.common.NO_DATA_AVAILABLE", new Object[0]), PDFUtil.black_text_small, Color.white);
                noDataCell.setHorizontalAlignment(1);
                noDataTable.addCell(noDataCell);
                doc.add((Element)noDataTable);
                doc.add((Element)this.createEmptySpace(1));
                this.addLine(doc);
                return;
            }
            for (int i = 0; i < deviceLocationHistory.length(); ++i) {
                final JSONObject details = deviceLocationHistory.getJSONObject(i);
                final Color bgColr = (i % 2 == 0) ? LocationReportPdfUtil.even_row_color : LocationReportPdfUtil.odd_row_color;
                String address = details.optString("address", "--");
                address = ((this.exportType == 1) ? defaultExportRedactHandler.mask(address) : ((this.exportType == 2) ? "--" : address));
                String latitude = details.optString("latitude", "--");
                latitude = ((this.exportType == 1) ? defaultExportRedactHandler.mask(latitude) : ((this.exportType == 2) ? "--" : latitude));
                String longitude = details.optString("longitude", "--");
                longitude = ((this.exportType == 1) ? defaultExportRedactHandler.mask(longitude) : ((this.exportType == 2) ? "--" : longitude));
                pTable.addCell(this.createLocationDataCell(address, PDFUtil.black_text_small, bgColr));
                pTable.addCell(this.createLocationDataCell(latitude, PDFUtil.black_text_small, bgColr));
                pTable.addCell(this.createLocationDataCell(longitude, PDFUtil.black_text_small, bgColr));
                pTable.addCell(this.createLocationDataCell(details.optString("located_time", "--"), PDFUtil.black_text_small, bgColr));
            }
            pTable.setComplete(true);
            doc.add((Element)pTable);
            doc.add((Element)this.createEmptySpace(1));
            this.addLine(doc);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error in adding device data in location pdf report", e);
        }
    }
    
    private void addLine(final Document doc) throws Exception {
        final PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100.0f);
        final PdfPCell cell = new PdfPCell(new Phrase());
        cell.setBackgroundColor(Color.lightGray);
        cell.setFixedHeight(2.5f);
        cell.setBorder(0);
        lineTable.addCell(cell);
        doc.add((Element)lineTable);
    }
    
    private PdfPCell createLocationDataCell(final String text, final Font font, final Color bgColr) {
        final PdfPCell dataCell1 = new PdfPCell(new Phrase(text, font));
        dataCell1.setBackgroundColor(bgColr);
        dataCell1.setMinimumHeight(20.0f);
        dataCell1.setBorder(0);
        dataCell1.setPaddingLeft(7.0f);
        dataCell1.setVerticalAlignment(5);
        return dataCell1;
    }
    
    private void setDeviceLocationHeader(final Document doc, final JSONObject deviceDetails) {
        try {
            final DefaultExportRedactHandler defaultExportRedactHandler = new DefaultExportRedactHandler();
            doc.add((Element)this.createEmptySpace(1));
            final PdfPTable deviceDetailsTable = new PdfPTable(2);
            deviceDetailsTable.setWidthPercentage(100.0f);
            final float[] columnWidth = { 9.0f, 91.0f };
            deviceDetailsTable.setWidths(columnWidth);
            deviceDetailsTable.addCell(this.getLogoCell(deviceDetails.optInt("platform")));
            String deviceNameString = deviceDetails.optString("device_name", "--");
            deviceNameString = ((this.exportType == 1) ? defaultExportRedactHandler.mask(deviceNameString) : ((this.exportType == 2) ? "--" : deviceNameString));
            final PdfPCell deviceNameCell = new PdfPCell(new Phrase(deviceNameString, LocationReportPdfUtil.black_text_heading));
            deviceNameCell.setBackgroundColor(Color.white);
            deviceNameCell.setBorder(0);
            deviceNameCell.setVerticalAlignment(5);
            deviceNameCell.setPaddingRight(1.0f);
            deviceNameCell.setPaddingLeft(1.0f);
            deviceDetailsTable.addCell(deviceNameCell);
            String userNameString = deviceDetails.optString("user_name", "--");
            userNameString = ((this.exportType == 1) ? defaultExportRedactHandler.mask(userNameString) : ((this.exportType == 2) ? "--" : userNameString));
            final String subHead = I18N.getMsg("dc.common.USER_NAME", new Object[0]) + "   :   " + userNameString + "     |     " + I18N.getMsg("mdm.geoLoc.geo_status", new Object[0]) + "   :   " + deviceDetails.optString("geo_status", "--") + "     | \n" + I18N.getMsg("dc.common.GROUPS", new Object[0]) + "   :   " + deviceDetails.optString("group_names", "--");
            deviceDetailsTable.addCell(this.createSubHeaderCell(subHead, false, false));
            doc.add((Element)deviceDetailsTable);
        }
        catch (final Exception e) {
            LocationReportPdfUtil.logger.log(Level.SEVERE, "error in adding device header in location pdf report", e);
        }
    }
    
    private PdfPCell getLogoCell(final int platform) {
        PdfPCell logoCell = new PdfPCell();
        try {
            final String logoPath = this.getLogoPath(platform);
            Chunk chunk = new Chunk("");
            if (!logoPath.isEmpty()) {
                final Image image = Image.getInstance(logoPath);
                image.scaleToFit(127.0f, 34.0f);
                chunk = new Chunk(image, 0.0f, 0.0f);
            }
            logoCell = new PdfPCell(new Phrase(chunk));
            logoCell.setBorder(0);
            logoCell.setRowspan(2);
            logoCell.setVerticalAlignment(4);
        }
        catch (final Exception ex) {
            LocationReportPdfUtil.logger.log(Level.WARNING, "location pdf gen - getLogoCell : ", ex);
        }
        return logoCell;
    }
    
    private String getLogoPath(final int platform) {
        String path = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "location_report_platform_logos" + File.separator;
        switch (platform) {
            case 1: {
                path += "platform_1.png";
                break;
            }
            case 2: {
                path += "platform_2.png";
                break;
            }
            case 3: {
                path += "platform_3.png";
                break;
            }
            case 4: {
                path += "platform_4.png";
                break;
            }
            default: {
                path = "";
                break;
            }
        }
        return path;
    }
    
    private PdfPTable createEmptySpace(final int rows) {
        final PdfPTable table_spaces = new PdfPTable(1);
        table_spaces.setWidthPercentage(100.0f);
        final PdfPCell emptycells = new PdfPCell(new Phrase(""));
        emptycells.setBackgroundColor(Color.white);
        emptycells.setMinimumHeight(20.0f);
        emptycells.setBorder(0);
        emptycells.setPaddingTop(1.0f);
        emptycells.setPaddingBottom(1.0f);
        emptycells.setPaddingRight(1.0f);
        emptycells.setPaddingLeft(1.0f);
        for (int i = 0; i < rows; ++i) {
            table_spaces.addCell(emptycells);
        }
        return table_spaces;
    }
    
    private PdfPCell createSubHeaderCell(final String string, final boolean border, final boolean leftpadding) {
        final PdfPCell cell = new PdfPCell(new Phrase(string, LocationReportPdfUtil.black_text_medium));
        cell.setBackgroundColor(Color.white);
        cell.setBorder(0);
        cell.setMinimumHeight(20.0f);
        if (border) {
            cell.setBorderWidthRight(1.0f);
            cell.setBorderColor(Color.gray);
        }
        cell.setVerticalAlignment(5);
        cell.setPaddingRight(1.0f);
        if (leftpadding) {
            cell.setPaddingLeft(10.0f);
        }
        cell.setLeading(15.0f, 0.0f);
        return cell;
    }
    
    private PdfPTable setEmptyCell(final PdfPTable table, final int cellCount, final int rowSpan) {
        final Phrase spacePhrase = new Phrase();
        spacePhrase.add((Object)new Chunk(""));
        final PdfPCell spaceCell = new PdfPCell(spacePhrase);
        spaceCell.setBorder(0);
        spaceCell.setRowspan(rowSpan);
        for (int i = 0; i < cellCount; ++i) {
            table.addCell(spaceCell);
        }
        return table;
    }
    
    static {
        LocationReportPdfUtil.logger = Logger.getLogger("ScheduleReportLogger");
        LocationReportPdfUtil.table_header_color = new Color(7637139);
        LocationReportPdfUtil.even_row_color = new Color(16316664);
        LocationReportPdfUtil.odd_row_color = new Color(15921906);
        LocationReportPdfUtil.product_name_header_color = new Color(4819654);
        LocationReportPdfUtil.white_text_big = FontFactory.getFont("Lato", 10.0f, 0, Color.WHITE);
        LocationReportPdfUtil.black_text_heading = FontFactory.getFont("Lato", 12.0f, 0, Color.BLACK);
        LocationReportPdfUtil.black_text_medium = FontFactory.getFont("Lato", 9.0f, 0, Color.BLACK);
    }
}
