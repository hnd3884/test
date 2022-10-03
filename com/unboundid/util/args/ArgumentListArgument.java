package com.unboundid.util.args;

import java.util.Iterator;
import java.util.Collections;
import java.text.ParseException;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ArgumentListArgument extends Argument
{
    private static final long serialVersionUID = 1926330851837348378L;
    private final ArgumentParser parser;
    private final List<ArgumentParser> values;
    private final List<String> valueStrings;
    
    public ArgumentListArgument(final Character shortIdentifier, final String longIdentifier, final String description, final ArgumentParser parser) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description, parser);
    }
    
    public ArgumentListArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final ArgumentParser parser) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_ARGS.get() : valuePlaceholder, description);
        this.parser = parser.getCleanCopy();
        this.values = new ArrayList<ArgumentParser>(10);
        this.valueStrings = new ArrayList<String>(10);
    }
    
    private ArgumentListArgument(final ArgumentListArgument source) {
        super(source);
        this.parser = source.parser;
        this.values = new ArrayList<ArgumentParser>(10);
        this.valueStrings = new ArrayList<String>(10);
    }
    
    public ArgumentParser getCleanParser() {
        return this.parser.getCleanCopy();
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        List<String> argList;
        try {
            argList = StaticUtils.toArgumentList(valueString);
        }
        catch (final ParseException pe) {
            Debug.debugException(pe);
            throw new ArgumentException(ArgsMessages.ERR_ARG_LIST_MALFORMED_VALUE.get(valueString, this.getIdentifierString(), pe.getMessage()), pe);
        }
        final String[] args = new String[argList.size()];
        argList.toArray(args);
        final ArgumentParser p = this.parser.getCleanCopy();
        try {
            p.parse(args);
        }
        catch (final ArgumentException ae) {
            Debug.debugException(ae);
            throw new ArgumentException(ArgsMessages.ERR_ARG_LIST_INVALID_VALUE.get(valueString, this.getIdentifierString(), ae.getMessage()), ae);
        }
        this.values.add(p);
        this.valueStrings.add(valueString);
    }
    
    public List<ArgumentParser> getValueParsers() {
        return Collections.unmodifiableList((List<? extends ArgumentParser>)this.values);
    }
    
    public List<String> getValueStrings() {
        return Collections.unmodifiableList((List<? extends String>)this.valueStrings);
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        return Collections.unmodifiableList((List<? extends String>)this.valueStrings);
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return false;
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_ARG_LIST_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_ARG_LIST_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public ArgumentListArgument getCleanCopy() {
        return new ArgumentListArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.valueStrings != null) {
            for (final String s : this.valueStrings) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(s);
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ArgumentListArgument(");
        this.appendBasicToStringInfo(buffer);
        buffer.append(", parser=");
        this.parser.toString(buffer);
        buffer.append(')');
    }
}
