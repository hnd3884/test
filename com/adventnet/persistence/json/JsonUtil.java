package com.adventnet.persistence.json;

import com.adventnet.persistence.PersistenceInitializer;
import org.json.JSONException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;

public class JsonUtil
{
    private static EnDecryptionValueHandler enDecryptionValueHandler;
    
    static void assertTableName(final String tableName) throws JSONException {
        try {
            if (MetaDataUtil.getTableDefinitionByName(tableName) == null) {
                throw createJsonExpception("Wrong table Name - " + tableName);
            }
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
        }
    }
    
    static JSONException createJsonExpception(final String message) {
        return createJsonExpception(message, null);
    }
    
    static JSONException createJsonExpception(final String message, final Throwable cause) {
        final JSONException toRet = new JSONException(message);
        if (cause != null) {
            toRet.initCause(cause);
        }
        return toRet;
    }
    
    private static void initEnDecryptionValueHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String className = (PersistenceInitializer.getConfigurationValue("EnDecryptionHandler") != null) ? PersistenceInitializer.getConfigurationValue("EnDecryptionHandler") : DefaultEnDecryptionValueHandler.class.getName();
        JsonUtil.enDecryptionValueHandler = (DefaultEnDecryptionValueHandler)Class.forName(className).newInstance();
    }
    
    public static String encryptValue(final String value) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (JsonUtil.enDecryptionValueHandler == null) {
            initEnDecryptionValueHandler();
        }
        return JsonUtil.enDecryptionValueHandler.encrypt(value);
    }
    
    public static String decryptValue(final String value) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (JsonUtil.enDecryptionValueHandler == null) {
            initEnDecryptionValueHandler();
        }
        return JsonUtil.enDecryptionValueHandler.decrypt(value);
    }
    
    static {
        JsonUtil.enDecryptionValueHandler = null;
    }
}
