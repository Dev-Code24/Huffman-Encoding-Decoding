public class HuffmanDecoder {
    private Node root;

    public HuffmanDecoder(Node root) {
        this.root = root;
    }

    public String decode(byte[] encodedBytes) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : encodedBytes) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }

        StringBuilder decodedString = new StringBuilder();
        Node currentNode = root;
        for (int i = 0; i < binaryString.length(); i++) {
            char bit = binaryString.charAt(i);
            if (bit == '0') {
                if (currentNode.left != null) {
                    currentNode = currentNode.left;
                } else {
                    currentNode = root;
                }
            } else {
                if (currentNode.right != null) {
                    currentNode = currentNode.right;
                } else {
                    currentNode = root;
                }
            }
            if (currentNode.left == null && currentNode.right == null) {
                decodedString.append(currentNode.character);
                currentNode = root;
            }
        }
        return decodedString.toString();
    }
}
