package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.Unmarshaller;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Iterator;
import java.util.Collections;
import javax.xml.namespace.QName;
import java.util.Collection;
import org.xml.sax.SAXException;

public abstract class Loader
{
    protected boolean expectText;
    
    protected Loader(final boolean expectText) {
        this.expectText = expectText;
    }
    
    protected Loader() {
    }
    
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
    }
    
    public void childElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        this.reportUnexpectedChildElement(ea, true);
        state.setLoader(Discarder.INSTANCE);
        state.setReceiver(null);
    }
    
    protected final void reportUnexpectedChildElement(final TagName ea, final boolean canRecover) throws SAXException {
        if (canRecover) {
            final UnmarshallingContext context = UnmarshallingContext.getInstance();
            if (!context.parent.hasEventHandler() || !context.shouldErrorBeReported()) {
                return;
            }
        }
        if (ea.uri != ea.uri.intern() || ea.local != ea.local.intern()) {
            reportError(Messages.UNINTERNED_STRINGS.format(new Object[0]), canRecover);
        }
        else {
            reportError(Messages.UNEXPECTED_ELEMENT.format(ea.uri, ea.local, this.computeExpectedElements()), canRecover);
        }
    }
    
    public Collection<QName> getExpectedChildElements() {
        return (Collection<QName>)Collections.emptyList();
    }
    
    public Collection<QName> getExpectedAttributes() {
        return (Collection<QName>)Collections.emptyList();
    }
    
    public void text(final UnmarshallingContext.State state, CharSequence text) throws SAXException {
        text = text.toString().replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim();
        reportError(Messages.UNEXPECTED_TEXT.format(text), true);
    }
    
    public final boolean expectText() {
        return this.expectText;
    }
    
    public void leaveElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
    }
    
    private String computeExpectedElements() {
        final StringBuilder r = new StringBuilder();
        for (final QName n : this.getExpectedChildElements()) {
            if (r.length() != 0) {
                r.append(',');
            }
            r.append("<{").append(n.getNamespaceURI()).append('}').append(n.getLocalPart()).append('>');
        }
        if (r.length() == 0) {
            return "(none)";
        }
        return r.toString();
    }
    
    protected final void fireBeforeUnmarshal(final JaxBeanInfo beanInfo, final Object child, final UnmarshallingContext.State state) throws SAXException {
        if (beanInfo.lookForLifecycleMethods()) {
            final UnmarshallingContext context = state.getContext();
            final Unmarshaller.Listener listener = context.parent.getListener();
            if (beanInfo.hasBeforeUnmarshalMethod()) {
                beanInfo.invokeBeforeUnmarshalMethod(context.parent, child, state.getPrev().getTarget());
            }
            if (listener != null) {
                listener.beforeUnmarshal(child, state.getPrev().getTarget());
            }
        }
    }
    
    protected final void fireAfterUnmarshal(final JaxBeanInfo beanInfo, final Object child, final UnmarshallingContext.State state) throws SAXException {
        if (beanInfo.lookForLifecycleMethods()) {
            final UnmarshallingContext context = state.getContext();
            final Unmarshaller.Listener listener = context.parent.getListener();
            if (beanInfo.hasAfterUnmarshalMethod()) {
                beanInfo.invokeAfterUnmarshalMethod(context.parent, child, state.getTarget());
            }
            if (listener != null) {
                listener.afterUnmarshal(child, state.getTarget());
            }
        }
    }
    
    protected static void handleGenericException(final Exception e) throws SAXException {
        handleGenericException(e, false);
    }
    
    public static void handleGenericException(final Exception e, final boolean canRecover) throws SAXException {
        reportError(e.getMessage(), e, canRecover);
    }
    
    public static void handleGenericError(final Error e) throws SAXException {
        reportError(e.getMessage(), false);
    }
    
    protected static void reportError(final String msg, final boolean canRecover) throws SAXException {
        reportError(msg, null, canRecover);
    }
    
    public static void reportError(final String msg, final Exception nested, final boolean canRecover) throws SAXException {
        final UnmarshallingContext context = UnmarshallingContext.getInstance();
        context.handleEvent(new ValidationEventImpl(canRecover ? 1 : 2, msg, context.getLocator().getLocation(), nested), canRecover);
    }
    
    protected static void handleParseConversionException(final UnmarshallingContext.State state, final Exception e) throws SAXException {
        state.getContext().handleError(e);
    }
}
