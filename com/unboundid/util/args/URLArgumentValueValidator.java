package com.unboundid.util.args;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class URLArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = -4431100566624433212L;
    private final Set<String> allowedSchemes;
    
    public URLArgumentValueValidator(final String... allowedSchemes) {
        this(StaticUtils.toList(allowedSchemes));
    }
    
    public URLArgumentValueValidator(final Collection<String> allowedSchemes) {
        if (allowedSchemes == null) {
            this.allowedSchemes = Collections.emptySet();
        }
        else {
            this.allowedSchemes = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(allowedSchemes));
        }
    }
    
    public Set<String> getAllowedSchemes() {
        return this.allowedSchemes;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        URI uri;
        try {
            uri = new URI(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_URL_VALIDATOR_VALUE_NOT_URL.get(valueString, argument.getIdentifierString(), StaticUtils.getExceptionMessage(e)), e);
        }
        if (uri.getScheme() == null) {
            throw new ArgumentException(ArgsMessages.ERR_URL_VALIDATOR_MISSING_SCHEME.get(valueString, argument.getIdentifierString()));
        }
        if (!this.allowedSchemes.isEmpty() && !this.allowedSchemes.contains(uri.getScheme())) {
            throw new ArgumentException(ArgsMessages.ERR_URL_VALIDATOR_UNACCEPTABLE_SCHEME.get(valueString, argument.getIdentifierString(), uri.getScheme()));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("URLArgumentValueValidator(");
        if (this.allowedSchemes != null) {
            buffer.append("allowedSchemes={");
            final Iterator<String> iterator = this.allowedSchemes.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
