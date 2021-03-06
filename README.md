# Spring Batch demo

## Scenarios

### Testing restartability

Precondition:

1. Use a database (embeddded on file is fine). Spring batch auto creates its
database on the first startup

Running HSQLDB in server mode

```
java -cp ~/.m2/repository/org/hsqldb/hsqldb/2.5.0/hsqldb-2.5.0.jar org.hsqldb.server.Server --database.0 file:~/db/mydb --dbname
```

Running Client GUI

```
java -cp ~/.m2/repository/org/hsqldb/hsqldb/2.5.0/hsqldb-2.5.0.jar org.hsqldb.util.DatabaseManagerSwing
```


```
#A default dabase on file is defined

spring.datasource.url=jdbc:hsqldb:file:~/filedb;shutdown=true
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.username=sa
spring.datasource.password=sa
```


2. Insert a dirty record in the existing csv file (*sample-data.csv*) to prove restartiblity is
performed 

```
//E.g.

Jill,Doe
Joe,Doe
JustinDoe <---- file without comma will throw an exception
Jane,Doe
John,Doe

```


*NOTE: to drop database delete file or drop spring batch tables to let recreate it*

