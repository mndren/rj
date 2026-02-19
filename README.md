# Info

uso java 21 installato da <b>sdkman</b>

### sdk use java 21.0.2-open

compilo direttamente un jar

### mvn clean package && java -jar target/rj-1.0-SNAPSHOT.jar

# info docker postgres

comando per lanciare pg su docker

### docker run --name postgres-container -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=db -p 5432:5432 -d postgres

comando per entrare nel container

### docker exec -it postgres-container psql -U postgres -W postgres