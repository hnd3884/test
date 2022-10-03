package io.opencensus.stats;

import java.util.Set;
import javax.annotation.Nullable;

public abstract class ViewManager
{
    public abstract void registerView(final View p0);
    
    @Nullable
    public abstract ViewData getView(final View.Name p0);
    
    public abstract Set<View> getAllExportedViews();
}
