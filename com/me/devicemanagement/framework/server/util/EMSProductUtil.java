package com.me.devicemanagement.framework.server.util;

import org.json.JSONArray;
import com.me.devicemanagement.framework.utils.EMSSuiteConfigurations;
import org.json.JSONObject;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.adventnet.persistence.DataObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class EMSProductUtil
{
    private static EMSProductUtil emsProductUtil;
    protected static Logger logger;
    public static ArrayList currentproductList;
    public static ArrayList ems_supported_products;
    public static DataObject emsProductsDO;
    
    public static EMSProductUtil getInstance() {
        if (EMSProductUtil.emsProductUtil == null) {
            EMSProductUtil.emsProductUtil = new EMSProductUtil();
        }
        return EMSProductUtil.emsProductUtil;
    }
    
    public static ArrayList getEMSProductCode() {
        ArrayList arrayList = new ArrayList();
        try {
            final String serverURLClass = ProductClassLoader.getSingleImplProductClass("DM_UTIL_ACCESS_API_CLASS");
            final UtilAccessAPI utilAccessAPI = (UtilAccessAPI)Class.forName(serverURLClass).newInstance();
            arrayList = utilAccessAPI.getEMSProductCode();
        }
        catch (final Exception ex) {
            EMSProductUtil.logger.log(Level.WARNING, "Exception in getEMSProductCode: ", ex);
        }
        return arrayList;
    }
    
    public static Long getBitwiseValueForCurrentProduct() {
        final ArrayList currentProductCodes = getEMSProductCode();
        return getBitwiseValueForProductCode(currentProductCodes);
    }
    
    private static Long getBitwiseValueForProductCode(final ArrayList<String> productCodes) {
        Long bitwiseValue = 0L;
        try {
            final DataObject emsProductsDO = getEMSProductsDO();
            final List bitwiseIndexList = DBUtil.getColumnValuesAsList(emsProductsDO.getRows("EMSProductCodes", new Criteria(Column.getColumn("EMSProductCodes", "EMS_PRODUCT_CODE"), (Object)productCodes.toArray(), 8)), "BITWISE_INDEX");
            for (final Object bitwiseIndex : bitwiseIndexList) {
                bitwiseValue += new BigDecimal(2).pow(Integer.parseInt(bitwiseIndex.toString())).longValue();
            }
        }
        catch (final Exception e) {
            EMSProductUtil.logger.log(Level.WARNING, "Exception occurred while getting Bitwise index value for product codes : " + productCodes, e);
        }
        return bitwiseValue;
    }
    
    public static Long getBitwiseValueForProductCode(final String productCode) {
        Long bitwiseValue = 0L;
        try {
            final DataObject emsProductsDO = getEMSProductsDO();
            final Row emsProductsRow = emsProductsDO.getRow("EMSProductCodes", new Criteria(Column.getColumn("EMSProductCodes", "EMS_PRODUCT_CODE"), (Object)productCode, 0));
            if (emsProductsRow != null) {
                bitwiseValue += new BigDecimal(2).pow(Integer.parseInt(emsProductsRow.get("BITWISE_INDEX").toString())).longValue();
            }
            else {
                EMSProductUtil.logger.log(Level.INFO, "Bitwise index not available for the EMS Product code : " + productCode);
            }
        }
        catch (final Exception ex) {
            EMSProductUtil.logger.log(Level.SEVERE, "Exception occurred while getting Bitwise index value for product code {0} : {1}", new Object[] { productCode, ex });
        }
        return bitwiseValue;
    }
    
    private static DataObject getEMSProductsDO() throws SyMException {
        try {
            if (EMSProductUtil.emsProductsDO == null || EMSProductUtil.emsProductsDO.isEmpty()) {
                EMSProductUtil.emsProductsDO = getEMSProductsDOFromDB();
            }
        }
        catch (final Exception ex) {
            EMSProductUtil.logger.log(Level.SEVERE, "Caught exception while getting EMS Products DO...", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return EMSProductUtil.emsProductsDO;
    }
    
    private static DataObject getEMSProductsDOFromDB() throws SyMException {
        DataObject emsProductsDO = null;
        try {
            EMSProductUtil.logger.log(Level.FINE, "Loading getEMSProductsDOFromDB...");
            emsProductsDO = DataAccess.get("EMSProductCodes", (Criteria)null);
        }
        catch (final Exception ex) {
            EMSProductUtil.logger.log(Level.WARNING, "Caught exception while retrieving EMS Products DO from DB...", ex);
        }
        return emsProductsDO;
    }
    
    public static Criteria getBitwiseCriteriaForCurrentProduct(final String tableName, final String columnName) {
        Criteria bitwiseCriteria = null;
        try {
            final Long bitwiseValue = getBitwiseValueForCurrentProduct();
            bitwiseCriteria = new Criteria(Column.getColumn(tableName, columnName), (Object)bitwiseValue, 0);
        }
        catch (final Exception e) {
            EMSProductUtil.logger.log(Level.WARNING, "Exception occurred in getBitwiseCriteriaForCurrentProduct...", e);
        }
        return bitwiseCriteria;
    }
    
    public static Criteria getBitwiseCriteriaForProductCode(final String productCode, final String tableName, final String columnName) {
        Criteria bitwiseCriteria = null;
        try {
            final Long bitwiseValue = getBitwiseValueForProductCode(productCode);
            bitwiseCriteria = new Criteria(Column.getColumn(tableName, columnName), (Object)bitwiseValue, 0);
        }
        catch (final Exception e) {
            EMSProductUtil.logger.log(Level.WARNING, "Exception occurred in getBitwiseCriteriaForProductCode...", e);
        }
        return bitwiseCriteria;
    }
    
    public static List getEMSProductsListForBitwiseValue(final Long bitwiseValue) {
        List emsProductsList = new ArrayList();
        try {
            if (bitwiseValue == 0L) {
                emsProductsList = DBUtil.getColumnValuesAsList(getEMSProductsDO().getRows("EMSProductCodes"), "EMS_PRODUCT_CODE");
            }
            else {
                final List bitwiseIndexList = getBitwiseIndexListForBitwiseValue(bitwiseValue);
                emsProductsList = DBUtil.getColumnValuesAsList(getEMSProductsDO().getRows("EMSProductCodes", new Criteria(Column.getColumn("EMSProductCodes", "BITWISE_INDEX"), (Object)bitwiseIndexList.toArray(), 8)), "EMS_PRODUCT_CODE");
            }
        }
        catch (final Exception e) {
            EMSProductUtil.logger.log(Level.WARNING, "Exception occurred in getEMSProductsForBitwiseValue...", e);
        }
        return emsProductsList;
    }
    
    private static List getBitwiseIndexListForBitwiseValue(final Long bitwiseValue) {
        final List bitwiseIndexList = new ArrayList();
        String binaryValue = Long.toBinaryString(bitwiseValue);
        binaryValue = new StringBuilder(binaryValue).reverse().toString();
        final char[] binaryValueArray = binaryValue.toCharArray();
        for (int i = 0; i < binaryValueArray.length; ++i) {
            if (binaryValueArray[i] == '1') {
                bitwiseIndexList.add(i);
            }
        }
        return bitwiseIndexList;
    }
    
    public static Criteria constructProductCodeCriteria(final String tableName, final String tableColumn, final Integer productCode) {
        Boolean isCriteriaEnabled = false;
        Criteria criteria = null;
        try {
            final String serverURLClass = ProductClassLoader.getSingleImplProductClass("DM_UTIL_ACCESS_API_CLASS");
            final UtilAccessAPI utilAccessAPI = (UtilAccessAPI)Class.forName(serverURLClass).newInstance();
            isCriteriaEnabled = utilAccessAPI.isProductColumnCriteriaEnabled();
        }
        catch (final Exception exception) {
            EMSProductUtil.logger.log(Level.INFO, "Exception while gettings isSAS Migration Flag", exception);
            return null;
        }
        if (isCriteriaEnabled) {
            final Column updCol = (Column)Column.createFunction("AND", new Object[] { Column.getColumn(tableName, tableColumn), productCode });
            updCol.setType(4);
            updCol.setTableAlias(tableName);
            criteria = new Criteria(updCol, (Object)getBitwiseValueForCurrentProduct(), 0).or(new Criteria(Column.getColumn(tableName, tableColumn), (Object)0, 0));
        }
        return criteria;
    }
    
    public static Criteria constructProductCodeCriteria(final String tableName) {
        return constructProductCodeCriteria(tableName, "PRODUCT_CODE");
    }
    
    public static Criteria constructProductCodeCriteria(final String tableName, final String tableColumn) {
        return constructProductCodeCriteria(tableName, tableColumn, (int)(long)getBitwiseValueForCurrentProduct());
    }
    
    public static Boolean isEMSFlowSupportedForCurrentProduct() {
        return isEMSFlowSupportedForProductCode(String.valueOf(getEMSProductCode().get(0)));
    }
    
    public static Boolean isEMSFlowSupportedForProductCode(final String productCode) {
        final ArrayList ems_supported_products = getProductsInEMSFlow();
        return ems_supported_products.contains(productCode);
    }
    
    public static ArrayList getProductsInEMSFlow() {
        final JSONObject jsonObject = new JSONObject();
        if (EMSProductUtil.ems_supported_products.isEmpty()) {
            try {
                if (EMSSuiteConfigurations.loadJsonFile(jsonObject).has("EMSS_SUPPORTED_PRODUCTS")) {
                    final JSONArray jsonArray = (JSONArray)jsonObject.get("EMSS_SUPPORTED_PRODUCTS");
                    for (int len = jsonArray.length(), i = 0; i < len; ++i) {
                        EMSProductUtil.ems_supported_products.add(jsonArray.get(i));
                    }
                }
            }
            catch (final Exception ex) {
                EMSProductUtil.logger.log(Level.SEVERE, "Exception while getting EMSConfigurations from isEMSFlowSupportedForProductCode method", ex);
            }
        }
        return EMSProductUtil.ems_supported_products;
    }
    
    static {
        EMSProductUtil.emsProductUtil = null;
        EMSProductUtil.logger = Logger.getLogger(EMSProductUtil.class.getName());
        EMSProductUtil.currentproductList = new ArrayList();
        EMSProductUtil.ems_supported_products = new ArrayList();
        EMSProductUtil.emsProductsDO = null;
    }
}
