package hr.foi.air.herbert.engine.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

final public class Lex{
    public final int MAX = 5000;
    private Vector<Element> tokens;
    private enum Tokens {
        SLR,
        ID,
        VAR,
        INT,
        NL,
        OZAG,
        ZZAG,
        ZAREZ,
        DVOTOCKA,
        MINUS,
        UNKNOWN
    }
    private enum ExceptionStatus{
        SYNTAX,
        SEMANTIC,
        PARSER
    }
    private class Element{
        Tokens t;
        int poz;
        public Element(Tokens tok, int p){
            t = tok;
            poz = p;
        }
    }
    public void tokenizer(InputCode.StringWrapper wrapper) throws LexerException {
        String herbertCode = wrapper.getString();
        tokens.ensureCapacity(herbertCode.length());
        ArrayList<Integer> pozicije = new ArrayList<>();
        for (int i = 0; i < herbertCode.length(); i++){
            if (slr(herbertCode.charAt(i))){
                tokens.add(new Element(Tokens.SLR, i));
            }
            else if (var(herbertCode.charAt(i))){
                tokens.add(new Element(Tokens.VAR, i));
            }
            else if (id(herbertCode.charAt(i))){
                tokens.add(new Element(Tokens.ID, i));
            }
            else if (broj(herbertCode.charAt(i))){
                int j = i;
                boolean pom = true;
                i++;
                while (i < herbertCode.length() && pom){
                    if (broj(herbertCode.charAt(i)))
                        i++;
                    else
                        pom = false;
                }
                String broj = String.valueOf(Integer.parseInt(herbertCode.substring(j, i)));
                herbertCode = herbertCode.substring(0, j) + broj + herbertCode.substring(i, herbertCode.length());
                tokens.add(new Element(Tokens.INT, j));
                i = j + broj.length() - 1;
            }
            else if (nl(herbertCode.charAt(i))){
                tokens.add(new Element(Tokens.NL, i));
            }
            else if (herbertCode.charAt(i) == '('){
                tokens.add(new Element(Tokens.OZAG, i));
            }
            else if (herbertCode.charAt(i) == ')'){
                tokens.add(new Element(Tokens.ZZAG, i));
            }
            else if (herbertCode.charAt(i) == ','){
                tokens.add(new Element(Tokens.ZAREZ, i));
            }
            else if (herbertCode.charAt(i) == ':'){
                tokens.add(new Element(Tokens.DVOTOCKA, i));
            }
            else if (herbertCode.charAt(i) == '-'){
                tokens.add(new Element(Tokens.MINUS, i));
            }
            else {
                tokens.add(new Element(Tokens.UNKNOWN, i));
                pozicije.add(i);
            }
        }
        wrapper.setString(herbertCode);
        if (!pozicije.isEmpty()) {
            throw new LexerException(pozicije);
        }
    }
    public class HerbertException extends Exception {
        private ExceptionStatus exceptionStatus;
        public ExceptionStatus getExceptionStatus(){ return exceptionStatus; }
        private int pozicija;
        public int getPozicija() { return pozicija; }
        private String description;
        public String getDescription() { return description; }
        public HerbertException(ExceptionStatus status, int x, String name) {
            exceptionStatus = status;
            pozicija = x;
            description = name;
        }
    }
    public class LexerException extends Exception {
        private ArrayList<Integer> pozicije;
        public LexerException(ArrayList<Integer> poz){
            pozicije = new ArrayList<>(poz);
        }
        public ArrayList<Integer> getPozicije() { return pozicije; }
    }
    public Lex() {
        tokens = new Vector<>();
        zamjene = new HashMap<>();
        real = new HashMap<>();
        funkcije = new HashMap<>();
        //filip = new String();
        filip = new ArrayList<>();
        argument = new StringBuilder();
    }
    //LinkedList<Element> token;
    private HashMap<Character, String> zamjene;
    private HashMap<Character, String> real;
    private HashMap<Character, HashMap<Character, String>> funkcije;
    //String filip;
    private ArrayList<String> filip;
    private char key_g = 0;
    private int dubina = 0;
    private StringBuilder argument;

