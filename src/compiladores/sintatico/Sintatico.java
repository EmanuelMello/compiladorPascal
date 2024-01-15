package compiladores.sintatico;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import compiladores.lexico.Classe;
import compiladores.lexico.Lexico;
import compiladores.lexico.Token;

public class Sintatico {

    private Lexico lexico;
    private Token token;
    private BufferedWriter bufferedWriter;
    private String rotulo = "";
    private int endereco;
    private List<String> variaveisDeclaradas = new ArrayList<>();

    public Sintatico(Lexico lexico) {
        this.lexico = lexico;
    }

    // public void analisar() {
    //     token = lexico.nextToken();
    //     programa();
    // }

    public void AcaoCriarCabeçalhoCodigoC() {
        // tabela=new TabelaSimbolos();

        // tabela.setTabelaPai(null);

        // Registro registro=new Registro();
        // registro.setNome(token.getValor().getValorIdentificador());
        // registro.setCategoria(Categoria.PROGRAMAPRINCIPAL);

        // registro.setNivel(0);
        // registro.setOffset(0);
        // registro.setTabelaSimbolos(tabela);
        // registro.setRotulo("main");
        // tabela.inserirRegistro(registro);
        String codigo = "#include <stdio.h>\n" +
                "\nint main(){\n";

        gerarCodigo(codigo);
    }

    private void AcaoDeclararVariavelNumerica(String type) {
        String codigo = '\t'+type;
        for(int i = 0; i<this.variaveisDeclaradas.size(); i++)
        {
            codigo = codigo + ' ' + this.variaveisDeclaradas.get(i);
            if(i == this.variaveisDeclaradas.size()-1)
            {
                codigo = codigo + ';';
            }
            else{
                codigo = codigo + ',';
            }
        }
        gerarCodigo(codigo);
    }

    private void gerarCodigo(String instrucoes) {
        try {
            if (rotulo.isEmpty()) {
                bufferedWriter.write(instrucoes + "\n");
            } else {
                bufferedWriter.write(rotulo + ": " +  instrucoes + "\n");
                rotulo = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void analisar() {
        token = lexico.nextToken();

        this.endereco = 0;
        String caminhoArquivoSaida = Paths.get("outputFile.c").toAbsolutePath().toString();

        bufferedWriter = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(caminhoArquivoSaida, Charset.forName("UTF-8"));
            bufferedWriter = new BufferedWriter(fileWriter);
            programa();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Simbolos encontrados:");
        //System.out.println(this.tabela);
    }
    // <programa> ::= program <id> {A01} ; <corpo> • {A45}

    private void programa() {
        if (palavraReservada(token, "program")) {
            token = lexico.nextToken();
            if (token.getClasse() == Classe.cId) {
                token = lexico.nextToken();
                AcaoCriarCabeçalhoCodigoC();
                if (token.getClasse() == Classe.cPontoVirg) {
                    token = lexico.nextToken();
                    corpo();
                    if (token.getClasse() == Classe.cPonto) {
                        token = lexico.nextToken();
                        // {A45}
                    } else {
                        System.out.println(token.getLinha() + "," + token.getColuna() + ", Ponto Final (.) esperado");
                    }
                } else {
                    System.out.println(token.getLinha() + "," + token.getColuna() + ", Ponto e vírgula (;) esperado");
                }
            } else {
                System.out.println(token.getLinha() + "," + token.getColuna() + ", Nome do Programa esperado");
            }
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", program esperado");
        }
    }

    private void corpo() {
        declara();
        // rotina();
        // {A44}
        if (palavraReservada(token, "begin")) {
            token = lexico.nextToken();
            sentencas();
            if (palavraReservada(token, "end")) {
                token = lexico.nextToken();
                // {A46}
            } else {
                System.out.println(token.getLinha() + "," + token.getColuna() + ", end esperado");
            }
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", begin esperado");
        }
    }

    private void declara() {
        if (palavraReservada(token, "var")) {
            token = lexico.nextToken();
            dvar();
            mais_dc();
        }
    }

    private void mais_dc() {
        if (token.getClasse() == Classe.cPontoVirg) {
            token = lexico.nextToken();
            cont_dc();
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Dois pontos (;) esperado");
        }
    }

    private void cont_dc() {
        if (token.getClasse() == Classe.cId) {
            dvar();
            mais_dc();
        }
    }

    private void dvar() {
        variaveis();
        if (token.getClasse() == Classe.cDoisPontos) {
            token = lexico.nextToken();
            tipo_var();
            AcaoDeclararVariavelNumerica("int");
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Dois pontos (:) esperado");
        }
    }

    private void tipo_var() {
        if (palavraReservada(token, "integer")) {
            token = lexico.nextToken();
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", integer esperado");
        }
    }

