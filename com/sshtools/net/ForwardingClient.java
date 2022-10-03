package com.sshtools.net;

import java.net.Socket;
import java.net.ServerSocket;
import com.maverick.ssh.ChannelEventListener;
import com.maverick.ssh.SshTransport;
import com.maverick.ssh.SshChannel;
import com.maverick.ssh.ChannelAdapter;
import com.maverick.events.EventLog;
import com.maverick.util.IOStreamConnector;
import com.maverick.ssh.SshTunnel;
import com.maverick.ssh.components.ComponentManager;
import java.net.InetAddress;
import com.maverick.util.ByteArrayReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.File;
import com.maverick.ssh.ForwardingRequestListener;
import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.events.Event;
import com.maverick.events.EventServiceImplementation;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import com.maverick.ssh.SshClient;
import com.maverick.ssh.Client;

public class ForwardingClient implements Client
{
    SshClient e;
    Hashtable g;
    Hashtable j;
    Hashtable h;
    Hashtable d;
    _c f;
    _b c;
    Vector i;
    public static final String X11_KEY = "X11";
    boolean b;
    public final int LOWEST_RANDOM_PORT = 49152;
    public final int HIGHEST_RANDOM_PORT = 65535;
    
    public ForwardingClient(final SshClient e) {
        this.g = new Hashtable();
        this.j = new Hashtable();
        this.h = new Hashtable();
        this.d = new Hashtable();
        this.f = new _c();
        this.c = new _b();
        this.i = new Vector();
        this.b = false;
        this.e = e;
    }
    
    public void addListener(final ForwardingClientListener forwardingClientListener) {
        if (forwardingClientListener != null) {
            this.i.addElement(forwardingClientListener);
            final Enumeration elements = this.d.elements();
            while (elements.hasMoreElements()) {
                final _d d = (_d)elements.nextElement();
                if (d.c()) {
                    forwardingClientListener.forwardingStarted(1, this.b(d.e, d.h), d.f, d.c);
                }
            }
            final Enumeration keys = this.g.keys();
            while (keys.hasMoreElements()) {
                final String s = (String)keys.nextElement();
                if (!s.equals("X11")) {
                    if (this.e.getContext().getX11Display() != null && this.e.getContext().getX11Display().equals(s)) {
                        continue;
                    }
                    final String s2 = this.j.get(s);
                    forwardingClientListener.forwardingStarted(2, s, s2.substring(0, s2.indexOf(58)), Integer.parseInt(s2.substring(s2.indexOf(58) + 1)));
                }
            }
            final String x11Display = this.e.getContext().getX11Display();
            if (x11Display != null && this.b) {
                String substring = "localhost";
                final int index = x11Display.indexOf(58);
                int n;
                if (index != -1) {
                    substring = x11Display.substring(0, index);
                    n = Integer.parseInt(x11Display.substring(index + 1));
                }
                else {
                    n = Integer.parseInt(x11Display);
                }
                forwardingClientListener.forwardingStarted(3, "X11", substring, n);
            }
        }
    }
    
    public boolean hasRemoteForwarding(final String s, final int n) {
        return this.j.containsKey(this.b(s, n));
    }
    
    public boolean hasLocalForwarding(final String s, final int n) {
        return this.d.containsKey(this.b(s, n));
    }
    
    public void removeListener(final ForwardingClientListener forwardingClientListener) {
        this.i.removeElement(forwardingClientListener);
    }
    
