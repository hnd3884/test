package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.openxmlformats.schemas.presentationml.x2006.main.CTExtensionListModify;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupShapeImpl extends XmlComplexContentImpl implements CTGroupShape
{
    private static final long serialVersionUID = 1L;
    private static final QName NVGRPSPPR$0;
    private static final QName GRPSPPR$2;
    private static final QName SP$4;
    private static final QName GRPSP$6;
    private static final QName GRAPHICFRAME$8;
    private static final QName CXNSP$10;
    private static final QName PIC$12;
    private static final QName EXTLST$14;
    
    public CTGroupShapeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGroupShapeNonVisual getNvGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShapeNonVisual ctGroupShapeNonVisual = (CTGroupShapeNonVisual)this.get_store().find_element_user(CTGroupShapeImpl.NVGRPSPPR$0, 0);
            if (ctGroupShapeNonVisual == null) {
                return null;
            }
            return ctGroupShapeNonVisual;
        }
    }
    
    public void setNvGrpSpPr(final CTGroupShapeNonVisual ctGroupShapeNonVisual) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShapeNonVisual, CTGroupShapeImpl.NVGRPSPPR$0, 0, (short)1);
    }
    
    public CTGroupShapeNonVisual addNewNvGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShapeNonVisual)this.get_store().add_element_user(CTGroupShapeImpl.NVGRPSPPR$0);
        }
    }
    
    public CTGroupShapeProperties getGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShapeProperties ctGroupShapeProperties = (CTGroupShapeProperties)this.get_store().find_element_user(CTGroupShapeImpl.GRPSPPR$2, 0);
            if (ctGroupShapeProperties == null) {
                return null;
            }
            return ctGroupShapeProperties;
        }
    }
    
    public void setGrpSpPr(final CTGroupShapeProperties ctGroupShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShapeProperties, CTGroupShapeImpl.GRPSPPR$2, 0, (short)1);
    }
    
    public CTGroupShapeProperties addNewGrpSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShapeProperties)this.get_store().add_element_user(CTGroupShapeImpl.GRPSPPR$2);
        }
    }
    
    public List<CTShape> getSpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SpList extends AbstractList<CTShape>
            {
                @Override
                public CTShape get(final int n) {
                    return CTGroupShapeImpl.this.getSpArray(n);
                }
                
                @Override
                public CTShape set(final int n, final CTShape ctShape) {
                    final CTShape spArray = CTGroupShapeImpl.this.getSpArray(n);
                    CTGroupShapeImpl.this.setSpArray(n, ctShape);
                    return spArray;
                }
                
                @Override
                public void add(final int n, final CTShape ctShape) {
                    CTGroupShapeImpl.this.insertNewSp(n).set((XmlObject)ctShape);
                }
                
                @Override
                public CTShape remove(final int n) {
                    final CTShape spArray = CTGroupShapeImpl.this.getSpArray(n);
                    CTGroupShapeImpl.this.removeSp(n);
                    return spArray;
                }
                
                @Override
                public int size() {
                    return CTGroupShapeImpl.this.sizeOfSpArray();
                }
            }
            return new SpList();
        }
    }
    
    @Deprecated
    public CTShape[] getSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupShapeImpl.SP$4, (List)list);
            final CTShape[] array = new CTShape[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShape getSpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTGroupShapeImpl.SP$4, n);
            if (ctShape == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShape;
        }
    }
    
    public int sizeOfSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.SP$4);
        }
    }
    
    public void setSpArray(final CTShape[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupShapeImpl.SP$4);
    }
    
    public void setSpArray(final int n, final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTGroupShapeImpl.SP$4, n, (short)2);
    }
    
    public CTShape insertNewSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().insert_element_user(CTGroupShapeImpl.SP$4, n);
        }
    }
    
    public CTShape addNewSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTGroupShapeImpl.SP$4);
        }
    }
    
    public void removeSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.SP$4, n);
        }
    }
    
    public List<CTGroupShape> getGrpSpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GrpSpList extends AbstractList<CTGroupShape>
            {
                @Override
                public CTGroupShape get(final int n) {
                    return CTGroupShapeImpl.this.getGrpSpArray(n);
                }
                
                @Override
                public CTGroupShape set(final int n, final CTGroupShape ctGroupShape) {
                    final CTGroupShape grpSpArray = CTGroupShapeImpl.this.getGrpSpArray(n);
                    CTGroupShapeImpl.this.setGrpSpArray(n, ctGroupShape);
                    return grpSpArray;
                }
                
                @Override
                public void add(final int n, final CTGroupShape ctGroupShape) {
                    CTGroupShapeImpl.this.insertNewGrpSp(n).set((XmlObject)ctGroupShape);
                }
                
                @Override
                public CTGroupShape remove(final int n) {
                    final CTGroupShape grpSpArray = CTGroupShapeImpl.this.getGrpSpArray(n);
                    CTGroupShapeImpl.this.removeGrpSp(n);
                    return grpSpArray;
                }
                
                @Override
                public int size() {
                    return CTGroupShapeImpl.this.sizeOfGrpSpArray();
                }
            }
            return new GrpSpList();
        }
    }
    
    @Deprecated
    public CTGroupShape[] getGrpSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupShapeImpl.GRPSP$6, (List)list);
            final CTGroupShape[] array = new CTGroupShape[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGroupShape getGrpSpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupShape ctGroupShape = (CTGroupShape)this.get_store().find_element_user(CTGroupShapeImpl.GRPSP$6, n);
            if (ctGroupShape == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGroupShape;
        }
    }
    
    public int sizeOfGrpSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.GRPSP$6);
        }
    }
    
    public void setGrpSpArray(final CTGroupShape[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupShapeImpl.GRPSP$6);
    }
    
    public void setGrpSpArray(final int n, final CTGroupShape ctGroupShape) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupShape, CTGroupShapeImpl.GRPSP$6, n, (short)2);
    }
    
    public CTGroupShape insertNewGrpSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShape)this.get_store().insert_element_user(CTGroupShapeImpl.GRPSP$6, n);
        }
    }
    
    public CTGroupShape addNewGrpSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupShape)this.get_store().add_element_user(CTGroupShapeImpl.GRPSP$6);
        }
    }
    
    public void removeGrpSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.GRPSP$6, n);
        }
    }
    
    public List<CTGraphicalObjectFrame> getGraphicFrameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GraphicFrameList extends AbstractList<CTGraphicalObjectFrame>
            {
                @Override
                public CTGraphicalObjectFrame get(final int n) {
                    return CTGroupShapeImpl.this.getGraphicFrameArray(n);
                }
                
                @Override
                public CTGraphicalObjectFrame set(final int n, final CTGraphicalObjectFrame ctGraphicalObjectFrame) {
                    final CTGraphicalObjectFrame graphicFrameArray = CTGroupShapeImpl.this.getGraphicFrameArray(n);
                    CTGroupShapeImpl.this.setGraphicFrameArray(n, ctGraphicalObjectFrame);
                    return graphicFrameArray;
                }
                
                @Override
                public void add(final int n, final CTGraphicalObjectFrame ctGraphicalObjectFrame) {
                    CTGroupShapeImpl.this.insertNewGraphicFrame(n).set((XmlObject)ctGraphicalObjectFrame);
                }
                
                @Override
                public CTGraphicalObjectFrame remove(final int n) {
                    final CTGraphicalObjectFrame graphicFrameArray = CTGroupShapeImpl.this.getGraphicFrameArray(n);
                    CTGroupShapeImpl.this.removeGraphicFrame(n);
                    return graphicFrameArray;
                }
                
                @Override
                public int size() {
                    return CTGroupShapeImpl.this.sizeOfGraphicFrameArray();
                }
            }
            return new GraphicFrameList();
        }
    }
    
    @Deprecated
    public CTGraphicalObjectFrame[] getGraphicFrameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupShapeImpl.GRAPHICFRAME$8, (List)list);
            final CTGraphicalObjectFrame[] array = new CTGraphicalObjectFrame[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGraphicalObjectFrame getGraphicFrameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGraphicalObjectFrame ctGraphicalObjectFrame = (CTGraphicalObjectFrame)this.get_store().find_element_user(CTGroupShapeImpl.GRAPHICFRAME$8, n);
            if (ctGraphicalObjectFrame == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGraphicalObjectFrame;
        }
    }
    
    public int sizeOfGraphicFrameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.GRAPHICFRAME$8);
        }
    }
    
    public void setGraphicFrameArray(final CTGraphicalObjectFrame[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupShapeImpl.GRAPHICFRAME$8);
    }
    
    public void setGraphicFrameArray(final int n, final CTGraphicalObjectFrame ctGraphicalObjectFrame) {
        this.generatedSetterHelperImpl((XmlObject)ctGraphicalObjectFrame, CTGroupShapeImpl.GRAPHICFRAME$8, n, (short)2);
    }
    
    public CTGraphicalObjectFrame insertNewGraphicFrame(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrame)this.get_store().insert_element_user(CTGroupShapeImpl.GRAPHICFRAME$8, n);
        }
    }
    
    public CTGraphicalObjectFrame addNewGraphicFrame() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGraphicalObjectFrame)this.get_store().add_element_user(CTGroupShapeImpl.GRAPHICFRAME$8);
        }
    }
    
    public void removeGraphicFrame(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.GRAPHICFRAME$8, n);
        }
    }
    
    public List<CTConnector> getCxnSpList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CxnSpList extends AbstractList<CTConnector>
            {
                @Override
                public CTConnector get(final int n) {
                    return CTGroupShapeImpl.this.getCxnSpArray(n);
                }
                
                @Override
                public CTConnector set(final int n, final CTConnector ctConnector) {
                    final CTConnector cxnSpArray = CTGroupShapeImpl.this.getCxnSpArray(n);
                    CTGroupShapeImpl.this.setCxnSpArray(n, ctConnector);
                    return cxnSpArray;
                }
                
                @Override
                public void add(final int n, final CTConnector ctConnector) {
                    CTGroupShapeImpl.this.insertNewCxnSp(n).set((XmlObject)ctConnector);
                }
                
                @Override
                public CTConnector remove(final int n) {
                    final CTConnector cxnSpArray = CTGroupShapeImpl.this.getCxnSpArray(n);
                    CTGroupShapeImpl.this.removeCxnSp(n);
                    return cxnSpArray;
                }
                
                @Override
                public int size() {
                    return CTGroupShapeImpl.this.sizeOfCxnSpArray();
                }
            }
            return new CxnSpList();
        }
    }
    
    @Deprecated
    public CTConnector[] getCxnSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupShapeImpl.CXNSP$10, (List)list);
            final CTConnector[] array = new CTConnector[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTConnector getCxnSpArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConnector ctConnector = (CTConnector)this.get_store().find_element_user(CTGroupShapeImpl.CXNSP$10, n);
            if (ctConnector == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctConnector;
        }
    }
    
    public int sizeOfCxnSpArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.CXNSP$10);
        }
    }
    
    public void setCxnSpArray(final CTConnector[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupShapeImpl.CXNSP$10);
    }
    
    public void setCxnSpArray(final int n, final CTConnector ctConnector) {
        this.generatedSetterHelperImpl((XmlObject)ctConnector, CTGroupShapeImpl.CXNSP$10, n, (short)2);
    }
    
    public CTConnector insertNewCxnSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnector)this.get_store().insert_element_user(CTGroupShapeImpl.CXNSP$10, n);
        }
    }
    
    public CTConnector addNewCxnSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConnector)this.get_store().add_element_user(CTGroupShapeImpl.CXNSP$10);
        }
    }
    
    public void removeCxnSp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.CXNSP$10, n);
        }
    }
    
    public List<CTPicture> getPicList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PicList extends AbstractList<CTPicture>
            {
                @Override
                public CTPicture get(final int n) {
                    return CTGroupShapeImpl.this.getPicArray(n);
                }
                
                @Override
                public CTPicture set(final int n, final CTPicture ctPicture) {
                    final CTPicture picArray = CTGroupShapeImpl.this.getPicArray(n);
                    CTGroupShapeImpl.this.setPicArray(n, ctPicture);
                    return picArray;
                }
                
                @Override
                public void add(final int n, final CTPicture ctPicture) {
                    CTGroupShapeImpl.this.insertNewPic(n).set((XmlObject)ctPicture);
                }
                
                @Override
                public CTPicture remove(final int n) {
                    final CTPicture picArray = CTGroupShapeImpl.this.getPicArray(n);
                    CTGroupShapeImpl.this.removePic(n);
                    return picArray;
                }
                
                @Override
                public int size() {
                    return CTGroupShapeImpl.this.sizeOfPicArray();
                }
            }
            return new PicList();
        }
    }
    
    @Deprecated
    public CTPicture[] getPicArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupShapeImpl.PIC$12, (List)list);
            final CTPicture[] array = new CTPicture[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPicture getPicArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPicture ctPicture = (CTPicture)this.get_store().find_element_user(CTGroupShapeImpl.PIC$12, n);
            if (ctPicture == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPicture;
        }
    }
    
    public int sizeOfPicArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.PIC$12);
        }
    }
    
    public void setPicArray(final CTPicture[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupShapeImpl.PIC$12);
    }
    
    public void setPicArray(final int n, final CTPicture ctPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctPicture, CTGroupShapeImpl.PIC$12, n, (short)2);
    }
    
    public CTPicture insertNewPic(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().insert_element_user(CTGroupShapeImpl.PIC$12, n);
        }
    }
    
    public CTPicture addNewPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().add_element_user(CTGroupShapeImpl.PIC$12);
        }
    }
    
    public void removePic(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.PIC$12, n);
        }
    }
    
    public CTExtensionListModify getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionListModify ctExtensionListModify = (CTExtensionListModify)this.get_store().find_element_user(CTGroupShapeImpl.EXTLST$14, 0);
            if (ctExtensionListModify == null) {
                return null;
            }
            return ctExtensionListModify;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupShapeImpl.EXTLST$14) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionListModify ctExtensionListModify) {
        this.generatedSetterHelperImpl((XmlObject)ctExtensionListModify, CTGroupShapeImpl.EXTLST$14, 0, (short)1);
    }
    
    public CTExtensionListModify addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionListModify)this.get_store().add_element_user(CTGroupShapeImpl.EXTLST$14);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupShapeImpl.EXTLST$14, 0);
        }
    }
    
    static {
        NVGRPSPPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "nvGrpSpPr");
        GRPSPPR$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "grpSpPr");
        SP$4 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sp");
        GRPSP$6 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "grpSp");
        GRAPHICFRAME$8 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "graphicFrame");
        CXNSP$10 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "cxnSp");
        PIC$12 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "pic");
        EXTLST$14 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "extLst");
    }
}
