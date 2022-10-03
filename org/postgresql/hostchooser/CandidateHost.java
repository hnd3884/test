package org.postgresql.hostchooser;

import org.postgresql.util.HostSpec;

public class CandidateHost
{
    public final HostSpec hostSpec;
    public final HostRequirement targetServerType;
    
    public CandidateHost(final HostSpec hostSpec, final HostRequirement targetServerType) {
        this.hostSpec = hostSpec;
        this.targetServerType = targetServerType;
    }
}
