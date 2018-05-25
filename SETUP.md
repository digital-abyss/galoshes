# Appendix of Detailed Setup Instructions



## VSCode Setup for Java

I installed the following plugins to get debugging and auto-complete to work in vscode:
* [Java debugger](https://github.com/Microsoft/vscode-java-debug)
* [Java Extension Pack](https://github.com/Microsoft/vscode-java-pack)
* [Java Test Runner](https://github.com/Microsoft/vscode-java-test)
* [Language Support for Java](https://github.com/redhat-developer/vscode-java)
* [Maven for Java](https://github.com/Microsoft/vscode-maven)
* [Spring Boot Extension Pack](https://github.com/spring-projects/sts4)
* [Spring Boot Tools](https://github.com/spring-projects/sts4)
* [Spring Initializr](https://github.com/Microsoft/vscode-spring-initializr)


## Database setup

### Local database setup

1. Run docker compose in the local folder:

```bash
docker-compose up
```

A closer look at the docker file will show you the database configuration options.  Data will be persisted by default to `$PWD/data`  You can change this by setting `GALOSHES_DATA_ROOT_DIR` variable.

2. You can now connect to your local database.  If you haven't modified the docker-compose file, the default mySQL port of 3306 is mapped to 33060.  So connect to 33060.

3. You may need to grant the admin user privileges to access the database:

```sql
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'%'
```

### PCF database setup and conn

1. Login to PCF (if your system is behind a proxy you may need to skip ssl validation :S ):

```bash
cf login -a <your-enterprise-pcf-endpoint> --skip-ssl-validation
```

(You can find the login url from Apps Manager on PCF's portal -- for more information consult [PCF Documentation](https://docs.pivotal.io/pivotalcf/2-0/opsguide/api-endpoint.html#console))

2. Create a database:

```bash
cf create-service p.mysql db-small ians-test-db
```

3. Wait for its status to be provisioned:

```bash
$ cf service ian-test-db-v2
Showing info of service ian-test-db-v2 in org UE90 / space dev as 521210013...

name:            ian-test-db-v2
service:         p.mysql
tags:
plan:            db-small
description:     Dedicated instances of MySQL
documentation:
dashboard:

There are no bound apps for this service.

Showing status of last operation from service ian-test-db-v2...

status:    create succeeded
message:   Instance provisioning completed
started:   2018-05-03T16:27:09Z
updated:   2018-05-03T16:35:12Z
```

4. Create a service key (used to get read-only access to the database)

```bash
$ cf create-service-key ian-test-db-v2 my-test-key -c '{ "read-only": true }'
Creating service key my-test-key for service instance ian-test-db-v2 as 521210013...
OK
```

5. Show your service key to find credentials:

```bash
$ cf service-key ian-test-db-v2 my-test-key
Getting key my-test-key for service instance ian-test-db-v2 as 521210013...

{
 "hostname": "192.168.28.32",
 "jdbcUrl": "jdbc:mysql://192.168.28.32:3306/service_instance_db?user=myusername\u0026password=mypassword",
 "name": "service_instance_db",
 "password": "mypassword",
 "port": 3306,
 "uri": "mysql://myusername:mypassword@192.168.28.32:3306/service_instance_db?reconnect=true",
 "username": "myusername"
}
```

6. Push any application to cf (you need to setup an ssh tunnel through the app)

```bash
cf push my-test-app
...
```

7. Enable ssh for your application:

```bash
$ cf enable-ssh ian-test-app-v1
Enabling ssh support for 'ian-test-app-v1'...

OK
```

8. Create an ssh tunnel through your application to your database (the ip comes from the service key creds):

```bash
cf ssh -L 63306:192.168.28.32:3306 ian-test-app-v1
```

9. Connect to your database through your tunnel:
* Note you connect through `localhost:63306` as per your ssh tunnel

## Appendix: Setting up a spring boot application from scratch

* Visit [Spring Initializr](https://start.spring.io/) and click 'Switch to Full Version'
  * Select `Web`, `MySQL`, `DevTools`, `Flyway`, `JPA`, `Cloud-Connectors`, `REST-Docs`
  * CLick Generate project
* Extract the downloaded zip file
  * Issues: `mvwn` doesn't work behind corporate proxy, so give up on trying to make this work.

* Configure your database by adding the following to your `src/resources/application.properties` file:

```ini
spring.jpa.hibernate.ddl-auto=create
spring.datasource.url=jdbc:mysql://localhost:33060/testdb
spring.datasource.username=admin
spring.datasource.password=passw0rd!
```

* Configure your database migrations:
  * We are using [Flyway](https://flywaydb.org/) for schema versioning.
  * To do so, you'll need to add the following to your `src/resources/application.properties` file:

```ini
spring.flyway.locations=classpath:db/mysql/migration
```

  * As well, you'll need an initial migration in `src/resources/db/mysql/migration/V1.0__Widgets.sql` (file naming convention is important for flyway)

```sql
CREATE TABLE IF NOT EXISTS testdb.widgets (
  ID INT NOT NULL AUTO_INCREMENT,
  FIRST_NAME VARCHAR(50) NOT NULL,
  LAST_NAME VARCHAR(50) NOT NULL,
  ADD_TS DATETIME(6) NOT NULL,
  CARDSTATUS VARCHAR(50) NOT NULL,
  PRIMARY KEY (ID))
ENGINE = InnoDB;
```

* Now your application should start!  Huzzah!