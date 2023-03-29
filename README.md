Execute the following commands
```shell
mvn clean package
```
```shell
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar init
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar add
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar commit
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar branch second
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar checkout second
```
Modify some files
```shell
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar add
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar commit
java -jar ./target/git-implementation-1.0-SNAPSHOT.jar checkout master
```
See changes