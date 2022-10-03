package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellStyleXfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFillId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellProtection;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTXfImpl extends XmlComplexContentImpl implements CTXf
{
    private static final long serialVersionUID = 1L;
    private static final QName ALIGNMENT$0;
    private static final QName PROTECTION$2;
    private static final QName EXTLST$4;
    private static final QName NUMFMTID$6;
    private static final QName FONTID$8;
    private static final QName FILLID$10;
    private static final QName BORDERID$12;
    private static final QName XFID$14;
    private static final QName QUOTEPREFIX$16;
    private static final QName PIVOTBUTTON$18;
    private static final QName APPLYNUMBERFORMAT$20;
    private static final QName APPLYFONT$22;
    private static final QName APPLYFILL$24;
    private static final QName APPLYBORDER$26;
    private static final QName APPLYALIGNMENT$28;
    private static final QName APPLYPROTECTION$30;
    
    public CTXfImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCellAlignment getAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellAlignment ctCellAlignment = (CTCellAlignment)this.get_store().find_element_user(CTXfImpl.ALIGNMENT$0, 0);
            if (ctCellAlignment == null) {
                return null;
            }
            return ctCellAlignment;
        }
    }
    
    public boolean isSetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTXfImpl.ALIGNMENT$0) != 0;
        }
    }
    
    public void setAlignment(final CTCellAlignment ctCellAlignment) {
        this.generatedSetterHelperImpl((XmlObject)ctCellAlignment, CTXfImpl.ALIGNMENT$0, 0, (short)1);
    }
    
    public CTCellAlignment addNewAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellAlignment)this.get_store().add_element_user(CTXfImpl.ALIGNMENT$0);
        }
    }
    
    public void unsetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTXfImpl.ALIGNMENT$0, 0);
        }
    }
    
    public CTCellProtection getProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellProtection ctCellProtection = (CTCellProtection)this.get_store().find_element_user(CTXfImpl.PROTECTION$2, 0);
            if (ctCellProtection == null) {
                return null;
            }
            return ctCellProtection;
        }
    }
    
    public boolean isSetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTXfImpl.PROTECTION$2) != 0;
        }
    }
    
    public void setProtection(final CTCellProtection ctCellProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctCellProtection, CTXfImpl.PROTECTION$2, 0, (short)1);
    }
    
    public CTCellProtection addNewProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellProtection)this.get_store().add_element_user(CTXfImpl.PROTECTION$2);
        }
    }
    
    public void unsetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTXfImpl.PROTECTION$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTXfImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTXfImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTXfImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTXfImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTXfImpl.EXTLST$4, 0);
        }
    }
    
    public long getNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.NUMFMTID$6);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STNumFmtId xgetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumFmtId)this.get_store().find_attribute_user(CTXfImpl.NUMFMTID$6);
        }
    }
    
    public boolean isSetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.NUMFMTID$6) != null;
        }
    }
    
    public void setNumFmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.NUMFMTID$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.NUMFMTID$6);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetNumFmtId(final STNumFmtId stNumFmtId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumFmtId stNumFmtId2 = (STNumFmtId)this.get_store().find_attribute_user(CTXfImpl.NUMFMTID$6);
            if (stNumFmtId2 == null) {
                stNumFmtId2 = (STNumFmtId)this.get_store().add_attribute_user(CTXfImpl.NUMFMTID$6);
            }
            stNumFmtId2.set((XmlObject)stNumFmtId);
        }
    }
    
    public void unsetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.NUMFMTID$6);
        }
    }
    
    public long getFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.FONTID$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STFontId xgetFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFontId)this.get_store().find_attribute_user(CTXfImpl.FONTID$8);
        }
    }
    
    public boolean isSetFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.FONTID$8) != null;
        }
    }
    
    public void setFontId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.FONTID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.FONTID$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFontId(final STFontId stFontId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFontId stFontId2 = (STFontId)this.get_store().find_attribute_user(CTXfImpl.FONTID$8);
            if (stFontId2 == null) {
                stFontId2 = (STFontId)this.get_store().add_attribute_user(CTXfImpl.FONTID$8);
            }
            stFontId2.set((XmlObject)stFontId);
        }
    }
    
    public void unsetFontId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.FONTID$8);
        }
    }
    
    public long getFillId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.FILLID$10);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STFillId xgetFillId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFillId)this.get_store().find_attribute_user(CTXfImpl.FILLID$10);
        }
    }
    
    public boolean isSetFillId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.FILLID$10) != null;
        }
    }
    
    public void setFillId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.FILLID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.FILLID$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFillId(final STFillId stFillId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFillId stFillId2 = (STFillId)this.get_store().find_attribute_user(CTXfImpl.FILLID$10);
            if (stFillId2 == null) {
                stFillId2 = (STFillId)this.get_store().add_attribute_user(CTXfImpl.FILLID$10);
            }
            stFillId2.set((XmlObject)stFillId);
        }
    }
    
    public void unsetFillId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.FILLID$10);
        }
    }
    
    public long getBorderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.BORDERID$12);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STBorderId xgetBorderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBorderId)this.get_store().find_attribute_user(CTXfImpl.BORDERID$12);
        }
    }
    
    public boolean isSetBorderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.BORDERID$12) != null;
        }
    }
    
    public void setBorderId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.BORDERID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.BORDERID$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBorderId(final STBorderId stBorderId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBorderId stBorderId2 = (STBorderId)this.get_store().find_attribute_user(CTXfImpl.BORDERID$12);
            if (stBorderId2 == null) {
                stBorderId2 = (STBorderId)this.get_store().add_attribute_user(CTXfImpl.BORDERID$12);
            }
            stBorderId2.set((XmlObject)stBorderId);
        }
    }
    
    public void unsetBorderId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.BORDERID$12);
        }
    }
    
    public long getXfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.XFID$14);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STCellStyleXfId xgetXfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellStyleXfId)this.get_store().find_attribute_user(CTXfImpl.XFID$14);
        }
    }
    
    public boolean isSetXfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.XFID$14) != null;
        }
    }
    
    public void setXfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.XFID$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.XFID$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetXfId(final STCellStyleXfId stCellStyleXfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellStyleXfId stCellStyleXfId2 = (STCellStyleXfId)this.get_store().find_attribute_user(CTXfImpl.XFID$14);
            if (stCellStyleXfId2 == null) {
                stCellStyleXfId2 = (STCellStyleXfId)this.get_store().add_attribute_user(CTXfImpl.XFID$14);
            }
            stCellStyleXfId2.set((XmlObject)stCellStyleXfId);
        }
    }
    
    public void unsetXfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.XFID$14);
        }
    }
    
    public boolean getQuotePrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTXfImpl.QUOTEPREFIX$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetQuotePrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTXfImpl.QUOTEPREFIX$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetQuotePrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.QUOTEPREFIX$16) != null;
        }
    }
    
    public void setQuotePrefix(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetQuotePrefix(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.QUOTEPREFIX$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetQuotePrefix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.QUOTEPREFIX$16);
        }
    }
    
    public boolean getPivotButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTXfImpl.PIVOTBUTTON$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPivotButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTXfImpl.PIVOTBUTTON$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPivotButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.PIVOTBUTTON$18) != null;
        }
    }
    
    public void setPivotButton(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPivotButton(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.PIVOTBUTTON$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPivotButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.PIVOTBUTTON$18);
        }
    }
    
    public boolean getApplyNumberFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyNumberFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
        }
    }
    
    public boolean isSetApplyNumberFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20) != null;
        }
    }
    
    public void setApplyNumberFormat(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyNumberFormat(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYNUMBERFORMAT$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyNumberFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYNUMBERFORMAT$20);
        }
    }
    
    public boolean getApplyFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYFONT$22);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYFONT$22);
        }
    }
    
    public boolean isSetApplyFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYFONT$22) != null;
        }
    }
    
    public void setApplyFont(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYFONT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYFONT$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyFont(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYFONT$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYFONT$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYFONT$22);
        }
    }
    
    public boolean getApplyFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYFILL$24);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYFILL$24);
        }
    }
    
    public boolean isSetApplyFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYFILL$24) != null;
        }
    }
    
    public void setApplyFill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYFILL$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYFILL$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyFill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYFILL$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYFILL$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYFILL$24);
        }
    }
    
    public boolean getApplyBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYBORDER$26);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYBORDER$26);
        }
    }
    
    public boolean isSetApplyBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYBORDER$26) != null;
        }
    }
    
    public void setApplyBorder(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYBORDER$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYBORDER$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyBorder(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYBORDER$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYBORDER$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyBorder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYBORDER$26);
        }
    }
    
    public boolean getApplyAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
        }
    }
    
    public boolean isSetApplyAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYALIGNMENT$28) != null;
        }
    }
    
    public void setApplyAlignment(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyAlignment(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYALIGNMENT$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYALIGNMENT$28);
        }
    }
    
    public boolean getApplyProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYPROTECTION$30);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetApplyProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYPROTECTION$30);
        }
    }
    
    public boolean isSetApplyProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTXfImpl.APPLYPROTECTION$30) != null;
        }
    }
    
    public void setApplyProtection(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTXfImpl.APPLYPROTECTION$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTXfImpl.APPLYPROTECTION$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetApplyProtection(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTXfImpl.APPLYPROTECTION$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTXfImpl.APPLYPROTECTION$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetApplyProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTXfImpl.APPLYPROTECTION$30);
        }
    }
    
    static {
        ALIGNMENT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "alignment");
        PROTECTION$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "protection");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        NUMFMTID$6 = new QName("", "numFmtId");
        FONTID$8 = new QName("", "fontId");
        FILLID$10 = new QName("", "fillId");
        BORDERID$12 = new QName("", "borderId");
        XFID$14 = new QName("", "xfId");
        QUOTEPREFIX$16 = new QName("", "quotePrefix");
        PIVOTBUTTON$18 = new QName("", "pivotButton");
        APPLYNUMBERFORMAT$20 = new QName("", "applyNumberFormat");
        APPLYFONT$22 = new QName("", "applyFont");
        APPLYFILL$24 = new QName("", "applyFill");
        APPLYBORDER$26 = new QName("", "applyBorder");
        APPLYALIGNMENT$28 = new QName("", "applyAlignment");
        APPLYPROTECTION$30 = new QName("", "applyProtection");
    }
}
