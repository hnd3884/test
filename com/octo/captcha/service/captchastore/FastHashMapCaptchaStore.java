package com.octo.captcha.service.captchastore;

import java.util.Map;
import org.apache.commons.collections.FastHashMap;

public class FastHashMapCaptchaStore extends MapCaptchaStore
{
    public FastHashMapCaptchaStore() {
        this.store = (Map)new FastHashMap();
    }
}
