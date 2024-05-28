import java.nio.file.*;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class Main {
    public static void main(String[] args) {
        try {
            // Ensure the output directory exists
            Path outputDir = Paths.get("../output");
            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }

            // Read input text from a file
            String inputText = new String(Files.readAllBytes(Paths.get("../input.txt")));

            // Encode the text using Huffman coding
            HuffmanEncoder encoder = new HuffmanEncoder();
            encoder.buildHuffmanTree(inputText);
            byte[] encodedBytes = encoder.encode(inputText);
            Files.write(outputDir.resolve("encoded.txt"), encodedBytes);
            Files.write(outputDir.resolve("encoded.bin"), encodedBytes);

            // Decode the encoded text
            HuffmanDecoder decoder = new HuffmanDecoder(encoder.getRoot());
            String decodedText = decoder.decode(encodedBytes);
            Files.write(outputDir.resolve("decoded.txt"), decodedText.getBytes());

            // Print results
            System.out.println("Original text: " + inputText);
            System.out.println("Encoded bytes length: " + encodedBytes.length);
            System.out.println("Decoded text: " + decodedText);

        } catch (IOException e) {
            if (e instanceof NoSuchFileException) {
                System.out.println("The specified file was not found: " + e.getMessage());
            } else {
                e.printStackTrace();
            }
        }
    }
}
