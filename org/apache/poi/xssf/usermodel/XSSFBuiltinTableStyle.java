package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.DifferentialStyleProvider;
import org.apache.poi.ss.usermodel.TableStyleType;
import java.util.EnumMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.poi.util.XMLHelper;
import java.io.StringWriter;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xssf.model.StylesTable;
import org.w3c.dom.Element;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.ss.usermodel.TableStyle;
import java.util.Map;

public enum XSSFBuiltinTableStyle
{
    TableStyleDark1, 
    TableStyleDark2, 
    TableStyleDark3, 
    TableStyleDark4, 
    TableStyleDark5, 
    TableStyleDark6, 
    TableStyleDark7, 
    TableStyleDark8, 
    TableStyleDark9, 
    TableStyleDark10, 
    TableStyleDark11, 
    TableStyleLight1, 
    TableStyleLight2, 
    TableStyleLight3, 
    TableStyleLight4, 
    TableStyleLight5, 
    TableStyleLight6, 
    TableStyleLight7, 
    TableStyleLight8, 
    TableStyleLight9, 
    TableStyleLight10, 
    TableStyleLight11, 
    TableStyleLight12, 
    TableStyleLight13, 
    TableStyleLight14, 
    TableStyleLight15, 
    TableStyleLight16, 
    TableStyleLight17, 
    TableStyleLight18, 
    TableStyleLight19, 
    TableStyleLight20, 
    TableStyleLight21, 
    TableStyleMedium1, 
    TableStyleMedium2, 
    TableStyleMedium3, 
    TableStyleMedium4, 
    TableStyleMedium5, 
    TableStyleMedium6, 
    TableStyleMedium7, 
    TableStyleMedium8, 
    TableStyleMedium9, 
    TableStyleMedium10, 
    TableStyleMedium11, 
    TableStyleMedium12, 
    TableStyleMedium13, 
    TableStyleMedium14, 
    TableStyleMedium15, 
    TableStyleMedium16, 
    TableStyleMedium17, 
    TableStyleMedium18, 
    TableStyleMedium19, 
    TableStyleMedium20, 
    TableStyleMedium21, 
    TableStyleMedium22, 
    TableStyleMedium23, 
    TableStyleMedium24, 
    TableStyleMedium25, 
    TableStyleMedium26, 
    TableStyleMedium27, 
    TableStyleMedium28, 
    PivotStyleMedium1, 
    PivotStyleMedium2, 
    PivotStyleMedium3, 
    PivotStyleMedium4, 
    PivotStyleMedium5, 
    PivotStyleMedium6, 
    PivotStyleMedium7, 
    PivotStyleMedium8, 
    PivotStyleMedium9, 
    PivotStyleMedium10, 
    PivotStyleMedium11, 
    PivotStyleMedium12, 
    PivotStyleMedium13, 
    PivotStyleMedium14, 
    PivotStyleMedium15, 
    PivotStyleMedium16, 
    PivotStyleMedium17, 
    PivotStyleMedium18, 
    PivotStyleMedium19, 
    PivotStyleMedium20, 
    PivotStyleMedium21, 
    PivotStyleMedium22, 
    PivotStyleMedium23, 
    PivotStyleMedium24, 
    PivotStyleMedium25, 
    PivotStyleMedium26, 
    PivotStyleMedium27, 
    PivotStyleMedium28, 
    PivotStyleLight1, 
    PivotStyleLight2, 
    PivotStyleLight3, 
    PivotStyleLight4, 
    PivotStyleLight5, 
    PivotStyleLight6, 
    PivotStyleLight7, 
    PivotStyleLight8, 
    PivotStyleLight9, 
    PivotStyleLight10, 
    PivotStyleLight11, 
    PivotStyleLight12, 
    PivotStyleLight13, 
    PivotStyleLight14, 
    PivotStyleLight15, 
    PivotStyleLight16, 
    PivotStyleLight17, 
    PivotStyleLight18, 
    PivotStyleLight19, 
    PivotStyleLight20, 
    PivotStyleLight21, 
    PivotStyleLight22, 
    PivotStyleLight23, 
    PivotStyleLight24, 
    PivotStyleLight25, 
    PivotStyleLight26, 
    PivotStyleLight27, 
    PivotStyleLight28, 
    PivotStyleDark1, 
    PivotStyleDark2, 
    PivotStyleDark3, 
    PivotStyleDark4, 
    PivotStyleDark5, 
    PivotStyleDark6, 
    PivotStyleDark7, 
    PivotStyleDark8, 
    PivotStyleDark9, 
    PivotStyleDark10, 
    PivotStyleDark11, 
    PivotStyleDark12, 
    PivotStyleDark13, 
    PivotStyleDark14, 
    PivotStyleDark15, 
    PivotStyleDark16, 
    PivotStyleDark17, 
    PivotStyleDark18, 
    PivotStyleDark19, 
    PivotStyleDark20, 
    PivotStyleDark21, 
    PivotStyleDark22, 
    PivotStyleDark23, 
    PivotStyleDark24, 
    PivotStyleDark25, 
    PivotStyleDark26, 
    PivotStyleDark27, 
    PivotStyleDark28;
    
