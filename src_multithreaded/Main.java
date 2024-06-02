import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            Path inputDir = Paths.get("input");
            Path outputDir = Paths.get("output");
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }
            Files.list(inputDir).forEach(file -> {
                try {
                    String input = new String(Files.readAllBytes(file));
                    int chunkSize = 10 * 1024 * 1024; // 10 MB
                    List<String> chunks = new ArrayList<>();
                    for (int start = 0; start < input.length(); start += chunkSize) {
                        chunks.add(input.substring(start, Math.min(input.length(), start + chunkSize)));
                    }
                    
                    // Encode each chunk separately
                    HuffmanEncoder encoder = new HuffmanEncoder();
                    encoder.buildHuffmanTree(input);
                    List<byte[]> encodedChunks = new ArrayList<>();
                    for (int i = 0; i < chunks.size(); i++) {
                        String chunk = chunks.get(i);
                        byte[] encodedData = encoder.encode(chunk);
                        encodedChunks.add(encodedData);
                        System.out.println("Encoded chunk " + i + ": " + new String(encodedData)); // Debugging
                    }

                    // Write encoded chunks to output files
                    for (int i = 0; i < encodedChunks.size(); i++) {
                        byte[] encodedData = encodedChunks.get(i);
                        Path outputFile = outputDir.resolve(file.getFileName() + "_chunk" + i + "_encoded.bin");
                        Files.write(outputFile, encodedData);
                        System.out.println("Encoded chunk " + i + " written to file: " + outputFile); // Debugging
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
