package org.apache.lucene.search.highlight;

public class GradientFormatter implements Formatter
{
    private float maxScore;
    int fgRMin;
    int fgGMin;
    int fgBMin;
    int fgRMax;
    int fgGMax;
    int fgBMax;
    protected boolean highlightForeground;
    int bgRMin;
    int bgGMin;
    int bgBMin;
    int bgRMax;
    int bgGMax;
    int bgBMax;
    protected boolean highlightBackground;
    private static char[] hexDigits;
    
    public GradientFormatter(final float maxScore, final String minForegroundColor, final String maxForegroundColor, final String minBackgroundColor, final String maxBackgroundColor) {
        this.highlightForeground = (minForegroundColor != null && maxForegroundColor != null);
        if (this.highlightForeground) {
            if (minForegroundColor.length() != 7) {
                throw new IllegalArgumentException("minForegroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            if (maxForegroundColor.length() != 7) {
                throw new IllegalArgumentException("minForegroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            this.fgRMin = hexToInt(minForegroundColor.substring(1, 3));
            this.fgGMin = hexToInt(minForegroundColor.substring(3, 5));
            this.fgBMin = hexToInt(minForegroundColor.substring(5, 7));
            this.fgRMax = hexToInt(maxForegroundColor.substring(1, 3));
            this.fgGMax = hexToInt(maxForegroundColor.substring(3, 5));
            this.fgBMax = hexToInt(maxForegroundColor.substring(5, 7));
        }
        this.highlightBackground = (minBackgroundColor != null && maxBackgroundColor != null);
        if (this.highlightBackground) {
            if (minBackgroundColor.length() != 7) {
                throw new IllegalArgumentException("minBackgroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            if (maxBackgroundColor.length() != 7) {
                throw new IllegalArgumentException("minBackgroundColor is not 7 bytes long eg a hex RGB value such as #FFFFFF");
            }
            this.bgRMin = hexToInt(minBackgroundColor.substring(1, 3));
            this.bgGMin = hexToInt(minBackgroundColor.substring(3, 5));
            this.bgBMin = hexToInt(minBackgroundColor.substring(5, 7));
            this.bgRMax = hexToInt(maxBackgroundColor.substring(1, 3));
            this.bgGMax = hexToInt(maxBackgroundColor.substring(3, 5));
            this.bgBMax = hexToInt(maxBackgroundColor.substring(5, 7));
        }
        this.maxScore = maxScore;
    }
    
    @Override
    public String highlightTerm(final String originalText, final TokenGroup tokenGroup) {
        if (tokenGroup.getTotalScore() == 0.0f) {
            return originalText;
        }
        final float score = tokenGroup.getTotalScore();
        if (score == 0.0f) {
            return originalText;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("<font ");
        if (this.highlightForeground) {
            sb.append("color=\"");
            sb.append(this.getForegroundColorString(score));
            sb.append("\" ");
        }
        if (this.highlightBackground) {
            sb.append("bgcolor=\"");
            sb.append(this.getBackgroundColorString(score));
            sb.append("\" ");
        }
        sb.append(">");
        sb.append(originalText);
        sb.append("</font>");
        return sb.toString();
    }
    
    protected String getForegroundColorString(final float score) {
        final int rVal = this.getColorVal(this.fgRMin, this.fgRMax, score);
        final int gVal = this.getColorVal(this.fgGMin, this.fgGMax, score);
        final int bVal = this.getColorVal(this.fgBMin, this.fgBMax, score);
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(intToHex(rVal));
        sb.append(intToHex(gVal));
        sb.append(intToHex(bVal));
        return sb.toString();
    }
    
    protected String getBackgroundColorString(final float score) {
        final int rVal = this.getColorVal(this.bgRMin, this.bgRMax, score);
        final int gVal = this.getColorVal(this.bgGMin, this.bgGMax, score);
        final int bVal = this.getColorVal(this.bgBMin, this.bgBMax, score);
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(intToHex(rVal));
        sb.append(intToHex(gVal));
        sb.append(intToHex(bVal));
        return sb.toString();
    }
    
    private int getColorVal(final int colorMin, final int colorMax, final float score) {
        if (colorMin == colorMax) {
            return colorMin;
        }
        final float scale = (float)Math.abs(colorMin - colorMax);
        final float relScorePercent = Math.min(this.maxScore, score) / this.maxScore;
        final float colScore = scale * relScorePercent;
        return Math.min(colorMin, colorMax) + (int)colScore;
    }
    
    private static String intToHex(final int i) {
        return "" + GradientFormatter.hexDigits[(i & 0xF0) >> 4] + GradientFormatter.hexDigits[i & 0xF];
    }
    
    public static final int hexToInt(final String hex) {
        final int len = hex.length();
        if (len > 16) {
            throw new NumberFormatException();
        }
        int l = 0;
        for (int i = 0; i < len; ++i) {
            l <<= 4;
            final int c = Character.digit(hex.charAt(i), 16);
            if (c < 0) {
                throw new NumberFormatException();
            }
            l |= c;
        }
        return l;
    }
    
    static {
        GradientFormatter.hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
