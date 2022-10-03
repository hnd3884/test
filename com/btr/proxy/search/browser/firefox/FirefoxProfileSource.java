package com.btr.proxy.search.browser.firefox;

import java.io.IOException;
import java.io.File;

interface FirefoxProfileSource
{
    File getProfileFolder() throws IOException;
}
