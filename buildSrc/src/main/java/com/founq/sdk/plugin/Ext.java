package com.founq.sdk.plugin;

/**
 * Created by ring on 2021/2/20.
 * 类似android插件的compileSdkVersion那些
 */
public class Ext {

    int compileVersion;
    String buildVersion;

    public Ext() {
    }

    public Ext(int compileVersion, String buildVersion) {
        this.compileVersion = compileVersion;
        this.buildVersion = buildVersion;
    }

    public int getCompileVersion() {
        return compileVersion;
    }

    public void setCompileVersion(int compileVersion) {
        this.compileVersion = compileVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }
}
