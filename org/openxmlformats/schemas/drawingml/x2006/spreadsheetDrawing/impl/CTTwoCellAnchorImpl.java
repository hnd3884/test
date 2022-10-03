package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STEditAs;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAnchorClientData;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTwoCellAnchorImpl extends XmlComplexContentImpl implements CTTwoCellAnchor
{
    private static final long serialVersionUID = 1L;
    private static final QName FROM$0;
    private static final QName TO$2;
    private static final QName SP$4;
    private static final QName GRPSP$6;
    private static final QName GRAPHICFRAME$8;
    private static final QName CXNSP$10;
    private static final QName PIC$12;
    private static final QName CLIENTDATA$14;
    private static final QName EDITAS$16;
    
    public CTTwoCellAnchorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTMarker getFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarker ctMarker = (CTMarker)this.get_store().find_element_user(CTTwoCellAnchorImpl.FROM$0, 0);
            if (ctMarker == null) {
                return null;
            }
            return ctMarker;
        }
    }
    
    public void setFrom(final CTMarker ctMarker) {
        this.generatedSetterHelperImpl((XmlObject)ctMarker, CTTwoCellAnchorImpl.FROM$0, 0, (short)1);
    }
    
    public CTMarker addNewFrom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarker)this.get_store().add_element_user(CTTwoCellAnchorImpl.FROM$0);
        }
    }
    
    public CTMarker getTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarker ctMarker = (CTMarker)this.get_store().find_element_user(CTTwoCellAnchorImpl.TO$2, 0);
            if (ctMarker == null) {
                return null;
            }
            return ctMarker;
        }
    }
    
    public void setTo(final CTMarker ctMarker) {
        this.generatedSetterHelperImpl((XmlObject)ctMarker, CTTwoCellAnchorImpl.TO$2, 0, (short)1);
    }
    
    public CTMarker addNewTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarker)this.get_store().add_element_user(CTTwoCellAnchorImpl.TO$2);
        }
    }
    
    public CTShape getSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTTwoCellAnchorImpl.SP$4, 0);
            if (ctShape == null) {
                return null;
            }
            return ctShape;
        }
    }
    
    public boolean isSetSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTwoCellAnchorImpl.SP$4) != 0;
        }
    }
    
    public void setSp(final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTTwoCellAnchorImpl.SP$4, 0, (short)1);
    }
    
    public CTShape addNewSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTTwoCellAnchorImpl.SP$4);
        }
    }
    
    public void unsetSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTwoCellAnchorImpl.SP$4, 0);
        }
    }
    
    public CTGroupShape getGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShape ctGroupShape = (CTGroupShape)this.get_store().find_element_user(CTTwoCellAnchorImpl.GRPSP$6, 0);
            if (ctGroupShape == null) {
                return null;
            }
            return ctGroupShape;
        }
    }
    
    public boolean isSetGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTwoCellAnchorImpl.GRPSP$6) != 0;
        }
    }
    
    public void setGrpSp(final CTGroupShape ctGroupShape) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShape, CTTwoCellAnchorImpl.GRPSP$6, 0, (short)1);
    }
    
    public CTGroupShape addNewGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShape)this.get_store().add_element_user(CTTwoCellAnchorImpl.GRPSP$6);
        }
    }
    
    public void unsetGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTwoCellAnchorImpl.GRPSP$6, 0);
        }
    }
    
    public CTGraphicalObjectFrame getGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrame ctGraphicalObjectFrame = (CTGraphicalObjectFrame)this.get_store().find_element_user(CTTwoCellAnchorImpl.GRAPHICFRAME$8, 0);
            if (ctGraphicalObjectFrame == null) {
                return null;
            }
            return ctGraphicalObjectFrame;
        }
    }
    
    public boolean isSetGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTwoCellAnchorImpl.GRAPHICFRAME$8) != 0;
        }
    }
    
    public void setGraphicFrame(final CTGraphicalObjectFrame ctGraphicalObjectFrame) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrame, CTTwoCellAnchorImpl.GRAPHICFRAME$8, 0, (short)1);
    }
    
    public CTGraphicalObjectFrame addNewGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrame)this.get_store().add_element_user(CTTwoCellAnchorImpl.GRAPHICFRAME$8);
        }
    }
    
    public void unsetGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTwoCellAnchorImpl.GRAPHICFRAME$8, 0);
        }
    }
    
    public CTConnector getCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnector ctConnector = (CTConnector)this.get_store().find_element_user(CTTwoCellAnchorImpl.CXNSP$10, 0);
            if (ctConnector == null) {
                return null;
            }
            return ctConnector;
        }
    }
    
    public boolean isSetCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTwoCellAnchorImpl.CXNSP$10) != 0;
        }
    }
    
    public void setCxnSp(final CTConnector ctConnector) {
        this.generatedSetterHelperImpl((XmlObject)ctConnector, CTTwoCellAnchorImpl.CXNSP$10, 0, (short)1);
    }
    
    public CTConnector addNewCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnector)this.get_store().add_element_user(CTTwoCellAnchorImpl.CXNSP$10);
        }
    }
    
    public void unsetCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTwoCellAnchorImpl.CXNSP$10, 0);
        }
    }
    
    public CTPicture getPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPicture ctPicture = (CTPicture)this.get_store().find_element_user(CTTwoCellAnchorImpl.PIC$12, 0);
            if (ctPicture == null) {
                return null;
            }
            return ctPicture;
        }
    }
    
    public boolean isSetPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTwoCellAnchorImpl.PIC$12) != 0;
        }
    }
    
    public void setPic(final CTPicture ctPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctPicture, CTTwoCellAnchorImpl.PIC$12, 0, (short)1);
    }
    
    public CTPicture addNewPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().add_element_user(CTTwoCellAnchorImpl.PIC$12);
        }
    }
    
    public void unsetPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTwoCellAnchorImpl.PIC$12, 0);
        }
    }
    
    public CTAnchorClientData getClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchorClientData ctAnchorClientData = (CTAnchorClientData)this.get_store().find_element_user(CTTwoCellAnchorImpl.CLIENTDATA$14, 0);
            if (ctAnchorClientData == null) {
                return null;
            }
            return ctAnchorClientData;
        }
    }
    
    public void setClientData(final CTAnchorClientData ctAnchorClientData) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchorClientData, CTTwoCellAnchorImpl.CLIENTDATA$14, 0, (short)1);
    }
    
    public CTAnchorClientData addNewClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorClientData)this.get_store().add_element_user(CTTwoCellAnchorImpl.CLIENTDATA$14);
        }
    }
    
    public STEditAs.Enum getEditAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTwoCellAnchorImpl.EDITAS$16);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STEditAs.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STEditAs xgetEditAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEditAs stEditAs = (STEditAs)this.get_store().find_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            if (stEditAs == null) {
                stEditAs = (STEditAs)this.get_default_attribute_value(CTTwoCellAnchorImpl.EDITAS$16);
            }
            return stEditAs;
        }
    }
    
    public boolean isSetEditAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTwoCellAnchorImpl.EDITAS$16) != null;
        }
    }
    
    public void setEditAs(final STEditAs.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEditAs(final STEditAs stEditAs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEditAs stEditAs2 = (STEditAs)this.get_store().find_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            if (stEditAs2 == null) {
                stEditAs2 = (STEditAs)this.get_store().add_attribute_user(CTTwoCellAnchorImpl.EDITAS$16);
            }
            stEditAs2.set((XmlObject)stEditAs);
        }
    }
    
    public void unsetEditAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTwoCellAnchorImpl.EDITAS$16);
        }
    }
    
    static {
        FROM$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "from");
        TO$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "to");
        SP$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "sp");
        GRPSP$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "grpSp");
        GRAPHICFRAME$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "graphicFrame");
        CXNSP$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "cxnSp");
        PIC$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "pic");
        CLIENTDATA$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "clientData");
        EDITAS$16 = new QName("", "editAs");
    }
}
