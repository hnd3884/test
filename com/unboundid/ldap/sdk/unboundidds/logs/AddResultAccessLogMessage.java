package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationRemoteLevel;
import com.unboundid.ldap.sdk.unboundidds.controls.AssuredReplicationLocalLevel;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class AddResultAccessLogMessage extends AddRequestAccessLogMessage implements OperationResultAccessLogMessage
{
    private static final long serialVersionUID = -6660463102516326216L;
    private final AssuredReplicationLocalLevel assuredReplicationLocalLevel;
    private final AssuredReplicationRemoteLevel assuredReplicationRemoteLevel;
    private final Boolean responseDelayedByAssurance;
    private final Boolean uncachedDataAccessed;
    private final Double processingTime;
    private final Double queueTime;
    private final Integer targetPort;
    private final List<String> indexesWithKeysAccessedNearEntryLimit;
    private final List<String> indexesWithKeysAccessedOverEntryLimit;
    private final List<String> missingPrivileges;
    private final List<String> preAuthZUsedPrivileges;
    private final List<String> referralURLs;
    private final List<String> responseControlOIDs;
    private final List<String> serversAccessed;
    private final List<String> usedPrivileges;
    private final Long assuredReplicationTimeoutMillis;
    private final Long intermediateResponsesReturned;
    private final ResultCode resultCode;
    private final String additionalInformation;
    private final String authzDN;
    private final String diagnosticMessage;
    private final String intermediateClientResult;
    private final String matchedDN;
    private final String replicationChangeID;
    private final String targetHost;
    private final String targetProtocol;
    private final String undeleteFromDN;
    
    public AddResultAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public AddResultAccessLogMessage(final LogMessage m) {
        super(m);
        this.diagnosticMessage = this.getNamedValue("message");
        this.additionalInformation = this.getNamedValue("additionalInfo");
        this.matchedDN = this.getNamedValue("matchedDN");
        this.processingTime = this.getNamedValueAsDouble("etime");
        this.queueTime = this.getNamedValueAsDouble("qtime");
        this.intermediateClientResult = this.getNamedValue("from");
        this.authzDN = this.getNamedValue("authzDN");
        this.replicationChangeID = this.getNamedValue("replicationChangeID");
        this.targetHost = this.getNamedValue("targetHost");
        this.targetPort = this.getNamedValueAsInteger("targetPort");
        this.targetProtocol = this.getNamedValue("targetProtocol");
        this.undeleteFromDN = this.getNamedValue("undeleteFromDN");
        this.intermediateResponsesReturned = this.getNamedValueAsLong("intermediateResponsesReturned");
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
        final String localLevelStr = this.getNamedValue("localAssuranceLevel");
        if (localLevelStr == null) {
            this.assuredReplicationLocalLevel = null;
        }
        else {
            this.assuredReplicationLocalLevel = AssuredReplicationLocalLevel.valueOf(localLevelStr);
        }
        final String remoteLevelStr = this.getNamedValue("remoteAssuranceLevel");
        if (remoteLevelStr == null) {
            this.assuredReplicationRemoteLevel = null;
        }
        else {
            this.assuredReplicationRemoteLevel = AssuredReplicationRemoteLevel.valueOf(remoteLevelStr);
        }
        this.assuredReplicationTimeoutMillis = this.getNamedValueAsLong("assuranceTimeoutMillis");
        this.responseDelayedByAssurance = this.getNamedValueAsBoolean("responseDelayedByAssurance");
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
        final String indexesNearLimitStr = this.getNamedValue("indexesWithKeysAccessedNearEntryLimit");
        if (indexesNearLimitStr == null || indexesNearLimitStr.isEmpty()) {
            this.indexesWithKeysAccessedNearEntryLimit = Collections.emptyList();
        }
        else {
            final LinkedList<String> indexes = new LinkedList<String>();
            final StringTokenizer tokenizer5 = new StringTokenizer(indexesNearLimitStr, ",");
            while (tokenizer5.hasMoreTokens()) {
                indexes.add(tokenizer5.nextToken());
            }
            this.indexesWithKeysAccessedNearEntryLimit = Collections.unmodifiableList((List<? extends String>)indexes);
        }
        final String indexesOverLimitStr = this.getNamedValue("indexesWithKeysAccessedExceedingEntryLimit");
        if (indexesOverLimitStr == null || indexesOverLimitStr.isEmpty()) {
            this.indexesWithKeysAccessedOverEntryLimit = Collections.emptyList();
        }
        else {
            final LinkedList<String> indexes2 = new LinkedList<String>();
            final StringTokenizer tokenizer6 = new StringTokenizer(indexesOverLimitStr, ",");
            while (tokenizer6.hasMoreTokens()) {
                indexes2.add(tokenizer6.nextToken());
            }
            this.indexesWithKeysAccessedOverEntryLimit = Collections.unmodifiableList((List<? extends String>)indexes2);
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
    public Long getIntermediateResponsesReturned() {
        return this.intermediateResponsesReturned;
    }
    
    @Override
    public Double getProcessingTimeMillis() {
        return this.processingTime;
    }
    
    @Override
    public Double getQueueTimeMillis() {
        return this.queueTime;
    }
    
    @Override
    public List<String> getResponseControlOIDs() {
        return this.responseControlOIDs;
    }
    
    @Override
    public List<String> getServersAccessed() {
        return this.serversAccessed;
    }
    
    public Boolean getUncachedDataAccessed() {
        return this.uncachedDataAccessed;
    }
    
    @Override
    public String getIntermediateClientResult() {
        return this.intermediateClientResult;
    }
    
    public String getAlternateAuthorizationDN() {
        return this.authzDN;
    }
    
    public String getReplicationChangeID() {
        return this.replicationChangeID;
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
    
    public String getUndeleteFromDN() {
        return this.undeleteFromDN;
    }
    
    public AssuredReplicationLocalLevel getAssuredReplicationLocalLevel() {
        return this.assuredReplicationLocalLevel;
    }
    
    public AssuredReplicationRemoteLevel getAssuredReplicationRemoteLevel() {
        return this.assuredReplicationRemoteLevel;
    }
    
    public Long getAssuredReplicationTimeoutMillis() {
        return this.assuredReplicationTimeoutMillis;
    }
    
    public Boolean getResponseDelayedByAssurance() {
        return this.responseDelayedByAssurance;
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
    
    public List<String> getIndexesWithKeysAccessedNearEntryLimit() {
        return this.indexesWithKeysAccessedNearEntryLimit;
    }
    
    public List<String> getIndexesWithKeysAccessedOverEntryLimit() {
        return this.indexesWithKeysAccessedOverEntryLimit;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.RESULT;
    }
}
