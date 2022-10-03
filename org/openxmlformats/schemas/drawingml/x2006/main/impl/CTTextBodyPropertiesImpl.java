package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextColumnCount;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVertOverflowType;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.STAngle;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFlatText;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShape3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextShapeAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNoAutofit;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetTextShape;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextBodyPropertiesImpl extends XmlComplexContentImpl implements CTTextBodyProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName PRSTTXWARP$0;
    private static final QName NOAUTOFIT$2;
    private static final QName NORMAUTOFIT$4;
    private static final QName SPAUTOFIT$6;
    private static final QName SCENE3D$8;
    private static final QName SP3D$10;
    private static final QName FLATTX$12;
    private static final QName EXTLST$14;
    private static final QName ROT$16;
    private static final QName SPCFIRSTLASTPARA$18;
    private static final QName VERTOVERFLOW$20;
    private static final QName HORZOVERFLOW$22;
    private static final QName VERT$24;
    private static final QName WRAP$26;
    private static final QName LINS$28;
    private static final QName TINS$30;
    private static final QName RINS$32;
    private static final QName BINS$34;
    private static final QName NUMCOL$36;
    private static final QName SPCCOL$38;
    private static final QName RTLCOL$40;
    private static final QName FROMWORDART$42;
    private static final QName ANCHOR$44;
    private static final QName ANCHORCTR$46;
    private static final QName FORCEAA$48;
    private static final QName UPRIGHT$50;
    private static final QName COMPATLNSPC$52;
    
    public CTTextBodyPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPresetTextShape getPrstTxWarp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetTextShape ctPresetTextShape = (CTPresetTextShape)this.get_store().find_element_user(CTTextBodyPropertiesImpl.PRSTTXWARP$0, 0);
            if (ctPresetTextShape == null) {
                return null;
            }
            return ctPresetTextShape;
        }
    }
    
    public boolean isSetPrstTxWarp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.PRSTTXWARP$0) != 0;
        }
    }
    
    public void setPrstTxWarp(final CTPresetTextShape ctPresetTextShape) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetTextShape, CTTextBodyPropertiesImpl.PRSTTXWARP$0, 0, (short)1);
    }
    
    public CTPresetTextShape addNewPrstTxWarp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetTextShape)this.get_store().add_element_user(CTTextBodyPropertiesImpl.PRSTTXWARP$0);
        }
    }
    
    public void unsetPrstTxWarp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.PRSTTXWARP$0, 0);
        }
    }
    
    public CTTextNoAutofit getNoAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextNoAutofit ctTextNoAutofit = (CTTextNoAutofit)this.get_store().find_element_user(CTTextBodyPropertiesImpl.NOAUTOFIT$2, 0);
            if (ctTextNoAutofit == null) {
                return null;
            }
            return ctTextNoAutofit;
        }
    }
    
    public boolean isSetNoAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.NOAUTOFIT$2) != 0;
        }
    }
    
    public void setNoAutofit(final CTTextNoAutofit ctTextNoAutofit) {
        this.generatedSetterHelperImpl((XmlObject)ctTextNoAutofit, CTTextBodyPropertiesImpl.NOAUTOFIT$2, 0, (short)1);
    }
    
    public CTTextNoAutofit addNewNoAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextNoAutofit)this.get_store().add_element_user(CTTextBodyPropertiesImpl.NOAUTOFIT$2);
        }
    }
    
    public void unsetNoAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.NOAUTOFIT$2, 0);
        }
    }
    
    public CTTextNormalAutofit getNormAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextNormalAutofit ctTextNormalAutofit = (CTTextNormalAutofit)this.get_store().find_element_user(CTTextBodyPropertiesImpl.NORMAUTOFIT$4, 0);
            if (ctTextNormalAutofit == null) {
                return null;
            }
            return ctTextNormalAutofit;
        }
    }
    
    public boolean isSetNormAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.NORMAUTOFIT$4) != 0;
        }
    }
    
    public void setNormAutofit(final CTTextNormalAutofit ctTextNormalAutofit) {
        this.generatedSetterHelperImpl((XmlObject)ctTextNormalAutofit, CTTextBodyPropertiesImpl.NORMAUTOFIT$4, 0, (short)1);
    }
    
    public CTTextNormalAutofit addNewNormAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextNormalAutofit)this.get_store().add_element_user(CTTextBodyPropertiesImpl.NORMAUTOFIT$4);
        }
    }
    
    public void unsetNormAutofit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.NORMAUTOFIT$4, 0);
        }
    }
    
    public CTTextShapeAutofit getSpAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextShapeAutofit ctTextShapeAutofit = (CTTextShapeAutofit)this.get_store().find_element_user(CTTextBodyPropertiesImpl.SPAUTOFIT$6, 0);
            if (ctTextShapeAutofit == null) {
                return null;
            }
            return ctTextShapeAutofit;
        }
    }
    
    public boolean isSetSpAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.SPAUTOFIT$6) != 0;
        }
    }
    
    public void setSpAutoFit(final CTTextShapeAutofit ctTextShapeAutofit) {
        this.generatedSetterHelperImpl((XmlObject)ctTextShapeAutofit, CTTextBodyPropertiesImpl.SPAUTOFIT$6, 0, (short)1);
    }
    
    public CTTextShapeAutofit addNewSpAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextShapeAutofit)this.get_store().add_element_user(CTTextBodyPropertiesImpl.SPAUTOFIT$6);
        }
    }
    
    public void unsetSpAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.SPAUTOFIT$6, 0);
        }
    }
    
    public CTScene3D getScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScene3D ctScene3D = (CTScene3D)this.get_store().find_element_user(CTTextBodyPropertiesImpl.SCENE3D$8, 0);
            if (ctScene3D == null) {
                return null;
            }
            return ctScene3D;
        }
    }
    
    public boolean isSetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.SCENE3D$8) != 0;
        }
    }
    
    public void setScene3D(final CTScene3D ctScene3D) {
        this.generatedSetterHelperImpl((XmlObject)ctScene3D, CTTextBodyPropertiesImpl.SCENE3D$8, 0, (short)1);
    }
    
    public CTScene3D addNewScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScene3D)this.get_store().add_element_user(CTTextBodyPropertiesImpl.SCENE3D$8);
        }
    }
    
    public void unsetScene3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.SCENE3D$8, 0);
        }
    }
    
    public CTShape3D getSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape3D ctShape3D = (CTShape3D)this.get_store().find_element_user(CTTextBodyPropertiesImpl.SP3D$10, 0);
            if (ctShape3D == null) {
                return null;
            }
            return ctShape3D;
        }
    }
    
    public boolean isSetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.SP3D$10) != 0;
        }
    }
    
    public void setSp3D(final CTShape3D ctShape3D) {
        this.generatedSetterHelperImpl((XmlObject)ctShape3D, CTTextBodyPropertiesImpl.SP3D$10, 0, (short)1);
    }
    
    public CTShape3D addNewSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape3D)this.get_store().add_element_user(CTTextBodyPropertiesImpl.SP3D$10);
        }
    }
    
    public void unsetSp3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.SP3D$10, 0);
        }
    }
    
    public CTFlatText getFlatTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFlatText ctFlatText = (CTFlatText)this.get_store().find_element_user(CTTextBodyPropertiesImpl.FLATTX$12, 0);
            if (ctFlatText == null) {
                return null;
            }
            return ctFlatText;
        }
    }
    
    public boolean isSetFlatTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.FLATTX$12) != 0;
        }
    }
    
    public void setFlatTx(final CTFlatText ctFlatText) {
        this.generatedSetterHelperImpl((XmlObject)ctFlatText, CTTextBodyPropertiesImpl.FLATTX$12, 0, (short)1);
    }
    
    public CTFlatText addNewFlatTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFlatText)this.get_store().add_element_user(CTTextBodyPropertiesImpl.FLATTX$12);
        }
    }
    
    public void unsetFlatTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.FLATTX$12, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTextBodyPropertiesImpl.EXTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextBodyPropertiesImpl.EXTLST$14) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTextBodyPropertiesImpl.EXTLST$14, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTextBodyPropertiesImpl.EXTLST$14);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextBodyPropertiesImpl.EXTLST$14, 0);
        }
    }
    
    public int getRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STAngle xgetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAngle)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
        }
    }
    
    public boolean isSetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ROT$16) != null;
        }
    }
    
    public void setRot(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRot(final STAngle stAngle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAngle stAngle2 = (STAngle)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
            if (stAngle2 == null) {
                stAngle2 = (STAngle)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ROT$16);
            }
            stAngle2.set((XmlObject)stAngle);
        }
    }
    
    public void unsetRot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.ROT$16);
        }
    }
    
    public boolean getSpcFirstLastPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSpcFirstLastPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
        }
    }
    
    public boolean isSetSpcFirstLastPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18) != null;
        }
    }
    
    public void setSpcFirstLastPara(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSpcFirstLastPara(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSpcFirstLastPara() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.SPCFIRSTLASTPARA$18);
        }
    }
    
    public STTextVertOverflowType.Enum getVertOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
            if (simpleValue == null) {
                return null;
            }
            return (STTextVertOverflowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextVertOverflowType xgetVertOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextVertOverflowType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
        }
    }
    
    public boolean isSetVertOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20) != null;
        }
    }
    
    public void setVertOverflow(final STTextVertOverflowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVertOverflow(final STTextVertOverflowType stTextVertOverflowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextVertOverflowType stTextVertOverflowType2 = (STTextVertOverflowType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
            if (stTextVertOverflowType2 == null) {
                stTextVertOverflowType2 = (STTextVertOverflowType)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
            }
            stTextVertOverflowType2.set((XmlObject)stTextVertOverflowType);
        }
    }
    
    public void unsetVertOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.VERTOVERFLOW$20);
        }
    }
    
    public STTextHorzOverflowType.Enum getHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
            if (simpleValue == null) {
                return null;
            }
            return (STTextHorzOverflowType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextHorzOverflowType xgetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextHorzOverflowType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
        }
    }
    
    public boolean isSetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22) != null;
        }
    }
    
    public void setHorzOverflow(final STTextHorzOverflowType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHorzOverflow(final STTextHorzOverflowType stTextHorzOverflowType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextHorzOverflowType stTextHorzOverflowType2 = (STTextHorzOverflowType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
            if (stTextHorzOverflowType2 == null) {
                stTextHorzOverflowType2 = (STTextHorzOverflowType)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
            }
            stTextHorzOverflowType2.set((XmlObject)stTextHorzOverflowType);
        }
    }
    
    public void unsetHorzOverflow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.HORZOVERFLOW$22);
        }
    }
    
    public STTextVerticalType.Enum getVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
            if (simpleValue == null) {
                return null;
            }
            return (STTextVerticalType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextVerticalType xgetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextVerticalType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
        }
    }
    
    public boolean isSetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERT$24) != null;
        }
    }
    
    public void setVert(final STTextVerticalType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVert(final STTextVerticalType stTextVerticalType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextVerticalType stTextVerticalType2 = (STTextVerticalType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
            if (stTextVerticalType2 == null) {
                stTextVerticalType2 = (STTextVerticalType)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.VERT$24);
            }
            stTextVerticalType2.set((XmlObject)stTextVerticalType);
        }
    }
    
    public void unsetVert() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.VERT$24);
        }
    }
    
    public STTextWrappingType.Enum getWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
            if (simpleValue == null) {
                return null;
            }
            return (STTextWrappingType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextWrappingType xgetWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextWrappingType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
        }
    }
    
    public boolean isSetWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.WRAP$26) != null;
        }
    }
    
    public void setWrap(final STTextWrappingType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetWrap(final STTextWrappingType stTextWrappingType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextWrappingType stTextWrappingType2 = (STTextWrappingType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
            if (stTextWrappingType2 == null) {
                stTextWrappingType2 = (STTextWrappingType)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.WRAP$26);
            }
            stTextWrappingType2.set((XmlObject)stTextWrappingType);
        }
    }
    
    public void unsetWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.WRAP$26);
        }
    }
    
    public int getLIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetLIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
        }
    }
    
    public boolean isSetLIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.LINS$28) != null;
        }
    }
    
    public void setLIns(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetLIns(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.LINS$28);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetLIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.LINS$28);
        }
    }
    
    public int getTIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetTIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
        }
    }
    
    public boolean isSetTIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.TINS$30) != null;
        }
    }
    
    public void setTIns(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetTIns(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.TINS$30);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetTIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.TINS$30);
        }
    }
    
    public int getRIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetRIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
        }
    }
    
    public boolean isSetRIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RINS$32) != null;
        }
    }
    
    public void setRIns(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRIns(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.RINS$32);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetRIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.RINS$32);
        }
    }
    
    public int getBIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetBIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
        }
    }
    
    public boolean isSetBIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.BINS$34) != null;
        }
    }
    
    public void setBIns(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetBIns(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.BINS$34);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetBIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.BINS$34);
        }
    }
    
    public int getNumCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextColumnCount xgetNumCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextColumnCount)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
        }
    }
    
    public boolean isSetNumCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36) != null;
        }
    }
    
    public void setNumCol(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetNumCol(final STTextColumnCount stTextColumnCount) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextColumnCount stTextColumnCount2 = (STTextColumnCount)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
            if (stTextColumnCount2 == null) {
                stTextColumnCount2 = (STTextColumnCount)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.NUMCOL$36);
            }
            stTextColumnCount2.set((XmlObject)stTextColumnCount);
        }
    }
    
    public void unsetNumCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.NUMCOL$36);
        }
    }
    
    public int getSpcCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveCoordinate32 xgetSpcCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
        }
    }
    
    public boolean isSetSpcCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38) != null;
        }
    }
    
    public void setSpcCol(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSpcCol(final STPositiveCoordinate32 stPositiveCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate32 stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
            if (stPositiveCoordinate33 == null) {
                stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.SPCCOL$38);
            }
            stPositiveCoordinate33.set((XmlObject)stPositiveCoordinate32);
        }
    }
    
    public void unsetSpcCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.SPCCOL$38);
        }
    }
    
    public boolean getRtlCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRtlCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
        }
    }
    
    public boolean isSetRtlCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40) != null;
        }
    }
    
    public void setRtlCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRtlCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.RTLCOL$40);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRtlCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.RTLCOL$40);
        }
    }
    
    public boolean getFromWordArt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFromWordArt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
        }
    }
    
    public boolean isSetFromWordArt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42) != null;
        }
    }
    
    public void setFromWordArt(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFromWordArt(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.FROMWORDART$42);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFromWordArt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.FROMWORDART$42);
        }
    }
    
    public STTextAnchoringType.Enum getAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
            if (simpleValue == null) {
                return null;
            }
            return (STTextAnchoringType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextAnchoringType xgetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextAnchoringType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
        }
    }
    
    public boolean isSetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44) != null;
        }
    }
    
    public void setAnchor(final STTextAnchoringType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAnchor(final STTextAnchoringType stTextAnchoringType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextAnchoringType stTextAnchoringType2 = (STTextAnchoringType)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
            if (stTextAnchoringType2 == null) {
                stTextAnchoringType2 = (STTextAnchoringType)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ANCHOR$44);
            }
            stTextAnchoringType2.set((XmlObject)stTextAnchoringType);
        }
    }
    
    public void unsetAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.ANCHOR$44);
        }
    }
    
    public boolean getAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
        }
    }
    
    public boolean isSetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46) != null;
        }
    }
    
    public void setAnchorCtr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAnchorCtr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.ANCHORCTR$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAnchorCtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.ANCHORCTR$46);
        }
    }
    
    public boolean getForceAA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetForceAA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
        }
    }
    
    public boolean isSetForceAA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48) != null;
        }
    }
    
    public void setForceAA(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetForceAA(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.FORCEAA$48);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetForceAA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.FORCEAA$48);
        }
    }
    
    public boolean getUpright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextBodyPropertiesImpl.UPRIGHT$50);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUpright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTextBodyPropertiesImpl.UPRIGHT$50);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUpright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50) != null;
        }
    }
    
    public void setUpright(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUpright(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.UPRIGHT$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUpright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.UPRIGHT$50);
        }
    }
    
    public boolean getCompatLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCompatLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
        }
    }
    
    public boolean isSetCompatLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52) != null;
        }
    }
    
    public void setCompatLnSpc(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCompatLnSpc(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCompatLnSpc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextBodyPropertiesImpl.COMPATLNSPC$52);
        }
    }
    
    static {
        PRSTTXWARP$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstTxWarp");
        NOAUTOFIT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noAutofit");
        NORMAUTOFIT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "normAutofit");
        SPAUTOFIT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spAutoFit");
        SCENE3D$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scene3d");
        SP3D$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sp3d");
        FLATTX$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "flatTx");
        EXTLST$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        ROT$16 = new QName("", "rot");
        SPCFIRSTLASTPARA$18 = new QName("", "spcFirstLastPara");
        VERTOVERFLOW$20 = new QName("", "vertOverflow");
        HORZOVERFLOW$22 = new QName("", "horzOverflow");
        VERT$24 = new QName("", "vert");
        WRAP$26 = new QName("", "wrap");
        LINS$28 = new QName("", "lIns");
        TINS$30 = new QName("", "tIns");
        RINS$32 = new QName("", "rIns");
        BINS$34 = new QName("", "bIns");
        NUMCOL$36 = new QName("", "numCol");
        SPCCOL$38 = new QName("", "spcCol");
        RTLCOL$40 = new QName("", "rtlCol");
        FROMWORDART$42 = new QName("", "fromWordArt");
        ANCHOR$44 = new QName("", "anchor");
        ANCHORCTR$46 = new QName("", "anchorCtr");
        FORCEAA$48 = new QName("", "forceAA");
        UPRIGHT$50 = new QName("", "upright");
        COMPATLNSPC$52 = new QName("", "compatLnSpc");
    }
}
