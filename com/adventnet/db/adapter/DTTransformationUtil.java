package com.adventnet.db.adapter;

import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.util.logging.Logger;

public class DTTransformationUtil
{
    private static final Logger OUT;
    
    public static Object transform(final String tableName, final String columnName, final Object value, final String dataType, final String dbType) throws DataAccessException {
        if (DataTypeManager.isDataTypeSupported(dataType)) {
            final DTTransformer dtTransformer = DataTypeManager.getDataTypeDefinition(dataType).getDTTransformer(dbType);
            if (dtTransformer != null) {
                try {
                    return dtTransformer.transform(tableName, columnName, value, dataType);
                }
                catch (final Exception e) {
                    DTTransformationUtil.OUT.log(Level.SEVERE, "Exception during transforming data.", e);
                    throw new DataAccessException("Exception during transforming data." + e);
                }
            }
            throw new IllegalArgumentException("Transformer is not defined for the type :: " + DataTypeManager.getDataTypeDefinition(dataType).getDataType() + ", for the DB : " + dbType);
        }
        throw new IllegalArgumentException("Unknown UDT DataType: " + dataType);
    }
    
    public static Object unTransform(final String tableName, final String columnName, final Object value, final String dataType, final String dbType) throws DataAccessException {
        if (DataTypeManager.isDataTypeSupported(dataType)) {
            final DTTransformer dtTransformer = DataTypeManager.getDataTypeDefinition(dataType).getDTTransformer(dbType);
            if (dtTransformer != null) {
                try {
                    return dtTransformer.unTransform(tableName, columnName, value, dataType);
                }
                catch (final Exception e) {
                    DTTransformationUtil.OUT.log(Level.SEVERE, "Exception during transforming data.", e);
                    throw new DataAccessException("Exception during transforming data." + e);
                }
            }
            throw new IllegalArgumentException("Transformer is not defined for the type :: " + DataTypeManager.getDataTypeDefinition(dataType).getDataType());
        }
        throw new IllegalArgumentException("Unknown UDT DataType: " + dataType);
    }
    
    static {
        OUT = Logger.getLogger(DTTransformationUtil.class.getName());
    }
}
