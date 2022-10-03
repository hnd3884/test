package sun.misc;

import java.text.MessageFormat;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.ResourceBundle;

public class ExtensionInfo
{
    public static final int COMPATIBLE = 0;
    public static final int REQUIRE_SPECIFICATION_UPGRADE = 1;
    public static final int REQUIRE_IMPLEMENTATION_UPGRADE = 2;
    public static final int REQUIRE_VENDOR_SWITCH = 3;
    public static final int INCOMPATIBLE = 4;
    public String title;
    public String name;
    public String specVersion;
    public String specVendor;
    public String implementationVersion;
    public String vendor;
    public String vendorId;
    public String url;
    private static final ResourceBundle rb;
    
    public ExtensionInfo() {
    }
    
    public ExtensionInfo(final String s, final Attributes attributes) throws NullPointerException {
        String string;
        if (s != null) {
            string = s + "-";
        }
        else {
            string = "";
        }
        this.name = attributes.getValue(string + Attributes.Name.EXTENSION_NAME.toString());
        if (this.name != null) {
            this.name = this.name.trim();
        }
        this.title = attributes.getValue(string + Attributes.Name.SPECIFICATION_TITLE.toString());
        if (this.title != null) {
            this.title = this.title.trim();
        }
        this.specVersion = attributes.getValue(string + Attributes.Name.SPECIFICATION_VERSION.toString());
        if (this.specVersion != null) {
            this.specVersion = this.specVersion.trim();
        }
        this.specVendor = attributes.getValue(string + Attributes.Name.SPECIFICATION_VENDOR.toString());
        if (this.specVendor != null) {
            this.specVendor = this.specVendor.trim();
        }
        this.implementationVersion = attributes.getValue(string + Attributes.Name.IMPLEMENTATION_VERSION.toString());
        if (this.implementationVersion != null) {
            this.implementationVersion = this.implementationVersion.trim();
        }
        this.vendor = attributes.getValue(string + Attributes.Name.IMPLEMENTATION_VENDOR.toString());
        if (this.vendor != null) {
            this.vendor = this.vendor.trim();
        }
        this.vendorId = attributes.getValue(string + Attributes.Name.IMPLEMENTATION_VENDOR_ID.toString());
        if (this.vendorId != null) {
            this.vendorId = this.vendorId.trim();
        }
        this.url = attributes.getValue(string + Attributes.Name.IMPLEMENTATION_URL.toString());
        if (this.url != null) {
            this.url = this.url.trim();
        }
    }
    
    public int isCompatibleWith(final ExtensionInfo extensionInfo) {
        if (this.name == null || extensionInfo.name == null) {
            return 4;
        }
        if (this.name.compareTo(extensionInfo.name) != 0) {
            return 4;
        }
        if (this.specVersion == null || extensionInfo.specVersion == null) {
            return 0;
        }
        if (this.compareExtensionVersion(this.specVersion, extensionInfo.specVersion) >= 0) {
            if (this.vendorId != null && extensionInfo.vendorId != null) {
                if (this.vendorId.compareTo(extensionInfo.vendorId) != 0) {
                    return 3;
                }
                if (this.implementationVersion != null && extensionInfo.implementationVersion != null && this.compareExtensionVersion(this.implementationVersion, extensionInfo.implementationVersion) < 0) {
                    return 2;
                }
            }
            return 0;
        }
        if (this.vendorId != null && extensionInfo.vendorId != null && this.vendorId.compareTo(extensionInfo.vendorId) != 0) {
            return 3;
        }
        return 1;
    }
    
    @Override
    public String toString() {
        return "Extension : title(" + this.title + "), name(" + this.name + "), spec vendor(" + this.specVendor + "), spec version(" + this.specVersion + "), impl vendor(" + this.vendor + "), impl vendor id(" + this.vendorId + "), impl version(" + this.implementationVersion + "), impl url(" + this.url + ")";
    }
    
