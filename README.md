# machine_learning
Machine learning experiments

## Local Development

We have a gradle build file that will get the needed resources for
you to be able to run the project. I have added the gradlewrapper to the 
project for you convienence to avoid having to install gradle.

### Compiling Java
`./gradlew compileJava` will compile the code for you

### Running Unit Tests
`./gradlew test` will run the unit tests for you

### Building the Project
`./gradlew build` will build and run unit tests

### Generating javadocs
`./gradlew javadoc` will generate a javadoc api for you to use.
The docs are stored in `build/docs/javadoc/com/index.html`