    private void variaveis() {
        if (token.getClasse() == Classe.cId) {
            token = lexico.nextToken();
            // {A03}
            mais_var();
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Identificador esperado");
        }
    }

    private void mais_var() {
        if (token.getClasse() == Classe.cVirg) {
            token = lexico.nextToken();
            variaveis();
        }
    }

    private void sentencas() {
        comando();
        mais_sentencas();
    }

    private void mais_sentencas() {
        if (token.getClasse() == Classe.cPontoVirg) {
            token = lexico.nextToken();
            cont_sentencas();
        }
    }

    private void cont_sentencas() {
        if (palavraReservada(token, "read") || palavraReservada(token, "write") || palavraReservada(token, "writeln")
                || palavraReservada(token, "for") || palavraReservada(token, "repeat")
                || palavraReservada(token, "while") || palavraReservada(token, "if")
                || token.getClasse() == Classe.cId) {
            sentencas();
        }
    }

    private void var_read() {
        if (token.getClasse() == Classe.cId) {
            token = lexico.nextToken();
            // {A08}
            mais_var_read();
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Identificador esperado");
        }
    }

    private void mais_var_read() {
        if (token.getClasse() == Classe.cVirg) {
            token = lexico.nextToken();
            var_read();
        }
    }

    private void exp_write() {
        if (token.getClasse() == Classe.cId) {
            token = lexico.nextToken();
            // {A09}
            mais_exp_write();
        } else if (token.getClasse() == Classe.cString) {
            token = lexico.nextToken();
            // {A59}
            mais_exp_write();
        } else if (token.getClasse() == Classe.cInt) {
            token = lexico.nextToken();
            // {A43}
            mais_exp_write();
        } else {
            System.out.println(
                    token.getLinha() + "," + token.getColuna() + ", Identificador, String ou Inteiro esperado");
        }
    }

    private void mais_exp_write() {
        if (token.getClasse() == Classe.cVirg) {
            token = lexico.nextToken();
            exp_write();
        }
    }

    // <pfalsa> ::= else {A25} begin <sentencas> end | ε
    private void pfalsa() {
        if (palavraReservada(token, "else")) {
            token = lexico.nextToken();
            // {A25}
            if (palavraReservada(token, "begin")) {
                token = lexico.nextToken();
                sentencas();
                if (palavraReservada(token, "end")) {
                    token = lexico.nextToken();
                } else {
                    System.out.println(token.getLinha() + "," + token.getColuna() + ", end esperado");
                }
            } else {
                System.out.println(token.getLinha() + "," + token.getColuna() + ", begin esperado");
            }
        }
    }

    private boolean palavraReservada(Token token, String palavra) {
        if (token.getClasse() == Classe.cPalavraReservada && token.getValor().getTexto().equals(palavra)) {
            return true;
        } else {
            return false;
        }
    }

