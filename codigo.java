package busquedaexterna;

import java.io.*;
import java.util.Scanner;

public class BusquedaExterna {

    static final String ARCHIVO_DATOS = "datos.txt";

    static final int TOTAL_REGISTROS = 100_000;

    static final int TAMANO_BLOQUE = 1000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!new File(ARCHIVO_DATOS).exists()) {
            System.out.println("Creando archivo de datos..");
            crearArchivoDatos();
        }

        System.out.println("=== BUSQUEDA EXTERNA ===");
        System.out.print("Ingresa el ID a buscar (ej: ID50000): ");
        String valorBuscado = scanner.nextLine().trim();

        boolean encontrado = buscarEnArchivo(valorBuscado);

        if (encontrado) {
            System.out.println(" Valor encontrado en el archivo.");
        } else {
            System.out.println(" Valor No encontrado.");
        }

        scanner.close();
    }

    public static void crearArchivoDatos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DATOS))) {
            for (int i = 0; i < TOTAL_REGISTROS; i++) {
                String registro = "ID" + i + ",Nombre_" + i;
                writer.write(registro);
                writer.newLine();
            }
            System.out.println("Archivo creado con " + TOTAL_REGISTROS + " registros.");
        } catch (IOException e) {
            System.err.println("Error al crear el archivo: " + e.getMessage());
        }
    }

    public static boolean buscarEnArchivo(String valor) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DATOS))) {
            String[] bloque = new String[TAMANO_BLOQUE];
            int index = 0;
            String linea;
            int totalLeidos = 0;
            int bloquesLeidos = 0;

            while ((linea = reader.readLine()) != null) {
                bloque[index++] = linea;
                totalLeidos++;

                if (index == TAMANO_BLOQUE || totalLeidos == TOTAL_REGISTROS) {
                    bloquesLeidos++;
                    System.out.println("Leyendo bloque #" + bloquesLeidos);

                    for (int i = 0; i < index; i++) {
                        if (bloque[i].startsWith(valor + ",")) {
                            System.out.println("Registro: " + bloque[i]);
                            return true;
                        }
                    }
                    index = 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        return false;
    }
}
fgdfgfdgf

