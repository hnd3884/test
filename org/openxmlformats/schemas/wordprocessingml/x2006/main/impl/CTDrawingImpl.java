package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDrawingImpl extends XmlComplexContentImpl implements CTDrawing
{
    private static final long serialVersionUID = 1L;
    private static final QName ANCHOR$0;
    private static final QName INLINE$2;
    
    public CTDrawingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTAnchor> getAnchorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorList extends AbstractList<CTAnchor>
            {
                @Override
                public CTAnchor get(final int n) {
                    return CTDrawingImpl.this.getAnchorArray(n);
                }
                
                @Override
                public CTAnchor set(final int n, final CTAnchor ctAnchor) {
                    final CTAnchor anchorArray = CTDrawingImpl.this.getAnchorArray(n);
                    CTDrawingImpl.this.setAnchorArray(n, ctAnchor);
                    return anchorArray;
                }
                
                @Override
                public void add(final int n, final CTAnchor ctAnchor) {
                    CTDrawingImpl.this.insertNewAnchor(n).set((XmlObject)ctAnchor);
                }
                
                @Override
                public CTAnchor remove(final int n) {
                    final CTAnchor anchorArray = CTDrawingImpl.this.getAnchorArray(n);
                    CTDrawingImpl.this.removeAnchor(n);
                    return anchorArray;
                }
                
                @Override
                public int size() {
                    return CTDrawingImpl.this.sizeOfAnchorArray();
                }
            }
            return new AnchorList();
        }
    }
    
    @Deprecated
    public CTAnchor[] getAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDrawingImpl.ANCHOR$0, (List)list);
            final CTAnchor[] array = new CTAnchor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAnchor getAnchorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchor ctAnchor = (CTAnchor)this.get_store().find_element_user(CTDrawingImpl.ANCHOR$0, n);
            if (ctAnchor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAnchor;
        }
    }
    
    public int sizeOfAnchorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDrawingImpl.ANCHOR$0);
        }
    }
    
    public void setAnchorArray(final CTAnchor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDrawingImpl.ANCHOR$0);
    }
    
    public void setAnchorArray(final int n, final CTAnchor ctAnchor) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchor, CTDrawingImpl.ANCHOR$0, n, (short)2);
    }
    
    public CTAnchor insertNewAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchor)this.get_store().insert_element_user(CTDrawingImpl.ANCHOR$0, n);
        }
    }
    
    public CTAnchor addNewAnchor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchor)this.get_store().add_element_user(CTDrawingImpl.ANCHOR$0);
        }
    }
    
    public void removeAnchor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDrawingImpl.ANCHOR$0, n);
        }
    }
    
    public List<CTInline> getInlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InlineList extends AbstractList<CTInline>
            {
                @Override
                public CTInline get(final int n) {
                    return CTDrawingImpl.this.getInlineArray(n);
                }
                
                @Override
                public CTInline set(final int n, final CTInline ctInline) {
                    final CTInline inlineArray = CTDrawingImpl.this.getInlineArray(n);
                    CTDrawingImpl.this.setInlineArray(n, ctInline);
                    return inlineArray;
                }
                
                @Override
                public void add(final int n, final CTInline ctInline) {
                    CTDrawingImpl.this.insertNewInline(n).set((XmlObject)ctInline);
                }
                
                @Override
                public CTInline remove(final int n) {
                    final CTInline inlineArray = CTDrawingImpl.this.getInlineArray(n);
                    CTDrawingImpl.this.removeInline(n);
                    return inlineArray;
                }
                
                @Override
                public int size() {
                    return CTDrawingImpl.this.sizeOfInlineArray();
                }
            }
            return new InlineList();
        }
    }
    
    @Deprecated
    public CTInline[] getInlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDrawingImpl.INLINE$2, (List)list);
            final CTInline[] array = new CTInline[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInline getInlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInline ctInline = (CTInline)this.get_store().find_element_user(CTDrawingImpl.INLINE$2, n);
            if (ctInline == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInline;
        }
    }
    
    public int sizeOfInlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDrawingImpl.INLINE$2);
        }
    }
    
    public void setInlineArray(final CTInline[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDrawingImpl.INLINE$2);
    }
    
    public void setInlineArray(final int n, final CTInline ctInline) {
        this.generatedSetterHelperImpl((XmlObject)ctInline, CTDrawingImpl.INLINE$2, n, (short)2);
    }
    
    public CTInline insertNewInline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInline)this.get_store().insert_element_user(CTDrawingImpl.INLINE$2, n);
        }
    }
    
    public CTInline addNewInline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInline)this.get_store().add_element_user(CTDrawingImpl.INLINE$2);
        }
    }
    
    public void removeInline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDrawingImpl.INLINE$2, n);
        }
    }
    
    static {
        ANCHOR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "anchor");
        INLINE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing", "inline");
    }
}
