package com.me.mdm.onpremise.server.settings;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.client.view.web.ViewContext;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.me.mdm.onpremise.server.settings.proxy.ProxyValidator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.PatchDataBaseSettingAPI;

public class ProxySettingsImpl implements PatchDataBaseSettingAPI
{
    public static final int MDM_TYPE = 21;
    public static final int DOMAIN_ACCESS_FAILED = 2;
    public static final int DOMAIN_ACCESS_PROGRESS = 3;
    public static final int DOMAIN_ACCESS_READY = 4;
    private static Logger out;
    
    public JSONObject getPatchDataBaseSetting() {
        final JSONObject updatesData = new JSONObject();
        return updatesData;
    }
    
    public void performActionAfterDomainValidation() {
        MDMUtil.setDomainValFailedAttributes();
        ProxyValidator.validate();
    }
    
    public Criteria getDomainExceptionListCri(final JSONObject patchDBSettingObj) {
        final Criteria domainCrit = null;
        return domainCrit;
    }
    
    public List getDomainExceptionList(final Criteria crit, final String urlType) {
        final List domainsList = new ArrayList();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DomainExceptionList"));
            if (crit != null) {
                selectQuery.setCriteria(crit);
            }
            selectQuery.setDistinct((boolean)Boolean.TRUE);
            selectQuery.addSelectColumn(Column.getColumn("DomainExceptionList", "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterate = dataObject.getRows("DomainExceptionList");
                while (iterate.hasNext()) {
                    final Properties domainProp = new Properties();
                    final Row domainRow = iterate.next();
                    ((Hashtable<String, String>)domainProp).put("URLID", String.valueOf(domainRow.get("URLID")));
                    ((Hashtable<String, Object>)domainProp).put("VENDORNAME", domainRow.get("VENDORNAME"));
                    ((Hashtable<String, Object>)domainProp).put("URLDOMAIN", domainRow.get("URLDOMAIN"));
                    ((Hashtable<String, String>)domainProp).put("URLTYPE", String.valueOf(domainRow.get("URLTYPE")));
                    ((Hashtable<String, Object>)domainProp).put("REMARKS", domainRow.get("REMARKS"));
                    ((Hashtable<String, Object>)domainProp).put("VERSION", domainRow.get("VERSION"));
                    domainsList.add(domainProp);
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return domainsList;
    }
    
    public Long[] getSupportedDomainIdList(final String urlType) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DomainExceptionList"));
            final Column colmn1 = new Column("DomainExceptionList", "URLID");
            selectQuery.addSelectColumn(colmn1);
            Criteria nullcrit = new Criteria(Column.getColumn("DomainExceptionList", "URLID"), (Object)null, 1);
            final Criteria crit = new Criteria(Column.getColumn("DomainExceptionList", "URLTYPE"), (Object)new Integer[] { 21 }, 8);
            nullcrit = nullcrit.and(crit);
            selectQuery.setCriteria(nullcrit);
            final DataObject dataObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObj.isEmpty()) {
                final Iterator iterate = dataObj.getRows("DomainExceptionList");
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
    
    public void updateDBSync() {
    }
    
    public Criteria getDomainExceptionListCri(final String urlType, final Boolean isDBUpdateValidation, final Long[] urlList) {
        Criteria domainCrit = null;
        Criteria versionCrit = null;
        try {
            if (isDBUpdateValidation && urlList != null) {
                long maxDBValue = 0L;
                if (SyMUtil.getSyMParameter("validated_domain_max_db_version") != null) {
                    maxDBValue = Long.parseLong(SyMUtil.getSyMParameter("validated_domain_max_db_version"));
                }
                versionCrit = new Criteria(Column.getColumn("DomainExceptionList", "VERSION"), (Object)maxDBValue, 5);
            }
            if ("mdm".equals(urlType)) {
                domainCrit = new Criteria(Column.getColumn("DomainExceptionList", "URLTYPE"), (Object)21, 0);
            }
            if (domainCrit != null && versionCrit != null) {
                domainCrit = domainCrit.and(versionCrit);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return domainCrit;
    }
    
    public String isMacEnabledSetup() {
        return "false";
    }
    
    public void setDomainExceptionListViewCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final String urlType = request.getParameter("urlType");
        final String pageType = request.getParameter("pageType");
        try {
            if (viewCtx.getUniqueId().equalsIgnoreCase("domainExceptionListView") && urlType != null && urlType.equals("mdm")) {
                final Criteria crit = new Criteria(Column.getColumn("DomainExceptionList", "URLTYPE"), (Object)21, 0);
                selectQuery.setCriteria(crit);
            }
            if (viewCtx.getUniqueId().equalsIgnoreCase("dcdomainExceptionListView") && urlType != null && urlType.equals("mdm")) {
                final Criteria crit = new Criteria(Column.getColumn("DCDomainExceptionList", "URLTYPE"), (Object)21, 0);
                if (pageType != null && pageType.equals("failedDomains")) {
                    final Criteria crit2 = new Criteria(Column.getColumn("DCDomainExceptionList", "STATUS"), (Object)new Integer[] { 2, 4, 3 }, 8);
                    selectQuery.setCriteria(crit.and(crit2));
                }
                else {
                    selectQuery.setCriteria(crit);
                }
            }
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
            ProxySettingsImpl.out.log(Level.FINE, "Query ==> {0}", sQuery);
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
    }
    
    static {
        ProxySettingsImpl.out = Logger.getLogger(ProxySettingsImpl.class.getName());
    }
}
