package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.vml.STEditAs;
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
import com.microsoft.schemas.office.office.CTDiagram;
import com.microsoft.schemas.vml.CTRoundRect;
import com.microsoft.schemas.vml.CTRect;
import com.microsoft.schemas.vml.CTPolyLine;
import com.microsoft.schemas.vml.CTOval;
import com.microsoft.schemas.vml.CTLine;
import com.microsoft.schemas.vml.CTImage;
import com.microsoft.schemas.vml.CTCurve;
import com.microsoft.schemas.vml.CTArc;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.CTShape;
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
import com.microsoft.schemas.vml.CTGroup;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTGroupImpl extends XmlComplexContentImpl implements CTGroup
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
    private static final QName GROUP$46;
    private static final QName SHAPE$48;
    private static final QName SHAPETYPE$50;
    private static final QName ARC$52;
    private static final QName CURVE$54;
    private static final QName IMAGE$56;
    private static final QName LINE$58;
    private static final QName OVAL$60;
    private static final QName POLYLINE$62;
    private static final QName RECT$64;
    private static final QName ROUNDRECT$66;
    private static final QName DIAGRAM$68;
    private static final QName ID$70;
    private static final QName STYLE$72;
    private static final QName HREF$74;
    private static final QName TARGET$76;
    private static final QName CLASS1$78;
    private static final QName TITLE$80;
    private static final QName ALT$82;
    private static final QName COORDSIZE$84;
    private static final QName COORDORIGIN$86;
    private static final QName WRAPCOORDS$88;
    private static final QName PRINT$90;
    private static final QName SPID$92;
    private static final QName ONED$94;
    private static final QName REGROUPID$96;
    private static final QName DOUBLECLICKNOTIFY$98;
    private static final QName BUTTON$100;
    private static final QName USERHIDDEN$102;
    private static final QName BULLET$104;
    private static final QName HR$106;
    private static final QName HRSTD$108;
    private static final QName HRNOSHADE$110;
    private static final QName HRPCT$112;
    private static final QName HRALIGN$114;
    private static final QName ALLOWINCELL$116;
    private static final QName ALLOWOVERLAP$118;
    private static final QName USERDRAWN$120;
    private static final QName BORDERTOPCOLOR$122;
    private static final QName BORDERLEFTCOLOR$124;
    private static final QName BORDERBOTTOMCOLOR$126;
    private static final QName BORDERRIGHTCOLOR$128;
    private static final QName DGMLAYOUT$130;
    private static final QName DGMNODEKIND$132;
    private static final QName DGMLAYOUTMRU$134;
    private static final QName INSETMODE$136;
    private static final QName FILLED$138;
    private static final QName FILLCOLOR$140;
    private static final QName EDITAS$142;
    private static final QName TABLEPROPERTIES$144;
    private static final QName TABLELIMITS$146;
    
    public CTGroupImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPath> getPathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PathList extends AbstractList<CTPath>
            {
                @Override
                public CTPath get(final int n) {
                    return CTGroupImpl.this.getPathArray(n);
                }
                
                @Override
                public CTPath set(final int n, final CTPath ctPath) {
                    final CTPath pathArray = CTGroupImpl.this.getPathArray(n);
                    CTGroupImpl.this.setPathArray(n, ctPath);
                    return pathArray;
                }
                
                @Override
                public void add(final int n, final CTPath ctPath) {
                    CTGroupImpl.this.insertNewPath(n).set((XmlObject)ctPath);
                }
                
                @Override
                public CTPath remove(final int n) {
                    final CTPath pathArray = CTGroupImpl.this.getPathArray(n);
                    CTGroupImpl.this.removePath(n);
                    return pathArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfPathArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.PATH$0, (List)list);
            final CTPath[] array = new CTPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath getPathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath ctPath = (CTPath)this.get_store().find_element_user(CTGroupImpl.PATH$0, n);
            if (ctPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath;
        }
    }
    
    public int sizeOfPathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.PATH$0);
        }
    }
    
    public void setPathArray(final CTPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.PATH$0);
    }
    
    public void setPathArray(final int n, final CTPath ctPath) {
        this.generatedSetterHelperImpl((XmlObject)ctPath, CTGroupImpl.PATH$0, n, (short)2);
    }
    
    public CTPath insertNewPath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().insert_element_user(CTGroupImpl.PATH$0, n);
        }
    }
    
    public CTPath addNewPath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath)this.get_store().add_element_user(CTGroupImpl.PATH$0);
        }
    }
    
    public void removePath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.PATH$0, n);
        }
    }
    
    public List<CTFormulas> getFormulasList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FormulasList extends AbstractList<CTFormulas>
            {
                @Override
                public CTFormulas get(final int n) {
                    return CTGroupImpl.this.getFormulasArray(n);
                }
                
                @Override
                public CTFormulas set(final int n, final CTFormulas ctFormulas) {
                    final CTFormulas formulasArray = CTGroupImpl.this.getFormulasArray(n);
                    CTGroupImpl.this.setFormulasArray(n, ctFormulas);
                    return formulasArray;
                }
                
                @Override
                public void add(final int n, final CTFormulas ctFormulas) {
                    CTGroupImpl.this.insertNewFormulas(n).set((XmlObject)ctFormulas);
                }
                
                @Override
                public CTFormulas remove(final int n) {
                    final CTFormulas formulasArray = CTGroupImpl.this.getFormulasArray(n);
                    CTGroupImpl.this.removeFormulas(n);
                    return formulasArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfFormulasArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.FORMULAS$2, (List)list);
            final CTFormulas[] array = new CTFormulas[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFormulas getFormulasArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFormulas ctFormulas = (CTFormulas)this.get_store().find_element_user(CTGroupImpl.FORMULAS$2, n);
            if (ctFormulas == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFormulas;
        }
    }
    
    public int sizeOfFormulasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.FORMULAS$2);
        }
    }
    
    public void setFormulasArray(final CTFormulas[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.FORMULAS$2);
    }
    
    public void setFormulasArray(final int n, final CTFormulas ctFormulas) {
        this.generatedSetterHelperImpl((XmlObject)ctFormulas, CTGroupImpl.FORMULAS$2, n, (short)2);
    }
    
    public CTFormulas insertNewFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().insert_element_user(CTGroupImpl.FORMULAS$2, n);
        }
    }
    
    public CTFormulas addNewFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFormulas)this.get_store().add_element_user(CTGroupImpl.FORMULAS$2);
        }
    }
    
    public void removeFormulas(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.FORMULAS$2, n);
        }
    }
    
    public List<CTHandles> getHandlesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HandlesList extends AbstractList<CTHandles>
            {
                @Override
                public CTHandles get(final int n) {
                    return CTGroupImpl.this.getHandlesArray(n);
                }
                
                @Override
                public CTHandles set(final int n, final CTHandles ctHandles) {
                    final CTHandles handlesArray = CTGroupImpl.this.getHandlesArray(n);
                    CTGroupImpl.this.setHandlesArray(n, ctHandles);
                    return handlesArray;
                }
                
                @Override
                public void add(final int n, final CTHandles ctHandles) {
                    CTGroupImpl.this.insertNewHandles(n).set((XmlObject)ctHandles);
                }
                
                @Override
                public CTHandles remove(final int n) {
                    final CTHandles handlesArray = CTGroupImpl.this.getHandlesArray(n);
                    CTGroupImpl.this.removeHandles(n);
                    return handlesArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfHandlesArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.HANDLES$4, (List)list);
            final CTHandles[] array = new CTHandles[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHandles getHandlesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHandles ctHandles = (CTHandles)this.get_store().find_element_user(CTGroupImpl.HANDLES$4, n);
            if (ctHandles == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHandles;
        }
    }
    
    public int sizeOfHandlesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.HANDLES$4);
        }
    }
    
    public void setHandlesArray(final CTHandles[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.HANDLES$4);
    }
    
    public void setHandlesArray(final int n, final CTHandles ctHandles) {
        this.generatedSetterHelperImpl((XmlObject)ctHandles, CTGroupImpl.HANDLES$4, n, (short)2);
    }
    
    public CTHandles insertNewHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().insert_element_user(CTGroupImpl.HANDLES$4, n);
        }
    }
    
    public CTHandles addNewHandles() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHandles)this.get_store().add_element_user(CTGroupImpl.HANDLES$4);
        }
    }
    
    public void removeHandles(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.HANDLES$4, n);
        }
    }
    
    public List<CTFill> getFillList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FillList extends AbstractList<CTFill>
            {
                @Override
                public CTFill get(final int n) {
                    return CTGroupImpl.this.getFillArray(n);
                }
                
                @Override
                public CTFill set(final int n, final CTFill ctFill) {
                    final CTFill fillArray = CTGroupImpl.this.getFillArray(n);
                    CTGroupImpl.this.setFillArray(n, ctFill);
                    return fillArray;
                }
                
                @Override
                public void add(final int n, final CTFill ctFill) {
                    CTGroupImpl.this.insertNewFill(n).set((XmlObject)ctFill);
                }
                
                @Override
                public CTFill remove(final int n) {
                    final CTFill fillArray = CTGroupImpl.this.getFillArray(n);
                    CTGroupImpl.this.removeFill(n);
                    return fillArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfFillArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.FILL$6, (List)list);
            final CTFill[] array = new CTFill[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFill getFillArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFill ctFill = (CTFill)this.get_store().find_element_user(CTGroupImpl.FILL$6, n);
            if (ctFill == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFill;
        }
    }
    
    public int sizeOfFillArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.FILL$6);
        }
    }
    
    public void setFillArray(final CTFill[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.FILL$6);
    }
    
    public void setFillArray(final int n, final CTFill ctFill) {
        this.generatedSetterHelperImpl((XmlObject)ctFill, CTGroupImpl.FILL$6, n, (short)2);
    }
    
    public CTFill insertNewFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().insert_element_user(CTGroupImpl.FILL$6, n);
        }
    }
    
    public CTFill addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFill)this.get_store().add_element_user(CTGroupImpl.FILL$6);
        }
    }
    
    public void removeFill(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.FILL$6, n);
        }
    }
    
    public List<CTStroke> getStrokeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StrokeList extends AbstractList<CTStroke>
            {
                @Override
                public CTStroke get(final int n) {
                    return CTGroupImpl.this.getStrokeArray(n);
                }
                
                @Override
                public CTStroke set(final int n, final CTStroke ctStroke) {
                    final CTStroke strokeArray = CTGroupImpl.this.getStrokeArray(n);
                    CTGroupImpl.this.setStrokeArray(n, ctStroke);
                    return strokeArray;
                }
                
                @Override
                public void add(final int n, final CTStroke ctStroke) {
                    CTGroupImpl.this.insertNewStroke(n).set((XmlObject)ctStroke);
                }
                
                @Override
                public CTStroke remove(final int n) {
                    final CTStroke strokeArray = CTGroupImpl.this.getStrokeArray(n);
                    CTGroupImpl.this.removeStroke(n);
                    return strokeArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfStrokeArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.STROKE$8, (List)list);
            final CTStroke[] array = new CTStroke[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStroke getStrokeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStroke ctStroke = (CTStroke)this.get_store().find_element_user(CTGroupImpl.STROKE$8, n);
            if (ctStroke == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStroke;
        }
    }
    
    public int sizeOfStrokeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.STROKE$8);
        }
    }
    
    public void setStrokeArray(final CTStroke[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.STROKE$8);
    }
    
    public void setStrokeArray(final int n, final CTStroke ctStroke) {
        this.generatedSetterHelperImpl((XmlObject)ctStroke, CTGroupImpl.STROKE$8, n, (short)2);
    }
    
    public CTStroke insertNewStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().insert_element_user(CTGroupImpl.STROKE$8, n);
        }
    }
    
    public CTStroke addNewStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStroke)this.get_store().add_element_user(CTGroupImpl.STROKE$8);
        }
    }
    
    public void removeStroke(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.STROKE$8, n);
        }
    }
    
    public List<CTShadow> getShadowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadowList extends AbstractList<CTShadow>
            {
                @Override
                public CTShadow get(final int n) {
                    return CTGroupImpl.this.getShadowArray(n);
                }
                
                @Override
                public CTShadow set(final int n, final CTShadow ctShadow) {
                    final CTShadow shadowArray = CTGroupImpl.this.getShadowArray(n);
                    CTGroupImpl.this.setShadowArray(n, ctShadow);
                    return shadowArray;
                }
                
                @Override
                public void add(final int n, final CTShadow ctShadow) {
                    CTGroupImpl.this.insertNewShadow(n).set((XmlObject)ctShadow);
                }
                
                @Override
                public CTShadow remove(final int n) {
                    final CTShadow shadowArray = CTGroupImpl.this.getShadowArray(n);
                    CTGroupImpl.this.removeShadow(n);
                    return shadowArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfShadowArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.SHADOW$10, (List)list);
            final CTShadow[] array = new CTShadow[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShadow getShadowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShadow ctShadow = (CTShadow)this.get_store().find_element_user(CTGroupImpl.SHADOW$10, n);
            if (ctShadow == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShadow;
        }
    }
    
    public int sizeOfShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.SHADOW$10);
        }
    }
    
    public void setShadowArray(final CTShadow[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.SHADOW$10);
    }
    
    public void setShadowArray(final int n, final CTShadow ctShadow) {
        this.generatedSetterHelperImpl((XmlObject)ctShadow, CTGroupImpl.SHADOW$10, n, (short)2);
    }
    
    public CTShadow insertNewShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().insert_element_user(CTGroupImpl.SHADOW$10, n);
        }
    }
    
    public CTShadow addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShadow)this.get_store().add_element_user(CTGroupImpl.SHADOW$10);
        }
    }
    
    public void removeShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.SHADOW$10, n);
        }
    }
    
    public List<CTTextbox> getTextboxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextboxList extends AbstractList<CTTextbox>
            {
                @Override
                public CTTextbox get(final int n) {
                    return CTGroupImpl.this.getTextboxArray(n);
                }
                
                @Override
                public CTTextbox set(final int n, final CTTextbox ctTextbox) {
                    final CTTextbox textboxArray = CTGroupImpl.this.getTextboxArray(n);
                    CTGroupImpl.this.setTextboxArray(n, ctTextbox);
                    return textboxArray;
                }
                
                @Override
                public void add(final int n, final CTTextbox ctTextbox) {
                    CTGroupImpl.this.insertNewTextbox(n).set((XmlObject)ctTextbox);
                }
                
                @Override
                public CTTextbox remove(final int n) {
                    final CTTextbox textboxArray = CTGroupImpl.this.getTextboxArray(n);
                    CTGroupImpl.this.removeTextbox(n);
                    return textboxArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfTextboxArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.TEXTBOX$12, (List)list);
            final CTTextbox[] array = new CTTextbox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextbox getTextboxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextbox ctTextbox = (CTTextbox)this.get_store().find_element_user(CTGroupImpl.TEXTBOX$12, n);
            if (ctTextbox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextbox;
        }
    }
    
    public int sizeOfTextboxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.TEXTBOX$12);
        }
    }
    
    public void setTextboxArray(final CTTextbox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.TEXTBOX$12);
    }
    
    public void setTextboxArray(final int n, final CTTextbox ctTextbox) {
        this.generatedSetterHelperImpl((XmlObject)ctTextbox, CTGroupImpl.TEXTBOX$12, n, (short)2);
    }
    
    public CTTextbox insertNewTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().insert_element_user(CTGroupImpl.TEXTBOX$12, n);
        }
    }
    
    public CTTextbox addNewTextbox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextbox)this.get_store().add_element_user(CTGroupImpl.TEXTBOX$12);
        }
    }
    
    public void removeTextbox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.TEXTBOX$12, n);
        }
    }
    
    public List<CTTextPath> getTextpathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextpathList extends AbstractList<CTTextPath>
            {
                @Override
                public CTTextPath get(final int n) {
                    return CTGroupImpl.this.getTextpathArray(n);
                }
                
                @Override
                public CTTextPath set(final int n, final CTTextPath ctTextPath) {
                    final CTTextPath textpathArray = CTGroupImpl.this.getTextpathArray(n);
                    CTGroupImpl.this.setTextpathArray(n, ctTextPath);
                    return textpathArray;
                }
                
                @Override
                public void add(final int n, final CTTextPath ctTextPath) {
                    CTGroupImpl.this.insertNewTextpath(n).set((XmlObject)ctTextPath);
                }
                
                @Override
                public CTTextPath remove(final int n) {
                    final CTTextPath textpathArray = CTGroupImpl.this.getTextpathArray(n);
                    CTGroupImpl.this.removeTextpath(n);
                    return textpathArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfTextpathArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.TEXTPATH$14, (List)list);
            final CTTextPath[] array = new CTTextPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextPath getTextpathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextPath ctTextPath = (CTTextPath)this.get_store().find_element_user(CTGroupImpl.TEXTPATH$14, n);
            if (ctTextPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextPath;
        }
    }
    
    public int sizeOfTextpathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.TEXTPATH$14);
        }
    }
    
    public void setTextpathArray(final CTTextPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.TEXTPATH$14);
    }
    
    public void setTextpathArray(final int n, final CTTextPath ctTextPath) {
        this.generatedSetterHelperImpl((XmlObject)ctTextPath, CTGroupImpl.TEXTPATH$14, n, (short)2);
    }
    
    public CTTextPath insertNewTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().insert_element_user(CTGroupImpl.TEXTPATH$14, n);
        }
    }
    
    public CTTextPath addNewTextpath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextPath)this.get_store().add_element_user(CTGroupImpl.TEXTPATH$14);
        }
    }
    
    public void removeTextpath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.TEXTPATH$14, n);
        }
    }
    
    public List<CTImageData> getImagedataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ImagedataList extends AbstractList<CTImageData>
            {
                @Override
                public CTImageData get(final int n) {
                    return CTGroupImpl.this.getImagedataArray(n);
                }
                
                @Override
                public CTImageData set(final int n, final CTImageData ctImageData) {
                    final CTImageData imagedataArray = CTGroupImpl.this.getImagedataArray(n);
                    CTGroupImpl.this.setImagedataArray(n, ctImageData);
                    return imagedataArray;
                }
                
                @Override
                public void add(final int n, final CTImageData ctImageData) {
                    CTGroupImpl.this.insertNewImagedata(n).set((XmlObject)ctImageData);
                }
                
                @Override
                public CTImageData remove(final int n) {
                    final CTImageData imagedataArray = CTGroupImpl.this.getImagedataArray(n);
                    CTGroupImpl.this.removeImagedata(n);
                    return imagedataArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfImagedataArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.IMAGEDATA$16, (List)list);
            final CTImageData[] array = new CTImageData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTImageData getImagedataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTImageData ctImageData = (CTImageData)this.get_store().find_element_user(CTGroupImpl.IMAGEDATA$16, n);
            if (ctImageData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctImageData;
        }
    }
    
    public int sizeOfImagedataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.IMAGEDATA$16);
        }
    }
    
    public void setImagedataArray(final CTImageData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.IMAGEDATA$16);
    }
    
    public void setImagedataArray(final int n, final CTImageData ctImageData) {
        this.generatedSetterHelperImpl((XmlObject)ctImageData, CTGroupImpl.IMAGEDATA$16, n, (short)2);
    }
    
    public CTImageData insertNewImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().insert_element_user(CTGroupImpl.IMAGEDATA$16, n);
        }
    }
    
    public CTImageData addNewImagedata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImageData)this.get_store().add_element_user(CTGroupImpl.IMAGEDATA$16);
        }
    }
    
    public void removeImagedata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.IMAGEDATA$16, n);
        }
    }
    
    public List<CTSkew> getSkewList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SkewList extends AbstractList<CTSkew>
            {
                @Override
                public CTSkew get(final int n) {
                    return CTGroupImpl.this.getSkewArray(n);
                }
                
                @Override
                public CTSkew set(final int n, final CTSkew ctSkew) {
                    final CTSkew skewArray = CTGroupImpl.this.getSkewArray(n);
                    CTGroupImpl.this.setSkewArray(n, ctSkew);
                    return skewArray;
                }
                
                @Override
                public void add(final int n, final CTSkew ctSkew) {
                    CTGroupImpl.this.insertNewSkew(n).set((XmlObject)ctSkew);
                }
                
                @Override
                public CTSkew remove(final int n) {
                    final CTSkew skewArray = CTGroupImpl.this.getSkewArray(n);
                    CTGroupImpl.this.removeSkew(n);
                    return skewArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfSkewArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.SKEW$18, (List)list);
            final CTSkew[] array = new CTSkew[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSkew getSkewArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSkew ctSkew = (CTSkew)this.get_store().find_element_user(CTGroupImpl.SKEW$18, n);
            if (ctSkew == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSkew;
        }
    }
    
    public int sizeOfSkewArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.SKEW$18);
        }
    }
    
    public void setSkewArray(final CTSkew[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.SKEW$18);
    }
    
    public void setSkewArray(final int n, final CTSkew ctSkew) {
        this.generatedSetterHelperImpl((XmlObject)ctSkew, CTGroupImpl.SKEW$18, n, (short)2);
    }
    
    public CTSkew insertNewSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().insert_element_user(CTGroupImpl.SKEW$18, n);
        }
    }
    
    public CTSkew addNewSkew() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSkew)this.get_store().add_element_user(CTGroupImpl.SKEW$18);
        }
    }
    
    public void removeSkew(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.SKEW$18, n);
        }
    }
    
    public List<CTExtrusion> getExtrusionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtrusionList extends AbstractList<CTExtrusion>
            {
                @Override
                public CTExtrusion get(final int n) {
                    return CTGroupImpl.this.getExtrusionArray(n);
                }
                
                @Override
                public CTExtrusion set(final int n, final CTExtrusion ctExtrusion) {
                    final CTExtrusion extrusionArray = CTGroupImpl.this.getExtrusionArray(n);
                    CTGroupImpl.this.setExtrusionArray(n, ctExtrusion);
                    return extrusionArray;
                }
                
                @Override
                public void add(final int n, final CTExtrusion ctExtrusion) {
                    CTGroupImpl.this.insertNewExtrusion(n).set((XmlObject)ctExtrusion);
                }
                
                @Override
                public CTExtrusion remove(final int n) {
                    final CTExtrusion extrusionArray = CTGroupImpl.this.getExtrusionArray(n);
                    CTGroupImpl.this.removeExtrusion(n);
                    return extrusionArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfExtrusionArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.EXTRUSION$20, (List)list);
            final CTExtrusion[] array = new CTExtrusion[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTExtrusion getExtrusionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtrusion ctExtrusion = (CTExtrusion)this.get_store().find_element_user(CTGroupImpl.EXTRUSION$20, n);
            if (ctExtrusion == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctExtrusion;
        }
    }
    
    public int sizeOfExtrusionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.EXTRUSION$20);
        }
    }
    
    public void setExtrusionArray(final CTExtrusion[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.EXTRUSION$20);
    }
    
    public void setExtrusionArray(final int n, final CTExtrusion ctExtrusion) {
        this.generatedSetterHelperImpl((XmlObject)ctExtrusion, CTGroupImpl.EXTRUSION$20, n, (short)2);
    }
    
    public CTExtrusion insertNewExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().insert_element_user(CTGroupImpl.EXTRUSION$20, n);
        }
    }
    
    public CTExtrusion addNewExtrusion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtrusion)this.get_store().add_element_user(CTGroupImpl.EXTRUSION$20);
        }
    }
    
    public void removeExtrusion(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.EXTRUSION$20, n);
        }
    }
    
    public List<CTCallout> getCalloutList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CalloutList extends AbstractList<CTCallout>
            {
                @Override
                public CTCallout get(final int n) {
                    return CTGroupImpl.this.getCalloutArray(n);
                }
                
                @Override
                public CTCallout set(final int n, final CTCallout ctCallout) {
                    final CTCallout calloutArray = CTGroupImpl.this.getCalloutArray(n);
                    CTGroupImpl.this.setCalloutArray(n, ctCallout);
                    return calloutArray;
                }
                
                @Override
                public void add(final int n, final CTCallout ctCallout) {
                    CTGroupImpl.this.insertNewCallout(n).set((XmlObject)ctCallout);
                }
                
                @Override
                public CTCallout remove(final int n) {
                    final CTCallout calloutArray = CTGroupImpl.this.getCalloutArray(n);
                    CTGroupImpl.this.removeCallout(n);
                    return calloutArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfCalloutArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.CALLOUT$22, (List)list);
            final CTCallout[] array = new CTCallout[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCallout getCalloutArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCallout ctCallout = (CTCallout)this.get_store().find_element_user(CTGroupImpl.CALLOUT$22, n);
            if (ctCallout == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCallout;
        }
    }
    
    public int sizeOfCalloutArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.CALLOUT$22);
        }
    }
    
    public void setCalloutArray(final CTCallout[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.CALLOUT$22);
    }
    
    public void setCalloutArray(final int n, final CTCallout ctCallout) {
        this.generatedSetterHelperImpl((XmlObject)ctCallout, CTGroupImpl.CALLOUT$22, n, (short)2);
    }
    
    public CTCallout insertNewCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().insert_element_user(CTGroupImpl.CALLOUT$22, n);
        }
    }
    
    public CTCallout addNewCallout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCallout)this.get_store().add_element_user(CTGroupImpl.CALLOUT$22);
        }
    }
    
    public void removeCallout(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.CALLOUT$22, n);
        }
    }
    
    public List<CTLock> getLockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockList extends AbstractList<CTLock>
            {
                @Override
                public CTLock get(final int n) {
                    return CTGroupImpl.this.getLockArray(n);
                }
                
                @Override
                public CTLock set(final int n, final CTLock ctLock) {
                    final CTLock lockArray = CTGroupImpl.this.getLockArray(n);
                    CTGroupImpl.this.setLockArray(n, ctLock);
                    return lockArray;
                }
                
                @Override
                public void add(final int n, final CTLock ctLock) {
                    CTGroupImpl.this.insertNewLock(n).set((XmlObject)ctLock);
                }
                
                @Override
                public CTLock remove(final int n) {
                    final CTLock lockArray = CTGroupImpl.this.getLockArray(n);
                    CTGroupImpl.this.removeLock(n);
                    return lockArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfLockArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.LOCK$24, (List)list);
            final CTLock[] array = new CTLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLock getLockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLock ctLock = (CTLock)this.get_store().find_element_user(CTGroupImpl.LOCK$24, n);
            if (ctLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLock;
        }
    }
    
    public int sizeOfLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.LOCK$24);
        }
    }
    
    public void setLockArray(final CTLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.LOCK$24);
    }
    
    public void setLockArray(final int n, final CTLock ctLock) {
        this.generatedSetterHelperImpl((XmlObject)ctLock, CTGroupImpl.LOCK$24, n, (short)2);
    }
    
    public CTLock insertNewLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().insert_element_user(CTGroupImpl.LOCK$24, n);
        }
    }
    
    public CTLock addNewLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().add_element_user(CTGroupImpl.LOCK$24);
        }
    }
    
    public void removeLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.LOCK$24, n);
        }
    }
    
    public List<CTClipPath> getClippathList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClippathList extends AbstractList<CTClipPath>
            {
                @Override
                public CTClipPath get(final int n) {
                    return CTGroupImpl.this.getClippathArray(n);
                }
                
                @Override
                public CTClipPath set(final int n, final CTClipPath ctClipPath) {
                    final CTClipPath clippathArray = CTGroupImpl.this.getClippathArray(n);
                    CTGroupImpl.this.setClippathArray(n, ctClipPath);
                    return clippathArray;
                }
                
                @Override
                public void add(final int n, final CTClipPath ctClipPath) {
                    CTGroupImpl.this.insertNewClippath(n).set((XmlObject)ctClipPath);
                }
                
                @Override
                public CTClipPath remove(final int n) {
                    final CTClipPath clippathArray = CTGroupImpl.this.getClippathArray(n);
                    CTGroupImpl.this.removeClippath(n);
                    return clippathArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfClippathArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.CLIPPATH$26, (List)list);
            final CTClipPath[] array = new CTClipPath[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClipPath getClippathArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClipPath ctClipPath = (CTClipPath)this.get_store().find_element_user(CTGroupImpl.CLIPPATH$26, n);
            if (ctClipPath == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClipPath;
        }
    }
    
    public int sizeOfClippathArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.CLIPPATH$26);
        }
    }
    
    public void setClippathArray(final CTClipPath[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.CLIPPATH$26);
    }
    
    public void setClippathArray(final int n, final CTClipPath ctClipPath) {
        this.generatedSetterHelperImpl((XmlObject)ctClipPath, CTGroupImpl.CLIPPATH$26, n, (short)2);
    }
    
    public CTClipPath insertNewClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().insert_element_user(CTGroupImpl.CLIPPATH$26, n);
        }
    }
    
    public CTClipPath addNewClippath() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClipPath)this.get_store().add_element_user(CTGroupImpl.CLIPPATH$26);
        }
    }
    
    public void removeClippath(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.CLIPPATH$26, n);
        }
    }
    
    public List<CTSignatureLine> getSignaturelineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SignaturelineList extends AbstractList<CTSignatureLine>
            {
                @Override
                public CTSignatureLine get(final int n) {
                    return CTGroupImpl.this.getSignaturelineArray(n);
                }
                
                @Override
                public CTSignatureLine set(final int n, final CTSignatureLine ctSignatureLine) {
                    final CTSignatureLine signaturelineArray = CTGroupImpl.this.getSignaturelineArray(n);
                    CTGroupImpl.this.setSignaturelineArray(n, ctSignatureLine);
                    return signaturelineArray;
                }
                
                @Override
                public void add(final int n, final CTSignatureLine ctSignatureLine) {
                    CTGroupImpl.this.insertNewSignatureline(n).set((XmlObject)ctSignatureLine);
                }
                
                @Override
                public CTSignatureLine remove(final int n) {
                    final CTSignatureLine signaturelineArray = CTGroupImpl.this.getSignaturelineArray(n);
                    CTGroupImpl.this.removeSignatureline(n);
                    return signaturelineArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfSignaturelineArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.SIGNATURELINE$28, (List)list);
            final CTSignatureLine[] array = new CTSignatureLine[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSignatureLine getSignaturelineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSignatureLine ctSignatureLine = (CTSignatureLine)this.get_store().find_element_user(CTGroupImpl.SIGNATURELINE$28, n);
            if (ctSignatureLine == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSignatureLine;
        }
    }
    
    public int sizeOfSignaturelineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.SIGNATURELINE$28);
        }
    }
    
    public void setSignaturelineArray(final CTSignatureLine[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.SIGNATURELINE$28);
    }
    
    public void setSignaturelineArray(final int n, final CTSignatureLine ctSignatureLine) {
        this.generatedSetterHelperImpl((XmlObject)ctSignatureLine, CTGroupImpl.SIGNATURELINE$28, n, (short)2);
    }
    
    public CTSignatureLine insertNewSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().insert_element_user(CTGroupImpl.SIGNATURELINE$28, n);
        }
    }
    
    public CTSignatureLine addNewSignatureline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSignatureLine)this.get_store().add_element_user(CTGroupImpl.SIGNATURELINE$28);
        }
    }
    
    public void removeSignatureline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.SIGNATURELINE$28, n);
        }
    }
    
    public List<CTWrap> getWrapList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WrapList extends AbstractList<CTWrap>
            {
                @Override
                public CTWrap get(final int n) {
                    return CTGroupImpl.this.getWrapArray(n);
                }
                
                @Override
                public CTWrap set(final int n, final CTWrap ctWrap) {
                    final CTWrap wrapArray = CTGroupImpl.this.getWrapArray(n);
                    CTGroupImpl.this.setWrapArray(n, ctWrap);
                    return wrapArray;
                }
                
                @Override
                public void add(final int n, final CTWrap ctWrap) {
                    CTGroupImpl.this.insertNewWrap(n).set((XmlObject)ctWrap);
                }
                
                @Override
                public CTWrap remove(final int n) {
                    final CTWrap wrapArray = CTGroupImpl.this.getWrapArray(n);
                    CTGroupImpl.this.removeWrap(n);
                    return wrapArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfWrapArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.WRAP$30, (List)list);
            final CTWrap[] array = new CTWrap[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTWrap getWrapArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWrap ctWrap = (CTWrap)this.get_store().find_element_user(CTGroupImpl.WRAP$30, n);
            if (ctWrap == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctWrap;
        }
    }
    
    public int sizeOfWrapArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.WRAP$30);
        }
    }
    
    public void setWrapArray(final CTWrap[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.WRAP$30);
    }
    
    public void setWrapArray(final int n, final CTWrap ctWrap) {
        this.generatedSetterHelperImpl((XmlObject)ctWrap, CTGroupImpl.WRAP$30, n, (short)2);
    }
    
    public CTWrap insertNewWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().insert_element_user(CTGroupImpl.WRAP$30, n);
        }
    }
    
    public CTWrap addNewWrap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWrap)this.get_store().add_element_user(CTGroupImpl.WRAP$30);
        }
    }
    
    public void removeWrap(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.WRAP$30, n);
        }
    }
    
    public List<CTAnchorLock> getAnchorlockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AnchorlockList extends AbstractList<CTAnchorLock>
            {
                @Override
                public CTAnchorLock get(final int n) {
                    return CTGroupImpl.this.getAnchorlockArray(n);
                }
                
                @Override
                public CTAnchorLock set(final int n, final CTAnchorLock ctAnchorLock) {
                    final CTAnchorLock anchorlockArray = CTGroupImpl.this.getAnchorlockArray(n);
                    CTGroupImpl.this.setAnchorlockArray(n, ctAnchorLock);
                    return anchorlockArray;
                }
                
                @Override
                public void add(final int n, final CTAnchorLock ctAnchorLock) {
                    CTGroupImpl.this.insertNewAnchorlock(n).set((XmlObject)ctAnchorLock);
                }
                
                @Override
                public CTAnchorLock remove(final int n) {
                    final CTAnchorLock anchorlockArray = CTGroupImpl.this.getAnchorlockArray(n);
                    CTGroupImpl.this.removeAnchorlock(n);
                    return anchorlockArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfAnchorlockArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.ANCHORLOCK$32, (List)list);
            final CTAnchorLock[] array = new CTAnchorLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAnchorLock getAnchorlockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAnchorLock ctAnchorLock = (CTAnchorLock)this.get_store().find_element_user(CTGroupImpl.ANCHORLOCK$32, n);
            if (ctAnchorLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAnchorLock;
        }
    }
    
    public int sizeOfAnchorlockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.ANCHORLOCK$32);
        }
    }
    
    public void setAnchorlockArray(final CTAnchorLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.ANCHORLOCK$32);
    }
    
    public void setAnchorlockArray(final int n, final CTAnchorLock ctAnchorLock) {
        this.generatedSetterHelperImpl((XmlObject)ctAnchorLock, CTGroupImpl.ANCHORLOCK$32, n, (short)2);
    }
    
    public CTAnchorLock insertNewAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().insert_element_user(CTGroupImpl.ANCHORLOCK$32, n);
        }
    }
    
    public CTAnchorLock addNewAnchorlock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAnchorLock)this.get_store().add_element_user(CTGroupImpl.ANCHORLOCK$32);
        }
    }
    
    public void removeAnchorlock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.ANCHORLOCK$32, n);
        }
    }
    
    public List<CTBorder> getBordertopList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BordertopList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTGroupImpl.this.getBordertopArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder bordertopArray = CTGroupImpl.this.getBordertopArray(n);
                    CTGroupImpl.this.setBordertopArray(n, ctBorder);
                    return bordertopArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTGroupImpl.this.insertNewBordertop(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder bordertopArray = CTGroupImpl.this.getBordertopArray(n);
                    CTGroupImpl.this.removeBordertop(n);
                    return bordertopArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfBordertopArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.BORDERTOP$34, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBordertopArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTGroupImpl.BORDERTOP$34, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBordertopArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.BORDERTOP$34);
        }
    }
    
    public void setBordertopArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.BORDERTOP$34);
    }
    
    public void setBordertopArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTGroupImpl.BORDERTOP$34, n, (short)2);
    }
    
    public CTBorder insertNewBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTGroupImpl.BORDERTOP$34, n);
        }
    }
    
    public CTBorder addNewBordertop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTGroupImpl.BORDERTOP$34);
        }
    }
    
    public void removeBordertop(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.BORDERTOP$34, n);
        }
    }
    
    public List<CTBorder> getBorderbottomList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderbottomList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTGroupImpl.this.getBorderbottomArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderbottomArray = CTGroupImpl.this.getBorderbottomArray(n);
                    CTGroupImpl.this.setBorderbottomArray(n, ctBorder);
                    return borderbottomArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTGroupImpl.this.insertNewBorderbottom(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderbottomArray = CTGroupImpl.this.getBorderbottomArray(n);
                    CTGroupImpl.this.removeBorderbottom(n);
                    return borderbottomArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfBorderbottomArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.BORDERBOTTOM$36, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderbottomArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTGroupImpl.BORDERBOTTOM$36, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderbottomArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.BORDERBOTTOM$36);
        }
    }
    
    public void setBorderbottomArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.BORDERBOTTOM$36);
    }
    
    public void setBorderbottomArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTGroupImpl.BORDERBOTTOM$36, n, (short)2);
    }
    
    public CTBorder insertNewBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTGroupImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public CTBorder addNewBorderbottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTGroupImpl.BORDERBOTTOM$36);
        }
    }
    
    public void removeBorderbottom(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.BORDERBOTTOM$36, n);
        }
    }
    
    public List<CTBorder> getBorderleftList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderleftList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTGroupImpl.this.getBorderleftArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderleftArray = CTGroupImpl.this.getBorderleftArray(n);
                    CTGroupImpl.this.setBorderleftArray(n, ctBorder);
                    return borderleftArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTGroupImpl.this.insertNewBorderleft(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderleftArray = CTGroupImpl.this.getBorderleftArray(n);
                    CTGroupImpl.this.removeBorderleft(n);
                    return borderleftArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfBorderleftArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.BORDERLEFT$38, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderleftArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTGroupImpl.BORDERLEFT$38, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderleftArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.BORDERLEFT$38);
        }
    }
    
    public void setBorderleftArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.BORDERLEFT$38);
    }
    
    public void setBorderleftArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTGroupImpl.BORDERLEFT$38, n, (short)2);
    }
    
    public CTBorder insertNewBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTGroupImpl.BORDERLEFT$38, n);
        }
    }
    
    public CTBorder addNewBorderleft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTGroupImpl.BORDERLEFT$38);
        }
    }
    
    public void removeBorderleft(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.BORDERLEFT$38, n);
        }
    }
    
    public List<CTBorder> getBorderrightList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BorderrightList extends AbstractList<CTBorder>
            {
                @Override
                public CTBorder get(final int n) {
                    return CTGroupImpl.this.getBorderrightArray(n);
                }
                
                @Override
                public CTBorder set(final int n, final CTBorder ctBorder) {
                    final CTBorder borderrightArray = CTGroupImpl.this.getBorderrightArray(n);
                    CTGroupImpl.this.setBorderrightArray(n, ctBorder);
                    return borderrightArray;
                }
                
                @Override
                public void add(final int n, final CTBorder ctBorder) {
                    CTGroupImpl.this.insertNewBorderright(n).set((XmlObject)ctBorder);
                }
                
                @Override
                public CTBorder remove(final int n) {
                    final CTBorder borderrightArray = CTGroupImpl.this.getBorderrightArray(n);
                    CTGroupImpl.this.removeBorderright(n);
                    return borderrightArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfBorderrightArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.BORDERRIGHT$40, (List)list);
            final CTBorder[] array = new CTBorder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBorder getBorderrightArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBorder ctBorder = (CTBorder)this.get_store().find_element_user(CTGroupImpl.BORDERRIGHT$40, n);
            if (ctBorder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBorder;
        }
    }
    
    public int sizeOfBorderrightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.BORDERRIGHT$40);
        }
    }
    
    public void setBorderrightArray(final CTBorder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.BORDERRIGHT$40);
    }
    
    public void setBorderrightArray(final int n, final CTBorder ctBorder) {
        this.generatedSetterHelperImpl((XmlObject)ctBorder, CTGroupImpl.BORDERRIGHT$40, n, (short)2);
    }
    
    public CTBorder insertNewBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().insert_element_user(CTGroupImpl.BORDERRIGHT$40, n);
        }
    }
    
    public CTBorder addNewBorderright() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBorder)this.get_store().add_element_user(CTGroupImpl.BORDERRIGHT$40);
        }
    }
    
    public void removeBorderright(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.BORDERRIGHT$40, n);
        }
    }
    
    public List<CTClientData> getClientDataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ClientDataList extends AbstractList<CTClientData>
            {
                @Override
                public CTClientData get(final int n) {
                    return CTGroupImpl.this.getClientDataArray(n);
                }
                
                @Override
                public CTClientData set(final int n, final CTClientData ctClientData) {
                    final CTClientData clientDataArray = CTGroupImpl.this.getClientDataArray(n);
                    CTGroupImpl.this.setClientDataArray(n, ctClientData);
                    return clientDataArray;
                }
                
                @Override
                public void add(final int n, final CTClientData ctClientData) {
                    CTGroupImpl.this.insertNewClientData(n).set((XmlObject)ctClientData);
                }
                
                @Override
                public CTClientData remove(final int n) {
                    final CTClientData clientDataArray = CTGroupImpl.this.getClientDataArray(n);
                    CTGroupImpl.this.removeClientData(n);
                    return clientDataArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfClientDataArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.CLIENTDATA$42, (List)list);
            final CTClientData[] array = new CTClientData[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTClientData getClientDataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTClientData ctClientData = (CTClientData)this.get_store().find_element_user(CTGroupImpl.CLIENTDATA$42, n);
            if (ctClientData == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctClientData;
        }
    }
    
    public int sizeOfClientDataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.CLIENTDATA$42);
        }
    }
    
    public void setClientDataArray(final CTClientData[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.CLIENTDATA$42);
    }
    
    public void setClientDataArray(final int n, final CTClientData ctClientData) {
        this.generatedSetterHelperImpl((XmlObject)ctClientData, CTGroupImpl.CLIENTDATA$42, n, (short)2);
    }
    
    public CTClientData insertNewClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().insert_element_user(CTGroupImpl.CLIENTDATA$42, n);
        }
    }
    
    public CTClientData addNewClientData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTClientData)this.get_store().add_element_user(CTGroupImpl.CLIENTDATA$42);
        }
    }
    
    public void removeClientData(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.CLIENTDATA$42, n);
        }
    }
    
    public List<CTRel> getTextdataList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextdataList extends AbstractList<CTRel>
            {
                @Override
                public CTRel get(final int n) {
                    return CTGroupImpl.this.getTextdataArray(n);
                }
                
                @Override
                public CTRel set(final int n, final CTRel ctRel) {
                    final CTRel textdataArray = CTGroupImpl.this.getTextdataArray(n);
                    CTGroupImpl.this.setTextdataArray(n, ctRel);
                    return textdataArray;
                }
                
                @Override
                public void add(final int n, final CTRel ctRel) {
                    CTGroupImpl.this.insertNewTextdata(n).set((XmlObject)ctRel);
                }
                
                @Override
                public CTRel remove(final int n) {
                    final CTRel textdataArray = CTGroupImpl.this.getTextdataArray(n);
                    CTGroupImpl.this.removeTextdata(n);
                    return textdataArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfTextdataArray();
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
            this.get_store().find_all_element_users(CTGroupImpl.TEXTDATA$44, (List)list);
            final CTRel[] array = new CTRel[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRel getTextdataArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTGroupImpl.TEXTDATA$44, n);
            if (ctRel == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRel;
        }
    }
    
    public int sizeOfTextdataArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.TEXTDATA$44);
        }
    }
    
    public void setTextdataArray(final CTRel[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.TEXTDATA$44);
    }
    
    public void setTextdataArray(final int n, final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTGroupImpl.TEXTDATA$44, n, (short)2);
    }
    
    public CTRel insertNewTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().insert_element_user(CTGroupImpl.TEXTDATA$44, n);
        }
    }
    
    public CTRel addNewTextdata() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTGroupImpl.TEXTDATA$44);
        }
    }
    
    public void removeTextdata(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.TEXTDATA$44, n);
        }
    }
    
    public List<CTGroup> getGroupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GroupList extends AbstractList<CTGroup>
            {
                @Override
                public CTGroup get(final int n) {
                    return CTGroupImpl.this.getGroupArray(n);
                }
                
                @Override
                public CTGroup set(final int n, final CTGroup ctGroup) {
                    final CTGroup groupArray = CTGroupImpl.this.getGroupArray(n);
                    CTGroupImpl.this.setGroupArray(n, ctGroup);
                    return groupArray;
                }
                
                @Override
                public void add(final int n, final CTGroup ctGroup) {
                    CTGroupImpl.this.insertNewGroup(n).set((XmlObject)ctGroup);
                }
                
                @Override
                public CTGroup remove(final int n) {
                    final CTGroup groupArray = CTGroupImpl.this.getGroupArray(n);
                    CTGroupImpl.this.removeGroup(n);
                    return groupArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfGroupArray();
                }
            }
            return new GroupList();
        }
    }
    
    @Deprecated
    public CTGroup[] getGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.GROUP$46, (List)list);
            final CTGroup[] array = new CTGroup[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTGroup getGroupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGroup ctGroup = (CTGroup)this.get_store().find_element_user(CTGroupImpl.GROUP$46, n);
            if (ctGroup == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctGroup;
        }
    }
    
    public int sizeOfGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.GROUP$46);
        }
    }
    
    public void setGroupArray(final CTGroup[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.GROUP$46);
    }
    
    public void setGroupArray(final int n, final CTGroup ctGroup) {
        this.generatedSetterHelperImpl((XmlObject)ctGroup, CTGroupImpl.GROUP$46, n, (short)2);
    }
    
    public CTGroup insertNewGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroup)this.get_store().insert_element_user(CTGroupImpl.GROUP$46, n);
        }
    }
    
    public CTGroup addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGroup)this.get_store().add_element_user(CTGroupImpl.GROUP$46);
        }
    }
    
    public void removeGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.GROUP$46, n);
        }
    }
    
    public List<CTShape> getShapeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShapeList extends AbstractList<CTShape>
            {
                @Override
                public CTShape get(final int n) {
                    return CTGroupImpl.this.getShapeArray(n);
                }
                
                @Override
                public CTShape set(final int n, final CTShape ctShape) {
                    final CTShape shapeArray = CTGroupImpl.this.getShapeArray(n);
                    CTGroupImpl.this.setShapeArray(n, ctShape);
                    return shapeArray;
                }
                
                @Override
                public void add(final int n, final CTShape ctShape) {
                    CTGroupImpl.this.insertNewShape(n).set((XmlObject)ctShape);
                }
                
                @Override
                public CTShape remove(final int n) {
                    final CTShape shapeArray = CTGroupImpl.this.getShapeArray(n);
                    CTGroupImpl.this.removeShape(n);
                    return shapeArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfShapeArray();
                }
            }
            return new ShapeList();
        }
    }
    
    @Deprecated
    public CTShape[] getShapeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.SHAPE$48, (List)list);
            final CTShape[] array = new CTShape[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShape getShapeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTGroupImpl.SHAPE$48, n);
            if (ctShape == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShape;
        }
    }
    
    public int sizeOfShapeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.SHAPE$48);
        }
    }
    
    public void setShapeArray(final CTShape[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.SHAPE$48);
    }
    
    public void setShapeArray(final int n, final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTGroupImpl.SHAPE$48, n, (short)2);
    }
    
    public CTShape insertNewShape(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().insert_element_user(CTGroupImpl.SHAPE$48, n);
        }
    }
    
    public CTShape addNewShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTGroupImpl.SHAPE$48);
        }
    }
    
    public void removeShape(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.SHAPE$48, n);
        }
    }
    
    public List<CTShapetype> getShapetypeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShapetypeList extends AbstractList<CTShapetype>
            {
                @Override
                public CTShapetype get(final int n) {
                    return CTGroupImpl.this.getShapetypeArray(n);
                }
                
                @Override
                public CTShapetype set(final int n, final CTShapetype ctShapetype) {
                    final CTShapetype shapetypeArray = CTGroupImpl.this.getShapetypeArray(n);
                    CTGroupImpl.this.setShapetypeArray(n, ctShapetype);
                    return shapetypeArray;
                }
                
                @Override
                public void add(final int n, final CTShapetype ctShapetype) {
                    CTGroupImpl.this.insertNewShapetype(n).set((XmlObject)ctShapetype);
                }
                
                @Override
                public CTShapetype remove(final int n) {
                    final CTShapetype shapetypeArray = CTGroupImpl.this.getShapetypeArray(n);
                    CTGroupImpl.this.removeShapetype(n);
                    return shapetypeArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfShapetypeArray();
                }
            }
            return new ShapetypeList();
        }
    }
    
    @Deprecated
    public CTShapetype[] getShapetypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.SHAPETYPE$50, (List)list);
            final CTShapetype[] array = new CTShapetype[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTShapetype getShapetypeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapetype ctShapetype = (CTShapetype)this.get_store().find_element_user(CTGroupImpl.SHAPETYPE$50, n);
            if (ctShapetype == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctShapetype;
        }
    }
    
    public int sizeOfShapetypeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.SHAPETYPE$50);
        }
    }
    
    public void setShapetypeArray(final CTShapetype[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.SHAPETYPE$50);
    }
    
    public void setShapetypeArray(final int n, final CTShapetype ctShapetype) {
        this.generatedSetterHelperImpl((XmlObject)ctShapetype, CTGroupImpl.SHAPETYPE$50, n, (short)2);
    }
    
    public CTShapetype insertNewShapetype(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapetype)this.get_store().insert_element_user(CTGroupImpl.SHAPETYPE$50, n);
        }
    }
    
    public CTShapetype addNewShapetype() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapetype)this.get_store().add_element_user(CTGroupImpl.SHAPETYPE$50);
        }
    }
    
    public void removeShapetype(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.SHAPETYPE$50, n);
        }
    }
    
    public List<CTArc> getArcList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ArcList extends AbstractList<CTArc>
            {
                @Override
                public CTArc get(final int n) {
                    return CTGroupImpl.this.getArcArray(n);
                }
                
                @Override
                public CTArc set(final int n, final CTArc ctArc) {
                    final CTArc arcArray = CTGroupImpl.this.getArcArray(n);
                    CTGroupImpl.this.setArcArray(n, ctArc);
                    return arcArray;
                }
                
                @Override
                public void add(final int n, final CTArc ctArc) {
                    CTGroupImpl.this.insertNewArc(n).set((XmlObject)ctArc);
                }
                
                @Override
                public CTArc remove(final int n) {
                    final CTArc arcArray = CTGroupImpl.this.getArcArray(n);
                    CTGroupImpl.this.removeArc(n);
                    return arcArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfArcArray();
                }
            }
            return new ArcList();
        }
    }
    
    @Deprecated
    public CTArc[] getArcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.ARC$52, (List)list);
            final CTArc[] array = new CTArc[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTArc getArcArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTArc ctArc = (CTArc)this.get_store().find_element_user(CTGroupImpl.ARC$52, n);
            if (ctArc == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctArc;
        }
    }
    
    public int sizeOfArcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.ARC$52);
        }
    }
    
    public void setArcArray(final CTArc[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.ARC$52);
    }
    
    public void setArcArray(final int n, final CTArc ctArc) {
        this.generatedSetterHelperImpl((XmlObject)ctArc, CTGroupImpl.ARC$52, n, (short)2);
    }
    
    public CTArc insertNewArc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTArc)this.get_store().insert_element_user(CTGroupImpl.ARC$52, n);
        }
    }
    
    public CTArc addNewArc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTArc)this.get_store().add_element_user(CTGroupImpl.ARC$52);
        }
    }
    
    public void removeArc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.ARC$52, n);
        }
    }
    
    public List<CTCurve> getCurveList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CurveList extends AbstractList<CTCurve>
            {
                @Override
                public CTCurve get(final int n) {
                    return CTGroupImpl.this.getCurveArray(n);
                }
                
                @Override
                public CTCurve set(final int n, final CTCurve ctCurve) {
                    final CTCurve curveArray = CTGroupImpl.this.getCurveArray(n);
                    CTGroupImpl.this.setCurveArray(n, ctCurve);
                    return curveArray;
                }
                
                @Override
                public void add(final int n, final CTCurve ctCurve) {
                    CTGroupImpl.this.insertNewCurve(n).set((XmlObject)ctCurve);
                }
                
                @Override
                public CTCurve remove(final int n) {
                    final CTCurve curveArray = CTGroupImpl.this.getCurveArray(n);
                    CTGroupImpl.this.removeCurve(n);
                    return curveArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfCurveArray();
                }
            }
            return new CurveList();
        }
    }
    
    @Deprecated
    public CTCurve[] getCurveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.CURVE$54, (List)list);
            final CTCurve[] array = new CTCurve[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCurve getCurveArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCurve ctCurve = (CTCurve)this.get_store().find_element_user(CTGroupImpl.CURVE$54, n);
            if (ctCurve == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCurve;
        }
    }
    
    public int sizeOfCurveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.CURVE$54);
        }
    }
    
    public void setCurveArray(final CTCurve[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.CURVE$54);
    }
    
    public void setCurveArray(final int n, final CTCurve ctCurve) {
        this.generatedSetterHelperImpl((XmlObject)ctCurve, CTGroupImpl.CURVE$54, n, (short)2);
    }
    
    public CTCurve insertNewCurve(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCurve)this.get_store().insert_element_user(CTGroupImpl.CURVE$54, n);
        }
    }
    
    public CTCurve addNewCurve() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCurve)this.get_store().add_element_user(CTGroupImpl.CURVE$54);
        }
    }
    
    public void removeCurve(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.CURVE$54, n);
        }
    }
    
    public List<CTImage> getImageList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ImageList extends AbstractList<CTImage>
            {
                @Override
                public CTImage get(final int n) {
                    return CTGroupImpl.this.getImageArray(n);
                }
                
                @Override
                public CTImage set(final int n, final CTImage ctImage) {
                    final CTImage imageArray = CTGroupImpl.this.getImageArray(n);
                    CTGroupImpl.this.setImageArray(n, ctImage);
                    return imageArray;
                }
                
                @Override
                public void add(final int n, final CTImage ctImage) {
                    CTGroupImpl.this.insertNewImage(n).set((XmlObject)ctImage);
                }
                
                @Override
                public CTImage remove(final int n) {
                    final CTImage imageArray = CTGroupImpl.this.getImageArray(n);
                    CTGroupImpl.this.removeImage(n);
                    return imageArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfImageArray();
                }
            }
            return new ImageList();
        }
    }
    
    @Deprecated
    public CTImage[] getImageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.IMAGE$56, (List)list);
            final CTImage[] array = new CTImage[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTImage getImageArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTImage ctImage = (CTImage)this.get_store().find_element_user(CTGroupImpl.IMAGE$56, n);
            if (ctImage == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctImage;
        }
    }
    
    public int sizeOfImageArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.IMAGE$56);
        }
    }
    
    public void setImageArray(final CTImage[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.IMAGE$56);
    }
    
    public void setImageArray(final int n, final CTImage ctImage) {
        this.generatedSetterHelperImpl((XmlObject)ctImage, CTGroupImpl.IMAGE$56, n, (short)2);
    }
    
    public CTImage insertNewImage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImage)this.get_store().insert_element_user(CTGroupImpl.IMAGE$56, n);
        }
    }
    
    public CTImage addNewImage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTImage)this.get_store().add_element_user(CTGroupImpl.IMAGE$56);
        }
    }
    
    public void removeImage(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.IMAGE$56, n);
        }
    }
    
    public List<CTLine> getLineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LineList extends AbstractList<CTLine>
            {
                @Override
                public CTLine get(final int n) {
                    return CTGroupImpl.this.getLineArray(n);
                }
                
                @Override
                public CTLine set(final int n, final CTLine ctLine) {
                    final CTLine lineArray = CTGroupImpl.this.getLineArray(n);
                    CTGroupImpl.this.setLineArray(n, ctLine);
                    return lineArray;
                }
                
                @Override
                public void add(final int n, final CTLine ctLine) {
                    CTGroupImpl.this.insertNewLine(n).set((XmlObject)ctLine);
                }
                
                @Override
                public CTLine remove(final int n) {
                    final CTLine lineArray = CTGroupImpl.this.getLineArray(n);
                    CTGroupImpl.this.removeLine(n);
                    return lineArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfLineArray();
                }
            }
            return new LineList();
        }
    }
    
    @Deprecated
    public CTLine[] getLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.LINE$58, (List)list);
            final CTLine[] array = new CTLine[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLine getLineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLine ctLine = (CTLine)this.get_store().find_element_user(CTGroupImpl.LINE$58, n);
            if (ctLine == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLine;
        }
    }
    
    public int sizeOfLineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.LINE$58);
        }
    }
    
    public void setLineArray(final CTLine[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.LINE$58);
    }
    
    public void setLineArray(final int n, final CTLine ctLine) {
        this.generatedSetterHelperImpl((XmlObject)ctLine, CTGroupImpl.LINE$58, n, (short)2);
    }
    
    public CTLine insertNewLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLine)this.get_store().insert_element_user(CTGroupImpl.LINE$58, n);
        }
    }
    
    public CTLine addNewLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLine)this.get_store().add_element_user(CTGroupImpl.LINE$58);
        }
    }
    
    public void removeLine(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.LINE$58, n);
        }
    }
    
    public List<CTOval> getOvalList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OvalList extends AbstractList<CTOval>
            {
                @Override
                public CTOval get(final int n) {
                    return CTGroupImpl.this.getOvalArray(n);
                }
                
                @Override
                public CTOval set(final int n, final CTOval ctOval) {
                    final CTOval ovalArray = CTGroupImpl.this.getOvalArray(n);
                    CTGroupImpl.this.setOvalArray(n, ctOval);
                    return ovalArray;
                }
                
                @Override
                public void add(final int n, final CTOval ctOval) {
                    CTGroupImpl.this.insertNewOval(n).set((XmlObject)ctOval);
                }
                
                @Override
                public CTOval remove(final int n) {
                    final CTOval ovalArray = CTGroupImpl.this.getOvalArray(n);
                    CTGroupImpl.this.removeOval(n);
                    return ovalArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfOvalArray();
                }
            }
            return new OvalList();
        }
    }
    
    @Deprecated
    public CTOval[] getOvalArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.OVAL$60, (List)list);
            final CTOval[] array = new CTOval[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOval getOvalArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOval ctOval = (CTOval)this.get_store().find_element_user(CTGroupImpl.OVAL$60, n);
            if (ctOval == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOval;
        }
    }
    
    public int sizeOfOvalArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.OVAL$60);
        }
    }
    
    public void setOvalArray(final CTOval[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.OVAL$60);
    }
    
    public void setOvalArray(final int n, final CTOval ctOval) {
        this.generatedSetterHelperImpl((XmlObject)ctOval, CTGroupImpl.OVAL$60, n, (short)2);
    }
    
    public CTOval insertNewOval(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOval)this.get_store().insert_element_user(CTGroupImpl.OVAL$60, n);
        }
    }
    
    public CTOval addNewOval() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOval)this.get_store().add_element_user(CTGroupImpl.OVAL$60);
        }
    }
    
    public void removeOval(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.OVAL$60, n);
        }
    }
    
    public List<CTPolyLine> getPolylineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PolylineList extends AbstractList<CTPolyLine>
            {
                @Override
                public CTPolyLine get(final int n) {
                    return CTGroupImpl.this.getPolylineArray(n);
                }
                
                @Override
                public CTPolyLine set(final int n, final CTPolyLine ctPolyLine) {
                    final CTPolyLine polylineArray = CTGroupImpl.this.getPolylineArray(n);
                    CTGroupImpl.this.setPolylineArray(n, ctPolyLine);
                    return polylineArray;
                }
                
                @Override
                public void add(final int n, final CTPolyLine ctPolyLine) {
                    CTGroupImpl.this.insertNewPolyline(n).set((XmlObject)ctPolyLine);
                }
                
                @Override
                public CTPolyLine remove(final int n) {
                    final CTPolyLine polylineArray = CTGroupImpl.this.getPolylineArray(n);
                    CTGroupImpl.this.removePolyline(n);
                    return polylineArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfPolylineArray();
                }
            }
            return new PolylineList();
        }
    }
    
    @Deprecated
    public CTPolyLine[] getPolylineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.POLYLINE$62, (List)list);
            final CTPolyLine[] array = new CTPolyLine[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPolyLine getPolylineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPolyLine ctPolyLine = (CTPolyLine)this.get_store().find_element_user(CTGroupImpl.POLYLINE$62, n);
            if (ctPolyLine == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPolyLine;
        }
    }
    
    public int sizeOfPolylineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.POLYLINE$62);
        }
    }
    
    public void setPolylineArray(final CTPolyLine[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.POLYLINE$62);
    }
    
    public void setPolylineArray(final int n, final CTPolyLine ctPolyLine) {
        this.generatedSetterHelperImpl((XmlObject)ctPolyLine, CTGroupImpl.POLYLINE$62, n, (short)2);
    }
    
    public CTPolyLine insertNewPolyline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPolyLine)this.get_store().insert_element_user(CTGroupImpl.POLYLINE$62, n);
        }
    }
    
    public CTPolyLine addNewPolyline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPolyLine)this.get_store().add_element_user(CTGroupImpl.POLYLINE$62);
        }
    }
    
    public void removePolyline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.POLYLINE$62, n);
        }
    }
    
    public List<CTRect> getRectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RectList extends AbstractList<CTRect>
            {
                @Override
                public CTRect get(final int n) {
                    return CTGroupImpl.this.getRectArray(n);
                }
                
                @Override
                public CTRect set(final int n, final CTRect ctRect) {
                    final CTRect rectArray = CTGroupImpl.this.getRectArray(n);
                    CTGroupImpl.this.setRectArray(n, ctRect);
                    return rectArray;
                }
                
                @Override
                public void add(final int n, final CTRect ctRect) {
                    CTGroupImpl.this.insertNewRect(n).set((XmlObject)ctRect);
                }
                
                @Override
                public CTRect remove(final int n) {
                    final CTRect rectArray = CTGroupImpl.this.getRectArray(n);
                    CTGroupImpl.this.removeRect(n);
                    return rectArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfRectArray();
                }
            }
            return new RectList();
        }
    }
    
    @Deprecated
    public CTRect[] getRectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.RECT$64, (List)list);
            final CTRect[] array = new CTRect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRect getRectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRect ctRect = (CTRect)this.get_store().find_element_user(CTGroupImpl.RECT$64, n);
            if (ctRect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRect;
        }
    }
    
    public int sizeOfRectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.RECT$64);
        }
    }
    
    public void setRectArray(final CTRect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.RECT$64);
    }
    
    public void setRectArray(final int n, final CTRect ctRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRect, CTGroupImpl.RECT$64, n, (short)2);
    }
    
    public CTRect insertNewRect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRect)this.get_store().insert_element_user(CTGroupImpl.RECT$64, n);
        }
    }
    
    public CTRect addNewRect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRect)this.get_store().add_element_user(CTGroupImpl.RECT$64);
        }
    }
    
    public void removeRect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.RECT$64, n);
        }
    }
    
    public List<CTRoundRect> getRoundrectList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RoundrectList extends AbstractList<CTRoundRect>
            {
                @Override
                public CTRoundRect get(final int n) {
                    return CTGroupImpl.this.getRoundrectArray(n);
                }
                
                @Override
                public CTRoundRect set(final int n, final CTRoundRect ctRoundRect) {
                    final CTRoundRect roundrectArray = CTGroupImpl.this.getRoundrectArray(n);
                    CTGroupImpl.this.setRoundrectArray(n, ctRoundRect);
                    return roundrectArray;
                }
                
                @Override
                public void add(final int n, final CTRoundRect ctRoundRect) {
                    CTGroupImpl.this.insertNewRoundrect(n).set((XmlObject)ctRoundRect);
                }
                
                @Override
                public CTRoundRect remove(final int n) {
                    final CTRoundRect roundrectArray = CTGroupImpl.this.getRoundrectArray(n);
                    CTGroupImpl.this.removeRoundrect(n);
                    return roundrectArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfRoundrectArray();
                }
            }
            return new RoundrectList();
        }
    }
    
    @Deprecated
    public CTRoundRect[] getRoundrectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.ROUNDRECT$66, (List)list);
            final CTRoundRect[] array = new CTRoundRect[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRoundRect getRoundrectArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRoundRect ctRoundRect = (CTRoundRect)this.get_store().find_element_user(CTGroupImpl.ROUNDRECT$66, n);
            if (ctRoundRect == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRoundRect;
        }
    }
    
    public int sizeOfRoundrectArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.ROUNDRECT$66);
        }
    }
    
    public void setRoundrectArray(final CTRoundRect[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.ROUNDRECT$66);
    }
    
    public void setRoundrectArray(final int n, final CTRoundRect ctRoundRect) {
        this.generatedSetterHelperImpl((XmlObject)ctRoundRect, CTGroupImpl.ROUNDRECT$66, n, (short)2);
    }
    
    public CTRoundRect insertNewRoundrect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRoundRect)this.get_store().insert_element_user(CTGroupImpl.ROUNDRECT$66, n);
        }
    }
    
    public CTRoundRect addNewRoundrect() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRoundRect)this.get_store().add_element_user(CTGroupImpl.ROUNDRECT$66);
        }
    }
    
    public void removeRoundrect(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.ROUNDRECT$66, n);
        }
    }
    
    public List<CTDiagram> getDiagramList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DiagramList extends AbstractList<CTDiagram>
            {
                @Override
                public CTDiagram get(final int n) {
                    return CTGroupImpl.this.getDiagramArray(n);
                }
                
                @Override
                public CTDiagram set(final int n, final CTDiagram ctDiagram) {
                    final CTDiagram diagramArray = CTGroupImpl.this.getDiagramArray(n);
                    CTGroupImpl.this.setDiagramArray(n, ctDiagram);
                    return diagramArray;
                }
                
                @Override
                public void add(final int n, final CTDiagram ctDiagram) {
                    CTGroupImpl.this.insertNewDiagram(n).set((XmlObject)ctDiagram);
                }
                
                @Override
                public CTDiagram remove(final int n) {
                    final CTDiagram diagramArray = CTGroupImpl.this.getDiagramArray(n);
                    CTGroupImpl.this.removeDiagram(n);
                    return diagramArray;
                }
                
                @Override
                public int size() {
                    return CTGroupImpl.this.sizeOfDiagramArray();
                }
            }
            return new DiagramList();
        }
    }
    
    @Deprecated
    public CTDiagram[] getDiagramArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTGroupImpl.DIAGRAM$68, (List)list);
            final CTDiagram[] array = new CTDiagram[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDiagram getDiagramArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDiagram ctDiagram = (CTDiagram)this.get_store().find_element_user(CTGroupImpl.DIAGRAM$68, n);
            if (ctDiagram == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDiagram;
        }
    }
    
    public int sizeOfDiagramArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTGroupImpl.DIAGRAM$68);
        }
    }
    
    public void setDiagramArray(final CTDiagram[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTGroupImpl.DIAGRAM$68);
    }
    
    public void setDiagramArray(final int n, final CTDiagram ctDiagram) {
        this.generatedSetterHelperImpl((XmlObject)ctDiagram, CTGroupImpl.DIAGRAM$68, n, (short)2);
    }
    
    public CTDiagram insertNewDiagram(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDiagram)this.get_store().insert_element_user(CTGroupImpl.DIAGRAM$68, n);
        }
    }
    
    public CTDiagram addNewDiagram() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDiagram)this.get_store().add_element_user(CTGroupImpl.DIAGRAM$68);
        }
    }
    
    public void removeDiagram(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTGroupImpl.DIAGRAM$68, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ID$70);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.ID$70);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.ID$70) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ID$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.ID$70);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.ID$70);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.ID$70);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.ID$70);
        }
    }
    
    public String getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.STYLE$72);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.STYLE$72);
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.STYLE$72) != null;
        }
    }
    
    public void setStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.STYLE$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.STYLE$72);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetStyle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.STYLE$72);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.STYLE$72);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.STYLE$72);
        }
    }
    
    public String getHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HREF$74);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.HREF$74);
        }
    }
    
    public boolean isSetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HREF$74) != null;
        }
    }
    
    public void setHref(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HREF$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HREF$74);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHref(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.HREF$74);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.HREF$74);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HREF$74);
        }
    }
    
    public String getTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TARGET$76);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TARGET$76);
        }
    }
    
    public boolean isSetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.TARGET$76) != null;
        }
    }
    
    public void setTarget(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TARGET$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.TARGET$76);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTarget(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TARGET$76);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.TARGET$76);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.TARGET$76);
        }
    }
    
    public String getClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.CLASS1$78);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.CLASS1$78);
        }
    }
    
    public boolean isSetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.CLASS1$78) != null;
        }
    }
    
    public void setClass1(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.CLASS1$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.CLASS1$78);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetClass1(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.CLASS1$78);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.CLASS1$78);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetClass1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.CLASS1$78);
        }
    }
    
    public String getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TITLE$80);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TITLE$80);
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.TITLE$80) != null;
        }
    }
    
    public void setTitle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TITLE$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.TITLE$80);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTitle(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TITLE$80);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.TITLE$80);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.TITLE$80);
        }
    }
    
    public String getAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALT$82);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.ALT$82);
        }
    }
    
    public boolean isSetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.ALT$82) != null;
        }
    }
    
    public void setAlt(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALT$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.ALT$82);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAlt(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.ALT$82);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.ALT$82);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAlt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.ALT$82);
        }
    }
    
    public String getCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.COORDSIZE$84);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.COORDSIZE$84);
        }
    }
    
    public boolean isSetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.COORDSIZE$84) != null;
        }
    }
    
    public void setCoordsize(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.COORDSIZE$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.COORDSIZE$84);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordsize(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.COORDSIZE$84);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.COORDSIZE$84);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordsize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.COORDSIZE$84);
        }
    }
    
    public String getCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.COORDORIGIN$86);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.COORDORIGIN$86);
        }
    }
    
    public boolean isSetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.COORDORIGIN$86) != null;
        }
    }
    
    public void setCoordorigin(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.COORDORIGIN$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.COORDORIGIN$86);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCoordorigin(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.COORDORIGIN$86);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.COORDORIGIN$86);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCoordorigin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.COORDORIGIN$86);
        }
    }
    
    public String getWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.WRAPCOORDS$88);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.WRAPCOORDS$88);
        }
    }
    
    public boolean isSetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.WRAPCOORDS$88) != null;
        }
    }
    
    public void setWrapcoords(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.WRAPCOORDS$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.WRAPCOORDS$88);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetWrapcoords(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.WRAPCOORDS$88);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.WRAPCOORDS$88);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetWrapcoords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.WRAPCOORDS$88);
        }
    }
    
    public STTrueFalse.Enum getPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.PRINT$90);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.PRINT$90);
        }
    }
    
    public boolean isSetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.PRINT$90) != null;
        }
    }
    
    public void setPrint(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.PRINT$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.PRINT$90);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetPrint(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.PRINT$90);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.PRINT$90);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetPrint() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.PRINT$90);
        }
    }
    
    public String getSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.SPID$92);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.SPID$92);
        }
    }
    
    public boolean isSetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.SPID$92) != null;
        }
    }
    
    public void setSpid(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.SPID$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.SPID$92);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSpid(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.SPID$92);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.SPID$92);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSpid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.SPID$92);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ONED$94);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ONED$94);
        }
    }
    
    public boolean isSetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.ONED$94) != null;
        }
    }
    
    public void setOned(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ONED$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.ONED$94);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOned(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ONED$94);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.ONED$94);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetOned() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.ONED$94);
        }
    }
    
    public BigInteger getRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.REGROUPID$96);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.REGROUPID$96);
        }
    }
    
    public boolean isSetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.REGROUPID$96) != null;
        }
    }
    
    public void setRegroupid(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.REGROUPID$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.REGROUPID$96);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetRegroupid(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.REGROUPID$96);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTGroupImpl.REGROUPID$96);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetRegroupid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.REGROUPID$96);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
        }
    }
    
    public boolean isSetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98) != null;
        }
    }
    
    public void setDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetDoubleclicknotify(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.DOUBLECLICKNOTIFY$98);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetDoubleclicknotify() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.DOUBLECLICKNOTIFY$98);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BUTTON$100);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.BUTTON$100);
        }
    }
    
    public boolean isSetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BUTTON$100) != null;
        }
    }
    
    public void setButton(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BUTTON$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BUTTON$100);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetButton(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.BUTTON$100);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.BUTTON$100);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetButton() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BUTTON$100);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.USERHIDDEN$102);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.USERHIDDEN$102);
        }
    }
    
    public boolean isSetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.USERHIDDEN$102) != null;
        }
    }
    
    public void setUserhidden(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.USERHIDDEN$102);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.USERHIDDEN$102);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserhidden(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.USERHIDDEN$102);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.USERHIDDEN$102);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserhidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.USERHIDDEN$102);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BULLET$104);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.BULLET$104);
        }
    }
    
    public boolean isSetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BULLET$104) != null;
        }
    }
    
    public void setBullet(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BULLET$104);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BULLET$104);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBullet(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.BULLET$104);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.BULLET$104);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetBullet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BULLET$104);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HR$106);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HR$106);
        }
    }
    
    public boolean isSetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HR$106) != null;
        }
    }
    
    public void setHr(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HR$106);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HR$106);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHr(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HR$106);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.HR$106);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HR$106);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRSTD$108);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HRSTD$108);
        }
    }
    
    public boolean isSetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HRSTD$108) != null;
        }
    }
    
    public void setHrstd(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRSTD$108);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HRSTD$108);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrstd(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HRSTD$108);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.HRSTD$108);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrstd() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HRSTD$108);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRNOSHADE$110);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HRNOSHADE$110);
        }
    }
    
    public boolean isSetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HRNOSHADE$110) != null;
        }
    }
    
    public void setHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRNOSHADE$110);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HRNOSHADE$110);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHrnoshade(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.HRNOSHADE$110);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.HRNOSHADE$110);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetHrnoshade() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HRNOSHADE$110);
        }
    }
    
    public float getHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRPCT$112);
            if (simpleValue == null) {
                return 0.0f;
            }
            return simpleValue.getFloatValue();
        }
    }
    
    public XmlFloat xgetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlFloat)this.get_store().find_attribute_user(CTGroupImpl.HRPCT$112);
        }
    }
    
    public boolean isSetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HRPCT$112) != null;
        }
    }
    
    public void setHrpct(final float floatValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRPCT$112);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HRPCT$112);
            }
            simpleValue.setFloatValue(floatValue);
        }
    }
    
    public void xsetHrpct(final XmlFloat xmlFloat) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlFloat xmlFloat2 = (XmlFloat)this.get_store().find_attribute_user(CTGroupImpl.HRPCT$112);
            if (xmlFloat2 == null) {
                xmlFloat2 = (XmlFloat)this.get_store().add_attribute_user(CTGroupImpl.HRPCT$112);
            }
            xmlFloat2.set((XmlObject)xmlFloat);
        }
    }
    
    public void unsetHrpct() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HRPCT$112);
        }
    }
    
    public STHrAlign.Enum getHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRALIGN$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupImpl.HRALIGN$114);
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
            STHrAlign stHrAlign = (STHrAlign)this.get_store().find_attribute_user(CTGroupImpl.HRALIGN$114);
            if (stHrAlign == null) {
                stHrAlign = (STHrAlign)this.get_default_attribute_value(CTGroupImpl.HRALIGN$114);
            }
            return stHrAlign;
        }
    }
    
    public boolean isSetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.HRALIGN$114) != null;
        }
    }
    
    public void setHralign(final STHrAlign.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.HRALIGN$114);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.HRALIGN$114);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHralign(final STHrAlign stHrAlign) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHrAlign stHrAlign2 = (STHrAlign)this.get_store().find_attribute_user(CTGroupImpl.HRALIGN$114);
            if (stHrAlign2 == null) {
                stHrAlign2 = (STHrAlign)this.get_store().add_attribute_user(CTGroupImpl.HRALIGN$114);
            }
            stHrAlign2.set((XmlObject)stHrAlign);
        }
    }
    
    public void unsetHralign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.HRALIGN$114);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALLOWINCELL$116);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ALLOWINCELL$116);
        }
    }
    
    public boolean isSetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.ALLOWINCELL$116) != null;
        }
    }
    
    public void setAllowincell(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALLOWINCELL$116);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.ALLOWINCELL$116);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowincell(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ALLOWINCELL$116);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.ALLOWINCELL$116);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowincell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.ALLOWINCELL$116);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
        }
    }
    
    public boolean isSetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.ALLOWOVERLAP$118) != null;
        }
    }
    
    public void setAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAllowoverlap(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.ALLOWOVERLAP$118);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetAllowoverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.ALLOWOVERLAP$118);
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse.Enum getUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.USERDRAWN$120);
            if (simpleValue == null) {
                return null;
            }
            return (com.microsoft.schemas.office.office.STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public com.microsoft.schemas.office.office.STTrueFalse xgetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.USERDRAWN$120);
        }
    }
    
    public boolean isSetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.USERDRAWN$120) != null;
        }
    }
    
    public void setUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.USERDRAWN$120);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.USERDRAWN$120);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetUserdrawn(final com.microsoft.schemas.office.office.STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            com.microsoft.schemas.office.office.STTrueFalse stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.USERDRAWN$120);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (com.microsoft.schemas.office.office.STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.USERDRAWN$120);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetUserdrawn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.USERDRAWN$120);
        }
    }
    
    public String getBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
        }
    }
    
    public boolean isSetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122) != null;
        }
    }
    
    public void setBordertopcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBordertopcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.BORDERTOPCOLOR$122);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBordertopcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BORDERTOPCOLOR$122);
        }
    }
    
    public String getBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
        }
    }
    
    public boolean isSetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124) != null;
        }
    }
    
    public void setBorderleftcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderleftcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.BORDERLEFTCOLOR$124);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderleftcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BORDERLEFTCOLOR$124);
        }
    }
    
    public String getBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
        }
    }
    
    public boolean isSetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126) != null;
        }
    }
    
    public void setBorderbottomcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderbottomcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.BORDERBOTTOMCOLOR$126);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderbottomcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BORDERBOTTOMCOLOR$126);
        }
    }
    
    public String getBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
        }
    }
    
    public boolean isSetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128) != null;
        }
    }
    
    public void setBorderrightcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetBorderrightcolor(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.BORDERRIGHTCOLOR$128);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetBorderrightcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.BORDERRIGHTCOLOR$128);
        }
    }
    
    public BigInteger getDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUT$130);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUT$130);
        }
    }
    
    public boolean isSetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUT$130) != null;
        }
    }
    
    public void setDgmlayout(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUT$130);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.DGMLAYOUT$130);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayout(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUT$130);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTGroupImpl.DGMLAYOUT$130);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.DGMLAYOUT$130);
        }
    }
    
    public BigInteger getDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMNODEKIND$132);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMNODEKIND$132);
        }
    }
    
    public boolean isSetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.DGMNODEKIND$132) != null;
        }
    }
    
    public void setDgmnodekind(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMNODEKIND$132);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.DGMNODEKIND$132);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmnodekind(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMNODEKIND$132);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTGroupImpl.DGMNODEKIND$132);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmnodekind() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.DGMNODEKIND$132);
        }
    }
    
    public BigInteger getDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getBigIntegerValue();
        }
    }
    
    public XmlInteger xgetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
        }
    }
    
    public boolean isSetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134) != null;
        }
    }
    
    public void setDgmlayoutmru(final BigInteger bigIntegerValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
            }
            simpleValue.setBigIntegerValue(bigIntegerValue);
        }
    }
    
    public void xsetDgmlayoutmru(final XmlInteger xmlInteger) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInteger xmlInteger2 = (XmlInteger)this.get_store().find_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
            if (xmlInteger2 == null) {
                xmlInteger2 = (XmlInteger)this.get_store().add_attribute_user(CTGroupImpl.DGMLAYOUTMRU$134);
            }
            xmlInteger2.set((XmlObject)xmlInteger);
        }
    }
    
    public void unsetDgmlayoutmru() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.DGMLAYOUTMRU$134);
        }
    }
    
    public STInsetMode.Enum getInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.INSETMODE$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTGroupImpl.INSETMODE$136);
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
            STInsetMode stInsetMode = (STInsetMode)this.get_store().find_attribute_user(CTGroupImpl.INSETMODE$136);
            if (stInsetMode == null) {
                stInsetMode = (STInsetMode)this.get_default_attribute_value(CTGroupImpl.INSETMODE$136);
            }
            return stInsetMode;
        }
    }
    
    public boolean isSetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.INSETMODE$136) != null;
        }
    }
    
    public void setInsetmode(final STInsetMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.INSETMODE$136);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.INSETMODE$136);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetInsetmode(final STInsetMode stInsetMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STInsetMode stInsetMode2 = (STInsetMode)this.get_store().find_attribute_user(CTGroupImpl.INSETMODE$136);
            if (stInsetMode2 == null) {
                stInsetMode2 = (STInsetMode)this.get_store().add_attribute_user(CTGroupImpl.INSETMODE$136);
            }
            stInsetMode2.set((XmlObject)stInsetMode);
        }
    }
    
    public void unsetInsetmode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.INSETMODE$136);
        }
    }
    
    public STTrueFalse.Enum getFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.FILLED$138);
            if (simpleValue == null) {
                return null;
            }
            return (STTrueFalse.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTrueFalse xgetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.FILLED$138);
        }
    }
    
    public boolean isSetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.FILLED$138) != null;
        }
    }
    
    public void setFilled(final STTrueFalse.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.FILLED$138);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.FILLED$138);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFilled(final STTrueFalse stTrueFalse) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTrueFalse stTrueFalse2 = (STTrueFalse)this.get_store().find_attribute_user(CTGroupImpl.FILLED$138);
            if (stTrueFalse2 == null) {
                stTrueFalse2 = (STTrueFalse)this.get_store().add_attribute_user(CTGroupImpl.FILLED$138);
            }
            stTrueFalse2.set((XmlObject)stTrueFalse);
        }
    }
    
    public void unsetFilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.FILLED$138);
        }
    }
    
    public String getFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.FILLCOLOR$140);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STColorType xgetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorType)this.get_store().find_attribute_user(CTGroupImpl.FILLCOLOR$140);
        }
    }
    
    public boolean isSetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.FILLCOLOR$140) != null;
        }
    }
    
    public void setFillcolor(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.FILLCOLOR$140);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.FILLCOLOR$140);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFillcolor(final STColorType stColorType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorType stColorType2 = (STColorType)this.get_store().find_attribute_user(CTGroupImpl.FILLCOLOR$140);
            if (stColorType2 == null) {
                stColorType2 = (STColorType)this.get_store().add_attribute_user(CTGroupImpl.FILLCOLOR$140);
            }
            stColorType2.set((XmlObject)stColorType);
        }
    }
    
    public void unsetFillcolor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.FILLCOLOR$140);
        }
    }
    
    public STEditAs.Enum getEditas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.EDITAS$142);
            if (simpleValue == null) {
                return null;
            }
            return (STEditAs.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STEditAs xgetEditas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STEditAs)this.get_store().find_attribute_user(CTGroupImpl.EDITAS$142);
        }
    }
    
    public boolean isSetEditas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.EDITAS$142) != null;
        }
    }
    
    public void setEditas(final STEditAs.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.EDITAS$142);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.EDITAS$142);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetEditas(final STEditAs stEditAs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STEditAs stEditAs2 = (STEditAs)this.get_store().find_attribute_user(CTGroupImpl.EDITAS$142);
            if (stEditAs2 == null) {
                stEditAs2 = (STEditAs)this.get_store().add_attribute_user(CTGroupImpl.EDITAS$142);
            }
            stEditAs2.set((XmlObject)stEditAs);
        }
    }
    
    public void unsetEditas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.EDITAS$142);
        }
    }
    
    public String getTableproperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTableproperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
        }
    }
    
    public boolean isSetTableproperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.TABLEPROPERTIES$144) != null;
        }
    }
    
    public void setTableproperties(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTableproperties(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.TABLEPROPERTIES$144);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTableproperties() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.TABLEPROPERTIES$144);
        }
    }
    
    public String getTablelimits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TABLELIMITS$146);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTablelimits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TABLELIMITS$146);
        }
    }
    
    public boolean isSetTablelimits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTGroupImpl.TABLELIMITS$146) != null;
        }
    }
    
    public void setTablelimits(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTGroupImpl.TABLELIMITS$146);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTGroupImpl.TABLELIMITS$146);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTablelimits(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTGroupImpl.TABLELIMITS$146);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTGroupImpl.TABLELIMITS$146);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTablelimits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTGroupImpl.TABLELIMITS$146);
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
        GROUP$46 = new QName("urn:schemas-microsoft-com:vml", "group");
        SHAPE$48 = new QName("urn:schemas-microsoft-com:vml", "shape");
        SHAPETYPE$50 = new QName("urn:schemas-microsoft-com:vml", "shapetype");
        ARC$52 = new QName("urn:schemas-microsoft-com:vml", "arc");
        CURVE$54 = new QName("urn:schemas-microsoft-com:vml", "curve");
        IMAGE$56 = new QName("urn:schemas-microsoft-com:vml", "image");
        LINE$58 = new QName("urn:schemas-microsoft-com:vml", "line");
        OVAL$60 = new QName("urn:schemas-microsoft-com:vml", "oval");
        POLYLINE$62 = new QName("urn:schemas-microsoft-com:vml", "polyline");
        RECT$64 = new QName("urn:schemas-microsoft-com:vml", "rect");
        ROUNDRECT$66 = new QName("urn:schemas-microsoft-com:vml", "roundrect");
        DIAGRAM$68 = new QName("urn:schemas-microsoft-com:office:office", "diagram");
        ID$70 = new QName("", "id");
        STYLE$72 = new QName("", "style");
        HREF$74 = new QName("", "href");
        TARGET$76 = new QName("", "target");
        CLASS1$78 = new QName("", "class");
        TITLE$80 = new QName("", "title");
        ALT$82 = new QName("", "alt");
        COORDSIZE$84 = new QName("", "coordsize");
        COORDORIGIN$86 = new QName("", "coordorigin");
        WRAPCOORDS$88 = new QName("", "wrapcoords");
        PRINT$90 = new QName("", "print");
        SPID$92 = new QName("urn:schemas-microsoft-com:office:office", "spid");
        ONED$94 = new QName("urn:schemas-microsoft-com:office:office", "oned");
        REGROUPID$96 = new QName("urn:schemas-microsoft-com:office:office", "regroupid");
        DOUBLECLICKNOTIFY$98 = new QName("urn:schemas-microsoft-com:office:office", "doubleclicknotify");
        BUTTON$100 = new QName("urn:schemas-microsoft-com:office:office", "button");
        USERHIDDEN$102 = new QName("urn:schemas-microsoft-com:office:office", "userhidden");
        BULLET$104 = new QName("urn:schemas-microsoft-com:office:office", "bullet");
        HR$106 = new QName("urn:schemas-microsoft-com:office:office", "hr");
        HRSTD$108 = new QName("urn:schemas-microsoft-com:office:office", "hrstd");
        HRNOSHADE$110 = new QName("urn:schemas-microsoft-com:office:office", "hrnoshade");
        HRPCT$112 = new QName("urn:schemas-microsoft-com:office:office", "hrpct");
        HRALIGN$114 = new QName("urn:schemas-microsoft-com:office:office", "hralign");
        ALLOWINCELL$116 = new QName("urn:schemas-microsoft-com:office:office", "allowincell");
        ALLOWOVERLAP$118 = new QName("urn:schemas-microsoft-com:office:office", "allowoverlap");
        USERDRAWN$120 = new QName("urn:schemas-microsoft-com:office:office", "userdrawn");
        BORDERTOPCOLOR$122 = new QName("urn:schemas-microsoft-com:office:office", "bordertopcolor");
        BORDERLEFTCOLOR$124 = new QName("urn:schemas-microsoft-com:office:office", "borderleftcolor");
        BORDERBOTTOMCOLOR$126 = new QName("urn:schemas-microsoft-com:office:office", "borderbottomcolor");
        BORDERRIGHTCOLOR$128 = new QName("urn:schemas-microsoft-com:office:office", "borderrightcolor");
        DGMLAYOUT$130 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayout");
        DGMNODEKIND$132 = new QName("urn:schemas-microsoft-com:office:office", "dgmnodekind");
        DGMLAYOUTMRU$134 = new QName("urn:schemas-microsoft-com:office:office", "dgmlayoutmru");
        INSETMODE$136 = new QName("urn:schemas-microsoft-com:office:office", "insetmode");
        FILLED$138 = new QName("", "filled");
        FILLCOLOR$140 = new QName("", "fillcolor");
        EDITAS$142 = new QName("", "editas");
        TABLEPROPERTIES$144 = new QName("urn:schemas-microsoft-com:office:office", "tableproperties");
        TABLELIMITS$146 = new QName("urn:schemas-microsoft-com:office:office", "tablelimits");
    }
}
