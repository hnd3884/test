package org.apache.poi.hssf.usermodel;

import java.util.HashMap;
import org.apache.poi.util.POILogFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.awt.Font;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.util.POILogger;

final class StaticFontMetrics
{
    private static final POILogger LOGGER;
    private static Properties fontMetricsProps;
    private static final Map<String, FontDetails> fontDetailsMap;
    
    private StaticFontMetrics() {
    }
    
    public static synchronized FontDetails getFontDetails(final Font font) {
        if (StaticFontMetrics.fontMetricsProps == null) {
            try {
                StaticFontMetrics.fontMetricsProps = loadMetrics();
            }
            catch (final IOException e) {
                throw new RuntimeException("Could not load font metrics", e);
            }
        }
        String fontName = font.getName();
        String fontStyle = "";
        if (font.isPlain()) {
            fontStyle += "plain";
        }
        if (font.isBold()) {
            fontStyle += "bold";
        }
        if (font.isItalic()) {
            fontStyle += "italic";
        }
        final String fontHeight = FontDetails.buildFontHeightProperty(fontName);
        final String styleHeight = FontDetails.buildFontHeightProperty(fontName + "." + fontStyle);
        if (StaticFontMetrics.fontMetricsProps.get(fontHeight) == null && StaticFontMetrics.fontMetricsProps.get(styleHeight) != null) {
            fontName = fontName + "." + fontStyle;
        }
        FontDetails fontDetails = StaticFontMetrics.fontDetailsMap.get(fontName);
        if (fontDetails == null) {
            fontDetails = FontDetails.create(fontName, StaticFontMetrics.fontMetricsProps);
            StaticFontMetrics.fontDetailsMap.put(fontName, fontDetails);
        }
        return fontDetails;
    }
    
    private static Properties loadMetrics() throws IOException {
        File propFile = null;
        try {
            final String propFileName = System.getProperty("font.metrics.filename");
            if (propFileName != null) {
                propFile = new File(propFileName);
                if (!propFile.exists()) {
                    StaticFontMetrics.LOGGER.log(5, "font_metrics.properties not found at path " + propFile.getAbsolutePath());
                    propFile = null;
                }
            }
        }
        catch (final SecurityException e) {
            StaticFontMetrics.LOGGER.log(5, "Can't access font.metrics.filename system property", e);
        }
        InputStream metricsIn = null;
        try {
            if (propFile != null) {
                metricsIn = new FileInputStream(propFile);
            }
            else {
                metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
                if (metricsIn == null) {
                    final String err = "font_metrics.properties not found in classpath";
                    throw new IOException(err);
                }
            }
            final Properties props = new Properties();
            props.load(metricsIn);
            return props;
        }
        finally {
            if (metricsIn != null) {
                metricsIn.close();
            }
        }
    }
    
    static {
        LOGGER = POILogFactory.getLogger(StaticFontMetrics.class);
        fontDetailsMap = new HashMap<String, FontDetails>();
    }
}
