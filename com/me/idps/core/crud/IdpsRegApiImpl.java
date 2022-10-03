package com.me.idps.core.crud;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import com.me.idps.core.factory.IdpsRegAPI;

public abstract class IdpsRegApiImpl implements IdpsRegAPI
{
    @Override
    public ArrayList<DMDomainListener> getDMDomainListener() {
        return new ArrayList<DMDomainListener>(Arrays.asList(new DMDomainListenerImpl()));
    }
}
