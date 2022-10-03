package com.unboundid.util.args;

import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ScopeArgument extends Argument
{
    private static final Map<String, SearchScope> SCOPE_STRINGS;
    private static final long serialVersionUID = 5962857448814911423L;
    private final AtomicReference<SearchScope> value;
    private final SearchScope defaultValue;
    
    public ScopeArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, null, description);
    }
    
    public ScopeArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, valuePlaceholder, description, null);
    }
    
    public ScopeArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final String valuePlaceholder, final String description, final SearchScope defaultValue) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, 1, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_SCOPE.get() : valuePlaceholder, description);
        this.defaultValue = defaultValue;
        this.value = new AtomicReference<SearchScope>();
    }
    
    private ScopeArgument(final ScopeArgument source) {
        super(source);
        this.defaultValue = source.defaultValue;
        this.value = new AtomicReference<SearchScope>();
    }
    
    public SearchScope getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        final SearchScope scope = ScopeArgument.SCOPE_STRINGS.get(StaticUtils.toLowerCase(valueString));
        if (scope == null) {
            throw new ArgumentException(ArgsMessages.ERR_SCOPE_VALUE_NOT_VALID.get(valueString, this.getIdentifierString()));
        }
        if (!this.value.compareAndSet(null, scope)) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
    }
    
    public SearchScope getValue() {
        final SearchScope s = this.value.get();
        if (s == null) {
            return this.defaultValue;
        }
        return s;
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        SearchScope s = this.value.get();
        if (useDefault && s == null) {
            s = this.defaultValue;
        }
        if (s == null) {
            return Collections.emptyList();
        }
        String scopeStr = null;
        switch (s.intValue()) {
            case 0: {
                scopeStr = "base";
                break;
            }
            case 1: {
                scopeStr = "one";
                break;
            }
            case 2: {
                scopeStr = "sub";
                break;
            }
            case 3: {
                scopeStr = "subordinates";
                break;
            }
            default: {
                scopeStr = s.getName();
                break;
            }
        }
        return Collections.singletonList(scopeStr);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValue != null;
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_SCOPE_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_SCOPE_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.value.set(null);
    }
    
    @Override
    public ScopeArgument getCleanCopy() {
        return new ScopeArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        final SearchScope s = this.value.get();
        if (s != null) {
            if (this.isSensitive()) {
                argStrings.add(this.getIdentifierString());
                argStrings.add("***REDACTED***");
                return;
            }
            switch (s.intValue()) {
                case 0: {
                    argStrings.add(this.getIdentifierString());
                    argStrings.add("base");
                    break;
                }
                case 1: {
                    argStrings.add(this.getIdentifierString());
                    argStrings.add("one");
                    break;
                }
                case 2: {
                    argStrings.add(this.getIdentifierString());
                    argStrings.add("sub");
                    break;
                }
                case 3: {
                    argStrings.add(this.getIdentifierString());
                    argStrings.add("subordinates");
                    break;
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ScopeArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValue != null) {
            buffer.append(", defaultValue='");
            switch (this.defaultValue.intValue()) {
                case 0: {
                    buffer.append("base");
                    break;
                }
                case 1: {
                    buffer.append("one");
                    break;
                }
                case 2: {
                    buffer.append("sub");
                    break;
                }
                case 3: {
                    buffer.append("subordinate");
                    break;
                }
                default: {
                    buffer.append(this.defaultValue.intValue());
                    break;
                }
            }
            buffer.append('\'');
        }
        buffer.append(')');
    }
    
    static {
        final HashMap<String, SearchScope> scopeMap = new HashMap<String, SearchScope>(StaticUtils.computeMapCapacity(21));
        scopeMap.put("base", SearchScope.BASE);
        scopeMap.put("baseobject", SearchScope.BASE);
        scopeMap.put("base-object", SearchScope.BASE);
        scopeMap.put("0", SearchScope.BASE);
        scopeMap.put("one", SearchScope.ONE);
        scopeMap.put("singlelevel", SearchScope.ONE);
        scopeMap.put("single-level", SearchScope.ONE);
        scopeMap.put("onelevel", SearchScope.ONE);
        scopeMap.put("one-level", SearchScope.ONE);
        scopeMap.put("1", SearchScope.ONE);
        scopeMap.put("sub", SearchScope.SUB);
        scopeMap.put("subtree", SearchScope.SUB);
        scopeMap.put("wholesubtree", SearchScope.SUB);
        scopeMap.put("whole-subtree", SearchScope.SUB);
        scopeMap.put("2", SearchScope.SUB);
        scopeMap.put("subord", SearchScope.SUBORDINATE_SUBTREE);
        scopeMap.put("subordinate", SearchScope.SUBORDINATE_SUBTREE);
        scopeMap.put("subordinates", SearchScope.SUBORDINATE_SUBTREE);
        scopeMap.put("subordinatesubtree", SearchScope.SUBORDINATE_SUBTREE);
        scopeMap.put("subordinate-subtree", SearchScope.SUBORDINATE_SUBTREE);
        scopeMap.put("3", SearchScope.SUBORDINATE_SUBTREE);
        SCOPE_STRINGS = Collections.unmodifiableMap((Map<? extends String, ? extends SearchScope>)scopeMap);
    }
}
