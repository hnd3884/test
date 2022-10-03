package com.me.mdm.server.profiles.font;

import org.json.JSONArray;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import com.me.mdm.server.profiles.ProfilePayloadOperator;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.mdm.files.MDMFileUtil;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.FontFormatException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.me.mdm.server.payload.PayloadException;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.awt.Font;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfilePayloadMapping;
import java.util.List;

public class FontDetailsHandler
{
    public static final String FONT_FILE_PATH = "FONT_FILE_PATH";
    public static final int FONT_TRUE_TYPE = 1;
    public static final int FONT_OPEN_TYPE = 2;
    private static final String FONT_TRUE_TYPE_FILE_NAME = "font.ttf";
    private static final String FONT_OPEN_TYPE_FILE_NAME = "font.otf";
    List<ProfilePayloadMapping> profilePayloadMappings;
    HashMap unConfigureMap;
    
    public FontDetailsHandler() {
        this.profilePayloadMappings = null;
        this.unConfigureMap = null;
        this.profilePayloadMappings = new ArrayList<ProfilePayloadMapping>();
        this.unConfigureMap = new HashMap();
        this.profilePayloadMappings.add(new FontMapping("CfgDataItemToFontRel", "FONT_ID", true, true));
        final List fontTable = new ArrayList();
        fontTable.add(526);
        fontTable.add(763);
        this.unConfigureMap.put("CfgDataItemToFontRel", fontTable);
    }
    
    public Object addFontDetails(final JSONObject jsonObject, final DataObject dataObject) throws Exception {
        Long fontId = null;
        try {
            final DataObject fontDataObject = (DataObject)new WritableDataObject();
            final String fontFilePath = jsonObject.getString("FONT_FILE_PATH");
            final InputStream fontStream = ApiFactoryProvider.getFileAccessAPI().getInputStream(fontFilePath);
            final int fontDetectedType = jsonObject.getInt("FONT_TYPE");
            final int fontType = this.getFontTypeForExtraction(fontFilePath);
            final Font font = Font.createFont(fontType, fontStream);
            final String fontName = font.getFontName();
            final String fontFamily = font.getFamily();
            final Long customerId = jsonObject.getLong("CUSTOMER_ID");
            final String userGivenFontName = jsonObject.getString("NAME");
            final Criteria fontCriteria = new Criteria(new Column("FontDetails", "NAME"), (Object)userGivenFontName, 0, false).and(new Criteria(new Column("FontDetails", "CUSTOMER_ID"), (Object)customerId, 0));
            final int count = MDMDBUtil.getRecordCount("FontDetails", "FONT_ID", fontCriteria);
            if (count > 0) {
                throw new PayloadException("COM0010");
            }
            Row fontRow = new Row("FontDetails");
            fontRow.set("NAME", (Object)userGivenFontName);
            fontRow.set("FONT_NAME", (Object)fontName);
            fontRow.set("FONT_FAMILY_NAME", (Object)fontFamily);
            fontRow.set("CUSTOMER_ID", (Object)customerId);
            fontRow.set("FONT_TYPE", (Object)fontDetectedType);
            fontDataObject.addRow(fontRow);
            MDMUtil.getPersistenceLite().add(fontDataObject);
            fontRow = fontDataObject.getRow("FontDetails");
            fontId = (Long)fontRow.get("FONT_ID");
            this.handleFontPath(fontId, fontFilePath, customerId, fontDetectedType);
            dataObject.updateBlindly(fontRow);
        }
        catch (final FontFormatException e) {
            throw new PayloadException("PAY0010");
        }
        catch (final PayloadException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exeption in font detail handler", e3);
            throw e3;
        }
        return fontId;
    }
    
    protected int getFontTypeForExtraction(final String fontPath) {
        return 0;
    }
    
    protected String handleFontPath(final Long fontId, final String fontPath, final Long customerId, final int fontType) {
        String fontDBCompletePath = "";
        if (!MDMStringUtils.isEmpty(fontPath)) {
            final String fileName = this.getFontNameForFontType(fontType);
            final String customProfileDBpath = ProfileUtil.getFontDBPath(fontId, customerId);
            final String customProfileFolderPath = ProfileUtil.getFontFolderPath(fontId, customerId);
            MDMFileUtil.uploadFileToDirectory(fontPath, customProfileFolderPath, fileName);
            fontDBCompletePath = customProfileDBpath + File.separator + fileName;
            fontDBCompletePath = fontDBCompletePath.replaceAll("\\\\", "/");
        }
        return fontDBCompletePath;
    }
    
    protected String getFontNameForFontType(final int fontType) {
        String fontName = null;
        switch (fontType) {
            case 1: {
                fontName = "font.ttf";
                break;
            }
            case 2: {
                fontName = "font.otf";
                break;
            }
        }
        return fontName;
    }
    
