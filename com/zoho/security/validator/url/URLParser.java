package com.zoho.security.validator.url;

import java.util.BitSet;
import java.io.CharArrayWriter;
import java.util.List;
import java.util.logging.Level;
import java.net.MalformedURLException;
import java.util.logging.Logger;

public class URLParser
{
    private static final Logger LOGGER;
    static final int CASEDIFF = 32;
    private int start;
    private int end;
    private int len;
    private int customSchemeDataStartIndex;
    ZSecURL safeurl_obj;
    URLValidatorAPI urlvalidator;
    private Scheme currentSchemeObj;
    
    public URLParser(final URLValidatorAPI validator, final String url) throws MalformedURLException {
        this.start = 0;
        this.end = 0;
        this.urlvalidator = validator;
        this.safeurl_obj = new ZSecURL(url);
        this.len = url.length();
        this.parseAndCheckSafeURL();
    }
    
    private void parseAndCheckSafeURL() throws MalformedURLException {
        final StringBuilder safeUrlBuilder = new StringBuilder();
        this.scan(":/?#");
        boolean schemaFound = false;
        if (this.isCurrentChar(58)) {
            safeUrlBuilder.append(this.safeurl_obj.scheme = this.safeurl_obj.original_url.substring(this.start, this.end));
            this.skipAndAppendChars(":", safeUrlBuilder);
            schemaFound = true;
        }
        if (this.skip("//")) {
            while (this.isCurrentChar(47)) {
                ++this.end;
            }
            safeUrlBuilder.append("//");
            if (this.urlvalidator.allowRelativeURL && this.safeurl_obj.scheme == null) {
                this.safeurl_obj.scheme = "https";
                schemaFound = true;
            }
            this.start = this.end;
        }
        if (schemaFound) {
            if (this.setSchemeIfAllowed(this.safeurl_obj.scheme)) {
                final String surl = this.parseURLBasedOnScheme(safeUrlBuilder);
                if (surl.length() > 0) {
                    safeUrlBuilder.append(surl);
                }
                else {
                    safeUrlBuilder.setLength(0);
                }
            }
            else if (this.isCustomScheme(this.safeurl_obj.scheme)) {
                this.parseAndValidateCustomScheme(safeUrlBuilder);
            }
            else {
                this.throwExceptionIfErrorMode("URL_INVALID_SCHEME", "Scheme :\" " + this.safeurl_obj.scheme + " \"  is not allowed in URL");
                this.safeurl_obj.scheme = null;
                this.start = ((this.safeurl_obj.customScheme != null) ? this.customSchemeDataStartIndex : 0);
                this.end = this.len;
                safeUrlBuilder.setLength(0);
                if (this.safeurl_obj.customScheme != null) {
                    safeUrlBuilder.append(this.getSafeURLComponent(URLValidatorAPI.SCHEMES.RELATIVE, URLValidatorAPI.URLCOMPONENTS.PATHINFO));
                }
                else {
                    safeUrlBuilder.append(this.getSafeURLComponent(null, null));
                }
            }
        }
        else if (this.safeurl_obj.customScheme != null) {
            this.parseCustomSchemeNonURLData(safeUrlBuilder);
        }
        else if (this.urlvalidator.allowRelativeURL) {
            this.currentSchemeObj = this.urlvalidator.getScheme(URLValidatorAPI.SCHEMES.RELATIVE.name().toLowerCase());
            safeUrlBuilder.append(this.getRelativePathURLComponents());
        }
        else {
            this.throwExceptionIfErrorMode("URL_INVALID_SCHEME", "No scheme found in URL");
            this.start = 0;
            this.end = this.len;
            safeUrlBuilder.setLength(0);
            safeUrlBuilder.append(this.getSafeURLComponent(null, null));
        }
        this.safeurl_obj.safeURL = safeUrlBuilder.toString();
    }
    
    private boolean isCustomScheme(final String scheme) {
        return this.safeurl_obj.customScheme == null && this.urlvalidator.getCustomScheme(scheme) != null;
    }
    
    private void parseAndValidateCustomScheme(final StringBuilder safeUrlBuilder) throws MalformedURLException {
        this.safeurl_obj.customScheme = this.safeurl_obj.scheme;
        this.safeurl_obj.scheme = null;
        this.customSchemeDataStartIndex = this.end;
        this.currentSchemeObj = this.urlvalidator.getCustomScheme(this.safeurl_obj.customScheme);
        this.parseAndCheckSafeURL();
        safeUrlBuilder.append(this.safeurl_obj.safeURL);
    }
    
