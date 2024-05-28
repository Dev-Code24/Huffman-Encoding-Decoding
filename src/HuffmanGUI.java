import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.*;
import java.io.IOException;

public class HuffmanGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton loadButton;
    private JButton saveButton;

    public HuffmanGUI() {
        setTitle("Huffman Encoder/Decoder");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inputTextArea = new JTextArea();
        outputTextArea = new JTextArea();
        encodeButton = new JButton("Encode");
        decodeButton = new JButton("Decode");
        loadButton = new JButton("Load Input");
        saveButton = new JButton("Save Output");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(new JScrollPane(inputTextArea));
        panel.add(new JScrollPane(outputTextArea));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(loadButton);
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(saveButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addListeners();
    }

    private void addListeners() {
        encodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String inputText = inputTextArea.getText();
                    HuffmanEncoder encoder = new HuffmanEncoder();
                    encoder.buildHuffmanTree(inputText);
                    byte[] encodedBytes = encoder.encode(inputText);

                    // Save the encoded data to a file
                    Path outputDir = Paths.get("output");
                    if (!Files.exists(outputDir)) {
                        Files.createDirectory(outputDir);
                    }
                    Files.write(outputDir.resolve("encoded.bin"), encodedBytes);

                    outputTextArea.setText("Encoded data saved to encoded.bin");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Read encoded data from the file
                    Path encodedFilePath = Paths.get("output/encoded.bin");
                    byte[] encodedBytes = Files.readAllBytes(encodedFilePath);

                    // Decode the encoded data
                    HuffmanEncoder encoder = new HuffmanEncoder();
                    String inputText = inputTextArea.getText();
                    encoder.buildHuffmanTree(inputText); // Build tree with the same input
                    HuffmanDecoder decoder = new HuffmanDecoder(encoder.getRoot());
                    String decodedText = decoder.decode(encodedBytes);

                    outputTextArea.setText(decodedText);

                    // Save the decoded text to a file
                    Path outputDir = Paths.get("output");
                    Files.write(outputDir.resolve("decoded.txt"), decodedText.getBytes());

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(HuffmanGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        Path filePath = fileChooser.getSelectedFile().toPath();
                        String content = new String(Files.readAllBytes(filePath));
                        inputTextArea.setText(content);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(HuffmanGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        Path filePath = fileChooser.getSelectedFile().toPath();
                        String content = outputTextArea.getText();
                        Files.write(filePath, content.getBytes());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HuffmanGUI().setVisible(true);
            }
        });
    }
}
