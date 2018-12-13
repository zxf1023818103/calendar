package chaoxinghelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;
import java.net.URI;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
        builder.headless(false).run(args);
        try {
            Desktop.getDesktop().browse(URI.create("http://localhost"));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
