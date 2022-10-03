package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInverseGammaTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGammaTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedAngle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositivePercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInverseTransform;
import org.openxmlformats.schemas.drawingml.x2006.main.CTComplementTransform;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedPercentage;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScRgbColorImpl extends XmlComplexContentImpl implements CTScRgbColor
{
    private static final long serialVersionUID = 1L;
    private static final QName TINT$0;
    private static final QName SHADE$2;
    private static final QName COMP$4;
    private static final QName INV$6;
    private static final QName GRAY$8;
    private static final QName ALPHA$10;
    private static final QName ALPHAOFF$12;
    private static final QName ALPHAMOD$14;
    private static final QName HUE$16;
    private static final QName HUEOFF$18;
    private static final QName HUEMOD$20;
    private static final QName SAT$22;
    private static final QName SATOFF$24;
    private static final QName SATMOD$26;
    private static final QName LUM$28;
    private static final QName LUMOFF$30;
    private static final QName LUMMOD$32;
    private static final QName RED$34;
    private static final QName REDOFF$36;
    private static final QName REDMOD$38;
    private static final QName GREEN$40;
    private static final QName GREENOFF$42;
    private static final QName GREENMOD$44;
    private static final QName BLUE$46;
    private static final QName BLUEOFF$48;
    private static final QName BLUEMOD$50;
    private static final QName GAMMA$52;
    private static final QName INVGAMMA$54;
    private static final QName R$56;
    private static final QName G$58;
    private static final QName B$60;
    
    public CTScRgbColorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPositiveFixedPercentage> getTintList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TintList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getTintArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage tintArray = CTScRgbColorImpl.this.getTintArray(n);
                    CTScRgbColorImpl.this.setTintArray(n, ctPositiveFixedPercentage);
                    return tintArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTScRgbColorImpl.this.insertNewTint(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage tintArray = CTScRgbColorImpl.this.getTintArray(n);
                    CTScRgbColorImpl.this.removeTint(n);
                    return tintArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfTintArray();
                }
            }
            return new TintList();
        }
    }
    
    @Deprecated
    public CTPositiveFixedPercentage[] getTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.TINT$0, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getTintArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTScRgbColorImpl.TINT$0, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.TINT$0);
        }
    }
    
    public void setTintArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.TINT$0);
    }
    
    public void setTintArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTScRgbColorImpl.TINT$0, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.TINT$0, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTScRgbColorImpl.TINT$0);
        }
    }
    
    public void removeTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.TINT$0, n);
        }
    }
    
    public List<CTPositiveFixedPercentage> getShadeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadeList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getShadeArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage shadeArray = CTScRgbColorImpl.this.getShadeArray(n);
                    CTScRgbColorImpl.this.setShadeArray(n, ctPositiveFixedPercentage);
                    return shadeArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTScRgbColorImpl.this.insertNewShade(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage shadeArray = CTScRgbColorImpl.this.getShadeArray(n);
                    CTScRgbColorImpl.this.removeShade(n);
                    return shadeArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfShadeArray();
                }
            }
            return new ShadeList();
        }
    }
    
    @Deprecated
    public CTPositiveFixedPercentage[] getShadeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.SHADE$2, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getShadeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTScRgbColorImpl.SHADE$2, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfShadeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.SHADE$2);
        }
    }
    
    public void setShadeArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.SHADE$2);
    }
    
    public void setShadeArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTScRgbColorImpl.SHADE$2, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewShade(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.SHADE$2, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTScRgbColorImpl.SHADE$2);
        }
    }
    
    public void removeShade(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.SHADE$2, n);
        }
    }
    
    public List<CTComplementTransform> getCompList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CompList extends AbstractList<CTComplementTransform>
            {
                @Override
                public CTComplementTransform get(final int n) {
                    return CTScRgbColorImpl.this.getCompArray(n);
                }
                
                @Override
                public CTComplementTransform set(final int n, final CTComplementTransform ctComplementTransform) {
                    final CTComplementTransform compArray = CTScRgbColorImpl.this.getCompArray(n);
                    CTScRgbColorImpl.this.setCompArray(n, ctComplementTransform);
                    return compArray;
                }
                
                @Override
                public void add(final int n, final CTComplementTransform ctComplementTransform) {
                    CTScRgbColorImpl.this.insertNewComp(n).set((XmlObject)ctComplementTransform);
                }
                
                @Override
                public CTComplementTransform remove(final int n) {
                    final CTComplementTransform compArray = CTScRgbColorImpl.this.getCompArray(n);
                    CTScRgbColorImpl.this.removeComp(n);
                    return compArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfCompArray();
                }
            }
            return new CompList();
        }
    }
    
    @Deprecated
    public CTComplementTransform[] getCompArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.COMP$4, (List)list);
            final CTComplementTransform[] array = new CTComplementTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTComplementTransform getCompArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComplementTransform ctComplementTransform = (CTComplementTransform)this.get_store().find_element_user(CTScRgbColorImpl.COMP$4, n);
            if (ctComplementTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctComplementTransform;
        }
    }
    
    public int sizeOfCompArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.COMP$4);
        }
    }
    
    public void setCompArray(final CTComplementTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.COMP$4);
    }
    
    public void setCompArray(final int n, final CTComplementTransform ctComplementTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctComplementTransform, CTScRgbColorImpl.COMP$4, n, (short)2);
    }
    
    public CTComplementTransform insertNewComp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComplementTransform)this.get_store().insert_element_user(CTScRgbColorImpl.COMP$4, n);
        }
    }
    
    public CTComplementTransform addNewComp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComplementTransform)this.get_store().add_element_user(CTScRgbColorImpl.COMP$4);
        }
    }
    
    public void removeComp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.COMP$4, n);
        }
    }
    
    public List<CTInverseTransform> getInvList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InvList extends AbstractList<CTInverseTransform>
            {
                @Override
                public CTInverseTransform get(final int n) {
                    return CTScRgbColorImpl.this.getInvArray(n);
                }
                
                @Override
                public CTInverseTransform set(final int n, final CTInverseTransform ctInverseTransform) {
                    final CTInverseTransform invArray = CTScRgbColorImpl.this.getInvArray(n);
                    CTScRgbColorImpl.this.setInvArray(n, ctInverseTransform);
                    return invArray;
                }
                
                @Override
                public void add(final int n, final CTInverseTransform ctInverseTransform) {
                    CTScRgbColorImpl.this.insertNewInv(n).set((XmlObject)ctInverseTransform);
                }
                
                @Override
                public CTInverseTransform remove(final int n) {
                    final CTInverseTransform invArray = CTScRgbColorImpl.this.getInvArray(n);
                    CTScRgbColorImpl.this.removeInv(n);
                    return invArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfInvArray();
                }
            }
            return new InvList();
        }
    }
    
    @Deprecated
    public CTInverseTransform[] getInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.INV$6, (List)list);
            final CTInverseTransform[] array = new CTInverseTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInverseTransform getInvArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInverseTransform ctInverseTransform = (CTInverseTransform)this.get_store().find_element_user(CTScRgbColorImpl.INV$6, n);
            if (ctInverseTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInverseTransform;
        }
    }
    
    public int sizeOfInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.INV$6);
        }
    }
    
    public void setInvArray(final CTInverseTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.INV$6);
    }
    
    public void setInvArray(final int n, final CTInverseTransform ctInverseTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctInverseTransform, CTScRgbColorImpl.INV$6, n, (short)2);
    }
    
    public CTInverseTransform insertNewInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseTransform)this.get_store().insert_element_user(CTScRgbColorImpl.INV$6, n);
        }
    }
    
    public CTInverseTransform addNewInv() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseTransform)this.get_store().add_element_user(CTScRgbColorImpl.INV$6);
        }
    }
    
    public void removeInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.INV$6, n);
        }
    }
    
    public List<CTGrayscaleTransform> getGrayList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GrayList extends AbstractList<CTGrayscaleTransform>
            {
                @Override
                public CTGrayscaleTransform get(final int n) {
                    return CTScRgbColorImpl.this.getGrayArray(n);
                }
                
                @Override
                public CTGrayscaleTransform set(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
                    final CTGrayscaleTransform grayArray = CTScRgbColorImpl.this.getGrayArray(n);
                    CTScRgbColorImpl.this.setGrayArray(n, ctGrayscaleTransform);
                    return grayArray;
                }
                
                @Override
                public void add(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
                    CTScRgbColorImpl.this.insertNewGray(n).set((XmlObject)ctGrayscaleTransform);
                }
                
                @Override
                public CTGrayscaleTransform remove(final int n) {
                    final CTGrayscaleTransform grayArray = CTScRgbColorImpl.this.getGrayArray(n);
                    CTScRgbColorImpl.this.removeGray(n);
                    return grayArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfGrayArray();
                }
            }
            return new GrayList();
        }
    }
    
    @Deprecated
    public CTGrayscaleTransform[] getGrayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.GRAY$8, (List)list);
            final CTGrayscaleTransform[] array = new CTGrayscaleTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGrayscaleTransform getGrayArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrayscaleTransform ctGrayscaleTransform = (CTGrayscaleTransform)this.get_store().find_element_user(CTScRgbColorImpl.GRAY$8, n);
            if (ctGrayscaleTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGrayscaleTransform;
        }
    }
    
    public int sizeOfGrayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.GRAY$8);
        }
    }
    
    public void setGrayArray(final CTGrayscaleTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.GRAY$8);
    }
    
    public void setGrayArray(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctGrayscaleTransform, CTScRgbColorImpl.GRAY$8, n, (short)2);
    }
    
    public CTGrayscaleTransform insertNewGray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleTransform)this.get_store().insert_element_user(CTScRgbColorImpl.GRAY$8, n);
        }
    }
    
    public CTGrayscaleTransform addNewGray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleTransform)this.get_store().add_element_user(CTScRgbColorImpl.GRAY$8);
        }
    }
    
    public void removeGray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.GRAY$8, n);
        }
    }
    
    public List<CTPositiveFixedPercentage> getAlphaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getAlphaArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage alphaArray = CTScRgbColorImpl.this.getAlphaArray(n);
                    CTScRgbColorImpl.this.setAlphaArray(n, ctPositiveFixedPercentage);
                    return alphaArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTScRgbColorImpl.this.insertNewAlpha(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage alphaArray = CTScRgbColorImpl.this.getAlphaArray(n);
                    CTScRgbColorImpl.this.removeAlpha(n);
                    return alphaArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfAlphaArray();
                }
            }
            return new AlphaList();
        }
    }
    
    @Deprecated
    public CTPositiveFixedPercentage[] getAlphaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.ALPHA$10, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getAlphaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTScRgbColorImpl.ALPHA$10, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfAlphaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.ALPHA$10);
        }
    }
    
    public void setAlphaArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.ALPHA$10);
    }
    
    public void setAlphaArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTScRgbColorImpl.ALPHA$10, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewAlpha(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.ALPHA$10, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewAlpha() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTScRgbColorImpl.ALPHA$10);
        }
    }
    
    public void removeAlpha(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.ALPHA$10, n);
        }
    }
    
    public List<CTFixedPercentage> getAlphaOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaOffList extends AbstractList<CTFixedPercentage>
            {
                @Override
                public CTFixedPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getAlphaOffArray(n);
                }
                
                @Override
                public CTFixedPercentage set(final int n, final CTFixedPercentage ctFixedPercentage) {
                    final CTFixedPercentage alphaOffArray = CTScRgbColorImpl.this.getAlphaOffArray(n);
                    CTScRgbColorImpl.this.setAlphaOffArray(n, ctFixedPercentage);
                    return alphaOffArray;
                }
                
                @Override
                public void add(final int n, final CTFixedPercentage ctFixedPercentage) {
                    CTScRgbColorImpl.this.insertNewAlphaOff(n).set((XmlObject)ctFixedPercentage);
                }
                
                @Override
                public CTFixedPercentage remove(final int n) {
                    final CTFixedPercentage alphaOffArray = CTScRgbColorImpl.this.getAlphaOffArray(n);
                    CTScRgbColorImpl.this.removeAlphaOff(n);
                    return alphaOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfAlphaOffArray();
                }
            }
            return new AlphaOffList();
        }
    }
    
    @Deprecated
    public CTFixedPercentage[] getAlphaOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.ALPHAOFF$12, (List)list);
            final CTFixedPercentage[] array = new CTFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFixedPercentage getAlphaOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFixedPercentage ctFixedPercentage = (CTFixedPercentage)this.get_store().find_element_user(CTScRgbColorImpl.ALPHAOFF$12, n);
            if (ctFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFixedPercentage;
        }
    }
    
    public int sizeOfAlphaOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.ALPHAOFF$12);
        }
    }
    
    public void setAlphaOffArray(final CTFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.ALPHAOFF$12);
    }
    
    public void setAlphaOffArray(final int n, final CTFixedPercentage ctFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctFixedPercentage, CTScRgbColorImpl.ALPHAOFF$12, n, (short)2);
    }
    
    public CTFixedPercentage insertNewAlphaOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFixedPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.ALPHAOFF$12, n);
        }
    }
    
    public CTFixedPercentage addNewAlphaOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFixedPercentage)this.get_store().add_element_user(CTScRgbColorImpl.ALPHAOFF$12);
        }
    }
    
    public void removeAlphaOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.ALPHAOFF$12, n);
        }
    }
    
    public List<CTPositivePercentage> getAlphaModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModList extends AbstractList<CTPositivePercentage>
            {
                @Override
                public CTPositivePercentage get(final int n) {
                    return CTScRgbColorImpl.this.getAlphaModArray(n);
                }
                
                @Override
                public CTPositivePercentage set(final int n, final CTPositivePercentage ctPositivePercentage) {
                    final CTPositivePercentage alphaModArray = CTScRgbColorImpl.this.getAlphaModArray(n);
                    CTScRgbColorImpl.this.setAlphaModArray(n, ctPositivePercentage);
                    return alphaModArray;
                }
                
                @Override
                public void add(final int n, final CTPositivePercentage ctPositivePercentage) {
                    CTScRgbColorImpl.this.insertNewAlphaMod(n).set((XmlObject)ctPositivePercentage);
                }
                
                @Override
                public CTPositivePercentage remove(final int n) {
                    final CTPositivePercentage alphaModArray = CTScRgbColorImpl.this.getAlphaModArray(n);
                    CTScRgbColorImpl.this.removeAlphaMod(n);
                    return alphaModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfAlphaModArray();
                }
            }
            return new AlphaModList();
        }
    }
    
    @Deprecated
    public CTPositivePercentage[] getAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.ALPHAMOD$14, (List)list);
            final CTPositivePercentage[] array = new CTPositivePercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositivePercentage getAlphaModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositivePercentage ctPositivePercentage = (CTPositivePercentage)this.get_store().find_element_user(CTScRgbColorImpl.ALPHAMOD$14, n);
            if (ctPositivePercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositivePercentage;
        }
    }
    
    public int sizeOfAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.ALPHAMOD$14);
        }
    }
    
    public void setAlphaModArray(final CTPositivePercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.ALPHAMOD$14);
    }
    
    public void setAlphaModArray(final int n, final CTPositivePercentage ctPositivePercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositivePercentage, CTScRgbColorImpl.ALPHAMOD$14, n, (short)2);
    }
    
    public CTPositivePercentage insertNewAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().insert_element_user(CTScRgbColorImpl.ALPHAMOD$14, n);
        }
    }
    
    public CTPositivePercentage addNewAlphaMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().add_element_user(CTScRgbColorImpl.ALPHAMOD$14);
        }
    }
    
    public void removeAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.ALPHAMOD$14, n);
        }
    }
    
    public List<CTPositiveFixedAngle> getHueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueList extends AbstractList<CTPositiveFixedAngle>
            {
                @Override
                public CTPositiveFixedAngle get(final int n) {
                    return CTScRgbColorImpl.this.getHueArray(n);
                }
                
                @Override
                public CTPositiveFixedAngle set(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
                    final CTPositiveFixedAngle hueArray = CTScRgbColorImpl.this.getHueArray(n);
                    CTScRgbColorImpl.this.setHueArray(n, ctPositiveFixedAngle);
                    return hueArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
                    CTScRgbColorImpl.this.insertNewHue(n).set((XmlObject)ctPositiveFixedAngle);
                }
                
                @Override
                public CTPositiveFixedAngle remove(final int n) {
                    final CTPositiveFixedAngle hueArray = CTScRgbColorImpl.this.getHueArray(n);
                    CTScRgbColorImpl.this.removeHue(n);
                    return hueArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfHueArray();
                }
            }
            return new HueList();
        }
    }
    
    @Deprecated
    public CTPositiveFixedAngle[] getHueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.HUE$16, (List)list);
            final CTPositiveFixedAngle[] array = new CTPositiveFixedAngle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedAngle getHueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedAngle ctPositiveFixedAngle = (CTPositiveFixedAngle)this.get_store().find_element_user(CTScRgbColorImpl.HUE$16, n);
            if (ctPositiveFixedAngle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedAngle;
        }
    }
    
    public int sizeOfHueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.HUE$16);
        }
    }
    
    public void setHueArray(final CTPositiveFixedAngle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.HUE$16);
    }
    
    public void setHueArray(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedAngle, CTScRgbColorImpl.HUE$16, n, (short)2);
    }
    
    public CTPositiveFixedAngle insertNewHue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedAngle)this.get_store().insert_element_user(CTScRgbColorImpl.HUE$16, n);
        }
    }
    
    public CTPositiveFixedAngle addNewHue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedAngle)this.get_store().add_element_user(CTScRgbColorImpl.HUE$16);
        }
    }
    
    public void removeHue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.HUE$16, n);
        }
    }
    
    public List<CTAngle> getHueOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueOffList extends AbstractList<CTAngle>
            {
                @Override
                public CTAngle get(final int n) {
                    return CTScRgbColorImpl.this.getHueOffArray(n);
                }
                
                @Override
                public CTAngle set(final int n, final CTAngle ctAngle) {
                    final CTAngle hueOffArray = CTScRgbColorImpl.this.getHueOffArray(n);
                    CTScRgbColorImpl.this.setHueOffArray(n, ctAngle);
                    return hueOffArray;
                }
                
                @Override
                public void add(final int n, final CTAngle ctAngle) {
                    CTScRgbColorImpl.this.insertNewHueOff(n).set((XmlObject)ctAngle);
                }
                
                @Override
                public CTAngle remove(final int n) {
                    final CTAngle hueOffArray = CTScRgbColorImpl.this.getHueOffArray(n);
                    CTScRgbColorImpl.this.removeHueOff(n);
                    return hueOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfHueOffArray();
                }
            }
            return new HueOffList();
        }
    }
    
    @Deprecated
    public CTAngle[] getHueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.HUEOFF$18, (List)list);
            final CTAngle[] array = new CTAngle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAngle getHueOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAngle ctAngle = (CTAngle)this.get_store().find_element_user(CTScRgbColorImpl.HUEOFF$18, n);
            if (ctAngle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAngle;
        }
    }
    
    public int sizeOfHueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.HUEOFF$18);
        }
    }
    
    public void setHueOffArray(final CTAngle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.HUEOFF$18);
    }
    
    public void setHueOffArray(final int n, final CTAngle ctAngle) {
        this.generatedSetterHelperImpl((XmlObject)ctAngle, CTScRgbColorImpl.HUEOFF$18, n, (short)2);
    }
    
    public CTAngle insertNewHueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAngle)this.get_store().insert_element_user(CTScRgbColorImpl.HUEOFF$18, n);
        }
    }
    
    public CTAngle addNewHueOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAngle)this.get_store().add_element_user(CTScRgbColorImpl.HUEOFF$18);
        }
    }
    
    public void removeHueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.HUEOFF$18, n);
        }
    }
    
    public List<CTPositivePercentage> getHueModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueModList extends AbstractList<CTPositivePercentage>
            {
                @Override
                public CTPositivePercentage get(final int n) {
                    return CTScRgbColorImpl.this.getHueModArray(n);
                }
                
                @Override
                public CTPositivePercentage set(final int n, final CTPositivePercentage ctPositivePercentage) {
                    final CTPositivePercentage hueModArray = CTScRgbColorImpl.this.getHueModArray(n);
                    CTScRgbColorImpl.this.setHueModArray(n, ctPositivePercentage);
                    return hueModArray;
                }
                
                @Override
                public void add(final int n, final CTPositivePercentage ctPositivePercentage) {
                    CTScRgbColorImpl.this.insertNewHueMod(n).set((XmlObject)ctPositivePercentage);
                }
                
                @Override
                public CTPositivePercentage remove(final int n) {
                    final CTPositivePercentage hueModArray = CTScRgbColorImpl.this.getHueModArray(n);
                    CTScRgbColorImpl.this.removeHueMod(n);
                    return hueModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfHueModArray();
                }
            }
            return new HueModList();
        }
    }
    
    @Deprecated
    public CTPositivePercentage[] getHueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.HUEMOD$20, (List)list);
            final CTPositivePercentage[] array = new CTPositivePercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositivePercentage getHueModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositivePercentage ctPositivePercentage = (CTPositivePercentage)this.get_store().find_element_user(CTScRgbColorImpl.HUEMOD$20, n);
            if (ctPositivePercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositivePercentage;
        }
    }
    
    public int sizeOfHueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.HUEMOD$20);
        }
    }
    
    public void setHueModArray(final CTPositivePercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.HUEMOD$20);
    }
    
    public void setHueModArray(final int n, final CTPositivePercentage ctPositivePercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositivePercentage, CTScRgbColorImpl.HUEMOD$20, n, (short)2);
    }
    
    public CTPositivePercentage insertNewHueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().insert_element_user(CTScRgbColorImpl.HUEMOD$20, n);
        }
    }
    
    public CTPositivePercentage addNewHueMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().add_element_user(CTScRgbColorImpl.HUEMOD$20);
        }
    }
    
    public void removeHueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.HUEMOD$20, n);
        }
    }
    
    public List<CTPercentage> getSatList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getSatArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satArray = CTScRgbColorImpl.this.getSatArray(n);
                    CTScRgbColorImpl.this.setSatArray(n, ctPercentage);
                    return satArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewSat(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satArray = CTScRgbColorImpl.this.getSatArray(n);
                    CTScRgbColorImpl.this.removeSat(n);
                    return satArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfSatArray();
                }
            }
            return new SatList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getSatArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.SAT$22, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.SAT$22, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.SAT$22);
        }
    }
    
    public void setSatArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.SAT$22);
    }
    
    public void setSatArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.SAT$22, n, (short)2);
    }
    
    public CTPercentage insertNewSat(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.SAT$22, n);
        }
    }
    
    public CTPercentage addNewSat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.SAT$22);
        }
    }
    
    public void removeSat(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.SAT$22, n);
        }
    }
    
    public List<CTPercentage> getSatOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getSatOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satOffArray = CTScRgbColorImpl.this.getSatOffArray(n);
                    CTScRgbColorImpl.this.setSatOffArray(n, ctPercentage);
                    return satOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewSatOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satOffArray = CTScRgbColorImpl.this.getSatOffArray(n);
                    CTScRgbColorImpl.this.removeSatOff(n);
                    return satOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfSatOffArray();
                }
            }
            return new SatOffList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getSatOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.SATOFF$24, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.SATOFF$24, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.SATOFF$24);
        }
    }
    
    public void setSatOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.SATOFF$24);
    }
    
    public void setSatOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.SATOFF$24, n, (short)2);
    }
    
    public CTPercentage insertNewSatOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.SATOFF$24, n);
        }
    }
    
    public CTPercentage addNewSatOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.SATOFF$24);
        }
    }
    
    public void removeSatOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.SATOFF$24, n);
        }
    }
    
    public List<CTPercentage> getSatModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getSatModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satModArray = CTScRgbColorImpl.this.getSatModArray(n);
                    CTScRgbColorImpl.this.setSatModArray(n, ctPercentage);
                    return satModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewSatMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satModArray = CTScRgbColorImpl.this.getSatModArray(n);
                    CTScRgbColorImpl.this.removeSatMod(n);
                    return satModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfSatModArray();
                }
            }
            return new SatModList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getSatModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.SATMOD$26, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.SATMOD$26, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.SATMOD$26);
        }
    }
    
    public void setSatModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.SATMOD$26);
    }
    
    public void setSatModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.SATMOD$26, n, (short)2);
    }
    
    public CTPercentage insertNewSatMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.SATMOD$26, n);
        }
    }
    
    public CTPercentage addNewSatMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.SATMOD$26);
        }
    }
    
    public void removeSatMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.SATMOD$26, n);
        }
    }
    
    public List<CTPercentage> getLumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getLumArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumArray = CTScRgbColorImpl.this.getLumArray(n);
                    CTScRgbColorImpl.this.setLumArray(n, ctPercentage);
                    return lumArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewLum(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumArray = CTScRgbColorImpl.this.getLumArray(n);
                    CTScRgbColorImpl.this.removeLum(n);
                    return lumArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfLumArray();
                }
            }
            return new LumList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.LUM$28, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.LUM$28, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.LUM$28);
        }
    }
    
    public void setLumArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.LUM$28);
    }
    
    public void setLumArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.LUM$28, n, (short)2);
    }
    
    public CTPercentage insertNewLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.LUM$28, n);
        }
    }
    
    public CTPercentage addNewLum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.LUM$28);
        }
    }
    
    public void removeLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.LUM$28, n);
        }
    }
    
    public List<CTPercentage> getLumOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getLumOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumOffArray = CTScRgbColorImpl.this.getLumOffArray(n);
                    CTScRgbColorImpl.this.setLumOffArray(n, ctPercentage);
                    return lumOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewLumOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumOffArray = CTScRgbColorImpl.this.getLumOffArray(n);
                    CTScRgbColorImpl.this.removeLumOff(n);
                    return lumOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfLumOffArray();
                }
            }
            return new LumOffList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getLumOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.LUMOFF$30, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.LUMOFF$30, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.LUMOFF$30);
        }
    }
    
    public void setLumOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.LUMOFF$30);
    }
    
    public void setLumOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.LUMOFF$30, n, (short)2);
    }
    
    public CTPercentage insertNewLumOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.LUMOFF$30, n);
        }
    }
    
    public CTPercentage addNewLumOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.LUMOFF$30);
        }
    }
    
    public void removeLumOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.LUMOFF$30, n);
        }
    }
    
    public List<CTPercentage> getLumModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getLumModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumModArray = CTScRgbColorImpl.this.getLumModArray(n);
                    CTScRgbColorImpl.this.setLumModArray(n, ctPercentage);
                    return lumModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewLumMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumModArray = CTScRgbColorImpl.this.getLumModArray(n);
                    CTScRgbColorImpl.this.removeLumMod(n);
                    return lumModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfLumModArray();
                }
            }
            return new LumModList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getLumModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.LUMMOD$32, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.LUMMOD$32, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.LUMMOD$32);
        }
    }
    
    public void setLumModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.LUMMOD$32);
    }
    
    public void setLumModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.LUMMOD$32, n, (short)2);
    }
    
    public CTPercentage insertNewLumMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.LUMMOD$32, n);
        }
    }
    
    public CTPercentage addNewLumMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.LUMMOD$32);
        }
    }
    
    public void removeLumMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.LUMMOD$32, n);
        }
    }
    
    public List<CTPercentage> getRedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getRedArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redArray = CTScRgbColorImpl.this.getRedArray(n);
                    CTScRgbColorImpl.this.setRedArray(n, ctPercentage);
                    return redArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewRed(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redArray = CTScRgbColorImpl.this.getRedArray(n);
                    CTScRgbColorImpl.this.removeRed(n);
                    return redArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfRedArray();
                }
            }
            return new RedList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getRedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.RED$34, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.RED$34, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.RED$34);
        }
    }
    
    public void setRedArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.RED$34);
    }
    
    public void setRedArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.RED$34, n, (short)2);
    }
    
    public CTPercentage insertNewRed(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.RED$34, n);
        }
    }
    
    public CTPercentage addNewRed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.RED$34);
        }
    }
    
    public void removeRed(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.RED$34, n);
        }
    }
    
    public List<CTPercentage> getRedOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getRedOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redOffArray = CTScRgbColorImpl.this.getRedOffArray(n);
                    CTScRgbColorImpl.this.setRedOffArray(n, ctPercentage);
                    return redOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewRedOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redOffArray = CTScRgbColorImpl.this.getRedOffArray(n);
                    CTScRgbColorImpl.this.removeRedOff(n);
                    return redOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfRedOffArray();
                }
            }
            return new RedOffList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getRedOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.REDOFF$36, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.REDOFF$36, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.REDOFF$36);
        }
    }
    
    public void setRedOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.REDOFF$36);
    }
    
    public void setRedOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.REDOFF$36, n, (short)2);
    }
    
    public CTPercentage insertNewRedOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.REDOFF$36, n);
        }
    }
    
    public CTPercentage addNewRedOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.REDOFF$36);
        }
    }
    
    public void removeRedOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.REDOFF$36, n);
        }
    }
    
    public List<CTPercentage> getRedModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getRedModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redModArray = CTScRgbColorImpl.this.getRedModArray(n);
                    CTScRgbColorImpl.this.setRedModArray(n, ctPercentage);
                    return redModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewRedMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redModArray = CTScRgbColorImpl.this.getRedModArray(n);
                    CTScRgbColorImpl.this.removeRedMod(n);
                    return redModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfRedModArray();
                }
            }
            return new RedModList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getRedModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.REDMOD$38, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.REDMOD$38, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.REDMOD$38);
        }
    }
    
    public void setRedModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.REDMOD$38);
    }
    
    public void setRedModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.REDMOD$38, n, (short)2);
    }
    
    public CTPercentage insertNewRedMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.REDMOD$38, n);
        }
    }
    
    public CTPercentage addNewRedMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.REDMOD$38);
        }
    }
    
    public void removeRedMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.REDMOD$38, n);
        }
    }
    
    public List<CTPercentage> getGreenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getGreenArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenArray = CTScRgbColorImpl.this.getGreenArray(n);
                    CTScRgbColorImpl.this.setGreenArray(n, ctPercentage);
                    return greenArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewGreen(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenArray = CTScRgbColorImpl.this.getGreenArray(n);
                    CTScRgbColorImpl.this.removeGreen(n);
                    return greenArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfGreenArray();
                }
            }
            return new GreenList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getGreenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.GREEN$40, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.GREEN$40, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.GREEN$40);
        }
    }
    
    public void setGreenArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.GREEN$40);
    }
    
    public void setGreenArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.GREEN$40, n, (short)2);
    }
    
    public CTPercentage insertNewGreen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.GREEN$40, n);
        }
    }
    
    public CTPercentage addNewGreen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.GREEN$40);
        }
    }
    
    public void removeGreen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.GREEN$40, n);
        }
    }
    
    public List<CTPercentage> getGreenOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getGreenOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenOffArray = CTScRgbColorImpl.this.getGreenOffArray(n);
                    CTScRgbColorImpl.this.setGreenOffArray(n, ctPercentage);
                    return greenOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewGreenOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenOffArray = CTScRgbColorImpl.this.getGreenOffArray(n);
                    CTScRgbColorImpl.this.removeGreenOff(n);
                    return greenOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfGreenOffArray();
                }
            }
            return new GreenOffList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getGreenOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.GREENOFF$42, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.GREENOFF$42, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.GREENOFF$42);
        }
    }
    
    public void setGreenOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.GREENOFF$42);
    }
    
    public void setGreenOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.GREENOFF$42, n, (short)2);
    }
    
    public CTPercentage insertNewGreenOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.GREENOFF$42, n);
        }
    }
    
    public CTPercentage addNewGreenOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.GREENOFF$42);
        }
    }
    
    public void removeGreenOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.GREENOFF$42, n);
        }
    }
    
    public List<CTPercentage> getGreenModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getGreenModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenModArray = CTScRgbColorImpl.this.getGreenModArray(n);
                    CTScRgbColorImpl.this.setGreenModArray(n, ctPercentage);
                    return greenModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewGreenMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenModArray = CTScRgbColorImpl.this.getGreenModArray(n);
                    CTScRgbColorImpl.this.removeGreenMod(n);
                    return greenModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfGreenModArray();
                }
            }
            return new GreenModList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getGreenModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.GREENMOD$44, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.GREENMOD$44, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.GREENMOD$44);
        }
    }
    
    public void setGreenModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.GREENMOD$44);
    }
    
    public void setGreenModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.GREENMOD$44, n, (short)2);
    }
    
    public CTPercentage insertNewGreenMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.GREENMOD$44, n);
        }
    }
    
    public CTPercentage addNewGreenMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.GREENMOD$44);
        }
    }
    
    public void removeGreenMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.GREENMOD$44, n);
        }
    }
    
    public List<CTPercentage> getBlueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getBlueArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueArray = CTScRgbColorImpl.this.getBlueArray(n);
                    CTScRgbColorImpl.this.setBlueArray(n, ctPercentage);
                    return blueArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewBlue(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueArray = CTScRgbColorImpl.this.getBlueArray(n);
                    CTScRgbColorImpl.this.removeBlue(n);
                    return blueArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfBlueArray();
                }
            }
            return new BlueList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getBlueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.BLUE$46, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.BLUE$46, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.BLUE$46);
        }
    }
    
    public void setBlueArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.BLUE$46);
    }
    
    public void setBlueArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.BLUE$46, n, (short)2);
    }
    
    public CTPercentage insertNewBlue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.BLUE$46, n);
        }
    }
    
    public CTPercentage addNewBlue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.BLUE$46);
        }
    }
    
    public void removeBlue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.BLUE$46, n);
        }
    }
    
    public List<CTPercentage> getBlueOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getBlueOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueOffArray = CTScRgbColorImpl.this.getBlueOffArray(n);
                    CTScRgbColorImpl.this.setBlueOffArray(n, ctPercentage);
                    return blueOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewBlueOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueOffArray = CTScRgbColorImpl.this.getBlueOffArray(n);
                    CTScRgbColorImpl.this.removeBlueOff(n);
                    return blueOffArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfBlueOffArray();
                }
            }
            return new BlueOffList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getBlueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.BLUEOFF$48, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.BLUEOFF$48, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.BLUEOFF$48);
        }
    }
    
    public void setBlueOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.BLUEOFF$48);
    }
    
    public void setBlueOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.BLUEOFF$48, n, (short)2);
    }
    
    public CTPercentage insertNewBlueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.BLUEOFF$48, n);
        }
    }
    
    public CTPercentage addNewBlueOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.BLUEOFF$48);
        }
    }
    
    public void removeBlueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.BLUEOFF$48, n);
        }
    }
    
    public List<CTPercentage> getBlueModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTScRgbColorImpl.this.getBlueModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueModArray = CTScRgbColorImpl.this.getBlueModArray(n);
                    CTScRgbColorImpl.this.setBlueModArray(n, ctPercentage);
                    return blueModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTScRgbColorImpl.this.insertNewBlueMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueModArray = CTScRgbColorImpl.this.getBlueModArray(n);
                    CTScRgbColorImpl.this.removeBlueMod(n);
                    return blueModArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfBlueModArray();
                }
            }
            return new BlueModList();
        }
    }
    
    @Deprecated
    public CTPercentage[] getBlueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.BLUEMOD$50, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTScRgbColorImpl.BLUEMOD$50, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.BLUEMOD$50);
        }
    }
    
    public void setBlueModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.BLUEMOD$50);
    }
    
    public void setBlueModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTScRgbColorImpl.BLUEMOD$50, n, (short)2);
    }
    
    public CTPercentage insertNewBlueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTScRgbColorImpl.BLUEMOD$50, n);
        }
    }
    
    public CTPercentage addNewBlueMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTScRgbColorImpl.BLUEMOD$50);
        }
    }
    
    public void removeBlueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.BLUEMOD$50, n);
        }
    }
    
    public List<CTGammaTransform> getGammaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GammaList extends AbstractList<CTGammaTransform>
            {
                @Override
                public CTGammaTransform get(final int n) {
                    return CTScRgbColorImpl.this.getGammaArray(n);
                }
                
                @Override
                public CTGammaTransform set(final int n, final CTGammaTransform ctGammaTransform) {
                    final CTGammaTransform gammaArray = CTScRgbColorImpl.this.getGammaArray(n);
                    CTScRgbColorImpl.this.setGammaArray(n, ctGammaTransform);
                    return gammaArray;
                }
                
                @Override
                public void add(final int n, final CTGammaTransform ctGammaTransform) {
                    CTScRgbColorImpl.this.insertNewGamma(n).set((XmlObject)ctGammaTransform);
                }
                
                @Override
                public CTGammaTransform remove(final int n) {
                    final CTGammaTransform gammaArray = CTScRgbColorImpl.this.getGammaArray(n);
                    CTScRgbColorImpl.this.removeGamma(n);
                    return gammaArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfGammaArray();
                }
            }
            return new GammaList();
        }
    }
    
    @Deprecated
    public CTGammaTransform[] getGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.GAMMA$52, (List)list);
            final CTGammaTransform[] array = new CTGammaTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGammaTransform getGammaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGammaTransform ctGammaTransform = (CTGammaTransform)this.get_store().find_element_user(CTScRgbColorImpl.GAMMA$52, n);
            if (ctGammaTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGammaTransform;
        }
    }
    
    public int sizeOfGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.GAMMA$52);
        }
    }
    
    public void setGammaArray(final CTGammaTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.GAMMA$52);
    }
    
    public void setGammaArray(final int n, final CTGammaTransform ctGammaTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctGammaTransform, CTScRgbColorImpl.GAMMA$52, n, (short)2);
    }
    
    public CTGammaTransform insertNewGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGammaTransform)this.get_store().insert_element_user(CTScRgbColorImpl.GAMMA$52, n);
        }
    }
    
    public CTGammaTransform addNewGamma() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGammaTransform)this.get_store().add_element_user(CTScRgbColorImpl.GAMMA$52);
        }
    }
    
    public void removeGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.GAMMA$52, n);
        }
    }
    
    public List<CTInverseGammaTransform> getInvGammaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InvGammaList extends AbstractList<CTInverseGammaTransform>
            {
                @Override
                public CTInverseGammaTransform get(final int n) {
                    return CTScRgbColorImpl.this.getInvGammaArray(n);
                }
                
                @Override
                public CTInverseGammaTransform set(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
                    final CTInverseGammaTransform invGammaArray = CTScRgbColorImpl.this.getInvGammaArray(n);
                    CTScRgbColorImpl.this.setInvGammaArray(n, ctInverseGammaTransform);
                    return invGammaArray;
                }
                
                @Override
                public void add(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
                    CTScRgbColorImpl.this.insertNewInvGamma(n).set((XmlObject)ctInverseGammaTransform);
                }
                
                @Override
                public CTInverseGammaTransform remove(final int n) {
                    final CTInverseGammaTransform invGammaArray = CTScRgbColorImpl.this.getInvGammaArray(n);
                    CTScRgbColorImpl.this.removeInvGamma(n);
                    return invGammaArray;
                }
                
                @Override
                public int size() {
                    return CTScRgbColorImpl.this.sizeOfInvGammaArray();
                }
            }
            return new InvGammaList();
        }
    }
    
    @Deprecated
    public CTInverseGammaTransform[] getInvGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScRgbColorImpl.INVGAMMA$54, (List)list);
            final CTInverseGammaTransform[] array = new CTInverseGammaTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInverseGammaTransform getInvGammaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInverseGammaTransform ctInverseGammaTransform = (CTInverseGammaTransform)this.get_store().find_element_user(CTScRgbColorImpl.INVGAMMA$54, n);
            if (ctInverseGammaTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInverseGammaTransform;
        }
    }
    
    public int sizeOfInvGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScRgbColorImpl.INVGAMMA$54);
        }
    }
    
    public void setInvGammaArray(final CTInverseGammaTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScRgbColorImpl.INVGAMMA$54);
    }
    
    public void setInvGammaArray(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctInverseGammaTransform, CTScRgbColorImpl.INVGAMMA$54, n, (short)2);
    }
    
    public CTInverseGammaTransform insertNewInvGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseGammaTransform)this.get_store().insert_element_user(CTScRgbColorImpl.INVGAMMA$54, n);
        }
    }
    
    public CTInverseGammaTransform addNewInvGamma() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseGammaTransform)this.get_store().add_element_user(CTScRgbColorImpl.INVGAMMA$54);
        }
    }
    
    public void removeInvGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScRgbColorImpl.INVGAMMA$54, n);
        }
    }
    
    public int getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.R$56);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.R$56);
        }
    }
    
    public void setR(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.R$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTScRgbColorImpl.R$56);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetR(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.R$56);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTScRgbColorImpl.R$56);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public int getG() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.G$58);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetG() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.G$58);
        }
    }
    
    public void setG(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.G$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTScRgbColorImpl.G$58);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetG(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.G$58);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTScRgbColorImpl.G$58);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public int getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.B$60);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.B$60);
        }
    }
    
    public void setB(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTScRgbColorImpl.B$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTScRgbColorImpl.B$60);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetB(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTScRgbColorImpl.B$60);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTScRgbColorImpl.B$60);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    static {
        TINT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tint");
        SHADE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "shade");
        COMP$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "comp");
        INV$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "inv");
        GRAY$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gray");
        ALPHA$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alpha");
        ALPHAOFF$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaOff");
        ALPHAMOD$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaMod");
        HUE$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hue");
        HUEOFF$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hueOff");
        HUEMOD$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hueMod");
        SAT$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "sat");
        SATOFF$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "satOff");
        SATMOD$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "satMod");
        LUM$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lum");
        LUMOFF$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lumOff");
        LUMMOD$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lumMod");
        RED$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "red");
        REDOFF$36 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "redOff");
        REDMOD$38 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "redMod");
        GREEN$40 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "green");
        GREENOFF$42 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "greenOff");
        GREENMOD$44 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "greenMod");
        BLUE$46 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blue");
        BLUEOFF$48 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blueOff");
        BLUEMOD$50 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blueMod");
        GAMMA$52 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "gamma");
        INVGAMMA$54 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "invGamma");
        R$56 = new QName("", "r");
        G$58 = new QName("", "g");
        B$60 = new QName("", "b");
    }
}
