package com.unboundid.util.args;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.DN;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ProhibitDNInSubtreeArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = 171827460774234825L;
    private final List<DN> baseDNs;
    
    public ProhibitDNInSubtreeArgumentValueValidator(final DN... baseDNs) {
        this(StaticUtils.toList(baseDNs));
    }
    
    public ProhibitDNInSubtreeArgumentValueValidator(final Collection<DN> baseDNs) {
        Validator.ensureNotNull(baseDNs);
        Validator.ensureFalse(baseDNs.isEmpty());
        this.baseDNs = Collections.unmodifiableList((List<? extends DN>)new ArrayList<DN>(baseDNs));
    }
    
    public List<DN> getBaseDNs() {
        return this.baseDNs;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        DN dn;
        try {
            dn = new DN(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_PROHIBIT_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_DN.get(valueString, argument.getIdentifierString()), e);
        }
        for (final DN baseDN : this.baseDNs) {
            if (dn.isDescendantOf(baseDN, true)) {
                throw new ArgumentException(ArgsMessages.ERR_PROHIBIT_DN_IN_SUBTREE_VALIDATOR_VALUE_IN_SUBTREE.get(valueString, argument.getIdentifierString(), String.valueOf(baseDN)));
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("ProhibitDNInSubtreeArgumentValueValidator(baseDNs={");
        final Iterator<DN> iterator = this.baseDNs.iterator();
        while (iterator.hasNext()) {
            buffer.append('\'');
            buffer.append(iterator.next().toString());
            buffer.append('\'');
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
