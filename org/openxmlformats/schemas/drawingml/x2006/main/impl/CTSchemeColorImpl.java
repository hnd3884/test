package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STSchemeColorVal;
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
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSchemeColorImpl extends XmlComplexContentImpl implements CTSchemeColor
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
    private static final QName VAL$56;
    
    public CTSchemeColorImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPositiveFixedPercentage> getTintList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TintList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getTintArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage tintArray = CTSchemeColorImpl.this.getTintArray(n);
                    CTSchemeColorImpl.this.setTintArray(n, ctPositiveFixedPercentage);
                    return tintArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTSchemeColorImpl.this.insertNewTint(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage tintArray = CTSchemeColorImpl.this.getTintArray(n);
                    CTSchemeColorImpl.this.removeTint(n);
                    return tintArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfTintArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.TINT$0, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getTintArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTSchemeColorImpl.TINT$0, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.TINT$0);
        }
    }
    
    public void setTintArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.TINT$0);
    }
    
    public void setTintArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTSchemeColorImpl.TINT$0, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.TINT$0, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTSchemeColorImpl.TINT$0);
        }
    }
    
    public void removeTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.TINT$0, n);
        }
    }
    
    public List<CTPositiveFixedPercentage> getShadeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadeList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getShadeArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage shadeArray = CTSchemeColorImpl.this.getShadeArray(n);
                    CTSchemeColorImpl.this.setShadeArray(n, ctPositiveFixedPercentage);
                    return shadeArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTSchemeColorImpl.this.insertNewShade(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage shadeArray = CTSchemeColorImpl.this.getShadeArray(n);
                    CTSchemeColorImpl.this.removeShade(n);
                    return shadeArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfShadeArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.SHADE$2, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getShadeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTSchemeColorImpl.SHADE$2, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfShadeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.SHADE$2);
        }
    }
    
    public void setShadeArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.SHADE$2);
    }
    
    public void setShadeArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTSchemeColorImpl.SHADE$2, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewShade(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.SHADE$2, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewShade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTSchemeColorImpl.SHADE$2);
        }
    }
    
    public void removeShade(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.SHADE$2, n);
        }
    }
    
    public List<CTComplementTransform> getCompList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CompList extends AbstractList<CTComplementTransform>
            {
                @Override
                public CTComplementTransform get(final int n) {
                    return CTSchemeColorImpl.this.getCompArray(n);
                }
                
                @Override
                public CTComplementTransform set(final int n, final CTComplementTransform ctComplementTransform) {
                    final CTComplementTransform compArray = CTSchemeColorImpl.this.getCompArray(n);
                    CTSchemeColorImpl.this.setCompArray(n, ctComplementTransform);
                    return compArray;
                }
                
                @Override
                public void add(final int n, final CTComplementTransform ctComplementTransform) {
                    CTSchemeColorImpl.this.insertNewComp(n).set((XmlObject)ctComplementTransform);
                }
                
                @Override
                public CTComplementTransform remove(final int n) {
                    final CTComplementTransform compArray = CTSchemeColorImpl.this.getCompArray(n);
                    CTSchemeColorImpl.this.removeComp(n);
                    return compArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfCompArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.COMP$4, (List)list);
            final CTComplementTransform[] array = new CTComplementTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTComplementTransform getCompArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComplementTransform ctComplementTransform = (CTComplementTransform)this.get_store().find_element_user(CTSchemeColorImpl.COMP$4, n);
            if (ctComplementTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctComplementTransform;
        }
    }
    
    public int sizeOfCompArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.COMP$4);
        }
    }
    
    public void setCompArray(final CTComplementTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.COMP$4);
    }
    
    public void setCompArray(final int n, final CTComplementTransform ctComplementTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctComplementTransform, CTSchemeColorImpl.COMP$4, n, (short)2);
    }
    
    public CTComplementTransform insertNewComp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComplementTransform)this.get_store().insert_element_user(CTSchemeColorImpl.COMP$4, n);
        }
    }
    
    public CTComplementTransform addNewComp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComplementTransform)this.get_store().add_element_user(CTSchemeColorImpl.COMP$4);
        }
    }
    
    public void removeComp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.COMP$4, n);
        }
    }
    
    public List<CTInverseTransform> getInvList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InvList extends AbstractList<CTInverseTransform>
            {
                @Override
                public CTInverseTransform get(final int n) {
                    return CTSchemeColorImpl.this.getInvArray(n);
                }
                
                @Override
                public CTInverseTransform set(final int n, final CTInverseTransform ctInverseTransform) {
                    final CTInverseTransform invArray = CTSchemeColorImpl.this.getInvArray(n);
                    CTSchemeColorImpl.this.setInvArray(n, ctInverseTransform);
                    return invArray;
                }
                
                @Override
                public void add(final int n, final CTInverseTransform ctInverseTransform) {
                    CTSchemeColorImpl.this.insertNewInv(n).set((XmlObject)ctInverseTransform);
                }
                
                @Override
                public CTInverseTransform remove(final int n) {
                    final CTInverseTransform invArray = CTSchemeColorImpl.this.getInvArray(n);
                    CTSchemeColorImpl.this.removeInv(n);
                    return invArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfInvArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.INV$6, (List)list);
            final CTInverseTransform[] array = new CTInverseTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInverseTransform getInvArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInverseTransform ctInverseTransform = (CTInverseTransform)this.get_store().find_element_user(CTSchemeColorImpl.INV$6, n);
            if (ctInverseTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInverseTransform;
        }
    }
    
    public int sizeOfInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.INV$6);
        }
    }
    
    public void setInvArray(final CTInverseTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.INV$6);
    }
    
    public void setInvArray(final int n, final CTInverseTransform ctInverseTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctInverseTransform, CTSchemeColorImpl.INV$6, n, (short)2);
    }
    
    public CTInverseTransform insertNewInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseTransform)this.get_store().insert_element_user(CTSchemeColorImpl.INV$6, n);
        }
    }
    
    public CTInverseTransform addNewInv() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseTransform)this.get_store().add_element_user(CTSchemeColorImpl.INV$6);
        }
    }
    
    public void removeInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.INV$6, n);
        }
    }
    
    public List<CTGrayscaleTransform> getGrayList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GrayList extends AbstractList<CTGrayscaleTransform>
            {
                @Override
                public CTGrayscaleTransform get(final int n) {
                    return CTSchemeColorImpl.this.getGrayArray(n);
                }
                
                @Override
                public CTGrayscaleTransform set(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
                    final CTGrayscaleTransform grayArray = CTSchemeColorImpl.this.getGrayArray(n);
                    CTSchemeColorImpl.this.setGrayArray(n, ctGrayscaleTransform);
                    return grayArray;
                }
                
                @Override
                public void add(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
                    CTSchemeColorImpl.this.insertNewGray(n).set((XmlObject)ctGrayscaleTransform);
                }
                
                @Override
                public CTGrayscaleTransform remove(final int n) {
                    final CTGrayscaleTransform grayArray = CTSchemeColorImpl.this.getGrayArray(n);
                    CTSchemeColorImpl.this.removeGray(n);
                    return grayArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfGrayArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.GRAY$8, (List)list);
            final CTGrayscaleTransform[] array = new CTGrayscaleTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGrayscaleTransform getGrayArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrayscaleTransform ctGrayscaleTransform = (CTGrayscaleTransform)this.get_store().find_element_user(CTSchemeColorImpl.GRAY$8, n);
            if (ctGrayscaleTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGrayscaleTransform;
        }
    }
    
    public int sizeOfGrayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.GRAY$8);
        }
    }
    
    public void setGrayArray(final CTGrayscaleTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.GRAY$8);
    }
    
    public void setGrayArray(final int n, final CTGrayscaleTransform ctGrayscaleTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctGrayscaleTransform, CTSchemeColorImpl.GRAY$8, n, (short)2);
    }
    
    public CTGrayscaleTransform insertNewGray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleTransform)this.get_store().insert_element_user(CTSchemeColorImpl.GRAY$8, n);
        }
    }
    
    public CTGrayscaleTransform addNewGray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleTransform)this.get_store().add_element_user(CTSchemeColorImpl.GRAY$8);
        }
    }
    
    public void removeGray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.GRAY$8, n);
        }
    }
    
    public List<CTPositiveFixedPercentage> getAlphaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaList extends AbstractList<CTPositiveFixedPercentage>
            {
                @Override
                public CTPositiveFixedPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getAlphaArray(n);
                }
                
                @Override
                public CTPositiveFixedPercentage set(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    final CTPositiveFixedPercentage alphaArray = CTSchemeColorImpl.this.getAlphaArray(n);
                    CTSchemeColorImpl.this.setAlphaArray(n, ctPositiveFixedPercentage);
                    return alphaArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
                    CTSchemeColorImpl.this.insertNewAlpha(n).set((XmlObject)ctPositiveFixedPercentage);
                }
                
                @Override
                public CTPositiveFixedPercentage remove(final int n) {
                    final CTPositiveFixedPercentage alphaArray = CTSchemeColorImpl.this.getAlphaArray(n);
                    CTSchemeColorImpl.this.removeAlpha(n);
                    return alphaArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfAlphaArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.ALPHA$10, (List)list);
            final CTPositiveFixedPercentage[] array = new CTPositiveFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedPercentage getAlphaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedPercentage ctPositiveFixedPercentage = (CTPositiveFixedPercentage)this.get_store().find_element_user(CTSchemeColorImpl.ALPHA$10, n);
            if (ctPositiveFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedPercentage;
        }
    }
    
    public int sizeOfAlphaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.ALPHA$10);
        }
    }
    
    public void setAlphaArray(final CTPositiveFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.ALPHA$10);
    }
    
    public void setAlphaArray(final int n, final CTPositiveFixedPercentage ctPositiveFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedPercentage, CTSchemeColorImpl.ALPHA$10, n, (short)2);
    }
    
    public CTPositiveFixedPercentage insertNewAlpha(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.ALPHA$10, n);
        }
    }
    
    public CTPositiveFixedPercentage addNewAlpha() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedPercentage)this.get_store().add_element_user(CTSchemeColorImpl.ALPHA$10);
        }
    }
    
    public void removeAlpha(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.ALPHA$10, n);
        }
    }
    
    public List<CTFixedPercentage> getAlphaOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaOffList extends AbstractList<CTFixedPercentage>
            {
                @Override
                public CTFixedPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getAlphaOffArray(n);
                }
                
                @Override
                public CTFixedPercentage set(final int n, final CTFixedPercentage ctFixedPercentage) {
                    final CTFixedPercentage alphaOffArray = CTSchemeColorImpl.this.getAlphaOffArray(n);
                    CTSchemeColorImpl.this.setAlphaOffArray(n, ctFixedPercentage);
                    return alphaOffArray;
                }
                
                @Override
                public void add(final int n, final CTFixedPercentage ctFixedPercentage) {
                    CTSchemeColorImpl.this.insertNewAlphaOff(n).set((XmlObject)ctFixedPercentage);
                }
                
                @Override
                public CTFixedPercentage remove(final int n) {
                    final CTFixedPercentage alphaOffArray = CTSchemeColorImpl.this.getAlphaOffArray(n);
                    CTSchemeColorImpl.this.removeAlphaOff(n);
                    return alphaOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfAlphaOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.ALPHAOFF$12, (List)list);
            final CTFixedPercentage[] array = new CTFixedPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFixedPercentage getAlphaOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFixedPercentage ctFixedPercentage = (CTFixedPercentage)this.get_store().find_element_user(CTSchemeColorImpl.ALPHAOFF$12, n);
            if (ctFixedPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFixedPercentage;
        }
    }
    
    public int sizeOfAlphaOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.ALPHAOFF$12);
        }
    }
    
    public void setAlphaOffArray(final CTFixedPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.ALPHAOFF$12);
    }
    
    public void setAlphaOffArray(final int n, final CTFixedPercentage ctFixedPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctFixedPercentage, CTSchemeColorImpl.ALPHAOFF$12, n, (short)2);
    }
    
    public CTFixedPercentage insertNewAlphaOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFixedPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.ALPHAOFF$12, n);
        }
    }
    
    public CTFixedPercentage addNewAlphaOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFixedPercentage)this.get_store().add_element_user(CTSchemeColorImpl.ALPHAOFF$12);
        }
    }
    
    public void removeAlphaOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.ALPHAOFF$12, n);
        }
    }
    
    public List<CTPositivePercentage> getAlphaModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModList extends AbstractList<CTPositivePercentage>
            {
                @Override
                public CTPositivePercentage get(final int n) {
                    return CTSchemeColorImpl.this.getAlphaModArray(n);
                }
                
                @Override
                public CTPositivePercentage set(final int n, final CTPositivePercentage ctPositivePercentage) {
                    final CTPositivePercentage alphaModArray = CTSchemeColorImpl.this.getAlphaModArray(n);
                    CTSchemeColorImpl.this.setAlphaModArray(n, ctPositivePercentage);
                    return alphaModArray;
                }
                
                @Override
                public void add(final int n, final CTPositivePercentage ctPositivePercentage) {
                    CTSchemeColorImpl.this.insertNewAlphaMod(n).set((XmlObject)ctPositivePercentage);
                }
                
                @Override
                public CTPositivePercentage remove(final int n) {
                    final CTPositivePercentage alphaModArray = CTSchemeColorImpl.this.getAlphaModArray(n);
                    CTSchemeColorImpl.this.removeAlphaMod(n);
                    return alphaModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfAlphaModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.ALPHAMOD$14, (List)list);
            final CTPositivePercentage[] array = new CTPositivePercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositivePercentage getAlphaModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositivePercentage ctPositivePercentage = (CTPositivePercentage)this.get_store().find_element_user(CTSchemeColorImpl.ALPHAMOD$14, n);
            if (ctPositivePercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositivePercentage;
        }
    }
    
    public int sizeOfAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.ALPHAMOD$14);
        }
    }
    
    public void setAlphaModArray(final CTPositivePercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.ALPHAMOD$14);
    }
    
    public void setAlphaModArray(final int n, final CTPositivePercentage ctPositivePercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositivePercentage, CTSchemeColorImpl.ALPHAMOD$14, n, (short)2);
    }
    
    public CTPositivePercentage insertNewAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().insert_element_user(CTSchemeColorImpl.ALPHAMOD$14, n);
        }
    }
    
    public CTPositivePercentage addNewAlphaMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().add_element_user(CTSchemeColorImpl.ALPHAMOD$14);
        }
    }
    
    public void removeAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.ALPHAMOD$14, n);
        }
    }
    
    public List<CTPositiveFixedAngle> getHueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueList extends AbstractList<CTPositiveFixedAngle>
            {
                @Override
                public CTPositiveFixedAngle get(final int n) {
                    return CTSchemeColorImpl.this.getHueArray(n);
                }
                
                @Override
                public CTPositiveFixedAngle set(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
                    final CTPositiveFixedAngle hueArray = CTSchemeColorImpl.this.getHueArray(n);
                    CTSchemeColorImpl.this.setHueArray(n, ctPositiveFixedAngle);
                    return hueArray;
                }
                
                @Override
                public void add(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
                    CTSchemeColorImpl.this.insertNewHue(n).set((XmlObject)ctPositiveFixedAngle);
                }
                
                @Override
                public CTPositiveFixedAngle remove(final int n) {
                    final CTPositiveFixedAngle hueArray = CTSchemeColorImpl.this.getHueArray(n);
                    CTSchemeColorImpl.this.removeHue(n);
                    return hueArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfHueArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.HUE$16, (List)list);
            final CTPositiveFixedAngle[] array = new CTPositiveFixedAngle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositiveFixedAngle getHueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositiveFixedAngle ctPositiveFixedAngle = (CTPositiveFixedAngle)this.get_store().find_element_user(CTSchemeColorImpl.HUE$16, n);
            if (ctPositiveFixedAngle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositiveFixedAngle;
        }
    }
    
    public int sizeOfHueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.HUE$16);
        }
    }
    
    public void setHueArray(final CTPositiveFixedAngle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.HUE$16);
    }
    
    public void setHueArray(final int n, final CTPositiveFixedAngle ctPositiveFixedAngle) {
        this.generatedSetterHelperImpl((XmlObject)ctPositiveFixedAngle, CTSchemeColorImpl.HUE$16, n, (short)2);
    }
    
    public CTPositiveFixedAngle insertNewHue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedAngle)this.get_store().insert_element_user(CTSchemeColorImpl.HUE$16, n);
        }
    }
    
    public CTPositiveFixedAngle addNewHue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositiveFixedAngle)this.get_store().add_element_user(CTSchemeColorImpl.HUE$16);
        }
    }
    
    public void removeHue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.HUE$16, n);
        }
    }
    
    public List<CTAngle> getHueOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueOffList extends AbstractList<CTAngle>
            {
                @Override
                public CTAngle get(final int n) {
                    return CTSchemeColorImpl.this.getHueOffArray(n);
                }
                
                @Override
                public CTAngle set(final int n, final CTAngle ctAngle) {
                    final CTAngle hueOffArray = CTSchemeColorImpl.this.getHueOffArray(n);
                    CTSchemeColorImpl.this.setHueOffArray(n, ctAngle);
                    return hueOffArray;
                }
                
                @Override
                public void add(final int n, final CTAngle ctAngle) {
                    CTSchemeColorImpl.this.insertNewHueOff(n).set((XmlObject)ctAngle);
                }
                
                @Override
                public CTAngle remove(final int n) {
                    final CTAngle hueOffArray = CTSchemeColorImpl.this.getHueOffArray(n);
                    CTSchemeColorImpl.this.removeHueOff(n);
                    return hueOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfHueOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.HUEOFF$18, (List)list);
            final CTAngle[] array = new CTAngle[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAngle getHueOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAngle ctAngle = (CTAngle)this.get_store().find_element_user(CTSchemeColorImpl.HUEOFF$18, n);
            if (ctAngle == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAngle;
        }
    }
    
    public int sizeOfHueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.HUEOFF$18);
        }
    }
    
    public void setHueOffArray(final CTAngle[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.HUEOFF$18);
    }
    
    public void setHueOffArray(final int n, final CTAngle ctAngle) {
        this.generatedSetterHelperImpl((XmlObject)ctAngle, CTSchemeColorImpl.HUEOFF$18, n, (short)2);
    }
    
    public CTAngle insertNewHueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAngle)this.get_store().insert_element_user(CTSchemeColorImpl.HUEOFF$18, n);
        }
    }
    
    public CTAngle addNewHueOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAngle)this.get_store().add_element_user(CTSchemeColorImpl.HUEOFF$18);
        }
    }
    
    public void removeHueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.HUEOFF$18, n);
        }
    }
    
    public List<CTPositivePercentage> getHueModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HueModList extends AbstractList<CTPositivePercentage>
            {
                @Override
                public CTPositivePercentage get(final int n) {
                    return CTSchemeColorImpl.this.getHueModArray(n);
                }
                
                @Override
                public CTPositivePercentage set(final int n, final CTPositivePercentage ctPositivePercentage) {
                    final CTPositivePercentage hueModArray = CTSchemeColorImpl.this.getHueModArray(n);
                    CTSchemeColorImpl.this.setHueModArray(n, ctPositivePercentage);
                    return hueModArray;
                }
                
                @Override
                public void add(final int n, final CTPositivePercentage ctPositivePercentage) {
                    CTSchemeColorImpl.this.insertNewHueMod(n).set((XmlObject)ctPositivePercentage);
                }
                
                @Override
                public CTPositivePercentage remove(final int n) {
                    final CTPositivePercentage hueModArray = CTSchemeColorImpl.this.getHueModArray(n);
                    CTSchemeColorImpl.this.removeHueMod(n);
                    return hueModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfHueModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.HUEMOD$20, (List)list);
            final CTPositivePercentage[] array = new CTPositivePercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPositivePercentage getHueModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPositivePercentage ctPositivePercentage = (CTPositivePercentage)this.get_store().find_element_user(CTSchemeColorImpl.HUEMOD$20, n);
            if (ctPositivePercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPositivePercentage;
        }
    }
    
    public int sizeOfHueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.HUEMOD$20);
        }
    }
    
    public void setHueModArray(final CTPositivePercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.HUEMOD$20);
    }
    
    public void setHueModArray(final int n, final CTPositivePercentage ctPositivePercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPositivePercentage, CTSchemeColorImpl.HUEMOD$20, n, (short)2);
    }
    
    public CTPositivePercentage insertNewHueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().insert_element_user(CTSchemeColorImpl.HUEMOD$20, n);
        }
    }
    
    public CTPositivePercentage addNewHueMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPositivePercentage)this.get_store().add_element_user(CTSchemeColorImpl.HUEMOD$20);
        }
    }
    
    public void removeHueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.HUEMOD$20, n);
        }
    }
    
    public List<CTPercentage> getSatList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getSatArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satArray = CTSchemeColorImpl.this.getSatArray(n);
                    CTSchemeColorImpl.this.setSatArray(n, ctPercentage);
                    return satArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewSat(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satArray = CTSchemeColorImpl.this.getSatArray(n);
                    CTSchemeColorImpl.this.removeSat(n);
                    return satArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfSatArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.SAT$22, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.SAT$22, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.SAT$22);
        }
    }
    
    public void setSatArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.SAT$22);
    }
    
    public void setSatArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.SAT$22, n, (short)2);
    }
    
    public CTPercentage insertNewSat(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.SAT$22, n);
        }
    }
    
    public CTPercentage addNewSat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.SAT$22);
        }
    }
    
    public void removeSat(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.SAT$22, n);
        }
    }
    
    public List<CTPercentage> getSatOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getSatOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satOffArray = CTSchemeColorImpl.this.getSatOffArray(n);
                    CTSchemeColorImpl.this.setSatOffArray(n, ctPercentage);
                    return satOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewSatOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satOffArray = CTSchemeColorImpl.this.getSatOffArray(n);
                    CTSchemeColorImpl.this.removeSatOff(n);
                    return satOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfSatOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.SATOFF$24, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.SATOFF$24, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.SATOFF$24);
        }
    }
    
    public void setSatOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.SATOFF$24);
    }
    
    public void setSatOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.SATOFF$24, n, (short)2);
    }
    
    public CTPercentage insertNewSatOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.SATOFF$24, n);
        }
    }
    
    public CTPercentage addNewSatOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.SATOFF$24);
        }
    }
    
    public void removeSatOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.SATOFF$24, n);
        }
    }
    
    public List<CTPercentage> getSatModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SatModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getSatModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage satModArray = CTSchemeColorImpl.this.getSatModArray(n);
                    CTSchemeColorImpl.this.setSatModArray(n, ctPercentage);
                    return satModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewSatMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage satModArray = CTSchemeColorImpl.this.getSatModArray(n);
                    CTSchemeColorImpl.this.removeSatMod(n);
                    return satModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfSatModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.SATMOD$26, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getSatModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.SATMOD$26, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfSatModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.SATMOD$26);
        }
    }
    
    public void setSatModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.SATMOD$26);
    }
    
    public void setSatModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.SATMOD$26, n, (short)2);
    }
    
    public CTPercentage insertNewSatMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.SATMOD$26, n);
        }
    }
    
    public CTPercentage addNewSatMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.SATMOD$26);
        }
    }
    
    public void removeSatMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.SATMOD$26, n);
        }
    }
    
    public List<CTPercentage> getLumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getLumArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumArray = CTSchemeColorImpl.this.getLumArray(n);
                    CTSchemeColorImpl.this.setLumArray(n, ctPercentage);
                    return lumArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewLum(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumArray = CTSchemeColorImpl.this.getLumArray(n);
                    CTSchemeColorImpl.this.removeLum(n);
                    return lumArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfLumArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.LUM$28, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.LUM$28, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.LUM$28);
        }
    }
    
    public void setLumArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.LUM$28);
    }
    
    public void setLumArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.LUM$28, n, (short)2);
    }
    
    public CTPercentage insertNewLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.LUM$28, n);
        }
    }
    
    public CTPercentage addNewLum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.LUM$28);
        }
    }
    
    public void removeLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.LUM$28, n);
        }
    }
    
    public List<CTPercentage> getLumOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getLumOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumOffArray = CTSchemeColorImpl.this.getLumOffArray(n);
                    CTSchemeColorImpl.this.setLumOffArray(n, ctPercentage);
                    return lumOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewLumOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumOffArray = CTSchemeColorImpl.this.getLumOffArray(n);
                    CTSchemeColorImpl.this.removeLumOff(n);
                    return lumOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfLumOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.LUMOFF$30, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.LUMOFF$30, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.LUMOFF$30);
        }
    }
    
    public void setLumOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.LUMOFF$30);
    }
    
    public void setLumOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.LUMOFF$30, n, (short)2);
    }
    
    public CTPercentage insertNewLumOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.LUMOFF$30, n);
        }
    }
    
    public CTPercentage addNewLumOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.LUMOFF$30);
        }
    }
    
    public void removeLumOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.LUMOFF$30, n);
        }
    }
    
    public List<CTPercentage> getLumModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getLumModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage lumModArray = CTSchemeColorImpl.this.getLumModArray(n);
                    CTSchemeColorImpl.this.setLumModArray(n, ctPercentage);
                    return lumModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewLumMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage lumModArray = CTSchemeColorImpl.this.getLumModArray(n);
                    CTSchemeColorImpl.this.removeLumMod(n);
                    return lumModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfLumModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.LUMMOD$32, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getLumModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.LUMMOD$32, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfLumModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.LUMMOD$32);
        }
    }
    
    public void setLumModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.LUMMOD$32);
    }
    
    public void setLumModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.LUMMOD$32, n, (short)2);
    }
    
    public CTPercentage insertNewLumMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.LUMMOD$32, n);
        }
    }
    
    public CTPercentage addNewLumMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.LUMMOD$32);
        }
    }
    
    public void removeLumMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.LUMMOD$32, n);
        }
    }
    
    public List<CTPercentage> getRedList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getRedArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redArray = CTSchemeColorImpl.this.getRedArray(n);
                    CTSchemeColorImpl.this.setRedArray(n, ctPercentage);
                    return redArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewRed(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redArray = CTSchemeColorImpl.this.getRedArray(n);
                    CTSchemeColorImpl.this.removeRed(n);
                    return redArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfRedArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.RED$34, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.RED$34, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.RED$34);
        }
    }
    
    public void setRedArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.RED$34);
    }
    
    public void setRedArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.RED$34, n, (short)2);
    }
    
    public CTPercentage insertNewRed(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.RED$34, n);
        }
    }
    
    public CTPercentage addNewRed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.RED$34);
        }
    }
    
    public void removeRed(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.RED$34, n);
        }
    }
    
    public List<CTPercentage> getRedOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getRedOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redOffArray = CTSchemeColorImpl.this.getRedOffArray(n);
                    CTSchemeColorImpl.this.setRedOffArray(n, ctPercentage);
                    return redOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewRedOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redOffArray = CTSchemeColorImpl.this.getRedOffArray(n);
                    CTSchemeColorImpl.this.removeRedOff(n);
                    return redOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfRedOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.REDOFF$36, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.REDOFF$36, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.REDOFF$36);
        }
    }
    
    public void setRedOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.REDOFF$36);
    }
    
    public void setRedOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.REDOFF$36, n, (short)2);
    }
    
    public CTPercentage insertNewRedOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.REDOFF$36, n);
        }
    }
    
    public CTPercentage addNewRedOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.REDOFF$36);
        }
    }
    
    public void removeRedOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.REDOFF$36, n);
        }
    }
    
    public List<CTPercentage> getRedModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RedModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getRedModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage redModArray = CTSchemeColorImpl.this.getRedModArray(n);
                    CTSchemeColorImpl.this.setRedModArray(n, ctPercentage);
                    return redModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewRedMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage redModArray = CTSchemeColorImpl.this.getRedModArray(n);
                    CTSchemeColorImpl.this.removeRedMod(n);
                    return redModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfRedModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.REDMOD$38, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getRedModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.REDMOD$38, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfRedModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.REDMOD$38);
        }
    }
    
    public void setRedModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.REDMOD$38);
    }
    
    public void setRedModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.REDMOD$38, n, (short)2);
    }
    
    public CTPercentage insertNewRedMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.REDMOD$38, n);
        }
    }
    
    public CTPercentage addNewRedMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.REDMOD$38);
        }
    }
    
    public void removeRedMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.REDMOD$38, n);
        }
    }
    
    public List<CTPercentage> getGreenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getGreenArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenArray = CTSchemeColorImpl.this.getGreenArray(n);
                    CTSchemeColorImpl.this.setGreenArray(n, ctPercentage);
                    return greenArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewGreen(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenArray = CTSchemeColorImpl.this.getGreenArray(n);
                    CTSchemeColorImpl.this.removeGreen(n);
                    return greenArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfGreenArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.GREEN$40, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.GREEN$40, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.GREEN$40);
        }
    }
    
    public void setGreenArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.GREEN$40);
    }
    
    public void setGreenArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.GREEN$40, n, (short)2);
    }
    
    public CTPercentage insertNewGreen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.GREEN$40, n);
        }
    }
    
    public CTPercentage addNewGreen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.GREEN$40);
        }
    }
    
    public void removeGreen(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.GREEN$40, n);
        }
    }
    
    public List<CTPercentage> getGreenOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getGreenOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenOffArray = CTSchemeColorImpl.this.getGreenOffArray(n);
                    CTSchemeColorImpl.this.setGreenOffArray(n, ctPercentage);
                    return greenOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewGreenOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenOffArray = CTSchemeColorImpl.this.getGreenOffArray(n);
                    CTSchemeColorImpl.this.removeGreenOff(n);
                    return greenOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfGreenOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.GREENOFF$42, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.GREENOFF$42, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.GREENOFF$42);
        }
    }
    
    public void setGreenOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.GREENOFF$42);
    }
    
    public void setGreenOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.GREENOFF$42, n, (short)2);
    }
    
    public CTPercentage insertNewGreenOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.GREENOFF$42, n);
        }
    }
    
    public CTPercentage addNewGreenOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.GREENOFF$42);
        }
    }
    
    public void removeGreenOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.GREENOFF$42, n);
        }
    }
    
    public List<CTPercentage> getGreenModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GreenModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getGreenModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage greenModArray = CTSchemeColorImpl.this.getGreenModArray(n);
                    CTSchemeColorImpl.this.setGreenModArray(n, ctPercentage);
                    return greenModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewGreenMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage greenModArray = CTSchemeColorImpl.this.getGreenModArray(n);
                    CTSchemeColorImpl.this.removeGreenMod(n);
                    return greenModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfGreenModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.GREENMOD$44, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getGreenModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.GREENMOD$44, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfGreenModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.GREENMOD$44);
        }
    }
    
    public void setGreenModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.GREENMOD$44);
    }
    
    public void setGreenModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.GREENMOD$44, n, (short)2);
    }
    
    public CTPercentage insertNewGreenMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.GREENMOD$44, n);
        }
    }
    
    public CTPercentage addNewGreenMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.GREENMOD$44);
        }
    }
    
    public void removeGreenMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.GREENMOD$44, n);
        }
    }
    
    public List<CTPercentage> getBlueList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getBlueArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueArray = CTSchemeColorImpl.this.getBlueArray(n);
                    CTSchemeColorImpl.this.setBlueArray(n, ctPercentage);
                    return blueArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewBlue(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueArray = CTSchemeColorImpl.this.getBlueArray(n);
                    CTSchemeColorImpl.this.removeBlue(n);
                    return blueArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfBlueArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.BLUE$46, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.BLUE$46, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.BLUE$46);
        }
    }
    
    public void setBlueArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.BLUE$46);
    }
    
    public void setBlueArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.BLUE$46, n, (short)2);
    }
    
    public CTPercentage insertNewBlue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.BLUE$46, n);
        }
    }
    
    public CTPercentage addNewBlue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.BLUE$46);
        }
    }
    
    public void removeBlue(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.BLUE$46, n);
        }
    }
    
    public List<CTPercentage> getBlueOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueOffList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getBlueOffArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueOffArray = CTSchemeColorImpl.this.getBlueOffArray(n);
                    CTSchemeColorImpl.this.setBlueOffArray(n, ctPercentage);
                    return blueOffArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewBlueOff(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueOffArray = CTSchemeColorImpl.this.getBlueOffArray(n);
                    CTSchemeColorImpl.this.removeBlueOff(n);
                    return blueOffArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfBlueOffArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.BLUEOFF$48, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.BLUEOFF$48, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.BLUEOFF$48);
        }
    }
    
    public void setBlueOffArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.BLUEOFF$48);
    }
    
    public void setBlueOffArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.BLUEOFF$48, n, (short)2);
    }
    
    public CTPercentage insertNewBlueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.BLUEOFF$48, n);
        }
    }
    
    public CTPercentage addNewBlueOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.BLUEOFF$48);
        }
    }
    
    public void removeBlueOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.BLUEOFF$48, n);
        }
    }
    
    public List<CTPercentage> getBlueModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlueModList extends AbstractList<CTPercentage>
            {
                @Override
                public CTPercentage get(final int n) {
                    return CTSchemeColorImpl.this.getBlueModArray(n);
                }
                
                @Override
                public CTPercentage set(final int n, final CTPercentage ctPercentage) {
                    final CTPercentage blueModArray = CTSchemeColorImpl.this.getBlueModArray(n);
                    CTSchemeColorImpl.this.setBlueModArray(n, ctPercentage);
                    return blueModArray;
                }
                
                @Override
                public void add(final int n, final CTPercentage ctPercentage) {
                    CTSchemeColorImpl.this.insertNewBlueMod(n).set((XmlObject)ctPercentage);
                }
                
                @Override
                public CTPercentage remove(final int n) {
                    final CTPercentage blueModArray = CTSchemeColorImpl.this.getBlueModArray(n);
                    CTSchemeColorImpl.this.removeBlueMod(n);
                    return blueModArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfBlueModArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.BLUEMOD$50, (List)list);
            final CTPercentage[] array = new CTPercentage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPercentage getBlueModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPercentage ctPercentage = (CTPercentage)this.get_store().find_element_user(CTSchemeColorImpl.BLUEMOD$50, n);
            if (ctPercentage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPercentage;
        }
    }
    
    public int sizeOfBlueModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.BLUEMOD$50);
        }
    }
    
    public void setBlueModArray(final CTPercentage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.BLUEMOD$50);
    }
    
    public void setBlueModArray(final int n, final CTPercentage ctPercentage) {
        this.generatedSetterHelperImpl((XmlObject)ctPercentage, CTSchemeColorImpl.BLUEMOD$50, n, (short)2);
    }
    
    public CTPercentage insertNewBlueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().insert_element_user(CTSchemeColorImpl.BLUEMOD$50, n);
        }
    }
    
    public CTPercentage addNewBlueMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPercentage)this.get_store().add_element_user(CTSchemeColorImpl.BLUEMOD$50);
        }
    }
    
    public void removeBlueMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.BLUEMOD$50, n);
        }
    }
    
    public List<CTGammaTransform> getGammaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GammaList extends AbstractList<CTGammaTransform>
            {
                @Override
                public CTGammaTransform get(final int n) {
                    return CTSchemeColorImpl.this.getGammaArray(n);
                }
                
                @Override
                public CTGammaTransform set(final int n, final CTGammaTransform ctGammaTransform) {
                    final CTGammaTransform gammaArray = CTSchemeColorImpl.this.getGammaArray(n);
                    CTSchemeColorImpl.this.setGammaArray(n, ctGammaTransform);
                    return gammaArray;
                }
                
                @Override
                public void add(final int n, final CTGammaTransform ctGammaTransform) {
                    CTSchemeColorImpl.this.insertNewGamma(n).set((XmlObject)ctGammaTransform);
                }
                
                @Override
                public CTGammaTransform remove(final int n) {
                    final CTGammaTransform gammaArray = CTSchemeColorImpl.this.getGammaArray(n);
                    CTSchemeColorImpl.this.removeGamma(n);
                    return gammaArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfGammaArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.GAMMA$52, (List)list);
            final CTGammaTransform[] array = new CTGammaTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGammaTransform getGammaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGammaTransform ctGammaTransform = (CTGammaTransform)this.get_store().find_element_user(CTSchemeColorImpl.GAMMA$52, n);
            if (ctGammaTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGammaTransform;
        }
    }
    
    public int sizeOfGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.GAMMA$52);
        }
    }
    
    public void setGammaArray(final CTGammaTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.GAMMA$52);
    }
    
    public void setGammaArray(final int n, final CTGammaTransform ctGammaTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctGammaTransform, CTSchemeColorImpl.GAMMA$52, n, (short)2);
    }
    
    public CTGammaTransform insertNewGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGammaTransform)this.get_store().insert_element_user(CTSchemeColorImpl.GAMMA$52, n);
        }
    }
    
    public CTGammaTransform addNewGamma() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGammaTransform)this.get_store().add_element_user(CTSchemeColorImpl.GAMMA$52);
        }
    }
    
    public void removeGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.GAMMA$52, n);
        }
    }
    
    public List<CTInverseGammaTransform> getInvGammaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InvGammaList extends AbstractList<CTInverseGammaTransform>
            {
                @Override
                public CTInverseGammaTransform get(final int n) {
                    return CTSchemeColorImpl.this.getInvGammaArray(n);
                }
                
                @Override
                public CTInverseGammaTransform set(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
                    final CTInverseGammaTransform invGammaArray = CTSchemeColorImpl.this.getInvGammaArray(n);
                    CTSchemeColorImpl.this.setInvGammaArray(n, ctInverseGammaTransform);
                    return invGammaArray;
                }
                
                @Override
                public void add(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
                    CTSchemeColorImpl.this.insertNewInvGamma(n).set((XmlObject)ctInverseGammaTransform);
                }
                
                @Override
                public CTInverseGammaTransform remove(final int n) {
                    final CTInverseGammaTransform invGammaArray = CTSchemeColorImpl.this.getInvGammaArray(n);
                    CTSchemeColorImpl.this.removeInvGamma(n);
                    return invGammaArray;
                }
                
                @Override
                public int size() {
                    return CTSchemeColorImpl.this.sizeOfInvGammaArray();
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
            this.get_store().find_all_element_users(CTSchemeColorImpl.INVGAMMA$54, (List)list);
            final CTInverseGammaTransform[] array = new CTInverseGammaTransform[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInverseGammaTransform getInvGammaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInverseGammaTransform ctInverseGammaTransform = (CTInverseGammaTransform)this.get_store().find_element_user(CTSchemeColorImpl.INVGAMMA$54, n);
            if (ctInverseGammaTransform == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInverseGammaTransform;
        }
    }
    
    public int sizeOfInvGammaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSchemeColorImpl.INVGAMMA$54);
        }
    }
    
    public void setInvGammaArray(final CTInverseGammaTransform[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSchemeColorImpl.INVGAMMA$54);
    }
    
    public void setInvGammaArray(final int n, final CTInverseGammaTransform ctInverseGammaTransform) {
        this.generatedSetterHelperImpl((XmlObject)ctInverseGammaTransform, CTSchemeColorImpl.INVGAMMA$54, n, (short)2);
    }
    
    public CTInverseGammaTransform insertNewInvGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseGammaTransform)this.get_store().insert_element_user(CTSchemeColorImpl.INVGAMMA$54, n);
        }
    }
    
    public CTInverseGammaTransform addNewInvGamma() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInverseGammaTransform)this.get_store().add_element_user(CTSchemeColorImpl.INVGAMMA$54);
        }
    }
    
    public void removeInvGamma(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSchemeColorImpl.INVGAMMA$54, n);
        }
    }
    
    public STSchemeColorVal.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemeColorImpl.VAL$56);
            if (simpleValue == null) {
                return null;
            }
            return (STSchemeColorVal.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSchemeColorVal xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSchemeColorVal)this.get_store().find_attribute_user(CTSchemeColorImpl.VAL$56);
        }
    }
    
    public void setVal(final STSchemeColorVal.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSchemeColorImpl.VAL$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSchemeColorImpl.VAL$56);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STSchemeColorVal stSchemeColorVal) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSchemeColorVal stSchemeColorVal2 = (STSchemeColorVal)this.get_store().find_attribute_user(CTSchemeColorImpl.VAL$56);
            if (stSchemeColorVal2 == null) {
                stSchemeColorVal2 = (STSchemeColorVal)this.get_store().add_attribute_user(CTSchemeColorImpl.VAL$56);
            }
            stSchemeColorVal2.set((XmlObject)stSchemeColorVal);
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
        VAL$56 = new QName("", "val");
    }
}
