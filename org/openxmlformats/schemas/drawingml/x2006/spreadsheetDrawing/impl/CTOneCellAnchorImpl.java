package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAnchorClientData;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTOneCellAnchorImpl extends XmlComplexContentImpl implements CTOneCellAnchor
{
    private static final long serialVersionUID = 1L;
    private static final QName FROM$0;
    private static final QName EXT$2;
    private static final QName SP$4;
    private static final QName GRPSP$6;
    private static final QName GRAPHICFRAME$8;
    private static final QName CXNSP$10;
    private static final QName PIC$12;
    private static final QName CLIENTDATA$14;
    
    public CTOneCellAnchorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTMarker getFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarker ctMarker = (CTMarker)this.get_store().find_element_user(CTOneCellAnchorImpl.FROM$0, 0);
            if (ctMarker == null) {
                return null;
            }
            return ctMarker;
        }
    }
    
    public void setFrom(final CTMarker ctMarker) {
        this.generatedSetterHelperImpl((XmlObject)ctMarker, CTOneCellAnchorImpl.FROM$0, 0, (short)1);
    }
    
    public CTMarker addNewFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarker)this.get_store().add_element_user(CTOneCellAnchorImpl.FROM$0);
        }
    }
    
    public CTPositiveSize2D getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveSize2D ctPositiveSize2D = (CTPositiveSize2D)this.get_store().find_element_user(CTOneCellAnchorImpl.EXT$2, 0);
            if (ctPositiveSize2D == null) {
                return null;
            }
            return ctPositiveSize2D;
        }
    }
    
    public void setExt(final CTPositiveSize2D ctPositiveSize2D) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveSize2D, CTOneCellAnchorImpl.EXT$2, 0, (short)1);
    }
    
    public CTPositiveSize2D addNewExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveSize2D)this.get_store().add_element_user(CTOneCellAnchorImpl.EXT$2);
        }
    }
    
    public CTShape getSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTOneCellAnchorImpl.SP$4, 0);
            if (ctShape == null) {
                return null;
            }
            return ctShape;
        }
    }
    
    public boolean isSetSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOneCellAnchorImpl.SP$4) != 0;
        }
    }
    
    public void setSp(final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTOneCellAnchorImpl.SP$4, 0, (short)1);
    }
    
    public CTShape addNewSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTOneCellAnchorImpl.SP$4);
        }
    }
    
    public void unsetSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOneCellAnchorImpl.SP$4, 0);
        }
    }
    
    public CTGroupShape getGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShape ctGroupShape = (CTGroupShape)this.get_store().find_element_user(CTOneCellAnchorImpl.GRPSP$6, 0);
            if (ctGroupShape == null) {
                return null;
            }
            return ctGroupShape;
        }
    }
    
    public boolean isSetGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOneCellAnchorImpl.GRPSP$6) != 0;
        }
    }
    
    public void setGrpSp(final CTGroupShape ctGroupShape) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShape, CTOneCellAnchorImpl.GRPSP$6, 0, (short)1);
    }
    
    public CTGroupShape addNewGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShape)this.get_store().add_element_user(CTOneCellAnchorImpl.GRPSP$6);
        }
    }
    
    public void unsetGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOneCellAnchorImpl.GRPSP$6, 0);
        }
    }
    
    public CTGraphicalObjectFrame getGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrame ctGraphicalObjectFrame = (CTGraphicalObjectFrame)this.get_store().find_element_user(CTOneCellAnchorImpl.GRAPHICFRAME$8, 0);
            if (ctGraphicalObjectFrame == null) {
                return null;
            }
            return ctGraphicalObjectFrame;
        }
    }
    
    public boolean isSetGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOneCellAnchorImpl.GRAPHICFRAME$8) != 0;
        }
    }
    
    public void setGraphicFrame(final CTGraphicalObjectFrame ctGraphicalObjectFrame) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrame, CTOneCellAnchorImpl.GRAPHICFRAME$8, 0, (short)1);
    }
    
    public CTGraphicalObjectFrame addNewGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrame)this.get_store().add_element_user(CTOneCellAnchorImpl.GRAPHICFRAME$8);
        }
    }
    
    public void unsetGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOneCellAnchorImpl.GRAPHICFRAME$8, 0);
        }
    }
    
    public CTConnector getCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnector ctConnector = (CTConnector)this.get_store().find_element_user(CTOneCellAnchorImpl.CXNSP$10, 0);
            if (ctConnector == null) {
                return null;
            }
            return ctConnector;
        }
    }
    
    public boolean isSetCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOneCellAnchorImpl.CXNSP$10) != 0;
        }
    }
    
    public void setCxnSp(final CTConnector ctConnector) {
        this.generatedSetterHelperImpl((XmlObject)ctConnector, CTOneCellAnchorImpl.CXNSP$10, 0, (short)1);
    }
    
    public CTConnector addNewCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnector)this.get_store().add_element_user(CTOneCellAnchorImpl.CXNSP$10);
        }
    }
    
    public void unsetCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOneCellAnchorImpl.CXNSP$10, 0);
        }
    }
    
    public CTPicture getPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPicture ctPicture = (CTPicture)this.get_store().find_element_user(CTOneCellAnchorImpl.PIC$12, 0);
            if (ctPicture == null) {
                return null;
            }
            return ctPicture;
        }
    }
    
    public boolean isSetPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTOneCellAnchorImpl.PIC$12) != 0;
        }
    }
    
    public void setPic(final CTPicture ctPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctPicture, CTOneCellAnchorImpl.PIC$12, 0, (short)1);
    }
    
    public CTPicture addNewPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().add_element_user(CTOneCellAnchorImpl.PIC$12);
        }
    }
    
    public void unsetPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTOneCellAnchorImpl.PIC$12, 0);
        }
    }
    
    public CTAnchorClientData getClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchorClientData ctAnchorClientData = (CTAnchorClientData)this.get_store().find_element_user(CTOneCellAnchorImpl.CLIENTDATA$14, 0);
            if (ctAnchorClientData == null) {
                return null;
            }
            return ctAnchorClientData;
        }
    }
    
    public void setClientData(final CTAnchorClientData ctAnchorClientData) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchorClientData, CTOneCellAnchorImpl.CLIENTDATA$14, 0, (short)1);
    }
    
    public CTAnchorClientData addNewClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorClientData)this.get_store().add_element_user(CTOneCellAnchorImpl.CLIENTDATA$14);
        }
    }
    
    static {
        FROM$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "from");
        EXT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "ext");
        SP$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "sp");
        GRPSP$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "grpSp");
        GRAPHICFRAME$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "graphicFrame");
        CXNSP$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cxnSp");
        PIC$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "pic");
        CLIENTDATA$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "clientData");
    }
}