    private void parseCustomSchemeNonURLData(final StringBuilder safeUrlBuilder) throws MalformedURLException {
        this.scan("/?#");
        this.setSafeURLComponent(this.getSchemeName(), URLValidatorAPI.URLCOMPONENTS.DOMAINAUTHORITY, safeUrlBuilder);
        this.start = this.end;
        safeUrlBuilder.append(this.getRelativePathURLComponents());
    }
    
    private boolean setSchemeIfAllowed(final String scheme) throws MalformedURLException {
        if (this.currentSchemeObj == null) {
            if (this.urlvalidator.getScheme(this.safeurl_obj.scheme) != null) {
                this.currentSchemeObj = this.urlvalidator.getScheme(this.safeurl_obj.scheme);
                return true;
            }
        }
        else if (((CustomScheme)this.currentSchemeObj).getScheme(this.safeurl_obj.scheme) != null) {
            this.currentSchemeObj = ((CustomScheme)this.currentSchemeObj).getScheme(this.safeurl_obj.scheme);
            return true;
        }
        return false;
    }
    
    private void throwExceptionIfErrorMode(final String msg, final String logMsg) throws MalformedURLException {
        if (this.urlvalidator.mode.equalsIgnoreCase("error")) {
            URLParser.LOGGER.log(Level.SEVERE, "\n Exception occured while validating the URL : \"{0}\" & Exception Message : {1}", new Object[] { this.safeurl_obj.original_url, logMsg });
            throw new MalformedURLException(msg);
        }
    }
    
    private String getRelativePathURLComponents() throws MalformedURLException {
        final StringBuilder urlComponentBuilder = new StringBuilder();
        this.scan("?#");
        final URLValidatorAPI.SCHEMES scheme = this.getSchemeName();
        this.setSafeURLComponent(scheme, URLValidatorAPI.URLCOMPONENTS.PATHINFO, urlComponentBuilder);
        this.start = this.end;
        if (this.isCurrentChar(63)) {
            this.skipAndAppendChars("?", urlComponentBuilder);
            this.scan("#");
            this.setSafeURLComponent(scheme, URLValidatorAPI.URLCOMPONENTS.QUERYSTRING, urlComponentBuilder);
            this.start = this.end;
        }
        if (this.isCurrentChar(35)) {
            this.skipAndAppendChars("#", urlComponentBuilder);
            this.end = this.len;
            this.setSafeURLComponent(scheme, URLValidatorAPI.URLCOMPONENTS.FRAGMENT, urlComponentBuilder);
        }
        return urlComponentBuilder.toString();
    }
    
    private boolean skipAndAppendChars(final String skipChars, final StringBuilder urlComponentBuilder) {
        if (this.skip(skipChars)) {
            urlComponentBuilder.append(skipChars);
            this.start = this.end;
            return true;
        }
        return false;
    }
    
    private URLValidatorAPI.SCHEMES getSchemeName() {
        if (this.safeurl_obj.scheme != null && this.safeurl_obj.scheme.length() > 0) {
            try {
                return URLValidatorAPI.SCHEMES.valueOf(this.safeurl_obj.scheme.toUpperCase());
            }
            catch (final Exception e) {
                return null;
            }
        }
        return URLValidatorAPI.SCHEMES.RELATIVE;
    }
    
