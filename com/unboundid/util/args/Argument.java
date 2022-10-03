package com.unboundid.util.args;

import com.unboundid.util.LDAPSDKUsageException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public abstract class Argument implements Serializable
{
    private static final long serialVersionUID = -6938320885602903919L;
    private boolean isHidden;
    private boolean isRegistered;
    private final boolean isRequired;
    private boolean isSensitive;
    private boolean isUsageArgument;
    private int maxOccurrences;
    private int numOccurrences;
    private final Map<Character, Boolean> shortIdentifiers;
    private final Map<String, Boolean> longIdentifiers;
    private String argumentGroupName;
    private final String description;
    private final String valuePlaceholder;
    
    protected Argument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        if (description == null) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_DESCRIPTION_NULL.get());
        }
        if (shortIdentifier == null && longIdentifier == null) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_NO_IDENTIFIERS.get());
        }
        this.shortIdentifiers = new LinkedHashMap<Character, Boolean>(StaticUtils.computeMapCapacity(5));
        if (shortIdentifier != null) {
            this.shortIdentifiers.put(shortIdentifier, false);
        }
        this.longIdentifiers = new LinkedHashMap<String, Boolean>(StaticUtils.computeMapCapacity(5));
        if (longIdentifier != null) {
            this.longIdentifiers.put(longIdentifier, false);
        }
        this.isRequired = isRequired;
        this.valuePlaceholder = valuePlaceholder;
        this.description = description;
        if (maxOccurrences > 0) {
            this.maxOccurrences = maxOccurrences;
        }
        else {
            this.maxOccurrences = Integer.MAX_VALUE;
        }
        this.argumentGroupName = null;
        this.numOccurrences = 0;
        this.isHidden = false;
        this.isRegistered = false;
        this.isSensitive = false;
        this.isUsageArgument = false;
    }
    
    protected Argument(final Argument source) {
        this.argumentGroupName = source.argumentGroupName;
        this.isHidden = source.isHidden;
        this.isRequired = source.isRequired;
        this.isSensitive = source.isSensitive;
        this.isUsageArgument = source.isUsageArgument;
        this.maxOccurrences = source.maxOccurrences;
        this.description = source.description;
        this.valuePlaceholder = source.valuePlaceholder;
        this.isRegistered = false;
        this.numOccurrences = 0;
        this.shortIdentifiers = new LinkedHashMap<Character, Boolean>(source.shortIdentifiers);
        this.longIdentifiers = new LinkedHashMap<String, Boolean>(source.longIdentifiers);
    }
    
    public final boolean hasShortIdentifier() {
        return !this.shortIdentifiers.isEmpty();
    }
    
    public final Character getShortIdentifier() {
        for (final Map.Entry<Character, Boolean> e : this.shortIdentifiers.entrySet()) {
            if (e.getValue()) {
                continue;
            }
            return e.getKey();
        }
        return null;
    }
    
    public final List<Character> getShortIdentifiers() {
        return this.getShortIdentifiers(true);
    }
    
    public final List<Character> getShortIdentifiers(final boolean includeHidden) {
        final ArrayList<Character> identifierList = new ArrayList<Character>(this.shortIdentifiers.size());
        for (final Map.Entry<Character, Boolean> e : this.shortIdentifiers.entrySet()) {
            if (includeHidden || !e.getValue()) {
                identifierList.add(e.getKey());
            }
        }
        return Collections.unmodifiableList((List<? extends Character>)identifierList);
    }
    
    public final void addShortIdentifier(final Character c) throws ArgumentException {
        this.addShortIdentifier(c, false);
    }
    
    public final void addShortIdentifier(final Character c, final boolean isHidden) throws ArgumentException {
        if (this.isRegistered) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_ID_CHANGE_AFTER_REGISTERED.get(this.getIdentifierString()));
        }
        this.shortIdentifiers.put(c, isHidden);
    }
    
    public final boolean hasLongIdentifier() {
        return !this.longIdentifiers.isEmpty();
    }
    
    public final String getLongIdentifier() {
        for (final Map.Entry<String, Boolean> e : this.longIdentifiers.entrySet()) {
            if (e.getValue()) {
                continue;
            }
            return e.getKey();
        }
        return null;
    }
    
    public final List<String> getLongIdentifiers() {
        return this.getLongIdentifiers(true);
    }
    
    public final List<String> getLongIdentifiers(final boolean includeHidden) {
        final ArrayList<String> identifierList = new ArrayList<String>(this.longIdentifiers.size());
        for (final Map.Entry<String, Boolean> e : this.longIdentifiers.entrySet()) {
            if (includeHidden || !e.getValue()) {
                identifierList.add(e.getKey());
            }
        }
        return Collections.unmodifiableList((List<? extends String>)identifierList);
    }
    
    public final void addLongIdentifier(final String s) throws ArgumentException {
        this.addLongIdentifier(s, false);
    }
    
    public final void addLongIdentifier(final String s, final boolean isHidden) throws ArgumentException {
        if (this.isRegistered) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_ID_CHANGE_AFTER_REGISTERED.get(this.getIdentifierString()));
        }
        this.longIdentifiers.put(s, isHidden);
    }
    
    public final String getIdentifierString() {
        for (final Map.Entry<String, Boolean> e : this.longIdentifiers.entrySet()) {
            if (!e.getValue()) {
                return "--" + e.getKey();
            }
        }
        for (final Map.Entry<Character, Boolean> e2 : this.shortIdentifiers.entrySet()) {
            if (!e2.getValue()) {
                return "-" + e2.getKey();
            }
        }
        throw new LDAPSDKUsageException(ArgsMessages.ERR_ARG_NO_NON_HIDDEN_IDENTIFIER.get(this.toString()));
    }
    
    public final boolean isRequired() {
        return this.isRequired;
    }
    
    public final int getMaxOccurrences() {
        return this.maxOccurrences;
    }
    
    public final void setMaxOccurrences(final int maxOccurrences) {
        if (maxOccurrences <= 0) {
            this.maxOccurrences = Integer.MAX_VALUE;
        }
        else {
            this.maxOccurrences = maxOccurrences;
        }
    }
    
    public boolean takesValue() {
        return this.valuePlaceholder != null;
    }
    
    public final String getValuePlaceholder() {
        return this.valuePlaceholder;
    }
    
    public abstract List<String> getValueStringRepresentations(final boolean p0);
    
    public final String getDescription() {
        return this.description;
    }
    
    public final String getArgumentGroupName() {
        return this.argumentGroupName;
    }
    
    public final void setArgumentGroupName(final String argumentGroupName) {
        this.argumentGroupName = argumentGroupName;
    }
    
    public final boolean isHidden() {
        return this.isHidden;
    }
    
    public final void setHidden(final boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public final boolean isUsageArgument() {
        return this.isUsageArgument;
    }
    
    public final void setUsageArgument(final boolean isUsageArgument) {
        this.isUsageArgument = isUsageArgument;
    }
    
    public final boolean isPresent() {
        return this.numOccurrences > 0 || this.hasDefaultValue();
    }
    
    public final int getNumOccurrences() {
        return this.numOccurrences;
    }
    
    final void incrementOccurrences() throws ArgumentException {
        if (this.numOccurrences >= this.maxOccurrences) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        ++this.numOccurrences;
    }
    
    protected abstract void addValue(final String p0) throws ArgumentException;
    
    protected abstract boolean hasDefaultValue();
    
    public final boolean isSensitive() {
        return this.isSensitive;
    }
    
    public final void setSensitive(final boolean isSensitive) {
        this.isSensitive = isSensitive;
    }
    
    boolean isRegistered() {
        return this.isRegistered;
    }
    
    void setRegistered() throws ArgumentException {
        if (this.isRegistered) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_ALREADY_REGISTERED.get(this.getIdentifierString()));
        }
        this.isRegistered = true;
    }
    
    public abstract String getDataTypeName();
    
    public String getValueConstraints() {
        return null;
    }
    
    protected void reset() {
        this.numOccurrences = 0;
    }
    
    public abstract Argument getCleanCopy();
    
    protected abstract void addToCommandLine(final List<String> p0);
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
    
    protected void appendBasicToStringInfo(final StringBuilder buffer) {
        switch (this.shortIdentifiers.size()) {
            case 0: {
                break;
            }
            case 1: {
                buffer.append("shortIdentifier='-");
                buffer.append(this.shortIdentifiers.keySet().iterator().next());
                buffer.append('\'');
                break;
            }
            default: {
                buffer.append("shortIdentifiers={");
                final Iterator<Character> iterator = this.shortIdentifiers.keySet().iterator();
                while (iterator.hasNext()) {
                    buffer.append("'-");
                    buffer.append(iterator.next());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
                break;
            }
        }
        if (!this.shortIdentifiers.isEmpty()) {
            buffer.append(", ");
        }
        switch (this.longIdentifiers.size()) {
            case 0: {
                break;
            }
            case 1: {
                buffer.append("longIdentifier='--");
                buffer.append(this.longIdentifiers.keySet().iterator().next());
                buffer.append('\'');
                break;
            }
            default: {
                buffer.append("longIdentifiers={");
                final Iterator<String> iterator2 = this.longIdentifiers.keySet().iterator();
                while (iterator2.hasNext()) {
                    buffer.append("'--");
                    buffer.append(iterator2.next());
                    buffer.append('\'');
                    if (iterator2.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
                break;
            }
        }
        buffer.append(", description='");
        buffer.append(this.description);
        if (this.argumentGroupName != null) {
            buffer.append("', argumentGroup='");
            buffer.append(this.argumentGroupName);
        }
        buffer.append("', isRequired=");
        buffer.append(this.isRequired);
        buffer.append(", maxOccurrences=");
        if (this.maxOccurrences == 0) {
            buffer.append("unlimited");
        }
        else {
            buffer.append(this.maxOccurrences);
        }
        if (this.valuePlaceholder == null) {
            buffer.append(", takesValue=false");
        }
        else {
            buffer.append(", takesValue=true, valuePlaceholder='");
            buffer.append(this.valuePlaceholder);
            buffer.append('\'');
        }
        if (this.isHidden) {
            buffer.append(", isHidden=true");
        }
    }
}
