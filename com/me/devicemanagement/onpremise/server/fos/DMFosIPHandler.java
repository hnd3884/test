package com.me.devicemanagement.onpremise.server.fos;

import com.adventnet.persistence.fos.FOS;

public class DMFosIPHandler
{
    public static void main(final String[] args) throws Exception {
        final FOS fos = new FOS();
        fos.initialize();
        final String publicIP = fos.getFOSConfig().publicIP();
        final Process ipdeleteProcess = new ProcessBuilder(new String[] { "../tools/fos/bin/ipdel.exe", publicIP }).start();
    }
}
