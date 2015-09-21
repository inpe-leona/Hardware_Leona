/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.leona.hardware.controller;

//import br.leona.hardware.file.FileXML;
import br.leona.hardware.file.FileXML;
import gnu.io.CommPortIdentifier;
import java.util.Enumeration;

/**
 *
 * @author leona
 */
public final class PTZController {

    private String fileName;
    private FileXML fileXML;
    private SerialPort serialPort;
    private String portaCOM;
    CommPortIdentifier portas = null;
    String left, right, up, down, zeroGraus;
    int AzGraus, ElGraus, valorAtual; // AzimuteGraus e ElevacaoGraus

    public PTZController() {
        searchPorts();
        serialPort = new SerialPort(portaCOM, 9600);
        fileName = "c:/ProjetoLeona/pantilt.xml";
        System.out.println("receberDados: " + recebeDados());
        fileXML = new FileXML();
        fileXML.writeFile(fileName, serialPort.getServico());
    }

    /*
     * Buscar todas as USB Serial Port
     */
    public void searchPorts() {
        try {
            Enumeration pList = CommPortIdentifier.getPortIdentifiers();
            System.out.println("Porta =: " + pList.hasMoreElements());
            while (pList.hasMoreElements()) {
                portas = (CommPortIdentifier) pList.nextElement();
                if (portas.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    System.out.println("Serial USB Port: " + portas.getName());
                } else if (portas.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
                    System.out.println("Parallel Port: " + portas.getName());
                } else {
                    System.out.println("Unknown Port: " + portas.getName());
                }
                portaCOM = portas.getName();
            }
            System.out.println("Porta escolhida =" + portaCOM);
        } catch (Exception e) {
            System.out.println("*****Erro ao escolher a porta******");
        }
    }
    /*
     Calculo da Azimute
     */

    public int calculoAzimuteElevacao(int graus, String coordenada) {
        System.out.println("graus digitado:" + AzGraus);
        System.out.println("graus digitado:" + graus);
        int retorno = 0;
        try {
            if (coordenada.equals("azimute")) {

                System.out.println("*****************AZIMUTE*******************");
                if (graus >= 0 && graus < 351) {
                    if (AzGraus < graus) {
                        int c = graus - AzGraus;
                        System.out.println("Calculo =" + graus + "-" + AzGraus + " = " + c);
                        retorno = right(c);
                    } else if (AzGraus > graus) {
                        int dif = AzGraus - graus;
                        System.out.println("Calculo =" + AzGraus + "-" + graus + " = " + dif);
                        retorno = left(dif);
                    } else if (graus == 0) {
                        int dif = AzGraus - AzGraus;
                        System.out.println("Calculo =" + AzGraus + "-" + AzGraus + " = " + dif);
                        retorno = left(dif);
                    }
                }
                AzGraus = graus;
            } else {
                System.out.println("*****************ELEVAÇÃO*******************");
                if (graus >= 1 && graus <= 39) {
                    System.out.println("**********Valores Positivos**********");
                    if (valorAtual < graus) {

                        int c = graus - valorAtual;
                        up(c);
                        System.out.println("1 Calculo: " + graus + " - " + valorAtual + " = " + c);
                    } else if (valorAtual > graus) {
                        int dif = valorAtual - graus;
                        down(dif);
                        System.out.println("1.0 Calculo: " + valorAtual + " - " + graus + "= " + dif);
                    }
                    valorAtual = graus;
                    System.out.println("1 Valor salvo: " + valorAtual);
                    System.out.println("****************************");

                }
                if (graus <= -1 && graus >= -39) {
                    System.out.println("**********Valores Negativos**********");
                    if (valorAtual < graus) {

                        int c = graus - valorAtual;
                        up(c);
                        System.out.println("3 Calculo: " + graus + " - " + valorAtual + " = " + c);
                    } else if (valorAtual > graus) {
                        int dif = valorAtual - graus;
                        down(dif);
                        System.out.println("3.0 Calculo: " + valorAtual + " - " + graus + "= " + dif);
                    }
                    valorAtual = graus;
                    System.out.println("3 Valor salvo: " + valorAtual);
                    System.out.println("****************************");
                } else if (graus == 0) {
                    if (valorAtual < graus) {
                        int c = -valorAtual;
                        System.out.println("c antes do up = " + c);
                        up(c);
                        System.out.println(" 2.0 valor que estava armazenado era....entao " + valorAtual + "- ( " + valorAtual + " ) =" + c);
                    } else if (valorAtual > graus) {
                        int dif = valorAtual;
                        down(dif);
                        System.out.println(" 2.1 valor que estava armazenado era....entao " + valorAtual + "- ( " + valorAtual + " )=" + dif);
                    }
                    valorAtual = graus;
                    System.out.println("2 Sai do IF valor atual deveria ser: " + valorAtual);
                }
            }
            valorAtual = graus;

        } catch (Exception e) {
            System.out.println("*****Erro ao calcular azimute 0º a 350º*  e Elevação -35º a 35ºº*****");
            retorno = 0;
        }
        return retorno;
    }
    /*
     *Move para Esquerda
     */

