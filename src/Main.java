import java.nio.file.*;
import java.io.IOException;

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
                    HuffmanEncoder encoder = new HuffmanEncoder();
                    encoder.buildHuffmanTree(input);
                    byte[] encodedData = encoder.encode(input);
                    Files.write(outputDir.resolve(file.getFileName() + "_encoded.txt"), encodedData);
                    System.out.println("Encoding completed for: " + file.getFileName());

                    HuffmanDecoder decoder = new HuffmanDecoder(encoder.getRoot());
                    String decodedText = decoder.decode(encodedData);
                    Files.write(outputDir.resolve(file.getFileName() + "_decoded.txt"), decodedText.getBytes());
                    System.out.println("Decoding completed for: " + file.getFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}