package org.owasp.esapi.waf.configuration;

import org.owasp.esapi.ESAPI;
import nu.xom.Elements;
import nu.xom.Element;
import nu.xom.Document;
import java.io.IOException;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import bsh.EvalError;
import java.io.FileNotFoundException;
import org.owasp.esapi.waf.rules.BeanShellRule;
import org.owasp.esapi.waf.rules.DetectOutboundContentRule;
import org.owasp.esapi.waf.rules.ReplaceContentRule;
import org.owasp.esapi.waf.rules.AddSecureFlagRule;
import java.util.List;
import org.owasp.esapi.waf.rules.AddHTTPOnlyFlagRule;
import java.util.ArrayList;
import org.owasp.esapi.waf.rules.AddHeaderRule;
import org.owasp.esapi.waf.rules.SimpleVirtualPatchRule;
import org.owasp.esapi.waf.rules.RestrictUserAgentRule;
import org.owasp.esapi.waf.rules.RestrictContentTypeRule;
import org.owasp.esapi.waf.rules.EnforceHTTPSRule;
import org.owasp.esapi.waf.rules.HTTPMethodRule;
import org.owasp.esapi.waf.rules.PathExtensionRule;
import org.owasp.esapi.waf.rules.MustMatchRule;
import org.owasp.esapi.waf.rules.IPRule;
import org.owasp.esapi.waf.rules.Rule;
import org.owasp.esapi.waf.rules.AuthenticatedRule;
import org.apache.log4j.Level;
import org.owasp.esapi.waf.ConfigurationException;
import java.util.regex.Pattern;
import nu.xom.Builder;
import java.io.InputStream;

public class ConfigurationParser
{
    private static final String REGEX = "regex";
    private static final String DEFAULT_PATH_APPLY_ALL = ".*";
    private static final int DEFAULT_RESPONSE_CODE = 403;
    private static final String DEFAULT_SESSION_COOKIE;
    private static final String[] STAGES;
    