    public int left(int graus) {
        if (graus < 351) { //limite de elevação 270º
            if (graus < 10) {
                left = "!00" + graus + "L*";
                System.out.println("LEFT = " + left);
                return serialPort.enviaDados(left);
            } else if (graus >= 10 && graus < 100) {
                left = "!0" + graus + "L*";
                System.out.println("LEFT = " + left);
                return serialPort.enviaDados(left);
            } else {
                left = "!" + graus + "L*";
                System.out.println("LEFT = " + left);
                return serialPort.enviaDados(left);
            }
        } else {
            System.out.println(" EXCEDE O LIMITE DE AZIMUTE PERMITIDO");
            return 0;
        }
    }

    /*
     *Move para Direita
     */
    public int right(int graus) {
        if (graus < 351) { //limite de elevação 270º
            if (graus < 10) {
                String right1 = "!00" + graus + "R*";
                System.out.println("RIGHT = " + right1);
                return serialPort.enviaDados(right1);
            } else if (graus >= 10 && graus < 100) {
                String right2 = "!0" + graus + "R*";
                System.out.println("RIGHT = " + right2);
                return serialPort.enviaDados(right2);
            } else {
                String right3 = "!" + graus + "R*";
                System.out.println("RIGHT = " + right3);
                return serialPort.enviaDados(right3);
            }
        } else {
            System.out.println(" EXCEDE O LIMITE DE AZIMUTE PERMITIDO");
            return 0;
        }
    }

    /*
     *Move para Cima
     */
    public int up(int graus) {
        if (graus < 71) { //limite de elevação 85º
            if (graus < 10) {
                String up1 = "!00" + graus + "U*";
                System.out.println("UP = " + up1);
                return serialPort.enviaDados(up1);
            } else {
                String up2 = "!0" + graus + "U*";
                System.out.println("UP = " + up2);
                return serialPort.enviaDados(up2);
            }
        } else {
            System.out.println(" EXCEDE O LIMITE DE ELEVAÇÃO PERMITIDO");
            return 0;
        }
    }

    /*
     *Move para Baixo
     */
    public int down(int graus) {
        if (graus < 71) { //limite de elevação 85º
            if (graus < 10) {
                String x3 = "!00" + graus + "D*";
                System.out.println("DOWN = " + x3);
                return serialPort.enviaDados(x3);
            } else {
                String x3 = "!0" + graus + "D*";
                System.out.println("DOWN = " + x3);
                return serialPort.enviaDados(x3);
            }
        } else {
            System.out.println(" EXCEDE O LIMITE DE ELEVAÇÃO PERMITIDO");
            return 0;
        }
    }

    /*
     *Liga a camera
     */
    public int cameraOn() {
        System.out.println("hw- ligarCamera");
        return serialPort.enviaDados("!111O*");//camera ON        
    }

    /*
     *Liga desliga a camera
     */
    public int cameraOff() {
        System.out.println("hw- desligarCamera");
        return serialPort.enviaDados("!111F*"); //camera OFF
    }

    /*
     *Reset o pantilt para 0º e camera (a definir a posição) para Posição Inicial
     */
    public int reset() {
        System.out.println("Reset");
        return serialPort.enviaDados("!111S*");
    }

    /*
     * Chamada do método que fecha a comunicação com a porta serial
     */
    public int close() {
        System.out.println("close");
        return serialPort.close();
    }

    /*
     *Reset o pantilt para 0º e camera 0º para Posição Inicial
     */
    public int resetPantilt() {
        down = "!001S*"; // ajustará para 0 graus a elevação
        left = "!350L*";

        if (serialPort.enviaDados(down) == 1) {

            return serialPort.enviaDados(left);
            // }
        } else {
            return 0;
        }
    }
    /*
     * testa se o arduino está ativo ou inativo
     */

    public int recebeDados() {
        return serialPort.recebeDados();
    }

}
