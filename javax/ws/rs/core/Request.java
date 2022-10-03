package javax.ws.rs.core;

import java.util.Date;
import java.util.List;

public interface Request
{
    String getMethod();
    
    Variant selectVariant(final List<Variant> p0);
    
    Response.ResponseBuilder evaluatePreconditions(final EntityTag p0);
    
    Response.ResponseBuilder evaluatePreconditions(final Date p0);
    
    Response.ResponseBuilder evaluatePreconditions(final Date p0, final EntityTag p1);
    
    Response.ResponseBuilder evaluatePreconditions();
}
