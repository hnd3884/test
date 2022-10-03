package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.SettingsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SettingsDocumentImpl extends XmlComplexContentImpl implements SettingsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SETTINGS$0;
    
    public SettingsDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSettings getSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSettings ctSettings = (CTSettings)this.get_store().find_element_user(SettingsDocumentImpl.SETTINGS$0, 0);
            if (ctSettings == null) {
                return null;
            }
            return ctSettings;
        }
    }
    
    public void setSettings(final CTSettings ctSettings) {
        this.generatedSetterHelperImpl((XmlObject)ctSettings, SettingsDocumentImpl.SETTINGS$0, 0, (short)1);
    }
    
    public CTSettings addNewSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSettings)this.get_store().add_element_user(SettingsDocumentImpl.SETTINGS$0);
        }
    }
    
    static {
        SETTINGS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "settings");
    }
}
