package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.logging.Level;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class EMSServerUtil
{
    private static EMSServerUtil emsServerUtil;
    private static Logger logger;
    public static final String SUMMARY_SERVER = "Summary";
    public static final String PROBE_SERVER = "Probe";
    public static final String STANDALONE_SERVER = "Standalone";
    public static DataObject emsServerTypesDO;
    
    public static EMSServerUtil getInstance() {
        if (EMSServerUtil.emsServerUtil == null) {
            EMSServerUtil.emsServerUtil = new EMSServerUtil();
        }
        return EMSServerUtil.emsServerUtil;
    }
    
    public static String getCurrentEMSServerType() {
        final String serverType = null;
        try {
            if (SyMUtil.isSummaryServer()) {
                return "Summary";
            }
            if (SyMUtil.isProbeServer()) {
                return "Probe";
            }
            return "Standalone";
        }
        catch (final Exception exception) {
            EMSServerUtil.logger.log(Level.INFO, "Exception while getting EMSServerType" + exception);
            return serverType;
        }
    }
    
    public static Long getBitwiseValueForCurrentServer() {
        final String currentEMSServerType = getCurrentEMSServerType();
        final ArrayList serverTypes = new ArrayList();
        serverTypes.add(currentEMSServerType);
        return getBitwiseValueForServerTypes(serverTypes);
    }
    
    public static Long getBitwiseValueForServerTypes(final ArrayList<String> serverTypes) {
        Long bitwiseValue = 0L;
        try {
            final DataObject emsServerTypesDO = getEMSServerTypesDO();
            final List bitwiseIndexList = DBUtil.getColumnValuesAsList(emsServerTypesDO.getRows("EMSServerTypes", new Criteria(Column.getColumn("EMSServerTypes", "EMS_SERVER_TYPE"), (Object)serverTypes.toArray(), 8)), "BITWISE_INDEX");
            for (final Object bitwiseIndex : bitwiseIndexList) {
                bitwiseValue += new BigDecimal(2).pow(Integer.parseInt(bitwiseIndex.toString())).longValue();
            }
        }
        catch (final Exception e) {
            EMSServerUtil.logger.log(Level.WARNING, "Exception occurred while getting Bitwise index value for server types : " + serverTypes, e);
        }
        return bitwiseValue;
    }
    
    public static Long getBitwiseValueForServerType(final String serverType) {
        Long bitwiseValue = 0L;
        try {
            final DataObject emsServerTypesDO = getEMSServerTypesDO();
            final Row emsServerTypeRow = emsServerTypesDO.getRow("EMSServerTypes", new Criteria(Column.getColumn("EMSServerTypes", "EMS_SERVER_TYPE"), (Object)serverType, 0));
            if (emsServerTypeRow != null) {
                bitwiseValue += new BigDecimal(2).pow(Integer.parseInt(emsServerTypeRow.get("BITWISE_INDEX").toString())).longValue();
            }
            else {
                EMSServerUtil.logger.log(Level.INFO, "Bitwise index not available for the EMS Server Type : " + serverType);
            }
        }
        catch (final Exception e) {
            EMSServerUtil.logger.log(Level.WARNING, "Exception occurred while getting Bitwise index value for server type : " + serverType, e);
        }
        return bitwiseValue;
    }
    
    private static DataObject getEMSServerTypesDO() throws SyMException {
        try {
            if (EMSServerUtil.emsServerTypesDO == null || EMSServerUtil.emsServerTypesDO.isEmpty()) {
                EMSServerUtil.emsServerTypesDO = getEMSServerTypesDOFromDB();
            }
        }
        catch (final Exception ex) {
            EMSServerUtil.logger.log(Level.SEVERE, "Caught exception while getting EMS ServerTypes DO...", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return EMSServerUtil.emsServerTypesDO;
    }
    
    private static DataObject getEMSServerTypesDOFromDB() throws SyMException {
        DataObject emsServerTypesDO = null;
        try {
            EMSServerUtil.logger.log(Level.FINE, "Loading getEMSServerTypesDOFromDB...");
            emsServerTypesDO = DataAccess.get("EMSServerTypes", (Criteria)null);
        }
        catch (final Exception ex) {
            EMSServerUtil.logger.log(Level.WARNING, "Caught exception while retrieving EMS ServerTypes DO from DB...", ex);
        }
        return emsServerTypesDO;
    }
    
    public static boolean isApplicableForCurrentProduct(final ArrayList serverTypes) {
        final Long currentServerBitWise = getBitwiseValueForCurrentServer();
        for (int i = 0; i < serverTypes.size(); ++i) {
            final String serverType = serverTypes.get(i);
            final Long serverBitWise = getBitwiseValueForServerType(serverType);
            if (serverBitWise == currentServerBitWise) {
                return true;
            }
        }
        return false;
    }
    
    public static Criteria constructServerTypeCriteria(final String tableName, final String tableColumn, final Integer serverType) {
        final Column updCol = (Column)Column.createFunction("AND", new Object[] { Column.getColumn(tableName, tableColumn), serverType });
        updCol.setType(1);
        updCol.setTableAlias(tableName);
        final Criteria criteria = new Criteria(updCol, (Object)getBitwiseValueForCurrentServer(), 0).or(new Criteria(Column.getColumn(tableName, tableColumn), (Object)0, 0));
        return criteria;
    }
    
    public static Criteria constructServerTypeCriteria(final String tableName) {
        return constructServerTypeCriteria(tableName, "SERVER_TYPE");
    }
    
    public static Criteria constructServerTypeCriteria(final String tableName, final String tableColumn) {
        return constructServerTypeCriteria(tableName, tableColumn, (int)(long)getBitwiseValueForCurrentServer());
    }
    
    public static boolean isMatchingServerType(final String serverTypes) {
        if (serverTypes != null && !serverTypes.equals("")) {
            boolean isMatching = false;
            final UtilAccessAPI uai = ApiFactoryProvider.getUtilAccessAPI();
            final boolean isNegated = serverTypes.contains("!");
            if (serverTypes.contains("PROBE")) {
                isMatching = ((!isNegated && uai.isProbeServer()) || (isNegated && !uai.isProbeServer()));
            }
            else if (serverTypes.contains("SUMMARY")) {
                isMatching = ((!isNegated && uai.isSummaryServer()) || (isNegated && !uai.isSummaryServer()));
            }
            else if (serverTypes.contains("STANDALONE")) {
                isMatching = ((!isNegated && !uai.isSummaryServer() && !uai.isProbeServer()) || (isNegated && (uai.isSummaryServer() || uai.isProbeServer())));
            }
            if (!isMatching) {
                return false;
            }
        }
        return true;
    }
    
    static {
        EMSServerUtil.emsServerUtil = null;
        EMSServerUtil.logger = Logger.getLogger(EMSServerUtil.class.getName());
        EMSServerUtil.emsServerTypesDO = null;
    }
}
