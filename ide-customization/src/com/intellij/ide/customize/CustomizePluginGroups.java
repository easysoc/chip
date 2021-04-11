package com.intellij.ide.customize;

import icons.PlatformImplIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomizePluginGroups extends PluginGroups {

  @Override
  protected void initGroups(@NotNull List<? super Group> groups, @NotNull Map<String, String> featuredPlugins) {
    groups.add(new Group("Core Functions","Core Functions", PlatformImplIcons.PluginDevelopment, null, Arrays.asList(
            "org.jetbrains.plugins.terminal",
            "com.intellij.tasks",
            "ByteCodeViewer",
            "org.jetbrains.java.decompiler",
            "com.jetbrains.sh",
            "org.intellij.plugins.markdown",
            "com.intellij.properties",
            "com.intellij.copyright"
            )));
    groups.add(new Group("Version Controls","Version Controls", PlatformImplIcons.VersionControls, null, Arrays.asList(
            "Git4Idea",
            "org.jetbrains.plugins.github",
            //"hg4idea",
            "com.jetbrains.changeReminder"
    )));
    groups.add(new Group("Other Tools","Other Tools", PlatformImplIcons.OtherTools, null, Arrays.asList(
            "org.jetbrains.plugins.yaml",
            "org.editorconfig.editorconfigjetbrains"
    )));

    List<IdSet> otherTools = getSets("Other Tools");
    for(IdSet id: otherTools) {
      setIdSetEnabled(id, false);
    }

    // must be here
    //setPluginEnabledWithDependencies(PluginId.getId("hg4idea"),false);
    //setPluginEnabledWithDependencies(PluginId.getId("com.jetbrains.changeReminder"),false);

    initFeaturedPlugins(featuredPlugins);
  }

  @Override
  protected void initFeaturedPlugins(Map<String, String> featuredPlugins) {
    featuredPlugins.put("EasySoC Verilog", "Featured Plugins:Jump to the corresponding Chisel code by navigate(Ctrl+Click) on the special verilog comment:org.easysoc.verilog");
    featuredPlugins.put("Python", "Custom Languages:The Python plug-in provides smart editing for Python scripts:PythonCore");
    featuredPlugins.put("Makefile", "Custom Languages:This plugin provides GNU Make language support:name.kropp.intellij.makefile");
  }
}
