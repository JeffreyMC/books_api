package com.jeff.booksapi.principal;

import com.jeff.booksapi.model.Datos;
import com.jeff.booksapi.model.DatosLibros;
import com.jeff.booksapi.service.ConsumoAPI;
import com.jeff.booksapi.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);

    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);

        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        //10 libros mas descargados
        System.out.println("Los 10 libros mas descargados son:");
        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
                .limit(10)
                .map(b -> b.titulo().toUpperCase())
                .forEach(System.out::println);

        //buscar libros por nombre
        System.out.println("Ingrese el libro que desee busccar: ");
        var tituloLibro = teclado.nextLine();

        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);

        Optional<DatosLibros> libroBuscado = datosBusqueda.libros().stream()
                .filter(b -> b.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();

        if(libroBuscado.isPresent()){
            System.out.println("Libro encontrado");
            System.out.println(libroBuscado.get());
        }else{
            System.out.println("Libro no encontrado");
        }

        //usando estadisticas
        DoubleSummaryStatistics est = datos.libros().stream()
                .filter(d -> d.numeroDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDescargas));

        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad max de descargas: " + est.getMax());
        System.out.println("Cantidad min de descargas: " + est.getMin());
        System.out.println("Cantidad de registros: " + est.getCount());

    }
}
