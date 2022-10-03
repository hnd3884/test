package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.office.office.STTrueFalseBlank;
import com.microsoft.schemas.office.office.STBWMode;
import com.microsoft.schemas.office.office.STConnectorType;
import com.microsoft.schemas.vml.STColorType;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.office.STHrAlign;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.StringEnumAbstractBase;
import com.microsoft.schemas.vml.STTrueFalse;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.office.office.CTComplex;
import com.microsoft.schemas.office.powerpoint.CTRel;
import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.word.CTBorder;
import com.microsoft.schemas.office.word.CTAnchorLock;
import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.office.office.CTClipPath;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.CTCallout;
import com.microsoft.schemas.office.office.CTExtrusion;
import com.microsoft.schemas.office.office.CTSkew;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.CTTextbox;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTStroke;
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTFormulas;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import com.microsoft.schemas.vml.CTPath;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.vml.CTShapetype;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapetypeImpl extends XmlComplexContentImpl implements CTShapetype
{
    private static final long serialVersionUID = 1L;
    private static final QName PATH$0;
    private static final QName FORMULAS$2;
    private static final QName HANDLES$4;
    private static final QName FILL$6;
    private static final QName STROKE$8;
    private static final QName SHADOW$10;
    private static final QName TEXTBOX$12;
    private static final QName TEXTPATH$14;
    private static final QName IMAGEDATA$16;
    private static final QName SKEW$18;
    private static final QName EXTRUSION$20;
    private static final QName CALLOUT$22;
    private static final QName LOCK$24;
    private static final QName CLIPPATH$26;
    private static final QName SIGNATURELINE$28;
    private static final QName WRAP$30;
    private static final QName ANCHORLOCK$32;
    private static final QName BORDERTOP$34;
    private static final QName BORDERBOTTOM$36;
    private static final QName BORDERLEFT$38;
    private static final QName BORDERRIGHT$40;
    private static final QName CLIENTDATA$42;
    private static final QName TEXTDATA$44;
    private static final QName COMPLEX$46;
    private static final QName ID$48;
    private static final QName STYLE$50;
    private static final QName HREF$52;
    private static final QName TARGET$54;
    private static final QName CLASS1$56;
    private static final QName TITLE$58;
    private static final QName ALT$60;
    private static final QName COORDSIZE$62;
    private static final QName COORDORIGIN$64;
    private static final QName WRAPCOORDS$66;
    private static final QName PRINT$68;
    private static final QName SPID$70;
    private static final QName ONED$72;
    private static final QName REGROUPID$74;
    private static final QName DOUBLECLICKNOTIFY$76;
    private static final QName BUTTON$78;
    private static final QName USERHIDDEN$80;
    private static final QName BULLET$82;
    private static final QName HR$84;
    private static final QName HRSTD$86;
    private static final QName HRNOSHADE$88;
    private static final QName HRPCT$90;
    private static final QName HRALIGN$92;
    private static final QName ALLOWINCELL$94;
    private static final QName ALLOWOVERLAP$96;
    private static final QName USERDRAWN$98;
    private static final QName BORDERTOPCOLOR$100;
    private static final QName BORDERLEFTCOLOR$102;
    private static final QName BORDERBOTTOMCOLOR$104;
    private static final QName BORDERRIGHTCOLOR$106;
    private static final QName DGMLAYOUT$108;
    private static final QName DGMNODEKIND$110;
    private static final QName DGMLAYOUTMRU$112;
    private static final QName INSETMODE$114;
    private static final QName CHROMAKEY$116;
    private static final QName FILLED$118;
    private static final QName FILLCOLOR$120;
    private static final QName OPACITY$122;
    private static final QName STROKED$124;
    private static final QName STROKECOLOR$126;
    private static final QName STROKEWEIGHT$128;
    private static final QName INSETPEN$130;
    private static final QName SPT$132;
    private static final QName CONNECTORTYPE$134;
    private static final QName BWMODE$136;
    private static final QName BWPURE$138;
    private static final QName BWNORMAL$140;
    private static final QName FORCEDASH$142;
    private static final QName OLEICON$144;
    private static final QName OLE$146;
    private static final QName PREFERRELATIVE$148;
    private static final QName CLIPTOWRAP$150;
    private static final QName CLIP$152;
    private static final QName ADJ$154;
    private static final QName PATH2$156;
    private static final QName MASTER$158;
    
    public CTShapetypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPath> getPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PathList extends AbstractList<CTPath>
            {
                @Override
                public CTPath get(final int n) {
                    return CTShapetypeImpl.this.getPathArray(n);
                }
                
                @Override
                public CTPath set(final int n, final CTPath ctPath) {
                    final CTPath pathArray = CTShapetypeImpl.this.getPathArray(n);
                    CTShapetypeImpl.this.setPathArray(n, ctPath);
                    return pathArray;
                }
                
                @Override
                public void add(final int n, final CTPath ctPath) {
                    CTShapetypeImpl.this.insertNewPath(n).set((XmlObject)ctPath);
                }
                
                @Override
                public CTPath remove(final int n) {
                    final CTPath pathArray = CTShapetypeImpl.this.getPathArray(n);
                    CTShapetypeImpl.this.removePath(n);
                    return pathArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfPathArray();
                }
            }
            return new PathList();
        }
    }
    
    @Deprecated
    public CTPath[] getPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.PATH$0, (List)list);
            final CTPath[] array = new CTPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath getPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath ctPath = (CTPath)this.get_store().find_element_user(CTShapetypeImpl.PATH$0, n);
            if (ctPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath;
        }
    }
    
    public int sizeOfPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.PATH$0);
        }
    }
    
    public void setPathArray(final CTPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.PATH$0);
    }
    
    public void setPathArray(final int n, final CTPath ctPath) {
        this.generatedSetterHelperImpl((XmlObject)ctPath, CTShapetypeImpl.PATH$0, n, (short)2);
    }
    
    public CTPath insertNewPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().insert_element_user(CTShapetypeImpl.PATH$0, n);
        }
    }
    
    public CTPath addNewPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().add_element_user(CTShapetypeImpl.PATH$0);
        }
    }
    
    public void removePath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.PATH$0, n);
        }
    }
    
    public List<CTFormulas> getFormulasList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FormulasList extends AbstractList<CTFormulas>
            {
                @Override
                public CTFormulas get(final int n) {
                    return CTShapetypeImpl.this.getFormulasArray(n);
                }
                
                @Override
                public CTFormulas set(final int n, final CTFormulas ctFormulas) {
                    final CTFormulas formulasArray = CTShapetypeImpl.this.getFormulasArray(n);
                    CTShapetypeImpl.this.setFormulasArray(n, ctFormulas);
                    return formulasArray;
                }
                
                @Override
                public void add(final int n, final CTFormulas ctFormulas) {
                    CTShapetypeImpl.this.insertNewFormulas(n).set((XmlObject)ctFormulas);
                }
                
                @Override
                public CTFormulas remove(final int n) {
                    final CTFormulas formulasArray = CTShapetypeImpl.this.getFormulasArray(n);
                    CTShapetypeImpl.this.removeFormulas(n);
                    return formulasArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfFormulasArray();
                }
            }
            return new FormulasList();
        }
    }
    
    @Deprecated
    public CTFormulas[] getFormulasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.FORMULAS$2, (List)list);
            final CTFormulas[] array = new CTFormulas[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFormulas getFormulasArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFormulas ctFormulas = (CTFormulas)this.get_store().find_element_user(CTShapetypeImpl.FORMULAS$2, n);
            if (ctFormulas == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFormulas;
        }
    }
    
    public int sizeOfFormulasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.FORMULAS$2);
        }
    }
    
    public void setFormulasArray(final CTFormulas[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.FORMULAS$2);
    }
    
    public void setFormulasArray(final int n, final CTFormulas ctFormulas) {
        this.generatedSetterHelperImpl((XmlObject)ctFormulas, CTShapetypeImpl.FORMULAS$2, n, (short)2);
    }
    
    public CTFormulas insertNewFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().insert_element_user(CTShapetypeImpl.FORMULAS$2, n);
        }
    }
    
    public CTFormulas addNewFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().add_element_user(CTShapetypeImpl.FORMULAS$2);
        }
    }
    
    public void removeFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.FORMULAS$2, n);
        }
    }
    
    public List<CTHandles> getHandlesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HandlesList extends AbstractList<CTHandles>
            {
                @Override
                public CTHandles get(final int n) {
                    return CTShapetypeImpl.this.getHandlesArray(n);
                }
                
                @Override
                public CTHandles set(final int n, final CTHandles ctHandles) {
                    final CTHandles handlesArray = CTShapetypeImpl.this.getHandlesArray(n);
                    CTShapetypeImpl.this.setHandlesArray(n, ctHandles);
                    return handlesArray;
                }
                
                @Override
                public void add(final int n, final CTHandles ctHandles) {
                    CTShapetypeImpl.this.insertNewHandles(n).set((XmlObject)ctHandles);
                }
                
                @Override
                public CTHandles remove(final int n) {
                    final CTHandles handlesArray = CTShapetypeImpl.this.getHandlesArray(n);
                    CTShapetypeImpl.this.removeHandles(n);
                    return handlesArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfHandlesArray();
                }
            }
            return new HandlesList();
        }
    }
    
    @Deprecated
    public CTHandles[] getHandlesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.HANDLES$4, (List)list);
            final CTHandles[] array = new CTHandles[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHandles getHandlesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHandles ctHandles = (CTHandles)this.get_store().find_element_user(CTShapetypeImpl.HANDLES$4, n);
            if (ctHandles == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHandles;
        }
    }
    
    public int sizeOfHandlesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.HANDLES$4);
        }
    }
    
    public void setHandlesArray(final CTHandles[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.HANDLES$4);
    }
    
    public void setHandlesArray(final int n, final CTHandles ctHandles) {
        this.generatedSetterHelperImpl((XmlObject)ctHandles, CTShapetypeImpl.HANDLES$4, n, (short)2);
    }
    
    public CTHandles insertNewHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().insert_element_user(CTShapetypeImpl.HANDLES$4, n);
        }
    }
    
    public CTHandles addNewHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().add_element_user(CTShapetypeImpl.HANDLES$4);
        }
    }
    
    public void removeHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.HANDLES$4, n);
        }
    }
    
    public List<CTFill> getFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillList extends AbstractList<CTFill>
            {
                @Override
                public CTFill get(final int n) {
                    return CTShapetypeImpl.this.getFillArray(n);
                }
                
                @Override
                public CTFill set(final int n, final CTFill ctFill) {
                    final CTFill fillArray = CTShapetypeImpl.this.getFillArray(n);
                    CTShapetypeImpl.this.setFillArray(n, ctFill);
                    return fillArray;
                }
                
                @Override
                public void add(final int n, final CTFill ctFill) {
                    CTShapetypeImpl.this.insertNewFill(n).set((XmlObject)ctFill);
                }
                
                @Override
                public CTFill remove(final int n) {
                    final CTFill fillArray = CTShapetypeImpl.this.getFillArray(n);
                    CTShapetypeImpl.this.removeFill(n);
                    return fillArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfFillArray();
                }
            }
            return new FillList();
        }
    }
    
    @Deprecated
    public CTFill[] getFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.FILL$6, (List)list);
            final CTFill[] array = new CTFill[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFill getFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFill ctFill = (CTFill)this.get_store().find_element_user(CTShapetypeImpl.FILL$6, n);
            if (ctFill == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFill;
        }
    }
    
    public int sizeOfFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.FILL$6);
        }
    }
    
    public void setFillArray(final CTFill[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.FILL$6);
    }
    
    public void setFillArray(final int n, final CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTShapetypeImpl.FILL$6, n, (short)2);
    }
    
    public CTFill insertNewFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().insert_element_user(CTShapetypeImpl.FILL$6, n);
        }
    }
    
    public CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().add_element_user(CTShapetypeImpl.FILL$6);
        }
    }
    
    public void removeFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.FILL$6, n);
        }
    }
    
    public List<CTStroke> getStrokeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StrokeList extends AbstractList<CTStroke>
            {
                @Override
                public CTStroke get(final int n) {
                    return CTShapetypeImpl.this.getStrokeArray(n);
                }
                
                @Override
                public CTStroke set(final int n, final CTStroke ctStroke) {
                    final CTStroke strokeArray = CTShapetypeImpl.this.getStrokeArray(n);
                    CTShapetypeImpl.this.setStrokeArray(n, ctStroke);
                    return strokeArray;
                }
                
                @Override
                public void add(final int n, final CTStroke ctStroke) {
                    CTShapetypeImpl.this.insertNewStroke(n).set((XmlObject)ctStroke);
                }
                
                @Override
                public CTStroke remove(final int n) {
                    final CTStroke strokeArray = CTShapetypeImpl.this.getStrokeArray(n);
                    CTShapetypeImpl.this.removeStroke(n);
                    return strokeArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfStrokeArray();
                }
            }
            return new StrokeList();
        }
    }
    
    @Deprecated
    public CTStroke[] getStrokeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.STROKE$8, (List)list);
            final CTStroke[] array = new CTStroke[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStroke getStrokeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStroke ctStroke = (CTStroke)this.get_store().find_element_user(CTShapetypeImpl.STROKE$8, n);
            if (ctStroke == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStroke;
        }
    }
    
    public int sizeOfStrokeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.STROKE$8);
        }
    }
    
    public void setStrokeArray(final CTStroke[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.STROKE$8);
    }
    
    public void setStrokeArray(final int n, final CTStroke ctStroke) {
        this.generatedSetterHelperImpl((XmlObject)ctStroke, CTShapetypeImpl.STROKE$8, n, (short)2);
    }
    
    public CTStroke insertNewStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().insert_element_user(CTShapetypeImpl.STROKE$8, n);
        }
    }
    
    public CTStroke addNewStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().add_element_user(CTShapetypeImpl.STROKE$8);
        }
    }
    
    public void removeStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.STROKE$8, n);
        }
    }
    
    public List<CTShadow> getShadowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadowList extends AbstractList<CTShadow>
            {
                @Override
                public CTShadow get(final int n) {
                    return CTShapetypeImpl.this.getShadowArray(n);
                }
                
                @Override
                public CTShadow set(final int n, final CTShadow ctShadow) {
                    final CTShadow shadowArray = CTShapetypeImpl.this.getShadowArray(n);
                    CTShapetypeImpl.this.setShadowArray(n, ctShadow);
                    return shadowArray;
                }
                
                @Override
                public void add(final int n, final CTShadow ctShadow) {
                    CTShapetypeImpl.this.insertNewShadow(n).set((XmlObject)ctShadow);
                }
                
                @Override
                public CTShadow remove(final int n) {
                    final CTShadow shadowArray = CTShapetypeImpl.this.getShadowArray(n);
                    CTShapetypeImpl.this.removeShadow(n);
                    return shadowArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfShadowArray();
                }
            }
            return new ShadowList();
        }
    }
    
    @Deprecated
    public CTShadow[] getShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.SHADOW$10, (List)list);
            final CTShadow[] array = new CTShadow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShadow getShadowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShadow ctShadow = (CTShadow)this.get_store().find_element_user(CTShapetypeImpl.SHADOW$10, n);
            if (ctShadow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShadow;
        }
    }
    
    public int sizeOfShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.SHADOW$10);
        }
    }
    
    public void setShadowArray(final CTShadow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.SHADOW$10);
    }
    
    public void setShadowArray(final int n, final CTShadow ctShadow) {
        this.generatedSetterHelperImpl((XmlObject)ctShadow, CTShapetypeImpl.SHADOW$10, n, (short)2);
    }
    
    public CTShadow insertNewShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().insert_element_user(CTShapetypeImpl.SHADOW$10, n);
        }
    }
    
    public CTShadow addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().add_element_user(CTShapetypeImpl.SHADOW$10);
        }
    }
    
    public void removeShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.SHADOW$10, n);
        }
    }
    
    public List<CTTextbox> getTextboxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextboxList extends AbstractList<CTTextbox>
            {
                @Override
                public CTTextbox get(final int n) {
                    return CTShapetypeImpl.this.getTextboxArray(n);
                }
                
                @Override
                public CTTextbox set(final int n, final CTTextbox ctTextbox) {
                    final CTTextbox textboxArray = CTShapetypeImpl.this.getTextboxArray(n);
                    CTShapetypeImpl.this.setTextboxArray(n, ctTextbox);
                    return textboxArray;
                }
                
                @Override
                public void add(final int n, final CTTextbox ctTextbox) {
                    CTShapetypeImpl.this.insertNewTextbox(n).set((XmlObject)ctTextbox);
                }
                
                @Override
                public CTTextbox remove(final int n) {
                    final CTTextbox textboxArray = CTShapetypeImpl.this.getTextboxArray(n);
                    CTShapetypeImpl.this.removeTextbox(n);
                    return textboxArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfTextboxArray();
                }
            }
            return new TextboxList();
        }
    }
    
    @Deprecated
    public CTTextbox[] getTextboxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.TEXTBOX$12, (List)list);
            final CTTextbox[] array = new CTTextbox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextbox getTextboxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextbox ctTextbox = (CTTextbox)this.get_store().find_element_user(CTShapetypeImpl.TEXTBOX$12, n);
            if (ctTextbox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextbox;
        }
    }
    
    public int sizeOfTextboxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.TEXTBOX$12);
        }
    }
    
    public void setTextboxArray(final CTTextbox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.TEXTBOX$12);
    }
    
    public void setTextboxArray(final int n, final CTTextbox ctTextbox) {
        this.generatedSetterHelperImpl((XmlObject)ctTextbox, CTShapetypeImpl.TEXTBOX$12, n, (short)2);
    }
    
    public CTTextbox insertNewTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().insert_element_user(CTShapetypeImpl.TEXTBOX$12, n);
        }
    }
    
    public CTTextbox addNewTextbox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().add_element_user(CTShapetypeImpl.TEXTBOX$12);
        }
    }
    
    public void removeTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.TEXTBOX$12, n);
        }
    }
    
    public List<CTTextPath> getTextpathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextpathList extends AbstractList<CTTextPath>
            {
                @Override
                public CTTextPath get(final int n) {
                    return CTShapetypeImpl.this.getTextpathArray(n);
                }
                
                @Override
                public CTTextPath set(final int n, final CTTextPath ctTextPath) {
                    final CTTextPath textpathArray = CTShapetypeImpl.this.getTextpathArray(n);
                    CTShapetypeImpl.this.setTextpathArray(n, ctTextPath);
                    return textpathArray;
                }
                
                @Override
                public void add(final int n, final CTTextPath ctTextPath) {
                    CTShapetypeImpl.this.insertNewTextpath(n).set((XmlObject)ctTextPath);
                }
                
                @Override
                public CTTextPath remove(final int n) {
                    final CTTextPath textpathArray = CTShapetypeImpl.this.getTextpathArray(n);
                    CTShapetypeImpl.this.removeTextpath(n);
                    return textpathArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfTextpathArray();
                }
            }
            return new TextpathList();
        }
    }
    
    @Deprecated
    public CTTextPath[] getTextpathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.TEXTPATH$14, (List)list);
            final CTTextPath[] array = new CTTextPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextPath getTextpathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextPath ctTextPath = (CTTextPath)this.get_store().find_element_user(CTShapetypeImpl.TEXTPATH$14, n);
            if (ctTextPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextPath;
        }
    }
    
    public int sizeOfTextpathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.TEXTPATH$14);
        }
    }
    
    public void setTextpathArray(final CTTextPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.TEXTPATH$14);
    }
    
    public void setTextpathArray(final int n, final CTTextPath ctTextPath) {
        this.generatedSetterHelperImpl((XmlObject)ctTextPath, CTShapetypeImpl.TEXTPATH$14, n, (short)2);
    }
    
    public CTTextPath insertNewTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().insert_element_user(CTShapetypeImpl.TEXTPATH$14, n);
        }
    }
    
    public CTTextPath addNewTextpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().add_element_user(CTShapetypeImpl.TEXTPATH$14);
        }
    }
    
    public void removeTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.TEXTPATH$14, n);
        }
    }
    
    public List<CTImageData> getImagedataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ImagedataList extends AbstractList<CTImageData>
            {
                @Override
                public CTImageData get(final int n) {
                    return CTShapetypeImpl.this.getImagedataArray(n);
                }
                
                @Override
                public CTImageData set(final int n, final CTImageData ctImageData) {
                    final CTImageData imagedataArray = CTShapetypeImpl.this.getImagedataArray(n);
                    CTShapetypeImpl.this.setImagedataArray(n, ctImageData);
                    return imagedataArray;
                }
                
                @Override
                public void add(final int n, final CTImageData ctImageData) {
                    CTShapetypeImpl.this.insertNewImagedata(n).set((XmlObject)ctImageData);
                }
                
                @Override
                public CTImageData remove(final int n) {
                    final CTImageData imagedataArray = CTShapetypeImpl.this.getImagedataArray(n);
                    CTShapetypeImpl.this.removeImagedata(n);
                    return imagedataArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfImagedataArray();
                }
            }
            return new ImagedataList();
        }
    }
    
    @Deprecated
    public CTImageData[] getImagedataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.IMAGEDATA$16, (List)list);
            final CTImageData[] array = new CTImageData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTImageData getImagedataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTImageData ctImageData = (CTImageData)this.get_store().find_element_user(CTShapetypeImpl.IMAGEDATA$16, n);
            if (ctImageData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctImageData;
        }
    }
    
    public int sizeOfImagedataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.IMAGEDATA$16);
        }
    }
    
    public void setImagedataArray(final CTImageData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.IMAGEDATA$16);
    }
    
    public void setImagedataArray(final int n, final CTImageData ctImageData) {
        this.generatedSetterHelperImpl((XmlObject)ctImageData, CTShapetypeImpl.IMAGEDATA$16, n, (short)2);
    }
    
    public CTImageData insertNewImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().insert_element_user(CTShapetypeImpl.IMAGEDATA$16, n);
        }
    }
    
    public CTImageData addNewImagedata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().add_element_user(CTShapetypeImpl.IMAGEDATA$16);
        }
    }
    
    public void removeImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.IMAGEDATA$16, n);
        }
    }
    
    public List<CTSkew> getSkewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SkewList extends AbstractList<CTSkew>
            {
                @Override
                public CTSkew get(final int n) {
                    return CTShapetypeImpl.this.getSkewArray(n);
                }
                
                @Override
                public CTSkew set(final int n, final CTSkew ctSkew) {
                    final CTSkew skewArray = CTShapetypeImpl.this.getSkewArray(n);
                    CTShapetypeImpl.this.setSkewArray(n, ctSkew);
                    return skewArray;
                }
                
                @Override
                public void add(final int n, final CTSkew ctSkew) {
                    CTShapetypeImpl.this.insertNewSkew(n).set((XmlObject)ctSkew);
                }
                
                @Override
                public CTSkew remove(final int n) {
                    final CTSkew skewArray = CTShapetypeImpl.this.getSkewArray(n);
                    CTShapetypeImpl.this.removeSkew(n);
                    return skewArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfSkewArray();
                }
            }
            return new SkewList();
        }
    }
    
    @Deprecated
    public CTSkew[] getSkewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.SKEW$18, (List)list);
            final CTSkew[] array = new CTSkew[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSkew getSkewArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSkew ctSkew = (CTSkew)this.get_store().find_element_user(CTShapetypeImpl.SKEW$18, n);
            if (ctSkew == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSkew;
        }
    }
    
    public int sizeOfSkewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.SKEW$18);
        }
    }
    
    public void setSkewArray(final CTSkew[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.SKEW$18);
    }
    
    public void setSkewArray(final int n, final CTSkew ctSkew) {
        this.generatedSetterHelperImpl((XmlObject)ctSkew, CTShapetypeImpl.SKEW$18, n, (short)2);
    }
    
    public CTSkew insertNewSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().insert_element_user(CTShapetypeImpl.SKEW$18, n);
        }
    }
    
    public CTSkew addNewSkew() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().add_element_user(CTShapetypeImpl.SKEW$18);
        }
    }
    
    public void removeSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.SKEW$18, n);
        }
    }
    
    public List<CTExtrusion> getExtrusionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtrusionList extends AbstractList<CTExtrusion>
            {
                @Override
                public CTExtrusion get(final int n) {
                    return CTShapetypeImpl.this.getExtrusionArray(n);
                }
                
                @Override
                public CTExtrusion set(final int n, final CTExtrusion ctExtrusion) {
                    final CTExtrusion extrusionArray = CTShapetypeImpl.this.getExtrusionArray(n);
                    CTShapetypeImpl.this.setExtrusionArray(n, ctExtrusion);
                    return extrusionArray;
                }
                
                @Override
                public void add(final int n, final CTExtrusion ctExtrusion) {
                    CTShapetypeImpl.this.insertNewExtrusion(n).set((XmlObject)ctExtrusion);
                }
                
                @Override
                public CTExtrusion remove(final int n) {
                    final CTExtrusion extrusionArray = CTShapetypeImpl.this.getExtrusionArray(n);
                    CTShapetypeImpl.this.removeExtrusion(n);
                    return extrusionArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfExtrusionArray();
                }
            }
            return new ExtrusionList();
        }
    }
    
    @Deprecated
    public CTExtrusion[] getExtrusionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.EXTRUSION$20, (List)list);
            final CTExtrusion[] array = new CTExtrusion[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExtrusion getExtrusionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtrusion ctExtrusion = (CTExtrusion)this.get_store().find_element_user(CTShapetypeImpl.EXTRUSION$20, n);
            if (ctExtrusion == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExtrusion;
        }
    }
    
    public int sizeOfExtrusionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.EXTRUSION$20);
        }
    }
    
    public void setExtrusionArray(final CTExtrusion[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.EXTRUSION$20);
    }
    
    public void setExtrusionArray(final int n, final CTExtrusion ctExtrusion) {
        this.generatedSetterHelperImpl((XmlObject)ctExtrusion, CTShapetypeImpl.EXTRUSION$20, n, (short)2);
    }
    
    public CTExtrusion insertNewExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().insert_element_user(CTShapetypeImpl.EXTRUSION$20, n);
        }
    }
    
    public CTExtrusion addNewExtrusion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().add_element_user(CTShapetypeImpl.EXTRUSION$20);
        }
    }
    
    public void removeExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.EXTRUSION$20, n);
        }
    }
    
    public List<CTCallout> getCalloutList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CalloutList extends AbstractList<CTCallout>
            {
                @Override
                public CTCallout get(final int n) {
                    return CTShapetypeImpl.this.getCalloutArray(n);
                }
                
                @Override
                public CTCallout set(final int n, final CTCallout ctCallout) {
                    final CTCallout calloutArray = CTShapetypeImpl.this.getCalloutArray(n);
                    CTShapetypeImpl.this.setCalloutArray(n, ctCallout);
                    return calloutArray;
                }
                
                @Override
                public void add(final int n, final CTCallout ctCallout) {
                    CTShapetypeImpl.this.insertNewCallout(n).set((XmlObject)ctCallout);
                }
                
                @Override
                public CTCallout remove(final int n) {
                    final CTCallout calloutArray = CTShapetypeImpl.this.getCalloutArray(n);
                    CTShapetypeImpl.this.removeCallout(n);
                    return calloutArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfCalloutArray();
                }
            }
            return new CalloutList();
        }
    }
    
    @Deprecated
    public CTCallout[] getCalloutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.CALLOUT$22, (List)list);
            final CTCallout[] array = new CTCallout[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCallout getCalloutArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCallout ctCallout = (CTCallout)this.get_store().find_element_user(CTShapetypeImpl.CALLOUT$22, n);
            if (ctCallout == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCallout;
        }
    }
    
    public int sizeOfCalloutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.CALLOUT$22);
        }
    }
    
    public void setCalloutArray(final CTCallout[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.CALLOUT$22);
    }
    
    public void setCalloutArray(final int n, final CTCallout ctCallout) {
        this.generatedSetterHelperImpl((XmlObject)ctCallout, CTShapetypeImpl.CALLOUT$22, n, (short)2);
    }
    
    public CTCallout insertNewCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().insert_element_user(CTShapetypeImpl.CALLOUT$22, n);
        }
    }
    
    public CTCallout addNewCallout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().add_element_user(CTShapetypeImpl.CALLOUT$22);
        }
    }
    
    public void removeCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.CALLOUT$22, n);
        }
    }
    
    public List<CTLock> getLockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockList extends AbstractList<CTLock>
            {
                @Override
                public CTLock get(final int n) {
                    return CTShapetypeImpl.this.getLockArray(n);
                }
                
                @Override
                public CTLock set(final int n, final CTLock ctLock) {
                    final CTLock lockArray = CTShapetypeImpl.this.getLockArray(n);
                    CTShapetypeImpl.this.setLockArray(n, ctLock);
                    return lockArray;
                }
                
                @Override
                public void add(final int n, final CTLock ctLock) {
                    CTShapetypeImpl.this.insertNewLock(n).set((XmlObject)ctLock);
                }
                
                @Override
                public CTLock remove(final int n) {
                    final CTLock lockArray = CTShapetypeImpl.this.getLockArray(n);
                    CTShapetypeImpl.this.removeLock(n);
                    return lockArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfLockArray();
                }
            }
            return new LockList();
        }
    }
    
    @Deprecated
    public CTLock[] getLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.LOCK$24, (List)list);
            final CTLock[] array = new CTLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLock getLockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLock ctLock = (CTLock)this.get_store().find_element_user(CTShapetypeImpl.LOCK$24, n);
            if (ctLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLock;
        }
    }
    
    public int sizeOfLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.LOCK$24);
        }
    }
    
    public void setLockArray(final CTLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.LOCK$24);
    }
    
    public void setLockArray(final int n, final CTLock ctLock) {
        this.generatedSetterHelperImpl((XmlObject)ctLock, CTShapetypeImpl.LOCK$24, n, (short)2);
    }
    
    public CTLock insertNewLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().insert_element_user(CTShapetypeImpl.LOCK$24, n);
        }
    }
    
    public CTLock addNewLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().add_element_user(CTShapetypeImpl.LOCK$24);
        }
    }
    
    public void removeLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.LOCK$24, n);
        }
    }
    
    public List<CTClipPath> getClippathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClippathList extends AbstractList<CTClipPath>
            {
                @Override
                public CTClipPath get(final int n) {
                    return CTShapetypeImpl.this.getClippathArray(n);
                }
                
                @Override
                public CTClipPath set(final int n, final CTClipPath ctClipPath) {
                    final CTClipPath clippathArray = CTShapetypeImpl.this.getClippathArray(n);
                    CTShapetypeImpl.this.setClippathArray(n, ctClipPath);
                    return clippathArray;
                }
                
                @Override
                public void add(final int n, final CTClipPath ctClipPath) {
                    CTShapetypeImpl.this.insertNewClippath(n).set((XmlObject)ctClipPath);
                }
                
                @Override
                public CTClipPath remove(final int n) {
                    final CTClipPath clippathArray = CTShapetypeImpl.this.getClippathArray(n);
                    CTShapetypeImpl.this.removeClippath(n);
                    return clippathArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfClippathArray();
                }
            }
            return new ClippathList();
        }
    }
    
    @Deprecated
    public CTClipPath[] getClippathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.CLIPPATH$26, (List)list);
            final CTClipPath[] array = new CTClipPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClipPath getClippathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClipPath ctClipPath = (CTClipPath)this.get_store().find_element_user(CTShapetypeImpl.CLIPPATH$26, n);
            if (ctClipPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClipPath;
        }
    }
    
    public int sizeOfClippathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.CLIPPATH$26);
        }
    }
    
    public void setClippathArray(final CTClipPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.CLIPPATH$26);
    }
    
    public void setClippathArray(final int n, final CTClipPath ctClipPath) {
        this.generatedSetterHelperImpl((XmlObject)ctClipPath, CTShapetypeImpl.CLIPPATH$26, n, (short)2);
    }
    
    public CTClipPath insertNewClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().insert_element_user(CTShapetypeImpl.CLIPPATH$26, n);
        }
    }
    
    public CTClipPath addNewClippath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().add_element_user(CTShapetypeImpl.CLIPPATH$26);
        }
    }
    
    public void removeClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.CLIPPATH$26, n);
        }
    }
    
    public List<CTSignatureLine> getSignaturelineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SignaturelineList extends AbstractList<CTSignatureLine>
            {
                @Override
                public CTSignatureLine get(final int n) {
                    return CTShapetypeImpl.this.getSignaturelineArray(n);
                }
                
                @Override
                public CTSignatureLine set(final int n, final CTSignatureLine ctSignatureLine) {
                    final CTSignatureLine signaturelineArray = CTShapetypeImpl.this.getSignaturelineArray(n);
                    CTShapetypeImpl.this.setSignaturelineArray(n, ctSignatureLine);
                    return signaturelineArray;
                }
                
                @Override
                public void add(final int n, final CTSignatureLine ctSignatureLine) {
                    CTShapetypeImpl.this.insertNewSignatureline(n).set((XmlObject)ctSignatureLine);
                }
                
                @Override
                public CTSignatureLine remove(final int n) {
                    final CTSignatureLine signaturelineArray = CTShapetypeImpl.this.getSignaturelineArray(n);
                    CTShapetypeImpl.this.removeSignatureline(n);
                    return signaturelineArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfSignaturelineArray();
                }
            }
            return new SignaturelineList();
        }
    }
    
    @Deprecated
    public CTSignatureLine[] getSignaturelineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.SIGNATURELINE$28, (List)list);
            final CTSignatureLine[] array = new CTSignatureLine[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSignatureLine getSignaturelineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignatureLine ctSignatureLine = (CTSignatureLine)this.get_store().find_element_user(CTShapetypeImpl.SIGNATURELINE$28, n);
            if (ctSignatureLine == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSignatureLine;
        }
    }
    
    public int sizeOfSignaturelineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.SIGNATURELINE$28);
        }
    }
    
    public void setSignaturelineArray(final CTSignatureLine[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.SIGNATURELINE$28);
    }
    
    public void setSignaturelineArray(final int n, final CTSignatureLine ctSignatureLine) {
        this.generatedSetterHelperImpl((XmlObject)ctSignatureLine, CTShapetypeImpl.SIGNATURELINE$28, n, (short)2);
    }
    
    public CTSignatureLine insertNewSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().insert_element_user(CTShapetypeImpl.SIGNATURELINE$28, n);
        }
    }
    
    public CTSignatureLine addNewSignatureline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().add_element_user(CTShapetypeImpl.SIGNATURELINE$28);
        }
    }
    
    public void removeSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.SIGNATURELINE$28, n);
        }
    }
    
    public List<CTWrap> getWrapList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WrapList extends AbstractList<CTWrap>
            {
                @Override
                public CTWrap get(final int n) {
                    return CTShapetypeImpl.this.getWrapArray(n);
                }
                
                @Override
                public CTWrap set(final int n, final CTWrap ctWrap) {
                    final CTWrap wrapArray = CTShapetypeImpl.this.getWrapArray(n);
                    CTShapetypeImpl.this.setWrapArray(n, ctWrap);
                    return wrapArray;
                }
                
                @Override
                public void add(final int n, final CTWrap ctWrap) {
                    CTShapetypeImpl.this.insertNewWrap(n).set((XmlObject)ctWrap);
                }
                
                @Override
                public CTWrap remove(final int n) {
                    final CTWrap wrapArray = CTShapetypeImpl.this.getWrapArray(n);
                    CTShapetypeImpl.this.removeWrap(n);
                    return wrapArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfWrapArray();
                }
            }
            return new WrapList();
        }
    }
    
    @Deprecated
    public CTWrap[] getWrapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.WRAP$30, (List)list);
            final CTWrap[] array = new CTWrap[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTWrap getWrapArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrap ctWrap = (CTWrap)this.get_store().find_element_user(CTShapetypeImpl.WRAP$30, n);
            if (ctWrap == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctWrap;
        }
    }
    
    public int sizeOfWrapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.WRAP$30);
        }
    }
    
    public void setWrapArray(final CTWrap[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.WRAP$30);
    }
    
    public void setWrapArray(final int n, final CTWrap ctWrap) {
        this.generatedSetterHelperImpl((XmlObject)ctWrap, CTShapetypeImpl.WRAP$30, n, (short)2);
    }
    
    public CTWrap insertNewWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().insert_element_user(CTShapetypeImpl.WRAP$30, n);
        }
    }
    
    public CTWrap addNewWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().add_element_user(CTShapetypeImpl.WRAP$30);
        }
    }
    
    public void removeWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.WRAP$30, n);
        }
    }
    
    public List<CTAnchorLock> getAnchorlockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorlockList extends AbstractList<CTAnchorLock>
            {
                @Override
                public CTAnchorLock get(final int n) {
                    return CTShapetypeImpl.this.getAnchorlockArray(n);
                }
                
                @Override
                public CTAnchorLock set(final int n, final CTAnchorLock ctAnchorLock) {
                    final CTAnchorLock anchorlockArray = CTShapetypeImpl.this.getAnchorlockArray(n);
                    CTShapetypeImpl.this.setAnchorlockArray(n, ctAnchorLock);
                    return anchorlockArray;
                }
                
                @Override
                public void add(final int n, final CTAnchorLock ctAnchorLock) {
                    CTShapetypeImpl.this.insertNewAnchorlock(n).set((XmlObject)ctAnchorLock);
                }
                
                @Override
                public CTAnchorLock remove(final int n) {
                    final CTAnchorLock anchorlockArray = CTShapetypeImpl.this.getAnchorlockArray(n);
                    CTShapetypeImpl.this.removeAnchorlock(n);
                    return anchorlockArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfAnchorlockArray();
                }
            }
            return new AnchorlockList();
        }
    }
    
    @Deprecated
    public CTAnchorLock[] getAnchorlockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.ANCHORLOCK$32, (List)list);
            final CTAnchorLock[] array = new CTAnchorLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAnchorLock getAnchorlockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchorLock ctAnchorLock = (CTAnchorLock)this.get_store().find_element_user(CTShapetypeImpl.ANCHORLOCK$32, n);
            if (ctAnchorLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAnchorLock;
        }
    }
    
    public int sizeOfAnchorlockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.ANCHORLOCK$32);
        }
    }
    
    public void setAnchorlockArray(final CTAnchorLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.ANCHORLOCK$32);
    }
    
    public void setAnchorlockArray(final int n, final CTAnchorLock ctAnchorLock) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchorLock, CTShapetypeImpl.ANCHORLOCK$32, n, (short)2);
    }
    
    public CTAnchorLock insertNewAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().insert_element_user(CTShapetypeImpl.ANCHORLOCK$32, n);
        }
    }
    
    public CTAnchorLock addNewAnchorlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().add_element_user(CTShapetypeImpl.ANCHORLOCK$32);
        }
    }
    
    public void removeAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.ANCHORLOCK$32, n);
        }
    }
    
    public List<CTBorder> getBordertopList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BordertopList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTShapetypeImpl.this.getBordertopArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder bordertopArray = CTShapetypeImpl.this.getBordertopArray(n);
                    CTShapetypeImpl.this.setBordertopArray(n, ctBorder);
                    return bordertopArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTShapetypeImpl.this.insertNewBordertop(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder bordertopArray = CTShapetypeImpl.this.getBordertopArray(n);
                    CTShapetypeImpl.this.removeBordertop(n);
                    return bordertopArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfBordertopArray();
                }
            }
            return new BordertopList();
        }
    }
    
    @Deprecated
    public CTBorder[] getBordertopArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.BORDERTOP$34, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBordertopArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTShapetypeImpl.BORDERTOP$34, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBordertopArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.BORDERTOP$34);
        }
    }
    
    public void setBordertopArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.BORDERTOP$34);
    }
    
    public void setBordertopArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTShapetypeImpl.BORDERTOP$34, n, (short)2);
    }
    
    public CTBorder insertNewBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTShapetypeImpl.BORDERTOP$34, n);
        }
    }
    
    public CTBorder addNewBordertop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTShapetypeImpl.BORDERTOP$34);
        }
    }
    
    public void removeBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.BORDERTOP$34, n);
        }
    }
    
    public List<CTBorder> getBorderbottomList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderbottomList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTShapetypeImpl.this.getBorderbottomArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderbottomArray = CTShapetypeImpl.this.getBorderbottomArray(n);
                    CTShapetypeImpl.this.setBorderbottomArray(n, ctBorder);
                    return borderbottomArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTShapetypeImpl.this.insertNewBorderbottom(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderbottomArray = CTShapetypeImpl.this.getBorderbottomArray(n);
                    CTShapetypeImpl.this.removeBorderbottom(n);
                    return borderbottomArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfBorderbottomArray();
                }
            }
            return new BorderbottomList();
        }
    }
    
    @Deprecated
    public CTBorder[] getBorderbottomArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.BORDERBOTTOM$36, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderbottomArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTShapetypeImpl.BORDERBOTTOM$36, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderbottomArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.BORDERBOTTOM$36);
        }
    }
    
    public void setBorderbottomArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.BORDERBOTTOM$36);
    }
    
    public void setBorderbottomArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTShapetypeImpl.BORDERBOTTOM$36, n, (short)2);
    }
    
    public CTBorder insertNewBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTShapetypeImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public CTBorder addNewBorderbottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTShapetypeImpl.BORDERBOTTOM$36);
        }
    }
    
    public void removeBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public List<CTBorder> getBorderleftList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderleftList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTShapetypeImpl.this.getBorderleftArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderleftArray = CTShapetypeImpl.this.getBorderleftArray(n);
                    CTShapetypeImpl.this.setBorderleftArray(n, ctBorder);
                    return borderleftArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTShapetypeImpl.this.insertNewBorderleft(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderleftArray = CTShapetypeImpl.this.getBorderleftArray(n);
                    CTShapetypeImpl.this.removeBorderleft(n);
                    return borderleftArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfBorderleftArray();
                }
            }
            return new BorderleftList();
        }
    }
    
    @Deprecated
    public CTBorder[] getBorderleftArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.BORDERLEFT$38, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderleftArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTShapetypeImpl.BORDERLEFT$38, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderleftArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.BORDERLEFT$38);
        }
    }
    
    public void setBorderleftArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.BORDERLEFT$38);
    }
    
    public void setBorderleftArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTShapetypeImpl.BORDERLEFT$38, n, (short)2);
    }
    
    public CTBorder insertNewBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTShapetypeImpl.BORDERLEFT$38, n);
        }
    }
    
    public CTBorder addNewBorderleft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTShapetypeImpl.BORDERLEFT$38);
        }
    }
    
    public void removeBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.BORDERLEFT$38, n);
        }
    }
    
    public List<CTBorder> getBorderrightList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderrightList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTShapetypeImpl.this.getBorderrightArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderrightArray = CTShapetypeImpl.this.getBorderrightArray(n);
                    CTShapetypeImpl.this.setBorderrightArray(n, ctBorder);
                    return borderrightArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTShapetypeImpl.this.insertNewBorderright(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderrightArray = CTShapetypeImpl.this.getBorderrightArray(n);
                    CTShapetypeImpl.this.removeBorderright(n);
                    return borderrightArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfBorderrightArray();
                }
            }
            return new BorderrightList();
        }
    }
    
    @Deprecated
    public CTBorder[] getBorderrightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.BORDERRIGHT$40, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderrightArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTShapetypeImpl.BORDERRIGHT$40, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderrightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.BORDERRIGHT$40);
        }
    }
    
    public void setBorderrightArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.BORDERRIGHT$40);
    }
    
    public void setBorderrightArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTShapetypeImpl.BORDERRIGHT$40, n, (short)2);
    }
    
    public CTBorder insertNewBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTShapetypeImpl.BORDERRIGHT$40, n);
        }
    }
    
    public CTBorder addNewBorderright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTShapetypeImpl.BORDERRIGHT$40);
        }
    }
    
    public void removeBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.BORDERRIGHT$40, n);
        }
    }
    
    public List<CTClientData> getClientDataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClientDataList extends AbstractList<CTClientData>
            {
                @Override
                public CTClientData get(final int n) {
                    return CTShapetypeImpl.this.getClientDataArray(n);
                }
                
                @Override
                public CTClientData set(final int n, final CTClientData ctClientData) {
                    final CTClientData clientDataArray = CTShapetypeImpl.this.getClientDataArray(n);
                    CTShapetypeImpl.this.setClientDataArray(n, ctClientData);
                    return clientDataArray;
                }
                
                @Override
                public void add(final int n, final CTClientData ctClientData) {
                    CTShapetypeImpl.this.insertNewClientData(n).set((XmlObject)ctClientData);
                }
                
                @Override
                public CTClientData remove(final int n) {
                    final CTClientData clientDataArray = CTShapetypeImpl.this.getClientDataArray(n);
                    CTShapetypeImpl.this.removeClientData(n);
                    return clientDataArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfClientDataArray();
                }
            }
            return new ClientDataList();
        }
    }
    
    @Deprecated
    public CTClientData[] getClientDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.CLIENTDATA$42, (List)list);
            final CTClientData[] array = new CTClientData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClientData getClientDataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClientData ctClientData = (CTClientData)this.get_store().find_element_user(CTShapetypeImpl.CLIENTDATA$42, n);
            if (ctClientData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClientData;
        }
    }
    
    public int sizeOfClientDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.CLIENTDATA$42);
        }
    }
    
    public void setClientDataArray(final CTClientData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.CLIENTDATA$42);
    }
    
    public void setClientDataArray(final int n, final CTClientData ctClientData) {
        this.generatedSetterHelperImpl((XmlObject)ctClientData, CTShapetypeImpl.CLIENTDATA$42, n, (short)2);
    }
    
    public CTClientData insertNewClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().insert_element_user(CTShapetypeImpl.CLIENTDATA$42, n);
        }
    }
    
    public CTClientData addNewClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().add_element_user(CTShapetypeImpl.CLIENTDATA$42);
        }
    }
    
    public void removeClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.CLIENTDATA$42, n);
        }
    }
    
    public List<CTRel> getTextdataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextdataList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTShapetypeImpl.this.getTextdataArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel textdataArray = CTShapetypeImpl.this.getTextdataArray(n);
                    CTShapetypeImpl.this.setTextdataArray(n, ctRel);
                    return textdataArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTShapetypeImpl.this.insertNewTextdata(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel textdataArray = CTShapetypeImpl.this.getTextdataArray(n);
                    CTShapetypeImpl.this.removeTextdata(n);
                    return textdataArray;
                }
                
                @Override
                public int size() {
                    return CTShapetypeImpl.this.sizeOfTextdataArray();
                }
            }
            return new TextdataList();
        }
    }
    
    @Deprecated
    public CTRel[] getTextdataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTShapetypeImpl.TEXTDATA$44, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getTextdataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTShapetypeImpl.TEXTDATA$44, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfTextdataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.TEXTDATA$44);
        }
    }
    
    public void setTextdataArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTShapetypeImpl.TEXTDATA$44);
    }
    
    public void setTextdataArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTShapetypeImpl.TEXTDATA$44, n, (short)2);
    }
    
    public CTRel insertNewTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTShapetypeImpl.TEXTDATA$44, n);
        }
    }
    
    public CTRel addNewTextdata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTShapetypeImpl.TEXTDATA$44);
        }
    }
    
    public void removeTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.TEXTDATA$44, n);
        }
    }
    
    public CTComplex getComplex() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTComplex ctComplex = (CTComplex)this.get_store().find_element_user(CTShapetypeImpl.COMPLEX$46, 0);
            if (ctComplex == null) {
                return null;
            }
            return ctComplex;
        }
    }
    
    public boolean isSetComplex() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapetypeImpl.COMPLEX$46) != 0;
        }
    }
    
    public void setComplex(final CTComplex ctComplex) {
        this.generatedSetterHelperImpl((XmlObject)ctComplex, CTShapetypeImpl.COMPLEX$46, 0, (short)1);
    }
    
    public CTComplex addNewComplex() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTComplex)this.get_store().add_element_user(CTShapetypeImpl.COMPLEX$46);
        }
    }
    
    public void unsetComplex() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapetypeImpl.COMPLEX$46, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ID$48);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ID$48);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ID$48) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ID$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ID$48);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ID$48);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.ID$48);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ID$48);
        }
    }
    
    public String getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STYLE$50);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.STYLE$50);
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.STYLE$50) != null;
        }
    }
    
    public void setStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STYLE$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.STYLE$50);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.STYLE$50);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.STYLE$50);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.STYLE$50);
        }
    }
    
    public String getHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HREF$52);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.HREF$52);
        }
    }
    
    public boolean isSetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HREF$52) != null;
        }
    }
    
    public void setHref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HREF$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HREF$52);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.HREF$52);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.HREF$52);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HREF$52);
        }
    }
    
    public String getTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.TARGET$54);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.TARGET$54);
        }
    }
    
    public boolean isSetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.TARGET$54) != null;
        }
    }
    
    public void setTarget(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.TARGET$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.TARGET$54);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTarget(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.TARGET$54);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.TARGET$54);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.TARGET$54);
        }
    }
    
    public String getClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLASS1$56);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.CLASS1$56);
        }
    }
    
    public boolean isSetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.CLASS1$56) != null;
        }
    }
    
    public void setClass1(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLASS1$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.CLASS1$56);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetClass1(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.CLASS1$56);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.CLASS1$56);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.CLASS1$56);
        }
    }
    
    public String getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.TITLE$58);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.TITLE$58);
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.TITLE$58) != null;
        }
    }
    
    public void setTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.TITLE$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.TITLE$58);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTitle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.TITLE$58);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.TITLE$58);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.TITLE$58);
        }
    }
    
    public String getAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALT$60);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ALT$60);
        }
    }
    
    public boolean isSetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ALT$60) != null;
        }
    }
    
    public void setAlt(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALT$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ALT$60);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlt(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ALT$60);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.ALT$60);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ALT$60);
        }
    }
    
    public String getCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.COORDSIZE$62);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.COORDSIZE$62);
        }
    }
    
    public boolean isSetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.COORDSIZE$62) != null;
        }
    }
    
    public void setCoordsize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.COORDSIZE$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.COORDSIZE$62);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordsize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.COORDSIZE$62);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.COORDSIZE$62);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.COORDSIZE$62);
        }
    }
    
    public String getCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
        }
    }
    
    public boolean isSetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.COORDORIGIN$64) != null;
        }
    }
    
    public void setCoordorigin(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordorigin(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.COORDORIGIN$64);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.COORDORIGIN$64);
        }
    }
    
    public String getWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
        }
    }
    
    public boolean isSetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.WRAPCOORDS$66) != null;
        }
    }
    
    public void setWrapcoords(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetWrapcoords(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.WRAPCOORDS$66);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.WRAPCOORDS$66);
        }
    }
    
    public STTrueFalse.Enum getPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PRINT$68);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.PRINT$68);
        }
    }
    
    public boolean isSetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.PRINT$68) != null;
        }
    }
    
    public void setPrint(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PRINT$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.PRINT$68);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrint(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.PRINT$68);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.PRINT$68);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.PRINT$68);
        }
    }
    
    public String getSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.SPID$70);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.SPID$70);
        }
    }
    
    public boolean isSetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.SPID$70) != null;
        }
    }
    
    public void setSpid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.SPID$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.SPID$70);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSpid(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.SPID$70);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.SPID$70);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.SPID$70);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ONED$72);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ONED$72);
        }
    }
    
    public boolean isSetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ONED$72) != null;
        }
    }
    
    public void setOned(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ONED$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ONED$72);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOned(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ONED$72);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.ONED$72);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ONED$72);
        }
    }
    
    public BigInteger getRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.REGROUPID$74);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.REGROUPID$74);
        }
    }
    
    public boolean isSetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.REGROUPID$74) != null;
        }
    }
    
    public void setRegroupid(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.REGROUPID$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.REGROUPID$74);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRegroupid(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.REGROUPID$74);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTShapetypeImpl.REGROUPID$74);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.REGROUPID$74);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
        }
    }
    
    public boolean isSetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76) != null;
        }
    }
    
    public void setDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.DOUBLECLICKNOTIFY$76);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BUTTON$78);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.BUTTON$78);
        }
    }
    
    public boolean isSetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BUTTON$78) != null;
        }
    }
    
    public void setButton(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BUTTON$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BUTTON$78);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetButton(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.BUTTON$78);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.BUTTON$78);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BUTTON$78);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
        }
    }
    
    public boolean isSetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.USERHIDDEN$80) != null;
        }
    }
    
    public void setUserhidden(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserhidden(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.USERHIDDEN$80);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.USERHIDDEN$80);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BULLET$82);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.BULLET$82);
        }
    }
    
    public boolean isSetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BULLET$82) != null;
        }
    }
    
    public void setBullet(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BULLET$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BULLET$82);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBullet(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.BULLET$82);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.BULLET$82);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BULLET$82);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HR$84);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HR$84);
        }
    }
    
    public boolean isSetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HR$84) != null;
        }
    }
    
    public void setHr(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HR$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HR$84);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHr(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HR$84);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.HR$84);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HR$84);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRSTD$86);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HRSTD$86);
        }
    }
    
    public boolean isSetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HRSTD$86) != null;
        }
    }
    
    public void setHrstd(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRSTD$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HRSTD$86);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrstd(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HRSTD$86);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.HRSTD$86);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HRSTD$86);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
        }
    }
    
    public boolean isSetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HRNOSHADE$88) != null;
        }
    }
    
    public void setHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.HRNOSHADE$88);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HRNOSHADE$88);
        }
    }
    
    public float getHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRPCT$90);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_attribute_user(CTShapetypeImpl.HRPCT$90);
        }
    }
    
    public boolean isSetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HRPCT$90) != null;
        }
    }
    
    public void setHrpct(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRPCT$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HRPCT$90);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetHrpct(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_attribute_user(CTShapetypeImpl.HRPCT$90);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_attribute_user(CTShapetypeImpl.HRPCT$90);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HRPCT$90);
        }
    }
    
    public STHrAlign.Enum getHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRALIGN$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapetypeImpl.HRALIGN$92);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STHrAlign.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHrAlign xgetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHrAlign stHrAlign = (STHrAlign)this.get_store().find_attribute_user(CTShapetypeImpl.HRALIGN$92);
            if (stHrAlign == null) {
                stHrAlign = (STHrAlign)this.get_default_attribute_value(CTShapetypeImpl.HRALIGN$92);
            }
            return stHrAlign;
        }
    }
    
    public boolean isSetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.HRALIGN$92) != null;
        }
    }
    
    public void setHralign(final STHrAlign.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.HRALIGN$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.HRALIGN$92);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHralign(final STHrAlign stHrAlign) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHrAlign stHrAlign2 = (STHrAlign)this.get_store().find_attribute_user(CTShapetypeImpl.HRALIGN$92);
            if (stHrAlign2 == null) {
                stHrAlign2 = (STHrAlign)this.get_store().add_attribute_user(CTShapetypeImpl.HRALIGN$92);
            }
            stHrAlign2.set((XmlObject)stHrAlign);
        }
    }
    
    public void unsetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.HRALIGN$92);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
        }
    }
    
    public boolean isSetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWINCELL$94) != null;
        }
    }
    
    public void setAllowincell(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowincell(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.ALLOWINCELL$94);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ALLOWINCELL$94);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
        }
    }
    
    public boolean isSetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96) != null;
        }
    }
    
    public void setAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.ALLOWOVERLAP$96);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ALLOWOVERLAP$96);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.USERDRAWN$98);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.USERDRAWN$98);
        }
    }
    
    public boolean isSetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.USERDRAWN$98) != null;
        }
    }
    
    public void setUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.USERDRAWN$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.USERDRAWN$98);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.USERDRAWN$98);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.USERDRAWN$98);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.USERDRAWN$98);
        }
    }
    
    public String getBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
        }
    }
    
    public boolean isSetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100) != null;
        }
    }
    
    public void setBordertopcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBordertopcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERTOPCOLOR$100);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BORDERTOPCOLOR$100);
        }
    }
    
    public String getBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
        }
    }
    
    public boolean isSetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102) != null;
        }
    }
    
    public void setBorderleftcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderleftcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERLEFTCOLOR$102);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BORDERLEFTCOLOR$102);
        }
    }
    
    public String getBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
        }
    }
    
    public boolean isSetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104) != null;
        }
    }
    
    public void setBorderbottomcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderbottomcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BORDERBOTTOMCOLOR$104);
        }
    }
    
    public String getBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
        }
    }
    
    public boolean isSetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106) != null;
        }
    }
    
    public void setBorderrightcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderrightcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BORDERRIGHTCOLOR$106);
        }
    }
    
    public BigInteger getDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
        }
    }
    
    public boolean isSetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUT$108) != null;
        }
    }
    
    public void setDgmlayout(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayout(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTShapetypeImpl.DGMLAYOUT$108);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.DGMLAYOUT$108);
        }
    }
    
    public BigInteger getDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
        }
    }
    
    public boolean isSetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.DGMNODEKIND$110) != null;
        }
    }
    
    public void setDgmnodekind(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmnodekind(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTShapetypeImpl.DGMNODEKIND$110);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.DGMNODEKIND$110);
        }
    }
    
    public BigInteger getDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
        }
    }
    
    public boolean isSetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112) != null;
        }
    }
    
    public void setDgmlayoutmru(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayoutmru(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTShapetypeImpl.DGMLAYOUTMRU$112);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.DGMLAYOUTMRU$112);
        }
    }
    
    public STInsetMode.Enum getInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.INSETMODE$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapetypeImpl.INSETMODE$114);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STInsetMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STInsetMode xgetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode = (STInsetMode)this.get_store().find_attribute_user(CTShapetypeImpl.INSETMODE$114);
            if (stInsetMode == null) {
                stInsetMode = (STInsetMode)this.get_default_attribute_value(CTShapetypeImpl.INSETMODE$114);
            }
            return stInsetMode;
        }
    }
    
    public boolean isSetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.INSETMODE$114) != null;
        }
    }
    
    public void setInsetmode(final STInsetMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.INSETMODE$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.INSETMODE$114);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetmode(final STInsetMode stInsetMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode2 = (STInsetMode)this.get_store().find_attribute_user(CTShapetypeImpl.INSETMODE$114);
            if (stInsetMode2 == null) {
                stInsetMode2 = (STInsetMode)this.get_store().add_attribute_user(CTShapetypeImpl.INSETMODE$114);
            }
            stInsetMode2.set((XmlObject)stInsetMode);
        }
    }
    
    public void unsetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.INSETMODE$114);
        }
    }
    
    public String getChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
        }
    }
    
    public boolean isSetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.CHROMAKEY$116) != null;
        }
    }
    
    public void setChromakey(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetChromakey(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTShapetypeImpl.CHROMAKEY$116);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.CHROMAKEY$116);
        }
    }
    
    public STTrueFalse.Enum getFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FILLED$118);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.FILLED$118);
        }
    }
    
    public boolean isSetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.FILLED$118) != null;
        }
    }
    
    public void setFilled(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FILLED$118);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.FILLED$118);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFilled(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.FILLED$118);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.FILLED$118);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.FILLED$118);
        }
    }
    
    public String getFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
        }
    }
    
    public boolean isSetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.FILLCOLOR$120) != null;
        }
    }
    
    public void setFillcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFillcolor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTShapetypeImpl.FILLCOLOR$120);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.FILLCOLOR$120);
        }
    }
    
    public String getOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OPACITY$122);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.OPACITY$122);
        }
    }
    
    public boolean isSetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.OPACITY$122) != null;
        }
    }
    
    public void setOpacity(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OPACITY$122);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.OPACITY$122);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.OPACITY$122);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.OPACITY$122);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.OPACITY$122);
        }
    }
    
    public STTrueFalse.Enum getStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKED$124);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.STROKED$124);
        }
    }
    
    public boolean isSetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.STROKED$124) != null;
        }
    }
    
    public void setStroked(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKED$124);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.STROKED$124);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStroked(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.STROKED$124);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.STROKED$124);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.STROKED$124);
        }
    }
    
    public String getStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
        }
    }
    
    public boolean isSetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.STROKECOLOR$126) != null;
        }
    }
    
    public void setStrokecolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStrokecolor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTShapetypeImpl.STROKECOLOR$126);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.STROKECOLOR$126);
        }
    }
    
    public String getStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
        }
    }
    
    public boolean isSetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128) != null;
        }
    }
    
    public void setStrokeweight(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStrokeweight(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.STROKEWEIGHT$128);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.STROKEWEIGHT$128);
        }
    }
    
    public STTrueFalse.Enum getInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.INSETPEN$130);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.INSETPEN$130);
        }
    }
    
    public boolean isSetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.INSETPEN$130) != null;
        }
    }
    
    public void setInsetpen(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.INSETPEN$130);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.INSETPEN$130);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetpen(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.INSETPEN$130);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.INSETPEN$130);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.INSETPEN$130);
        }
    }
    
    public float getSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.SPT$132);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_attribute_user(CTShapetypeImpl.SPT$132);
        }
    }
    
    public boolean isSetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.SPT$132) != null;
        }
    }
    
    public void setSpt(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.SPT$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.SPT$132);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetSpt(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_attribute_user(CTShapetypeImpl.SPT$132);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_attribute_user(CTShapetypeImpl.SPT$132);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.SPT$132);
        }
    }
    
    public STConnectorType.Enum getConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTShapetypeImpl.CONNECTORTYPE$134);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STConnectorType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STConnectorType xgetConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STConnectorType stConnectorType = (STConnectorType)this.get_store().find_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            if (stConnectorType == null) {
                stConnectorType = (STConnectorType)this.get_default_attribute_value(CTShapetypeImpl.CONNECTORTYPE$134);
            }
            return stConnectorType;
        }
    }
    
    public boolean isSetConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134) != null;
        }
    }
    
    public void setConnectortype(final STConnectorType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetConnectortype(final STConnectorType stConnectorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STConnectorType stConnectorType2 = (STConnectorType)this.get_store().find_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            if (stConnectorType2 == null) {
                stConnectorType2 = (STConnectorType)this.get_store().add_attribute_user(CTShapetypeImpl.CONNECTORTYPE$134);
            }
            stConnectorType2.set((XmlObject)stConnectorType);
        }
    }
    
    public void unsetConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.CONNECTORTYPE$134);
        }
    }
    
    public STBWMode.Enum getBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWMODE$136);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWMODE$136);
        }
    }
    
    public boolean isSetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BWMODE$136) != null;
        }
    }
    
    public void setBwmode(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWMODE$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BWMODE$136);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwmode(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWMODE$136);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTShapetypeImpl.BWMODE$136);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BWMODE$136);
        }
    }
    
    public STBWMode.Enum getBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWPURE$138);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWPURE$138);
        }
    }
    
    public boolean isSetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BWPURE$138) != null;
        }
    }
    
    public void setBwpure(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWPURE$138);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BWPURE$138);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwpure(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWPURE$138);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTShapetypeImpl.BWPURE$138);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BWPURE$138);
        }
    }
    
    public STBWMode.Enum getBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWNORMAL$140);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWNORMAL$140);
        }
    }
    
    public boolean isSetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.BWNORMAL$140) != null;
        }
    }
    
    public void setBwnormal(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.BWNORMAL$140);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.BWNORMAL$140);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwnormal(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTShapetypeImpl.BWNORMAL$140);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTShapetypeImpl.BWNORMAL$140);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.BWNORMAL$140);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FORCEDASH$142);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.FORCEDASH$142);
        }
    }
    
    public boolean isSetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.FORCEDASH$142) != null;
        }
    }
    
    public void setForcedash(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.FORCEDASH$142);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.FORCEDASH$142);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetForcedash(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.FORCEDASH$142);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.FORCEDASH$142);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.FORCEDASH$142);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OLEICON$144);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.OLEICON$144);
        }
    }
    
    public boolean isSetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.OLEICON$144) != null;
        }
    }
    
    public void setOleicon(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OLEICON$144);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.OLEICON$144);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOleicon(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.OLEICON$144);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.OLEICON$144);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.OLEICON$144);
        }
    }
    
    public STTrueFalseBlank.Enum getOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OLE$146);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalseBlank xgetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().find_attribute_user(CTShapetypeImpl.OLE$146);
        }
    }
    
    public boolean isSetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.OLE$146) != null;
        }
    }
    
    public void setOle(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.OLE$146);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.OLE$146);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOle(final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_attribute_user(CTShapetypeImpl.OLE$146);
            if (stTrueFalseBlank2 == null) {
                stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().add_attribute_user(CTShapetypeImpl.OLE$146);
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void unsetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.OLE$146);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
        }
    }
    
    public boolean isSetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148) != null;
        }
    }
    
    public void setPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.PREFERRELATIVE$148);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.PREFERRELATIVE$148);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
        }
    }
    
    public boolean isSetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150) != null;
        }
    }
    
    public void setCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.CLIPTOWRAP$150);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.CLIPTOWRAP$150);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLIP$152);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.CLIP$152);
        }
    }
    
    public boolean isSetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.CLIP$152) != null;
        }
    }
    
    public void setClip(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.CLIP$152);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.CLIP$152);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetClip(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTShapetypeImpl.CLIP$152);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTShapetypeImpl.CLIP$152);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.CLIP$152);
        }
    }
    
    public String getAdj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ADJ$154);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAdj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ADJ$154);
        }
    }
    
    public boolean isSetAdj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.ADJ$154) != null;
        }
    }
    
    public void setAdj(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.ADJ$154);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.ADJ$154);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAdj(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.ADJ$154);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.ADJ$154);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAdj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.ADJ$154);
        }
    }
    
    public String getPath2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PATH2$156);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPath2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.PATH2$156);
        }
    }
    
    public boolean isSetPath2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.PATH2$156) != null;
        }
    }
    
    public void setPath2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.PATH2$156);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.PATH2$156);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPath2(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.PATH2$156);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.PATH2$156);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPath2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.PATH2$156);
        }
    }
    
    public String getMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.MASTER$158);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.MASTER$158);
        }
    }
    
    public boolean isSetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapetypeImpl.MASTER$158) != null;
        }
    }
    
    public void setMaster(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapetypeImpl.MASTER$158);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapetypeImpl.MASTER$158);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetMaster(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTShapetypeImpl.MASTER$158);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTShapetypeImpl.MASTER$158);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapetypeImpl.MASTER$158);
        }
    }
    
    static {
        PATH$0 = new QName("urn:schemas-microsoft-com:vml", "path");
        FORMULAS$2 = new QName("urn:schemas-microsoft-com:vml", "formulas");
        HANDLES$4 = new QName("urn:schemas-microsoft-com:vml", "handles");
        FILL$6 = new QName("urn:schemas-microsoft-com:vml", "fill");
        STROKE$8 = new QName("urn:schemas-microsoft-com:vml", "stroke");
        SHADOW$10 = new QName("urn:schemas-microsoft-com:vml", "shadow");
        TEXTBOX$12 = new QName("urn:schemas-microsoft-com:vml", "textbox");
        TEXTPATH$14 = new QName("urn:schemas-microsoft-com:vml", "textpath");
        IMAGEDATA$16 = new QName("urn:schemas-microsoft-com:vml", "imagedata");
        SKEW$18 = new QName("urn:schemas-microsoft-com:office:office", "skew");
        EXTRUSION$20 = new QName("urn:schemas-microsoft-com:office:office", "extrusion");
        CALLOUT$22 = new QName("urn:schemas-microsoft-com:office:office", "callout");
        LOCK$24 = new QName("urn:schemas-microsoft-com:office:office", "lock");
        CLIPPATH$26 = new QName("urn:schemas-microsoft-com:office:office", "clippath");
        SIGNATURELINE$28 = new QName("urn:schemas-microsoft-com:office:office", "signatureline");
        WRAP$30 = new QName("urn:schemas-microsoft-com:office:word", "wrap");
        ANCHORLOCK$32 = new QName("urn:schemas-microsoft-com:office:word", "anchorlock");
        BORDERTOP$34 = new QName("urn:schemas-microsoft-com:office:word", "bordertop");
        BORDERBOTTOM$36 = new QName("urn:schemas-microsoft-com:office:word", "borderbottom");
        BORDERLEFT$38 = new QName("urn:schemas-microsoft-com:office:word", "borderleft");
        BORDERRIGHT$40 = new QName("urn:schemas-microsoft-com:office:word", "borderright");
        CLIENTDATA$42 = new QName("urn:schemas-microsoft-com:office:excel", "ClientData");
        TEXTDATA$44 = new QName("urn:schemas-microsoft-com:office:powerpoint", "textdata");
        COMPLEX$46 = new QName("urn:schemas-microsoft-com:office:office", "complex");
        ID$48 = new QName("", "id");
        STYLE$50 = new QName("", "style");
        HREF$52 = new QName("", "href");
        TARGET$54 = new QName("", "target");
        CLASS1$56 = new QName("", "class");
        TITLE$58 = new QName("", "title");
        ALT$60 = new QName("", "alt");
        COORDSIZE$62 = new QName("", "coordsize");
        COORDORIGIN$64 = new QName("", "coordorigin");
        WRAPCOORDS$66 = new QName("", "wrapcoords");
        PRINT$68 = new QName("", "print");
        SPID$70 = new QName("urn:schemas-microsoft-com:office:office", "spid");
        ONED$72 = new QName("urn:schemas-microsoft-com:office:office", "oned");
        REGROUPID$74 = new QName("urn:schemas-microsoft-com:office:office", "regroupid");
        DOUBLECLICKNOTIFY$76 = new QName("urn:schemas-microsoft-com:office:office", "doubleclicknotify");
        BUTTON$78 = new QName("urn:schemas-microsoft-com:office:office", "button");
        USERHIDDEN$80 = new QName("urn:schemas-microsoft-com:office:office", "userhidden");
        BULLET$82 = new QName("urn:schemas-microsoft-com:office:office", "bullet");
        HR$84 = new QName("urn:schemas-microsoft-com:office:office", "hr");
        HRSTD$86 = new QName("urn:schemas-microsoft-com:office:office", "hrstd");
        HRNOSHADE$88 = new QName("urn:schemas-microsoft-com:office:office", "hrnoshade");
        HRPCT$90 = new QName("urn:schemas-microsoft-com:office:office", "hrpct");
        HRALIGN$92 = new QName("urn:schemas-microsoft-com:office:office", "hralign");
        ALLOWINCELL$94 = new QName("urn:schemas-microsoft-com:office:office", "allowincell");
        ALLOWOVERLAP$96 = new QName("urn:schemas-microsoft-com:office:office", "allowoverlap");
        USERDRAWN$98 = new QName("urn:schemas-microsoft-com:office:office", "userdrawn");
        BORDERTOPCOLOR$100 = new QName("urn:schemas-microsoft-com:office:office", "bordertopcolor");
        BORDERLEFTCOLOR$102 = new QName("urn:schemas-microsoft-com:office:office", "borderleftcolor");
        BORDERBOTTOMCOLOR$104 = new QName("urn:schemas-microsoft-com:office:office", "borderbottomcolor");
        BORDERRIGHTCOLOR$106 = new QName("urn:schemas-microsoft-com:office:office", "borderrightcolor");
        DGMLAYOUT$108 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayout");
        DGMNODEKIND$110 = new QName("urn:schemas-microsoft-com:office:office", "dgmnodekind");
        DGMLAYOUTMRU$112 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayoutmru");
        INSETMODE$114 = new QName("urn:schemas-microsoft-com:office:office", "insetmode");
        CHROMAKEY$116 = new QName("", "chromakey");
        FILLED$118 = new QName("", "filled");
        FILLCOLOR$120 = new QName("", "fillcolor");
        OPACITY$122 = new QName("", "opacity");
        STROKED$124 = new QName("", "stroked");
        STROKECOLOR$126 = new QName("", "strokecolor");
        STROKEWEIGHT$128 = new QName("", "strokeweight");
        INSETPEN$130 = new QName("", "insetpen");
        SPT$132 = new QName("urn:schemas-microsoft-com:office:office", "spt");
        CONNECTORTYPE$134 = new QName("urn:schemas-microsoft-com:office:office", "connectortype");
        BWMODE$136 = new QName("urn:schemas-microsoft-com:office:office", "bwmode");
        BWPURE$138 = new QName("urn:schemas-microsoft-com:office:office", "bwpure");
        BWNORMAL$140 = new QName("urn:schemas-microsoft-com:office:office", "bwnormal");
        FORCEDASH$142 = new QName("urn:schemas-microsoft-com:office:office", "forcedash");
        OLEICON$144 = new QName("urn:schemas-microsoft-com:office:office", "oleicon");
        OLE$146 = new QName("urn:schemas-microsoft-com:office:office", "ole");
        PREFERRELATIVE$148 = new QName("urn:schemas-microsoft-com:office:office", "preferrelative");
        CLIPTOWRAP$150 = new QName("urn:schemas-microsoft-com:office:office", "cliptowrap");
        CLIP$152 = new QName("urn:schemas-microsoft-com:office:office", "clip");
        ADJ$154 = new QName("", "adj");
        PATH2$156 = new QName("", "path");
        MASTER$158 = new QName("urn:schemas-microsoft-com:office:office", "master");
    }
}
