package HTTPClient;

import java.io.IOException;

public interface HTTPClientModule extends HTTPClientModuleConstants
{
    int requestHandler(final Request p0, final Response[] p1) throws IOException, ModuleException;
    
    void responsePhase1Handler(final Response p0, final RoRequest p1) throws IOException, ModuleException;
    
    int responsePhase2Handler(final Response p0, final Request p1) throws IOException, ModuleException;
    
    void responsePhase3Handler(final Response p0, final RoRequest p1) throws IOException, ModuleException;
    
    void trailerHandler(final Response p0, final RoRequest p1) throws IOException, ModuleException;
}
