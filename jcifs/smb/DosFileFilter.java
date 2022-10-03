package jcifs.smb;

public class DosFileFilter implements SmbFileFilter
{
    protected String wildcard;
    protected int attributes;
    
    public DosFileFilter(final String wildcard, final int attributes) {
        this.wildcard = wildcard;
        this.attributes = attributes;
    }
    
    public boolean accept(final SmbFile file) throws SmbException {
        return (file.getAttributes() & this.attributes) != 0x0;
    }
}
