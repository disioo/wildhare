Wildhare Gradle Extension and Plugin
====================================

Overview
--------

Wild Hare Gradle provides gradle infrastructure like plugins and so on.


Wildhar for Developer
---------------------

### Getting Started
> **Important JDK 8**  
> Before you build Polyserv you have to ensure that you use JDK 8 or later and gradle is installed.

Go to the wildhare directory. 

For the first time you have to initialize the gradle wrapper to ensure the specified version of gradle.

```
> cd wildhare

#### First install the specified wrapper
> gradle wrapper

#### to build the project
> ./gradlew build

#### to install the project to your local repository
> ./gradlew install

```

### Project Layout

The project consists of follwing subproject:

* genplugin  
  contains a gradle plugin used by generators  
