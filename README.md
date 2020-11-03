# Alignment-Service

## Project Structure
```
├── app                 → Application source code.
│   ├── controllers     → Application controller source code.
│   ├── models          → Application business source code.
│   └── views           → Application UI templates.
├── build.gradle        → Project build script.
├── conf                → Application configuration file and routes files.
├── public              → Public assets.
└── test                → Test source code.
```

The application is built with gradle.
Run `./gradlew tasks` to see a list of tasks that you can execute.

## Build
 - `./gradlew build`

## Test
 - `./gradlew test`

## Lint
 - `./gradlew spotlessApply`
