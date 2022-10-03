package com.adventnet.iam.security.antivirus;

import java.io.File;
import java.util.List;

public interface VendorAVProvider
{
    List<VendorAV<File>> getVendorAVs();
}
