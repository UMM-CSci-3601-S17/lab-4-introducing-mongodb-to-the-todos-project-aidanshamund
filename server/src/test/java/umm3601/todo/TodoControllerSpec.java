package umm3601.todo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.*;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TodoControllerSpec
{
    private TodoController todoController;
    private String samsIdString;

    @Before
    public void clearAndPopulateDB() throws IOException {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = mongoClient.getDatabase("test");
        MongoCollection<Document> todoDocuments = db.getCollection("todos");
        todoDocuments.drop();
        List<Document> testTodos = new ArrayList<>();
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985a22c04e761776d54\",\n" +
                "                    owner: \"Blanche\",\n" +
                "                    status: false,\n" +
                "                    body: \"UMM\",\n" +
                "                    category: \"software design\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985c1849992336c219b\",\n" +
                "                    owner: \"Fry\",\n" +
                "                    status: false,\n" +
                "                    body: \"IBM\",\n" +
                "                    category: \"video games\"\n" +
                "                }"));
        testTodos.add(Document.parse("{\n" +
                "                    _id: \"58895985ae3b752b124e7663\",\n" +
                "                    owner: \"Fry\",\n" +
                "                    status: true,\n" +
                "                    body: \"Frogs, Inc.\",\n" +
                "                    category: \"homework\"\n" +
                "                }"));
        ObjectId samsId = new ObjectId();
        BasicDBObject sam = new BasicDBObject("_id", samsId);
        sam = sam.append("_id", "58895985c1849992336c219c")
                .append("owner", "Sam")
                .append("status", true)
                .append("body", "Frogs, Inc.")
                .append("category", "homework");
        samsIdString = samsId.toHexString();
        todoDocuments.insertMany(testTodos);
        todoDocuments.insertOne(Document.parse(sam.toJson()));

        // It might be important to construct this _after_ the DB is set up
        // in case there are bits in the constructor that care about the state
        // of the database.
        todoController = new TodoController();
    }

    // http://stackoverflow.com/questions/34436952/json-parse-equivalent-in-mongo-driver-3-x-for-java
    private BsonArray parseJsonArray(String json) {
        final CodecRegistry codecRegistry
                = CodecRegistries.fromProviders(Arrays.asList(
                new ValueCodecProvider(),
                new BsonValueCodecProvider(),
                new DocumentCodecProvider()));

        JsonReader reader = new JsonReader(json);
        BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);

        return arrayReader.decode(reader, DecoderContext.builder().build());
    }


    private static String getOwner(BsonValue val) {
        BsonDocument doc = val.asDocument();
        return ((BsonString) doc.get("owner")).getValue();
    }

    @Test
    public void getAllTodos() {
        Map<String, String[]> emptyMap = new HashMap<>();
        String jsonResult = todoController.listTodos(emptyMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 4 todos", 4, docs.size());
        List<String> owners = docs
                .stream()
                .map(TodoControllerSpec::getOwner)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Blanche", "Fry", "Fry", "Sam");
        assertEquals("Owners should match", expectedOwners, owners);
    }

    @Test
    public void getTodosThatAreComplete() {
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("status", new String[] { "true" });
        String jsonResult = todoController.listTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);

        assertEquals("Should be 2 todos", 2, docs.size());
        List<String> owners = docs
                .stream()
                .map(TodoControllerSpec::getOwner)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Fry", "Sam");
        assertEquals("Owners should match", expectedOwners, owners);
    }

    @Test
    public void getSamById() {
        Map<String, String[]> argMap = new HashMap<>();
        argMap.put("_id", new String[] { "58895985c1849992336c219c" });
        String jsonResult = todoController.listTodos(argMap);
        BsonArray docs = parseJsonArray(jsonResult);
        List<String> sam = docs
                .stream()
                .map(TodoControllerSpec::getOwner)
                .sorted()
                .collect(Collectors.toList());
        List<String> expectedOwners = Arrays.asList("Sam");
        assertEquals("Owner should match", expectedOwners, sam);
    }
}
