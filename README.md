# Library to generate GSON Type Adapters

- To Run this library in compile time phase of a project build.
- Specify the classes for which type adapters need to be generated
- The library shall generate the type adapter classes which could be used in the project.

## Code
- src/com/ahanda/gson/GenTACLoader.java
The main file which extends the URLClassLoader, it loads the class files and using reflections finds the fields/methods(getter/setter).
Using them, it generates the TypeAdapter code.

## Further steps
- To develop a build plugin for gradle which will integrate with the build process and invoke the library
