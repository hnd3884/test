package org.apache.xmlbeans.impl.validator;

import org.apache.xmlbeans.impl.common.XmlWhitespace;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.impl.common.PrefixResolver;
import java.util.Collection;
import org.apache.xmlbeans.SchemaType;

public class ValidatorUtil
{
    public static boolean validateSimpleType(final SchemaType type, final String value, final Collection errors, final PrefixResolver prefixResolver) {
        if (type.isSimpleType() || type.getContentType() == 2) {
            final Validator validator = new Validator(type, null, type.getTypeSystem(), null, errors);
            final EventImpl ev = new EventImpl(prefixResolver, value);
            validator.nextEvent(1, ev);
            validator.nextEvent(3, ev);
            validator.nextEvent(2, ev);
            return validator.isValid();
        }
        assert false;
        throw new RuntimeException("Not a simple type");
    }
    
    private static class EventImpl implements ValidatorListener.Event
    {
        PrefixResolver _prefixResolver;
        String _text;
        
        EventImpl(final PrefixResolver prefixResolver, final String text) {
            this._prefixResolver = prefixResolver;
            this._text = text;
        }
        
        @Override
        public XmlCursor getLocationAsCursor() {
            return null;
        }
        
        @Override
        public Location getLocation() {
            return null;
        }
        
        @Override
        public String getXsiType() {
            return null;
        }
        
        @Override
        public String getXsiNil() {
            return null;
        }
        
        @Override
        public String getXsiLoc() {
            return null;
        }
        
        @Override
        public String getXsiNoLoc() {
            return null;
        }
        
        @Override
        public QName getName() {
            return null;
        }
        
        @Override
        public String getText() {
            return this._text;
        }
        
        @Override
        public String getText(final int wsr) {
            return XmlWhitespace.collapse(this._text, wsr);
        }
        
        @Override
        public boolean textIsWhitespace() {
            return false;
        }
        
        @Override
        public String getNamespaceForPrefix(final String prefix) {
            return this._prefixResolver.getNamespaceForPrefix(prefix);
        }
    }
}
