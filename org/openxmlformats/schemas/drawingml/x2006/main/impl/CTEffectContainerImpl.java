package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STEffectContainerType;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransformEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTintEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeOffsetEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLuminanceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHSLEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDuotoneEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorChangeEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlendEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBiLevelEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaReplaceEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaOutsetEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateFixedEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaInverseEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaFloorEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaCeilingEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaBiLevelEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectReference;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTEffectContainerImpl extends XmlComplexContentImpl implements CTEffectContainer
{
    private static final long serialVersionUID = 1L;
    private static final QName CONT$0;
    private static final QName EFFECT$2;
    private static final QName ALPHABILEVEL$4;
    private static final QName ALPHACEILING$6;
    private static final QName ALPHAFLOOR$8;
    private static final QName ALPHAINV$10;
    private static final QName ALPHAMOD$12;
    private static final QName ALPHAMODFIX$14;
    private static final QName ALPHAOUTSET$16;
    private static final QName ALPHAREPL$18;
    private static final QName BILEVEL$20;
    private static final QName BLEND$22;
    private static final QName BLUR$24;
    private static final QName CLRCHANGE$26;
    private static final QName CLRREPL$28;
    private static final QName DUOTONE$30;
    private static final QName FILL$32;
    private static final QName FILLOVERLAY$34;
    private static final QName GLOW$36;
    private static final QName GRAYSCL$38;
    private static final QName HSL$40;
    private static final QName INNERSHDW$42;
    private static final QName LUM$44;
    private static final QName OUTERSHDW$46;
    private static final QName PRSTSHDW$48;
    private static final QName REFLECTION$50;
    private static final QName RELOFF$52;
    private static final QName SOFTEDGE$54;
    private static final QName TINT$56;
    private static final QName XFRM$58;
    private static final QName TYPE$60;
    private static final QName NAME$62;
    
    public CTEffectContainerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTEffectContainer> getContList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ContList extends AbstractList<CTEffectContainer>
            {
                @Override
                public CTEffectContainer get(final int n) {
                    return CTEffectContainerImpl.this.getContArray(n);
                }
                
                @Override
                public CTEffectContainer set(final int n, final CTEffectContainer ctEffectContainer) {
                    final CTEffectContainer contArray = CTEffectContainerImpl.this.getContArray(n);
                    CTEffectContainerImpl.this.setContArray(n, ctEffectContainer);
                    return contArray;
                }
                
                @Override
                public void add(final int n, final CTEffectContainer ctEffectContainer) {
                    CTEffectContainerImpl.this.insertNewCont(n).set((XmlObject)ctEffectContainer);
                }
                
                @Override
                public CTEffectContainer remove(final int n) {
                    final CTEffectContainer contArray = CTEffectContainerImpl.this.getContArray(n);
                    CTEffectContainerImpl.this.removeCont(n);
                    return contArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfContArray();
                }
            }
            return new ContList();
        }
    }
    
    @Deprecated
    public CTEffectContainer[] getContArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.CONT$0, (List)list);
            final CTEffectContainer[] array = new CTEffectContainer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEffectContainer getContArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectContainer ctEffectContainer = (CTEffectContainer)this.get_store().find_element_user(CTEffectContainerImpl.CONT$0, n);
            if (ctEffectContainer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEffectContainer;
        }
    }
    
    public int sizeOfContArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.CONT$0);
        }
    }
    
    public void setContArray(final CTEffectContainer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.CONT$0);
    }
    
    public void setContArray(final int n, final CTEffectContainer ctEffectContainer) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectContainer, CTEffectContainerImpl.CONT$0, n, (short)2);
    }
    
    public CTEffectContainer insertNewCont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().insert_element_user(CTEffectContainerImpl.CONT$0, n);
        }
    }
    
    public CTEffectContainer addNewCont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectContainer)this.get_store().add_element_user(CTEffectContainerImpl.CONT$0);
        }
    }
    
    public void removeCont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.CONT$0, n);
        }
    }
    
    public List<CTEffectReference> getEffectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EffectList extends AbstractList<CTEffectReference>
            {
                @Override
                public CTEffectReference get(final int n) {
                    return CTEffectContainerImpl.this.getEffectArray(n);
                }
                
                @Override
                public CTEffectReference set(final int n, final CTEffectReference ctEffectReference) {
                    final CTEffectReference effectArray = CTEffectContainerImpl.this.getEffectArray(n);
                    CTEffectContainerImpl.this.setEffectArray(n, ctEffectReference);
                    return effectArray;
                }
                
                @Override
                public void add(final int n, final CTEffectReference ctEffectReference) {
                    CTEffectContainerImpl.this.insertNewEffect(n).set((XmlObject)ctEffectReference);
                }
                
                @Override
                public CTEffectReference remove(final int n) {
                    final CTEffectReference effectArray = CTEffectContainerImpl.this.getEffectArray(n);
                    CTEffectContainerImpl.this.removeEffect(n);
                    return effectArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfEffectArray();
                }
            }
            return new EffectList();
        }
    }
    
    @Deprecated
    public CTEffectReference[] getEffectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.EFFECT$2, (List)list);
            final CTEffectReference[] array = new CTEffectReference[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEffectReference getEffectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectReference ctEffectReference = (CTEffectReference)this.get_store().find_element_user(CTEffectContainerImpl.EFFECT$2, n);
            if (ctEffectReference == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEffectReference;
        }
    }
    
    public int sizeOfEffectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.EFFECT$2);
        }
    }
    
    public void setEffectArray(final CTEffectReference[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.EFFECT$2);
    }
    
    public void setEffectArray(final int n, final CTEffectReference ctEffectReference) {
        this.generatedSetterHelperImpl((XmlObject)ctEffectReference, CTEffectContainerImpl.EFFECT$2, n, (short)2);
    }
    
    public CTEffectReference insertNewEffect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectReference)this.get_store().insert_element_user(CTEffectContainerImpl.EFFECT$2, n);
        }
    }
    
    public CTEffectReference addNewEffect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectReference)this.get_store().add_element_user(CTEffectContainerImpl.EFFECT$2);
        }
    }
    
    public void removeEffect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.EFFECT$2, n);
        }
    }
    
    public List<CTAlphaBiLevelEffect> getAlphaBiLevelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaBiLevelList extends AbstractList<CTAlphaBiLevelEffect>
            {
                @Override
                public CTAlphaBiLevelEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaBiLevelArray(n);
                }
                
                @Override
                public CTAlphaBiLevelEffect set(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
                    final CTAlphaBiLevelEffect alphaBiLevelArray = CTEffectContainerImpl.this.getAlphaBiLevelArray(n);
                    CTEffectContainerImpl.this.setAlphaBiLevelArray(n, ctAlphaBiLevelEffect);
                    return alphaBiLevelArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaBiLevel(n).set((XmlObject)ctAlphaBiLevelEffect);
                }
                
                @Override
                public CTAlphaBiLevelEffect remove(final int n) {
                    final CTAlphaBiLevelEffect alphaBiLevelArray = CTEffectContainerImpl.this.getAlphaBiLevelArray(n);
                    CTEffectContainerImpl.this.removeAlphaBiLevel(n);
                    return alphaBiLevelArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaBiLevelArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHABILEVEL$4, (List)list);
            final CTAlphaBiLevelEffect[] array = new CTAlphaBiLevelEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaBiLevelEffect getAlphaBiLevelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaBiLevelEffect ctAlphaBiLevelEffect = (CTAlphaBiLevelEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHABILEVEL$4, n);
            if (ctAlphaBiLevelEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaBiLevelEffect;
        }
    }
    
    public int sizeOfAlphaBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHABILEVEL$4);
        }
    }
    
    public void setAlphaBiLevelArray(final CTAlphaBiLevelEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHABILEVEL$4);
    }
    
    public void setAlphaBiLevelArray(final int n, final CTAlphaBiLevelEffect ctAlphaBiLevelEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaBiLevelEffect, CTEffectContainerImpl.ALPHABILEVEL$4, n, (short)2);
    }
    
    public CTAlphaBiLevelEffect insertNewAlphaBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaBiLevelEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHABILEVEL$4, n);
        }
    }
    
    public CTAlphaBiLevelEffect addNewAlphaBiLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaBiLevelEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHABILEVEL$4);
        }
    }
    
    public void removeAlphaBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHABILEVEL$4, n);
        }
    }
    
    public List<CTAlphaCeilingEffect> getAlphaCeilingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaCeilingList extends AbstractList<CTAlphaCeilingEffect>
            {
                @Override
                public CTAlphaCeilingEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaCeilingArray(n);
                }
                
                @Override
                public CTAlphaCeilingEffect set(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
                    final CTAlphaCeilingEffect alphaCeilingArray = CTEffectContainerImpl.this.getAlphaCeilingArray(n);
                    CTEffectContainerImpl.this.setAlphaCeilingArray(n, ctAlphaCeilingEffect);
                    return alphaCeilingArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaCeiling(n).set((XmlObject)ctAlphaCeilingEffect);
                }
                
                @Override
                public CTAlphaCeilingEffect remove(final int n) {
                    final CTAlphaCeilingEffect alphaCeilingArray = CTEffectContainerImpl.this.getAlphaCeilingArray(n);
                    CTEffectContainerImpl.this.removeAlphaCeiling(n);
                    return alphaCeilingArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaCeilingArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHACEILING$6, (List)list);
            final CTAlphaCeilingEffect[] array = new CTAlphaCeilingEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaCeilingEffect getAlphaCeilingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaCeilingEffect ctAlphaCeilingEffect = (CTAlphaCeilingEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHACEILING$6, n);
            if (ctAlphaCeilingEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaCeilingEffect;
        }
    }
    
    public int sizeOfAlphaCeilingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHACEILING$6);
        }
    }
    
    public void setAlphaCeilingArray(final CTAlphaCeilingEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHACEILING$6);
    }
    
    public void setAlphaCeilingArray(final int n, final CTAlphaCeilingEffect ctAlphaCeilingEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaCeilingEffect, CTEffectContainerImpl.ALPHACEILING$6, n, (short)2);
    }
    
    public CTAlphaCeilingEffect insertNewAlphaCeiling(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaCeilingEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHACEILING$6, n);
        }
    }
    
    public CTAlphaCeilingEffect addNewAlphaCeiling() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaCeilingEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHACEILING$6);
        }
    }
    
    public void removeAlphaCeiling(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHACEILING$6, n);
        }
    }
    
    public List<CTAlphaFloorEffect> getAlphaFloorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaFloorList extends AbstractList<CTAlphaFloorEffect>
            {
                @Override
                public CTAlphaFloorEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaFloorArray(n);
                }
                
                @Override
                public CTAlphaFloorEffect set(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
                    final CTAlphaFloorEffect alphaFloorArray = CTEffectContainerImpl.this.getAlphaFloorArray(n);
                    CTEffectContainerImpl.this.setAlphaFloorArray(n, ctAlphaFloorEffect);
                    return alphaFloorArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaFloor(n).set((XmlObject)ctAlphaFloorEffect);
                }
                
                @Override
                public CTAlphaFloorEffect remove(final int n) {
                    final CTAlphaFloorEffect alphaFloorArray = CTEffectContainerImpl.this.getAlphaFloorArray(n);
                    CTEffectContainerImpl.this.removeAlphaFloor(n);
                    return alphaFloorArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaFloorArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAFLOOR$8, (List)list);
            final CTAlphaFloorEffect[] array = new CTAlphaFloorEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaFloorEffect getAlphaFloorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaFloorEffect ctAlphaFloorEffect = (CTAlphaFloorEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAFLOOR$8, n);
            if (ctAlphaFloorEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaFloorEffect;
        }
    }
    
    public int sizeOfAlphaFloorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAFLOOR$8);
        }
    }
    
    public void setAlphaFloorArray(final CTAlphaFloorEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAFLOOR$8);
    }
    
    public void setAlphaFloorArray(final int n, final CTAlphaFloorEffect ctAlphaFloorEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaFloorEffect, CTEffectContainerImpl.ALPHAFLOOR$8, n, (short)2);
    }
    
    public CTAlphaFloorEffect insertNewAlphaFloor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaFloorEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAFLOOR$8, n);
        }
    }
    
    public CTAlphaFloorEffect addNewAlphaFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaFloorEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAFLOOR$8);
        }
    }
    
    public void removeAlphaFloor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAFLOOR$8, n);
        }
    }
    
    public List<CTAlphaInverseEffect> getAlphaInvList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaInvList extends AbstractList<CTAlphaInverseEffect>
            {
                @Override
                public CTAlphaInverseEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaInvArray(n);
                }
                
                @Override
                public CTAlphaInverseEffect set(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
                    final CTAlphaInverseEffect alphaInvArray = CTEffectContainerImpl.this.getAlphaInvArray(n);
                    CTEffectContainerImpl.this.setAlphaInvArray(n, ctAlphaInverseEffect);
                    return alphaInvArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaInv(n).set((XmlObject)ctAlphaInverseEffect);
                }
                
                @Override
                public CTAlphaInverseEffect remove(final int n) {
                    final CTAlphaInverseEffect alphaInvArray = CTEffectContainerImpl.this.getAlphaInvArray(n);
                    CTEffectContainerImpl.this.removeAlphaInv(n);
                    return alphaInvArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaInvArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAINV$10, (List)list);
            final CTAlphaInverseEffect[] array = new CTAlphaInverseEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaInverseEffect getAlphaInvArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaInverseEffect ctAlphaInverseEffect = (CTAlphaInverseEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAINV$10, n);
            if (ctAlphaInverseEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaInverseEffect;
        }
    }
    
    public int sizeOfAlphaInvArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAINV$10);
        }
    }
    
    public void setAlphaInvArray(final CTAlphaInverseEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAINV$10);
    }
    
    public void setAlphaInvArray(final int n, final CTAlphaInverseEffect ctAlphaInverseEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaInverseEffect, CTEffectContainerImpl.ALPHAINV$10, n, (short)2);
    }
    
    public CTAlphaInverseEffect insertNewAlphaInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaInverseEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAINV$10, n);
        }
    }
    
    public CTAlphaInverseEffect addNewAlphaInv() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaInverseEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAINV$10);
        }
    }
    
    public void removeAlphaInv(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAINV$10, n);
        }
    }
    
    public List<CTAlphaModulateEffect> getAlphaModList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModList extends AbstractList<CTAlphaModulateEffect>
            {
                @Override
                public CTAlphaModulateEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaModArray(n);
                }
                
                @Override
                public CTAlphaModulateEffect set(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
                    final CTAlphaModulateEffect alphaModArray = CTEffectContainerImpl.this.getAlphaModArray(n);
                    CTEffectContainerImpl.this.setAlphaModArray(n, ctAlphaModulateEffect);
                    return alphaModArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaMod(n).set((XmlObject)ctAlphaModulateEffect);
                }
                
                @Override
                public CTAlphaModulateEffect remove(final int n) {
                    final CTAlphaModulateEffect alphaModArray = CTEffectContainerImpl.this.getAlphaModArray(n);
                    CTEffectContainerImpl.this.removeAlphaMod(n);
                    return alphaModArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaModArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAMOD$12, (List)list);
            final CTAlphaModulateEffect[] array = new CTAlphaModulateEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaModulateEffect getAlphaModArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaModulateEffect ctAlphaModulateEffect = (CTAlphaModulateEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAMOD$12, n);
            if (ctAlphaModulateEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaModulateEffect;
        }
    }
    
    public int sizeOfAlphaModArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAMOD$12);
        }
    }
    
    public void setAlphaModArray(final CTAlphaModulateEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAMOD$12);
    }
    
    public void setAlphaModArray(final int n, final CTAlphaModulateEffect ctAlphaModulateEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaModulateEffect, CTEffectContainerImpl.ALPHAMOD$12, n, (short)2);
    }
    
    public CTAlphaModulateEffect insertNewAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAMOD$12, n);
        }
    }
    
    public CTAlphaModulateEffect addNewAlphaMod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAMOD$12);
        }
    }
    
    public void removeAlphaMod(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAMOD$12, n);
        }
    }
    
    public List<CTAlphaModulateFixedEffect> getAlphaModFixList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaModFixList extends AbstractList<CTAlphaModulateFixedEffect>
            {
                @Override
                public CTAlphaModulateFixedEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaModFixArray(n);
                }
                
                @Override
                public CTAlphaModulateFixedEffect set(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
                    final CTAlphaModulateFixedEffect alphaModFixArray = CTEffectContainerImpl.this.getAlphaModFixArray(n);
                    CTEffectContainerImpl.this.setAlphaModFixArray(n, ctAlphaModulateFixedEffect);
                    return alphaModFixArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaModFix(n).set((XmlObject)ctAlphaModulateFixedEffect);
                }
                
                @Override
                public CTAlphaModulateFixedEffect remove(final int n) {
                    final CTAlphaModulateFixedEffect alphaModFixArray = CTEffectContainerImpl.this.getAlphaModFixArray(n);
                    CTEffectContainerImpl.this.removeAlphaModFix(n);
                    return alphaModFixArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaModFixArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAMODFIX$14, (List)list);
            final CTAlphaModulateFixedEffect[] array = new CTAlphaModulateFixedEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaModulateFixedEffect getAlphaModFixArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect = (CTAlphaModulateFixedEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAMODFIX$14, n);
            if (ctAlphaModulateFixedEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaModulateFixedEffect;
        }
    }
    
    public int sizeOfAlphaModFixArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAMODFIX$14);
        }
    }
    
    public void setAlphaModFixArray(final CTAlphaModulateFixedEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAMODFIX$14);
    }
    
    public void setAlphaModFixArray(final int n, final CTAlphaModulateFixedEffect ctAlphaModulateFixedEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaModulateFixedEffect, CTEffectContainerImpl.ALPHAMODFIX$14, n, (short)2);
    }
    
    public CTAlphaModulateFixedEffect insertNewAlphaModFix(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateFixedEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAMODFIX$14, n);
        }
    }
    
    public CTAlphaModulateFixedEffect addNewAlphaModFix() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaModulateFixedEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAMODFIX$14);
        }
    }
    
    public void removeAlphaModFix(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAMODFIX$14, n);
        }
    }
    
    public List<CTAlphaOutsetEffect> getAlphaOutsetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaOutsetList extends AbstractList<CTAlphaOutsetEffect>
            {
                @Override
                public CTAlphaOutsetEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaOutsetArray(n);
                }
                
                @Override
                public CTAlphaOutsetEffect set(final int n, final CTAlphaOutsetEffect ctAlphaOutsetEffect) {
                    final CTAlphaOutsetEffect alphaOutsetArray = CTEffectContainerImpl.this.getAlphaOutsetArray(n);
                    CTEffectContainerImpl.this.setAlphaOutsetArray(n, ctAlphaOutsetEffect);
                    return alphaOutsetArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaOutsetEffect ctAlphaOutsetEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaOutset(n).set((XmlObject)ctAlphaOutsetEffect);
                }
                
                @Override
                public CTAlphaOutsetEffect remove(final int n) {
                    final CTAlphaOutsetEffect alphaOutsetArray = CTEffectContainerImpl.this.getAlphaOutsetArray(n);
                    CTEffectContainerImpl.this.removeAlphaOutset(n);
                    return alphaOutsetArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaOutsetArray();
                }
            }
            return new AlphaOutsetList();
        }
    }
    
    @Deprecated
    public CTAlphaOutsetEffect[] getAlphaOutsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAOUTSET$16, (List)list);
            final CTAlphaOutsetEffect[] array = new CTAlphaOutsetEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaOutsetEffect getAlphaOutsetArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaOutsetEffect ctAlphaOutsetEffect = (CTAlphaOutsetEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAOUTSET$16, n);
            if (ctAlphaOutsetEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaOutsetEffect;
        }
    }
    
    public int sizeOfAlphaOutsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAOUTSET$16);
        }
    }
    
    public void setAlphaOutsetArray(final CTAlphaOutsetEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAOUTSET$16);
    }
    
    public void setAlphaOutsetArray(final int n, final CTAlphaOutsetEffect ctAlphaOutsetEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaOutsetEffect, CTEffectContainerImpl.ALPHAOUTSET$16, n, (short)2);
    }
    
    public CTAlphaOutsetEffect insertNewAlphaOutset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaOutsetEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAOUTSET$16, n);
        }
    }
    
    public CTAlphaOutsetEffect addNewAlphaOutset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaOutsetEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAOUTSET$16);
        }
    }
    
    public void removeAlphaOutset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAOUTSET$16, n);
        }
    }
    
    public List<CTAlphaReplaceEffect> getAlphaReplList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AlphaReplList extends AbstractList<CTAlphaReplaceEffect>
            {
                @Override
                public CTAlphaReplaceEffect get(final int n) {
                    return CTEffectContainerImpl.this.getAlphaReplArray(n);
                }
                
                @Override
                public CTAlphaReplaceEffect set(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
                    final CTAlphaReplaceEffect alphaReplArray = CTEffectContainerImpl.this.getAlphaReplArray(n);
                    CTEffectContainerImpl.this.setAlphaReplArray(n, ctAlphaReplaceEffect);
                    return alphaReplArray;
                }
                
                @Override
                public void add(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
                    CTEffectContainerImpl.this.insertNewAlphaRepl(n).set((XmlObject)ctAlphaReplaceEffect);
                }
                
                @Override
                public CTAlphaReplaceEffect remove(final int n) {
                    final CTAlphaReplaceEffect alphaReplArray = CTEffectContainerImpl.this.getAlphaReplArray(n);
                    CTEffectContainerImpl.this.removeAlphaRepl(n);
                    return alphaReplArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfAlphaReplArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.ALPHAREPL$18, (List)list);
            final CTAlphaReplaceEffect[] array = new CTAlphaReplaceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAlphaReplaceEffect getAlphaReplArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAlphaReplaceEffect ctAlphaReplaceEffect = (CTAlphaReplaceEffect)this.get_store().find_element_user(CTEffectContainerImpl.ALPHAREPL$18, n);
            if (ctAlphaReplaceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAlphaReplaceEffect;
        }
    }
    
    public int sizeOfAlphaReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.ALPHAREPL$18);
        }
    }
    
    public void setAlphaReplArray(final CTAlphaReplaceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.ALPHAREPL$18);
    }
    
    public void setAlphaReplArray(final int n, final CTAlphaReplaceEffect ctAlphaReplaceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctAlphaReplaceEffect, CTEffectContainerImpl.ALPHAREPL$18, n, (short)2);
    }
    
    public CTAlphaReplaceEffect insertNewAlphaRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaReplaceEffect)this.get_store().insert_element_user(CTEffectContainerImpl.ALPHAREPL$18, n);
        }
    }
    
    public CTAlphaReplaceEffect addNewAlphaRepl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAlphaReplaceEffect)this.get_store().add_element_user(CTEffectContainerImpl.ALPHAREPL$18);
        }
    }
    
    public void removeAlphaRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.ALPHAREPL$18, n);
        }
    }
    
    public List<CTBiLevelEffect> getBiLevelList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BiLevelList extends AbstractList<CTBiLevelEffect>
            {
                @Override
                public CTBiLevelEffect get(final int n) {
                    return CTEffectContainerImpl.this.getBiLevelArray(n);
                }
                
                @Override
                public CTBiLevelEffect set(final int n, final CTBiLevelEffect ctBiLevelEffect) {
                    final CTBiLevelEffect biLevelArray = CTEffectContainerImpl.this.getBiLevelArray(n);
                    CTEffectContainerImpl.this.setBiLevelArray(n, ctBiLevelEffect);
                    return biLevelArray;
                }
                
                @Override
                public void add(final int n, final CTBiLevelEffect ctBiLevelEffect) {
                    CTEffectContainerImpl.this.insertNewBiLevel(n).set((XmlObject)ctBiLevelEffect);
                }
                
                @Override
                public CTBiLevelEffect remove(final int n) {
                    final CTBiLevelEffect biLevelArray = CTEffectContainerImpl.this.getBiLevelArray(n);
                    CTEffectContainerImpl.this.removeBiLevel(n);
                    return biLevelArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfBiLevelArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.BILEVEL$20, (List)list);
            final CTBiLevelEffect[] array = new CTBiLevelEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBiLevelEffect getBiLevelArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBiLevelEffect ctBiLevelEffect = (CTBiLevelEffect)this.get_store().find_element_user(CTEffectContainerImpl.BILEVEL$20, n);
            if (ctBiLevelEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBiLevelEffect;
        }
    }
    
    public int sizeOfBiLevelArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.BILEVEL$20);
        }
    }
    
    public void setBiLevelArray(final CTBiLevelEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.BILEVEL$20);
    }
    
    public void setBiLevelArray(final int n, final CTBiLevelEffect ctBiLevelEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBiLevelEffect, CTEffectContainerImpl.BILEVEL$20, n, (short)2);
    }
    
    public CTBiLevelEffect insertNewBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBiLevelEffect)this.get_store().insert_element_user(CTEffectContainerImpl.BILEVEL$20, n);
        }
    }
    
    public CTBiLevelEffect addNewBiLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBiLevelEffect)this.get_store().add_element_user(CTEffectContainerImpl.BILEVEL$20);
        }
    }
    
    public void removeBiLevel(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.BILEVEL$20, n);
        }
    }
    
    public List<CTBlendEffect> getBlendList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlendList extends AbstractList<CTBlendEffect>
            {
                @Override
                public CTBlendEffect get(final int n) {
                    return CTEffectContainerImpl.this.getBlendArray(n);
                }
                
                @Override
                public CTBlendEffect set(final int n, final CTBlendEffect ctBlendEffect) {
                    final CTBlendEffect blendArray = CTEffectContainerImpl.this.getBlendArray(n);
                    CTEffectContainerImpl.this.setBlendArray(n, ctBlendEffect);
                    return blendArray;
                }
                
                @Override
                public void add(final int n, final CTBlendEffect ctBlendEffect) {
                    CTEffectContainerImpl.this.insertNewBlend(n).set((XmlObject)ctBlendEffect);
                }
                
                @Override
                public CTBlendEffect remove(final int n) {
                    final CTBlendEffect blendArray = CTEffectContainerImpl.this.getBlendArray(n);
                    CTEffectContainerImpl.this.removeBlend(n);
                    return blendArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfBlendArray();
                }
            }
            return new BlendList();
        }
    }
    
    @Deprecated
    public CTBlendEffect[] getBlendArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.BLEND$22, (List)list);
            final CTBlendEffect[] array = new CTBlendEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBlendEffect getBlendArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlendEffect ctBlendEffect = (CTBlendEffect)this.get_store().find_element_user(CTEffectContainerImpl.BLEND$22, n);
            if (ctBlendEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBlendEffect;
        }
    }
    
    public int sizeOfBlendArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.BLEND$22);
        }
    }
    
    public void setBlendArray(final CTBlendEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.BLEND$22);
    }
    
    public void setBlendArray(final int n, final CTBlendEffect ctBlendEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBlendEffect, CTEffectContainerImpl.BLEND$22, n, (short)2);
    }
    
    public CTBlendEffect insertNewBlend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlendEffect)this.get_store().insert_element_user(CTEffectContainerImpl.BLEND$22, n);
        }
    }
    
    public CTBlendEffect addNewBlend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlendEffect)this.get_store().add_element_user(CTEffectContainerImpl.BLEND$22);
        }
    }
    
    public void removeBlend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.BLEND$22, n);
        }
    }
    
    public List<CTBlurEffect> getBlurList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BlurList extends AbstractList<CTBlurEffect>
            {
                @Override
                public CTBlurEffect get(final int n) {
                    return CTEffectContainerImpl.this.getBlurArray(n);
                }
                
                @Override
                public CTBlurEffect set(final int n, final CTBlurEffect ctBlurEffect) {
                    final CTBlurEffect blurArray = CTEffectContainerImpl.this.getBlurArray(n);
                    CTEffectContainerImpl.this.setBlurArray(n, ctBlurEffect);
                    return blurArray;
                }
                
                @Override
                public void add(final int n, final CTBlurEffect ctBlurEffect) {
                    CTEffectContainerImpl.this.insertNewBlur(n).set((XmlObject)ctBlurEffect);
                }
                
                @Override
                public CTBlurEffect remove(final int n) {
                    final CTBlurEffect blurArray = CTEffectContainerImpl.this.getBlurArray(n);
                    CTEffectContainerImpl.this.removeBlur(n);
                    return blurArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfBlurArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.BLUR$24, (List)list);
            final CTBlurEffect[] array = new CTBlurEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBlurEffect getBlurArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBlurEffect ctBlurEffect = (CTBlurEffect)this.get_store().find_element_user(CTEffectContainerImpl.BLUR$24, n);
            if (ctBlurEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBlurEffect;
        }
    }
    
    public int sizeOfBlurArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.BLUR$24);
        }
    }
    
    public void setBlurArray(final CTBlurEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.BLUR$24);
    }
    
    public void setBlurArray(final int n, final CTBlurEffect ctBlurEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctBlurEffect, CTEffectContainerImpl.BLUR$24, n, (short)2);
    }
    
    public CTBlurEffect insertNewBlur(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlurEffect)this.get_store().insert_element_user(CTEffectContainerImpl.BLUR$24, n);
        }
    }
    
    public CTBlurEffect addNewBlur() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBlurEffect)this.get_store().add_element_user(CTEffectContainerImpl.BLUR$24);
        }
    }
    
    public void removeBlur(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.BLUR$24, n);
        }
    }
    
    public List<CTColorChangeEffect> getClrChangeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClrChangeList extends AbstractList<CTColorChangeEffect>
            {
                @Override
                public CTColorChangeEffect get(final int n) {
                    return CTEffectContainerImpl.this.getClrChangeArray(n);
                }
                
                @Override
                public CTColorChangeEffect set(final int n, final CTColorChangeEffect ctColorChangeEffect) {
                    final CTColorChangeEffect clrChangeArray = CTEffectContainerImpl.this.getClrChangeArray(n);
                    CTEffectContainerImpl.this.setClrChangeArray(n, ctColorChangeEffect);
                    return clrChangeArray;
                }
                
                @Override
                public void add(final int n, final CTColorChangeEffect ctColorChangeEffect) {
                    CTEffectContainerImpl.this.insertNewClrChange(n).set((XmlObject)ctColorChangeEffect);
                }
                
                @Override
                public CTColorChangeEffect remove(final int n) {
                    final CTColorChangeEffect clrChangeArray = CTEffectContainerImpl.this.getClrChangeArray(n);
                    CTEffectContainerImpl.this.removeClrChange(n);
                    return clrChangeArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfClrChangeArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.CLRCHANGE$26, (List)list);
            final CTColorChangeEffect[] array = new CTColorChangeEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColorChangeEffect getClrChangeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorChangeEffect ctColorChangeEffect = (CTColorChangeEffect)this.get_store().find_element_user(CTEffectContainerImpl.CLRCHANGE$26, n);
            if (ctColorChangeEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColorChangeEffect;
        }
    }
    
    public int sizeOfClrChangeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.CLRCHANGE$26);
        }
    }
    
    public void setClrChangeArray(final CTColorChangeEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.CLRCHANGE$26);
    }
    
    public void setClrChangeArray(final int n, final CTColorChangeEffect ctColorChangeEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctColorChangeEffect, CTEffectContainerImpl.CLRCHANGE$26, n, (short)2);
    }
    
    public CTColorChangeEffect insertNewClrChange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorChangeEffect)this.get_store().insert_element_user(CTEffectContainerImpl.CLRCHANGE$26, n);
        }
    }
    
    public CTColorChangeEffect addNewClrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorChangeEffect)this.get_store().add_element_user(CTEffectContainerImpl.CLRCHANGE$26);
        }
    }
    
    public void removeClrChange(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.CLRCHANGE$26, n);
        }
    }
    
    public List<CTColorReplaceEffect> getClrReplList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClrReplList extends AbstractList<CTColorReplaceEffect>
            {
                @Override
                public CTColorReplaceEffect get(final int n) {
                    return CTEffectContainerImpl.this.getClrReplArray(n);
                }
                
                @Override
                public CTColorReplaceEffect set(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
                    final CTColorReplaceEffect clrReplArray = CTEffectContainerImpl.this.getClrReplArray(n);
                    CTEffectContainerImpl.this.setClrReplArray(n, ctColorReplaceEffect);
                    return clrReplArray;
                }
                
                @Override
                public void add(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
                    CTEffectContainerImpl.this.insertNewClrRepl(n).set((XmlObject)ctColorReplaceEffect);
                }
                
                @Override
                public CTColorReplaceEffect remove(final int n) {
                    final CTColorReplaceEffect clrReplArray = CTEffectContainerImpl.this.getClrReplArray(n);
                    CTEffectContainerImpl.this.removeClrRepl(n);
                    return clrReplArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfClrReplArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.CLRREPL$28, (List)list);
            final CTColorReplaceEffect[] array = new CTColorReplaceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColorReplaceEffect getClrReplArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorReplaceEffect ctColorReplaceEffect = (CTColorReplaceEffect)this.get_store().find_element_user(CTEffectContainerImpl.CLRREPL$28, n);
            if (ctColorReplaceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColorReplaceEffect;
        }
    }
    
    public int sizeOfClrReplArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.CLRREPL$28);
        }
    }
    
    public void setClrReplArray(final CTColorReplaceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.CLRREPL$28);
    }
    
    public void setClrReplArray(final int n, final CTColorReplaceEffect ctColorReplaceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctColorReplaceEffect, CTEffectContainerImpl.CLRREPL$28, n, (short)2);
    }
    
    public CTColorReplaceEffect insertNewClrRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorReplaceEffect)this.get_store().insert_element_user(CTEffectContainerImpl.CLRREPL$28, n);
        }
    }
    
    public CTColorReplaceEffect addNewClrRepl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorReplaceEffect)this.get_store().add_element_user(CTEffectContainerImpl.CLRREPL$28);
        }
    }
    
    public void removeClrRepl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.CLRREPL$28, n);
        }
    }
    
    public List<CTDuotoneEffect> getDuotoneList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DuotoneList extends AbstractList<CTDuotoneEffect>
            {
                @Override
                public CTDuotoneEffect get(final int n) {
                    return CTEffectContainerImpl.this.getDuotoneArray(n);
                }
                
                @Override
                public CTDuotoneEffect set(final int n, final CTDuotoneEffect ctDuotoneEffect) {
                    final CTDuotoneEffect duotoneArray = CTEffectContainerImpl.this.getDuotoneArray(n);
                    CTEffectContainerImpl.this.setDuotoneArray(n, ctDuotoneEffect);
                    return duotoneArray;
                }
                
                @Override
                public void add(final int n, final CTDuotoneEffect ctDuotoneEffect) {
                    CTEffectContainerImpl.this.insertNewDuotone(n).set((XmlObject)ctDuotoneEffect);
                }
                
                @Override
                public CTDuotoneEffect remove(final int n) {
                    final CTDuotoneEffect duotoneArray = CTEffectContainerImpl.this.getDuotoneArray(n);
                    CTEffectContainerImpl.this.removeDuotone(n);
                    return duotoneArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfDuotoneArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.DUOTONE$30, (List)list);
            final CTDuotoneEffect[] array = new CTDuotoneEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDuotoneEffect getDuotoneArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDuotoneEffect ctDuotoneEffect = (CTDuotoneEffect)this.get_store().find_element_user(CTEffectContainerImpl.DUOTONE$30, n);
            if (ctDuotoneEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDuotoneEffect;
        }
    }
    
    public int sizeOfDuotoneArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.DUOTONE$30);
        }
    }
    
    public void setDuotoneArray(final CTDuotoneEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.DUOTONE$30);
    }
    
    public void setDuotoneArray(final int n, final CTDuotoneEffect ctDuotoneEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctDuotoneEffect, CTEffectContainerImpl.DUOTONE$30, n, (short)2);
    }
    
    public CTDuotoneEffect insertNewDuotone(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDuotoneEffect)this.get_store().insert_element_user(CTEffectContainerImpl.DUOTONE$30, n);
        }
    }
    
    public CTDuotoneEffect addNewDuotone() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDuotoneEffect)this.get_store().add_element_user(CTEffectContainerImpl.DUOTONE$30);
        }
    }
    
    public void removeDuotone(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.DUOTONE$30, n);
        }
    }
    
    public List<CTFillEffect> getFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillList extends AbstractList<CTFillEffect>
            {
                @Override
                public CTFillEffect get(final int n) {
                    return CTEffectContainerImpl.this.getFillArray(n);
                }
                
                @Override
                public CTFillEffect set(final int n, final CTFillEffect ctFillEffect) {
                    final CTFillEffect fillArray = CTEffectContainerImpl.this.getFillArray(n);
                    CTEffectContainerImpl.this.setFillArray(n, ctFillEffect);
                    return fillArray;
                }
                
                @Override
                public void add(final int n, final CTFillEffect ctFillEffect) {
                    CTEffectContainerImpl.this.insertNewFill(n).set((XmlObject)ctFillEffect);
                }
                
                @Override
                public CTFillEffect remove(final int n) {
                    final CTFillEffect fillArray = CTEffectContainerImpl.this.getFillArray(n);
                    CTEffectContainerImpl.this.removeFill(n);
                    return fillArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfFillArray();
                }
            }
            return new FillList();
        }
    }
    
    @Deprecated
    public CTFillEffect[] getFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.FILL$32, (List)list);
            final CTFillEffect[] array = new CTFillEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFillEffect getFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillEffect ctFillEffect = (CTFillEffect)this.get_store().find_element_user(CTEffectContainerImpl.FILL$32, n);
            if (ctFillEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFillEffect;
        }
    }
    
    public int sizeOfFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.FILL$32);
        }
    }
    
    public void setFillArray(final CTFillEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.FILL$32);
    }
    
    public void setFillArray(final int n, final CTFillEffect ctFillEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctFillEffect, CTEffectContainerImpl.FILL$32, n, (short)2);
    }
    
    public CTFillEffect insertNewFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillEffect)this.get_store().insert_element_user(CTEffectContainerImpl.FILL$32, n);
        }
    }
    
    public CTFillEffect addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillEffect)this.get_store().add_element_user(CTEffectContainerImpl.FILL$32);
        }
    }
    
    public void removeFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.FILL$32, n);
        }
    }
    
    public List<CTFillOverlayEffect> getFillOverlayList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillOverlayList extends AbstractList<CTFillOverlayEffect>
            {
                @Override
                public CTFillOverlayEffect get(final int n) {
                    return CTEffectContainerImpl.this.getFillOverlayArray(n);
                }
                
                @Override
                public CTFillOverlayEffect set(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
                    final CTFillOverlayEffect fillOverlayArray = CTEffectContainerImpl.this.getFillOverlayArray(n);
                    CTEffectContainerImpl.this.setFillOverlayArray(n, ctFillOverlayEffect);
                    return fillOverlayArray;
                }
                
                @Override
                public void add(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
                    CTEffectContainerImpl.this.insertNewFillOverlay(n).set((XmlObject)ctFillOverlayEffect);
                }
                
                @Override
                public CTFillOverlayEffect remove(final int n) {
                    final CTFillOverlayEffect fillOverlayArray = CTEffectContainerImpl.this.getFillOverlayArray(n);
                    CTEffectContainerImpl.this.removeFillOverlay(n);
                    return fillOverlayArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfFillOverlayArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.FILLOVERLAY$34, (List)list);
            final CTFillOverlayEffect[] array = new CTFillOverlayEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFillOverlayEffect getFillOverlayArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillOverlayEffect ctFillOverlayEffect = (CTFillOverlayEffect)this.get_store().find_element_user(CTEffectContainerImpl.FILLOVERLAY$34, n);
            if (ctFillOverlayEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFillOverlayEffect;
        }
    }
    
    public int sizeOfFillOverlayArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.FILLOVERLAY$34);
        }
    }
    
    public void setFillOverlayArray(final CTFillOverlayEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.FILLOVERLAY$34);
    }
    
    public void setFillOverlayArray(final int n, final CTFillOverlayEffect ctFillOverlayEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctFillOverlayEffect, CTEffectContainerImpl.FILLOVERLAY$34, n, (short)2);
    }
    
    public CTFillOverlayEffect insertNewFillOverlay(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillOverlayEffect)this.get_store().insert_element_user(CTEffectContainerImpl.FILLOVERLAY$34, n);
        }
    }
    
    public CTFillOverlayEffect addNewFillOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillOverlayEffect)this.get_store().add_element_user(CTEffectContainerImpl.FILLOVERLAY$34);
        }
    }
    
    public void removeFillOverlay(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.FILLOVERLAY$34, n);
        }
    }
    
    public List<CTGlowEffect> getGlowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GlowList extends AbstractList<CTGlowEffect>
            {
                @Override
                public CTGlowEffect get(final int n) {
                    return CTEffectContainerImpl.this.getGlowArray(n);
                }
                
                @Override
                public CTGlowEffect set(final int n, final CTGlowEffect ctGlowEffect) {
                    final CTGlowEffect glowArray = CTEffectContainerImpl.this.getGlowArray(n);
                    CTEffectContainerImpl.this.setGlowArray(n, ctGlowEffect);
                    return glowArray;
                }
                
                @Override
                public void add(final int n, final CTGlowEffect ctGlowEffect) {
                    CTEffectContainerImpl.this.insertNewGlow(n).set((XmlObject)ctGlowEffect);
                }
                
                @Override
                public CTGlowEffect remove(final int n) {
                    final CTGlowEffect glowArray = CTEffectContainerImpl.this.getGlowArray(n);
                    CTEffectContainerImpl.this.removeGlow(n);
                    return glowArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfGlowArray();
                }
            }
            return new GlowList();
        }
    }
    
    @Deprecated
    public CTGlowEffect[] getGlowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.GLOW$36, (List)list);
            final CTGlowEffect[] array = new CTGlowEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGlowEffect getGlowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGlowEffect ctGlowEffect = (CTGlowEffect)this.get_store().find_element_user(CTEffectContainerImpl.GLOW$36, n);
            if (ctGlowEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGlowEffect;
        }
    }
    
    public int sizeOfGlowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.GLOW$36);
        }
    }
    
    public void setGlowArray(final CTGlowEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.GLOW$36);
    }
    
    public void setGlowArray(final int n, final CTGlowEffect ctGlowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctGlowEffect, CTEffectContainerImpl.GLOW$36, n, (short)2);
    }
    
    public CTGlowEffect insertNewGlow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGlowEffect)this.get_store().insert_element_user(CTEffectContainerImpl.GLOW$36, n);
        }
    }
    
    public CTGlowEffect addNewGlow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGlowEffect)this.get_store().add_element_user(CTEffectContainerImpl.GLOW$36);
        }
    }
    
    public void removeGlow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.GLOW$36, n);
        }
    }
    
    public List<CTGrayscaleEffect> getGraysclList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GraysclList extends AbstractList<CTGrayscaleEffect>
            {
                @Override
                public CTGrayscaleEffect get(final int n) {
                    return CTEffectContainerImpl.this.getGraysclArray(n);
                }
                
                @Override
                public CTGrayscaleEffect set(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
                    final CTGrayscaleEffect graysclArray = CTEffectContainerImpl.this.getGraysclArray(n);
                    CTEffectContainerImpl.this.setGraysclArray(n, ctGrayscaleEffect);
                    return graysclArray;
                }
                
                @Override
                public void add(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
                    CTEffectContainerImpl.this.insertNewGrayscl(n).set((XmlObject)ctGrayscaleEffect);
                }
                
                @Override
                public CTGrayscaleEffect remove(final int n) {
                    final CTGrayscaleEffect graysclArray = CTEffectContainerImpl.this.getGraysclArray(n);
                    CTEffectContainerImpl.this.removeGrayscl(n);
                    return graysclArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfGraysclArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.GRAYSCL$38, (List)list);
            final CTGrayscaleEffect[] array = new CTGrayscaleEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGrayscaleEffect getGraysclArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrayscaleEffect ctGrayscaleEffect = (CTGrayscaleEffect)this.get_store().find_element_user(CTEffectContainerImpl.GRAYSCL$38, n);
            if (ctGrayscaleEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGrayscaleEffect;
        }
    }
    
    public int sizeOfGraysclArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.GRAYSCL$38);
        }
    }
    
    public void setGraysclArray(final CTGrayscaleEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.GRAYSCL$38);
    }
    
    public void setGraysclArray(final int n, final CTGrayscaleEffect ctGrayscaleEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctGrayscaleEffect, CTEffectContainerImpl.GRAYSCL$38, n, (short)2);
    }
    
    public CTGrayscaleEffect insertNewGrayscl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleEffect)this.get_store().insert_element_user(CTEffectContainerImpl.GRAYSCL$38, n);
        }
    }
    
    public CTGrayscaleEffect addNewGrayscl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrayscaleEffect)this.get_store().add_element_user(CTEffectContainerImpl.GRAYSCL$38);
        }
    }
    
    public void removeGrayscl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.GRAYSCL$38, n);
        }
    }
    
    public List<CTHSLEffect> getHslList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HslList extends AbstractList<CTHSLEffect>
            {
                @Override
                public CTHSLEffect get(final int n) {
                    return CTEffectContainerImpl.this.getHslArray(n);
                }
                
                @Override
                public CTHSLEffect set(final int n, final CTHSLEffect cthslEffect) {
                    final CTHSLEffect hslArray = CTEffectContainerImpl.this.getHslArray(n);
                    CTEffectContainerImpl.this.setHslArray(n, cthslEffect);
                    return hslArray;
                }
                
                @Override
                public void add(final int n, final CTHSLEffect cthslEffect) {
                    CTEffectContainerImpl.this.insertNewHsl(n).set((XmlObject)cthslEffect);
                }
                
                @Override
                public CTHSLEffect remove(final int n) {
                    final CTHSLEffect hslArray = CTEffectContainerImpl.this.getHslArray(n);
                    CTEffectContainerImpl.this.removeHsl(n);
                    return hslArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfHslArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.HSL$40, (List)list);
            final CTHSLEffect[] array = new CTHSLEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHSLEffect getHslArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHSLEffect cthslEffect = (CTHSLEffect)this.get_store().find_element_user(CTEffectContainerImpl.HSL$40, n);
            if (cthslEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return cthslEffect;
        }
    }
    
    public int sizeOfHslArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.HSL$40);
        }
    }
    
    public void setHslArray(final CTHSLEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.HSL$40);
    }
    
    public void setHslArray(final int n, final CTHSLEffect cthslEffect) {
        this.generatedSetterHelperImpl((XmlObject)cthslEffect, CTEffectContainerImpl.HSL$40, n, (short)2);
    }
    
    public CTHSLEffect insertNewHsl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHSLEffect)this.get_store().insert_element_user(CTEffectContainerImpl.HSL$40, n);
        }
    }
    
    public CTHSLEffect addNewHsl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHSLEffect)this.get_store().add_element_user(CTEffectContainerImpl.HSL$40);
        }
    }
    
    public void removeHsl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.HSL$40, n);
        }
    }
    
    public List<CTInnerShadowEffect> getInnerShdwList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class InnerShdwList extends AbstractList<CTInnerShadowEffect>
            {
                @Override
                public CTInnerShadowEffect get(final int n) {
                    return CTEffectContainerImpl.this.getInnerShdwArray(n);
                }
                
                @Override
                public CTInnerShadowEffect set(final int n, final CTInnerShadowEffect ctInnerShadowEffect) {
                    final CTInnerShadowEffect innerShdwArray = CTEffectContainerImpl.this.getInnerShdwArray(n);
                    CTEffectContainerImpl.this.setInnerShdwArray(n, ctInnerShadowEffect);
                    return innerShdwArray;
                }
                
                @Override
                public void add(final int n, final CTInnerShadowEffect ctInnerShadowEffect) {
                    CTEffectContainerImpl.this.insertNewInnerShdw(n).set((XmlObject)ctInnerShadowEffect);
                }
                
                @Override
                public CTInnerShadowEffect remove(final int n) {
                    final CTInnerShadowEffect innerShdwArray = CTEffectContainerImpl.this.getInnerShdwArray(n);
                    CTEffectContainerImpl.this.removeInnerShdw(n);
                    return innerShdwArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfInnerShdwArray();
                }
            }
            return new InnerShdwList();
        }
    }
    
    @Deprecated
    public CTInnerShadowEffect[] getInnerShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.INNERSHDW$42, (List)list);
            final CTInnerShadowEffect[] array = new CTInnerShadowEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTInnerShadowEffect getInnerShdwArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTInnerShadowEffect ctInnerShadowEffect = (CTInnerShadowEffect)this.get_store().find_element_user(CTEffectContainerImpl.INNERSHDW$42, n);
            if (ctInnerShadowEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctInnerShadowEffect;
        }
    }
    
    public int sizeOfInnerShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.INNERSHDW$42);
        }
    }
    
    public void setInnerShdwArray(final CTInnerShadowEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.INNERSHDW$42);
    }
    
    public void setInnerShdwArray(final int n, final CTInnerShadowEffect ctInnerShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctInnerShadowEffect, CTEffectContainerImpl.INNERSHDW$42, n, (short)2);
    }
    
    public CTInnerShadowEffect insertNewInnerShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInnerShadowEffect)this.get_store().insert_element_user(CTEffectContainerImpl.INNERSHDW$42, n);
        }
    }
    
    public CTInnerShadowEffect addNewInnerShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTInnerShadowEffect)this.get_store().add_element_user(CTEffectContainerImpl.INNERSHDW$42);
        }
    }
    
    public void removeInnerShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.INNERSHDW$42, n);
        }
    }
    
    public List<CTLuminanceEffect> getLumList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LumList extends AbstractList<CTLuminanceEffect>
            {
                @Override
                public CTLuminanceEffect get(final int n) {
                    return CTEffectContainerImpl.this.getLumArray(n);
                }
                
                @Override
                public CTLuminanceEffect set(final int n, final CTLuminanceEffect ctLuminanceEffect) {
                    final CTLuminanceEffect lumArray = CTEffectContainerImpl.this.getLumArray(n);
                    CTEffectContainerImpl.this.setLumArray(n, ctLuminanceEffect);
                    return lumArray;
                }
                
                @Override
                public void add(final int n, final CTLuminanceEffect ctLuminanceEffect) {
                    CTEffectContainerImpl.this.insertNewLum(n).set((XmlObject)ctLuminanceEffect);
                }
                
                @Override
                public CTLuminanceEffect remove(final int n) {
                    final CTLuminanceEffect lumArray = CTEffectContainerImpl.this.getLumArray(n);
                    CTEffectContainerImpl.this.removeLum(n);
                    return lumArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfLumArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.LUM$44, (List)list);
            final CTLuminanceEffect[] array = new CTLuminanceEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLuminanceEffect getLumArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLuminanceEffect ctLuminanceEffect = (CTLuminanceEffect)this.get_store().find_element_user(CTEffectContainerImpl.LUM$44, n);
            if (ctLuminanceEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLuminanceEffect;
        }
    }
    
    public int sizeOfLumArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.LUM$44);
        }
    }
    
    public void setLumArray(final CTLuminanceEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.LUM$44);
    }
    
    public void setLumArray(final int n, final CTLuminanceEffect ctLuminanceEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctLuminanceEffect, CTEffectContainerImpl.LUM$44, n, (short)2);
    }
    
    public CTLuminanceEffect insertNewLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLuminanceEffect)this.get_store().insert_element_user(CTEffectContainerImpl.LUM$44, n);
        }
    }
    
    public CTLuminanceEffect addNewLum() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLuminanceEffect)this.get_store().add_element_user(CTEffectContainerImpl.LUM$44);
        }
    }
    
    public void removeLum(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.LUM$44, n);
        }
    }
    
    public List<CTOuterShadowEffect> getOuterShdwList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OuterShdwList extends AbstractList<CTOuterShadowEffect>
            {
                @Override
                public CTOuterShadowEffect get(final int n) {
                    return CTEffectContainerImpl.this.getOuterShdwArray(n);
                }
                
                @Override
                public CTOuterShadowEffect set(final int n, final CTOuterShadowEffect ctOuterShadowEffect) {
                    final CTOuterShadowEffect outerShdwArray = CTEffectContainerImpl.this.getOuterShdwArray(n);
                    CTEffectContainerImpl.this.setOuterShdwArray(n, ctOuterShadowEffect);
                    return outerShdwArray;
                }
                
                @Override
                public void add(final int n, final CTOuterShadowEffect ctOuterShadowEffect) {
                    CTEffectContainerImpl.this.insertNewOuterShdw(n).set((XmlObject)ctOuterShadowEffect);
                }
                
                @Override
                public CTOuterShadowEffect remove(final int n) {
                    final CTOuterShadowEffect outerShdwArray = CTEffectContainerImpl.this.getOuterShdwArray(n);
                    CTEffectContainerImpl.this.removeOuterShdw(n);
                    return outerShdwArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfOuterShdwArray();
                }
            }
            return new OuterShdwList();
        }
    }
    
    @Deprecated
    public CTOuterShadowEffect[] getOuterShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.OUTERSHDW$46, (List)list);
            final CTOuterShadowEffect[] array = new CTOuterShadowEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOuterShadowEffect getOuterShdwArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOuterShadowEffect ctOuterShadowEffect = (CTOuterShadowEffect)this.get_store().find_element_user(CTEffectContainerImpl.OUTERSHDW$46, n);
            if (ctOuterShadowEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOuterShadowEffect;
        }
    }
    
    public int sizeOfOuterShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.OUTERSHDW$46);
        }
    }
    
    public void setOuterShdwArray(final CTOuterShadowEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.OUTERSHDW$46);
    }
    
    public void setOuterShdwArray(final int n, final CTOuterShadowEffect ctOuterShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctOuterShadowEffect, CTEffectContainerImpl.OUTERSHDW$46, n, (short)2);
    }
    
    public CTOuterShadowEffect insertNewOuterShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOuterShadowEffect)this.get_store().insert_element_user(CTEffectContainerImpl.OUTERSHDW$46, n);
        }
    }
    
    public CTOuterShadowEffect addNewOuterShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOuterShadowEffect)this.get_store().add_element_user(CTEffectContainerImpl.OUTERSHDW$46);
        }
    }
    
    public void removeOuterShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.OUTERSHDW$46, n);
        }
    }
    
    public List<CTPresetShadowEffect> getPrstShdwList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PrstShdwList extends AbstractList<CTPresetShadowEffect>
            {
                @Override
                public CTPresetShadowEffect get(final int n) {
                    return CTEffectContainerImpl.this.getPrstShdwArray(n);
                }
                
                @Override
                public CTPresetShadowEffect set(final int n, final CTPresetShadowEffect ctPresetShadowEffect) {
                    final CTPresetShadowEffect prstShdwArray = CTEffectContainerImpl.this.getPrstShdwArray(n);
                    CTEffectContainerImpl.this.setPrstShdwArray(n, ctPresetShadowEffect);
                    return prstShdwArray;
                }
                
                @Override
                public void add(final int n, final CTPresetShadowEffect ctPresetShadowEffect) {
                    CTEffectContainerImpl.this.insertNewPrstShdw(n).set((XmlObject)ctPresetShadowEffect);
                }
                
                @Override
                public CTPresetShadowEffect remove(final int n) {
                    final CTPresetShadowEffect prstShdwArray = CTEffectContainerImpl.this.getPrstShdwArray(n);
                    CTEffectContainerImpl.this.removePrstShdw(n);
                    return prstShdwArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfPrstShdwArray();
                }
            }
            return new PrstShdwList();
        }
    }
    
    @Deprecated
    public CTPresetShadowEffect[] getPrstShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.PRSTSHDW$48, (List)list);
            final CTPresetShadowEffect[] array = new CTPresetShadowEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPresetShadowEffect getPrstShdwArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPresetShadowEffect ctPresetShadowEffect = (CTPresetShadowEffect)this.get_store().find_element_user(CTEffectContainerImpl.PRSTSHDW$48, n);
            if (ctPresetShadowEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPresetShadowEffect;
        }
    }
    
    public int sizeOfPrstShdwArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.PRSTSHDW$48);
        }
    }
    
    public void setPrstShdwArray(final CTPresetShadowEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.PRSTSHDW$48);
    }
    
    public void setPrstShdwArray(final int n, final CTPresetShadowEffect ctPresetShadowEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctPresetShadowEffect, CTEffectContainerImpl.PRSTSHDW$48, n, (short)2);
    }
    
    public CTPresetShadowEffect insertNewPrstShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetShadowEffect)this.get_store().insert_element_user(CTEffectContainerImpl.PRSTSHDW$48, n);
        }
    }
    
    public CTPresetShadowEffect addNewPrstShdw() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPresetShadowEffect)this.get_store().add_element_user(CTEffectContainerImpl.PRSTSHDW$48);
        }
    }
    
    public void removePrstShdw(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.PRSTSHDW$48, n);
        }
    }
    
    public List<CTReflectionEffect> getReflectionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ReflectionList extends AbstractList<CTReflectionEffect>
            {
                @Override
                public CTReflectionEffect get(final int n) {
                    return CTEffectContainerImpl.this.getReflectionArray(n);
                }
                
                @Override
                public CTReflectionEffect set(final int n, final CTReflectionEffect ctReflectionEffect) {
                    final CTReflectionEffect reflectionArray = CTEffectContainerImpl.this.getReflectionArray(n);
                    CTEffectContainerImpl.this.setReflectionArray(n, ctReflectionEffect);
                    return reflectionArray;
                }
                
                @Override
                public void add(final int n, final CTReflectionEffect ctReflectionEffect) {
                    CTEffectContainerImpl.this.insertNewReflection(n).set((XmlObject)ctReflectionEffect);
                }
                
                @Override
                public CTReflectionEffect remove(final int n) {
                    final CTReflectionEffect reflectionArray = CTEffectContainerImpl.this.getReflectionArray(n);
                    CTEffectContainerImpl.this.removeReflection(n);
                    return reflectionArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfReflectionArray();
                }
            }
            return new ReflectionList();
        }
    }
    
    @Deprecated
    public CTReflectionEffect[] getReflectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.REFLECTION$50, (List)list);
            final CTReflectionEffect[] array = new CTReflectionEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTReflectionEffect getReflectionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTReflectionEffect ctReflectionEffect = (CTReflectionEffect)this.get_store().find_element_user(CTEffectContainerImpl.REFLECTION$50, n);
            if (ctReflectionEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctReflectionEffect;
        }
    }
    
    public int sizeOfReflectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.REFLECTION$50);
        }
    }
    
    public void setReflectionArray(final CTReflectionEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.REFLECTION$50);
    }
    
    public void setReflectionArray(final int n, final CTReflectionEffect ctReflectionEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctReflectionEffect, CTEffectContainerImpl.REFLECTION$50, n, (short)2);
    }
    
    public CTReflectionEffect insertNewReflection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTReflectionEffect)this.get_store().insert_element_user(CTEffectContainerImpl.REFLECTION$50, n);
        }
    }
    
    public CTReflectionEffect addNewReflection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTReflectionEffect)this.get_store().add_element_user(CTEffectContainerImpl.REFLECTION$50);
        }
    }
    
    public void removeReflection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.REFLECTION$50, n);
        }
    }
    
    public List<CTRelativeOffsetEffect> getRelOffList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RelOffList extends AbstractList<CTRelativeOffsetEffect>
            {
                @Override
                public CTRelativeOffsetEffect get(final int n) {
                    return CTEffectContainerImpl.this.getRelOffArray(n);
                }
                
                @Override
                public CTRelativeOffsetEffect set(final int n, final CTRelativeOffsetEffect ctRelativeOffsetEffect) {
                    final CTRelativeOffsetEffect relOffArray = CTEffectContainerImpl.this.getRelOffArray(n);
                    CTEffectContainerImpl.this.setRelOffArray(n, ctRelativeOffsetEffect);
                    return relOffArray;
                }
                
                @Override
                public void add(final int n, final CTRelativeOffsetEffect ctRelativeOffsetEffect) {
                    CTEffectContainerImpl.this.insertNewRelOff(n).set((XmlObject)ctRelativeOffsetEffect);
                }
                
                @Override
                public CTRelativeOffsetEffect remove(final int n) {
                    final CTRelativeOffsetEffect relOffArray = CTEffectContainerImpl.this.getRelOffArray(n);
                    CTEffectContainerImpl.this.removeRelOff(n);
                    return relOffArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfRelOffArray();
                }
            }
            return new RelOffList();
        }
    }
    
    @Deprecated
    public CTRelativeOffsetEffect[] getRelOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.RELOFF$52, (List)list);
            final CTRelativeOffsetEffect[] array = new CTRelativeOffsetEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRelativeOffsetEffect getRelOffArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelativeOffsetEffect ctRelativeOffsetEffect = (CTRelativeOffsetEffect)this.get_store().find_element_user(CTEffectContainerImpl.RELOFF$52, n);
            if (ctRelativeOffsetEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRelativeOffsetEffect;
        }
    }
    
    public int sizeOfRelOffArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.RELOFF$52);
        }
    }
    
    public void setRelOffArray(final CTRelativeOffsetEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.RELOFF$52);
    }
    
    public void setRelOffArray(final int n, final CTRelativeOffsetEffect ctRelativeOffsetEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctRelativeOffsetEffect, CTEffectContainerImpl.RELOFF$52, n, (short)2);
    }
    
    public CTRelativeOffsetEffect insertNewRelOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeOffsetEffect)this.get_store().insert_element_user(CTEffectContainerImpl.RELOFF$52, n);
        }
    }
    
    public CTRelativeOffsetEffect addNewRelOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelativeOffsetEffect)this.get_store().add_element_user(CTEffectContainerImpl.RELOFF$52);
        }
    }
    
    public void removeRelOff(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.RELOFF$52, n);
        }
    }
    
    public List<CTSoftEdgesEffect> getSoftEdgeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SoftEdgeList extends AbstractList<CTSoftEdgesEffect>
            {
                @Override
                public CTSoftEdgesEffect get(final int n) {
                    return CTEffectContainerImpl.this.getSoftEdgeArray(n);
                }
                
                @Override
                public CTSoftEdgesEffect set(final int n, final CTSoftEdgesEffect ctSoftEdgesEffect) {
                    final CTSoftEdgesEffect softEdgeArray = CTEffectContainerImpl.this.getSoftEdgeArray(n);
                    CTEffectContainerImpl.this.setSoftEdgeArray(n, ctSoftEdgesEffect);
                    return softEdgeArray;
                }
                
                @Override
                public void add(final int n, final CTSoftEdgesEffect ctSoftEdgesEffect) {
                    CTEffectContainerImpl.this.insertNewSoftEdge(n).set((XmlObject)ctSoftEdgesEffect);
                }
                
                @Override
                public CTSoftEdgesEffect remove(final int n) {
                    final CTSoftEdgesEffect softEdgeArray = CTEffectContainerImpl.this.getSoftEdgeArray(n);
                    CTEffectContainerImpl.this.removeSoftEdge(n);
                    return softEdgeArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfSoftEdgeArray();
                }
            }
            return new SoftEdgeList();
        }
    }
    
    @Deprecated
    public CTSoftEdgesEffect[] getSoftEdgeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.SOFTEDGE$54, (List)list);
            final CTSoftEdgesEffect[] array = new CTSoftEdgesEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSoftEdgesEffect getSoftEdgeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSoftEdgesEffect ctSoftEdgesEffect = (CTSoftEdgesEffect)this.get_store().find_element_user(CTEffectContainerImpl.SOFTEDGE$54, n);
            if (ctSoftEdgesEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSoftEdgesEffect;
        }
    }
    
    public int sizeOfSoftEdgeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.SOFTEDGE$54);
        }
    }
    
    public void setSoftEdgeArray(final CTSoftEdgesEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.SOFTEDGE$54);
    }
    
    public void setSoftEdgeArray(final int n, final CTSoftEdgesEffect ctSoftEdgesEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctSoftEdgesEffect, CTEffectContainerImpl.SOFTEDGE$54, n, (short)2);
    }
    
    public CTSoftEdgesEffect insertNewSoftEdge(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSoftEdgesEffect)this.get_store().insert_element_user(CTEffectContainerImpl.SOFTEDGE$54, n);
        }
    }
    
    public CTSoftEdgesEffect addNewSoftEdge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSoftEdgesEffect)this.get_store().add_element_user(CTEffectContainerImpl.SOFTEDGE$54);
        }
    }
    
    public void removeSoftEdge(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.SOFTEDGE$54, n);
        }
    }
    
    public List<CTTintEffect> getTintList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TintList extends AbstractList<CTTintEffect>
            {
                @Override
                public CTTintEffect get(final int n) {
                    return CTEffectContainerImpl.this.getTintArray(n);
                }
                
                @Override
                public CTTintEffect set(final int n, final CTTintEffect ctTintEffect) {
                    final CTTintEffect tintArray = CTEffectContainerImpl.this.getTintArray(n);
                    CTEffectContainerImpl.this.setTintArray(n, ctTintEffect);
                    return tintArray;
                }
                
                @Override
                public void add(final int n, final CTTintEffect ctTintEffect) {
                    CTEffectContainerImpl.this.insertNewTint(n).set((XmlObject)ctTintEffect);
                }
                
                @Override
                public CTTintEffect remove(final int n) {
                    final CTTintEffect tintArray = CTEffectContainerImpl.this.getTintArray(n);
                    CTEffectContainerImpl.this.removeTint(n);
                    return tintArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfTintArray();
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
            this.get_store().find_all_element_users(CTEffectContainerImpl.TINT$56, (List)list);
            final CTTintEffect[] array = new CTTintEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTintEffect getTintArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTintEffect ctTintEffect = (CTTintEffect)this.get_store().find_element_user(CTEffectContainerImpl.TINT$56, n);
            if (ctTintEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTintEffect;
        }
    }
    
    public int sizeOfTintArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.TINT$56);
        }
    }
    
    public void setTintArray(final CTTintEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.TINT$56);
    }
    
    public void setTintArray(final int n, final CTTintEffect ctTintEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctTintEffect, CTEffectContainerImpl.TINT$56, n, (short)2);
    }
    
    public CTTintEffect insertNewTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTintEffect)this.get_store().insert_element_user(CTEffectContainerImpl.TINT$56, n);
        }
    }
    
    public CTTintEffect addNewTint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTintEffect)this.get_store().add_element_user(CTEffectContainerImpl.TINT$56);
        }
    }
    
    public void removeTint(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.TINT$56, n);
        }
    }
    
    public List<CTTransformEffect> getXfrmList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class XfrmList extends AbstractList<CTTransformEffect>
            {
                @Override
                public CTTransformEffect get(final int n) {
                    return CTEffectContainerImpl.this.getXfrmArray(n);
                }
                
                @Override
                public CTTransformEffect set(final int n, final CTTransformEffect ctTransformEffect) {
                    final CTTransformEffect xfrmArray = CTEffectContainerImpl.this.getXfrmArray(n);
                    CTEffectContainerImpl.this.setXfrmArray(n, ctTransformEffect);
                    return xfrmArray;
                }
                
                @Override
                public void add(final int n, final CTTransformEffect ctTransformEffect) {
                    CTEffectContainerImpl.this.insertNewXfrm(n).set((XmlObject)ctTransformEffect);
                }
                
                @Override
                public CTTransformEffect remove(final int n) {
                    final CTTransformEffect xfrmArray = CTEffectContainerImpl.this.getXfrmArray(n);
                    CTEffectContainerImpl.this.removeXfrm(n);
                    return xfrmArray;
                }
                
                @Override
                public int size() {
                    return CTEffectContainerImpl.this.sizeOfXfrmArray();
                }
            }
            return new XfrmList();
        }
    }
    
    @Deprecated
    public CTTransformEffect[] getXfrmArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTEffectContainerImpl.XFRM$58, (List)list);
            final CTTransformEffect[] array = new CTTransformEffect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTransformEffect getXfrmArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTransformEffect ctTransformEffect = (CTTransformEffect)this.get_store().find_element_user(CTEffectContainerImpl.XFRM$58, n);
            if (ctTransformEffect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTransformEffect;
        }
    }
    
    public int sizeOfXfrmArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTEffectContainerImpl.XFRM$58);
        }
    }
    
    public void setXfrmArray(final CTTransformEffect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTEffectContainerImpl.XFRM$58);
    }
    
    public void setXfrmArray(final int n, final CTTransformEffect ctTransformEffect) {
        this.generatedSetterHelperImpl((XmlObject)ctTransformEffect, CTEffectContainerImpl.XFRM$58, n, (short)2);
    }
    
    public CTTransformEffect insertNewXfrm(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTransformEffect)this.get_store().insert_element_user(CTEffectContainerImpl.XFRM$58, n);
        }
    }
    
    public CTTransformEffect addNewXfrm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTransformEffect)this.get_store().add_element_user(CTEffectContainerImpl.XFRM$58);
        }
    }
    
    public void removeXfrm(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTEffectContainerImpl.XFRM$58, n);
        }
    }
    
    public STEffectContainerType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEffectContainerImpl.TYPE$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTEffectContainerImpl.TYPE$60);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STEffectContainerType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STEffectContainerType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEffectContainerType stEffectContainerType = (STEffectContainerType)this.get_store().find_attribute_user(CTEffectContainerImpl.TYPE$60);
            if (stEffectContainerType == null) {
                stEffectContainerType = (STEffectContainerType)this.get_default_attribute_value(CTEffectContainerImpl.TYPE$60);
            }
            return stEffectContainerType;
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTEffectContainerImpl.TYPE$60) != null;
        }
    }
    
    public void setType(final STEffectContainerType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEffectContainerImpl.TYPE$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTEffectContainerImpl.TYPE$60);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STEffectContainerType stEffectContainerType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEffectContainerType stEffectContainerType2 = (STEffectContainerType)this.get_store().find_attribute_user(CTEffectContainerImpl.TYPE$60);
            if (stEffectContainerType2 == null) {
                stEffectContainerType2 = (STEffectContainerType)this.get_store().add_attribute_user(CTEffectContainerImpl.TYPE$60);
            }
            stEffectContainerType2.set((XmlObject)stEffectContainerType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTEffectContainerImpl.TYPE$60);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEffectContainerImpl.NAME$62);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlToken xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlToken)this.get_store().find_attribute_user(CTEffectContainerImpl.NAME$62);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTEffectContainerImpl.NAME$62) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTEffectContainerImpl.NAME$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTEffectContainerImpl.NAME$62);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlToken xmlToken) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken xmlToken2 = (XmlToken)this.get_store().find_attribute_user(CTEffectContainerImpl.NAME$62);
            if (xmlToken2 == null) {
                xmlToken2 = (XmlToken)this.get_store().add_attribute_user(CTEffectContainerImpl.NAME$62);
            }
            xmlToken2.set((XmlObject)xmlToken);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTEffectContainerImpl.NAME$62);
        }
    }
    
    static {
        CONT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cont");
        EFFECT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effect");
        ALPHABILEVEL$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaBiLevel");
        ALPHACEILING$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaCeiling");
        ALPHAFLOOR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaFloor");
        ALPHAINV$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaInv");
        ALPHAMOD$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaMod");
        ALPHAMODFIX$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaModFix");
        ALPHAOUTSET$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaOutset");
        ALPHAREPL$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "alphaRepl");
        BILEVEL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "biLevel");
        BLEND$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blend");
        BLUR$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "blur");
        CLRCHANGE$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrChange");
        CLRREPL$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "clrRepl");
        DUOTONE$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "duotone");
        FILL$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fill");
        FILLOVERLAY$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillOverlay");
        GLOW$36 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "glow");
        GRAYSCL$38 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "grayscl");
        HSL$40 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hsl");
        INNERSHDW$42 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "innerShdw");
        LUM$44 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lum");
        OUTERSHDW$46 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "outerShdw");
        PRSTSHDW$48 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "prstShdw");
        REFLECTION$50 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "reflection");
        RELOFF$52 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "relOff");
        SOFTEDGE$54 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "softEdge");
        TINT$56 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tint");
        XFRM$58 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "xfrm");
        TYPE$60 = new QName("", "type");
        NAME$62 = new QName("", "name");
    }
}
