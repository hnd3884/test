package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.apache.poi.ss.util.CellAddress;
import com.microsoft.schemas.office.excel.CTClientData;
import java.math.BigInteger;
import org.apache.poi.ss.util.CellReference;
import com.microsoft.schemas.vml.CTShape;
import org.apache.poi.xssf.model.CommentsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.apache.poi.ss.usermodel.Comment;

public class XSSFComment implements Comment
{
    private final CTComment _comment;
    private final CommentsTable _comments;
    private final CTShape _vmlShape;
    private XSSFRichTextString _str;
    
    public XSSFComment(final CommentsTable comments, final CTComment comment, final CTShape vmlShape) {
        this._comment = comment;
        this._comments = comments;
        this._vmlShape = vmlShape;
        if (comment != null && vmlShape != null && vmlShape.sizeOfClientDataArray() > 0) {
            final CellReference ref = new CellReference(comment.getRef());
            final CTClientData clientData = vmlShape.getClientDataArray(0);
            clientData.setRowArray(0, new BigInteger(String.valueOf(ref.getRow())));
            clientData.setColumnArray(0, new BigInteger(String.valueOf(ref.getCol())));
            avoidXmlbeansCorruptPointer(vmlShape);
        }
    }
    
    public String getAuthor() {
        return this._comments.getAuthor(this._comment.getAuthorId());
    }
    
    public void setAuthor(final String author) {
        this._comment.setAuthorId((long)this._comments.findAuthor(author));
    }
    
    public int getColumn() {
        return this.getAddress().getColumn();
    }
    
    public int getRow() {
        return this.getAddress().getRow();
    }
    
    public boolean isVisible() {
        boolean visible = false;
        if (this._vmlShape != null) {
            final String style = this._vmlShape.getStyle();
            visible = (style != null && style.contains("visibility:visible"));
        }
        return visible;
    }
    
    public void setVisible(final boolean visible) {
        if (this._vmlShape != null) {
            String style;
            if (visible) {
                style = "position:absolute;visibility:visible";
            }
            else {
                style = "position:absolute;visibility:hidden";
            }
            this._vmlShape.setStyle(style);
        }
    }
    
    public CellAddress getAddress() {
        return new CellAddress(this._comment.getRef());
    }
    
    public void setAddress(final int row, final int col) {
        this.setAddress(new CellAddress(row, col));
    }
    
    public void setAddress(final CellAddress address) {
        final CellAddress oldRef = new CellAddress(this._comment.getRef());
        if (address.equals((Object)oldRef)) {
            return;
        }
        this._comment.setRef(address.formatAsString());
        this._comments.referenceUpdated(oldRef, this._comment);
        if (this._vmlShape != null) {
            final CTClientData clientData = this._vmlShape.getClientDataArray(0);
            clientData.setRowArray(0, new BigInteger(String.valueOf(address.getRow())));
            clientData.setColumnArray(0, new BigInteger(String.valueOf(address.getColumn())));
            avoidXmlbeansCorruptPointer(this._vmlShape);
        }
    }
    
    public void setColumn(final int col) {
        this.setAddress(this.getRow(), col);
    }
    
    public void setRow(final int row) {
        this.setAddress(row, this.getColumn());
    }
    
    public XSSFRichTextString getString() {
        if (this._str == null) {
            final CTRst rst = this._comment.getText();
            if (rst != null) {
                this._str = new XSSFRichTextString(this._comment.getText());
            }
        }
        return this._str;
    }
    
    public void setString(final RichTextString string) {
        if (!(string instanceof XSSFRichTextString)) {
            throw new IllegalArgumentException("Only XSSFRichTextString argument is supported");
        }
        this._str = (XSSFRichTextString)string;
        this._comment.setText(this._str.getCTRst());
    }
    
    public void setString(final String string) {
        this.setString((RichTextString)new XSSFRichTextString(string));
    }
    
    public ClientAnchor getClientAnchor() {
        if (this._vmlShape == null) {
            return null;
        }
        final String position = this._vmlShape.getClientDataArray(0).getAnchorArray(0);
        final int[] pos = new int[8];
        int i = 0;
        for (final String s : position.split(",")) {
            pos[i++] = Integer.parseInt(s.trim());
        }
        return (ClientAnchor)new XSSFClientAnchor(pos[1] * 9525, pos[3] * 9525, pos[5] * 9525, pos[7] * 9525, pos[0], pos[2], pos[4], pos[6]);
    }
    
    protected CTComment getCTComment() {
        return this._comment;
    }
    
    protected CTShape getCTShape() {
        return this._vmlShape;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof XSSFComment)) {
            return false;
        }
        final XSSFComment other = (XSSFComment)obj;
        return this.getCTComment() == other.getCTComment() && this.getCTShape() == other.getCTShape();
    }
    
    @Override
    public int hashCode() {
        return (this.getRow() * 17 + this.getColumn()) * 31;
    }
    
    private static void avoidXmlbeansCorruptPointer(final CTShape vmlShape) {
        vmlShape.getClientDataList().toString();
    }
}
