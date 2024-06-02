import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class HuffmanGUI extends JFrame {
    private HuffmanEncoder encoder;
    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;
    private JButton loadInputButton;
    private JButton selectOutputDirButton;
    private JButton encodeButton;
    private JButton decodeButton;
    private List<File> selectedFiles;
    private File outputDir;

    public HuffmanGUI() {
        encoder = new HuffmanEncoder();
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
                    JOptionPane.showMessageDialog(HuffmanGUI.this,
                            "Output directory selected: " + outputDir.getAbsolutePath());
                    encodeButton.setEnabled(!selectedFiles.isEmpty()); // Enable encode button if files are selected
                }
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Iterate over selected files
                for (File file : selectedFiles) {
                    try {
                        // Read file content using InputStream
                        try (InputStream inputStream = new FileInputStream(file)) {
                            byte[] inputBytes = new byte[(int) file.length()];
                            inputStream.read(inputBytes);

                            // Encode file content
                            encoder.buildHuffmanTree(new String(inputBytes));
                            byte[] encodedBytes = encoder.encode(new String(inputBytes));

                            // Write encoded data to output file
                            try (OutputStream outputStream = new FileOutputStream(
                                    outputDir.toPath().resolve(file.getName() + ".bin").toFile())) {
                                outputStream.write(encodedBytes);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(HuffmanGUI.this, "Encoding completed for selected files.");
                decodeButton.setEnabled(true); // Enable decode button after encoding
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Iterate over selected files
                for (File file : selectedFiles) {
                    try {
                        // Read encoded file content
                        byte[] encodedBytes = Files.readAllBytes(outputDir.toPath().resolve(file.getName() + ".bin"));

                        // Decode the encoded data
                        HuffmanDecoder decoder = new HuffmanDecoder(encoder.getRoot());
                        String decodedText = decoder.decode(encodedBytes);

                        // Write decoded data to output file
                        try (Writer writer = new FileWriter(
                                outputDir.toPath().resolve(file.getName() + "_decoded.txt").toFile())) {
                            writer.write(decodedText);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(HuffmanGUI.this, "Decoding completed for selected files.");
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
