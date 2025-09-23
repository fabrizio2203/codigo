import javax.swing.*;
import java.util.*;
import java.security.*;

public class HashSorting {

    public static void main(String[] args) {
        List<String> lista = generarListaAleatoria();
        List<String> listaOriginal = new ArrayList<>(lista);
        Map<Integer, List<String>> tablaHash = generarTablaHash(lista);
        List<String> listaOrdenada = ordenarBucket(tablaHash);
        Collections.sort(listaOrdenada); 

        imprimirResultados(listaOriginal, listaOrdenada);
        generarInformeDeTablaHash(tablaHash);

        String mensaje = generarReporte(listaOriginal, listaOrdenada);
        mostrarCuadro(mensaje);
    }

    public static List<String> generarListaAleatoria() {
        List<String> listaAleatoria = new ArrayList<>();
        String[] frutas = {
            "banana", "manzana", "cereza", "uva", "kiwi", "mango",
            "fresa", "pera", "melón", "cantalupo", "durazno", "ciruela",
     };
        for (String fruta : frutas) {
            listaAleatoria.add(fruta);
        }
        Collections.shuffle(listaAleatoria);
        return listaAleatoria;
    }
