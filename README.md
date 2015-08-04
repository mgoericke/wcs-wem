# README #

## WEM Application Prototype / Template ##

Die Applikation basiert auf Spring Boot v1.1.6 (annotation based) und enthŠlt einen eingebetteten Tomcat Container (localhost:8081). Per default ist die Applikation fŸr ein Jump Start Kit, 
das auf Port 9080 lauscht, konfiguriert. Ein Deployment in einen AppServer ist nicht nštig!

### Features ###

* Spring Boot Applikation
	* Installation / Start ohne AppServer
	* Annotation based Spring Configuration
	* Thymeleaf Template Engine [http://www.thymeleaf.org](http://www.thymeleaf.org)
	* so wenig JavaScript wie mšglich!
	* Kann als Basis fŸr weitere WEM Applikationen dienen
* Install UI
	* Installation einer WEM Applikation
	* Installation von 2 AssetTypes in WCS
* Admin UI
	* Kommentare nach Status ein/ausblenden
	* Status von Kommentaren Šndern
	* BlogPosts nach Status ein/ausblenden
	* Status von BlogPosts Šndern
* App UI
	* Anzeige von Produkten
	* Anzeige von Kommentaren zu PRodukten
	* Erstellen neuer Kommentare
	* Suche in Produkten
* Simple JSON REST API (Wrapper fŸr REST Aufrufe in Web Center Sites)
	* Sites: [http://localhost:8081/wcs/sites](http://localhost:8081/wcs/sites)
	* Enabled Asset Types: [http://localhost:8081/wcs/assets/types](http://localhost:8081/wcs/assets/types)
	* Asset Type Definition [http://localhost:8081/wcs/assets/types/FW_Comment](http://localhost:8081/wcs/assets/types/FW_Comment)
		* Ersetze FW_Comment mit einem beliebigen Asset Type
	* Asset Search: http://localhost:8081/wcs/assets/types/FW_Comment/search
		* Ersetze FW_Comment mit einem beliebigen Asset Type
		* Optionale Parameter:
			* sites (Name einer Site)
			* query	(Suchbegriff, Suche erfolgt in indizierten Feldern)
			* fields (Attribute Names, Attribute, die im Ergebnis enthalten sein sollen)
			* startIndex (Startpunkt fŸr Paging)
			* count	(Anzahl der Ergebnisse)
			* sortField (Attribut, nach dem sortiert werden soll)
			* sortDirection (aufsteigend oder absteigend, asc|desc)
* Simple JSON REST API - Kommentare / BlogPosts
	* Kommentare [http://localhost:8081/comments](http://localhost:8081/comments)
	* Blog Posts [http://localhost:8081/blogposts](http://localhost:8081/blogposts)



### Version ###

* v0.0.1


### Setup ###

* `git clone https://bitbucket.org/mgoericke/wcs-wem.git`
* `mvn spring-boot:run`

#### Konfiguration ####

Hostnames und Ports sind abhŠngig vom App Server, auf dem die WEM Applikation bzw. WebCenter Sites lŠuft. Aktuell ist die Applikation konfiguriert fŸr:

* JSK oder WebCenter Sites Installation localhost, lauscht auf 9080
* WEM Applikation localhost, lauscht auf 8081

+ Port fŸr den Redirect auf CAS. Sollte in der Regel auf 4666 lauschen:
	* ./src/main/resources/cas-cache.xml
		* ehcache
			* cacheManagerPeerProviderFactory
				* multicastGroupPort = 4666

+ Ports und urls fŸr WCS und wem-community App Server
	* ./src/main/resources/SSOConfig.xml
		* beans.bean.property
			* `casUrl = http://localhost:9080/cas` (JSK oder WCS installation)
	* ./src/main/resources/application.properties
		* `csUrl = http://localhost:9080/cs` (JSK oder WCS installation)
		* `contextPathUrl = http://localhost:8081` (WEM application installation)

> EhCache mag keine IPv6 Adressen :( - Sollte ein Fehler `EhCache: cannot assign requested address` im log des App Server erscheinen, dann folgendes Argument an Java Ÿbergeben:
`-Djava.net.preferIPv4Stack=true`

* In WCS muss fŸr die AssetTypen Product_C und Content_C der Suchindex aktiviert werden, bevor die Installation beginnt!

#### Build und Installation ####
Ins Verzeichnis wechseln, in dem das Repository geladen wurde und `mvn spring-boot:run` ausfŸhren. Spring Boot startet einen Container, der auf den Port lauscht, der in `/src/main/resources/application.properties` als `server-port` angegeben wurde.

### Applikationen ###

#### Install UI (secured by CAS) ####
Installation: [http://localhost:8081/install](http://localhost:8081/install)

* Installiert die Applikation und die notwendigen Asset Typen. Die WEM Applikation wird als WEM in WebCenter Sites installiert und der Site Firstsite zugewiesen. 

> Anm.: WEM scheint ein Problem zu haben, wenn in URLs ein Port vorkommt. Icons werden deshalb nicht in der Applikation gehalten. Im Moment sind die auf http://javamark.de/silbury.png bzw. http://javamark.de/silbury-active.png abgelegt. Sollten spŠter an anderer Stelle gehostet werden. 

Es werden folgende Basic Asset Type(s) installiert:

* FW_Comment
	* Kommentare zu einem Asset (FW_Comment)
		* Es werden 1-2 Kommentare zu Produkten hinzugefŸgt. Die Kommentare habe verschiedene Freigabe Status (WAIT, APPROVED) - kann in Admin bearbeitet werden
	* BlogPost (FW_BlogPost)
		* initial gibt es nur einen Blog mit n-BlogPosts. 
 
Nach Installation und Aktivierung erscheint im WEM Header in WebCenter Sites (FirstSiteII) ein Logo. Klickt man darauf, kommt man ins Admin UI.

#### Admin UI (securde by CAS) ####
UI zur Administration der Applikation UND der Assets. Zu finden im WEM Admin im WebCenter Sites. Soll in ZUkunft die Administration (approval, publish, reject) der Kommentare abbilden.

#### Public UI (public mit optionalem Login) ####
…ffentliche Seite: [http://localhost:8081/app](http://localhost:8081/app) 

* Diese View zeigt WCS Produkte und, sofern vorhanden, die freigegebenen Kommentare an. Benutzer kšnnen sich anmelden und selbst Kommentare verfassen. 
* Im Moment kann ein angemeldeter Benutzer Kommentare anlegen und diese sind sofort freigegeben. Ziel sollte es sein, das als "Schalter" im Admin festzulegen: AUTO Approval vs. Editorial Approval

> Es gibt ein Problem ein Custom Login Template einzubinden, aktuell wird die Login Form direkt vom WCS eingebunden. Ist der Benutzer angemeldet, erscheint dort "Hallo Benutzer"



### Who do I talk to? ###

* [Mark](mark@jvamark.de)
* tba ;)