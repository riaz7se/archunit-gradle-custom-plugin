package com.iz.arunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@Slf4j
public abstract class IzArchUnitRuleTask extends DefaultTask {

    @Input
    public abstract Property<IzArchUnitExt> getArchUnitExtensions();

    @TaskAction
    public void applyRules() throws IOException {
        IzArchUnitExt izArchUnitExt = getArchUnitExtensions().get();

        if (!izArchUnitExt.isEnable()) {
            log.warn("ArchUnit is Disabled");
            return;
        }

        Set<String> modulesHiearchySet = izArchUnitExt.getModulesHiearchySet();
        Set<String> excludeModulesSet = izArchUnitExt.getExcludeModulesSet();

        Project project = izArchUnitExt.getProject();

        String mainProjectName = project.getName ();
        String mainProjectGroupId = String. valueOf (project. getGroup());
        log.debug("Project Name: ()", mainProjectName);
        Set<Project> allProjects = project.getSubprojects();
        log.debug("Project Dependencies: ()", project.getDependencies());


        Map<String, IzProject> subProjectaths = allProjects.stream()
                .filter(subPrj -> !excludeModulesSet.contains(":" + subPrj.getName()))
                .map (prj -> createProjectPathMap (prj))
        .flatMap (prjMap -> prjMap.entrySet().stream()).collect(Collectors. toMap(
                Map. Entry:: getKey, Map.Entry::getValue, (v1, v2) -> v2));

        String[] subProjectPath = subProjectaths.values().stream()
                .map(IzProject::getClasspath)
                .toArray(String[]::new);


        String analyzePath = izArchUnitExt.getClassPath();
        log.info("Analyze Path: {}", analyzePath);


        JavaClasses classes = new ClassFileImporter()
                .importPaths(subProjectPath);

        noClasses().should(ACCESS_STANDARD_STREAMS).check(classes);

        NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(classes);

        NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(classes);

        NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES.check(classes);

        noClasses()
                .should()
                .accessClassesThat()
                .resideInAPackage("..controller..");

        noClasses()
                .that()
                .resideInAPackage("..service..")
                .should().accessClassesThat().resideInAPackage("..controller..")
                .check(classes);

        noClasses()
                .that().resideInAPackage("..dao..")
                .should().accessClassesThat().resideInAPackage("..service..");

        //customized test as per project modules

        List<String> modulesHiearchyList = new ArrayList<>(modulesHiearchySet);

        IntStream.range(0, modulesHiearchyList.size() - 1)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(modulesHiearchyList.get(i),modulesHiearchyList.get(i+1)))
                .filter(entry -> entry.getKey() .equals(entry.getValue()))
                .forEach (module -> checkHiearchyPackageDependency(module, subProjectaths, mainProjectGroupId, classes));
    }

    private Map<String, IzProject> createProjectPathMap(Project prj) {
        String asPath = prj.getExtensions().getByType(SourceSetContainer.class)
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME).getOutput()
                .getClassesDirs().getAsPath();
        log.debug("group... ()", prj.getGroup());
        Map<String, String> stringStringHashMap = new HashMap<>();
        Map<String, IzProject> subProjectMap = new HashMap<>();
        subProjectMap.put(": " + prj.getName(),
                new IzProject(prj.getName(), asPath, String.valueOf(prj.getGroup())));
        stringStringHashMap.put(prj.getName(), asPath);
        return subProjectMap;
    }

    private void checkHiearchyPackageDependency(AbstractMap.SimpleEntry entryModule,
                                                Map<String, IzProject> subProjectPaths, String mainProjectGroupId, JavaClasses classes) {
        IzProject currSubProject = subProjectPaths.get(":" + entryModule.getKey());
        IzProject nextSubProject = subProjectPaths.get(":" + entryModule.getValue());
        if (currSubProject != null && nextSubProject != null) {
            String resideIn = ".." + currSubProject.getGroupId() + ".(**)";
            String notReside = ".." + nextSubProject.getGroupId() + ".(**)";
            log.error("Reside in: ()", resideIn);
            log.debug("Not Reside in: ()", notReside);

            noClasses().that()
                    .resideInAnyPackage(resideIn)
                    .should().dependOnClassesThat().resideInAnyPackage(notReside)
                    .because("Packages in: " + entryModule.getKey() + " not allowed to depend on packages in: " + entryModule.getValue())
                    .check(classes);
        }
    }
}