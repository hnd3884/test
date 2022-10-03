package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

final class PolicyScope
{
    private static final PolicyLogger LOGGER;
    private final List<PolicySubject> subjects;
    
    PolicyScope(final List<PolicySubject> initialSubjects) {
        this.subjects = new LinkedList<PolicySubject>();
        if (initialSubjects != null && !initialSubjects.isEmpty()) {
            this.subjects.addAll(initialSubjects);
        }
    }
    
    void attach(final PolicySubject subject) {
        if (subject == null) {
            throw PolicyScope.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0020_SUBJECT_PARAM_MUST_NOT_BE_NULL()));
        }
        this.subjects.add(subject);
    }
    
    void dettachAllSubjects() {
        this.subjects.clear();
    }
    
    Policy getEffectivePolicy(final PolicyMerger merger) throws PolicyException {
        final LinkedList<Policy> policies = new LinkedList<Policy>();
        for (final PolicySubject subject : this.subjects) {
            policies.add(subject.getEffectivePolicy(merger));
        }
        return merger.merge(policies);
    }
    
    Collection<PolicySubject> getPolicySubjects() {
        return this.subjects;
    }
    
    @Override
    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }
    
    StringBuffer toString(final int indentLevel, final StringBuffer buffer) {
        final String indent = PolicyUtils.Text.createIndent(indentLevel);
        buffer.append(indent).append("policy scope {").append(PolicyUtils.Text.NEW_LINE);
        for (final PolicySubject policySubject : this.subjects) {
            policySubject.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
        }
        buffer.append(indent).append('}');
        return buffer;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyScope.class);
    }
}
