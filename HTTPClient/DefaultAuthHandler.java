package HTTPClient;

import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Vector;
import java.io.IOException;

public class DefaultAuthHandler implements AuthorizationHandler, GlobalConstants
{
    private static final byte[] NUL;
    private static final int DI_A1 = 0;
    private static final int DI_A1S = 1;
    private static final int DI_QOP = 2;
    private static byte[] digest_secret;
    private static AuthorizationPrompter prompter;
    private static boolean prompterSet;
    
    public AuthorizationInfo fixupAuthInfo(final AuthorizationInfo info, final RoRequest req, final AuthorizationInfo challenge, final RoResponse resp) throws AuthSchemeNotImplException {
        if (info.getScheme().equalsIgnoreCase("Basic") || info.getScheme().equalsIgnoreCase("SOCKS5")) {
            return info;
        }
        if (!info.getScheme().equalsIgnoreCase("Digest")) {
            throw new AuthSchemeNotImplException(info.getScheme());
        }
        if (Log.isEnabled(8)) {
            Log.write(8, "Auth:  fixing up Authorization for host " + info.getHost() + ":" + info.getPort() + "; scheme: " + info.getScheme() + "; realm: " + info.getRealm());
        }
        return digest_fixup(info, req, challenge, resp);
    }
    
    public AuthorizationInfo getAuthorization(final AuthorizationInfo challenge, final RoRequest req, final RoResponse resp) throws AuthSchemeNotImplException, IOException {
        if (Log.isEnabled(8)) {
            Log.write(8, "Auth:  Requesting Authorization for host " + challenge.getHost() + ":" + challenge.getPort() + "; scheme: " + challenge.getScheme() + "; realm: " + challenge.getRealm());
        }
        if (!challenge.getScheme().equalsIgnoreCase("Basic") && !challenge.getScheme().equalsIgnoreCase("Digest") && !challenge.getScheme().equalsIgnoreCase("SOCKS5")) {
            throw new AuthSchemeNotImplException(challenge.getScheme());
        }
        if (challenge.getScheme().equalsIgnoreCase("Digest")) {
            final AuthorizationInfo cred = digest_check_stale(challenge, req, resp);
            if (cred != null) {
                return cred;
            }
        }
        NVPair answer;
        synchronized (this.getClass()) {
            if (!req.allowUI() || (DefaultAuthHandler.prompterSet && DefaultAuthHandler.prompter == null)) {
                final AuthorizationInfo authorizationInfo = null;
                monitorexit(this.getClass());
                return authorizationInfo;
            }
            if (DefaultAuthHandler.prompter == null) {
                setDefaultPrompter();
            }
            answer = DefaultAuthHandler.prompter.getUsernamePassword(challenge, resp.getStatusCode() == 407);
            monitorexit(this.getClass());
        }
        if (answer == null) {
            return null;
        }
        AuthorizationInfo cred;
        if (challenge.getScheme().equalsIgnoreCase("basic")) {
            cred = new AuthorizationInfo(challenge.getHost(), challenge.getPort(), challenge.getScheme(), challenge.getRealm(), Codecs.base64Encode(String.valueOf(answer.getName()) + ":" + answer.getValue()));
        }
        else if (challenge.getScheme().equalsIgnoreCase("Digest")) {
            cred = digest_gen_auth_info(challenge.getHost(), challenge.getPort(), challenge.getRealm(), answer.getName(), answer.getValue(), req.getConnection().getContext());
            cred = digest_fixup(cred, req, challenge, null);
        }
        else {
            final NVPair[] upwd = { answer };
            cred = new AuthorizationInfo(challenge.getHost(), challenge.getPort(), challenge.getScheme(), challenge.getRealm(), upwd, null);
        }
        answer = null;
        System.gc();
        Log.write(8, "Auth:  Got Authorization");
        return cred;
    }
    
    public void handleAuthHeaders(final Response resp, final RoRequest req, final AuthorizationInfo prev, final AuthorizationInfo prxy) throws IOException {
        String auth_info = resp.getHeader("Authentication-Info");
        String prxy_info = resp.getHeader("Proxy-Authentication-Info");
        if (auth_info == null && prev != null && hasParam(prev.getParams(), "qop", "auth-int")) {
            auth_info = "";
        }
        if (prxy_info == null && prxy != null && hasParam(prxy.getParams(), "qop", "auth-int")) {
            prxy_info = "";
        }
        try {
            handleAuthInfo(auth_info, "Authentication-Info", prev, resp, req, true);
            handleAuthInfo(prxy_info, "Proxy-Authentication-Info", prxy, resp, req, true);
        }
        catch (final ParseException pe) {
            throw new IOException(pe.toString());
        }
    }
    
    public void handleAuthTrailers(final Response resp, final RoRequest req, final AuthorizationInfo prev, final AuthorizationInfo prxy) throws IOException {
        final String auth_info = resp.getTrailer("Authentication-Info");
        final String prxy_info = resp.getTrailer("Proxy-Authentication-Info");
        try {
            handleAuthInfo(auth_info, "Authentication-Info", prev, resp, req, false);
            handleAuthInfo(prxy_info, "Proxy-Authentication-Info", prxy, resp, req, false);
        }
        catch (final ParseException pe) {
            throw new IOException(pe.toString());
        }
    }
    
    private static void handleAuthInfo(final String auth_info, final String hdr_name, final AuthorizationInfo prev, final Response resp, final RoRequest req, final boolean in_headers) throws ParseException, IOException {
        if (auth_info == null) {
            return;
        }
        final Vector pai = Util.parseHeader(auth_info);
        HttpHeaderElement elem;
        if (handle_nextnonce(prev, req, elem = Util.getElement(pai, "nextnonce"))) {
            pai.removeElement(elem);
        }
        if (handle_discard(prev, req, elem = Util.getElement(pai, "discard"))) {
            pai.removeElement(elem);
        }
        if (in_headers) {
            HttpHeaderElement qop = null;
            if (pai != null && (qop = Util.getElement(pai, "qop")) != null && qop.getValue() != null) {
                handle_rspauth(prev, resp, req, pai, hdr_name);
            }
            else if (prev != null && ((Util.hasToken(resp.getHeader("Trailer"), hdr_name) && hasParam(prev.getParams(), "qop", null)) || hasParam(prev.getParams(), "qop", "auth-int"))) {
                handle_rspauth(prev, resp, req, null, hdr_name);
            }
            else if ((pai != null && qop == null && pai.contains(new HttpHeaderElement("digest"))) || (Util.hasToken(resp.getHeader("Trailer"), hdr_name) && prev != null && !hasParam(prev.getParams(), "qop", null))) {
                handle_digest(prev, resp, req, hdr_name);
            }
        }
        if (pai.size() > 0) {
            resp.setHeader(hdr_name, Util.assembleHeader(pai));
        }
        else {
            resp.deleteHeader(hdr_name);
        }
    }
    
    private static final boolean hasParam(final NVPair[] params, final String name, final String val) {
        for (int idx = 0; idx < params.length; ++idx) {
            if (params[idx].getName().equalsIgnoreCase(name) && (val == null || params[idx].getValue().equalsIgnoreCase(val))) {
                return true;
            }
        }
        return false;
    }
    
