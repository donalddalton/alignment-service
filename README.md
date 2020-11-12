# Alignment-Service

## Project Structure
```
├── app                 → Application source code
│   ├── controllers     → Application controller source code
│   ├── flyway          → Migrations
│   ├── lib             → Application libraries
│   ├── models          → Application business source code
│   └── views           → Application UI templates
├── build.gradle        → Project build script
├── conf                → Application configuration file and routes files
├── docker-compose.yml  → Deploy / local dev
├── Dockerfile          → Application build
├── public              → Public assets
└── test                → Test source code
```

The application is built with gradle. Run `./gradlew tasks` to see a list of tasks that you can execute.
 - Build `./gradlew build` 
 - Test  `./gradlew test`
 - Lint  `./gradlew spotlessApply`

## Local dev / deploy
- `$ docker-compose up -d`

## Ports of interest
| port  | application
| ----- | ---
| 8080  | play application
| 5432  | postgres
| 9090  | pgadmin

## Architecture


## Profiling
```
$ httperf --server localhost \
   --port 9000 \
   --uri /jobs/profile/100 \    # Create a random query with 100 characters
   --rate 50 \                  # Send 50 requests per sec
   --num-conn 10000 \           # Send 10000 requests total
   --num-call 1                 # Call the endpoint once per connection

// 10000 requests
Total: connections 10000 requests 10000 replies 10000 test-duration 201.879 s

// 50 requests/sec (RPS)
Connection rate: 49.5 conn/s (20.2 ms/conn, <=644 concurrent connections)
Connection time [ms]: min 2.3 avg 1287.1 max 15814.2 median 5.5 stddev 2957.7
Request rate: 49.5 req/s (20.2 ms/req)

// No errors
Reply status: 1xx=0 2xx=10000 3xx=0 4xx=0 5xx=0
```
