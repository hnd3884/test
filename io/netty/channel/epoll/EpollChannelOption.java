package io.netty.channel.epoll;

import java.net.InetAddress;
import java.util.Map;
import io.netty.channel.ChannelOption;
import io.netty.channel.unix.UnixChannelOption;

public final class EpollChannelOption<T> extends UnixChannelOption<T>
{
    public static final ChannelOption<Boolean> TCP_CORK;
    public static final ChannelOption<Long> TCP_NOTSENT_LOWAT;
    public static final ChannelOption<Integer> TCP_KEEPIDLE;
    public static final ChannelOption<Integer> TCP_KEEPINTVL;
    public static final ChannelOption<Integer> TCP_KEEPCNT;
    public static final ChannelOption<Integer> TCP_USER_TIMEOUT;
    public static final ChannelOption<Boolean> IP_FREEBIND;
    public static final ChannelOption<Boolean> IP_TRANSPARENT;
    public static final ChannelOption<Boolean> IP_RECVORIGDSTADDR;
    @Deprecated
    public static final ChannelOption<Integer> TCP_FASTOPEN;
    @Deprecated
    public static final ChannelOption<Boolean> TCP_FASTOPEN_CONNECT;
    public static final ChannelOption<Integer> TCP_DEFER_ACCEPT;
    public static final ChannelOption<Boolean> TCP_QUICKACK;
    public static final ChannelOption<Integer> SO_BUSY_POLL;
    public static final ChannelOption<EpollMode> EPOLL_MODE;
    public static final ChannelOption<Map<InetAddress, byte[]>> TCP_MD5SIG;
    public static final ChannelOption<Integer> MAX_DATAGRAM_PAYLOAD_SIZE;
    public static final ChannelOption<Boolean> UDP_GRO;
    
    private EpollChannelOption() {
    }
    
    static {
        TCP_CORK = ChannelOption.valueOf(EpollChannelOption.class, "TCP_CORK");
        TCP_NOTSENT_LOWAT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_NOTSENT_LOWAT");
        TCP_KEEPIDLE = ChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPIDLE");
        TCP_KEEPINTVL = ChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPINTVL");
        TCP_KEEPCNT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_KEEPCNT");
        TCP_USER_TIMEOUT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_USER_TIMEOUT");
        IP_FREEBIND = ChannelOption.valueOf("IP_FREEBIND");
        IP_TRANSPARENT = ChannelOption.valueOf("IP_TRANSPARENT");
        IP_RECVORIGDSTADDR = ChannelOption.valueOf("IP_RECVORIGDSTADDR");
        TCP_FASTOPEN = ChannelOption.TCP_FASTOPEN;
        TCP_FASTOPEN_CONNECT = ChannelOption.TCP_FASTOPEN_CONNECT;
        TCP_DEFER_ACCEPT = ChannelOption.valueOf(EpollChannelOption.class, "TCP_DEFER_ACCEPT");
        TCP_QUICKACK = ChannelOption.valueOf(EpollChannelOption.class, "TCP_QUICKACK");
        SO_BUSY_POLL = ChannelOption.valueOf(EpollChannelOption.class, "SO_BUSY_POLL");
        EPOLL_MODE = ChannelOption.valueOf(EpollChannelOption.class, "EPOLL_MODE");
        TCP_MD5SIG = ChannelOption.valueOf("TCP_MD5SIG");
        MAX_DATAGRAM_PAYLOAD_SIZE = ChannelOption.valueOf("MAX_DATAGRAM_PAYLOAD_SIZE");
        UDP_GRO = ChannelOption.valueOf("UDP_GRO");
    }
}
