package Parser;

import Symbols.SymbolTable;

public class Block {
    private final SymbolTable symbolTable;
    private final Block parent;

    public Block(Block parent) {
        this.parent = parent;
        this.symbolTable = parent.symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public Block getParent() {
        return parent;
    }
}
