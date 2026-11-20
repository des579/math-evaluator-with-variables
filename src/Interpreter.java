import java.util.ArrayList;
import java.util.Hashtable;

public class Interpreter {
    private Lexer lexer = new Lexer();
    private Evaluator evaluator = new Evaluator();

    public Double input(String input) {
        ArrayList<Token> tokens = this.lexer.tokenize(input);

        if (lexer.hasError) {
            for (String error : this.lexer.errors) {
                System.out.println("LEXER ERROR: " + error);
            }
            return null;
        }


        Double r = this.evaluator.parse(tokens);
        if (this.evaluator.hasError) {
            for (String error : this.evaluator.errors) {
                System.out.println("EVALUATOR ERROR: " + error);
            }
            return null;
        }
        return r;
    }
}

class Evaluator {
    private ArrayList<Token> tokens;
    private int posCurrent = 0;
    private Hashtable<String, Token> variables = new Hashtable<>();

    public boolean hasError = false;
    public ArrayList<String> errors = new ArrayList<String>();

    public Double parse(ArrayList<Token> tokens) {
        this.reset();
        this.tokens = tokens;

        return this.expr();
    }

    private Double expr() {
        if (this.isAssign())
            return this.handleAssign();
        
        return this.eval();
    }

    private Double handleAssign() {
//         System.out.println("hadling assign");
        Token name = this.peek();
        this.posCurrent += 2; // skip both identifier and '='
        Double val = this.eval();
//         System.out.println("val: " + val);
        if (val == null)
            return null;

        this.variables.put(name.literal, new Token(Double.toString(val), TokenType.NUMBER_FLOAT));
//         System.out.println(this.variables.get(name.literal));
        return val;
    }

    private Double eval() {
        Double l = this.factor();
        if (l == null)
            return null;
        Double result = l;
        while (this.match(false, TokenType.PLUS, TokenType.MINUS)) {
            Token op = this.peek();
            this.posCurrent++;
            Double right = this.factor();
            if (right == null)
                return null;
            if (op.type == TokenType.PLUS)
                result += right;
            else
                result -= right;
        }
        return result;
    }

    private Double factor() {
        Double l = this.prim();
        if (l == null)
            return null;
        Double result = l;
        while (this.match(false, TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token op = this.peek();
            this.posCurrent++;
            Double right = this.prim();
            if (right == null)
                return null;
            if (op.type == TokenType.STAR)
                result *= right;
            else if (op.type == TokenType.SLASH)
                result /= right;
            else 
                result %= right;
        }

        return result;
    }

    private Double prim() {
        if (this.hasError)
            return null;

        if (this.match(false, TokenType.NUMBER_INT, TokenType.NUMBER_FLOAT)) {
            Token t = this.peek();
            this.posCurrent++;
            return new Double(Double.parseDouble(t.literal));
        }
        else if (this.match(true, TokenType.MINUS))
            return new Double(-1 * this.prim().doubleValue());
        else if (this.match(true, TokenType.LP)) {
            Double d = this.eval();
            if (!this.match(true, TokenType.RP)) {
                this.createError(TokenType.RP + " expected");
                return null;
            }
            return d;
        }
        else if (this.match(false, TokenType.IDENTIFIER)) {
            Token i = this.peek();
//             System.out.println("TokenType.IDENTIFIER in prim " + i.type + " " + "'" + i.literal + "'");
            Token t = this.variables.get(i.literal);
//             System.out.println("this.variables.size: " + this.variables.size());
            if (t == null) {
                this.createError("input '" + i.literal + "'");
                return null;
            }
            this.posCurrent++;
            return new Double(Double.parseDouble(t.literal));
        }
        this.createError("Unexpected token");
        return null;
    }


    private boolean isAssign() {
//         System.out.println("isAssign called");
        // first move the posCurrent
        this.posCurrent++;
        if (this.match(false, TokenType.EQUAL)) {
            this.posCurrent--;
            return true;
        }
        this.posCurrent--;
        return false;
    }

    private void reset() {
        this.hasError = false;
        this.errors.clear();
        this.posCurrent = 0;
    }


    private void createError(String error) {
        this.hasError = true;
        this.errors.add(error);
    }

    private boolean match(boolean consumeToken, TokenType ...types) {
        if (this.hasError)
            return false;
        if (this.isAtEnd())
            return false;
        Token t = this.peek();
        for (TokenType type : types) {
            if (t.type != type) continue;
            if (consumeToken)
                this.posCurrent++;
            return true;
        }

        return false;
    }

    private Token peek() {
        if (this.isAtEnd()) return null;
        return this.tokens.get(this.posCurrent);
    }

    private boolean isAtEnd() {
        return this.posCurrent >= this.tokens.size();
    }
}


class Lexer {
    private String data;
    private int posStart;
    private int posCurrent;
    private ArrayList<Token> tokens = new ArrayList<Token>();
    
