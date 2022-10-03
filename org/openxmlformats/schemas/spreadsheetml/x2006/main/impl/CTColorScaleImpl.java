package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorScaleImpl extends XmlComplexContentImpl implements CTColorScale
{
    private static final long serialVersionUID = 1L;
    private static final QName CFVO$0;
    private static final QName COLOR$2;
    
    public CTColorScaleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCfvo> getCfvoList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CfvoList extends AbstractList<CTCfvo>
            {
                @Override
                public CTCfvo get(final int n) {
                    return CTColorScaleImpl.this.getCfvoArray(n);
                }
                
                @Override
                public CTCfvo set(final int n, final CTCfvo ctCfvo) {
                    final CTCfvo cfvoArray = CTColorScaleImpl.this.getCfvoArray(n);
                    CTColorScaleImpl.this.setCfvoArray(n, ctCfvo);
                    return cfvoArray;
                }
                
                @Override
                public void add(final int n, final CTCfvo ctCfvo) {
                    CTColorScaleImpl.this.insertNewCfvo(n).set((XmlObject)ctCfvo);
                }
                
                @Override
                public CTCfvo remove(final int n) {
                    final CTCfvo cfvoArray = CTColorScaleImpl.this.getCfvoArray(n);
                    CTColorScaleImpl.this.removeCfvo(n);
                    return cfvoArray;
                }
                
                @Override
                public int size() {
                    return CTColorScaleImpl.this.sizeOfCfvoArray();
                }
            }
            return new CfvoList();
        }
    }
    
    @Deprecated
    public CTCfvo[] getCfvoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTColorScaleImpl.CFVO$0, (List)list);
            final CTCfvo[] array = new CTCfvo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCfvo getCfvoArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCfvo ctCfvo = (CTCfvo)this.get_store().find_element_user(CTColorScaleImpl.CFVO$0, n);
            if (ctCfvo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCfvo;
        }
    }
    
    public int sizeOfCfvoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorScaleImpl.CFVO$0);
        }
    }
    
    public void setCfvoArray(final CTCfvo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTColorScaleImpl.CFVO$0);
    }
    
    public void setCfvoArray(final int n, final CTCfvo ctCfvo) {
        this.generatedSetterHelperImpl((XmlObject)ctCfvo, CTColorScaleImpl.CFVO$0, n, (short)2);
    }
    
    public CTCfvo insertNewCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().insert_element_user(CTColorScaleImpl.CFVO$0, n);
        }
    }
    
    public CTCfvo addNewCfvo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfvo)this.get_store().add_element_user(CTColorScaleImpl.CFVO$0);
        }
    }
    
    public void removeCfvo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorScaleImpl.CFVO$0, n);
        }
    }
    
    public List<CTColor> getColorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColorList extends AbstractList<CTColor>
            {
                @Override
                public CTColor get(final int n) {
                    return CTColorScaleImpl.this.getColorArray(n);
                }
                
                @Override
                public CTColor set(final int n, final CTColor ctColor) {
                    final CTColor colorArray = CTColorScaleImpl.this.getColorArray(n);
                    CTColorScaleImpl.this.setColorArray(n, ctColor);
                    return colorArray;
                }
                
                @Override
                public void add(final int n, final CTColor ctColor) {
                    CTColorScaleImpl.this.insertNewColor(n).set((XmlObject)ctColor);
                }
                
                @Override
                public CTColor remove(final int n) {
                    final CTColor colorArray = CTColorScaleImpl.this.getColorArray(n);
                    CTColorScaleImpl.this.removeColor(n);
                    return colorArray;
                }
                
                @Override
                public int size() {
                    return CTColorScaleImpl.this.sizeOfColorArray();
                }
            }
            return new ColorList();
        }
    }
    
    @Deprecated
    public CTColor[] getColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTColorScaleImpl.COLOR$2, (List)list);
            final CTColor[] array = new CTColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColor getColorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorScaleImpl.COLOR$2, n);
            if (ctColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColor;
        }
    }
    
    public int sizeOfColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorScaleImpl.COLOR$2);
        }
    }
    
    public void setColorArray(final CTColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTColorScaleImpl.COLOR$2);
    }
    
    public void setColorArray(final int n, final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorScaleImpl.COLOR$2, n, (short)2);
    }
    
    public CTColor insertNewColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().insert_element_user(CTColorScaleImpl.COLOR$2, n);
        }
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorScaleImpl.COLOR$2);
        }
    }
    
    public void removeColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorScaleImpl.COLOR$2, n);
        }
    }
    
    static {
        CFVO$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cfvo");
        COLOR$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "color");
    }
}
