package com.ctl.utils.db;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
public class MongoDbDemo1 {
    public static void main( String args[] ){
        try{
            // 连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

            // 连接到数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
            System.out.println("Connect to database successfully");
            System.out.println(mongoDatabase.getName());
            System.out.println(mongoClient.getDatabase("test").getCollection("test"));
            mongoClient.close();
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
