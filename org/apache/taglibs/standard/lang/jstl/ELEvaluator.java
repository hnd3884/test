package org.apache.taglibs.standard.lang.jstl;

import org.apache.taglibs.standard.lang.jstl.parser.Token;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.io.Reader;
import org.apache.taglibs.standard.lang.jstl.parser.TokenMgrError;
import org.apache.taglibs.standard.lang.jstl.parser.ParseException;
import org.apache.taglibs.standard.lang.jstl.parser.ELParser;
import java.io.StringReader;
import javax.servlet.jsp.PageContext;
import java.util.Map;

public class ELEvaluator
{
    private static final String EXPR_CACHE_PARAM = "org.apache.taglibs.standard.lang.jstl.exprCacheSize";
    private static final int MAX_SIZE = 100;
    static Map sCachedExpressionStrings;
    static Map sCachedExpectedTypes;
    static Logger sLogger;
    VariableResolver mResolver;
    boolean mBypassCache;
    PageContext pageContext;
    
    public ELEvaluator(final VariableResolver pResolver) {
        this.mResolver = pResolver;
    }
    
    public void setBypassCache(final boolean pBypassCache) {
        this.mBypassCache = pBypassCache;
    }
    
    public Object evaluate(final String pExpressionString, final Object pContext, final Class pExpectedType, final Map functions, final String defaultPrefix) throws ELException {
        return this.evaluate(pExpressionString, pContext, pExpectedType, functions, defaultPrefix, ELEvaluator.sLogger);
    }
    
    Object evaluate(final String pExpressionString, final Object pContext, final Class pExpectedType, final Map functions, final String defaultPrefix, final Logger pLogger) throws ELException {
        if (pExpressionString == null) {
            throw new ELException(Constants.NULL_EXPRESSION_STRING);
        }
        this.pageContext = (PageContext)pContext;
        final Object parsedValue = this.parseExpressionString(pExpressionString);
        if (parsedValue instanceof String) {
            final String strValue = (String)parsedValue;
            return this.convertStaticValueToExpectedType(strValue, pExpectedType, pLogger);
        }
        if (parsedValue instanceof Expression) {
            final Object value = ((Expression)parsedValue).evaluate(pContext, this.mResolver, functions, defaultPrefix, pLogger);
            return this.convertToExpectedType(value, pExpectedType, pLogger);
        }
        if (parsedValue instanceof ExpressionString) {
            final String strValue = ((ExpressionString)parsedValue).evaluate(pContext, this.mResolver, functions, defaultPrefix, pLogger);
            return this.convertToExpectedType(strValue, pExpectedType, pLogger);
        }
        return null;
    }
    
    public Object parseExpressionString(final String pExpressionString) throws ELException {
        if (pExpressionString.length() == 0) {
            return "";
        }
        if (!this.mBypassCache && ELEvaluator.sCachedExpressionStrings == null) {
            this.createExpressionStringMap();
        }
        Object ret = this.mBypassCache ? null : ELEvaluator.sCachedExpressionStrings.get(pExpressionString);
        if (ret == null) {
            final Reader r = new StringReader(pExpressionString);
            final ELParser parser = new ELParser(r);
            try {
                ret = parser.ExpressionString();
                if (!this.mBypassCache) {
                    ELEvaluator.sCachedExpressionStrings.put(pExpressionString, ret);
                }
            }
            catch (final ParseException exc) {
                throw new ELException(formatParseException(pExpressionString, exc));
            }
            catch (final TokenMgrError exc2) {
                throw new ELException(exc2.getMessage());
            }
        }
        return ret;
    }
    
    Object convertToExpectedType(final Object pValue, final Class pExpectedType, final Logger pLogger) throws ELException {
        return Coercions.coerce(pValue, pExpectedType, pLogger);
    }
    
    Object convertStaticValueToExpectedType(final String pValue, final Class pExpectedType, final Logger pLogger) throws ELException {
        if (pExpectedType == String.class || pExpectedType == Object.class) {
            return pValue;
        }
        final Map valueByString = getOrCreateExpectedTypeMap(pExpectedType);
        if (!this.mBypassCache && valueByString.containsKey(pValue)) {
            return valueByString.get(pValue);
        }
        final Object ret = Coercions.coerce(pValue, pExpectedType, pLogger);
        valueByString.put(pValue, ret);
        return ret;
    }
    
