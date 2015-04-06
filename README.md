Polyserv Gradle
===============

Overview
--------

Polyserv Gradle provides gradle infrastructure like plugins and so on needed by Polyserv.


Polyserv for Developer
------------------

### Getting Started
#### Checkout

```
$> git clone ssh://git@user.githost.com/disioo/org.disioo.polyserv.gradle.git
```
#### Build
> **Important JDK 8**  
> Before you build Polyserv you have to ensure that you use JDK 8 or later and gradle is installed.

Go to the com.adcubum.polyserv.gradle directory. 

For the first time you have to initialize the gradle wrapper to ensure the specified version of gradle.

```
> cd com.adcubum.polyserv.gradle

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
  contains a gradle plugin for generate with Polyserv  
