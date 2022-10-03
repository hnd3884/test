package sun.security.x509;

import sun.misc.HexDumpEncoder;

class UnparseableExtension extends Extension
{
    private String name;
    private Throwable why;
    
    public UnparseableExtension(final Extension extension, final Throwable why) {
        super(extension);
        this.name = "";
        try {
            final Class<?> class1 = OIDMap.getClass(extension.getExtensionId());
            if (class1 != null) {
                this.name = (String)class1.getDeclaredField("NAME").get(null) + " ";
            }
        }
        catch (final Exception ex) {}
        this.why = why;
    }
    
    @Override
    public String toString() {
        return super.toString() + "Unparseable " + this.name + "extension due to\n" + this.why + "\n\n" + new HexDumpEncoder().encodeBuffer(this.getExtensionValue());
    }
}
