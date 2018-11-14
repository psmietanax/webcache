This project is a web caching service implementation.

It uses Spring Boot and the gradle build tool to manage builds and dependencies.
A user is able to store and retrieve arbitrary key-value pairs. The values can either be scalars, nested key-value pairs, or lists.
Every time an entry is created/updated a new unique version id is generated.

To run the service, please follow the following steps:
1) Build a project jar
  Run './gradlew bootJar' from the main project directory.
2) Copy generated jar into script directory:
  Run 'mv build/libs/webcache-0.0.1-SNAPSHOT.jar bin/jar' from the main project directory.
3) Run the main project script:
  Run 'bin/webcache_start.sh <N>' from the main project directory, where N is an optional argument that specifies the cache capacity. If not passed in, the default value set in the application.properties file is used.

The service is running on http://localhost:8080

REST API endpoints:
* http://localhost:8080/cache/entries
  To put a new entry, send a PUT request to the endpoint with a JSON payload that contains both the key and value.

* http://localhost:8080/cache/entries?key=<KEY>
  To get a cached entry, send a GET request with the 'key' request parameter that contains a given key.

* http://localhost:8080/cache/stats
  To get a cache statistics, send a GET request to the endpoint.

* http://localhost:8080/client
  Displays a simple Javascript-based client page.

Run and tested in IntelliJ IDEA Ultimate 2018.2 and Gradle 4.8.1.
