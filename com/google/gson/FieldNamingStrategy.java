package com.google.gson;

import java.lang.reflect.Field;

public interface FieldNamingStrategy
{
    String translateName(final Field p0);
}
