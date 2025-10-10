import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class ExternalSearchDemo {
    
    private static final Path DOCS_DIR = Paths.get("docs");
    private static final Path INDEX_FILE = Paths.get("disk", "index.txt");
    private static final Path META_INDEX_FILE = Paths.get("disk", "meta_index.txt");

    private final Map<String, Set<String>> invertedIndex = new HashMap<>();

    private final Map<String, Map<String, Set<String>>> metaIndex = new HashMap<>();

    public static void main(String[] args) {
        ExternalSearchDemo demo = new ExternalSearchDemo();

        try {
            Files.createDirectories(INDEX_FILE.getParent());

            System.out.println("Indexando documentos en: " + DOCS_DIR.toAbsolutePath());
            demo.buildIndexFromDocs(DOCS_DIR);

            demo.saveIndexToDisk(INDEX_FILE);
            demo.saveMetaIndexToDisk(META_INDEX_FILE);

            ExternalSearchDemo loaded = new ExternalSearchDemo();
            loaded.loadIndexFromDisk(INDEX_FILE);
            loaded.loadMetaIndexFromDisk(META_INDEX_FILE);

            System.out.println("\n--- Búsquedas de ejemplo ---");
            System.out.println("Buscar término: 'algoritmo'");
            Set<String> r1 = loaded.searchByTerms(Arrays.asList("algoritmo"));
            System.out.println("Resultados: " + r1);

            System.out.println("\nBuscar AND: 'búsqueda' AND 'externa'");
            Set<String> r2 = loaded.searchAnd(Arrays.asList("búsqueda", "externa"));
            System.out.println("Resultados AND: " + r2);

            System.out.println("\nBuscar por metadato author:Juan");
            Set<String> r3 = loaded.searchByMeta("author", "Juan");
            System.out.println("Resultados meta: " + r3);

            System.out.println("\nBuscar término + meta (algoritmo + author:Juan)");
            Set<String> r4 = loaded.searchCombined(Arrays.asList("algoritmo"), "author", "Juan");
            System.out.println("Resultados combinados: " + r4);

            System.out.println("\nTerminos más comunes indexados (top 10):");
            loaded.printTopTerms(10);

        } catch (IOException e) {
            System.err.println("Error de IO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void buildIndexFromDocs(Path docsDir) throws IOException {
        if (!Files.exists(docsDir) || !Files.isDirectory(docsDir)) {
            System.out.println("No se encontró la carpeta docs/. Crea 'docs' y coloca archivos .txt para indexar.");
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(docsDir, "*.txt")) {
            for (Path file : stream) {
                String docId = file.getFileName().toString();
                indexSingleFile(file, docId);
            }
        }
    }

    private void indexSingleFile(Path file, String docId) {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String first = reader.readLine();
            int lineIndex = 0;
            if (first != null && first.startsWith("#meta")) {
                parseMetaLine(first.substring(5).trim(), docId); 
                lineIndex++;
            } else 
            {
                if (first != null) {
                    indexTextLine(first, docId);
                    lineIndex++;
                }
            }
            String line;
            while ((line = reader.readLine()) != null) {
                indexTextLine(line, docId);
                lineIndex++;
            }
            System.out.println("Indexado: " + docId + " (" + lineIndex + " líneas)");
        } catch (IOException e) {
            System.err.println("Fallo al indexar " + docId + ": " + e.getMessage());
        }
    }

    private void parseMetaLine(String meta, String docId) {
        String[] pairs = meta.split(";");
        for (String pair : pairs) {
            if (pair.trim().isEmpty()) continue;
            String[] kv = pair.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim().toLowerCase();
            String value = kv[1].trim();
            metaIndex.computeIfAbsent(key, k -> new HashMap<>())
                    .computeIfAbsent(value, v -> new HashSet<>())
                    .add(docId);
        }
    }

    private void indexTextLine(String line, String docId) {
        String[] tokens = line.toLowerCase().split("[^a-z0-9áéíóúüñ]+");
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            if (token.length() <= 2) continue;
            invertedIndex.computeIfAbsent(token, t -> new HashSet<>()).add(docId);
        }
    }

    public void saveIndexToDisk(Path indexPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(indexPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Map.Entry<String, Set<String>> e : invertedIndex.entrySet()) {
                String line = e.getKey() + "|" + String.join(",", e.getValue());
                writer.write(line);
                writer.newLine();
            }
        }
        System.out.println("Índice invertido guardado en: " + indexPath.toAbsolutePath());
    }
    public void saveMetaIndexToDisk(Path metaPath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(metaPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Map.Entry<String, Map<String, Set<String>>> fieldEntry : metaIndex.entrySet()) {
                String field = fieldEntry.getKey();
                for (Map.Entry<String, Set<String>> valueEntry : fieldEntry.getValue().entrySet()) {
                    String value = valueEntry.getKey();
                    String docs = String.join(",", valueEntry.getValue());
                    String line = field + "|" + value + "|" + docs;
                    writer.write(line);
                    writer.newLine();
                }
            }
        }
        System.out.println("Multilistas (metadatos) guardadas en: " + metaPath.toAbsolutePath());
    }
    public void loadIndexFromDisk(Path indexPath) throws IOException {
        invertedIndex.clear();
        if (!Files.exists(indexPath)) {
            System.out.println("No existe el archivo de índice en: " + indexPath.toAbsolutePath());
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(indexPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length != 2) continue;
                String term = parts[0];
                String[] docs = parts[1].split(",");
                Set<String> set = invertedIndex.computeIfAbsent(term, t -> new HashSet<>());
                for (String d : docs) {
                    if (!d.isEmpty()) set.add(d);
                }
            }
        }
        System.out.println("Índice invertido cargado desde disco. Términos: " + invertedIndex.size());
    }
    public void loadMetaIndexFromDisk(Path metaPath) throws IOException {
        metaIndex.clear();
        if (!Files.exists(metaPath)) {
            System.out.println("No existe el archivo de metadatos en: " + metaPath.toAbsolutePath());
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(metaPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length != 3) continue;
                String field = parts[0];
                String value = parts[1];
                String[] docs = parts[2].split(",");
                Map<String, Set<String>> fieldMap = metaIndex.computeIfAbsent(field, f -> new HashMap<>());
                Set<String> set = fieldMap.computeIfAbsent(value, v -> new HashSet<>());
                for (String d : docs) {
                    if (!d.isEmpty()) set.add(d);
                }
            }
        }
        System.out.println("Metadatos cargados desde disco. Campos: " + metaIndex.size());
    }

    public Set<String> searchByTerms(List<String> terms) {
        Set<String> results = new HashSet<>();
        for (String termRaw : terms) {
            String term = termRaw.toLowerCase();
            Set<String> docs = invertedIndex.get(term);
            if (docs != null) results.addAll(docs);
        }
        return results;
    }

    public Set<String> searchAnd(List<String> terms) {
        List<Set<String>> lists = new ArrayList<>();
        for (String termRaw : terms) {
            String term = termRaw.toLowerCase();
            Set<String> docs = invertedIndex.getOrDefault(term, Collections.emptySet());
            lists.add(new HashSet<>(docs));
        }
        if (lists.isEmpty()) return Collections.emptySet();
        lists.sort(Comparator.comparingInt(Set::size));
        Set<String> inter = new HashSet<>(lists.get(0));
        for (int i = 1; i < lists.size(); i++) {
            inter.retainAll(lists.get(i));
            if (inter.isEmpty()) break;
        }
        return inter;
    }

    public Set<String> searchByMeta(String field, String value) {
        Map<String, Set<String>> fieldMap = metaIndex.get(field.toLowerCase());
        if (fieldMap == null) return Collections.emptySet();
        return new HashSet<>(fieldMap.getOrDefault(value, Collections.emptySet()));
    }

    public Set<String> searchCombined(List<String> terms, String metaField, String metaValue) {
        Set<String> termResults = searchByTerms(terms);
        Set<String> metaResults = searchByMeta(metaField, metaValue);
        termResults.retainAll(metaResults);
        return termResults;
    }

    public void printTopTerms(int n) {
        List<Map.Entry<String, Set<String>>> list = new ArrayList<>(invertedIndex.entrySet());
        list.sort((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()));
        int limit = Math.min(n, list.size());
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Set<String>> e = list.get(i);
            System.out.printf("%2d. %-15s -> %d docs%n", i + 1, e.getKey(), e.getValue().size());
        }
    }
}
