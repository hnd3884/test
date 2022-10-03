package com.adventnet.client.components.chart.util;

import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import javax.servlet.ServletContext;
import com.adventnet.client.components.chart.web.ChartConstants;
import com.adventnet.persistence.Row;
import java.util.Map;
import com.adventnet.client.view.web.ViewContext;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.logging.Level;
import java.util.StringTokenizer;
import java.awt.Font;
import javax.swing.table.TableModel;
import java.util.logging.Logger;

public class ChartUtil
{
    private static Logger logger;
    
    public static int getColumn(final TableModel tm, String columnName) {
        columnName = columnName.trim();
        for (int count = tm.getColumnCount(), i = 0; i < count; ++i) {
            final String column = tm.getColumnName(i);
            if (column.trim().equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("There is no such column named :" + columnName + " in passed table model" + tm);
    }
    
    public static Font getFont(final String value) throws Exception {
        String name = "Dialog";
        int style = 0;
        int size = 10;
        final StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final int index = token.indexOf("=");
            if (index != -1) {
                final String key = token.substring(0, index);
                final String val = token.substring(index + 1, token.length());
                if (key.trim().equalsIgnoreCase("name")) {
                    name = val;
                }
                else if (key.trim().equalsIgnoreCase("size")) {
                    try {
                        size = Integer.parseInt(val);
                    }
                    catch (final NumberFormatException ne) {
                        ChartUtil.logger.log(Level.WARNING, "Exception in applying the value : " + ne, ne);
                    }
                }
                else {
                    if (!key.trim().equalsIgnoreCase("style")) {
                        continue;
                    }
                    if (val.trim().equalsIgnoreCase("plain")) {
                        style = 0;
                    }
                    else if (val.trim().equalsIgnoreCase("bold")) {
                        style = 1;
                    }
                    else {
                        if (!val.trim().equalsIgnoreCase("italic")) {
                            continue;
                        }
                        style = 2;
                    }
                }
            }
        }
        final Font font = new Font(name, style, size);
        return font;
    }
    
    public static Color getColorFromHex(final String str) {
        final String r = str.substring(0, 2);
        final String g = str.substring(2, 4);
        final String b = str.substring(4, 6);
        return new Color(Integer.parseInt(r, 16), Integer.parseInt(g, 16), Integer.parseInt(b, 16));
    }
    
    public static BasicStroke getStroke(final String value) throws Exception {
        float width = 0.0f;
        int cap = 2;
        int join = 0;
        float miterLimit = 10.0f;
        float[] dash = null;
        float dash_phase = 0.0f;
        final StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final int index = token.indexOf("=");
            if (index != -1) {
                final String key = token.substring(0, index);
                final String val = token.substring(index + 1, token.length());
                if (key.trim().equalsIgnoreCase("width")) {
                    try {
                        width = Float.parseFloat(val);
                    }
                    catch (final NumberFormatException ne) {
                        ChartUtil.logger.log(Level.WARNING, "Exception in applying the value : " + ne, ne);
                    }
                }
                else if (key.trim().equalsIgnoreCase("dash_phase")) {
                    try {
                        dash_phase = Float.parseFloat(val);
                    }
                    catch (final NumberFormatException ne) {
                        ChartUtil.logger.log(Level.WARNING, "Exception in applying the value : " + ne, ne);
                    }
                }
                else if (key.trim().equalsIgnoreCase("miterlimit")) {
                    try {
                        miterLimit = Float.parseFloat(val);
                    }
                    catch (final NumberFormatException ne) {
                        ChartUtil.logger.log(Level.WARNING, "Exception in applying the value : " + ne, ne);
                    }
                }
                else if (key.trim().equalsIgnoreCase("cap")) {
                    if (val.trim().equalsIgnoreCase("butt")) {
                        cap = 0;
                    }
                    else if (val.trim().equalsIgnoreCase("round")) {
                        cap = 1;
                    }
                    else {
                        if (!val.trim().equalsIgnoreCase("square")) {
                            continue;
                        }
                        cap = 2;
                    }
                }
                else if (key.trim().equalsIgnoreCase("join")) {
                    if (val.trim().equalsIgnoreCase("bevel")) {
                        join = 2;
                    }
                    else if (val.trim().equalsIgnoreCase("miter")) {
                        join = 0;
                    }
                    else {
                        if (!val.trim().equalsIgnoreCase("round")) {
                            continue;
                        }
                        join = 1;
                    }
                }
                else {
                    if (!key.trim().equalsIgnoreCase("dash")) {
                        continue;
                    }
                    final StringTokenizer splitter = new StringTokenizer(val, ":");
                    dash = new float[splitter.countTokens()];
                    int counter = 0;
                    while (splitter.hasMoreTokens()) {
                        final String tokenVal = splitter.nextToken();
                        try {
                            dash[counter++] = Float.parseFloat(tokenVal);
                        }
                        catch (final NumberFormatException ex) {
                            ChartUtil.logger.log(Level.WARNING, "Exception in applying the value : " + ex, ex);
                        }
                    }
                }
            }
        }
        final BasicStroke stroke = new BasicStroke(width, cap, join, miterLimit, dash, dash_phase);
        return stroke;
    }
    
