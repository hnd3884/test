package com.microsoft.schemas.office.visio.x2012.main.impl;

import com.microsoft.schemas.office.visio.x2012.main.PublishSettingsType;
import com.microsoft.schemas.office.visio.x2012.main.HeaderFooterType;
import com.microsoft.schemas.office.visio.x2012.main.EventListType;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSheetType;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetsType;
import com.microsoft.schemas.office.visio.x2012.main.FaceNamesType;
import com.microsoft.schemas.office.visio.x2012.main.ColorsType;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.visio.x2012.main.DocumentSettingsType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.visio.x2012.main.VisioDocumentType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class VisioDocumentTypeImpl extends XmlComplexContentImpl implements VisioDocumentType
{
    private static final long serialVersionUID = 1L;
    private static final QName DOCUMENTSETTINGS$0;
    private static final QName COLORS$2;
    private static final QName FACENAMES$4;
    private static final QName STYLESHEETS$6;
    private static final QName DOCUMENTSHEET$8;
    private static final QName EVENTLIST$10;
    private static final QName HEADERFOOTER$12;
    private static final QName PUBLISHSETTINGS$14;
    
    public VisioDocumentTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public DocumentSettingsType getDocumentSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DocumentSettingsType documentSettingsType = (DocumentSettingsType)this.get_store().find_element_user(VisioDocumentTypeImpl.DOCUMENTSETTINGS$0, 0);
            if (documentSettingsType == null) {
                return null;
            }
            return documentSettingsType;
        }
    }
    
    public boolean isSetDocumentSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.DOCUMENTSETTINGS$0) != 0;
        }
    }
    
    public void setDocumentSettings(final DocumentSettingsType documentSettingsType) {
        this.generatedSetterHelperImpl((XmlObject)documentSettingsType, VisioDocumentTypeImpl.DOCUMENTSETTINGS$0, 0, (short)1);
    }
    
    public DocumentSettingsType addNewDocumentSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DocumentSettingsType)this.get_store().add_element_user(VisioDocumentTypeImpl.DOCUMENTSETTINGS$0);
        }
    }
    
    public void unsetDocumentSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.DOCUMENTSETTINGS$0, 0);
        }
    }
    
    public ColorsType getColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ColorsType colorsType = (ColorsType)this.get_store().find_element_user(VisioDocumentTypeImpl.COLORS$2, 0);
            if (colorsType == null) {
                return null;
            }
            return colorsType;
        }
    }
    
    public boolean isSetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.COLORS$2) != 0;
        }
    }
    
    public void setColors(final ColorsType colorsType) {
        this.generatedSetterHelperImpl((XmlObject)colorsType, VisioDocumentTypeImpl.COLORS$2, 0, (short)1);
    }
    
    public ColorsType addNewColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ColorsType)this.get_store().add_element_user(VisioDocumentTypeImpl.COLORS$2);
        }
    }
    
    public void unsetColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.COLORS$2, 0);
        }
    }
    
    public FaceNamesType getFaceNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final FaceNamesType faceNamesType = (FaceNamesType)this.get_store().find_element_user(VisioDocumentTypeImpl.FACENAMES$4, 0);
            if (faceNamesType == null) {
                return null;
            }
            return faceNamesType;
        }
    }
    
    public boolean isSetFaceNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.FACENAMES$4) != 0;
        }
    }
    
    public void setFaceNames(final FaceNamesType faceNamesType) {
        this.generatedSetterHelperImpl((XmlObject)faceNamesType, VisioDocumentTypeImpl.FACENAMES$4, 0, (short)1);
    }
    
    public FaceNamesType addNewFaceNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (FaceNamesType)this.get_store().add_element_user(VisioDocumentTypeImpl.FACENAMES$4);
        }
    }
    
    public void unsetFaceNames() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.FACENAMES$4, 0);
        }
    }
    
    public StyleSheetsType getStyleSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final StyleSheetsType styleSheetsType = (StyleSheetsType)this.get_store().find_element_user(VisioDocumentTypeImpl.STYLESHEETS$6, 0);
            if (styleSheetsType == null) {
                return null;
            }
            return styleSheetsType;
        }
    }
    
    public boolean isSetStyleSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.STYLESHEETS$6) != 0;
        }
    }
    
    public void setStyleSheets(final StyleSheetsType styleSheetsType) {
        this.generatedSetterHelperImpl((XmlObject)styleSheetsType, VisioDocumentTypeImpl.STYLESHEETS$6, 0, (short)1);
    }
    
    public StyleSheetsType addNewStyleSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (StyleSheetsType)this.get_store().add_element_user(VisioDocumentTypeImpl.STYLESHEETS$6);
        }
    }
    
    public void unsetStyleSheets() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.STYLESHEETS$6, 0);
        }
    }
    
    public DocumentSheetType getDocumentSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final DocumentSheetType documentSheetType = (DocumentSheetType)this.get_store().find_element_user(VisioDocumentTypeImpl.DOCUMENTSHEET$8, 0);
            if (documentSheetType == null) {
                return null;
            }
            return documentSheetType;
        }
    }
    
    public boolean isSetDocumentSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.DOCUMENTSHEET$8) != 0;
        }
    }
    
    public void setDocumentSheet(final DocumentSheetType documentSheetType) {
        this.generatedSetterHelperImpl((XmlObject)documentSheetType, VisioDocumentTypeImpl.DOCUMENTSHEET$8, 0, (short)1);
    }
    
    public DocumentSheetType addNewDocumentSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (DocumentSheetType)this.get_store().add_element_user(VisioDocumentTypeImpl.DOCUMENTSHEET$8);
        }
    }
    
    public void unsetDocumentSheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.DOCUMENTSHEET$8, 0);
        }
    }
    
    public EventListType getEventList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final EventListType eventListType = (EventListType)this.get_store().find_element_user(VisioDocumentTypeImpl.EVENTLIST$10, 0);
            if (eventListType == null) {
                return null;
            }
            return eventListType;
        }
    }
    
    public boolean isSetEventList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.EVENTLIST$10) != 0;
        }
    }
    
    public void setEventList(final EventListType eventListType) {
        this.generatedSetterHelperImpl((XmlObject)eventListType, VisioDocumentTypeImpl.EVENTLIST$10, 0, (short)1);
    }
    
    public EventListType addNewEventList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EventListType)this.get_store().add_element_user(VisioDocumentTypeImpl.EVENTLIST$10);
        }
    }
    
    public void unsetEventList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.EVENTLIST$10, 0);
        }
    }
    
    public HeaderFooterType getHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final HeaderFooterType headerFooterType = (HeaderFooterType)this.get_store().find_element_user(VisioDocumentTypeImpl.HEADERFOOTER$12, 0);
            if (headerFooterType == null) {
                return null;
            }
            return headerFooterType;
        }
    }
    
    public boolean isSetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.HEADERFOOTER$12) != 0;
        }
    }
    
    public void setHeaderFooter(final HeaderFooterType headerFooterType) {
        this.generatedSetterHelperImpl((XmlObject)headerFooterType, VisioDocumentTypeImpl.HEADERFOOTER$12, 0, (short)1);
    }
    
    public HeaderFooterType addNewHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (HeaderFooterType)this.get_store().add_element_user(VisioDocumentTypeImpl.HEADERFOOTER$12);
        }
    }
    
    public void unsetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.HEADERFOOTER$12, 0);
        }
    }
    
    public PublishSettingsType getPublishSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final PublishSettingsType publishSettingsType = (PublishSettingsType)this.get_store().find_element_user(VisioDocumentTypeImpl.PUBLISHSETTINGS$14, 0);
            if (publishSettingsType == null) {
                return null;
            }
            return publishSettingsType;
        }
    }
    
    public boolean isSetPublishSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(VisioDocumentTypeImpl.PUBLISHSETTINGS$14) != 0;
        }
    }
    
    public void setPublishSettings(final PublishSettingsType publishSettingsType) {
        this.generatedSetterHelperImpl((XmlObject)publishSettingsType, VisioDocumentTypeImpl.PUBLISHSETTINGS$14, 0, (short)1);
    }
    
    public PublishSettingsType addNewPublishSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (PublishSettingsType)this.get_store().add_element_user(VisioDocumentTypeImpl.PUBLISHSETTINGS$14);
        }
    }
    
    public void unsetPublishSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(VisioDocumentTypeImpl.PUBLISHSETTINGS$14, 0);
        }
    }
    
    static {
        DOCUMENTSETTINGS$0 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "DocumentSettings");
        COLORS$2 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "Colors");
        FACENAMES$4 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "FaceNames");
        STYLESHEETS$6 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "StyleSheets");
        DOCUMENTSHEET$8 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "DocumentSheet");
        EVENTLIST$10 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "EventList");
        HEADERFOOTER$12 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "HeaderFooter");
        PUBLISHSETTINGS$14 = new QName("http://schemas.microsoft.com/office/visio/2012/main", "PublishSettings");
    }
}
