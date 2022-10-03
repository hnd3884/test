package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import java.util.function.Predicate;
import java.util.function.Function;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

class Hidden
{
    @SuppressJava6Requirement(reason = "BlockHound is Java 8+, but this class is only loaded by it's SPI")
    public static final class NettyBlockHoundIntegration implements BlockHoundIntegration
    {
        public void applyTo(final BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("io.netty.channel.nio.NioEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.channel.kqueue.KQueueEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.channel.epoll.EpollEventLoop", "handleLoopException");
            builder.allowBlockingCallsInside("io.netty.util.HashedWheelTimer", "start");
            builder.allowBlockingCallsInside("io.netty.util.HashedWheelTimer", "stop");
            builder.allowBlockingCallsInside("io.netty.util.HashedWheelTimer$Worker", "waitForNextTick");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "confirmShutdown");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "handshake");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "runAllDelegatedTasks");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.SslHandler", "runDelegatedTasks");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.GlobalEventExecutor", "takeTask");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.GlobalEventExecutor", "addTask");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "takeTask");
            builder.allowBlockingCallsInside("io.netty.util.concurrent.SingleThreadEventExecutor", "addTask");
            builder.allowBlockingCallsInside("io.netty.handler.ssl.ReferenceCountedOpenSslClientContext$ExtendedTrustManagerVerifyCallback", "verify");
            builder.allowBlockingCallsInside("sun.security.ssl.SSLEngineImpl", "unwrap");
            builder.allowBlockingCallsInside("sun.security.ssl.SSLEngineImpl", "wrap");
            builder.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parse");
            builder.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parseEtcResolverSearchDomains");
            builder.allowBlockingCallsInside("io.netty.resolver.dns.UnixResolverDnsServerAddressStreamProvider", "parseEtcResolverOptions");
            builder.allowBlockingCallsInside("io.netty.resolver.HostsFileEntriesProvider$ParserImpl", "parse");
            builder.nonBlockingThreadPredicate((Function)new Function<Predicate<Thread>, Predicate<Thread>>() {
                @Override
                public Predicate<Thread> apply(final Predicate<Thread> p) {
                    return new Predicate<Thread>() {
                        @SuppressJava6Requirement(reason = "Predicate#test")
                        @Override
                        public boolean test(final Thread thread) {
                            return p.test(thread) || thread instanceof FastThreadLocalThread;
                        }
                    };
                }
            });
        }
        
        public int compareTo(final BlockHoundIntegration o) {
            return 0;
        }
    }
}
