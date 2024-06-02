import java.util.*;

public class HuffmanEncoder {
    private Map<Character, String> huffmanCode;
    private PriorityQueue<Node> priorityQueue;

    public HuffmanEncoder() {
        huffmanCode = new HashMap<>();
        priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));
    }

    public void buildHuffmanTree(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char ch : text.toCharArray()) {
            frequencyMap.put(ch, frequencyMap.getOrDefault(ch, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            priorityQueue.offer(new Node(entry.getKey(), entry.getValue()));
        }

        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node sum = new Node('\0', left.frequency + right.frequency);
            sum.left = left;
            sum.right = right;
            priorityQueue.offer(sum);
        }

        generateHuffmanCodes(priorityQueue.peek(), "");
    }

    private void generateHuffmanCodes(Node root, String code) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.character, code);
            System.out.println("Character: " + root.character + ", Code: " + code); // Debugging
        }
        generateHuffmanCodes(root.left, code + '0');
        generateHuffmanCodes(root.right, code + '1');
    }

    public byte[] encode(String text) {
        StringBuilder encodedString = new StringBuilder();
        for (char ch : text.toCharArray()) {
            encodedString.append(huffmanCode.get(ch));
        }
      //  System.out.println("Encoded String: " + encodedString.toString()); // Debugging
        return getBytesFromBinaryString(encodedString.toString());
    }

    public Node getRoot() {
        return priorityQueue.peek();
    }

    public Map<Character, String> getHuffmanCode() {
        return huffmanCode;
    }

    private byte[] getBytesFromBinaryString(String binaryString) {
        int byteLength = (binaryString.length() + 7) / 8;
        byte[] result = new byte[byteLength];

        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '1') {
                result[i / 8] |= 1 << (7 - (i % 8));
            }
        }

        return result;
    }
}
