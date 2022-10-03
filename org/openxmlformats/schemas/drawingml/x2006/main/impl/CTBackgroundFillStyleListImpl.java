package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackgroundFillStyleList;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBackgroundFillStyleListImpl extends XmlComplexContentImpl implements CTBackgroundFillStyleList
{
    private static final long serialVersionUID = 1L;
    private static final QName NOFILL$0;
    private static final QName SOLIDFILL$2;
    private static final QName GRADFILL$4;
    private static final QName BLIPFILL$6;
    private static final QName PATTFILL$8;
    private static final QName GRPFILL$10;
    
    public CTBackgroundFillStyleListImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTNoFillProperties> getNoFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NoFillList extends AbstractList<CTNoFillProperties>
            {
                @Override
                public CTNoFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getNoFillArray(n);
                }
                
                @Override
                public CTNoFillProperties set(final int n, final CTNoFillProperties ctNoFillProperties) {
                    final CTNoFillProperties noFillArray = CTBackgroundFillStyleListImpl.this.getNoFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setNoFillArray(n, ctNoFillProperties);
                    return noFillArray;
                }
                
                @Override
                public void add(final int n, final CTNoFillProperties ctNoFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewNoFill(n).set((XmlObject)ctNoFillProperties);
                }
                
                @Override
                public CTNoFillProperties remove(final int n) {
                    final CTNoFillProperties noFillArray = CTBackgroundFillStyleListImpl.this.getNoFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removeNoFill(n);
                    return noFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfNoFillArray();
                }
            }
            return new NoFillList();
        }
    }
    
    @Deprecated
    public CTNoFillProperties[] getNoFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.NOFILL$0, (List)list);
            final CTNoFillProperties[] array = new CTNoFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNoFillProperties getNoFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNoFillProperties ctNoFillProperties = (CTNoFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.NOFILL$0, n);
            if (ctNoFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNoFillProperties;
        }
    }
    
    public int sizeOfNoFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.NOFILL$0);
        }
    }
    
    public void setNoFillArray(final CTNoFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.NOFILL$0);
    }
    
    public void setNoFillArray(final int n, final CTNoFillProperties ctNoFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctNoFillProperties, CTBackgroundFillStyleListImpl.NOFILL$0, n, (short)2);
    }
    
    public CTNoFillProperties insertNewNoFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.NOFILL$0, n);
        }
    }
    
    public CTNoFillProperties addNewNoFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNoFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.NOFILL$0);
        }
    }
    
    public void removeNoFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.NOFILL$0, n);
        }
    }
    
    public List<CTSolidColorFillProperties> getSolidFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SolidFillList extends AbstractList<CTSolidColorFillProperties>
            {
                @Override
                public CTSolidColorFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getSolidFillArray(n);
                }
                
                @Override
                public CTSolidColorFillProperties set(final int n, final CTSolidColorFillProperties ctSolidColorFillProperties) {
                    final CTSolidColorFillProperties solidFillArray = CTBackgroundFillStyleListImpl.this.getSolidFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setSolidFillArray(n, ctSolidColorFillProperties);
                    return solidFillArray;
                }
                
                @Override
                public void add(final int n, final CTSolidColorFillProperties ctSolidColorFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewSolidFill(n).set((XmlObject)ctSolidColorFillProperties);
                }
                
                @Override
                public CTSolidColorFillProperties remove(final int n) {
                    final CTSolidColorFillProperties solidFillArray = CTBackgroundFillStyleListImpl.this.getSolidFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removeSolidFill(n);
                    return solidFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfSolidFillArray();
                }
            }
            return new SolidFillList();
        }
    }
    
    @Deprecated
    public CTSolidColorFillProperties[] getSolidFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.SOLIDFILL$2, (List)list);
            final CTSolidColorFillProperties[] array = new CTSolidColorFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSolidColorFillProperties getSolidFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSolidColorFillProperties ctSolidColorFillProperties = (CTSolidColorFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.SOLIDFILL$2, n);
            if (ctSolidColorFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSolidColorFillProperties;
        }
    }
    
    public int sizeOfSolidFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.SOLIDFILL$2);
        }
    }
    
    public void setSolidFillArray(final CTSolidColorFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.SOLIDFILL$2);
    }
    
    public void setSolidFillArray(final int n, final CTSolidColorFillProperties ctSolidColorFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctSolidColorFillProperties, CTBackgroundFillStyleListImpl.SOLIDFILL$2, n, (short)2);
    }
    
    public CTSolidColorFillProperties insertNewSolidFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.SOLIDFILL$2, n);
        }
    }
    
    public CTSolidColorFillProperties addNewSolidFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSolidColorFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.SOLIDFILL$2);
        }
    }
    
    public void removeSolidFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.SOLIDFILL$2, n);
        }
    }
    
    public List<CTGradientFillProperties> getGradFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GradFillList extends AbstractList<CTGradientFillProperties>
            {
                @Override
                public CTGradientFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getGradFillArray(n);
                }
                
                @Override
                public CTGradientFillProperties set(final int n, final CTGradientFillProperties ctGradientFillProperties) {
                    final CTGradientFillProperties gradFillArray = CTBackgroundFillStyleListImpl.this.getGradFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setGradFillArray(n, ctGradientFillProperties);
                    return gradFillArray;
                }
                
                @Override
                public void add(final int n, final CTGradientFillProperties ctGradientFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewGradFill(n).set((XmlObject)ctGradientFillProperties);
                }
                
                @Override
                public CTGradientFillProperties remove(final int n) {
                    final CTGradientFillProperties gradFillArray = CTBackgroundFillStyleListImpl.this.getGradFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removeGradFill(n);
                    return gradFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfGradFillArray();
                }
            }
            return new GradFillList();
        }
    }
    
    @Deprecated
    public CTGradientFillProperties[] getGradFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.GRADFILL$4, (List)list);
            final CTGradientFillProperties[] array = new CTGradientFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGradientFillProperties getGradFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGradientFillProperties ctGradientFillProperties = (CTGradientFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.GRADFILL$4, n);
            if (ctGradientFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGradientFillProperties;
        }
    }
    
    public int sizeOfGradFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.GRADFILL$4);
        }
    }
    
    public void setGradFillArray(final CTGradientFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.GRADFILL$4);
    }
    
    public void setGradFillArray(final int n, final CTGradientFillProperties ctGradientFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGradientFillProperties, CTBackgroundFillStyleListImpl.GRADFILL$4, n, (short)2);
    }
    
    public CTGradientFillProperties insertNewGradFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.GRADFILL$4, n);
        }
    }
    
    public CTGradientFillProperties addNewGradFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGradientFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.GRADFILL$4);
        }
    }
    
    public void removeGradFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.GRADFILL$4, n);
        }
    }
    
    public List<CTBlipFillProperties> getBlipFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlipFillList extends AbstractList<CTBlipFillProperties>
            {
                @Override
                public CTBlipFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getBlipFillArray(n);
                }
                
                @Override
                public CTBlipFillProperties set(final int n, final CTBlipFillProperties ctBlipFillProperties) {
                    final CTBlipFillProperties blipFillArray = CTBackgroundFillStyleListImpl.this.getBlipFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setBlipFillArray(n, ctBlipFillProperties);
                    return blipFillArray;
                }
                
                @Override
                public void add(final int n, final CTBlipFillProperties ctBlipFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewBlipFill(n).set((XmlObject)ctBlipFillProperties);
                }
                
                @Override
                public CTBlipFillProperties remove(final int n) {
                    final CTBlipFillProperties blipFillArray = CTBackgroundFillStyleListImpl.this.getBlipFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removeBlipFill(n);
                    return blipFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfBlipFillArray();
                }
            }
            return new BlipFillList();
        }
    }
    
    @Deprecated
    public CTBlipFillProperties[] getBlipFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.BLIPFILL$6, (List)list);
            final CTBlipFillProperties[] array = new CTBlipFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBlipFillProperties getBlipFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlipFillProperties ctBlipFillProperties = (CTBlipFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.BLIPFILL$6, n);
            if (ctBlipFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBlipFillProperties;
        }
    }
    
    public int sizeOfBlipFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.BLIPFILL$6);
        }
    }
    
    public void setBlipFillArray(final CTBlipFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.BLIPFILL$6);
    }
    
    public void setBlipFillArray(final int n, final CTBlipFillProperties ctBlipFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBlipFillProperties, CTBackgroundFillStyleListImpl.BLIPFILL$6, n, (short)2);
    }
    
    public CTBlipFillProperties insertNewBlipFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.BLIPFILL$6, n);
        }
    }
    
    public CTBlipFillProperties addNewBlipFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlipFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.BLIPFILL$6);
        }
    }
    
    public void removeBlipFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.BLIPFILL$6, n);
        }
    }
    
    public List<CTPatternFillProperties> getPattFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PattFillList extends AbstractList<CTPatternFillProperties>
            {
                @Override
                public CTPatternFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getPattFillArray(n);
                }
                
                @Override
                public CTPatternFillProperties set(final int n, final CTPatternFillProperties ctPatternFillProperties) {
                    final CTPatternFillProperties pattFillArray = CTBackgroundFillStyleListImpl.this.getPattFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setPattFillArray(n, ctPatternFillProperties);
                    return pattFillArray;
                }
                
                @Override
                public void add(final int n, final CTPatternFillProperties ctPatternFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewPattFill(n).set((XmlObject)ctPatternFillProperties);
                }
                
                @Override
                public CTPatternFillProperties remove(final int n) {
                    final CTPatternFillProperties pattFillArray = CTBackgroundFillStyleListImpl.this.getPattFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removePattFill(n);
                    return pattFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfPattFillArray();
                }
            }
            return new PattFillList();
        }
    }
    
    @Deprecated
    public CTPatternFillProperties[] getPattFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.PATTFILL$8, (List)list);
            final CTPatternFillProperties[] array = new CTPatternFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPatternFillProperties getPattFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPatternFillProperties ctPatternFillProperties = (CTPatternFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.PATTFILL$8, n);
            if (ctPatternFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPatternFillProperties;
        }
    }
    
    public int sizeOfPattFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.PATTFILL$8);
        }
    }
    
    public void setPattFillArray(final CTPatternFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.PATTFILL$8);
    }
    
    public void setPattFillArray(final int n, final CTPatternFillProperties ctPatternFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctPatternFillProperties, CTBackgroundFillStyleListImpl.PATTFILL$8, n, (short)2);
    }
    
    public CTPatternFillProperties insertNewPattFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.PATTFILL$8, n);
        }
    }
    
    public CTPatternFillProperties addNewPattFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPatternFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.PATTFILL$8);
        }
    }
    
    public void removePattFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.PATTFILL$8, n);
        }
    }
    
    public List<CTGroupFillProperties> getGrpFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GrpFillList extends AbstractList<CTGroupFillProperties>
            {
                @Override
                public CTGroupFillProperties get(final int n) {
                    return CTBackgroundFillStyleListImpl.this.getGrpFillArray(n);
                }
                
                @Override
                public CTGroupFillProperties set(final int n, final CTGroupFillProperties ctGroupFillProperties) {
                    final CTGroupFillProperties grpFillArray = CTBackgroundFillStyleListImpl.this.getGrpFillArray(n);
                    CTBackgroundFillStyleListImpl.this.setGrpFillArray(n, ctGroupFillProperties);
                    return grpFillArray;
                }
                
                @Override
                public void add(final int n, final CTGroupFillProperties ctGroupFillProperties) {
                    CTBackgroundFillStyleListImpl.this.insertNewGrpFill(n).set((XmlObject)ctGroupFillProperties);
                }
                
                @Override
                public CTGroupFillProperties remove(final int n) {
                    final CTGroupFillProperties grpFillArray = CTBackgroundFillStyleListImpl.this.getGrpFillArray(n);
                    CTBackgroundFillStyleListImpl.this.removeGrpFill(n);
                    return grpFillArray;
                }
                
                @Override
                public int size() {
                    return CTBackgroundFillStyleListImpl.this.sizeOfGrpFillArray();
                }
            }
            return new GrpFillList();
        }
    }
    
    @Deprecated
    public CTGroupFillProperties[] getGrpFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBackgroundFillStyleListImpl.GRPFILL$10, (List)list);
            final CTGroupFillProperties[] array = new CTGroupFillProperties[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGroupFillProperties getGrpFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroupFillProperties ctGroupFillProperties = (CTGroupFillProperties)this.get_store().find_element_user(CTBackgroundFillStyleListImpl.GRPFILL$10, n);
            if (ctGroupFillProperties == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGroupFillProperties;
        }
    }
    
    public int sizeOfGrpFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundFillStyleListImpl.GRPFILL$10);
        }
    }
    
    public void setGrpFillArray(final CTGroupFillProperties[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBackgroundFillStyleListImpl.GRPFILL$10);
    }
    
    public void setGrpFillArray(final int n, final CTGroupFillProperties ctGroupFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctGroupFillProperties, CTBackgroundFillStyleListImpl.GRPFILL$10, n, (short)2);
    }
    
    public CTGroupFillProperties insertNewGrpFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().insert_element_user(CTBackgroundFillStyleListImpl.GRPFILL$10, n);
        }
    }
    
    public CTGroupFillProperties addNewGrpFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroupFillProperties)this.get_store().add_element_user(CTBackgroundFillStyleListImpl.GRPFILL$10);
        }
    }
    
    public void removeGrpFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundFillStyleListImpl.GRPFILL$10, n);
        }
    }
    
    static {
        NOFILL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "noFill");
        SOLIDFILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "solidFill");
        GRADFILL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gradFill");
        BLIPFILL$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blipFill");
        PATTFILL$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pattFill");
        GRPFILL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grpFill");
    }
}
