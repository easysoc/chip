package org.jetbrains.intellij.build

import com.intellij.openapi.util.io.FileUtil
import groovy.transform.CompileStatic
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.intellij.build.impl.PluginLayout
import org.jetbrains.intellij.build.python.PythonCommunityPluginModules
import org.jetbrains.jps.model.module.JpsModule

import java.nio.file.Files

import static org.jetbrains.intellij.build.impl.PluginLayout.plugin

@CompileStatic
final class ChipRepositoryModules {
  /**
   * Specifies non-trivial layout for all plugins which sources are located in 'community' and 'contrib' repositories
   */
  static List<PluginLayout> COMMUNITY_REPOSITORY_PLUGINS = [
    plugin("intellij.ant") {
      mainJarName = "antIntegration.jar"
      withModule("intellij.ant.jps")
    },
    plugin("intellij.laf.macos") {
      bundlingRestrictions.supportedOs = [OsFamily.MACOS]
    },
    plugin("intellij.webp"){
      withResource("lib/libwebp/linux", "lib/libwebp/linux")
      withResource("lib/libwebp/mac", "lib/libwebp/mac")
      withResource("lib/libwebp/win", "lib/libwebp/win")
    },
    plugin("intellij.laf.win10") {
      bundlingRestrictions.supportedOs = [OsFamily.WINDOWS]
    },
    plugin("intellij.java.guiForms.designer") {
      directoryName = "uiDesigner"
      mainJarName = "uiDesigner.jar"
      withModule("intellij.java.guiForms.jps", "jps/java-guiForms-jps.jar", null)
    },
    plugin("intellij.properties") {
      withModule("intellij.properties.psi", "properties.jar")
      withModule("intellij.properties.psi.impl", "properties.jar")
    },
    plugin("intellij.properties.resource.bundle.editor"),
    plugin("intellij.vcs.git") {
      withModule("intellij.vcs.git.rt", "git4idea-rt.jar", null)
    },
    plugin("intellij.vcs.svn"){
      withProjectLibrary("sqlite")
    },
    plugin("intellij.xpath") {
      withModule("intellij.xpath.rt", "rt/xslt-rt.jar")
    },
    plugin("intellij.platform.langInjection") {
      withModule("intellij.java.langInjection", "IntelliLang.jar")
      withModule("intellij.xml.langInjection", "IntelliLang.jar")
      withModule("intellij.java.langInjection.jps")
      doNotCreateSeparateJarForLocalizableResources()
    },
    plugin("intellij.tasks.core") {
      directoryName = "tasks"
      withModule("intellij.tasks")
      withModule("intellij.tasks.compatibility")
      withModule("intellij.tasks.jira")
      withModule("intellij.tasks.java")
      doNotCreateSeparateJarForLocalizableResources()
    },
    plugin("intellij.xslt.debugger") {
      withModule("intellij.xslt.debugger.rt", "xslt-debugger-rt.jar")
      withModule("intellij.xslt.debugger.impl.rt", "rt/xslt-debugger-impl-rt.jar")
      withModuleLibrary("Saxon-6.5.5", "intellij.xslt.debugger.impl.rt", "rt")
      withModuleLibrary("Saxon-9HE", "intellij.xslt.debugger.impl.rt", "rt")
      withModuleLibrary("Xalan-2.7.2", "intellij.xslt.debugger.impl.rt", "rt")
    },
    plugin("intellij.maven") {
      withModule("intellij.maven.jps")
      withModule("intellij.maven.server")
      withModule("intellij.maven.server.m2.impl")
      withModule("intellij.maven.server.m3.common")
      withModule("intellij.maven.server.m30.impl")
      withModule("intellij.maven.server.m3.impl")
      withModule("intellij.maven.server.m36.impl")
      withModule("intellij.maven.errorProne.compiler")
      withModule("intellij.maven.artifactResolver.m2", "artifact-resolver-m2.jar")
      withModule("intellij.maven.artifactResolver.common", "artifact-resolver-m2.jar")
      withModule("intellij.maven.artifactResolver.m3", "artifact-resolver-m3.jar")
      withModule("intellij.maven.artifactResolver.common", "artifact-resolver-m3.jar")
      withModule("intellij.maven.artifactResolver.m31", "artifact-resolver-m31.jar")
      withModule("intellij.maven.artifactResolver.common", "artifact-resolver-m31.jar")
      withArtifact("maven-event-listener", "")
      withResource("maven36-server-impl/lib/maven3", "lib/maven3")
      withResource("maven3-server-common/lib", "lib/maven3-server-lib")
      [
        "archetype-common-2.0-alpha-4-SNAPSHOT.jar",
        "commons-beanutils.jar",
        "maven-dependency-tree-1.2.jar",
        "mercury-artifact-1.0-alpha-6.jar",
        "nexus-indexer-1.2.3.jar"
      ].each {withResource("maven2-server-impl/lib/$it", "lib/maven2-server-lib")}
      doNotCopyModuleLibrariesAutomatically([
        "intellij.maven.server.m2.impl", "intellij.maven.server.m3.common", "intellij.maven.server.m36.impl", "intellij.maven.server.m3.impl", "intellij.maven.server.m30.impl",
        "intellij.maven.server.m2.impl", "intellij.maven.server.m36.impl", "intellij.maven.server.m3.impl", "intellij.maven.server.m30.impl",
        "intellij.maven.artifactResolver.common", "intellij.maven.artifactResolver.m2", "intellij.maven.artifactResolver.m3", "intellij.maven.artifactResolver.m31"
      ])
    },
    plugin("intellij.gradle") {
      withModule("intellij.gradle.common")
      withModule("intellij.gradle.toolingExtension")
      withModule("intellij.gradle.toolingExtension.impl")
      withModule("intellij.gradle.toolingProxy")
      withProjectLibrary("Gradle")
    },
    plugin("intellij.externalSystem.dependencyUpdater"),
    //plugin("intellij.gradle.dependencyUpdater"),
    //plugin("intellij.android.gradle.dsl") {
    //  withModule("intellij.android.gradle.dsl")
    //  withModule("intellij.android.gradle.dsl.kotlin.impl")
    //  withModule("intellij.android.gradle.dsl.impl")
    //},
    plugin("intellij.gradle.java") {
      withModule("intellij.gradle.jps")
    },
    plugin("intellij.gradle.java.maven"),
    plugin("intellij.platform.testGuiFramework") {
      mainJarName = "testGuiFramework.jar"
      withProjectLibrary("fest")
      withProjectLibrary("fest-swing")
    },
    plugin("intellij.junit") {
      mainJarName = "idea-junit.jar"
      withModule("intellij.junit.rt", "junit-rt.jar")
      withModule("intellij.junit.v5.rt", "junit5-rt.jar")
    },
    plugin("intellij.java.byteCodeViewer") {
      mainJarName = "byteCodeViewer.jar"
    },
    plugin("intellij.testng") {
      mainJarName = "testng-plugin.jar"
      withModule("intellij.testng.rt", "testng-rt.jar")
      withProjectLibrary("TestNG")
    },
    plugin("intellij.devkit") {
      withModule("intellij.devkit.jps")
    },
    plugin("intellij.eclipse") {
      withModule("intellij.eclipse.jps", "eclipse-jps.jar", null)
      withModule("intellij.eclipse.common")
    },
    plugin("intellij.java.coverage") {
      withModule("intellij.java.coverage.rt")
    },
    plugin("intellij.java.decompiler") {
      directoryName = "java-decompiler"
      mainJarName = "java-decompiler.jar"
      withModule("intellij.java.decompiler.engine", mainJarName)
      doNotCreateSeparateJarForLocalizableResources()
    },
    //javaFXPlugin("intellij.javaFX.community"),
    plugin("intellij.terminal") {
      withResource("resources/.zshenv", "")
      withResource("resources/jediterm-bash.in", "")
      withResource("resources/fish/config.fish", "fish")
    },
    plugin("intellij.emojipicker") {
      bundlingRestrictions.supportedOs = [OsFamily.LINUX]
    },
    plugin("intellij.textmate") {
      withModule("intellij.textmate.core")
      withResource("lib/bundles", "lib/bundles")
    },
    PythonCommunityPluginModules.pythonCommunityPluginLayout(),
    // required for android plugin
    //plugin("intellij.android.smali") {
    //  withModule("intellij.android.smali")
    //},
    plugin("intellij.completionMlRanking"),
    plugin("intellij.completionMlRankingModels") {
      bundlingRestrictions.includeInEapOnly = true
    },
    plugin("intellij.statsCollector") {
      bundlingRestrictions.includeInEapOnly = true
    },
    plugin("intellij.ml.models.local") {
      bundlingRestrictions.includeInEapOnly = true
    },
    plugin("intellij.jps.cache"),
    plugin("intellij.lombok") {
      withModule("intellij.lombok.generated")
    },
    //plugin("intellij.android.jpsBuildPlugin") {
    //  withModule("intellij.android.jpsBuildPlugin.common")
    //  withModule("intellij.android.jpsBuildPlugin.jps", "jps/android-jps-plugin.jar", null)
    //}
  ]
}
