package org.owasp.validator.css;

import org.w3c.dom.css.CSSStyleDeclaration;
import java.util.List;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import java.util.ArrayList;
import org.w3c.dom.css.CSSStyleSheet;
import java.util.regex.Matcher;
import java.io.IOException;
import org.owasp.validator.html.ScanException;
import java.util.logging.Level;
import org.w3c.dom.Node;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import org.owasp.validator.html.CleanResults;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.Parser;
import com.steadystate.css.parser.SACParserCSS3;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import com.steadystate.css.parser.CSSOMParser;
import org.owasp.validator.html.InternalPolicy;
import java.util.logging.Logger;

public class ZohoCssScanner
{
    private static final Logger LOGGER;
    protected final InternalPolicy policy;
    private static final String CDATA = "^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$";
    CSSOMParser parser;
    ZohoCssValidator cssValidator;
    boolean removeImportRule;
    private static final Pattern CDATA_PATTERN;
    
    public ZohoCssScanner(final InternalPolicy policy, final ResourceBundle messages) {
        this.removeImportRule = true;
        this.policy = policy;
        (this.parser = new CSSOMParser((Parser)new SACParserCSS3())).setErrorHandler((ErrorHandler)new CSSErrorHandler());
        this.cssValidator = new ZohoCssValidator(policy, messages);
    }
    
    public CleanResults scanStyleSheet(String taintedCss) throws ScanException {
        final long startOfScan = System.currentTimeMillis();
        final Matcher m = ZohoCssScanner.CDATA_PATTERN.matcher(taintedCss);
        final boolean isCdata = m.matches();
        if (isCdata) {
            taintedCss = m.group(1);
        }
        taintedCss = this.removeUnicodeSpace(taintedCss);
        final InputSource is = new InputSource((Reader)new StringReader(taintedCss));
        CleanResults cleanCSS;
        try {
            long startTime = System.currentTimeMillis();
            final CSSStyleSheet stylesheet = this.parser.parseStyleSheet(is, (Node)null, (String)null);
            long totalTime = System.currentTimeMillis() - startTime;
            if (totalTime > 5000L) {
                ZohoCssScanner.LOGGER.log(Level.INFO, "Style sheet scanning time in milli second: ", new Object[] { totalTime });
            }
            startTime = System.currentTimeMillis();
            cleanCSS = this.cssValidator.validateStyleSheet(stylesheet, isCdata, startOfScan);
            totalTime = System.currentTimeMillis() - startTime;
            if (totalTime > 5000L) {
                ZohoCssScanner.LOGGER.log(Level.INFO, "Style sheet validation time in milli second: ", new Object[] { totalTime });
            }
        }
        catch (final IOException e) {
            throw new ScanException(e);
        }
        return cleanCSS;
    }
    
    private String removeUnicodeSpace(final String str) {
        return str.replace(" ", "");
    }
    
    public CleanResults scanInlineStyle(String attrvalue) throws ScanException {
        final long startOfScan = System.currentTimeMillis();
        final List<String> errorMessages = new ArrayList<String>();
        attrvalue = this.removeUnicodeSpace(attrvalue.trim());
        final InputSource is = new InputSource((Reader)new StringReader(attrvalue));
        CleanResults cleanedCSSResult;
        try {
            long startTime = System.currentTimeMillis();
            final CSSStyleDeclaration propertiesList = this.parser.parseStyleDeclaration(is);
            long totalTime = System.currentTimeMillis() - startTime;
            if (totalTime > 5000L) {
                ZohoCssScanner.LOGGER.log(Level.INFO, "Inline style scanning time in milli second: ", new Object[] { totalTime });
            }
            final CSSStyleDeclarationImpl cssProperties = (CSSStyleDeclarationImpl)propertiesList;
            startTime = System.currentTimeMillis();
            cleanedCSSResult = this.cssValidator.validateInlineStyles(startOfScan, cssProperties, errorMessages);
            totalTime = System.currentTimeMillis() - startTime;
            if (totalTime > 5000L) {
                ZohoCssScanner.LOGGER.log(Level.INFO, "Inline style validation time in milli second: ", new Object[] { totalTime });
            }
        }
        catch (final IOException e) {
            throw new ScanException(e);
        }
        return cleanedCSSResult;
    }
    
    static {
        LOGGER = Logger.getLogger(ZohoCssScanner.class.getName());
        CDATA_PATTERN = Pattern.compile("^\\s*<!\\[CDATA\\[(.*)\\]\\]>\\s*$", 32);
    }
}
