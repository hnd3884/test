package com.adventnet.tools.prevalent;

import java.util.Iterator;
import java.util.HashMap;
import java.io.Serializable;

public class BObject implements Serializable
{
    private static final long serialVersionUID = 3487495895819111L;
    private HashMap versionMap;
    private HashMap productMap;
    private int[] productName;
    private int[] productVersion;
    private int[] backwardState;
    private int[] bundleNativeFiles;
    private int[] isTrialMacBased;
    private int[] allowFreeAfterExpiry;
    private int[] oneTimeStandardEval;
    private int[] isAgreementHide;
    private int[] mandatoryMacLicense;
    
    public BObject(final int[] name, final int[] version, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final int[] nativeFiles, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.bundleNativeFiles = nativeFiles;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final int[] nativeFiles, final int[] macTrial, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.bundleNativeFiles = nativeFiles;
        this.isTrialMacBased = macTrial;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final int[] nativeFiles, final int[] macTrial, final int[] allowFree, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.bundleNativeFiles = nativeFiles;
        this.isTrialMacBased = macTrial;
        this.allowFreeAfterExpiry = allowFree;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final int[] nativeFiles, final int[] macTrial, final int[] allowFree, final int[] oneTimeEval, final HashMap versionMap, final HashMap product) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.bundleNativeFiles = nativeFiles;
        this.isTrialMacBased = macTrial;
        this.allowFreeAfterExpiry = allowFree;
        this.oneTimeStandardEval = oneTimeEval;
        this.versionMap = versionMap;
        this.productMap = product;
    }
    
    public BObject(final int[] name, final int[] version, final int[] backward, final int[] nativeFiles, final int[] macTrial, final int[] allowFree, final int[] oneTimeEval, final HashMap versionMap, final HashMap product, final int[] agreementHide, final int[] macbasedLicense) {
        this.versionMap = null;
        this.productMap = null;
        this.productName = null;
        this.productVersion = null;
        this.backwardState = null;
        this.bundleNativeFiles = null;
        this.isTrialMacBased = null;
        this.allowFreeAfterExpiry = null;
        this.oneTimeStandardEval = null;
        this.isAgreementHide = null;
        this.mandatoryMacLicense = null;
        this.productName = name;
        this.productVersion = version;
        this.backwardState = backward;
        this.bundleNativeFiles = nativeFiles;
        this.isTrialMacBased = macTrial;
        this.allowFreeAfterExpiry = allowFree;
        this.oneTimeStandardEval = oneTimeEval;
        this.versionMap = versionMap;
        this.productMap = product;
        this.isAgreementHide = agreementHide;
        this.mandatoryMacLicense = macbasedLicense;
    }
    
    public int[] getProductName() {
        return this.productName;
    }
    
    public int[] getProductVersion() {
        return this.productVersion;
    }
    
    public void setProductName(final String product) {
        if (product != null) {
            this.productName = Encode.shiftBytes(product);
        }
    }
    
    public void setProductVersion(final String version) {
        if (version != null) {
            this.productVersion = Encode.shiftBytes(version);
        }
    }
    
    public int[] getBackwardState() {
        return this.backwardState;
    }
    
    public int[] getNativeFilesState() {
        return this.bundleNativeFiles;
    }
    
    public int[] isTrialMacBased() {
        return this.isTrialMacBased;
    }
    
    public int[] allowFreeAfterExpiry() {
        return this.allowFreeAfterExpiry;
    }
    
    public int[] oneTimeStandardEval() {
        return this.oneTimeStandardEval;
    }
    
    public int[] isAgreementHide() {
        return this.isAgreementHide;
    }
    
    public int[] mandatoryMacLicense() {
        return this.mandatoryMacLicense;
    }
    
    public Product getSupportedProduct(final String version, final String type) {
        final String str = version + "_" + type;
        final Version ver = this.versionMap.get(str);
        if (ver == null) {
            return null;
        }
        final int[] id = ver.getID();
        final char[] ch = Encode.revShiftBytes(id);
        final String product = new String(ch);
        return this.productMap.get(product);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("\n\nProduct Name  : " + new String(Encode.revShiftBytes(this.productName)));
        buf.append("\nProduct Version : " + new String(Encode.revShiftBytes(this.productVersion)));
        if (this.backwardState != null) {
            buf.append("\nBackward State  : " + new String(Encode.revShiftBytes(this.backwardState)));
        }
        else {
            buf.append("\nBackward State  : " + this.backwardState);
        }
        if (this.bundleNativeFiles != null) {
            buf.append("\nIs Native Files bundled : " + new String(Encode.revShiftBytes(this.bundleNativeFiles)));
        }
        else {
            buf.append("\nIs Native Files bundled : " + this.bundleNativeFiles);
        }
        if (this.isTrialMacBased != null) {
            buf.append("\nIs Mac Based Trial: " + new String(Encode.revShiftBytes(this.isTrialMacBased)));
        }
        else {
            buf.append("\nIs Mac Based Trial: " + this.isTrialMacBased);
        }
        if (this.allowFreeAfterExpiry != null) {
            buf.append("\nAllow Free after expiry: " + new String(Encode.revShiftBytes(this.allowFreeAfterExpiry)));
        }
        else {
            buf.append("\nAllow Free after expiry: " + this.allowFreeAfterExpiry);
        }
        if (this.oneTimeStandardEval != null) {
            buf.append("\nAllow Standard Evaluation only once: " + new String(Encode.revShiftBytes(this.oneTimeStandardEval)));
        }
        else {
            buf.append("\nAllow Standard Evaluation only once: " + this.oneTimeStandardEval);
        }
        if (this.isAgreementHide != null) {
            buf.append("\nAgreement panel hide: " + new String(Encode.revShiftBytes(this.isAgreementHide)));
        }
        else {
            buf.append("\nAgreement panel hide: " + this.isAgreementHide);
        }
        if (this.mandatoryMacLicense != null) {
            buf.append("\nMandatory Mac Based License: " + new String(Encode.revShiftBytes(this.mandatoryMacLicense)));
        }
        else {
            buf.append("\nMandatory Man Based License: " + this.mandatoryMacLicense);
        }
        for (final Version ver : this.versionMap.values()) {
            buf.append("\n" + ver);
        }
        for (final Product pro : this.productMap.values()) {
            buf.append("\n" + pro);
        }
        return buf.toString();
    }
}
