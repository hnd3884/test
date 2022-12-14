package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.awt.Dimension;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherPropertyTypes;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Picture;

public class HSSFPicture extends HSSFSimpleShape implements Picture
{
    private static POILogger logger;
    public static final int PICTURE_TYPE_EMF = 2;
    public static final int PICTURE_TYPE_WMF = 3;
    public static final int PICTURE_TYPE_PICT = 4;
    public static final int PICTURE_TYPE_JPEG = 5;
    public static final int PICTURE_TYPE_PNG = 6;
    public static final int PICTURE_TYPE_DIB = 7;
    
    public HSSFPicture(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        super(spContainer, objRecord);
    }
    
    public HSSFPicture(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
        super.setShapeType(75);
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short)8);
    }
    
    public int getPictureIndex() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(EscherPropertyTypes.BLIP__BLIPTODISPLAY);
        if (null == property) {
            return -1;
        }
        return property.getPropertyValue();
    }
    
    public void setPictureIndex(final int pictureIndex) {
        this.setPropertyValue(new EscherSimpleProperty(EscherPropertyTypes.BLIP__BLIPTODISPLAY, false, true, pictureIndex));
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spContainer = super.createSpContainer();
        final EscherOptRecord opt = spContainer.getChildById(EscherOptRecord.RECORD_ID);
        opt.removeEscherProperty(EscherPropertyTypes.LINESTYLE__LINEDASHING);
        opt.removeEscherProperty(EscherPropertyTypes.LINESTYLE__NOLINEDRAWDASH);
        spContainer.removeChildRecord(spContainer.getChildById(EscherTextboxRecord.RECORD_ID));
        return spContainer;
    }
    
    @Override
    public void resize() {
        this.resize(Double.MAX_VALUE);
    }
    
    @Override
    public void resize(final double scale) {
        this.resize(scale, scale);
    }
    
    @Override
    public void resize(final double scaleX, final double scaleY) {
        final HSSFClientAnchor anchor = this.getClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
        final HSSFClientAnchor pref = this.getPreferredSize(scaleX, scaleY);
        final int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        final int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2((short)col2);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy2(pref.getDy2());
    }
    
    @Override
    public HSSFClientAnchor getPreferredSize() {
        return this.getPreferredSize(1.0);
    }
    
    public HSSFClientAnchor getPreferredSize(final double scale) {
        return this.getPreferredSize(scale, scale);
    }
    
    @Override
    public HSSFClientAnchor getPreferredSize(final double scaleX, final double scaleY) {
        ImageUtils.setPreferredSize(this, scaleX, scaleY);
        return this.getClientAnchor();
    }
    
    @Override
    public Dimension getImageDimension() {
        final InternalWorkbook iwb = this.getPatriarch().getSheet().getWorkbook().getWorkbook();
        final EscherBSERecord bse = iwb.getBSERecord(this.getPictureIndex());
        final byte[] data = bse.getBlipRecord().getPicturedata();
        final int type = bse.getBlipTypeWin32();
        return ImageUtils.getImageDimension(new ByteArrayInputStream(data), type);
    }
    
    @Override
    public HSSFPictureData getPictureData() {
        final int picIdx = this.getPictureIndex();
        if (picIdx == -1) {
            return null;
        }
        HSSFPatriarch patriarch = this.getPatriarch();
        for (HSSFShape parent = this.getParent(); patriarch == null && parent != null; patriarch = parent.getPatriarch(), parent = parent.getParent()) {}
        if (patriarch == null) {
            throw new IllegalStateException("Could not find a patriarch for a HSSPicture");
        }
        final InternalWorkbook iwb = patriarch.getSheet().getWorkbook().getWorkbook();
        final EscherBSERecord bse = iwb.getBSERecord(picIdx);
        final EscherBlipRecord blipRecord = bse.getBlipRecord();
        return new HSSFPictureData(blipRecord);
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        final EscherAggregate agg = patriarch.getBoundAggregate();
        agg.associateShapeToObjRecord(this.getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), this.getObjRecord());
        if (this.getPictureIndex() != -1) {
            final EscherBSERecord bse = patriarch.getSheet().getWorkbook().getWorkbook().getBSERecord(this.getPictureIndex());
            bse.setRef(bse.getRef() + 1);
        }
    }
    
    public String getFileName() {
        final EscherComplexProperty propFile = this.getOptRecord().lookup(EscherPropertyTypes.BLIP__BLIPFILENAME);
        return (null == propFile) ? "" : StringUtil.getFromUnicodeLE(propFile.getComplexData()).trim();
    }
    
    public void setFileName(final String data) {
        final byte[] bytes = StringUtil.getToUnicodeLE(data);
        final EscherComplexProperty prop = new EscherComplexProperty(EscherPropertyTypes.BLIP__BLIPFILENAME, true, bytes.length);
        prop.setComplexData(bytes);
        this.setPropertyValue(prop);
    }
    
    @Override
    public void setShapeType(final int shapeType) {
        throw new IllegalStateException("Shape type can not be changed in " + this.getClass().getSimpleName());
    }
    
    @Override
    protected HSSFShape cloneShape() {
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final byte[] inSp = this.getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        final ObjRecord obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        return new HSSFPicture(spContainer, obj);
    }
    
    @Override
    public HSSFClientAnchor getClientAnchor() {
        final HSSFAnchor a = this.getAnchor();
        return (a instanceof HSSFClientAnchor) ? ((HSSFClientAnchor)a) : null;
    }
    
    @Override
    public HSSFSheet getSheet() {
        return this.getPatriarch().getSheet();
    }
    
    static {
        HSSFPicture.logger = POILogFactory.getLogger(HSSFPicture.class);
    }
}
