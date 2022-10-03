package org.apache.http.protocol;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpRequestInterceptor;

public interface HttpProcessor extends HttpRequestInterceptor, HttpResponseInterceptor
{
}
