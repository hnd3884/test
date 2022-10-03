package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.ddf.EscherShapePathProperty;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.ss.usermodel.SimpleShape;

public class HSSFSimpleShape extends HSSFShape implements SimpleShape
{
    public static final short OBJECT_TYPE_LINE = 20;
    public static final short OBJECT_TYPE_RECTANGLE = 1;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_ARC = 19;
    public static final short OBJECT_TYPE_PICTURE = 75;
    public static final short OBJECT_TYPE_COMBO_BOX = 201;
    public static final short OBJECT_TYPE_COMMENT = 202;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    public static final int WRAP_SQUARE = 0;
    public static final int WRAP_BY_POINTS = 1;
    public static final int WRAP_NONE = 2;
    private TextObjectRecord _textObjectRecord;
    
    public HSSFSimpleShape(final EscherContainerRecord spContainer, final ObjRecord objRecord, final TextObjectRecord textObjectRecord) {
        super(spContainer, objRecord);
        this._textObjectRecord = textObjectRecord;
    }
    
    public HSSFSimpleShape(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        super(spContainer, objRecord);
    }
    
    public HSSFSimpleShape(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
        this._textObjectRecord = this.createTextObjRecord();
    }
    
    protected TextObjectRecord getTextObjectRecord() {
        return this._textObjectRecord;
    }
    
    protected TextObjectRecord createTextObjRecord() {
        final TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(2);
        obj.setVerticalTextAlignment(2);
        obj.setTextLocked(true);
        obj.setTextOrientation(0);
        obj.setStr(new HSSFRichTextString(""));
        return obj;
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer.setOptions((short)15);
        final EscherSpRecord sp = new EscherSpRecord();
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setFlags(2560);
        sp.setVersion((short)2);
        final EscherClientDataRecord clientData = new EscherClientDataRecord();
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions((short)0);
        final EscherOptRecord optRecord = new EscherOptRecord();
        optRecord.setEscherProperty(new EscherSimpleProperty(EscherPropertyTypes.LINESTYLE__LINEDASHING, 0));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524296));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherPropertyTypes.FILL__FILLCOLOR, 134217737));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherPropertyTypes.LINESTYLE__COLOR, 134217792));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.FILL__NOFILLHITTEST, 65536));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH, 524296));
        optRecord.setEscherProperty(new EscherShapePathProperty(EscherPropertyTypes.GEOMETRY__SHAPEPATH, 4));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherPropertyTypes.GROUPSHAPE__FLAGS, 524288));
        optRecord.setRecordId(EscherOptRecord.RECORD_ID);
        final EscherTextboxRecord escherTextbox = new EscherTextboxRecord();
        escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        escherTextbox.setOptions((short)0);
        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(optRecord);
        spContainer.addChildRecord(this.getAnchor().getEscherAnchor());
        spContainer.addChildRecord(clientData);
        spContainer.addChildRecord(escherTextbox);
        return spContainer;
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        final EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }
    
    @Override
    protected void afterRemove(final HSSFPatriarch patriarch) {
        patriarch.getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID));
        if (null != this.getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID)) {
            patriarch.getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID));
        }
    }
    
    public HSSFRichTextString getString() {
        return this._textObjectRecord.getStr();
    }
    
    public void setString(final RichTextString string) {
        if (this.getShapeType() == 0 || this.getShapeType() == 20) {
            throw new IllegalStateException("Cannot set text for shape type: " + this.getShapeType());
        }
        final HSSFRichTextString rtr = (HSSFRichTextString)string;
        if (rtr.numFormattingRuns() == 0) {
            rtr.applyFont((short)0);
        }
        final TextObjectRecord txo = this.getOrCreateTextObjRecord();
        txo.setStr(rtr);
        if (string.getString() != null) {
            this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.TEXT__TEXTID, string.getString().hashCode()));
        }
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        final EscherAggregate agg = patriarch.getBoundAggregate();
        agg.associateShapeToObjRecord(this.getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), this.getObjRecord());
        if (null != this.getTextObjectRecord()) {
            agg.associateShapeToObjRecord(this.getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID), this.getTextObjectRecord());
        }
    }
    
    @Override
    protected HSSFShape cloneShape() {
        TextObjectRecord txo = null;
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final byte[] inSp = this.getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        final ObjRecord obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        if (this.getTextObjectRecord() != null && this.getString() != null && null != this.getString().getString()) {
            txo = (TextObjectRecord)this.getTextObjectRecord().cloneViaReserialise();
        }
        return new HSSFSimpleShape(spContainer, obj, txo);
    }
    
    public int getShapeType() {
        final EscherSpRecord spRecord = this.getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        return spRecord.getShapeType();
    }
    
    public int getWrapText() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.TEXT__WRAPTEXT);
        return (null == property) ? 0 : property.getPropertyValue();
    }
    
    public void setWrapText(final int value) {
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.TEXT__WRAPTEXT, false, false, value));
    }
    
    public void setShapeType(final int value) {
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short)30);
        final EscherSpRecord spRecord = this.getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        spRecord.setShapeType((short)value);
    }
    
    private TextObjectRecord getOrCreateTextObjRecord() {
        if (this.getTextObjectRecord() == null) {
            this._textObjectRecord = this.createTextObjRecord();
        }
        EscherTextboxRecord escherTextbox = this.getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID);
        if (null == escherTextbox) {
            escherTextbox = new EscherTextboxRecord();
            escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
            escherTextbox.setOptions((short)0);
            this.getEscherContainer().addChildRecord(escherTextbox);
            this.getPatriarch().getBoundAggregate().associateShapeToObjRecord(escherTextbox, this._textObjectRecord);
        }
        return this._textObjectRecord;
    }
    
    @Override
    public int getShapeId() {
        return super.getShapeId();
    }
}
