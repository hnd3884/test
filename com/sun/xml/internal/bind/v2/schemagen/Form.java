package com.sun.xml.internal.bind.v2.schemagen;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Schema;
import javax.xml.bind.annotation.XmlNsForm;

enum Form
{
    QUALIFIED(XmlNsForm.QUALIFIED, true) {
        @Override
        void declare(final String attName, final Schema schema) {
            schema._attribute(attName, "qualified");
        }
    }, 
    UNQUALIFIED(XmlNsForm.UNQUALIFIED, false) {
        @Override
        void declare(final String attName, final Schema schema) {
            schema._attribute(attName, "unqualified");
        }
    }, 
    UNSET(XmlNsForm.UNSET, false) {
        @Override
        void declare(final String attName, final Schema schema) {
        }
    };
    
    private final XmlNsForm xnf;
    public final boolean isEffectivelyQualified;
    
    private Form(final XmlNsForm xnf, final boolean effectivelyQualified) {
        this.xnf = xnf;
        this.isEffectivelyQualified = effectivelyQualified;
    }
    
    abstract void declare(final String p0, final Schema p1);
    
    public void writeForm(final LocalElement e, final QName tagName) {
        this._writeForm(e, tagName);
    }
    
    public void writeForm(final LocalAttribute a, final QName tagName) {
        this._writeForm(a, tagName);
    }
    
    private void _writeForm(final TypedXmlWriter e, final QName tagName) {
        final boolean qualified = tagName.getNamespaceURI().length() > 0;
        if (qualified && this != Form.QUALIFIED) {
            e._attribute("form", "qualified");
        }
        else if (!qualified && this == Form.QUALIFIED) {
            e._attribute("form", "unqualified");
        }
    }
    
    public static Form get(final XmlNsForm xnf) {
        for (final Form v : values()) {
            if (v.xnf == xnf) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
}
