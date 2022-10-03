package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class CTDefinedNameImpl extends JavaStringHolderEx implements CTDefinedName
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName COMMENT$2;
    private static final QName CUSTOMMENU$4;
    private static final QName DESCRIPTION$6;
    private static final QName HELP$8;
    private static final QName STATUSBAR$10;
    private static final QName LOCALSHEETID$12;
    private static final QName HIDDEN$14;
    private static final QName FUNCTION$16;
    private static final QName VBPROCEDURE$18;
    private static final QName XLM$20;
    private static final QName FUNCTIONGROUPID$22;
    private static final QName SHORTCUTKEY$24;
    private static final QName PUBLISHTOSERVER$26;
    private static final QName WORKBOOKPARAMETER$28;
    
    public CTDefinedNameImpl(final SchemaType schemaType) {
        super(schemaType, true);
    }
    
    protected CTDefinedNameImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.NAME$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.NAME$0);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.NAME$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.NAME$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.NAME$0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.NAME$0);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public String getComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.COMMENT$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.COMMENT$2);
        }
    }
    
    public boolean isSetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.COMMENT$2) != null;
        }
    }
    
    public void setComment(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.COMMENT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.COMMENT$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetComment(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.COMMENT$2);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.COMMENT$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetComment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.COMMENT$2);
        }
    }
    
    public String getCustomMenu() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetCustomMenu() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
        }
    }
    
    public boolean isSetCustomMenu() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4) != null;
        }
    }
    
    public void setCustomMenu(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCustomMenu(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.CUSTOMMENU$4);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetCustomMenu() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.CUSTOMMENU$4);
        }
    }
    
    public String getDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
        }
    }
    
    public boolean isSetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.DESCRIPTION$6) != null;
        }
    }
    
    public void setDescription(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDescription(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.DESCRIPTION$6);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetDescription() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.DESCRIPTION$6);
        }
    }
    
    public String getHelp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.HELP$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetHelp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.HELP$8);
        }
    }
    
    public boolean isSetHelp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.HELP$8) != null;
        }
    }
    
    public void setHelp(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.HELP$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.HELP$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHelp(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.HELP$8);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.HELP$8);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetHelp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.HELP$8);
        }
    }
    
    public String getStatusBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetStatusBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
        }
    }
    
    public boolean isSetStatusBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.STATUSBAR$10) != null;
        }
    }
    
    public void setStatusBar(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStatusBar(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.STATUSBAR$10);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetStatusBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.STATUSBAR$10);
        }
    }
    
    public long getLocalSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetLocalSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
        }
    }
    
    public boolean isSetLocalSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12) != null;
        }
    }
    
    public void setLocalSheetId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetLocalSheetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDefinedNameImpl.LOCALSHEETID$12);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetLocalSheetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.LOCALSHEETID$12);
        }
    }
    
    public boolean getHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.HIDDEN$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.HIDDEN$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.HIDDEN$14) != null;
        }
    }
    
    public void setHidden(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHidden(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.HIDDEN$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.HIDDEN$14);
        }
    }
    
    public boolean getFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.FUNCTION$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.FUNCTION$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTION$16) != null;
        }
    }
    
    public void setFunction(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFunction(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.FUNCTION$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.FUNCTION$16);
        }
    }
    
    public boolean getVbProcedure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.VBPROCEDURE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVbProcedure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.VBPROCEDURE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVbProcedure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18) != null;
        }
    }
    
    public void setVbProcedure(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVbProcedure(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.VBPROCEDURE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVbProcedure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.VBPROCEDURE$18);
        }
    }
    
    public boolean getXlm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.XLM$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.XLM$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetXlm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.XLM$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.XLM$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetXlm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.XLM$20) != null;
        }
    }
    
    public void setXlm(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.XLM$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.XLM$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetXlm(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.XLM$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.XLM$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetXlm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.XLM$20);
        }
    }
    
    public long getFunctionGroupId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFunctionGroupId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
        }
    }
    
    public boolean isSetFunctionGroupId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22) != null;
        }
    }
    
    public void setFunctionGroupId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFunctionGroupId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDefinedNameImpl.FUNCTIONGROUPID$22);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetFunctionGroupId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.FUNCTIONGROUPID$22);
        }
    }
    
    public String getShortcutKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetShortcutKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
        }
    }
    
    public boolean isSetShortcutKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24) != null;
        }
    }
    
    public void setShortcutKey(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetShortcutKey(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDefinedNameImpl.SHORTCUTKEY$24);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetShortcutKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.SHORTCUTKEY$24);
        }
    }
    
    public boolean getPublishToServer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPublishToServer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPublishToServer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26) != null;
        }
    }
    
    public void setPublishToServer(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPublishToServer(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.PUBLISHTOSERVER$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPublishToServer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.PUBLISHTOSERVER$26);
        }
    }
    
    public boolean getWorkbookParameter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetWorkbookParameter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetWorkbookParameter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28) != null;
        }
    }
    
    public void setWorkbookParameter(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetWorkbookParameter(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetWorkbookParameter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDefinedNameImpl.WORKBOOKPARAMETER$28);
        }
    }
    
    static {
        NAME$0 = new QName("", "name");
        COMMENT$2 = new QName("", "comment");
        CUSTOMMENU$4 = new QName("", "customMenu");
        DESCRIPTION$6 = new QName("", "description");
        HELP$8 = new QName("", "help");
        STATUSBAR$10 = new QName("", "statusBar");
        LOCALSHEETID$12 = new QName("", "localSheetId");
        HIDDEN$14 = new QName("", "hidden");
        FUNCTION$16 = new QName("", "function");
        VBPROCEDURE$18 = new QName("", "vbProcedure");
        XLM$20 = new QName("", "xlm");
        FUNCTIONGROUPID$22 = new QName("", "functionGroupId");
        SHORTCUTKEY$24 = new QName("", "shortcutKey");
        PUBLISHTOSERVER$26 = new QName("", "publishToServer");
        WORKBOOKPARAMETER$28 = new QName("", "workbookParameter");
    }
}
