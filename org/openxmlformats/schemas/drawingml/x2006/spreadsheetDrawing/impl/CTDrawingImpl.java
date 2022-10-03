package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.impl;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTAbsoluteAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTOneCellAnchor;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDrawingImpl extends XmlComplexContentImpl implements CTDrawing
{
    private static final long serialVersionUID = 1L;
    private static final QName TWOCELLANCHOR$0;
    private static final QName ONECELLANCHOR$2;
    private static final QName ABSOLUTEANCHOR$4;
    
    public CTDrawingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTTwoCellAnchor> getTwoCellAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TwoCellAnchorList extends AbstractList<CTTwoCellAnchor>
            {
                @Override
                public CTTwoCellAnchor get(final int n) {
                    return CTDrawingImpl.this.getTwoCellAnchorArray(n);
                }
                
                @Override
                public CTTwoCellAnchor set(final int n, final CTTwoCellAnchor ctTwoCellAnchor) {
                    final CTTwoCellAnchor twoCellAnchorArray = CTDrawingImpl.this.getTwoCellAnchorArray(n);
                    CTDrawingImpl.this.setTwoCellAnchorArray(n, ctTwoCellAnchor);
                    return twoCellAnchorArray;
                }
                
                @Override
                public void add(final int n, final CTTwoCellAnchor ctTwoCellAnchor) {
                    CTDrawingImpl.this.insertNewTwoCellAnchor(n).set((XmlObject)ctTwoCellAnchor);
                }
                
                @Override
                public CTTwoCellAnchor remove(final int n) {
                    final CTTwoCellAnchor twoCellAnchorArray = CTDrawingImpl.this.getTwoCellAnchorArray(n);
                    CTDrawingImpl.this.removeTwoCellAnchor(n);
                    return twoCellAnchorArray;
                }
                
                @Override
                public int size() {
                    return CTDrawingImpl.this.sizeOfTwoCellAnchorArray();
                }
            }
            return new TwoCellAnchorList();
        }
    }
    
    @Deprecated
    public CTTwoCellAnchor[] getTwoCellAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDrawingImpl.TWOCELLANCHOR$0, (List)list);
            final CTTwoCellAnchor[] array = new CTTwoCellAnchor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTwoCellAnchor getTwoCellAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTwoCellAnchor ctTwoCellAnchor = (CTTwoCellAnchor)this.get_store().find_element_user(CTDrawingImpl.TWOCELLANCHOR$0, n);
            if (ctTwoCellAnchor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTwoCellAnchor;
        }
    }
    
    public int sizeOfTwoCellAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDrawingImpl.TWOCELLANCHOR$0);
        }
    }
    
    public void setTwoCellAnchorArray(final CTTwoCellAnchor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDrawingImpl.TWOCELLANCHOR$0);
    }
    
    public void setTwoCellAnchorArray(final int n, final CTTwoCellAnchor ctTwoCellAnchor) {
        this.generatedSetterHelperImpl((XmlObject)ctTwoCellAnchor, CTDrawingImpl.TWOCELLANCHOR$0, n, (short)2);
    }
    
    public CTTwoCellAnchor insertNewTwoCellAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwoCellAnchor)this.get_store().insert_element_user(CTDrawingImpl.TWOCELLANCHOR$0, n);
        }
    }
    
    public CTTwoCellAnchor addNewTwoCellAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTwoCellAnchor)this.get_store().add_element_user(CTDrawingImpl.TWOCELLANCHOR$0);
        }
    }
    
    public void removeTwoCellAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDrawingImpl.TWOCELLANCHOR$0, n);
        }
    }
    
    public List<CTOneCellAnchor> getOneCellAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OneCellAnchorList extends AbstractList<CTOneCellAnchor>
            {
                @Override
                public CTOneCellAnchor get(final int n) {
                    return CTDrawingImpl.this.getOneCellAnchorArray(n);
                }
                
                @Override
                public CTOneCellAnchor set(final int n, final CTOneCellAnchor ctOneCellAnchor) {
                    final CTOneCellAnchor oneCellAnchorArray = CTDrawingImpl.this.getOneCellAnchorArray(n);
                    CTDrawingImpl.this.setOneCellAnchorArray(n, ctOneCellAnchor);
                    return oneCellAnchorArray;
                }
                
                @Override
                public void add(final int n, final CTOneCellAnchor ctOneCellAnchor) {
                    CTDrawingImpl.this.insertNewOneCellAnchor(n).set((XmlObject)ctOneCellAnchor);
                }
                
                @Override
                public CTOneCellAnchor remove(final int n) {
                    final CTOneCellAnchor oneCellAnchorArray = CTDrawingImpl.this.getOneCellAnchorArray(n);
                    CTDrawingImpl.this.removeOneCellAnchor(n);
                    return oneCellAnchorArray;
                }
                
                @Override
                public int size() {
                    return CTDrawingImpl.this.sizeOfOneCellAnchorArray();
                }
            }
            return new OneCellAnchorList();
        }
    }
    
    @Deprecated
    public CTOneCellAnchor[] getOneCellAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDrawingImpl.ONECELLANCHOR$2, (List)list);
            final CTOneCellAnchor[] array = new CTOneCellAnchor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOneCellAnchor getOneCellAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOneCellAnchor ctOneCellAnchor = (CTOneCellAnchor)this.get_store().find_element_user(CTDrawingImpl.ONECELLANCHOR$2, n);
            if (ctOneCellAnchor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOneCellAnchor;
        }
    }
    
    public int sizeOfOneCellAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDrawingImpl.ONECELLANCHOR$2);
        }
    }
    
    public void setOneCellAnchorArray(final CTOneCellAnchor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDrawingImpl.ONECELLANCHOR$2);
    }
    
    public void setOneCellAnchorArray(final int n, final CTOneCellAnchor ctOneCellAnchor) {
        this.generatedSetterHelperImpl((XmlObject)ctOneCellAnchor, CTDrawingImpl.ONECELLANCHOR$2, n, (short)2);
    }
    
    public CTOneCellAnchor insertNewOneCellAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOneCellAnchor)this.get_store().insert_element_user(CTDrawingImpl.ONECELLANCHOR$2, n);
        }
    }
    
    public CTOneCellAnchor addNewOneCellAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOneCellAnchor)this.get_store().add_element_user(CTDrawingImpl.ONECELLANCHOR$2);
        }
    }
    
    public void removeOneCellAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDrawingImpl.ONECELLANCHOR$2, n);
        }
    }
    
    public List<CTAbsoluteAnchor> getAbsoluteAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AbsoluteAnchorList extends AbstractList<CTAbsoluteAnchor>
            {
                @Override
                public CTAbsoluteAnchor get(final int n) {
                    return CTDrawingImpl.this.getAbsoluteAnchorArray(n);
                }
                
                @Override
                public CTAbsoluteAnchor set(final int n, final CTAbsoluteAnchor ctAbsoluteAnchor) {
                    final CTAbsoluteAnchor absoluteAnchorArray = CTDrawingImpl.this.getAbsoluteAnchorArray(n);
                    CTDrawingImpl.this.setAbsoluteAnchorArray(n, ctAbsoluteAnchor);
                    return absoluteAnchorArray;
                }
                
                @Override
                public void add(final int n, final CTAbsoluteAnchor ctAbsoluteAnchor) {
                    CTDrawingImpl.this.insertNewAbsoluteAnchor(n).set((XmlObject)ctAbsoluteAnchor);
                }
                
                @Override
                public CTAbsoluteAnchor remove(final int n) {
                    final CTAbsoluteAnchor absoluteAnchorArray = CTDrawingImpl.this.getAbsoluteAnchorArray(n);
                    CTDrawingImpl.this.removeAbsoluteAnchor(n);
                    return absoluteAnchorArray;
                }
                
                @Override
                public int size() {
                    return CTDrawingImpl.this.sizeOfAbsoluteAnchorArray();
                }
            }
            return new AbsoluteAnchorList();
        }
    }
    
    @Deprecated
    public CTAbsoluteAnchor[] getAbsoluteAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDrawingImpl.ABSOLUTEANCHOR$4, (List)list);
            final CTAbsoluteAnchor[] array = new CTAbsoluteAnchor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAbsoluteAnchor getAbsoluteAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAbsoluteAnchor ctAbsoluteAnchor = (CTAbsoluteAnchor)this.get_store().find_element_user(CTDrawingImpl.ABSOLUTEANCHOR$4, n);
            if (ctAbsoluteAnchor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAbsoluteAnchor;
        }
    }
    
    public int sizeOfAbsoluteAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDrawingImpl.ABSOLUTEANCHOR$4);
        }
    }
    
    public void setAbsoluteAnchorArray(final CTAbsoluteAnchor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDrawingImpl.ABSOLUTEANCHOR$4);
    }
    
    public void setAbsoluteAnchorArray(final int n, final CTAbsoluteAnchor ctAbsoluteAnchor) {
        this.generatedSetterHelperImpl((XmlObject)ctAbsoluteAnchor, CTDrawingImpl.ABSOLUTEANCHOR$4, n, (short)2);
    }
    
    public CTAbsoluteAnchor insertNewAbsoluteAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAbsoluteAnchor)this.get_store().insert_element_user(CTDrawingImpl.ABSOLUTEANCHOR$4, n);
        }
    }
    
    public CTAbsoluteAnchor addNewAbsoluteAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAbsoluteAnchor)this.get_store().add_element_user(CTDrawingImpl.ABSOLUTEANCHOR$4);
        }
    }
    
    public void removeAbsoluteAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDrawingImpl.ABSOLUTEANCHOR$4, n);
        }
    }
    
    static {
        TWOCELLANCHOR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "twoCellAnchor");
        ONECELLANCHOR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "oneCellAnchor");
        ABSOLUTEANCHOR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing", "absoluteAnchor");
    }
}
