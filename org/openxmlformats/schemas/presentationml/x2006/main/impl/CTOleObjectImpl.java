package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate32;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeID;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectLink;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObjectEmbed;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTOleObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOleObjectImpl extends XmlComplexContentImpl implements CTOleObject
{
    private static final long serialVersionUID = 1L;
    private static final QName EMBED$0;
    private static final QName LINK$2;
    private static final QName SPID$4;
    private static final QName NAME$6;
    private static final QName SHOWASICON$8;
    private static final QName ID$10;
    private static final QName IMGW$12;
    private static final QName IMGH$14;
    private static final QName PROGID$16;
    
    public CTOleObjectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOleObjectEmbed getEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleObjectEmbed ctOleObjectEmbed = (CTOleObjectEmbed)this.get_store().find_element_user(CTOleObjectImpl.EMBED$0, 0);
            if (ctOleObjectEmbed == null) {
                return null;
            }
            return ctOleObjectEmbed;
        }
    }
    
    public boolean isSetEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOleObjectImpl.EMBED$0) != 0;
        }
    }
    
    public void setEmbed(final CTOleObjectEmbed ctOleObjectEmbed) {
        this.generatedSetterHelperImpl((XmlObject)ctOleObjectEmbed, CTOleObjectImpl.EMBED$0, 0, (short)1);
    }
    
    public CTOleObjectEmbed addNewEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObjectEmbed)this.get_store().add_element_user(CTOleObjectImpl.EMBED$0);
        }
    }
    
    public void unsetEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOleObjectImpl.EMBED$0, 0);
        }
    }
    
    public CTOleObjectLink getLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleObjectLink ctOleObjectLink = (CTOleObjectLink)this.get_store().find_element_user(CTOleObjectImpl.LINK$2, 0);
            if (ctOleObjectLink == null) {
                return null;
            }
            return ctOleObjectLink;
        }
    }
    
    public boolean isSetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOleObjectImpl.LINK$2) != 0;
        }
    }
    
    public void setLink(final CTOleObjectLink ctOleObjectLink) {
        this.generatedSetterHelperImpl((XmlObject)ctOleObjectLink, CTOleObjectImpl.LINK$2, 0, (short)1);
    }
    
    public CTOleObjectLink addNewLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleObjectLink)this.get_store().add_element_user(CTOleObjectImpl.LINK$2);
        }
    }
    
    public void unsetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOleObjectImpl.LINK$2, 0);
        }
    }
    
    public String getSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SPID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STShapeID xgetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STShapeID)this.get_store().find_attribute_user(CTOleObjectImpl.SPID$4);
        }
    }
    
    public void setSpid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SPID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.SPID$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSpid(final STShapeID stShapeID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShapeID stShapeID2 = (STShapeID)this.get_store().find_attribute_user(CTOleObjectImpl.SPID$4);
            if (stShapeID2 == null) {
                stShapeID2 = (STShapeID)this.get_store().add_attribute_user(CTOleObjectImpl.SPID$4);
            }
            stShapeID2.set((XmlObject)stShapeID);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.NAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOleObjectImpl.NAME$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.NAME$6);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTOleObjectImpl.NAME$6);
            }
            return xmlString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.NAME$6) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.NAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.NAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.NAME$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTOleObjectImpl.NAME$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.NAME$6);
        }
    }
    
    public boolean getShowAsIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTOleObjectImpl.SHOWASICON$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowAsIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTOleObjectImpl.SHOWASICON$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowAsIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.SHOWASICON$8) != null;
        }
    }
    
    public void setShowAsIcon(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowAsIcon(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTOleObjectImpl.SHOWASICON$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowAsIcon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.SHOWASICON$8);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.ID$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTOleObjectImpl.ID$10);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.ID$10) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.ID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.ID$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTOleObjectImpl.ID$10);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTOleObjectImpl.ID$10);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.ID$10);
        }
    }
    
    public int getImgW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.IMGW$12);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveCoordinate32 xgetImgW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveCoordinate32)this.get_store().find_attribute_user(CTOleObjectImpl.IMGW$12);
        }
    }
    
    public boolean isSetImgW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.IMGW$12) != null;
        }
    }
    
    public void setImgW(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.IMGW$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.IMGW$12);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetImgW(final STPositiveCoordinate32 stPositiveCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate32 stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().find_attribute_user(CTOleObjectImpl.IMGW$12);
            if (stPositiveCoordinate33 == null) {
                stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().add_attribute_user(CTOleObjectImpl.IMGW$12);
            }
            stPositiveCoordinate33.set((XmlObject)stPositiveCoordinate32);
        }
    }
    
    public void unsetImgW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.IMGW$12);
        }
    }
    
    public int getImgH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.IMGH$14);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositiveCoordinate32 xgetImgH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositiveCoordinate32)this.get_store().find_attribute_user(CTOleObjectImpl.IMGH$14);
        }
    }
    
    public boolean isSetImgH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.IMGH$14) != null;
        }
    }
    
    public void setImgH(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.IMGH$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.IMGH$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetImgH(final STPositiveCoordinate32 stPositiveCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate32 stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().find_attribute_user(CTOleObjectImpl.IMGH$14);
            if (stPositiveCoordinate33 == null) {
                stPositiveCoordinate33 = (STPositiveCoordinate32)this.get_store().add_attribute_user(CTOleObjectImpl.IMGH$14);
            }
            stPositiveCoordinate33.set((XmlObject)stPositiveCoordinate32);
        }
    }
    
    public void unsetImgH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.IMGH$14);
        }
    }
    
    public String getProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$16);
        }
    }
    
    public boolean isSetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$16) != null;
        }
    }
    
    public void setProgId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTOleObjectImpl.PROGID$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetProgId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTOleObjectImpl.PROGID$16);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTOleObjectImpl.PROGID$16);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetProgId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTOleObjectImpl.PROGID$16);
        }
    }
    
    static {
        EMBED$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "embed");
        LINK$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "link");
        SPID$4 = new QName("", "spid");
        NAME$6 = new QName("", "name");
        SHOWASICON$8 = new QName("", "showAsIcon");
        ID$10 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        IMGW$12 = new QName("", "imgW");
        IMGH$14 = new QName("", "imgH");
        PROGID$16 = new QName("", "progId");
    }
}
