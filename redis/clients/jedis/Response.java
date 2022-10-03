package redis.clients.jedis;

import redis.clients.jedis.exceptions.JedisDataException;

public class Response<T>
{
    protected T response;
    protected JedisDataException exception;
    private boolean building;
    private boolean built;
    private boolean set;
    private Builder<T> builder;
    private Object data;
    private Response<?> dependency;
    
    public Response(final Builder<T> b) {
        this.response = null;
        this.exception = null;
        this.building = false;
        this.built = false;
        this.set = false;
        this.dependency = null;
        this.builder = b;
    }
    
    public void set(final Object data) {
        this.data = data;
        this.set = true;
    }
    
    public T get() {
        if (this.dependency != null && this.dependency.set && !this.dependency.built) {
            this.dependency.build();
        }
        if (!this.set) {
            throw new JedisDataException("Please close pipeline or multi block before calling this method.");
        }
        if (!this.built) {
            this.build();
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.response;
    }
    
    public void setDependency(final Response<?> dependency) {
        this.dependency = dependency;
    }
    
    private void build() {
        if (this.building) {
            return;
        }
        this.building = true;
        try {
            if (this.data != null) {
                if (this.data instanceof JedisDataException) {
                    this.exception = (JedisDataException)this.data;
                }
                else {
                    this.response = this.builder.build(this.data);
                }
            }
            this.data = null;
        }
        finally {
            this.building = false;
            this.built = true;
        }
    }
    
    @Override
    public String toString() {
        return "Response " + this.builder.toString();
    }
}
