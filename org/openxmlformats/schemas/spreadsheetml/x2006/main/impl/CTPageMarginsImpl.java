package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPageMarginsImpl extends XmlComplexContentImpl implements CTPageMargins
{
    private static final long serialVersionUID = 1L;
    private static final QName LEFT$0;
    private static final QName RIGHT$2;
    private static final QName TOP$4;
    private static final QName BOTTOM$6;
    private static final QName HEADER$8;
    private static final QName FOOTER$10;
    
    public CTPageMarginsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public double getLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.LEFT$0);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.LEFT$0);
        }
    }
    
    public void setLeft(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.LEFT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.LEFT$0);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetLeft(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.LEFT$0);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.LEFT$0);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.RIGHT$2);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetRight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.RIGHT$2);
        }
    }
    
    public void setRight(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.RIGHT$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.RIGHT$2);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetRight(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.RIGHT$2);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.RIGHT$2);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.TOP$4);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.TOP$4);
        }
    }
    
    public void setTop(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.TOP$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.TOP$4);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetTop(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.TOP$4);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.TOP$4);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.BOTTOM$6);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.BOTTOM$6);
        }
    }
    
    public void setBottom(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.BOTTOM$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.BOTTOM$6);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetBottom(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.BOTTOM$6);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.BOTTOM$6);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
        }
    }
    
    public void setHeader(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.HEADER$8);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetHeader(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.HEADER$8);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.HEADER$8);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public double getFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
        }
    }
    
    public void setFooter(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPageMarginsImpl.FOOTER$10);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetFooter(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPageMarginsImpl.FOOTER$10);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPageMarginsImpl.FOOTER$10);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    static {
        LEFT$0 = new QName("", "left");
        RIGHT$2 = new QName("", "right");
        TOP$4 = new QName("", "top");
        BOTTOM$6 = new QName("", "bottom");
        HEADER$8 = new QName("", "header");
        FOOTER$10 = new QName("", "footer");
    }
}
