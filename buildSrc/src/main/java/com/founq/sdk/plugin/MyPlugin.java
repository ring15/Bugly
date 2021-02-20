package com.founq.sdk.plugin;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by ring on 2021/2/20.
 */
public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("====自定义扩展=====");
        Ext ext = project.getExtensions().create("androidx", Ext.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                System.out.println(ext.getBuildVersion());
                System.out.println(ext.getCompileVersion());
            }
        });
    }
}
