package uni.hamburg.inf.sssa.weights;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TLongFloatHashMap;

import org.bson.BSON;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;

public class WeightMongoDB extends Weights {

	MongoClient mongoClient;
	MongoDatabase mongoDb; 
	MongoCollection<Document> table ;
	
	public WeightMongoDB() {
		super();
		mongoClient= new MongoClient("localhost", 27017);
		mongoDb = mongoClient.getDatabase("testdb");
		table = mongoDb.getCollection("features");
		table.drop();
		IndexOptions indexOptions = new IndexOptions().unique(true);
		table.createIndex(Indexes.ascending("feature"), indexOptions);
		//table.createIndex(arg0)ensureIndex({name:1}, {unique:true});
		
	}
	@Override
public void addWeight(long featureKey, float value)
{
		try {
			Document document = new Document();
			document.put("feature",featureKey+"");
			document.put("weight" , Float.valueOf(value));
			table.insertOne(document);	
		} catch (Exception e) {
			//System.out.println("ALLES GUT");
			//e.printStackTrace();
		}
		
}
	@Override
public void addWeight(long featureKey)
{
	if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.randomWeights))
	{
		addWeight(featureKey,(float)(Math.random()*0.5));
	}
	else
	{
		addWeight(featureKey,0.5f);
	}
}
	@Override
public void updateWeight(long featureKey, float value)
{
		
		BasicDBObject newDocument = new BasicDBObject();
		  //newDocument.put("weight", value);
		  newDocument.append("$set", new BasicDBObject().append("weight", value));
		  
		  BasicDBObject searchQuery = new BasicDBObject().append("feature", featureKey+"");

		  table.updateOne(searchQuery, newDocument);

}
	@Override
	public TLongFloatMap getPublicWeights(FeatureVector fv) {
		TLongFloatMap results=new TLongFloatHashMap();
		long[] keys=fv.getFeatureMap().keys();
		
		for (int i = 0; i <keys.length; i++) {
			results.put(keys[i], (float)find((int)keys[i]));
		}
		
		return results;
	}
	public double find(int featurekey)
	{
		 BasicDBObject searchQuery = new BasicDBObject().append("feature", featurekey+"");
		 FindIterable<Document> result= table.find(searchQuery);
		 for (Document document : result) {
			 return (double)document.get("weight");
		 }
		 return 0.0;
	}

	public float readAll()
	{
	//BSON searchQuery2 = new BSON().append("feature", featurekey);

	FindIterable<Document>  elments = table.find();

	for (Document document : elments) {
		System.out.println(document.get("weight"));
	}
return 0f;
}

public final void closeMongoDBConnection() {
    try {
        mongoClient.close();
    } catch (Exception e) {
        System.err.println("Error in terminating connection");
       
    }
}
public static void main (String[] arg)
{
	WeightMongoDB weightMongo=new WeightMongoDB();
	weightMongo.addWeight(1, 0.2f);
	weightMongo.addWeight(2, 0.5f);
	weightMongo.addWeight(3, 0.3f);
	weightMongo.updateWeight(1, 0.1f);
	weightMongo.addWeight(3, 0.9f);
	weightMongo.readAll();
	//float value=(float)weightMondo.find(1);
	FeatureVector fv=new FeatureVector();
	fv.addFeature(2, 1f);
	fv.addFeature(3, 1f);
	fv.addFeature(1, 1f);
	fv.addFeature(1, 1f);
	fv.addFeature(5, 1f);
	System.out.println(weightMongo.getPublicWeights(fv));
	//System.out.println(value);	
}
	
}
