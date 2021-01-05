package beamline.declare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"beamline.core"})
public class DeclareMinerBackendApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(DeclareMinerBackendApplication.class, args);
	}
}