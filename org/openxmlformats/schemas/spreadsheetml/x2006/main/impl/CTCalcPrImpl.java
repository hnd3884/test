package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlDouble;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRefMode;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCalcMode;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCalcPrImpl extends XmlComplexContentImpl implements CTCalcPr
{
    private static final long serialVersionUID = 1L;
    private static final QName CALCID$0;
    private static final QName CALCMODE$2;
    private static final QName FULLCALCONLOAD$4;
    private static final QName REFMODE$6;
    private static final QName ITERATE$8;
    private static final QName ITERATECOUNT$10;
    private static final QName ITERATEDELTA$12;
    private static final QName FULLPRECISION$14;
    private static final QName CALCCOMPLETED$16;
    private static final QName CALCONSAVE$18;
    private static final QName CONCURRENTCALC$20;
    private static final QName CONCURRENTMANUALCOUNT$22;
    private static final QName FORCEFULLCALC$24;
    
    public CTCalcPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getCalcId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCalcId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.CALCID$0);
        }
    }
    
    public boolean isSetCalcId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CALCID$0) != null;
        }
    }
    
    public void setCalcId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CALCID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCalcId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.CALCID$0);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCalcPrImpl.CALCID$0);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCalcId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CALCID$0);
        }
    }
    
    public STCalcMode.Enum getCalcMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCMODE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.CALCMODE$2);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STCalcMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCalcMode xgetCalcMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCalcMode stCalcMode = (STCalcMode)this.get_store().find_attribute_user(CTCalcPrImpl.CALCMODE$2);
            if (stCalcMode == null) {
                stCalcMode = (STCalcMode)this.get_default_attribute_value(CTCalcPrImpl.CALCMODE$2);
            }
            return stCalcMode;
        }
    }
    
    public boolean isSetCalcMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CALCMODE$2) != null;
        }
    }
    
    public void setCalcMode(final STCalcMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCMODE$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CALCMODE$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetCalcMode(final STCalcMode stCalcMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCalcMode stCalcMode2 = (STCalcMode)this.get_store().find_attribute_user(CTCalcPrImpl.CALCMODE$2);
            if (stCalcMode2 == null) {
                stCalcMode2 = (STCalcMode)this.get_store().add_attribute_user(CTCalcPrImpl.CALCMODE$2);
            }
            stCalcMode2.set((XmlObject)stCalcMode);
        }
    }
    
    public void unsetCalcMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CALCMODE$2);
        }
    }
    
    public boolean getFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.FULLCALCONLOAD$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.FULLCALCONLOAD$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4) != null;
        }
    }
    
    public void setFullCalcOnLoad(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFullCalcOnLoad(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.FULLCALCONLOAD$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFullCalcOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.FULLCALCONLOAD$4);
        }
    }
    
    public STRefMode.Enum getRefMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.REFMODE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.REFMODE$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STRefMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STRefMode xgetRefMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRefMode stRefMode = (STRefMode)this.get_store().find_attribute_user(CTCalcPrImpl.REFMODE$6);
            if (stRefMode == null) {
                stRefMode = (STRefMode)this.get_default_attribute_value(CTCalcPrImpl.REFMODE$6);
            }
            return stRefMode;
        }
    }
    
    public boolean isSetRefMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.REFMODE$6) != null;
        }
    }
    
    public void setRefMode(final STRefMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.REFMODE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.REFMODE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRefMode(final STRefMode stRefMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRefMode stRefMode2 = (STRefMode)this.get_store().find_attribute_user(CTCalcPrImpl.REFMODE$6);
            if (stRefMode2 == null) {
                stRefMode2 = (STRefMode)this.get_store().add_attribute_user(CTCalcPrImpl.REFMODE$6);
            }
            stRefMode2.set((XmlObject)stRefMode);
        }
    }
    
    public void unsetRefMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.REFMODE$6);
        }
    }
    
    public boolean getIterate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.ITERATE$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIterate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATE$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.ITERATE$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetIterate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.ITERATE$8) != null;
        }
    }
    
    public void setIterate(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATE$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATE$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIterate(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATE$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATE$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIterate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.ITERATE$8);
        }
    }
    
    public long getIterateCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.ITERATECOUNT$10);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIterateCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCalcPrImpl.ITERATECOUNT$10);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetIterateCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.ITERATECOUNT$10) != null;
        }
    }
    
    public void setIterateCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIterateCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATECOUNT$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIterateCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.ITERATECOUNT$10);
        }
    }
    
    public double getIterateDelta() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.ITERATEDELTA$12);
            }
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetIterateDelta() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble = (XmlDouble)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            if (xmlDouble == null) {
                xmlDouble = (XmlDouble)this.get_default_attribute_value(CTCalcPrImpl.ITERATEDELTA$12);
            }
            return xmlDouble;
        }
    }
    
    public boolean isSetIterateDelta() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.ITERATEDELTA$12) != null;
        }
    }
    
    public void setIterateDelta(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetIterateDelta(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTCalcPrImpl.ITERATEDELTA$12);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetIterateDelta() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.ITERATEDELTA$12);
        }
    }
    
    public boolean getFullPrecision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.FULLPRECISION$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetFullPrecision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.FULLPRECISION$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetFullPrecision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.FULLPRECISION$14) != null;
        }
    }
    
    public void setFullPrecision(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetFullPrecision(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.FULLPRECISION$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetFullPrecision() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.FULLPRECISION$14);
        }
    }
    
    public boolean getCalcCompleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.CALCCOMPLETED$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCalcCompleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.CALCCOMPLETED$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCalcCompleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16) != null;
        }
    }
    
    public void setCalcCompleted(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCalcCompleted(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.CALCCOMPLETED$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCalcCompleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CALCCOMPLETED$16);
        }
    }
    
    public boolean getCalcOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.CALCONSAVE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCalcOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.CALCONSAVE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCalcOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CALCONSAVE$18) != null;
        }
    }
    
    public void setCalcOnSave(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCalcOnSave(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.CALCONSAVE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCalcOnSave() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CALCONSAVE$18);
        }
    }
    
    public boolean getConcurrentCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcPrImpl.CONCURRENTCALC$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetConcurrentCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcPrImpl.CONCURRENTCALC$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetConcurrentCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20) != null;
        }
    }
    
    public void setConcurrentCalc(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetConcurrentCalc(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.CONCURRENTCALC$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetConcurrentCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CONCURRENTCALC$20);
        }
    }
    
    public long getConcurrentManualCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetConcurrentManualCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
        }
    }
    
    public boolean isSetConcurrentManualCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22) != null;
        }
    }
    
    public void setConcurrentManualCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetConcurrentManualCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetConcurrentManualCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.CONCURRENTMANUALCOUNT$22);
        }
    }
    
    public boolean getForceFullCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetForceFullCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
        }
    }
    
    public boolean isSetForceFullCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24) != null;
        }
    }
    
    public void setForceFullCalc(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetForceFullCalc(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcPrImpl.FORCEFULLCALC$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetForceFullCalc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcPrImpl.FORCEFULLCALC$24);
        }
    }
    
    static {
        CALCID$0 = new QName("", "calcId");
        CALCMODE$2 = new QName("", "calcMode");
        FULLCALCONLOAD$4 = new QName("", "fullCalcOnLoad");
        REFMODE$6 = new QName("", "refMode");
        ITERATE$8 = new QName("", "iterate");
        ITERATECOUNT$10 = new QName("", "iterateCount");
        ITERATEDELTA$12 = new QName("", "iterateDelta");
        FULLPRECISION$14 = new QName("", "fullPrecision");
        CALCCOMPLETED$16 = new QName("", "calcCompleted");
        CALCONSAVE$18 = new QName("", "calcOnSave");
        CONCURRENTCALC$20 = new QName("", "concurrentCalc");
        CONCURRENTMANUALCOUNT$22 = new QName("", "concurrentManualCount");
        FORCEFULLCALC$24 = new QName("", "forceFullCalc");
    }
}
