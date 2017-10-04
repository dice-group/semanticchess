# Semantic Chess

A questions answering system for chess games.


## Prerequisite

You need:

- Docker (for the database)
- Maven


## Installing

1. Download the project

2. run Docker

3. start the database

```sh
$ cd src/main/database
$ docker-compose up
```

4. start the engine

```sh
$ mvn spring-boot:run
```

## Note

At first the program converts the file 1610-1899.pgn to ttl-files. After that it uploads thes files plus a mapping file and an chess opening file to the database. That will take some time.


## Use

> You can now visit http://localhost/8080 and ask some queries.
> The database runs on http://localhost/8890.
> The sparql query editor runs on http://localhost/8890.


## Prefixes
If you want to use the sparql editor on http://localhost/8080: There are already in use.

```sh
PREFIX ex:    <http://example.com> 
PREFIX res:   <http://example.com/res/> 
PREFIX prop:  <http://example.com/prop/> 
PREFIX cres:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology/Resources/> 
PREFIX cont:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> 
PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>
```

