package com.maverick.ssh;

import java.io.IOException;

public interface Client
{
    void exit() throws SshException, ShellTimeoutException, IOException;
}