    private static AuthorizationInfo digest_gen_auth_info(final String host, final int port, final String realm, final String user, final String pass, final Object context) {
        final String A1 = String.valueOf(user) + ":" + realm + ":" + pass;
        final String[] info = { MD5.hexDigest(A1), null, null };
        final AuthorizationInfo prev = AuthorizationInfo.getAuthorization(host, port, "Digest", realm, context);
        NVPair[] params;
        if (prev == null) {
            params = new NVPair[] { new NVPair("username", user), new NVPair("uri", ""), new NVPair("nonce", ""), new NVPair("response", "") };
        }
        else {
            params = prev.getParams();
            for (int idx = 0; idx < params.length; ++idx) {
                if (params[idx].getName().equalsIgnoreCase("username")) {
                    params[idx] = new NVPair("username", user);
                    break;
                }
            }
        }
        return new AuthorizationInfo(host, port, "Digest", realm, params, info);
    }
    
    private static AuthorizationInfo digest_fixup(final AuthorizationInfo info, final RoRequest req, final AuthorizationInfo challenge, final RoResponse resp) throws AuthSchemeNotImplException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore          ch_domain
        //     3: iconst_m1      
        //     4: istore          ch_nonce
        //     6: iconst_m1      
        //     7: istore          ch_alg
        //     9: iconst_m1      
        //    10: istore          ch_opaque
        //    12: iconst_m1      
        //    13: istore          ch_stale
        //    15: iconst_m1      
        //    16: istore          ch_dreq
        //    18: iconst_m1      
        //    19: istore          ch_qop
        //    21: aconst_null    
        //    22: astore          ch_params
        //    24: aload_2         /* challenge */
        //    25: ifnull          180
        //    28: aload_2         /* challenge */
        //    29: invokevirtual   HTTPClient/AuthorizationInfo.getParams:()[LHTTPClient/NVPair;
        //    32: astore          ch_params
        //    34: iconst_0       
        //    35: istore          idx
        //    37: goto            172
        //    40: aload           ch_params
        //    42: iload           idx
        //    44: aaload         
        //    45: invokevirtual   HTTPClient/NVPair.getName:()Ljava/lang/String;
        //    48: invokevirtual   java/lang/String.toLowerCase:()Ljava/lang/String;
        //    51: astore          name
        //    53: aload           name
        //    55: ldc             "domain"
        //    57: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    60: ifeq            70
        //    63: iload           idx
        //    65: istore          ch_domain
        //    67: goto            169
        //    70: aload           name
        //    72: ldc             "nonce"
        //    74: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    77: ifeq            87
        //    80: iload           idx
        //    82: istore          ch_nonce
        //    84: goto            169
        //    87: aload           name
        //    89: ldc             "opaque"
        //    91: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    94: ifeq            104
        //    97: iload           idx
        //    99: istore          ch_opaque
        //   101: goto            169
        //   104: aload           name
        //   106: ldc             "algorithm"
        //   108: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   111: ifeq            121
        //   114: iload           idx
        //   116: istore          ch_alg
        //   118: goto            169
        //   121: aload           name
        //   123: ldc             "stale"
        //   125: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   128: ifeq            138
        //   131: iload           idx
        //   133: istore          ch_stale
        //   135: goto            169
        //   138: aload           name
        //   140: ldc             "digest-required"
        //   142: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   145: ifeq            155
        //   148: iload           idx
        //   150: istore          ch_dreq
        //   152: goto            169
        //   155: aload           name
        //   157: ldc             "qop"
        //   159: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   162: ifeq            169
        //   165: iload           idx
        //   167: istore          ch_qop
        //   169: iinc            idx, 1
        //   172: iload           idx
        //   174: aload           ch_params
        //   176: arraylength    
        //   177: if_icmplt       40
        //   180: iconst_m1      
        //   181: istore          uri
        //   183: iconst_m1      
        //   184: istore          user
        //   186: iconst_m1      
        //   187: istore          alg
        //   189: iconst_m1      
        //   190: istore          response
        //   192: iconst_m1      
        //   193: istore          nonce
        //   195: iconst_m1      
        //   196: istore          cnonce
        //   198: iconst_m1      
        //   199: istore          nc
        //   201: iconst_m1      
        //   202: istore          opaque
        //   204: iconst_m1      
        //   205: istore          digest
        //   207: iconst_m1      
        //   208: istore          dreq
        //   210: iconst_m1      
        //   211: istore          qop
        //   213: aload_0         /* info */
        //   214: astore          25
        //   216: aload           25
        //   218: monitorenter   
        //   219: aload_0         /* info */
        //   220: invokevirtual   HTTPClient/AuthorizationInfo.getParams:()[LHTTPClient/NVPair;
        //   223: astore          params
        //   225: iconst_0       
        //   226: istore          idx
        //   228: goto            431
        //   231: aload           params
        //   233: iload           idx
        //   235: aaload         
        //   236: invokevirtual   HTTPClient/NVPair.getName:()Ljava/lang/String;
        //   239: invokevirtual   java/lang/String.toLowerCase:()Ljava/lang/String;
        //   242: astore          name
        //   244: aload           name
        //   246: ldc             "uri"
        //   248: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   251: ifeq            261
        //   254: iload           idx
        //   256: istore          uri
        //   258: goto            428
        //   261: aload           name
        //   263: ldc             "username"
        //   265: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   268: ifeq            278
        //   271: iload           idx
        //   273: istore          user
        //   275: goto            428
        //   278: aload           name
        //   280: ldc             "algorithm"
        //   282: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   285: ifeq            295
        //   288: iload           idx
        //   290: istore          alg
        //   292: goto            428
        //   295: aload           name
        //   297: ldc             "nonce"
        //   299: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   302: ifeq            312
        //   305: iload           idx
        //   307: istore          nonce
        //   309: goto            428
        //   312: aload           name
        //   314: ldc             "cnonce"
        //   316: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   319: ifeq            329
        //   322: iload           idx
        //   324: istore          cnonce
        //   326: goto            428
        //   329: aload           name
        //   331: ldc             "nc"
        //   333: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   336: ifeq            346
        //   339: iload           idx
        //   341: istore          nc
        //   343: goto            428
        //   346: aload           name
        //   348: ldc             "response"
        //   350: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   353: ifeq            363
        //   356: iload           idx
        //   358: istore          response
        //   360: goto            428
        //   363: aload           name
        //   365: ldc             "opaque"
        //   367: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   370: ifeq            380
        //   373: iload           idx
        //   375: istore          opaque
        //   377: goto            428
        //   380: aload           name
        //   382: ldc             "digest"
        //   384: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   387: ifeq            397
        //   390: iload           idx
        //   392: istore          digest
        //   394: goto            428
        //   397: aload           name
        //   399: ldc             "digest-required"
        //   401: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   404: ifeq            414
        //   407: iload           idx
        //   409: istore          dreq
        //   411: goto            428
        //   414: aload           name
        //   416: ldc             "qop"
        //   418: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   421: ifeq            428
        //   424: iload           idx
        //   426: istore          qop
        //   428: iinc            idx, 1
        //   431: iload           idx
        //   433: aload           params
        //   435: arraylength    
        //   436: if_icmplt       231
        //   439: aload_0         /* info */
        //   440: invokevirtual   HTTPClient/AuthorizationInfo.getExtraInfo:()Ljava/lang/Object;
        //   443: checkcast       [Ljava/lang/String;
        //   446: astore          extra
        //   448: iload           alg
        //   450: iconst_m1      
        //   451: if_icmpeq       522
        //   454: aload           params
        //   456: iload           alg
        //   458: aaload         
        //   459: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   462: ldc             "MD5"
        //   464: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   467: ifne            522
        //   470: aload           params
        //   472: iload           alg
        //   474: aaload         
        //   475: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   478: ldc             "MD5-sess"
        //   480: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   483: ifne            522
        //   486: new             LHTTPClient/AuthSchemeNotImplException;
        //   489: dup            
        //   490: new             Ljava/lang/StringBuffer;
        //   493: dup            
        //   494: ldc             "Digest auth scheme: Algorithm "
        //   496: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //   499: aload           params
        //   501: iload           alg
        //   503: aaload         
        //   504: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   507: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   510: ldc             " not implemented"
        //   512: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   515: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //   518: invokespecial   HTTPClient/AuthSchemeNotImplException.<init>:(Ljava/lang/String;)V
        //   521: athrow         
        //   522: iload           ch_alg
        //   524: iconst_m1      
        //   525: if_icmpeq       596
        //   528: aload           ch_params
        //   530: iload           ch_alg
        //   532: aaload         
        //   533: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   536: ldc             "MD5"
        //   538: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   541: ifne            596
        //   544: aload           ch_params
        //   546: iload           ch_alg
        //   548: aaload         
        //   549: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   552: ldc             "MD5-sess"
        //   554: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   557: ifne            596
        //   560: new             LHTTPClient/AuthSchemeNotImplException;
        //   563: dup            
        //   564: new             Ljava/lang/StringBuffer;
        //   567: dup            
        //   568: ldc             "Digest auth scheme: Algorithm "
        //   570: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //   573: aload           ch_params
        //   575: iload           ch_alg
        //   577: aaload         
        //   578: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   581: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   584: ldc             " not implemented"
        //   586: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //   589: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //   592: invokespecial   HTTPClient/AuthSchemeNotImplException.<init>:(Ljava/lang/String;)V
        //   595: athrow         
        //   596: aload           params
        //   598: iload           uri
        //   600: new             LHTTPClient/NVPair;
        //   603: dup            
        //   604: ldc             "uri"
        //   606: aload_1         /* req */
        //   607: invokeinterface HTTPClient/RoRequest.getRequestURI:()Ljava/lang/String;
        //   612: getstatic       HTTPClient/URI.escpdPathChar:Ljava/util/BitSet;
        //   615: iconst_0       
        //   616: invokestatic    HTTPClient/URI.escape:(Ljava/lang/String;Ljava/util/BitSet;Z)Ljava/lang/String;
        //   619: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   622: aastore        
        //   623: aload           params
        //   625: iload           nonce
        //   627: aaload         
        //   628: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   631: astore          old_nonce
        //   633: iload           ch_nonce
        //   635: iconst_m1      
        //   636: if_icmpeq       665
        //   639: aload           old_nonce
        //   641: aload           ch_params
        //   643: iload           ch_nonce
        //   645: aaload         
        //   646: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   649: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   652: ifne            665
        //   655: aload           params
        //   657: iload           nonce
        //   659: aload           ch_params
        //   661: iload           ch_nonce
        //   663: aaload         
        //   664: aastore        
        //   665: iload           ch_opaque
        //   667: iconst_m1      
        //   668: if_icmpeq       706
        //   671: iload           opaque
        //   673: iconst_m1      
        //   674: if_icmpne       696
        //   677: aload           params
        //   679: aload           params
        //   681: arraylength    
        //   682: iconst_1       
        //   683: iadd           
        //   684: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //   687: astore          params
        //   689: aload           params
        //   691: arraylength    
        //   692: iconst_1       
        //   693: isub           
        //   694: istore          opaque
        //   696: aload           params
        //   698: iload           opaque
        //   700: aload           ch_params
        //   702: iload           ch_opaque
        //   704: aaload         
        //   705: aastore        
        //   706: iload           ch_alg
        //   708: iconst_m1      
        //   709: if_icmpeq       747
        //   712: iload           alg
        //   714: iconst_m1      
        //   715: if_icmpne       737
        //   718: aload           params
        //   720: aload           params
        //   722: arraylength    
        //   723: iconst_1       
        //   724: iadd           
        //   725: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //   728: astore          params
        //   730: aload           params
        //   732: arraylength    
        //   733: iconst_1       
        //   734: isub           
        //   735: istore          alg
        //   737: aload           params
        //   739: iload           alg
        //   741: aload           ch_params
        //   743: iload           ch_alg
        //   745: aaload         
        //   746: aastore        
        //   747: iload           ch_qop
        //   749: iconst_m1      
        //   750: if_icmpne       775
        //   753: iload           ch_alg
        //   755: iconst_m1      
        //   756: if_icmpeq       966
        //   759: aload           ch_params
        //   761: iload           ch_alg
        //   763: aaload         
        //   764: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //   767: ldc             "MD5-sess"
        //   769: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //   772: ifeq            966
        //   775: iload           cnonce
        //   777: iconst_m1      
        //   778: if_icmpne       800
        //   781: aload           params
        //   783: aload           params
        //   785: arraylength    
        //   786: iconst_1       
        //   787: iadd           
        //   788: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //   791: astore          params
        //   793: aload           params
        //   795: arraylength    
        //   796: iconst_1       
        //   797: isub           
        //   798: istore          cnonce
        //   800: getstatic       HTTPClient/DefaultAuthHandler.digest_secret:[B
        //   803: ifnonnull       814
        //   806: bipush          20
        //   808: invokestatic    HTTPClient/DefaultAuthHandler.gen_random_bytes:(I)[B
        //   811: putstatic       HTTPClient/DefaultAuthHandler.digest_secret:[B
        //   814: invokestatic    java/lang/System.currentTimeMillis:()J
        //   817: lstore          l_time
        //   819: bipush          8
        //   821: newarray        B
        //   823: astore          time
        //   825: aload           time
        //   827: iconst_0       
        //   828: lload           l_time
        //   830: ldc2_w          255
        //   833: land           
        //   834: l2i            
        //   835: i2b            
        //   836: bastore        
        //   837: aload           time
        //   839: iconst_1       
        //   840: lload           l_time
        //   842: bipush          8
        //   844: lshr           
        //   845: ldc2_w          255
        //   848: land           
        //   849: l2i            
        //   850: i2b            
        //   851: bastore        
        //   852: aload           time
        //   854: iconst_2       
        //   855: lload           l_time
        //   857: bipush          16
        //   859: lshr           
        //   860: ldc2_w          255
        //   863: land           
        //   864: l2i            
        //   865: i2b            
        //   866: bastore        
        //   867: aload           time
        //   869: iconst_3       
        //   870: lload           l_time
        //   872: bipush          24
        //   874: lshr           
        //   875: ldc2_w          255
        //   878: land           
        //   879: l2i            
        //   880: i2b            
        //   881: bastore        
        //   882: aload           time
        //   884: iconst_4       
        //   885: lload           l_time
        //   887: bipush          32
        //   889: lshr           
        //   890: ldc2_w          255
        //   893: land           
        //   894: l2i            
        //   895: i2b            
        //   896: bastore        
        //   897: aload           time
        //   899: iconst_5       
        //   900: lload           l_time
        //   902: bipush          40
        //   904: lshr           
        //   905: ldc2_w          255
        //   908: land           
        //   909: l2i            
        //   910: i2b            
        //   911: bastore        
        //   912: aload           time
        //   914: bipush          6
        //   916: lload           l_time
        //   918: bipush          48
        //   920: lshr           
        //   921: ldc2_w          255
        //   924: land           
        //   925: l2i            
        //   926: i2b            
        //   927: bastore        
        //   928: aload           time
        //   930: bipush          7
        //   932: lload           l_time
        //   934: bipush          56
        //   936: lshr           
        //   937: ldc2_w          255
        //   940: land           
        //   941: l2i            
        //   942: i2b            
        //   943: bastore        
        //   944: aload           params
        //   946: iload           cnonce
        //   948: new             LHTTPClient/NVPair;
        //   951: dup            
        //   952: ldc             "cnonce"
        //   954: getstatic       HTTPClient/DefaultAuthHandler.digest_secret:[B
        //   957: aload           time
        //   959: invokestatic    HTTPClient/MD5.hexDigest:([B[B)Ljava/lang/String;
        //   962: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   965: aastore        
        //   966: iload           ch_qop
        //   968: iconst_m1      
        //   969: if_icmpeq       1209
        //   972: iload           qop
        //   974: iconst_m1      
        //   975: if_icmpne       997
        //   978: aload           params
        //   980: aload           params
        //   982: arraylength    
        //   983: iconst_1       
        //   984: iadd           
        //   985: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //   988: astore          params
        //   990: aload           params
        //   992: arraylength    
        //   993: iconst_1       
        //   994: isub           
        //   995: istore          qop
        //   997: aload           extra
        //   999: iconst_2       
        //  1000: aload           ch_params
        //  1002: iload           ch_qop
        //  1004: aaload         
        //  1005: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1008: aastore        
        //  1009: aload           extra
        //  1011: iconst_2       
        //  1012: aaload         
        //  1013: ldc             ","
        //  1015: invokestatic    HTTPClient/DefaultAuthHandler.splitList:(Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
        //  1018: astore          qops
        //  1020: aconst_null    
        //  1021: astore          p
        //  1023: iconst_0       
        //  1024: istore          idx
        //  1026: goto            1104
        //  1029: aload           qops
        //  1031: iload           idx
        //  1033: aaload         
        //  1034: ldc             "auth-int"
        //  1036: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1039: ifeq            1084
        //  1042: aload_1         /* req */
        //  1043: invokeinterface HTTPClient/RoRequest.getStream:()LHTTPClient/HttpOutputStream;
        //  1048: ifnull          1077
        //  1051: aload_1         /* req */
        //  1052: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1057: getfield        HTTPClient/HTTPConnection.ServProtVersKnown:Z
        //  1060: ifeq            1084
        //  1063: aload_1         /* req */
        //  1064: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1069: getfield        HTTPClient/HTTPConnection.ServerProtocolVersion:I
        //  1072: ldc             65537
        //  1074: if_icmplt       1084
        //  1077: ldc             "auth-int"
        //  1079: astore          p
        //  1081: goto            1112
        //  1084: aload           qops
        //  1086: iload           idx
        //  1088: aaload         
        //  1089: ldc             "auth"
        //  1091: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1094: ifeq            1101
        //  1097: ldc             "auth"
        //  1099: astore          p
        //  1101: iinc            idx, 1
        //  1104: iload           idx
        //  1106: aload           qops
        //  1108: arraylength    
        //  1109: if_icmplt       1029
        //  1112: aload           p
        //  1114: ifnonnull       1193
        //  1117: iconst_0       
        //  1118: istore          idx
        //  1120: goto            1149
        //  1123: aload           qops
        //  1125: iload           idx
        //  1127: aaload         
        //  1128: ldc             "auth-int"
        //  1130: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1133: ifeq            1146
        //  1136: new             LHTTPClient/AuthSchemeNotImplException;
        //  1139: dup            
        //  1140: ldc             "Digest auth scheme: Can't comply with qop option 'auth-int' because an HttpOutputStream is being used and the server doesn't speak HTTP/1.1"
        //  1142: invokespecial   HTTPClient/AuthSchemeNotImplException.<init>:(Ljava/lang/String;)V
        //  1145: athrow         
        //  1146: iinc            idx, 1
        //  1149: iload           idx
        //  1151: aload           qops
        //  1153: arraylength    
        //  1154: if_icmplt       1123
        //  1157: new             LHTTPClient/AuthSchemeNotImplException;
        //  1160: dup            
        //  1161: new             Ljava/lang/StringBuffer;
        //  1164: dup            
        //  1165: ldc             "Digest auth scheme: None of the available qop options '"
        //  1167: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //  1170: aload           ch_params
        //  1172: iload           ch_qop
        //  1174: aaload         
        //  1175: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1178: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1181: ldc             "' implemented"
        //  1183: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1186: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //  1189: invokespecial   HTTPClient/AuthSchemeNotImplException.<init>:(Ljava/lang/String;)V
        //  1192: athrow         
        //  1193: aload           params
        //  1195: iload           qop
        //  1197: new             LHTTPClient/NVPair;
        //  1200: dup            
        //  1201: ldc             "qop"
        //  1203: aload           p
        //  1205: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1208: aastore        
        //  1209: iload           qop
        //  1211: iconst_m1      
        //  1212: if_icmpeq       1356
        //  1215: iload           nc
        //  1217: iconst_m1      
        //  1218: if_icmpne       1259
        //  1221: aload           params
        //  1223: aload           params
        //  1225: arraylength    
        //  1226: iconst_1       
        //  1227: iadd           
        //  1228: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //  1231: astore          params
        //  1233: aload           params
        //  1235: arraylength    
        //  1236: iconst_1       
        //  1237: isub           
        //  1238: istore          nc
        //  1240: aload           params
        //  1242: iload           nc
        //  1244: new             LHTTPClient/NVPair;
        //  1247: dup            
        //  1248: ldc             "nc"
        //  1250: ldc             "00000001"
        //  1252: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1255: aastore        
        //  1256: goto            1356
        //  1259: aload           old_nonce
        //  1261: aload           params
        //  1263: iload           nonce
        //  1265: aaload         
        //  1266: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1269: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //  1272: ifeq            1340
        //  1275: aload           params
        //  1277: iload           nc
        //  1279: aaload         
        //  1280: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1283: bipush          16
        //  1285: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;I)J
        //  1288: lconst_1       
        //  1289: ladd           
        //  1290: invokestatic    java/lang/Long.toHexString:(J)Ljava/lang/String;
        //  1293: astore          29
        //  1295: aload           params
        //  1297: iload           nc
        //  1299: new             LHTTPClient/NVPair;
        //  1302: dup            
        //  1303: ldc             "nc"
        //  1305: new             Ljava/lang/StringBuffer;
        //  1308: dup            
        //  1309: ldc             "00000000"
        //  1311: aload           29
        //  1313: invokevirtual   java/lang/String.length:()I
        //  1316: invokevirtual   java/lang/String.substring:(I)Ljava/lang/String;
        //  1319: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //  1322: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //  1325: aload           29
        //  1327: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1330: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //  1333: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1336: aastore        
        //  1337: goto            1356
        //  1340: aload           params
        //  1342: iload           nc
        //  1344: new             LHTTPClient/NVPair;
        //  1347: dup            
        //  1348: ldc             "nc"
        //  1350: ldc             "00000001"
        //  1352: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1355: aastore        
        //  1356: aload_2         /* challenge */
        //  1357: ifnull          1460
        //  1360: iload           ch_stale
        //  1362: iconst_m1      
        //  1363: if_icmpeq       1382
        //  1366: aload           ch_params
        //  1368: iload           ch_stale
        //  1370: aaload         
        //  1371: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1374: ldc             "true"
        //  1376: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1379: ifne            1460
        //  1382: iload           alg
        //  1384: iconst_m1      
        //  1385: if_icmpeq       1460
        //  1388: aload           params
        //  1390: iload           alg
        //  1392: aaload         
        //  1393: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1396: ldc             "MD5-sess"
        //  1398: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1401: ifeq            1460
        //  1404: aload           extra
        //  1406: iconst_1       
        //  1407: new             Ljava/lang/StringBuffer;
        //  1410: dup            
        //  1411: aload           extra
        //  1413: iconst_0       
        //  1414: aaload         
        //  1415: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
        //  1418: invokespecial   java/lang/StringBuffer.<init>:(Ljava/lang/String;)V
        //  1421: ldc             ":"
        //  1423: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1426: aload           params
        //  1428: iload           nonce
        //  1430: aaload         
        //  1431: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1434: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1437: ldc             ":"
        //  1439: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1442: aload           params
        //  1444: iload           cnonce
        //  1446: aaload         
        //  1447: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1450: invokevirtual   java/lang/StringBuffer.append:(Ljava/lang/String;)Ljava/lang/StringBuffer;
        //  1453: invokevirtual   java/lang/StringBuffer.toString:()Ljava/lang/String;
        //  1456: invokestatic    HTTPClient/MD5.hexDigest:(Ljava/lang/String;)Ljava/lang/String;
        //  1459: aastore        
        //  1460: aload_0         /* info */
        //  1461: aload           params
        //  1463: invokevirtual   HTTPClient/AuthorizationInfo.setParams:([LHTTPClient/NVPair;)V
        //  1466: aload_0         /* info */
        //  1467: aload           extra
        //  1469: invokevirtual   HTTPClient/AuthorizationInfo.setExtraInfo:(Ljava/lang/Object;)V
        //  1472: aload           25
        //  1474: monitorexit    
        //  1475: goto            1482
        //  1478: aload           25
        //  1480: monitorexit    
        //  1481: athrow         
        //  1482: aconst_null    
        //  1483: astore          hash
        //  1485: iload           qop
        //  1487: iconst_m1      
        //  1488: if_icmpeq       1542
        //  1491: aload           params
        //  1493: iload           qop
        //  1495: aaload         
        //  1496: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1499: ldc             "auth-int"
        //  1501: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1504: ifeq            1542
        //  1507: aload_1         /* req */
        //  1508: invokeinterface HTTPClient/RoRequest.getStream:()LHTTPClient/HttpOutputStream;
        //  1513: ifnonnull       1542
        //  1516: aload_1         /* req */
        //  1517: invokeinterface HTTPClient/RoRequest.getData:()[B
        //  1522: ifnonnull       1531
        //  1525: getstatic       HTTPClient/DefaultAuthHandler.NUL:[B
        //  1528: goto            1537
        //  1531: aload_1         /* req */
        //  1532: invokeinterface HTTPClient/RoRequest.getData:()[B
        //  1537: invokestatic    HTTPClient/MD5.hexDigest:([B)Ljava/lang/String;
        //  1540: astore          hash
        //  1542: aload_1         /* req */
        //  1543: invokeinterface HTTPClient/RoRequest.getStream:()LHTTPClient/HttpOutputStream;
        //  1548: ifnonnull       1592
        //  1551: aload           params
        //  1553: iload           response
        //  1555: new             LHTTPClient/NVPair;
        //  1558: dup            
        //  1559: ldc             "response"
        //  1561: aload           hash
        //  1563: aload           extra
        //  1565: aload           params
        //  1567: iload           alg
        //  1569: iload           uri
        //  1571: iload           qop
        //  1573: iload           nonce
        //  1575: iload           nc
        //  1577: iload           cnonce
        //  1579: aload_1         /* req */
        //  1580: invokeinterface HTTPClient/RoRequest.getMethod:()Ljava/lang/String;
        //  1585: invokestatic    HTTPClient/DefaultAuthHandler.calcResponseAttr:(Ljava/lang/String;[Ljava/lang/String;[LHTTPClient/NVPair;IIIIIILjava/lang/String;)Ljava/lang/String;
        //  1588: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1591: aastore        
        //  1592: iconst_0       
        //  1593: istore          ch_dreq_val
        //  1595: iload           ch_dreq
        //  1597: iconst_m1      
        //  1598: if_icmpeq       1631
        //  1601: aload           ch_params
        //  1603: iload           ch_dreq
        //  1605: aaload         
        //  1606: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1609: ifnull          1628
        //  1612: aload           ch_params
        //  1614: iload           ch_dreq
        //  1616: aaload         
        //  1617: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1620: ldc             "true"
        //  1622: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1625: ifeq            1631
        //  1628: iconst_1       
        //  1629: istore          ch_dreq_val
        //  1631: iload           ch_dreq_val
        //  1633: ifne            1642
        //  1636: iload           digest
        //  1638: iconst_m1      
        //  1639: if_icmpeq       1782
        //  1642: aload_1         /* req */
        //  1643: invokeinterface HTTPClient/RoRequest.getStream:()LHTTPClient/HttpOutputStream;
        //  1648: ifnonnull       1782
        //  1651: iload           digest
        //  1653: iconst_m1      
        //  1654: if_icmpne       1677
        //  1657: aload           params
        //  1659: aload           params
        //  1661: arraylength    
        //  1662: iconst_1       
        //  1663: iadd           
        //  1664: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //  1667: astore          28
        //  1669: aload           params
        //  1671: arraylength    
        //  1672: istore          digest
        //  1674: goto            1681
        //  1677: aload           params
        //  1679: astore          28
        //  1681: aload           28
        //  1683: iload           digest
        //  1685: new             LHTTPClient/NVPair;
        //  1688: dup            
        //  1689: ldc             "digest"
        //  1691: aload_1         /* req */
        //  1692: aload           extra
        //  1694: iconst_0       
        //  1695: aaload         
        //  1696: aload           params
        //  1698: iload           nonce
        //  1700: aaload         
        //  1701: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1704: invokestatic    HTTPClient/DefaultAuthHandler.calc_digest:(LHTTPClient/RoRequest;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //  1707: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1710: aastore        
        //  1711: iload           dreq
        //  1713: iconst_m1      
        //  1714: if_icmpne       1750
        //  1717: aload           28
        //  1719: arraylength    
        //  1720: istore          dreq
        //  1722: aload           28
        //  1724: aload           28
        //  1726: arraylength    
        //  1727: iconst_1       
        //  1728: iadd           
        //  1729: invokestatic    HTTPClient/Util.resizeArray:([LHTTPClient/NVPair;I)[LHTTPClient/NVPair;
        //  1732: astore          28
        //  1734: aload           28
        //  1736: iload           dreq
        //  1738: new             LHTTPClient/NVPair;
        //  1741: dup            
        //  1742: ldc             "digest-required"
        //  1744: ldc             "true"
        //  1746: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  1749: aastore        
        //  1750: new             LHTTPClient/AuthorizationInfo;
        //  1753: dup            
        //  1754: aload_0         /* info */
        //  1755: invokevirtual   HTTPClient/AuthorizationInfo.getHost:()Ljava/lang/String;
        //  1758: aload_0         /* info */
        //  1759: invokevirtual   HTTPClient/AuthorizationInfo.getPort:()I
        //  1762: aload_0         /* info */
        //  1763: invokevirtual   HTTPClient/AuthorizationInfo.getScheme:()Ljava/lang/String;
        //  1766: aload_0         /* info */
        //  1767: invokevirtual   HTTPClient/AuthorizationInfo.getRealm:()Ljava/lang/String;
        //  1770: aload           28
        //  1772: aload           extra
        //  1774: invokespecial   HTTPClient/AuthorizationInfo.<init>:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;[LHTTPClient/NVPair;Ljava/lang/Object;)V
        //  1777: astore          new_info
        //  1779: goto            1822
        //  1782: iload           ch_dreq_val
        //  1784: ifeq            1793
        //  1787: aconst_null    
        //  1788: astore          new_info
        //  1790: goto            1822
        //  1793: new             LHTTPClient/AuthorizationInfo;
        //  1796: dup            
        //  1797: aload_0         /* info */
        //  1798: invokevirtual   HTTPClient/AuthorizationInfo.getHost:()Ljava/lang/String;
        //  1801: aload_0         /* info */
        //  1802: invokevirtual   HTTPClient/AuthorizationInfo.getPort:()I
        //  1805: aload_0         /* info */
        //  1806: invokevirtual   HTTPClient/AuthorizationInfo.getScheme:()Ljava/lang/String;
        //  1809: aload_0         /* info */
        //  1810: invokevirtual   HTTPClient/AuthorizationInfo.getRealm:()Ljava/lang/String;
        //  1813: aload           params
        //  1815: aload           extra
        //  1817: invokespecial   HTTPClient/AuthorizationInfo.<init>:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;[LHTTPClient/NVPair;Ljava/lang/Object;)V
        //  1820: astore          new_info
        //  1822: aload_2         /* challenge */
        //  1823: ifnull          1845
        //  1826: aload_2         /* challenge */
        //  1827: invokevirtual   HTTPClient/AuthorizationInfo.getHost:()Ljava/lang/String;
        //  1830: aload_1         /* req */
        //  1831: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1836: invokevirtual   HTTPClient/HTTPConnection.getHost:()Ljava/lang/String;
        //  1839: invokevirtual   java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z
        //  1842: ifne            1849
        //  1845: iconst_0       
        //  1846: goto            1850
        //  1849: iconst_1       
        //  1850: istore          from_server
        //  1852: iload           ch_domain
        //  1854: iconst_m1      
        //  1855: if_icmpeq       2076
        //  1858: aconst_null    
        //  1859: astore          29
        //  1861: new             LHTTPClient/URI;
        //  1864: dup            
        //  1865: aload_1         /* req */
        //  1866: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1871: invokevirtual   HTTPClient/HTTPConnection.getProtocol:()Ljava/lang/String;
        //  1874: aload_1         /* req */
        //  1875: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1880: invokevirtual   HTTPClient/HTTPConnection.getHost:()Ljava/lang/String;
        //  1883: aload_1         /* req */
        //  1884: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1889: invokevirtual   HTTPClient/HTTPConnection.getPort:()I
        //  1892: aload_1         /* req */
        //  1893: invokeinterface HTTPClient/RoRequest.getRequestURI:()Ljava/lang/String;
        //  1898: invokespecial   HTTPClient/URI.<init>:(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V
        //  1901: astore          29
        //  1903: goto            1907
        //  1906: pop            
        //  1907: new             Ljava/util/StringTokenizer;
        //  1910: dup            
        //  1911: aload           ch_params
        //  1913: iload           ch_domain
        //  1915: aaload         
        //  1916: invokevirtual   HTTPClient/NVPair.getValue:()Ljava/lang/String;
        //  1919: invokespecial   java/util/StringTokenizer.<init>:(Ljava/lang/String;)V
        //  1922: astore          tok
        //  1924: goto            2065
        //  1927: new             LHTTPClient/URI;
        //  1930: dup            
        //  1931: aload           29
        //  1933: aload           tok
        //  1935: invokevirtual   java/util/StringTokenizer.nextToken:()Ljava/lang/String;
        //  1938: invokespecial   HTTPClient/URI.<init>:(LHTTPClient/URI;Ljava/lang/String;)V
        //  1941: astore          31
        //  1943: goto            1950
        //  1946: pop            
        //  1947: goto            2065
        //  1950: aload           Uri
        //  1952: invokevirtual   HTTPClient/URI.getHost:()Ljava/lang/String;
        //  1955: ifnull          2065
        //  1958: aload           Uri
        //  1960: invokevirtual   HTTPClient/URI.getHost:()Ljava/lang/String;
        //  1963: aload           Uri
        //  1965: invokevirtual   HTTPClient/URI.getPort:()I
        //  1968: aload_0         /* info */
        //  1969: invokevirtual   HTTPClient/AuthorizationInfo.getScheme:()Ljava/lang/String;
        //  1972: aload_0         /* info */
        //  1973: invokevirtual   HTTPClient/AuthorizationInfo.getRealm:()Ljava/lang/String;
        //  1976: aload_1         /* req */
        //  1977: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  1982: invokevirtual   HTTPClient/HTTPConnection.getContext:()Ljava/lang/Object;
        //  1985: invokestatic    HTTPClient/AuthorizationInfo.getAuthorization:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;)LHTTPClient/AuthorizationInfo;
        //  1988: astore          tmp
        //  1990: aload           tmp
        //  1992: ifnonnull       2050
        //  1995: aload           params
        //  1997: iload           uri
        //  1999: new             LHTTPClient/NVPair;
        //  2002: dup            
        //  2003: ldc             "uri"
        //  2005: aload           Uri
        //  2007: invokevirtual   HTTPClient/URI.getPathAndQuery:()Ljava/lang/String;
        //  2010: invokespecial   HTTPClient/NVPair.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //  2013: aastore        
        //  2014: new             LHTTPClient/AuthorizationInfo;
        //  2017: dup            
        //  2018: aload           Uri
        //  2020: invokevirtual   HTTPClient/URI.getHost:()Ljava/lang/String;
        //  2023: aload           Uri
        //  2025: invokevirtual   HTTPClient/URI.getPort:()I
        //  2028: aload_0         /* info */
        //  2029: invokevirtual   HTTPClient/AuthorizationInfo.getScheme:()Ljava/lang/String;
        //  2032: aload_0         /* info */
        //  2033: invokevirtual   HTTPClient/AuthorizationInfo.getRealm:()Ljava/lang/String;
        //  2036: aload           params
        //  2038: aload           extra
        //  2040: invokespecial   HTTPClient/AuthorizationInfo.<init>:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;[LHTTPClient/NVPair;Ljava/lang/Object;)V
        //  2043: astore          tmp
        //  2045: aload           tmp
        //  2047: invokestatic    HTTPClient/AuthorizationInfo.addAuthorization:(LHTTPClient/AuthorizationInfo;)V
        //  2050: iload           from_server
        //  2052: ifeq            2065
        //  2055: aload           tmp
        //  2057: aload           Uri
        //  2059: invokevirtual   HTTPClient/URI.getPathAndQuery:()Ljava/lang/String;
        //  2062: invokevirtual   HTTPClient/AuthorizationInfo.addPath:(Ljava/lang/String;)V
        //  2065: aload           tok
        //  2067: invokevirtual   java/util/StringTokenizer.hasMoreTokens:()Z
        //  2070: ifne            1927
        //  2073: goto            2127
        //  2076: iload           from_server
        //  2078: ifeq            2127
        //  2081: aload_2         /* challenge */
        //  2082: ifnull          2127
        //  2085: aload_2         /* challenge */
        //  2086: invokevirtual   HTTPClient/AuthorizationInfo.getHost:()Ljava/lang/String;
        //  2089: aload_2         /* challenge */
        //  2090: invokevirtual   HTTPClient/AuthorizationInfo.getPort:()I
        //  2093: aload_0         /* info */
        //  2094: invokevirtual   HTTPClient/AuthorizationInfo.getScheme:()Ljava/lang/String;
        //  2097: aload_0         /* info */
        //  2098: invokevirtual   HTTPClient/AuthorizationInfo.getRealm:()Ljava/lang/String;
        //  2101: aload_1         /* req */
        //  2102: invokeinterface HTTPClient/RoRequest.getConnection:()LHTTPClient/HTTPConnection;
        //  2107: invokevirtual   HTTPClient/HTTPConnection.getContext:()Ljava/lang/Object;
        //  2110: invokestatic    HTTPClient/AuthorizationInfo.getAuthorization:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/Object;)LHTTPClient/AuthorizationInfo;
        //  2113: astore          tmp
        //  2115: aload           tmp
        //  2117: ifnull          2127
        //  2120: aload           tmp
        //  2122: ldc             "/"
        //  2124: invokevirtual   HTTPClient/AuthorizationInfo.addPath:(Ljava/lang/String;)V
        //  2127: aload           new_info
        //  2129: areturn        
        //    Exceptions:
        //  throws HTTPClient.AuthSchemeNotImplException
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                       
        //  -----  -----  -----  -----  ---------------------------
        //  219    1472   1478   1482   Any
        //  1861   1903   1906   1907   LHTTPClient/ParseException;
        //  1927   1943   1946   1950   LHTTPClient/ParseException;
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
    
