package com.commitstrip.commitstripreader.util;

import android.security.NetworkSecurityPolicy;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(NetworkSecurityPolicy.class)
public class NetworkSecurityPolicyWorkaround {
    @Implementation
    public static NetworkSecurityPolicy getInstance() {
        //noinspection OverlyBroadCatchBlock
        try {
            Class<?> shadow = Class.forName("android.security.NetworkSecurityPolicy");
            return (NetworkSecurityPolicy) shadow.newInstance();
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    @Implementation
    public boolean isCleartextTrafficPermitted(String hostname) {
        return true;
    }
}
