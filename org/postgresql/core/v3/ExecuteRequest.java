package org.postgresql.core.v3;

class ExecuteRequest
{
    public final SimpleQuery query;
    public final Portal portal;
    public final boolean asSimple;
    
    ExecuteRequest(final SimpleQuery query, final Portal portal, final boolean asSimple) {
        this.query = query;
        this.portal = portal;
        this.asSimple = asSimple;
    }
}