    private static AuthorizationInfo digest_check_stale(final AuthorizationInfo challenge, final RoRequest req, final RoResponse resp) throws AuthSchemeNotImplException, IOException {
        AuthorizationInfo cred = null;
        final NVPair[] params = challenge.getParams();
        int idx = 0;
        while (idx < params.length) {
            final String name = params[idx].getName();
            if (name.equalsIgnoreCase("stale") && params[idx].getValue().equalsIgnoreCase("true")) {
                cred = AuthorizationInfo.getAuthorization(challenge, req, resp, false);
                if (cred != null) {
                    return digest_fixup(cred, req, challenge, resp);
                }
                break;
            }
            else {
                ++idx;
            }
        }
        return cred;
    }
    
    private static boolean handle_nextnonce(final AuthorizationInfo prev, final RoRequest req, final HttpHeaderElement nextnonce) throws IOException {
        if (prev == null || nextnonce == null || nextnonce.getValue() == null) {
            return false;
        }
        AuthorizationInfo ai;
        try {
            ai = AuthorizationInfo.getAuthorization(prev, req, null, false);
        }
        catch (final AuthSchemeNotImplException ex) {
            ai = prev;
        }
        synchronized (ai) {
            NVPair[] params = ai.getParams();
            params = setValue(params, "nonce", nextnonce.getValue());
            params = setValue(params, "nc", "00000000");
            ai.setParams(params);
        }
        return true;
    }
    
