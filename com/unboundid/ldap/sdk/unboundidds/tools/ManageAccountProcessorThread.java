package com.unboundid.ldap.sdk.unboundidds.tools;

import com.unboundid.ldap.sdk.unboundidds.extensions.PasswordPolicyStateExtendedRequest;

final class ManageAccountProcessorThread extends Thread
{
    private final ManageAccountProcessor processor;
    
    ManageAccountProcessorThread(final int threadNumber, final ManageAccountProcessor processor) {
        this.setName("manage-account Processor Thread " + threadNumber);
        this.processor = processor;
    }
    
    @Override
    public void run() {
        while (true) {
            final PasswordPolicyStateExtendedRequest request = this.processor.getRequest();
            if (request == null) {
                break;
            }
            this.processor.process(request);
        }
    }
}
