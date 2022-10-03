package com.adventnet.iam.security.antivirus.icap;

import java.util.concurrent.Executors;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.channel.ExceptionEvent;
import java.io.IOException;
import ch.mimo.netty.handler.codec.icap.IcapResponse;
import ch.mimo.netty.handler.codec.icap.IcapChunk;
import ch.mimo.netty.handler.codec.icap.IcapResponseStatus;
import ch.mimo.netty.handler.codec.icap.DefaultIcapChunkTrailer;
import ch.mimo.netty.handler.codec.icap.DefaultIcapChunk;
import org.jboss.netty.buffer.ChannelBuffers;
import ch.mimo.netty.handler.codec.icap.IcapMessageElementEnum;
import java.io.InputStream;
import com.adventnet.iam.security.antivirus.AVScanFailureInfo;
import org.jboss.netty.channel.ChannelFutureListener;
import java.net.SocketAddress;
import ch.mimo.netty.handler.codec.icap.IcapMethod;
import ch.mimo.netty.handler.codec.icap.DefaultIcapRequest;
import ch.mimo.netty.handler.codec.icap.IcapVersion;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelFactory;
import java.util.concurrent.Executor;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import java.util.concurrent.CountDownLatch;
import org.jboss.netty.channel.ChannelFuture;
import ch.mimo.netty.handler.codec.icap.IcapRequest;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.Channel;
import java.net.InetSocketAddress;
import org.jboss.netty.bootstrap.ClientBootstrap;
import java.util.concurrent.ExecutorService;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import java.io.Closeable;

public class IcapClient implements Closeable
{
    private static final HttpRequest DUMMY_HTTP_REQUEST;
    private static final HttpResponse DUMMY_HTTP_RESPONSE;
    private static final String USER_AGENT_NAME = "Java-ICAP-Client";
    private static final ExecutorService THREAD_POOL;
    private ClientBootstrap bootStrap;
    private InetSocketAddress inetAddress;
    private Channel channel;
    private TimeUnit timeunit;
    private IcapRequest request;
    private ChannelFuture closeFuture;
    private int timeout;
    private CountDownLatch responseCountDownLatch;
    
    public IcapClient(final String host, final int port, final String service) {
        this.timeunit = TimeUnit.MILLISECONDS;
        this.timeout = 60000;
        (this.bootStrap = new ClientBootstrap((ChannelFactory)new OioClientSocketChannelFactory((Executor)IcapClient.THREAD_POOL))).setPipelineFactory((ChannelPipelineFactory)new IcapClientChannelPipeline());
        this.inetAddress = new InetSocketAddress(host, port);
        (this.request = (IcapRequest)new DefaultIcapRequest(IcapVersion.ICAP_1_0, (IcapMethod)null, "icap://" + host + ':' + port + '/' + service, host)).addHeader("User-Agent", (Object)"Java-ICAP-Client");
    }
    
