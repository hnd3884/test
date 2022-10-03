package org.apache.axiom.util.stax.debug;

import org.apache.commons.logging.LogFactory;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

public class XMLStreamReaderValidator extends XMLStreamReaderWrapper
{
    private static final Log log;
    private static boolean IS_ADV_DEBUG_ENABLED;
    private boolean throwExceptions;
    private Stack stack;
    
    public XMLStreamReaderValidator(final XMLStreamReader delegate, final boolean throwExceptions) {
        super(delegate);
        this.throwExceptions = false;
        this.stack = new Stack();
        this.throwExceptions = throwExceptions;
    }
    
    @Override
    public int next() throws XMLStreamException {
        final int event = super.next();
        this.trackEvent(event);
        return event;
    }
    
    @Override
    public String getElementText() throws XMLStreamException {
        final String text = super.getElementText();
        this.trackEvent(2);
        return text;
    }
    
    @Override
    public int nextTag() throws XMLStreamException {
        final int event = super.nextTag();
        this.trackEvent(event);
        return event;
    }
    
    private void trackEvent(final int event) throws XMLStreamException {
        this.logParserState();
        switch (event) {
            case 1: {
                this.stack.push(super.getName());
                break;
            }
            case 2: {
                final QName delegateQName = super.getName();
                if (this.stack.isEmpty()) {
                    this.reportError("An END_ELEMENT event for " + delegateQName + " was encountered, but the START_ELEMENT stack is empty.");
                    break;
                }
                final QName expectedQName = this.stack.pop();
                if (!expectedQName.equals(delegateQName)) {
                    this.reportError("An END_ELEMENT event for " + delegateQName + " was encountered, but this doesn't match the corresponding START_ELEMENT " + expectedQName + " event.");
                }
                break;
            }
            case 8: {
                if (!this.stack.isEmpty()) {
                    this.reportError("An unexpected END_DOCUMENT event was encountered; element stack: " + this.stack);
                    break;
                }
                break;
            }
        }
    }
    
    private void reportError(final String message) throws XMLStreamException {
        XMLStreamReaderValidator.log.debug((Object)message);
        if (this.throwExceptions) {
            throw new XMLStreamException(message);
        }
    }
    
    protected void logParserState() {
        if (XMLStreamReaderValidator.IS_ADV_DEBUG_ENABLED) {
            final int currentEvent = super.getEventType();
            switch (currentEvent) {
                case 1: {
                    XMLStreamReaderValidator.log.trace((Object)"START_ELEMENT: ");
                    XMLStreamReaderValidator.log.trace((Object)("  QName: " + super.getName()));
                    break;
                }
                case 7: {
                    XMLStreamReaderValidator.log.trace((Object)"START_DOCUMENT: ");
                    break;
                }
                case 4: {
                    XMLStreamReaderValidator.log.trace((Object)"CHARACTERS: ");
                    XMLStreamReaderValidator.log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 12: {
                    XMLStreamReaderValidator.log.trace((Object)"CDATA: ");
                    XMLStreamReaderValidator.log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 2: {
                    XMLStreamReaderValidator.log.trace((Object)"END_ELEMENT: ");
                    XMLStreamReaderValidator.log.trace((Object)("  QName: " + super.getName()));
                    break;
                }
                case 8: {
                    XMLStreamReaderValidator.log.trace((Object)"END_DOCUMENT: ");
                    break;
                }
                case 6: {
                    XMLStreamReaderValidator.log.trace((Object)"SPACE: ");
                    XMLStreamReaderValidator.log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 5: {
                    XMLStreamReaderValidator.log.trace((Object)"COMMENT: ");
                    XMLStreamReaderValidator.log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 11: {
                    XMLStreamReaderValidator.log.trace((Object)"DTD: ");
                    XMLStreamReaderValidator.log.trace((Object)("[" + super.getText() + "]"));
                    break;
                }
                case 3: {
                    XMLStreamReaderValidator.log.trace((Object)"PROCESSING_INSTRUCTION: ");
                    XMLStreamReaderValidator.log.trace((Object)("   [" + super.getPITarget() + "][" + super.getPIData() + "]"));
                    break;
                }
                case 9: {
                    XMLStreamReaderValidator.log.trace((Object)"ENTITY_REFERENCE: ");
                    XMLStreamReaderValidator.log.trace((Object)("    " + super.getLocalName() + "[" + super.getText() + "]"));
                    break;
                }
                default: {
                    XMLStreamReaderValidator.log.trace((Object)("UNKNOWN_STATE: " + currentEvent));
                    break;
                }
            }
        }
    }
    
    static {
        log = LogFactory.getLog((Class)XMLStreamReaderValidator.class);
        XMLStreamReaderValidator.IS_ADV_DEBUG_ENABLED = false;
    }
}
