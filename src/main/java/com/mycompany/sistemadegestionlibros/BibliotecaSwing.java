package com.mycompany.sistemadegestionlibros;

import com.mongodb.client.*;
import com.mongodb.client.gridfs.*;
import com.mongodb.client.gridfs.model.*;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

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
        //Conectamos a la base de datos MongoDB
        // Asegúrate de tener el servidor MongoDB corriendo en localhost:27017
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
        JButton btnEliminar = new JButton("Eliminar Libro");

        btnSubir.addActionListener(e -> subirLibro());
        btnAbrir.addActionListener(e -> abrirLibroSeleccionado());
        btnEliminar.addActionListener(e -> eliminarLibroSeleccionado());

        JPanel botones = new JPanel();
        botones.add(btnSubir);
        botones.add(btnAbrir);
        botones.add(btnEliminar);

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

    private void eliminarLibroSeleccionado() {
        String nombre = listaLibros.getSelectedValue();
        if (nombre == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la lista.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas eliminar el libro \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            GridFSFindIterable archivos = bucket.find(new Document("filename", nombre));
            GridFSFile archivo = archivos.first();

            if (archivo != null) {
                bucket.delete(archivo.getObjectId());
                modeloLista.removeElement(nombre);
                JOptionPane.showMessageDialog(this, "Libro eliminado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el libro en la base de datos.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BibliotecaSwing().setVisible(true));
    }
}
