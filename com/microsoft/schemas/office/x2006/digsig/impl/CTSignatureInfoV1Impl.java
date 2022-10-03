package com.microsoft.schemas.office.x2006.digsig.impl;

import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlString;
import com.microsoft.schemas.office.x2006.digsig.STSignatureType;
import org.apache.xmlbeans.XmlInt;
import com.microsoft.schemas.office.x2006.digsig.STSignatureProviderUrl;
import com.microsoft.schemas.office.x2006.digsig.STPositiveInteger;
import com.microsoft.schemas.office.x2006.digsig.STVersion;
import com.microsoft.schemas.office.x2006.digsig.STSignatureComments;
import org.apache.xmlbeans.XmlBase64Binary;
import com.microsoft.schemas.office.x2006.digsig.STSignatureText;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.digsig.STUniqueIdentifierWithBraces;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSignatureInfoV1Impl extends XmlComplexContentImpl implements CTSignatureInfoV1
{
    private static final long serialVersionUID = 1L;
    private static final QName SETUPID$0;
    private static final QName SIGNATURETEXT$2;
    private static final QName SIGNATUREIMAGE$4;
    private static final QName SIGNATURECOMMENTS$6;
    private static final QName WINDOWSVERSION$8;
    private static final QName OFFICEVERSION$10;
    private static final QName APPLICATIONVERSION$12;
    private static final QName MONITORS$14;
    private static final QName HORIZONTALRESOLUTION$16;
    private static final QName VERTICALRESOLUTION$18;
    private static final QName COLORDEPTH$20;
    private static final QName SIGNATUREPROVIDERID$22;
    private static final QName SIGNATUREPROVIDERURL$24;
    private static final QName SIGNATUREPROVIDERDETAILS$26;
    private static final QName SIGNATURETYPE$28;
    private static final QName DELEGATESUGGESTEDSIGNER$30;
    private static final QName DELEGATESUGGESTEDSIGNER2$32;
    private static final QName DELEGATESUGGESTEDSIGNEREMAIL$34;
    private static final QName MANIFESTHASHALGORITHM$36;
    
    public CTSignatureInfoV1Impl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getSetupID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SETUPID$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STUniqueIdentifierWithBraces xgetSetupID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUniqueIdentifierWithBraces)this.get_store().find_element_user(CTSignatureInfoV1Impl.SETUPID$0, 0);
        }
    }
    
    public void setSetupID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SETUPID$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SETUPID$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSetupID(final STUniqueIdentifierWithBraces stUniqueIdentifierWithBraces) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces stUniqueIdentifierWithBraces2 = (STUniqueIdentifierWithBraces)this.get_store().find_element_user(CTSignatureInfoV1Impl.SETUPID$0, 0);
            if (stUniqueIdentifierWithBraces2 == null) {
                stUniqueIdentifierWithBraces2 = (STUniqueIdentifierWithBraces)this.get_store().add_element_user(CTSignatureInfoV1Impl.SETUPID$0);
            }
            stUniqueIdentifierWithBraces2.set((XmlObject)stUniqueIdentifierWithBraces);
        }
    }
    
    public String getSignatureText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STSignatureText xgetSignatureText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignatureText)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2, 0);
        }
    }
    
    public void setSignatureText(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSignatureText(final STSignatureText stSignatureText) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignatureText stSignatureText2 = (STSignatureText)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2, 0);
            if (stSignatureText2 == null) {
                stSignatureText2 = (STSignatureText)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURETEXT$2);
            }
            stSignatureText2.set((XmlObject)stSignatureText);
        }
    }
    
    public byte[] getSignatureImage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetSignatureImage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4, 0);
        }
    }
    
    public void setSignatureImage(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetSignatureImage(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4, 0);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREIMAGE$4);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public String getSignatureComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STSignatureComments xgetSignatureComments() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignatureComments)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6, 0);
        }
    }
    
    public void setSignatureComments(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSignatureComments(final STSignatureComments stSignatureComments) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignatureComments stSignatureComments2 = (STSignatureComments)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6, 0);
            if (stSignatureComments2 == null) {
                stSignatureComments2 = (STSignatureComments)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURECOMMENTS$6);
            }
            stSignatureComments2.set((XmlObject)stSignatureComments);
        }
    }
    
    public String getWindowsVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STVersion xgetWindowsVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8, 0);
        }
    }
    
    public void setWindowsVersion(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetWindowsVersion(final STVersion stVersion) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVersion stVersion2 = (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8, 0);
            if (stVersion2 == null) {
                stVersion2 = (STVersion)this.get_store().add_element_user(CTSignatureInfoV1Impl.WINDOWSVERSION$8);
            }
            stVersion2.set((XmlObject)stVersion);
        }
    }
    
    public String getOfficeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STVersion xgetOfficeVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10, 0);
        }
    }
    
    public void setOfficeVersion(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOfficeVersion(final STVersion stVersion) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVersion stVersion2 = (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10, 0);
            if (stVersion2 == null) {
                stVersion2 = (STVersion)this.get_store().add_element_user(CTSignatureInfoV1Impl.OFFICEVERSION$10);
            }
            stVersion2.set((XmlObject)stVersion);
        }
    }
    
    public String getApplicationVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STVersion xgetApplicationVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12, 0);
        }
    }
    
    public void setApplicationVersion(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetApplicationVersion(final STVersion stVersion) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVersion stVersion2 = (STVersion)this.get_store().find_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12, 0);
            if (stVersion2 == null) {
                stVersion2 = (STVersion)this.get_store().add_element_user(CTSignatureInfoV1Impl.APPLICATIONVERSION$12);
            }
            stVersion2.set((XmlObject)stVersion);
        }
    }
    
    public int getMonitors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.MONITORS$14, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveInteger xgetMonitors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.MONITORS$14, 0);
        }
    }
    
    public void setMonitors(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.MONITORS$14, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.MONITORS$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMonitors(final STPositiveInteger stPositiveInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveInteger stPositiveInteger2 = (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.MONITORS$14, 0);
            if (stPositiveInteger2 == null) {
                stPositiveInteger2 = (STPositiveInteger)this.get_store().add_element_user(CTSignatureInfoV1Impl.MONITORS$14);
            }
            stPositiveInteger2.set((XmlObject)stPositiveInteger);
        }
    }
    
    public int getHorizontalResolution() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveInteger xgetHorizontalResolution() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16, 0);
        }
    }
    
    public void setHorizontalResolution(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetHorizontalResolution(final STPositiveInteger stPositiveInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveInteger stPositiveInteger2 = (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16, 0);
            if (stPositiveInteger2 == null) {
                stPositiveInteger2 = (STPositiveInteger)this.get_store().add_element_user(CTSignatureInfoV1Impl.HORIZONTALRESOLUTION$16);
            }
            stPositiveInteger2.set((XmlObject)stPositiveInteger);
        }
    }
    
    public int getVerticalResolution() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveInteger xgetVerticalResolution() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18, 0);
        }
    }
    
    public void setVerticalResolution(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetVerticalResolution(final STPositiveInteger stPositiveInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveInteger stPositiveInteger2 = (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18, 0);
            if (stPositiveInteger2 == null) {
                stPositiveInteger2 = (STPositiveInteger)this.get_store().add_element_user(CTSignatureInfoV1Impl.VERTICALRESOLUTION$18);
            }
            stPositiveInteger2.set((XmlObject)stPositiveInteger);
        }
    }
    
    public int getColorDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveInteger xgetColorDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20, 0);
        }
    }
    
    public void setColorDepth(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetColorDepth(final STPositiveInteger stPositiveInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveInteger stPositiveInteger2 = (STPositiveInteger)this.get_store().find_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20, 0);
            if (stPositiveInteger2 == null) {
                stPositiveInteger2 = (STPositiveInteger)this.get_store().add_element_user(CTSignatureInfoV1Impl.COLORDEPTH$20);
            }
            stPositiveInteger2.set((XmlObject)stPositiveInteger);
        }
    }
    
    public String getSignatureProviderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STUniqueIdentifierWithBraces xgetSignatureProviderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STUniqueIdentifierWithBraces)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22, 0);
        }
    }
    
    public void setSignatureProviderId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSignatureProviderId(final STUniqueIdentifierWithBraces stUniqueIdentifierWithBraces) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STUniqueIdentifierWithBraces stUniqueIdentifierWithBraces2 = (STUniqueIdentifierWithBraces)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22, 0);
            if (stUniqueIdentifierWithBraces2 == null) {
                stUniqueIdentifierWithBraces2 = (STUniqueIdentifierWithBraces)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERID$22);
            }
            stUniqueIdentifierWithBraces2.set((XmlObject)stUniqueIdentifierWithBraces);
        }
    }
    
    public String getSignatureProviderUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STSignatureProviderUrl xgetSignatureProviderUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignatureProviderUrl)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24, 0);
        }
    }
    
    public void setSignatureProviderUrl(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSignatureProviderUrl(final STSignatureProviderUrl stSignatureProviderUrl) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignatureProviderUrl stSignatureProviderUrl2 = (STSignatureProviderUrl)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24, 0);
            if (stSignatureProviderUrl2 == null) {
                stSignatureProviderUrl2 = (STSignatureProviderUrl)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERURL$24);
            }
            stSignatureProviderUrl2.set((XmlObject)stSignatureProviderUrl);
        }
    }
    
    public int getSignatureProviderDetails() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetSignatureProviderDetails() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26, 0);
        }
    }
    
    public void setSignatureProviderDetails(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSignatureProviderDetails(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATUREPROVIDERDETAILS$26);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public int getSignatureType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STSignatureType xgetSignatureType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSignatureType)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28, 0);
        }
    }
    
    public void setSignatureType(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSignatureType(final STSignatureType stSignatureType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSignatureType stSignatureType2 = (STSignatureType)this.get_store().find_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28, 0);
            if (stSignatureType2 == null) {
                stSignatureType2 = (STSignatureType)this.get_store().add_element_user(CTSignatureInfoV1Impl.SIGNATURETYPE$28);
            }
            stSignatureType2.set((XmlObject)stSignatureType);
        }
    }
    
    public String getDelegateSuggestedSigner() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDelegateSuggestedSigner() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30, 0);
        }
    }
    
    public boolean isSetDelegateSuggestedSigner() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30) != 0;
        }
    }
    
    public void setDelegateSuggestedSigner(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDelegateSuggestedSigner(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDelegateSuggestedSigner() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER$30, 0);
        }
    }
    
    public String getDelegateSuggestedSigner2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDelegateSuggestedSigner2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32, 0);
        }
    }
    
    public boolean isSetDelegateSuggestedSigner2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32) != 0;
        }
    }
    
    public void setDelegateSuggestedSigner2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDelegateSuggestedSigner2(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDelegateSuggestedSigner2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNER2$32, 0);
        }
    }
    
    public String getDelegateSuggestedSignerEmail() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetDelegateSuggestedSignerEmail() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34, 0);
        }
    }
    
    public boolean isSetDelegateSuggestedSignerEmail() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34) != 0;
        }
    }
    
    public void setDelegateSuggestedSignerEmail(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDelegateSuggestedSignerEmail(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetDelegateSuggestedSignerEmail() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSignatureInfoV1Impl.DELEGATESUGGESTEDSIGNEREMAIL$34, 0);
        }
    }
    
    public String getManifestHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlAnyURI xgetManifestHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlAnyURI)this.get_store().find_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36, 0);
        }
    }
    
    public boolean isSetManifestHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36) != 0;
        }
    }
    
    public void setManifestHashAlgorithm(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetManifestHashAlgorithm(final XmlAnyURI xmlAnyURI) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlAnyURI xmlAnyURI2 = (XmlAnyURI)this.get_store().find_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36, 0);
            if (xmlAnyURI2 == null) {
                xmlAnyURI2 = (XmlAnyURI)this.get_store().add_element_user(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36);
            }
            xmlAnyURI2.set((XmlObject)xmlAnyURI);
        }
    }
    
    public void unsetManifestHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSignatureInfoV1Impl.MANIFESTHASHALGORITHM$36, 0);
        }
    }
    
    static {
        SETUPID$0 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SetupID");
        SIGNATURETEXT$2 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureText");
        SIGNATUREIMAGE$4 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureImage");
        SIGNATURECOMMENTS$6 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureComments");
        WINDOWSVERSION$8 = new QName("http://schemas.microsoft.com/office/2006/digsig", "WindowsVersion");
        OFFICEVERSION$10 = new QName("http://schemas.microsoft.com/office/2006/digsig", "OfficeVersion");
        APPLICATIONVERSION$12 = new QName("http://schemas.microsoft.com/office/2006/digsig", "ApplicationVersion");
        MONITORS$14 = new QName("http://schemas.microsoft.com/office/2006/digsig", "Monitors");
        HORIZONTALRESOLUTION$16 = new QName("http://schemas.microsoft.com/office/2006/digsig", "HorizontalResolution");
        VERTICALRESOLUTION$18 = new QName("http://schemas.microsoft.com/office/2006/digsig", "VerticalResolution");
        COLORDEPTH$20 = new QName("http://schemas.microsoft.com/office/2006/digsig", "ColorDepth");
        SIGNATUREPROVIDERID$22 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderId");
        SIGNATUREPROVIDERURL$24 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderUrl");
        SIGNATUREPROVIDERDETAILS$26 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureProviderDetails");
        SIGNATURETYPE$28 = new QName("http://schemas.microsoft.com/office/2006/digsig", "SignatureType");
        DELEGATESUGGESTEDSIGNER$30 = new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSigner");
        DELEGATESUGGESTEDSIGNER2$32 = new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSigner2");
        DELEGATESUGGESTEDSIGNEREMAIL$34 = new QName("http://schemas.microsoft.com/office/2006/digsig", "DelegateSuggestedSignerEmail");
        MANIFESTHASHALGORITHM$36 = new QName("http://schemas.microsoft.com/office/2006/digsig", "ManifestHashAlgorithm");
    }
}
