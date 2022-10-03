package com.unboundid.util.args;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.persist.PersistUtils;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AttributeNameArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = 1781129993679474323L;
    private final boolean allowOptions;
    private final Schema schema;
    
    public AttributeNameArgumentValueValidator() {
        this(false, null);
    }
    
    public AttributeNameArgumentValueValidator(final boolean allowOptions, final Schema schema) {
        this.allowOptions = allowOptions;
        this.schema = schema;
    }
    
    public boolean allowOptions() {
        return this.allowOptions;
    }
    
    public Schema getSchema() {
        return this.schema;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        final StringBuilder errorMessage = new StringBuilder();
        if (!PersistUtils.isValidLDAPName(valueString, this.allowOptions, errorMessage)) {
            throw new ArgumentException(ArgsMessages.ERR_ATTR_NAME_VALIDATOR_INVALID_VALUE.get(valueString, argument.getIdentifierString(), String.valueOf(errorMessage)));
        }
        if (this.schema != null) {
            final String baseName = Attribute.getBaseName(valueString);
            if (this.schema.getAttributeType(baseName) == null) {
                throw new ArgumentException(ArgsMessages.ERR_ATTR_NAME_VALIDATOR_TYPE_NOT_DEFINED.get(valueString, argument.getIdentifierString(), baseName));
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
        buffer.append("AttributeNameArgumentValueValidator(allowOptions=");
        buffer.append(this.allowOptions);
        buffer.append(", hasSchema=");
        buffer.append(this.schema != null);
        buffer.append(')');
    }
}
