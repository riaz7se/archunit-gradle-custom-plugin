# archunit-gradle-custom-plugin
ArchUnit Test in Gradle Custom Plugin
<br/>
This repository contains gradle custom plugin that has ArchUnit test.

This plugin can be added to any java-gradle project to test standard archunit test.

To add this plugin to your project, follow below steps

```gradle
//add this this to project build.gradle

buildscript {
	dependencies {
		classpath "com.iz.aunit:iz-archunit:1.0.0-SNAPSHOT"
	}
	repositories {
		mavenLocal()
		mavenCentral()
	}
}
```

```gradle
apply plugin: 'com.iz.aunit'
```

```gradle
archUnitTest {
	enable = true // flag to disable archunit test
	classesPath = sourceSets.main.output[0] //project classes path to test
}

####

configurations {
    // configuration that holds jars to include in the jar
    extraLibs
}

dependencies {
    compile 'org.codehaus.groovy:groovy-all:2.4.7'
    extraLibs group: 'net.java.dev.jna', name: 'jna-platform', version: '4.2.2'
    testCompile group: 'junit', name: 'junit', version: '+'
    configurations.compile.extendsFrom(configurations.extraLibs)
}

jar {
    from {
            configurations.extraLibs.collect { it.isDirectory() ? it : zipTree(it) }
        }
}



```
