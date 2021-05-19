Heroku Guide:
Empfehlungen (hat zumindest bei mir funktionert xD):
    -   JDK 1.8_291
    -   Powershell
    -   SBT auf Rechner installieren

Einrichtung:
1.  Heroku CLI installieren (https://devcenter.heroku.com/articles/heroku-cli#download-and-install)
2.  heroku login    ausführen
3.  heroku git:remote -a sgplayserver   ausführen

Hochladen:
1.  ins Verzeichnis PlayServer wechseln
2.  sbt stage deployHeroku  ausführen (dauernd relativ lange)
3.  (normal auf git pushen, falls noch nicht geschehen)

Lokales Testen des PlayServers:
0. Auf euren eigenen "TestAccount" wechseln
1. In der Android App in der Configuration.java die ServerURL auf eure lokale IP-Adresse einstellen (natürlich vor dem Build)
2. sbt run      ausführen (Powershell)
3. App starten

# Play Hello World Web Tutorial for Java

To follow the steps in this tutorial, you will need the correct version of Java and a build tool. You can build Play projects with any Java build tool. Since sbt takes advantage of Play features such as auto-reload, the tutorial describes how to build the project with sbt. 

Prerequisites include:

* Java Software Developer's Kit (SE) 1.8 or higher
* sbt 0.13.15 or higher (we recommend 1.2.3) Note: if you downloaded this project as a zip file from https://developer.lightbend.com, the file includes an sbt distribution for your convenience.

To check your Java version, enter the following in a command window:

`java -version`

To check your sbt version, enter the following in a command window:

`sbt sbtVersion`

If you do not have the required versions, follow these links to obtain them:

* [Java SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [sbt](http://www.scala-sbt.org/download.html)

## Build and run the project

This example Play project was created from a seed template. It includes all Play components and an Akka HTTP server. The project is also configured with filters for Cross-Site Request Forgery (CSRF) protection and security headers.

To build and run the project:

1. Use a command window to change into the example project directory, for example: `cd play-java-hello-world-web`

2. Build the project. Enter: `sbt run`. The project builds and starts the embedded HTTP server. Since this downloads libraries and dependencies, the amount of time required depends partly on your connection's speed.

3. After the message `Server started, ...` displays, enter the following URL in a browser: <http://localhost:9000>

The Play application responds: `Welcome to the Hello World Tutorial!`
