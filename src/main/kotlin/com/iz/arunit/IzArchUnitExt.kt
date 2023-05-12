package com.iz.arunit

import org.gradle.api.Project

open class IzArchUnitExt(var project: Project) {
    var isEnable = true
    var classPath: String? = null
    var modulesHiearchyList: Set<String>? = mutableSetOf()
    var excludeModulesList: Set<String>? = mutableSetOf()
}