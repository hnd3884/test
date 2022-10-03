package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.transport.ReadTimeouts;

public class ReadTCPTimeoutsImpl implements ReadTimeouts
{
    private int initial_time_to_wait;
    private int max_time_to_wait;
    private int max_giop_header_time_to_wait;
    private double backoff_factor;
    
    public ReadTCPTimeoutsImpl(final int initial_time_to_wait, final int max_time_to_wait, final int max_giop_header_time_to_wait, final int n) {
        this.initial_time_to_wait = initial_time_to_wait;
        this.max_time_to_wait = max_time_to_wait;
        this.max_giop_header_time_to_wait = max_giop_header_time_to_wait;
        this.backoff_factor = 1.0 + n / 100.0;
    }
    
    @Override
    public int get_initial_time_to_wait() {
        return this.initial_time_to_wait;
    }
    
    @Override
    public int get_max_time_to_wait() {
        return this.max_time_to_wait;
    }
    
    @Override
    public double get_backoff_factor() {
        return this.backoff_factor;
    }
    
    @Override
    public int get_max_giop_header_time_to_wait() {
        return this.max_giop_header_time_to_wait;
    }
}