    public static String getPropValue(final ViewContext viewCtxt, final String graphType, final String propkey) {
        final Map ht = (Map)viewCtxt.getTransientState("CCCHART_PROPERTIESL");
        if (ht == null) {
            return null;
        }
        final Row propRow = ht.get(propkey);
        if (propRow != null) {
            final String gt = (String)propRow.get(4);
            if (gt.equals("ALL") || gt.equals(graphType)) {
                final int scope = propRow.get(6).hashCode();
                final String key = (String)propRow.get(5);
                if (scope == ChartConstants.STATIC) {
                    return key;
                }
                String value = null;
                if (scope == ChartConstants.STATE) {
                    value = (String)viewCtxt.getStateParameter(key);
                }
                else if (scope == ChartConstants.REQUEST) {
                    value = viewCtxt.getRequest().getParameter(key);
                    if (value == null) {
                        final Object temp = viewCtxt.getRequest().getAttribute(key);
                        if (temp != null) {
                            value = temp.toString();
                        }
                    }
                }
                else if (scope == ChartConstants.SESSION) {
                    value = (String)viewCtxt.getRequest().getSession().getAttribute(key);
                }
                if (value == null) {
                    value = (String)propRow.get(7);
                }
                return value;
            }
        }
        return null;
    }
    
    public static PdfPTable getTable(final TableModel tm, final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme pdfTheme, final String boxType) throws Exception {
        final int noOfColumn = tm.getColumnCount();
        final int noOfRow = tm.getRowCount();
        String rowType = "evenRow";
        final PdfPTable pTab = new PdfPTable(noOfColumn);
        for (int k = 0; k < noOfColumn; ++k) {
            final Object value = tm.getColumnName(k);
            final String strVal = (value == null) ? "NULL" : value.toString();
            final Chunk ch = new Chunk(strVal);
            final Element headerValue = pdfTheme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)ch, "tableHeader");
            PdfPCell pcell = new PdfPCell();
            if (headerValue instanceof Phrase) {
                pcell.setPhrase((Phrase)headerValue);
            }
            else if (headerValue instanceof Chunk) {
                final Phrase phrase = new Phrase((Chunk)headerValue);
                pcell.setPhrase(phrase);
            }
            else {
                pcell.addElement(headerValue);
            }
            pcell = (PdfPCell)pdfTheme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)pcell, "tableHeader");
            pTab.addCell(pcell);
        }
        for (int i = 0; i < noOfRow; ++i) {
            for (int j = 0; j < noOfColumn; ++j) {
                final Object value2 = tm.getValueAt(i, j);
                final String strVal2 = (value2 == null) ? "NULL" : value2.toString();
                final Chunk ch2 = new Chunk(strVal2);
                PdfPCell pcell = new PdfPCell(new Phrase(ch2));
                if (i % 2 == 0) {
                    rowType = "oddRow";
                }
                else {
                    rowType = "evenRow";
                }
                pcell = (PdfPCell)pdfTheme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)pcell, rowType);
                pTab.addCell(pcell);
            }
        }
        pTab.setSpacingAfter(0.0f);
        pTab.setSpacingBefore(0.0f);
        pTab.setWidthPercentage(100.0f);
        return pTab;
    }
    
    static {
        ChartUtil.logger = Logger.getLogger(ChartUtil.class.getName());
    }
    
    public static class GraphPoint implements Comparable
    {
        Object data;
        int pos;
        
        public GraphPoint(final Object dataArg, final int posArg) {
            this.data = dataArg;
            this.pos = posArg;
        }
        
        public int getPosition() {
            return this.pos;
        }
        
        @Override
        public int compareTo(final Object grpPoint) {
            return this.pos - ((GraphPoint)grpPoint).pos;
        }
        
        @Override
        public String toString() {
            return this.data.toString();
        }
    }
}
