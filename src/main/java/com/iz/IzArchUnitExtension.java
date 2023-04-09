package com.iz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gradle.api.Project;

@Getter
@Setter
@NoArgsConstructor
public class IzArchUnitExtension {

    private boolean enable = true;

    private String classesPath;

    private Project project;

    public IzArchUnitExtension(Project project) {
        this.project = project;
    }
}
