package HTTPClient;

import java.util.Enumeration;
import java.util.Hashtable;

class IdempotentSequence
{
    private static final int UNKNOWN = 0;
    private static final int HEAD = 1;
    private static final int GET = 2;
    private static final int POST = 3;
    private static final int PUT = 4;
    private static final int DELETE = 5;
    private static final int OPTIONS = 6;
    private static final int TRACE = 7;
    private static final int PROPFIND = 8;
    private static final int PROPPATCH = 9;
    private static final int MKCOL = 10;
    private static final int COPY = 11;
    private static final int MOVE = 12;
    private static final int LOCK = 13;
    private static final int UNLOCK = 14;
    private int[] m_history;
    private String[] r_history;
    private int m_len;
    private int r_len;
    private boolean analysis_done;
    private Hashtable threads;
    private static final Object INDET;
    
    public IdempotentSequence() {
        this.analysis_done = false;
        this.threads = new Hashtable();
        this.m_history = new int[10];
        this.r_history = new String[10];
        this.m_len = 0;
        this.r_len = 0;
    }
    
    public void add(final Request req) {
        if (this.m_len >= this.m_history.length) {
            this.m_history = Util.resizeArray(this.m_history, this.m_history.length + 10);
        }
        this.m_history[this.m_len++] = methodNum(req.getMethod());
        if (this.r_len >= this.r_history.length) {
            this.r_history = Util.resizeArray(this.r_history, this.r_history.length + 10);
        }
        this.r_history[this.r_len++] = req.getRequestURI();
    }
    
    public boolean isIdempotent(final Request req) {
        if (!this.analysis_done) {
            this.do_analysis();
        }
        return this.threads.get(req.getRequestURI());
    }
    
    private void do_analysis() {
        for (int idx = 0; idx < this.r_len; ++idx) {
            final Object t_state = this.threads.get(this.r_history[idx]);
            if (this.m_history[idx] == 0) {
                this.threads.put(this.r_history[idx], Boolean.FALSE);
            }
            else if (t_state == null) {
                if (methodHasSideEffects(this.m_history[idx])) {
                    if (methodIsComplete(this.m_history[idx])) {
                        this.threads.put(this.r_history[idx], Boolean.TRUE);
                    }
                    else {
                        this.threads.put(this.r_history[idx], Boolean.FALSE);
                    }
                }
                else {
                    this.threads.put(this.r_history[idx], IdempotentSequence.INDET);
                }
            }
            else if (t_state == IdempotentSequence.INDET && methodHasSideEffects(this.m_history[idx])) {
                this.threads.put(this.r_history[idx], Boolean.FALSE);
            }
        }
        final Enumeration te = this.threads.keys();
        while (te.hasMoreElements()) {
            final String res = te.nextElement();
            if (this.threads.get(res) == IdempotentSequence.INDET) {
                this.threads.put(res, Boolean.TRUE);
            }
        }
    }
    
    public static boolean methodIsIdempotent(final String method) {
        return methodIsIdempotent(methodNum(method));
    }
    
    private static boolean methodIsIdempotent(final int method) {
        switch (method) {
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean methodIsComplete(final String method) {
        return methodIsComplete(methodNum(method));
    }
    
    private static boolean methodIsComplete(final int method) {
        switch (method) {
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 11:
            case 12:
            case 13:
            case 14: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean methodHasSideEffects(final String method) {
        return methodHasSideEffects(methodNum(method));
    }
    
    private static boolean methodHasSideEffects(final int method) {
        switch (method) {
            case 1:
            case 2:
            case 6:
            case 7:
            case 8:
            case 13:
            case 14: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    private static int methodNum(final String method) {
        if (method.equals("GET")) {
            return 2;
        }
        if (method.equals("POST")) {
            return 3;
        }
        if (method.equals("HEAD")) {
            return 1;
        }
        if (method.equals("PUT")) {
            return 4;
        }
        if (method.equals("DELETE")) {
            return 5;
        }
        if (method.equals("OPTIONS")) {
            return 6;
        }
        if (method.equals("TRACE")) {
            return 7;
        }
        if (method.equals("PROPFIND")) {
            return 8;
        }
        if (method.equals("PROPPATCH")) {
            return 9;
        }
        if (method.equals("MKCOL")) {
            return 10;
        }
        if (method.equals("COPY")) {
            return 11;
        }
        if (method.equals("MOVE")) {
            return 12;
        }
        if (method.equals("LOCK")) {
            return 13;
        }
        if (method.equals("UNLOCK")) {
            return 14;
        }
        return 0;
    }
    
    public static void main(final String[] args) {
        final IdempotentSequence seq = new IdempotentSequence();
        seq.add(new Request(null, "GET", "/b1", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b2", null, null, null, false));
        seq.add(new Request(null, "GET", "/b1", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b3", null, null, null, false));
        seq.add(new Request(null, "GET", "/b2", null, null, null, false));
        seq.add(new Request(null, "POST", "/b8", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b3", null, null, null, false));
        seq.add(new Request(null, "GET", "/b1", null, null, null, false));
        seq.add(new Request(null, "TRACE", "/b4", null, null, null, false));
        seq.add(new Request(null, "GET", "/b9", null, null, null, false));
        seq.add(new Request(null, "LINK", "/b4", null, null, null, false));
        seq.add(new Request(null, "GET", "/b4", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b5", null, null, null, false));
        seq.add(new Request(null, "HEAD", "/b5", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b5", null, null, null, false));
        seq.add(new Request(null, "POST", "/b9", null, null, null, false));
        seq.add(new Request(null, "GET", "/b6", null, null, null, false));
        seq.add(new Request(null, "DELETE", "/b6", null, null, null, false));
        seq.add(new Request(null, "HEAD", "/b6", null, null, null, false));
        seq.add(new Request(null, "OPTIONS", "/b7", null, null, null, false));
        seq.add(new Request(null, "TRACE", "/b7", null, null, null, false));
        seq.add(new Request(null, "GET", "/b7", null, null, null, false));
        seq.add(new Request(null, "PUT", "/b7", null, null, null, false));
        if (!seq.isIdempotent(new Request(null, null, "/b1", null, null, null, false))) {
            System.err.println("Sequence b1 failed");
        }
        if (!seq.isIdempotent(new Request(null, null, "/b2", null, null, null, false))) {
            System.err.println("Sequence b2 failed");
        }
        if (!seq.isIdempotent(new Request(null, null, "/b3", null, null, null, false))) {
            System.err.println("Sequence b3 failed");
        }
        if (seq.isIdempotent(new Request(null, null, "/b4", null, null, null, false))) {
            System.err.println("Sequence b4 failed");
        }
        if (!seq.isIdempotent(new Request(null, null, "/b5", null, null, null, false))) {
            System.err.println("Sequence b5 failed");
        }
        if (seq.isIdempotent(new Request(null, null, "/b6", null, null, null, false))) {
            System.err.println("Sequence b6 failed");
        }
        if (seq.isIdempotent(new Request(null, null, "/b7", null, null, null, false))) {
            System.err.println("Sequence b7 failed");
        }
        if (seq.isIdempotent(new Request(null, null, "/b8", null, null, null, false))) {
            System.err.println("Sequence b8 failed");
        }
        if (seq.isIdempotent(new Request(null, null, "/b9", null, null, null, false))) {
            System.err.println("Sequence b9 failed");
        }
        System.out.println("Tests finished");
    }
    
    static {
        INDET = new Object();
    }
}