    private static final Map<XSSFBuiltinTableStyle, TableStyle> styleMap;
    
    public TableStyle getStyle() {
        init();
        return XSSFBuiltinTableStyle.styleMap.get(this);
    }
    
    public static boolean isBuiltinStyle(final TableStyle style) {
        if (style == null) {
            return false;
        }
        try {
            valueOf(style.getName());
            return true;
        }
        catch (final IllegalArgumentException e) {
            return false;
        }
    }
    
    public static synchronized void init() {
        if (!XSSFBuiltinTableStyle.styleMap.isEmpty()) {
            return;
        }
        try {
            final InputStream is = XSSFBuiltinTableStyle.class.getResourceAsStream("presetTableStyles.xml");
            try {
                final Document doc = DocumentHelper.readDocument(is);
                final NodeList styleNodes = doc.getDocumentElement().getChildNodes();
                for (int i = 0; i < styleNodes.getLength(); ++i) {
                    final Node node = styleNodes.item(i);
                    if (node.getNodeType() == 1) {
                        final Element tag = (Element)node;
                        final String styleName = tag.getTagName();
                        final XSSFBuiltinTableStyle builtIn = valueOf(styleName);
                        final Node dxfsNode = tag.getElementsByTagName("dxfs").item(0);
                        final Node tableStyleNode = tag.getElementsByTagName("tableStyles").item(0);
                        final StylesTable styles = new StylesTable();
                        styles.readFrom(new ByteArrayInputStream(styleXML(dxfsNode, tableStyleNode).getBytes(StandardCharsets.UTF_8)));
                        XSSFBuiltinTableStyle.styleMap.put(builtIn, (TableStyle)new XSSFBuiltinTypeStyleStyle(builtIn, styles.getExplicitTableStyle(styleName)));
                    }
                }
            }
            finally {
                IOUtils.closeQuietly((Closeable)is);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String styleXML(final Node dxfsNode, final Node tableStyleNode) throws IOException, TransformerException {
        dxfsNode.insertBefore(dxfsNode.getOwnerDocument().createElement("dxf"), dxfsNode.getFirstChild());
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n").append("<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" ").append("xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" ").append("xmlns:x14ac=\"http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac\" ").append("xmlns:x16r2=\"http://schemas.microsoft.com/office/spreadsheetml/2015/02/main\" ").append("mc:Ignorable=\"x14ac x16r2\">\n");
        sb.append(writeToString(dxfsNode));
        sb.append(writeToString(tableStyleNode));
        sb.append("</styleSheet>");
        return sb.toString();
    }
    
    private static String writeToString(final Node node) throws IOException, TransformerException {
        try (final StringWriter sw = new StringWriter()) {
            final Transformer transformer = XMLHelper.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(new DOMSource(node), new StreamResult(sw));
            return sw.toString();
        }
    }
    
    static {
        styleMap = new EnumMap<XSSFBuiltinTableStyle, TableStyle>(XSSFBuiltinTableStyle.class);
    }
    
    protected static class XSSFBuiltinTypeStyleStyle implements TableStyle
    {
        private final XSSFBuiltinTableStyle builtIn;
        private final TableStyle style;
        
        protected XSSFBuiltinTypeStyleStyle(final XSSFBuiltinTableStyle builtIn, final TableStyle style) {
            this.builtIn = builtIn;
            this.style = style;
        }
        
        public String getName() {
            return this.style.getName();
        }
        
        public int getIndex() {
            return this.builtIn.ordinal();
        }
        
        public boolean isBuiltin() {
            return true;
        }
        
        public DifferentialStyleProvider getStyle(final TableStyleType type) {
            return this.style.getStyle(type);
        }
    }
}
