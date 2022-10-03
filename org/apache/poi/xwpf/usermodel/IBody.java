package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public interface IBody
{
    POIXMLDocumentPart getPart();
    
    BodyType getPartType();
    
    List<IBodyElement> getBodyElements();
    
    List<XWPFParagraph> getParagraphs();
    
    List<XWPFTable> getTables();
    
    XWPFParagraph getParagraph(final CTP p0);
    
    XWPFTable getTable(final CTTbl p0);
    
    XWPFParagraph getParagraphArray(final int p0);
    
    XWPFTable getTableArray(final int p0);
    
    XWPFParagraph insertNewParagraph(final XmlCursor p0);
    
    XWPFTable insertNewTbl(final XmlCursor p0);
    
    void insertTable(final int p0, final XWPFTable p1);
    
    XWPFTableCell getTableCell(final CTTc p0);
    
    XWPFDocument getXWPFDocument();
}
