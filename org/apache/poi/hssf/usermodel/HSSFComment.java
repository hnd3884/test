package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.RichTextString;
import java.util.Objects;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.NoteStructureSubRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.ss.usermodel.Comment;

public class HSSFComment extends HSSFTextbox implements Comment
{
    private static final int FILL_TYPE_SOLID = 0;
    private static final int FILL_TYPE_PICTURE = 3;
    private static final int GROUP_SHAPE_PROPERTY_DEFAULT_VALUE = 655362;
    private static final int GROUP_SHAPE_HIDDEN_MASK = 16777218;
    private static final int GROUP_SHAPE_NOT_HIDDEN_MASK = -16777219;
    private final NoteRecord _note;
    
    public HSSFComment(final EscherContainerRecord spContainer, final ObjRecord objRecord, final TextObjectRecord textObjectRecord, final NoteRecord note) {
        super(spContainer, objRecord, textObjectRecord);
        this._note = note;
    }
    
    public HSSFComment(final HSSFShape parent, final HSSFAnchor anchor) {
        this(parent, anchor, createNoteRecord());
    }
    
    private HSSFComment(final HSSFShape parent, final HSSFAnchor anchor, final NoteRecord note) {
        super(parent, anchor);
        this._note = note;
        this.setFillColor(134217808);
        this.setVisible(false);
        this.setAuthor("");
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short)25);
    }
    
    protected HSSFComment(final NoteRecord note, final TextObjectRecord txo) {
        this(null, new HSSFClientAnchor(), note);
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        super.afterInsert(patriarch);
        patriarch.getBoundAggregate().addTailRecord(this.getNoteRecord());
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spContainer = super.createSpContainer();
        final EscherOptRecord opt = spContainer.getChildById(EscherOptRecord.RECORD_ID);
        opt.removeEscherProperty(EscherPropertyTypes.TEXT__TEXTLEFT);
        opt.removeEscherProperty(EscherPropertyTypes.TEXT__TEXTRIGHT);
        opt.removeEscherProperty(EscherPropertyTypes.TEXT__TEXTTOP);
        opt.removeEscherProperty(EscherPropertyTypes.TEXT__TEXTBOTTOM);
        opt.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, false, false, 655362));
        return spContainer;
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setObjectType((short)202);
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(false);
        c.setAutoline(true);
        final NoteStructureSubRecord u = new NoteStructureSubRecord();
        final EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(u);
        obj.addSubRecord(e);
        return obj;
    }
    
    private static NoteRecord createNoteRecord() {
        final NoteRecord note = new NoteRecord();
        note.setFlags((short)0);
        note.setAuthor("");
        return note;
    }
    
    @Override
    void setShapeId(final int shapeId) {
        if (shapeId > 65535) {
            throw new IllegalArgumentException("Cannot add more than 65535 shapes");
        }
        super.setShapeId(shapeId);
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectId(shapeId);
        this._note.setShapeId(shapeId);
    }
    
    @Override
    public void setVisible(final boolean visible) {
        this._note.setFlags((short)(visible ? 2 : 0));
        this.setHidden(!visible);
    }
    
    @Override
    public boolean isVisible() {
        return this._note.getFlags() == 2;
    }
    
    @Override
    public CellAddress getAddress() {
        return new CellAddress(this.getRow(), this.getColumn());
    }
    
    @Override
    public void setAddress(final CellAddress address) {
        this.setRow(address.getRow());
        this.setColumn(address.getColumn());
    }
    
    @Override
    public void setAddress(final int row, final int col) {
        this.setRow(row);
        this.setColumn(col);
    }
    
    @Override
    public int getRow() {
        return this._note.getRow();
    }
    
    @Override
    public void setRow(final int row) {
        this._note.setRow(row);
    }
    
    @Override
    public int getColumn() {
        return this._note.getColumn();
    }
    
    @Override
    public void setColumn(final int col) {
        this._note.setColumn(col);
    }
    
    @Override
    public String getAuthor() {
        return this._note.getAuthor();
    }
    
    @Override
    public void setAuthor(final String author) {
        if (this._note != null) {
            this._note.setAuthor(author);
        }
    }
    
    protected NoteRecord getNoteRecord() {
        return this._note;
    }
    
    public boolean hasPosition() {
        return this._note != null && this.getColumn() >= 0 && this.getRow() >= 0;
    }
    
    @Override
    public ClientAnchor getClientAnchor() {
        final HSSFAnchor ha = super.getAnchor();
        if (ha instanceof ClientAnchor) {
            return (ClientAnchor)ha;
        }
        throw new IllegalStateException("Anchor can not be changed in " + ClientAnchor.class.getSimpleName());
    }
    
    @Override
    public void setShapeType(final int shapeType) {
        throw new IllegalStateException("Shape type can not be changed in " + this.getClass().getSimpleName());
    }
    
    public void afterRemove(final HSSFPatriarch patriarch) {
        super.afterRemove(patriarch);
        patriarch.getBoundAggregate().removeTailRecord(this.getNoteRecord());
    }
    
    @Override
    protected HSSFShape cloneShape() {
        final TextObjectRecord txo = (TextObjectRecord)this.getTextObjectRecord().cloneViaReserialise();
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final byte[] inSp = this.getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        final ObjRecord obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        final NoteRecord note = (NoteRecord)this.getNoteRecord().cloneViaReserialise();
        return new HSSFComment(spContainer, obj, txo, note);
    }
    
    public void setBackgroundImage(final int pictureIndex) {
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.FILL__PATTERNTEXTURE, false, true, pictureIndex));
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.FILL__FILLTYPE, false, false, 3));
        final EscherBSERecord bse = this.getPatriarch().getSheet().getWorkbook().getWorkbook().getBSERecord(pictureIndex);
        bse.setRef(bse.getRef() + 1);
    }
    
    public void resetBackgroundImage() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.FILL__PATTERNTEXTURE);
        if (null != property) {
            final EscherBSERecord bse = this.getPatriarch().getSheet().getWorkbook().getWorkbook().getBSERecord(property.getPropertyValue());
            bse.setRef(bse.getRef() - 1);
            this.getOptRecord().removeEscherProperty(EscherPropertyTypes.FILL__PATTERNTEXTURE);
        }
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.FILL__FILLTYPE, false, false, 0));
    }
    
    public int getBackgroundImageId() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.FILL__PATTERNTEXTURE);
        return (property == null) ? 0 : property.getPropertyValue();
    }
    
    private void setHidden(final boolean value) {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.GROUPSHAPE__FLAGS);
        if (value) {
            this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, false, false, property.getPropertyValue() | 0x1000002));
        }
        else {
            this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, false, false, property.getPropertyValue() & 0xFEFFFFFD));
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof HSSFComment)) {
            return false;
        }
        final HSSFComment other = (HSSFComment)obj;
        return this.getNoteRecord().equals(other.getNoteRecord());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.getRow(), this.getColumn());
    }
}
