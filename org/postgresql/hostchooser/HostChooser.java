package org.postgresql.hostchooser;

import java.util.Iterator;

public interface HostChooser extends Iterable<CandidateHost>
{
    Iterator<CandidateHost> iterator();
}
