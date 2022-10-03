package HTTPClient;

import java.net.ProtocolException;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Hashtable;

public class CookieModule implements HTTPClientModule
{
    private static Hashtable cookie_cntxt_list;
    private static File cookie_jar;
    private static Object cookieSaver;
    private static CookiePolicyHandler cookie_handler;
    
    private static void loadCookies() {
        try {
            CookieModule.cookie_jar = new File(getCookieJarName());
            if (CookieModule.cookie_jar.isFile() && CookieModule.cookie_jar.canRead()) {
                final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CookieModule.cookie_jar));
                CookieModule.cookie_cntxt_list.put(HTTPConnection.getDefaultContext(), ois.readObject());
                ois.close();
            }
        }
        catch (final Throwable t) {
            CookieModule.cookie_jar = null;
        }
    }
    
    private static void saveCookies() {
        if (CookieModule.cookie_jar != null && (!CookieModule.cookie_jar.exists() || (CookieModule.cookie_jar.isFile() && CookieModule.cookie_jar.canWrite()))) {
            final Hashtable cookie_list = new Hashtable();
            final Enumeration enum1 = Util.getList(CookieModule.cookie_cntxt_list, HTTPConnection.getDefaultContext()).elements();
            while (enum1.hasMoreElements()) {
                final Cookie cookie = enum1.nextElement();
                if (!cookie.discard()) {
                    cookie_list.put(cookie, cookie);
                }
            }
            if (cookie_list.size() > 0) {
                try {
                    final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CookieModule.cookie_jar));
                    oos.writeObject(cookie_list);
                    oos.close();
                }
                catch (final Throwable t) {}
            }
        }
    }
    
    private static String getCookieJarName() {
        String file = null;
        try {
            file = System.getProperty("HTTPClient.cookies.jar");
        }
        catch (final Exception ex) {}
        if (file == null) {
            final String os = System.getProperty("os.name");
            if (os.equalsIgnoreCase("Windows 95") || os.equalsIgnoreCase("16-bit Windows") || os.equalsIgnoreCase("Windows")) {
                file = String.valueOf(System.getProperty("java.home")) + File.separator + ".httpclient_cookies";
            }
            else if (os.equalsIgnoreCase("Windows NT")) {
                file = String.valueOf(System.getProperty("user.home")) + File.separator + ".httpclient_cookies";
            }
            else if (os.equalsIgnoreCase("OS/2")) {
                file = String.valueOf(System.getProperty("user.home")) + File.separator + ".httpclient_cookies";
            }
            else if (os.equalsIgnoreCase("Mac OS") || os.equalsIgnoreCase("MacOS")) {
                file = "System Folder" + File.separator + "Preferences" + File.separator + "HTTPClientCookies";
            }
            else {
                file = String.valueOf(System.getProperty("user.home")) + File.separator + ".httpclient_cookies";
            }
        }
        return file;
    }
    
    CookieModule() {
    }
    
    public int requestHandler(final Request req, final Response[] resp) {
        NVPair[] hdrs = req.getHeaders();
        int length = hdrs.length;
        for (int idx = 0; idx < hdrs.length; ++idx) {
            final int beg = idx;
            while (idx < hdrs.length && hdrs[idx].getName().equalsIgnoreCase("Cookie")) {
                ++idx;
            }
            if (idx - beg > 0) {
                length -= idx - beg;
                System.arraycopy(hdrs, idx, hdrs, beg, length - beg);
            }
        }
        if (length < hdrs.length) {
            hdrs = Util.resizeArray(hdrs, length);
            req.setHeaders(hdrs);
        }
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, req.getConnection().getContext());
        if (cookie_list.size() == 0) {
            return 0;
        }
        final Vector names = new Vector();
        final Vector lens = new Vector();
        int version = 0;
        synchronized (cookie_list) {
            final Enumeration list = cookie_list.elements();
            Vector remove_list = null;
            while (list.hasMoreElements()) {
                final Cookie cookie = list.nextElement();
                if (cookie.hasExpired()) {
                    Log.write(16, "CookM: cookie has expired and is being removed: " + cookie);
                    if (remove_list == null) {
                        remove_list = new Vector();
                    }
                    remove_list.addElement(cookie);
                }
                else {
                    if (!cookie.sendWith(req) || (CookieModule.cookie_handler != null && !CookieModule.cookie_handler.sendCookie(cookie, req))) {
                        continue;
                    }
                    int len;
                    int idx2;
                    for (len = cookie.getPath().length(), idx2 = 0; idx2 < lens.size() && lens.elementAt(idx2) >= len; ++idx2) {}
                    names.insertElementAt(cookie.toExternalForm(), idx2);
                    lens.insertElementAt(new Integer(len), idx2);
                    if (!(cookie instanceof Cookie2)) {
                        continue;
                    }
                    version = Math.max(version, ((Cookie2)cookie).getVersion());
                }
            }
            if (remove_list != null) {
                for (int idx3 = 0; idx3 < remove_list.size(); ++idx3) {
                    cookie_list.remove(remove_list.elementAt(idx3));
                }
            }
            monitorexit(cookie_list);
        }
        if (!names.isEmpty()) {
            final StringBuffer value = new StringBuffer();
            if (version > 0) {
                value.append("$Version=\"" + version + "\"; ");
            }
            value.append(names.elementAt(0));
            for (int idx4 = 1; idx4 < names.size(); ++idx4) {
                value.append("; ");
                value.append(names.elementAt(idx4));
            }
            hdrs = Util.resizeArray(hdrs, hdrs.length + 1);
            hdrs[hdrs.length - 1] = new NVPair("Cookie", value.toString());
            if (version != 1) {
                int idx5;
                for (idx5 = 0; idx5 < hdrs.length && !hdrs[idx5].getName().equalsIgnoreCase("Cookie2"); ++idx5) {}
                if (idx5 == hdrs.length) {
                    hdrs = Util.resizeArray(hdrs, hdrs.length + 1);
                    hdrs[hdrs.length - 1] = new NVPair("Cookie2", "$Version=\"1\"");
                }
            }
            req.setHeaders(hdrs);
            Log.write(16, "CookM: Sending cookies '" + (Object)value + "'");
        }
        return 0;
    }
    
    public void responsePhase1Handler(final Response resp, final RoRequest req) throws IOException {
        final String set_cookie = resp.getHeader("Set-Cookie");
        final String set_cookie2 = resp.getHeader("Set-Cookie2");
        if (set_cookie == null && set_cookie2 == null) {
            return;
        }
        resp.deleteHeader("Set-Cookie");
        resp.deleteHeader("Set-Cookie2");
        if (set_cookie != null) {
            this.handleCookie(set_cookie, false, req, resp);
        }
        if (set_cookie2 != null) {
            this.handleCookie(set_cookie2, true, req, resp);
        }
    }
    
    public int responsePhase2Handler(final Response resp, final Request req) {
        return 10;
    }
    
    public void responsePhase3Handler(final Response resp, final RoRequest req) {
    }
    
    public void trailerHandler(final Response resp, final RoRequest req) throws IOException {
        final String set_cookie = resp.getTrailer("Set-Cookie");
        final String set_cookie2 = resp.getTrailer("Set-Cookie2");
        if (set_cookie == null && set_cookie2 == null) {
            return;
        }
        resp.deleteTrailer("Set-Cookie");
        resp.deleteTrailer("Set-Cookie2");
        if (set_cookie != null) {
            this.handleCookie(set_cookie, false, req, resp);
        }
        if (set_cookie2 != null) {
            this.handleCookie(set_cookie2, true, req, resp);
        }
    }
    
    private void handleCookie(final String set_cookie, final boolean cookie2, final RoRequest req, final Response resp) throws ProtocolException {
        Cookie[] cookies;
        if (cookie2) {
            cookies = Cookie2.parse(set_cookie, req);
        }
        else {
            cookies = Cookie.parse(set_cookie, req);
        }
        if (Log.isEnabled(16)) {
            Log.write(16, "CookM: Received and parsed " + cookies.length + " cookies:");
            for (int idx = 0; idx < cookies.length; ++idx) {
                Log.write(16, "CookM: Cookie " + idx + ": " + cookies[idx]);
            }
        }
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, req.getConnection().getContext());
        synchronized (cookie_list) {
            for (int idx2 = 0; idx2 < cookies.length; ++idx2) {
                final Cookie cookie3 = cookie_list.get(cookies[idx2]);
                if (cookie3 != null && cookies[idx2].hasExpired()) {
                    Log.write(16, "CookM: cookie has expired and is being removed: " + cookie3);
                    cookie_list.remove(cookie3);
                }
                else if (!cookies[idx2].hasExpired() && (CookieModule.cookie_handler == null || CookieModule.cookie_handler.acceptCookie(cookies[idx2], req, resp))) {
                    cookie_list.put(cookies[idx2], cookies[idx2]);
                }
            }
            monitorexit(cookie_list);
        }
    }
    
    public static void discardAllCookies() {
        CookieModule.cookie_cntxt_list.clear();
    }
    
    public static void discardAllCookies(final Object context) {
        if (context != null) {
            CookieModule.cookie_cntxt_list.remove(context);
        }
    }
    
    public static Cookie[] listAllCookies() {
        synchronized (CookieModule.cookie_cntxt_list) {
            Cookie[] cookies = new Cookie[0];
            int idx = 0;
            final Enumeration cntxt_list = CookieModule.cookie_cntxt_list.elements();
            while (cntxt_list.hasMoreElements()) {
                final Hashtable hashtable;
                final Hashtable cntxt = hashtable = cntxt_list.nextElement();
                monitorenter(hashtable);
                try {
                    cookies = Util.resizeArray(cookies, idx + cntxt.size());
                    final Enumeration cookie_list = cntxt.elements();
                    while (cookie_list.hasMoreElements()) {
                        cookies[idx++] = cookie_list.nextElement();
                    }
                    monitorexit(hashtable);
                }
                finally {}
            }
            final Cookie[] array = cookies;
            monitorexit(CookieModule.cookie_cntxt_list);
            return array;
        }
    }
    
    public static Cookie[] listAllCookies(final Object context) {
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, context);
        synchronized (cookie_list) {
            final Cookie[] cookies = new Cookie[cookie_list.size()];
            int idx = 0;
            final Enumeration enum1 = cookie_list.elements();
            while (enum1.hasMoreElements()) {
                cookies[idx++] = enum1.nextElement();
            }
            final Cookie[] array = cookies;
            monitorexit(cookie_list);
            return array;
        }
    }
    
    public static void addCookie(final Cookie cookie) {
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, HTTPConnection.getDefaultContext());
        cookie_list.put(cookie, cookie);
    }
    
    public static void addCookie(final Cookie cookie, final Object context) {
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, context);
        cookie_list.put(cookie, cookie);
    }
    
    public static void removeCookie(final Cookie cookie) {
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, HTTPConnection.getDefaultContext());
        cookie_list.remove(cookie);
    }
    
    public static void removeCookie(final Cookie cookie, final Object context) {
        final Hashtable cookie_list = Util.getList(CookieModule.cookie_cntxt_list, context);
        cookie_list.remove(cookie);
    }
    
    public static synchronized CookiePolicyHandler setCookiePolicyHandler(final CookiePolicyHandler handler) {
        final CookiePolicyHandler old = CookieModule.cookie_handler;
        CookieModule.cookie_handler = handler;
        return old;
    }
    
    static {
        CookieModule.cookie_cntxt_list = new Hashtable();
        CookieModule.cookie_jar = null;
        CookieModule.cookieSaver = null;
        CookieModule.cookie_handler = new DefaultCookiePolicyHandler();
        boolean persist;
        try {
            persist = Boolean.getBoolean("HTTPClient.cookies.save");
        }
        catch (final Exception ex) {
            persist = false;
        }
        if (persist) {
            loadCookies();
            CookieModule.cookieSaver = new Object() {
                public void finalize() {
                    saveCookies();
                }
            };
            try {
                System.runFinalizersOnExit(true);
            }
            catch (final Throwable t) {}
        }
    }
}
