package org.apache.poi.xssf.usermodel.helpers;

public class HeaderFooterHelper
{
    private static final String HeaderFooterEntity_L = "&L";
    private static final String HeaderFooterEntity_C = "&C";
    private static final String HeaderFooterEntity_R = "&R";
    public static final String HeaderFooterEntity_File = "&F";
    public static final String HeaderFooterEntity_Date = "&D";
    public static final String HeaderFooterEntity_Time = "&T";
    
    public String getLeftSection(final String string) {
        return this.getParts(string)[0];
    }
    
    public String getCenterSection(final String string) {
        return this.getParts(string)[1];
    }
    
    public String getRightSection(final String string) {
        return this.getParts(string)[2];
    }
    
    public String setLeftSection(final String string, final String newLeft) {
        final String[] parts = this.getParts(string);
        parts[0] = newLeft;
        return this.joinParts(parts);
    }
    
    public String setCenterSection(final String string, final String newCenter) {
        final String[] parts = this.getParts(string);
        parts[1] = newCenter;
        return this.joinParts(parts);
    }
    
    public String setRightSection(final String string, final String newRight) {
        final String[] parts = this.getParts(string);
        parts[2] = newRight;
        return this.joinParts(parts);
    }
    
    private String[] getParts(String string) {
        final String[] parts = { "", "", "" };
        if (string == null) {
            return parts;
        }
        int lAt = 0;
        int cAt = 0;
        int rAt = 0;
        while ((lAt = string.indexOf("&L")) > -2 && (cAt = string.indexOf("&C")) > -2 && (rAt = string.indexOf("&R")) > -2 && (lAt > -1 || cAt > -1 || rAt > -1)) {
            if (rAt > cAt && rAt > lAt) {
                parts[2] = string.substring(rAt + "&R".length());
                string = string.substring(0, rAt);
            }
            else if (cAt > rAt && cAt > lAt) {
                parts[1] = string.substring(cAt + "&C".length());
                string = string.substring(0, cAt);
            }
            else {
                parts[0] = string.substring(lAt + "&L".length());
                string = string.substring(0, lAt);
            }
        }
        return parts;
    }
    
    private String joinParts(final String[] parts) {
        return this.joinParts(parts[0], parts[1], parts[2]);
    }
    
    private String joinParts(final String l, final String c, final String r) {
        final StringBuilder ret = new StringBuilder(64);
        if (c.length() > 0) {
            ret.append("&C");
            ret.append(c);
        }
        if (l.length() > 0) {
            ret.append("&L");
            ret.append(l);
        }
        if (r.length() > 0) {
            ret.append("&R");
            ret.append(r);
        }
        return ret.toString();
    }
}
