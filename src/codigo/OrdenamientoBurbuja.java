import java.util.Random;
import javax.swing.JOptionPane;

public class OrdenamientoBurbuja {

    public static void main(String[] args) {
        int n = 5;
        int[] datos = new int[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            datos[i] = rand.nextInt(100);
        }

        StringBuilder original = new StringBuilder("Arreglo original:\n");
        for (int i = 0; i < n; i++) {
            original.append(datos[i]).append(" ");
        }

        long inicio = System.currentTimeMillis();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (datos[j] > datos[j + 1]) {
                    int temp = datos[j];
                    datos[j] = datos[j + 1];
                    datos[j + 1] = temp;
                }
            }
        }

        long fin = System.currentTimeMillis();

        StringBuilder ordenado = new StringBuilder("\n\nArreglo ordenado:\n");
        for (int i = 0; i < n; i++) {
            ordenado.append(datos[i]).append(" ");
        }

        JOptionPane.showMessageDialog(null,
            original.toString() + ordenado.toString() +
            "\n\nTiempo: " + (fin - inicio) + " ms");
    }
}