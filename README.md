Some change
# Spring Boot + Flyway + JOOQ prototype

Goals:

* Demonstrate a working prototype for managing schema with flyway
* Demonstrate a working protytpe using JOOQ as the interaction library with the database.
* database setup using docker for local development and MySQL tile for PCF

## Prerequisites

### Tooling

* [Docker](https://www.docker.com/)
* Your favourite java IDE (e.g. [STS](https://spring.io/tools/sts) or [VSCode](https://code.visualstudio.com/) )
* [Java](http://www.oracle.com/technetwork/java/javase/downloads/jre10-downloads-4417026.html)
* [Maven](https://maven.apache.org/) -- note you'll have to edit your ~/.m2/settings.xml file to use a proxy.
* [mysql workbench](https://dev.mysql.com/downloads/workbench/) (at the time of writing GA release had issues opening connections to mysql running on a container)

### Database Setup
Please see [Database Setup](./SETUP.md) for detailed instructions on setting up a PCF database or if you're new to docker or docker-compose.

To use a local mysql instance, in project root folder run:

```bash
docker-compose up
```

## Running the sample application

To build:

```bash
mvn clean install
mvn package
```

To run:

```bash
java -jar /path/to/generated/jar/file/from/package/command
```

To see end to end database interaction:

```bash
curl http://localhost:9089/
```

The next steps go into details about what's going on.

## Database Evolutions

A good starting point for reading is here: https://martinfowler.com/articles/evodb.html

Essentially there a few problems that crop up once you start persisting data in a SQL database in a world of continuous delivery.
Namely:

* Application developers may need to change the schema to develop a new feature.
  * Developers working on disperate features may make breaking changes during development process
* Once a feature is ready, the new database schema must be propagated.  This become complex when you have production data.
  * What happens when you need to roll back your release because of a critical bug?
  * What happens during deployment when you have two versions of your application running at the same instance, both with different schemas? (e.g. durning deployments)

Some high level principles were generated to try and address these problems:
* Developers have their own database
* Database changes are part of source control
* All database changes are captured in a migration script
* Use continuous integration for schema changes
* New schema must be backward compatible with previous application version
<TODO - there may be more?>


### Flyway

[Flyway](https://flywaydb.org/) is a tool for managing schema changes.  [This page](https://flywaydb.org/getstarted/why) is a good read for understanding motivation.

#### How it works in this project

All database schemas are represented as a series of migrations.  Migrations are stored in `src/resources/db/mysql/migration`.

When you want to add a new version, you simply add a new sql file to transform your schema.  The main pom file has been updated with build commands to run this as part of
the build process.

`mvn clean install` will connect to the database as defined in your configuration, check the `flyway_schema_history` table that flyway has created, and then apply
any outstanding schema changes.

Now you are running on a new schema!

#### Caveat Emptor

You, the developer, are still responsible for ensuring your schema changes aren't breaking the database.  This sounds daunting, but can be mitigated by adopting some best practices:

<suggested best practices coming soon!>

### JOOQ

[Spring Data JPA](https://projects.spring.io/spring-data-jpa/) is a wrapper around a JPA implementation (generally [Hibernate](http://hibernate.org/))

[This stack overflow post](https://stackoverflow.com/questions/36920843/is-spring-data-jpa-a-jpa-implementation?rq=1) describes the relationship in better detail.

An alternative approach to using Spring Data is one adopted by the library like [jooq](https://www.jooq.org/) - a nice lightweight wrapper around the database.  With jooq, we can auto-generate some java code from our database schema and use it effectively in our java projects.

With flyway and jooq, the pattern trying to be established is listed out [here](https://blog.jooq.org/2014/06/25/flyway-and-jooq-for-unbeatable-sql-development-productivity/).

Here is a copy of the relevant flow details:

1. Database increment – You need a new column in your database, so you write the necessary DDL in a Flyway script
2. Database migration – This Flyway script is now part of your deliverable, which you can share with all developers who can migrate their databases with it, the next time they check out your change
3. Code re-generation – Once the database is migrated, you regenerate all jOOQ artefacts (see code generation), locally
4. Development – You continue developing your business logic, writing code against the udpated, generated database schema

#### How this works in this project

Currently there is a build plugin to maven that will run flyway, then jooq.  Flyway will make any modifications to the schema, and then jooq will generate some java code
that makes referencing these entities easy.

`mvn clean install` does both.

### Managing upgrades to development and production

There are a few approaches to applying schema changes:

1) As part of your CI/CD deployment
2) Application boot time
3) In advance (e.g. by exposing a rest endpoint in your app that applies a schema change)

The above approach favours applying changes as part of the pipeline.


### General Problems with Schema management (regardless of approach)

* If you are not running your CI/CD pipeline all the way to production for every change and batch changes up in a development / UAT test environment, you may end up batching a series of schema changes together.
  * Applying them all at once is not what happened in your development environment, and could result in:
    * Schema changes not applying in the same order (unexpected behaviour or errors)
    * Backward incompatibility when all changes are applied at once
* Large table mutation may require significant time to apply, and/or may use unexpected amounts of db resources, and/or may cause deadlock with running application code
* Individual Schema changes need to be written in a transactional fashion (in case a change fails part way through)
* Production data may not be 100% mirrored in dev environments, and resulting schema changes may not function in prod (e.g. if you concatonate two fields and store it in a new column that's not big enough for the result, but your dev data didn't have this case)


### Outstanding Investigations

1. Transactions at scale
2. Transaction error handling and rollbacks
3. Some tests :)
4. Improved REST.


