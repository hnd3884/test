package com.zoho.mickey.tools.crypto;

import com.zoho.mickey.exception.KeyModificationException;

public interface KeyModifier
{
    void changeKey(final String p0) throws KeyModificationException;
}
