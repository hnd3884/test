package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import org.xml.sax.Attributes;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.istack.internal.Nullable;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;

public class XsiTypeLoader extends Loader
{
    private final JaxBeanInfo defaultBeanInfo;
    static final QName XsiTypeQNAME;
    
    public XsiTypeLoader(final JaxBeanInfo defaultBeanInfo) {
        super(true);
        this.defaultBeanInfo = defaultBeanInfo;
    }
    
    @Override
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        JaxBeanInfo beanInfo = parseXsiType(state, ea, this.defaultBeanInfo);
        if (beanInfo == null) {
            beanInfo = this.defaultBeanInfo;
        }
        final Loader loader = beanInfo.getLoader(null, false);
        state.setLoader(loader);
        loader.startElement(state, ea);
    }
    
    static JaxBeanInfo parseXsiType(final UnmarshallingContext.State state, final TagName ea, @Nullable final JaxBeanInfo defaultBeanInfo) throws SAXException {
        final UnmarshallingContext context = state.getContext();
        JaxBeanInfo beanInfo = null;
        final Attributes atts = ea.atts;
        final int idx = atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
        if (idx >= 0) {
            final String value = atts.getValue(idx);
            final QName type = DatatypeConverterImpl._parseQName(value, context);
            if (type == null) {
                Loader.reportError(Messages.NOT_A_QNAME.format(value), true);
            }
            else {
                if (defaultBeanInfo != null && defaultBeanInfo.getTypeNames().contains(type)) {
                    return defaultBeanInfo;
                }
                beanInfo = context.getJAXBContext().getGlobalType(type);
                if (beanInfo == null && context.parent.hasEventHandler() && context.shouldErrorBeReported()) {
                    final String nearest = context.getJAXBContext().getNearestTypeName(type);
                    if (nearest != null) {
                        Loader.reportError(Messages.UNRECOGNIZED_TYPE_NAME_MAYBE.format(type, nearest), true);
                    }
                    else {
                        Loader.reportError(Messages.UNRECOGNIZED_TYPE_NAME.format(type), true);
                    }
                }
            }
        }
        return beanInfo;
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        final Collection<QName> expAttrs = new HashSet<QName>();
        expAttrs.addAll(super.getExpectedAttributes());
        expAttrs.add(XsiTypeLoader.XsiTypeQNAME);
        return Collections.unmodifiableCollection((Collection<? extends QName>)expAttrs);
    }
    
    static {
        XsiTypeQNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    }
}
