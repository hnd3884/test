package sun.security.provider.certpath;

import java.security.cert.X509Certificate;

public class BuildStep
{
    private Vertex vertex;
    private X509Certificate cert;
    private Throwable throwable;
    private int result;
    public static final int POSSIBLE = 1;
    public static final int BACK = 2;
    public static final int FOLLOW = 3;
    public static final int FAIL = 4;
    public static final int SUCCEED = 5;
    
    public BuildStep(final Vertex vertex, final int result) {
        this.vertex = vertex;
        if (this.vertex != null) {
            this.cert = this.vertex.getCertificate();
            this.throwable = this.vertex.getThrowable();
        }
        this.result = result;
    }
    
    public Vertex getVertex() {
        return this.vertex;
    }
    
    public X509Certificate getCertificate() {
        return this.cert;
    }
    
    public String getIssuerName() {
        return this.getIssuerName(null);
    }
    
    public String getIssuerName(final String s) {
        return (this.cert == null) ? s : this.cert.getIssuerX500Principal().toString();
    }
    
    public String getSubjectName() {
        return this.getSubjectName(null);
    }
    
    public String getSubjectName(final String s) {
        return (this.cert == null) ? s : this.cert.getSubjectX500Principal().toString();
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public int getResult() {
        return this.result;
    }
    
    public String resultToString(final int n) {
        String s = null;
        switch (n) {
            case 1: {
                s = "Certificate to be tried.\n";
                break;
            }
            case 2: {
                s = "Certificate backed out since path does not satisfy build requirements.\n";
                break;
            }
            case 3: {
                s = "Certificate satisfies conditions.\n";
                break;
            }
            case 4: {
                s = "Certificate backed out since path does not satisfy conditions.\n";
                break;
            }
            case 5: {
                s = "Certificate satisfies conditions.\n";
                break;
            }
            default: {
                s = "Internal error: Invalid step result value.\n";
                break;
            }
        }
        return s;
    }
    
    @Override
    public String toString() {
        String s = null;
        switch (this.result) {
            case 2:
            case 4: {
                s = this.resultToString(this.result) + this.vertex.throwableToString();
                break;
            }
            case 1:
            case 3:
            case 5: {
                s = this.resultToString(this.result);
                break;
            }
            default: {
                s = "Internal Error: Invalid step result\n";
                break;
            }
        }
        return s;
    }
    
    public String verboseToString() {
        String s = this.resultToString(this.getResult());
        switch (this.result) {
            case 2:
            case 4: {
                s += this.vertex.throwableToString();
                break;
            }
            case 3:
            case 5: {
                s += this.vertex.moreToString();
            }
        }
        return s + "Certificate contains:\n" + this.vertex.certToString();
    }
    
    public String fullToString() {
        return this.resultToString(this.getResult()) + this.vertex.toString();
    }
}
