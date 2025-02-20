package Lexer;

import java.util.HashMap;
import java.util.Map;


import java.util.HashMap;
import java.util.Map;

public class DFAStore {
    private static final Map<String, DFA> dfaMap = new HashMap<>();

    static {
        // DFA for Identifiers (variable names)
        
        NFA identifierNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[a-z]+").parse());
        ThompsonConstruction.printTransitionTable(identifierNFA);
        DFA identifierDFA = DFAConverter.convertNFAtoDFA(identifierNFA);
        dfaMap.put("IDENTIFIER", identifierDFA);

        // DFA for Whole Numbers (e.g., integers)
        NFA wholeNumberNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[0-9]+").parse());
        ThompsonConstruction.printTransitionTable(wholeNumberNFA);
        DFA wholeNumberDFA = DFAConverter.convertNFAtoDFA(wholeNumberNFA);
        dfaMap.put("WHOLE_NUMBER", wholeNumberDFA);

        // DFA for Decimal Numbers (e.g., floats)
        NFA decimalNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[0-9]+(\\.[0-9]+)*").parse());
        ThompsonConstruction.printTransitionTable(decimalNFA);
        DFA decimalDFA = DFAConverter.convertNFAtoDFA(decimalNFA);
        dfaMap.put("DECIMAL", decimalDFA);

        // DFA for Keywords (e.g., int, float, global, local, char, bool)
        NFA keywordNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("int|float|global|char|bool").parse());
        ThompsonConstruction.printTransitionTable(keywordNFA);
        DFA keywordDFA = DFAConverter.convertNFAtoDFA(keywordNFA);
        dfaMap.put("KEYWORD", keywordDFA);

        // DFA for Char Input (e.g., single character inputs)
        NFA charNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("'[a-z0-9]'"
).parse());
        ThompsonConstruction.printTransitionTable(charNFA);
        DFA charDFA = DFAConverter.convertNFAtoDFA(charNFA);
        dfaMap.put("CHAR", charDFA);

        // DFA for Operators (e.g., %, +, -, @, =)
        NFA operatorNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[%+\\-@=*^]").parse());
        ThompsonConstruction.printTransitionTable(operatorNFA);
        DFA operatorDFA = DFAConverter.convertNFAtoDFA(operatorNFA);
        dfaMap.put("OPERATOR", operatorDFA);

        // DFA for Single Line Comment (#)
        NFA singleLineCommentNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("#[a-z]*").parse());
        DFA singleLineCommentDFA = DFAConverter.convertNFAtoDFA(singleLineCommentNFA);
        dfaMap.put("SINGLE_LINE_COMMENT", singleLineCommentDFA);

        // DFA for Multi-Line Comments (@...@)
        NFA multiLineCommentNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("@[a-zA-Z0-9]*@").parse());
        DFA multiLineCommentDFA = DFAConverter.convertNFAtoDFA(multiLineCommentNFA);
        dfaMap.put("MULTI_LINE_COMMENT", multiLineCommentDFA);
        // DFA for Boolean Values (0 or 1)
        NFA booleanNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("0|1").parse());
        ThompsonConstruction.printTransitionTable(booleanNFA);
        DFA booleanDFA = DFAConverter.convertNFAtoDFA(booleanNFA);
        dfaMap.put("BOOLEAN", booleanDFA);

        NFA inputNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("input|output").parse());
        ThompsonConstruction.printTransitionTable(inputNFA);
        DFA inputDFA = DFAConverter.convertNFAtoDFA(inputNFA);
        dfaMap.put("INPUT/OUTPUT", inputDFA);
        
        NFA anystringNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("\"[^\"]*\"").parse());
        ThompsonConstruction.printTransitionTable(anystringNFA);
        DFA anystringDFA = DFAConverter.convertNFAtoDFA(anystringNFA);
        dfaMap.put("String", anystringDFA);
        
        // DFA for Semicolon (;)
        NFA semicolonNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[;()]").parse());
        ThompsonConstruction.printTransitionTable(semicolonNFA);
        DFA semicolonDFA = DFAConverter.convertNFAtoDFA(semicolonNFA);
        dfaMap.put("SYMBOL", semicolonDFA);

        // DFA for Open Bracket ({)
        NFA openBracketNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[{]").parse());
       
        DFA openBracketDFA = DFAConverter.convertNFAtoDFA(openBracketNFA);
        dfaMap.put("OPEN_BRACKET", openBracketDFA);

        // DFA for Close Bracket (})
        NFA closeBracketNFA = ThompsonConstruction.buildNFAFromRegex(new Parser("[}]").parse());
        DFA closeBracketDFA = DFAConverter.convertNFAtoDFA(closeBracketNFA);
        dfaMap.put("CLOSE_BRACKET", closeBracketDFA);
    }

    public static DFA getDFA(String tokenType) {
        return dfaMap.get(tokenType);
    }
}
