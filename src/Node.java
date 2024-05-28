public class Node {
    char character;
    int frequency;
    Node left;
    Node right;

    public Node(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }
}
