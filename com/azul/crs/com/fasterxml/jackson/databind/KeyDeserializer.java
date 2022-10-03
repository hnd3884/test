package com.azul.crs.com.fasterxml.jackson.databind;

import java.io.IOException;

public abstract class KeyDeserializer
{
    public abstract Object deserializeKey(final String p0, final DeserializationContext p1) throws IOException;
    
    public abstract static class None extends KeyDeserializer
    {
    }
}
