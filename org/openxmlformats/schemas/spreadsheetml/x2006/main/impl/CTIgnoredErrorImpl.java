package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;
import org.apache.xmlbeans.SimpleValue;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTIgnoredErrorImpl extends XmlComplexContentImpl implements CTIgnoredError
{
    private static final long serialVersionUID = 1L;
    private static final QName SQREF$0;
    private static final QName EVALERROR$2;
    private static final QName TWODIGITTEXTYEAR$4;
    private static final QName NUMBERSTOREDASTEXT$6;
    private static final QName FORMULA$8;
    private static final QName FORMULARANGE$10;
    private static final QName UNLOCKEDFORMULA$12;
    private static final QName EMPTYCELLREFERENCE$14;
    private static final QName LISTDATAVALIDATION$16;
    private static final QName CALCULATEDCOLUMN$18;
    
    public CTIgnoredErrorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List getSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.SQREF$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getListValue();
        }
    }
    
    public STSqref xgetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSqref)this.get_store().find_attribute_user(CTIgnoredErrorImpl.SQREF$0);
        }
    }
    
    public void setSqref(final List listValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.SQREF$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.SQREF$0);
            }
            simpleValue.setListValue(listValue);
        }
    }
    
    public void xsetSqref(final STSqref stSqref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSqref stSqref2 = (STSqref)this.get_store().find_attribute_user(CTIgnoredErrorImpl.SQREF$0);
            if (stSqref2 == null) {
                stSqref2 = (STSqref)this.get_store().add_attribute_user(CTIgnoredErrorImpl.SQREF$0);
            }
            stSqref2.set((XmlObject)stSqref);
        }
    }
    
    public boolean getEvalError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.EVALERROR$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEvalError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.EVALERROR$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEvalError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.EVALERROR$2) != null;
        }
    }
    
    public void setEvalError(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEvalError(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.EVALERROR$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEvalError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.EVALERROR$2);
        }
    }
    
    public boolean getTwoDigitTextYear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTwoDigitTextYear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTwoDigitTextYear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4) != null;
        }
    }
    
    public void setTwoDigitTextYear(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTwoDigitTextYear(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTwoDigitTextYear() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.TWODIGITTEXTYEAR$4);
        }
    }
    
    public boolean getNumberStoredAsText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNumberStoredAsText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNumberStoredAsText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6) != null;
        }
    }
    
    public void setNumberStoredAsText(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNumberStoredAsText(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNumberStoredAsText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.NUMBERSTOREDASTEXT$6);
        }
    }
    
    public boolean getFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.FORMULA$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.FORMULA$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULA$8) != null;
        }
    }
    
    public void setFormula(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFormula(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.FORMULA$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.FORMULA$8);
        }
    }
    
    public boolean getFormulaRange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.FORMULARANGE$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFormulaRange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.FORMULARANGE$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFormulaRange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10) != null;
        }
    }
    
    public void setFormulaRange(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFormulaRange(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.FORMULARANGE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFormulaRange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.FORMULARANGE$10);
        }
    }
    
    public boolean getUnlockedFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUnlockedFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUnlockedFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12) != null;
        }
    }
    
    public void setUnlockedFormula(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUnlockedFormula(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUnlockedFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.UNLOCKEDFORMULA$12);
        }
    }
    
    public boolean getEmptyCellReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEmptyCellReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEmptyCellReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14) != null;
        }
    }
    
    public void setEmptyCellReference(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEmptyCellReference(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEmptyCellReference() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.EMPTYCELLREFERENCE$14);
        }
    }
    
    public boolean getListDataValidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetListDataValidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetListDataValidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16) != null;
        }
    }
    
    public void setListDataValidation(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetListDataValidation(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetListDataValidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.LISTDATAVALIDATION$16);
        }
    }
    
    public boolean getCalculatedColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCalculatedColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCalculatedColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18) != null;
        }
    }
    
    public void setCalculatedColumn(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCalculatedColumn(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCalculatedColumn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTIgnoredErrorImpl.CALCULATEDCOLUMN$18);
        }
    }
    
    static {
        SQREF$0 = new QName("", "sqref");
        EVALERROR$2 = new QName("", "evalError");
        TWODIGITTEXTYEAR$4 = new QName("", "twoDigitTextYear");
        NUMBERSTOREDASTEXT$6 = new QName("", "numberStoredAsText");
        FORMULA$8 = new QName("", "formula");
        FORMULARANGE$10 = new QName("", "formulaRange");
        UNLOCKEDFORMULA$12 = new QName("", "unlockedFormula");
        EMPTYCELLREFERENCE$14 = new QName("", "emptyCellReference");
        LISTDATAVALIDATION$16 = new QName("", "listDataValidation");
        CALCULATEDCOLUMN$18 = new QName("", "calculatedColumn");
    }
}
