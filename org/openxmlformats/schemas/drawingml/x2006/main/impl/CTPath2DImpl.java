package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathFillMode;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DArcTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DClose;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPath2DImpl extends XmlComplexContentImpl implements CTPath2D
{
    private static final long serialVersionUID = 1L;
    private static final QName CLOSE$0;
    private static final QName MOVETO$2;
    private static final QName LNTO$4;
    private static final QName ARCTO$6;
    private static final QName QUADBEZTO$8;
    private static final QName CUBICBEZTO$10;
    private static final QName W$12;
    private static final QName H$14;
    private static final QName FILL$16;
    private static final QName STROKE$18;
    private static final QName EXTRUSIONOK$20;
    
    public CTPath2DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTPath2DClose> getCloseList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CloseList extends AbstractList<CTPath2DClose>
            {
                @Override
                public CTPath2DClose get(final int n) {
                    return CTPath2DImpl.this.getCloseArray(n);
                }
                
                @Override
                public CTPath2DClose set(final int n, final CTPath2DClose ctPath2DClose) {
                    final CTPath2DClose closeArray = CTPath2DImpl.this.getCloseArray(n);
                    CTPath2DImpl.this.setCloseArray(n, ctPath2DClose);
                    return closeArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DClose ctPath2DClose) {
                    CTPath2DImpl.this.insertNewClose(n).set((XmlObject)ctPath2DClose);
                }
                
                @Override
                public CTPath2DClose remove(final int n) {
                    final CTPath2DClose closeArray = CTPath2DImpl.this.getCloseArray(n);
                    CTPath2DImpl.this.removeClose(n);
                    return closeArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfCloseArray();
                }
            }
            return new CloseList();
        }
    }
    
    @Deprecated
    public CTPath2DClose[] getCloseArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.CLOSE$0, (List)list);
            final CTPath2DClose[] array = new CTPath2DClose[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DClose getCloseArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DClose ctPath2DClose = (CTPath2DClose)this.get_store().find_element_user(CTPath2DImpl.CLOSE$0, n);
            if (ctPath2DClose == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DClose;
        }
    }
    
    public int sizeOfCloseArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.CLOSE$0);
        }
    }
    
    public void setCloseArray(final CTPath2DClose[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.CLOSE$0);
    }
    
    public void setCloseArray(final int n, final CTPath2DClose ctPath2DClose) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DClose, CTPath2DImpl.CLOSE$0, n, (short)2);
    }
    
    public CTPath2DClose insertNewClose(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DClose)this.get_store().insert_element_user(CTPath2DImpl.CLOSE$0, n);
        }
    }
    
    public CTPath2DClose addNewClose() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DClose)this.get_store().add_element_user(CTPath2DImpl.CLOSE$0);
        }
    }
    
    public void removeClose(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.CLOSE$0, n);
        }
    }
    
    public List<CTPath2DMoveTo> getMoveToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MoveToList extends AbstractList<CTPath2DMoveTo>
            {
                @Override
                public CTPath2DMoveTo get(final int n) {
                    return CTPath2DImpl.this.getMoveToArray(n);
                }
                
                @Override
                public CTPath2DMoveTo set(final int n, final CTPath2DMoveTo ctPath2DMoveTo) {
                    final CTPath2DMoveTo moveToArray = CTPath2DImpl.this.getMoveToArray(n);
                    CTPath2DImpl.this.setMoveToArray(n, ctPath2DMoveTo);
                    return moveToArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DMoveTo ctPath2DMoveTo) {
                    CTPath2DImpl.this.insertNewMoveTo(n).set((XmlObject)ctPath2DMoveTo);
                }
                
                @Override
                public CTPath2DMoveTo remove(final int n) {
                    final CTPath2DMoveTo moveToArray = CTPath2DImpl.this.getMoveToArray(n);
                    CTPath2DImpl.this.removeMoveTo(n);
                    return moveToArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfMoveToArray();
                }
            }
            return new MoveToList();
        }
    }
    
    @Deprecated
    public CTPath2DMoveTo[] getMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.MOVETO$2, (List)list);
            final CTPath2DMoveTo[] array = new CTPath2DMoveTo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DMoveTo getMoveToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DMoveTo ctPath2DMoveTo = (CTPath2DMoveTo)this.get_store().find_element_user(CTPath2DImpl.MOVETO$2, n);
            if (ctPath2DMoveTo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DMoveTo;
        }
    }
    
    public int sizeOfMoveToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.MOVETO$2);
        }
    }
    
    public void setMoveToArray(final CTPath2DMoveTo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.MOVETO$2);
    }
    
    public void setMoveToArray(final int n, final CTPath2DMoveTo ctPath2DMoveTo) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DMoveTo, CTPath2DImpl.MOVETO$2, n, (short)2);
    }
    
    public CTPath2DMoveTo insertNewMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DMoveTo)this.get_store().insert_element_user(CTPath2DImpl.MOVETO$2, n);
        }
    }
    
    public CTPath2DMoveTo addNewMoveTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DMoveTo)this.get_store().add_element_user(CTPath2DImpl.MOVETO$2);
        }
    }
    
    public void removeMoveTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.MOVETO$2, n);
        }
    }
    
    public List<CTPath2DLineTo> getLnToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LnToList extends AbstractList<CTPath2DLineTo>
            {
                @Override
                public CTPath2DLineTo get(final int n) {
                    return CTPath2DImpl.this.getLnToArray(n);
                }
                
                @Override
                public CTPath2DLineTo set(final int n, final CTPath2DLineTo ctPath2DLineTo) {
                    final CTPath2DLineTo lnToArray = CTPath2DImpl.this.getLnToArray(n);
                    CTPath2DImpl.this.setLnToArray(n, ctPath2DLineTo);
                    return lnToArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DLineTo ctPath2DLineTo) {
                    CTPath2DImpl.this.insertNewLnTo(n).set((XmlObject)ctPath2DLineTo);
                }
                
                @Override
                public CTPath2DLineTo remove(final int n) {
                    final CTPath2DLineTo lnToArray = CTPath2DImpl.this.getLnToArray(n);
                    CTPath2DImpl.this.removeLnTo(n);
                    return lnToArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfLnToArray();
                }
            }
            return new LnToList();
        }
    }
    
    @Deprecated
    public CTPath2DLineTo[] getLnToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.LNTO$4, (List)list);
            final CTPath2DLineTo[] array = new CTPath2DLineTo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DLineTo getLnToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DLineTo ctPath2DLineTo = (CTPath2DLineTo)this.get_store().find_element_user(CTPath2DImpl.LNTO$4, n);
            if (ctPath2DLineTo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DLineTo;
        }
    }
    
    public int sizeOfLnToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.LNTO$4);
        }
    }
    
    public void setLnToArray(final CTPath2DLineTo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.LNTO$4);
    }
    
    public void setLnToArray(final int n, final CTPath2DLineTo ctPath2DLineTo) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DLineTo, CTPath2DImpl.LNTO$4, n, (short)2);
    }
    
    public CTPath2DLineTo insertNewLnTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DLineTo)this.get_store().insert_element_user(CTPath2DImpl.LNTO$4, n);
        }
    }
    
    public CTPath2DLineTo addNewLnTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DLineTo)this.get_store().add_element_user(CTPath2DImpl.LNTO$4);
        }
    }
    
    public void removeLnTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.LNTO$4, n);
        }
    }
    
    public List<CTPath2DArcTo> getArcToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ArcToList extends AbstractList<CTPath2DArcTo>
            {
                @Override
                public CTPath2DArcTo get(final int n) {
                    return CTPath2DImpl.this.getArcToArray(n);
                }
                
                @Override
                public CTPath2DArcTo set(final int n, final CTPath2DArcTo ctPath2DArcTo) {
                    final CTPath2DArcTo arcToArray = CTPath2DImpl.this.getArcToArray(n);
                    CTPath2DImpl.this.setArcToArray(n, ctPath2DArcTo);
                    return arcToArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DArcTo ctPath2DArcTo) {
                    CTPath2DImpl.this.insertNewArcTo(n).set((XmlObject)ctPath2DArcTo);
                }
                
                @Override
                public CTPath2DArcTo remove(final int n) {
                    final CTPath2DArcTo arcToArray = CTPath2DImpl.this.getArcToArray(n);
                    CTPath2DImpl.this.removeArcTo(n);
                    return arcToArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfArcToArray();
                }
            }
            return new ArcToList();
        }
    }
    
    @Deprecated
    public CTPath2DArcTo[] getArcToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.ARCTO$6, (List)list);
            final CTPath2DArcTo[] array = new CTPath2DArcTo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DArcTo getArcToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DArcTo ctPath2DArcTo = (CTPath2DArcTo)this.get_store().find_element_user(CTPath2DImpl.ARCTO$6, n);
            if (ctPath2DArcTo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DArcTo;
        }
    }
    
    public int sizeOfArcToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.ARCTO$6);
        }
    }
    
    public void setArcToArray(final CTPath2DArcTo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.ARCTO$6);
    }
    
    public void setArcToArray(final int n, final CTPath2DArcTo ctPath2DArcTo) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DArcTo, CTPath2DImpl.ARCTO$6, n, (short)2);
    }
    
    public CTPath2DArcTo insertNewArcTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DArcTo)this.get_store().insert_element_user(CTPath2DImpl.ARCTO$6, n);
        }
    }
    
    public CTPath2DArcTo addNewArcTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DArcTo)this.get_store().add_element_user(CTPath2DImpl.ARCTO$6);
        }
    }
    
    public void removeArcTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.ARCTO$6, n);
        }
    }
    
    public List<CTPath2DQuadBezierTo> getQuadBezToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class QuadBezToList extends AbstractList<CTPath2DQuadBezierTo>
            {
                @Override
                public CTPath2DQuadBezierTo get(final int n) {
                    return CTPath2DImpl.this.getQuadBezToArray(n);
                }
                
                @Override
                public CTPath2DQuadBezierTo set(final int n, final CTPath2DQuadBezierTo ctPath2DQuadBezierTo) {
                    final CTPath2DQuadBezierTo quadBezToArray = CTPath2DImpl.this.getQuadBezToArray(n);
                    CTPath2DImpl.this.setQuadBezToArray(n, ctPath2DQuadBezierTo);
                    return quadBezToArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DQuadBezierTo ctPath2DQuadBezierTo) {
                    CTPath2DImpl.this.insertNewQuadBezTo(n).set((XmlObject)ctPath2DQuadBezierTo);
                }
                
                @Override
                public CTPath2DQuadBezierTo remove(final int n) {
                    final CTPath2DQuadBezierTo quadBezToArray = CTPath2DImpl.this.getQuadBezToArray(n);
                    CTPath2DImpl.this.removeQuadBezTo(n);
                    return quadBezToArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfQuadBezToArray();
                }
            }
            return new QuadBezToList();
        }
    }
    
    @Deprecated
    public CTPath2DQuadBezierTo[] getQuadBezToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.QUADBEZTO$8, (List)list);
            final CTPath2DQuadBezierTo[] array = new CTPath2DQuadBezierTo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DQuadBezierTo getQuadBezToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DQuadBezierTo ctPath2DQuadBezierTo = (CTPath2DQuadBezierTo)this.get_store().find_element_user(CTPath2DImpl.QUADBEZTO$8, n);
            if (ctPath2DQuadBezierTo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DQuadBezierTo;
        }
    }
    
    public int sizeOfQuadBezToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.QUADBEZTO$8);
        }
    }
    
    public void setQuadBezToArray(final CTPath2DQuadBezierTo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.QUADBEZTO$8);
    }
    
    public void setQuadBezToArray(final int n, final CTPath2DQuadBezierTo ctPath2DQuadBezierTo) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DQuadBezierTo, CTPath2DImpl.QUADBEZTO$8, n, (short)2);
    }
    
    public CTPath2DQuadBezierTo insertNewQuadBezTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DQuadBezierTo)this.get_store().insert_element_user(CTPath2DImpl.QUADBEZTO$8, n);
        }
    }
    
    public CTPath2DQuadBezierTo addNewQuadBezTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DQuadBezierTo)this.get_store().add_element_user(CTPath2DImpl.QUADBEZTO$8);
        }
    }
    
    public void removeQuadBezTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.QUADBEZTO$8, n);
        }
    }
    
    public List<CTPath2DCubicBezierTo> getCubicBezToList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CubicBezToList extends AbstractList<CTPath2DCubicBezierTo>
            {
                @Override
                public CTPath2DCubicBezierTo get(final int n) {
                    return CTPath2DImpl.this.getCubicBezToArray(n);
                }
                
                @Override
                public CTPath2DCubicBezierTo set(final int n, final CTPath2DCubicBezierTo ctPath2DCubicBezierTo) {
                    final CTPath2DCubicBezierTo cubicBezToArray = CTPath2DImpl.this.getCubicBezToArray(n);
                    CTPath2DImpl.this.setCubicBezToArray(n, ctPath2DCubicBezierTo);
                    return cubicBezToArray;
                }
                
                @Override
                public void add(final int n, final CTPath2DCubicBezierTo ctPath2DCubicBezierTo) {
                    CTPath2DImpl.this.insertNewCubicBezTo(n).set((XmlObject)ctPath2DCubicBezierTo);
                }
                
                @Override
                public CTPath2DCubicBezierTo remove(final int n) {
                    final CTPath2DCubicBezierTo cubicBezToArray = CTPath2DImpl.this.getCubicBezToArray(n);
                    CTPath2DImpl.this.removeCubicBezTo(n);
                    return cubicBezToArray;
                }
                
                @Override
                public int size() {
                    return CTPath2DImpl.this.sizeOfCubicBezToArray();
                }
            }
            return new CubicBezToList();
        }
    }
    
    @Deprecated
    public CTPath2DCubicBezierTo[] getCubicBezToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPath2DImpl.CUBICBEZTO$10, (List)list);
            final CTPath2DCubicBezierTo[] array = new CTPath2DCubicBezierTo[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPath2DCubicBezierTo getCubicBezToArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPath2DCubicBezierTo ctPath2DCubicBezierTo = (CTPath2DCubicBezierTo)this.get_store().find_element_user(CTPath2DImpl.CUBICBEZTO$10, n);
            if (ctPath2DCubicBezierTo == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPath2DCubicBezierTo;
        }
    }
    
    public int sizeOfCubicBezToArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPath2DImpl.CUBICBEZTO$10);
        }
    }
    
    public void setCubicBezToArray(final CTPath2DCubicBezierTo[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPath2DImpl.CUBICBEZTO$10);
    }
    
    public void setCubicBezToArray(final int n, final CTPath2DCubicBezierTo ctPath2DCubicBezierTo) {
        this.generatedSetterHelperImpl((XmlObject)ctPath2DCubicBezierTo, CTPath2DImpl.CUBICBEZTO$10, n, (short)2);
    }
    
    public CTPath2DCubicBezierTo insertNewCubicBezTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DCubicBezierTo)this.get_store().insert_element_user(CTPath2DImpl.CUBICBEZTO$10, n);
        }
    }
    
    public CTPath2DCubicBezierTo addNewCubicBezTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPath2DCubicBezierTo)this.get_store().add_element_user(CTPath2DImpl.CUBICBEZTO$10);
        }
    }
    
    public void removeCubicBezTo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPath2DImpl.CUBICBEZTO$10, n);
        }
    }
    
    public long getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.W$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPath2DImpl.W$12);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPath2DImpl.W$12);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTPath2DImpl.W$12);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPath2DImpl.W$12) != null;
        }
    }
    
    public void setW(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.W$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPath2DImpl.W$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetW(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPath2DImpl.W$12);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTPath2DImpl.W$12);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPath2DImpl.W$12);
        }
    }
    
    public long getH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.H$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPath2DImpl.H$14);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STPositiveCoordinate xgetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPath2DImpl.H$14);
            if (stPositiveCoordinate == null) {
                stPositiveCoordinate = (STPositiveCoordinate)this.get_default_attribute_value(CTPath2DImpl.H$14);
            }
            return stPositiveCoordinate;
        }
    }
    
    public boolean isSetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPath2DImpl.H$14) != null;
        }
    }
    
    public void setH(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.H$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPath2DImpl.H$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetH(final STPositiveCoordinate stPositiveCoordinate) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositiveCoordinate stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().find_attribute_user(CTPath2DImpl.H$14);
            if (stPositiveCoordinate2 == null) {
                stPositiveCoordinate2 = (STPositiveCoordinate)this.get_store().add_attribute_user(CTPath2DImpl.H$14);
            }
            stPositiveCoordinate2.set((XmlObject)stPositiveCoordinate);
        }
    }
    
    public void unsetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPath2DImpl.H$14);
        }
    }
    
    public STPathFillMode.Enum getFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.FILL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPath2DImpl.FILL$16);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STPathFillMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPathFillMode xgetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPathFillMode stPathFillMode = (STPathFillMode)this.get_store().find_attribute_user(CTPath2DImpl.FILL$16);
            if (stPathFillMode == null) {
                stPathFillMode = (STPathFillMode)this.get_default_attribute_value(CTPath2DImpl.FILL$16);
            }
            return stPathFillMode;
        }
    }
    
    public boolean isSetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPath2DImpl.FILL$16) != null;
        }
    }
    
    public void setFill(final STPathFillMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.FILL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPath2DImpl.FILL$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFill(final STPathFillMode stPathFillMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPathFillMode stPathFillMode2 = (STPathFillMode)this.get_store().find_attribute_user(CTPath2DImpl.FILL$16);
            if (stPathFillMode2 == null) {
                stPathFillMode2 = (STPathFillMode)this.get_store().add_attribute_user(CTPath2DImpl.FILL$16);
            }
            stPathFillMode2.set((XmlObject)stPathFillMode);
        }
    }
    
    public void unsetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPath2DImpl.FILL$16);
        }
    }
    
    public boolean getStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.STROKE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPath2DImpl.STROKE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPath2DImpl.STROKE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPath2DImpl.STROKE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPath2DImpl.STROKE$18) != null;
        }
    }
    
    public void setStroke(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.STROKE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPath2DImpl.STROKE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetStroke(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPath2DImpl.STROKE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPath2DImpl.STROKE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetStroke() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPath2DImpl.STROKE$18);
        }
    }
    
    public boolean getExtrusionOk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPath2DImpl.EXTRUSIONOK$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetExtrusionOk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPath2DImpl.EXTRUSIONOK$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetExtrusionOk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPath2DImpl.EXTRUSIONOK$20) != null;
        }
    }
    
    public void setExtrusionOk(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetExtrusionOk(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPath2DImpl.EXTRUSIONOK$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetExtrusionOk() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPath2DImpl.EXTRUSIONOK$20);
        }
    }
    
    static {
        CLOSE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "close");
        MOVETO$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "moveTo");
        LNTO$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnTo");
        ARCTO$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "arcTo");
        QUADBEZTO$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "quadBezTo");
        CUBICBEZTO$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cubicBezTo");
        W$12 = new QName("", "w");
        H$14 = new QName("", "h");
        FILL$16 = new QName("", "fill");
        STROKE$18 = new QName("", "stroke");
        EXTRUSIONOK$20 = new QName("", "extrusionOk");
    }
}
