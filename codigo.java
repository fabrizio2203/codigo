import java.util.Random;
import javax.swing.JOptionPane;

public class OrdenamientoSimple {
    
    public static void quickSort(int[] arr, int inicio, int fin) {
        if (inicio < fin) {
            int pivote = particion(arr, inicio, fin);
            quickSort(arr, inicio, pivote - 1);
            quickSort(arr, pivote + 1, fin);
        }
    }

    private static int particion(int[] arr, int inicio, int fin) {
        int pivote = arr[fin], i = inicio - 1;
        for (int j = inicio; j < fin; j++) {
            if (arr[j] <= pivote) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[fin]; arr[fin] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        int n = 10000;
        int[] datos = new int[n];
        Random rand = new Random();
        for (int i = 0; i < n; i++) datos[i] = rand.nextInt(100000);

        long inicio = System.currentTimeMillis();
        quickSort(datos, 0, datos.length - 1);
        long fin = System.currentTimeMillis();

        JOptionPane.showMessageDialog(null,
            "QuickSort completado en " + (fin - inicio) + " ms");
    }
}
