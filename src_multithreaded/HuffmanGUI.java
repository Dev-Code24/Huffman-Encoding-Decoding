import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class HuffmanGUI extends JFrame {
    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;
    private JButton loadInputButton;
    private JButton selectOutputDirButton;
    private JButton encodeButton;
    private JButton decodeButton;
    private List<File> selectedFiles;
    private File outputDir;

    public HuffmanGUI() {
        setTitle("Huffman Encoder/Decoder");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        JScrollPane fileListScrollPane = new JScrollPane(fileList);

        loadInputButton = new JButton("Load Input");
        selectOutputDirButton = new JButton("Select Output Directory");
        encodeButton = new JButton("Encode");
        decodeButton = new JButton("Decode");
        encodeButton.setEnabled(false); // Initially disable encode button
        decodeButton.setEnabled(false); // Initially disable decode button

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(loadInputButton);
        buttonPanel.add(selectOutputDirButton);
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(fileListScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        selectedFiles = new ArrayList<>();

        addListeners();
    }

    private void addListeners() {
        loadInputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                int result = fileChooser.showOpenDialog(HuffmanGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    selectedFiles.clear();
                    fileListModel.clear();
                    for (File file : files) {
                        selectedFiles.add(file);
                        fileListModel.addElement(file.getName() + " - " + file.getAbsolutePath());
                    }
                    encodeButton.setEnabled(outputDir != null); // Enable encode button if output directory is selected
                }
            }
        });

        selectOutputDirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser dirChooser = new JFileChooser();
                dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = dirChooser.showOpenDialog(HuffmanGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputDir = dirChooser.getSelectedFile();
                    encodeButton.setEnabled(!selectedFiles.isEmpty()); // Enable encode button if files are selected
                }
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (File file : selectedFiles) {
                    Thread encodeThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                String input = new String(Files.readAllBytes(file.toPath()));
                                int chunkSize = 10 * 1024 * 1024; // 10 MB
                                List<String> chunks = new ArrayList<>();
                                for (int start = 0; start < input.length(); start += chunkSize) {
                                    chunks.add(input.substring(start, Math.min(input.length(), start + chunkSize)));
                                }

                                HuffmanEncoder encoder = new HuffmanEncoder();
                                encoder.buildHuffmanTree(input);
                                Path outputFile = outputDir.toPath().resolve(file.getName() + "_encoded.bin");
                                try (BufferedOutputStream bos = new BufferedOutputStream(
                                        new FileOutputStream(outputFile.toFile()))) {
                                    for (String chunk : chunks) {
                                        byte[] encodedData = encoder.encode(chunk);
                                        bos.write(encodedData);
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    encodeThread.start();
                }
                decodeButton.setEnabled(true);
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (File file : selectedFiles) {
                    Thread decodeThread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Path encodedFilePath = outputDir.toPath().resolve(file.getName() + "_encoded.bin");
                                byte[] encodedData = Files.readAllBytes(encodedFilePath);

                                // Rebuild the Huffman tree from the original input
                                String input = new String(Files.readAllBytes(file.toPath()));
                                HuffmanEncoder encoder = new HuffmanEncoder();
                                encoder.buildHuffmanTree(input);
                                HuffmanDecoder decoder = new HuffmanDecoder(encoder.getRoot());

                                // Decode the data in chunks
                                int chunkSize = 10 * 1024 * 1024; // 10 MB
                                int start = 0;
                                Path outputFile = outputDir.toPath().resolve(file.getName() + "_decoded.txt");
                                try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
                                    while (start < encodedData.length) {
                                        int end = Math.min(encodedData.length, start + chunkSize);
                                        byte[] chunk = new byte[end - start];
                                        System.arraycopy(encodedData, start, chunk, 0, end - start);
                                        String decodedText = decoder.decode(chunk);
                                        writer.write(decodedText);
                                        start = end;
                                    }
                                }
                                System.out.println("Decoded file written to: " + outputFile); 
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    decodeThread.start();
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
