package org.apache.commons.chain;

public interface Filter extends Command
{
    boolean postprocess(final Context p0, final Exception p1);
}
