package org.apache.catalina.ant;

import org.apache.tools.ant.BuildException;

public class ServerinfoTask extends AbstractCatalinaTask
{
    @Override
    public void execute() throws BuildException {
        super.execute();
        this.execute("/serverinfo");
    }
}