    private static boolean handle_digest(final AuthorizationInfo prev, final Response resp, final RoRequest req, final String hdr_name) throws IOException {
        if (prev == null) {
            return false;
        }
        final NVPair[] params = prev.getParams();
        final VerifyDigest verifier = new VerifyDigest(((String[])prev.getExtraInfo())[0], getValue(params, "nonce"), req.getMethod(), getValue(params, "uri"), hdr_name, resp);
        if (resp.hasEntity()) {
            Log.write(8, "Auth:  pushing md5-check-stream to verify digest from " + hdr_name);
            resp.inp_stream = new MD5InputStream(resp.inp_stream, verifier);
        }
        else {
            Log.write(8, "Auth:  verifying digest from " + hdr_name);
            verifier.verifyHash(MD5.digest(DefaultAuthHandler.NUL), 0L);
        }
        return true;
    }
    
    private static boolean handle_rspauth(final AuthorizationInfo prev, final Response resp, final RoRequest req, final Vector auth_info, final String hdr_name) throws IOException {
        if (prev == null) {
            return false;
        }
        final NVPair[] params = prev.getParams();
        int uri = -1;
        int alg = -1;
        int nonce = -1;
        int cnonce = -1;
        int nc = -1;
        for (int idx = 0; idx < params.length; ++idx) {
            final String name = params[idx].getName().toLowerCase();
            if (name.equals("uri")) {
                uri = idx;
            }
            else if (name.equals("algorithm")) {
                alg = idx;
            }
            else if (name.equals("nonce")) {
                nonce = idx;
            }
            else if (name.equals("cnonce")) {
                cnonce = idx;
            }
            else if (name.equals("nc")) {
                nc = idx;
            }
        }
        final VerifyRspAuth verifier = new VerifyRspAuth(params[uri].getValue(), ((String[])prev.getExtraInfo())[0], (alg == -1) ? null : params[alg].getValue(), params[nonce].getValue(), (cnonce == -1) ? "" : params[cnonce].getValue(), (nc == -1) ? "" : params[nc].getValue(), hdr_name, resp);
        HttpHeaderElement qop = null;
        if (auth_info != null && (qop = Util.getElement(auth_info, "qop")) != null && qop.getValue() != null && (qop.getValue().equalsIgnoreCase("auth") || (!resp.hasEntity() && qop.getValue().equalsIgnoreCase("auth-int")))) {
            Log.write(8, "Auth:  verifying rspauth from " + hdr_name);
            verifier.verifyHash(MD5.digest(DefaultAuthHandler.NUL), 0L);
        }
        else {
            Log.write(8, "Auth:  pushing md5-check-stream to verify rspauth from " + hdr_name);
            resp.inp_stream = new MD5InputStream(resp.inp_stream, verifier);
        }
        return true;
    }
    
