package com.zoho.security;

import java.io.IOException;
import org.xml.sax.SAXException;
import com.adventnet.iam.security.SecurityFilterProperties;

public interface SFCorePlugin
{
    void initRules(final SecurityFilterProperties p0) throws SAXException, IOException;
    
    void clean();
}