    public void startLocalForwarding(final String s, final int n, final String s2, final int n2) throws SshException {
        final String b = this.b(s, n);
        final _d d = new _d(s, n, s2, n2);
        d.d();
        this.d.put(b, d);
        this.h.put(b, new Vector());
        for (int i = 0; i < this.i.size(); ++i) {
            ((ForwardingClientListener)this.i.elementAt(i)).forwardingStarted(1, b, s2, n2);
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 16, true).addAttribute("FORWARDING_TUNNEL_ENTRANCE", b).addAttribute("FORWARDING_TUNNEL_EXIT", s2 + ":" + n2));
    }
    
    public int startLocalForwardingOnRandomPort(final String s, final int n, final String s2, final int n2) throws SshException {
        int i = 0;
        while (i < n) {
            try {
                final int selectRandomPort = this.selectRandomPort();
                final String b = this.b(s, selectRandomPort);
                final _d d = new _d(s, selectRandomPort, s2, n2);
                d.d();
                this.d.put(b, d);
                this.h.put(b, new Vector());
                for (int j = 0; j < this.i.size(); ++j) {
                    ((ForwardingClientListener)this.i.elementAt(j)).forwardingStarted(1, b, s2, n2);
                }
                EventServiceImplementation.getInstance().fireEvent(new Event(this, 16, true).addAttribute("FORWARDING_TUNNEL_ENTRANCE", b).addAttribute("FORWARDING_TUNNEL_EXIT", s2 + ":" + n2));
                return selectRandomPort;
            }
            catch (final Throwable t) {
                ++i;
                continue;
            }
            break;
        }
        throw new SshException("Maximum retry limit reached for random port selection", 14);
    }
    
    public String[] getRemoteForwardings() {
        final String[] array = new String[this.j.size() - (this.j.containsKey("X11") ? 1 : 0)];
        int n = 0;
        final Enumeration keys = this.j.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            if (!s.equals("X11")) {
                array[n++] = s;
            }
        }
        return array;
    }
    
    public String[] getLocalForwardings() {
        final String[] array = new String[this.d.size()];
        int n = 0;
        final Enumeration keys = this.d.keys();
        while (keys.hasMoreElements()) {
            array[n++] = (String)keys.nextElement();
        }
        return array;
    }
    
    public ActiveTunnel[] getLocalForwardingTunnels(final String s) throws IOException {
        if (!this.d.containsKey(s)) {
            throw new IOException(s + " is not a valid local forwarding configuration");
        }
        if (this.h.containsKey(s)) {
            final Vector vector = this.h.get(s);
            final ActiveTunnel[] array = new ActiveTunnel[vector.size()];
            vector.copyInto(array);
            return array;
        }
        return new ActiveTunnel[0];
    }
    
    public ActiveTunnel[] getLocalForwardingTunnels(final String s, final int n) throws IOException {
        return this.getLocalForwardingTunnels(this.b(s, n));
    }
    
    public ActiveTunnel[] getRemoteForwardingTunnels() throws IOException {
        final Vector vector = new Vector();
        final String[] remoteForwardings = this.getRemoteForwardings();
        for (int i = 0; i < remoteForwardings.length; ++i) {
            final ActiveTunnel[] remoteForwardingTunnels = this.getRemoteForwardingTunnels(remoteForwardings[i]);
            for (int j = 0; j < remoteForwardingTunnels.length; ++j) {
                vector.add(remoteForwardingTunnels[j]);
            }
        }
        return vector.toArray(new ActiveTunnel[vector.size()]);
    }
    
    public ActiveTunnel[] getLocalForwardingTunnels() throws IOException {
        final Vector vector = new Vector();
        final String[] localForwardings = this.getLocalForwardings();
        for (int i = 0; i < localForwardings.length; ++i) {
            final ActiveTunnel[] localForwardingTunnels = this.getLocalForwardingTunnels(localForwardings[i]);
            for (int j = 0; j < localForwardingTunnels.length; ++j) {
                vector.add(localForwardingTunnels[j]);
            }
        }
        return vector.toArray(new ActiveTunnel[vector.size()]);
    }
    
    public ActiveTunnel[] getRemoteForwardingTunnels(final String s) throws IOException {
        if (!this.j.containsKey(s)) {
            throw new IOException(s + " is not a valid remote forwarding configuration");
        }
        synchronized (this.g) {
            if (this.g.containsKey(s)) {
                final Vector vector = this.g.get(s);
                final ActiveTunnel[] array = new ActiveTunnel[vector.size()];
                vector.copyInto(array);
                return array;
            }
        }
        return new ActiveTunnel[0];
    }
    
    public boolean isXForwarding() {
        return this.b;
    }
    
    public ActiveTunnel[] getRemoteForwardingTunnels(final String s, final int n) throws IOException {
        return this.getRemoteForwardingTunnels(this.b(s, n));
    }
    
    public ActiveTunnel[] getX11ForwardingTunnels() throws IOException {
        if (!this.j.containsKey("X11")) {
            throw new IOException("Failed to find X11 forwarding key");
        }
        if (this.g.containsKey("X11")) {
            final Vector vector = this.g.get("X11");
            final ActiveTunnel[] array = new ActiveTunnel[vector.size()];
            vector.copyInto(array);
            return array;
        }
        return new ActiveTunnel[0];
    }
    
    public boolean requestRemoteForwarding(final String s, final int n, final String s2, final int n2) throws SshException {
        if (this.e.requestRemoteForwarding(s, n, s2, n2, this.f)) {
            final String b = this.b(s, n);
            this.g.put(b, new Vector());
            this.j.put(b, s2 + ":" + n2);
            for (int i = 0; i < this.i.size(); ++i) {
                ((ForwardingClientListener)this.i.elementAt(i)).forwardingStarted(2, b, s2, n2);
            }
            return true;
        }
        return false;
    }
    
    public void allowX11Forwarding(String substring, final String s) throws SshException {
        if (this.j.containsKey("X11")) {
            throw new SshException("X11 forwarding is already in use!", 14);
        }
        this.g.put("X11", new Vector());
        this.e.getContext().setX11Display(substring);
        this.e.getContext().setX11RequestListener(this.f);
        final byte[] x11RealCookie = new byte[16];
        if (s.length() != 32) {
            throw new SshException("Invalid MIT-MAGIC_COOKIE-1 value " + s, 14);
        }
        for (int i = 0; i < 32; i += 2) {
            x11RealCookie[i / 2] = (byte)Integer.parseInt(s.substring(i, i + 2), 16);
        }
        this.e.getContext().setX11RealCookie(x11RealCookie);
        String substring2 = "localhost";
        int int1 = 0;
        final int index = substring.indexOf(58);
        if (index != -1) {
            substring2 = substring.substring(0, index);
            substring = substring.substring(index + 1);
        }
        final int index2;
        if ((index2 = substring.indexOf(46)) > -1) {
            int1 = Integer.parseInt(substring.substring(index2 + 1));
        }
        for (int j = 0; j < this.i.size(); ++j) {
            ((ForwardingClientListener)this.i.elementAt(j)).forwardingStarted(3, "X11", substring2, int1);
        }
        this.b = true;
    }
    
    public void allowX11Forwarding(final String s) throws SshException {
        String property = "";
        try {
            property = System.getProperty("user.home");
        }
        catch (final SecurityException ex) {}
        this.allowX11Forwarding(s, new File(property, ".Xauthority"));
    }
    
    public void allowX11Forwarding(String substring, final File file) throws SshException {
        if (this.g.containsKey("X11")) {
            throw new SshException("X11 forwarding is already in use!", 14);
        }
        this.g.put("X11", new Vector());
        this.e.getContext().setX11Display(substring);
        this.e.getContext().setX11RequestListener(this.f);
        try {
            if (file.exists()) {
                String substring2 = "";
                int int1 = 0;
                final int index = substring.indexOf(58);
                if (index != -1) {
                    substring2 = substring.substring(0, index);
                    int1 = Integer.parseInt(substring.substring(index + 1));
                }
                final FileInputStream fileInputStream = new FileInputStream(file);
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int read;
                while ((read = fileInputStream.read()) != -1) {
                    byteArrayOutputStream.write(read);
                }
                fileInputStream.close();
                final ByteArrayReader byteArrayReader = new ByteArrayReader(byteArrayOutputStream.toByteArray());
                while (byteArrayReader.available() > 0) {
                    final short short1 = byteArrayReader.readShort();
                    final byte[] array = new byte[byteArrayReader.readShort()];
                    byteArrayReader.read(array);
                    final byte[] array2 = new byte[byteArrayReader.readShort()];
                    byteArrayReader.read(array2);
                    final byte[] array3 = new byte[byteArrayReader.readShort()];
                    byteArrayReader.read(array3);
                    final byte[] array4 = new byte[byteArrayReader.readShort()];
                    byteArrayReader.read(array4);
                    final int int2 = Integer.parseInt(new String(array2));
                    if (new String(array3).equals("MIT-MAGIC-COOKIE-1")) {
                        if (short1 == 0) {
                            final InetAddress byName = InetAddress.getByName((array[0] & 0xFF) + "." + (array[1] & 0xFF) + "." + (array[2] & 0xFF) + "." + (array[3] & 0xFF));
                            if ((byName.getHostAddress().equals(substring2) || byName.getHostName().equals(substring2)) && int1 == int2) {
                                this.e.getContext().setX11RealCookie(array4);
                                break;
                            }
                            continue;
                        }
                        else {
                            if (short1 == 256 && new String(array).equals(substring2) && int1 == int2) {
                                this.e.getContext().setX11RealCookie(array4);
                                break;
                            }
                            continue;
                        }
                    }
                }
            }
            String substring3 = "localhost";
            int int3 = 0;
            final int index2 = substring.indexOf(58);
            if (index2 != -1) {
                substring3 = substring.substring(0, index2);
                substring = substring.substring(index2 + 1);
            }
            final int index3;
            if ((index3 = substring.indexOf(46)) > -1) {
                int3 = Integer.parseInt(substring.substring(index3 + 1));
            }
            for (int i = 0; i < this.i.size(); ++i) {
                ((ForwardingClientListener)this.i.elementAt(i)).forwardingStarted(3, "X11", substring3, int3);
            }
            this.b = true;
        }
        catch (final IOException ex) {
            throw new SshException(ex.getMessage(), 14);
        }
    }
    
    public void cancelRemoteForwarding(final String s, final int n) throws SshException {
        this.cancelRemoteForwarding(s, n, false);
    }
    
    public void cancelRemoteForwarding(final String s, final int n, final boolean b) throws SshException {
        final String b2 = this.b(s, n);
        if (!this.j.containsKey(b2)) {
            throw new SshException("Remote forwarding has not been started on " + b2, 14);
        }
        if (b) {
            try {
                final ActiveTunnel[] remoteForwardingTunnels = this.getRemoteForwardingTunnels(s, n);
                if (remoteForwardingTunnels != null) {
                    for (int i = 0; i < remoteForwardingTunnels.length; ++i) {
                        remoteForwardingTunnels[i].stop();
                    }
                }
            }
            catch (final IOException ex) {}
        }
        if (this.e == null) {
            return;
        }
        this.e.cancelRemoteForwarding(s, n);
        this.g.remove(b2);
        final String s2 = this.j.get(b2);
        final int index = s2.indexOf(":");
        if (index == -1) {
            throw new SshException("Invalid port reference in remote forwarding key!", 5);
        }
        final String substring = s2.substring(0, index);
        final int int1 = Integer.parseInt(s2.substring(index + 1));
        for (int j = 0; j < this.i.size(); ++j) {
            if (this.i.elementAt(j) != null) {
                ((ForwardingClientListener)this.i.elementAt(j)).forwardingStopped(2, b2, substring, int1);
            }
        }
        this.j.remove(b2);
    }
    
    public synchronized void cancelAllRemoteForwarding() throws SshException {
        this.cancelAllRemoteForwarding(false);
    }
    
    public synchronized void cancelAllRemoteForwarding(final boolean b) throws SshException {
        if (this.j == null) {
            return;
        }
        final Enumeration keys = this.j.keys();
        while (keys.hasMoreElements()) {
            final String s = (String)keys.nextElement();
            if (s == null) {
                return;
            }
            try {
                final int index = s.indexOf(58);
                int n;
                String substring;
                if (index == -1) {
                    n = Integer.parseInt(s);
                    substring = "";
                }
                else {
                    n = Integer.parseInt(s.substring(index + 1));
                    substring = s.substring(0, index);
                }
                this.cancelRemoteForwarding(substring, n, b);
            }
            catch (final NumberFormatException ex) {}
        }
    }
    
    protected int selectRandomPort() {
        try {
            int n = ComponentManager.getInstance().getRND().nextInt() % 16384;
            if (n < 0) {
                n = -n;
            }
            return 49152 + n;
        }
        catch (final SshException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public synchronized void stopAllLocalForwarding() throws SshException {
        this.stopAllLocalForwarding(false);
    }
    
    public synchronized void stopAllLocalForwarding(final boolean b) throws SshException {
        final Enumeration keys = this.d.keys();
        while (keys.hasMoreElements()) {
            this.stopLocalForwarding((String)keys.nextElement(), b);
        }
    }
    
    public synchronized void stopLocalForwarding(final String s, final int n) throws SshException {
        this.stopLocalForwarding(s, n, false);
    }
    
    public synchronized void stopLocalForwarding(final String s, final int n, final boolean b) throws SshException {
        this.stopLocalForwarding(this.b(s, n), b);
    }
    
    public synchronized void stopLocalForwarding(final String s, final boolean b) throws SshException {
        if (s == null) {
            return;
        }
        if (!this.d.containsKey(s)) {
            throw new SshException("Local forwarding has not been started for " + s, 14);
        }
        final _d d = this.d.get(s);
        if (d == null) {
            return;
        }
        if (b) {
            try {
                final ActiveTunnel[] localForwardingTunnels = this.getLocalForwardingTunnels(s);
                if (localForwardingTunnels != null) {
                    for (int i = 0; i < localForwardingTunnels.length; ++i) {
                        localForwardingTunnels[i].stop();
                    }
                }
            }
            catch (final IOException ex) {}
        }
        d.b();
        this.h.remove(s);
        this.d.remove(s);
        for (int j = 0; j < this.i.size(); ++j) {
            if (this.i.elementAt(j) != null) {
                ((ForwardingClientListener)this.i.elementAt(j)).forwardingStopped(1, s, d.f, d.c);
            }
        }
        EventServiceImplementation.getInstance().fireEvent(new Event(this, 18, true).addAttribute("FORWARDING_TUNNEL_ENTRANCE", s).addAttribute("FORWARDING_TUNNEL_EXIT", d.f + ":" + d.c));
    }
    
    String b(final String s, final int n) {
        return s.equals("") ? String.valueOf(n) : (s + ":" + String.valueOf(n));
    }
    
    public void exit() throws SshException {
        this.stopAllLocalForwarding();
        this.cancelAllRemoteForwarding();
    }
    
    public class ActiveTunnel
    {
        SshTunnel c;
        IOStreamConnector b;
        IOStreamConnector e;
        _b d;
        
        ActiveTunnel(final SshTunnel c) {
            this.d = new _b();
            this.c = c;
        }
        
        void b() throws IOException {
            try {
                for (int i = 0; i < ForwardingClient.this.i.size(); ++i) {
                    ((ForwardingClientListener)ForwardingClient.this.i.elementAt(i)).channelOpened(this.c.isLocal() ? 1 : (this.c.isX11() ? 3 : 2), this.c.isX11() ? "X11" : ForwardingClient.this.b(this.c.getListeningAddress(), this.c.getListeningPort()), this.c);
                }
                (this.e = new IOStreamConnector()).addListener(this.d);
                this.e.connect(this.c.getInputStream(), this.c.getTransport().getOutputStream());
                (this.b = new IOStreamConnector()).addListener(this.d);
                this.b.connect(this.c.getTransport().getInputStream(), this.c.getOutputStream());
                final String b = ForwardingClient.this.b(this.c.getListeningAddress(), this.c.getListeningPort());
                final Hashtable hashtable = this.c.isLocal() ? ForwardingClient.this.h : ForwardingClient.this.g;
                if (!hashtable.containsKey(b)) {
                    hashtable.put(b, new Vector());
                }
                ((Vector<ActiveTunnel>)hashtable.get(b)).addElement(this);
            }
            catch (final Exception ex) {
                EventLog.LogEvent(this, "Exception whilst opening channel", ex);
                try {
                    this.c.close();
                }
                catch (final Exception ex2) {}
                throw new IOException("The tunnel failed to start: " + ex.getMessage());
            }
        }
        
        public synchronized void stop() {
            if (!this.e.isClosed()) {
                this.e.close();
            }
            if (!this.b.isClosed()) {
                this.b.close();
            }
            final String b = ForwardingClient.this.b(this.c.getListeningAddress(), this.c.getListeningPort());
            final Vector vector = (this.c.isLocal() ? ForwardingClient.this.h : ForwardingClient.this.g).get(b);
            if (vector != null && vector.contains(this)) {
                vector.removeElement(this);
                for (int i = 0; i < ForwardingClient.this.i.size(); ++i) {
                    ((ForwardingClientListener)ForwardingClient.this.i.elementAt(i)).channelClosed(this.c.isLocal() ? 1 : (this.c.isX11() ? 3 : 2), this.c.isX11() ? "X11" : b, this.c);
                }
            }
        }
        
        class _b implements IOStreamConnector.IOStreamConnectorListener
        {
            public synchronized void connectorClosed(final IOStreamConnector ioStreamConnector) {
                if (!ActiveTunnel.this.c.isClosed()) {
                    try {
                        ActiveTunnel.this.c.getTransport().close();
                    }
                    catch (final IOException ex) {}
                    try {
                        ActiveTunnel.this.c.close();
                    }
                    catch (final Exception ex2) {}
                }
                ActiveTunnel.this.stop();
            }
            
            public void dataTransfered(final byte[] array, final int n) {
            }
            
            public void connectorTimeout(final IOStreamConnector ioStreamConnector) {
                if (!ActiveTunnel.this.c.isLocalEOF()) {
                    if (!ActiveTunnel.this.c.isRemoteEOF()) {
                        return;
                    }
                }
                try {
                    ActiveTunnel.this.c.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    class _b extends ChannelAdapter
    {
        public void channelOpened(final SshChannel sshChannel) {
            if (sshChannel instanceof SshTunnel) {
                final ActiveTunnel activeTunnel = new ActiveTunnel((SshTunnel)sshChannel);
                try {
                    activeTunnel.b();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    class _c implements ForwardingRequestListener
    {
        public SshTransport createConnection(final String s, final int n) throws SshException {
            try {
                final SocketTransport socketTransport = new SocketTransport(s, n);
                socketTransport.setSoTimeout(30000);
                return socketTransport;
            }
            catch (final IOException ex) {
                for (int i = 0; i < ForwardingClient.this.i.size(); ++i) {
                    ((ForwardingClientListener)ForwardingClient.this.i.elementAt(i)).channelFailure(2, s + ":" + n, s, n, ForwardingClient.this.e.isConnected(), ex);
                }
                throw new SshException("Failed to connect", 10);
            }
        }
        
        public void initializeTunnel(final SshTunnel sshTunnel) {
            sshTunnel.addChannelEventListener(ForwardingClient.this.c);
        }
    }
    
    class _d implements Runnable
    {
        String e;
        int h;
        String f;
        int c;
        ServerSocket g;
        private Thread b;
        private boolean d;
        
        public _d(final String e, final int h, final String f, final int c) {
            this.e = e;
            this.h = h;
            this.f = f;
            this.c = c;
        }
        
        public boolean c() {
            return this.d;
        }
        
        public void run() {
            try {
                this.d = true;
                while (this.d && ForwardingClient.this.e.isConnected()) {
                    final Socket accept = this.g.accept();
                    if (!this.d) {
                        break;
                    }
                    if (accept == null) {
                        break;
                    }
                    new Thread() {
                        public void run() {
                            try {
                                ForwardingClient.this.e.openForwardingChannel(_d.this.f, _d.this.c, _d.this.e, _d.this.h, accept.getInetAddress().getHostAddress(), accept.getPort(), new SocketWrapper(accept), ForwardingClient.this.c);
                                accept.setSoTimeout(30000);
                            }
                            catch (final Exception ex) {
                                EventLog.LogEvent(this, "Exception whilst opening channel", ex);
                                try {
                                    accept.close();
                                }
                                catch (final IOException ex2) {}
                                finally {
                                    for (int i = 0; i < ForwardingClient.this.i.size(); ++i) {
                                        ((ForwardingClientListener)ForwardingClient.this.i.elementAt(i)).channelFailure(1, _d.this.e + ":" + _d.this.h, _d.this.f, _d.this.c, ForwardingClient.this.e.isConnected(), ex);
                                    }
                                }
                            }
                        }
                    }.start();
                }
            }
            catch (final IOException ex) {}
            finally {
                this.b();
                this.b = null;
            }
        }
        
        public void d() throws SshException {
            try {
                this.g = new ServerSocket(this.h, 1000, this.e.equals("") ? null : InetAddress.getByName(this.e));
                (this.b = new Thread(this)).setDaemon(true);
                this.b.setName("SocketListener " + this.e + ":" + String.valueOf(this.h));
                this.b.start();
            }
            catch (final IOException ex) {
                throw new SshException("Failed to local forwarding server. ", 6, ex);
            }
        }
        
        public void b() {
            try {
                if (this.g != null) {
                    this.g.close();
                }
            }
            catch (final IOException ex) {}
            this.g = null;
            this.d = false;
        }
    }
}