    public String getFontFilePath(final Row fontRow) {
        final String webDirPath = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final int fontType = (int)fontRow.get("FONT_TYPE");
        final Long fontId = (Long)fontRow.get("FONT_ID");
        final Long customerId = (Long)fontRow.get("CUSTOMER_ID");
        String fontPath = ProfileUtil.getFontDBPath(fontId, customerId) + File.separator + this.getFontNameForFontType(fontType);
        fontPath = webDirPath + File.separator + fontPath;
        return fontPath;
    }
    
    private String getFontFolderPath(final Row fontRow) {
        final int fontType = (int)fontRow.get("FONT_TYPE");
        final Long fontId = (Long)fontRow.get("FONT_ID");
        final Long customerId = (Long)fontRow.get("CUSTOMER_ID");
        final String fontPath = ProfileUtil.getFontFolderPath(fontId, customerId) + File.separator + this.getFontNameForFontType(fontType);
        return fontPath;
    }
    
    public void handleDeleteFonts(final List fontList, final Long customerId, final Long userId) throws Exception {
        new ProfilePayloadOperator(this.profilePayloadMappings, this.unConfigureMap).performPayloadOperation(fontList, customerId, userId, -1L, true, true);
    }
    
    public void deleteFontsInFile(final List<Long> fontList, final DataObject dataObject) throws Exception {
        for (final Long fontId : fontList) {
            final Criteria criteria = new Criteria(new Column("FontDetails", "FONT_ID"), (Object)fontId, 0);
            final Row row = dataObject.getRow("FontDetails", criteria);
            final String fontPath = this.getFontFolderPath(row);
            if (ApiFactoryProvider.getFileAccessAPI().deleteDirectory(fontPath)) {
                Logger.getLogger("MDMConfigLogger").log(Level.INFO, "Deleted Font for {0}", new Object[] { fontId });
            }
        }
    }
    
    public JSONObject isFontInProfile(final List<Long> fontIds) {
        final JSONObject jsonObject = new JSONObject();
        try {
            final SelectQuery configQuery = new ProfilePayloadOperator(this.profilePayloadMappings, this.unConfigureMap).getCertConfigSelectQuery(fontIds);
            final SelectQuery deliveredColumnConfigQuery = (SelectQuery)configQuery.clone();
            configQuery.addJoin(new Join("ConfigDataItem", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
            configQuery.addJoin(new Join("CfgDataToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            final SelectQuery columnQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToCollection"));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForGroup", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            columnQuery.addJoin(new Join("ProfileToCollection", "RecentProfileToColln", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 1));
            final Column column = new Column("ProfileToCollection", "COLLECTION_ID").distinct();
            columnQuery.addSelectColumn(column);
            final Criteria recentCriteria = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)null, 1).or(new Criteria(new Column("RecentProfileToColln", "COLLECTION_ID"), (Object)null, 1)));
            columnQuery.setCriteria(recentCriteria);
            final DerivedColumn profileCollectionColumn = new DerivedColumn("profileCollection", columnQuery);
            final DerivedColumn configDataColumn = new DerivedColumn("configColumn", deliveredColumnConfigQuery);
            final Criteria profileCollectionCriteria = new Criteria(new Column("ProfileToCollection", "COLLECTION_ID"), (Object)profileCollectionColumn, 8);
            final Criteria configDataCriteria = new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"), (Object)configDataColumn, 8);
            configQuery.setCriteria(profileCollectionCriteria.and(configDataCriteria));
            final List<Column> removalColumns = configQuery.getSelectColumns();
            for (final Column removeColumn : removalColumns) {
                configQuery.removeSelectColumn(removeColumn);
            }
            int columnName = 0;
            for (final ProfilePayloadMapping mapping : this.profilePayloadMappings) {
                final List<Column> columns = mapping.getColumns();
                for (final Column column2 : columns) {
                    column2.setColumnAlias(String.valueOf(columnName));
                    ++columnName;
                    configQuery.addSelectColumn(column2);
                }
            }
            configQuery.addSelectColumn(new Column("ProfileToCollection", "PROFILE_ID"));
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)configQuery);
            while (dataSetWrapper.next()) {
                final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
                for (int i = 0; i < columnName; ++i) {
                    final Long fontId = (Long)dataSetWrapper.getValue(String.valueOf(i));
                    if (fontId != null) {
                        if (jsonObject.has(String.valueOf(fontId))) {
                            final JSONArray profileIds = jsonObject.getJSONArray(String.valueOf(fontId));
                            profileIds.put((Object)profileId);
                        }
                        else {
                            final JSONArray profileIds = new JSONArray();
                            profileIds.put((Object)profileId);
                            jsonObject.put(String.valueOf(fontId), (Object)profileIds);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in get profiles", e);
        }
        return jsonObject;
    }
}
