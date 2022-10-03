package HTTPClient;

import java.util.NoSuchElementException;
import java.net.ProtocolException;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.io.EOFException;
import java.io.ByteArrayInputStream;
import java.io.InterruptedIOException;
import java.util.Date;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public final class Response implements RoResponse, GlobalConstants, Cloneable
{
    private static final Hashtable singleValueHeaders;
    private HTTPConnection connection;
    private StreamDemultiplexor stream_handler;
    HTTPResponse http_resp;
    int timeout;
    public InputStream inp_stream;
    private RespInputStream resp_inp_stream;
    private String method;
    String resource;
    private boolean used_proxy;
    private boolean sent_entity;
    int StatusCode;
    String ReasonLine;
    String Version;
    URI EffectiveURI;
    CIHashtable Headers;
    CIHashtable Trailers;
    int ContentLength;
    int cd_type;
    byte[] Data;
    boolean reading_headers;
    boolean got_headers;
    boolean got_trailers;
    private IOException exception;
    boolean final_resp;
    boolean retry;
    private byte[] buf;
    private int buf_pos;
    private StringBuffer hdrs;
    private boolean reading_lines;
    private boolean bol;
    private boolean got_cr;
    boolean trailers_read;
    Request req;
    boolean isFirstResponse;
    
    Response(final Request request, final boolean used_proxy, final StreamDemultiplexor stream_handler) throws IOException {
        this.Headers = new CIHashtable();
        this.Trailers = new CIHashtable();
        this.ContentLength = -1;
        this.cd_type = 1;
        this.reading_headers = false;
        this.got_headers = false;
        this.got_trailers = false;
        this.final_resp = false;
        this.retry = false;
        this.buf = new byte[7];
        this.hdrs = new StringBuffer(400);
        this.reading_lines = false;
        this.bol = true;
        this.got_cr = false;
        this.trailers_read = false;
        this.isFirstResponse = false;
        this.connection = request.getConnection();
        this.method = request.getMethod();
        this.resource = request.getRequestURI();
        this.used_proxy = used_proxy;
        this.stream_handler = stream_handler;
        this.sent_entity = (request.getData() != null);
        stream_handler.register(this, request);
        this.resp_inp_stream = stream_handler.getStream(this);
        this.inp_stream = this.resp_inp_stream;
    }
    
    Response(final Request request, final InputStream is) throws IOException {
        this.Headers = new CIHashtable();
        this.Trailers = new CIHashtable();
        this.ContentLength = -1;
        this.cd_type = 1;
        this.reading_headers = false;
        this.got_headers = false;
        this.got_trailers = false;
        this.final_resp = false;
        this.retry = false;
        this.buf = new byte[7];
        this.hdrs = new StringBuffer(400);
        this.reading_lines = false;
        this.bol = true;
        this.got_cr = false;
        this.trailers_read = false;
        this.isFirstResponse = false;
        this.connection = request.getConnection();
        this.method = request.getMethod();
        this.resource = request.getRequestURI();
        this.used_proxy = false;
        this.stream_handler = null;
        this.sent_entity = (request.getData() != null);
        this.inp_stream = is;
    }
    
    public Response(final String version, final int status, final String reason, final NVPair[] headers, final byte[] data, final InputStream is, final int cont_len) {
        this.Headers = new CIHashtable();
        this.Trailers = new CIHashtable();
        this.ContentLength = -1;
        this.cd_type = 1;
        this.reading_headers = false;
        this.got_headers = false;
        this.got_trailers = false;
        this.final_resp = false;
        this.retry = false;
        this.buf = new byte[7];
        this.hdrs = new StringBuffer(400);
        this.reading_lines = false;
        this.bol = true;
        this.got_cr = false;
        this.trailers_read = false;
        this.isFirstResponse = false;
        this.Version = version;
        this.StatusCode = status;
        this.ReasonLine = reason;
        if (headers != null) {
            for (int idx = 0; idx < headers.length; ++idx) {
                this.setHeader(headers[idx].getName(), headers[idx].getValue());
            }
        }
        if (data != null) {
            this.Data = data;
        }
        else if (is == null) {
            this.Data = new byte[0];
        }
        else {
            this.inp_stream = is;
            this.ContentLength = cont_len;
        }
        this.got_headers = true;
        this.got_trailers = true;
    }
    
    public final int getStatusCode() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.StatusCode;
    }
    
    public final String getReasonLine() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.ReasonLine;
    }
    
    public final String getVersion() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.Version;
    }
    
    int getContinue() throws IOException {
        this.getHeaders(false);
        return this.StatusCode;
    }
    
    public final URI getEffectiveURI() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.EffectiveURI;
    }
    
    public void setEffectiveURI(final URI final_uri) {
        this.EffectiveURI = final_uri;
    }
    
    public final URL getEffectiveURL() throws IOException {
        return this.getEffectiveURI().toURL();
    }
    
    public void setEffectiveURL(final URL final_url) {
        try {
            this.setEffectiveURI(new URI(final_url));
        }
        catch (final ParseException pe) {
            throw new Error(pe.toString());
        }
    }
    
    public String getHeader(final String hdr) throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return (String)this.Headers.get(hdr.trim());
    }
    
    public int getHeaderAsInt(final String hdr) throws IOException, NumberFormatException {
        final String val = this.getHeader(hdr);
        if (val == null) {
            throw new NumberFormatException("null");
        }
        return Integer.parseInt(val);
    }
    
    public Date getHeaderAsDate(final String hdr) throws IOException, IllegalArgumentException {
        String raw_date = this.getHeader(hdr);
        if (raw_date == null) {
            return null;
        }
        if (raw_date.toUpperCase().indexOf("GMT") == -1 && raw_date.indexOf(32) > 0) {
            raw_date = String.valueOf(raw_date) + " GMT";
        }
        Date date;
        try {
            date = Util.parseHttpDate(raw_date);
        }
        catch (final IllegalArgumentException iae) {
            long time;
            try {
                time = Long.parseLong(raw_date);
            }
            catch (final NumberFormatException ex) {
                throw iae;
            }
            if (time < 0L) {
                time = 0L;
            }
            date = new Date(time * 1000L);
        }
        return date;
    }
    
    public void setHeader(final String header, final String value) {
        this.Headers.put(header.trim(), value.trim());
    }
    
    public void deleteHeader(final String header) {
        this.Headers.remove(header.trim());
    }
    
    public String getTrailer(final String trailer) throws IOException {
        if (!this.got_trailers) {
            this.getTrailers();
        }
        return (String)this.Trailers.get(trailer.trim());
    }
    
    public int getTrailerAsInt(final String trailer) throws IOException, NumberFormatException {
        final String val = this.getTrailer(trailer);
        if (val == null) {
            throw new NumberFormatException("null");
        }
        return Integer.parseInt(val);
    }
    
    public Date getTrailerAsDate(final String trailer) throws IOException, IllegalArgumentException {
        String raw_date = this.getTrailer(trailer);
        if (raw_date == null) {
            return null;
        }
        if (raw_date.toUpperCase().indexOf("GMT") == -1 && raw_date.indexOf(32) > 0) {
            raw_date = String.valueOf(raw_date) + " GMT";
        }
        Date date;
        try {
            date = Util.parseHttpDate(raw_date);
        }
        catch (final IllegalArgumentException iae) {
            long time;
            try {
                time = Long.parseLong(raw_date);
            }
            catch (final NumberFormatException ex) {
                throw iae;
            }
            if (time < 0L) {
                time = 0L;
            }
            date = new Date(time * 1000L);
        }
        return date;
    }
    
    public void setTrailer(final String trailer, final String value) {
        this.Trailers.put(trailer.trim(), value.trim());
    }
    
    public void deleteTrailer(final String trailer) {
        this.Trailers.remove(trailer.trim());
    }
    
    public synchronized byte[] getData() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        if (this.Data == null) {
            try {
                this.readResponseData(this.inp_stream);
            }
            catch (final InterruptedIOException ie) {
                throw ie;
            }
            catch (final IOException ioe) {
                Log.write(2, "Resp:  (" + this.inp_stream.hashCode() + ")", ioe);
                try {
                    this.inp_stream.close();
                }
                catch (final Exception ex) {}
                throw ioe;
            }
            this.inp_stream.close();
        }
        return this.Data;
    }
    
    public synchronized InputStream getInputStream() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        if (this.Data == null) {
            return this.inp_stream;
        }
        return new ByteArrayInputStream(this.Data);
    }
    
    public synchronized boolean hasEntity() throws IOException {
        if (!this.got_headers) {
            this.getHeaders(true);
        }
        return this.cd_type != 2;
    }
    
    public void setRetryRequest(final boolean flag) {
        this.retry = flag;
    }
    
    public boolean retryRequest() {
        return this.retry;
    }
    
    private synchronized void getHeaders(final boolean skip_cont) throws IOException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        HTTPClient/Response.got_headers:Z
        //     4: ifeq            8
        //     7: return         
        //     8: aload_0         /* this */
        //     9: getfield        HTTPClient/Response.exception:Ljava/io/IOException;
        //    12: ifnull          28
        //    15: aload_0         /* this */
        //    16: getfield        HTTPClient/Response.exception:Ljava/io/IOException;
        //    19: invokevirtual   java/lang/Throwable.fillInStackTrace:()Ljava/lang/Throwable;
        //    22: pop            
        //    23: aload_0         /* this */
        //    24: getfield        HTTPClient/Response.exception:Ljava/io/IOException;
        //    27: athrow         
        //    28: aload_0         /* this */
        //    29: iconst_1       
        //    30: putfield        HTTPClient/Response.reading_headers:Z
        //    33: aload_0         /* this */
        //    34: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //    37: invokevirtual   java/util/Hashtable.clear:()V
        //    40: aload_0         /* this */
        //    41: aload_0         /* this */
        //    42: getfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //    45: invokespecial   HTTPClient/Response.readResponseHeaders:(Ljava/io/InputStream;)Ljava/lang/String;
        //    48: astore          headers
        //    50: aload_0         /* this */
        //    51: aload           headers
        //    53: invokespecial   HTTPClient/Response.parseResponseHeaders:(Ljava/lang/String;)V
        //    56: aload_0         /* this */
        //    57: getfield        HTTPClient/Response.StatusCode:I
        //    60: bipush          100
        //    62: if_icmpne       69
        //    65: iload_1         /* skip_cont */
        //    66: ifne            33
        //    69: aload_0         /* this */
        //    70: getfield        HTTPClient/Response.StatusCode:I
        //    73: bipush          101
        //    75: if_icmple       138
        //    78: aload_0         /* this */
        //    79: getfield        HTTPClient/Response.StatusCode:I
        //    82: sipush          200
        //    85: if_icmplt       33
        //    88: goto            138
        //    91: astore          null
        //    93: aload           4
        //    95: instanceof      Ljava/io/InterruptedIOException;
        //    98: ifne            107
        //   101: aload_0         /* this */
        //   102: aload           4
        //   104: putfield        HTTPClient/Response.exception:Ljava/io/IOException;
        //   107: aload           4
        //   109: instanceof      Ljava/net/ProtocolException;
        //   112: ifeq            135
        //   115: aload_0         /* this */
        //   116: iconst_3       
        //   117: putfield        HTTPClient/Response.cd_type:I
        //   120: aload_0         /* this */
        //   121: getfield        HTTPClient/Response.stream_handler:LHTTPClient/StreamDemultiplexor;
        //   124: ifnull          135
        //   127: aload_0         /* this */
        //   128: getfield        HTTPClient/Response.stream_handler:LHTTPClient/StreamDemultiplexor;
        //   131: aload_0         /* this */
        //   132: invokevirtual   HTTPClient/StreamDemultiplexor.markForClose:(LHTTPClient/Response;)V
        //   135: aload           4
        //   137: athrow         
        //   138: jsr             150
        //   141: goto            158
        //   144: astore_2       
        //   145: jsr             150
        //   148: aload_2        
        //   149: athrow         
        //   150: astore_3       
        //   151: aload_0         /* this */
        //   152: iconst_0       
        //   153: putfield        HTTPClient/Response.reading_headers:Z
        //   156: ret             3
        //   158: aload_0         /* this */
        //   159: getfield        HTTPClient/Response.StatusCode:I
        //   162: bipush          100
        //   164: if_icmpne       168
        //   167: return         
        //   168: iconst_m1      
        //   169: istore_2        /* cont_len */
        //   170: aload_0         /* this */
        //   171: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //   174: ldc             "Content-Length"
        //   176: invokevirtual   HTTPClient/CIHashtable.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   179: checkcast       Ljava/lang/String;
        //   182: astore_3        /* cl_hdr */
        //   183: aload_3         /* cl_hdr */
        //   184: ifnull          229
        //   187: aload_3         /* cl_hdr */
        //   188: invokestatic    java/lang/Integer.parseInt:(Ljava/lang/String;)I
        //   191: istore_2        /* cont_len */
        //   192: iload_2         /* cont_len */
        //   193: ifge            229
        //   196: new             Ljava/lang/NumberFormatException;
        //   199: dup            
        //   200: invokespecial   java/lang/NumberFormatException.<init>:()V
        //   203: athrow         
        //   204: pop            
        //   205: new             Ljava/net/ProtocolException;
        //   208: dup            
        //   209: new             Ljava/lang/StringBuffer;
        //   212: dup            
        //   213: ldc             "Invalid Content-length header received: "
        //   215: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //   218: aload_3         /* cl_hdr */
        //   219: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   222: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //   225: invokespecial   java/net/ProtocolException.<init>:(Ljava/lang/String;)V
        //   228: athrow         
        //   229: iconst_0       
        //   230: istore          te_chunked
        //   232: iconst_1       
        //   233: istore          te_is_identity
        //   235: iconst_0       
        //   236: istore          ct_mpbr
        //   238: aconst_null    
        //   239: astore          te_hdr
        //   241: aload_0         /* this */
        //   242: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //   245: ldc             "Transfer-Encoding"
        //   247: invokevirtual   HTTPClient/CIHashtable.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   250: checkcast       Ljava/lang/String;
        //   253: invokestatic    HTTPClient/Util.parseHeader:(Ljava/lang/String;)Ljava/util/Vector;
        //   256: astore          te_hdr
        //   258: goto            262
        //   261: pop            
        //   262: aload           te_hdr
        //   264: ifnull          341
        //   267: aload           te_hdr
        //   269: invokevirtual   java/util/Vector.lastElement:()Ljava/lang/Object;
        //   272: checkcast       LHTTPClient/HttpHeaderElement;
        //   275: invokevirtual   HTTPClient/HttpHeaderElement.getName:()Ljava/lang/String;
        //   278: ldc             "chunked"
        //   280: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   283: istore          te_chunked
        //   285: iconst_0       
        //   286: istore          idx
        //   288: goto            331
        //   291: aload           te_hdr
        //   293: iload           idx
        //   295: invokevirtual   java/util/Vector.elementAt:(I)Ljava/lang/Object;
        //   298: checkcast       LHTTPClient/HttpHeaderElement;
        //   301: invokevirtual   HTTPClient/HttpHeaderElement.getName:()Ljava/lang/String;
        //   304: ldc             "identity"
        //   306: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   309: ifeq            325
        //   312: aload           te_hdr
        //   314: iload           idx
        //   316: iinc            idx, -1
        //   319: invokevirtual   java/util/Vector.removeElementAt:(I)V
        //   322: goto            328
        //   325: iconst_0       
        //   326: istore          te_is_identity
        //   328: iinc            idx, 1
        //   331: iload           idx
        //   333: aload           te_hdr
        //   335: invokevirtual   java/util/Vector.size:()I
        //   338: if_icmplt       291
        //   341: aload_0         /* this */
        //   342: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //   345: ldc             "Content-Type"
        //   347: invokevirtual   HTTPClient/CIHashtable.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   350: checkcast       Ljava/lang/String;
        //   353: dup            
        //   354: astore          hdr
        //   356: ifnull          411
        //   359: aload           hdr
        //   361: invokestatic    HTTPClient/Util.parseHeader:(Ljava/lang/String;)Ljava/util/Vector;
        //   364: astore          phdr
        //   366: aload           phdr
        //   368: new             LHTTPClient/HttpHeaderElement;
        //   371: dup            
        //   372: ldc             "multipart/byteranges"
        //   374: invokespecial   HTTPClient/HttpHeaderElement.<init>:(Ljava/lang/String;)V
        //   377: invokevirtual   java/util/Vector.contains:(Ljava/lang/Object;)Z
        //   380: ifne            404
        //   383: aload           phdr
        //   385: new             LHTTPClient/HttpHeaderElement;
        //   388: dup            
        //   389: ldc             "multipart/x-byteranges"
        //   391: invokespecial   HTTPClient/HttpHeaderElement.<init>:(Ljava/lang/String;)V
        //   394: invokevirtual   java/util/Vector.contains:(Ljava/lang/Object;)Z
        //   397: ifne            404
        //   400: iconst_0       
        //   401: goto            405
        //   404: iconst_1       
        //   405: istore          ct_mpbr
        //   407: goto            411
        //   410: pop            
        //   411: aload_0         /* this */
        //   412: getfield        HTTPClient/Response.StatusCode:I
        //   415: sipush          200
        //   418: if_icmplt       451
        //   421: aload_0         /* this */
        //   422: getfield        HTTPClient/Response.StatusCode:I
        //   425: sipush          204
        //   428: if_icmpeq       451
        //   431: aload_0         /* this */
        //   432: getfield        HTTPClient/Response.StatusCode:I
        //   435: sipush          205
        //   438: if_icmpeq       451
        //   441: aload_0         /* this */
        //   442: getfield        HTTPClient/Response.StatusCode:I
        //   445: sipush          304
        //   448: if_icmpne       459
        //   451: aload_0         /* this */
        //   452: iconst_2       
        //   453: putfield        HTTPClient/Response.cd_type:I
        //   456: goto            624
        //   459: iload           te_chunked
        //   461: ifeq            512
        //   464: aload_0         /* this */
        //   465: iconst_5       
        //   466: putfield        HTTPClient/Response.cd_type:I
        //   469: aload           te_hdr
        //   471: aload           te_hdr
        //   473: invokevirtual   java/util/Vector.size:()I
        //   476: iconst_1       
        //   477: isub           
        //   478: invokevirtual   java/util/Vector.removeElementAt:(I)V
        //   481: aload           te_hdr
        //   483: invokevirtual   java/util/Vector.size:()I
        //   486: ifle            503
        //   489: aload_0         /* this */
        //   490: ldc             "Transfer-Encoding"
        //   492: aload           te_hdr
        //   494: invokestatic    HTTPClient/Util.assembleHeader:(Ljava/util/Vector;)Ljava/lang/String;
        //   497: invokevirtual   HTTPClient/Response.setHeader:(Ljava/lang/String;Ljava/lang/String;)V
        //   500: goto            624
        //   503: aload_0         /* this */
        //   504: ldc             "Transfer-Encoding"
        //   506: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   509: goto            624
        //   512: iload_2         /* cont_len */
        //   513: iconst_m1      
        //   514: if_icmpeq       530
        //   517: iload           te_is_identity
        //   519: ifeq            530
        //   522: aload_0         /* this */
        //   523: iconst_4       
        //   524: putfield        HTTPClient/Response.cd_type:I
        //   527: goto            624
        //   530: iload           ct_mpbr
        //   532: ifeq            549
        //   535: iload           te_is_identity
        //   537: ifeq            549
        //   540: aload_0         /* this */
        //   541: bipush          6
        //   543: putfield        HTTPClient/Response.cd_type:I
        //   546: goto            624
        //   549: aload_0         /* this */
        //   550: getfield        HTTPClient/Response.method:Ljava/lang/String;
        //   553: ldc             "HEAD"
        //   555: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   558: ifne            624
        //   561: aload_0         /* this */
        //   562: iconst_3       
        //   563: putfield        HTTPClient/Response.cd_type:I
        //   566: aload_0         /* this */
        //   567: getfield        HTTPClient/Response.stream_handler:LHTTPClient/StreamDemultiplexor;
        //   570: ifnull          581
        //   573: aload_0         /* this */
        //   574: getfield        HTTPClient/Response.stream_handler:LHTTPClient/StreamDemultiplexor;
        //   577: aload_0         /* this */
        //   578: invokevirtual   HTTPClient/StreamDemultiplexor.markForClose:(LHTTPClient/Response;)V
        //   581: aload_0         /* this */
        //   582: getfield        HTTPClient/Response.Version:Ljava/lang/String;
        //   585: ldc             "HTTP/0.9"
        //   587: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   590: ifeq            624
        //   593: aload_0         /* this */
        //   594: new             Ljava/io/SequenceInputStream;
        //   597: dup            
        //   598: new             Ljava/io/ByteArrayInputStream;
        //   601: dup            
        //   602: aload_0         /* this */
        //   603: getfield        HTTPClient/Response.Data:[B
        //   606: invokespecial   java/io/ByteArrayInputStream.<init>:([B)V
        //   609: aload_0         /* this */
        //   610: getfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //   613: invokespecial   java/io/SequenceInputStream.<init>:(Ljava/io/InputStream;Ljava/io/InputStream;)V
        //   616: putfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //   619: aload_0         /* this */
        //   620: aconst_null    
        //   621: putfield        HTTPClient/Response.Data:[B
        //   624: aload_0         /* this */
        //   625: getfield        HTTPClient/Response.cd_type:I
        //   628: iconst_4       
        //   629: if_icmpne       640
        //   632: aload_0         /* this */
        //   633: iload_2         /* cont_len */
        //   634: putfield        HTTPClient/Response.ContentLength:I
        //   637: goto            646
        //   640: aload_0         /* this */
        //   641: ldc             "Content-Length"
        //   643: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   646: aload_0         /* this */
        //   647: getfield        HTTPClient/Response.method:Ljava/lang/String;
        //   650: ldc             "HEAD"
        //   652: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   655: ifeq            663
        //   658: aload_0         /* this */
        //   659: iconst_2       
        //   660: putfield        HTTPClient/Response.cd_type:I
        //   663: aload_0         /* this */
        //   664: getfield        HTTPClient/Response.cd_type:I
        //   667: iconst_2       
        //   668: if_icmpne       690
        //   671: aload_0         /* this */
        //   672: iconst_0       
        //   673: putfield        HTTPClient/Response.ContentLength:I
        //   676: aload_0         /* this */
        //   677: iconst_0       
        //   678: newarray        B
        //   680: putfield        HTTPClient/Response.Data:[B
        //   683: aload_0         /* this */
        //   684: getfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //   687: invokevirtual   java/io/InputStream.close:()V
        //   690: iconst_2       
        //   691: new             Ljava/lang/StringBuffer;
        //   694: dup            
        //   695: ldc             "Resp:  Response entity delimiter: "
        //   697: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //   700: aload_0         /* this */
        //   701: getfield        HTTPClient/Response.cd_type:I
        //   704: iconst_2       
        //   705: if_icmpne       713
        //   708: ldc             "No Entity"
        //   710: goto            768
        //   713: aload_0         /* this */
        //   714: getfield        HTTPClient/Response.cd_type:I
        //   717: iconst_3       
        //   718: if_icmpne       726
        //   721: ldc             "Close"
        //   723: goto            768
        //   726: aload_0         /* this */
        //   727: getfield        HTTPClient/Response.cd_type:I
        //   730: iconst_4       
        //   731: if_icmpne       739
        //   734: ldc             "Content-Length"
        //   736: goto            768
        //   739: aload_0         /* this */
        //   740: getfield        HTTPClient/Response.cd_type:I
        //   743: iconst_5       
        //   744: if_icmpne       752
        //   747: ldc             "Chunked"
        //   749: goto            768
        //   752: aload_0         /* this */
        //   753: getfield        HTTPClient/Response.cd_type:I
        //   756: bipush          6
        //   758: if_icmpne       766
        //   761: ldc             "Multipart"
        //   763: goto            768
        //   766: ldc             "???"
        //   768: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   771: ldc             " ("
        //   773: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   776: aload_0         /* this */
        //   777: getfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //   780: invokevirtual   java/lang/Object.hashCode:()I
        //   783: invokevirtual   java/lang/StringBuffer.append:(I)Ljava/lang/StringBuffer;
        //   786: ldc             ")"
        //   788: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   791: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //   794: invokestatic    HTTPClient/Log.write:(ILjava/lang/String;)V
        //   797: aload_0         /* this */
        //   798: getfield        HTTPClient/Response.connection:LHTTPClient/HTTPConnection;
        //   801: getfield        HTTPClient/HTTPConnection.ServerProtocolVersion:I
        //   804: ldc             65537
        //   806: if_icmplt       818
        //   809: aload_0         /* this */
        //   810: ldc             "Proxy-Connection"
        //   812: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   815: goto            1077
        //   818: aload_0         /* this */
        //   819: getfield        HTTPClient/Response.connection:LHTTPClient/HTTPConnection;
        //   822: invokevirtual   HTTPClient/HTTPConnection.getProxyHost:()Ljava/lang/String;
        //   825: ifnull          837
        //   828: aload_0         /* this */
        //   829: ldc             "Connection"
        //   831: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   834: goto            843
        //   837: aload_0         /* this */
        //   838: ldc             "Proxy-Connection"
        //   840: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   843: aload_0         /* this */
        //   844: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //   847: ldc             "Connection"
        //   849: invokevirtual   HTTPClient/CIHashtable.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   852: checkcast       Ljava/lang/String;
        //   855: invokestatic    HTTPClient/Util.parseHeader:(Ljava/lang/String;)Ljava/util/Vector;
        //   858: astore          pco
        //   860: goto            867
        //   863: pop            
        //   864: aconst_null    
        //   865: astore          pco
        //   867: aload           pco
        //   869: ifnull          960
        //   872: iconst_0       
        //   873: istore          idx
        //   875: goto            922
        //   878: aload           pco
        //   880: iload           idx
        //   882: invokevirtual   java/util/Vector.elementAt:(I)Ljava/lang/Object;
        //   885: checkcast       LHTTPClient/HttpHeaderElement;
        //   888: invokevirtual   HTTPClient/HttpHeaderElement.getName:()Ljava/lang/String;
        //   891: astore          name
        //   893: aload           name
        //   895: ldc             "keep-alive"
        //   897: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   900: ifne            919
        //   903: aload           pco
        //   905: iload           idx
        //   907: invokevirtual   java/util/Vector.removeElementAt:(I)V
        //   910: aload_0         /* this */
        //   911: aload           name
        //   913: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   916: iinc            idx, -1
        //   919: iinc            idx, 1
        //   922: iload           idx
        //   924: aload           pco
        //   926: invokevirtual   java/util/Vector.size:()I
        //   929: if_icmplt       878
        //   932: aload           pco
        //   934: invokevirtual   java/util/Vector.size:()I
        //   937: ifle            954
        //   940: aload_0         /* this */
        //   941: ldc             "Connection"
        //   943: aload           pco
        //   945: invokestatic    HTTPClient/Util.assembleHeader:(Ljava/util/Vector;)Ljava/lang/String;
        //   948: invokevirtual   HTTPClient/Response.setHeader:(Ljava/lang/String;Ljava/lang/String;)V
        //   951: goto            960
        //   954: aload_0         /* this */
        //   955: ldc             "Connection"
        //   957: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //   960: aload_0         /* this */
        //   961: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //   964: ldc             "Proxy-Connection"
        //   966: invokevirtual   HTTPClient/CIHashtable.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   969: checkcast       Ljava/lang/String;
        //   972: invokestatic    HTTPClient/Util.parseHeader:(Ljava/lang/String;)Ljava/util/Vector;
        //   975: astore          pco
        //   977: goto            984
        //   980: pop            
        //   981: aconst_null    
        //   982: astore          pco
        //   984: aload           pco
        //   986: ifnull          1077
        //   989: iconst_0       
        //   990: istore          idx
        //   992: goto            1039
        //   995: aload           pco
        //   997: iload           idx
        //   999: invokevirtual   java/util/Vector.elementAt:(I)Ljava/lang/Object;
        //  1002: checkcast       LHTTPClient/HttpHeaderElement;
        //  1005: invokevirtual   HTTPClient/HttpHeaderElement.getName:()Ljava/lang/String;
        //  1008: astore          name
        //  1010: aload           name
        //  1012: ldc             "keep-alive"
        //  1014: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1017: ifne            1036
        //  1020: aload           pco
        //  1022: iload           idx
        //  1024: invokevirtual   java/util/Vector.removeElementAt:(I)V
        //  1027: aload_0         /* this */
        //  1028: aload           name
        //  1030: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //  1033: iinc            idx, -1
        //  1036: iinc            idx, 1
        //  1039: iload           idx
        //  1041: aload           pco
        //  1043: invokevirtual   java/util/Vector.size:()I
        //  1046: if_icmplt       995
        //  1049: aload           pco
        //  1051: invokevirtual   java/util/Vector.size:()I
        //  1054: ifle            1071
        //  1057: aload_0         /* this */
        //  1058: ldc             "Proxy-Connection"
        //  1060: aload           pco
        //  1062: invokestatic    HTTPClient/Util.assembleHeader:(Ljava/util/Vector;)Ljava/lang/String;
        //  1065: invokevirtual   HTTPClient/Response.setHeader:(Ljava/lang/String;Ljava/lang/String;)V
        //  1068: goto            1077
        //  1071: aload_0         /* this */
        //  1072: ldc             "Proxy-Connection"
        //  1074: invokevirtual   HTTPClient/Response.deleteHeader:(Ljava/lang/String;)V
        //  1077: aload_0         /* this */
        //  1078: iconst_1       
        //  1079: putfield        HTTPClient/Response.got_headers:Z
        //  1082: aload_0         /* this */
        //  1083: getfield        HTTPClient/Response.isFirstResponse:Z
        //  1086: ifeq            1222
        //  1089: aload_0         /* this */
        //  1090: getfield        HTTPClient/Response.connection:LHTTPClient/HTTPConnection;
        //  1093: aload_0         /* this */
        //  1094: getfield        HTTPClient/Response.req:LHTTPClient/Request;
        //  1097: aload_0         /* this */
        //  1098: invokevirtual   HTTPClient/HTTPConnection.handleFirstRequest:(LHTTPClient/Request;LHTTPClient/Response;)Z
        //  1101: ifne            1222
        //  1104: aload_0         /* this */
        //  1105: getfield        HTTPClient/Response.connection:LHTTPClient/HTTPConnection;
        //  1108: aload_0         /* this */
        //  1109: getfield        HTTPClient/Response.req:LHTTPClient/Request;
        //  1112: aload_0         /* this */
        //  1113: getfield        HTTPClient/Response.timeout:I
        //  1116: invokevirtual   HTTPClient/HTTPConnection.sendRequest:(LHTTPClient/Request;I)LHTTPClient/Response;
        //  1119: astore          8
        //  1121: goto            1139
        //  1124: astore          me
        //  1126: new             Ljava/io/IOException;
        //  1129: dup            
        //  1130: aload           me
        //  1132: invokevirtual   java/lang/Throwable.toString:()Ljava/lang/String;
        //  1135: invokespecial   java/io/IOException.<init>:(Ljava/lang/String;)V
        //  1138: athrow         
        //  1139: aload           resp
        //  1141: invokevirtual   HTTPClient/Response.getVersion:()Ljava/lang/String;
        //  1144: pop            
        //  1145: aload_0         /* this */
        //  1146: aload           resp
        //  1148: getfield        HTTPClient/Response.StatusCode:I
        //  1151: putfield        HTTPClient/Response.StatusCode:I
        //  1154: aload_0         /* this */
        //  1155: aload           resp
        //  1157: getfield        HTTPClient/Response.ReasonLine:Ljava/lang/String;
        //  1160: putfield        HTTPClient/Response.ReasonLine:Ljava/lang/String;
        //  1163: aload_0         /* this */
        //  1164: aload           resp
        //  1166: getfield        HTTPClient/Response.Version:Ljava/lang/String;
        //  1169: putfield        HTTPClient/Response.Version:Ljava/lang/String;
        //  1172: aload_0         /* this */
        //  1173: aload           resp
        //  1175: getfield        HTTPClient/Response.EffectiveURI:LHTTPClient/URI;
        //  1178: putfield        HTTPClient/Response.EffectiveURI:LHTTPClient/URI;
        //  1181: aload_0         /* this */
        //  1182: aload           resp
        //  1184: getfield        HTTPClient/Response.ContentLength:I
        //  1187: putfield        HTTPClient/Response.ContentLength:I
        //  1190: aload_0         /* this */
        //  1191: aload           resp
        //  1193: getfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //  1196: putfield        HTTPClient/Response.Headers:LHTTPClient/CIHashtable;
        //  1199: aload_0         /* this */
        //  1200: aload           resp
        //  1202: getfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //  1205: putfield        HTTPClient/Response.inp_stream:Ljava/io/InputStream;
        //  1208: aload_0         /* this */
        //  1209: aload           resp
        //  1211: getfield        HTTPClient/Response.Data:[B
        //  1214: putfield        HTTPClient/Response.Data:[B
        //  1217: aload_0         /* this */
        //  1218: aconst_null    
        //  1219: putfield        HTTPClient/Response.req:LHTTPClient/Request;
        //  1222: return         
        //    Exceptions:
        //  throws java.io.IOException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  33     88     91     138    Ljava/io/IOException;
        //  33     138    144    150    Any
        //  187    204    204    229    Ljava/lang/NumberFormatException;
        //  241    258    261    262    LHTTPClient/ParseException;
        //  341    407    410    411    LHTTPClient/ParseException;
        //  843    860    863    867    LHTTPClient/ParseException;
        //  960    977    980    984    LHTTPClient/ParseException;
        //  1104   1121   1124   1139   LHTTPClient/ModuleException;
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2945)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2501)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private String readResponseHeaders(final InputStream inp) throws IOException {
        if (this.buf_pos == 0) {
            Log.write(2, "Resp:  Reading Response headers " + this.inp_stream.hashCode());
        }
        else {
            Log.write(2, "Resp:  Resuming reading Response headers " + this.inp_stream.hashCode());
        }
        if (!this.reading_lines) {
            try {
                Label_0168: {
                    if (this.buf_pos == 0) {
                        int c;
                        while ((c = inp.read()) != -1) {
                            if (!Character.isWhitespace((char)c)) {
                                this.buf[0] = (byte)c;
                                this.buf_pos = 1;
                                break Label_0168;
                            }
                        }
                        throw new EOFException("Encountered premature EOF while reading Version");
                    }
                }
                while (this.buf_pos < this.buf.length) {
                    final int got = inp.read(this.buf, this.buf_pos, this.buf.length - this.buf_pos);
                    if (got == -1) {
                        throw new EOFException("Encountered premature EOF while reading Version");
                    }
                    this.buf_pos += got;
                }
            }
            catch (final EOFException eof) {
                Log.write(2, "Resp:  (" + this.inp_stream.hashCode() + ")", eof);
                throw eof;
            }
            for (int idx = 0; idx < this.buf.length; ++idx) {
                this.hdrs.append((char)this.buf[idx]);
            }
            this.reading_lines = true;
        }
        if (this.hdrs.toString().startsWith("HTTP/") || this.hdrs.toString().startsWith("HTTP ")) {
            this.readLines(inp);
        }
        this.buf_pos = 0;
        this.reading_lines = false;
        this.bol = true;
        this.got_cr = false;
        final String tmp = this.hdrs.toString();
        this.hdrs.setLength(0);
        return tmp;
    }
    
    void readTrailers(final InputStream inp) throws IOException {
        try {
            this.readLines(inp);
            this.trailers_read = true;
        }
        catch (final IOException ioe) {
            if (!(ioe instanceof InterruptedIOException)) {
                this.exception = ioe;
            }
            throw ioe;
        }
    }
    
    private void readLines(final InputStream inp) throws IOException {
        while (true) {
            final int b = inp.read();
            switch (b) {
                case -1: {
                    throw new EOFException("Encountered premature EOF while reading headers:\n" + (Object)this.hdrs);
                }
                case 13: {
                    this.got_cr = true;
                    continue;
                }
                case 10: {
                    if (!this.bol) {
                        this.hdrs.append('\n');
                        this.bol = true;
                        this.got_cr = false;
                        continue;
                    }
                    return;
                }
                case 9:
                case 32: {
                    if (this.bol) {
                        this.hdrs.setCharAt(this.hdrs.length() - 1, ' ');
                        this.bol = false;
                        continue;
                    }
                    break;
                }
            }
            if (this.got_cr) {
                this.hdrs.append('\r');
                this.got_cr = false;
            }
            this.hdrs.append((char)(b & 0xFF));
            this.bol = false;
        }
    }
    
    private void parseResponseHeaders(final String headers) throws ProtocolException {
        String sts_line = null;
        final StringTokenizer lines = new StringTokenizer(headers, "\r\n");
        if (Log.isEnabled(2)) {
            Log.write(2, "Resp:  Parsing Response headers from Request \"" + this.method + " " + this.resource + "\":  (" + this.inp_stream.hashCode() + ")\n\n" + headers);
        }
        if (!headers.regionMatches(true, 0, "HTTP/", 0, 5) && !headers.regionMatches(true, 0, "HTTP ", 0, 5)) {
            this.Version = "HTTP/0.9";
            this.StatusCode = 200;
            this.ReasonLine = "OK";
            try {
                this.Data = headers.getBytes("8859_1");
            }
            catch (final UnsupportedEncodingException uee) {
                throw new Error(uee.toString());
            }
            return;
        }
        StringTokenizer elem;
        try {
            sts_line = lines.nextToken();
            elem = new StringTokenizer(sts_line, " \t");
            this.Version = elem.nextToken();
            this.StatusCode = Integer.valueOf(elem.nextToken());
            if (this.Version.equalsIgnoreCase("HTTP")) {
                this.Version = "HTTP/1.0";
            }
        }
        catch (final NoSuchElementException ex) {
            throw new ProtocolException("Invalid HTTP status line received: " + sts_line);
        }
        try {
            this.ReasonLine = elem.nextToken("").trim();
        }
        catch (final NoSuchElementException ex2) {
            this.ReasonLine = "";
        }
        if (this.StatusCode >= 300 && this.sent_entity && this.stream_handler != null) {
            this.stream_handler.markForClose(this);
        }
        this.parseHeaderFields(lines, this.Headers);
        if (this.Headers.get("Trailer") != null && this.resp_inp_stream != null) {
            this.resp_inp_stream.dontTruncate();
        }
        int vers;
        if (this.Version.equalsIgnoreCase("HTTP/0.9") || this.Version.equalsIgnoreCase("HTTP/1.0")) {
            vers = 0;
        }
        else {
            vers = 1;
        }
        try {
            final String con = (String)this.Headers.get("Connection");
            final String pcon = (String)this.Headers.get("Proxy-Connection");
            if (((vers == 1 && con != null && Util.hasToken(con, "close")) || (vers == 0 && (this.used_proxy || con == null || !Util.hasToken(con, "keep-alive")) && (!this.used_proxy || pcon == null || !Util.hasToken(pcon, "keep-alive")))) && this.stream_handler != null) {
                this.stream_handler.markForClose(this);
            }
        }
        catch (final ParseException ex3) {}
    }
    
    private synchronized void getTrailers() throws IOException {
        if (this.got_trailers) {
            return;
        }
        if (this.exception != null) {
            this.exception.fillInStackTrace();
            throw this.exception;
        }
        Log.write(2, "Resp:  Reading Response trailers " + this.inp_stream.hashCode());
        try {
            if (!this.trailers_read && this.resp_inp_stream != null) {
                this.resp_inp_stream.readAll(this.timeout);
            }
            if (this.trailers_read) {
                Log.write(2, "Resp:  Parsing Response trailers from Request \"" + this.method + " " + this.resource + "\":  (" + this.inp_stream.hashCode() + ")\n\n" + (Object)this.hdrs);
                this.parseHeaderFields(new StringTokenizer(this.hdrs.toString(), "\r\n"), this.Trailers);
            }
        }
        finally {
            this.got_trailers = true;
        }
    }
    
    private void parseHeaderFields(final StringTokenizer lines, final CIHashtable list) throws ProtocolException {
        while (lines.hasMoreTokens()) {
            final String hdr = lines.nextToken();
            int sep = hdr.indexOf(58);
            if (sep == -1) {
                sep = hdr.indexOf(32);
            }
            if (sep == -1) {
                throw new ProtocolException("Invalid HTTP header received: " + hdr);
            }
            final String hdr_name = hdr.substring(0, sep).trim();
            final String hdr_value = hdr.substring(sep + 1).trim();
            if (!Response.singleValueHeaders.containsKey(hdr_name.toLowerCase())) {
                final String old_value = (String)list.get(hdr_name);
                if (old_value == null) {
                    list.put(hdr_name, hdr_value);
                }
                else {
                    list.put(hdr_name, String.valueOf(old_value) + ", " + hdr_value);
                }
            }
            else {
                list.put(hdr_name, hdr_value);
            }
        }
    }
    
    private void readResponseData(final InputStream inp) throws IOException {
        if (this.ContentLength == 0) {
            return;
        }
        if (this.Data == null) {
            this.Data = new byte[0];
        }
        int off = this.Data.length;
        try {
            if (this.getHeader("Content-Length") != null) {
                int rcvd = 0;
                this.Data = new byte[this.ContentLength];
                do {
                    off += rcvd;
                    rcvd = inp.read(this.Data, off, this.ContentLength - off);
                    if (rcvd != -1) {
                        continue;
                    }
                    break;
                } while (off + rcvd < this.ContentLength);
            }
            else {
                final int inc = 1000;
                int rcvd2 = 0;
                do {
                    off += rcvd2;
                    this.Data = Util.resizeArray(this.Data, off + inc);
                } while ((rcvd2 = inp.read(this.Data, off, inc)) != -1);
                this.Data = Util.resizeArray(this.Data, off);
            }
        }
        catch (final IOException ioe) {
            this.Data = Util.resizeArray(this.Data, off);
            throw ioe;
        }
        finally {
            try {
                inp.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    void markAsFirstResponse(final Request req) {
        this.req = req;
        this.isFirstResponse = true;
    }
    
    public Object clone() {
        Response cl;
        try {
            cl = (Response)super.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            throw new InternalError(cnse.toString());
        }
        cl.Headers = (CIHashtable)this.Headers.clone();
        cl.Trailers = (CIHashtable)this.Trailers.clone();
        return cl;
    }
    
    static {
        final String[] singleValueHeaderNames = { "age", "location", "content-base", "content-length", "content-location", "content-md5", "content-range", "content-type", "date", "etag", "expires", "proxy-authenticate", "retry-after" };
        singleValueHeaders = new Hashtable(singleValueHeaderNames.length);
        for (int idx = 0; idx < singleValueHeaderNames.length; ++idx) {
            Response.singleValueHeaders.put(singleValueHeaderNames[idx], singleValueHeaderNames[idx]);
        }
    }
}
