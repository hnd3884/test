package org.apache.poi.xssf.binary;

import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import com.microsoft.schemas.vml.CTShape;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFComment;

@Internal
class XSSFBComment extends XSSFComment
{
    private final CellAddress cellAddress;
    private final String author;
    private final XSSFBRichTextString comment;
    private boolean visible;
    
    XSSFBComment(final CellAddress cellAddress, final String author, final String comment) {
        super(null, null, null);
        this.visible = true;
        this.cellAddress = cellAddress;
        this.author = author;
        this.comment = new XSSFBRichTextString(comment);
    }
    
    @Override
    public void setVisible(final boolean visible) {
        throw new IllegalArgumentException("XSSFBComment is read only.");
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    @Override
    public CellAddress getAddress() {
        return this.cellAddress;
    }
    
    @Override
    public void setAddress(final CellAddress addr) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public void setAddress(final int row, final int col) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public int getRow() {
        return this.cellAddress.getRow();
    }
    
    @Override
    public void setRow(final int row) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public int getColumn() {
        return this.cellAddress.getColumn();
    }
    
    @Override
    public void setColumn(final int col) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public String getAuthor() {
        return this.author;
    }
    
    @Override
    public void setAuthor(final String author) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public XSSFBRichTextString getString() {
        return this.comment;
    }
    
    @Override
    public void setString(final RichTextString string) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }
    
    @Override
    public ClientAnchor getClientAnchor() {
        return null;
    }
}