    private String parseURLBasedOnScheme(final StringBuilder safeUrlBuilder) throws MalformedURLException {
        final StringBuilder sb = new StringBuilder();
        if (this.safeurl_obj.scheme.equalsIgnoreCase("mailto")) {
            this.scan("?");
            this.setSafeURLComponent(URLValidatorAPI.SCHEMES.MAILTO, URLValidatorAPI.URLCOMPONENTS.DOMAINAUTHORITY, sb);
            if (this.isCurrentChar(63)) {
                this.skipAndAppendChars("?", sb);
                this.end = this.len;
                this.setSafeURLComponent(URLValidatorAPI.SCHEMES.MAILTO, URLValidatorAPI.URLCOMPONENTS.QUERYSTRING, sb);
            }
        }
        else if (this.safeurl_obj.scheme.equalsIgnoreCase("tel")) {
            this.end = this.len;
            this.setSafeURLComponent(URLValidatorAPI.SCHEMES.TEL, URLValidatorAPI.URLCOMPONENTS.DOMAINAUTHORITY, sb);
        }
        else if (this.safeurl_obj.scheme.equalsIgnoreCase("data")) {
            this.scan(",");
            boolean isValid = false;
            if (this.isCurrentChar(44)) {
                final String headerpart = this.safeurl_obj.original_url.substring(this.start, this.end);
                final String[] headers = headerpart.split(";");
                for (int i = 0; i < headers.length; ++i) {
                    final String header = headers[i];
                    final int equal_ind = header.indexOf(61);
                    String hname = null;
                    String hvalue = null;
                    if (equal_ind == -1) {
                        hname = header.trim();
                    }
                    else {
                        hname = header.substring(0, equal_ind).trim();
                        hvalue = header.substring(equal_ind + 1).trim();
                    }
                    if (hname != null && hname.length() > 0) {
                        if ((i != 0 || equal_ind != -1 || !this.checkAndSetDataURIHeaders(URLValidatorAPI.DATAURIHEADERS.MIMETYPES, this.currentSchemeObj.allowedMimetypes, hname, sb)) && (i <= 0 || equal_ind != -1 || !this.checkAndSetDataURIHeaders(URLValidatorAPI.DATAURIHEADERS.ENCODING, this.currentSchemeObj.allowedEncoding, hname, sb)) && (i <= 0 || equal_ind == -1 || !hname.equalsIgnoreCase("charset") || !this.checkAndSetDataURIHeaders(URLValidatorAPI.DATAURIHEADERS.CHARSET, this.currentSchemeObj.allowedCharsets, hvalue, sb))) {
                            this.throwExceptionIfErrorMode("DATAURL_INVALID_HEADER", "Header :\" " + hname + " \"  is not allowed in Data URL");
                            isValid = false;
                            break;
                        }
                        isValid = true;
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                    sb.append(",");
                }
                this.skip(",");
                this.start = this.end;
            }
            if (isValid) {
                this.end = this.len;
                this.setSafeURLComponent(URLValidatorAPI.SCHEMES.DATA, URLValidatorAPI.URLCOMPONENTS.FRAGMENT, sb);
            }
            else {
                this.safeurl_obj.scheme = null;
                this.start = 0;
                this.end = this.len;
                sb.setLength(0);
                safeUrlBuilder.setLength(0);
                sb.append(this.getSafeURLComponent());
            }
        }
        else {
            this.scan("/?#");
            this.setSafeURLComponent(this.getSchemeName(), URLValidatorAPI.URLCOMPONENTS.DOMAINAUTHORITY, sb);
            this.start = this.end;
            sb.append(this.getRelativePathURLComponents());
        }
        return sb.toString();
    }
    
    private boolean checkAndSetDataURIHeaders(final URLValidatorAPI.DATAURIHEADERS header, final List<String> allowedList, final String headerKey, final StringBuilder sb) throws MalformedURLException {
        if (allowedList.contains(headerKey.toLowerCase())) {
            switch (header) {
                case MIMETYPES: {
                    this.safeurl_obj.dataURIMimeType = headerKey;
                    sb.append(headerKey).append(";");
                    break;
                }
                case ENCODING: {
                    this.safeurl_obj.dataURIEncoding = headerKey;
                    sb.append(headerKey).append(";");
                    break;
                }
                case CHARSET: {
                    this.safeurl_obj.dataURICharset = headerKey;
                    sb.append("charset=" + headerKey).append(";");
                    break;
                }
            }
            return true;
        }
        this.throwExceptionIfErrorMode("DATAURL_INVALID_HEADER", header.name() + " :\" " + headerKey + " \"  is not allowed in data URL");
        return false;
    }
    
    private String getSafeURLComponent() throws MalformedURLException {
        return this.getSafeURLComponent(null, null);
    }
    
    private String getSafeURLComponent(final URLValidatorAPI.SCHEMES protocol, final URLValidatorAPI.URLCOMPONENTS url_comp) throws MalformedURLException {
        final String component = this.validate(this.safeurl_obj.original_url.substring(this.start, this.end), protocol, url_comp);
        if (component.length() > 0) {
            return component;
        }
        return "";
    }
    
    private void setSafeURLComponent(final URLValidatorAPI.SCHEMES protocol, final URLValidatorAPI.URLCOMPONENTS url_comp, final StringBuilder urlComponentBuilder) throws MalformedURLException {
        final String component = this.getSafeURLComponent(protocol, url_comp);
        switch (url_comp) {
            case DOMAINAUTHORITY: {
                this.safeurl_obj.domainAuthority = component;
                break;
            }
            case PATHINFO: {
                this.safeurl_obj.pathInfo = component;
                break;
            }
            case QUERYSTRING: {
                this.safeurl_obj.queryString = component;
                break;
            }
            case FRAGMENT: {
                if (protocol == URLValidatorAPI.SCHEMES.DATA) {
                    this.safeurl_obj.dataURIDatapart = component;
                    break;
                }
                this.safeurl_obj.fragment = component;
                break;
            }
        }
        urlComponentBuilder.append(component);
    }
    
    public String validate(final String input, final URLValidatorAPI.SCHEMES protocol, final URLValidatorAPI.URLCOMPONENTS urlcomponent) throws MalformedURLException {
        boolean needToChange = false;
        final StringBuffer out = new StringBuffer(input.length());
        final CharArrayWriter charArrayWriter = new CharArrayWriter();
        int i = 0;
        while (i < input.length()) {
            int c = input.charAt(i);
            if (!this.isEncodingRequired(c, protocol, urlcomponent)) {
                out.append((char)c);
                ++i;
            }
            else {
                if (!this.urlvalidator.mode.equalsIgnoreCase("encode")) {
                    URLParser.LOGGER.log(Level.SEVERE, "\n  Exception occured while validating the URL :\"{0}\". URL contains unsafe character: \"{1}\" at index {2} in the {3} component of the URL", new Object[] { this.safeurl_obj.original_url, (char)c, i, urlcomponent });
                    throw new MalformedURLException("URL_INVALID_CHARACTER");
                }
                do {
                    charArrayWriter.write(c);
                    if (c >= 55296 && c <= 56319 && i + 1 < input.length()) {
                        final int d = input.charAt(i + 1);
                        if (d < 56320 || d > 57343) {
                            continue;
                        }
                        charArrayWriter.write(d);
                        ++i;
                    }
                } while (++i < input.length() && this.isEncodingRequired(c = input.charAt(i), protocol, urlcomponent));
                charArrayWriter.flush();
                final String str = new String(charArrayWriter.toCharArray());
                final byte[] ba = str.getBytes(this.urlvalidator.url_charset_encoding);
                for (int j = 0; j < ba.length; ++j) {
                    out.append('%');
                    out.append(this.convertByteToHex(ba[j]));
                }
                charArrayWriter.reset();
                needToChange = true;
            }
        }
        return needToChange ? out.toString() : input;
    }
    
    private String convertByteToHex(final byte b) {
        final StringBuilder hexBuilder = new StringBuilder();
        char ch = Character.forDigit(b >> 4 & 0xF, 16);
        if (Character.isLetter(ch)) {
            ch -= ' ';
        }
        hexBuilder.append(ch);
        ch = Character.forDigit(b & 0xF, 16);
        if (Character.isLetter(ch)) {
            ch -= ' ';
        }
        hexBuilder.append(ch);
        return hexBuilder.toString();
    }
    
    private boolean isEncodingRequired(final int c, final URLValidatorAPI.SCHEMES protocol, final URLValidatorAPI.URLCOMPONENTS component) {
        if (URLValidatorAPI.alphanumeric_hyphen_period.get(c)) {
            return false;
        }
        if (protocol == null || component == null) {
            return true;
        }
        if (this.currentSchemeObj != null) {
            final BitSet urlComponent = this.currentSchemeObj.urlcomponents.get(component.value);
            if (urlComponent != null && urlComponent.get(c)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean skip(final String skip) {
        final int slength = skip.length();
        final int skipindex = this.end + slength;
        if (this.end < this.len && skipindex <= this.len && this.safeurl_obj.original_url.subSequence(this.end, skipindex).equals(skip)) {
            this.end = skipindex;
            return true;
        }
        return false;
    }
    
    private boolean scan(final String delimiter) {
        while (this.end < this.len) {
            final char c = this.charAt(this.end);
            if (delimiter.indexOf(c) >= 0) {
                return true;
            }
            ++this.end;
        }
        return false;
    }
    
    private char charAt(final int p) {
        return this.safeurl_obj.original_url.charAt(p);
    }
    
    private boolean isCurrentChar(final int p) {
        return this.end < this.len && this.charAt(this.end) == p;
    }
    
    static {
        LOGGER = Logger.getLogger(URLParser.class.getName());
    }
}
