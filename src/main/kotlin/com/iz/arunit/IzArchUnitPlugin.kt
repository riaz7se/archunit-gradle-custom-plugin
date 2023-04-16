package com.iz.arunit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.Task

class IzArchUnitPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val logger = project.logger
        logger.info("IzArchUnit Started.....")

        val archUnitExt = project.extensions.create("archUnitTest", IzArchUnitExtension::class.java, project)

        val applyRulesTask = project.tasks.create("applyRules", IzArchUnitRuleTask::class.java)
        applyRulesTask.archUnitExtensions.convention(archUnitExt)

        val checkTask = findExistingTaskOrFailOtherwise("check", project)
        val testTask = findExistingTaskOrFailOtherwise("test", project)

        archUnitExt.project = project

        checkTask.dependsOn(applyRulesTask)
        applyRulesTask.mustRunAfter(testTask)

    }

    private fun findExistingTaskOrFailOtherwise(taskName: String, project: Project): Task {

        val taskToFind = project.tasks.findByName(taskName)

        if (taskToFind == null) {
            throw GradleException("can't find the '$taskName' task on which archUnitGradle task will depend - please check Gradle java plugin is applied")
        }

        return taskToFind
    }
}
