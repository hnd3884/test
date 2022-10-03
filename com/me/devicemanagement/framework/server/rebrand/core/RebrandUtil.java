package com.me.devicemanagement.framework.server.rebrand.core;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.IOException;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.InputStream;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.io.File;
import com.adventnet.i18n.I18N;
import java.util.Map;
import com.adventnet.iam.security.UploadedFileItem;
import java.util.logging.Logger;

public class RebrandUtil
{
    private static Logger logger;
    
    public static boolean validateImage(final UploadedFileItem dcLogo, final Map rebrandDetails) throws Exception {
        final String logoName = dcLogo.getFileName();
        boolean isValidImg = true;
        if (logoName != null && !logoName.equals("")) {
            if (dcLogo.getFileSize() > 51200L) {
                rebrandDetails.put("status", "Failed");
                rebrandDetails.put("message", I18N.getMsg("dc.admin.rebranding.image_size_is_greater", new Object[] { logoName }));
                RebrandUtil.logger.info(" Image file size is greater. So Returning...");
                isValidImg = false;
            }
            else {
                final String mimetype = dcLogo.getRequestContentType();
                final String type = mimetype.split("/")[0];
                if (!type.equals("image")) {
                    rebrandDetails.put("status", "Failed");
                    rebrandDetails.put("message", I18N.getMsg("dc.rebrand.upload.image.only", new Object[0]));
                    RebrandUtil.logger.info(" Uploaded file is not an image. So Returning...");
                    isValidImg = false;
                }
            }
        }
        else {
            isValidImg = false;
        }
        return isValidImg;
    }
    
    public static boolean saveRebrandImage(final UploadedFileItem uploadedFile, final String directory, final String imgName) throws Exception {
        return saveRebrandImage(uploadedFile.getUploadedFile(), directory, imgName);
    }
    
    public static boolean saveRebrandImage(final File file, final String directory, final String imgName) throws Exception {
        try {
            final InputStream fileInput = new FileInputStream(file);
            return copyFile(fileInput, directory, imgName);
        }
        catch (final Exception ex) {
            RebrandUtil.logger.log(Level.WARNING, "Import Uploaded FIle Operation failed " + file.getName() + " ", ex);
            throw ex;
        }
    }
    
    private static boolean copyFile(final InputStream fileInput, final String directory, String fileName) throws IOException {
        final FileOutputStream fout = null;
        boolean copyFile = false;
        try {
            final byte[] file = new byte[fileInput.available()];
            fileInput.read(file);
            RebrandUtil.logger.log(Level.INFO, "Request to import new form file: " + fileName);
            if (file.length < 51200) {
                if (file.length != 0) {
                    fileName = directory + File.separator + fileName;
                    ApiFactoryProvider.getFileAccessAPI().writeFile(fileName, file);
                    RebrandUtil.logger.log(Level.INFO, "Successfully copied file : " + fileName + " to Images Repository");
                    copyFile = true;
                }
            }
            else {
                RebrandUtil.logger.log(Level.WARNING, " File size is greater than 50 kb ; So couldn't copy!");
            }
        }
        catch (final Exception ex) {
            RebrandUtil.logger.log(Level.WARNING, "Import Form File Operation failed " + fileName + " ", ex);
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception ex) {
                RebrandUtil.logger.log(Level.WARNING, "Exception while closing the stream!", ex);
                throw ex;
            }
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception ex2) {
                RebrandUtil.logger.log(Level.WARNING, "Exception while closing the stream!", ex2);
                throw ex2;
            }
        }
        return copyFile;
    }
    
    public static boolean setRebrandCommDetails(final Map data) throws DataAccessException {
        boolean isChanged = false;
        try {
            RebrandUtil.logger.log(Level.FINE, "RebrandUtil - setRebrandCommDetails...");
            DataObject dataObject;
            if (CustomerInfoUtil.isSASAndMSP()) {
                dataObject = SyMUtil.getPersistence().get("RebrandCommDetails", (Criteria)null);
            }
            else {
                dataObject = SyMUtil.getReadOnlyPersistence().get("RebrandCommDetails", (Criteria)null);
            }
            Row row;
            if (dataObject != null && !dataObject.isEmpty()) {
                row = dataObject.getFirstRow("RebrandCommDetails");
            }
            else {
                row = new Row("RebrandCommDetails");
            }
            final ArrayList<String> cols = new ArrayList<String>(row.getColumns());
            cols.removeAll(row.getPKColumns());
            for (final String col : cols) {
                String value = (data.get(col) != null) ? data.get(col).toString().trim() : "--";
                if (value.equals("")) {
                    value = "--";
                }
                if (!value.equals(row.get(col))) {
                    isChanged = true;
                    row.set(col, (Object)value);
                }
            }
            if (dataObject != null && !dataObject.isEmpty()) {
                if (isChanged) {
                    RebrandUtil.logger.log(Level.FINE, "RebrandUtil - setRebrandCommDetails - Changed");
                    dataObject.updateRow(row);
                }
            }
            else {
                dataObject.addRow(row);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final DataAccessException ex) {
            RebrandUtil.logger.log(Level.WARNING, "DataAccessException setRebrandCommDetails() : " + ex);
            throw ex;
        }
        return isChanged;
    }
    
    public static HashMap getRebrandCommDetails() throws DataAccessException {
        RebrandUtil.logger.log(Level.FINE, "RebrandUtil - getRebrandCommDetails...");
        final HashMap<String, String> data = new HashMap<String, String>();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RebrandCommDetails"));
            query.addSelectColumn(Column.getColumn("RebrandCommDetails", "*"));
            DataObject detailsDO;
            if (CustomerInfoUtil.isSASAndMSP()) {
                detailsDO = SyMUtil.getPersistence().get(query);
            }
            else {
                detailsDO = SyMUtil.getReadOnlyPersistence().get(query);
            }
            final Row row = new Row("RebrandCommDetails");
            final ArrayList<String> cols = new ArrayList<String>(row.getColumns());
            cols.removeAll(row.getPKColumns());
            final Row detailsRow = detailsDO.getRow("RebrandCommDetails");
            if (detailsRow != null) {
                for (final String col : cols) {
                    if (detailsRow.get(col).equals("--")) {
                        data.put(col, "");
                    }
                    else {
                        data.put(col, (String)detailsRow.get(col));
                    }
                }
            }
            else {
                for (final String col : cols) {
                    data.put(col, "");
                }
            }
        }
        catch (final DataAccessException ex) {
            RebrandUtil.logger.log(Level.WARNING, "getRebrandCommDetails() : ", (Throwable)ex);
            throw ex;
        }
        RebrandUtil.logger.log(Level.FINEST, "getRebrandCommDetails() : " + data);
        return data;
    }
    
    static {
        RebrandUtil.logger = Logger.getLogger(RebrandUtil.class.getName());
    }
}
