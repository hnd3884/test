package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTHyperlinkImpl extends XmlComplexContentImpl implements CTHyperlink
{
    private static final long serialVersionUID = 1L;
    private static final QName SND$0;
    private static final QName EXTLST$2;
    private static final QName ID$4;
    private static final QName INVALIDURL$6;
    private static final QName ACTION$8;
    private static final QName TGTFRAME$10;
    private static final QName TOOLTIP$12;
    private static final QName HISTORY$14;
    private static final QName HIGHLIGHTCLICK$16;
    private static final QName ENDSND$18;
    
    public CTHyperlinkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTEmbeddedWAVAudioFile getSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmbeddedWAVAudioFile ctEmbeddedWAVAudioFile = (CTEmbeddedWAVAudioFile)this.get_store().find_element_user(CTHyperlinkImpl.SND$0, 0);
            if (ctEmbeddedWAVAudioFile == null) {
                return null;
            }
            return ctEmbeddedWAVAudioFile;
        }
    }
    
    public boolean isSetSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.SND$0) != 0;
        }
    }
    
    public void setSnd(final CTEmbeddedWAVAudioFile ctEmbeddedWAVAudioFile) {
        this.generatedSetterHelperImpl((XmlObject)ctEmbeddedWAVAudioFile, CTHyperlinkImpl.SND$0, 0, (short)1);
    }
    
    public CTEmbeddedWAVAudioFile addNewSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmbeddedWAVAudioFile)this.get_store().add_element_user(CTHyperlinkImpl.SND$0);
        }
    }
    
    public void unsetSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.SND$0, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTHyperlinkImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTHyperlinkImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTHyperlinkImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTHyperlinkImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTHyperlinkImpl.EXTLST$2, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$4);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ID$4) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTHyperlinkImpl.ID$4);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTHyperlinkImpl.ID$4);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ID$4);
        }
    }
    
    public String getInvalidUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.INVALIDURL$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetInvalidUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTHyperlinkImpl.INVALIDURL$6);
            }
            return xmlString;
        }
    }
    
    public boolean isSetInvalidUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.INVALIDURL$6) != null;
        }
    }
    
    public void setInvalidUrl(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetInvalidUrl(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHyperlinkImpl.INVALIDURL$6);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetInvalidUrl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.INVALIDURL$6);
        }
    }
    
    public String getAction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ACTION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.ACTION$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.ACTION$8);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTHyperlinkImpl.ACTION$8);
            }
            return xmlString;
        }
    }
    
    public boolean isSetAction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ACTION$8) != null;
        }
    }
    
    public void setAction(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ACTION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ACTION$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAction(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.ACTION$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHyperlinkImpl.ACTION$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ACTION$8);
        }
    }
    
    public String getTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.TGTFRAME$10);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTHyperlinkImpl.TGTFRAME$10);
            }
            return xmlString;
        }
    }
    
    public boolean isSetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$10) != null;
        }
    }
    
    public void setTgtFrame(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTgtFrame(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHyperlinkImpl.TGTFRAME$10);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTgtFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.TGTFRAME$10);
        }
    }
    
    public String getTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.TOOLTIP$12);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTHyperlinkImpl.TOOLTIP$12);
            }
            return xmlString;
        }
    }
    
    public boolean isSetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$12) != null;
        }
    }
    
    public void setTooltip(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTooltip(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTHyperlinkImpl.TOOLTIP$12);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTooltip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.TOOLTIP$12);
        }
    }
    
    public boolean getHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.HISTORY$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHyperlinkImpl.HISTORY$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$14) != null;
        }
    }
    
    public void setHistory(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.HISTORY$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHistory(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.HISTORY$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHyperlinkImpl.HISTORY$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHistory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.HISTORY$14);
        }
    }
    
    public boolean getHighlightClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHighlightClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHighlightClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16) != null;
        }
    }
    
    public void setHighlightClick(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHighlightClick(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHighlightClick() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.HIGHLIGHTCLICK$16);
        }
    }
    
    public boolean getEndSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ENDSND$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTHyperlinkImpl.ENDSND$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEndSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.ENDSND$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTHyperlinkImpl.ENDSND$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEndSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTHyperlinkImpl.ENDSND$18) != null;
        }
    }
    
    public void setEndSnd(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTHyperlinkImpl.ENDSND$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTHyperlinkImpl.ENDSND$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEndSnd(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTHyperlinkImpl.ENDSND$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTHyperlinkImpl.ENDSND$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEndSnd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTHyperlinkImpl.ENDSND$18);
        }
    }
    
    static {
        SND$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "snd");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        ID$4 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        INVALIDURL$6 = new QName("", "invalidUrl");
        ACTION$8 = new QName("", "action");
        TGTFRAME$10 = new QName("", "tgtFrame");
        TOOLTIP$12 = new QName("", "tooltip");
        HISTORY$14 = new QName("", "history");
        HIGHLIGHTCLICK$16 = new QName("", "highlightClick");
        ENDSND$18 = new QName("", "endSnd");
    }
}
