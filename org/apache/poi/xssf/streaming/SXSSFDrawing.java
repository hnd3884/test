package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.usermodel.Picture;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.ss.usermodel.Drawing;

public class SXSSFDrawing implements Drawing<XSSFShape>
{
    private final SXSSFWorkbook _wb;
    private final XSSFDrawing _drawing;
    
    public SXSSFDrawing(final SXSSFWorkbook workbook, final XSSFDrawing drawing) {
        this._wb = workbook;
        this._drawing = drawing;
    }
    
    public SXSSFPicture createPicture(final ClientAnchor anchor, final int pictureIndex) {
        final XSSFPicture pict = this._drawing.createPicture(anchor, pictureIndex);
        return new SXSSFPicture(this._wb, pict);
    }
    
    public Comment createCellComment(final ClientAnchor anchor) {
        return (Comment)this._drawing.createCellComment(anchor);
    }
    
    public ClientAnchor createAnchor(final int dx1, final int dy1, final int dx2, final int dy2, final int col1, final int row1, final int col2, final int row2) {
        return (ClientAnchor)this._drawing.createAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
    }
    
    public ObjectData createObjectData(final ClientAnchor anchor, final int storageId, final int pictureIndex) {
        return (ObjectData)this._drawing.createObjectData(anchor, storageId, pictureIndex);
    }
    
    public Iterator<XSSFShape> iterator() {
        return this._drawing.getShapes().iterator();
    }
}
