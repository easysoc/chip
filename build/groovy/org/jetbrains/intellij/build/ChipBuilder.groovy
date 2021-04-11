package org.jetbrains.intellij.build

import groovy.transform.CompileStatic

import java.nio.file.Paths

@CompileStatic
class ChipBuilder {
  private final BuildContext buildContext

  ChipBuilder(String home, ProprietaryBuildTools buildTools = ProprietaryBuildTools.DUMMY, BuildOptions options = new BuildOptions(), String projectHome = home) {
    buildContext = BuildContext.createContext(home, projectHome, new ChipProperties(home), buildTools, options)
  }

  ChipBuilder(BuildContext buildContext) {
    this.buildContext = buildContext
  }

  void buildFullUpdater() {
    def tasks = BuildTasks.create(buildContext)
    tasks.compileModules(["updater"])
    tasks.buildFullUpdaterJar()
  }

  void buildDistributions() {
    // org.jetbrains.intellij.build.impl.BuildTasksImpl
    def tasks = BuildTasks.create(buildContext)
    tasks.buildDistributions()
//    tasks.buildUpdaterJar()

    // if buildSourcesArchive=true buildAdditionalArtifacts method will build zipSourcesOfModules
//    if (!buildContext.options.buildStepsToSkip.contains(BuildOptions.SOURCES_ARCHIVE_STEP)) {
//      tasks.zipProjectSources()
//    }
  }

  void buildUnpackedDistribution(String targetDirectory) {
    BuildTasks.create(buildContext).buildUnpackedDistribution(Paths.get(targetDirectory), false)
  }
}