    public void initChannel() throws InterruptedException, IcapClientException {
        final ChannelFuture future = this.bootStrap.connect((SocketAddress)this.inetAddress);
        final CountDownLatch channelLatch = new CountDownLatch(1);
        future.addListener((ChannelFutureListener)new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture cf) throws Exception {
                try {
                    if (cf.isSuccess()) {
                        IcapClient.this.channel = cf.getChannel();
                    }
                }
                finally {
                    channelLatch.countDown();
                }
            }
        });
        channelLatch.await(this.getResponseTimeOut(), this.getTimeUnit());
        if (this.channel == null || !this.channel.isOpen()) {
            throw new IcapClientException(AVScanFailureInfo.FailedCause.AV_CONNECTION_FAILED, "Remote host [ " + ((future.getCause() != null) ? future.getCause().getMessage() : " Cause not provided") + "] connection failed");
        }
        this.channel.getCloseFuture().addListener((ChannelFutureListener)new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (IcapClient.this.responseCountDownLatch != null && IcapClient.this.responseCountDownLatch.getCount() != 0L) {
                    IcapClient.this.channel.setAttachment((Object)new IcapClientException("Channel closed"));
                    IcapClient.this.responseCountDownLatch.countDown();
                }
            }
        });
    }
    
    public int getResponseTimeOut() {
        return this.timeout;
    }
    
    public TimeUnit getTimeUnit() {
        return this.timeunit;
    }
    
    public void setResonseTimeOut(final int timeoutInMillis) {
        this.setResonseTimeOut(timeoutInMillis, TimeUnit.MILLISECONDS);
    }
    
    public void setResonseTimeOut(final int timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.timeunit = unit;
    }
    
    public void send(final InputStream inputStream, final Integer previewSize) throws IOException, InterruptedException, IcapClientException {
        this.request.setMethod(IcapMethod.RESPMOD);
        this.request.addHeader("Allow", (Object)"204");
        this.request.setHttpRequest(IcapClient.DUMMY_HTTP_REQUEST);
        this.request.setHttpResponse(IcapClient.DUMMY_HTTP_RESPONSE);
        this.request.setBody(IcapMessageElementEnum.RESBODY);
        boolean isEarlyTermination = false;
        if (previewSize != null) {
            this.request.addHeader("Preview", (Object)previewSize);
        }
        this.channel.write((Object)this.request);
        if (previewSize != null) {
            int readedPreviewSize = 0;
            this.responseCountDownLatch = new CountDownLatch(1);
            this.channel.setAttachment((Object)this.responseCountDownLatch);
            if (previewSize > 0) {
                final byte[] previewBuffer = new byte[(int)previewSize];
                readedPreviewSize = inputStream.read(previewBuffer);
                if (readedPreviewSize == -1) {
                    throw new IcapClientException("File is empty");
                }
                final IcapChunk previewChunk = (IcapChunk)new DefaultIcapChunk(ChannelBuffers.wrappedBuffer(previewBuffer, 0, readedPreviewSize));
                previewChunk.setPreviewChunk(true);
                this.channel.write((Object)previewChunk);
            }
            isEarlyTermination = (readedPreviewSize < previewSize);
            this.channel.write((Object)new DefaultIcapChunkTrailer(true, isEarlyTermination));
            if (!isEarlyTermination) {
                final IcapResponse response = getResponse(this.channel, this.responseCountDownLatch, this.getResponseTimeOut(), this.getTimeUnit());
                if (!response.getStatus().equals((Object)IcapResponseStatus.CONTINUE)) {
                    throw new IcapClientException(AVScanFailureInfo.FailedCause.AV_OTHER_FAILURE_CASES, "Expected ICAP Response is CONTINUE. Response is " + response.getStatus());
                }
            }
        }
        if (!isEarlyTermination) {
            this.responseCountDownLatch = new CountDownLatch(1);
            this.channel.setAttachment((Object)this.responseCountDownLatch);
            this.channel.write((Object)inputStream);
            this.channel.write((Object)new DefaultIcapChunkTrailer(true, false));
        }
    }
    
    public IcapResponse getIcapResponse() throws InterruptedException {
        return getResponse(this.channel, this.responseCountDownLatch, this.getResponseTimeOut(), this.getTimeUnit());
    }
    
    private static IcapResponse getResponse(final Channel channel, final CountDownLatch countDownLatch, final int responseTimeout, final TimeUnit timeunit) throws InterruptedException {
        if (!countDownLatch.await(responseTimeout, timeunit)) {
            throw new IcapClientException(AVScanFailureInfo.FailedCause.AV_SCAN_TIME_OUT, "Time out to get response from ICAP server.");
        }
        if (channel.getAttachment() instanceof IcapResponse) {
            final IcapResponse response = (IcapResponse)channel.getAttachment();
            clearAttachement(channel);
            return response;
        }
        if (channel.getAttachment() instanceof ExceptionEvent) {
            final ExceptionEvent exception = (ExceptionEvent)channel.getAttachment();
            clearAttachement(channel);
            throw new IcapClientException(exception.getCause().getLocalizedMessage());
        }
        if (channel.getAttachment() instanceof IcapClientException) {
            throw (IcapClientException)channel.getAttachment();
        }
        throw new IcapClientException("Unrecognisable Channel Attachment. Attachment is: " + channel.getAttachment());
    }
    
    public IcapResponse sendOption() throws InterruptedException, IcapClientException {
        this.request.setMethod(IcapMethod.OPTIONS);
        final CountDownLatch icapPreviewDownLatch = new CountDownLatch(1);
        this.channel.setAttachment((Object)icapPreviewDownLatch);
        this.channel.write((Object)this.request);
        if (!icapPreviewDownLatch.await(this.getResponseTimeOut(), this.getTimeUnit())) {
            throw new IcapClientException(AVScanFailureInfo.FailedCause.AV_SCAN_TIME_OUT, "Time out to get response from ICAP server.");
        }
        if (this.channel.getAttachment() instanceof IcapResponse) {
            final IcapResponse optionsResponse = (IcapResponse)this.channel.getAttachment();
            clearAttachement(this.channel);
            return optionsResponse;
        }
        if (this.channel.getAttachment() instanceof ExceptionEvent) {
            final ExceptionEvent exception = (ExceptionEvent)this.channel.getAttachment();
            clearAttachement(this.channel);
            throw new IcapClientException(exception.getCause().getLocalizedMessage());
        }
        return null;
    }
    
    private static void clearAttachement(final Channel channel) {
        channel.setAttachment((Object)null);
    }
    
    @Override
    public void close() {
        if (this.channel != null) {
            final CountDownLatch channelLatch = new CountDownLatch(1);
            final ChannelFuture closeAction = this.channel.close();
            closeAction.addListener((ChannelFutureListener)new ChannelFutureListener() {
                public void operationComplete(final ChannelFuture future) throws Exception {
                    try {
                        IcapClient.this.closeFuture = future;
                    }
                    finally {
                        channelLatch.countDown();
                    }
                }
            });
            try {
                if (!channelLatch.await(this.getResponseTimeOut(), this.getTimeUnit())) {
                    throw new IcapClientException("Icap client channel closed failed");
                }
            }
            catch (final InterruptedException e) {
                throw new IcapClientException("Icap client channel closed failed");
            }
        }
    }
    
    public boolean isClosed() {
        return this.closeFuture != null && this.closeFuture.isSuccess();
    }
    
    public Throwable getCloseCause() {
        return (this.closeFuture == null) ? null : this.closeFuture.getCause();
    }
    
    static {
        DUMMY_HTTP_REQUEST = (HttpRequest)new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/some/servers/uri");
        DUMMY_HTTP_RESPONSE = (HttpResponse)new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        THREAD_POOL = Executors.newCachedThreadPool();
        IcapClient.DUMMY_HTTP_RESPONSE.setChunked(true);
    }
}
