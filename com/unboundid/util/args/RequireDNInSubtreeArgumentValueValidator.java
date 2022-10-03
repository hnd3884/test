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
public final class RequireDNInSubtreeArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = -4517307608327628921L;
    private final List<DN> baseDNs;
    
    public RequireDNInSubtreeArgumentValueValidator(final DN... baseDNs) {
        this(StaticUtils.toList(baseDNs));
    }
    
    public RequireDNInSubtreeArgumentValueValidator(final Collection<DN> baseDNs) {
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
            throw new ArgumentException(ArgsMessages.ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_DN.get(valueString, argument.getIdentifierString()), e);
        }
        if (this.baseDNs.size() != 1) {
            final StringBuilder dnList = new StringBuilder();
            final Iterator<DN> iterator = this.baseDNs.iterator();
            while (iterator.hasNext()) {
                final DN baseDN = iterator.next();
                if (dn.isDescendantOf(baseDN, true)) {
                    return;
                }
                dnList.append('\'');
                dnList.append(baseDN);
                dnList.append('\'');
                if (!iterator.hasNext()) {
                    continue;
                }
                dnList.append(", ");
            }
            throw new ArgumentException(ArgsMessages.ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_IN_SUBTREES.get(valueString, argument.getIdentifierString(), dnList.toString()));
        }
        if (!dn.isDescendantOf(this.baseDNs.get(0), true)) {
            throw new ArgumentException(ArgsMessages.ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_IN_SUBTREE.get(valueString, argument.getIdentifierString(), String.valueOf(this.baseDNs.get(0))));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("RequireDNInSubtreeArgumentValueValidator(baseDNs={");
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