    private static String calcResponseAttr(final String hash, final String[] extra, final NVPair[] params, final int alg, final int uri, final int qop, final int nonce, final int nc, final int cnonce, final String method) {
        String A1;
        if (alg != -1 && params[alg].getValue().equalsIgnoreCase("MD5-sess")) {
            A1 = extra[1];
        }
        else {
            A1 = extra[0];
        }
        String A2 = String.valueOf(method) + ":" + params[uri].getValue();
        if (qop != -1 && params[qop].getValue().equalsIgnoreCase("auth-int")) {
            A2 = String.valueOf(A2) + ":" + hash;
        }
        A2 = MD5.hexDigest(A2);
        String resp_val;
        if (qop == -1) {
            resp_val = MD5.hexDigest(String.valueOf(A1) + ":" + params[nonce].getValue() + ":" + A2);
        }
        else {
            resp_val = MD5.hexDigest(String.valueOf(A1) + ":" + params[nonce].getValue() + ":" + params[nc].getValue() + ":" + params[cnonce].getValue() + ":" + params[qop].getValue() + ":" + A2);
        }
        return resp_val;
    }
    
    private static String calc_digest(final RoRequest req, final String A1_hash, final String nonce) {
        if (req.getStream() != null) {
            return "";
        }
        int ct = -1;
        int ce = -1;
        int lm = -1;
        int ex = -1;
        int dt = -1;
        for (int idx = 0; idx < req.getHeaders().length; ++idx) {
            final String name = req.getHeaders()[idx].getName();
            if (name.equalsIgnoreCase("Content-type")) {
                ct = idx;
            }
            else if (name.equalsIgnoreCase("Content-Encoding")) {
                ce = idx;
            }
            else if (name.equalsIgnoreCase("Last-Modified")) {
                lm = idx;
            }
            else if (name.equalsIgnoreCase("Expires")) {
                ex = idx;
            }
            else if (name.equalsIgnoreCase("Date")) {
                dt = idx;
            }
        }
        final NVPair[] hdrs = req.getHeaders();
        final byte[] entity_body = (req.getData() == null) ? DefaultAuthHandler.NUL : req.getData();
        final String entity_hash = MD5.hexDigest(entity_body);
        final String entity_info = MD5.hexDigest(String.valueOf(req.getRequestURI()) + ":" + ((ct == -1) ? "" : hdrs[ct].getValue()) + ":" + entity_body.length + ":" + ((ce == -1) ? "" : hdrs[ce].getValue()) + ":" + ((lm == -1) ? "" : hdrs[lm].getValue()) + ":" + ((ex == -1) ? "" : hdrs[ex].getValue()));
        final String entity_digest = String.valueOf(A1_hash) + ":" + nonce + ":" + req.getMethod() + ":" + ((dt == -1) ? "" : hdrs[dt].getValue()) + ":" + entity_info + ":" + entity_hash;
        if (Log.isEnabled(8)) {
            Log.write(8, "Auth:  Entity-Info: '" + req.getRequestURI() + ":" + ((ct == -1) ? "" : hdrs[ct].getValue()) + ":" + entity_body.length + ":" + ((ce == -1) ? "" : hdrs[ce].getValue()) + ":" + ((lm == -1) ? "" : hdrs[lm].getValue()) + ":" + ((ex == -1) ? "" : hdrs[ex].getValue()) + "'");
            Log.write(8, "Auth:  Entity-Body: '" + entity_hash + "'");
            Log.write(8, "Auth:  Entity-Digest: '" + entity_digest + "'");
        }
        return MD5.hexDigest(entity_digest);
    }
    
