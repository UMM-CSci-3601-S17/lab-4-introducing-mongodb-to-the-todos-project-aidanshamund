package umm3601.todo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;

import java.io.IOException;
import java.util.Map;

public class TodoController {

    private final MongoCollection<Document> todoCollection;

    public TodoController() throws IOException{
        // Set up our server address
        // (Default host: 'localhost', default port: 27017)
        // ServerAddress testAddress = new ServerAddress();

        // Try connecting to the server
        //MongoClient mongoClient = new MongoClient(testAddress, credentials);
        MongoClient mongoClient = new MongoClient(); // Defaults!

        // Try connecting to a database
        MongoDatabase db = mongoClient.getDatabase("test");

        todoCollection = db.getCollection("todos");
    }

    public String listTodos(Map<String, String[]> queryParameter) {
        Document filterDoc = new Document();

        //Finds with _id
        if(queryParameter.containsKey("_id")) {
            String target_id = (queryParameter.get("_id")[0]);
            filterDoc = filterDoc.append("_id", target_id);
        }

        //status todos
        if(queryParameter.containsKey("status")) {
            boolean targetStatus = Boolean.parseBoolean(queryParameter.get("status")[0]);
            filterDoc = filterDoc.append("status", targetStatus);
        }

        //Todos with specified word in body
        if(queryParameter.containsKey("contains")){
            String targetContains = (queryParameter.get("contains")[0]);
            filterDoc = filterDoc.append("contains", targetContains);
        }

        //Todos specified by owner
        if(queryParameter.containsKey("owner")){
            String targetOwner = (queryParameter.get("owner")[0]);
            filterDoc = filterDoc.append("owner", targetOwner);
        }

        //Todos specified by category
        if(queryParameter.containsKey("category")){
            String targetCategory = (queryParameter.get("category")[0]);
            filterDoc = filterDoc.append("category", targetCategory);
        }

        FindIterable<Document> matchingTodos = todoCollection.find(filterDoc);

        return JSON.serialize(matchingTodos);
    }

}