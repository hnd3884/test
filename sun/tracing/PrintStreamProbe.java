package sun.tracing;

class PrintStreamProbe extends ProbeSkeleton
{
    private PrintStreamProvider provider;
    private String name;
    
    PrintStreamProbe(final PrintStreamProvider provider, final String name, final Class<?>[] array) {
        super(array);
        this.provider = provider;
        this.name = name;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public void uncheckedTrigger(final Object[] array) {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.provider.getName());
        sb.append(".");
        sb.append(this.name);
        sb.append("(");
        int n = 1;
        for (final Object o : array) {
            if (n == 0) {
                sb.append(",");
            }
            else {
                n = 0;
            }
            sb.append(o.toString());
        }
        sb.append(")");
        this.provider.getStream().println(sb.toString());
    }
}
