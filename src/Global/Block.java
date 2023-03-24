package Global;

public class Block {
    private final String id;
    private final Block parent;

    public Block(String id, Block parent) {
        this.id = id;
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    public Block getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Block block)) {
            return false;
        }
        return id.equals(block.id);
    }
}
