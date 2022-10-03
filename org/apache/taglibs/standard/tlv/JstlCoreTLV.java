package org.apache.taglibs.standard.tlv;

import java.util.Set;
import org.apache.taglibs.standard.resources.Resources;
import org.xml.sax.Attributes;
import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;

public class JstlCoreTLV extends JstlBaseTLV
{
    private static final String CHOOSE = "choose";
    private static final String WHEN = "when";
    private static final String OTHERWISE = "otherwise";
    private static final String EXPR = "out";
    private static final String SET = "set";
    private static final String IMPORT = "import";
    private static final String URL = "url";
    private static final String REDIRECT = "redirect";
    private static final String PARAM = "param";
    private static final String TEXT = "text";
    private static final String VALUE = "value";
    private static final String DEFAULT = "default";
    private static final String VAR_READER = "varReader";
    private static final String IMPORT_WITH_READER = "import varReader=''";
    private static final String IMPORT_WITHOUT_READER = "import var=''";
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        return super.validate(1, prefix, uri, page);
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
        private Stack urlTags;
        private String lastElementName;
        private boolean bodyNecessary;
        private boolean bodyIllegal;
        
        private Handler() {
            this.depth = 0;
            this.chooseDepths = new Stack();
            this.chooseHasOtherwise = new Stack();
            this.chooseHasWhen = new Stack();
            this.urlTags = new Stack();
            this.lastElementName = null;
            this.bodyNecessary = false;
            this.bodyIllegal = false;
        }
        
        @Override
        public void startElement(final String ns, String ln, final String qn, final Attributes a) {
            if (ln == null) {
                ln = JstlCoreTLV.this.getLocalPart(qn);
            }
            if (JstlCoreTLV.this.isJspTag(ns, ln, "text")) {
                return;
            }
            if (this.bodyIllegal) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            final Set expAtts;
            if (qn.startsWith(JstlCoreTLV.this.prefix + ":") && (expAtts = JstlCoreTLV.this.config.get(ln)) != null) {
                for (int i = 0; i < a.getLength(); ++i) {
                    final String attName = a.getLocalName(i);
                    if (expAtts.contains(attName)) {
                        final String vMsg = JstlCoreTLV.this.validateExpression(ln, attName, a.getValue(i));
                        if (vMsg != null) {
                            JstlCoreTLV.this.fail(vMsg);
                        }
                    }
                }
            }
            if (qn.startsWith(JstlCoreTLV.this.prefix + ":") && !JstlCoreTLV.this.hasNoInvalidScope(a)) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_INVALID_ATTRIBUTE", "scope", qn, a.getValue("scope")));
            }
            if (qn.startsWith(JstlCoreTLV.this.prefix + ":") && JstlCoreTLV.this.hasEmptyVar(a)) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_EMPTY_VAR", qn));
            }
            if (qn.startsWith(JstlCoreTLV.this.prefix + ":") && JstlCoreTLV.this.hasDanglingScope(a)) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_DANGLING_SCOPE", qn));
            }
            if (this.chooseChild()) {
                if (JstlCoreTLV.this.isCoreTag(ns, ln, "when")) {
                    this.chooseHasWhen.pop();
                    this.chooseHasWhen.push(Boolean.TRUE);
                }
                if (!JstlCoreTLV.this.isCoreTag(ns, ln, "when") && !JstlCoreTLV.this.isCoreTag(ns, ln, "otherwise")) {
                    JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_CHILD_TAG", JstlCoreTLV.this.prefix, "choose", qn));
                }
                if (this.chooseHasOtherwise.peek()) {
                    JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_ORDER", qn, JstlCoreTLV.this.prefix, "otherwise", "choose"));
                }
                if (JstlCoreTLV.this.isCoreTag(ns, ln, "otherwise")) {
                    this.chooseHasOtherwise.pop();
                    this.chooseHasOtherwise.push(Boolean.TRUE);
                }
            }
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "param")) {
                if (this.urlTags.empty() || this.urlTags.peek().equals("param")) {
                    JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_ORPHAN", "param"));
                }
                if (!this.urlTags.empty() && this.urlTags.peek().equals("import varReader=''")) {
                    JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_PARAM", JstlCoreTLV.this.prefix, "param", "import", "varReader"));
                }
            }
            else if (!this.urlTags.empty() && this.urlTags.peek().equals("import var=''")) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_CHILD_TAG", JstlCoreTLV.this.prefix, "import", qn));
            }
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "choose")) {
                this.chooseDepths.push(new Integer(this.depth));
                this.chooseHasWhen.push(Boolean.FALSE);
                this.chooseHasOtherwise.push(Boolean.FALSE);
            }
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "import")) {
                if (JstlCoreTLV.this.hasAttribute(a, "varReader")) {
                    this.urlTags.push("import varReader=''");
                }
                else {
                    this.urlTags.push("import var=''");
                }
            }
            else if (JstlCoreTLV.this.isCoreTag(ns, ln, "param")) {
                this.urlTags.push("param");
            }
            else if (JstlCoreTLV.this.isCoreTag(ns, ln, "redirect")) {
                this.urlTags.push("redirect");
            }
            else if (JstlCoreTLV.this.isCoreTag(ns, ln, "url")) {
                this.urlTags.push("url");
            }
            this.bodyIllegal = false;
            this.bodyNecessary = false;
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "out")) {
                if (JstlCoreTLV.this.hasAttribute(a, "default")) {
                    this.bodyIllegal = true;
                }
            }
            else if (JstlCoreTLV.this.isCoreTag(ns, ln, "set") && JstlCoreTLV.this.hasAttribute(a, "value")) {
                this.bodyIllegal = true;
            }
            this.lastElementName = qn;
            JstlCoreTLV.this.lastElementId = a.getValue("http://java.sun.com/JSP/Page", "id");
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
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            if (!this.urlTags.empty() && this.urlTags.peek().equals("import var=''")) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", JstlCoreTLV.this.prefix + ":" + "import"));
            }
            if (this.chooseChild()) {
                final String msg = Resources.getMessage("TLV_ILLEGAL_TEXT_BODY", JstlCoreTLV.this.prefix, "choose", (s.length() < 7) ? s : s.substring(0, 7));
                JstlCoreTLV.this.fail(msg);
            }
        }
        
        @Override
        public void endElement(final String ns, final String ln, final String qn) {
            if (JstlCoreTLV.this.isJspTag(ns, ln, "text")) {
                return;
            }
            if (this.bodyNecessary) {
                JstlCoreTLV.this.fail(Resources.getMessage("TLV_MISSING_BODY", this.lastElementName));
            }
            this.bodyIllegal = false;
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "choose")) {
                final Boolean b = this.chooseHasWhen.pop();
                if (!b) {
                    JstlCoreTLV.this.fail(Resources.getMessage("TLV_PARENT_WITHOUT_SUBTAG", "choose", "when"));
                }
                this.chooseDepths.pop();
                this.chooseHasOtherwise.pop();
            }
            if (JstlCoreTLV.this.isCoreTag(ns, ln, "import") || JstlCoreTLV.this.isCoreTag(ns, ln, "param") || JstlCoreTLV.this.isCoreTag(ns, ln, "redirect") || JstlCoreTLV.this.isCoreTag(ns, ln, "url")) {
                this.urlTags.pop();
            }
            --this.depth;
        }
        
        private boolean chooseChild() {
            return !this.chooseDepths.empty() && this.depth - 1 == this.chooseDepths.peek();
        }
    }
}
