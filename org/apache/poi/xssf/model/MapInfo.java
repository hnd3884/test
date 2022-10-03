package org.apache.poi.xssf.model;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import org.apache.xmlbeans.XmlException;
import java.util.HashMap;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.MapInfoDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFMap;
import java.util.Map;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMapInfo;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class MapInfo extends POIXMLDocumentPart
{
    private CTMapInfo mapInfo;
    private Map<Integer, XSSFMap> maps;
    
    public MapInfo() {
        this.mapInfo = CTMapInfo.Factory.newInstance();
    }
    
    public MapInfo(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final MapInfoDocument doc = MapInfoDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.mapInfo = doc.getMapInfo();
            this.maps = new HashMap<Integer, XSSFMap>();
            for (final CTMap map : this.mapInfo.getMapArray()) {
                this.maps.put((int)map.getID(), new XSSFMap(map, this));
            }
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public XSSFWorkbook getWorkbook() {
        return (XSSFWorkbook)this.getParent();
    }
    
    public CTMapInfo getCTMapInfo() {
        return this.mapInfo;
    }
    
    public CTSchema getCTSchemaById(final String schemaId) {
        CTSchema xmlSchema = null;
        for (final CTSchema schema : this.mapInfo.getSchemaArray()) {
            if (schema.getID().equals(schemaId)) {
                xmlSchema = schema;
                break;
            }
        }
        return xmlSchema;
    }
    
    public XSSFMap getXSSFMapById(final int id) {
        return this.maps.get(id);
    }
    
    public XSSFMap getXSSFMapByName(final String name) {
        XSSFMap matchedMap = null;
        for (final XSSFMap map : this.maps.values()) {
            if (map.getCtMap().getName() != null && map.getCtMap().getName().equals(name)) {
                matchedMap = map;
            }
        }
        return matchedMap;
    }
    
    public Collection<XSSFMap> getAllXSSFMaps() {
        return this.maps.values();
    }
    
    protected void writeTo(final OutputStream out) throws IOException {
        final MapInfoDocument doc = MapInfoDocument.Factory.newInstance();
        doc.setMapInfo(this.mapInfo);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
}
