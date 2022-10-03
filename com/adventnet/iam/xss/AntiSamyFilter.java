package com.adventnet.iam.xss;

import org.owasp.validator.html.CleanResults;
import java.util.ResourceBundle;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.scan.AbstractAntiSamyScanner;
import java.io.File;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Properties;
import org.owasp.validator.css.ZohoCssScanner;
import org.owasp.validator.html.AntiSamy;
import java.util.logging.Logger;
import org.owasp.validator.html.Policy;

public class AntiSamyFilter extends XSSFilter
{
    private static String policy_file;
    Policy policy;
    Logger currentlogger;
    AntiSamy as;
    int parserMode;
    ZohoCssScanner cssScanner;
    boolean filterCSS;
    boolean filterCSSProperties;
    
    public AntiSamyFilter() {
        this.currentlogger = Logger.getLogger(AntiSamyFilter.class.getName());
        this.parserMode = 1;
        this.filterCSS = false;
        this.filterCSSProperties = false;
    }
    
    @Override
    void init(final Properties props, final XSSFilterConfiguration xssFilterConfig) {
        if (props.containsKey("whitelist-policy")) {
            AntiSamyFilter.policy_file = SecurityUtil.getSecurityConfigurationDir() + File.separator + props.getProperty("whitelist-policy");
        }
        try {
            this.policy = ((AntiSamyFilter.policy_file != null) ? Policy.getInstance(AntiSamyFilter.policy_file) : null);
            final String filter_type = props.getProperty("xss-filter-type");
            if (filter_type.equalsIgnoreCase("ANTISAMY_CSS") || filter_type.equalsIgnoreCase("ANTISAMY_CSSPROPERTIES")) {
                if (filter_type.equalsIgnoreCase("ANTISAMY_CSS")) {
                    this.filterCSS = true;
                }
                else {
                    this.filterCSSProperties = true;
                }
                final ResourceBundle messages = AbstractAntiSamyScanner.getResourceBundle();
                this.cssScanner = new ZohoCssScanner((InternalPolicy)this.policy, messages);
            }
            else {
                if (props.containsKey("parser-mode")) {
                    this.parserMode = ("DOM".equalsIgnoreCase(props.getProperty("parser-mode")) ? 0 : 1);
                }
                this.as = new AntiSamy();
            }
        }
        catch (final Exception e) {
            this.currentlogger.log(Level.WARNING, "Exception in loading  AntiSamyFilter policy configuration - {0}", e.getMessage());
            throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
        }
    }
    
    @Override
    public String filterXSS(final String domain, final String value, final String encoding) {
        if (this.policy == null) {
            this.currentlogger.log(Level.WARNING, "Unable to load  AntiSamyFilter policy configuration");
            throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
        }
        try {
            CleanResults cr;
            if (this.filterCSS) {
                cr = this.cssScanner.scanStyleSheet(value);
            }
            else if (this.filterCSSProperties) {
                cr = this.cssScanner.scanInlineStyle(value);
            }
            else {
                cr = this.as.scan(value, this.policy, this.parserMode);
            }
            this.SET_ALTERED();
            return cr.getCleanHTML();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "AntiSamy SCAN Error : {0}", e.getMessage());
            this.logger.log(Level.FINE, "AntiSamy SCAN Error", e);
            final String exception = (this.filterCSS || this.filterCSSProperties) ? "CSS_PARSE_FAILED" : "HTML_PARSE_FAILED";
            throw new IAMSecurityException(exception);
        }
    }
    
    @Override
    public String balanceHTMLContent(final String content, final String encoding) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
