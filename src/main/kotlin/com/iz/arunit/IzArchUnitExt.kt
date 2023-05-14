package com.iz.arunit

import org.gradle.api.Project

open class IzArchUnitExt(var project: Project) {
    var isEnable = true
    var classPath: String? = null
    var modulesHiearchySet: Set<String>? = mutableSetOf()
    var excludeModulesSet: Set<String>? = mutableSetOf()
}