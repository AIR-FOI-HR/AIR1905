package hr.foi.air.herbert.engine.parser;

public class InputCode {
    public class StringWrapper{
        private String string;
        public StringWrapper(String s) {
            this.string = s;
        }
        public String getString() {
            return this.string;
        }
        public void setString(String s) {
            this.string = s;
        }
    }
    private String input;
    private String herbertCode;
    private int inputLen;
    private Lex lex = new Lex();
    public InputCode(){
        input = new String();
        herbertCode = new String();
        inputLen = 0;
    }
    public String getMoves(String code) throws Lex.HerbertException, Lex.LexerException {
        input = code + "\n";
        herbertCode = input.replaceAll("[ \t\r]", "").replaceAll("\n{2,}", "\n");
        //herbertCode=input.replaceAll("[^\\p{Graph}]", "").replaceAll("\n{2,}", "\n");
        StringWrapper wrapper = new StringWrapper(herbertCode);
        lex.tokenizer(wrapper);
        herbertCode = wrapper.getString();
        inputLen = herbertCode.length();
        lex.syntax(herbertCode);
        lex.fillHashMaps(herbertCode);
        lex.semantic();
        lex.Parse();
        return lex.getCode();
    }
    public int getInputLen(){
        return inputLen;
    }
}