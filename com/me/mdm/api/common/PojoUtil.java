package com.me.mdm.api.common;

import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.me.mdm.api.common.filter.UpperCaseStrategy;
import com.fasterxml.jackson.databind.MapperFeature;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class PojoUtil
{
    protected static Logger logger;
    private static PojoUtil pojoUtil;
    
    private PojoUtil() {
    }
    
    public static PojoUtil getInstance() {
        if (PojoUtil.pojoUtil == null) {
            PojoUtil.pojoUtil = new PojoUtil();
        }
        return PojoUtil.pojoUtil;
    }
    
    public <T> T getModelfromDO(final DataObject dataObject, final Class<T> valueType, final T pojoObject) {
        try {
            JSONObject subJSON = new JSONObject();
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                if (subJSON.length() == 0) {
                    subJSON = row.getAsJSON();
                }
                else {
                    JSONUtil.getInstance();
                    JSONUtil.putAll(subJSON, row.getAsJSON());
                }
            }
            final ObjectMapper objectMapper = new ObjectMapper();
            if (pojoObject != null) {
                final JSONObject jsonObject = new JSONObject(objectMapper.writeValueAsString((Object)pojoObject));
                JSONUtil.getInstance();
                JSONUtil.putAll(subJSON, jsonObject);
            }
            return (T)objectMapper.readValue(subJSON.toString(), (Class)valueType);
        }
        catch (final DataAccessException exp) {
            PojoUtil.logger.log(Level.WARNING, "DataAccessException while getting POJO from dataObject", (Throwable)exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception exp2) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating POJO from dataObject", exp2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public <T> T getModelFromJsonObject(final JSONObject jsonObject, final Class<T> valueType, final T pojoObject, final boolean isUpperCase) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            if (isUpperCase) {
                objectMapper.enable(new MapperFeature[] { MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING });
                objectMapper.setPropertyNamingStrategy((PropertyNamingStrategy)new UpperCaseStrategy());
            }
            if (pojoObject != null) {
                final JSONObject subJSON = new JSONObject(objectMapper.writeValueAsString((Object)pojoObject));
                JSONUtil.getInstance();
                JSONUtil.putAll(jsonObject, subJSON);
            }
            final T value = (T)objectMapper.readValue(jsonObject.toString(), (Class)valueType);
            return value;
        }
        catch (final Exception exp) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating POJO from json Object", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public <T> List<T> getModelListFromJsonArray(final JSONArray jsonArray, final Class<T[]> valueType) throws SyMException {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final T[] valueTypes = (T[])objectMapper.readValue(jsonArray.toString(), (Class)valueType);
            return new ArrayList<T>((Collection<? extends T>)Arrays.asList(valueTypes));
        }
        catch (final Exception exp) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating List of POJO from jsonArray", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public <T> List<T> getModelListFromRows(final Iterator<Row> rows, final Class<T> valueType) throws SyMException {
        try {
            final List<T> values = new ArrayList<T>();
            final ObjectMapper objectMapper = new ObjectMapper();
            while (rows.hasNext()) {
                final JSONObject jsonObject = rows.next().getAsJSON();
                final T value = (T)objectMapper.readValue(jsonObject.toString(), (Class)valueType);
                values.add(value);
            }
            return values;
        }
        catch (final Exception exp) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating List of POJO from rows", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private <T> List<T> getModelListFromDMDataSetWrapper(final DMDataSetWrapper dataSetWrapper, final Class<T> valueType, final boolean isUpperCase) throws SyMException {
        try {
            final List<T> values = new ArrayList<T>();
            final ObjectMapper objectMapper = new ObjectMapper();
            while (dataSetWrapper.next()) {
                final JSONObject jsonObject = new JSONObject();
                for (int index = 1; index <= dataSetWrapper.getColumnCount(); ++index) {
                    jsonObject.put(dataSetWrapper.getColumnName(index), dataSetWrapper.getValue(index));
                }
                values.add(this.getModelFromJsonObject(jsonObject, valueType, (T)null, isUpperCase));
            }
            return values;
        }
        catch (final Exception exp) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating List of POJO from rows", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private <T> T getModeFromDMDataSetWrapper(final DMDataSetWrapper dataSetWrapper, final Class<T> valueType, T pojoObject, final boolean isUpperCase) throws SyMException {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            while (dataSetWrapper.next()) {
                final JSONObject jsonObject = new JSONObject();
                for (int index = 1; index <= dataSetWrapper.getColumnCount(); ++index) {
                    jsonObject.put(dataSetWrapper.getColumnName(index), dataSetWrapper.getValue(index));
                }
                pojoObject = this.getModelFromJsonObject(jsonObject, valueType, pojoObject, isUpperCase);
            }
            return pojoObject;
        }
        catch (final Exception exp) {
            PojoUtil.logger.log(Level.WARNING, "Exception while creating List of POJO from rows", exp);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        PojoUtil.logger = Logger.getLogger("MDMApiLogger");
    }
}
