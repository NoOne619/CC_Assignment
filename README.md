# Complex (.cmx) Programming Language - Lexical Analyzer

## Overview
This repository contains the implementation of a lexical analyzer for a custom programming language. The lexer is built using:
- **Thompson's Construction** to convert regular expressions into NFAs.
- **Subset Construction (NFA to DFA conversion)** for efficient token recognition.
- **Symbol Table Management** to track identifiers and reserved keywords.

The lexer scans source code, identifies tokens, and outputs their types along with symbol table information.

---

## Syntax and Constraints
### Data Types
- `int` - Represents whole numbers.
- `float` - Represents decimal numbers.
- `bool` - Can only hold `true` or `false`.
- `char` - Stores a single character.

**Constraint:** `int` type cannot store the ASCII value of a `char`.

### Variable Naming Rules
- Variables must follow the regex: `[a-z]+`
  - Must start with a lowercase letter.
  - Example: `var`, `hello`, `abc` (✅ Valid)
  - Example: `1var`, `Hello`, `_var` (❌ Invalid)

### Values
- **Integer Numbers:** `10, -42, 0`
- **Float Numbers:** `3.14, -0.99`
- **Boolean:** `1, 0`
- **Character:** `'a'`, `'z'`

### Comments
- **Single-line comments**: Start with `#`.
- **Multi-line comments**: Enclosed between `@...@`.
  - Example:
    ```
    # This is a single-line comment
    @
    This is a multi-line comment
    @
    ```

### Operators
- **Arithmetic Operators:** `+`, `-`, `*`, `/`, `%`, '^'
- **Logical Operators:** (not defined yet)
- **Comparison Operators:** (not defined yet)

### Reserved Keywords
- `global` - Declares a global variable.
- `local` - Declares a local variable.

**Constraint:** No user-defined function mechanism exists (no `main` function needed).

### Scope Rules
- **All variables are global by default** unless explicitly marked as `local`.

### Lexer Functionality
1. Tokenizes input source code.
2. Stores variables in the **symbol table** (name, line number, value, type, scope).
3. Detects errors in lexical structure (invalid characters, malformed numbers, etc.).

---

## Implementation Details
- **ThompsonConstruction**: Converts regex to NFA.
- **DFAConverter**: Converts NFA to DFA.
- **DFAStore**: Stores precomputed DFAs for different token types.
- **Lexer**: Uses DFAs to recognize tokens and output symbol table information.

---

## How to Use
### Running the Lexer
```sh
javac *.java
java MainClassName < input.txt
```
This will process `input.txt` and output recognized tokens and symbol table entries.

### Example Input
```sh
int x = 10;
char c = 'a';
float pi = 3.14;
```

### Expected Token Output
```
TOKEN: int, TYPE: Keyword
TOKEN: x, TYPE: Identifier
TOKEN: =, TYPE: Operator
TOKEN: 10, TYPE: Integer
TOKEN: ;, TYPE: Delimiter
TOKEN: char, TYPE: Keyword
TOKEN: c, TYPE: Identifier
...
```

---

## Future Improvements
- **Syntax Parser**: Implement a parser using DFA-based parsing.
- **Error Handling**: Provide detailed error messages with line numbers.
- **Operator Expansion**: Add logical and comparison operators.

---

## License
This project is licensed under the MIT License.

