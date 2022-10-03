package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DomainValidatorUtil
{
    private static final Logger LOGGER;
    private static DomainValidatorUtil domainValidatorUtil;
    public static final int WIN_OS_TYPE = 1;
    public static final int WIN_TP_TYPE = 2;
    public static final int MAC_OS_TYPE = 3;
    public static final int MAC_TP_TYPE = 4;
    public static final int INV_TYPE = 11;
    public static final int MDM_TYPE = 21;
    public static final int DOMAIN_ACCESS_FAILED = 2;
    public static final int DOMAIN_ACCESS_SUCCESS = 1;
    public static final int DOMAIN_ACCESS_PROGRESS = 3;
    public static final int DOMAIN_ACCESS_READY = 4;
    
    public static DomainValidatorUtil getInstance() {
        if (DomainValidatorUtil.domainValidatorUtil == null) {
            DomainValidatorUtil.domainValidatorUtil = new DomainValidatorUtil();
        }
        return DomainValidatorUtil.domainValidatorUtil;
    }
    
    public List getDCValidatedDomainsList(final String urlType) {
        final List dcUrlList = new ArrayList();
        try {
            if (urlType != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCDomainExceptionList"));
                Criteria crit = null;
                if (urlType.equals("patch")) {
                    final JSONObject patchDBSettingObj = ApiFactoryProvider.getPatchDBAPI().getPatchDataBaseSetting();
                    crit = this.getPatchDBSettingsCrit(patchDBSettingObj);
                }
                else if (urlType.equals("inventory")) {
                    crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)11, 0);
                }
                else if (urlType.equals("mdm")) {
                    crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)21, 0);
                }
                selectQuery.setCriteria(crit);
                selectQuery.setDistinct((boolean)Boolean.TRUE);
                selectQuery.addSelectColumn(Column.getColumn("DCDomainExceptionList", "*"));
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterate = dataObject.getRows("DCDomainExceptionList");
                    while (iterate.hasNext()) {
                        final Properties domainProp = new Properties();
                        final Row domainRow = iterate.next();
                        ((Hashtable<String, String>)domainProp).put("URLID", String.valueOf(domainRow.get("URLID")));
                        ((Hashtable<String, Object>)domainProp).put("VENDORNAME", domainRow.get("VENDORNAME"));
                        ((Hashtable<String, Object>)domainProp).put("URLDOMAIN", domainRow.get("URLDOMAIN"));
                        ((Hashtable<String, String>)domainProp).put("URLTYPE", String.valueOf(domainRow.get("URLTYPE")));
                        ((Hashtable<String, Object>)domainProp).put("REMARKS", domainRow.get("REMARKS"));
                        dcUrlList.add(domainProp);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return dcUrlList;
    }
    
    public List getDCValidatedFailedDomainsList(final String urlType, final List dcUrlList) {
        try {
            if (urlType != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCDomainExceptionList"));
                Criteria crit = null;
                if (urlType.equals("patch")) {
                    final JSONObject patchDBSettingObj = ApiFactoryProvider.getPatchDBAPI().getPatchDataBaseSetting();
                    crit = this.getPatchDBSettingsCrit(patchDBSettingObj);
                }
                else if (urlType.equals("inventory")) {
                    crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)11, 0);
                }
                else if (urlType.equals("mdm")) {
                    crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)21, 0);
                }
                final Criteria failedCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "STATUS"), (Object)2, 0);
                if (crit == null) {
                    selectQuery.setCriteria(failedCrit);
                }
                else {
                    selectQuery.setCriteria(crit.and(failedCrit));
                }
                selectQuery.setDistinct((boolean)Boolean.TRUE);
                selectQuery.addSelectColumn(Column.getColumn("DCDomainExceptionList", "*"));
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterate = dataObject.getRows("DCDomainExceptionList");
                    while (iterate.hasNext()) {
                        final Properties domainProp = new Properties();
                        final Row domainRow = iterate.next();
                        ((Hashtable<String, String>)domainProp).put("URLID", String.valueOf(domainRow.get("URLID")));
                        ((Hashtable<String, Object>)domainProp).put("VENDORNAME", domainRow.get("VENDORNAME"));
                        ((Hashtable<String, Object>)domainProp).put("URLDOMAIN", domainRow.get("URLDOMAIN"));
                        ((Hashtable<String, String>)domainProp).put("URLTYPE", String.valueOf(domainRow.get("URLTYPE")));
                        ((Hashtable<String, Object>)domainProp).put("REMARKS", domainRow.get("REMARKS"));
                        dcUrlList.add(domainProp);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return dcUrlList;
    }
    
    public void setDCBulkValidationStatus(final String urlType, final int status) {
        try {
            Criteria crit = null;
            if (urlType.equals("patch")) {
                final JSONObject patchDBSettingObj = ApiFactoryProvider.getPatchDBAPI().getPatchDataBaseSetting();
                crit = this.getPatchDBSettingsCrit(patchDBSettingObj);
            }
            else if (urlType.equals("inventory")) {
                crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)11, 0);
            }
            else if (urlType.equals("mdm")) {
                crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)21, 0);
            }
            if (crit != null) {
                final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("DCDomainExceptionList");
                query.setCriteria(crit);
                query.setUpdateColumn("STATUS", (Object)status);
                final Persistence persistence = SyMUtil.getPersistence();
                persistence.update(query);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addOrUpdateDCDomainExceptionList(final Properties domainDetails) throws DataAccessException {
        try {
            final String vendorName = domainDetails.getProperty("VENDORNAME");
            final String urlDomain = domainDetails.getProperty("URLDOMAIN");
            final Integer urlType = Integer.parseInt(domainDetails.getProperty("URLTYPE"));
            final String remarks = domainDetails.getProperty("REMARKS");
            final Integer status = Integer.parseInt(domainDetails.getProperty("STATUS"));
            final Integer urlId = Integer.parseInt(domainDetails.getProperty("URLID"));
            final Criteria urlIdCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)urlId, 0);
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject dataObject = persistence.get("DCDomainExceptionList", urlIdCrit);
            final long currentTime = new Long(System.currentTimeMillis());
            if (dataObject.isEmpty()) {
                final Row domainRow = new Row("DCDomainExceptionList");
                domainRow.set("URLID", (Object)urlId);
                domainRow.set("VENDORNAME", (Object)vendorName);
                domainRow.set("URLDOMAIN", (Object)urlDomain);
                domainRow.set("URLTYPE", (Object)urlType);
                domainRow.set("REMARKS", (Object)remarks);
                domainRow.set("STATUS", (Object)status);
                domainRow.set("UPDATED_VERSION", (Object)currentTime);
                dataObject.addRow(domainRow);
                SyMUtil.getPersistence().add(dataObject);
            }
            else {
                final Row domainRow = dataObject.getRow("DCDomainExceptionList");
                domainRow.set("VENDORNAME", (Object)vendorName);
                domainRow.set("URLDOMAIN", (Object)urlDomain);
                domainRow.set("URLTYPE", (Object)urlType);
                domainRow.set("REMARKS", (Object)remarks);
                domainRow.set("STATUS", (Object)status);
                domainRow.set("UPDATED_VERSION", (Object)currentTime);
                dataObject.updateRow(domainRow);
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public static void deleteDCDomainExceptionList() {
        try {
            DomainValidatorUtil.LOGGER.log(Level.INFO, "deleteDCDomainExceptionList method called");
            final Column col = new Column("DCDomainExceptionList", "URLID");
            final Criteria criteria = new Criteria(col, (Object)new Integer(0), 1);
            DataAccess.delete(criteria);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void clearNotSupportedDomains() {
        try {
            DomainValidatorUtil.LOGGER.log(Level.INFO, "getNotSupportedDomains method called");
            final Long[] patchDomainsListLong = ApiFactoryProvider.getPatchDBAPI().getSupportedDomainIdList("patch");
            final Long[] domainsListLong = ApiFactoryProvider.getPatchDBAPI().getSupportedDomainIdList("others");
            Integer[] domainsList = null;
            Integer[] patchDomainsList = null;
            if (patchDomainsListLong != null) {
                patchDomainsList = new Integer[patchDomainsListLong.length];
                for (int i = 0; i < patchDomainsListLong.length; ++i) {
                    patchDomainsList[i] = patchDomainsListLong[i].intValue();
                }
            }
            if (domainsListLong != null) {
                domainsList = new Integer[domainsListLong.length];
                for (int i = 0; i < domainsListLong.length; ++i) {
                    domainsList[i] = domainsListLong[i].intValue();
                }
            }
            if (domainsList != null && patchDomainsList != null) {
                final Criteria patchCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)patchDomainsList, 9);
                final Criteria crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)domainsList, 9);
                DataAccess.delete("DCDomainExceptionList", patchCrit.and(crit));
            }
            else if (domainsList != null) {
                final Criteria crit2 = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)domainsList, 9);
                DataAccess.delete("DCDomainExceptionList", crit2);
            }
            else if (patchDomainsList != null) {
                final Criteria patchCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)patchDomainsList, 9);
                DataAccess.delete("DCDomainExceptionList", patchCrit);
            }
            else {
                DomainValidatorUtil.LOGGER.log(Level.INFO, "Crawler domainsList is empty");
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public Criteria getPatchDBSettingsNotSuppDomains(final JSONObject patchDBSettingObj) {
        Criteria domainCrit = null;
        try {
            if (!patchDBSettingObj.optBoolean("ENABLE_WIN_OS_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)1, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)1, 0);
                }
            }
            if (!patchDBSettingObj.optBoolean("ENABLE_WIN_TP_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)2, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)2, 0);
                }
            }
            if (!patchDBSettingObj.optBoolean("ENABLE_MAC_OS_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)3, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)3, 0);
                }
            }
            if (!patchDBSettingObj.optBoolean("ENABLE_MAC_TP_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)4, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)4, 0);
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return domainCrit;
    }
    
    public Criteria getPatchDBSettingsCrit(final JSONObject patchDBSettingObj) {
        Criteria domainCrit = null;
        try {
            final String isMacEnabled = ApiFactoryProvider.getPatchDBAPI().isMacEnabledSetup();
            if (patchDBSettingObj.optBoolean("ENABLE_WIN_OS_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)1, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)1, 0);
                }
            }
            if (patchDBSettingObj.optBoolean("ENABLE_WIN_TP_UPDATES")) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)2, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)2, 0);
                }
            }
            if (patchDBSettingObj.optBoolean("ENABLE_MAC_OS_UPDATES") && "true".equals(isMacEnabled)) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)3, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)3, 0);
                }
            }
            if (patchDBSettingObj.optBoolean("ENABLE_MAC_TP_UPDATES") && "true".equals(isMacEnabled)) {
                if (domainCrit != null) {
                    domainCrit = domainCrit.or(new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)4, 0));
                }
                else {
                    domainCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)4, 0);
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return domainCrit;
    }
    
    public void removePatchDBSettingsNotSuppDomains() {
        try {
            final JSONObject patchDBSettingObj = ApiFactoryProvider.getPatchDBAPI().getPatchDataBaseSetting();
            final Criteria crit = this.getPatchDBSettingsNotSuppDomains(patchDBSettingObj);
            if (crit != null) {
                DataAccess.delete("DCDomainExceptionList", crit);
            }
            if ("failed".equals(SyMUtil.getSyMParameter("patch_domain_validation"))) {
                final Criteria domainCrit = this.getPatchDBSettingsCrit(patchDBSettingObj);
                final Criteria failedCrit = new Criteria(Column.getColumn("DCDomainExceptionList", "STATUS"), (Object)2, 0);
                final Persistence persistence = SyMUtil.getPersistence();
                final DataObject dataObject = persistence.get("DCDomainExceptionList", domainCrit.and(failedCrit));
                if (dataObject.isEmpty()) {
                    SyMUtil.updateSyMParameter("patch_domain_validation", "success");
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
    }
    
    public Long[] getDCDomainIdList() {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCDomainExceptionList"));
            final Column colmn1 = new Column("DCDomainExceptionList", "URLID");
            selectQuery.addSelectColumn(colmn1);
            final Criteria nullcrit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLID"), (Object)null, 1);
            selectQuery.setCriteria(nullcrit);
            final DataObject dataObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObj.isEmpty()) {
                final Iterator iterate = dataObj.getRows("DCDomainExceptionList");
                final List<Long> domainsList = new ArrayList<Long>();
                while (iterate.hasNext()) {
                    final Row domainRow = iterate.next();
                    final long urlId = Long.parseLong(String.valueOf(domainRow.get("URLID")));
                    domainsList.add(urlId);
                }
                if (domainsList.size() > 0) {
                    final Long[] domainsListAry = domainsList.toArray(new Long[domainsList.size()]);
                    return domainsListAry;
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(DomainValidatorUtil.class.getName());
        DomainValidatorUtil.domainValidatorUtil = null;
    }
}
