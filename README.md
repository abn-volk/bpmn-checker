# bpmn-checker
A BPMN validator for USE (https://sourceforge.net/projects/useocl/)

This is a USE plugin to validate BPMN files created by BPMN designers (such as Activiti or Camunda). It utilises Camunda's bpmn and xml parser libraries (available in `libs`).

## Setting up
* Before you begin, download USE and import it as a project into Eclipse (choose *File - New - Project... - Java Project from Existing Ant Bundle*.
* First, clone the project and import it into Eclipse.
* Include USE in *Project - Properties - Java Build Path - Projects* and the libraries in *Project - Properties - Java Build Path - Libraries*

## Building the project
* To build the project, choose *File - Export - JAR file* and make sure to include every project files except Eclipse's metadata files as well as the `lib` and `demo` folders.
* In *JAR Manifest Specification*, choose *Use existing manifest from workspace* and browse to `META-INF/MANIFEST.MF`
* Finally, click Finish.

## How to use 
* Copy the JAR file to USE's `lib/plugins` directory and the two libraries from `lib` to USE's `lib` directory.
* For more information, see the `demo` folder.
