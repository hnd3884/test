package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Sheet;
import java.util.Iterator;
import org.apache.poi.xssf.model.SingleXmlCells;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.util.ArrayList;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import java.util.List;
import org.w3c.dom.Node;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSchema;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.MapInfo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;

public class XSSFMap
{
    private CTMap ctMap;
    private MapInfo mapInfo;
    
    public XSSFMap(final CTMap ctMap, final MapInfo mapInfo) {
        this.ctMap = ctMap;
        this.mapInfo = mapInfo;
    }
    
    @Internal
    public CTMap getCtMap() {
        return this.ctMap;
    }
    
    @Internal
    public CTSchema getCTSchema() {
        final String schemaId = this.ctMap.getSchemaID();
        return this.mapInfo.getCTSchemaById(schemaId);
    }
    
    public Node getSchema() {
        Node xmlSchema = null;
        final CTSchema schema = this.getCTSchema();
        xmlSchema = schema.getDomNode().getFirstChild();
        return xmlSchema;
    }
    
    public List<XSSFSingleXmlCell> getRelatedSingleXMLCell() {
        final List<XSSFSingleXmlCell> relatedSimpleXmlCells = new ArrayList<XSSFSingleXmlCell>();
        for (int sheetNumber = this.mapInfo.getWorkbook().getNumberOfSheets(), i = 0; i < sheetNumber; ++i) {
            final XSSFSheet sheet = this.mapInfo.getWorkbook().getSheetAt(i);
            for (final POIXMLDocumentPart p : sheet.getRelations()) {
                if (p instanceof SingleXmlCells) {
                    final SingleXmlCells singleXMLCells = (SingleXmlCells)p;
                    for (final XSSFSingleXmlCell cell : singleXMLCells.getAllSimpleXmlCell()) {
                        if (cell.getMapId() == this.ctMap.getID()) {
                            relatedSimpleXmlCells.add(cell);
                        }
                    }
                }
            }
        }
        return relatedSimpleXmlCells;
    }
    
    public List<XSSFTable> getRelatedTables() {
        final List<XSSFTable> tables = new ArrayList<XSSFTable>();
        for (final Sheet sheet : this.mapInfo.getWorkbook()) {
            for (final POIXMLDocumentPart.RelationPart rp : ((XSSFSheet)sheet).getRelationParts()) {
                if (rp.getRelationship().getRelationshipType().equals(XSSFRelation.TABLE.getRelation())) {
                    final XSSFTable table = rp.getDocumentPart();
                    if (!table.mapsTo(this.ctMap.getID())) {
                        continue;
                    }
                    tables.add(table);
                }
            }
        }
        return tables;
    }
}
