package org.bouncycastle.est.jcajce;

import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.est.Source;
import java.util.Map;
import org.bouncycastle.util.Properties;
import java.net.URL;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.ESTRequest;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.est.ESTClientSourceProvider;
import java.nio.charset.Charset;
import org.bouncycastle.est.ESTClient;

class DefaultESTClient implements ESTClient
{
    private static final Charset utf8;
    private static byte[] CRLF;
    private final ESTClientSourceProvider sslSocketProvider;
    
    public DefaultESTClient(final ESTClientSourceProvider sslSocketProvider) {
        this.sslSocketProvider = sslSocketProvider;
    }
    
    private static void writeLine(final OutputStream outputStream, final String s) throws IOException {
        outputStream.write(s.getBytes());
        outputStream.write(DefaultESTClient.CRLF);
    }
    
    public ESTResponse doRequest(final ESTRequest estRequest) throws IOException {
        ESTRequest redirectURL = estRequest;
        int n = 15;
        ESTResponse performRequest;
        do {
            performRequest = this.performRequest(redirectURL);
            redirectURL = this.redirectURL(performRequest);
        } while (redirectURL != null && --n > 0);
        if (n == 0) {
            throw new ESTException("Too many redirects..");
        }
        return performRequest;
    }
    
    protected ESTRequest redirectURL(final ESTResponse estResponse) throws IOException {
        ESTRequest estRequest = null;
        if (estResponse.getStatusCode() >= 300 && estResponse.getStatusCode() <= 399) {
            switch (estResponse.getStatusCode()) {
                case 301:
                case 302:
                case 303:
                case 306:
                case 307: {
                    final String header = estResponse.getHeader("Location");
                    if ("".equals(header)) {
                        throw new ESTException("Redirect status type: " + estResponse.getStatusCode() + " but no location header");
                    }
                    final ESTRequestBuilder estRequestBuilder = new ESTRequestBuilder(estResponse.getOriginalRequest());
                    if (header.startsWith("http")) {
                        estRequest = estRequestBuilder.withURL(new URL(header)).build();
                        break;
                    }
                    final URL url = estResponse.getOriginalRequest().getURL();
                    estRequest = estRequestBuilder.withURL(new URL(url.getProtocol(), url.getHost(), url.getPort(), header)).build();
                    break;
                }
                default: {
                    throw new ESTException("Client does not handle http status code: " + estResponse.getStatusCode());
                }
            }
        }
        if (estRequest != null) {
            estResponse.close();
        }
        return estRequest;
    }
    
    public ESTResponse performRequest(ESTRequest onConnection) throws IOException {
        ESTResponse hijack = null;
        Source source = null;
        try {
            source = this.sslSocketProvider.makeSource(onConnection.getURL().getHost(), onConnection.getURL().getPort());
            if (onConnection.getListener() != null) {
                onConnection = onConnection.getListener().onConnection(source, onConnection);
            }
            final Set keySet = Properties.asKeySet("org.bouncycastle.debug.est");
            OutputStream outputStream;
            if (keySet.contains("output") || keySet.contains("all")) {
                outputStream = new PrintingOutputStream(source.getOutputStream());
            }
            else {
                outputStream = source.getOutputStream();
            }
            final String string = onConnection.getURL().getPath() + ((onConnection.getURL().getQuery() != null) ? onConnection.getURL().getQuery() : "");
            final ESTRequestBuilder estRequestBuilder = new ESTRequestBuilder(onConnection);
            if (!onConnection.getHeaders().containsKey("Connection")) {
                estRequestBuilder.addHeader("Connection", "close");
            }
            final URL url = onConnection.getURL();
            if (url.getPort() > -1) {
                estRequestBuilder.setHeader("Host", String.format("%s:%d", url.getHost(), url.getPort()));
            }
            else {
                estRequestBuilder.setHeader("Host", url.getHost());
            }
            final ESTRequest build = estRequestBuilder.build();
            writeLine(outputStream, build.getMethod() + " " + string + " HTTP/1.1");
            for (final Map.Entry entry : build.getHeaders().entrySet()) {
                final String[] array = (String[])entry.getValue();
                for (int i = 0; i != array.length; ++i) {
                    writeLine(outputStream, (String)entry.getKey() + ": " + array[i]);
                }
            }
            outputStream.write(DefaultESTClient.CRLF);
            outputStream.flush();
            build.writeData(outputStream);
            outputStream.flush();
            if (build.getHijacker() != null) {
                hijack = build.getHijacker().hijack(build, source);
                return hijack;
            }
            hijack = new ESTResponse(build, source);
            return hijack;
        }
        finally {
            if (source != null && hijack == null) {
                source.close();
            }
        }
    }
    
    static {
        utf8 = Charset.forName("UTF-8");
        DefaultESTClient.CRLF = new byte[] { 13, 10 };
    }
    
    private class PrintingOutputStream extends OutputStream
    {
        private final OutputStream tgt;
        
        public PrintingOutputStream(final OutputStream tgt) {
            this.tgt = tgt;
        }
        
        @Override
        public void write(final int n) throws IOException {
            System.out.print(String.valueOf((char)n));
            this.tgt.write(n);
        }
    }
}
