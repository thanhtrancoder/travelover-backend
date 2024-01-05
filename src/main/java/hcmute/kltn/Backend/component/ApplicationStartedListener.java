package hcmute.kltn.Backend.component;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

@Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationStartedListener.class);
	
    private MongoClient mongoClient;
    
    @Value("${spring.data.mongodb.database}")
    private String database;

    @Autowired
    public void MongoConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
    
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
    	try {
            mongoClient.getDatabase(database);
            System.out.println("Connected to MongoDB");
        } catch (Exception e) {
            System.err.println("MongoDB connection error: " + e.getMessage());
        }
    } 
//    @PostConstruct
//    public void checkMongoDBConnection() {
//        try {
//            mongoClient.getDatabase(database);
//            System.out.println("Connected to MongoDB");
//        } catch (Exception e) {
//            System.err.println("MongoDB connection error: " + e.getMessage());
//        }
//    }
	
//	@Autowired
//    private DataSource dataSource;
//
//    @Override
//    public void onApplicationEvent(ApplicationStartedEvent event) {
//    	try (Connection connection = dataSource.getConnection()) {
//            logger.info("Connected to database: {}", connection.getMetaData().getURL());
//        } catch (SQLException e) {
//            logger.error("Failed to connect to database: {}", e.getMessage());
//        }
//    } 
}