    public boolean hasError = false;
    public ArrayList<String> errors = new ArrayList<String>();

    public ArrayList<Token> tokenize(String data) {
        this.reset();
        this.data = data;

        while (!this.isAtEnd()) {
            this.posStart = this.posCurrent;
            char ch = this.advance();
            this.handleCh(ch);
        }
        this.addToken("", TokenType.END);
        return tokens;
    }

    private void reset() {
        this.posStart = 0;
        this.posCurrent = 0;
        this.tokens.clear();

        this.hasError = false;
        this.errors.clear();
    }

    private void handleCh(char ch) {
        switch (ch) {
            case ' ':
                break;
            case '(':
                this.addToken("", TokenType.LP);
                break;
            case ')':
                this.addToken("", TokenType.RP);
                break;
            case '+':
                this.addToken("", TokenType.PLUS);
                break;
            case '-':
                this.addToken("", TokenType.MINUS);
                break;
            case '*':
                this.addToken("", TokenType.STAR);
                break;
            case '/':
                this.addToken("", TokenType.SLASH);
                break;
            case '%':
                this.addToken("", TokenType.PERCENT);
                break;
            case '=':
                this.addToken("", TokenType.EQUAL);
                break;
            default:
                if (Character.isDigit(ch)) {
                    this.handleNumber();
                }
                else if (Character.isAlphabetic(ch) || ch == '_')
                    this.handleIdentifier();
                else
                    this.createError("Unexpected character '" + ch + "'");
                break;
        }
    }

    private void createError(String error) {
        this.hasError = true;
        this.errors.add(error);
    }

    private void handleIdentifier() {
        char ch = this.peek();
        while (Character.isAlphabetic(ch) || ch == '_' || Character.isDigit(ch)) {
            this.advance();
            ch = this.peek();
        }
        String name = this.data.substring(this.posStart, this.posCurrent);
        this.addToken(name, TokenType.IDENTIFIER);
    }

    private void handleNumber() {
        int dots = 0;
        while (Character.isDigit(this.peek()) || this.peek() == '.') {
            if (this.peek() == '.')
                dots++;
            if (dots > 1)
                break;
            this.advance();
        }

        String number = this.data.substring(this.posStart, this.posCurrent);
        if (dots == 1 && number.charAt(number.length() - 1) == '.')
            this.createError("Digit expected after '.'");
        if (dots == 0)
            this.addToken(number, TokenType.NUMBER_INT);
        else 
            this.addToken(number, TokenType.NUMBER_FLOAT);
    }

    private void addToken(String literal, TokenType type) {
        this.tokens.add(new Token(literal, type));
    }

    private char advance() {
        if (this.isAtEnd()) 
            return '\0';
        this.posCurrent++;
        return this.data.charAt(this.posCurrent - 1);
    }

    private boolean isAtEnd() {
        return this.posCurrent >= this.data.length();
    }

    private char peek() {
        if (this.isAtEnd()) 
            return '\0';
        return this.data.charAt(this.posCurrent);
    }
}

class Token {
    public TokenType type;
    public String literal;

    public Token(String literal, TokenType type) {
        this.type = type;
        this.literal = literal;
    }
}


enum TokenType {
    NUMBER_INT,
    NUMBER_FLOAT,

    // ( )
    LP,
    RP,

    // operators
    PLUS,
    MINUS,
    STAR,
    SLASH,
    PERCENT,

    // is not an operator, it is used in assignment
    EQUAL,

    IDENTIFIER,
    END,
}

