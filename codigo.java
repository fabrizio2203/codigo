package hashsorting;

import java.util.*;

public class HashSorting {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("========== ALGORITMO HASH SORTING ==========");
        System.out.print("Ingrese la cantidad de palabras a generar o ingresar: ");
        int cantidad = sc.nextInt();
        sc.nextLine();

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

       
        Map<Integer, List<String>> tablaHash = crearTablaHash(listaPalabras);

       
        System.out.println("\n--- Tabla Hash Generada ---");
        imprimirTablaHash(tablaHash);

       
        System.out.println("\n--- Ordenando Buckets... ---");
        ordenarBuckets(tablaHash);

       
        List<String> listaOrdenada = combinarBuckets(tablaHash);

        
        Collections.sort(listaOrdenada);

        System.out.println("\n--- Lista final ordenada ---");
        imprimirLista(listaOrdenada);

       
        compararTiempos(listaPalabras);

        System.out.println("\n========== FIN DEL PROGRAMA ==========");
    }

    
    public static List<String> generarPalabrasAleatorias(int cantidad) {
        List<String> palabras = new ArrayList<>();
        String letras = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        for (int i = 0; i < cantidad; i++) {
            int longitud = random.nextInt(5) + 3; 
            StringBuilder palabra = new StringBuilder();
            for (int j = 0; j < longitud; j++) {
                palabra.append(letras.charAt(random.nextInt(letras.length())));
            }
            palabras.add(palabra.toString());
        }
        return palabras;
    }

    
    public static List<String> leerPalabrasUsuario(int cantidad, Scanner sc) {
        List<String> palabras = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            System.out.print("Ingrese palabra #" + (i + 1) + ": ");
            palabras.add(sc.nextLine());
        }
        return palabras;
    }

   
    public static Map<Integer, List<String>> crearTablaHash(List<String> palabras) {
        Map<Integer, List<String>> tabla = new HashMap<>();

        for (String palabra : palabras) {
            int hash = palabra.hashCode() % 10; 
            tabla.putIfAbsent(hash, new ArrayList<>());
            tabla.get(hash).add(palabra);
        }
        return tabla;
    }

    
    public static void ordenarBuckets(Map<Integer, List<String>> tabla) {
        for (Map.Entry<Integer, List<String>> entrada : tabla.entrySet()) {
            Collections.sort(entrada.getValue());
        }
    }

    
    public static List<String> combinarBuckets(Map<Integer, List<String>> tabla) {
        List<String> resultado = new ArrayList<>();
        for (List<String> bucket : tabla.values()) {
            resultado.addAll(bucket);
        }
        return resultado;
    }

  
    public static void imprimirTablaHash(Map<Integer, List<String>> tabla) {
        for (Map.Entry<Integer, List<String>> entrada : tabla.entrySet()) {
            System.out.println("Hash " + entrada.getKey() + " → " + entrada.getValue());
        }
    }

    
    public static void imprimirLista(List<String> lista) {
        for (String palabra : lista) {
            System.out.println(" - " + palabra);
        }
    }

    
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
