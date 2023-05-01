package Nodes;

public class JsonTraversal {
    /**
     * Traverses an AST and represents the tree using JSON.
     *
     * @param node the root of the AST to be traversed.
     * @return a JSON string containing the AST's information.
     */
    public String traverse(Node node) {
        StringBuilder output = new StringBuilder();

        if (node != null) {
            output.append("{\n");
            output.append(node.toJson());
            output.append(",\n\"Children\": [\n");
            int numChildren = node.countChildren();
            for (int i = 0; i < numChildren; ++i) {
                // Preorder traversal
                traverse(node.getChild(i));
                if (i < numChildren - 1) {
                    output.append(",\n");
                }
            }
            output.append("\n]\n}\n");
        }

        return output.toString();
    }
}
