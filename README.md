# trial - user management 

## install

`mvn clean install`

## Run

`docker-compose up`

`java -jar trial-api/target/trial-api-1.0-SNAPSHOT.jar`

`http://locahost:8080/swagger-ui.html`


## how to authenticate and get a token:

````
✗ curl -i -X POST "localhost:8080/login?username=admin&password=password"

HTTP/1.1 200
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Transfer-Encoding: chunked
Date: Thu, 30 Jul 2020 11:21:05 GMT

{"token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJtZSIsImF1ZCI6InRyaWFsLWFwaSIsInN1YiI6ImFkbWluIiwiZXhwIjoxNTk2MTE0MDY1fQ.NReGfzpoDNTrua0gDF0r7kmijlLzozAw61rpkC3NhgscRlhZvTXl6nyjLUQpBMiiVYGPb75Ek77gpuVTbilDdA"}```