package com.me.mdm.server.directory;

import java.util.Set;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class DirectoryTemplateHandler
{
    private Logger logger;
    
    public DirectoryTemplateHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    private DataObject getDOForTemplates(final Long bind_policy_id, final Long customer_id, Criteria criteria) throws APIHTTPException {
        try {
            final SelectQuery query = this.getTemplateQuery();
            this.addSelectColumnsForTemplate(query);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"), (Object)bind_policy_id, 0);
                if (bind_policy_id != -1L) {
                    criteria = criteria.and(new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"), (Object)customer_id, 0));
                }
            }
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            return dataObject;
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~Exception occured at DirectoryTemplateHandler[getDOForTemplate]~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~Exception occured at DirectoryTemplateHandler[getDOForTemplate]~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONArray dataObjectToJSONArrayTemplates(final DataObject dataObject) throws APIHTTPException {
        try {
            final Iterator dirRows = dataObject.getRows("DirectoryBindPolicyTemplate");
            final JSONArray templates = new JSONArray();
            while (dirRows.hasNext()) {
                final Row dirRow = dirRows.next();
                final Long bind_policy_id = (Long)dirRow.get("BIND_POLICY_ID");
                final int type = (int)dirRow.get("TYPE");
                final Long domain_id = (Long)dirRow.get("DOMAIN_ID");
                final Row domainRow = dataObject.getRow("DMManagedDomain", new Criteria(Column.getColumn("DMManagedDomain", "DOMAIN_ID"), (Object)domain_id, 0));
                final String domain = (String)domainRow.get("AD_DOMAIN_NAME");
                switch (type) {
                    case 1: {
                        final Row adRow = dataObject.getRow("ADBindPolicyTemplate", new Criteria(Column.getColumn("ADBindPolicyTemplate", "BIND_POLICY_ID"), (Object)bind_policy_id, 0));
                        if (adRow == null) {
                            continue;
                        }
                        final JSONObject template = adRow.getAsJSON();
                        template.put("name", (Object)dirRow.get("NAME"));
                        template.put("bind_policy_id", (Object)bind_policy_id);
                        template.put("customer_id", (Object)dirRow.get("CUSTOMER_ID"));
                        template.put("domain_id", (Object)domain_id);
                        template.put("ad_domain_name", (Object)domain);
                        template.put("description", (Object)dirRow.get("DESCRIPTION"));
                        template.put("added_at", (Object)dirRow.get("ADDED_AT"));
                        template.put("added_by", (Object)dirRow.get("ADDED_BY"));
                        template.put("modified_at", (Object)dirRow.get("MODIFIED_AT"));
                        template.put("modified_by", (Object)dirRow.get("MODIFIED_BY"));
                        template.put("type", type);
                        final Iterator rows = dataObject.getRows("ADBindRestrictedDDNS", new Criteria(Column.getColumn("ADBindRestrictedDDNS", "BIND_POLICY_ID"), (Object)bind_policy_id, 0));
                        final JSONArray ddnsArray = new JSONArray();
                        while (rows.hasNext()) {
                            final Row ddnsRow = rows.next();
                            final String ddns = (String)ddnsRow.get("RESTRICTED_INTERFACE");
                            if (ddns != null) {
                                ddnsArray.put((Object)ddns);
                            }
                        }
                        if (ddnsArray.length() > 0) {
                            template.put("adrestrictddns", (Object)ddnsArray);
                        }
                        templates.put((Object)template);
                        continue;
                    }
                }
            }
            return templates;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getDirectoryTemplate(final Long bind_policy_id, final Long customer) throws APIHTTPException {
        try {
            final DataObject dataObject = this.getDOForTemplates(bind_policy_id, customer, null);
            final JSONArray templateArray = this.dataObjectToJSONArrayTemplates(dataObject);
            return templateArray.getJSONObject(0);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDirectoryTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDirectoryTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getAllDirectoryTemplate(final JSONObject request) throws APIHTTPException {
        final JSONObject templates = new JSONObject();
        Long domain = -1L;
        try {
            final Long customer_id = APIUtil.getCustomerID(request);
            try {
                domain = APIUtil.getLongFilter(request, "domain");
            }
            catch (final Exception e) {
                domain = -1L;
            }
            Criteria criteria = new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"), (Object)customer_id, 0);
            if (domain != -1L && domain != 0L) {
                criteria = criteria.and(new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)domain, 0));
            }
            final DataObject dataObject = this.getDOForTemplates(-1L, customer_id, criteria);
            final JSONArray templateArray = this.dataObjectToJSONArrayTemplates(dataObject);
            templates.put("directory_bind_templates", (Object)templateArray);
            return templates;
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getAllDirectoryTemplates] ~~~~~~~~~~~~~~~~~~~~~", e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getAllDirectoryTemplates] ~~~~~~~~~~~~~~~~~~~~~", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private SelectQuery getTemplateQuery() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectoryBindPolicyTemplate"));
        query.addJoin(new Join("DirectoryBindPolicyTemplate", "ADBindPolicyTemplate", new String[] { "BIND_POLICY_ID" }, new String[] { "BIND_POLICY_ID" }, 1));
        query.addJoin(new Join("DirectoryBindPolicyTemplate", "DMDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
        query.addJoin(new Join("DMDomain", "DMManagedDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
        query.addJoin(new Join("DMManagedDomain", "DMManagedDomainCredentialRel", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 1));
        query.addJoin(new Join("DMManagedDomainCredentialRel", "Credential", new String[] { "CREDENTIAL_ID" }, new String[] { "CREDENTIAL_ID" }, 1));
        query.addJoin(new Join("DMDomain", "DirResRel", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 1));
        query.addJoin(new Join("DirResRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addJoin(new Join("ADBindPolicyTemplate", "ADBindRestrictedDDNS", new String[] { "BIND_POLICY_ID" }, new String[] { "BIND_POLICY_ID" }, 1));
        return query;
    }
    
    private void validateIfTemplateExists(final Long template, final Long customer) throws APIHTTPException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DirectoryBindPolicyTemplate"));
            query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"));
            query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"));
            Criteria criteria = new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"), (Object)template, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"), (Object)customer, 0));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:validateifTemplateExists] ~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:validateifTemplateExists] ~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void setActiveDirectoryRow(final JSONObject request, final Row adrow) {
        try {
            if (request.has("create_ma_at_login")) {
                adrow.set("CREATE_MA_AT_LOGIN", (Object)Boolean.valueOf(String.valueOf(request.get("create_ma_at_login"))));
            }
            if (request.has("warn_before_ma")) {
                adrow.set("WARN_BEFORE_MA", (Object)Boolean.valueOf(String.valueOf(request.get("warn_before_ma"))));
            }
            if (request.has("force_home_local")) {
                adrow.set("FORCE_HOME_LOCAL", (Object)Boolean.valueOf(String.valueOf(request.get("force_home_local"))));
            }
            if (request.has("windows_unc_path")) {
                adrow.set("WINDOWS_UNC_PATH", (Object)Boolean.valueOf(String.valueOf(request.get("windows_unc_path"))));
            }
            if (request.has("default_shell") && !MDMStringUtils.isEmpty(String.valueOf(request.get("default_shell")))) {
                adrow.set("DEFAULT_SHELL", (Object)String.valueOf(request.get("default_shell")));
            }
            if (request.has("preferred_dc") && !MDMStringUtils.isEmpty(String.valueOf(request.get("preferred_dc")))) {
                adrow.set("PREFERRED_DC", (Object)String.valueOf(request.get("preferred_dc")));
            }
            if (request.has("preferred_dc_flag")) {
                adrow.set("PREFERRED_DC_FLAG", (Object)Boolean.valueOf(String.valueOf(request.get("preferred_dc_flag"))));
            }
            if (request.has("allow_multi_domain_auth")) {
                adrow.set("ALLOW_MULTI_DOMAIN_AUTH", (Object)Boolean.valueOf(String.valueOf(request.get("allow_multi_domain_auth"))));
            }
            if (request.has("namespace")) {
                adrow.set("NAMESPACE", (Object)Integer.parseInt(String.valueOf(request.get("namespace"))));
            }
            if (request.has("mount_style")) {
                adrow.set("MOUNT_STYLE", (Object)Integer.parseInt(String.valueOf(request.get("mount_style"))));
            }
            if (request.has("packet_sign")) {
                adrow.set("PACKET_SIGN", (Object)Integer.parseInt(String.valueOf(request.get("packet_sign"))));
            }
            if (request.has("packet_encryption")) {
                adrow.set("PACKET_ENCRYPTION", (Object)Integer.parseInt(String.valueOf(request.get("packet_encryption"))));
            }
            if (request.has("trust_pass_interval")) {
                adrow.set("TRUST_PASS_INTERVAL", (Object)Integer.parseInt(String.valueOf(request.get("trust_pass_interval"))));
            }
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject createTemplate(final JSONObject message) throws APIHTTPException {
        Long customer_id = -1L;
        Long policy_id = -1L;
        try {
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject request = message.getJSONObject("msg_body");
            if (request.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            customer_id = APIUtil.getCustomerID(message);
            final Long user = APIUtil.getUserID(message);
            new MDMUtil();
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            Row dirrow = new Row("DirectoryBindPolicyTemplate");
            final Row adrow = new Row("ADBindPolicyTemplate");
            String name;
            if (request.has("name") && !MDMStringUtils.isEmpty(String.valueOf(request.get("name")))) {
                name = String.valueOf(request.get("name"));
            }
            else {
                name = "DirectoryBindPolicyTemplate";
            }
            dirrow.set("NAME", (Object)name);
            dirrow.set("CUSTOMER_ID", (Object)customer_id);
            Long domain_id;
            if (request.has("domain_id")) {
                domain_id = JSONUtil.optLong(request, "domain_id", -1L);
            }
            else {
                domain_id = this.getDomainIDFromName(customer_id, String.valueOf(request.get("domain_name")));
            }
            dirrow.set("DOMAIN_ID", (Object)domain_id);
            dirrow.set("TYPE", (Object)Integer.parseInt(String.valueOf(request.get("type"))));
            if (request.has("description") && !MDMStringUtils.isEmpty(String.valueOf(request.get("description")))) {
                dirrow.set("DESCRIPTION", (Object)String.valueOf(request.get("description")));
            }
            else {
                dirrow.set("DESCRIPTION", (Object)"This is a template for binding Mac devices to directory services.");
            }
            dirrow.set("ADDED_AT", (Object)System.currentTimeMillis());
            dirrow.set("MODIFIED_AT", (Object)System.currentTimeMillis());
            dirrow.set("ADDED_BY", (Object)user);
            dirrow.set("MODIFIED_BY", (Object)user);
            dataObject.addRow(dirrow);
            switch (request.getInt("type")) {
                case 1: {
                    this.setActiveDirectoryRow(request, adrow);
                    adrow.set("BIND_POLICY_ID", dirrow.get("BIND_POLICY_ID"));
                    dataObject.addRow(adrow);
                    JSONArray ddns = null;
                    if (request.has("adrestrictddns")) {
                        ddns = new JSONArray(String.valueOf(request.get("adrestrictddns")));
                    }
                    if (ddns != null) {
                        final Set<String> array = new HashSet<String>();
                        for (int i = 0; i < ddns.length(); ++i) {
                            array.add(String.valueOf(ddns.get(i)));
                        }
                        final Iterator iterator = array.iterator();
                        while (iterator.hasNext()) {
                            final Row ddnsrow = new Row("ADBindRestrictedDDNS");
                            ddnsrow.set("RESTRICTED_INTERFACE", (Object)iterator.next());
                            ddnsrow.set("BIND_POLICY_ID", adrow.get("BIND_POLICY_ID"));
                            dataObject.addRow(ddnsrow);
                        }
                    }
                    dataObject = MDMUtil.getPersistence().add(dataObject);
                    dirrow = dataObject.getRow("DirectoryBindPolicyTemplate");
                    policy_id = (Long)dirrow.get("BIND_POLICY_ID");
                    return this.getDirectoryTemplate(policy_id, customer_id);
                }
                default: {
                    throw new APIHTTPException("COM0014", new Object[0]);
                }
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:createTemplate] ~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:createTemplate] ~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject modifyTemplate(final JSONObject message) throws APIHTTPException {
        try {
            final Long policy_id = APIUtil.getResourceID(message, "bindpolicytemplate_id");
            final Long customer_id = APIUtil.getCustomerID(message);
            this.validateIfTemplateExists(policy_id, customer_id);
            if (!message.has("msg_body")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject request = message.getJSONObject("msg_body");
            if (request.length() == 0) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            request.put("BIND_POLICY_ID", (Object)policy_id);
            final Iterator<String> keys = request.keys();
            final Set<String> fields = new HashSet<String>();
            Criteria criteria = new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"), (Object)policy_id, 0);
            DataObject dataObject = MDMUtil.getPersistence().get("DirectoryBindPolicyTemplate", criteria);
            final Row DirRow = dataObject.getRow("DirectoryBindPolicyTemplate");
            final int type = (int)DirRow.get("TYPE");
            if (request.has("name") && String.valueOf(request.get("name")) != null) {
                DirRow.set("NAME", (Object)String.valueOf(request.get("name")));
            }
            if (request.has("domain_id") && JSONUtil.optLongForUVH(request, "domain_id", Long.valueOf(-1L)) != -1L) {
                DirRow.set("DOMAIN_ID", (Object)JSONUtil.optLongForUVH(request, "domain_id", Long.valueOf(-1L)));
            }
            else if (request.has("domain_name")) {
                DirRow.set("DOMAIN_ID", (Object)this.getDomainIDFromName(customer_id, String.valueOf(request.get("domain_name"))));
            }
            if (request.has("description") && String.valueOf(request.get("description")) != null) {
                DirRow.set("DESCRIPTION", (Object)String.valueOf(request.get("description")));
            }
            final Long user = APIUtil.getUserID(message);
            DirRow.set("MODIFIED_BY", (Object)user);
            DirRow.set("MODIFIED_AT", (Object)System.currentTimeMillis());
            dataObject.updateRow(DirRow);
            MDMUtil.getPersistence().update(dataObject);
            switch (type) {
                case 1: {
                    criteria = new Criteria(Column.getColumn("ADBindPolicyTemplate", "BIND_POLICY_ID"), (Object)policy_id, 0);
                    dataObject = MDMUtil.getPersistence().get("ADBindPolicyTemplate", criteria);
                    final Row adRow = dataObject.getRow("ADBindPolicyTemplate");
                    this.setActiveDirectoryRow(request, adRow);
                    dataObject.updateRow(adRow);
                    MDMUtil.getPersistence().update(dataObject);
                    JSONArray ddnsJson = null;
                    if (request.has("adrestrictddns")) {
                        ddnsJson = new JSONArray(String.valueOf(request.get("adrestrictddns")));
                    }
                    if (ddnsJson != null) {
                        criteria = new Criteria(Column.getColumn("ADBindRestrictedDDNS", "BIND_POLICY_ID"), (Object)policy_id, 0);
                        dataObject = MDMUtil.getPersistence().get("ADBindRestrictedDDNS", criteria);
                        if (!dataObject.isEmpty()) {
                            dataObject.deleteRows("ADBindRestrictedDDNS", criteria);
                            MDMUtil.getPersistenceLite().update(dataObject);
                        }
                        final Set<String> set = new HashSet<String>();
                        for (int i = 0; i < ddnsJson.length(); ++i) {
                            set.add(String.valueOf(ddnsJson.get(i)));
                        }
                        for (final String ddns : set) {
                            final Row ddnsRow = new Row("ADBindRestrictedDDNS");
                            ddnsRow.set("BIND_POLICY_ID", (Object)policy_id);
                            ddnsRow.set("RESTRICTED_INTERFACE", (Object)ddns);
                            dataObject.addRow(ddnsRow);
                        }
                        if (!dataObject.isEmpty()) {
                            MDMUtil.getPersistence().add(dataObject);
                        }
                    }
                    return this.getDirectoryTemplate(policy_id, customer_id);
                }
                default: {
                    throw new APIHTTPException("COM0014", new Object[0]);
                }
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:modifyTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:modifyTemplate] ~~~~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Long getDomainIDFromName(final Long customer_id, final String domainName) throws APIHTTPException {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
            query.addJoin(new Join("DMDomain", "DMManagedDomain", new String[] { "DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
            query.addSelectColumn(Column.getColumn("DMDomain", "DOMAIN_ID"));
            Criteria criteria = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customer_id, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("DMManagedDomain", "AD_DOMAIN_NAME"), (Object)domainName, 2));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[0]);
            }
            final Row row = dataObject.getRow("DMDomain");
            return (Long)row.get("DOMAIN_ID");
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDomainIDFromName] ~~~~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~~~~ Exception at program directoryFacade [Function:getDomainIDFromName] ~~~~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void deleteTemplate(final JSONObject message) throws APIHTTPException {
        try {
            final Long customer_id = APIUtil.getCustomerID(message);
            final Long policy_id = APIUtil.getResourceID(message, "bindpolicytemplate_id");
            Criteria criteria = new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"), (Object)customer_id, 0);
            if (policy_id != -1L) {
                criteria = criteria.and(new Criteria(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"), (Object)policy_id, 0));
            }
            final DataObject dataObject = MDMUtil.getPersistence().get("DirectoryBindPolicyTemplate", criteria);
            if (!dataObject.isEmpty()) {
                dataObject.deleteRows("DirectoryBindPolicyTemplate", criteria);
                MDMUtil.getPersistenceLite().update(dataObject);
            }
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ APIHTTPException occured at directoryFacade [Function:deleteTemplate] ~~~~~~~~~~~~~~~~~~~", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "~~~~~~~~~~~~~~~~~ Exception occured at directoryFacade [Function:deleteTemplate] ~~~~~~~~~~~~~~~~~~~", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void addSelectColumnsForTemplate(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "BIND_POLICY_ID"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "NAME"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "DOMAIN_ID"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "TYPE"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "ADDED_AT"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "MODIFIED_AT"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "DESCRIPTION"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "ADDED_BY"));
        query.addSelectColumn(Column.getColumn("DirectoryBindPolicyTemplate", "MODIFIED_BY"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "BIND_POLICY_ID"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "CREATE_MA_AT_LOGIN"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "WARN_BEFORE_MA"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "FORCE_HOME_LOCAL"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "WINDOWS_UNC_PATH"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "DEFAULT_SHELL"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "PREFERRED_DC"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "PREFERRED_DC_FLAG"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "ALLOW_MULTI_DOMAIN_AUTH"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "NAMESPACE"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "MOUNT_STYLE"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "PACKET_SIGN"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "PACKET_ENCRYPTION"));
        query.addSelectColumn(Column.getColumn("ADBindPolicyTemplate", "TRUST_PASS_INTERVAL"));
        query.addSelectColumn(Column.getColumn("ADBindRestrictedDDNS", "RESTRICTED_DDNS_ID"));
        query.addSelectColumn(Column.getColumn("ADBindRestrictedDDNS", "BIND_POLICY_ID"));
        query.addSelectColumn(Column.getColumn("ADBindRestrictedDDNS", "RESTRICTED_INTERFACE"));
        query.addSelectColumn(Column.getColumn("DMManagedDomain", "AD_DOMAIN_NAME"));
        query.addSelectColumn(Column.getColumn("DMManagedDomain", "DOMAIN_ID"));
    }
}
