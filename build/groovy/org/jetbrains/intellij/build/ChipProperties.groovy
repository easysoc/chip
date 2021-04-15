package org.jetbrains.intellij.build

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.jetbrains.annotations.NotNull
import org.jetbrains.intellij.build.impl.BuildHelper

import java.nio.file.Path
import java.nio.file.Paths

@CompileStatic
class ChipProperties extends IdeaCommunityProperties {

  // module name in ${home}/plugins dir
  protected static final List<String> BUNDLED_PLUGIN_MODULES = [
    "intellij.java.plugin",
    "intellij.java.ide.customization",
    "intellij.copyright",
    "intellij.properties",
    "intellij.properties.resource.bundle.editor",
    "intellij.terminal",
//    "intellij.emojipicker",
//    "intellij.textmate",
    "intellij.editorconfig",
//     "intellij.settingsRepository",
//     "intellij.configurationScript",
    "intellij.yaml",
    "intellij.tasks.core",
    "intellij.repository.search",
    "intellij.maven.model",
//    "intellij.maven",
//    "intellij.externalSystem.dependencyUpdater",
//    "intellij.gradle",
//    "intellij.gradle.dependencyUpdater",
//    "intellij.android.gradle.dsl",
//    "intellij.gradle.java",
//    "intellij.gradle.java.maven",
    "intellij.vcs.git",
//    "intellij.vcs.svn",
//    "intellij.vcs.hg",
    "intellij.vcs.github",
//    "intellij.groovy",
//    "intellij.junit",
//    "intellij.testng",
//    "intellij.xpath",
//    "intellij.xslt.debugger",
//    "intellij.android.plugin",
//    "intellij.javaFX.community",
//    "intellij.java.i18n",
//    "intellij.ant",
//    "intellij.java.guiForms.designer",
    "intellij.java.byteCodeViewer",
//    "intellij.java.coverage",
    "intellij.java.decompiler",
//    "intellij.devkit",
//    "intellij.eclipse",
    "intellij.platform.langInjection",
//    "intellij.java.debugger.streams",
//    "intellij.android.smali",
//    "intellij.completionMlRanking",
//    "intellij.completionMlRankingModels",
//    "intellij.statsCollector",
//    "intellij.ml.models.local",
    "intellij.sh",
    "intellij.vcs.changeReminder",
    "intellij.filePrediction",
    "intellij.markdown",
    "intellij.webp"
//    "intellij.grazie",
//    "intellij.featuresTrainer",
//    "intellij.lombok"
  ]

  ChipProperties(String home) {
    super(home)
    baseFileName = "chip"
    platformPrefix = "Chip"
    applicationInfoModule = "easysoc.chip"

    // used for plugin development,should not skip step "sources_archive"
    buildSourcesArchive = true
    generateLibrariesLicensesTable = false
    buildCrossPlatformDistribution = false

    // put module into productLayout.mainJarName jar file
    productLayout.productImplementationModules.add("easysoc.chip.ide.customization")
    // remove java ide resources
    productLayout.additionalPlatformJars.remove("resources.jar", "intellij.idea.community.resources")
    productLayout.additionalPlatformJars.put("resources.jar", "easysoc.chip")

    productLayout.bundledPluginModules = ProductModulesLayout.DEFAULT_BUNDLED_PLUGINS + BUNDLED_PLUGIN_MODULES
    // see CommunityRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS
    productLayout.allNonTrivialPlugins = ChipRepositoryModules.COMMUNITY_REPOSITORY_PLUGINS + [
      JavaPluginLayout.javaPlugin()
    ]

    customJvmMemoryOptionsX64 = "-Xms512m -Xmx2048m -XX:ReservedCodeCacheSize=240m"

    // Additional arguments which will be added to JVM command line
    // https://github.com/JetBrains/intellij-community/commit/58c0f96e6851e91da89996b37226777e433fca18#diff-8486904feb6b4247890f4f733a60ff15f7a2fa3d059098f506440c34cdf22e01
//    additionalIdeJvmArguments = "-Didea.show.customize.ide.wizard=true"
  }

