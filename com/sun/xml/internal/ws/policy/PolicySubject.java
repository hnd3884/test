package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.LinkedList;
import java.util.List;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class PolicySubject
{
    private static final PolicyLogger LOGGER;
    private final List<Policy> policies;
    private final Object subject;
    
    public PolicySubject(final Object subject, final Policy policy) throws IllegalArgumentException {
        this.policies = new LinkedList<Policy>();
        if (subject == null || policy == null) {
            throw PolicySubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0021_SUBJECT_AND_POLICY_PARAM_MUST_NOT_BE_NULL(subject, policy)));
        }
        this.subject = subject;
        this.attach(policy);
    }
    
    public PolicySubject(final Object subject, final Collection<Policy> policies) throws IllegalArgumentException {
        this.policies = new LinkedList<Policy>();
        if (subject == null || policies == null) {
            throw PolicySubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
        }
        if (policies.isEmpty()) {
            throw PolicySubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0064_INITIAL_POLICY_COLLECTION_MUST_NOT_BE_EMPTY()));
        }
        this.subject = subject;
        this.policies.addAll(policies);
    }
    
    public void attach(final Policy policy) {
        if (policy == null) {
            throw PolicySubject.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0038_POLICY_TO_ATTACH_MUST_NOT_BE_NULL()));
        }
        this.policies.add(policy);
    }
    
    public Policy getEffectivePolicy(final PolicyMerger merger) throws PolicyException {
        return merger.merge(this.policies);
    }
    
    public Object getSubject() {
        return this.subject;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        final String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append("policy subject {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("subject = '").append(this.subject).append('\'').append(PolicyUtils.Text.NEW_LINE);
        for (final Policy policy : this.policies) {
            policy.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicySubject.class);
    }
}
