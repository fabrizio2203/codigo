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
            "higo", "sandía", "limón", "naranja", "pomelo", "granada",
            "guayaba", "lichi"
        };
        for (String fruta : frutas) {
            listaAleatoria.add(fruta);
        }
        Collections.shuffle(listaAleatoria);
        return listaAleatoria;
    }

    public static Map<Integer, List<String>> generarTablaHash(List<String> lista) {
        Map<Integer, List<String>> hashTable = new HashMap<>();
        for (String item : lista) {
            int hashValue = getHash(item);
            hashTable.computeIfAbsent(hashValue, k -> new ArrayList<>()).add(item);
        }
        return hashTable;
    }

    public static List<String> ordenarBucket(Map<Integer, List<String>> hashTable) {
        List<String> ordenada = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : hashTable.entrySet()) {
            List<String> bucket = entry.getValue();
            Collections.sort(bucket);
            ordenada.addAll(bucket);
        }
        return ordenada;
    }

    public static int getHash(String item) {
        int hashValue = 0;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(item.getBytes());
            for (int i = 0; i < 4; i++) {
                hashValue |= (hashBytes[i] & 0xFF) << (i * 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashValue;
    }

    public static String generarReporte(List<String> original, List<String> ordenada) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista original:\n");
        for (String item : original) {
            sb.append(item).append(", ");
        }
        sb.append("\n\nLista ordenada alfabéticamente:\n");
        for (String item : ordenada) {
            sb.append(item).append(", ");
        }
        return sb.toString();
    }

    public static void mostrarCuadro(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Resultado de Ordenación", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void imprimirResultados(List<String> original, List<String> ordenada) {
        System.out.println("Lista original:");
        for (String item : original) {
            System.out.println(item);
        }
        System.out.println("\nLista ordenada alfabéticamente:");
        for (String item : ordenada) {
            System.out.println(item);
        }
    }

    public static void generarInformeDeTablaHash(Map<Integer, List<String>> tablaHash) {
        System.out.println("\n--- Tabla Hash Generada ---");
        for (Map.Entry<Integer, List<String>> entry : tablaHash.entrySet()) {
            System.out.println("Hash: " + entry.getKey() + " → " + entry.getValue());
        }
    }
}
