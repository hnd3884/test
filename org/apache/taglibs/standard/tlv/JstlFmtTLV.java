package org.apache.taglibs.standard.tlv;

import java.util.Set;
import org.apache.taglibs.standard.resources.Resources;
import org.xml.sax.Attributes;
import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;

public class JstlFmtTLV extends JstlBaseTLV
{
    private static final String SETLOCALE = "setLocale";
    private static final String SETBUNDLE = "setBundle";
    private static final String SETTIMEZONE = "setTimeZone";
    private static final String BUNDLE = "bundle";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_PARAM = "param";
    private static final String FORMAT_NUMBER = "formatNumber";
    private static final String PARSE_NUMBER = "parseNumber";
    private static final String PARSE_DATE = "parseDate";
    private static final String JSP_TEXT = "jsp:text";
    private static final String EVAL = "evaluator";
    private static final String MESSAGE_KEY = "key";
    private static final String BUNDLE_PREFIX = "prefix";
    private static final String VALUE = "value";
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        return super.validate(2, prefix, uri, page);
    }
    
    @Override
    protected DefaultHandler getHandler() {
        return new Handler();
    }
    
    private class Handler extends DefaultHandler
    {
        private int depth;
        private Stack messageDepths;
        private String lastElementName;
        private boolean bodyNecessary;
        private boolean bodyIllegal;
        
        private Handler() {
            this.depth = 0;
            this.messageDepths = new Stack();
            this.lastElementName = null;
            this.bodyNecessary = false;
            this.bodyIllegal = false;
        }
        
        @Override
        public void startElement(final String ns, String ln, final String qn, final Attributes a) {
            if (ln == null) {
                ln = JstlFmtTLV.this.getLocalPart(qn);
            }
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyIllegal) {
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            final Set expAtts;
            if (qn.startsWith(JstlFmtTLV.this.prefix + ":") && (expAtts = JstlFmtTLV.this.config.get(ln)) != null) {
                for (int i = 0; i < a.getLength(); ++i) {
                    final String attName = a.getLocalName(i);
                    if (expAtts.contains(attName)) {
                        final String vMsg = JstlFmtTLV.this.validateExpression(ln, attName, a.getValue(i));
                        if (vMsg != null) {
                            JstlFmtTLV.this.fail(vMsg);
                        }
                    }
                }
            }
            if (qn.startsWith(JstlFmtTLV.this.prefix + ":") && !JstlFmtTLV.this.hasNoInvalidScope(a)) {
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_INVALID_ATTRIBUTE", "scope", qn, a.getValue("scope")));
            }
            if (qn.startsWith(JstlFmtTLV.this.prefix + ":") && JstlFmtTLV.this.hasEmptyVar(a)) {
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_EMPTY_VAR", qn));
            }
            if (qn.startsWith(JstlFmtTLV.this.prefix + ":") && !JstlFmtTLV.this.isFmtTag(ns, ln, "setLocale") && !JstlFmtTLV.this.isFmtTag(ns, ln, "setBundle") && !JstlFmtTLV.this.isFmtTag(ns, ln, "setTimeZone") && JstlFmtTLV.this.hasDanglingScope(a)) {
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_DANGLING_SCOPE", qn));
            }
            if (JstlFmtTLV.this.isFmtTag(ns, ln, "param") && this.messageDepths.empty()) {
                JstlFmtTLV.this.fail(Resources.getMessage("PARAM_OUTSIDE_MESSAGE"));
            }
            if (JstlFmtTLV.this.isFmtTag(ns, ln, "message")) {
                this.messageDepths.push(new Integer(this.depth));
            }
            this.bodyIllegal = false;
            this.bodyNecessary = false;
            if (JstlFmtTLV.this.isFmtTag(ns, ln, "param") || JstlFmtTLV.this.isFmtTag(ns, ln, "formatNumber") || JstlFmtTLV.this.isFmtTag(ns, ln, "parseNumber") || JstlFmtTLV.this.isFmtTag(ns, ln, "parseDate")) {
                if (JstlFmtTLV.this.hasAttribute(a, "value")) {
                    this.bodyIllegal = true;
                }
                else {
                    this.bodyNecessary = true;
                }
            }
            else if (JstlFmtTLV.this.isFmtTag(ns, ln, "message") && !JstlFmtTLV.this.hasAttribute(a, "key")) {
                this.bodyNecessary = true;
            }
            else if (JstlFmtTLV.this.isFmtTag(ns, ln, "bundle") && JstlFmtTLV.this.hasAttribute(a, "prefix")) {
                this.bodyNecessary = true;
            }
            this.lastElementName = qn;
            JstlFmtTLV.this.lastElementId = a.getValue("http://java.sun.com/JSP/Page", "id");
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
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
        }
        
        @Override
        public void endElement(final String ns, final String ln, final String qn) {
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyNecessary) {
                JstlFmtTLV.this.fail(Resources.getMessage("TLV_MISSING_BODY", this.lastElementName));
            }
            this.bodyIllegal = false;
            if (JstlFmtTLV.this.isFmtTag(ns, ln, "message")) {
                this.messageDepths.pop();
            }
            --this.depth;
        }
    }
}
