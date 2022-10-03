package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBinding;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMap;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTMapImpl extends XmlComplexContentImpl implements CTMap
{
    private static final long serialVersionUID = 1L;
    private static final QName DATABINDING$0;
    private static final QName ID$2;
    private static final QName NAME$4;
    private static final QName ROOTELEMENT$6;
    private static final QName SCHEMAID$8;
    private static final QName SHOWIMPORTEXPORTVALIDATIONERRORS$10;
    private static final QName AUTOFIT$12;
    private static final QName APPEND$14;
    private static final QName PRESERVESORTAFLAYOUT$16;
    private static final QName PRESERVEFORMAT$18;
    
    public CTMapImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTDataBinding getDataBinding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataBinding ctDataBinding = (CTDataBinding)this.get_store().find_element_user(CTMapImpl.DATABINDING$0, 0);
            if (ctDataBinding == null) {
                return null;
            }
            return ctDataBinding;
        }
    }
    
    public boolean isSetDataBinding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTMapImpl.DATABINDING$0) != 0;
        }
    }
    
    public void setDataBinding(final CTDataBinding ctDataBinding) {
        this.generatedSetterHelperImpl((XmlObject)ctDataBinding, CTMapImpl.DATABINDING$0, 0, (short)1);
    }
    
    public CTDataBinding addNewDataBinding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataBinding)this.get_store().add_element_user(CTMapImpl.DATABINDING$0);
        }
    }
    
    public void unsetDataBinding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTMapImpl.DATABINDING$0, 0);
        }
    }
    
    public long getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.ID$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTMapImpl.ID$2);
        }
    }
    
    public void setID(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.ID$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.ID$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetID(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTMapImpl.ID$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTMapImpl.ID$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.NAME$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTMapImpl.NAME$4);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.NAME$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.NAME$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTMapImpl.NAME$4);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTMapImpl.NAME$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public String getRootElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.ROOTELEMENT$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetRootElement() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTMapImpl.ROOTELEMENT$6);
        }
    }
    
    public void setRootElement(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.ROOTELEMENT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.ROOTELEMENT$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRootElement(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTMapImpl.ROOTELEMENT$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTMapImpl.ROOTELEMENT$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public String getSchemaID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.SCHEMAID$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSchemaID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTMapImpl.SCHEMAID$8);
        }
    }
    
    public void setSchemaID(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.SCHEMAID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.SCHEMAID$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSchemaID(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTMapImpl.SCHEMAID$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTMapImpl.SCHEMAID$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public boolean getShowImportExportValidationErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowImportExportValidationErrors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
        }
    }
    
    public void setShowImportExportValidationErrors(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowImportExportValidationErrors(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTMapImpl.SHOWIMPORTEXPORTVALIDATIONERRORS$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.AUTOFIT$12);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.AUTOFIT$12);
        }
    }
    
    public void setAutoFit(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.AUTOFIT$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.AUTOFIT$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoFit(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.AUTOFIT$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTMapImpl.AUTOFIT$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getAppend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.APPEND$14);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAppend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.APPEND$14);
        }
    }
    
    public void setAppend(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.APPEND$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.APPEND$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAppend(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.APPEND$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTMapImpl.APPEND$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getPreserveSortAFLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreserveSortAFLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
        }
    }
    
    public void setPreserveSortAFLayout(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreserveSortAFLayout(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTMapImpl.PRESERVESORTAFLAYOUT$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public boolean getPreserveFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPreserveFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
        }
    }
    
    public void setPreserveFormat(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPreserveFormat(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTMapImpl.PRESERVEFORMAT$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    static {
        DATABINDING$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "DataBinding");
        ID$2 = new QName("", "ID");
        NAME$4 = new QName("", "Name");
        ROOTELEMENT$6 = new QName("", "RootElement");
        SCHEMAID$8 = new QName("", "SchemaID");
        SHOWIMPORTEXPORTVALIDATIONERRORS$10 = new QName("", "ShowImportExportValidationErrors");
        AUTOFIT$12 = new QName("", "AutoFit");
        APPEND$14 = new QName("", "Append");
        PRESERVESORTAFLAYOUT$16 = new QName("", "PreserveSortAFLayout");
        PRESERVEFORMAT$18 = new QName("", "PreserveFormat");
    }
}
