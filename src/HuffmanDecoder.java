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
            currentNode = (bit == '0') ? currentNode.left : currentNode.right;
            if (currentNode.left == null && currentNode.right == null) {
                decodedString.append(currentNode.character);
                currentNode = root;
            }
        }
        return decodedString.toString();
    }
}
