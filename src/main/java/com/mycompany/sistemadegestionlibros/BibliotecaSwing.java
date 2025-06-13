/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.sistemadegestionlibros;

/**
 *
 * @author JHOANS
 */
import com.mongodb.client.*;
import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class BibliotecaSwing extends JFrame {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private GridFSBucket bucket;
    private DefaultListModel<String> modeloLista;
    private JList<String> listaLibros;

    public BibliotecaSwing() {
        setTitle("Biblioteca PDF");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initMongo();
        initUI();
        cargarLibros();
    }

    private void initMongo() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("biblioteca");
        bucket = GridFSBuckets.create(database);
    }

    private void initUI() {
        modeloLista = new DefaultListModel<>();
        listaLibros = new JList<>(modeloLista);
        JScrollPane scroll = new JScrollPane(listaLibros);

        JButton btnSubir = new JButton("Subir Libro PDF");
        JButton btnAbrir = new JButton("Abrir Libro");

        btnSubir.addActionListener(e -> subirLibro());
        btnAbrir.addActionListener(e -> abrirLibroSeleccionado());

        JPanel botones = new JPanel();
        botones.add(btnSubir);
        botones.add(btnAbrir);

        add(scroll, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void subirLibro() {
        JFileChooser chooser = new JFileChooser();
        int opcion = chooser.showOpenDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            String nombreArchivo = archivo.getName();

            String titulo = JOptionPane.showInputDialog(this, "Título del libro:");
            String autor = JOptionPane.showInputDialog(this, "Autor del libro:");

            try (FileInputStream fis = new FileInputStream(archivo)) {
                GridFSUploadOptions opciones = new GridFSUploadOptions()
                        .metadata(new Document("titulo", titulo)
                                          .append("autor", autor));

                bucket.uploadFromStream(nombreArchivo, fis, opciones);
                modeloLista.addElement(nombreArchivo);
                JOptionPane.showMessageDialog(this, "Libro subido exitosamente.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al subir el archivo.");
            }
        }
    }

    private void cargarLibros() {
        modeloLista.clear();
        for (GridFSFile file : bucket.find()) {
            modeloLista.addElement(file.getFilename());
        }
    }

    private void abrirLibroSeleccionado() {
        String nombre = listaLibros.getSelectedValue();
        if (nombre == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la lista.");
            return;
        }

        try {
            File destino = new File("descargas/" + nombre);
            destino.getParentFile().mkdirs(); // crea carpeta si no existe

            try (FileOutputStream salida = new FileOutputStream(destino)) {
                bucket.downloadToStream(nombre, salida);
            }

            Desktop.getDesktop().open(destino);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al abrir el libro.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BibliotecaSwing().setVisible(true));
    }
}
