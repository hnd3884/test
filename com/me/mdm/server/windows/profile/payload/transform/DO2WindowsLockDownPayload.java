package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import javax.xml.bind.JAXBException;
import com.me.mdm.core.xmlparser.XmlBeanUtil;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.windows.profile.payload.content.lockdown.AppProperties;
import com.me.mdm.server.apps.windows.BusinessStoreAPIAccess;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.content.lockdown.HandheldLockdown;
import java.util.ArrayList;
import com.me.mdm.server.windows.profile.payload.WindowsLockDownPayload;
import com.me.mdm.server.windows.profile.payload.WindowsLockDownModePayload;
import com.me.mdm.core.lockdown.windows.WindowsLockdownHandler;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsLockDownPayload extends DO2WindowsPayload
{
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        WindowsLockDownPayload windowsLockDownPayload = null;
        WindowsLockDownPayload windowsMobileLockDownPayload = null;
        WindowsLockDownPayload windowsMobile81LockDownPayload = null;
        WindowsLockDownModePayload windowsDesktopLockDownPayload = null;
        WindowsLockdownHandler lockdownHandler = new WindowsLockdownHandler();
        try {
            windowsDesktopLockDownPayload = new WindowsLockDownModePayload();
            lockdownHandler = new WindowsLockdownHandler();
            windowsLockDownPayload = new WindowsLockDownPayload();
            windowsLockDownPayload.getReplacePayloadCommand().addRequestItem(windowsLockDownPayload.createTargetItemTagElement("%lockdown_replace_payload_xml%"));
            windowsMobileLockDownPayload = new WindowsLockDownPayload();
            windowsMobile81LockDownPayload = new WindowsLockDownPayload();
            final Row row = dataObject.getFirstRow("WindowsKioskPolicy");
            final String domain = (String)row.get("DOMAIN");
            String user = (String)row.get("USER");
            if (user.equals("%upn%")) {
                user = "%kioskDynamic%%upn%";
            }
            final List appList = new ArrayList();
            final HandheldLockdown.EnterpriseLockDownProperties properties = new HandheldLockdown.EnterpriseLockDownProperties();
            Iterator iterator = dataObject.getRows("WindowsAppDetails");
            while (iterator.hasNext()) {
                final Row appRow = iterator.next();
                final String aumid = (String)appRow.get("AUMID");
                if (aumid != null && !appList.contains(aumid)) {
                    appList.add(aumid);
                    String productID = (String)appRow.get("PHONE_PRODUCT_ID");
                    if (MDMStringUtils.isEmpty(productID)) {
                        final String storeID = (String)appRow.get("PRODUCT_ID");
                        if (!MDMStringUtils.isEmpty(storeID)) {
                            final JSONObject params = new JSONObject();
                            params.put("Type", (Object)"PackageIDQuery");
                            params.put("StoreID", (Object)storeID);
                            final JSONObject jsonObject = new BusinessStoreAPIAccess().getDataFromBusinessStore(params);
                            productID = jsonObject.optString("windowsPhoneLegacyId", "");
                            appRow.set("PHONE_PRODUCT_ID", (Object)productID);
                            this.updatePhoneID(appRow);
                        }
                    }
                    final AppProperties app = new AppProperties();
                    app.aumid = aumid;
                    app.productID = "{" + productID + "}";
                    properties.getAllowedApps().add(app);
                }
            }
            iterator = dataObject.getRows("WindowsSystemApps");
            while (iterator.hasNext()) {
                final Row appRow = iterator.next();
                final String aumid = (String)appRow.get("AUMID");
                if (aumid != null && !appList.contains(aumid)) {
                    appList.add(aumid);
                    final String productID = (String)appRow.get("PHONE_PRODUCT_ID");
                    final AppProperties app = new AppProperties();
                    app.aumid = aumid;
                    app.productID = "{" + productID + "}";
                    properties.getAllowedApps().add(app);
                }
            }
            final String mobileKioskXML = this.getKioskXMLAsString(properties, "10");
            final String mobile81KioskXML = this.getKioskXMLAsString(properties, "81");
            windowsDesktopLockDownPayload.setConfigurationXML(lockdownHandler.getLockDownXML(lockdownHandler.convertLockDownPropertiesToPolicy(properties), true));
            windowsMobileLockDownPayload.setLockDownXml(mobileKioskXML);
            windowsMobile81LockDownPayload.setLockDownXml(mobile81KioskXML);
            this.packOsSpecificPayloadToXML(dataObject, windowsDesktopLockDownPayload, "install", "Windows10DesktopLockdown");
            this.packOsSpecificPayloadToXML(dataObject, windowsMobileLockDownPayload, "install", "Windows10MobileLockdown");
            this.packOsSpecificPayloadToXML(dataObject, windowsMobile81LockDownPayload, "install", "WindowsPhone81Lockdown");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while creating Windows Lockdown payload ", e);
        }
        return windowsLockDownPayload;
    }
    
    private String getKioskXMLAsString(final HandheldLockdown.EnterpriseLockDownProperties properties, final String os) throws JAXBException, JSONException, SyMException, ClassNotFoundException {
        final HandheldLockdown handheldLockdown = HandheldLockdown.getLockDownProfile(properties, os);
        final JSONObject beanUtilJSON = new JSONObject();
        beanUtilJSON.put("BEAN_OBJECT", (Object)handheldLockdown);
        beanUtilJSON.put("jaxb.fragment", (Object)Boolean.TRUE);
        beanUtilJSON.put("jaxb.encoding", (Object)"UTF-8");
        final JSONObject customProps = new JSONObject();
        customProps.put("com.sun.xml.internal.bind.xmlHeaders", (Object)"");
        beanUtilJSON.put("customMarshallerProps", (Object)customProps);
        final XmlBeanUtil<HandheldLockdown> xmlBeanUtil = new XmlBeanUtil<HandheldLockdown>(beanUtilJSON);
        final String kioskXML = xmlBeanUtil.beanToXmlString();
        return kioskXML;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsLockDownPayload windowsLockDownPayload = new WindowsLockDownPayload();
        windowsLockDownPayload.getDeletePayloadCommand().addRequestItem(windowsLockDownPayload.createTargetItemTagElement("%lockdown_payload_xml%"));
        final WindowsLockDownModePayload windowsDesktopLockDownPayload = new WindowsLockDownModePayload();
        final WindowsLockDownPayload mobileLockDownPayload = new WindowsLockDownPayload();
        windowsDesktopLockDownPayload.setConfigurationDelete();
        mobileLockDownPayload.setRemovePayload();
        this.packOsSpecificPayloadToXML(dataObject, windowsDesktopLockDownPayload, "remove", "Windows10DesktopLockdown");
        this.packOsSpecificPayloadToXML(dataObject, mobileLockDownPayload, "remove", "Windows10MobileLockdown");
        this.packOsSpecificPayloadToXML(dataObject, mobileLockDownPayload, "remove", "WindowsPhone81Lockdown");
        return windowsLockDownPayload;
    }
    
    private void updatePhoneID(final Row row) {
        final Long appID = (Long)row.get("APP_ID");
        final String phoneID = (String)row.get("PHONE_PRODUCT_ID");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("WindowsAppDetails");
        updateQuery.setCriteria(new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appID, 0));
        updateQuery.setUpdateColumn("PHONE_PRODUCT_ID", (Object)phoneID);
        try {
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Couldnt update phone ID", (Throwable)e);
        }
    }
}
