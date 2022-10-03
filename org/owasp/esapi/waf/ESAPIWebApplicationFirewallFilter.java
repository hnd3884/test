package org.owasp.esapi.waf;

import org.owasp.esapi.waf.actions.Action;
import java.util.List;
import org.apache.commons.fileupload.FileUploadException;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletRequest;
import org.owasp.esapi.waf.actions.DefaultAction;
import org.owasp.esapi.waf.actions.RedirectAction;
import org.owasp.esapi.waf.actions.BlockAction;
import org.owasp.esapi.waf.rules.Rule;
import org.owasp.esapi.waf.internal.InterceptingHTTPServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import org.apache.log4j.xml.DOMConfigurator;
import javax.servlet.ServletException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.owasp.esapi.waf.configuration.ConfigurationParser;
import java.io.FileInputStream;
import java.io.File;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import javax.servlet.FilterConfig;
import org.owasp.esapi.waf.configuration.AppGuardianConfiguration;
import javax.servlet.Filter;

public class ESAPIWebApplicationFirewallFilter implements Filter
{
    private AppGuardianConfiguration appGuardConfig;
    private static final String CONFIGURATION_FILE_PARAM = "configuration";
    private static final String LOGGING_FILE_PARAM = "log_settings";
    private static final String POLLING_TIME_PARAM = "polling_time";
    private static final int DEFAULT_POLLING_TIME = 30000;
    private String configurationFilename;
    private long pollingTime;
    private long lastConfigReadTime;
    private FilterConfig fc;
    private final Logger logger;
    
    public ESAPIWebApplicationFirewallFilter() {
        this.configurationFilename = null;
        this.logger = ESAPI.getLogger(ESAPIWebApplicationFirewallFilter.class);
    }
    
