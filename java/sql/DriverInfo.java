package java.sql;

class DriverInfo
{
    final Driver driver;
    DriverAction da;
    
    DriverInfo(final Driver driver, final DriverAction da) {
        this.driver = driver;
        this.da = da;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DriverInfo && this.driver == ((DriverInfo)o).driver;
    }
    
    @Override
    public int hashCode() {
        return this.driver.hashCode();
    }
    
    @Override
    public String toString() {
        return "driver[className=" + this.driver + "]";
    }
    
    DriverAction action() {
        return this.da;
    }
}
