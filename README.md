# wcgraphs (Weakly Connected Graphs)

Questo progetto è stato realizzato per il lavoro di tesi.<br/>
Il framework è in grado di confrontare algoritmi per il calcolo del diametro di _grafi diretti_ e _debolmente connessi_.
Gli algoritmi attualmente supportati sono:
* [AkibaCpp](https://github.com/kawatea/graph-diameter)
* AkibaJava (porting in Java di AkibaCpp)
* [SumSweep](https://sites.google.com/a/imtlucca.it/borassi/publications)
* [WebGraph](https://webgraph.di.unimi.it/)
* NewSumSweep (SumSweep implementato facendo uso delle librerie di WebGraph) 

## Come compilare il progetto

Il progetto richiede come dipendenza il jar del progetto SumSweep. È necessario compilare a parte il progetto ed installarlo nella repository locale di Maven (la cartella si chiama .m2) con il seguente comando:
```
mvn install:install-file \
   -Dfile=SumSweep.jar \
   -DgroupId=it.borassi \
   -DartifactId=sumsweep \
   -Dversion=1.0 \
   -Dpackaging=jar
```

Successivamente, è necessario spostarsi nella cartella del progetto che contiene il file pom.xml ed effettuare una compilazione Maven specificando il goal **package** per creare il jar.
```
cd /Users/Simone/workspace-unifi/wcgraphs/
mvn clean package
```
## Dataset

A scopo di esempio, vengono forniti alcuni dataset nella cartella "DATASET/test/".

## Esempi di utilizzo

Per iniziare a sfruttare le potenzialità che wcgraphs mette a disposizione, tra cui quella di eseguire singolarmente gli algoritmi, basta indicare la classe specifica ed il basename del grafo da esaminare.
Ad esempio, se volessi usare l'algoritmo implementato da Borassi et al., il comando da lanciare è:
```
java -cp wcgraphs-1.0-SNAPSHOT.jar it.unifi.simonesantarsiero.wcgraphs.sumsweep.SumSweep DATASETS/p2p-Gnutella04
```
Per confrontare i vari algoritmi, possiamo usare il comando:
```
java -jar wcgraphs-1.0-SNAPSHOT.jar DATASETS/test/p2p-Gnutella04
```

### Note:
* i dataset sono reperibili sul sito di [SNAP](https://snap.stanford.edu/data/);
* i dataset, per essere importati correttamente, devono seguire alcune accortezze:
  * il nodo più piccolo deve partire da 0;
  * i nodi source e target sono separati da uno spazio vuoto oppure da un tab;
  * gli archi non devono essere duplicati;
  * le righe devono essere ordinate in modo crescente;
  * il file deve avere estensione ".arcs".
