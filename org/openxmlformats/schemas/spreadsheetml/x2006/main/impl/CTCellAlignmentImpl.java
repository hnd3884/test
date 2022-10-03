package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignment;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STHorizontalAlignment;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCellAlignmentImpl extends XmlComplexContentImpl implements CTCellAlignment
{
    private static final long serialVersionUID = 1L;
    private static final QName HORIZONTAL$0;
    private static final QName VERTICAL$2;
    private static final QName TEXTROTATION$4;
    private static final QName WRAPTEXT$6;
    private static final QName INDENT$8;
    private static final QName RELATIVEINDENT$10;
    private static final QName JUSTIFYLASTLINE$12;
    private static final QName SHRINKTOFIT$14;
    private static final QName READINGORDER$16;
    
    public CTCellAlignmentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STHorizontalAlignment.Enum getHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
            if (simpleValue == null) {
                return null;
            }
            return (STHorizontalAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STHorizontalAlignment xgetHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STHorizontalAlignment)this.get_store().find_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
        }
    }
    
    public boolean isSetHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0) != null;
        }
    }
    
    public void setHorizontal(final STHorizontalAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHorizontal(final STHorizontalAlignment stHorizontalAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STHorizontalAlignment stHorizontalAlignment2 = (STHorizontalAlignment)this.get_store().find_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
            if (stHorizontalAlignment2 == null) {
                stHorizontalAlignment2 = (STHorizontalAlignment)this.get_store().add_attribute_user(CTCellAlignmentImpl.HORIZONTAL$0);
            }
            stHorizontalAlignment2.set((XmlObject)stHorizontalAlignment);
        }
    }
    
    public void unsetHorizontal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.HORIZONTAL$0);
        }
    }
    
    public STVerticalAlignment.Enum getVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
            if (simpleValue == null) {
                return null;
            }
            return (STVerticalAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STVerticalAlignment xgetVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STVerticalAlignment)this.get_store().find_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
        }
    }
    
    public boolean isSetVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.VERTICAL$2) != null;
        }
    }
    
    public void setVertical(final STVerticalAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVertical(final STVerticalAlignment stVerticalAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STVerticalAlignment stVerticalAlignment2 = (STVerticalAlignment)this.get_store().find_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
            if (stVerticalAlignment2 == null) {
                stVerticalAlignment2 = (STVerticalAlignment)this.get_store().add_attribute_user(CTCellAlignmentImpl.VERTICAL$2);
            }
            stVerticalAlignment2.set((XmlObject)stVerticalAlignment);
        }
    }
    
    public void unsetVertical() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.VERTICAL$2);
        }
    }
    
    public long getTextRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetTextRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
        }
    }
    
    public boolean isSetTextRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4) != null;
        }
    }
    
    public void setTextRotation(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTextRotation(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellAlignmentImpl.TEXTROTATION$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetTextRotation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.TEXTROTATION$4);
        }
    }
    
    public boolean getWrapText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetWrapText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
        }
    }
    
    public boolean isSetWrapText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6) != null;
        }
    }
    
    public void setWrapText(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetWrapText(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellAlignmentImpl.WRAPTEXT$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetWrapText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.WRAPTEXT$6);
        }
    }
    
    public long getIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.INDENT$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.INDENT$8);
        }
    }
    
    public boolean isSetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.INDENT$8) != null;
        }
    }
    
    public void setIndent(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.INDENT$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.INDENT$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIndent(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.INDENT$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellAlignmentImpl.INDENT$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.INDENT$8);
        }
    }
    
    public int getRelativeIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetRelativeIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
        }
    }
    
    public boolean isSetRelativeIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10) != null;
        }
    }
    
    public void setRelativeIndent(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRelativeIndent(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCellAlignmentImpl.RELATIVEINDENT$10);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetRelativeIndent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.RELATIVEINDENT$10);
        }
    }
    
    public boolean getJustifyLastLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetJustifyLastLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
        }
    }
    
    public boolean isSetJustifyLastLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12) != null;
        }
    }
    
    public void setJustifyLastLine(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetJustifyLastLine(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetJustifyLastLine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.JUSTIFYLASTLINE$12);
        }
    }
    
    public boolean getShrinkToFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShrinkToFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
        }
    }
    
    public boolean isSetShrinkToFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14) != null;
        }
    }
    
    public void setShrinkToFit(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShrinkToFit(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellAlignmentImpl.SHRINKTOFIT$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShrinkToFit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.SHRINKTOFIT$14);
        }
    }
    
    public long getReadingOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetReadingOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
        }
    }
    
    public boolean isSetReadingOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellAlignmentImpl.READINGORDER$16) != null;
        }
    }
    
    public void setReadingOrder(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetReadingOrder(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellAlignmentImpl.READINGORDER$16);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetReadingOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellAlignmentImpl.READINGORDER$16);
        }
    }
    
    static {
        HORIZONTAL$0 = new QName("", "horizontal");
        VERTICAL$2 = new QName("", "vertical");
        TEXTROTATION$4 = new QName("", "textRotation");
        WRAPTEXT$6 = new QName("", "wrapText");
        INDENT$8 = new QName("", "indent");
        RELATIVEINDENT$10 = new QName("", "relativeIndent");
        JUSTIFYLASTLINE$12 = new QName("", "justifyLastLine");
        SHRINKTOFIT$14 = new QName("", "shrinkToFit");
        READINGORDER$16 = new QName("", "readingOrder");
    }
}