    private static boolean handle_discard(final AuthorizationInfo prev, final RoRequest req, final HttpHeaderElement discard) {
        if (discard != null && prev != null) {
            AuthorizationInfo.removeAuthorization(prev, req.getConnection().getContext());
            return true;
        }
        return false;
    }
    
    private static byte[] gen_random_bytes(final int num) {
        try {
            final FileInputStream rnd = new FileInputStream("/dev/random");
            final DataInputStream din = new DataInputStream(rnd);
            final byte[] data = new byte[num];
            din.readFully(data);
            try {
                din.close();
            }
            catch (final IOException ex) {}
            return data;
        }
        catch (final Throwable t) {
            final byte[] data2 = new byte[num];
            try {
                final long fm = Runtime.getRuntime().freeMemory();
                data2[0] = (byte)(fm & 0xFFL);
                data2[1] = (byte)(fm >> 8 & 0xFFL);
                final int h = data2.hashCode();
                data2[2] = (byte)(h & 0xFF);
                data2[3] = (byte)(h >> 8 & 0xFF);
                data2[4] = (byte)(h >> 16 & 0xFF);
                data2[5] = (byte)(h >> 24 & 0xFF);
                final long time = System.currentTimeMillis();
                data2[6] = (byte)(time & 0xFFL);
                data2[7] = (byte)(time >> 8 & 0xFFL);
            }
            catch (final ArrayIndexOutOfBoundsException ex2) {}
            return data2;
        }
    }
    
