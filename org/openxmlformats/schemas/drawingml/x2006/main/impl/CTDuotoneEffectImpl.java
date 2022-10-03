package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDuotoneEffectImpl extends XmlComplexContentImpl implements CTDuotoneEffect
{
    private static final long serialVersionUID = 1L;
    private static final QName SCRGBCLR$0;
    private static final QName SRGBCLR$2;
    private static final QName HSLCLR$4;
    private static final QName SYSCLR$6;
    private static final QName SCHEMECLR$8;
    private static final QName PRSTCLR$10;
    
    public CTDuotoneEffectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTScRgbColor> getScrgbClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScrgbClrList extends AbstractList<CTScRgbColor>
            {
                @Override
                public CTScRgbColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getScrgbClrArray(n);
                }
                
                @Override
                public CTScRgbColor set(final int n, final CTScRgbColor ctScRgbColor) {
                    final CTScRgbColor scrgbClrArray = CTDuotoneEffectImpl.this.getScrgbClrArray(n);
                    CTDuotoneEffectImpl.this.setScrgbClrArray(n, ctScRgbColor);
                    return scrgbClrArray;
                }
                
                @Override
                public void add(final int n, final CTScRgbColor ctScRgbColor) {
                    CTDuotoneEffectImpl.this.insertNewScrgbClr(n).set((XmlObject)ctScRgbColor);
                }
                
                @Override
                public CTScRgbColor remove(final int n) {
                    final CTScRgbColor scrgbClrArray = CTDuotoneEffectImpl.this.getScrgbClrArray(n);
                    CTDuotoneEffectImpl.this.removeScrgbClr(n);
                    return scrgbClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfScrgbClrArray();
                }
            }
            return new ScrgbClrList();
        }
    }
    
    @Deprecated
    public CTScRgbColor[] getScrgbClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.SCRGBCLR$0, (List)list);
            final CTScRgbColor[] array = new CTScRgbColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTScRgbColor getScrgbClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScRgbColor ctScRgbColor = (CTScRgbColor)this.get_store().find_element_user(CTDuotoneEffectImpl.SCRGBCLR$0, n);
            if (ctScRgbColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctScRgbColor;
        }
    }
    
    public int sizeOfScrgbClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.SCRGBCLR$0);
        }
    }
    
    public void setScrgbClrArray(final CTScRgbColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.SCRGBCLR$0);
    }
    
    public void setScrgbClrArray(final int n, final CTScRgbColor ctScRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctScRgbColor, CTDuotoneEffectImpl.SCRGBCLR$0, n, (short)2);
    }
    
    public CTScRgbColor insertNewScrgbClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScRgbColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.SCRGBCLR$0, n);
        }
    }
    
    public CTScRgbColor addNewScrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScRgbColor)this.get_store().add_element_user(CTDuotoneEffectImpl.SCRGBCLR$0);
        }
    }
    
    public void removeScrgbClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.SCRGBCLR$0, n);
        }
    }
    
    public List<CTSRgbColor> getSrgbClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SrgbClrList extends AbstractList<CTSRgbColor>
            {
                @Override
                public CTSRgbColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getSrgbClrArray(n);
                }
                
                @Override
                public CTSRgbColor set(final int n, final CTSRgbColor ctsRgbColor) {
                    final CTSRgbColor srgbClrArray = CTDuotoneEffectImpl.this.getSrgbClrArray(n);
                    CTDuotoneEffectImpl.this.setSrgbClrArray(n, ctsRgbColor);
                    return srgbClrArray;
                }
                
                @Override
                public void add(final int n, final CTSRgbColor ctsRgbColor) {
                    CTDuotoneEffectImpl.this.insertNewSrgbClr(n).set((XmlObject)ctsRgbColor);
                }
                
                @Override
                public CTSRgbColor remove(final int n) {
                    final CTSRgbColor srgbClrArray = CTDuotoneEffectImpl.this.getSrgbClrArray(n);
                    CTDuotoneEffectImpl.this.removeSrgbClr(n);
                    return srgbClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfSrgbClrArray();
                }
            }
            return new SrgbClrList();
        }
    }
    
    @Deprecated
    public CTSRgbColor[] getSrgbClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.SRGBCLR$2, (List)list);
            final CTSRgbColor[] array = new CTSRgbColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSRgbColor getSrgbClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSRgbColor ctsRgbColor = (CTSRgbColor)this.get_store().find_element_user(CTDuotoneEffectImpl.SRGBCLR$2, n);
            if (ctsRgbColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctsRgbColor;
        }
    }
    
    public int sizeOfSrgbClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.SRGBCLR$2);
        }
    }
    
    public void setSrgbClrArray(final CTSRgbColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.SRGBCLR$2);
    }
    
    public void setSrgbClrArray(final int n, final CTSRgbColor ctsRgbColor) {
        this.generatedSetterHelperImpl((XmlObject)ctsRgbColor, CTDuotoneEffectImpl.SRGBCLR$2, n, (short)2);
    }
    
    public CTSRgbColor insertNewSrgbClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSRgbColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.SRGBCLR$2, n);
        }
    }
    
    public CTSRgbColor addNewSrgbClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSRgbColor)this.get_store().add_element_user(CTDuotoneEffectImpl.SRGBCLR$2);
        }
    }
    
    public void removeSrgbClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.SRGBCLR$2, n);
        }
    }
    
    public List<CTHslColor> getHslClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HslClrList extends AbstractList<CTHslColor>
            {
                @Override
                public CTHslColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getHslClrArray(n);
                }
                
                @Override
                public CTHslColor set(final int n, final CTHslColor ctHslColor) {
                    final CTHslColor hslClrArray = CTDuotoneEffectImpl.this.getHslClrArray(n);
                    CTDuotoneEffectImpl.this.setHslClrArray(n, ctHslColor);
                    return hslClrArray;
                }
                
                @Override
                public void add(final int n, final CTHslColor ctHslColor) {
                    CTDuotoneEffectImpl.this.insertNewHslClr(n).set((XmlObject)ctHslColor);
                }
                
                @Override
                public CTHslColor remove(final int n) {
                    final CTHslColor hslClrArray = CTDuotoneEffectImpl.this.getHslClrArray(n);
                    CTDuotoneEffectImpl.this.removeHslClr(n);
                    return hslClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfHslClrArray();
                }
            }
            return new HslClrList();
        }
    }
    
    @Deprecated
    public CTHslColor[] getHslClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.HSLCLR$4, (List)list);
            final CTHslColor[] array = new CTHslColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHslColor getHslClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHslColor ctHslColor = (CTHslColor)this.get_store().find_element_user(CTDuotoneEffectImpl.HSLCLR$4, n);
            if (ctHslColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHslColor;
        }
    }
    
    public int sizeOfHslClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.HSLCLR$4);
        }
    }
    
    public void setHslClrArray(final CTHslColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.HSLCLR$4);
    }
    
    public void setHslClrArray(final int n, final CTHslColor ctHslColor) {
        this.generatedSetterHelperImpl((XmlObject)ctHslColor, CTDuotoneEffectImpl.HSLCLR$4, n, (short)2);
    }
    
    public CTHslColor insertNewHslClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHslColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.HSLCLR$4, n);
        }
    }
    
    public CTHslColor addNewHslClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHslColor)this.get_store().add_element_user(CTDuotoneEffectImpl.HSLCLR$4);
        }
    }
    
    public void removeHslClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.HSLCLR$4, n);
        }
    }
    
    public List<CTSystemColor> getSysClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SysClrList extends AbstractList<CTSystemColor>
            {
                @Override
                public CTSystemColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getSysClrArray(n);
                }
                
                @Override
                public CTSystemColor set(final int n, final CTSystemColor ctSystemColor) {
                    final CTSystemColor sysClrArray = CTDuotoneEffectImpl.this.getSysClrArray(n);
                    CTDuotoneEffectImpl.this.setSysClrArray(n, ctSystemColor);
                    return sysClrArray;
                }
                
                @Override
                public void add(final int n, final CTSystemColor ctSystemColor) {
                    CTDuotoneEffectImpl.this.insertNewSysClr(n).set((XmlObject)ctSystemColor);
                }
                
                @Override
                public CTSystemColor remove(final int n) {
                    final CTSystemColor sysClrArray = CTDuotoneEffectImpl.this.getSysClrArray(n);
                    CTDuotoneEffectImpl.this.removeSysClr(n);
                    return sysClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfSysClrArray();
                }
            }
            return new SysClrList();
        }
    }
    
    @Deprecated
    public CTSystemColor[] getSysClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.SYSCLR$6, (List)list);
            final CTSystemColor[] array = new CTSystemColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSystemColor getSysClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSystemColor ctSystemColor = (CTSystemColor)this.get_store().find_element_user(CTDuotoneEffectImpl.SYSCLR$6, n);
            if (ctSystemColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSystemColor;
        }
    }
    
    public int sizeOfSysClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.SYSCLR$6);
        }
    }
    
    public void setSysClrArray(final CTSystemColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.SYSCLR$6);
    }
    
    public void setSysClrArray(final int n, final CTSystemColor ctSystemColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSystemColor, CTDuotoneEffectImpl.SYSCLR$6, n, (short)2);
    }
    
    public CTSystemColor insertNewSysClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSystemColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.SYSCLR$6, n);
        }
    }
    
    public CTSystemColor addNewSysClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSystemColor)this.get_store().add_element_user(CTDuotoneEffectImpl.SYSCLR$6);
        }
    }
    
    public void removeSysClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.SYSCLR$6, n);
        }
    }
    
    public List<CTSchemeColor> getSchemeClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SchemeClrList extends AbstractList<CTSchemeColor>
            {
                @Override
                public CTSchemeColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getSchemeClrArray(n);
                }
                
                @Override
                public CTSchemeColor set(final int n, final CTSchemeColor ctSchemeColor) {
                    final CTSchemeColor schemeClrArray = CTDuotoneEffectImpl.this.getSchemeClrArray(n);
                    CTDuotoneEffectImpl.this.setSchemeClrArray(n, ctSchemeColor);
                    return schemeClrArray;
                }
                
                @Override
                public void add(final int n, final CTSchemeColor ctSchemeColor) {
                    CTDuotoneEffectImpl.this.insertNewSchemeClr(n).set((XmlObject)ctSchemeColor);
                }
                
                @Override
                public CTSchemeColor remove(final int n) {
                    final CTSchemeColor schemeClrArray = CTDuotoneEffectImpl.this.getSchemeClrArray(n);
                    CTDuotoneEffectImpl.this.removeSchemeClr(n);
                    return schemeClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfSchemeClrArray();
                }
            }
            return new SchemeClrList();
        }
    }
    
    @Deprecated
    public CTSchemeColor[] getSchemeClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.SCHEMECLR$8, (List)list);
            final CTSchemeColor[] array = new CTSchemeColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSchemeColor getSchemeClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSchemeColor ctSchemeColor = (CTSchemeColor)this.get_store().find_element_user(CTDuotoneEffectImpl.SCHEMECLR$8, n);
            if (ctSchemeColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSchemeColor;
        }
    }
    
    public int sizeOfSchemeClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.SCHEMECLR$8);
        }
    }
    
    public void setSchemeClrArray(final CTSchemeColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.SCHEMECLR$8);
    }
    
    public void setSchemeClrArray(final int n, final CTSchemeColor ctSchemeColor) {
        this.generatedSetterHelperImpl((XmlObject)ctSchemeColor, CTDuotoneEffectImpl.SCHEMECLR$8, n, (short)2);
    }
    
    public CTSchemeColor insertNewSchemeClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemeColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.SCHEMECLR$8, n);
        }
    }
    
    public CTSchemeColor addNewSchemeClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSchemeColor)this.get_store().add_element_user(CTDuotoneEffectImpl.SCHEMECLR$8);
        }
    }
    
    public void removeSchemeClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.SCHEMECLR$8, n);
        }
    }
    
    public List<CTPresetColor> getPrstClrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PrstClrList extends AbstractList<CTPresetColor>
            {
                @Override
                public CTPresetColor get(final int n) {
                    return CTDuotoneEffectImpl.this.getPrstClrArray(n);
                }
                
                @Override
                public CTPresetColor set(final int n, final CTPresetColor ctPresetColor) {
                    final CTPresetColor prstClrArray = CTDuotoneEffectImpl.this.getPrstClrArray(n);
                    CTDuotoneEffectImpl.this.setPrstClrArray(n, ctPresetColor);
                    return prstClrArray;
                }
                
                @Override
                public void add(final int n, final CTPresetColor ctPresetColor) {
                    CTDuotoneEffectImpl.this.insertNewPrstClr(n).set((XmlObject)ctPresetColor);
                }
                
                @Override
                public CTPresetColor remove(final int n) {
                    final CTPresetColor prstClrArray = CTDuotoneEffectImpl.this.getPrstClrArray(n);
                    CTDuotoneEffectImpl.this.removePrstClr(n);
                    return prstClrArray;
                }
                
                @Override
                public int size() {
                    return CTDuotoneEffectImpl.this.sizeOfPrstClrArray();
                }
            }
            return new PrstClrList();
        }
    }
    
    @Deprecated
    public CTPresetColor[] getPrstClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDuotoneEffectImpl.PRSTCLR$10, (List)list);
            final CTPresetColor[] array = new CTPresetColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPresetColor getPrstClrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetColor ctPresetColor = (CTPresetColor)this.get_store().find_element_user(CTDuotoneEffectImpl.PRSTCLR$10, n);
            if (ctPresetColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPresetColor;
        }
    }
    
    public int sizeOfPrstClrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDuotoneEffectImpl.PRSTCLR$10);
        }
    }
    
    public void setPrstClrArray(final CTPresetColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDuotoneEffectImpl.PRSTCLR$10);
    }
    
    public void setPrstClrArray(final int n, final CTPresetColor ctPresetColor) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetColor, CTDuotoneEffectImpl.PRSTCLR$10, n, (short)2);
    }
    
    public CTPresetColor insertNewPrstClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetColor)this.get_store().insert_element_user(CTDuotoneEffectImpl.PRSTCLR$10, n);
        }
    }
    
    public CTPresetColor addNewPrstClr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetColor)this.get_store().add_element_user(CTDuotoneEffectImpl.PRSTCLR$10);
        }
    }
    
    public void removePrstClr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDuotoneEffectImpl.PRSTCLR$10, n);
        }
    }
    
    static {
        SCRGBCLR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "scrgbClr");
        SRGBCLR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "srgbClr");
        HSLCLR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hslClr");
        SYSCLR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sysClr");
        SCHEMECLR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "schemeClr");
        PRSTCLR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstClr");
    }
}
