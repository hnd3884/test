package com.adventnet.db.persistence.metadata.extended;

import java.util.Map;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.adventnet.persistence.DataAccessException;
import java.io.IOException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class MECustomAttributeHandler implements CustomAttributeHandler
{
    private static final Logger LOGGER;
    private String dynamicFile;
    private Properties dynamicProperties;
    private static final String ADD_ATTR = "add";
    private static final String MODIFY_ATTR = "modify";
    private static final String DELETE_ATTR = "delete";
    
    public MECustomAttributeHandler() {
        this.dynamicFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "CustomAttr" + File.separator + "dynamic.attr";
        this.dynamicProperties = new Properties();
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String columnName, final String attributeName, final String value) throws IOException, DataAccessException, MetaDataException {
        if (attributeName.equals("maxsize") || attributeName.equals("defaultvalue")) {
            throw new MetaDataException("maxSize/defaultValue of columns should not be changed during run time");
        }
        final String key = this.getKey(tableName, columnName, attributeName);
        CustomAttributeValidator instance = MetaDataUtil.getValidator(attributeName);
        if (instance == null) {
            instance = MetaDataUtil.getValidator("defaultvalidator");
        }
        if (this.dynamicProperties.get(key) == null) {
            if (instance.validateDynamicAttribute(key, null, value)) {
                return this.modifyDynamicFile(key, value, "add");
            }
        }
        else if (instance.validateDynamicAttribute(key, MetaDataUtil.getAttribute(key), value)) {
            return this.modifyDynamicFile(key, value, "modify");
        }
        return false;
    }
    
    private synchronized boolean modifyDynamicFile(final String attrName, final String attrValue, final String action) throws IOException {
        if (action.equals("add")) {
            final StringBuilder s = new StringBuilder();
            s.append("\n");
            s.append(attrName);
            s.append(" = ");
            s.append(attrValue);
            this.dynamicProperties.setProperty(attrName, attrValue);
            this.appendAttribute(s.toString());
            return true;
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(this.dynamicFile);
            if (action.equals("modify")) {
                this.dynamicProperties.setProperty(attrName, attrValue);
                this.dynamicProperties.store(fout, null);
            }
            if (action.equals("delete")) {
                if (this.dynamicProperties.remove(attrName) == null) {
                    MECustomAttributeHandler.LOGGER.log(Level.WARNING, "There is no custom attribute with key :: " + attrName);
                    return false;
                }
                this.dynamicProperties.store(fout, null);
            }
        }
        catch (final Exception e) {
            MECustomAttributeHandler.LOGGER.log(Level.SEVERE, "Exception occured while storing dynamic attribute in dynamic.attr :: " + e.getMessage());
            e.printStackTrace();
            this.dynamicProperties.store(fout, null);
            return false;
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
        return true;
    }
    
    private void appendAttribute(final String attribute) {
        try {
            final File file = new File(this.dynamicFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (final FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
                 final BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(attribute);
            }
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean setAttribute(final String tableName, final String attributeName, final String value) throws IOException, DataAccessException, MetaDataException {
        return this.setAttribute(tableName, null, attributeName, value);
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String columnName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        if (attributeName.equals("maxsize") || attributeName.equals("defaultvalue")) {
            throw new MetaDataException("maxSize/defaultValue of columns should not be changed during run time");
        }
        if (this.dynamicProperties.isEmpty()) {
            MECustomAttributeHandler.LOGGER.log(Level.WARNING, "There are no custom attributes to delete");
            return false;
        }
        final String key = this.getKey(tableName, columnName, attributeName);
        return this.modifyDynamicFile(key, null, "delete");
    }
    
    @Override
    public boolean removeAttribute(final String tableName, final String attributeName) throws IOException, DataAccessException, MetaDataException {
        return this.removeAttribute(tableName, null, attributeName);
    }
    
    @Override
    public ConcurrentHashMap<String, String> loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException {
        final File file = new File(this.dynamicFile);
        if (file.exists()) {
            try (final FileInputStream fileInput = new FileInputStream(this.dynamicFile)) {
                this.dynamicProperties.load(fileInput);
            }
            return new ConcurrentHashMap<String, String>((Map<? extends String, ? extends String>)this.dynamicProperties);
        }
        return new ConcurrentHashMap<String, String>();
    }
    
    private String getKey(final String tableName, final String columnName, final String attributeName) {
        String key = null;
        if (columnName != null) {
            key = tableName + "." + columnName + "." + attributeName;
        }
        else {
            key = tableName + "." + attributeName;
        }
        return key;
    }
    
    static {
        LOGGER = Logger.getLogger(MECustomAttributeHandler.class.getName());
    }
}
