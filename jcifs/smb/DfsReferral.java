package jcifs.smb;

public class DfsReferral extends SmbException
{
    public String path;
    public String node;
    public String server;
    public String share;
    public String nodepath;
    public boolean resolveHashes;
    
    public String toString() {
        return "DfsReferral[path=" + this.path + ",node=" + this.node + ",server=" + this.server + ",share=" + this.share + ",nodepath=" + this.nodepath + ",resolveHashes=" + this.resolveHashes + "]";
    }
}
