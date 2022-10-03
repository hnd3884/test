package com.adventnet.sym.server.mdm.terms;

import org.json.JSONException;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import org.json.simple.parser.JSONParser;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import java.util.Enumeration;
import java.util.Properties;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.core.auth.APIKey;
import java.util.Arrays;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONArray;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.util.Utils;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Paths;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.InputStream;
import java.io.File;
import com.me.mdm.server.doc.DocMgmtConstants;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMTermsHandler
{
    public Logger logger;
    private static MDMTermsHandler termsHandler;
    
    public MDMTermsHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static MDMTermsHandler getInstance() {
        if (MDMTermsHandler.termsHandler == null) {
            MDMTermsHandler.termsHandler = new MDMTermsHandler();
        }
        return MDMTermsHandler.termsHandler;
    }
    
    public int savetermsDetails(final JSONObject json) throws Exception {
        final Long returnTermsID = this.addOrUpdateTermsDetails(json);
        final int success = (returnTermsID != -1L) ? 1 : 0;
        return success;
    }
    
    public Long addOrUpdateTermsDetails(final JSONObject json) throws Exception {
        Object returnTermsID = -1L;
        try {
            boolean addNew = false;
            final Long termsID = json.getLong("TERMS_ID");
            final DataObject dobj = (DataObject)new WritableDataObject();
            int version = 1;
            boolean langEdit = false;
            int prevownedby = 0;
            final int notify = json.getInt("NOTIFY_USERS");
            int ownedby = 0;
            String editedLang = "";
            final Row row = new Row("TermsOfUse");
            returnTermsID = termsID;
            if (termsID == -1L) {
                addNew = true;
                langEdit = true;
                row.set("TERMS_NAME", (Object)String.valueOf(json.get("TERMS_NAME")));
                row.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                row.set("MODIFIED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
                row.set("CUSTOMER_ID", (Object)CustomerInfoUtil.getInstance().getCustomerId());
                row.set("LATEST_VERSION", (Object)version);
                row.set("NOTIFY_USERS", (Object)json.getInt("NOTIFY_USERS"));
                row.set("DEFAULT_LANG", (Object)String.valueOf(json.get("DEFAULT_LANG")));
                dobj.addRow(row);
                editedLang = String.valueOf(json.get("TERMS_LANGUAGES"));
                returnTermsID = row.get("TERMS_ID");
            }
            else {
                if (json.get("EDITED_LANG") != null && !String.valueOf(json.get("EDITED_LANG")).equalsIgnoreCase("")) {
                    langEdit = true;
                    editedLang = String.valueOf(json.get("EDITED_LANG"));
                }
                if (json.get("TERMS_LANGUAGES_BEFORE_EDIT") != null) {
                    final String langBeforeEdit = String.valueOf(json.get("TERMS_LANGUAGES_BEFORE_EDIT"));
                    final String langAfterEdit = String.valueOf(json.get("TERMS_LANGUAGES"));
                    final String[] langAfterEditArray = langAfterEdit.split(",");
                    for (int len = 0; len < langAfterEditArray.length; ++len) {
                        if (langBeforeEdit.indexOf(langAfterEditArray[len]) == -1) {
                            if (!editedLang.equalsIgnoreCase("")) {
                                editedLang = editedLang + "," + langAfterEditArray[len];
                                langEdit = true;
                            }
                            else {
                                editedLang = langAfterEditArray[len];
                                langEdit = true;
                            }
                        }
                    }
                }
                final boolean langDeleted = this.isLangDeleted(json);
                if (langDeleted) {
                    langEdit = true;
                }
                final Criteria criteria = new Criteria(Column.getColumn("TermsOfUse", "TERMS_ID"), (Object)json.getLong("TERMS_ID"), 0);
                final DataObject dataObject = MDMUtil.getPersistence().get("TermsOfUse", criteria);
                if (!dataObject.isEmpty()) {
                    final Row termsrow = dataObject.getRow("TermsOfUse");
                    version = (int)termsrow.get("LATEST_VERSION");
                    if (langEdit) {
                        json.put("VERSION", version);
                        ++version;
                        termsrow.set("LATEST_VERSION", (Object)version);
                    }
                    if (!String.valueOf(json.get("DEFAULT_LANG")).equalsIgnoreCase("")) {
                        String defaultlang = String.valueOf(json.get("DEFAULT_LANG"));
                        defaultlang = defaultlang.toLowerCase();
                        defaultlang = defaultlang.substring(0, 1).toUpperCase() + defaultlang.substring(1);
                        if (defaultlang.indexOf(" ") != -1) {
                            final int pos = defaultlang.indexOf(" ");
                            final String substr = defaultlang.substring(pos + 1, defaultlang.length());
                            defaultlang = defaultlang.substring(0, pos) + " " + substr.substring(0, 1).toUpperCase() + substr.substring(1);
                        }
                        termsrow.set("DEFAULT_LANG", (Object)defaultlang);
                    }
                    else {
                        termsrow.set("DEFAULT_LANG", (Object)new String(""));
                    }
                    termsrow.set("NOTIFY_USERS", (Object)notify);
                    termsrow.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                    termsrow.set("MODIFIED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
                    dataObject.updateRow(termsrow);
                    MDMUtil.getPersistenceLite().update(dataObject);
                }
            }
            final Row termsToVersionRow = new Row("TermsToVersionMapping");
            termsToVersionRow.set("INPUT_TYPE", (Object)json.getInt("INPUT_TYPE"));
            if (!addNew) {
                if (langEdit) {
                    termsToVersionRow.set("TERMS_ID", (Object)termsID);
                }
            }
            else {
                termsToVersionRow.set("TERMS_ID", row.get("TERMS_ID"));
            }
            if (langEdit) {
                termsToVersionRow.set("VERSION", (Object)version);
                dobj.addRow(termsToVersionRow);
            }
            final String applyTerms = (String)json.get("APPLY_TERMS");
            if (addNew) {
                final Row termsToOwnedByRow = new Row("TermsToOwnedByMapping");
                termsToOwnedByRow.set("TERMS_ID", row.get("TERMS_ID"));
                if (applyTerms.equalsIgnoreCase("All Devices")) {
                    ownedby = 3;
                    termsToOwnedByRow.set("OWNED_BY", (Object)3);
                }
                else if (applyTerms.equalsIgnoreCase("Corporate Devices")) {
                    ownedby = 1;
                    termsToOwnedByRow.set("OWNED_BY", (Object)1);
                }
                else if (applyTerms.equalsIgnoreCase("Personal Devices")) {
                    ownedby = 2;
                    termsToOwnedByRow.set("OWNED_BY", (Object)2);
                }
                dobj.addRow(termsToOwnedByRow);
            }
            else {
                final Criteria criteria2 = new Criteria(Column.getColumn("TermsToOwnedByMapping", "TERMS_ID"), (Object)termsID, 0);
                final DataObject DO = MDMUtil.getPersistence().get("TermsToOwnedByMapping", criteria2);
                final Row termsToOwnedByUpdateRow = DO.getFirstRow("TermsToOwnedByMapping");
                prevownedby = (int)termsToOwnedByUpdateRow.get("OWNED_BY");
                if (applyTerms.equalsIgnoreCase("All Devices")) {
                    ownedby = 3;
                    termsToOwnedByUpdateRow.set("OWNED_BY", (Object)3);
                }
                else if (applyTerms.equalsIgnoreCase("Corporate Devices")) {
                    ownedby = 1;
                    termsToOwnedByUpdateRow.set("OWNED_BY", (Object)1);
                }
                else if (applyTerms.equalsIgnoreCase("Personal Devices")) {
                    ownedby = 2;
                    termsToOwnedByUpdateRow.set("OWNED_BY", (Object)2);
                }
                DO.updateRow(termsToOwnedByUpdateRow);
                MDMUtil.getPersistenceLite().update(DO);
            }
            if (json.getInt("INPUT_TYPE") == 1 && langEdit) {
                final ArrayList<Long> docID = this.addEntiresInFileTable(json);
                for (int i = 0; i < docID.size(); ++i) {
                    final Row termsDocMappingRow = new Row("TermsToDocumentMapping");
                    termsDocMappingRow.set("DOC_ID", (Object)docID.get(i));
                    termsDocMappingRow.set("TERMS_TO_VERSION_ID", termsToVersionRow.get("TERMS_TO_VERSION_ID"));
                    if (addNew) {
                        termsDocMappingRow.set("TERMS_ID", row.get("TERMS_ID"));
                    }
                    else {
                        termsDocMappingRow.set("TERMS_ID", (Object)termsID);
                    }
                    dobj.addRow(termsDocMappingRow);
                }
            }
            else if (json.getInt("INPUT_TYPE") == 2 && langEdit) {
                final String selectedLang = (String)json.get("TERMS_LANGUAGES");
                final String[] lang = selectedLang.split(",");
                for (int j = 0; j < lang.length; ++j) {
                    final Row termsDocMappingRow2 = new Row("TermsToDocumentMapping");
                    if (editedLang.indexOf(lang[j]) != -1) {
                        final Row termsDocDetailsRow = new Row("TermsDocumentDetails");
                        termsDocDetailsRow.set("DOC_PATH", (Object)String.valueOf(json.get("DOC_URL_" + lang[j])));
                        termsDocDetailsRow.set("LANGUAGE", (Object)lang[j]);
                        termsDocDetailsRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                        termsDocDetailsRow.set("ADDED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
                        dobj.addRow(termsDocDetailsRow);
                        termsDocMappingRow2.set("DOC_ID", termsDocDetailsRow.get("DOC_ID"));
                    }
                    else {
                        termsDocMappingRow2.set("DOC_ID", (Object)this.getDocID(json, lang[j]));
                    }
                    termsDocMappingRow2.set("TERMS_TO_VERSION_ID", termsToVersionRow.get("TERMS_TO_VERSION_ID"));
                    if (addNew) {
                        termsDocMappingRow2.set("TERMS_ID", row.get("TERMS_ID"));
                    }
                    else {
                        termsDocMappingRow2.set("TERMS_ID", (Object)termsID);
                    }
                    dobj.addRow(termsDocMappingRow2);
                }
            }
            MDMUtil.getPersistenceLite().add(dobj);
            this.sendSyncCommand();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while adding entires in Terms of use related tables", exp);
            throw exp;
        }
        if (returnTermsID instanceof Long) {
            return (Long)returnTermsID;
        }
        final UniqueValueHolder uniqueValueHolder = (UniqueValueHolder)returnTermsID;
        return (Long)uniqueValueHolder.getValue();
    }
    
    public void sendSyncCommand() throws Exception {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final List<Long> toBeNotifiedDeviceListAndroid = ManagedDeviceHandler.getInstance().getAndroidManagedDevicesForCustomer(customerId);
        final List<Long> toBeNotifiedDeviceListWindows = ManagedDeviceHandler.getInstance().getWindowsNativeAppInstalledDeviceResId(customerId);
        final List<Long> toBeNotifiedDeviceListIos = ManagedDeviceHandler.getInstance().getIosManagedDevicesForCustomer(customerId);
        final List<Long> totalDeviceList = new ArrayList<Long>();
        if (!toBeNotifiedDeviceListAndroid.isEmpty()) {
            totalDeviceList.addAll(toBeNotifiedDeviceListAndroid);
            DeviceCommandRepository.getInstance().addTermsSyncCommand(toBeNotifiedDeviceListAndroid, 1);
        }
        final List<Long> nativeDeviceList = new ArrayList<Long>();
        if (!toBeNotifiedDeviceListIos.isEmpty()) {
            nativeDeviceList.addAll(toBeNotifiedDeviceListIos);
        }
        if (!toBeNotifiedDeviceListWindows.isEmpty()) {
            nativeDeviceList.addAll(toBeNotifiedDeviceListWindows);
        }
        if (!nativeDeviceList.isEmpty()) {
            totalDeviceList.addAll(nativeDeviceList);
            DeviceCommandRepository.getInstance().addTermsSyncCommand(nativeDeviceList, 2);
        }
        if (!totalDeviceList.isEmpty()) {
            NotificationHandler.getInstance().SendNotification(totalDeviceList);
        }
    }
    
    public ArrayList<Long> addEntiresInFileTable(final JSONObject json) throws Exception {
        final ArrayList<Long> docIDs = new ArrayList<Long>();
        try {
            final String selectedLang = (String)json.get("TERMS_LANGUAGES");
            String editedLang = (String)json.get("EDITED_LANG");
            final String[] lang = selectedLang.split(",");
            if (json.get("TERMS_LANGUAGES_BEFORE_EDIT") != null) {
                final String langBeforeEdit = String.valueOf(json.get("TERMS_LANGUAGES_BEFORE_EDIT"));
                for (int len = 0; len < lang.length; ++len) {
                    if (langBeforeEdit.indexOf(lang[len]) == -1) {
                        if (!editedLang.equalsIgnoreCase("")) {
                            editedLang = editedLang + "," + lang[len];
                        }
                        else {
                            editedLang = lang[len];
                        }
                    }
                }
            }
            for (int i = 0; i < lang.length; ++i) {
                if (editedLang.indexOf(lang[i]) != -1 && !lang[i].equalsIgnoreCase("")) {
                    final Row row = new Row("TermsDocumentDetails");
                    row.set("LANGUAGE", (Object)lang[i]);
                    row.set("ADDED_TIME", (Object)System.currentTimeMillis());
                    row.set("ADDED_BY", (Object)MDMUtil.getInstance().getLoggedInUserID());
                    final DataObject dobj = (DataObject)new WritableDataObject();
                    dobj.addRow(row);
                    MDMUtil.getPersistenceLite().add(dobj);
                    final Long addedTime = (Long)DBUtil.getMaxOfValue("TermsDocumentDetails", "ADDED_TIME", (Criteria)null);
                    final Long docID = (Long)DBUtil.getValueFromDB("TermsDocumentDetails", "ADDED_TIME", (Object)addedTime, "DOC_ID");
                    final String fileName = (String)json.get("DOC_UPLOAD_" + lang[i]);
                    final InputStream file = ApiFactoryProvider.getFileAccessAPI().readFile(fileName);
                    String path = DocMgmtConstants.TERMS_DOC_FILE_DIRECTORY + File.separator + String.valueOf(CustomerInfoUtil.getInstance().getCustomerId());
                    final String type = fileName.substring(fileName.lastIndexOf(46) + 1);
                    this.storeTermsFile(file, docID, path, type);
                    path = path.replace('\\', '/');
                    path = path + "/" + String.valueOf(docID) + "." + type;
                    docIDs.add(docID);
                    final Criteria criteria = new Criteria(Column.getColumn("TermsDocumentDetails", "DOC_ID"), (Object)docID, 0);
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get("TermsDocumentDetails", criteria);
                    if (!dataObject.isEmpty()) {
                        final Row updaterow = dataObject.getFirstRow("TermsDocumentDetails");
                        updaterow.set("DOC_PATH", (Object)path);
                        dataObject.updateRow(updaterow);
                        MDMUtil.getPersistenceLite().update(dataObject);
                    }
                }
                else if (!lang[i].equalsIgnoreCase("")) {
                    docIDs.add(this.getDocID(json, lang[i]));
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while adding entires in Document details table(TERMSDOCUMENTDETAILS)", exp);
            throw exp;
        }
        return docIDs;
    }
    
    public void storeTermsFile(final InputStream fileStream, final Long docID, final String path, final String type) {
        try {
            final String fileName = String.valueOf(docID) + "." + type;
            final String fullDirectoryPath = MDMMetaDataUtil.getInstance().getClientDataParentDir() + path;
            final Path dirpath = Paths.get(fullDirectoryPath, new String[0]);
            Files.createDirectories(dirpath, (FileAttribute<?>[])new FileAttribute[0]);
            ApiFactoryProvider.getFileAccessAPI().writeFile(fullDirectoryPath + File.separator + fileName, fileStream);
            fileStream.close();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while downloading file", ex);
        }
    }
    
    public String removeTerms(final Long termsID) throws Exception {
        String termsname = "";
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            if (customerID == null) {
                throw new Exception("Customerid is null");
            }
            termsname = (String)DBUtil.getValueFromDB("TermsOfUse", "TERMS_ID", (Object)termsID, "TERMS_NAME");
            final Criteria criteria = new Criteria(Column.getColumn("TermsOfUse", "TERMS_ID"), (Object)termsID, 0).and(new Criteria(Column.getColumn("TermsOfUse", "CUSTOMER_ID"), (Object)customerID, 0));
            MDMUtil.getPersistenceLite().delete(criteria);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while removing terms", exp);
            throw exp;
        }
        return termsname;
    }
    
    public JSONObject getTermsDetails(final Long termsID, final Long customerID) {
        final JSONObject json = new JSONObject();
        int count = 0;
        try {
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("TermsOfUse"));
            final Criteria cusIDcri = new Criteria(Column.getColumn("TermsOfUse", "CUSTOMER_ID"), (Object)customerID, 0);
            if (termsID != -1L) {
                final Criteria termscri = new Criteria(Column.getColumn("TermsOfUse", "TERMS_ID"), (Object)termsID, 0);
                squery.setCriteria(termscri.and(cusIDcri));
            }
            else {
                squery.setCriteria(cusIDcri);
            }
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "TERMS_ID"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "TERMS_NAME"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "MODIFIED_BY"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "MODIFIED_TIME"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "LATEST_VERSION"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "NOTIFY_USERS"));
            squery.addSelectColumn(Column.getColumn("TermsOfUse", "DEFAULT_LANG"));
            final DataObject dobj = MDMUtil.getPersistenceLite().get(squery);
            if (!dobj.isEmpty()) {
                final Iterator itr = dobj.getRows("TermsOfUse");
                while (itr.hasNext()) {
                    final JSONObject termsjson = new JSONObject();
                    final Row termsrow = itr.next();
                    final Long termsid = (Long)termsrow.get("TERMS_ID");
                    final int version = (int)termsrow.get("LATEST_VERSION");
                    termsjson.put("TERMS_ID", (Object)String.valueOf(termsid));
                    termsjson.put("DEFAULT_LANG", (Object)String.valueOf(termsrow.get("DEFAULT_LANG")));
                    termsjson.put("TERMS_NAME", (Object)String.valueOf(termsrow.get("TERMS_NAME")));
                    termsjson.put("LATEST_VERSION", (Object)String.valueOf(version));
                    termsjson.put("NOTIFY_USERS", (int)termsrow.get("NOTIFY_USERS"));
                    final String modifiedBy = (String)DBUtil.getValueFromDB("AaaUser", "USER_ID", (Object)termsrow.get("MODIFIED_BY"), "FIRST_NAME");
                    termsjson.put("MODIFIED_BY", (Object)modifiedBy);
                    final String modifiedTime = Utils.getEventTime((Long)termsrow.get("MODIFIED_TIME"));
                    termsjson.put("MODIFIED_TIME", (Object)modifiedTime);
                    final SelectQuery selectquery = (SelectQuery)new SelectQueryImpl(Table.getTable("TermsOfUse"));
                    selectquery.addJoin(new Join("TermsOfUse", "TermsToVersionMapping", new String[] { "TERMS_ID" }, new String[] { "TERMS_ID" }, 2));
                    final Criteria versioncri = new Criteria(Column.getColumn("TermsToVersionMapping", "VERSION"), (Object)version, 0);
                    final Criteria termsidcri = new Criteria(Column.getColumn("TermsOfUse", "TERMS_ID"), (Object)termsid, 0);
                    selectquery.setCriteria(versioncri.and(termsidcri));
                    selectquery.addJoin(new Join("TermsOfUse", "TermsToOwnedByMapping", new String[] { "TERMS_ID" }, new String[] { "TERMS_ID" }, 2));
                    selectquery.addSelectColumn(Column.getColumn("TermsOfUse", "TERMS_ID"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToVersionMapping", "INPUT_TYPE"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToVersionMapping", "TERMS_TO_VERSION_ID"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToVersionMapping", "TERMS_ID"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToVersionMapping", "VERSION"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToOwnedByMapping", "OWNED_BY"));
                    selectquery.addSelectColumn(Column.getColumn("TermsToOwnedByMapping", "TERMS_ID"));
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectquery);
                    if (!dataObject.isEmpty()) {
                        final Iterator itr2 = dataObject.getRows("TermsToVersionMapping");
                        final Iterator itr3 = dataObject.getRows("TermsToOwnedByMapping");
                        while (itr2.hasNext() && itr3.hasNext()) {
                            String languages = "";
                            final JSONArray docIDs = new JSONArray();
                            final JSONArray docPaths = new JSONArray();
                            final Row termstoversionrow = itr2.next();
                            final Row termstoownedbyrow = itr3.next();
                            final Long termstoversionid = (Long)termstoversionrow.get("TERMS_TO_VERSION_ID");
                            termsjson.put("INPUT_TYPE", (Object)String.valueOf(termstoversionrow.get("INPUT_TYPE")));
                            termsjson.put("OWNED_BY", (Object)String.valueOf(termstoownedbyrow.get("OWNED_BY")));
                            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("TermsDocumentDetails"));
                            sq.addJoin(new Join("TermsDocumentDetails", "TermsToDocumentMapping", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                            sq.setCriteria(new Criteria(Column.getColumn("TermsToDocumentMapping", "TERMS_TO_VERSION_ID"), (Object)termstoversionid, 0));
                            sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_ID"));
                            sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_PATH"));
                            sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "LANGUAGE"));
                            final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
                            final Iterator itr4 = DO.getRows("TermsDocumentDetails");
                            while (itr4.hasNext()) {
                                final Row docdetailsrow = itr4.next();
                                docIDs.put((Object)docdetailsrow.get("DOC_ID"));
                                docPaths.put((Object)docdetailsrow.get("DOC_PATH"));
                                if (languages.equalsIgnoreCase("")) {
                                    languages = (String)docdetailsrow.get("LANGUAGE");
                                }
                                else {
                                    languages = languages + "," + (String)docdetailsrow.get("LANGUAGE");
                                }
                            }
                            termsjson.put("LANGUAGES", (Object)languages);
                            termsjson.put("DOC_IDS", (Object)docIDs);
                            termsjson.put("DOC_PATHS", (Object)docPaths);
                        }
                    }
                    json.put(String.valueOf(count), (Object)termsjson);
                    ++count;
                }
            }
            json.put("count", count);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting terms details from db", exp);
        }
        return json;
    }
    
    public JSONObject getTerms(final Long customerID) {
        final JSONObject json = this.getTermsDetails(-1L, customerID);
        return json;
    }
    
    public Long getDocID(final JSONObject termsjson, final String lang) {
        Long docid = -1L;
        try {
            final JSONObject json = new JSONObject(String.valueOf(termsjson.get("TERMS_JSON")));
            final ArrayList<String> languages = new ArrayList<String>(Arrays.asList(String.valueOf(json.get("LANGUAGES")).split(",")));
            final JSONArray docids = (JSONArray)json.get("DOC_IDS");
            final int index = languages.indexOf(lang);
            docid = Long.valueOf(String.valueOf(docids.get(index)));
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting doc ids", exp);
        }
        return docid;
    }
    
    public String getPathForOwnedBy(final int ownedby, String langcode, final APIKey apikey, final Long erid) {
        if (langcode.indexOf(45) != -1) {
            langcode = langcode.substring(0, langcode.indexOf(45));
        }
        else if (langcode.indexOf(95) != -1) {
            langcode = langcode.substring(0, langcode.indexOf(95));
        }
        boolean defaultdoc = false;
        String path = "";
        String lang = null;
        int inputtype = 1;
        boolean langPresent = false;
        try {
            final Properties props = SyMUtil.getLocalesProperties();
            final Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                if (key.substring(0, key.indexOf(95)).equalsIgnoreCase(langcode)) {
                    lang = props.getProperty(key);
                    break;
                }
            }
            if (lang != null) {
                lang = lang.substring(lang.lastIndexOf("[") + 1, lang.lastIndexOf("]"));
                langPresent = true;
            }
            final Long customerid = this.getCustomerIdforErid(erid);
            Long termsID = this.getTermsIdForOwnedBy(ownedby, customerid);
            if (termsID == null) {
                termsID = this.getTermsIdForOwnedBy(3, customerid);
            }
            if (termsID == null) {
                path = this.getDefaultPath();
                defaultdoc = true;
                final HashMap hm = new HashMap();
                hm.put("path", path);
                hm.put("IS_SERVER", false);
                hm.put("IS_AUTHTOKEN", true);
                path = MDMEnrollmentUtil.getInstance().getServerBaseURL() + ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
            }
            else {
                final Criteria termsIDcri = new Criteria(Column.getColumn("TermsToVersionMapping", "TERMS_ID"), (Object)termsID, 0);
                final int version = (int)DBUtil.getMaxOfValue("TermsToVersionMapping", "VERSION", termsIDcri);
                final Criteria versioncri = new Criteria(Column.getColumn("TermsToVersionMapping", "VERSION"), (Object)version, 0);
                final Long termstoversionid = (Long)DBUtil.getMaxOfValue("TermsToVersionMapping", "TERMS_TO_VERSION_ID", termsIDcri.and(versioncri));
                inputtype = (int)DBUtil.getMaxOfValue("TermsToVersionMapping", "INPUT_TYPE", termsIDcri.and(versioncri));
                if (langPresent) {
                    path = this.getPathFromDB(termstoversionid, lang);
                }
                if (path.equalsIgnoreCase("")) {
                    final String defaultLang = (String)DBUtil.getValueFromDB("TermsOfUse", "TERMS_ID", (Object)termsID, "DEFAULT_LANG");
                    path = this.getPathFromDB(termstoversionid, defaultLang);
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting doc path for owned by", exp);
        }
        if (inputtype == 1 && !defaultdoc) {
            final HashMap hm2 = new HashMap();
            hm2.put("path", path);
            hm2.put("IS_SERVER", false);
            hm2.put("IS_AUTHTOKEN", true);
            try {
                path = MDMEnrollmentUtil.getInstance().getServerBaseURL() + ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm2);
            }
            catch (final Exception exp2) {
                this.logger.log(Level.SEVERE, "Exception while constructing file url", exp2);
            }
            if (apikey != null) {
                path = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(path, apikey, false, null);
                if (path.indexOf("txt") == -1 && path.indexOf("html") == -1) {
                    path += ((path.indexOf("?") == -1) ? "?renderFile=true" : "&renderFile=true");
                }
            }
        }
        return path;
    }
    
    public Long getTermsIdForOwnedBy(final int ownedby, final Long customerId) {
        Long termsID = null;
        if (customerId != null) {
            final Criteria custIdcri = new Criteria(Column.getColumn("TermsOfUse", "CUSTOMER_ID"), (Object)customerId, 0);
            termsID = this.getTermsIdForOwnedBy(ownedby, custIdcri);
        }
        else {
            termsID = this.getTermsIdForOwnedBy(ownedby, (Criteria)null);
        }
        return termsID;
    }
    
    public Long getTermsIdForOwnedBy(final int ownedby, Criteria cri) {
        Long termsID = null;
        try {
            final SelectQuery selectquery = (SelectQuery)new SelectQueryImpl(new Table("TermsToOwnedByMapping"));
            final Criteria ownedbycri = new Criteria(Column.getColumn("TermsToOwnedByMapping", "OWNED_BY"), (Object)ownedby, 0);
            selectquery.addJoin(new Join("TermsToOwnedByMapping", "TermsOfUse", new String[] { "TERMS_ID" }, new String[] { "TERMS_ID" }, 2));
            if (cri == null) {
                cri = new Criteria(Column.getColumn("TermsOfUse", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0);
            }
            selectquery.setCriteria(cri.and(ownedbycri));
            selectquery.addSelectColumn(Column.getColumn("TermsToOwnedByMapping", "TERMS_ID"));
            selectquery.addSelectColumn(Column.getColumn("TermsToOwnedByMapping", "OWNED_BY"));
            selectquery.addSelectColumn(Column.getColumn("TermsOfUse", "TERMS_ID"));
            final DataObject dobj = MDMUtil.getPersistence().get(selectquery);
            if (!dobj.isEmpty()) {
                final Row row = dobj.getFirstRow("TermsToOwnedByMapping");
                termsID = (Long)row.get("TERMS_ID");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting termsid for ownedy", exp);
        }
        return termsID;
    }
    
    public String getPathFromDB(final Long termstoversionid, final String lang) {
        String path = "";
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("TermsToDocumentMapping"));
            final Criteria cri = new Criteria(Column.getColumn("TermsToDocumentMapping", "TERMS_TO_VERSION_ID"), (Object)termstoversionid, 0);
            final Criteria langcri = new Criteria(Column.getColumn("TermsDocumentDetails", "LANGUAGE"), (Object)lang, 0);
            sq.addJoin(new Join("TermsToDocumentMapping", "TermsDocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
            sq.setCriteria(cri.and(langcri));
            sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_ID"));
            sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_PATH"));
            sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "DOC_ID"));
            sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "TERMS_ID"));
            sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "TERMS_TO_VERSION_ID"));
            final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("TermsDocumentDetails");
                path = (String)row.get("DOC_PATH");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting doc path from db", exp);
        }
        return path;
    }
    
    private String getDefaultPath() {
        return "/mdm/html/mdmTerms.html?" + System.currentTimeMillis();
    }
    
    public DeviceMessage getDiffAndUpdateStatus(final String request, final APIKey key) {
        final DeviceMessage msg = new DeviceMessage();
        try {
            final org.json.simple.JSONObject msgRequestJS = (org.json.simple.JSONObject)new JSONParser().parse(request);
            final Object lastSyncTimeObj = msgRequestJS.get((Object)"LastSyncTime");
            final Object eridObj = msgRequestJS.get((Object)"EnrollmentReqId");
            Long lastSyncTime = -1L;
            Long erid = -1L;
            if (lastSyncTimeObj instanceof String) {
                if (SyMUtil.isStringValid((String)lastSyncTimeObj)) {
                    lastSyncTime = Long.valueOf((String)lastSyncTimeObj);
                }
            }
            else {
                lastSyncTime = (long)lastSyncTimeObj;
            }
            if (eridObj instanceof String) {
                if (SyMUtil.isStringValid((String)eridObj)) {
                    erid = Long.valueOf((String)eridObj);
                }
            }
            else {
                erid = (long)eridObj;
            }
            final JSONObject messageResponse = this.getDiffjson(erid, lastSyncTime, key);
            msg.setMessageResponseJSON(messageResponse);
            msg.setMessageStatus("Acknowledged");
            msg.setMessageType("TermsOfUse");
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in getDiffAndUpdateStatus", exp);
        }
        return msg;
    }
    
    private JSONObject getDiffjson(final Long erid, final Long lastsynctime, final APIKey key) {
        final JSONObject json = new JSONObject();
        try {
            final int ownedby = MDMEnrollmentUtil.getInstance().getOwnedByforEnrollmentRequest(erid);
            final Long customerid = this.getCustomerIdforErid(erid);
            Long termsID = this.getTermsIdForOwnedBy(ownedby, customerid);
            if (termsID == null) {
                termsID = this.getTermsIdForOwnedBy(3, customerid);
            }
            if (termsID == null) {
                json.put("ShowCustomTermsOfUse", (Object)Boolean.FALSE);
                return json;
            }
            json.put("ShowCustomTermsOfUse", (Object)Boolean.TRUE);
            final String defaultlang = (String)DBUtil.getValueFromDB("TermsOfUse", "TERMS_ID", (Object)termsID, "DEFAULT_LANG");
            json.put("Default", (Object)this.getLangCode(defaultlang));
            final Criteria termsIDcri = new Criteria(Column.getColumn("TermsToVersionMapping", "TERMS_ID"), (Object)termsID, 0);
            final int version = (int)DBUtil.getMaxOfValue("TermsToVersionMapping", "VERSION", termsIDcri);
            final Criteria versioncri = new Criteria(Column.getColumn("TermsToVersionMapping", "VERSION"), (Object)version, 0);
            final Criteria synctimeCri = new Criteria(Column.getColumn("TermsDocumentDetails", "ADDED_TIME"), (Object)lastsynctime, 4);
            final Long termstoversionid = (Long)DBUtil.getMaxOfValue("TermsToVersionMapping", "TERMS_TO_VERSION_ID", termsIDcri.and(versioncri));
            json.put("Type", (int)DBUtil.getMaxOfValue("TermsToVersionMapping", "INPUT_TYPE", termsIDcri.and(versioncri)));
            final DataObject DO = this.getUpdatedDO(termstoversionid, synctimeCri, customerid);
            final int inputtype = (int)DBUtil.getValueFromDB("TermsToVersionMapping", "TERMS_TO_VERSION_ID", (Object)termstoversionid, "INPUT_TYPE");
            final JSONObject langJson = this.getLangJson(DO, inputtype, key);
            json.put("LocalizedURLS", (Object)langJson);
            json.put("LastSyncTime", System.currentTimeMillis());
            json.put("AvailableLang", (Object)this.getAvailableLang(ownedby, customerid));
            final boolean CanNotify = (int)DBUtil.getValueFromDB("TermsOfUse", "TERMS_ID", (Object)termsID, "NOTIFY_USERS") != 0;
            json.put("CanNotify", CanNotify);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting DO for agent update", exp);
        }
        return json;
    }
    
    private DataObject getUpdatedDO(final Long termstoversionid, final Criteria synctimecri, Long customerid) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("TermsToDocumentMapping"));
        Criteria cri = new Criteria(Column.getColumn("TermsToDocumentMapping", "TERMS_TO_VERSION_ID"), (Object)termstoversionid, 0);
        sq.addJoin(new Join("TermsToDocumentMapping", "TermsDocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        sq.addJoin(new Join("TermsToDocumentMapping", "TermsOfUse", new String[] { "TERMS_ID" }, new String[] { "TERMS_ID" }, 2));
        if (customerid == null) {
            customerid = CustomerInfoUtil.getInstance().getCustomerId();
        }
        final Criteria custIdcri = new Criteria(Column.getColumn("TermsOfUse", "CUSTOMER_ID"), (Object)customerid, 0);
        cri = cri.and(custIdcri);
        sq.setCriteria(cri.and(synctimecri));
        sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_ID"));
        sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "DOC_PATH"));
        sq.addSelectColumn(Column.getColumn("TermsDocumentDetails", "LANGUAGE"));
        sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "DOC_ID"));
        sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "TERMS_ID"));
        sq.addSelectColumn(Column.getColumn("TermsToDocumentMapping", "TERMS_TO_VERSION_ID"));
        final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
        return DO;
    }
    
    private JSONObject getLangJson(final DataObject dataObj, final int inputtype, final APIKey key) throws Exception {
        final JSONObject json = new JSONObject();
        if (!dataObj.isEmpty()) {
            final Iterator itr = dataObj.getRows("TermsDocumentDetails");
            while (itr.hasNext()) {
                final Row row = itr.next();
                final String lang = (String)row.get("LANGUAGE");
                final String path = (String)row.get("DOC_PATH");
                final JSONObject urljson = new JSONObject();
                if (inputtype == 1) {
                    final HashMap hm = new HashMap();
                    hm.put("path", path);
                    hm.put("IS_SERVER", false);
                    hm.put("IS_AUTHTOKEN", false);
                    String urlpath = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
                    if (key != null) {
                        urlpath = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(urlpath, key, false, null);
                    }
                    urljson.put("url", (Object)urlpath);
                }
                else {
                    urljson.put("url", (Object)path);
                }
                json.put(this.getLangCode(lang), (Object)urljson);
            }
        }
        return json;
    }
    
    private JSONArray getAvailableLang(final int ownedby, final Long customerid) {
        final JSONArray jsonArray = new JSONArray();
        try {
            Long termsID = this.getTermsIdForOwnedBy(ownedby, customerid);
            if (termsID == null) {
                termsID = this.getTermsIdForOwnedBy(3, customerid);
            }
            final JSONObject json = this.getTermsDetails(termsID, CustomerInfoUtil.getInstance().getCustomerId());
            final JSONObject termsjson = json.getJSONObject("0");
            final String availableLang = (String)termsjson.get("LANGUAGES");
            final List<String> langList = Arrays.asList(availableLang.split(","));
            for (int i = 0; i < langList.size(); ++i) {
                jsonArray.put((Object)this.getLangCode(langList.get(i)));
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting available lang", exp);
        }
        return jsonArray;
    }
    
    private String getLangCode(final String lang) {
        final Properties props = SyMUtil.getLocalesProperties();
        final Map<String, String> map = new HashMap<String, String>((Map<? extends String, ? extends String>)props);
        final HashMap<String, String> locales = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            locales.put(entry.getValue().substring(entry.getValue().lastIndexOf("[") + 1, entry.getValue().lastIndexOf("]")), entry.getKey());
        }
        return locales.get(lang).substring(0, locales.get(lang).indexOf(95));
    }
    
    private boolean isLangDeleted(final JSONObject json) {
        boolean langDeleted = false;
        try {
            final ArrayList<String> langBeforeEdit = new ArrayList<String>(Arrays.asList(String.valueOf(json.get("TERMS_LANGUAGES_BEFORE_EDIT")).split(",")));
            final ArrayList<String> lang = new ArrayList<String>(Arrays.asList(String.valueOf(json.get("TERMS_LANGUAGES")).split(",")));
            langBeforeEdit.removeAll(lang);
            if (langBeforeEdit.size() > 0) {
                langDeleted = true;
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while checking if any lang is deleted", exp);
        }
        return langDeleted;
    }
    
    public JSONObject checkIfTermsExistForOwnedBy(final Long termsID, final int ownedby, final Long customerId) throws Exception {
        final JSONObject json = new JSONObject();
        if (termsID == -1L) {
            final Long allDeviceTermsId = this.getTermsIdForOwnedBy(3, customerId);
            if (allDeviceTermsId != null) {
                json.put("success", -1);
                json.put("exists", 3);
                return json;
            }
            final Long selectedOwnedByTermsID = this.getTermsIdForOwnedBy(ownedby, customerId);
            if (selectedOwnedByTermsID != null) {
                json.put("success", -1);
                json.put("exists", ownedby);
                return json;
            }
            if (ownedby == 3) {
                final Long personaltermsID = this.getTermsIdForOwnedBy(2, customerId);
                final Long corporatetermsID = this.getTermsIdForOwnedBy(1, customerId);
                if (personaltermsID != null) {
                    json.put("success", -1);
                    json.put("exists", 2);
                    return json;
                }
                if (corporatetermsID != null) {
                    json.put("success", -1);
                    json.put("exists", 1);
                    return json;
                }
            }
        }
        else {
            final int prevOwnedBy = (int)DBUtil.getValueFromDB("TermsToOwnedByMapping", "TERMS_ID", (Object)termsID, "OWNED_BY");
            if (ownedby != prevOwnedBy) {
                final Long allDeviceTermsId2 = this.getTermsIdForOwnedBy(3, customerId);
                if (allDeviceTermsId2 != null && allDeviceTermsId2 != (long)termsID) {
                    json.put("success", -1);
                    json.put("exists", 3);
                    return json;
                }
                final Long selectedOwnedByTerms = this.getTermsIdForOwnedBy(ownedby, customerId);
                if (selectedOwnedByTerms != null && selectedOwnedByTerms != (long)termsID) {
                    json.put("success", -1);
                    json.put("exists", ownedby);
                    return json;
                }
                if (ownedby == 3) {
                    final Long personaltermsID2 = this.getTermsIdForOwnedBy(2, customerId);
                    final Long corporatetermsID2 = this.getTermsIdForOwnedBy(1, customerId);
                    if (prevOwnedBy == 2) {
                        if (corporatetermsID2 != null) {
                            json.put("success", -1);
                            json.put("exists", 1);
                            return json;
                        }
                    }
                    else if (personaltermsID2 != null) {
                        json.put("success", -1);
                        json.put("exists", 2);
                        return json;
                    }
                }
            }
        }
        if (json.opt("success") == null) {
            json.put("success", 1);
        }
        return json;
    }
    
    public JSONObject checkIfTermsExistForOwnedBy(final Long termsID, final String applyfor, final Long customerId) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            int ownedby = 0;
            if (applyfor.equalsIgnoreCase("All Devices")) {
                ownedby = 3;
            }
            else if (applyfor.equalsIgnoreCase("Corporate Devices")) {
                ownedby = 1;
            }
            else if (applyfor.equalsIgnoreCase("Personal Devices")) {
                ownedby = 2;
            }
            json = this.checkIfTermsExistForOwnedBy(termsID, ownedby, customerId);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while checking if terms for given owned by already exists", exp);
        }
        return json;
    }
    
    public Long getCustomerIdforErid(final Long erid) {
        Long customerId = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sq.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            sq.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sq.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sq.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sq.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            sq.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            sq.setCriteria(userNotInTrashCriteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0)));
            final DataObject DO = MDMUtil.getPersistence().get(sq);
            if (!DO.isEmpty()) {
                final Row row = DO.getFirstRow("Resource");
                customerId = (Long)row.get("CUSTOMER_ID");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while getting customer id for erid", exp);
        }
        return customerId;
    }
    
    public void syncTermsOnOwnedByUpdate(final Long enrollmentRequestID) {
        try {
            final List<Long> toBeNotifiedDeviceListAndroid = new ArrayList<Long>();
            final List<Long> toBeNotifiedDeviceListIos = new ArrayList<Long>();
            final List<Long> toBeNotifiedDeviceListWindows = new ArrayList<Long>();
            final int platform = (int)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "PLATFORM_TYPE");
            if (platform == 2) {
                toBeNotifiedDeviceListAndroid.add((Long)DBUtil.getValueFromDB("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "MANAGED_DEVICE_ID"));
            }
            if (platform == 1) {
                toBeNotifiedDeviceListIos.add((Long)DBUtil.getValueFromDB("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "MANAGED_DEVICE_ID"));
            }
            if (platform == 3) {
                toBeNotifiedDeviceListIos.add((Long)DBUtil.getValueFromDB("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "MANAGED_DEVICE_ID"));
            }
            if (!toBeNotifiedDeviceListAndroid.isEmpty()) {
                DeviceCommandRepository.getInstance().addTermsSyncCommand(toBeNotifiedDeviceListAndroid, 1);
                NotificationHandler.getInstance().SendNotification(toBeNotifiedDeviceListAndroid);
            }
            if (!toBeNotifiedDeviceListIos.isEmpty()) {
                DeviceCommandRepository.getInstance().addTermsSyncCommand(toBeNotifiedDeviceListIos, 2);
                NotificationHandler.getInstance().SendNotification(toBeNotifiedDeviceListIos);
            }
            if (!toBeNotifiedDeviceListWindows.isEmpty()) {
                DeviceCommandRepository.getInstance().addTermsSyncCommand(toBeNotifiedDeviceListWindows, 2);
                NotificationHandler.getInstance().SendNotification(toBeNotifiedDeviceListWindows);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while sending sync request on ownedby update", exp);
        }
    }
    
    static {
        MDMTermsHandler.termsHandler = null;
    }
}
