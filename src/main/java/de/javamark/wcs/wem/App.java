package de.javamark.wcs.wem;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

@Import(AppConfig.class)
public class App {

    public static void main(String[] args) throws Exception {
    	System.getProperties().setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(App.class, args);
    }

}
