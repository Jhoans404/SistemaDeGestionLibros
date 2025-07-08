/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gestionlibros.interfaz;

/**
 *
 * @author JHOANS
 */

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class ConexionMongo {
    public void conectar(){
       // Reemplaza con tu URI, si estás usando MongoDB Atlas u otra IP
        String uri = "mongodb://localhost:27017";

        // Crear cliente
        MongoClient mongoClient = MongoClients.create(uri);

        // Conectarse a la base de datos
        MongoDatabase database = mongoClient.getDatabase("mi_basedatos");

        // Conectarse a una colección
        MongoCollection<Document> coleccion = database.getCollection("mi_coleccion");

        // Insertar un documento de ejemplo
        Document doc = new Document("nombre", "Jhoans")
                            .append("edad", 21)
                            .append("Titulo", "Ingenieria de sistemas");
        coleccion.insertOne(doc);

        System.out.println("Documento insertado correctamente."); 
    }
}
