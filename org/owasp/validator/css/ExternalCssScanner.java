package org.owasp.validator.css;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.owasp.validator.html.ScanException;
import org.w3c.dom.Node;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import org.owasp.validator.html.util.ErrorMessageUtil;
import org.owasp.validator.html.util.HTMLEntityEncoder;
import java.net.URI;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.config.RequestConfig;
import java.util.ArrayList;
import java.util.LinkedList;
import org.owasp.validator.html.InternalPolicy;
import java.util.ResourceBundle;

public class ExternalCssScanner extends ZohoCssScanner
{
    protected static final int DEFAULT_TIMEOUT = 1000;
    protected final ResourceBundle messages;
    
    public ExternalCssScanner(final InternalPolicy policy, final ResourceBundle messages) {
        super(policy, messages);
        this.messages = messages;
    }
    
    protected void parseImportedStylesheets(final LinkedList<?> stylesheets, final CssHandler handler, final ArrayList<String> errorMessages, int sizeLimit) throws ScanException {
        int importedStylesheets = 0;
        if (!stylesheets.isEmpty()) {
            int timeout = 1000;
            try {
                timeout = Integer.parseInt(this.policy.getDirective("connectionTimeout"));
            }
            catch (final NumberFormatException ex) {}
            final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).build();
            final HttpClient httpClient = (HttpClient)HttpClientBuilder.create().disableAutomaticRetries().disableConnectionState().disableCookieManagement().setDefaultRequestConfig(requestConfig).build();
            int allowedImports = 1;
            try {
                allowedImports = Integer.parseInt(this.policy.getDirective("maxStyleSheetImports"));
            }
            catch (final NumberFormatException ex2) {}
            while (!stylesheets.isEmpty()) {
                final URI stylesheetUri = (URI)stylesheets.removeFirst();
                if (++importedStylesheets > allowedImports) {
                    errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.exceeded", new Object[] { HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()), String.valueOf(allowedImports) }));
                }
                else {
                    final HttpGet stylesheetRequest = new HttpGet(stylesheetUri);
                    byte[] stylesheet = null;
                    try {
                        final HttpResponse response = httpClient.execute((HttpUriRequest)stylesheetRequest);
                        stylesheet = EntityUtils.toByteArray(response.getEntity());
                        if (stylesheet != null && stylesheet.length > sizeLimit) {
                            errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.toolarge", new Object[] { HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()), String.valueOf(this.policy.getMaxInputSize()) }));
                            stylesheet = null;
                        }
                    }
                    catch (final IOException ioe) {
                        errorMessages.add(ErrorMessageUtil.getMessage(this.messages, "error.css.import.failure", new Object[] { HTMLEntityEncoder.htmlEntityEncode(stylesheetUri.toString()) }));
                    }
                    finally {
                        stylesheetRequest.releaseConnection();
                    }
                    if (stylesheet == null) {
                        continue;
                    }
                    sizeLimit -= stylesheet.length;
                    try {
                        final InputSource nextStyleSheet = new InputSource((Reader)new InputStreamReader(new ByteArrayInputStream(stylesheet), Charset.forName("UTF8")));
                        this.parser.parseStyleSheet(nextStyleSheet, (Node)null, (String)null);
                    }
                    catch (final IOException ioe) {
                        throw new ScanException(ioe);
                    }
                }
            }
        }
    }
}
