package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextListStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterTextStyles;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSlideMasterTextStylesImpl extends XmlComplexContentImpl implements CTSlideMasterTextStyles
{
    private static final long serialVersionUID = 1L;
    private static final QName TITLESTYLE$0;
    private static final QName BODYSTYLE$2;
    private static final QName OTHERSTYLE$4;
    private static final QName EXTLST$6;
    
    public CTSlideMasterTextStylesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextListStyle getTitleStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTSlideMasterTextStylesImpl.TITLESTYLE$0, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetTitleStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterTextStylesImpl.TITLESTYLE$0) != 0;
        }
    }
    
    public void setTitleStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTSlideMasterTextStylesImpl.TITLESTYLE$0, 0, (short)1);
    }
    
    public CTTextListStyle addNewTitleStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTSlideMasterTextStylesImpl.TITLESTYLE$0);
        }
    }
    
    public void unsetTitleStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterTextStylesImpl.TITLESTYLE$0, 0);
        }
    }
    
    public CTTextListStyle getBodyStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTSlideMasterTextStylesImpl.BODYSTYLE$2, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetBodyStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterTextStylesImpl.BODYSTYLE$2) != 0;
        }
    }
    
    public void setBodyStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTSlideMasterTextStylesImpl.BODYSTYLE$2, 0, (short)1);
    }
    
    public CTTextListStyle addNewBodyStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTSlideMasterTextStylesImpl.BODYSTYLE$2);
        }
    }
    
    public void unsetBodyStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterTextStylesImpl.BODYSTYLE$2, 0);
        }
    }
    
    public CTTextListStyle getOtherStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextListStyle ctTextListStyle = (CTTextListStyle)this.get_store().find_element_user(CTSlideMasterTextStylesImpl.OTHERSTYLE$4, 0);
            if (ctTextListStyle == null) {
                return null;
            }
            return ctTextListStyle;
        }
    }
    
    public boolean isSetOtherStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterTextStylesImpl.OTHERSTYLE$4) != 0;
        }
    }
    
    public void setOtherStyle(final CTTextListStyle ctTextListStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTextListStyle, CTSlideMasterTextStylesImpl.OTHERSTYLE$4, 0, (short)1);
    }
    
    public CTTextListStyle addNewOtherStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextListStyle)this.get_store().add_element_user(CTSlideMasterTextStylesImpl.OTHERSTYLE$4);
        }
    }
    
    public void unsetOtherStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterTextStylesImpl.OTHERSTYLE$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSlideMasterTextStylesImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSlideMasterTextStylesImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSlideMasterTextStylesImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSlideMasterTextStylesImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSlideMasterTextStylesImpl.EXTLST$6, 0);
        }
    }
    
    static {
        TITLESTYLE$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "titleStyle");
        BODYSTYLE$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bodyStyle");
        OTHERSTYLE$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "otherStyle");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
