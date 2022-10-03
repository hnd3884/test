package com.maverick.ssh;

public abstract class ChannelAdapter implements ChannelEventListener
{
    public void channelOpened(final SshChannel sshChannel) {
    }
    
    public void channelClosing(final SshChannel sshChannel) {
    }
    
    public void channelClosed(final SshChannel sshChannel) {
    }
    
    public void channelEOF(final SshChannel sshChannel) {
    }
    
    public void dataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
    }
    
    public void dataSent(final SshChannel sshChannel, final byte[] array, final int n, final int n2) {
    }
    
    public void extendedDataReceived(final SshChannel sshChannel, final byte[] array, final int n, final int n2, final int n3) {
    }
}