  @Override
  @CompileDynamic
  void copyAdditionalFiles(BuildContext buildContext, String targetDirectory) {
    def jetbrainsPluginRepository = "https://plugins.jetbrains.com/maven/com/jetbrains/plugins"
//        def easysocPluginRepository = "https://github.com/easysoc/plugins/releases/download/stable"
//        def easysocToolsRepository = "https://github.com/easysoc/tools/releases/download/stable"

    def dependenciesPath = "$buildContext.paths.communityHome/out/dependencies"
    buildContext.ant.mkdir(dir: dependenciesPath)

//        def toolsPath = "$targetDirectory/tools"
//        buildContext.ant.mkdir(dir: toolsPath)

    // from IdeaCommunityProperties
    buildContext.ant.copy(todir: targetDirectory) {
      fileset(file: "$buildContext.paths.communityHome/LICENSE.txt")
      fileset(file: "$buildContext.paths.communityHome/NOTICE.txt")
    }
    buildContext.ant.copy(todir: "$targetDirectory/bin") {
      fileset(dir: "$buildContext.paths.communityHome/build/conf/ideaCE/common/bin")
    }
    // from BaseIdeaProperties
    buildContext.ant.jar(destfile: "$targetDirectory/lib/jdkAnnotations.jar") {
      fileset(dir: "$buildContext.paths.communityHome/java/jdkAnnotations")
    }
    Path targetDir = Paths.get(targetDirectory).toAbsolutePath().normalize()

    Path java8AnnotationsJar = targetDir.resolve("lib/annotations.jar")
    BuildHelper.moveFile(java8AnnotationsJar, targetDir.resolve("redist/annotations-java8.jar"))
    // for compatibility with users projects which refer to IDEA_HOME/lib/annotations.jar
    BuildHelper.moveFile(targetDir.resolve("lib/annotations-java5.jar"), java8AnnotationsJar)

    // https://plugins.jetbrains.com/plugin/1347-scala/versions
    def ScalaPluginId = "org.intellij.scala"
    def ScalaPluginVersion = "2021.1.16"
    def ScalaPluginFile = "$dependenciesPath/scala-intellij-bin-${ScalaPluginVersion}.zip"
    buildContext.ant.get(src: "$jetbrainsPluginRepository/$ScalaPluginId/$ScalaPluginVersion/$ScalaPluginId-${ScalaPluginVersion}.zip",
                         dest: ScalaPluginFile, skipexisting: "true")
    buildContext.ant.unzip(src: ScalaPluginFile, dest: "$targetDirectory/plugins")

    def marketplacePluginId = "com.intellij.marketplace"
    def marketplacePluginVersion = "211.6693.121"
    def marketplacePluginFile = "$dependenciesPath/marketplace-${marketplacePluginVersion}.zip"
    buildContext.ant.get(src: "$jetbrainsPluginRepository/$marketplacePluginId/$marketplacePluginVersion/$marketplacePluginId-${marketplacePluginVersion}.zip",
                         dest: marketplacePluginFile, skipexisting: "true")
    buildContext.ant.unzip(src: marketplacePluginFile, dest: "$targetDirectory/plugins")

    // https://plugins.jetbrains.com/plugin/14183-easysoc-firrtl/versions
    def FirrtlPluginId = "org.easysoc.firrtl"
    def FirrtlPluginVersion = "1.1.3"
    def FirrtlPluginFile = "$dependenciesPath/easysoc-firrtl-${FirrtlPluginVersion}.zip"
    buildContext.ant.get(src: "$jetbrainsPluginRepository/$FirrtlPluginId/$FirrtlPluginVersion/$FirrtlPluginId-${FirrtlPluginVersion}.zip",
                         dest: FirrtlPluginFile, skipexisting: "true")
    buildContext.ant.unzip(src: FirrtlPluginFile, dest: "$targetDirectory/plugins")

    // https://plugins.jetbrains.com/plugin/14269-easysoc-chisel/versions
    def ChiselPluginId = "org.easysoc.chisel"
    def ChiselPluginVersion = "1.3.1"
    def ChiselPluginFile = "$dependenciesPath/easysoc-chisel-${ChiselPluginVersion}.zip"
    buildContext.ant.get(src: "$jetbrainsPluginRepository/$ChiselPluginId/$ChiselPluginVersion/$ChiselPluginId-${ChiselPluginVersion}.zip",
                         dest: ChiselPluginFile, skipexisting: "true")
    buildContext.ant.unzip(src: ChiselPluginFile, dest: "$targetDirectory/plugins")

//        def ChiselPluginName = "easysoc-chisel"
//        def ChiselPluginVersion = "1.0.0"
//        def ChiselPluginFile = "$dependenciesPath/$ChiselPluginName-${ChiselPluginVersion}.zip"
//        buildContext.ant.get(src: "$easysocPluginRepository/$ChiselPluginName-${ChiselPluginVersion}.zip",
//                dest: ChiselPluginFile, skipexisting: "true")
//        buildContext.ant.unzip(src: ChiselPluginFile, dest: "$targetDirectory/plugins")


//        def launcher = "$dependenciesPath/launcher.zip";
//        buildContext.ant.get(src: "$easysocToolsRepository/launcher.zip",
//                dest: launcher, skipexisting: "true")
//        buildContext.ant.unzip(src: launcher, dest: toolsPath)
  }

