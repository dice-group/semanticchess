<h1>Semantic Chess</h1>

<p>A question answering system for chess games.</p>
<div>
  <img src="https://github.com/semanticchess/semanticchess/blob/master/img/example.gif" alt="alt text" width="450px">
</div>

<h2>Prerequisite</h2>
<p>You need:</p>
<ul>
  <li>Java 8</li>
  <li>Docker (for the database)</li>
  <li>Maven</li>
</ul>

<h2>Installing</h2>
<ul>
  <li>download the project</li>
  <li>run Docker</li>
  <li>start the database</li>
</ul>

```sh
$ cd src/main/database
$ docker-compose up
```
<ul>
  <li>start the engine</li>
</ul>

```sh
$ mvn spring-boot:run
```

<h2>Note</h2>

At first the program converts the file 1610-1899.pgn to .ttl-files. After that it uploads these files plus a mapping file and a chess opening file to the database. That will take some time.


## Use

> http://localhost/8080 - Homepage
> http://localhost/8890 - Databse (Virtusos)
> http://localhost/8890/sparql (SPARQL editor)


## Prefixes
If you want to use the sparql editor on http://localhost/8080: The following prefixes are already in use:

```sh
PREFIX ex:    <http://example.com> 
PREFIX res:   <http://example.com/res/> 
PREFIX prop:  <http://example.com/prop/> 
PREFIX cres:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology/Resources/> 
PREFIX cont:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> 
PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>
```

## PGN Viewer
PgnViewerJS by [mliebelt](https://github.com/mliebelt/PgnViewerJS)

The PGN viewer is embedded by links from cdn.rawgit.com:
- https://raw.githubusercontent.com/mliebelt/PgnViewerJS/gh-pages/dist/css/pgnvjs.css
- https://cdn.rawgit.com/mliebelt/PgnViewerJS/gh-pages/dist/js/pgnvjs.js

If you want to include the files directly into the Semantic Chess project, go to [http://mliebelt.github.io/PgnViewerJS/docu/index.html](mliebelt.github.io)
and download the current version. After that insert the unzipped directory to 
```
/semanticchess/src/main/webapp/static
```
and uncomment line 13 and 35 in
```
/semanticchess/src/main/webapp/public/index.html
```

## Credits
### Frontend:
- PgnViewerJS by [mliebelt](https://github.com/mliebelt/PgnViewerJS)
- Font Awesome by Dave Gandy - http://fontawesome.io
- [Bootstrap](http://getbootstrap.com/)
- [AngularJS](https://angularjs.org/) 
- [jQuery](https://jquery.com/)

### Backend:
- [Stanford coreNLP](https://stanfordnlp.github.io/CoreNLP/)
- [spring.io](https://spring.io/)
- [Chess Opening JSON by hayatbiralem](https://github.com/hayatbiralem/eco.json/blob/master/eco.json)
- PGN to RDF and chess RDF ontology by [TortugaAttack](https://github.com/TortugaAttack/CACADUS) and [swp-sc13](http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/) 