    // <comando> ::= read ( <var_read> ) | write ( <exp_write> ) | writeln (
    // <exp_write> ) {A61} | for <id> {A57} := <expressao> {A11} to <expressao>
    // {A12} do begin <sentencas> end {A13} | repeat {A14} <sentencas> until (
    // <expressao_logica> ) {A15} | while {A16} ( <expressao_logica> ) {A17} do
    // begin <sentencas> end {A18} | if ( <expressao_logica> ) {A19} then begin
    // <sentencas> end {A20} <pfalsa> {A21} | <id> {A49} := <expressao> {A22}
    private void comando() {
        if (palavraReservada(token, "read")) {
            token = lexico.nextToken();
            if (token.getClasse() == Classe.cParEsq) {
                token = lexico.nextToken();
                var_read();
                if (token.getClasse() == Classe.cParDir) {
                    token = lexico.nextToken();
                } else {
                    System.out
                            .println(token.getLinha() + "," + token.getColuna() + ", Parênteses Direito ( ) esperado");
                }
            } else {
                System.out.println(token.getLinha() + "," + token.getColuna() + ", Parênteses Esquerdo ( ) esperado");
            }
        } else {
            if (palavraReservada(token, "write")) {
                token = lexico.nextToken();
                if (token.getClasse() == Classe.cParEsq) {
                    token = lexico.nextToken();
                    exp_write();
                    if (token.getClasse() == Classe.cParDir) {
                        token = lexico.nextToken();
                    } else {
                        System.out.println(
                                token.getLinha() + "," + token.getColuna() + ", Parênteses Direito ( ) esperado");
                    }
                } else {
                    System.out
                            .println(token.getLinha() + "," + token.getColuna() + ", Parênteses Esquerdo ( ) esperado");
                }
            } else {
                if (palavraReservada(token, "writeln")) {
                    token = lexico.nextToken();
                    if (token.getClasse() == Classe.cParEsq) {
                        token = lexico.nextToken();
                        exp_write();
                        if (token.getClasse() == Classe.cParDir) {
                            token = lexico.nextToken();
                            // {A61}
                        } else {
                            System.out.println(
                                    token.getLinha() + "," + token.getColuna() + ", Parênteses Direito ( ) esperado");
                        }
                    } else {
                        System.out.println(
                                token.getLinha() + "," + token.getColuna() + ", Parênteses Esquerdo ( ) esperado");
                    }
                } else {
                    if (palavraReservada(token, "repeat")) {
                        token = lexico.nextToken();
                        sentencas();
                        if (palavraReservada(token, "until")) {
                            token = lexico.nextToken();
                            if (token.getClasse() == Classe.cParEsq) {
                                token = lexico.nextToken();
                                expressao_logica();
                                if (token.getClasse() == Classe.cParDir) {
                                    token = lexico.nextToken();
                                } else {
                                    System.out
                                            .println(token.getLinha() + "," + token.getColuna()
                                                    + ", Parênteses Direito ) esperado");
                                    ;
                                }
                            } else {
                                System.out
                                        .println(token.getLinha() + "," + token.getColuna()
                                                + ", Parênteses Esquerdo ( esperado");
                            }
                        } else {
                            System.out
                                    .println(token.getLinha() + "," + token.getColuna() + ", until esperado");
                            ;
                        }
                    }

                    else if (palavraReservada(token, "while")) {
                        token = lexico.nextToken();
                        if (token.getClasse() == Classe.cParEsq) {
                            token = lexico.nextToken();
                            expressao_logica();
                            if (token.getClasse() == Classe.cParDir) {
                                token = lexico.nextToken();
                                if (palavraReservada(token, "do")) {
                                    token = lexico.nextToken();
                                    if (palavraReservada(token, "begin")) {
                                        token = lexico.nextToken();
                                        sentencas();
                                        if (palavraReservada(token, "end")) {
                                            token = lexico.nextToken();
                                        } else {
                                            System.out
                                                    .println(token.getLinha() + "," + token.getColuna()
                                                            + ", end no while esperado");
                                            ;
                                        }
                                    } else {
                                        System.out
                                                .println(token.getLinha() + "," + token.getColuna()
                                                        + ", begin no while esperado");
                                        ;
                                    }
                                } else {
                                    System.out
                                            .println(token.getLinha() + "," + token.getColuna()
                                                    + ", do no while esperado");
                                }
                            } else {
                                System.out
                                        .println(token.getLinha() + "," + token.getColuna()
                                                + ", Parênteses Direito ) esperado no while");
                            }
                        } else {
                            System.out
                                    .println(token.getLinha() + "," + token.getColuna()
                                            + ", Parênteses Esquerdo (  esperado no while");
                        }
                    } else if (palavraReservada(token, "for")) {
                        token = lexico.nextToken();
                        if (token.getClasse() == Classe.cId) {
                            token = lexico.nextToken();

                            if (token.getClasse() == Classe.cAtribuicao) {
                                token = lexico.nextToken();
                                expressao();
                                if (palavraReservada(token, "to")) {
                                    token = lexico.nextToken();
                                    expressao();
                                    if (palavraReservada(token, "do")) {
                                        token = lexico.nextToken();
                                        if (palavraReservada(token, "begin")) {
                                            token = lexico.nextToken();
                                            sentencas();
                                            if (palavraReservada(token, "end")) {
                                                token = lexico.nextToken();
                                                // {A29}
                                            } else {
                                                System.out
                                                        .println(token.getLinha() + "," + token.getColuna()
                                                                + ", end esperado no for");
                                            }
                                        } else {
                                            System.out
                                                    .println(token.getLinha() + "," + token.getColuna()
                                                            + ", begin esperado no for");
                                        }
                                    } else {
                                        System.out
                                                .println(token.getLinha() + "," + token.getColuna()
                                                        + ", do esperado no for");
                                    }
                                } else {
                                    System.out
                                            .println(token.getLinha() + "," + token.getColuna()
                                                    + ", to esperado no for");
                                }
                            } else {
                                System.out
                                        .println(token.getLinha() + "," + token.getColuna()
                                                + ", dois ponto igual esperado no for");
                            }
                        } else {
                            System.out
                                    .println(token.getLinha() + "," + token.getColuna()
                                            + ", identificador esperado no inicio for");
                        }
                    } else if (palavraReservada(token, "if")) {
                        token = lexico.nextToken();
                        if (token.getClasse() == Classe.cParEsq) {
                            token = lexico.nextToken();
                            expressao_logica();
                            if (token.getClasse() == Classe.cParDir) {
                                token = lexico.nextToken();
                                if (palavraReservada(token, "then")) {
                                    token = lexico.nextToken();
                                    if (palavraReservada(token, "begin")) {
                                        token = lexico.nextToken();
                                        sentencas();
                                        if (palavraReservada(token, "end")) {
                                            token = lexico.nextToken();
                                            pfalsa();
                                        } else {
                                            System.out
                                                    .println(token.getLinha() + "," + token.getColuna()
                                                            + ", end esperado no if");
                                        }
                                    } else {
                                        System.out
                                                .println(token.getLinha() + "," + token.getColuna()
                                                        + ", begin esperado no if");
                                    }
                                } else {
                                    System.out
                                            .println(token.getLinha() + "," + token.getColuna()
                                                    + ", then esperado no if");
                                }
                            } else {
                                System.out
                                        .println(token.getLinha() + "," + token.getColuna()
                                                + ", Parentese Direito esperado no if");
                            }
                        } else {
                            System.out
                                    .println(token.getLinha() + "," + token.getColuna()
                                            + ", Parentese esquerdo esperado no if");
                        }
                    } else if (token.getClasse() == Classe.cId) {
                        token = lexico.nextToken();
                        if (token.getClasse() == Classe.cAtribuicao) {
                            token = lexico.nextToken();
                            expressao();
                        } else {
                            System.out
                                    .println(token.getLinha() + "," + token.getColuna() + ", atribuicao esperado");
                        }
                    }
                }
            }
        }
    }

