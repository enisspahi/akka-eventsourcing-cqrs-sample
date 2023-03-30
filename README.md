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
