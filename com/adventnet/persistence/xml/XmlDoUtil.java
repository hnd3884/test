package com.adventnet.persistence.xml;

import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class XmlDoUtil
{
    private static final Logger LOGGER;
    
    private XmlDoUtil() {
    }
    
    public static ParentChildrenMap getPCM(final String rootName, final DataObject data) throws Exception {
        ParentChildrenMap pcm = null;
        final List tableNames = PersistenceUtil.sortTables(data.getTableNames());
        final boolean swap_parent_child = false;
        final XmlFormatBuilder builder = new XmlFormatBuilder(swap_parent_child);
        pcm = builder.createFormat(tableNames, rootName);
        return pcm;
    }
    
    public static Object convert(final String value, final String dataType) throws MetaDataException {
        return MetaDataUtil.convert(value, dataType);
    }
    
    public static boolean checkIfDynamicValueGeneratorExists(final String tableName) {
        boolean exists = false;
        final Object[] keys = DynamicValueHandlerRepositry.dynamicHandlers.keySet().toArray();
        XmlDoUtil.LOGGER.log(Level.FINE, "Dynamic value handlers are {0}", DynamicValueHandlerRepositry.dynamicHandlers);
        if (keys.length <= 0) {
            return false;
        }
        for (int i = 0; i < keys.length; ++i) {
            final String key = (String)keys[i];
            if (key.startsWith(tableName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }
    
    static {
        LOGGER = Logger.getLogger(XmlDoUtil.class.getName());
    }
}
