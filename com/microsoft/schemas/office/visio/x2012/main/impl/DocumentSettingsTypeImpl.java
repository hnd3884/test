package com.microsoft.schemas.office.visio.x2012.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.visio.x2012.main.AttachedToolbarsType;
import com.microsoft.schemas.office.visio.x2012.main.CustomToolbarsFileType;
import com.microsoft.schemas.office.visio.x2012.main.CustomMenusFileType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectBkgndsType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectMastersType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectShapesType;
import com.microsoft.schemas.office.visio.x2012.main.ProtectStylesType;
import com.microsoft.schemas.office.visio.x2012.main.DynamicGridEnabledType;
import com.microsoft.schemas.office.visio.x2012.main.SnapAnglesType;
import com.microsoft.schemas.office.visio.x2012.main.SnapExtensionsType;
import com.microsoft.schemas.office.visio.x2012.main.SnapSettingsType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.GlueSettingsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSettingsType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class DocumentSettingsTypeImpl extends XmlComplexContentImpl implements DocumentSettingsType
{
    private static final long serialVersionUID = 1L;
    private static final QName GLUESETTINGS$0;
    private static final QName SNAPSETTINGS$2;
    private static final QName SNAPEXTENSIONS$4;
    private static final QName SNAPANGLES$6;
    private static final QName DYNAMICGRIDENABLED$8;
    private static final QName PROTECTSTYLES$10;
    private static final QName PROTECTSHAPES$12;
    private static final QName PROTECTMASTERS$14;
    private static final QName PROTECTBKGNDS$16;
    private static final QName CUSTOMMENUSFILE$18;
    private static final QName CUSTOMTOOLBARSFILE$20;
    private static final QName ATTACHEDTOOLBARS$22;
    private static final QName TOPPAGE$24;
    private static final QName DEFAULTTEXTSTYLE$26;
    private static final QName DEFAULTLINESTYLE$28;
    private static final QName DEFAULTFILLSTYLE$30;
    private static final QName DEFAULTGUIDESTYLE$32;
    
    public DocumentSettingsTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public GlueSettingsType getGlueSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final GlueSettingsType glueSettingsType = (GlueSettingsType)this.get_store().find_element_user(DocumentSettingsTypeImpl.GLUESETTINGS$0, 0);
            if (glueSettingsType == null) {
                return null;
            }
            return glueSettingsType;
        }
    }
    
    public boolean isSetGlueSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.GLUESETTINGS$0) != 0;
        }
    }
    
    public void setGlueSettings(final GlueSettingsType glueSettingsType) {
        this.generatedSetterHelperImpl((XmlObject)glueSettingsType, DocumentSettingsTypeImpl.GLUESETTINGS$0, 0, (short)1);
    }
    
    public GlueSettingsType addNewGlueSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (GlueSettingsType)this.get_store().add_element_user(DocumentSettingsTypeImpl.GLUESETTINGS$0);
        }
    }
    
    public void unsetGlueSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.GLUESETTINGS$0, 0);
        }
    }
    
    public SnapSettingsType getSnapSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SnapSettingsType snapSettingsType = (SnapSettingsType)this.get_store().find_element_user(DocumentSettingsTypeImpl.SNAPSETTINGS$2, 0);
            if (snapSettingsType == null) {
                return null;
            }
            return snapSettingsType;
        }
    }
    
    public boolean isSetSnapSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.SNAPSETTINGS$2) != 0;
        }
    }
    
    public void setSnapSettings(final SnapSettingsType snapSettingsType) {
        this.generatedSetterHelperImpl((XmlObject)snapSettingsType, DocumentSettingsTypeImpl.SNAPSETTINGS$2, 0, (short)1);
    }
    
    public SnapSettingsType addNewSnapSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SnapSettingsType)this.get_store().add_element_user(DocumentSettingsTypeImpl.SNAPSETTINGS$2);
        }
    }
    
    public void unsetSnapSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.SNAPSETTINGS$2, 0);
        }
    }
    
    public SnapExtensionsType getSnapExtensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SnapExtensionsType snapExtensionsType = (SnapExtensionsType)this.get_store().find_element_user(DocumentSettingsTypeImpl.SNAPEXTENSIONS$4, 0);
            if (snapExtensionsType == null) {
                return null;
            }
            return snapExtensionsType;
        }
    }
    
    public boolean isSetSnapExtensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.SNAPEXTENSIONS$4) != 0;
        }
    }
    
    public void setSnapExtensions(final SnapExtensionsType snapExtensionsType) {
        this.generatedSetterHelperImpl((XmlObject)snapExtensionsType, DocumentSettingsTypeImpl.SNAPEXTENSIONS$4, 0, (short)1);
    }
    
    public SnapExtensionsType addNewSnapExtensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SnapExtensionsType)this.get_store().add_element_user(DocumentSettingsTypeImpl.SNAPEXTENSIONS$4);
        }
    }
    
    public void unsetSnapExtensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.SNAPEXTENSIONS$4, 0);
        }
    }
    
    public SnapAnglesType getSnapAngles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SnapAnglesType snapAnglesType = (SnapAnglesType)this.get_store().find_element_user(DocumentSettingsTypeImpl.SNAPANGLES$6, 0);
            if (snapAnglesType == null) {
                return null;
            }
            return snapAnglesType;
        }
    }
    
    public boolean isSetSnapAngles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.SNAPANGLES$6) != 0;
        }
    }
    
    public void setSnapAngles(final SnapAnglesType snapAnglesType) {
        this.generatedSetterHelperImpl((XmlObject)snapAnglesType, DocumentSettingsTypeImpl.SNAPANGLES$6, 0, (short)1);
    }
    
    public SnapAnglesType addNewSnapAngles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (SnapAnglesType)this.get_store().add_element_user(DocumentSettingsTypeImpl.SNAPANGLES$6);
        }
    }
    
    public void unsetSnapAngles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.SNAPANGLES$6, 0);
        }
    }
    
    public DynamicGridEnabledType getDynamicGridEnabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DynamicGridEnabledType dynamicGridEnabledType = (DynamicGridEnabledType)this.get_store().find_element_user(DocumentSettingsTypeImpl.DYNAMICGRIDENABLED$8, 0);
            if (dynamicGridEnabledType == null) {
                return null;
            }
            return dynamicGridEnabledType;
        }
    }
    
    public boolean isSetDynamicGridEnabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.DYNAMICGRIDENABLED$8) != 0;
        }
    }
    
    public void setDynamicGridEnabled(final DynamicGridEnabledType dynamicGridEnabledType) {
        this.generatedSetterHelperImpl((XmlObject)dynamicGridEnabledType, DocumentSettingsTypeImpl.DYNAMICGRIDENABLED$8, 0, (short)1);
    }
    
    public DynamicGridEnabledType addNewDynamicGridEnabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DynamicGridEnabledType)this.get_store().add_element_user(DocumentSettingsTypeImpl.DYNAMICGRIDENABLED$8);
        }
    }
    
    public void unsetDynamicGridEnabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.DYNAMICGRIDENABLED$8, 0);
        }
    }
    
    public ProtectStylesType getProtectStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ProtectStylesType protectStylesType = (ProtectStylesType)this.get_store().find_element_user(DocumentSettingsTypeImpl.PROTECTSTYLES$10, 0);
            if (protectStylesType == null) {
                return null;
            }
            return protectStylesType;
        }
    }
    
    public boolean isSetProtectStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.PROTECTSTYLES$10) != 0;
        }
    }
    
    public void setProtectStyles(final ProtectStylesType protectStylesType) {
        this.generatedSetterHelperImpl((XmlObject)protectStylesType, DocumentSettingsTypeImpl.PROTECTSTYLES$10, 0, (short)1);
    }
    
    public ProtectStylesType addNewProtectStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ProtectStylesType)this.get_store().add_element_user(DocumentSettingsTypeImpl.PROTECTSTYLES$10);
        }
    }
    
    public void unsetProtectStyles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.PROTECTSTYLES$10, 0);
        }
    }
    
    public ProtectShapesType getProtectShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ProtectShapesType protectShapesType = (ProtectShapesType)this.get_store().find_element_user(DocumentSettingsTypeImpl.PROTECTSHAPES$12, 0);
            if (protectShapesType == null) {
                return null;
            }
            return protectShapesType;
        }
    }
    
    public boolean isSetProtectShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.PROTECTSHAPES$12) != 0;
        }
    }
    
    public void setProtectShapes(final ProtectShapesType protectShapesType) {
        this.generatedSetterHelperImpl((XmlObject)protectShapesType, DocumentSettingsTypeImpl.PROTECTSHAPES$12, 0, (short)1);
    }
    
    public ProtectShapesType addNewProtectShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ProtectShapesType)this.get_store().add_element_user(DocumentSettingsTypeImpl.PROTECTSHAPES$12);
        }
    }
    
    public void unsetProtectShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.PROTECTSHAPES$12, 0);
        }
    }
    
    public ProtectMastersType getProtectMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ProtectMastersType protectMastersType = (ProtectMastersType)this.get_store().find_element_user(DocumentSettingsTypeImpl.PROTECTMASTERS$14, 0);
            if (protectMastersType == null) {
                return null;
            }
            return protectMastersType;
        }
    }
    
    public boolean isSetProtectMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.PROTECTMASTERS$14) != 0;
        }
    }
    
    public void setProtectMasters(final ProtectMastersType protectMastersType) {
        this.generatedSetterHelperImpl((XmlObject)protectMastersType, DocumentSettingsTypeImpl.PROTECTMASTERS$14, 0, (short)1);
    }
    
    public ProtectMastersType addNewProtectMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ProtectMastersType)this.get_store().add_element_user(DocumentSettingsTypeImpl.PROTECTMASTERS$14);
        }
    }
    
    public void unsetProtectMasters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.PROTECTMASTERS$14, 0);
        }
    }
    
    public ProtectBkgndsType getProtectBkgnds() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ProtectBkgndsType protectBkgndsType = (ProtectBkgndsType)this.get_store().find_element_user(DocumentSettingsTypeImpl.PROTECTBKGNDS$16, 0);
            if (protectBkgndsType == null) {
                return null;
            }
            return protectBkgndsType;
        }
    }
    
    public boolean isSetProtectBkgnds() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.PROTECTBKGNDS$16) != 0;
        }
    }
    
    public void setProtectBkgnds(final ProtectBkgndsType protectBkgndsType) {
        this.generatedSetterHelperImpl((XmlObject)protectBkgndsType, DocumentSettingsTypeImpl.PROTECTBKGNDS$16, 0, (short)1);
    }
    
    public ProtectBkgndsType addNewProtectBkgnds() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ProtectBkgndsType)this.get_store().add_element_user(DocumentSettingsTypeImpl.PROTECTBKGNDS$16);
        }
    }
    
    public void unsetProtectBkgnds() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.PROTECTBKGNDS$16, 0);
        }
    }
    
    public CustomMenusFileType getCustomMenusFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CustomMenusFileType customMenusFileType = (CustomMenusFileType)this.get_store().find_element_user(DocumentSettingsTypeImpl.CUSTOMMENUSFILE$18, 0);
            if (customMenusFileType == null) {
                return null;
            }
            return customMenusFileType;
        }
    }
    
    public boolean isSetCustomMenusFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.CUSTOMMENUSFILE$18) != 0;
        }
    }
    
    public void setCustomMenusFile(final CustomMenusFileType customMenusFileType) {
        this.generatedSetterHelperImpl((XmlObject)customMenusFileType, DocumentSettingsTypeImpl.CUSTOMMENUSFILE$18, 0, (short)1);
    }
    
    public CustomMenusFileType addNewCustomMenusFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CustomMenusFileType)this.get_store().add_element_user(DocumentSettingsTypeImpl.CUSTOMMENUSFILE$18);
        }
    }
    
    public void unsetCustomMenusFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.CUSTOMMENUSFILE$18, 0);
        }
    }
    
    public CustomToolbarsFileType getCustomToolbarsFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CustomToolbarsFileType customToolbarsFileType = (CustomToolbarsFileType)this.get_store().find_element_user(DocumentSettingsTypeImpl.CUSTOMTOOLBARSFILE$20, 0);
            if (customToolbarsFileType == null) {
                return null;
            }
            return customToolbarsFileType;
        }
    }
    
    public boolean isSetCustomToolbarsFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.CUSTOMTOOLBARSFILE$20) != 0;
        }
    }
    
    public void setCustomToolbarsFile(final CustomToolbarsFileType customToolbarsFileType) {
        this.generatedSetterHelperImpl((XmlObject)customToolbarsFileType, DocumentSettingsTypeImpl.CUSTOMTOOLBARSFILE$20, 0, (short)1);
    }
    
    public CustomToolbarsFileType addNewCustomToolbarsFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CustomToolbarsFileType)this.get_store().add_element_user(DocumentSettingsTypeImpl.CUSTOMTOOLBARSFILE$20);
        }
    }
    
    public void unsetCustomToolbarsFile() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.CUSTOMTOOLBARSFILE$20, 0);
        }
    }
    
    public AttachedToolbarsType getAttachedToolbars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final AttachedToolbarsType attachedToolbarsType = (AttachedToolbarsType)this.get_store().find_element_user(DocumentSettingsTypeImpl.ATTACHEDTOOLBARS$22, 0);
            if (attachedToolbarsType == null) {
                return null;
            }
            return attachedToolbarsType;
        }
    }
    
    public boolean isSetAttachedToolbars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(DocumentSettingsTypeImpl.ATTACHEDTOOLBARS$22) != 0;
        }
    }
    
    public void setAttachedToolbars(final AttachedToolbarsType attachedToolbarsType) {
        this.generatedSetterHelperImpl((XmlObject)attachedToolbarsType, DocumentSettingsTypeImpl.ATTACHEDTOOLBARS$22, 0, (short)1);
    }
    
    public AttachedToolbarsType addNewAttachedToolbars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AttachedToolbarsType)this.get_store().add_element_user(DocumentSettingsTypeImpl.ATTACHEDTOOLBARS$22);
        }
    }
    
    public void unsetAttachedToolbars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(DocumentSettingsTypeImpl.ATTACHEDTOOLBARS$22, 0);
        }
    }
    
    public long getTopPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTopPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
        }
    }
    
    public boolean isSetTopPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24) != null;
        }
    }
    
    public void setTopPage(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTopPage(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.TOPPAGE$24);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTopPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(DocumentSettingsTypeImpl.TOPPAGE$24);
        }
    }
    
    public long getDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
        }
    }
    
    public boolean isSetDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26) != null;
        }
    }
    
    public void setDefaultTextStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDefaultTextStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDefaultTextStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(DocumentSettingsTypeImpl.DEFAULTTEXTSTYLE$26);
        }
    }
    
    public long getDefaultLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDefaultLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
        }
    }
    
    public boolean isSetDefaultLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28) != null;
        }
    }
    
    public void setDefaultLineStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDefaultLineStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDefaultLineStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(DocumentSettingsTypeImpl.DEFAULTLINESTYLE$28);
        }
    }
    
    public long getDefaultFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDefaultFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
        }
    }
    
    public boolean isSetDefaultFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30) != null;
        }
    }
    
    public void setDefaultFillStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDefaultFillStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDefaultFillStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(DocumentSettingsTypeImpl.DEFAULTFILLSTYLE$30);
        }
    }
    
    public long getDefaultGuideStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetDefaultGuideStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
        }
    }
    
    public boolean isSetDefaultGuideStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32) != null;
        }
    }
    
    public void setDefaultGuideStyle(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDefaultGuideStyle(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetDefaultGuideStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(DocumentSettingsTypeImpl.DEFAULTGUIDESTYLE$32);
        }
    }
    
    static {
        GLUESETTINGS$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "GlueSettings");
        SNAPSETTINGS$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "SnapSettings");
        SNAPEXTENSIONS$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "SnapExtensions");
        SNAPANGLES$6 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "SnapAngles");
        DYNAMICGRIDENABLED$8 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "DynamicGridEnabled");
        PROTECTSTYLES$10 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "ProtectStyles");
        PROTECTSHAPES$12 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "ProtectShapes");
        PROTECTMASTERS$14 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "ProtectMasters");
        PROTECTBKGNDS$16 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "ProtectBkgnds");
        CUSTOMMENUSFILE$18 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "CustomMenusFile");
        CUSTOMTOOLBARSFILE$20 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "CustomToolbarsFile");
        ATTACHEDTOOLBARS$22 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "AttachedToolbars");
        TOPPAGE$24 = new QName("", "TopPage");
        DEFAULTTEXTSTYLE$26 = new QName("", "DefaultTextStyle");
        DEFAULTLINESTYLE$28 = new QName("", "DefaultLineStyle");
        DEFAULTFILLSTYLE$30 = new QName("", "DefaultFillStyle");
        DEFAULTGUIDESTYLE$32 = new QName("", "DefaultGuideStyle");
    }
}
