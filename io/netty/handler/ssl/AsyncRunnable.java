package io.netty.handler.ssl;

interface AsyncRunnable extends Runnable
{
    void run(final Runnable p0);
}