    private void expressao_logica() {
        termo_logico();
        mais_expr_logica();
    }

    private void mais_expr_logica() {
        if (palavraReservada(token, "or")) {
            token = lexico.nextToken();
            termo_logico();
            // {A26}
            mais_expr_logica();
        }
    }

    private void termo_logico() {
        fator_logico();
        mais_termo_logico();
    }

    private void mais_termo_logico() {
        if (palavraReservada(token, "and")) {
            token = lexico.nextToken();
            fator_logico();
            // {A27}
            mais_termo_logico();
        }
    }

    private void fator_logico() {
        if (token.getClasse() == Classe.cParEsq) {
            token = lexico.nextToken();
            expressao_logica();
            if (token.getClasse() == Classe.cParDir) {
                token = lexico.nextToken();
            } else {
                System.out.println(token.getLinha() + "," + token.getColuna() + ", Parênteses Direito  ) esperado");
            }
        } else {
            if (palavraReservada(token, "not")) {
                token = lexico.nextToken();
                fator_logico();
                // {A28}
            } else {
                if (palavraReservada(token, "true")) {
                    token = lexico.nextToken();
                    // {A29}
                } else {
                    if (palavraReservada(token, "false")) {
                        token = lexico.nextToken();
                        // {A30}
                    } else {
                        relacional();
                    }
                }
            }
        }
    }

