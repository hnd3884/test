package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMapInfo;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.MapInfoDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class MapInfoDocumentImpl extends XmlComplexContentImpl implements MapInfoDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName MAPINFO$0;
    
    public MapInfoDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTMapInfo getMapInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMapInfo ctMapInfo = (CTMapInfo)this.get_store().find_element_user(MapInfoDocumentImpl.MAPINFO$0, 0);
            if (ctMapInfo == null) {
                return null;
            }
            return ctMapInfo;
        }
    }
    
    public void setMapInfo(final CTMapInfo ctMapInfo) {
        this.generatedSetterHelperImpl((XmlObject)ctMapInfo, MapInfoDocumentImpl.MAPINFO$0, 0, (short)1);
    }
    
    public CTMapInfo addNewMapInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMapInfo)this.get_store().add_element_user(MapInfoDocumentImpl.MAPINFO$0);
        }
    }
    
    static {
        MAPINFO$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "MapInfo");
    }
}
