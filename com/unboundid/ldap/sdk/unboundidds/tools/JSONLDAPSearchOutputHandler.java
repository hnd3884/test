package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.Base64;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchResultReference;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.json.JSONBuffer;
import java.util.ArrayList;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class JSONLDAPSearchOutputHandler extends LDAPSearchOutputHandler
{
    private final ArrayList<String> formattedLines;
    private final JSONBuffer jsonBuffer;
    private final LDAPSearch ldapSearch;
    
    JSONLDAPSearchOutputHandler(final LDAPSearch ldapSearch) {
        this.ldapSearch = ldapSearch;
        this.formattedLines = new ArrayList<String>(10);
        this.jsonBuffer = new JSONBuffer(null, 0, true);
    }
    
    public void formatHeader() {
    }
    
    public void formatSearchResultEntry(final SearchResultEntry entry) {
        this.jsonBuffer.clear();
        this.jsonBuffer.beginObject();
        this.jsonBuffer.appendString("result-type", "entry");
        this.jsonBuffer.appendString("dn", entry.getDN());
        this.jsonBuffer.beginArray("attributes");
        for (final Attribute a : entry.getAttributes()) {
            this.jsonBuffer.beginObject();
            this.jsonBuffer.appendString("name", a.getName());
            this.jsonBuffer.beginArray("values");
            for (final String value : a.getValues()) {
                this.jsonBuffer.appendString(value);
            }
            this.jsonBuffer.endArray();
            this.jsonBuffer.endObject();
        }
        this.jsonBuffer.endArray();
        this.handleControls(entry.getControls());
        this.jsonBuffer.endObject();
        this.ldapSearch.writeOut(this.jsonBuffer.toString());
    }
    
    public void formatSearchResultReference(final SearchResultReference ref) {
        this.jsonBuffer.clear();
        this.jsonBuffer.beginObject();
        this.jsonBuffer.appendString("result-type", "reference");
        this.jsonBuffer.beginArray("referral-urls");
        for (final String url : ref.getReferralURLs()) {
            this.jsonBuffer.appendString(url);
        }
        this.jsonBuffer.endArray();
        this.handleControls(ref.getControls());
        this.jsonBuffer.endObject();
        this.ldapSearch.writeOut(this.jsonBuffer.toString());
    }
    
    public void formatResult(final LDAPResult result) {
        this.jsonBuffer.clear();
        this.jsonBuffer.beginObject();
        if (result instanceof SearchResult) {
            this.jsonBuffer.appendString("result-type", "search-result");
        }
        else {
            this.jsonBuffer.appendString("result-type", "ldap-result");
        }
        this.jsonBuffer.appendNumber("result-code", result.getResultCode().intValue());
        this.jsonBuffer.appendString("result-code-name", result.getResultCode().getName());
        final String diagnosticMessage = result.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            this.jsonBuffer.appendString("diagnostic-message", diagnosticMessage);
        }
        final String matchedDN = result.getMatchedDN();
        if (matchedDN != null) {
            this.jsonBuffer.appendString("matched-dn", matchedDN);
        }
        final String[] referralURLs = result.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            this.jsonBuffer.beginArray("referral-urls");
            for (final String url : referralURLs) {
                this.jsonBuffer.appendString(url);
            }
            this.jsonBuffer.endArray();
        }
        if (result instanceof SearchResult) {
            final SearchResult searchResult = (SearchResult)result;
            this.jsonBuffer.appendNumber("entries-returned", searchResult.getEntryCount());
            this.jsonBuffer.appendNumber("references-returned", searchResult.getReferenceCount());
        }
        this.handleControls(result.getResponseControls());
        this.jsonBuffer.endObject();
        this.ldapSearch.writeOut(this.jsonBuffer.toString());
    }
    
    public void formatUnsolicitedNotification(final LDAPConnection connection, final ExtendedResult notification) {
        this.jsonBuffer.clear();
        this.jsonBuffer.beginObject();
        this.jsonBuffer.appendString("result-type", "unsolicited-notification");
        final String oid = notification.getOID();
        if (oid != null) {
            this.jsonBuffer.appendString("oid", oid);
        }
        if (notification.hasValue()) {
            this.jsonBuffer.appendString("base64-encoded-value", Base64.encode(notification.getValue().getValue()));
        }
        this.jsonBuffer.appendNumber("result-code", notification.getResultCode().intValue());
        this.jsonBuffer.appendString("result-code-name", notification.getResultCode().getName());
        final String diagnosticMessage = notification.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            this.jsonBuffer.appendString("diagnostic-message", diagnosticMessage);
        }
        final String matchedDN = notification.getMatchedDN();
        if (matchedDN != null) {
            this.jsonBuffer.appendString("matched-dn", matchedDN);
        }
        final String[] referralURLs = notification.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            this.jsonBuffer.beginArray("referral-urls");
            for (final String url : referralURLs) {
                this.jsonBuffer.appendString(url);
            }
            this.jsonBuffer.endArray();
        }
        this.handleControls(notification.getResponseControls());
        this.formattedLines.clear();
        ResultUtils.formatUnsolicitedNotification(this.formattedLines, notification, false, 0, Integer.MAX_VALUE);
        this.jsonBuffer.beginArray("formatted-unsolicited-notification-lines");
        for (final String line : this.formattedLines) {
            this.jsonBuffer.appendString(line.trim());
        }
        this.jsonBuffer.endArray();
        this.jsonBuffer.endObject();
        this.ldapSearch.writeOut(this.jsonBuffer.toString());
    }
    
    private void handleControls(final Control[] controls) {
        if (controls == null || controls.length == 0) {
            return;
        }
        this.jsonBuffer.beginArray("controls");
        for (final Control c : controls) {
            this.jsonBuffer.beginObject();
            this.jsonBuffer.appendString("oid", c.getOID());
            this.jsonBuffer.appendBoolean("criticality", c.isCritical());
            if (c.hasValue()) {
                this.jsonBuffer.appendString("base64-encoded-value", Base64.encode(c.getValue().getValue()));
            }
            this.formattedLines.clear();
            ResultUtils.formatResponseControl(this.formattedLines, c, false, 0, Integer.MAX_VALUE);
            this.jsonBuffer.beginArray("formatted-control-lines");
            for (final String line : this.formattedLines) {
                this.jsonBuffer.appendString(line.trim());
            }
            this.jsonBuffer.endArray();
            this.jsonBuffer.endObject();
        }
        this.jsonBuffer.endArray();
    }
}