    public static AppGuardianConfiguration readConfigurationFile(final InputStream stream, final String webRootDir) throws ConfigurationException {
        final AppGuardianConfiguration config = new AppGuardianConfiguration();
        final Builder parser = new Builder();
        try {
            final Document doc = parser.build(stream);
            final Element root = doc.getRootElement();
            final Element aliasesRoot = root.getFirstChildElement("aliases");
            final Element settingsRoot = root.getFirstChildElement("settings");
            final Element authNRoot = root.getFirstChildElement("authentication-rules");
            final Element authZRoot = root.getFirstChildElement("authorization-rules");
            final Element urlRoot = root.getFirstChildElement("url-rules");
            final Element headerRoot = root.getFirstChildElement("header-rules");
            final Element customRulesRoot = root.getFirstChildElement("custom-rules");
            final Element virtualPatchesRoot = root.getFirstChildElement("virtual-patches");
            final Element outboundRoot = root.getFirstChildElement("outbound-rules");
            final Element beanShellRoot = root.getFirstChildElement("bean-shell-rules");
            if (aliasesRoot != null) {
                final Elements aliases = aliasesRoot.getChildElements("alias");
                for (int i = 0; i < aliases.size(); ++i) {
                    final Element e = aliases.get(i);
                    final String name = e.getAttributeValue("name");
                    final String type = e.getAttributeValue("type");
                    final String value = e.getValue();
                    if ("regex".equals(type)) {
                        config.addAlias(name, Pattern.compile(value));
                    }
                    else {
                        config.addAlias(name, value);
                    }
                }
            }
            if (settingsRoot == null) {
                throw new ConfigurationException("", "The <settings> section is required");
            }
            if (settingsRoot != null) {
                try {
                    final String sessionCookieName = settingsRoot.getFirstChildElement("session-cookie-name").getValue();
                    if (!"".equals(sessionCookieName)) {
                        config.setSessionCookieName(sessionCookieName);
                    }
                }
                catch (final NullPointerException npe) {
                    config.setSessionCookieName(ConfigurationParser.DEFAULT_SESSION_COOKIE);
                }
                final String mode = settingsRoot.getFirstChildElement("mode").getValue();
                if ("block".equals(mode.toLowerCase())) {
                    AppGuardianConfiguration.DEFAULT_FAIL_ACTION = 2;
                }
                else if ("redirect".equals(mode.toLowerCase())) {
                    AppGuardianConfiguration.DEFAULT_FAIL_ACTION = 1;
                }
                else {
                    AppGuardianConfiguration.DEFAULT_FAIL_ACTION = 0;
                }
                final Element errorHandlingRoot = settingsRoot.getFirstChildElement("error-handling");
                config.setDefaultErrorPage(errorHandlingRoot.getFirstChildElement("default-redirect-page").getValue());
                try {
                    config.setDefaultResponseCode(Integer.parseInt(errorHandlingRoot.getFirstChildElement("block-status").getValue()));
                }
                catch (final Exception e2) {
                    config.setDefaultResponseCode(403);
                }
            }
            final Element loggingRoot = settingsRoot.getFirstChildElement("logging");
            if (loggingRoot != null) {
                config.setLogDirectory(loggingRoot.getFirstChildElement("log-directory").getValue());
                config.setLogLevel(Level.toLevel(loggingRoot.getFirstChildElement("log-level").getValue()));
            }
            if (authNRoot != null) {
                final String key = authNRoot.getAttributeValue("key");
                final String path = authNRoot.getAttributeValue("path");
                final String id = authNRoot.getAttributeValue("id");
                if (path != null && key != null) {
                    config.addBeforeBodyRule(new AuthenticatedRule(id, key, Pattern.compile(path), getExceptionsFromElement(authNRoot)));
                }
                else {
                    if (key == null) {
                        throw new ConfigurationException("", "The <authentication-rules> rule requires a 'key' attribute");
                    }
                    config.addBeforeBodyRule(new AuthenticatedRule(id, key, null, getExceptionsFromElement(authNRoot)));
                }
            }
            if (authZRoot != null) {
                final Elements restrictNodes = authZRoot.getChildElements("restrict-source-ip");
                for (int j = 0; j < restrictNodes.size(); ++j) {
                    final Element restrictNodeRoot = restrictNodes.get(j);
                    final String id2 = restrictNodeRoot.getAttributeValue("id");
                    final Pattern ips = Pattern.compile(restrictNodeRoot.getAttributeValue("ip-regex"));
                    final String ipHeader = restrictNodeRoot.getAttributeValue("ip-header");
                    if ("regex".equalsIgnoreCase(restrictNodeRoot.getAttributeValue("type"))) {
                        config.addBeforeBodyRule(new IPRule(id2, ips, Pattern.compile(restrictNodeRoot.getValue()), ipHeader));
                    }
                    else {
                        config.addBeforeBodyRule(new IPRule(id2, ips, restrictNodeRoot.getValue()));
                    }
                }
                final Elements mustMatchNodes = authZRoot.getChildElements("must-match");
                for (int k = 0; k < mustMatchNodes.size(); ++k) {
                    final Element e3 = mustMatchNodes.get(k);
                    final Pattern path2 = Pattern.compile(e3.getAttributeValue("path"));
                    final String variable = e3.getAttributeValue("variable");
                    final String value2 = e3.getAttributeValue("value");
                    final String operator = e3.getAttributeValue("operator");
                    final String id3 = e3.getAttributeValue("id");
                    int op = 0;
                    if ("exists".equalsIgnoreCase(operator)) {
                        op = 3;
                    }
                    else if ("inList".equalsIgnoreCase(operator)) {
                        op = 2;
                    }
                    else if ("contains".equalsIgnoreCase(operator)) {
                        op = 1;
                    }
                    config.addAfterBodyRule(new MustMatchRule(id3, path2, variable, op, value2));
                }
            }
            if (urlRoot != null) {
                final Elements restrictExtensionNodes = urlRoot.getChildElements("restrict-extension");
                final Elements restrictMethodNodes = urlRoot.getChildElements("restrict-method");
                final Elements enforceHttpsNodes = urlRoot.getChildElements("enforce-https");
                for (int l = 0; l < restrictExtensionNodes.size(); ++l) {
                    final Element e4 = restrictExtensionNodes.get(l);
                    final String allow = e4.getAttributeValue("allow");
                    final String deny = e4.getAttributeValue("deny");
                    final String id4 = e4.getAttributeValue("id");
                    if (allow != null && deny != null) {
                        throw new ConfigurationException("", "restrict-extension rules can't have both 'allow' and 'deny'");
                    }
                    if (allow != null) {
                        config.addBeforeBodyRule(new PathExtensionRule(id4, Pattern.compile(".*\\" + allow + "$"), null));
                    }
                    else {
                        if (deny == null) {
                            throw new ConfigurationException("", "restrict extension rule should have either a 'deny' or 'allow' attribute");
                        }
                        config.addBeforeBodyRule(new PathExtensionRule(id4, null, Pattern.compile(".*\\" + deny + "$")));
                    }
                }
                for (int l = 0; l < restrictMethodNodes.size(); ++l) {
                    final Element e4 = restrictMethodNodes.get(l);
                    final String allow = e4.getAttributeValue("allow");
                    final String deny = e4.getAttributeValue("deny");
                    String path3 = e4.getAttributeValue("path");
                    final String id3 = e4.getAttributeValue("id");
                    if (path3 == null) {
                        path3 = ".*";
                    }
                    if (allow != null && deny != null) {
                        throw new ConfigurationException("", "restrict-method rule should not have both 'allow' and 'deny' values");
                    }
                    if (allow != null) {
                        config.addBeforeBodyRule(new HTTPMethodRule(id3, Pattern.compile(allow), null, Pattern.compile(path3)));
                    }
                    else {
                        if (deny == null) {
                            throw new ConfigurationException("", "restrict-method rule should have either an 'allow' or 'deny' value");
                        }
                        config.addBeforeBodyRule(new HTTPMethodRule(id3, null, Pattern.compile(deny), Pattern.compile(path3)));
                    }
                }
                for (int l = 0; l < enforceHttpsNodes.size(); ++l) {
                    final Element e4 = enforceHttpsNodes.get(l);
                    final String path4 = e4.getAttributeValue("path");
                    final String action = e4.getAttributeValue("action");
                    final String id4 = e4.getAttributeValue("id");
                    final List<Object> exceptions = getExceptionsFromElement(e4);
                    config.addBeforeBodyRule(new EnforceHTTPSRule(id4, Pattern.compile(path4), exceptions, action));
                }
            }
            if (headerRoot != null) {
                final Elements restrictContentTypes = headerRoot.getChildElements("restrict-content-type");
                final Elements restrictUserAgents = headerRoot.getChildElements("restrict-user-agent");
                for (int k = 0; k < restrictContentTypes.size(); ++k) {
                    final Element e3 = restrictContentTypes.get(k);
                    final String allow2 = e3.getAttributeValue("allow");
                    final String deny2 = e3.getAttributeValue("deny");
                    final String id5 = e3.getAttributeValue("id");
                    if (allow2 != null && deny2 != null) {
                        throw new ConfigurationException("", "restrict-content-type rule should not have both 'allow' and 'deny' values");
                    }
                    if (allow2 != null) {
                        config.addBeforeBodyRule(new RestrictContentTypeRule(id5, Pattern.compile(allow2), null));
                    }
                    else {
                        if (deny2 == null) {
                            throw new ConfigurationException("", "restrict-content-type rule should have either an 'allow' or 'deny' value");
                        }
                        config.addBeforeBodyRule(new RestrictContentTypeRule(id5, null, Pattern.compile(deny2)));
                    }
                }
                for (int k = 0; k < restrictUserAgents.size(); ++k) {
                    final Element e3 = restrictUserAgents.get(k);
                    final String id6 = e3.getAttributeValue("id");
                    final String allow = e3.getAttributeValue("allow");
                    final String deny = e3.getAttributeValue("deny");
                    if (allow != null && deny != null) {
                        throw new ConfigurationException("", "restrict-user-agent rule should not have both 'allow' and 'deny' values");
                    }
                    if (allow != null) {
                        config.addBeforeBodyRule(new RestrictUserAgentRule(id6, Pattern.compile(allow), null));
                    }
                    else {
                        if (deny == null) {
                            throw new ConfigurationException("", "restrict-user-agent rule should have either an 'allow' or 'deny' value");
                        }
                        config.addBeforeBodyRule(new RestrictUserAgentRule(id6, null, Pattern.compile(deny)));
                    }
                }
            }
            if (virtualPatchesRoot != null) {
                final Elements virtualPatchNodes = virtualPatchesRoot.getChildElements("virtual-patch");
                for (int j = 0; j < virtualPatchNodes.size(); ++j) {
                    final Element e5 = virtualPatchNodes.get(j);
                    final String id2 = e5.getAttributeValue("id");
                    final String path5 = e5.getAttributeValue("path");
                    final String variable = e5.getAttributeValue("variable");
                    final String pattern = e5.getAttributeValue("pattern");
                    final String message = e5.getAttributeValue("message");
                    config.addAfterBodyRule(new SimpleVirtualPatchRule(id2, Pattern.compile(path5), variable, Pattern.compile(pattern), message));
                }
            }
            if (outboundRoot != null) {
                final Elements addHeaderNodes = outboundRoot.getChildElements("add-header");
                for (int j = 0; j < addHeaderNodes.size(); ++j) {
                    final Element e5 = addHeaderNodes.get(j);
                    final String name2 = e5.getAttributeValue("name");
                    final String value = e5.getAttributeValue("value");
                    String path4 = e5.getAttributeValue("path");
                    final String id5 = e5.getAttributeValue("id");
                    if (path4 == null) {
                        path4 = ".*";
                    }
                    final AddHeaderRule ahr = new AddHeaderRule(id5, name2, value, Pattern.compile(path4), getExceptionsFromElement(e5));
                    config.addBeforeResponseRule(ahr);
                }
                final Elements addHTTPOnlyFlagNodes = outboundRoot.getChildElements("add-http-only-flag");
                for (int k = 0; k < addHTTPOnlyFlagNodes.size(); ++k) {
                    final Element e3 = addHTTPOnlyFlagNodes.get(k);
                    final Elements cookiePatterns = e3.getChildElements("cookie");
                    final String id7 = e3.getAttributeValue("id");
                    final ArrayList<Pattern> patterns = new ArrayList<Pattern>();
                    for (int m = 0; m < cookiePatterns.size(); ++m) {
                        final Element cookie = cookiePatterns.get(m);
                        patterns.add(Pattern.compile(cookie.getAttributeValue("name")));
                    }
                    final AddHTTPOnlyFlagRule ahfr = new AddHTTPOnlyFlagRule(id7, patterns);
                    config.addCookieRule(ahfr);
                    if (ahfr.doesCookieMatch(config.getSessionCookieName())) {
                        config.setApplyHTTPOnlyFlagToSessionCookie(true);
                    }
                }
                final Elements addSecureFlagNodes = outboundRoot.getChildElements("add-secure-flag");
                for (int l = 0; l < addSecureFlagNodes.size(); ++l) {
                    final Element e4 = addSecureFlagNodes.get(l);
                    final String id7 = e4.getAttributeValue("id");
                    final Elements cookiePatterns2 = e4.getChildElements("cookie");
                    final ArrayList<Pattern> patterns2 = new ArrayList<Pattern>();
                    for (int j2 = 0; j2 < cookiePatterns2.size(); ++j2) {
                        final Element cookie2 = cookiePatterns2.get(j2);
                        patterns2.add(Pattern.compile(cookie2.getAttributeValue("name")));
                    }
                    final AddSecureFlagRule asfr = new AddSecureFlagRule(id7, patterns2);
                    config.addCookieRule(asfr);
                    if (asfr.doesCookieMatch(config.getSessionCookieName())) {
                        config.setApplySecureFlagToSessionCookie(true);
                    }
                }
                final Elements dynamicInsertionNodes = outboundRoot.getChildElements("dynamic-insertion");
                for (int i2 = 0; i2 < dynamicInsertionNodes.size(); ++i2) {
                    final Element e6 = dynamicInsertionNodes.get(i2);
                    final String pattern = e6.getAttributeValue("pattern");
                    final String id4 = e6.getAttributeValue("id");
                    final String contentType = e6.getAttributeValue("content-type");
                    final String urlPaths = e6.getAttributeValue("path");
                    final Element replacement = e6.getFirstChildElement("replacement");
                    final ReplaceContentRule rcr = new ReplaceContentRule(id4, Pattern.compile(pattern, 32), replacement.getValue(), (contentType != null) ? Pattern.compile(contentType) : null, (urlPaths != null) ? Pattern.compile(urlPaths) : null);
                    config.addBeforeResponseRule(rcr);
                }
                final Elements detectContentNodes = outboundRoot.getChildElements("detect-content");
                for (int i3 = 0; i3 < detectContentNodes.size(); ++i3) {
                    final Element e7 = detectContentNodes.get(i3);
                    final String token = e7.getAttributeValue("pattern");
                    final String contentType = e7.getAttributeValue("content-type");
                    final String id8 = e7.getAttributeValue("id");
                    final String path6 = e7.getAttributeValue("path");
                    if (token == null) {
                        throw new ConfigurationException("", "<detect-content> rules must contain a 'pattern' attribute");
                    }
                    if (contentType == null) {
                        throw new ConfigurationException("", "<detect-content> rules must contain a 'content-type' attribute");
                    }
                    final DetectOutboundContentRule docr = new DetectOutboundContentRule(id8, Pattern.compile(contentType), Pattern.compile(token, 32), (path6 != null) ? Pattern.compile(path6) : null);
                    config.addBeforeResponseRule(docr);
                }
            }
            if (beanShellRoot != null) {
                final Elements beanShellRules = beanShellRoot.getChildElements("bean-shell-script");
                for (int j = 0; j < beanShellRules.size(); ++j) {
                    final Element e5 = beanShellRules.get(j);
                    final String id2 = e5.getAttributeValue("id");
                    final String fileName = e5.getAttributeValue("file");
                    final String stage = e5.getAttributeValue("stage");
                    final String path7 = e5.getAttributeValue("path");
                    if (id2 == null) {
                        throw new ConfigurationException("", "bean shell rules all require a unique 'id' attribute");
                    }
                    if (fileName == null) {
                        throw new ConfigurationException("", "bean shell rules all require a unique 'file' attribute that has the location of the .bsh script");
                    }
                    try {
                        final BeanShellRule bsr = new BeanShellRule(webRootDir + fileName, id2, (path7 != null) ? Pattern.compile(path7) : null);
                        if (ConfigurationParser.STAGES[0].equals(stage)) {
                            config.addBeforeBodyRule(bsr);
                        }
                        else if (ConfigurationParser.STAGES[1].equals(stage)) {
                            config.addAfterBodyRule(bsr);
                        }
                        else {
                            if (!ConfigurationParser.STAGES[2].equals(stage)) {
                                throw new ConfigurationException("", "bean shell rules all require a 'stage' attribute when the rule should be fired (valid values are " + ConfigurationParser.STAGES[0] + ", " + ConfigurationParser.STAGES[1] + ", or " + ConfigurationParser.STAGES[2] + ")");
                            }
                            config.addBeforeResponseRule(bsr);
                        }
                    }
                    catch (final FileNotFoundException fnfe) {
                        throw new ConfigurationException("", "bean shell rule '" + id2 + "' had a source file that could not be found (" + fileName + "), web directory = " + webRootDir);
                    }
                    catch (final EvalError ee) {
                        throw new ConfigurationException("", "bean shell rule '" + id2 + "' contained an error (" + ee.getErrorText() + "): " + ee.getScriptStackTrace());
                    }
                }
            }
        }
        catch (final ValidityException e8) {
            throw new ConfigurationException("", "Problem validating WAF XML file", (Throwable)e8);
        }
        catch (final ParsingException e9) {
            throw new ConfigurationException("", "Problem parsing WAF XML file", (Throwable)e9);
        }
        catch (final IOException e10) {
            throw new ConfigurationException("", "I/O problem reading WAF XML file", e10);
        }
        return config;
    }
    
    private static List<Object> getExceptionsFromElement(final Element root) {
        final Elements exceptions = root.getChildElements("path-exception");
        final ArrayList<Object> exceptionList = new ArrayList<Object>();
        for (int i = 0; i < exceptions.size(); ++i) {
            final Element e = exceptions.get(i);
            if ("regex".equalsIgnoreCase(e.getAttributeValue("type"))) {
                exceptionList.add(Pattern.compile(e.getValue()));
            }
            else {
                exceptionList.add(e.getValue());
            }
        }
        return exceptionList;
    }
    
    static {
        String sessionIdName = null;
        try {
            sessionIdName = ESAPI.securityConfiguration().getHttpSessionIdName();
        }
        catch (final Throwable t) {
            sessionIdName = "JSESSIONID";
        }
        DEFAULT_SESSION_COOKIE = sessionIdName;
        STAGES = new String[] { "before-request-body", "after-request-body", "before-response" };
    }
}
