package com.iz;

import com.tngtech.archunit.ArchConfiguration;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

@Slf4j
public abstract class IzArchUnitRuleTask extends DefaultTask {

    @Input
    public abstract Property<IzArchUnitExtension> getArchUnitExtensions();

    @TaskAction
    public void applyRules() throws IOException {
        IzArchUnitExtension izArchUnitExtension = getArchUnitExtensions().get();

        if (!izArchUnitExtension.isEnable()) {
            log.warn("ArchUnit is Disabled");
            return;
        }

        String analyzePath = izArchUnitExtension.getClassesPath();
        log.info("Analyze Path: {}", analyzePath);

        Properties properties = new Properties();

        // Set the properties in the ArchUnit configuration
        ArchConfiguration archConfiguration = ArchConfiguration.get();
        archConfiguration.setExtensionProperties("archunit", properties);

        JavaClasses classes = new ClassFileImporter()
                .importPath(Paths.get(analyzePath));

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

    }
}