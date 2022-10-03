package org.apache.poi.ooxml;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import org.apache.xmlbeans.XmlOptions;

public class POIXMLTypeLoader
{
    private static final String MS_OFFICE_URN = "urn:schemas-microsoft-com:office:office";
    private static final String MS_EXCEL_URN = "urn:schemas-microsoft-com:office:excel";
    private static final String MS_WORD_URN = "urn:schemas-microsoft-com:office:word";
    private static final String MS_VML_URN = "urn:schemas-microsoft-com:vml";
    public static final XmlOptions DEFAULT_XML_OPTIONS;
    
    static {
        (DEFAULT_XML_OPTIONS = new XmlOptions()).setSaveOuter();
        POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setUseDefaultNamespace();
        POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setSaveAggressiveNamespaces();
        POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setCharacterEncoding("UTF-8");
        POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setEntityExpansionLimit(1);
        final Map<String, String> map = new HashMap<String, String>();
        map.put("http://schemas.openxmlformats.org/drawingml/2006/main", "a");
        map.put("http://schemas.openxmlformats.org/drawingml/2006/chart", "c");
        map.put("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "wp");
        map.put("http://schemas.openxmlformats.org/markup-compatibility/2006", "ve");
        map.put("http://schemas.openxmlformats.org/officeDocument/2006/math", "m");
        map.put("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "r");
        map.put("http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes", "vt");
        map.put("http://schemas.openxmlformats.org/presentationml/2006/main", "p");
        map.put("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "w");
        map.put("http://schemas.microsoft.com/office/word/2006/wordml", "wne");
        map.put("urn:schemas-microsoft-com:office:office", "o");
        map.put("urn:schemas-microsoft-com:office:excel", "x");
        map.put("urn:schemas-microsoft-com:office:word", "w10");
        map.put("urn:schemas-microsoft-com:vml", "v");
        POIXMLTypeLoader.DEFAULT_XML_OPTIONS.setSaveSuggestedPrefixes((Map)Collections.unmodifiableMap((Map<?, ?>)map));
    }
}