    private int compareExtensionVersion(String lowerCase, String lowerCase2) throws NumberFormatException {
        lowerCase = lowerCase.toLowerCase();
        lowerCase2 = lowerCase2.toLowerCase();
        return this.strictCompareExtensionVersion(lowerCase, lowerCase2);
    }
    
    private int strictCompareExtensionVersion(final String s, final String s2) throws NumberFormatException {
        if (s.equals(s2)) {
            return 0;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ".,");
        final StringTokenizer stringTokenizer2 = new StringTokenizer(s2, ".,");
        int convertToken = 0;
        int convertToken2 = 0;
        if (stringTokenizer.hasMoreTokens()) {
            convertToken = this.convertToken(stringTokenizer.nextToken().toString());
        }
        if (stringTokenizer2.hasMoreTokens()) {
            convertToken2 = this.convertToken(stringTokenizer2.nextToken().toString());
        }
        if (convertToken > convertToken2) {
            return 1;
        }
        if (convertToken2 > convertToken) {
            return -1;
        }
        int index = s.indexOf(".");
        int index2 = s2.indexOf(".");
        if (index == -1) {
            index = s.length() - 1;
        }
        if (index2 == -1) {
            index2 = s2.length() - 1;
        }
        return this.strictCompareExtensionVersion(s.substring(index + 1), s2.substring(index2 + 1));
    }
    
    private int convertToken(final String s) {
        if (s == null || s.equals("")) {
            return 0;
        }
        int n = 0;
        final int length;
        final int n2 = length = s.length();
        final String format = new MessageFormat(ExtensionInfo.rb.getString("optpkg.versionerror")).format(new Object[] { this.name });
        final int index = s.indexOf("-");
        final int index2 = s.indexOf("_");
        if (index == -1 && index2 == -1) {
            try {
                return Integer.parseInt(s) * 100;
            }
            catch (final NumberFormatException ex) {
                System.out.println(format);
                return 0;
            }
        }
        if (index2 != -1) {
            int int1;
            int n3;
            try {
                int1 = Integer.parseInt(s.substring(0, index2));
                final char char1 = s.charAt(n2 - 1);
                if (Character.isLetter(char1)) {
                    final int numericValue = Character.getNumericValue(char1);
                    n3 = Integer.parseInt(s.substring(index2 + 1, n2 - 1));
                    if (numericValue >= Character.getNumericValue('a') && numericValue <= Character.getNumericValue('z')) {
                        n = n3 * 100 + numericValue;
                    }
                    else {
                        n = 0;
                        System.out.println(format);
                    }
                }
                else {
                    n3 = Integer.parseInt(s.substring(index2 + 1, length));
                }
            }
            catch (final NumberFormatException ex2) {
                System.out.println(format);
                return 0;
            }
            return int1 * 100 + (n3 + n);
        }
        int int2;
        try {
            int2 = Integer.parseInt(s.substring(0, index));
        }
        catch (final NumberFormatException ex3) {
            System.out.println(format);
            return 0;
        }
        final String substring = s.substring(index + 1);
        String s2 = "";
        int n4 = 0;
        if (substring.indexOf("ea") != -1) {
            s2 = substring.substring(2);
            n4 = 50;
        }
        else if (substring.indexOf("alpha") != -1) {
            s2 = substring.substring(5);
            n4 = 40;
        }
        else if (substring.indexOf("beta") != -1) {
            s2 = substring.substring(4);
            n4 = 30;
        }
        else if (substring.indexOf("rc") != -1) {
            s2 = substring.substring(2);
            n4 = 20;
        }
        if (s2 == null || s2.equals("")) {
            return int2 * 100 - n4;
        }
        try {
            return int2 * 100 - n4 + Integer.parseInt(s2);
        }
        catch (final NumberFormatException ex4) {
            System.out.println(format);
            return 0;
        }
    }
    
    static {
        rb = ResourceBundle.getBundle("sun.misc.resources.Messages");
    }
}
