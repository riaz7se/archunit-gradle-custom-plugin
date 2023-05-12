package com.iz.arunit

import com.iz.arunit.IzArchUnitRuleTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class IzArchUnitPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val archUnitExt = project.extensions
            .create("izArchUnitTest", IzArchUnitExt::class.java, project)
        val applyRulesTask = project.tasks
            .create("applyIzArchRules", IzArchUnitRuleTask::class.java)

        val checkTask = findTaskOrThrowException("check", project)
        val testTask = findTaskOrThrowException("test", project)

        archUnitExt.project = project

        checkTask?.dependsOn(applyRulesTask)
        applyRulesTask.mustRunAfter(testTask)
    }

    private fun findTaskOrThrowException(taskName: String, project: Project): Task? {
        val task = project.tasks.findByName(taskName)
        return task?.let { it } ?:
            throw GradleException("No Task with name: '$task' on which iz-archunit task will depend")
    }
}