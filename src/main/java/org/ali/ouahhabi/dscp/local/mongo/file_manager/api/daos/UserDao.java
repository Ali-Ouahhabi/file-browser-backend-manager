/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.User;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.UserRegister;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.Session;
import org.bson.Document;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Ali Ouahhabi
 */
@Configuration
public class UserDao {

    private final MongoCollection<UserRegister> usersCollection;
    private final MongoCollection<Document> sessionsCollection;

    @Autowired
    UserDao(MongoClient mongoClient, @Value("${spring.mongodb.database}") String database) {

        MongoDatabase myDatabase = mongoClient.getDatabase(database);

        CodecRegistry pojoCodecRegistry
                = fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(
                                PojoCodecProvider
                                        .builder()
                                        .automatic(true)
                                        .build()
                        )
                );

        usersCollection = myDatabase
                .getCollection("users", UserRegister.class)
                .withCodecRegistry(pojoCodecRegistry);
        sessionsCollection = myDatabase.getCollection("sessions");

    }

    public boolean addUser(UserRegister user) throws Exception {
        if (getUser(user.getEmail()) != null) {
            usersCollection.find();
            InsertOneResult insertOne = usersCollection.insertOne(user);
            return insertOne.wasAcknowledged();
        }else{
            throw new Exception("user email alerady exist");
        }
    }

    public User getUser(String email) {
        Document filter = new Document("email", email);
        UserRegister user = usersCollection.find(filter).iterator().tryNext();
        return new User(user.getEmail(), user.getPassword(), user.getRole());
    }

    public boolean createUserSession(String userId, String jwt) {
        deleteUserSessions(userId);
        Document userSession = new Document("user_id", userId);
        userSession.put("jwt", jwt);
        sessionsCollection.insertOne(userSession);
        return true;
    }
    
    public Session getUserSession(String userId) {
        Document filter = new Document("user_id", userId);
        Document result = sessionsCollection.find(filter).iterator().tryNext();
        if (result != null) {
            return getSessionFromDocument(result);
        }
        return null;
    }
    
    private Session getSessionFromDocument(Document doc) {
        Session session = new Session();
        session.setUserId(doc.getString("user_id"));
        session.setJwt(doc.getString("jwt"));
        return session;
    }

    public boolean deleteUserSessions(String userId) {
        Document filter = new Document("user_id", userId);
        DeleteResult rsp = sessionsCollection.deleteOne(filter);
        return rsp.wasAcknowledged();
    }
}
