package com.iz;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class IzArchUnitPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        IzArchUnitExtension archUnitExt = project.getExtensions()
                .create("archUnitTest", IzArchUnitExtension.class, project);

        IzArchUnitRuleTask applyRulesTask = project.getTasks().create("applyRules", IzArchUnitRuleTask.class);
        applyRulesTask.getArchUnitExtensions().convention(archUnitExt);

        final Task checkTask = findExistingTaskOrFailOtherwise("check", project);
        final Task testTask = findExistingTaskOrFailOtherwise("test", project);

        archUnitExt.setProject(project);

        checkTask.dependsOn(applyRulesTask);
        applyRulesTask.mustRunAfter(testTask);

    }

    private Task findExistingTaskOrFailOtherwise(String taskName, Project project){

        final Task taskToFind = project.getTasks().findByName(taskName);

        if (taskToFind==null){
            throw new GradleException("can't find the '"+taskName+"' task on which archUnitGradle task will depend - please check Gradle java plugin is applied");
        }

        return taskToFind;
    }
}
