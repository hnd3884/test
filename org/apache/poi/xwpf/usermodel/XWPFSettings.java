package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.SettingsDocument;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import java.util.Arrays;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STAlgClass;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STAlgType;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import java.security.SecureRandom;
import org.apache.poi.EncryptedDocumentException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STCryptProv;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import java.math.BigInteger;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTZoom;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XWPFSettings extends POIXMLDocumentPart
{
    private CTSettings ctSettings;
    
    public XWPFSettings(final PackagePart part) throws IOException {
        super(part);
    }
    
    public XWPFSettings() {
        this.ctSettings = CTSettings.Factory.newInstance();
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
        this.readFrom(this.getPackagePart().getInputStream());
    }
    
    public long getZoomPercent() {
        CTZoom zoom;
        if (!this.ctSettings.isSetZoom()) {
            zoom = this.ctSettings.addNewZoom();
        }
        else {
            zoom = this.ctSettings.getZoom();
        }
        final BigInteger percent = zoom.getPercent();
        if (percent == null) {
            return 100L;
        }
        return percent.longValue();
    }
    
    public void setZoomPercent(final long zoomPercent) {
        if (!this.ctSettings.isSetZoom()) {
            this.ctSettings.addNewZoom();
        }
        final CTZoom zoom = this.ctSettings.getZoom();
        zoom.setPercent(BigInteger.valueOf(zoomPercent));
    }
    
    public boolean isEnforcedWith() {
        final CTDocProtect ctDocProtect = this.ctSettings.getDocumentProtection();
        return ctDocProtect != null && ctDocProtect.getEnforcement().equals(STOnOff.X_1);
    }
    
    public boolean isEnforcedWith(final STDocProtect.Enum editValue) {
        final CTDocProtect ctDocProtect = this.ctSettings.getDocumentProtection();
        return ctDocProtect != null && ctDocProtect.getEnforcement().equals(STOnOff.X_1) && ctDocProtect.getEdit().equals(editValue);
    }
    
    public void setEnforcementEditValue(final STDocProtect.Enum editValue) {
        this.safeGetDocumentProtection().setEnforcement(STOnOff.X_1);
        this.safeGetDocumentProtection().setEdit(editValue);
    }
    
    public void setEnforcementEditValue(final STDocProtect.Enum editValue, final String password, HashAlgorithm hashAlgo) {
        this.safeGetDocumentProtection().setEnforcement(STOnOff.X_1);
        this.safeGetDocumentProtection().setEdit(editValue);
        if (password == null) {
            if (this.safeGetDocumentProtection().isSetCryptProviderType()) {
                this.safeGetDocumentProtection().unsetCryptProviderType();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmClass()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmClass();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmType()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmType();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmSid()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmSid();
            }
            if (this.safeGetDocumentProtection().isSetSalt()) {
                this.safeGetDocumentProtection().unsetSalt();
            }
            if (this.safeGetDocumentProtection().isSetCryptSpinCount()) {
                this.safeGetDocumentProtection().unsetCryptSpinCount();
            }
            if (this.safeGetDocumentProtection().isSetHash()) {
                this.safeGetDocumentProtection().unsetHash();
            }
        }
        else {
            if (hashAlgo == null) {
                hashAlgo = HashAlgorithm.sha1;
            }
            STCryptProv.Enum providerType = null;
            int sid = 0;
            switch (hashAlgo) {
                case md2: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 1;
                    break;
                }
                case md4: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 2;
                    break;
                }
                case md5: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 3;
                    break;
                }
                case sha1: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 4;
                    break;
                }
                case sha256: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 12;
                    break;
                }
                case sha384: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 13;
                    break;
                }
                case sha512: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 14;
                    break;
                }
                default: {
                    throw new EncryptedDocumentException("Hash algorithm '" + hashAlgo + "' is not supported for document write protection.");
                }
            }
            final SecureRandom random = new SecureRandom();
            final byte[] salt = random.generateSeed(16);
            final int spinCount = 100000;
            final String legacyHash = CryptoFunctions.xorHashPasswordReversed(password);
            final byte[] hash = CryptoFunctions.hashPassword(legacyHash, hashAlgo, salt, spinCount, false);
            this.safeGetDocumentProtection().setSalt(salt);
            this.safeGetDocumentProtection().setHash(hash);
            this.safeGetDocumentProtection().setCryptSpinCount(BigInteger.valueOf(spinCount));
            this.safeGetDocumentProtection().setCryptAlgorithmType(STAlgType.TYPE_ANY);
            this.safeGetDocumentProtection().setCryptAlgorithmClass(STAlgClass.HASH);
            this.safeGetDocumentProtection().setCryptProviderType(providerType);
            this.safeGetDocumentProtection().setCryptAlgorithmSid(BigInteger.valueOf(sid));
        }
    }
    
    public boolean validateProtectionPassword(final String password) {
        final BigInteger sid = this.safeGetDocumentProtection().getCryptAlgorithmSid();
        final byte[] hash = this.safeGetDocumentProtection().getHash();
        final byte[] salt = this.safeGetDocumentProtection().getSalt();
        final BigInteger spinCount = this.safeGetDocumentProtection().getCryptSpinCount();
        if (sid == null || hash == null || salt == null || spinCount == null) {
            return false;
        }
        HashAlgorithm hashAlgo = null;
        switch (sid.intValue()) {
            case 1: {
                hashAlgo = HashAlgorithm.md2;
                break;
            }
            case 2: {
                hashAlgo = HashAlgorithm.md4;
                break;
            }
            case 3: {
                hashAlgo = HashAlgorithm.md5;
                break;
            }
            case 4: {
                hashAlgo = HashAlgorithm.sha1;
                break;
            }
            case 12: {
                hashAlgo = HashAlgorithm.sha256;
                break;
            }
            case 13: {
                hashAlgo = HashAlgorithm.sha384;
                break;
            }
            case 14: {
                hashAlgo = HashAlgorithm.sha512;
                break;
            }
            default: {
                return false;
            }
        }
        final String legacyHash = CryptoFunctions.xorHashPasswordReversed(password);
        final byte[] hash2 = CryptoFunctions.hashPassword(legacyHash, hashAlgo, salt, spinCount.intValue(), false);
        return Arrays.equals(hash, hash2);
    }
    
    public void removeEnforcement() {
        this.safeGetDocumentProtection().setEnforcement(STOnOff.X_0);
    }
    
    public void setUpdateFields() {
        final CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(STOnOff.TRUE);
        this.ctSettings.setUpdateFields(onOff);
    }
    
    boolean isUpdateFields() {
        return this.ctSettings.isSetUpdateFields() && this.ctSettings.getUpdateFields().getVal() == STOnOff.TRUE;
    }
    
    public boolean isTrackRevisions() {
        return this.ctSettings.isSetTrackRevisions();
    }
    
    public void setTrackRevisions(final boolean enable) {
        if (enable) {
            if (!this.ctSettings.isSetTrackRevisions()) {
                this.ctSettings.addNewTrackRevisions();
            }
        }
        else if (this.ctSettings.isSetTrackRevisions()) {
            this.ctSettings.unsetTrackRevisions();
        }
    }
    
    @Override
    protected void commit() throws IOException {
        if (this.ctSettings == null) {
            throw new IllegalStateException("Unable to write out settings that were never read in!");
        }
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTSettings.type.getName().getNamespaceURI(), "settings"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.ctSettings.save(out, xmlOptions);
        out.close();
    }
    
    private CTDocProtect safeGetDocumentProtection() {
        CTDocProtect documentProtection = this.ctSettings.getDocumentProtection();
        if (documentProtection == null) {
            documentProtection = CTDocProtect.Factory.newInstance();
            this.ctSettings.setDocumentProtection(documentProtection);
        }
        return this.ctSettings.getDocumentProtection();
    }
    
    private void readFrom(final InputStream inputStream) {
        try {
            this.ctSettings = SettingsDocument.Factory.parse(inputStream, POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getSettings();
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean getEvenAndOddHeadings() {
        return this.ctSettings.isSetEvenAndOddHeaders();
    }
    
    public void setEvenAndOddHeadings(final boolean enable) {
        final CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(enable ? STOnOff.TRUE : STOnOff.FALSE);
        this.ctSettings.setEvenAndOddHeaders(onOff);
    }
    
    public boolean getMirrorMargins() {
        return this.ctSettings.isSetMirrorMargins();
    }
    
    public void setMirrorMargins(final boolean enable) {
        final CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(enable ? STOnOff.TRUE : STOnOff.FALSE);
        this.ctSettings.setMirrorMargins(onOff);
    }
}
