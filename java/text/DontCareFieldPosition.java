package java.text;

class DontCareFieldPosition extends FieldPosition
{
    static final FieldPosition INSTANCE;
    private final Format.FieldDelegate noDelegate;
    
    private DontCareFieldPosition() {
        super(0);
        this.noDelegate = new Format.FieldDelegate() {
            @Override
            public void formatted(final Format.Field field, final Object o, final int n, final int n2, final StringBuffer sb) {
            }
            
            @Override
            public void formatted(final int n, final Format.Field field, final Object o, final int n2, final int n3, final StringBuffer sb) {
            }
        };
    }
    
    @Override
    Format.FieldDelegate getFieldDelegate() {
        return this.noDelegate;
    }
    
    static {
        INSTANCE = new DontCareFieldPosition();
    }
}
