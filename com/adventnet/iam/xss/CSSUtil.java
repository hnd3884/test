package com.adventnet.iam.xss;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import com.steadystate.css.parser.HandlerBase;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.Node;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.Parser;
import com.steadystate.css.parser.SACParserCSS3;
import java.util.logging.Level;
import java.util.Properties;
import com.steadystate.css.parser.CSSOMParser;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class CSSUtil
{
    static final Logger logger;
    Pattern propNamePattern;
    Pattern propValuePattern;
    boolean removeImportRule;
    public static Pattern hyphenPrefixPattern;
    boolean handleHyphenPrefix;
    boolean removeCommentedCSS;
    protected boolean enableCssSyntaxStrict;
    CSSOMParser parser;
    boolean altered;
    public static final String ZOHOHYPHEN = "_zohohyphen_";
    
    public CSSUtil(final Properties props) {
        this.propNamePattern = null;
        this.propValuePattern = null;
        this.removeImportRule = true;
        this.handleHyphenPrefix = true;
        this.removeCommentedCSS = false;
        this.enableCssSyntaxStrict = false;
        this.parser = null;
        this.altered = false;
        if ("false".equals(props.getProperty("remove-import"))) {
            this.removeImportRule = false;
        }
        if ("false".equals(props.getProperty("handle-hyphen-prefix"))) {
            this.handleHyphenPrefix = false;
        }
        if ("true".equals(props.getProperty("remove-commented-css"))) {
            this.removeCommentedCSS = true;
        }
        if ("true".equals(props.getProperty("enable-csssyntax-strict"))) {
            this.enableCssSyntaxStrict = true;
        }
        final String propNameRegexp = props.getProperty("remove-css-property");
        if (propNameRegexp != null) {
            this.propNamePattern = Pattern.compile(propNameRegexp.toLowerCase());
        }
        final String propValueRegexp = props.getProperty("remove-css-value");
        if (propValueRegexp != null) {
            this.propValuePattern = Pattern.compile(propValueRegexp.toLowerCase());
        }
        final String hyphenPrefixPatternStr = props.getProperty("css-hyphen-prefix");
        if (hyphenPrefixPatternStr != null) {
            CSSUtil.hyphenPrefixPattern = Pattern.compile(hyphenPrefixPatternStr);
            CSSUtil.logger.log(Level.FINE, "hyphenPrefixPattern  {0} ", CSSUtil.hyphenPrefixPattern);
        }
        (this.parser = new CSSOMParser((Parser)new SACParserCSS3())).setErrorHandler((ErrorHandler)new CSSErrorHandler());
    }
    
    public String cleanStyleSheet(String cssString) {
        String originalString = cssString;
        cssString = cssString.trim();
        if (this.removeCommentedCSS) {
            final String value = this.removeCommentedCSS(cssString);
            if (!cssString.equals(value)) {
                cssString = value;
            }
        }
        boolean hypenPrefixPresent = false;
        if (this.handleHyphenPrefix && CSSUtil.hyphenPrefixPattern != null) {
            hypenPrefixPresent = CSSUtil.hyphenPrefixPattern.matcher(cssString).find();
            if (hypenPrefixPresent) {
                cssString = escapePropertyHyphenPrefix(cssString);
            }
        }
        final InputSource is = new InputSource((Reader)new StringReader(cssString));
        try {
            final CSSStyleSheet stylesheet = this.parser.parseStyleSheet(is, (Node)null, (String)null);
            final CSSRuleList rules = stylesheet.getCssRules();
            for (int i = 0; i < rules.getLength(); ++i) {
                final CSSRule rule = rules.item(i);
                CSSUtil.logger.log(Level.FINE, "cssRule  {0} ", rule);
                if (rule.getType() == 1) {
                    final CSSStyleRule styleRule = (CSSStyleRule)rule;
                    final CSSStyleDeclaration styleDec = styleRule.getStyle();
                    if (this.cleanStyleDeclaration(styleDec)) {
                        this.SET_ALTERED();
                    }
                }
                else if (this.removeImportRule && rule.getType() == 3) {
                    stylesheet.deleteRule(i);
                    this.SET_ALTERED();
                }
                else if (this.enableCssSyntaxStrict && rule.getType() == 0) {
                    stylesheet.deleteRule(i);
                    this.SET_ALTERED();
                }
            }
            if (this.altered) {
                String resultStr = "" + stylesheet;
                if (this.handleHyphenPrefix && hypenPrefixPresent) {
                    resultStr = revertPropertyHyphenPrefix(resultStr);
                }
                return resultStr;
            }
            return originalString;
        }
        catch (final Throwable e) {
            if (this.enableCssSyntaxStrict) {
                CSSUtil.logger.log(Level.SEVERE, "Invalid StyleSheet Parse Error: {0}", e.getMessage());
                this.SET_ALTERED();
                return null;
            }
            originalString = (this.removeCommentedCSS ? this.removeCommentedCSS(originalString) : originalString);
            CSSUtil.logger.log(Level.SEVERE, "Invalid StyleSheet Parse Error: {0} Returning Orig STR for NOW \n {1}", new Object[] { e.getMessage(), XSSUtil.getLogString(originalString) });
            return originalString;
        }
    }
    
    public String cleanStyleDeclaration(String cssString) {
        final String originalString = cssString;
        cssString = cssString.trim();
        cssString = this.removeCommentedCSS(cssString);
        boolean hypenPrefixPresent = false;
        if (this.handleHyphenPrefix && CSSUtil.hyphenPrefixPattern != null) {
            hypenPrefixPresent = CSSUtil.hyphenPrefixPattern.matcher(cssString).find();
            if (hypenPrefixPresent) {
                cssString = escapePropertyHyphenPrefix(cssString);
            }
        }
        final InputSource is = new InputSource((Reader)new StringReader(cssString));
        try {
            final CSSStyleDeclaration styleDec = this.parser.parseStyleDeclaration(is);
            if (this.cleanStyleDeclaration(styleDec)) {
                String resultStr = "" + styleDec;
                if (this.handleHyphenPrefix && hypenPrefixPresent) {
                    resultStr = revertPropertyHyphenPrefix(resultStr);
                }
                return resultStr;
            }
            return originalString;
        }
        catch (final Throwable e) {
            if (this.enableCssSyntaxStrict) {
                this.SET_ALTERED();
                CSSUtil.logger.log(Level.SEVERE, "Invalid StyleDec Parse Error : {0} ", e.getMessage());
                return null;
            }
            CSSUtil.logger.log(Level.SEVERE, "Invalid StyleDec Parse Error : {0} Returning Orig STR for NOW \n{1}", new Object[] { e.getMessage(), XSSUtil.getLogString(originalString) });
            return originalString;
        }
    }
    
    private boolean cleanStyleDeclaration(final CSSStyleDeclaration styleDec) {
        boolean removed = false;
        for (int m = 0; m < styleDec.getLength(); ++m) {
            final String propName = styleDec.item(m);
            CSSUtil.logger.log(Level.FINE, "propName {0}", propName);
            if (this.propNamePattern != null && this.propNamePattern.matcher(propName.toLowerCase()).find()) {
                CSSUtil.logger.log(Level.FINE, "Removing propName{0} ", propName);
                styleDec.removeProperty(propName);
                --m;
                this.SET_ALTERED();
                removed = true;
            }
            else {
                final CSSValue cssValue = styleDec.getPropertyCSSValue(propName);
                CSSUtil.logger.log(Level.FINE, "propName  {0} cssValue {1}", new Object[] { propName, cssValue });
                if (cssValue.getCssValueType() == 2) {
                    final CSSValueList csValList = (CSSValueList)cssValue;
                    for (int n = 0; n < csValList.getLength(); ++n) {
                        final CSSValue cssSubValue = csValList.item(n);
                        CSSUtil.logger.log(Level.FINE, "cssSubValue {0}", cssSubValue);
                        if (this.propValuePattern != null && this.propValuePattern.matcher(cssSubValue.getCssText().toLowerCase()).find()) {
                            CSSUtil.logger.log(Level.FINE, "Removing propName{0} cssSubValue{1} ", new Object[] { propName, cssSubValue });
                            styleDec.removeProperty(propName);
                            --m;
                            removed = true;
                            this.SET_ALTERED();
                            break;
                        }
                    }
                }
                else if (this.propValuePattern != null && this.propValuePattern.matcher(cssValue.getCssText().toLowerCase()).find()) {
                    CSSUtil.logger.log(Level.FINE, "Removing cssValue {0}", cssValue);
                    styleDec.removeProperty(propName);
                    removed = true;
                    this.SET_ALTERED();
                    --m;
                }
            }
        }
        return removed;
    }
    
    public static void main(final String[] args) throws Throwable {
        String buffer = "";
        boolean styleDec = false;
        CSSUtil cssUtil = null;
        for (int m = 0; m < args.length; ++m) {
            if (args[m].startsWith("-p")) {
                final Properties p = new Properties();
                p.load(new FileInputStream(args[m + 1]));
                cssUtil = new CSSUtil(p);
                ++m;
            }
            else if (args[m].startsWith("-f")) {
                if (args[m].equals("-fd")) {
                    styleDec = true;
                }
                final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[m + 1])));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer = buffer + line + "\n";
                }
                if (reader != null) {
                    reader.close();
                }
                ++m;
            }
            else if (buffer == null) {
                buffer = args[m];
            }
        }
        if (styleDec) {
            CSSUtil.logger.log(Level.INFO, "Filtered StyleDeclaration : {0}", cssUtil.cleanStyleDeclaration(buffer));
        }
        else {
            CSSUtil.logger.log(Level.INFO, "Filtered StyleSheet : {0}", cssUtil.cleanStyleSheet(buffer));
        }
    }
    
    void SET_ALTERED() {
        if (!this.altered) {
            this.altered = true;
        }
    }
    
    public static String escapePropertyHyphenPrefix(String cssString) {
        if (cssString != null) {
            CSSUtil.logger.log(Level.FINE, "INPUT  {0} ", XSSUtil.getLogString(cssString));
            cssString = cssString.replaceAll("-", "_zohohyphen_");
            CSSUtil.logger.log(Level.FINE, "OUTPUT  {0} ", XSSUtil.getLogString(cssString));
        }
        return cssString;
    }
    
    public static String revertPropertyHyphenPrefix(String resultStr) {
        if (resultStr != null && resultStr.contains("_zohohyphen_")) {
            CSSUtil.logger.log(Level.FINE, "INPUT   {0} ", XSSUtil.getLogString(resultStr));
            resultStr = resultStr.replaceAll("_zohohyphen_", "-");
            CSSUtil.logger.log(Level.FINE, "OUTPUT   {0} ", XSSUtil.getLogString(resultStr));
        }
        return resultStr;
    }
    
    public String removeCommentedCSS(String elemValue) {
        final String value = XSSUtil.removeComment(elemValue);
        if (value != null && !elemValue.trim().equals(value)) {
            this.SET_ALTERED();
            elemValue = value;
        }
        return elemValue;
    }
    
    static {
        logger = Logger.getLogger(CSSUtil.class.getName());
        CSSUtil.hyphenPrefixPattern = null;
    }
    
    public class CSSErrorHandler extends HandlerBase
    {
        public void warning(final CSSParseException exception) throws CSSException {
            CSSUtil.logger.log(Level.INFO, "CSS Syntax Warning at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ]");
        }
        
        public void error(final CSSParseException exception) throws CSSException {
            CSSUtil.logger.log(Level.SEVERE, "CSS Syntax Error at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ] ");
            if (!CSSUtil.this.enableCssSyntaxStrict) {
                throw exception;
            }
            CSSUtil.this.SET_ALTERED();
        }
        
        public void fatalError(final CSSParseException exception) throws CSSException {
            CSSUtil.logger.log(Level.SEVERE, "CSS Syntax Fatal Error at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ]");
            throw exception;
        }
    }
}