    private void relacional() {
        expressao();
        if (token.getClasse() == Classe.cIgual) {
            token = lexico.nextToken();
            expressao();
            // {A31}
        } else {
            if (token.getClasse() == Classe.cMaior) {
                token = lexico.nextToken();
                expressao();
                // {A32}
            } else {
                if (token.getClasse() == Classe.cMaiorIgual) {
                    token = lexico.nextToken();
                    expressao();
                    // {A33}
                } else {
                    if (token.getClasse() == Classe.cMenor) {
                        token = lexico.nextToken();
                        expressao();
                        // {A34}
                    } else {
                        if (token.getClasse() == Classe.cMenorIgual) {
                            token = lexico.nextToken();
                            expressao();
                            // {A35}
                        } else {
                            if (token.getClasse() == Classe.cDiferente) {
                                token = lexico.nextToken();
                                expressao();
                                // {A36}
                            }
                        }
                    }
                }
            }
        }
    }

    private void expressao() {
        termo();
        mais_expressao();
    }

    private void mais_expressao() {
        if (token.getClasse() == Classe.cAdicao) {
            token = lexico.nextToken();
            termo();
            // {A37}
            mais_expressao();
        } else {
            if (token.getClasse() == Classe.cSubtracao) {
                token = lexico.nextToken();
                termo();
                // {A38}
                mais_expressao();
            }
        }
    }

    private void termo() {
        fator();
        mais_termo();
    }

    private void mais_termo() {
        if (token.getClasse() == Classe.cMultiplicacao) {
            token = lexico.nextToken();
            fator();
            // {A39}
            mais_termo();
        } else {
            if (token.getClasse() == Classe.cDivisao) {
                token = lexico.nextToken();
                fator();
                // {A40}
                mais_termo();
            }
        }
    }

    private void fator() {
        if (token.getClasse() == Classe.cId) {
            token = lexico.nextToken();
            // {A55}
        } else {
            if (token.getClasse() == Classe.cInt) {
                token = lexico.nextToken();
                // {A41}
            } else {
                if (token.getClasse() == Classe.cParEsq) {
                    token = lexico.nextToken();
                    expressao();
                    if (token.getClasse() == Classe.cParDir) {
                        token = lexico.nextToken();
                    } else {
                        System.out.println(
                                token.getLinha() + "," + token.getColuna() + ", Parênteses Direito ( ) esperado");
                    }
                } else {
                    System.out.println(token.getLinha() + "," + token.getColuna()
                            + ", Identificador, Inteiro ou Parênteses Esquerdo ( esperado");
                }
            }
        }
    }

    private void id() {
        if (token.getClasse() == Classe.cId) {
            token = lexico.nextToken();
            // {A55}
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Identificador esperado");
        }
    }

    private void intnum() {
        if (token.getClasse() == Classe.cInt) {
            token = lexico.nextToken();
            // {A41}
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", Inteiro esperado");
        }
    }

    private void string() {
        if (token.getClasse() == Classe.cString) {
            token = lexico.nextToken();
            // {A59}
        } else {
            System.out.println(token.getLinha() + "," + token.getColuna() + ", String esperado");
        }
    }

}