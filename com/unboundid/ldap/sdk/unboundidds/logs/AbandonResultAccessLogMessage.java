package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AbandonResultAccessLogMessage extends AbandonRequestAccessLogMessage implements MinimalOperationResultAccessLogMessage
{
    private static final long serialVersionUID = 6714469240483228080L;
    private final Boolean uncachedDataAccessed;
    private final Double processingTime;
    private final Double queueTime;
    private final List<String> missingPrivileges;
    private final List<String> preAuthZUsedPrivileges;
    private final List<String> referralURLs;
    private final List<String> responseControlOIDs;
    private final List<String> serversAccessed;
    private final List<String> usedPrivileges;
    private final ResultCode resultCode;
    private final String additionalInformation;
    private final String diagnosticMessage;
    private final String intermediateClientResult;
    private final String matchedDN;
    private final Integer targetPort;
    private final String targetHost;
    private final String targetProtocol;
    
    public AbandonResultAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public AbandonResultAccessLogMessage(final LogMessage m) {
        super(m);
        this.diagnosticMessage = this.getNamedValue("message");
        this.additionalInformation = this.getNamedValue("additionalInfo");
        this.matchedDN = this.getNamedValue("matchedDN");
        this.processingTime = this.getNamedValueAsDouble("etime");
        this.queueTime = this.getNamedValueAsDouble("qtime");
        this.intermediateClientResult = this.getNamedValue("from");
        this.targetHost = this.getNamedValue("targetHost");
        this.targetPort = this.getNamedValueAsInteger("targetPort");
        this.targetProtocol = this.getNamedValue("targetProtocol");
        final Integer rcInteger = this.getNamedValueAsInteger("resultCode");
        if (rcInteger == null) {
            this.resultCode = null;
        }
        else {
            this.resultCode = ResultCode.valueOf(rcInteger);
        }
        final String refStr = this.getNamedValue("referralURLs");
        if (refStr == null || refStr.isEmpty()) {
            this.referralURLs = Collections.emptyList();
        }
        else {
            final LinkedList<String> refs = new LinkedList<String>();
            int startPos = 0;
            while (true) {
                final int commaPos = refStr.indexOf(",ldap", startPos);
                if (commaPos < 0) {
                    break;
                }
                refs.add(refStr.substring(startPos, commaPos));
                startPos = commaPos + 1;
            }
            refs.add(refStr.substring(startPos));
            this.referralURLs = Collections.unmodifiableList((List<? extends String>)refs);
        }
        final String controlStr = this.getNamedValue("responseControls");
        if (controlStr == null) {
            this.responseControlOIDs = Collections.emptyList();
        }
        else {
            final LinkedList<String> controlList = new LinkedList<String>();
            final StringTokenizer t = new StringTokenizer(controlStr, ",");
            while (t.hasMoreTokens()) {
                controlList.add(t.nextToken());
            }
            this.responseControlOIDs = Collections.unmodifiableList((List<? extends String>)controlList);
        }
        final String serversAccessedStr = this.getNamedValue("serversAccessed");
        if (serversAccessedStr == null || serversAccessedStr.isEmpty()) {
            this.serversAccessed = Collections.emptyList();
        }
        else {
            final LinkedList<String> servers = new LinkedList<String>();
            final StringTokenizer tokenizer = new StringTokenizer(serversAccessedStr, ",");
            while (tokenizer.hasMoreTokens()) {
                servers.add(tokenizer.nextToken());
            }
            this.serversAccessed = Collections.unmodifiableList((List<? extends String>)servers);
        }
        this.uncachedDataAccessed = this.getNamedValueAsBoolean("uncachedDataAccessed");
        final String usedPrivilegesStr = this.getNamedValue("usedPrivileges");
        if (usedPrivilegesStr == null || usedPrivilegesStr.isEmpty()) {
            this.usedPrivileges = Collections.emptyList();
        }
        else {
            final LinkedList<String> privileges = new LinkedList<String>();
            final StringTokenizer tokenizer2 = new StringTokenizer(usedPrivilegesStr, ",");
            while (tokenizer2.hasMoreTokens()) {
                privileges.add(tokenizer2.nextToken());
            }
            this.usedPrivileges = Collections.unmodifiableList((List<? extends String>)privileges);
        }
        final String preAuthZUsedPrivilegesStr = this.getNamedValue("preAuthZUsedPrivileges");
        if (preAuthZUsedPrivilegesStr == null || preAuthZUsedPrivilegesStr.isEmpty()) {
            this.preAuthZUsedPrivileges = Collections.emptyList();
        }
        else {
            final LinkedList<String> privileges2 = new LinkedList<String>();
            final StringTokenizer tokenizer3 = new StringTokenizer(preAuthZUsedPrivilegesStr, ",");
            while (tokenizer3.hasMoreTokens()) {
                privileges2.add(tokenizer3.nextToken());
            }
            this.preAuthZUsedPrivileges = Collections.unmodifiableList((List<? extends String>)privileges2);
        }
        final String missingPrivilegesStr = this.getNamedValue("missingPrivileges");
        if (missingPrivilegesStr == null || missingPrivilegesStr.isEmpty()) {
            this.missingPrivileges = Collections.emptyList();
        }
        else {
            final LinkedList<String> privileges3 = new LinkedList<String>();
            final StringTokenizer tokenizer4 = new StringTokenizer(missingPrivilegesStr, ",");
            while (tokenizer4.hasMoreTokens()) {
                privileges3.add(tokenizer4.nextToken());
            }
            this.missingPrivileges = Collections.unmodifiableList((List<? extends String>)privileges3);
        }
    }
    
    @Override
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    @Override
    public String getDiagnosticMessage() {
        return this.diagnosticMessage;
    }
    
    @Override
    public String getAdditionalInformation() {
        return this.additionalInformation;
    }
    
    @Override
    public String getMatchedDN() {
        return this.matchedDN;
    }
    
    @Override
    public List<String> getReferralURLs() {
        return this.referralURLs;
    }
    
    @Override
    public Double getProcessingTimeMillis() {
        return this.processingTime;
    }
    
    @Override
    public Double getQueueTimeMillis() {
        return this.queueTime;
    }
    
    public List<String> getResponseControlOIDs() {
        return this.responseControlOIDs;
    }
    
    public List<String> getServersAccessed() {
        return this.serversAccessed;
    }
    
    public Boolean getUncachedDataAccessed() {
        return this.uncachedDataAccessed;
    }
    
    public String getIntermediateClientResult() {
        return this.intermediateClientResult;
    }
    
    public String getTargetHost() {
        return this.targetHost;
    }
    
    public Integer getTargetPort() {
        return this.targetPort;
    }
    
    public String getTargetProtocol() {
        return this.targetProtocol;
    }
    
    public List<String> getUsedPrivileges() {
        return this.usedPrivileges;
    }
    
    public List<String> getPreAuthorizationUsedPrivileges() {
        return this.preAuthZUsedPrivileges;
    }
    
    public List<String> getMissingPrivileges() {
        return this.missingPrivileges;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.RESULT;
    }
}
