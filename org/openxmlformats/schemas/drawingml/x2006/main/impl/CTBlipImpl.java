package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlipCompression;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTintEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLuminanceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHSLEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorChangeEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBiLevelEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateFixedEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaInverseEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaFloorEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaCeilingEffect;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaBiLevelEffect;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBlipImpl extends XmlComplexContentImpl implements CTBlip
{
    private static final long serialVersionUID = 1L;
    private static final QName ALPHABILEVEL$0;
    private static final QName ALPHACEILING$2;
    private static final QName ALPHAFLOOR$4;
    private static final QName ALPHAINV$6;
    private static final QName ALPHAMOD$8;
    private static final QName ALPHAMODFIX$10;
    private static final QName ALPHAREPL$12;
    private static final QName BILEVEL$14;
    private static final QName BLUR$16;
    private static final QName CLRCHANGE$18;
    private static final QName CLRREPL$20;
    private static final QName DUOTONE$22;
    private static final QName FILLOVERLAY$24;
    private static final QName GRAYSCL$26;
    private static final QName HSL$28;
    private static final QName LUM$30;
    private static final QName TINT$32;
    private static final QName EXTLST$34;
    private static final QName EMBED$36;
    private static final QName LINK$38;
    private static final QName CSTATE$40;
    
    public CTBlipImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTAlphaBiLevelEffect> getAlphaBiLevelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaBiLevelList extends AbstractList<CTAlphaBiLevelEffect>
            {
                @Override
                public CTAlphaBiLevelEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaBiLevelArray(n);
                }
                
                @Override
                public CTAlphaBiLevelEffect set(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
                    final CTAlphaBiLevelEffect alphaBiLevelArray = CTBlipImpl.this.getAlphaBiLevelArray(n);
                    CTBlipImpl.this.setAlphaBiLevelArray(n, ctAlphaBiLevelEffect);
                    return alphaBiLevelArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
                    CTBlipImpl.this.insertNewAlphaBiLevel(n).set((XmlObject)ctAlphaBiLevelEffect);
                }
                
                @Override
                public CTAlphaBiLevelEffect remove(final int n) {
                    final CTAlphaBiLevelEffect alphaBiLevelArray = CTBlipImpl.this.getAlphaBiLevelArray(n);
                    CTBlipImpl.this.removeAlphaBiLevel(n);
                    return alphaBiLevelArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaBiLevelArray();
                }
            }
            return new AlphaBiLevelList();
        }
    }
    
    @Deprecated
    public CTAlphaBiLevelEffect[] getAlphaBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHABILEVEL$0, (List)list);
            final CTAlphaBiLevelEffect[] array = new CTAlphaBiLevelEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaBiLevelEffect getAlphaBiLevelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaBiLevelEffect ctAlphaBiLevelEffect = (CTAlphaBiLevelEffect)this.get_store().find_element_user(CTBlipImpl.ALPHABILEVEL$0, n);
            if (ctAlphaBiLevelEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaBiLevelEffect;
        }
    }
    
    public int sizeOfAlphaBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHABILEVEL$0);
        }
    }
    
    public void setAlphaBiLevelArray(final CTAlphaBiLevelEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHABILEVEL$0);
    }
    
    public void setAlphaBiLevelArray(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaBiLevelEffect, CTBlipImpl.ALPHABILEVEL$0, n, (short)2);
    }
    
    public CTAlphaBiLevelEffect insertNewAlphaBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaBiLevelEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHABILEVEL$0, n);
        }
    }
    
    public CTAlphaBiLevelEffect addNewAlphaBiLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaBiLevelEffect)this.get_store().add_element_user(CTBlipImpl.ALPHABILEVEL$0);
        }
    }
    
    public void removeAlphaBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHABILEVEL$0, n);
        }
    }
    
    public List<CTAlphaCeilingEffect> getAlphaCeilingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaCeilingList extends AbstractList<CTAlphaCeilingEffect>
            {
                @Override
                public CTAlphaCeilingEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaCeilingArray(n);
                }
                
                @Override
                public CTAlphaCeilingEffect set(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
                    final CTAlphaCeilingEffect alphaCeilingArray = CTBlipImpl.this.getAlphaCeilingArray(n);
                    CTBlipImpl.this.setAlphaCeilingArray(n, ctAlphaCeilingEffect);
                    return alphaCeilingArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
                    CTBlipImpl.this.insertNewAlphaCeiling(n).set((XmlObject)ctAlphaCeilingEffect);
                }
                
                @Override
                public CTAlphaCeilingEffect remove(final int n) {
                    final CTAlphaCeilingEffect alphaCeilingArray = CTBlipImpl.this.getAlphaCeilingArray(n);
                    CTBlipImpl.this.removeAlphaCeiling(n);
                    return alphaCeilingArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaCeilingArray();
                }
            }
            return new AlphaCeilingList();
        }
    }
    
    @Deprecated
    public CTAlphaCeilingEffect[] getAlphaCeilingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHACEILING$2, (List)list);
            final CTAlphaCeilingEffect[] array = new CTAlphaCeilingEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaCeilingEffect getAlphaCeilingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaCeilingEffect ctAlphaCeilingEffect = (CTAlphaCeilingEffect)this.get_store().find_element_user(CTBlipImpl.ALPHACEILING$2, n);
            if (ctAlphaCeilingEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaCeilingEffect;
        }
    }
    
    public int sizeOfAlphaCeilingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHACEILING$2);
        }
    }
    
    public void setAlphaCeilingArray(final CTAlphaCeilingEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHACEILING$2);
    }
    
    public void setAlphaCeilingArray(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaCeilingEffect, CTBlipImpl.ALPHACEILING$2, n, (short)2);
    }
    
    public CTAlphaCeilingEffect insertNewAlphaCeiling(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaCeilingEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHACEILING$2, n);
        }
    }
    
    public CTAlphaCeilingEffect addNewAlphaCeiling() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaCeilingEffect)this.get_store().add_element_user(CTBlipImpl.ALPHACEILING$2);
        }
    }
    
    public void removeAlphaCeiling(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHACEILING$2, n);
        }
    }
    
    public List<CTAlphaFloorEffect> getAlphaFloorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaFloorList extends AbstractList<CTAlphaFloorEffect>
            {
                @Override
                public CTAlphaFloorEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaFloorArray(n);
                }
                
                @Override
                public CTAlphaFloorEffect set(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
                    final CTAlphaFloorEffect alphaFloorArray = CTBlipImpl.this.getAlphaFloorArray(n);
                    CTBlipImpl.this.setAlphaFloorArray(n, ctAlphaFloorEffect);
                    return alphaFloorArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
                    CTBlipImpl.this.insertNewAlphaFloor(n).set((XmlObject)ctAlphaFloorEffect);
                }
                
                @Override
                public CTAlphaFloorEffect remove(final int n) {
                    final CTAlphaFloorEffect alphaFloorArray = CTBlipImpl.this.getAlphaFloorArray(n);
                    CTBlipImpl.this.removeAlphaFloor(n);
                    return alphaFloorArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaFloorArray();
                }
            }
            return new AlphaFloorList();
        }
    }
    
    @Deprecated
    public CTAlphaFloorEffect[] getAlphaFloorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHAFLOOR$4, (List)list);
            final CTAlphaFloorEffect[] array = new CTAlphaFloorEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaFloorEffect getAlphaFloorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaFloorEffect ctAlphaFloorEffect = (CTAlphaFloorEffect)this.get_store().find_element_user(CTBlipImpl.ALPHAFLOOR$4, n);
            if (ctAlphaFloorEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaFloorEffect;
        }
    }
    
    public int sizeOfAlphaFloorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHAFLOOR$4);
        }
    }
    
    public void setAlphaFloorArray(final CTAlphaFloorEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHAFLOOR$4);
    }
    
    public void setAlphaFloorArray(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaFloorEffect, CTBlipImpl.ALPHAFLOOR$4, n, (short)2);
    }
    
    public CTAlphaFloorEffect insertNewAlphaFloor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaFloorEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHAFLOOR$4, n);
        }
    }
    
    public CTAlphaFloorEffect addNewAlphaFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaFloorEffect)this.get_store().add_element_user(CTBlipImpl.ALPHAFLOOR$4);
        }
    }
    
    public void removeAlphaFloor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHAFLOOR$4, n);
        }
    }
    
    public List<CTAlphaInverseEffect> getAlphaInvList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaInvList extends AbstractList<CTAlphaInverseEffect>
            {
                @Override
                public CTAlphaInverseEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaInvArray(n);
                }
                
                @Override
                public CTAlphaInverseEffect set(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
                    final CTAlphaInverseEffect alphaInvArray = CTBlipImpl.this.getAlphaInvArray(n);
                    CTBlipImpl.this.setAlphaInvArray(n, ctAlphaInverseEffect);
                    return alphaInvArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
                    CTBlipImpl.this.insertNewAlphaInv(n).set((XmlObject)ctAlphaInverseEffect);
                }
                
                @Override
                public CTAlphaInverseEffect remove(final int n) {
                    final CTAlphaInverseEffect alphaInvArray = CTBlipImpl.this.getAlphaInvArray(n);
                    CTBlipImpl.this.removeAlphaInv(n);
                    return alphaInvArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaInvArray();
                }
            }
            return new AlphaInvList();
        }
    }
    
    @Deprecated
    public CTAlphaInverseEffect[] getAlphaInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHAINV$6, (List)list);
            final CTAlphaInverseEffect[] array = new CTAlphaInverseEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaInverseEffect getAlphaInvArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaInverseEffect ctAlphaInverseEffect = (CTAlphaInverseEffect)this.get_store().find_element_user(CTBlipImpl.ALPHAINV$6, n);
            if (ctAlphaInverseEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaInverseEffect;
        }
    }
    
    public int sizeOfAlphaInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHAINV$6);
        }
    }
    
    public void setAlphaInvArray(final CTAlphaInverseEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHAINV$6);
    }
    
    public void setAlphaInvArray(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaInverseEffect, CTBlipImpl.ALPHAINV$6, n, (short)2);
    }
    
    public CTAlphaInverseEffect insertNewAlphaInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaInverseEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHAINV$6, n);
        }
    }
    
    public CTAlphaInverseEffect addNewAlphaInv() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaInverseEffect)this.get_store().add_element_user(CTBlipImpl.ALPHAINV$6);
        }
    }
    
    public void removeAlphaInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHAINV$6, n);
        }
    }
    
    public List<CTAlphaModulateEffect> getAlphaModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModList extends AbstractList<CTAlphaModulateEffect>
            {
                @Override
                public CTAlphaModulateEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaModArray(n);
                }
                
                @Override
                public CTAlphaModulateEffect set(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
                    final CTAlphaModulateEffect alphaModArray = CTBlipImpl.this.getAlphaModArray(n);
                    CTBlipImpl.this.setAlphaModArray(n, ctAlphaModulateEffect);
                    return alphaModArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
                    CTBlipImpl.this.insertNewAlphaMod(n).set((XmlObject)ctAlphaModulateEffect);
                }
                
                @Override
                public CTAlphaModulateEffect remove(final int n) {
                    final CTAlphaModulateEffect alphaModArray = CTBlipImpl.this.getAlphaModArray(n);
                    CTBlipImpl.this.removeAlphaMod(n);
                    return alphaModArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaModArray();
                }
            }
            return new AlphaModList();
        }
    }
    
    @Deprecated
    public CTAlphaModulateEffect[] getAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHAMOD$8, (List)list);
            final CTAlphaModulateEffect[] array = new CTAlphaModulateEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaModulateEffect getAlphaModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaModulateEffect ctAlphaModulateEffect = (CTAlphaModulateEffect)this.get_store().find_element_user(CTBlipImpl.ALPHAMOD$8, n);
            if (ctAlphaModulateEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaModulateEffect;
        }
    }
    
    public int sizeOfAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHAMOD$8);
        }
    }
    
    public void setAlphaModArray(final CTAlphaModulateEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHAMOD$8);
    }
    
    public void setAlphaModArray(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaModulateEffect, CTBlipImpl.ALPHAMOD$8, n, (short)2);
    }
    
    public CTAlphaModulateEffect insertNewAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHAMOD$8, n);
        }
    }
    
    public CTAlphaModulateEffect addNewAlphaMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateEffect)this.get_store().add_element_user(CTBlipImpl.ALPHAMOD$8);
        }
    }
    
    public void removeAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHAMOD$8, n);
        }
    }
    
    public List<CTAlphaModulateFixedEffect> getAlphaModFixList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModFixList extends AbstractList<CTAlphaModulateFixedEffect>
            {
                @Override
                public CTAlphaModulateFixedEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaModFixArray(n);
                }
                
                @Override
                public CTAlphaModulateFixedEffect set(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
                    final CTAlphaModulateFixedEffect alphaModFixArray = CTBlipImpl.this.getAlphaModFixArray(n);
                    CTBlipImpl.this.setAlphaModFixArray(n, ctAlphaModulateFixedEffect);
                    return alphaModFixArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
                    CTBlipImpl.this.insertNewAlphaModFix(n).set((XmlObject)ctAlphaModulateFixedEffect);
                }
                
                @Override
                public CTAlphaModulateFixedEffect remove(final int n) {
                    final CTAlphaModulateFixedEffect alphaModFixArray = CTBlipImpl.this.getAlphaModFixArray(n);
                    CTBlipImpl.this.removeAlphaModFix(n);
                    return alphaModFixArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaModFixArray();
                }
            }
            return new AlphaModFixList();
        }
    }
    
    @Deprecated
    public CTAlphaModulateFixedEffect[] getAlphaModFixArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHAMODFIX$10, (List)list);
            final CTAlphaModulateFixedEffect[] array = new CTAlphaModulateFixedEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaModulateFixedEffect getAlphaModFixArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect = (CTAlphaModulateFixedEffect)this.get_store().find_element_user(CTBlipImpl.ALPHAMODFIX$10, n);
            if (ctAlphaModulateFixedEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaModulateFixedEffect;
        }
    }
    
    public int sizeOfAlphaModFixArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHAMODFIX$10);
        }
    }
    
    public void setAlphaModFixArray(final CTAlphaModulateFixedEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHAMODFIX$10);
    }
    
    public void setAlphaModFixArray(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaModulateFixedEffect, CTBlipImpl.ALPHAMODFIX$10, n, (short)2);
    }
    
    public CTAlphaModulateFixedEffect insertNewAlphaModFix(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateFixedEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHAMODFIX$10, n);
        }
    }
    
    public CTAlphaModulateFixedEffect addNewAlphaModFix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateFixedEffect)this.get_store().add_element_user(CTBlipImpl.ALPHAMODFIX$10);
        }
    }
    
    public void removeAlphaModFix(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHAMODFIX$10, n);
        }
    }
    
    public List<CTAlphaReplaceEffect> getAlphaReplList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaReplList extends AbstractList<CTAlphaReplaceEffect>
            {
                @Override
                public CTAlphaReplaceEffect get(final int n) {
                    return CTBlipImpl.this.getAlphaReplArray(n);
                }
                
                @Override
                public CTAlphaReplaceEffect set(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
                    final CTAlphaReplaceEffect alphaReplArray = CTBlipImpl.this.getAlphaReplArray(n);
                    CTBlipImpl.this.setAlphaReplArray(n, ctAlphaReplaceEffect);
                    return alphaReplArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
                    CTBlipImpl.this.insertNewAlphaRepl(n).set((XmlObject)ctAlphaReplaceEffect);
                }
                
                @Override
                public CTAlphaReplaceEffect remove(final int n) {
                    final CTAlphaReplaceEffect alphaReplArray = CTBlipImpl.this.getAlphaReplArray(n);
                    CTBlipImpl.this.removeAlphaRepl(n);
                    return alphaReplArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfAlphaReplArray();
                }
            }
            return new AlphaReplList();
        }
    }
    
    @Deprecated
    public CTAlphaReplaceEffect[] getAlphaReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.ALPHAREPL$12, (List)list);
            final CTAlphaReplaceEffect[] array = new CTAlphaReplaceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaReplaceEffect getAlphaReplArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaReplaceEffect ctAlphaReplaceEffect = (CTAlphaReplaceEffect)this.get_store().find_element_user(CTBlipImpl.ALPHAREPL$12, n);
            if (ctAlphaReplaceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaReplaceEffect;
        }
    }
    
    public int sizeOfAlphaReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.ALPHAREPL$12);
        }
    }
    
    public void setAlphaReplArray(final CTAlphaReplaceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.ALPHAREPL$12);
    }
    
    public void setAlphaReplArray(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaReplaceEffect, CTBlipImpl.ALPHAREPL$12, n, (short)2);
    }
    
    public CTAlphaReplaceEffect insertNewAlphaRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaReplaceEffect)this.get_store().insert_element_user(CTBlipImpl.ALPHAREPL$12, n);
        }
    }
    
    public CTAlphaReplaceEffect addNewAlphaRepl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaReplaceEffect)this.get_store().add_element_user(CTBlipImpl.ALPHAREPL$12);
        }
    }
    
    public void removeAlphaRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.ALPHAREPL$12, n);
        }
    }
    
    public List<CTBiLevelEffect> getBiLevelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BiLevelList extends AbstractList<CTBiLevelEffect>
            {
                @Override
                public CTBiLevelEffect get(final int n) {
                    return CTBlipImpl.this.getBiLevelArray(n);
                }
                
                @Override
                public CTBiLevelEffect set(final int n, final CTBiLevelEffect ctBiLevelEffect) {
                    final CTBiLevelEffect biLevelArray = CTBlipImpl.this.getBiLevelArray(n);
                    CTBlipImpl.this.setBiLevelArray(n, ctBiLevelEffect);
                    return biLevelArray;
                }
                
                @Override
                public void add(final int n, final CTBiLevelEffect ctBiLevelEffect) {
                    CTBlipImpl.this.insertNewBiLevel(n).set((XmlObject)ctBiLevelEffect);
                }
                
                @Override
                public CTBiLevelEffect remove(final int n) {
                    final CTBiLevelEffect biLevelArray = CTBlipImpl.this.getBiLevelArray(n);
                    CTBlipImpl.this.removeBiLevel(n);
                    return biLevelArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfBiLevelArray();
                }
            }
            return new BiLevelList();
        }
    }
    
    @Deprecated
    public CTBiLevelEffect[] getBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.BILEVEL$14, (List)list);
            final CTBiLevelEffect[] array = new CTBiLevelEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBiLevelEffect getBiLevelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBiLevelEffect ctBiLevelEffect = (CTBiLevelEffect)this.get_store().find_element_user(CTBlipImpl.BILEVEL$14, n);
            if (ctBiLevelEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBiLevelEffect;
        }
    }
    
    public int sizeOfBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.BILEVEL$14);
        }
    }
    
    public void setBiLevelArray(final CTBiLevelEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.BILEVEL$14);
    }
    
    public void setBiLevelArray(final int n, final CTBiLevelEffect ctBiLevelEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBiLevelEffect, CTBlipImpl.BILEVEL$14, n, (short)2);
    }
    
    public CTBiLevelEffect insertNewBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBiLevelEffect)this.get_store().insert_element_user(CTBlipImpl.BILEVEL$14, n);
        }
    }
    
    public CTBiLevelEffect addNewBiLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBiLevelEffect)this.get_store().add_element_user(CTBlipImpl.BILEVEL$14);
        }
    }
    
    public void removeBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.BILEVEL$14, n);
        }
    }
    
    public List<CTBlurEffect> getBlurList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlurList extends AbstractList<CTBlurEffect>
            {
                @Override
                public CTBlurEffect get(final int n) {
                    return CTBlipImpl.this.getBlurArray(n);
                }
                
                @Override
                public CTBlurEffect set(final int n, final CTBlurEffect ctBlurEffect) {
                    final CTBlurEffect blurArray = CTBlipImpl.this.getBlurArray(n);
                    CTBlipImpl.this.setBlurArray(n, ctBlurEffect);
                    return blurArray;
                }
                
                @Override
                public void add(final int n, final CTBlurEffect ctBlurEffect) {
                    CTBlipImpl.this.insertNewBlur(n).set((XmlObject)ctBlurEffect);
                }
                
                @Override
                public CTBlurEffect remove(final int n) {
                    final CTBlurEffect blurArray = CTBlipImpl.this.getBlurArray(n);
                    CTBlipImpl.this.removeBlur(n);
                    return blurArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfBlurArray();
                }
            }
            return new BlurList();
        }
    }
    
    @Deprecated
    public CTBlurEffect[] getBlurArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.BLUR$16, (List)list);
            final CTBlurEffect[] array = new CTBlurEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBlurEffect getBlurArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlurEffect ctBlurEffect = (CTBlurEffect)this.get_store().find_element_user(CTBlipImpl.BLUR$16, n);
            if (ctBlurEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBlurEffect;
        }
    }
    
    public int sizeOfBlurArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.BLUR$16);
        }
    }
    
    public void setBlurArray(final CTBlurEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.BLUR$16);
    }
    
    public void setBlurArray(final int n, final CTBlurEffect ctBlurEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBlurEffect, CTBlipImpl.BLUR$16, n, (short)2);
    }
    
    public CTBlurEffect insertNewBlur(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlurEffect)this.get_store().insert_element_user(CTBlipImpl.BLUR$16, n);
        }
    }
    
    public CTBlurEffect addNewBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlurEffect)this.get_store().add_element_user(CTBlipImpl.BLUR$16);
        }
    }
    
    public void removeBlur(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.BLUR$16, n);
        }
    }
    
    public List<CTColorChangeEffect> getClrChangeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClrChangeList extends AbstractList<CTColorChangeEffect>
            {
                @Override
                public CTColorChangeEffect get(final int n) {
                    return CTBlipImpl.this.getClrChangeArray(n);
                }
                
                @Override
                public CTColorChangeEffect set(final int n, final CTColorChangeEffect ctColorChangeEffect) {
                    final CTColorChangeEffect clrChangeArray = CTBlipImpl.this.getClrChangeArray(n);
                    CTBlipImpl.this.setClrChangeArray(n, ctColorChangeEffect);
                    return clrChangeArray;
                }
                
                @Override
                public void add(final int n, final CTColorChangeEffect ctColorChangeEffect) {
                    CTBlipImpl.this.insertNewClrChange(n).set((XmlObject)ctColorChangeEffect);
                }
                
                @Override
                public CTColorChangeEffect remove(final int n) {
                    final CTColorChangeEffect clrChangeArray = CTBlipImpl.this.getClrChangeArray(n);
                    CTBlipImpl.this.removeClrChange(n);
                    return clrChangeArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfClrChangeArray();
                }
            }
            return new ClrChangeList();
        }
    }
    
    @Deprecated
    public CTColorChangeEffect[] getClrChangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.CLRCHANGE$18, (List)list);
            final CTColorChangeEffect[] array = new CTColorChangeEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColorChangeEffect getClrChangeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorChangeEffect ctColorChangeEffect = (CTColorChangeEffect)this.get_store().find_element_user(CTBlipImpl.CLRCHANGE$18, n);
            if (ctColorChangeEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColorChangeEffect;
        }
    }
    
    public int sizeOfClrChangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.CLRCHANGE$18);
        }
    }
    
    public void setClrChangeArray(final CTColorChangeEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.CLRCHANGE$18);
    }
    
    public void setClrChangeArray(final int n, final CTColorChangeEffect ctColorChangeEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctColorChangeEffect, CTBlipImpl.CLRCHANGE$18, n, (short)2);
    }
    
    public CTColorChangeEffect insertNewClrChange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorChangeEffect)this.get_store().insert_element_user(CTBlipImpl.CLRCHANGE$18, n);
        }
    }
    
    public CTColorChangeEffect addNewClrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorChangeEffect)this.get_store().add_element_user(CTBlipImpl.CLRCHANGE$18);
        }
    }
    
    public void removeClrChange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.CLRCHANGE$18, n);
        }
    }
    
    public List<CTColorReplaceEffect> getClrReplList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClrReplList extends AbstractList<CTColorReplaceEffect>
            {
                @Override
                public CTColorReplaceEffect get(final int n) {
                    return CTBlipImpl.this.getClrReplArray(n);
                }
                
                @Override
                public CTColorReplaceEffect set(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
                    final CTColorReplaceEffect clrReplArray = CTBlipImpl.this.getClrReplArray(n);
                    CTBlipImpl.this.setClrReplArray(n, ctColorReplaceEffect);
                    return clrReplArray;
                }
                
                @Override
                public void add(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
                    CTBlipImpl.this.insertNewClrRepl(n).set((XmlObject)ctColorReplaceEffect);
                }
                
                @Override
                public CTColorReplaceEffect remove(final int n) {
                    final CTColorReplaceEffect clrReplArray = CTBlipImpl.this.getClrReplArray(n);
                    CTBlipImpl.this.removeClrRepl(n);
                    return clrReplArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfClrReplArray();
                }
            }
            return new ClrReplList();
        }
    }
    
    @Deprecated
    public CTColorReplaceEffect[] getClrReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.CLRREPL$20, (List)list);
            final CTColorReplaceEffect[] array = new CTColorReplaceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColorReplaceEffect getClrReplArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorReplaceEffect ctColorReplaceEffect = (CTColorReplaceEffect)this.get_store().find_element_user(CTBlipImpl.CLRREPL$20, n);
            if (ctColorReplaceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColorReplaceEffect;
        }
    }
    
    public int sizeOfClrReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.CLRREPL$20);
        }
    }
    
    public void setClrReplArray(final CTColorReplaceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.CLRREPL$20);
    }
    
    public void setClrReplArray(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctColorReplaceEffect, CTBlipImpl.CLRREPL$20, n, (short)2);
    }
    
    public CTColorReplaceEffect insertNewClrRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorReplaceEffect)this.get_store().insert_element_user(CTBlipImpl.CLRREPL$20, n);
        }
    }
    
    public CTColorReplaceEffect addNewClrRepl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorReplaceEffect)this.get_store().add_element_user(CTBlipImpl.CLRREPL$20);
        }
    }
    
    public void removeClrRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.CLRREPL$20, n);
        }
    }
    
    public List<CTDuotoneEffect> getDuotoneList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DuotoneList extends AbstractList<CTDuotoneEffect>
            {
                @Override
                public CTDuotoneEffect get(final int n) {
                    return CTBlipImpl.this.getDuotoneArray(n);
                }
                
                @Override
                public CTDuotoneEffect set(final int n, final CTDuotoneEffect ctDuotoneEffect) {
                    final CTDuotoneEffect duotoneArray = CTBlipImpl.this.getDuotoneArray(n);
                    CTBlipImpl.this.setDuotoneArray(n, ctDuotoneEffect);
                    return duotoneArray;
                }
                
                @Override
                public void add(final int n, final CTDuotoneEffect ctDuotoneEffect) {
                    CTBlipImpl.this.insertNewDuotone(n).set((XmlObject)ctDuotoneEffect);
                }
                
                @Override
                public CTDuotoneEffect remove(final int n) {
                    final CTDuotoneEffect duotoneArray = CTBlipImpl.this.getDuotoneArray(n);
                    CTBlipImpl.this.removeDuotone(n);
                    return duotoneArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfDuotoneArray();
                }
            }
            return new DuotoneList();
        }
    }
    
    @Deprecated
    public CTDuotoneEffect[] getDuotoneArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.DUOTONE$22, (List)list);
            final CTDuotoneEffect[] array = new CTDuotoneEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDuotoneEffect getDuotoneArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDuotoneEffect ctDuotoneEffect = (CTDuotoneEffect)this.get_store().find_element_user(CTBlipImpl.DUOTONE$22, n);
            if (ctDuotoneEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDuotoneEffect;
        }
    }
    
    public int sizeOfDuotoneArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.DUOTONE$22);
        }
    }
    
    public void setDuotoneArray(final CTDuotoneEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.DUOTONE$22);
    }
    
    public void setDuotoneArray(final int n, final CTDuotoneEffect ctDuotoneEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctDuotoneEffect, CTBlipImpl.DUOTONE$22, n, (short)2);
    }
    
    public CTDuotoneEffect insertNewDuotone(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDuotoneEffect)this.get_store().insert_element_user(CTBlipImpl.DUOTONE$22, n);
        }
    }
    
    public CTDuotoneEffect addNewDuotone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDuotoneEffect)this.get_store().add_element_user(CTBlipImpl.DUOTONE$22);
        }
    }
    
    public void removeDuotone(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.DUOTONE$22, n);
        }
    }
    
    public List<CTFillOverlayEffect> getFillOverlayList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillOverlayList extends AbstractList<CTFillOverlayEffect>
            {
                @Override
                public CTFillOverlayEffect get(final int n) {
                    return CTBlipImpl.this.getFillOverlayArray(n);
                }
                
                @Override
                public CTFillOverlayEffect set(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
                    final CTFillOverlayEffect fillOverlayArray = CTBlipImpl.this.getFillOverlayArray(n);
                    CTBlipImpl.this.setFillOverlayArray(n, ctFillOverlayEffect);
                    return fillOverlayArray;
                }
                
                @Override
                public void add(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
                    CTBlipImpl.this.insertNewFillOverlay(n).set((XmlObject)ctFillOverlayEffect);
                }
                
                @Override
                public CTFillOverlayEffect remove(final int n) {
                    final CTFillOverlayEffect fillOverlayArray = CTBlipImpl.this.getFillOverlayArray(n);
                    CTBlipImpl.this.removeFillOverlay(n);
                    return fillOverlayArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfFillOverlayArray();
                }
            }
            return new FillOverlayList();
        }
    }
    
    @Deprecated
    public CTFillOverlayEffect[] getFillOverlayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.FILLOVERLAY$24, (List)list);
            final CTFillOverlayEffect[] array = new CTFillOverlayEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFillOverlayEffect getFillOverlayArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillOverlayEffect ctFillOverlayEffect = (CTFillOverlayEffect)this.get_store().find_element_user(CTBlipImpl.FILLOVERLAY$24, n);
            if (ctFillOverlayEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFillOverlayEffect;
        }
    }
    
    public int sizeOfFillOverlayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.FILLOVERLAY$24);
        }
    }
    
    public void setFillOverlayArray(final CTFillOverlayEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.FILLOVERLAY$24);
    }
    
    public void setFillOverlayArray(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctFillOverlayEffect, CTBlipImpl.FILLOVERLAY$24, n, (short)2);
    }
    
    public CTFillOverlayEffect insertNewFillOverlay(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillOverlayEffect)this.get_store().insert_element_user(CTBlipImpl.FILLOVERLAY$24, n);
        }
    }
    
    public CTFillOverlayEffect addNewFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillOverlayEffect)this.get_store().add_element_user(CTBlipImpl.FILLOVERLAY$24);
        }
    }
    
    public void removeFillOverlay(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.FILLOVERLAY$24, n);
        }
    }
    
    public List<CTGrayscaleEffect> getGraysclList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GraysclList extends AbstractList<CTGrayscaleEffect>
            {
                @Override
                public CTGrayscaleEffect get(final int n) {
                    return CTBlipImpl.this.getGraysclArray(n);
                }
                
                @Override
                public CTGrayscaleEffect set(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
                    final CTGrayscaleEffect graysclArray = CTBlipImpl.this.getGraysclArray(n);
                    CTBlipImpl.this.setGraysclArray(n, ctGrayscaleEffect);
                    return graysclArray;
                }
                
                @Override
                public void add(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
                    CTBlipImpl.this.insertNewGrayscl(n).set((XmlObject)ctGrayscaleEffect);
                }
                
                @Override
                public CTGrayscaleEffect remove(final int n) {
                    final CTGrayscaleEffect graysclArray = CTBlipImpl.this.getGraysclArray(n);
                    CTBlipImpl.this.removeGrayscl(n);
                    return graysclArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfGraysclArray();
                }
            }
            return new GraysclList();
        }
    }
    
    @Deprecated
    public CTGrayscaleEffect[] getGraysclArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.GRAYSCL$26, (List)list);
            final CTGrayscaleEffect[] array = new CTGrayscaleEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGrayscaleEffect getGraysclArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrayscaleEffect ctGrayscaleEffect = (CTGrayscaleEffect)this.get_store().find_element_user(CTBlipImpl.GRAYSCL$26, n);
            if (ctGrayscaleEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGrayscaleEffect;
        }
    }
    
    public int sizeOfGraysclArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.GRAYSCL$26);
        }
    }
    
    public void setGraysclArray(final CTGrayscaleEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.GRAYSCL$26);
    }
    
    public void setGraysclArray(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctGrayscaleEffect, CTBlipImpl.GRAYSCL$26, n, (short)2);
    }
    
    public CTGrayscaleEffect insertNewGrayscl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleEffect)this.get_store().insert_element_user(CTBlipImpl.GRAYSCL$26, n);
        }
    }
    
    public CTGrayscaleEffect addNewGrayscl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleEffect)this.get_store().add_element_user(CTBlipImpl.GRAYSCL$26);
        }
    }
    
    public void removeGrayscl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.GRAYSCL$26, n);
        }
    }
    
    public List<CTHSLEffect> getHslList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HslList extends AbstractList<CTHSLEffect>
            {
                @Override
                public CTHSLEffect get(final int n) {
                    return CTBlipImpl.this.getHslArray(n);
                }
                
                @Override
                public CTHSLEffect set(final int n, final CTHSLEffect cthslEffect) {
                    final CTHSLEffect hslArray = CTBlipImpl.this.getHslArray(n);
                    CTBlipImpl.this.setHslArray(n, cthslEffect);
                    return hslArray;
                }
                
                @Override
                public void add(final int n, final CTHSLEffect cthslEffect) {
                    CTBlipImpl.this.insertNewHsl(n).set((XmlObject)cthslEffect);
                }
                
                @Override
                public CTHSLEffect remove(final int n) {
                    final CTHSLEffect hslArray = CTBlipImpl.this.getHslArray(n);
                    CTBlipImpl.this.removeHsl(n);
                    return hslArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfHslArray();
                }
            }
            return new HslList();
        }
    }
    
    @Deprecated
    public CTHSLEffect[] getHslArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.HSL$28, (List)list);
            final CTHSLEffect[] array = new CTHSLEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHSLEffect getHslArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHSLEffect cthslEffect = (CTHSLEffect)this.get_store().find_element_user(CTBlipImpl.HSL$28, n);
            if (cthslEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return cthslEffect;
        }
    }
    
    public int sizeOfHslArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.HSL$28);
        }
    }
    
    public void setHslArray(final CTHSLEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.HSL$28);
    }
    
    public void setHslArray(final int n, final CTHSLEffect cthslEffect) {
        this.generatedSetterHelperImpl((XmlObject)cthslEffect, CTBlipImpl.HSL$28, n, (short)2);
    }
    
    public CTHSLEffect insertNewHsl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHSLEffect)this.get_store().insert_element_user(CTBlipImpl.HSL$28, n);
        }
    }
    
    public CTHSLEffect addNewHsl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHSLEffect)this.get_store().add_element_user(CTBlipImpl.HSL$28);
        }
    }
    
    public void removeHsl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.HSL$28, n);
        }
    }
    
    public List<CTLuminanceEffect> getLumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumList extends AbstractList<CTLuminanceEffect>
            {
                @Override
                public CTLuminanceEffect get(final int n) {
                    return CTBlipImpl.this.getLumArray(n);
                }
                
                @Override
                public CTLuminanceEffect set(final int n, final CTLuminanceEffect ctLuminanceEffect) {
                    final CTLuminanceEffect lumArray = CTBlipImpl.this.getLumArray(n);
                    CTBlipImpl.this.setLumArray(n, ctLuminanceEffect);
                    return lumArray;
                }
                
                @Override
                public void add(final int n, final CTLuminanceEffect ctLuminanceEffect) {
                    CTBlipImpl.this.insertNewLum(n).set((XmlObject)ctLuminanceEffect);
                }
                
                @Override
                public CTLuminanceEffect remove(final int n) {
                    final CTLuminanceEffect lumArray = CTBlipImpl.this.getLumArray(n);
                    CTBlipImpl.this.removeLum(n);
                    return lumArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfLumArray();
                }
            }
            return new LumList();
        }
    }
    
    @Deprecated
    public CTLuminanceEffect[] getLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.LUM$30, (List)list);
            final CTLuminanceEffect[] array = new CTLuminanceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLuminanceEffect getLumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLuminanceEffect ctLuminanceEffect = (CTLuminanceEffect)this.get_store().find_element_user(CTBlipImpl.LUM$30, n);
            if (ctLuminanceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLuminanceEffect;
        }
    }
    
    public int sizeOfLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.LUM$30);
        }
    }
    
    public void setLumArray(final CTLuminanceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.LUM$30);
    }
    
    public void setLumArray(final int n, final CTLuminanceEffect ctLuminanceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctLuminanceEffect, CTBlipImpl.LUM$30, n, (short)2);
    }
    
    public CTLuminanceEffect insertNewLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLuminanceEffect)this.get_store().insert_element_user(CTBlipImpl.LUM$30, n);
        }
    }
    
    public CTLuminanceEffect addNewLum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLuminanceEffect)this.get_store().add_element_user(CTBlipImpl.LUM$30);
        }
    }
    
    public void removeLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.LUM$30, n);
        }
    }
    
    public List<CTTintEffect> getTintList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TintList extends AbstractList<CTTintEffect>
            {
                @Override
                public CTTintEffect get(final int n) {
                    return CTBlipImpl.this.getTintArray(n);
                }
                
                @Override
                public CTTintEffect set(final int n, final CTTintEffect ctTintEffect) {
                    final CTTintEffect tintArray = CTBlipImpl.this.getTintArray(n);
                    CTBlipImpl.this.setTintArray(n, ctTintEffect);
                    return tintArray;
                }
                
                @Override
                public void add(final int n, final CTTintEffect ctTintEffect) {
                    CTBlipImpl.this.insertNewTint(n).set((XmlObject)ctTintEffect);
                }
                
                @Override
                public CTTintEffect remove(final int n) {
                    final CTTintEffect tintArray = CTBlipImpl.this.getTintArray(n);
                    CTBlipImpl.this.removeTint(n);
                    return tintArray;
                }
                
                @Override
                public int size() {
                    return CTBlipImpl.this.sizeOfTintArray();
                }
            }
            return new TintList();
        }
    }
    
    @Deprecated
    public CTTintEffect[] getTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBlipImpl.TINT$32, (List)list);
            final CTTintEffect[] array = new CTTintEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTintEffect getTintArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTintEffect ctTintEffect = (CTTintEffect)this.get_store().find_element_user(CTBlipImpl.TINT$32, n);
            if (ctTintEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTintEffect;
        }
    }
    
    public int sizeOfTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.TINT$32);
        }
    }
    
    public void setTintArray(final CTTintEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBlipImpl.TINT$32);
    }
    
    public void setTintArray(final int n, final CTTintEffect ctTintEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctTintEffect, CTBlipImpl.TINT$32, n, (short)2);
    }
    
    public CTTintEffect insertNewTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTintEffect)this.get_store().insert_element_user(CTBlipImpl.TINT$32, n);
        }
    }
    
    public CTTintEffect addNewTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTintEffect)this.get_store().add_element_user(CTBlipImpl.TINT$32);
        }
    }
    
    public void removeTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.TINT$32, n);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTBlipImpl.EXTLST$34, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBlipImpl.EXTLST$34) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBlipImpl.EXTLST$34, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTBlipImpl.EXTLST$34);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBlipImpl.EXTLST$34, 0);
        }
    }
    
    public String getEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.EMBED$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBlipImpl.EMBED$36);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId = (STRelationshipId)this.get_store().find_attribute_user(CTBlipImpl.EMBED$36);
            if (stRelationshipId == null) {
                stRelationshipId = (STRelationshipId)this.get_default_attribute_value(CTBlipImpl.EMBED$36);
            }
            return stRelationshipId;
        }
    }
    
    public boolean isSetEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBlipImpl.EMBED$36) != null;
        }
    }
    
    public void setEmbed(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.EMBED$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBlipImpl.EMBED$36);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetEmbed(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTBlipImpl.EMBED$36);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTBlipImpl.EMBED$36);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetEmbed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBlipImpl.EMBED$36);
        }
    }
    
    public String getLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.LINK$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBlipImpl.LINK$38);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId = (STRelationshipId)this.get_store().find_attribute_user(CTBlipImpl.LINK$38);
            if (stRelationshipId == null) {
                stRelationshipId = (STRelationshipId)this.get_default_attribute_value(CTBlipImpl.LINK$38);
            }
            return stRelationshipId;
        }
    }
    
    public boolean isSetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBlipImpl.LINK$38) != null;
        }
    }
    
    public void setLink(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.LINK$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBlipImpl.LINK$38);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetLink(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTBlipImpl.LINK$38);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTBlipImpl.LINK$38);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBlipImpl.LINK$38);
        }
    }
    
    public STBlipCompression.Enum getCstate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.CSTATE$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBlipImpl.CSTATE$40);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STBlipCompression.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBlipCompression xgetCstate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlipCompression stBlipCompression = (STBlipCompression)this.get_store().find_attribute_user(CTBlipImpl.CSTATE$40);
            if (stBlipCompression == null) {
                stBlipCompression = (STBlipCompression)this.get_default_attribute_value(CTBlipImpl.CSTATE$40);
            }
            return stBlipCompression;
        }
    }
    
    public boolean isSetCstate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBlipImpl.CSTATE$40) != null;
        }
    }
    
    public void setCstate(final STBlipCompression.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBlipImpl.CSTATE$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBlipImpl.CSTATE$40);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCstate(final STBlipCompression stBlipCompression) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlipCompression stBlipCompression2 = (STBlipCompression)this.get_store().find_attribute_user(CTBlipImpl.CSTATE$40);
            if (stBlipCompression2 == null) {
                stBlipCompression2 = (STBlipCompression)this.get_store().add_attribute_user(CTBlipImpl.CSTATE$40);
            }
            stBlipCompression2.set((XmlObject)stBlipCompression);
        }
    }
    
    public void unsetCstate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBlipImpl.CSTATE$40);
        }
    }
    
    static {
        ALPHABILEVEL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaBiLevel");
        ALPHACEILING$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaCeiling");
        ALPHAFLOOR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaFloor");
        ALPHAINV$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaInv");
        ALPHAMOD$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaMod");
        ALPHAMODFIX$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaModFix");
        ALPHAREPL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaRepl");
        BILEVEL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "biLevel");
        BLUR$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blur");
        CLRCHANGE$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrChange");
        CLRREPL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrRepl");
        DUOTONE$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "duotone");
        FILLOVERLAY$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillOverlay");
        GRAYSCL$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grayscl");
        HSL$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hsl");
        LUM$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lum");
        TINT$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tint");
        EXTLST$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        EMBED$36 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "embed");
        LINK$38 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "link");
        CSTATE$40 = new QName("", "cstate");
    }
}
