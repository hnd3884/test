package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class MembersHasMember extends GenericJson
{
    @Key
    private Boolean isMember;
    
    public Boolean getIsMember() {
        return this.isMember;
    }
    
    public MembersHasMember setIsMember(final Boolean isMember) {
        this.isMember = isMember;
        return this;
    }
    
    public MembersHasMember set(final String fieldName, final Object value) {
        return (MembersHasMember)super.set(fieldName, value);
    }
    
    public MembersHasMember clone() {
        return (MembersHasMember)super.clone();
    }
}
