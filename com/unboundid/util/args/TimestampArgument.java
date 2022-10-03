package com.unboundid.util.args;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.unboundid.util.StaticUtils;
import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.util.ObjectPair;
import java.util.Date;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class TimestampArgument extends Argument
{
    private static final long serialVersionUID = -4842934851103696096L;
    private final List<ArgumentValueValidator> validators;
    private final List<Date> defaultValues;
    private final List<ObjectPair<Date, String>> values;
    
    public TimestampArgument(final Character shortIdentifier, final String longIdentifier, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, false, 1, null, description);
    }
    
    public TimestampArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (List<Date>)null);
    }
    
    public TimestampArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final Date defaultValue) throws ArgumentException {
        this(shortIdentifier, longIdentifier, isRequired, maxOccurrences, valuePlaceholder, description, (defaultValue == null) ? null : Collections.singletonList(defaultValue));
    }
    
    public TimestampArgument(final Character shortIdentifier, final String longIdentifier, final boolean isRequired, final int maxOccurrences, final String valuePlaceholder, final String description, final List<Date> defaultValues) throws ArgumentException {
        super(shortIdentifier, longIdentifier, isRequired, maxOccurrences, (valuePlaceholder == null) ? ArgsMessages.INFO_PLACEHOLDER_TIMESTAMP.get() : valuePlaceholder, description);
        if (defaultValues == null || defaultValues.isEmpty()) {
            this.defaultValues = null;
        }
        else {
            this.defaultValues = Collections.unmodifiableList((List<? extends Date>)defaultValues);
        }
        this.values = new ArrayList<ObjectPair<Date, String>>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(5);
    }
    
    private TimestampArgument(final TimestampArgument source) {
        super(source);
        this.defaultValues = source.defaultValues;
        this.values = new ArrayList<ObjectPair<Date, String>>(5);
        this.validators = new ArrayList<ArgumentValueValidator>(source.validators);
    }
    
    public List<Date> getDefaultValues() {
        return this.defaultValues;
    }
    
    public void addValueValidator(final ArgumentValueValidator validator) {
        this.validators.add(validator);
    }
    
    @Override
    protected void addValue(final String valueString) throws ArgumentException {
        Date d;
        try {
            d = parseTimestamp(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_TIMESTAMP_VALUE_NOT_TIMESTAMP.get(valueString, this.getIdentifierString()), e);
        }
        if (this.values.size() >= this.getMaxOccurrences()) {
            throw new ArgumentException(ArgsMessages.ERR_ARG_MAX_OCCURRENCES_EXCEEDED.get(this.getIdentifierString()));
        }
        for (final ArgumentValueValidator v : this.validators) {
            v.validateArgumentValue(this, valueString);
        }
        this.values.add(new ObjectPair<Date, String>(d, valueString));
    }
    
    public static Date parseTimestamp(final String s) throws ParseException {
        try {
            return StaticUtils.decodeGeneralizedTime(s);
        }
        catch (final Exception ex) {
            String dateFormatString = null;
            switch (s.length()) {
                case 18: {
                    dateFormatString = "yyyyMMddHHmmss.SSS";
                    break;
                }
                case 14: {
                    dateFormatString = "yyyyMMddHHmmss";
                    break;
                }
                case 12: {
                    dateFormatString = "yyyyMMddHHmm";
                    break;
                }
                default: {
                    throw new ParseException(ArgsMessages.ERR_TIMESTAMP_PARSE_ERROR.get(s), 0);
                }
            }
            final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
            dateFormat.setLenient(false);
            return dateFormat.parse(s);
        }
    }
    
    public Date getValue() {
        if (!this.values.isEmpty()) {
            return (Date)this.values.get(0).getFirst();
        }
        if (this.defaultValues == null || this.defaultValues.isEmpty()) {
            return null;
        }
        return this.defaultValues.get(0);
    }
    
    public List<Date> getValues() {
        if (this.values.isEmpty() && this.defaultValues != null) {
            return this.defaultValues;
        }
        final ArrayList<Date> dateList = new ArrayList<Date>(this.values.size());
        for (final ObjectPair<Date, String> p : this.values) {
            dateList.add(p.getFirst());
        }
        return Collections.unmodifiableList((List<? extends Date>)dateList);
    }
    
    public String getStringValue() {
        if (!this.values.isEmpty()) {
            return (String)this.values.get(0).getSecond();
        }
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            return StaticUtils.encodeGeneralizedTime(this.defaultValues.get(0));
        }
        return null;
    }
    
    @Override
    public List<String> getValueStringRepresentations(final boolean useDefault) {
        if (!this.values.isEmpty()) {
            final ArrayList<String> valueStrings = new ArrayList<String>(this.values.size());
            for (final ObjectPair<Date, String> p : this.values) {
                valueStrings.add(p.getSecond());
            }
            return Collections.unmodifiableList((List<? extends String>)valueStrings);
        }
        if (useDefault && this.defaultValues != null && !this.defaultValues.isEmpty()) {
            final ArrayList<String> valueStrings = new ArrayList<String>(this.defaultValues.size());
            for (final Date d : this.defaultValues) {
                valueStrings.add(StaticUtils.encodeGeneralizedTime(d));
            }
            return Collections.unmodifiableList((List<? extends String>)valueStrings);
        }
        return Collections.emptyList();
    }
    
    @Override
    protected boolean hasDefaultValue() {
        return this.defaultValues != null && !this.defaultValues.isEmpty();
    }
    
    @Override
    public String getDataTypeName() {
        return ArgsMessages.INFO_TIMESTAMP_TYPE_NAME.get();
    }
    
    @Override
    public String getValueConstraints() {
        return ArgsMessages.INFO_TIMESTAMP_CONSTRAINTS.get();
    }
    
    @Override
    protected void reset() {
        super.reset();
        this.values.clear();
    }
    
    @Override
    public TimestampArgument getCleanCopy() {
        return new TimestampArgument(this);
    }
    
    @Override
    protected void addToCommandLine(final List<String> argStrings) {
        if (this.values != null) {
            for (final ObjectPair<Date, String> p : this.values) {
                argStrings.add(this.getIdentifierString());
                if (this.isSensitive()) {
                    argStrings.add("***REDACTED***");
                }
                else {
                    argStrings.add(p.getSecond());
                }
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("TimestampArgument(");
        this.appendBasicToStringInfo(buffer);
        if (this.defaultValues != null && !this.defaultValues.isEmpty()) {
            if (this.defaultValues.size() == 1) {
                buffer.append(", defaultValue='");
                buffer.append(StaticUtils.encodeGeneralizedTime(this.defaultValues.get(0)));
            }
            else {
                buffer.append(", defaultValues={");
                final Iterator<Date> iterator = this.defaultValues.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(StaticUtils.encodeGeneralizedTime(iterator.next()));
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
            }
        }
        buffer.append(')');
    }
}
