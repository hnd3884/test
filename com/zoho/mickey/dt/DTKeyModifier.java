package com.zoho.mickey.dt;

import java.util.Map;
import com.zoho.mickey.exception.KeyModificationException;
import com.zoho.mickey.Initializable;

public interface DTKeyModifier extends Initializable
{
    boolean changeKey(final String p0, final String p1) throws KeyModificationException;
    
    boolean sanitize(final String p0, final String p1, final Map<String, String> p2) throws KeyModificationException;
    
    boolean cleanUp(final String p0, final String p1, final boolean p2) throws KeyModificationException;
}