  @Override
  WindowsDistributionCustomizer createWindowsCustomizer(String projectHome) {
    return new WindowsDistributionCustomizer() {
      {
        icoPath = "$projectHome/chip/images/win/chip_icon.ico"
        icoPathForEAP = icoPath
        installerImagesPath = "$projectHome/chip/images/win/install"
        fileAssociations = ["scala", "sbt"]
        include32BitLauncher = false
        buildZipArchive = false
      }

      @Override
      String getFullNameIncludingEdition(ApplicationInfoProperties applicationInfo) { "EasySoC CHIP Community Edition" }

      @Override
      String getFullNameIncludingEditionAndVendor(ApplicationInfoProperties applicationInfo) { "EasySoC CHIP Community Edition" }

      @Override
      String getUninstallFeedbackPageUrl(ApplicationInfoProperties applicationInfo) {
        "https://github.com/easysoc/chip/discussions?edition=Chip${applicationInfo.majorVersion}.${applicationInfo.minorVersion}"
      }

      @Override
      String getRootDirectoryName(ApplicationInfoProperties applicationInfo, String buildNumber) {
        "chip"
      }
    }
  }

  @Override
  LinuxDistributionCustomizer createLinuxCustomizer(String projectHome) {
    return new LinuxDistributionCustomizer() {
      {
        iconPngPath = "$projectHome/chip/resources/chip_icon.png"
        iconPngPathForEAP = iconPngPath
        snapName = "easysoc-chip-community"
        snapDescription =
          "The most intelligent Chisel IDE. Every aspect of EasySoC CHIP is specifically designed to maximize developer productivity. " +
          "Together, powerful static code analysis and ergonomic design make development not only productive but also an enjoyable experience."
//        extraExecutables = [
//          "plugins/Kotlin/kotlinc/bin/kotlin",
//          "plugins/Kotlin/kotlinc/bin/kotlinc",
//          "plugins/Kotlin/kotlinc/bin/kotlinc-js",
//          "plugins/Kotlin/kotlinc/bin/kotlinc-jvm",
//          "plugins/Kotlin/kotlinc/bin/kotlin-dce-js"
//        ]
      }

      @Override
      String getRootDirectoryName(ApplicationInfoProperties applicationInfo, String buildNumber) {
        "chip"
      }
    }
  }

  @Override
  MacDistributionCustomizer createMacCustomizer(String projectHome) {
    return new MacDistributionCustomizer() {
      {
        icnsPath = "$projectHome/chip/images/mac/chip_icon.icns"
        urlSchemes = ["chip"]
        associateIpr = true
        fileAssociations = FileAssociation.from("scala", "sbt")
        bundleIdentifier = "org.easysoc.chip"
        dmgImagePath = "$projectHome/chip/images/mac/dmg_background.tiff"
        icnsPathForEAP = icnsPath
      }

      @Override
      String getRootDirectoryName(ApplicationInfoProperties applicationInfo, String buildNumber) {
        applicationInfo.isEAP ? "EasySoC CHIP ${applicationInfo.majorVersion}.${applicationInfo.minorVersionMainPart} CE EAP.app"
                              : "EasySoC CHIP CE.app"
      }
    }
  }

  // override by bin/idea.properties and patchIdeaPropertiesFile in org.jetbrains.intellij.build.impl.BuildTasksImpl
  @Override
  String getSystemSelector(ApplicationInfoProperties applicationInfo, String buildNumber) {
    "Chip${applicationInfo.majorVersion}.${applicationInfo.minorVersionMainPart}"
  }

  @Override
  String getBaseArtifactName(ApplicationInfoProperties applicationInfo, String buildNumber) { "chip-$buildNumber" }

  @Override
  String getOutputDirectoryName(ApplicationInfoProperties applicationInfo) { "chip" }

  @Override
  List<Path> getAdditionalPluginPaths(@NotNull BuildContext context) {
    return Collections.emptyList()
  }
}
