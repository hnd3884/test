package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBase64Binary;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLongHexNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDecimalNumber;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STAlgType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STAlgClass;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STCryptProv;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocProtect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDocProtectImpl extends XmlComplexContentImpl implements CTDocProtect
{
    private static final long serialVersionUID = 1L;
    private static final QName EDIT$0;
    private static final QName FORMATTING$2;
    private static final QName ENFORCEMENT$4;
    private static final QName CRYPTPROVIDERTYPE$6;
    private static final QName CRYPTALGORITHMCLASS$8;
    private static final QName CRYPTALGORITHMTYPE$10;
    private static final QName CRYPTALGORITHMSID$12;
    private static final QName CRYPTSPINCOUNT$14;
    private static final QName CRYPTPROVIDER$16;
    private static final QName ALGIDEXT$18;
    private static final QName ALGIDEXTSOURCE$20;
    private static final QName CRYPTPROVIDERTYPEEXT$22;
    private static final QName CRYPTPROVIDERTYPEEXTSOURCE$24;
    private static final QName HASH$26;
    private static final QName SALT$28;
    
    public CTDocProtectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STDocProtect.Enum getEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.EDIT$0);
            if (simpleValue == null) {
                return null;
            }
            return (STDocProtect.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDocProtect xgetEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDocProtect)this.get_store().find_attribute_user(CTDocProtectImpl.EDIT$0);
        }
    }
    
    public boolean isSetEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.EDIT$0) != null;
        }
    }
    
    public void setEdit(final STDocProtect.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.EDIT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.EDIT$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEdit(final STDocProtect stDocProtect) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDocProtect stDocProtect2 = (STDocProtect)this.get_store().find_attribute_user(CTDocProtectImpl.EDIT$0);
            if (stDocProtect2 == null) {
                stDocProtect2 = (STDocProtect)this.get_store().add_attribute_user(CTDocProtectImpl.EDIT$0);
            }
            stDocProtect2.set((XmlObject)stDocProtect);
        }
    }
    
    public void unsetEdit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.EDIT$0);
        }
    }
    
    public STOnOff.Enum getFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.FORMATTING$2);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTDocProtectImpl.FORMATTING$2);
        }
    }
    
    public boolean isSetFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.FORMATTING$2) != null;
        }
    }
    
    public void setFormatting(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.FORMATTING$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.FORMATTING$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFormatting(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTDocProtectImpl.FORMATTING$2);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTDocProtectImpl.FORMATTING$2);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetFormatting() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.FORMATTING$2);
        }
    }
    
    public STOnOff.Enum getEnforcement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
            if (simpleValue == null) {
                return null;
            }
            return (STOnOff.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STOnOff xgetEnforcement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STOnOff)this.get_store().find_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
        }
    }
    
    public boolean isSetEnforcement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.ENFORCEMENT$4) != null;
        }
    }
    
    public void setEnforcement(final STOnOff.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEnforcement(final STOnOff stOnOff) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STOnOff stOnOff2 = (STOnOff)this.get_store().find_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
            if (stOnOff2 == null) {
                stOnOff2 = (STOnOff)this.get_store().add_attribute_user(CTDocProtectImpl.ENFORCEMENT$4);
            }
            stOnOff2.set((XmlObject)stOnOff);
        }
    }
    
    public void unsetEnforcement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.ENFORCEMENT$4);
        }
    }
    
    public STCryptProv.Enum getCryptProviderType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STCryptProv.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCryptProv xgetCryptProviderType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCryptProv)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
        }
    }
    
    public boolean isSetCryptProviderType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6) != null;
        }
    }
    
    public void setCryptProviderType(final STCryptProv.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCryptProviderType(final STCryptProv stCryptProv) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCryptProv stCryptProv2 = (STCryptProv)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
            if (stCryptProv2 == null) {
                stCryptProv2 = (STCryptProv)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
            }
            stCryptProv2.set((XmlObject)stCryptProv);
        }
    }
    
    public void unsetCryptProviderType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTPROVIDERTYPE$6);
        }
    }
    
    public STAlgClass.Enum getCryptAlgorithmClass() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
            if (simpleValue == null) {
                return null;
            }
            return (STAlgClass.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STAlgClass xgetCryptAlgorithmClass() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAlgClass)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
        }
    }
    
    public boolean isSetCryptAlgorithmClass() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8) != null;
        }
    }
    
    public void setCryptAlgorithmClass(final STAlgClass.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCryptAlgorithmClass(final STAlgClass stAlgClass) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAlgClass stAlgClass2 = (STAlgClass)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
            if (stAlgClass2 == null) {
                stAlgClass2 = (STAlgClass)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
            }
            stAlgClass2.set((XmlObject)stAlgClass);
        }
    }
    
    public void unsetCryptAlgorithmClass() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTALGORITHMCLASS$8);
        }
    }
    
    public STAlgType.Enum getCryptAlgorithmType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
            if (simpleValue == null) {
                return null;
            }
            return (STAlgType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STAlgType xgetCryptAlgorithmType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAlgType)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
        }
    }
    
    public boolean isSetCryptAlgorithmType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10) != null;
        }
    }
    
    public void setCryptAlgorithmType(final STAlgType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCryptAlgorithmType(final STAlgType stAlgType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAlgType stAlgType2 = (STAlgType)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
            if (stAlgType2 == null) {
                stAlgType2 = (STAlgType)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
            }
            stAlgType2.set((XmlObject)stAlgType);
        }
    }
    
    public void unsetCryptAlgorithmType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTALGORITHMTYPE$10);
        }
    }
    
    public BigInteger getCryptAlgorithmSid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetCryptAlgorithmSid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
        }
    }
    
    public boolean isSetCryptAlgorithmSid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12) != null;
        }
    }
    
    public void setCryptAlgorithmSid(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetCryptAlgorithmSid(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTALGORITHMSID$12);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetCryptAlgorithmSid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTALGORITHMSID$12);
        }
    }
    
    public BigInteger getCryptSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public STDecimalNumber xgetCryptSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDecimalNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
        }
    }
    
    public boolean isSetCryptSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14) != null;
        }
    }
    
    public void setCryptSpinCount(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetCryptSpinCount(final STDecimalNumber stDecimalNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDecimalNumber stDecimalNumber2 = (STDecimalNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
            if (stDecimalNumber2 == null) {
                stDecimalNumber2 = (STDecimalNumber)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTSPINCOUNT$14);
            }
            stDecimalNumber2.set((XmlObject)stDecimalNumber);
        }
    }
    
    public void unsetCryptSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTSPINCOUNT$14);
        }
    }
    
    public String getCryptProvider() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetCryptProvider() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
        }
    }
    
    public boolean isSetCryptProvider() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16) != null;
        }
    }
    
    public void setCryptProvider(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCryptProvider(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDER$16);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetCryptProvider() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTPROVIDER$16);
        }
    }
    
    public byte[] getAlgIdExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetAlgIdExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
        }
    }
    
    public boolean isSetAlgIdExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXT$18) != null;
        }
    }
    
    public void setAlgIdExt(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetAlgIdExt(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTDocProtectImpl.ALGIDEXT$18);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetAlgIdExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.ALGIDEXT$18);
        }
    }
    
    public String getAlgIdExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetAlgIdExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
        }
    }
    
    public boolean isSetAlgIdExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20) != null;
        }
    }
    
    public void setAlgIdExtSource(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlgIdExtSource(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTDocProtectImpl.ALGIDEXTSOURCE$20);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetAlgIdExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.ALGIDEXTSOURCE$20);
        }
    }
    
    public byte[] getCryptProviderTypeExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public STLongHexNumber xgetCryptProviderTypeExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STLongHexNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
        }
    }
    
    public boolean isSetCryptProviderTypeExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22) != null;
        }
    }
    
    public void setCryptProviderTypeExt(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetCryptProviderTypeExt(final STLongHexNumber stLongHexNumber) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STLongHexNumber stLongHexNumber2 = (STLongHexNumber)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
            if (stLongHexNumber2 == null) {
                stLongHexNumber2 = (STLongHexNumber)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
            }
            stLongHexNumber2.set((XmlObject)stLongHexNumber);
        }
    }
    
    public void unsetCryptProviderTypeExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTPROVIDERTYPEEXT$22);
        }
    }
    
    public String getCryptProviderTypeExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STString xgetCryptProviderTypeExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STString)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
        }
    }
    
    public boolean isSetCryptProviderTypeExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24) != null;
        }
    }
    
    public void setCryptProviderTypeExtSource(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCryptProviderTypeExtSource(final STString stString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STString stString2 = (STString)this.get_store().find_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
            if (stString2 == null) {
                stString2 = (STString)this.get_store().add_attribute_user(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
            }
            stString2.set((XmlObject)stString);
        }
    }
    
    public void unsetCryptProviderTypeExtSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.CRYPTPROVIDERTYPEEXTSOURCE$24);
        }
    }
    
    public byte[] getHash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.HASH$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetHash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTDocProtectImpl.HASH$26);
        }
    }
    
    public boolean isSetHash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.HASH$26) != null;
        }
    }
    
    public void setHash(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.HASH$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.HASH$26);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetHash(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTDocProtectImpl.HASH$26);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTDocProtectImpl.HASH$26);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetHash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.HASH$26);
        }
    }
    
    public byte[] getSalt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.SALT$28);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetSalt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTDocProtectImpl.SALT$28);
        }
    }
    
    public boolean isSetSalt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDocProtectImpl.SALT$28) != null;
        }
    }
    
    public void setSalt(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDocProtectImpl.SALT$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDocProtectImpl.SALT$28);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetSalt(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTDocProtectImpl.SALT$28);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTDocProtectImpl.SALT$28);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public void unsetSalt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDocProtectImpl.SALT$28);
        }
    }
    
    static {
        EDIT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "edit");
        FORMATTING$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "formatting");
        ENFORCEMENT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "enforcement");
        CRYPTPROVIDERTYPE$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptProviderType");
        CRYPTALGORITHMCLASS$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptAlgorithmClass");
        CRYPTALGORITHMTYPE$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptAlgorithmType");
        CRYPTALGORITHMSID$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptAlgorithmSid");
        CRYPTSPINCOUNT$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptSpinCount");
        CRYPTPROVIDER$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptProvider");
        ALGIDEXT$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "algIdExt");
        ALGIDEXTSOURCE$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "algIdExtSource");
        CRYPTPROVIDERTYPEEXT$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptProviderTypeExt");
        CRYPTPROVIDERTYPEEXTSOURCE$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cryptProviderTypeExtSource");
        HASH$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hash");
        SALT$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "salt");
    }
}
