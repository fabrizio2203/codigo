package hashsorting;

import java.util.*;

/**
 * Algoritmo Hash Sorting
 * Autor: Alonso (Ejemplo educativo)
 * Descripción:
 * Este programa demuestra cómo ordenar una lista de cadenas
 * utilizando una tabla hash (HashMap) para agruparlas
 * antes de ordenarlas alfabéticamente.
 */
public class HashSorting {

    public static void main(String[] args) {
        // Scanner para leer entrada del usuario
        Scanner sc = new Scanner(System.in);

        System.out.println("========== ALGORITMO HASH SORTING ==========");
        System.out.print("Ingrese la cantidad de palabras a generar o ingresar: ");
        int cantidad = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        System.out.println("¿Desea ingresar las palabras manualmente? (s/n): ");
        String opcion = sc.nextLine();

        List<String> listaPalabras;

        if (opcion.equalsIgnoreCase("s")) {
            listaPalabras = leerPalabrasUsuario(cantidad, sc);
        } else {
            listaPalabras = generarPalabrasAleatorias(cantidad);
        }

        System.out.println("\n--- Lista original ---");
        imprimirLista(listaPalabras);

        // Paso 1: Crear tabla hash
        Map<Integer, List<String>> tablaHash = crearTablaHash(listaPalabras);

        // Paso 2: Mostrar tabla hash creada
        System.out.println("\n--- Tabla Hash Generada ---");
        imprimirTablaHash(tablaHash);

        // Paso 3: Ordenar cada bucket de la tabla
        System.out.println("\n--- Ordenando Buckets... ---");
        ordenarBuckets(tablaHash);

        // Paso 4: Combinar todos los buckets
        List<String> listaOrdenada = combinarBuckets(tablaHash);

        // Paso 5: Orden alfabético final
        Collections.sort(listaOrdenada);

        System.out.println("\n--- Lista final ordenada ---");
        imprimirLista(listaOrdenada);

        // Paso 6: Mostrar comparación de tiempos
        compararTiempos(listaPalabras);

        System.out.println("\n========== FIN DEL PROGRAMA ==========");
    }

    // Genera palabras aleatorias
    public static List<String> generarPalabrasAleatorias(int cantidad) {
        List<String> palabras = new ArrayList<>();
        String letras = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        for (int i = 0; i < cantidad; i++) {
            int longitud = random.nextInt(5) + 3; // entre 3 y 7 letras
            StringBuilder palabra = new StringBuilder();
            for (int j = 0; j < longitud; j++) {
                palabra.append(letras.charAt(random.nextInt(letras.length())));
            }
            palabras.add(palabra.toString());
        }
        return palabras;
    }

    // Permite al usuario ingresar palabras manualmente
    public static List<String> leerPalabrasUsuario(int cantidad, Scanner sc) {
        List<String> palabras = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            System.out.print("Ingrese palabra #" + (i + 1) + ": ");
            palabras.add(sc.nextLine());
        }
        return palabras;
    }

    // Crea una tabla hash agrupando palabras según su código hash
    public static Map<Integer, List<String>> crearTablaHash(List<String> palabras) {
        Map<Integer, List<String>> tabla = new HashMap<>();

        for (String palabra : palabras) {
            int hash = palabra.hashCode() % 10; // agrupación por módulo
            tabla.putIfAbsent(hash, new ArrayList<>());
            tabla.get(hash).add(palabra);
        }
        return tabla;
    }

    // Ordena las listas dentro de cada bucket
    public static void ordenarBuckets(Map<Integer, List<String>> tabla) {
        for (Map.Entry<Integer, List<String>> entrada : tabla.entrySet()) {
            Collections.sort(entrada.getValue());
        }
    }

    // Combina todos los buckets en una lista final
    public static List<String> combinarBuckets(Map<Integer, List<String>> tabla) {
        List<String> resultado = new ArrayList<>();
        for (List<String> bucket : tabla.values()) {
            resultado.addAll(bucket);
        }
        return resultado;
    }

    // Imprime la tabla hash
    public static void imprimirTablaHash(Map<Integer, List<String>> tabla) {
        for (Map.Entry<Integer, List<String>> entrada : tabla.entrySet()) {
            System.out.println("Hash " + entrada.getKey() + " → " + entrada.getValue());
        }
    }

    // Imprime una lista de palabras
    public static void imprimirLista(List<String> lista) {
        for (String palabra : lista) {
            System.out.println(" - " + palabra);
        }
    }

    // Mide el tiempo de ordenamiento con HashSorting vs Collections.sort
    public static void compararTiempos(List<String> lista) {
        List<String> copia1 = new ArrayList<>(lista);
        List<String> copia2 = new ArrayList<>(lista);

        long inicioHash = System.nanoTime();
        Map<Integer, List<String>> tabla = crearTablaHash(copia1);
        ordenarBuckets(tabla);
        List<String> listaOrdenada = combinarBuckets(tabla);
        Collections.sort(listaOrdenada);
        long finHash = System.nanoTime();

        long inicioSort = System.nanoTime();
        Collections.sort(copia2);
        long finSort = System.nanoTime();

        System.out.println("\n--- Comparación de tiempos ---");
        System.out.println("HashSorting: " + (finHash - inicioHash) + " ns");
        System.out.println("Collections.sort: " + (finSort - inicioSort) + " ns");
    }
            }
