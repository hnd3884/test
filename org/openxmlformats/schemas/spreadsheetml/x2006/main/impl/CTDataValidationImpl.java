package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;
import java.util.List;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationImeMode;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationErrorStyle;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDataValidationImpl extends XmlComplexContentImpl implements CTDataValidation
{
    private static final long serialVersionUID = 1L;
    private static final QName FORMULA1$0;
    private static final QName FORMULA2$2;
    private static final QName TYPE$4;
    private static final QName ERRORSTYLE$6;
    private static final QName IMEMODE$8;
    private static final QName OPERATOR$10;
    private static final QName ALLOWBLANK$12;
    private static final QName SHOWDROPDOWN$14;
    private static final QName SHOWINPUTMESSAGE$16;
    private static final QName SHOWERRORMESSAGE$18;
    private static final QName ERRORTITLE$20;
    private static final QName ERROR$22;
    private static final QName PROMPTTITLE$24;
    private static final QName PROMPT$26;
    private static final QName SQREF$28;
    
    public CTDataValidationImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getFormula1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDataValidationImpl.FORMULA1$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STFormula xgetFormula1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFormula)this.get_store().find_element_user(CTDataValidationImpl.FORMULA1$0, 0);
        }
    }
    
    public boolean isSetFormula1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDataValidationImpl.FORMULA1$0) != 0;
        }
    }
    
    public void setFormula1(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDataValidationImpl.FORMULA1$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTDataValidationImpl.FORMULA1$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormula1(final STFormula stFormula) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFormula stFormula2 = (STFormula)this.get_store().find_element_user(CTDataValidationImpl.FORMULA1$0, 0);
            if (stFormula2 == null) {
                stFormula2 = (STFormula)this.get_store().add_element_user(CTDataValidationImpl.FORMULA1$0);
            }
            stFormula2.set((XmlObject)stFormula);
        }
    }
    
    public void unsetFormula1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDataValidationImpl.FORMULA1$0, 0);
        }
    }
    
    public String getFormula2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDataValidationImpl.FORMULA2$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STFormula xgetFormula2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFormula)this.get_store().find_element_user(CTDataValidationImpl.FORMULA2$2, 0);
        }
    }
    
    public boolean isSetFormula2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDataValidationImpl.FORMULA2$2) != 0;
        }
    }
    
    public void setFormula2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDataValidationImpl.FORMULA2$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTDataValidationImpl.FORMULA2$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormula2(final STFormula stFormula) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFormula stFormula2 = (STFormula)this.get_store().find_element_user(CTDataValidationImpl.FORMULA2$2, 0);
            if (stFormula2 == null) {
                stFormula2 = (STFormula)this.get_store().add_element_user(CTDataValidationImpl.FORMULA2$2);
            }
            stFormula2.set((XmlObject)stFormula);
        }
    }
    
    public void unsetFormula2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDataValidationImpl.FORMULA2$2, 0);
        }
    }
    
    public STDataValidationType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.TYPE$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDataValidationType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDataValidationType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationType stDataValidationType = (STDataValidationType)this.get_store().find_attribute_user(CTDataValidationImpl.TYPE$4);
            if (stDataValidationType == null) {
                stDataValidationType = (STDataValidationType)this.get_default_attribute_value(CTDataValidationImpl.TYPE$4);
            }
            return stDataValidationType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.TYPE$4) != null;
        }
    }
    
    public void setType(final STDataValidationType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.TYPE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.TYPE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STDataValidationType stDataValidationType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationType stDataValidationType2 = (STDataValidationType)this.get_store().find_attribute_user(CTDataValidationImpl.TYPE$4);
            if (stDataValidationType2 == null) {
                stDataValidationType2 = (STDataValidationType)this.get_store().add_attribute_user(CTDataValidationImpl.TYPE$4);
            }
            stDataValidationType2.set((XmlObject)stDataValidationType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.TYPE$4);
        }
    }
    
    public STDataValidationErrorStyle.Enum getErrorStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.ERRORSTYLE$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDataValidationErrorStyle.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDataValidationErrorStyle xgetErrorStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationErrorStyle stDataValidationErrorStyle = (STDataValidationErrorStyle)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            if (stDataValidationErrorStyle == null) {
                stDataValidationErrorStyle = (STDataValidationErrorStyle)this.get_default_attribute_value(CTDataValidationImpl.ERRORSTYLE$6);
            }
            return stDataValidationErrorStyle;
        }
    }
    
    public boolean isSetErrorStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.ERRORSTYLE$6) != null;
        }
    }
    
    public void setErrorStyle(final STDataValidationErrorStyle.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetErrorStyle(final STDataValidationErrorStyle stDataValidationErrorStyle) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationErrorStyle stDataValidationErrorStyle2 = (STDataValidationErrorStyle)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            if (stDataValidationErrorStyle2 == null) {
                stDataValidationErrorStyle2 = (STDataValidationErrorStyle)this.get_store().add_attribute_user(CTDataValidationImpl.ERRORSTYLE$6);
            }
            stDataValidationErrorStyle2.set((XmlObject)stDataValidationErrorStyle);
        }
    }
    
    public void unsetErrorStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.ERRORSTYLE$6);
        }
    }
    
    public STDataValidationImeMode.Enum getImeMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.IMEMODE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.IMEMODE$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDataValidationImeMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDataValidationImeMode xgetImeMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationImeMode stDataValidationImeMode = (STDataValidationImeMode)this.get_store().find_attribute_user(CTDataValidationImpl.IMEMODE$8);
            if (stDataValidationImeMode == null) {
                stDataValidationImeMode = (STDataValidationImeMode)this.get_default_attribute_value(CTDataValidationImpl.IMEMODE$8);
            }
            return stDataValidationImeMode;
        }
    }
    
    public boolean isSetImeMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.IMEMODE$8) != null;
        }
    }
    
    public void setImeMode(final STDataValidationImeMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.IMEMODE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.IMEMODE$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetImeMode(final STDataValidationImeMode stDataValidationImeMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationImeMode stDataValidationImeMode2 = (STDataValidationImeMode)this.get_store().find_attribute_user(CTDataValidationImpl.IMEMODE$8);
            if (stDataValidationImeMode2 == null) {
                stDataValidationImeMode2 = (STDataValidationImeMode)this.get_store().add_attribute_user(CTDataValidationImpl.IMEMODE$8);
            }
            stDataValidationImeMode2.set((XmlObject)stDataValidationImeMode);
        }
    }
    
    public void unsetImeMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.IMEMODE$8);
        }
    }
    
    public STDataValidationOperator.Enum getOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.OPERATOR$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.OPERATOR$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDataValidationOperator.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDataValidationOperator xgetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationOperator stDataValidationOperator = (STDataValidationOperator)this.get_store().find_attribute_user(CTDataValidationImpl.OPERATOR$10);
            if (stDataValidationOperator == null) {
                stDataValidationOperator = (STDataValidationOperator)this.get_default_attribute_value(CTDataValidationImpl.OPERATOR$10);
            }
            return stDataValidationOperator;
        }
    }
    
    public boolean isSetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.OPERATOR$10) != null;
        }
    }
    
    public void setOperator(final STDataValidationOperator.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.OPERATOR$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.OPERATOR$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOperator(final STDataValidationOperator stDataValidationOperator) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataValidationOperator stDataValidationOperator2 = (STDataValidationOperator)this.get_store().find_attribute_user(CTDataValidationImpl.OPERATOR$10);
            if (stDataValidationOperator2 == null) {
                stDataValidationOperator2 = (STDataValidationOperator)this.get_store().add_attribute_user(CTDataValidationImpl.OPERATOR$10);
            }
            stDataValidationOperator2.set((XmlObject)stDataValidationOperator);
        }
    }
    
    public void unsetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.OPERATOR$10);
        }
    }
    
    public boolean getAllowBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.ALLOWBLANK$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAllowBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataValidationImpl.ALLOWBLANK$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAllowBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.ALLOWBLANK$12) != null;
        }
    }
    
    public void setAllowBlank(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAllowBlank(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataValidationImpl.ALLOWBLANK$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAllowBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.ALLOWBLANK$12);
        }
    }
    
    public boolean getShowDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.SHOWDROPDOWN$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataValidationImpl.SHOWDROPDOWN$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14) != null;
        }
    }
    
    public void setShowDropDown(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDropDown(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWDROPDOWN$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDropDown() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.SHOWDROPDOWN$14);
        }
    }
    
    public boolean getShowInputMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowInputMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowInputMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16) != null;
        }
    }
    
    public void setShowInputMessage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowInputMessage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowInputMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.SHOWINPUTMESSAGE$16);
        }
    }
    
    public boolean getShowErrorMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowErrorMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowErrorMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18) != null;
        }
    }
    
    public void setShowErrorMessage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowErrorMessage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDataValidationImpl.SHOWERRORMESSAGE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowErrorMessage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.SHOWERRORMESSAGE$18);
        }
    }
    
    public String getErrorTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetErrorTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
        }
    }
    
    public boolean isSetErrorTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.ERRORTITLE$20) != null;
        }
    }
    
    public void setErrorTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetErrorTitle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDataValidationImpl.ERRORTITLE$20);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetErrorTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.ERRORTITLE$20);
        }
    }
    
    public String getError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERROR$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.ERROR$22);
        }
    }
    
    public boolean isSetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.ERROR$22) != null;
        }
    }
    
    public void setError(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.ERROR$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.ERROR$22);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetError(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.ERROR$22);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDataValidationImpl.ERROR$22);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetError() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.ERROR$22);
        }
    }
    
    public String getPromptTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetPromptTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
        }
    }
    
    public boolean isSetPromptTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.PROMPTTITLE$24) != null;
        }
    }
    
    public void setPromptTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPromptTitle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDataValidationImpl.PROMPTTITLE$24);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetPromptTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.PROMPTTITLE$24);
        }
    }
    
    public String getPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPT$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPT$26);
        }
    }
    
    public boolean isSetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataValidationImpl.PROMPT$26) != null;
        }
    }
    
    public void setPrompt(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.PROMPT$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPrompt(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDataValidationImpl.PROMPT$26);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDataValidationImpl.PROMPT$26);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetPrompt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataValidationImpl.PROMPT$26);
        }
    }
    
    public List getSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SQREF$28);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getListValue();
        }
    }
    
    public STSqref xgetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSqref)this.get_store().find_attribute_user(CTDataValidationImpl.SQREF$28);
        }
    }
    
    public void setSqref(final List listValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataValidationImpl.SQREF$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataValidationImpl.SQREF$28);
            }
            simpleValue.setListValue(listValue);
        }
    }
    
    public void xsetSqref(final STSqref stSqref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSqref stSqref2 = (STSqref)this.get_store().find_attribute_user(CTDataValidationImpl.SQREF$28);
            if (stSqref2 == null) {
                stSqref2 = (STSqref)this.get_store().add_attribute_user(CTDataValidationImpl.SQREF$28);
            }
            stSqref2.set((XmlObject)stSqref);
        }
    }
    
    static {
        FORMULA1$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "formula1");
        FORMULA2$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "formula2");
        TYPE$4 = new QName("", "type");
        ERRORSTYLE$6 = new QName("", "errorStyle");
        IMEMODE$8 = new QName("", "imeMode");
        OPERATOR$10 = new QName("", "operator");
        ALLOWBLANK$12 = new QName("", "allowBlank");
        SHOWDROPDOWN$14 = new QName("", "showDropDown");
        SHOWINPUTMESSAGE$16 = new QName("", "showInputMessage");
        SHOWERRORMESSAGE$18 = new QName("", "showErrorMessage");
        ERRORTITLE$20 = new QName("", "errorTitle");
        ERROR$22 = new QName("", "error");
        PROMPTTITLE$24 = new QName("", "promptTitle");
        PROMPT$26 = new QName("", "prompt");
        SQREF$28 = new QName("", "sqref");
    }
}