    private static final String getValue(final NVPair[] list, final String key) {
        for (int len = list.length, idx = 0; idx < len; ++idx) {
            if (list[idx].getName().equalsIgnoreCase(key)) {
                return list[idx].getValue();
            }
        }
        return null;
    }
    
    private static final int getIndex(final NVPair[] list, final String key) {
        for (int len = list.length, idx = 0; idx < len; ++idx) {
            if (list[idx].getName().equalsIgnoreCase(key)) {
                return idx;
            }
        }
        return -1;
    }
    
    private static final NVPair[] setValue(NVPair[] list, final String key, final String val) {
        int idx = getIndex(list, key);
        if (idx == -1) {
            idx = list.length;
            list = Util.resizeArray(list, list.length + 1);
        }
        list[idx] = new NVPair(key, val);
        return list;
    }
    
    private static String[] splitList(final String str, final String sep) {
        if (str == null) {
            return new String[0];
        }
        final StringTokenizer tok = new StringTokenizer(str, sep);
        final String[] list = new String[tok.countTokens()];
        for (int idx = 0; idx < list.length; ++idx) {
            list[idx] = tok.nextToken().trim();
        }
        return list;
    }
    
    static String hex(final byte[] buf) {
        final StringBuffer str = new StringBuffer(buf.length * 3);
        for (int idx = 0; idx < buf.length; ++idx) {
            str.append(Character.forDigit(buf[idx] >> 4 & 0xF, 16));
            str.append(Character.forDigit(buf[idx] & 0xF, 16));
            str.append(':');
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }
    
    static final byte[] unHex(final String hex) {
        final byte[] digest = new byte[hex.length() / 2];
        for (int idx = 0; idx < digest.length; ++idx) {
            digest[idx] = (byte)(0xFF & Integer.parseInt(hex.substring(2 * idx, 2 * (idx + 1)), 16));
        }
        return digest;
    }
    
    public static synchronized AuthorizationPrompter setAuthorizationPrompter(final AuthorizationPrompter prompt) {
        final AuthorizationPrompter prev = DefaultAuthHandler.prompter;
        DefaultAuthHandler.prompter = prompt;
        DefaultAuthHandler.prompterSet = true;
        return prev;
    }
    
    private static void setDefaultPrompter() {
        if (!SimpleAuthPrompt.canUseCLPrompt() || isAWTRunning()) {
            DefaultAuthHandler.prompter = new SimpleAuthPopup();
        }
        else {
            DefaultAuthHandler.prompter = new SimpleAuthPrompt();
        }
    }
    
    private static final boolean isAWTRunning() {
        ThreadGroup root;
        for (root = Thread.currentThread().getThreadGroup(); root.getParent() != null; root = root.getParent()) {}
        final Thread[] t_list = new Thread[root.activeCount() + 5];
        for (int t_num = root.enumerate(t_list), idx = 0; idx < t_num; ++idx) {
            if (t_list[idx].getName().startsWith("AWT-")) {
                return true;
            }
        }
        return false;
    }
    
    static {
        NUL = new byte[0];
        DefaultAuthHandler.digest_secret = null;
        DefaultAuthHandler.prompter = null;
    }
}
