package com.me.devicemanagement.onpremise.webclient.admin.certificate.manual;

import com.me.devicemanagement.onpremise.start.util.CheckServerStatus;

class CheckServerRunning
{
    public static void main(final String[] args) throws Exception {
        if (CheckServerStatus.getInstance().isServerRunning()) {
            System.exit(1);
        }
    }
}
