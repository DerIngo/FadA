package deringo.fada;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Autowired
    private Environment env;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    
    @SuppressWarnings("unused")
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            String key = "configuration";
            System.out.println(String.format("Value of '%s' is '%s'", key, env.getProperty(key)));
        };
    }
}