package com.intellij.ide.customize;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomizeChipWizardStepsProvider implements CustomizeIDEWizardStepsProvider {

  @Override
  public void initSteps(CustomizeIDEWizardDialog wizardDialog,
                        @NotNull List<? super AbstractCustomizeWizardStep> steps) {
    CustomizePluginGroups groups = new CustomizePluginGroups();

    steps.add(new CustomizeUIThemeStepPanel());

    if (CustomizeDesktopEntryStep.isAvailable()) {
      steps.add(new CustomizeDesktopEntryStep("/UbuntuDesktopEntry.png"));
    }

    if (CustomizeLauncherScriptStep.isAvailable()) {
      steps.add(new CustomizeLauncherScriptStep());
    }

    steps.add(new CustomizePluginsStepPanel(groups));
    steps.add(new CustomizeFeaturedPluginsStepPanel(groups));

    if (SystemInfo.isMac) {
      steps.add(new CustomizeMacKeyboardLayoutStep());
    }
  }
}
