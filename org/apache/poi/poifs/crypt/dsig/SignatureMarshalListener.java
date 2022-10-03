package org.apache.poi.poifs.crypt.dsig;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.EventListener;

public class SignatureMarshalListener implements EventListener, SignatureConfig.SignatureConfigurable
{
    ThreadLocal<EventTarget> target;
    SignatureConfig signatureConfig;
    
    public SignatureMarshalListener() {
        this.target = new ThreadLocal<EventTarget>();
    }
    
    public void setEventTarget(final EventTarget target) {
        this.target.set(target);
    }
    
    @Override
    public void handleEvent(final Event e) {
        if (!(e instanceof MutationEvent)) {
            return;
        }
        final MutationEvent mutEvt = (MutationEvent)e;
        final EventTarget et = mutEvt.getTarget();
        if (!(et instanceof Element)) {
            return;
        }
        this.handleElement((Element)et);
    }
    
    public void handleElement(final Element el) {
        final EventTarget target = this.target.get();
        if (el.hasAttribute("Id")) {
            el.setIdAttribute("Id", true);
        }
        setListener(target, this, false);
        if ("http://schemas.openxmlformats.org/package/2006/digital-signature".equals(el.getNamespaceURI())) {
            final String parentNS = el.getParentNode().getNamespaceURI();
            if (!"http://schemas.openxmlformats.org/package/2006/digital-signature".equals(parentNS) && !el.hasAttributeNS("http://www.w3.org/2000/xmlns/", "mdssi")) {
                el.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", "http://schemas.openxmlformats.org/package/2006/digital-signature");
            }
        }
        this.setPrefix(el);
        setListener(target, this, true);
    }
    
    public static void setListener(final EventTarget target, final EventListener listener, final boolean enabled) {
        final String type = "DOMSubtreeModified";
        final boolean useCapture = false;
        if (enabled) {
            target.addEventListener(type, listener, useCapture);
        }
        else {
            target.removeEventListener(type, listener, useCapture);
        }
    }
    
    protected void setPrefix(final Node el) {
        final String prefix = this.signatureConfig.getNamespacePrefixes().get(el.getNamespaceURI());
        if (prefix != null && el.getPrefix() == null) {
            el.setPrefix(prefix);
        }
        final NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            this.setPrefix(nl.item(i));
        }
    }
    
    @Override
    public void setSignatureConfig(final SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
}
