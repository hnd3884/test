package sun.management;

import java.lang.reflect.Type;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import com.sun.management.VMOption;

public class VMOptionCompositeData extends LazyCompositeData
{
    private final VMOption option;
    private static final CompositeType vmOptionCompositeType;
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String WRITEABLE = "writeable";
    private static final String ORIGIN = "origin";
    private static final String[] vmOptionItemNames;
    private static final long serialVersionUID = -2395573975093578470L;
    
    private VMOptionCompositeData(final VMOption option) {
        this.option = option;
    }
    
    public VMOption getVMOption() {
        return this.option;
    }
    
    public static CompositeData toCompositeData(final VMOption vmOption) {
        return new VMOptionCompositeData(vmOption).getCompositeData();
    }
    
    @Override
    protected CompositeData getCompositeData() {
        final Object[] array = { this.option.getName(), this.option.getValue(), new Boolean(this.option.isWriteable()), this.option.getOrigin().toString() };
        try {
            return new CompositeDataSupport(VMOptionCompositeData.vmOptionCompositeType, VMOptionCompositeData.vmOptionItemNames, array);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    static CompositeType getVMOptionCompositeType() {
        return VMOptionCompositeData.vmOptionCompositeType;
    }
    
    public static String getName(final CompositeData compositeData) {
        return LazyCompositeData.getString(compositeData, "name");
    }
    
    public static String getValue(final CompositeData compositeData) {
        return LazyCompositeData.getString(compositeData, "value");
    }
    
    public static VMOption.Origin getOrigin(final CompositeData compositeData) {
        return Enum.valueOf(VMOption.Origin.class, LazyCompositeData.getString(compositeData, "origin"));
    }
    
    public static boolean isWriteable(final CompositeData compositeData) {
        return LazyCompositeData.getBoolean(compositeData, "writeable");
    }
    
    public static void validateCompositeData(final CompositeData compositeData) {
        if (compositeData == null) {
            throw new NullPointerException("Null CompositeData");
        }
        if (!LazyCompositeData.isTypeMatched(VMOptionCompositeData.vmOptionCompositeType, compositeData.getCompositeType())) {
            throw new IllegalArgumentException("Unexpected composite type for VMOption");
        }
    }
    
    static {
        try {
            vmOptionCompositeType = (CompositeType)MappedMXBeanType.toOpenType(VMOption.class);
        }
        catch (final OpenDataException ex) {
            throw new AssertionError((Object)ex);
        }
        vmOptionItemNames = new String[] { "name", "value", "writeable", "origin" };
    }
}
