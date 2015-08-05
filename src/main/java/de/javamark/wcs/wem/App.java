package de.javamark.wcs.wem;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

/**
 * Starter Class - pom.xml: <start-class>de.javamark.wcs.wem.App</start-class>
 * 
 * @author mark
 *
 */
@Import(AppConfig.class)
public class App {

    public static void main(String[] args) throws Exception {
    	// EhCache mag keine IPv6 Adressen :( - Fehler ist `EhCache: cannot assign requested address, Lšsung ist folgende ...
    	System.getProperties().setProperty("java.net.preferIPv4Stack", "true");
    	
        SpringApplication.run(App.class, args);
    }

}
