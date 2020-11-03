# Alignment-Service

## Project Structure
```
├── app
│   ├── controllers
│   ├── models
│   │   ├── daos
│   │   │   └── impl
│   │   ├── entities
│   │   └── services
│   │       └── impl
│   └── views
├── conf
├── public
└── test
    └── controllers
```


The application is built with gradle.
Run `./gradlew tasks` to see a list of tasks that you can execute.

## Build
 - `./gradlew build`

## Test
 - `./gradlew test`

## Lint
 - `./gradlew spotlessApply`
