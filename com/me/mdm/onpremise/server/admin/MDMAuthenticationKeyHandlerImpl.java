package com.me.mdm.onpremise.server.admin;

import java.util.Hashtable;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.apache.commons.codec.binary.Base64;
import com.me.devicemanagement.framework.server.api.AuthenticationKeyHandlerAPI;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyHandler;

public class MDMAuthenticationKeyHandlerImpl extends AuthenticationKeyHandler implements AuthenticationKeyHandlerAPI
{
    public static AuthenticationKeyHandler getInstance() {
        return new MDMAuthenticationKeyHandlerImpl();
    }
    
    public String getDecryptedAuthToken(final String key, final Integer servericeType) throws SyMException {
        switch (servericeType) {
            case 10:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
            case 32:
            case 33:
            case 40:
            case 50:
            case 201: {
                return new String(Base64.decodeBase64(key));
            }
            default: {
                throw new SyMException(14001, "Auth Token cannot be Redistributed", (Throwable)null);
            }
        }
    }
    
    public String getEncryptedAuthToken(final String key, final Integer serviceType) throws SyMException {
        switch (serviceType) {
            case 10:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 30:
            case 31:
            case 32:
            case 33:
            case 40:
            case 50:
            case 201: {
                return Base64.encodeBase64String(key.getBytes());
            }
            default: {
                throw new SyMException(14001, "Auth Token cannot be Redistributed", (Throwable)null);
            }
        }
    }
    
    public String getOrAddAuthToken(final Properties prop) throws DataAccessException, SyMException {
        final Long loginId = Long.parseLong(prop.getProperty("loginID"));
        final int templateType = Integer.parseInt(prop.getProperty("TEMPLATE_TYPE"));
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMUserPurposeToken"));
        final Criteria criteria = new Criteria(Column.getColumn("MDMUserPurposeToken", "LOGIN_ID"), (Object)loginId, 0).and(new Criteria(Column.getColumn("MDMUserPurposeToken", "PURPOSE_KEY"), (Object)templateType, 0));
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(Column.getColumn("MDMUserPurposeToken", "MDM_USER_PURPOSE_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MDMUserPurposeToken", "PURPOSE_TOKEN"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("MDMUserPurposeToken");
            final String encryptedPurposeToken = (String)row.get("PURPOSE_TOKEN");
            return this.getDecryptedAuthToken(encryptedPurposeToken, templateType);
        }
        final String authToken = this.generateTechAPIKey();
        ((Hashtable<String, String>)prop).put("apiKey", authToken);
        this.addOrUpdateAPIKey(prop);
        return authToken;
    }
    
    public DataObject addOrUpdateAPIKey(final Properties props) throws SyMException, DataAccessException {
        final String login_id = props.getProperty("loginID");
        final String apiKey = props.getProperty("apiKey");
        final Integer purposeKey = Integer.parseInt(props.getProperty("TEMPLATE_TYPE"));
        final Criteria criteria = new Criteria(Column.getColumn("MDMUserPurposeToken", "PURPOSE_KEY"), (Object)purposeKey, 0).and(new Criteria(Column.getColumn("MDMUserPurposeToken", "LOGIN_ID"), (Object)login_id, 0));
        final DataObject tokenDataObject = MDMUtil.getPersistenceLite().get("MDMUserPurposeToken", criteria);
        if (tokenDataObject.isEmpty()) {
            final Row mdmUserTokenRow = new Row("MDMUserPurposeToken");
            mdmUserTokenRow.set("LOGIN_ID", (Object)login_id);
            mdmUserTokenRow.set("PURPOSE_KEY", (Object)purposeKey);
            mdmUserTokenRow.set("PURPOSE_TOKEN", (Object)this.getEncryptedAuthToken(apiKey, purposeKey));
            tokenDataObject.addRow(mdmUserTokenRow);
            final DataObject enrollmentTemplateDO = MDMUtil.getPersistenceLite().get("EnrollmentTemplate", new Criteria(Column.getColumn("EnrollmentTemplate", "ADDED_USER"), (Object)DMUserHandler.getUserIdForLoginId(Long.valueOf(login_id)), 0).and(new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)purposeKey, 0)));
            final Iterator enrollmentTemplateIterator = enrollmentTemplateDO.getRows("EnrollmentTemplate");
            while (enrollmentTemplateIterator.hasNext()) {
                final Row enrollmentTemplateRow = enrollmentTemplateIterator.next();
                final Row mappingRow = new Row("EnrollmentTemplateToMDMUserPurposeToken");
                mappingRow.set("MDM_USER_PURPOSE_TOKEN_ID", mdmUserTokenRow.get("MDM_USER_PURPOSE_TOKEN_ID"));
                mappingRow.set("TEMPLATE_ID", enrollmentTemplateRow.get("TEMPLATE_ID"));
                tokenDataObject.addRow(mappingRow);
            }
            MDMUtil.getPersistenceLite().add(tokenDataObject);
        }
        else {
            final Row mdmUserTokenRow = tokenDataObject.getRow("MDMUserPurposeToken");
            mdmUserTokenRow.set("PURPOSE_TOKEN", (Object)this.getEncryptedAuthToken(apiKey, purposeKey));
            tokenDataObject.updateRow(mdmUserTokenRow);
            MDMUtil.getPersistenceLite().update(tokenDataObject);
        }
        return tokenDataObject;
    }
}