    private void f_funkcije(HashMap<Character, String> real_val, Character key) throws HerbertException {
        int i = 0;
        try {
            if (!real_val.get(funkcije.get(key).get('%').charAt(0)).isEmpty() && Integer.parseInt(real_val.get(funkcije.get(key).get('%').charAt(0))) <= 0){
                return;
            }
            for (; i < funkcije.get(key).get('$').length() && filip.get(dubina).length() <= MAX; i++){
                if (slr(funkcije.get(key).get('$').charAt(i))){
                    StringBuilder s = new StringBuilder();
                    s.append(funkcije.get(key).get('$').charAt(i));
                    filip.set(dubina, filip.get(dubina) + s);
                    //filip = filip.concat(new String(s));
                } else if (var(funkcije.get(key).get('$').charAt(i))){
                    filip.set(dubina, filip.get(dubina) + real_val.get(funkcije.get(key).get('$').charAt(i)));
                    //filip = filip.concat(real_val.get(funkcije.get(key).get('$').charAt(i)));
                } else if (id(funkcije.get(key).get('$').charAt(i))){
                    HashMap<Character, String> val = new HashMap<>();
                    if ((i + 1 < funkcije.get(key).get('$').length()) && funkcije.get(key).get('$').charAt(i + 1) == '(') {
                        Character k = funkcije.get(key).get('$').charAt(i);
                        if (funkcije.get(k).get('%').length() == 1){
                            if (funkcije.get(key).get('$').charAt(i + 2) == ')') {
                                val.put(funkcije.get(k).get('%').charAt(0), new String());
                                i = i + 2;
                            } else {
                                int p = funkcije.get(key).get('$').indexOf(')', i + 2);
                                String prvi = funkcije.get(key).get('$').substring(i + 2, p);
                                if (prvi.indexOf('-') != -1) {
                                    if (real_val.containsKey(funkcije.get(k).get('%').charAt(0)) && real_val.get(funkcije.get(k).get('%').charAt(0)).isEmpty()){
                                        val.put(funkcije.get(k).get('%').charAt(0), new String());
                                    }
                                    else {
                                        prvi = prvi.replaceAll(String.valueOf(funkcije.get(key).get('%').charAt(0)), real_val.get(funkcije.get(key).get('%').charAt(0)));
                                        val.put(funkcije.get(k).get('%').charAt(0), String.valueOf(Character.getNumericValue(prvi.charAt(0)) - Character.getNumericValue(prvi.charAt(2))));
                                    }
                                } else
                                    val.put(funkcije.get(k).get('%').charAt(0), prvi);
                                i = p;
                            }
                        }
                        else {
                            if (funkcije.get(key).get('$').charAt(i + 2) == ',') {
                                val.put(funkcije.get(k).get('%').charAt(0), new String());
                                i = i + 3;
                            }
                            else {
                                int p = funkcije.get(key).get('$').indexOf(',', i + 2);
                                String prvi = funkcije.get(key).get('$').substring(i + 2, p);
                                if (prvi.indexOf('-') != -1) {
                                    if (real_val.containsKey(funkcije.get(k).get('%').charAt(0)) && real_val.get(funkcije.get(k).get('%').charAt(0)).isEmpty()){
                                        val.put(funkcije.get(k).get('%').charAt(0), new String());
                                    }
                                    else {
                                        prvi = prvi.replaceAll(String.valueOf(funkcije.get(key).get('%').charAt(0)), real_val.get(funkcije.get(key).get('%').charAt(0)));
                                        val.put(funkcije.get(k).get('%').charAt(0), String.valueOf(Character.getNumericValue(prvi.charAt(0)) - Character.getNumericValue(prvi.charAt(2))));
                                    }
                                }
                                else {
                                    val.put(funkcije.get(k).get('%').charAt(0), prvi);
                                }
                                i = p + 1;
                            }
                            HashMap<Character, String> dod_zam = new HashMap<>(zamjene);
                            int pom = i;
                            for (int z = 1; z != 0; i++) {
                                if (funkcije.get(key).get('$').charAt(i) == '(')
                                    z++;
                                else if (funkcije.get(key).get('$').charAt(i) == ')')
                                    z--;
                            }
                            i--;
                            char c = (char)((key_g++ % 26) + 65);
                            dod_zam.put(c, funkcije.get(key).get('$').substring(pom, i));
                            filip.add("");
                            dubina++;
                            argument = argument.append(key);
                            f_zamjene(dod_zam, real_val, c);
                            if (filip.get(dubina).length() > MAX)
                                return;
                            val.put(funkcije.get(k).get('%').charAt(1), filip.get(dubina));
                            filip.remove(dubina);
                            dubina--;
                            argument = argument.deleteCharAt(argument.length() - 1);
                        }
                        f_funkcije(val, k);
                    }
                    else {
                        f_zamjene(zamjene, val, funkcije.get(key).get('$').charAt(i));
                    }
                }
            }
        }
        catch (Exception ex) {
            if (ex instanceof HerbertException)
                throw ex;
            throw new HerbertException(ExceptionStatus.PARSER, -1, ex.getMessage());
        }
    }
    private void f_zamjene(HashMap<Character, String> zamjene, HashMap<Character, String> real_val, Character key) throws HerbertException {
        int i = 0;
        try {
            for (; i < zamjene.get(key).length() && filip.get(dubina).length() <= MAX; i++){
                if (slr(zamjene.get(key).charAt(i))) {
                    StringBuilder s = new StringBuilder();
                    s.append(zamjene.get(key).charAt(i));
                    filip.set(dubina, filip.get(dubina) + s);
                    //filip = filip.concat(new String(s));
                }
                else if (var(zamjene.get(key).charAt(i))) {
                    filip.set(dubina, filip.get(dubina) + real_val.get(zamjene.get(key).charAt(i)));
                    //filip = filip.concat(real_val.get(zamjene.get(key).charAt(i)));
                }
                else if (id(zamjene.get(key).charAt(i))) {
                    HashMap<Character, String> val = new HashMap<>();
                    if ((i + 1 < zamjene.get(key).length()) && zamjene.get(key).charAt(i + 1) == '(') {
                        Character k = zamjene.get(key).charAt(i);
                        if (funkcije.get(k).get('%').length() == 1) {
                            if (zamjene.get(key).charAt(i + 2) == ')') {
                                val.put(funkcije.get(k).get('%').charAt(0), new String());
                                i = i + 2;
                            }
                            else {
                                int p =zamjene.get(key).indexOf(')', i + 2);
                                val.put(funkcije.get(k).get('%').charAt(0), zamjene.get(key).substring(i + 2, p));
                                i = p;
                            }
                        }
                        else {
                            if (zamjene.get(key).charAt(i + 2) == ',') {
                                val.put(funkcije.get(k).get('%').charAt(0), new String());
                                i = i + 3;
                            }
                            else {
                                int p = zamjene.get(key).indexOf(',', i + 2);
                                val.put(funkcije.get(k).get('%').charAt(0), zamjene.get(key).substring(i + 2, p));
                                i = p + 1;
                            }
                            HashMap<Character, String> dod_zam = new HashMap<>(zamjene);
                            int pom = i;
                            for (int z = 1; z != 0; i++) {
                                if (zamjene.get(key).charAt(i) == '(')
                                    z++;
                                else if (zamjene.get(key).charAt(i) == ')')
                                    z--;
                            }
                            i--;
                            char c = (char)((key_g++ % 26) + 65);
                            dod_zam.put(c, zamjene.get(key).substring(pom, i));
                            filip.add("");
                            dubina++;
                            argument = argument.append(key);
                            f_zamjene(dod_zam, real_val, c);
                            if (filip.get(dubina).length() > MAX)
                                return;
                            val.put(funkcije.get(k).get('%').charAt(1), filip.get(dubina));
                            filip.remove(dubina);
                            dubina--;
                            argument = argument.deleteCharAt(argument.length() - 1);
                        }
                        f_funkcije(val, k);
                    }
                    else {
                        f_zamjene(zamjene, val, zamjene.get(key).charAt(i));
                    }
                }
            }
        }
        catch (Exception ex) {
            if (ex instanceof HerbertException)
                throw ex;
            throw new HerbertException(ExceptionStatus.PARSER, -1, ex.getMessage());
        }
    }
    public void fillHashMaps(String niz) {
        int i = 0;
        String[] ulaz = niz.trim().split("\n");
        zamjene.put('>', ulaz[ulaz.length - 1]);
        for (; i < ulaz.length - 1; i++){
            if (ulaz[i].charAt(1) == ':'){
                zamjene.put(ulaz[i].charAt(0), ulaz[i].substring(2));
            }
            else{
                String znakovi = ulaz[i].substring(ulaz[i].indexOf('(') + 1, ulaz[i].indexOf(')'));
                if (znakovi.length() > 0){
                    znakovi = znakovi.replaceAll(",", "");
                }
                HashMap<Character, String> argument = new HashMap<>();
                argument.put('%', znakovi);
                argument.put('$', ulaz[i].substring(ulaz[i].indexOf(':') + 1));
                funkcije.put(ulaz[i].charAt(0), argument);
            }
        }
    }
    private class Semantex {
        private String semantic_error;
        private String definition;
        private Semantex(){
            semantic_error = new String();
            definition = new String();
        }
        private ExceptionStatus status = ExceptionStatus.SYNTAX;
        private String getSemanticError(){ return semantic_error; }
        private ExceptionStatus getStatus() { return status; }
        private void setDefinition(Character name, boolean z) {
            if (z)
                definition = String.valueOf(name) + ":" + zamjene.get(name);
            String pom = "";
            if (funkcije.get(name).get('%').length() == 2)
                pom += ("," + funkcije.get(name).get('%').charAt(1));
            definition = String.valueOf(name) + "(" + funkcije.get(name).get('%').charAt(0) + pom + "):" + funkcije.get(name).get('$');
        }
        private String getDefinition(){ return definition; }
        private boolean semantic(){
            if (zamjene.keySet().size() > 1 || funkcije.keySet().size() != 0){
                Character[] key_zamjene =  zamjene.keySet().toArray(new Character[zamjene.size()]);
                Character[] key_funkcije = funkcije.keySet().toArray(new Character[funkcije.size()]);
                for (int i = 0; i < key_zamjene.length; i++){
                    String right = zamjene.get(key_zamjene[i]);
                    if (!check_zamjene(key_zamjene, key_funkcije, right)) {
                        setDefinition(key_zamjene[i], true);
                        return false;
                    }
                }
                for (int i = 0; i < key_funkcije.length; i++){
                    String right = funkcije.get(key_funkcije[i]).get('$');
                    if (!check_funkcije(key_zamjene, key_funkcije, right, key_funkcije[i])){
                        setDefinition(key_funkcije[i], false);
                        return false;
                    }
                }
            }
            else {
                String right = zamjene.get('>');
                for (int i = 0; i < right.length(); i++){
                    if (!slr(right.charAt(i))){
                        semantic_error = "Nepoznati identifikator " + right.charAt(i);
                        setDefinition('>', true);
                        return false;
                    }
                }
            }
            return true;
        }
        private boolean check_zamjene(Character[] key_zamjene, Character[] key_funkcije, String desno){
            for (int j = 0; j < desno.length(); j++){
                if (id(desno.charAt(j))){
                    if (j + 1 < desno.length() && desno.charAt(j + 1) == '('){
                        int k = 0;
                        for (; k < key_funkcije.length; k++)
                            if (desno.charAt(j) == key_funkcije[k])
                                break;
                        if (k >= key_funkcije.length) {
                            semantic_error = "Nepoznati identifikator funkcije " + desno.charAt(j) + "\n";
                            return false;
                        }
                        int zero = 1;
                        for (k = j + 2; k < desno.length() && zero != 0; k++){
                            if (desno.charAt(k) == '('){
                                zero++;
                            }
                            if (desno.charAt(k) == ')'){
                                zero--;
                            }
                        }
                        String podniz = desno.substring(j + 2, k - 1);
                        if (funkcije.get(desno.charAt(j)).get('%').length() == 1 && !podniz.isEmpty()){
                            if (!broj(podniz.charAt(0))){
                                semantic_error = "Kod poziva funkcije, prvi argument mora biti broj\n";
                                status = ExceptionStatus.SEMANTIC;
                                return false;
                            }
                            if (podniz.indexOf(',') != -1){
                                semantic_error = "Jedan argument viska pri pozivu funckije\n";
                                return false;
                            }
                        }
                        else if (funkcije.get(desno.charAt(j)).get('%').length() == 2){
                            if (podniz.isEmpty()){
                                semantic_error = "Nedostaju oba argumenta pri pozivu funkcije\n";
                                return false;
                            }
                            if (podniz.indexOf(',') != -1){
                                String prvi = podniz.substring(0, podniz.indexOf(','));
                                if (!prvi.isEmpty() && !broj(prvi.charAt(0))){
                                    semantic_error = "Kod poziva funkcije, prvi argument mora biti broj\n";
                                    status = ExceptionStatus.SEMANTIC;
                                    return false;
                                }
                            }
                            if (podniz.indexOf(',') == -1){
                                semantic_error = "Nedostaje drugi argument pri pozivu funkcije\n";
                                return false;
                            }
                            String drugi = podniz.substring(podniz.indexOf(',') + 1, podniz.length());
                            if (!check_zamjene(key_zamjene, key_funkcije, drugi))
                                return false;
                        }
                        j = k - 1;
                    }
                    else {
                        int k = 0;
                        for (; k < key_zamjene.length; k++) {
                            if (desno.charAt(j) == key_zamjene[k]) {
                                break;
                            }
                        }
                        if (k >= key_zamjene.length) {
                            semantic_error = "Nepoznati identifikator zamjene " + desno.charAt(j) + "\n";
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        private boolean check_funkcije(Character[] key_zamjene, Character[] key_funkcije, String desno, Character funk){
            for (int j = 0; j < desno.length(); j++){
                if (id(desno.charAt(j))){
                    if (j + 1 < desno.length() && desno.charAt(j + 1) == '('){
                        int k = 0;
                        for (; k < key_funkcije.length; k++)
                            if (desno.charAt(j) == key_funkcije[k])
                                break;
                        if (k >= key_funkcije.length) {
                            semantic_error = "Nepoznati identifikator funkcije " + desno.charAt(j) + "\n";
                            return false;
                        }
                        int zero = 1;
                        for (k = j + 2; k < desno.length() && zero != 0; k++){
                            if (desno.charAt(k) == '('){
                                zero++;
                            }
                            if (desno.charAt(k) == ')'){
                                zero--;
                            }
                        }
                        String podniz = desno.substring(j + 2, k - 1);
                        if (funkcije.get(desno.charAt(j)).get('%').length() == 1 && !podniz.isEmpty()){
                        /*if (!broj(podniz.charAt(0)) && !var(podniz.charAt(0))){
                            semantic_error = "Kod poziva funkcije, prvi argument mora biti broj\n";
                            return false;
                        }*/
                            if (var(podniz.charAt(0))){
                                if (funkcije.get(funk).get('%').length() == 1){
                                    if (podniz.charAt(0) != funkcije.get(funk).get('%').charAt(0)) {
                                        semantic_error = "Identifikator varijable ne postoji, ocekivana varijabla imena " + funkcije.get(funk).get('%').charAt(0) + "\n";
                                        return false;
                                    }
                                }
                                else {
                                    if (podniz.charAt(0) != funkcije.get(funk).get('%').charAt(0) && podniz.charAt(0) != funkcije.get(funk).get('%').charAt(1)){
                                        semantic_error = "Identifikator varijable ne postoji, ocekivana varijabla imena " + funkcije.get(funk).get('%').charAt(1) + "\n";
                                        return false;
                                    }
                                    if (podniz.charAt(0) == funkcije.get(funk).get('%').charAt(1)){
                                        semantic_error = "Kod poziva funkcije, prvi argument mora biti broj\n";
                                        status = ExceptionStatus.SEMANTIC;
                                        return false;
                                    }
                                }
                            }
                            if (podniz.indexOf(',') != -1){
                                semantic_error = "Jedan argument viska pri pozivu funckije\n";
                                return false;
                            }
                        }
                        else if (funkcije.get(desno.charAt(j)).get('%').length() == 2){
                            if (podniz.isEmpty()){
                                semantic_error = "Nedostaju oba argumenta pri pozivu funkcije\n";
                                return false;
                            }
                            if (podniz.indexOf(',') != -1){
                                String prvi = podniz.substring(0, podniz.indexOf(','));
                                if (!prvi.isEmpty()){
                                    if (var(prvi.charAt(0))){
                                        if (funkcije.get(funk).get('%').length() == 1){
                                            if (prvi.charAt(0) != funkcije.get(funk).get('%').charAt(0)) {
                                                semantic_error = "Identifikator varijable ne postoji, ocekivana varijabla imena " + funkcije.get(funk).get('%').charAt(0) + "\n";
                                                return false;
                                            }
                                        }
                                        else {
                                            if (prvi.charAt(0) != funkcije.get(funk).get('%').charAt(0) && prvi.charAt(0) != funkcije.get(funk).get('%').charAt(1)){
                                                semantic_error = "Identifikator varijable ne postoji, ocekivana varijabla imena " + funkcije.get(funk).get('%').charAt(1) + "\n";
                                                return false;
                                            }
                                            if (prvi.charAt(0) == funkcije.get(funk).get('%').charAt(1)){
                                                semantic_error = "Kod poziva funkcije, prvi argument mora biti broj\n";
                                                status = ExceptionStatus.SEMANTIC;
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                semantic_error = "Nedostaje drugi argument pri pozivu funkcije\n";
                                return false;
                            }
                            String drugi = podniz.substring(podniz.indexOf(',') + 1, podniz.length());
                            if (!check_funkcije(key_zamjene, key_funkcije, drugi, funk))
                                return false;
                        }
                        j = k - 1;
                    }
                    else {
                        int k = 0;
                        for (; k < key_zamjene.length; k++) {
                            if (desno.charAt(j) == key_zamjene[k]) {
                                break;
                            }
                        }
                        if (k >= key_zamjene.length) {
                            semantic_error = "Nepoznati identifikator zamjene " + desno.charAt(j) + "\n";
                            return false;
                        }
                    }
                }
                if (var(desno.charAt(j))){
                    if (funkcije.get(funk).get('%').length() == 1){
                        semantic_error = "Funkcija ima samo jedan argument i varijabla je broj\n";
                        status = ExceptionStatus.SEMANTIC;
                        return false;
                    }
                    else {
                        if (desno.charAt(j) == funkcije.get(funk).get('%').charAt(0)){
                            semantic_error = "Varijabla je broj\n";
                            status = ExceptionStatus.SEMANTIC;
                            return false;
                        }
                        if (desno.charAt(j) != funkcije.get(funk).get('%').charAt(1)){
                            semantic_error = "Identifikator varijable ne postoji, ocekivana varijabla imena " + funkcije.get(funk).get('%').charAt(1) + "\n";
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }
    public void Parse() throws HerbertException {
        filip.add("");
        f_zamjene(zamjene, real, '>');
    }
    public String getCode() {
        return filip.get(0);
    }
    static boolean id(Character sym){
        return Character.isLowerCase(sym) && sym != 's' && sym != 'l' && sym != 'r';
    }
    static boolean slr(Character sym){
        return sym == 's' || sym == 'l' || sym == 'r';
    }
    static boolean var(Character sym){
        return Character.isUpperCase(sym);
    }
    static boolean broj(Character sym){
        return Character.isDigit(sym);
    }
    static boolean nl(Character sym){
        return sym == '\n';
    }

    public void syntax(String niz) throws HerbertException {
        Syntaxer syntaxer = new Syntaxer(niz);
        if (!syntaxer.parser()) {
            throw new HerbertException(ExceptionStatus.SYNTAX, syntaxer.getPozicija(), syntaxer.getErrorDescription());
        }
    }
    public void semantic() throws HerbertException {
        Semantex semantex = new Semantex();
        if (!semantex.semantic()) {
            throw new HerbertException(semantex.getStatus(), -1, semantex.getDefinition() + "\n" + semantex.getSemanticError());
        }
    }

    private class Syntaxer{
        private int pozicija = 0;
        private String errorDescription;
        private String herbertCode;

        private String getErrorDescription() {
            return errorDescription;
        }
        private int getPozicija() {return pozicija; }

        private Syntaxer(String code){
            herbertCode = code;
            errorDescription = "";
        }
        private boolean parser(){
            if (pozicija < tokens.size()){
                if (tokens.elementAt(pozicija).t == Tokens.SLR){
                    pozicija++;
                    while (pozicija < tokens.size() - 1){
                        if (tokens.elementAt(pozicija).t == Tokens.SLR){
                            pozicija++;
                        }
                        else{
                            errorDescription += ("Ocekivani koraci s, l ili r umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                            return false;
                        }
                    }
                    return true;
                }
                else {
                    pozicija--;
                    if (!poc()){
                        errorDescription += "Ocekivana definicija funkcije ili zamjena\n";
                        return false;
                    }
                    else {
                        pozicija++;
                        if (pozicija < tokens.size()){
                            if (tokens.elementAt(pozicija).t != Tokens.NL){
                                errorDescription += ("Ocekivan prelazak u novi redak umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                return false;
                            }
                        }
                        else {
                            errorDescription += "Prekratak unos, ocekivan prelazak u novi redak\n";
                            return false;
                        }
                    }
                    //long newline = herbertCode.chars().filter(num -> num == '\n').count() - 2;
                    // ->Error:(602, 67) error: lambda expressions are not supported in -source 1.7
                    //(use -source 8 or higher to enable lambda expressions)
                    int newline = herbertCode.split("\\n").length - 2;
                    for (int i = 0; i < newline; i++){
                        if (!poc())
                            return false;
                        pozicija++;
                        if (pozicija < tokens.size()){
                            if (tokens.elementAt(pozicija).t != Tokens.NL){
                                errorDescription += ("Ocekivan prelazak u novi redak umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                return false;
                            }
                        }
                        else {
                            errorDescription += "Prekratak unos, ocekivan prelazak u novi redak\n";
                            return false;
                        }
                    }
                    if (p_f1(false, true)){
                        pozicija++;
                        while (p_f1(false, true)){
                            pozicija++;
                        }
                        return true;
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivana definicija funkcije ili zamjene\n";
                        return false;
                    }
                }
            }
            else{
                errorDescription += "Prekratak unos, ocekivani koraci, definicije funkcija ili zamjena\n";
                return false;
            }
        }
        private boolean expr(){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.VAR){
                    if (pozicija + 1 < tokens.size()){
                        if (tokens.elementAt(pozicija + 1).t == Tokens.MINUS){
                            pozicija++;
                            if (pozicija + 1 < tokens.size()){
                                pozicija++;
                                if (tokens.elementAt(pozicija).t == Tokens.INT){
                                    return true;
                                }
                                else {
                                    errorDescription += ("Ocekivan broj umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                    return false;
                                }
                            }
                            else {
                                errorDescription += "Prekratak unos, ocekivan broj nakon minusa\n";
                                return false;
                            }
                        }
                        else if (tokens.elementAt(pozicija + 1).t != Tokens.ZAREZ || tokens.elementAt(pozicija + 1).t != Tokens.ZZAG){
                            pozicija++;
                            if (tokens.elementAt(pozicija).t == Tokens.INT){
                                errorDescription += ("Ocekivan minus umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                return false;
                            }
                            errorDescription += ("Ocekivana zatvorena zagrada ili zarez umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                            return false;
                        }
                        return true;
                    }
                    return true;
                }
                else if (tokens.elementAt(pozicija).t == Tokens.INT){
                    return true;
                }
                else {
                    errorDescription += ("Ocekivana varijabla ili broj umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivana varijabla ili broj\n";
                return false;
            }
        }
        private boolean argd(){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.OZAG){
                    if (pozicija + 1 < tokens.size()){
                        pozicija++;
                        if (tokens.elementAt(pozicija).t == Tokens.VAR){
                            if (pozicija + 1 < tokens.size()){
                                pozicija++;
                                if (tokens.elementAt(pozicija).t == Tokens.ZZAG){
                                    return true;
                                }
                                else if (tokens.elementAt(pozicija).t == Tokens.ZAREZ){
                                    if (pozicija + 1 < tokens.size()){
                                        pozicija++;
                                        if (tokens.elementAt(pozicija).t == Tokens.VAR){
                                            if (pozicija + 1 < tokens.size()){
                                                pozicija++;
                                                if (tokens.elementAt(pozicija).t == Tokens.ZZAG){
                                                    return true;
                                                }
                                                else {
                                                    errorDescription += ("Ocekivana zatvorena zagrada umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                                    return false;
                                                }
                                            }
                                            else {
                                                errorDescription += "Prekratak unos, ocekivana zatvorena zagrada\n";
                                                return false;
                                            }
                                        }
                                        else {
                                            errorDescription += ("Ocekivana varijabla umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                            return false;
                                        }
                                    }
                                    else {
                                        errorDescription += "Prekratak unos, oceivana varijabla\n";
                                        return false;
                                    }
                                }
                                else {
                                    errorDescription += ("Ocekivana zatvorena zagrada ili zarez umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                    return false;
                                }
                            }
                            else {
                                errorDescription += "Prekratak unos, ocekivana zatvorena zagrada\n";
                                return false;
                            }
                        }
                        else {
                            errorDescription += ("Ocekivana varijabla umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                            return false;
                        }
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivana varijabla\n";
                        return false;
                    }
                }
                else{
                    errorDescription += ("Ocekivana otvorena zagrada umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivana otvorena zagrada\n";
                return false;
            }
        }
        private boolean p_fun(){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.ID){
                    if (pozicija + 1 < tokens.size()){
                        pozicija++;
                        if (tokens.elementAt(pozicija).t == Tokens.OZAG){
                            if (pozicija + 1 < tokens.size()){
                                pozicija++;
                                if (tokens.elementAt(pozicija).t == Tokens.ZZAG){
                                    return true;
                                }
                                if (tokens.elementAt(pozicija).t == Tokens.INT || tokens.elementAt(pozicija).t == Tokens.VAR){
                                    pozicija--;
                                    if (!expr())
                                        return false;
                                    if (pozicija + 1 >= tokens.size()){
                                        errorDescription += "Prekratak unos, nedostaje barem zatvorena zagrada\n";
                                        return false;
                                    }
                                    pozicija++;
                                    if (tokens.elementAt(pozicija).t != Tokens.ZZAG && tokens.elementAt(pozicija).t != Tokens.ZAREZ){
                                        errorDescription += "Prekratak unos, ocekivana zatvorena zagrada ili zarez\n";
                                        return false;
                                    }
                                    if (tokens.elementAt(pozicija).t == Tokens.ZZAG)
                                        return true;
                                }
                                if (tokens.elementAt(pozicija).t == Tokens.ZAREZ){
                                    if (pozicija + 1 < tokens.size()){
                                        pozicija++;
                                        if (tokens.elementAt(pozicija).t == Tokens.ZZAG){
                                            errorDescription += "Nedostaje drugi argument\n";
                                            return false;
                                        }
                                        pozicija--;
                                        if (!p_f1(true, true)){
                                            return false;
                                        }
                                        if (pozicija + 1 < tokens.size()){
                                            pozicija++;
                                            if (tokens.elementAt(pozicija).t == Tokens.ZZAG){
                                                return true;
                                            }
                                            else {
                                                errorDescription += ("Ocekivana zatvorena zagrada umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                                return false;
                                            }
                                        }
                                        else {
                                            errorDescription += "Prekratak unos, ocekivana zatvorena zagrada\n";
                                            return false;
                                        }
                                    }
                                    else {
                                        errorDescription += "Prekratak unos, ocekivan drugi argument\n";
                                        return false;
                                    }
                                }
                                else {
                                    errorDescription += ("Ocekivani ispravni prvi argument umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                    return false;
                                }
                            }
                            else {
                                errorDescription += "Prekratak unos, ocekivana barem zatvorena zagrada\n";
                                return false;
                            }
                        }
                        else {
                            errorDescription += ("Ocekivana otvorena zagrada umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                            return false;
                        }
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivana otvorena zagrada\n";
                        return false;
                    }
                }
                else {
                    errorDescription += ("Ocekivan identifikator umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivan identifikator\n";
                return false;
            }
        }
        private boolean p_f1(boolean withVAR, boolean first){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.ID && pozicija + 1 < tokens.size() && tokens.elementAt(pozicija + 1).t == Tokens.OZAG){
                    pozicija--;
                    if (!p_fun())
                        return false;
                }
                else if (tokens.elementAt(pozicija).t != Tokens.ID && tokens.elementAt(pozicija).t != Tokens.SLR && (tokens.elementAt(pozicija).t != Tokens.VAR || !withVAR)){
                    if (first){
                        errorDescription += ("Krivi simbol " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                        return false;
                    }
                    else{
                        pozicija--;
                        return true;
                    }
                }
                if (!p_f1(withVAR, false)){
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                errorDescription += ("Prekratak unos, ocekivan identifikator, korak, poziv funkcije" + (withVAR?" ili varijabla":"") + "\n");
                return false;
            }
        }
        private boolean d_fun(){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.ID){
                    if (pozicija + 1 < tokens.size()){
                        if (!argd())
                            return false;
                        if (pozicija + 1 < tokens.size()){
                            pozicija++;
                            if (tokens.elementAt(pozicija).t == Tokens.DVOTOCKA){
                                if (pozicija + 1 < tokens.size()){
                                    if (!p_f1(true, true))
                                        return false;
                                    return true;
                                }
                                else {
                                    errorDescription += "Prekratak unos, ocekivana desna strana definicije funkcije\n";
                                    return false;
                                }
                            }
                            else {
                                errorDescription += ("Ocekivano dvotocje umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                                return false;
                            }
                        }
                        else {
                            errorDescription += "Prekratak unos, ocekivano dvotocje\n";
                            return false;
                        }
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivani argumenti\n";
                        return false;
                    }
                }
                else {
                    errorDescription += ("Ocekivan identifikator umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivan identifikator\n";
                return false;
            }
        }
        private boolean prid(){
            if (pozicija + 1 < tokens.size()){
                pozicija++;
                if (tokens.elementAt(pozicija).t == Tokens.ID){
                    if (pozicija + 1 < tokens.size()){
                        pozicija++;
                        if (tokens.elementAt(pozicija).t == Tokens.DVOTOCKA){
                            if (pozicija + 1 < tokens.size()){
                                if (!p_f1(false, true))
                                    return false;
                                return true;
                            }
                            else {
                                errorDescription += "Prekratak unos, ocekivana desna strana definicije zamjene\n";
                                return  false;
                            }
                        }
                        else {
                            errorDescription += ("Ocekivano dvotocje umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                            return false;
                        }
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivano dvotocje\n";
                        return false;
                    }
                }
                else {
                    errorDescription += ("Ocekivan identifikator umjesto " + herbertCode.charAt(tokens.elementAt(pozicija).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivan identifikator\n";
                return false;
            }
        }
        private boolean poc(){
            if (pozicija + 1 < tokens.size()){
                if (tokens.elementAt(pozicija + 1).t == Tokens.ID){
                    if (pozicija + 2 < tokens.size()){
                        if (tokens.elementAt(pozicija + 2).t == Tokens.DVOTOCKA){
                            if (!prid())
                                return false;
                            return true;
                        }
                        else if (tokens.elementAt(pozicija + 2).t == Tokens.OZAG){
                            if (!d_fun())
                                return false;
                            return true;
                        }
                        else {
                            errorDescription += ("Ocekivano dvotocje ili otvorena zagrada umjesto " + herbertCode.charAt(tokens.elementAt(pozicija + 2).poz) + "\n");
                            return false;
                        }
                    }
                    else {
                        errorDescription += "Prekratak unos, ocekivano dvotocje ili otvorena zagrada\n";
                        return false;
                    }
                }
                else {
                    errorDescription += ("Ocekivan identifikator umjesto " + herbertCode.charAt(tokens.elementAt(pozicija + 1).poz) + "\n");
                    return false;
                }
            }
            else {
                errorDescription += "Prekratak unos, ocekivane definicije funckija ili zamjena\n";
                return false;
            }
        }
    }
}