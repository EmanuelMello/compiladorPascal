package compiladores.lexico;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexico {

    private BufferedReader br;
    private char caractere;
    private int linha;
    private int coluna;

    public Lexico(BufferedReader br) {
        this.br = br;
        linha = 1;
        coluna = 0;
        caractere = nextChar();
    }

    String[] keywordArray = {
            "and", "array", "begin", "case", "const", "div", "do", "downto", "else", "end",
            "file", "for", "function", "goto", "if", "in", "label", "mod", "nil", "not",
            "of", "or", "packed", "procedure", "program", "record", "repeat", "set",
            "then", "to", "type", "until", "var", "while", "with", "integer", "read", "write", "writeln"
    };

    List<String> keywords = new ArrayList<>(Arrays.asList(keywordArray));

    public Token nextToken() {

        StringBuilder lexema = new StringBuilder();

        while (caractere != 65535) { // 65535 fim da stream
            if (Character.isLetter(caractere)) {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                token.setClasse(Classe.cId);
                while (Character.isLetter(caractere) ||
                        Character.isDigit(caractere)) {
                    lexema.append(caractere);
                    caractere = nextChar();
                }
                token.setValor(new Valor(lexema.toString()));
                if (keywords.contains(lexema.toString().toLowerCase())) {
                    token.setClasse(Classe.cPalavraReservada);
                    token.setValor(new Valor(lexema.toString().toLowerCase()));
                }
                
                return token;
            } else if (Character.isDigit(caractere)) {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                while (Character.isDigit(caractere)) {
                    lexema.append(caractere);
                    caractere = nextChar();
                }
                token.setClasse(Classe.cInt);
                token.setValor(new Valor(Integer.parseInt(lexema.toString())));
                return token;
            } else if (Character.isWhitespace(caractere)) {
                if (caractere == '\n') {
                    linha++;
                    coluna = 0;
                }
                caractere = nextChar();
            } else if (caractere == '(') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cParEsq);
                return token;
            } else if (caractere == ')') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cParDir);
                return token;
            } else if (caractere == ',') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cVirg);
                return token;
            } else if (caractere == '.') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cPonto);
                return token;
            } else if (caractere == ';') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cPontoVirg);
                return token;
            } else if (caractere == '+') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cAdicao);
                return token;
            } else if (caractere == '-') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cSubtracao);
                return token;

            } else if (caractere == '/') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cDivisao);
                return token;
            } else if (caractere == '*') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cMultiplicacao);
                return token;

            } else if (caractere == '=') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                lexema.append(caractere);
                caractere = nextChar();
                token.setClasse(Classe.cIgual);
                return token;
            } else if (caractere == ':') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                caractere = nextChar();

                if (caractere == '=') {
                    lexema.append(caractere);
                    caractere = nextChar();
                    token.setClasse(Classe.cAtribuicao);
                    return token;
                } else {
                    lexema.append(caractere);
                    token.setClasse(Classe.cDoisPontos);
                    return token;
                }
            } else if (caractere == '>') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                caractere = nextChar();
                if (caractere == '=') {
                    lexema.append(caractere);
                    caractere = nextChar();
                    token.setClasse(Classe.cMaiorIgual);
                    return token;
                } else {
                    lexema.append(caractere);
                    token.setClasse(Classe.cMaior);
                    return token;
                }
            } else if (caractere == '<') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                caractere = nextChar();
                if (caractere == '=') {
                    lexema.append(caractere);
                    caractere = nextChar();
                    token.setClasse(Classe.cMenorIgual);
                    return token;
                } else if (caractere == '>') {
                    lexema.append(caractere);
                    caractere = nextChar();
                    token.setClasse(Classe.cDiferente);
                    return token;
                } else {
                    lexema.append(caractere);
                    token.setClasse(Classe.cMenor);
                    return token;
                }
            } else if (caractere == '\'') {
                Token token = new Token();
                token.setLinha(linha);
                token.setColuna(coluna);
                caractere = nextChar();
                while (caractere != '\'' && caractere != 65535 && caractere != '\n') {
                    lexema.append(caractere);
                    caractere = nextChar();
                }
                if (caractere == '\'') {
                    caractere = nextChar();
                    token.setClasse(Classe.cString);
                    token.setValor(new Valor(lexema.toString()));
                    return token;
                } else if (caractere == 65535 && caractere == '\n') {
                    System.out.println("Erro  -> " + "linha: " + linha + " coluna: " + coluna);
                }
            } else if (caractere == '{') {
                while (caractere != '}' && caractere != 65535) {
                    caractere = nextChar();
                }
                if (caractere == '}') {
                    caractere = nextChar();
                } else if (caractere == 65535) {
                    System.out.println("Erro  -> " + "linha: " + linha + " coluna: " + coluna);
                }
            } else {
                System.out.println("Erro  -> " + "linha: " + linha + " coluna: " + coluna);
                caractere = nextChar();
            }
        }
        Token token = new Token();
        token.setClasse(Classe.cEOF);
        token.setLinha(linha);
        token.setColuna(coluna);
        return token;
    }

    private char nextChar() {
        try {
            coluna++;
            return (char) br.read();
        } catch (IOException e) {
            return ' ';
        }
    }

}
