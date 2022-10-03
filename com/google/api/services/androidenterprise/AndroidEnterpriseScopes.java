package com.google.api.services.androidenterprise;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AndroidEnterpriseScopes
{
    public static final String ANDROIDENTERPRISE = "https://www.googleapis.com/auth/androidenterprise";
    
    public static Set<String> all() {
        final Set<String> set = new HashSet<String>();
        set.add("https://www.googleapis.com/auth/androidenterprise");
        return Collections.unmodifiableSet((Set<? extends String>)set);
    }
    
    private AndroidEnterpriseScopes() {
    }
}
