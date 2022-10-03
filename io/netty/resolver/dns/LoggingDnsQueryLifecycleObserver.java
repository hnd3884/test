package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsResponseCode;
import java.util.List;
import io.netty.channel.ChannelFuture;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;

final class LoggingDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver
{
    private final InternalLogger logger;
    private final InternalLogLevel level;
    private final DnsQuestion question;
    private InetSocketAddress dnsServerAddress;
    
    LoggingDnsQueryLifecycleObserver(final DnsQuestion question, final InternalLogger logger, final InternalLogLevel level) {
        this.question = ObjectUtil.checkNotNull(question, "question");
        this.logger = ObjectUtil.checkNotNull(logger, "logger");
        this.level = ObjectUtil.checkNotNull(level, "level");
    }
    
    @Override
    public void queryWritten(final InetSocketAddress dnsServerAddress, final ChannelFuture future) {
        this.dnsServerAddress = dnsServerAddress;
    }
    
    @Override
    public void queryCancelled(final int queriesRemaining) {
        if (this.dnsServerAddress != null) {
            this.logger.log(this.level, "from {} : {} cancelled with {} queries remaining", this.dnsServerAddress, this.question, queriesRemaining);
        }
        else {
            this.logger.log(this.level, "{} query never written and cancelled with {} queries remaining", this.question, queriesRemaining);
        }
    }
    
    @Override
    public DnsQueryLifecycleObserver queryRedirected(final List<InetSocketAddress> nameServers) {
        this.logger.log(this.level, "from {} : {} redirected", this.dnsServerAddress, this.question);
        return this;
    }
    
    @Override
    public DnsQueryLifecycleObserver queryCNAMEd(final DnsQuestion cnameQuestion) {
        this.logger.log(this.level, "from {} : {} CNAME question {}", this.dnsServerAddress, this.question, cnameQuestion);
        return this;
    }
    
    @Override
    public DnsQueryLifecycleObserver queryNoAnswer(final DnsResponseCode code) {
        this.logger.log(this.level, "from {} : {} no answer {}", this.dnsServerAddress, this.question, code);
        return this;
    }
    
    @Override
    public void queryFailed(final Throwable cause) {
        if (this.dnsServerAddress != null) {
            this.logger.log(this.level, "from {} : {} failure", this.dnsServerAddress, this.question, cause);
        }
        else {
            this.logger.log(this.level, "{} query never written and failed", this.question, cause);
        }
    }
    
    @Override
    public void querySucceed() {
    }
}