    static Map getOrCreateExpectedTypeMap(final Class pExpectedType) {
        synchronized (ELEvaluator.sCachedExpectedTypes) {
            Map ret = ELEvaluator.sCachedExpectedTypes.get(pExpectedType);
            if (ret == null) {
                ret = Collections.synchronizedMap(new HashMap<Object, Object>());
                ELEvaluator.sCachedExpectedTypes.put(pExpectedType, ret);
            }
            return ret;
        }
    }
    
    private synchronized void createExpressionStringMap() {
        if (ELEvaluator.sCachedExpressionStrings != null) {
            return;
        }
        int maxSize;
        if (this.pageContext != null && this.pageContext.getServletContext() != null) {
            final String value = this.pageContext.getServletContext().getInitParameter("org.apache.taglibs.standard.lang.jstl.exprCacheSize");
            if (value != null) {
                maxSize = Integer.valueOf(value);
            }
            else {
                maxSize = 100;
            }
        }
        else {
            maxSize = 100;
        }
        ELEvaluator.sCachedExpressionStrings = Collections.synchronizedMap((Map<Object, Object>)new LinkedHashMap() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry eldest) {
                return this.size() > maxSize;
            }
        });
    }
    
    static String formatParseException(final String pExpressionString, final ParseException pExc) {
        final StringBuffer expectedBuf = new StringBuffer();
        int maxSize = 0;
        boolean printedOne = false;
        if (pExc.expectedTokenSequences == null) {
            return pExc.toString();
        }
        for (int i = 0; i < pExc.expectedTokenSequences.length; ++i) {
            if (maxSize < pExc.expectedTokenSequences[i].length) {
                maxSize = pExc.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < pExc.expectedTokenSequences[i].length; ++j) {
                if (printedOne) {
                    expectedBuf.append(", ");
                }
                expectedBuf.append(pExc.tokenImage[pExc.expectedTokenSequences[i][j]]);
                printedOne = true;
            }
        }
        final String expected = expectedBuf.toString();
        final StringBuffer encounteredBuf = new StringBuffer();
        Token tok = pExc.currentToken.next;
        for (int k = 0; k < maxSize; ++k) {
            if (k != 0) {
                encounteredBuf.append(" ");
            }
            if (tok.kind == 0) {
                encounteredBuf.append(pExc.tokenImage[0]);
                break;
            }
            encounteredBuf.append(addEscapes(tok.image));
            tok = tok.next;
        }
        final String encountered = encounteredBuf.toString();
        return MessageFormat.format(Constants.PARSE_EXCEPTION, expected, encountered);
    }
    
    static String addEscapes(final String str) {
        final StringBuffer retval = new StringBuffer();
        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '\0': {
                    break;
                }
                case '\b': {
                    retval.append("\\b");
                    break;
                }
                case '\t': {
                    retval.append("\\t");
                    break;
                }
                case '\n': {
                    retval.append("\\n");
                    break;
                }
                case '\f': {
                    retval.append("\\f");
                    break;
                }
                case '\r': {
                    retval.append("\\r");
                    break;
                }
                default: {
                    final char ch;
                    if ((ch = str.charAt(i)) < ' ' || ch > '~') {
                        final String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                        break;
                    }
                    retval.append(ch);
                    break;
                }
            }
        }
        return retval.toString();
    }
    
    public String parseAndRender(final String pExpressionString) throws ELException {
        final Object val = this.parseExpressionString(pExpressionString);
        if (val instanceof String) {
            return (String)val;
        }
        if (val instanceof Expression) {
            return "${" + ((Expression)val).getExpressionString() + "}";
        }
        if (val instanceof ExpressionString) {
            return ((ExpressionString)val).getExpressionString();
        }
        return "";
    }
    
    static {
        ELEvaluator.sCachedExpressionStrings = null;
        ELEvaluator.sCachedExpectedTypes = new HashMap();
        ELEvaluator.sLogger = new Logger(System.out);
    }
}
