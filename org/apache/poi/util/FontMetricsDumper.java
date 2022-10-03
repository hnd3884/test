package org.apache.poi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.awt.FontMetrics;
import java.io.FileOutputStream;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Properties;

public class FontMetricsDumper
{
    @SuppressForbidden("command line tool")
    public static void main(final String[] args) throws IOException {
        final Properties props = new Properties();
        final Font[] allFonts2;
        final Font[] allFonts = allFonts2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (final Font allFont : allFonts2) {
            final String fontName = allFont.getFontName();
            final Font font = new Font(fontName, 1, 10);
            final FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
            final int fontHeight = fontMetrics.getHeight();
            props.setProperty("font." + fontName + ".height", fontHeight + "");
            final StringBuilder characters = new StringBuilder();
            for (char c = 'a'; c <= 'z'; ++c) {
                characters.append(c).append(", ");
            }
            for (char c = 'A'; c <= 'Z'; ++c) {
                characters.append(c).append(", ");
            }
            for (char c = '0'; c <= '9'; ++c) {
                characters.append(c).append(", ");
            }
            final StringBuilder widths = new StringBuilder();
            for (char c2 = 'a'; c2 <= 'z'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2]).append(", ");
            }
            for (char c2 = 'A'; c2 <= 'Z'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2]).append(", ");
            }
            for (char c2 = '0'; c2 <= '9'; ++c2) {
                widths.append(fontMetrics.getWidths()[c2]).append(", ");
            }
            props.setProperty("font." + fontName + ".characters", characters.toString());
            props.setProperty("font." + fontName + ".widths", widths.toString());
        }
        try (final OutputStream fileOut = new FileOutputStream("font_metrics.properties")) {
            props.store(fileOut, "Font Metrics");
        }
    }
}
