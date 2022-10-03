package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

final class PolicyReferenceData
{
    private static final PolicyLogger LOGGER;
    private static final URI DEFAULT_DIGEST_ALGORITHM_URI;
    private static final URISyntaxException CLASS_INITIALIZATION_EXCEPTION;
    private final URI referencedModelUri;
    private final String digest;
    private final URI digestAlgorithmUri;
    
    public PolicyReferenceData(final URI referencedModelUri) {
        this.referencedModelUri = referencedModelUri;
        this.digest = null;
        this.digestAlgorithmUri = null;
    }
    
    public PolicyReferenceData(final URI referencedModelUri, final String expectedDigest, final URI usedDigestAlgorithm) {
        if (PolicyReferenceData.CLASS_INITIALIZATION_EXCEPTION != null) {
            throw PolicyReferenceData.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0015_UNABLE_TO_INSTANTIATE_DIGEST_ALG_URI_FIELD(), PolicyReferenceData.CLASS_INITIALIZATION_EXCEPTION));
        }
        if (usedDigestAlgorithm != null && expectedDigest == null) {
            throw PolicyReferenceData.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0072_DIGEST_MUST_NOT_BE_NULL_WHEN_ALG_DEFINED()));
        }
        this.referencedModelUri = referencedModelUri;
        if (expectedDigest == null) {
            this.digest = null;
            this.digestAlgorithmUri = null;
        }
        else {
            this.digest = expectedDigest;
            if (usedDigestAlgorithm == null) {
                this.digestAlgorithmUri = PolicyReferenceData.DEFAULT_DIGEST_ALGORITHM_URI;
            }
            else {
                this.digestAlgorithmUri = usedDigestAlgorithm;
            }
        }
    }
    
    public URI getReferencedModelUri() {
        return this.referencedModelUri;
    }
    
    public String getDigest() {
        return this.digest;
    }
    
    public URI getDigestAlgorithmUri() {
        return this.digestAlgorithmUri;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    public StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append("reference data {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("referenced policy model URI = '").append(this.referencedModelUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
        if (this.digest == null) {
            buffer.append(innerIndent).append("no digest specified").append(PolicyUtils.Text.NEW_LINE);
        }
        else {
            buffer.append(innerIndent).append("digest algorith URI = '").append(this.digestAlgorithmUri).append('\'').append(PolicyUtils.Text.NEW_LINE);
            buffer.append(innerIndent).append("digest = '").append(this.digest).append('\'').append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyReferenceData.class);
        URISyntaxException tempEx = null;
        URI tempUri = null;
        try {
            tempUri = new URI("http://schemas.xmlsoap.org/ws/2004/09/policy/Sha1Exc");
        }
        catch (final URISyntaxException e) {
            tempEx = e;
        }
        finally {
            DEFAULT_DIGEST_ALGORITHM_URI = tempUri;
            CLASS_INITIALIZATION_EXCEPTION = tempEx;
        }
    }
}
