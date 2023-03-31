# Akka Event Sourcing and CQRS sample

## Postgres setup
* Login via `psql` CLI
`psql -h localhost -d postgres -U username -W`
* Run `ddl-scripts/postgres-create-schema.sql`
* Run `ddl-scripts/postgres-create-projection-schema.sql`

## Local run
`gradlew applicatin:run`

## Cluster Management
http://localhost:8558/cluster/members/


## Readings
* [Event Sourcing](https://learn.microsoft.com/en-us/previous-versions/msp-n-p/jj591559%28v=pandp.10%29)
* [Shopping Cart Example](https://developer.lightbend.com/docs/akka-guide/microservices-tutorial/overview.html)