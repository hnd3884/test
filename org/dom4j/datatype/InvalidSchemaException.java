package org.dom4j.datatype;

public class InvalidSchemaException extends IllegalArgumentException
{
    public InvalidSchemaException(final String reason) {
        super(reason);
    }
}
