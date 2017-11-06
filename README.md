<h1>Semantic Chess</h1>

<p>A question answering system for chess games. The engine will try to answer all chess questions, if the information can be found in a PGN file. Therefore, the questions must include players, events, locations, (annual) dates, ECO, openings, ELO ratings, results and/or moves. In the <a href="https://github.com/semanticchess/semanticchess/blob/master/benchmark/auswertung-20171105.pdf">benchmark</a> you  find some sample questions.</p>
<div>
  <p align="center">
  <img src="https://github.com/semanticchess/semanticchess/blob/master/img/example.gif" alt="Example gif" width="450px" >
  </p>
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
<p>The next commands must be executed in the folder with the pom.xml. If you already loaded data to the database just start the engine with the 4th command. At the first launch perform the following steps. </p>

<h4>1. Convert the PGN file to RDF data (in this project you can find 1610-1899.pgn as an example):</h4>

```sh
$ mvn spring-boot:run -Drun.arguments="pgn"
```
<h4>2. Map the chess openings to the games (optional! the mapping files already exist for 1610-1899.pgn):</h4>

```sh
$ mvn spring-boot:run -Drun.arguments="eco"
```
<h4>3. Load the RDF files to the Virtuoso database:</h4>
  
```sh
$ mvn spring-boot:run -Drun.arguments="load"
```

<h4>4. Start the engine:</h4>
  
```sh
$ mvn spring-boot:run
```

<h2>Note</h2>

<p>The conversion from PGN to RDF and the mapping of the chess openings to the chess games take some time.</p>


<h2>Use</h2>

- http://localhost/8080 - Homepage
- http://localhost/8890 - Databse (Virtuoso)
- http://localhost/8890/sparql (SPARQL editor)


<h2>Prefixes</h2>
If you want to use the SPARQL editor on http://localhost/8080, note that the following prefixes are already in use:

```sh
PREFIX ex:    <http://example.com> 
PREFIX res:   <http://example.com/res/> 
PREFIX prop:  <http://example.com/prop/> 
PREFIX cres:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology/Resources/> 
PREFIX cont:  <http://pcai042.informatik.uni-leipzig.de/~swp13-sc/ChessOntology#> 
PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>
```

<h2>PGN Viewer</h2>

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

<h2>Credits</h2>

<h3>Frontend:</h3>

- PgnViewerJS by [mliebelt](https://github.com/mliebelt/PgnViewerJS)
- Font Awesome by Dave Gandy - http://fontawesome.io
- [Bootstrap](http://getbootstrap.com/)
- [AngularJS](https://angularjs.org/) 
- [jQuery](https://jquery.com/)

<h3>Backend:</h3>

- [Stanford coreNLP](https://stanfordnlp.github.io/CoreNLP/)
- [spring.io](https://spring.io/)
- [Chess Opening JSON by hayatbiralem](https://github.com/hayatbiralem/eco.json/blob/master/eco.json)
- PGN to RDF and chess RDF ontology by [TortugaAttack](https://github.com/TortugaAttack/CACADUS) and [swp-sc13](http://pcai042.informatik.uni-leipzig.de/swp/SWP-13/swp13-sc/) 
