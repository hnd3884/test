package org.apache.taglibs.standard.tlv;

import java.util.Set;
import org.apache.taglibs.standard.resources.Resources;
import org.xml.sax.Attributes;
import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;

public class JstlXmlTLV extends JstlBaseTLV
{
    private static final String CHOOSE = "choose";
    private static final String WHEN = "when";
    private static final String OTHERWISE = "otherwise";
    private static final String PARSE = "parse";
    private static final String PARAM = "param";
    private static final String TRANSFORM = "transform";
    private static final String JSP_TEXT = "jsp:text";
    private static final String VALUE = "value";
    private static final String SOURCE = "xml";
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        return super.validate(4, prefix, uri, page);
    }
    
    @Override
    protected DefaultHandler getHandler() {
        return new Handler();
    }
    
    private class Handler extends DefaultHandler
    {
        private int depth;
        private Stack chooseDepths;
        private Stack chooseHasOtherwise;
        private Stack chooseHasWhen;
        private String lastElementName;
        private boolean bodyNecessary;
        private boolean bodyIllegal;
        private Stack transformWithSource;
        
        private Handler() {
            this.depth = 0;
            this.chooseDepths = new Stack();
            this.chooseHasOtherwise = new Stack();
            this.chooseHasWhen = new Stack();
            this.lastElementName = null;
            this.bodyNecessary = false;
            this.bodyIllegal = false;
            this.transformWithSource = new Stack();
        }
        
        @Override
        public void startElement(final String ns, String ln, final String qn, final Attributes a) {
            if (ln == null) {
                ln = JstlXmlTLV.this.getLocalPart(qn);
            }
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyIllegal) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            final Set expAtts;
            if (qn.startsWith(JstlXmlTLV.this.prefix + ":") && (expAtts = JstlXmlTLV.this.config.get(ln)) != null) {
                for (int i = 0; i < a.getLength(); ++i) {
                    final String attName = a.getLocalName(i);
                    if (expAtts.contains(attName)) {
                        final String vMsg = JstlXmlTLV.this.validateExpression(ln, attName, a.getValue(i));
                        if (vMsg != null) {
                            JstlXmlTLV.this.fail(vMsg);
                        }
                    }
                }
            }
            if (qn.startsWith(JstlXmlTLV.this.prefix + ":") && !JstlXmlTLV.this.hasNoInvalidScope(a)) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_INVALID_ATTRIBUTE", "scope", qn, a.getValue("scope")));
            }
            if (qn.startsWith(JstlXmlTLV.this.prefix + ":") && JstlXmlTLV.this.hasEmptyVar(a)) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_EMPTY_VAR", qn));
            }
            if (qn.startsWith(JstlXmlTLV.this.prefix + ":") && JstlXmlTLV.this.hasDanglingScope(a)) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_DANGLING_SCOPE", qn));
            }
            if (this.chooseChild()) {
                if (JstlXmlTLV.this.isXmlTag(ns, ln, "when")) {
                    this.chooseHasWhen.pop();
                    this.chooseHasWhen.push(Boolean.TRUE);
                }
                if (!JstlXmlTLV.this.isXmlTag(ns, ln, "when") && !JstlXmlTLV.this.isXmlTag(ns, ln, "otherwise")) {
                    JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_CHILD_TAG", JstlXmlTLV.this.prefix, "choose", qn));
                }
                if (this.chooseHasOtherwise.peek()) {
                    JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_ORDER", qn, JstlXmlTLV.this.prefix, "otherwise", "choose"));
                }
                if (JstlXmlTLV.this.isXmlTag(ns, ln, "otherwise")) {
                    this.chooseHasOtherwise.pop();
                    this.chooseHasOtherwise.push(Boolean.TRUE);
                }
            }
            if (!this.transformWithSource.empty() && this.topDepth(this.transformWithSource) == this.depth - 1 && !JstlXmlTLV.this.isXmlTag(ns, ln, "param")) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", JstlXmlTLV.this.prefix + ":" + "transform"));
            }
            if (JstlXmlTLV.this.isXmlTag(ns, ln, "choose")) {
                this.chooseDepths.push(new Integer(this.depth));
                this.chooseHasWhen.push(Boolean.FALSE);
                this.chooseHasOtherwise.push(Boolean.FALSE);
            }
            this.bodyIllegal = false;
            this.bodyNecessary = false;
            if (JstlXmlTLV.this.isXmlTag(ns, ln, "parse")) {
                if (JstlXmlTLV.this.hasAttribute(a, "xml")) {
                    this.bodyIllegal = true;
                }
            }
            else if (JstlXmlTLV.this.isXmlTag(ns, ln, "param")) {
                if (JstlXmlTLV.this.hasAttribute(a, "value")) {
                    this.bodyIllegal = true;
                }
                else {
                    this.bodyNecessary = true;
                }
            }
            else if (JstlXmlTLV.this.isXmlTag(ns, ln, "transform") && JstlXmlTLV.this.hasAttribute(a, "xml")) {
                this.transformWithSource.push(new Integer(this.depth));
            }
            this.lastElementName = qn;
            JstlXmlTLV.this.lastElementId = a.getValue("http://java.sun.com/JSP/Page", "id");
            ++this.depth;
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) {
            this.bodyNecessary = false;
            final String s = new String(ch, start, length).trim();
            if (s.equals("")) {
                return;
            }
            if (this.bodyIllegal) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            if (this.chooseChild()) {
                final String msg = Resources.getMessage("TLV_ILLEGAL_TEXT_BODY", JstlXmlTLV.this.prefix, "choose", (s.length() < 7) ? s : s.substring(0, 7));
                JstlXmlTLV.this.fail(msg);
            }
            if (!this.transformWithSource.empty() && this.topDepth(this.transformWithSource) == this.depth - 1) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", JstlXmlTLV.this.prefix + ":" + "transform"));
            }
        }
        
        @Override
        public void endElement(final String ns, final String ln, final String qn) {
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyNecessary) {
                JstlXmlTLV.this.fail(Resources.getMessage("TLV_MISSING_BODY", this.lastElementName));
            }
            this.bodyIllegal = false;
            if (JstlXmlTLV.this.isXmlTag(ns, ln, "choose")) {
                final Boolean b = this.chooseHasWhen.pop();
                if (!b) {
                    JstlXmlTLV.this.fail(Resources.getMessage("TLV_PARENT_WITHOUT_SUBTAG", "choose", "when"));
                }
                this.chooseDepths.pop();
                this.chooseHasOtherwise.pop();
            }
            if (!this.transformWithSource.empty() && this.topDepth(this.transformWithSource) == this.depth - 1) {
                this.transformWithSource.pop();
            }
            --this.depth;
        }
        
        private boolean chooseChild() {
            return !this.chooseDepths.empty() && this.depth - 1 == this.chooseDepths.peek();
        }
        
        private int topDepth(final Stack s) {
            return s.peek();
        }
    }
}