    public void setConfiguration(final String policyFilePath, final String webRootDir) throws FileNotFoundException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(policyFilePath));
            this.appGuardConfig = ConfigurationParser.readConfigurationFile(inputStream, webRootDir);
            this.lastConfigReadTime = System.currentTimeMillis();
            this.configurationFilename = policyFilePath;
        }
        catch (final ConfigurationException e) {
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    public AppGuardianConfiguration getConfiguration() {
        return this.appGuardConfig;
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        this.fc = fc;
        this.logger.debug(Logger.EVENT_SUCCESS, ">> Initializing WAF");
        final String logSettingsFilename = fc.getInitParameter("log_settings");
        final String realLogSettingsFilename = fc.getServletContext().getRealPath(logSettingsFilename);
        if (realLogSettingsFilename == null || !new File(realLogSettingsFilename).exists()) {
            throw new ServletException("[ESAPI WAF] Could not find log file at resolved path: " + realLogSettingsFilename);
        }
        this.configurationFilename = fc.getInitParameter("configuration");
        this.configurationFilename = fc.getServletContext().getRealPath(this.configurationFilename);
        if (this.configurationFilename == null || !new File(this.configurationFilename).exists()) {
            throw new ServletException("[ESAPI WAF] Could not find configuration file at resolved path: " + this.configurationFilename);
        }
        final String sPollingTime = fc.getInitParameter("polling_time");
        if (sPollingTime != null) {
            this.pollingTime = Long.parseLong(sPollingTime);
        }
        else {
            this.pollingTime = 30000L;
        }
        FileInputStream inputStream = null;
        try {
            final String webRootDir = fc.getServletContext().getRealPath("/");
            inputStream = new FileInputStream(this.configurationFilename);
            this.appGuardConfig = ConfigurationParser.readConfigurationFile(inputStream, webRootDir);
            DOMConfigurator.configure(realLogSettingsFilename);
            this.lastConfigReadTime = System.currentTimeMillis();
        }
        catch (final FileNotFoundException e) {
            throw new ServletException((Throwable)e);
        }
        catch (final ConfigurationException e2) {
            throw new ServletException((Throwable)e2);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException {
        if (System.currentTimeMillis() - this.lastConfigReadTime > this.pollingTime) {
            final File f = new File(this.configurationFilename);
            final long lastModified = f.lastModified();
            if (lastModified > this.lastConfigReadTime) {
                this.logger.debug(Logger.EVENT_SUCCESS, ">> Re-reading WAF policy");
                this.init(this.fc);
            }
        }
        this.logger.debug(Logger.EVENT_SUCCESS, ">>In WAF doFilter");
        final HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
        final HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;
        InterceptingHTTPServletRequest request = null;
        InterceptingHTTPServletResponse response = null;
        if (this.appGuardConfig.getCookieRules().size() + this.appGuardConfig.getBeforeResponseRules().size() > 0) {
            response = new InterceptingHTTPServletResponse(httpResponse, true, this.appGuardConfig.getCookieRules());
        }
        this.logger.debug(Logger.EVENT_SUCCESS, ">> Starting stage 1");
        List<Rule> rules = this.appGuardConfig.getBeforeBodyRules();
        for (int i = 0; i < rules.size(); ++i) {
            final Rule rule = rules.get(i);
            this.logger.debug(Logger.EVENT_SUCCESS, "  Applying BEFORE rule:  " + rule.getClass().getName());
            final Action action = rule.check(httpRequest, response, httpResponse);
            if (action.isActionNecessary()) {
                if (action instanceof BlockAction) {
                    if (response != null) {
                        response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    else {
                        httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    return;
                }
                if (action instanceof RedirectAction) {
                    this.sendRedirect(response, httpResponse, ((RedirectAction)action).getRedirectURL());
                    return;
                }
                if (action instanceof DefaultAction) {
                    switch (AppGuardianConfiguration.DEFAULT_FAIL_ACTION) {
                        case 2: {
                            if (response != null) {
                                response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            else {
                                httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            return;
                        }
                        case 1: {
                            this.sendRedirect(response, httpResponse);
                            return;
                        }
                    }
                }
            }
        }
        try {
            request = new InterceptingHTTPServletRequest((HttpServletRequest)servletRequest);
        }
        catch (final FileUploadException fue) {
            this.logger.error(Logger.EVENT_SUCCESS, "Error Wrapping Request", (Throwable)fue);
        }
        this.logger.debug(Logger.EVENT_SUCCESS, ">> Starting Stage 2");
        rules = this.appGuardConfig.getAfterBodyRules();
        for (int i = 0; i < rules.size(); ++i) {
            final Rule rule = rules.get(i);
            this.logger.debug(Logger.EVENT_SUCCESS, "  Applying BEFORE CHAIN rule:  " + rule.getClass().getName());
            final Action action = rule.check((HttpServletRequest)request, response, httpResponse);
            if (action.isActionNecessary()) {
                if (action instanceof BlockAction) {
                    if (response != null) {
                        response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    else {
                        httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    return;
                }
                if (action instanceof RedirectAction) {
                    this.sendRedirect(response, httpResponse, ((RedirectAction)action).getRedirectURL());
                    return;
                }
                if (action instanceof DefaultAction) {
                    switch (AppGuardianConfiguration.DEFAULT_FAIL_ACTION) {
                        case 2: {
                            if (response != null) {
                                response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            else {
                                httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            return;
                        }
                        case 1: {
                            this.sendRedirect(response, httpResponse);
                            return;
                        }
                    }
                }
            }
        }
        this.logger.debug(Logger.EVENT_SUCCESS, ">> Calling the FilterChain: " + chain);
        chain.doFilter((ServletRequest)request, (ServletResponse)((response != null) ? response : httpResponse));
        this.logger.debug(Logger.EVENT_SUCCESS, ">> Starting Stage 3");
        rules = this.appGuardConfig.getBeforeResponseRules();
        for (int i = 0; i < rules.size(); ++i) {
            final Rule rule = rules.get(i);
            this.logger.debug(Logger.EVENT_SUCCESS, "  Applying AFTER CHAIN rule:  " + rule.getClass().getName());
            final Action action = rule.check((HttpServletRequest)request, response, httpResponse);
            if (action.isActionNecessary()) {
                if (action instanceof BlockAction) {
                    if (response != null) {
                        response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    else {
                        httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                    }
                    return;
                }
                if (action instanceof RedirectAction) {
                    this.sendRedirect(response, httpResponse, ((RedirectAction)action).getRedirectURL());
                    return;
                }
                if (action instanceof DefaultAction) {
                    switch (AppGuardianConfiguration.DEFAULT_FAIL_ACTION) {
                        case 2: {
                            if (response != null) {
                                response.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            else {
                                httpResponse.setStatus(this.appGuardConfig.getDefaultResponseCode());
                            }
                            return;
                        }
                        case 1: {
                            this.sendRedirect(response, httpResponse);
                            return;
                        }
                    }
                }
            }
        }
        if (response != null) {
            this.logger.debug(Logger.EVENT_SUCCESS, ">>> committing reponse");
            response.commit();
        }
    }
    
    private void sendRedirect(final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse, final String redirectURL) throws IOException {
        if (response != null) {
            response.reset();
            response.resetBuffer();
            response.sendRedirect(redirectURL);
            response.commit();
        }
        else {
            httpResponse.sendRedirect(redirectURL);
        }
    }
    
    public void destroy() {
    }
    
    private void sendRedirect(final InterceptingHTTPServletResponse response, final HttpServletResponse httpResponse) throws IOException {
        if (response != null) {
            response.reset();
            response.resetBuffer();
            response.sendRedirect(this.appGuardConfig.getDefaultErrorPage());
        }
        else if (!httpResponse.isCommitted()) {
            httpResponse.sendRedirect(this.appGuardConfig.getDefaultErrorPage());
        }
    }
}
