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
import com.microsoft.schemas.vml.CTRect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRectImpl extends XmlComplexContentImpl implements CTRect
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
    private static final QName ID$46;
    private static final QName STYLE$48;
    private static final QName HREF$50;
    private static final QName TARGET$52;
    private static final QName CLASS1$54;
    private static final QName TITLE$56;
    private static final QName ALT$58;
    private static final QName COORDSIZE$60;
    private static final QName COORDORIGIN$62;
    private static final QName WRAPCOORDS$64;
    private static final QName PRINT$66;
    private static final QName SPID$68;
    private static final QName ONED$70;
    private static final QName REGROUPID$72;
    private static final QName DOUBLECLICKNOTIFY$74;
    private static final QName BUTTON$76;
    private static final QName USERHIDDEN$78;
    private static final QName BULLET$80;
    private static final QName HR$82;
    private static final QName HRSTD$84;
    private static final QName HRNOSHADE$86;
    private static final QName HRPCT$88;
    private static final QName HRALIGN$90;
    private static final QName ALLOWINCELL$92;
    private static final QName ALLOWOVERLAP$94;
    private static final QName USERDRAWN$96;
    private static final QName BORDERTOPCOLOR$98;
    private static final QName BORDERLEFTCOLOR$100;
    private static final QName BORDERBOTTOMCOLOR$102;
    private static final QName BORDERRIGHTCOLOR$104;
    private static final QName DGMLAYOUT$106;
    private static final QName DGMNODEKIND$108;
    private static final QName DGMLAYOUTMRU$110;
    private static final QName INSETMODE$112;
    private static final QName CHROMAKEY$114;
    private static final QName FILLED$116;
    private static final QName FILLCOLOR$118;
    private static final QName OPACITY$120;
    private static final QName STROKED$122;
    private static final QName STROKECOLOR$124;
    private static final QName STROKEWEIGHT$126;
    private static final QName INSETPEN$128;
    private static final QName SPT$130;
    private static final QName CONNECTORTYPE$132;
    private static final QName BWMODE$134;
    private static final QName BWPURE$136;
    private static final QName BWNORMAL$138;
    private static final QName FORCEDASH$140;
    private static final QName OLEICON$142;
    private static final QName OLE$144;
    private static final QName PREFERRELATIVE$146;
    private static final QName CLIPTOWRAP$148;
    private static final QName CLIP$150;
    
    public CTRectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPath> getPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PathList extends AbstractList<CTPath>
            {
                @Override
                public CTPath get(final int n) {
                    return CTRectImpl.this.getPathArray(n);
                }
                
                @Override
                public CTPath set(final int n, final CTPath ctPath) {
                    final CTPath pathArray = CTRectImpl.this.getPathArray(n);
                    CTRectImpl.this.setPathArray(n, ctPath);
                    return pathArray;
                }
                
                @Override
                public void add(final int n, final CTPath ctPath) {
                    CTRectImpl.this.insertNewPath(n).set((XmlObject)ctPath);
                }
                
                @Override
                public CTPath remove(final int n) {
                    final CTPath pathArray = CTRectImpl.this.getPathArray(n);
                    CTRectImpl.this.removePath(n);
                    return pathArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfPathArray();
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
            this.get_store().find_all_element_users(CTRectImpl.PATH$0, (List)list);
            final CTPath[] array = new CTPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath getPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath ctPath = (CTPath)this.get_store().find_element_user(CTRectImpl.PATH$0, n);
            if (ctPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath;
        }
    }
    
    public int sizeOfPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.PATH$0);
        }
    }
    
    public void setPathArray(final CTPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.PATH$0);
    }
    
    public void setPathArray(final int n, final CTPath ctPath) {
        this.generatedSetterHelperImpl((XmlObject)ctPath, CTRectImpl.PATH$0, n, (short)2);
    }
    
    public CTPath insertNewPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().insert_element_user(CTRectImpl.PATH$0, n);
        }
    }
    
    public CTPath addNewPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().add_element_user(CTRectImpl.PATH$0);
        }
    }
    
    public void removePath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.PATH$0, n);
        }
    }
    
    public List<CTFormulas> getFormulasList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FormulasList extends AbstractList<CTFormulas>
            {
                @Override
                public CTFormulas get(final int n) {
                    return CTRectImpl.this.getFormulasArray(n);
                }
                
                @Override
                public CTFormulas set(final int n, final CTFormulas ctFormulas) {
                    final CTFormulas formulasArray = CTRectImpl.this.getFormulasArray(n);
                    CTRectImpl.this.setFormulasArray(n, ctFormulas);
                    return formulasArray;
                }
                
                @Override
                public void add(final int n, final CTFormulas ctFormulas) {
                    CTRectImpl.this.insertNewFormulas(n).set((XmlObject)ctFormulas);
                }
                
                @Override
                public CTFormulas remove(final int n) {
                    final CTFormulas formulasArray = CTRectImpl.this.getFormulasArray(n);
                    CTRectImpl.this.removeFormulas(n);
                    return formulasArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfFormulasArray();
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
            this.get_store().find_all_element_users(CTRectImpl.FORMULAS$2, (List)list);
            final CTFormulas[] array = new CTFormulas[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFormulas getFormulasArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFormulas ctFormulas = (CTFormulas)this.get_store().find_element_user(CTRectImpl.FORMULAS$2, n);
            if (ctFormulas == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFormulas;
        }
    }
    
    public int sizeOfFormulasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.FORMULAS$2);
        }
    }
    
    public void setFormulasArray(final CTFormulas[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.FORMULAS$2);
    }
    
    public void setFormulasArray(final int n, final CTFormulas ctFormulas) {
        this.generatedSetterHelperImpl((XmlObject)ctFormulas, CTRectImpl.FORMULAS$2, n, (short)2);
    }
    
    public CTFormulas insertNewFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().insert_element_user(CTRectImpl.FORMULAS$2, n);
        }
    }
    
    public CTFormulas addNewFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().add_element_user(CTRectImpl.FORMULAS$2);
        }
    }
    
    public void removeFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.FORMULAS$2, n);
        }
    }
    
    public List<CTHandles> getHandlesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HandlesList extends AbstractList<CTHandles>
            {
                @Override
                public CTHandles get(final int n) {
                    return CTRectImpl.this.getHandlesArray(n);
                }
                
                @Override
                public CTHandles set(final int n, final CTHandles ctHandles) {
                    final CTHandles handlesArray = CTRectImpl.this.getHandlesArray(n);
                    CTRectImpl.this.setHandlesArray(n, ctHandles);
                    return handlesArray;
                }
                
                @Override
                public void add(final int n, final CTHandles ctHandles) {
                    CTRectImpl.this.insertNewHandles(n).set((XmlObject)ctHandles);
                }
                
                @Override
                public CTHandles remove(final int n) {
                    final CTHandles handlesArray = CTRectImpl.this.getHandlesArray(n);
                    CTRectImpl.this.removeHandles(n);
                    return handlesArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfHandlesArray();
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
            this.get_store().find_all_element_users(CTRectImpl.HANDLES$4, (List)list);
            final CTHandles[] array = new CTHandles[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHandles getHandlesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHandles ctHandles = (CTHandles)this.get_store().find_element_user(CTRectImpl.HANDLES$4, n);
            if (ctHandles == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHandles;
        }
    }
    
    public int sizeOfHandlesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.HANDLES$4);
        }
    }
    
    public void setHandlesArray(final CTHandles[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.HANDLES$4);
    }
    
    public void setHandlesArray(final int n, final CTHandles ctHandles) {
        this.generatedSetterHelperImpl((XmlObject)ctHandles, CTRectImpl.HANDLES$4, n, (short)2);
    }
    
    public CTHandles insertNewHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().insert_element_user(CTRectImpl.HANDLES$4, n);
        }
    }
    
    public CTHandles addNewHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().add_element_user(CTRectImpl.HANDLES$4);
        }
    }
    
    public void removeHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.HANDLES$4, n);
        }
    }
    
    public List<CTFill> getFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillList extends AbstractList<CTFill>
            {
                @Override
                public CTFill get(final int n) {
                    return CTRectImpl.this.getFillArray(n);
                }
                
                @Override
                public CTFill set(final int n, final CTFill ctFill) {
                    final CTFill fillArray = CTRectImpl.this.getFillArray(n);
                    CTRectImpl.this.setFillArray(n, ctFill);
                    return fillArray;
                }
                
                @Override
                public void add(final int n, final CTFill ctFill) {
                    CTRectImpl.this.insertNewFill(n).set((XmlObject)ctFill);
                }
                
                @Override
                public CTFill remove(final int n) {
                    final CTFill fillArray = CTRectImpl.this.getFillArray(n);
                    CTRectImpl.this.removeFill(n);
                    return fillArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfFillArray();
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
            this.get_store().find_all_element_users(CTRectImpl.FILL$6, (List)list);
            final CTFill[] array = new CTFill[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFill getFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFill ctFill = (CTFill)this.get_store().find_element_user(CTRectImpl.FILL$6, n);
            if (ctFill == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFill;
        }
    }
    
    public int sizeOfFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.FILL$6);
        }
    }
    
    public void setFillArray(final CTFill[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.FILL$6);
    }
    
    public void setFillArray(final int n, final CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTRectImpl.FILL$6, n, (short)2);
    }
    
    public CTFill insertNewFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().insert_element_user(CTRectImpl.FILL$6, n);
        }
    }
    
    public CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().add_element_user(CTRectImpl.FILL$6);
        }
    }
    
    public void removeFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.FILL$6, n);
        }
    }
    
    public List<CTStroke> getStrokeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StrokeList extends AbstractList<CTStroke>
            {
                @Override
                public CTStroke get(final int n) {
                    return CTRectImpl.this.getStrokeArray(n);
                }
                
                @Override
                public CTStroke set(final int n, final CTStroke ctStroke) {
                    final CTStroke strokeArray = CTRectImpl.this.getStrokeArray(n);
                    CTRectImpl.this.setStrokeArray(n, ctStroke);
                    return strokeArray;
                }
                
                @Override
                public void add(final int n, final CTStroke ctStroke) {
                    CTRectImpl.this.insertNewStroke(n).set((XmlObject)ctStroke);
                }
                
                @Override
                public CTStroke remove(final int n) {
                    final CTStroke strokeArray = CTRectImpl.this.getStrokeArray(n);
                    CTRectImpl.this.removeStroke(n);
                    return strokeArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfStrokeArray();
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
            this.get_store().find_all_element_users(CTRectImpl.STROKE$8, (List)list);
            final CTStroke[] array = new CTStroke[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStroke getStrokeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStroke ctStroke = (CTStroke)this.get_store().find_element_user(CTRectImpl.STROKE$8, n);
            if (ctStroke == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStroke;
        }
    }
    
    public int sizeOfStrokeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.STROKE$8);
        }
    }
    
    public void setStrokeArray(final CTStroke[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.STROKE$8);
    }
    
    public void setStrokeArray(final int n, final CTStroke ctStroke) {
        this.generatedSetterHelperImpl((XmlObject)ctStroke, CTRectImpl.STROKE$8, n, (short)2);
    }
    
    public CTStroke insertNewStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().insert_element_user(CTRectImpl.STROKE$8, n);
        }
    }
    
    public CTStroke addNewStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().add_element_user(CTRectImpl.STROKE$8);
        }
    }
    
    public void removeStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.STROKE$8, n);
        }
    }
    
    public List<CTShadow> getShadowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadowList extends AbstractList<CTShadow>
            {
                @Override
                public CTShadow get(final int n) {
                    return CTRectImpl.this.getShadowArray(n);
                }
                
                @Override
                public CTShadow set(final int n, final CTShadow ctShadow) {
                    final CTShadow shadowArray = CTRectImpl.this.getShadowArray(n);
                    CTRectImpl.this.setShadowArray(n, ctShadow);
                    return shadowArray;
                }
                
                @Override
                public void add(final int n, final CTShadow ctShadow) {
                    CTRectImpl.this.insertNewShadow(n).set((XmlObject)ctShadow);
                }
                
                @Override
                public CTShadow remove(final int n) {
                    final CTShadow shadowArray = CTRectImpl.this.getShadowArray(n);
                    CTRectImpl.this.removeShadow(n);
                    return shadowArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfShadowArray();
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
            this.get_store().find_all_element_users(CTRectImpl.SHADOW$10, (List)list);
            final CTShadow[] array = new CTShadow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShadow getShadowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShadow ctShadow = (CTShadow)this.get_store().find_element_user(CTRectImpl.SHADOW$10, n);
            if (ctShadow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShadow;
        }
    }
    
    public int sizeOfShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.SHADOW$10);
        }
    }
    
    public void setShadowArray(final CTShadow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.SHADOW$10);
    }
    
    public void setShadowArray(final int n, final CTShadow ctShadow) {
        this.generatedSetterHelperImpl((XmlObject)ctShadow, CTRectImpl.SHADOW$10, n, (short)2);
    }
    
    public CTShadow insertNewShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().insert_element_user(CTRectImpl.SHADOW$10, n);
        }
    }
    
    public CTShadow addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().add_element_user(CTRectImpl.SHADOW$10);
        }
    }
    
    public void removeShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.SHADOW$10, n);
        }
    }
    
    public List<CTTextbox> getTextboxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextboxList extends AbstractList<CTTextbox>
            {
                @Override
                public CTTextbox get(final int n) {
                    return CTRectImpl.this.getTextboxArray(n);
                }
                
                @Override
                public CTTextbox set(final int n, final CTTextbox ctTextbox) {
                    final CTTextbox textboxArray = CTRectImpl.this.getTextboxArray(n);
                    CTRectImpl.this.setTextboxArray(n, ctTextbox);
                    return textboxArray;
                }
                
                @Override
                public void add(final int n, final CTTextbox ctTextbox) {
                    CTRectImpl.this.insertNewTextbox(n).set((XmlObject)ctTextbox);
                }
                
                @Override
                public CTTextbox remove(final int n) {
                    final CTTextbox textboxArray = CTRectImpl.this.getTextboxArray(n);
                    CTRectImpl.this.removeTextbox(n);
                    return textboxArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfTextboxArray();
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
            this.get_store().find_all_element_users(CTRectImpl.TEXTBOX$12, (List)list);
            final CTTextbox[] array = new CTTextbox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextbox getTextboxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextbox ctTextbox = (CTTextbox)this.get_store().find_element_user(CTRectImpl.TEXTBOX$12, n);
            if (ctTextbox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextbox;
        }
    }
    
    public int sizeOfTextboxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.TEXTBOX$12);
        }
    }
    
    public void setTextboxArray(final CTTextbox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.TEXTBOX$12);
    }
    
    public void setTextboxArray(final int n, final CTTextbox ctTextbox) {
        this.generatedSetterHelperImpl((XmlObject)ctTextbox, CTRectImpl.TEXTBOX$12, n, (short)2);
    }
    
    public CTTextbox insertNewTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().insert_element_user(CTRectImpl.TEXTBOX$12, n);
        }
    }
    
    public CTTextbox addNewTextbox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().add_element_user(CTRectImpl.TEXTBOX$12);
        }
    }
    
    public void removeTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.TEXTBOX$12, n);
        }
    }
    
    public List<CTTextPath> getTextpathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextpathList extends AbstractList<CTTextPath>
            {
                @Override
                public CTTextPath get(final int n) {
                    return CTRectImpl.this.getTextpathArray(n);
                }
                
                @Override
                public CTTextPath set(final int n, final CTTextPath ctTextPath) {
                    final CTTextPath textpathArray = CTRectImpl.this.getTextpathArray(n);
                    CTRectImpl.this.setTextpathArray(n, ctTextPath);
                    return textpathArray;
                }
                
                @Override
                public void add(final int n, final CTTextPath ctTextPath) {
                    CTRectImpl.this.insertNewTextpath(n).set((XmlObject)ctTextPath);
                }
                
                @Override
                public CTTextPath remove(final int n) {
                    final CTTextPath textpathArray = CTRectImpl.this.getTextpathArray(n);
                    CTRectImpl.this.removeTextpath(n);
                    return textpathArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfTextpathArray();
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
            this.get_store().find_all_element_users(CTRectImpl.TEXTPATH$14, (List)list);
            final CTTextPath[] array = new CTTextPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextPath getTextpathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextPath ctTextPath = (CTTextPath)this.get_store().find_element_user(CTRectImpl.TEXTPATH$14, n);
            if (ctTextPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextPath;
        }
    }
    
    public int sizeOfTextpathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.TEXTPATH$14);
        }
    }
    
    public void setTextpathArray(final CTTextPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.TEXTPATH$14);
    }
    
    public void setTextpathArray(final int n, final CTTextPath ctTextPath) {
        this.generatedSetterHelperImpl((XmlObject)ctTextPath, CTRectImpl.TEXTPATH$14, n, (short)2);
    }
    
    public CTTextPath insertNewTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().insert_element_user(CTRectImpl.TEXTPATH$14, n);
        }
    }
    
    public CTTextPath addNewTextpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().add_element_user(CTRectImpl.TEXTPATH$14);
        }
    }
    
    public void removeTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.TEXTPATH$14, n);
        }
    }
    
    public List<CTImageData> getImagedataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ImagedataList extends AbstractList<CTImageData>
            {
                @Override
                public CTImageData get(final int n) {
                    return CTRectImpl.this.getImagedataArray(n);
                }
                
                @Override
                public CTImageData set(final int n, final CTImageData ctImageData) {
                    final CTImageData imagedataArray = CTRectImpl.this.getImagedataArray(n);
                    CTRectImpl.this.setImagedataArray(n, ctImageData);
                    return imagedataArray;
                }
                
                @Override
                public void add(final int n, final CTImageData ctImageData) {
                    CTRectImpl.this.insertNewImagedata(n).set((XmlObject)ctImageData);
                }
                
                @Override
                public CTImageData remove(final int n) {
                    final CTImageData imagedataArray = CTRectImpl.this.getImagedataArray(n);
                    CTRectImpl.this.removeImagedata(n);
                    return imagedataArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfImagedataArray();
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
            this.get_store().find_all_element_users(CTRectImpl.IMAGEDATA$16, (List)list);
            final CTImageData[] array = new CTImageData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTImageData getImagedataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTImageData ctImageData = (CTImageData)this.get_store().find_element_user(CTRectImpl.IMAGEDATA$16, n);
            if (ctImageData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctImageData;
        }
    }
    
    public int sizeOfImagedataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.IMAGEDATA$16);
        }
    }
    
    public void setImagedataArray(final CTImageData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.IMAGEDATA$16);
    }
    
    public void setImagedataArray(final int n, final CTImageData ctImageData) {
        this.generatedSetterHelperImpl((XmlObject)ctImageData, CTRectImpl.IMAGEDATA$16, n, (short)2);
    }
    
    public CTImageData insertNewImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().insert_element_user(CTRectImpl.IMAGEDATA$16, n);
        }
    }
    
    public CTImageData addNewImagedata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().add_element_user(CTRectImpl.IMAGEDATA$16);
        }
    }
    
    public void removeImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.IMAGEDATA$16, n);
        }
    }
    
    public List<CTSkew> getSkewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SkewList extends AbstractList<CTSkew>
            {
                @Override
                public CTSkew get(final int n) {
                    return CTRectImpl.this.getSkewArray(n);
                }
                
                @Override
                public CTSkew set(final int n, final CTSkew ctSkew) {
                    final CTSkew skewArray = CTRectImpl.this.getSkewArray(n);
                    CTRectImpl.this.setSkewArray(n, ctSkew);
                    return skewArray;
                }
                
                @Override
                public void add(final int n, final CTSkew ctSkew) {
                    CTRectImpl.this.insertNewSkew(n).set((XmlObject)ctSkew);
                }
                
                @Override
                public CTSkew remove(final int n) {
                    final CTSkew skewArray = CTRectImpl.this.getSkewArray(n);
                    CTRectImpl.this.removeSkew(n);
                    return skewArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfSkewArray();
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
            this.get_store().find_all_element_users(CTRectImpl.SKEW$18, (List)list);
            final CTSkew[] array = new CTSkew[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSkew getSkewArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSkew ctSkew = (CTSkew)this.get_store().find_element_user(CTRectImpl.SKEW$18, n);
            if (ctSkew == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSkew;
        }
    }
    
    public int sizeOfSkewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.SKEW$18);
        }
    }
    
    public void setSkewArray(final CTSkew[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.SKEW$18);
    }
    
    public void setSkewArray(final int n, final CTSkew ctSkew) {
        this.generatedSetterHelperImpl((XmlObject)ctSkew, CTRectImpl.SKEW$18, n, (short)2);
    }
    
    public CTSkew insertNewSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().insert_element_user(CTRectImpl.SKEW$18, n);
        }
    }
    
    public CTSkew addNewSkew() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().add_element_user(CTRectImpl.SKEW$18);
        }
    }
    
    public void removeSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.SKEW$18, n);
        }
    }
    
    public List<CTExtrusion> getExtrusionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtrusionList extends AbstractList<CTExtrusion>
            {
                @Override
                public CTExtrusion get(final int n) {
                    return CTRectImpl.this.getExtrusionArray(n);
                }
                
                @Override
                public CTExtrusion set(final int n, final CTExtrusion ctExtrusion) {
                    final CTExtrusion extrusionArray = CTRectImpl.this.getExtrusionArray(n);
                    CTRectImpl.this.setExtrusionArray(n, ctExtrusion);
                    return extrusionArray;
                }
                
                @Override
                public void add(final int n, final CTExtrusion ctExtrusion) {
                    CTRectImpl.this.insertNewExtrusion(n).set((XmlObject)ctExtrusion);
                }
                
                @Override
                public CTExtrusion remove(final int n) {
                    final CTExtrusion extrusionArray = CTRectImpl.this.getExtrusionArray(n);
                    CTRectImpl.this.removeExtrusion(n);
                    return extrusionArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfExtrusionArray();
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
            this.get_store().find_all_element_users(CTRectImpl.EXTRUSION$20, (List)list);
            final CTExtrusion[] array = new CTExtrusion[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExtrusion getExtrusionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtrusion ctExtrusion = (CTExtrusion)this.get_store().find_element_user(CTRectImpl.EXTRUSION$20, n);
            if (ctExtrusion == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExtrusion;
        }
    }
    
    public int sizeOfExtrusionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.EXTRUSION$20);
        }
    }
    
    public void setExtrusionArray(final CTExtrusion[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.EXTRUSION$20);
    }
    
    public void setExtrusionArray(final int n, final CTExtrusion ctExtrusion) {
        this.generatedSetterHelperImpl((XmlObject)ctExtrusion, CTRectImpl.EXTRUSION$20, n, (short)2);
    }
    
    public CTExtrusion insertNewExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().insert_element_user(CTRectImpl.EXTRUSION$20, n);
        }
    }
    
    public CTExtrusion addNewExtrusion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().add_element_user(CTRectImpl.EXTRUSION$20);
        }
    }
    
    public void removeExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.EXTRUSION$20, n);
        }
    }
    
    public List<CTCallout> getCalloutList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CalloutList extends AbstractList<CTCallout>
            {
                @Override
                public CTCallout get(final int n) {
                    return CTRectImpl.this.getCalloutArray(n);
                }
                
                @Override
                public CTCallout set(final int n, final CTCallout ctCallout) {
                    final CTCallout calloutArray = CTRectImpl.this.getCalloutArray(n);
                    CTRectImpl.this.setCalloutArray(n, ctCallout);
                    return calloutArray;
                }
                
                @Override
                public void add(final int n, final CTCallout ctCallout) {
                    CTRectImpl.this.insertNewCallout(n).set((XmlObject)ctCallout);
                }
                
                @Override
                public CTCallout remove(final int n) {
                    final CTCallout calloutArray = CTRectImpl.this.getCalloutArray(n);
                    CTRectImpl.this.removeCallout(n);
                    return calloutArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfCalloutArray();
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
            this.get_store().find_all_element_users(CTRectImpl.CALLOUT$22, (List)list);
            final CTCallout[] array = new CTCallout[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCallout getCalloutArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCallout ctCallout = (CTCallout)this.get_store().find_element_user(CTRectImpl.CALLOUT$22, n);
            if (ctCallout == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCallout;
        }
    }
    
    public int sizeOfCalloutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.CALLOUT$22);
        }
    }
    
    public void setCalloutArray(final CTCallout[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.CALLOUT$22);
    }
    
    public void setCalloutArray(final int n, final CTCallout ctCallout) {
        this.generatedSetterHelperImpl((XmlObject)ctCallout, CTRectImpl.CALLOUT$22, n, (short)2);
    }
    
    public CTCallout insertNewCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().insert_element_user(CTRectImpl.CALLOUT$22, n);
        }
    }
    
    public CTCallout addNewCallout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().add_element_user(CTRectImpl.CALLOUT$22);
        }
    }
    
    public void removeCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.CALLOUT$22, n);
        }
    }
    
    public List<CTLock> getLockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockList extends AbstractList<CTLock>
            {
                @Override
                public CTLock get(final int n) {
                    return CTRectImpl.this.getLockArray(n);
                }
                
                @Override
                public CTLock set(final int n, final CTLock ctLock) {
                    final CTLock lockArray = CTRectImpl.this.getLockArray(n);
                    CTRectImpl.this.setLockArray(n, ctLock);
                    return lockArray;
                }
                
                @Override
                public void add(final int n, final CTLock ctLock) {
                    CTRectImpl.this.insertNewLock(n).set((XmlObject)ctLock);
                }
                
                @Override
                public CTLock remove(final int n) {
                    final CTLock lockArray = CTRectImpl.this.getLockArray(n);
                    CTRectImpl.this.removeLock(n);
                    return lockArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfLockArray();
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
            this.get_store().find_all_element_users(CTRectImpl.LOCK$24, (List)list);
            final CTLock[] array = new CTLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLock getLockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLock ctLock = (CTLock)this.get_store().find_element_user(CTRectImpl.LOCK$24, n);
            if (ctLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLock;
        }
    }
    
    public int sizeOfLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.LOCK$24);
        }
    }
    
    public void setLockArray(final CTLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.LOCK$24);
    }
    
    public void setLockArray(final int n, final CTLock ctLock) {
        this.generatedSetterHelperImpl((XmlObject)ctLock, CTRectImpl.LOCK$24, n, (short)2);
    }
    
    public CTLock insertNewLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().insert_element_user(CTRectImpl.LOCK$24, n);
        }
    }
    
    public CTLock addNewLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().add_element_user(CTRectImpl.LOCK$24);
        }
    }
    
    public void removeLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.LOCK$24, n);
        }
    }
    
    public List<CTClipPath> getClippathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClippathList extends AbstractList<CTClipPath>
            {
                @Override
                public CTClipPath get(final int n) {
                    return CTRectImpl.this.getClippathArray(n);
                }
                
                @Override
                public CTClipPath set(final int n, final CTClipPath ctClipPath) {
                    final CTClipPath clippathArray = CTRectImpl.this.getClippathArray(n);
                    CTRectImpl.this.setClippathArray(n, ctClipPath);
                    return clippathArray;
                }
                
                @Override
                public void add(final int n, final CTClipPath ctClipPath) {
                    CTRectImpl.this.insertNewClippath(n).set((XmlObject)ctClipPath);
                }
                
                @Override
                public CTClipPath remove(final int n) {
                    final CTClipPath clippathArray = CTRectImpl.this.getClippathArray(n);
                    CTRectImpl.this.removeClippath(n);
                    return clippathArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfClippathArray();
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
            this.get_store().find_all_element_users(CTRectImpl.CLIPPATH$26, (List)list);
            final CTClipPath[] array = new CTClipPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClipPath getClippathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClipPath ctClipPath = (CTClipPath)this.get_store().find_element_user(CTRectImpl.CLIPPATH$26, n);
            if (ctClipPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClipPath;
        }
    }
    
    public int sizeOfClippathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.CLIPPATH$26);
        }
    }
    
    public void setClippathArray(final CTClipPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.CLIPPATH$26);
    }
    
    public void setClippathArray(final int n, final CTClipPath ctClipPath) {
        this.generatedSetterHelperImpl((XmlObject)ctClipPath, CTRectImpl.CLIPPATH$26, n, (short)2);
    }
    
    public CTClipPath insertNewClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().insert_element_user(CTRectImpl.CLIPPATH$26, n);
        }
    }
    
    public CTClipPath addNewClippath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().add_element_user(CTRectImpl.CLIPPATH$26);
        }
    }
    
    public void removeClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.CLIPPATH$26, n);
        }
    }
    
    public List<CTSignatureLine> getSignaturelineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SignaturelineList extends AbstractList<CTSignatureLine>
            {
                @Override
                public CTSignatureLine get(final int n) {
                    return CTRectImpl.this.getSignaturelineArray(n);
                }
                
                @Override
                public CTSignatureLine set(final int n, final CTSignatureLine ctSignatureLine) {
                    final CTSignatureLine signaturelineArray = CTRectImpl.this.getSignaturelineArray(n);
                    CTRectImpl.this.setSignaturelineArray(n, ctSignatureLine);
                    return signaturelineArray;
                }
                
                @Override
                public void add(final int n, final CTSignatureLine ctSignatureLine) {
                    CTRectImpl.this.insertNewSignatureline(n).set((XmlObject)ctSignatureLine);
                }
                
                @Override
                public CTSignatureLine remove(final int n) {
                    final CTSignatureLine signaturelineArray = CTRectImpl.this.getSignaturelineArray(n);
                    CTRectImpl.this.removeSignatureline(n);
                    return signaturelineArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfSignaturelineArray();
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
            this.get_store().find_all_element_users(CTRectImpl.SIGNATURELINE$28, (List)list);
            final CTSignatureLine[] array = new CTSignatureLine[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSignatureLine getSignaturelineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignatureLine ctSignatureLine = (CTSignatureLine)this.get_store().find_element_user(CTRectImpl.SIGNATURELINE$28, n);
            if (ctSignatureLine == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSignatureLine;
        }
    }
    
    public int sizeOfSignaturelineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.SIGNATURELINE$28);
        }
    }
    
    public void setSignaturelineArray(final CTSignatureLine[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.SIGNATURELINE$28);
    }
    
    public void setSignaturelineArray(final int n, final CTSignatureLine ctSignatureLine) {
        this.generatedSetterHelperImpl((XmlObject)ctSignatureLine, CTRectImpl.SIGNATURELINE$28, n, (short)2);
    }
    
    public CTSignatureLine insertNewSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().insert_element_user(CTRectImpl.SIGNATURELINE$28, n);
        }
    }
    
    public CTSignatureLine addNewSignatureline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().add_element_user(CTRectImpl.SIGNATURELINE$28);
        }
    }
    
    public void removeSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.SIGNATURELINE$28, n);
        }
    }
    
    public List<CTWrap> getWrapList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WrapList extends AbstractList<CTWrap>
            {
                @Override
                public CTWrap get(final int n) {
                    return CTRectImpl.this.getWrapArray(n);
                }
                
                @Override
                public CTWrap set(final int n, final CTWrap ctWrap) {
                    final CTWrap wrapArray = CTRectImpl.this.getWrapArray(n);
                    CTRectImpl.this.setWrapArray(n, ctWrap);
                    return wrapArray;
                }
                
                @Override
                public void add(final int n, final CTWrap ctWrap) {
                    CTRectImpl.this.insertNewWrap(n).set((XmlObject)ctWrap);
                }
                
                @Override
                public CTWrap remove(final int n) {
                    final CTWrap wrapArray = CTRectImpl.this.getWrapArray(n);
                    CTRectImpl.this.removeWrap(n);
                    return wrapArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfWrapArray();
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
            this.get_store().find_all_element_users(CTRectImpl.WRAP$30, (List)list);
            final CTWrap[] array = new CTWrap[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTWrap getWrapArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrap ctWrap = (CTWrap)this.get_store().find_element_user(CTRectImpl.WRAP$30, n);
            if (ctWrap == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctWrap;
        }
    }
    
    public int sizeOfWrapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.WRAP$30);
        }
    }
    
    public void setWrapArray(final CTWrap[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.WRAP$30);
    }
    
    public void setWrapArray(final int n, final CTWrap ctWrap) {
        this.generatedSetterHelperImpl((XmlObject)ctWrap, CTRectImpl.WRAP$30, n, (short)2);
    }
    
    public CTWrap insertNewWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().insert_element_user(CTRectImpl.WRAP$30, n);
        }
    }
    
    public CTWrap addNewWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().add_element_user(CTRectImpl.WRAP$30);
        }
    }
    
    public void removeWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.WRAP$30, n);
        }
    }
    
    public List<CTAnchorLock> getAnchorlockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorlockList extends AbstractList<CTAnchorLock>
            {
                @Override
                public CTAnchorLock get(final int n) {
                    return CTRectImpl.this.getAnchorlockArray(n);
                }
                
                @Override
                public CTAnchorLock set(final int n, final CTAnchorLock ctAnchorLock) {
                    final CTAnchorLock anchorlockArray = CTRectImpl.this.getAnchorlockArray(n);
                    CTRectImpl.this.setAnchorlockArray(n, ctAnchorLock);
                    return anchorlockArray;
                }
                
                @Override
                public void add(final int n, final CTAnchorLock ctAnchorLock) {
                    CTRectImpl.this.insertNewAnchorlock(n).set((XmlObject)ctAnchorLock);
                }
                
                @Override
                public CTAnchorLock remove(final int n) {
                    final CTAnchorLock anchorlockArray = CTRectImpl.this.getAnchorlockArray(n);
                    CTRectImpl.this.removeAnchorlock(n);
                    return anchorlockArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfAnchorlockArray();
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
            this.get_store().find_all_element_users(CTRectImpl.ANCHORLOCK$32, (List)list);
            final CTAnchorLock[] array = new CTAnchorLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAnchorLock getAnchorlockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchorLock ctAnchorLock = (CTAnchorLock)this.get_store().find_element_user(CTRectImpl.ANCHORLOCK$32, n);
            if (ctAnchorLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAnchorLock;
        }
    }
    
    public int sizeOfAnchorlockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.ANCHORLOCK$32);
        }
    }
    
    public void setAnchorlockArray(final CTAnchorLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.ANCHORLOCK$32);
    }
    
    public void setAnchorlockArray(final int n, final CTAnchorLock ctAnchorLock) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchorLock, CTRectImpl.ANCHORLOCK$32, n, (short)2);
    }
    
    public CTAnchorLock insertNewAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().insert_element_user(CTRectImpl.ANCHORLOCK$32, n);
        }
    }
    
    public CTAnchorLock addNewAnchorlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().add_element_user(CTRectImpl.ANCHORLOCK$32);
        }
    }
    
    public void removeAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.ANCHORLOCK$32, n);
        }
    }
    
    public List<CTBorder> getBordertopList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BordertopList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTRectImpl.this.getBordertopArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder bordertopArray = CTRectImpl.this.getBordertopArray(n);
                    CTRectImpl.this.setBordertopArray(n, ctBorder);
                    return bordertopArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTRectImpl.this.insertNewBordertop(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder bordertopArray = CTRectImpl.this.getBordertopArray(n);
                    CTRectImpl.this.removeBordertop(n);
                    return bordertopArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfBordertopArray();
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
            this.get_store().find_all_element_users(CTRectImpl.BORDERTOP$34, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBordertopArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTRectImpl.BORDERTOP$34, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBordertopArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.BORDERTOP$34);
        }
    }
    
    public void setBordertopArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.BORDERTOP$34);
    }
    
    public void setBordertopArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTRectImpl.BORDERTOP$34, n, (short)2);
    }
    
    public CTBorder insertNewBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTRectImpl.BORDERTOP$34, n);
        }
    }
    
    public CTBorder addNewBordertop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTRectImpl.BORDERTOP$34);
        }
    }
    
    public void removeBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.BORDERTOP$34, n);
        }
    }
    
    public List<CTBorder> getBorderbottomList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderbottomList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTRectImpl.this.getBorderbottomArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderbottomArray = CTRectImpl.this.getBorderbottomArray(n);
                    CTRectImpl.this.setBorderbottomArray(n, ctBorder);
                    return borderbottomArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTRectImpl.this.insertNewBorderbottom(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderbottomArray = CTRectImpl.this.getBorderbottomArray(n);
                    CTRectImpl.this.removeBorderbottom(n);
                    return borderbottomArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfBorderbottomArray();
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
            this.get_store().find_all_element_users(CTRectImpl.BORDERBOTTOM$36, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderbottomArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTRectImpl.BORDERBOTTOM$36, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderbottomArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.BORDERBOTTOM$36);
        }
    }
    
    public void setBorderbottomArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.BORDERBOTTOM$36);
    }
    
    public void setBorderbottomArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTRectImpl.BORDERBOTTOM$36, n, (short)2);
    }
    
    public CTBorder insertNewBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTRectImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public CTBorder addNewBorderbottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTRectImpl.BORDERBOTTOM$36);
        }
    }
    
    public void removeBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public List<CTBorder> getBorderleftList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderleftList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTRectImpl.this.getBorderleftArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderleftArray = CTRectImpl.this.getBorderleftArray(n);
                    CTRectImpl.this.setBorderleftArray(n, ctBorder);
                    return borderleftArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTRectImpl.this.insertNewBorderleft(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderleftArray = CTRectImpl.this.getBorderleftArray(n);
                    CTRectImpl.this.removeBorderleft(n);
                    return borderleftArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfBorderleftArray();
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
            this.get_store().find_all_element_users(CTRectImpl.BORDERLEFT$38, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderleftArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTRectImpl.BORDERLEFT$38, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderleftArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.BORDERLEFT$38);
        }
    }
    
    public void setBorderleftArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.BORDERLEFT$38);
    }
    
    public void setBorderleftArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTRectImpl.BORDERLEFT$38, n, (short)2);
    }
    
    public CTBorder insertNewBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTRectImpl.BORDERLEFT$38, n);
        }
    }
    
    public CTBorder addNewBorderleft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTRectImpl.BORDERLEFT$38);
        }
    }
    
    public void removeBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.BORDERLEFT$38, n);
        }
    }
    
    public List<CTBorder> getBorderrightList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderrightList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTRectImpl.this.getBorderrightArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderrightArray = CTRectImpl.this.getBorderrightArray(n);
                    CTRectImpl.this.setBorderrightArray(n, ctBorder);
                    return borderrightArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTRectImpl.this.insertNewBorderright(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderrightArray = CTRectImpl.this.getBorderrightArray(n);
                    CTRectImpl.this.removeBorderright(n);
                    return borderrightArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfBorderrightArray();
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
            this.get_store().find_all_element_users(CTRectImpl.BORDERRIGHT$40, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderrightArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTRectImpl.BORDERRIGHT$40, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderrightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.BORDERRIGHT$40);
        }
    }
    
    public void setBorderrightArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.BORDERRIGHT$40);
    }
    
    public void setBorderrightArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTRectImpl.BORDERRIGHT$40, n, (short)2);
    }
    
    public CTBorder insertNewBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTRectImpl.BORDERRIGHT$40, n);
        }
    }
    
    public CTBorder addNewBorderright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTRectImpl.BORDERRIGHT$40);
        }
    }
    
    public void removeBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.BORDERRIGHT$40, n);
        }
    }
    
    public List<CTClientData> getClientDataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClientDataList extends AbstractList<CTClientData>
            {
                @Override
                public CTClientData get(final int n) {
                    return CTRectImpl.this.getClientDataArray(n);
                }
                
                @Override
                public CTClientData set(final int n, final CTClientData ctClientData) {
                    final CTClientData clientDataArray = CTRectImpl.this.getClientDataArray(n);
                    CTRectImpl.this.setClientDataArray(n, ctClientData);
                    return clientDataArray;
                }
                
                @Override
                public void add(final int n, final CTClientData ctClientData) {
                    CTRectImpl.this.insertNewClientData(n).set((XmlObject)ctClientData);
                }
                
                @Override
                public CTClientData remove(final int n) {
                    final CTClientData clientDataArray = CTRectImpl.this.getClientDataArray(n);
                    CTRectImpl.this.removeClientData(n);
                    return clientDataArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfClientDataArray();
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
            this.get_store().find_all_element_users(CTRectImpl.CLIENTDATA$42, (List)list);
            final CTClientData[] array = new CTClientData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClientData getClientDataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClientData ctClientData = (CTClientData)this.get_store().find_element_user(CTRectImpl.CLIENTDATA$42, n);
            if (ctClientData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClientData;
        }
    }
    
    public int sizeOfClientDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.CLIENTDATA$42);
        }
    }
    
    public void setClientDataArray(final CTClientData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.CLIENTDATA$42);
    }
    
    public void setClientDataArray(final int n, final CTClientData ctClientData) {
        this.generatedSetterHelperImpl((XmlObject)ctClientData, CTRectImpl.CLIENTDATA$42, n, (short)2);
    }
    
    public CTClientData insertNewClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().insert_element_user(CTRectImpl.CLIENTDATA$42, n);
        }
    }
    
    public CTClientData addNewClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().add_element_user(CTRectImpl.CLIENTDATA$42);
        }
    }
    
    public void removeClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.CLIENTDATA$42, n);
        }
    }
    
    public List<CTRel> getTextdataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextdataList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTRectImpl.this.getTextdataArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel textdataArray = CTRectImpl.this.getTextdataArray(n);
                    CTRectImpl.this.setTextdataArray(n, ctRel);
                    return textdataArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTRectImpl.this.insertNewTextdata(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel textdataArray = CTRectImpl.this.getTextdataArray(n);
                    CTRectImpl.this.removeTextdata(n);
                    return textdataArray;
                }
                
                @Override
                public int size() {
                    return CTRectImpl.this.sizeOfTextdataArray();
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
            this.get_store().find_all_element_users(CTRectImpl.TEXTDATA$44, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getTextdataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTRectImpl.TEXTDATA$44, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfTextdataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRectImpl.TEXTDATA$44);
        }
    }
    
    public void setTextdataArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRectImpl.TEXTDATA$44);
    }
    
    public void setTextdataArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTRectImpl.TEXTDATA$44, n, (short)2);
    }
    
    public CTRel insertNewTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTRectImpl.TEXTDATA$44, n);
        }
    }
    
    public CTRel addNewTextdata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTRectImpl.TEXTDATA$44);
        }
    }
    
    public void removeTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRectImpl.TEXTDATA$44, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ID$46);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.ID$46);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.ID$46) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ID$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.ID$46);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.ID$46);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.ID$46);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.ID$46);
        }
    }
    
    public String getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STYLE$48);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.STYLE$48);
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.STYLE$48) != null;
        }
    }
    
    public void setStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STYLE$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.STYLE$48);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.STYLE$48);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.STYLE$48);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.STYLE$48);
        }
    }
    
    public String getHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HREF$50);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.HREF$50);
        }
    }
    
    public boolean isSetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HREF$50) != null;
        }
    }
    
    public void setHref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HREF$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HREF$50);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.HREF$50);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.HREF$50);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HREF$50);
        }
    }
    
    public String getTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.TARGET$52);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.TARGET$52);
        }
    }
    
    public boolean isSetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.TARGET$52) != null;
        }
    }
    
    public void setTarget(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.TARGET$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.TARGET$52);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTarget(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.TARGET$52);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.TARGET$52);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.TARGET$52);
        }
    }
    
    public String getClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLASS1$54);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.CLASS1$54);
        }
    }
    
    public boolean isSetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.CLASS1$54) != null;
        }
    }
    
    public void setClass1(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLASS1$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.CLASS1$54);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetClass1(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.CLASS1$54);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.CLASS1$54);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.CLASS1$54);
        }
    }
    
    public String getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.TITLE$56);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.TITLE$56);
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.TITLE$56) != null;
        }
    }
    
    public void setTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.TITLE$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.TITLE$56);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTitle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.TITLE$56);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.TITLE$56);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.TITLE$56);
        }
    }
    
    public String getAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALT$58);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.ALT$58);
        }
    }
    
    public boolean isSetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.ALT$58) != null;
        }
    }
    
    public void setAlt(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALT$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.ALT$58);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlt(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.ALT$58);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.ALT$58);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.ALT$58);
        }
    }
    
    public String getCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.COORDSIZE$60);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.COORDSIZE$60);
        }
    }
    
    public boolean isSetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.COORDSIZE$60) != null;
        }
    }
    
    public void setCoordsize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.COORDSIZE$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.COORDSIZE$60);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordsize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.COORDSIZE$60);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.COORDSIZE$60);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.COORDSIZE$60);
        }
    }
    
    public String getCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.COORDORIGIN$62);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.COORDORIGIN$62);
        }
    }
    
    public boolean isSetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.COORDORIGIN$62) != null;
        }
    }
    
    public void setCoordorigin(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.COORDORIGIN$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.COORDORIGIN$62);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordorigin(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.COORDORIGIN$62);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.COORDORIGIN$62);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.COORDORIGIN$62);
        }
    }
    
    public String getWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.WRAPCOORDS$64);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.WRAPCOORDS$64);
        }
    }
    
    public boolean isSetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.WRAPCOORDS$64) != null;
        }
    }
    
    public void setWrapcoords(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.WRAPCOORDS$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.WRAPCOORDS$64);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetWrapcoords(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.WRAPCOORDS$64);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.WRAPCOORDS$64);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.WRAPCOORDS$64);
        }
    }
    
    public STTrueFalse.Enum getPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.PRINT$66);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.PRINT$66);
        }
    }
    
    public boolean isSetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.PRINT$66) != null;
        }
    }
    
    public void setPrint(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.PRINT$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.PRINT$66);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrint(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.PRINT$66);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.PRINT$66);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.PRINT$66);
        }
    }
    
    public String getSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.SPID$68);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.SPID$68);
        }
    }
    
    public boolean isSetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.SPID$68) != null;
        }
    }
    
    public void setSpid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.SPID$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.SPID$68);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSpid(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.SPID$68);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.SPID$68);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.SPID$68);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ONED$70);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ONED$70);
        }
    }
    
    public boolean isSetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.ONED$70) != null;
        }
    }
    
    public void setOned(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ONED$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.ONED$70);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOned(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ONED$70);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.ONED$70);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.ONED$70);
        }
    }
    
    public BigInteger getRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.REGROUPID$72);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.REGROUPID$72);
        }
    }
    
    public boolean isSetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.REGROUPID$72) != null;
        }
    }
    
    public void setRegroupid(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.REGROUPID$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.REGROUPID$72);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRegroupid(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.REGROUPID$72);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTRectImpl.REGROUPID$72);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.REGROUPID$72);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
        }
    }
    
    public boolean isSetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74) != null;
        }
    }
    
    public void setDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.DOUBLECLICKNOTIFY$74);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.DOUBLECLICKNOTIFY$74);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BUTTON$76);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.BUTTON$76);
        }
    }
    
    public boolean isSetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BUTTON$76) != null;
        }
    }
    
    public void setButton(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BUTTON$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BUTTON$76);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetButton(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.BUTTON$76);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.BUTTON$76);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BUTTON$76);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.USERHIDDEN$78);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.USERHIDDEN$78);
        }
    }
    
    public boolean isSetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.USERHIDDEN$78) != null;
        }
    }
    
    public void setUserhidden(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.USERHIDDEN$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.USERHIDDEN$78);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserhidden(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.USERHIDDEN$78);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.USERHIDDEN$78);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.USERHIDDEN$78);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BULLET$80);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.BULLET$80);
        }
    }
    
    public boolean isSetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BULLET$80) != null;
        }
    }
    
    public void setBullet(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BULLET$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BULLET$80);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBullet(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.BULLET$80);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.BULLET$80);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BULLET$80);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HR$82);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HR$82);
        }
    }
    
    public boolean isSetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HR$82) != null;
        }
    }
    
    public void setHr(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HR$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HR$82);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHr(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HR$82);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.HR$82);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HR$82);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRSTD$84);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HRSTD$84);
        }
    }
    
    public boolean isSetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HRSTD$84) != null;
        }
    }
    
    public void setHrstd(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRSTD$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HRSTD$84);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrstd(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HRSTD$84);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.HRSTD$84);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HRSTD$84);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRNOSHADE$86);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HRNOSHADE$86);
        }
    }
    
    public boolean isSetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HRNOSHADE$86) != null;
        }
    }
    
    public void setHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRNOSHADE$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HRNOSHADE$86);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.HRNOSHADE$86);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.HRNOSHADE$86);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HRNOSHADE$86);
        }
    }
    
    public float getHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRPCT$88);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_attribute_user(CTRectImpl.HRPCT$88);
        }
    }
    
    public boolean isSetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HRPCT$88) != null;
        }
    }
    
    public void setHrpct(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRPCT$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HRPCT$88);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetHrpct(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_attribute_user(CTRectImpl.HRPCT$88);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_attribute_user(CTRectImpl.HRPCT$88);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HRPCT$88);
        }
    }
    
    public STHrAlign.Enum getHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRALIGN$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRectImpl.HRALIGN$90);
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
            STHrAlign stHrAlign = (STHrAlign)this.get_store().find_attribute_user(CTRectImpl.HRALIGN$90);
            if (stHrAlign == null) {
                stHrAlign = (STHrAlign)this.get_default_attribute_value(CTRectImpl.HRALIGN$90);
            }
            return stHrAlign;
        }
    }
    
    public boolean isSetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.HRALIGN$90) != null;
        }
    }
    
    public void setHralign(final STHrAlign.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.HRALIGN$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.HRALIGN$90);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHralign(final STHrAlign stHrAlign) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHrAlign stHrAlign2 = (STHrAlign)this.get_store().find_attribute_user(CTRectImpl.HRALIGN$90);
            if (stHrAlign2 == null) {
                stHrAlign2 = (STHrAlign)this.get_store().add_attribute_user(CTRectImpl.HRALIGN$90);
            }
            stHrAlign2.set((XmlObject)stHrAlign);
        }
    }
    
    public void unsetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.HRALIGN$90);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALLOWINCELL$92);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ALLOWINCELL$92);
        }
    }
    
    public boolean isSetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.ALLOWINCELL$92) != null;
        }
    }
    
    public void setAllowincell(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALLOWINCELL$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.ALLOWINCELL$92);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowincell(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ALLOWINCELL$92);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.ALLOWINCELL$92);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.ALLOWINCELL$92);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
        }
    }
    
    public boolean isSetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.ALLOWOVERLAP$94) != null;
        }
    }
    
    public void setAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.ALLOWOVERLAP$94);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.ALLOWOVERLAP$94);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.USERDRAWN$96);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.USERDRAWN$96);
        }
    }
    
    public boolean isSetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.USERDRAWN$96) != null;
        }
    }
    
    public void setUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.USERDRAWN$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.USERDRAWN$96);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.USERDRAWN$96);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.USERDRAWN$96);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.USERDRAWN$96);
        }
    }
    
    public String getBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
        }
    }
    
    public boolean isSetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BORDERTOPCOLOR$98) != null;
        }
    }
    
    public void setBordertopcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBordertopcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.BORDERTOPCOLOR$98);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BORDERTOPCOLOR$98);
        }
    }
    
    public String getBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
        }
    }
    
    public boolean isSetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100) != null;
        }
    }
    
    public void setBorderleftcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderleftcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.BORDERLEFTCOLOR$100);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BORDERLEFTCOLOR$100);
        }
    }
    
    public String getBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
        }
    }
    
    public boolean isSetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102) != null;
        }
    }
    
    public void setBorderbottomcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderbottomcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.BORDERBOTTOMCOLOR$102);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BORDERBOTTOMCOLOR$102);
        }
    }
    
    public String getBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
        }
    }
    
    public boolean isSetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104) != null;
        }
    }
    
    public void setBorderrightcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderrightcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.BORDERRIGHTCOLOR$104);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BORDERRIGHTCOLOR$104);
        }
    }
    
    public BigInteger getDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUT$106);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUT$106);
        }
    }
    
    public boolean isSetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUT$106) != null;
        }
    }
    
    public void setDgmlayout(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUT$106);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.DGMLAYOUT$106);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayout(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUT$106);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTRectImpl.DGMLAYOUT$106);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.DGMLAYOUT$106);
        }
    }
    
    public BigInteger getDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMNODEKIND$108);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMNODEKIND$108);
        }
    }
    
    public boolean isSetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.DGMNODEKIND$108) != null;
        }
    }
    
    public void setDgmnodekind(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMNODEKIND$108);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.DGMNODEKIND$108);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmnodekind(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMNODEKIND$108);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTRectImpl.DGMNODEKIND$108);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.DGMNODEKIND$108);
        }
    }
    
    public BigInteger getDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
        }
    }
    
    public boolean isSetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUTMRU$110) != null;
        }
    }
    
    public void setDgmlayoutmru(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayoutmru(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTRectImpl.DGMLAYOUTMRU$110);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.DGMLAYOUTMRU$110);
        }
    }
    
    public STInsetMode.Enum getInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.INSETMODE$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRectImpl.INSETMODE$112);
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
            STInsetMode stInsetMode = (STInsetMode)this.get_store().find_attribute_user(CTRectImpl.INSETMODE$112);
            if (stInsetMode == null) {
                stInsetMode = (STInsetMode)this.get_default_attribute_value(CTRectImpl.INSETMODE$112);
            }
            return stInsetMode;
        }
    }
    
    public boolean isSetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.INSETMODE$112) != null;
        }
    }
    
    public void setInsetmode(final STInsetMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.INSETMODE$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.INSETMODE$112);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetmode(final STInsetMode stInsetMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode2 = (STInsetMode)this.get_store().find_attribute_user(CTRectImpl.INSETMODE$112);
            if (stInsetMode2 == null) {
                stInsetMode2 = (STInsetMode)this.get_store().add_attribute_user(CTRectImpl.INSETMODE$112);
            }
            stInsetMode2.set((XmlObject)stInsetMode);
        }
    }
    
    public void unsetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.INSETMODE$112);
        }
    }
    
    public String getChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CHROMAKEY$114);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTRectImpl.CHROMAKEY$114);
        }
    }
    
    public boolean isSetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.CHROMAKEY$114) != null;
        }
    }
    
    public void setChromakey(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CHROMAKEY$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.CHROMAKEY$114);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetChromakey(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTRectImpl.CHROMAKEY$114);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTRectImpl.CHROMAKEY$114);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetChromakey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.CHROMAKEY$114);
        }
    }
    
    public STTrueFalse.Enum getFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FILLED$116);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.FILLED$116);
        }
    }
    
    public boolean isSetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.FILLED$116) != null;
        }
    }
    
    public void setFilled(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FILLED$116);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.FILLED$116);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFilled(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.FILLED$116);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.FILLED$116);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.FILLED$116);
        }
    }
    
    public String getFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FILLCOLOR$118);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTRectImpl.FILLCOLOR$118);
        }
    }
    
    public boolean isSetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.FILLCOLOR$118) != null;
        }
    }
    
    public void setFillcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FILLCOLOR$118);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.FILLCOLOR$118);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFillcolor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTRectImpl.FILLCOLOR$118);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTRectImpl.FILLCOLOR$118);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.FILLCOLOR$118);
        }
    }
    
    public String getOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OPACITY$120);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.OPACITY$120);
        }
    }
    
    public boolean isSetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.OPACITY$120) != null;
        }
    }
    
    public void setOpacity(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OPACITY$120);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.OPACITY$120);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetOpacity(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.OPACITY$120);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.OPACITY$120);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetOpacity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.OPACITY$120);
        }
    }
    
    public STTrueFalse.Enum getStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKED$122);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.STROKED$122);
        }
    }
    
    public boolean isSetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.STROKED$122) != null;
        }
    }
    
    public void setStroked(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKED$122);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.STROKED$122);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetStroked(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.STROKED$122);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.STROKED$122);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetStroked() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.STROKED$122);
        }
    }
    
    public String getStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKECOLOR$124);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTRectImpl.STROKECOLOR$124);
        }
    }
    
    public boolean isSetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.STROKECOLOR$124) != null;
        }
    }
    
    public void setStrokecolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKECOLOR$124);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.STROKECOLOR$124);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStrokecolor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTRectImpl.STROKECOLOR$124);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTRectImpl.STROKECOLOR$124);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetStrokecolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.STROKECOLOR$124);
        }
    }
    
    public String getStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKEWEIGHT$126);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTRectImpl.STROKEWEIGHT$126);
        }
    }
    
    public boolean isSetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.STROKEWEIGHT$126) != null;
        }
    }
    
    public void setStrokeweight(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.STROKEWEIGHT$126);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.STROKEWEIGHT$126);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStrokeweight(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTRectImpl.STROKEWEIGHT$126);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTRectImpl.STROKEWEIGHT$126);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStrokeweight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.STROKEWEIGHT$126);
        }
    }
    
    public STTrueFalse.Enum getInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.INSETPEN$128);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.INSETPEN$128);
        }
    }
    
    public boolean isSetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.INSETPEN$128) != null;
        }
    }
    
    public void setInsetpen(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.INSETPEN$128);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.INSETPEN$128);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetpen(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.INSETPEN$128);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.INSETPEN$128);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetInsetpen() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.INSETPEN$128);
        }
    }
    
    public float getSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.SPT$130);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_attribute_user(CTRectImpl.SPT$130);
        }
    }
    
    public boolean isSetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.SPT$130) != null;
        }
    }
    
    public void setSpt(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.SPT$130);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.SPT$130);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetSpt(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_attribute_user(CTRectImpl.SPT$130);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_attribute_user(CTRectImpl.SPT$130);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetSpt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.SPT$130);
        }
    }
    
    public STConnectorType.Enum getConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRectImpl.CONNECTORTYPE$132);
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
            STConnectorType stConnectorType = (STConnectorType)this.get_store().find_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            if (stConnectorType == null) {
                stConnectorType = (STConnectorType)this.get_default_attribute_value(CTRectImpl.CONNECTORTYPE$132);
            }
            return stConnectorType;
        }
    }
    
    public boolean isSetConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.CONNECTORTYPE$132) != null;
        }
    }
    
    public void setConnectortype(final STConnectorType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetConnectortype(final STConnectorType stConnectorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STConnectorType stConnectorType2 = (STConnectorType)this.get_store().find_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            if (stConnectorType2 == null) {
                stConnectorType2 = (STConnectorType)this.get_store().add_attribute_user(CTRectImpl.CONNECTORTYPE$132);
            }
            stConnectorType2.set((XmlObject)stConnectorType);
        }
    }
    
    public void unsetConnectortype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.CONNECTORTYPE$132);
        }
    }
    
    public STBWMode.Enum getBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWMODE$134);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWMODE$134);
        }
    }
    
    public boolean isSetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BWMODE$134) != null;
        }
    }
    
    public void setBwmode(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWMODE$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BWMODE$134);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwmode(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWMODE$134);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTRectImpl.BWMODE$134);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BWMODE$134);
        }
    }
    
    public STBWMode.Enum getBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWPURE$136);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWPURE$136);
        }
    }
    
    public boolean isSetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BWPURE$136) != null;
        }
    }
    
    public void setBwpure(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWPURE$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BWPURE$136);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwpure(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWPURE$136);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTRectImpl.BWPURE$136);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwpure() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BWPURE$136);
        }
    }
    
    public STBWMode.Enum getBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWNORMAL$138);
            if (simpleValue == null) {
                return null;
            }
            return (STBWMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBWMode xgetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWNORMAL$138);
        }
    }
    
    public boolean isSetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.BWNORMAL$138) != null;
        }
    }
    
    public void setBwnormal(final STBWMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.BWNORMAL$138);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.BWNORMAL$138);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwnormal(final STBWMode stbwMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBWMode stbwMode2 = (STBWMode)this.get_store().find_attribute_user(CTRectImpl.BWNORMAL$138);
            if (stbwMode2 == null) {
                stbwMode2 = (STBWMode)this.get_store().add_attribute_user(CTRectImpl.BWNORMAL$138);
            }
            stbwMode2.set((XmlObject)stbwMode);
        }
    }
    
    public void unsetBwnormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.BWNORMAL$138);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FORCEDASH$140);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.FORCEDASH$140);
        }
    }
    
    public boolean isSetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.FORCEDASH$140) != null;
        }
    }
    
    public void setForcedash(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.FORCEDASH$140);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.FORCEDASH$140);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetForcedash(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.FORCEDASH$140);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.FORCEDASH$140);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetForcedash() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.FORCEDASH$140);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OLEICON$142);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.OLEICON$142);
        }
    }
    
    public boolean isSetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.OLEICON$142) != null;
        }
    }
    
    public void setOleicon(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OLEICON$142);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.OLEICON$142);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOleicon(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.OLEICON$142);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.OLEICON$142);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOleicon() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.OLEICON$142);
        }
    }
    
    public STTrueFalseBlank.Enum getOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OLE$144);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalseBlank.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalseBlank xgetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalseBlank)this.get_store().find_attribute_user(CTRectImpl.OLE$144);
        }
    }
    
    public boolean isSetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.OLE$144) != null;
        }
    }
    
    public void setOle(final STTrueFalseBlank.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.OLE$144);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.OLE$144);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOle(final STTrueFalseBlank stTrueFalseBlank) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalseBlank stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().find_attribute_user(CTRectImpl.OLE$144);
            if (stTrueFalseBlank2 == null) {
                stTrueFalseBlank2 = (STTrueFalseBlank)this.get_store().add_attribute_user(CTRectImpl.OLE$144);
            }
            stTrueFalseBlank2.set((XmlObject)stTrueFalseBlank);
        }
    }
    
    public void unsetOle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.OLE$144);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.PREFERRELATIVE$146);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.PREFERRELATIVE$146);
        }
    }
    
    public boolean isSetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.PREFERRELATIVE$146) != null;
        }
    }
    
    public void setPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.PREFERRELATIVE$146);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.PREFERRELATIVE$146);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPreferrelative(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.PREFERRELATIVE$146);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.PREFERRELATIVE$146);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPreferrelative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.PREFERRELATIVE$146);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLIPTOWRAP$148);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.CLIPTOWRAP$148);
        }
    }
    
    public boolean isSetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.CLIPTOWRAP$148) != null;
        }
    }
    
    public void setCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLIPTOWRAP$148);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.CLIPTOWRAP$148);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCliptowrap(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.CLIPTOWRAP$148);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.CLIPTOWRAP$148);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetCliptowrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.CLIPTOWRAP$148);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLIP$150);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.CLIP$150);
        }
    }
    
    public boolean isSetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRectImpl.CLIP$150) != null;
        }
    }
    
    public void setClip(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRectImpl.CLIP$150);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRectImpl.CLIP$150);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetClip(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTRectImpl.CLIP$150);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTRectImpl.CLIP$150);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetClip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRectImpl.CLIP$150);
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
        ID$46 = new QName("", "id");
        STYLE$48 = new QName("", "style");
        HREF$50 = new QName("", "href");
        TARGET$52 = new QName("", "target");
        CLASS1$54 = new QName("", "class");
        TITLE$56 = new QName("", "title");
        ALT$58 = new QName("", "alt");
        COORDSIZE$60 = new QName("", "coordsize");
        COORDORIGIN$62 = new QName("", "coordorigin");
        WRAPCOORDS$64 = new QName("", "wrapcoords");
        PRINT$66 = new QName("", "print");
        SPID$68 = new QName("urn:schemas-microsoft-com:office:office", "spid");
        ONED$70 = new QName("urn:schemas-microsoft-com:office:office", "oned");
        REGROUPID$72 = new QName("urn:schemas-microsoft-com:office:office", "regroupid");
        DOUBLECLICKNOTIFY$74 = new QName("urn:schemas-microsoft-com:office:office", "doubleclicknotify");
        BUTTON$76 = new QName("urn:schemas-microsoft-com:office:office", "button");
        USERHIDDEN$78 = new QName("urn:schemas-microsoft-com:office:office", "userhidden");
        BULLET$80 = new QName("urn:schemas-microsoft-com:office:office", "bullet");
        HR$82 = new QName("urn:schemas-microsoft-com:office:office", "hr");
        HRSTD$84 = new QName("urn:schemas-microsoft-com:office:office", "hrstd");
        HRNOSHADE$86 = new QName("urn:schemas-microsoft-com:office:office", "hrnoshade");
        HRPCT$88 = new QName("urn:schemas-microsoft-com:office:office", "hrpct");
        HRALIGN$90 = new QName("urn:schemas-microsoft-com:office:office", "hralign");
        ALLOWINCELL$92 = new QName("urn:schemas-microsoft-com:office:office", "allowincell");
        ALLOWOVERLAP$94 = new QName("urn:schemas-microsoft-com:office:office", "allowoverlap");
        USERDRAWN$96 = new QName("urn:schemas-microsoft-com:office:office", "userdrawn");
        BORDERTOPCOLOR$98 = new QName("urn:schemas-microsoft-com:office:office", "bordertopcolor");
        BORDERLEFTCOLOR$100 = new QName("urn:schemas-microsoft-com:office:office", "borderleftcolor");
        BORDERBOTTOMCOLOR$102 = new QName("urn:schemas-microsoft-com:office:office", "borderbottomcolor");
        BORDERRIGHTCOLOR$104 = new QName("urn:schemas-microsoft-com:office:office", "borderrightcolor");
        DGMLAYOUT$106 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayout");
        DGMNODEKIND$108 = new QName("urn:schemas-microsoft-com:office:office", "dgmnodekind");
        DGMLAYOUTMRU$110 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayoutmru");
        INSETMODE$112 = new QName("urn:schemas-microsoft-com:office:office", "insetmode");
        CHROMAKEY$114 = new QName("", "chromakey");
        FILLED$116 = new QName("", "filled");
        FILLCOLOR$118 = new QName("", "fillcolor");
        OPACITY$120 = new QName("", "opacity");
        STROKED$122 = new QName("", "stroked");
        STROKECOLOR$124 = new QName("", "strokecolor");
        STROKEWEIGHT$126 = new QName("", "strokeweight");
        INSETPEN$128 = new QName("", "insetpen");
        SPT$130 = new QName("urn:schemas-microsoft-com:office:office", "spt");
        CONNECTORTYPE$132 = new QName("urn:schemas-microsoft-com:office:office", "connectortype");
        BWMODE$134 = new QName("urn:schemas-microsoft-com:office:office", "bwmode");
        BWPURE$136 = new QName("urn:schemas-microsoft-com:office:office", "bwpure");
        BWNORMAL$138 = new QName("urn:schemas-microsoft-com:office:office", "bwnormal");
        FORCEDASH$140 = new QName("urn:schemas-microsoft-com:office:office", "forcedash");
        OLEICON$142 = new QName("urn:schemas-microsoft-com:office:office", "oleicon");
        OLE$144 = new QName("urn:schemas-microsoft-com:office:office", "ole");
        PREFERRELATIVE$146 = new QName("urn:schemas-microsoft-com:office:office", "preferrelative");
        CLIPTOWRAP$148 = new QName("urn:schemas-microsoft-com:office:office", "cliptowrap");
        CLIP$150 = new QName("urn:schemas-microsoft-com:office:office", "clip");
    }
}
