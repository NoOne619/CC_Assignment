package Lexer;

import java.util.*;

abstract class RegexNode { }

class LiteralNode extends RegexNode {
    char literal;
    public LiteralNode(char literal) {
        this.literal = literal;
    }
}

class ConcatNode extends RegexNode {
    List<RegexNode> nodes = new ArrayList<>();
    public ConcatNode(List<RegexNode> nodes) {
        this.nodes = nodes;
    }
}

class UnionNode extends RegexNode {
    List<RegexNode> alternatives = new ArrayList<>();
    public UnionNode(List<RegexNode> alternatives) {
        this.alternatives = alternatives;
    }
}

class StarNode extends RegexNode {
    RegexNode node;
    public StarNode(RegexNode node) {
        this.node = node;
    }
}

class PlusNode extends RegexNode {
    RegexNode node;
    public PlusNode(RegexNode node) {
        this.node = node;
    }
}

class CharClassNode extends RegexNode {
    Set<Character> characters = new HashSet<>();
    public CharClassNode(Set<Character> characters) {
        this.characters = characters;
    }
}