package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleElement;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import java.util.ArrayList;
import java.util.EnumMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxfs;
import org.apache.poi.ss.usermodel.DifferentialStyleProvider;
import org.apache.poi.ss.usermodel.TableStyleType;
import java.util.Map;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.TableStyle;

public class XSSFTableStyle implements TableStyle
{
    private static final POILogger logger;
    private final String name;
    private final int index;
    private final Map<TableStyleType, DifferentialStyleProvider> elementMap;
    
    public XSSFTableStyle(final int index, final CTDxfs dxfs, final CTTableStyle tableStyle, final IndexedColorMap colorMap) {
        this.elementMap = new EnumMap<TableStyleType, DifferentialStyleProvider>(TableStyleType.class);
        this.name = tableStyle.getName();
        this.index = index;
        final List<CTDxf> dxfList = new ArrayList<CTDxf>();
        final XmlCursor cur = dxfs.newCursor();
        final String xquery = "declare namespace x='http://schemas.openxmlformats.org/spreadsheetml/2006/main' .//x:dxf | .//dxf";
        cur.selectPath(xquery);
        while (cur.toNextSelection()) {
            final XmlObject obj = cur.getObject();
            final String parentName = obj.getDomNode().getParentNode().getNodeName();
            if (!parentName.equals("mc:Fallback") && !parentName.equals("x:dxfs")) {
                if (!parentName.contentEquals("dxfs")) {
                    continue;
                }
            }
            try {
                CTDxf dxf;
                if (obj instanceof CTDxf) {
                    dxf = (CTDxf)obj;
                }
                else {
                    dxf = CTDxf.Factory.parse(obj.newXMLStreamReader(), new XmlOptions().setDocumentType(CTDxf.type));
                }
                if (dxf == null) {
                    continue;
                }
                dxfList.add(dxf);
            }
            catch (final XmlException e) {
                XSSFTableStyle.logger.log(5, new Object[] { "Error parsing XSSFTableStyle", e });
            }
        }
        for (final CTTableStyleElement element : tableStyle.getTableStyleElementList()) {
            final TableStyleType type = TableStyleType.valueOf(element.getType().toString());
            DifferentialStyleProvider dstyle = null;
            if (element.isSetDxfId()) {
                final int idx = (int)element.getDxfId();
                final CTDxf dxf2 = dxfList.get(idx);
                int stripeSize = 0;
                if (element.isSetSize()) {
                    stripeSize = (int)element.getSize();
                }
                if (dxf2 != null) {
                    dstyle = (DifferentialStyleProvider)new XSSFDxfStyleProvider(dxf2, stripeSize, colorMap);
                }
            }
            this.elementMap.put(type, dstyle);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public boolean isBuiltin() {
        return false;
    }
    
    public DifferentialStyleProvider getStyle(final TableStyleType type) {
        return this.elementMap.get(type);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)XSSFTableStyle.class);
    }
}
