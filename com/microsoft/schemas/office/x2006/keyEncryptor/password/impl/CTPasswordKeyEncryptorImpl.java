package com.microsoft.schemas.office.x2006.keyEncryptor.password.impl;

import com.microsoft.schemas.office.x2006.encryption.STSpinCount;
import org.apache.xmlbeans.XmlBase64Binary;
import com.microsoft.schemas.office.x2006.encryption.STHashAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.STCipherChaining;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.STHashSize;
import com.microsoft.schemas.office.x2006.encryption.STKeyBits;
import com.microsoft.schemas.office.x2006.encryption.STBlockSize;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.x2006.encryption.STSaltSize;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPasswordKeyEncryptorImpl extends XmlComplexContentImpl implements CTPasswordKeyEncryptor
{
    private static final long serialVersionUID = 1L;
    private static final QName SALTSIZE$0;
    private static final QName BLOCKSIZE$2;
    private static final QName KEYBITS$4;
    private static final QName HASHSIZE$6;
    private static final QName CIPHERALGORITHM$8;
    private static final QName CIPHERCHAINING$10;
    private static final QName HASHALGORITHM$12;
    private static final QName SALTVALUE$14;
    private static final QName SPINCOUNT$16;
    private static final QName ENCRYPTEDVERIFIERHASHINPUT$18;
    private static final QName ENCRYPTEDVERIFIERHASHVALUE$20;
    private static final QName ENCRYPTEDKEYVALUE$22;
    
    public CTPasswordKeyEncryptorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getSaltSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STSaltSize xgetSaltSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSaltSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
        }
    }
    
    public void setSaltSize(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSaltSize(final STSaltSize stSaltSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSaltSize stSaltSize2 = (STSaltSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
            if (stSaltSize2 == null) {
                stSaltSize2 = (STSaltSize)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SALTSIZE$0);
            }
            stSaltSize2.set((XmlObject)stSaltSize);
        }
    }
    
    public int getBlockSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STBlockSize xgetBlockSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBlockSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
        }
    }
    
    public void setBlockSize(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetBlockSize(final STBlockSize stBlockSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlockSize stBlockSize2 = (STBlockSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
            if (stBlockSize2 == null) {
                stBlockSize2 = (STBlockSize)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.BLOCKSIZE$2);
            }
            stBlockSize2.set((XmlObject)stBlockSize);
        }
    }
    
    public long getKeyBits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STKeyBits xgetKeyBits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STKeyBits)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
        }
    }
    
    public void setKeyBits(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetKeyBits(final STKeyBits stKeyBits) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STKeyBits stKeyBits2 = (STKeyBits)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
            if (stKeyBits2 == null) {
                stKeyBits2 = (STKeyBits)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.KEYBITS$4);
            }
            stKeyBits2.set((XmlObject)stKeyBits);
        }
    }
    
    public int getHashSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STHashSize xgetHashSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHashSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
        }
    }
    
    public void setHashSize(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetHashSize(final STHashSize stHashSize) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHashSize stHashSize2 = (STHashSize)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
            if (stHashSize2 == null) {
                stHashSize2 = (STHashSize)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.HASHSIZE$6);
            }
            stHashSize2.set((XmlObject)stHashSize);
        }
    }
    
    public STCipherAlgorithm.Enum getCipherAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
            if (simpleValue == null) {
                return null;
            }
            return (STCipherAlgorithm.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCipherAlgorithm xgetCipherAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCipherAlgorithm)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
        }
    }
    
    public void setCipherAlgorithm(final STCipherAlgorithm.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCipherAlgorithm(final STCipherAlgorithm stCipherAlgorithm) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCipherAlgorithm stCipherAlgorithm2 = (STCipherAlgorithm)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
            if (stCipherAlgorithm2 == null) {
                stCipherAlgorithm2 = (STCipherAlgorithm)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERALGORITHM$8);
            }
            stCipherAlgorithm2.set((XmlObject)stCipherAlgorithm);
        }
    }
    
    public STCipherChaining.Enum getCipherChaining() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
            if (simpleValue == null) {
                return null;
            }
            return (STCipherChaining.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCipherChaining xgetCipherChaining() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCipherChaining)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
        }
    }
    
    public void setCipherChaining(final STCipherChaining.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCipherChaining(final STCipherChaining stCipherChaining) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCipherChaining stCipherChaining2 = (STCipherChaining)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
            if (stCipherChaining2 == null) {
                stCipherChaining2 = (STCipherChaining)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.CIPHERCHAINING$10);
            }
            stCipherChaining2.set((XmlObject)stCipherChaining);
        }
    }
    
    public STHashAlgorithm.Enum getHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
            if (simpleValue == null) {
                return null;
            }
            return (STHashAlgorithm.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHashAlgorithm xgetHashAlgorithm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHashAlgorithm)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
        }
    }
    
    public void setHashAlgorithm(final STHashAlgorithm.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHashAlgorithm(final STHashAlgorithm stHashAlgorithm) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHashAlgorithm stHashAlgorithm2 = (STHashAlgorithm)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
            if (stHashAlgorithm2 == null) {
                stHashAlgorithm2 = (STHashAlgorithm)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.HASHALGORITHM$12);
            }
            stHashAlgorithm2.set((XmlObject)stHashAlgorithm);
        }
    }
    
    public byte[] getSaltValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetSaltValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
        }
    }
    
    public void setSaltValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetSaltValue(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SALTVALUE$14);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public int getSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STSpinCount xgetSpinCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSpinCount)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
        }
    }
    
    public void setSpinCount(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSpinCount(final STSpinCount stSpinCount) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSpinCount stSpinCount2 = (STSpinCount)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
            if (stSpinCount2 == null) {
                stSpinCount2 = (STSpinCount)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.SPINCOUNT$16);
            }
            stSpinCount2.set((XmlObject)stSpinCount);
        }
    }
    
    public byte[] getEncryptedVerifierHashInput() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedVerifierHashInput() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
        }
    }
    
    public void setEncryptedVerifierHashInput(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedVerifierHashInput(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHINPUT$18);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public byte[] getEncryptedVerifierHashValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedVerifierHashValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
        }
    }
    
    public void setEncryptedVerifierHashValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedVerifierHashValue(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDVERIFIERHASHVALUE$20);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    public byte[] getEncryptedKeyValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getByteArrayValue();
        }
    }
    
    public XmlBase64Binary xgetEncryptedKeyValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
        }
    }
    
    public void setEncryptedKeyValue(final byte[] byteArrayValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
            }
            simpleValue.setByteArrayValue(byteArrayValue);
        }
    }
    
    public void xsetEncryptedKeyValue(final XmlBase64Binary xmlBase64Binary) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBase64Binary xmlBase64Binary2 = (XmlBase64Binary)this.get_store().find_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
            if (xmlBase64Binary2 == null) {
                xmlBase64Binary2 = (XmlBase64Binary)this.get_store().add_attribute_user(CTPasswordKeyEncryptorImpl.ENCRYPTEDKEYVALUE$22);
            }
            xmlBase64Binary2.set((XmlObject)xmlBase64Binary);
        }
    }
    
    static {
        SALTSIZE$0 = new QName("", "saltSize");
        BLOCKSIZE$2 = new QName("", "blockSize");
        KEYBITS$4 = new QName("", "keyBits");
        HASHSIZE$6 = new QName("", "hashSize");
        CIPHERALGORITHM$8 = new QName("", "cipherAlgorithm");
        CIPHERCHAINING$10 = new QName("", "cipherChaining");
        HASHALGORITHM$12 = new QName("", "hashAlgorithm");
        SALTVALUE$14 = new QName("", "saltValue");
        SPINCOUNT$16 = new QName("", "spinCount");
        ENCRYPTEDVERIFIERHASHINPUT$18 = new QName("", "encryptedVerifierHashInput");
        ENCRYPTEDVERIFIERHASHVALUE$20 = new QName("", "encryptedVerifierHashValue");
        ENCRYPTEDKEYVALUE$22 = new QName("", "encryptedKeyValue");
    }
}
