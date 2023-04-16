package com.iz.arunit;

import org.gradle.api.Project;

import java.util.List;

class IzArchUnitExtension {

    public boolean enable = true;

    public String classesPath;

    public Project project;

    public List<String> modulesHiearchy;

    public IzArchUnitExtension(Project project) {
        this.project = project;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getClassesPath() {
        return classesPath;
    }

    public void setClassesPath(String classesPath) {
        this.classesPath = classesPath;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<String> getModulesHiearchy() {
        return modulesHiearchy;
    }

    public void setModulesHiearchy(List<String> modulesHiearchy) {
        this.modulesHiearchy = modulesHiearchy;
    }
}
