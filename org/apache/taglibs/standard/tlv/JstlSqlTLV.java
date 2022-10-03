package org.apache.taglibs.standard.tlv;

import java.util.Set;
import org.apache.taglibs.standard.resources.Resources;
import org.xml.sax.Attributes;
import java.util.Stack;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;

public class JstlSqlTLV extends JstlBaseTLV
{
    private static final String SETDATASOURCE = "setDataSource";
    private static final String QUERY = "query";
    private static final String UPDATE = "update";
    private static final String TRANSACTION = "transaction";
    private static final String PARAM = "param";
    private static final String DATEPARAM = "dateParam";
    private static final String JSP_TEXT = "jsp:text";
    private static final String SQL = "sql";
    private static final String DATASOURCE = "dataSource";
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        return super.validate(3, prefix, uri, page);
    }
    
    @Override
    protected DefaultHandler getHandler() {
        return new Handler();
    }
    
    private class Handler extends DefaultHandler
    {
        private int depth;
        private Stack queryDepths;
        private Stack updateDepths;
        private Stack transactionDepths;
        private String lastElementName;
        private boolean bodyNecessary;
        private boolean bodyIllegal;
        
        private Handler() {
            this.depth = 0;
            this.queryDepths = new Stack();
            this.updateDepths = new Stack();
            this.transactionDepths = new Stack();
            this.lastElementName = null;
            this.bodyNecessary = false;
            this.bodyIllegal = false;
        }
        
        @Override
        public void startElement(final String ns, String ln, final String qn, final Attributes a) {
            if (ln == null) {
                ln = JstlSqlTLV.this.getLocalPart(qn);
            }
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyIllegal) {
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
            final Set expAtts;
            if (qn.startsWith(JstlSqlTLV.this.prefix + ":") && (expAtts = JstlSqlTLV.this.config.get(ln)) != null) {
                for (int i = 0; i < a.getLength(); ++i) {
                    final String attName = a.getLocalName(i);
                    if (expAtts.contains(attName)) {
                        final String vMsg = JstlSqlTLV.this.validateExpression(ln, attName, a.getValue(i));
                        if (vMsg != null) {
                            JstlSqlTLV.this.fail(vMsg);
                        }
                    }
                }
            }
            if (qn.startsWith(JstlSqlTLV.this.prefix + ":") && !JstlSqlTLV.this.hasNoInvalidScope(a)) {
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_INVALID_ATTRIBUTE", "scope", qn, a.getValue("scope")));
            }
            if (qn.startsWith(JstlSqlTLV.this.prefix + ":") && JstlSqlTLV.this.hasEmptyVar(a)) {
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_EMPTY_VAR", qn));
            }
            if (qn.startsWith(JstlSqlTLV.this.prefix + ":") && JstlSqlTLV.this.hasDanglingScope(a) && !qn.startsWith(JstlSqlTLV.this.prefix + ":" + "setDataSource")) {
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_DANGLING_SCOPE", qn));
            }
            if ((JstlSqlTLV.this.isSqlTag(ns, ln, "param") || JstlSqlTLV.this.isSqlTag(ns, ln, "dateParam")) && this.queryDepths.empty() && this.updateDepths.empty()) {
                JstlSqlTLV.this.fail(Resources.getMessage("SQL_PARAM_OUTSIDE_PARENT"));
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "query")) {
                this.queryDepths.push(new Integer(this.depth));
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "update")) {
                this.updateDepths.push(new Integer(this.depth));
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "transaction")) {
                this.transactionDepths.push(new Integer(this.depth));
            }
            this.bodyIllegal = false;
            this.bodyNecessary = false;
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "query") || JstlSqlTLV.this.isSqlTag(ns, ln, "update")) {
                if (!JstlSqlTLV.this.hasAttribute(a, "sql")) {
                    this.bodyNecessary = true;
                }
                if (JstlSqlTLV.this.hasAttribute(a, "dataSource") && !this.transactionDepths.empty()) {
                    JstlSqlTLV.this.fail(Resources.getMessage("ERROR_NESTED_DATASOURCE"));
                }
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "dateParam")) {
                this.bodyIllegal = true;
            }
            this.lastElementName = qn;
            JstlSqlTLV.this.lastElementId = a.getValue("http://java.sun.com/JSP/Page", "id");
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
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_ILLEGAL_BODY", this.lastElementName));
            }
        }
        
        @Override
        public void endElement(final String ns, final String ln, final String qn) {
            if (qn.equals("jsp:text")) {
                return;
            }
            if (this.bodyNecessary) {
                JstlSqlTLV.this.fail(Resources.getMessage("TLV_MISSING_BODY", this.lastElementName));
            }
            this.bodyIllegal = false;
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "query")) {
                this.queryDepths.pop();
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "update")) {
                this.updateDepths.pop();
            }
            if (JstlSqlTLV.this.isSqlTag(ns, ln, "transaction")) {
                this.transactionDepths.pop();
            }
            --this.depth;
        }
    }
}
