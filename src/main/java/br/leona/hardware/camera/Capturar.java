/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.leona.hardware.camera;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;

/**
 *
 * @author Leona_Lenovo
 */
public class Capturar extends Thread {

    private final CameraController cameraController;
    String nfile;
    BufferedImage bi;
    LocalDateTime agora;

    Capturar(CameraController cameraController) {
        this.cameraController = cameraController;
    }

    @Override
    public void run() {
        System.out.println("hw-");
        Date data = new Date();

        String novaData = data.toString();
        String formatada = "" + novaData.charAt(11) + novaData.charAt(12) + novaData.charAt(14) + novaData.charAt(15) + novaData.charAt(17) + novaData.charAt(18);

        String diretorio = "C:/ProjetoLeona/";
        String observacao = "Evento_"
                + "" + localDate().format(DateTimeFormatter.ofPattern("ddMMyyyy"))
                + "_" + formatada
                + "/";
        String imagem;
        System.out.println("entrei na captura 1************************");
        int[] a = new int[1080000];
        File dir = new File(diretorio + observacao);

        System.out.println("entrei na captura 2************************");
        if (dir.mkdirs()) {

            for (int i = 0; i < a.length; i++) {
                FrameGrabbingControl fgc = (FrameGrabbingControl) cameraController.player.getControl("javax.media.control.FrameGrabbingControl");

                Buffer buffer = fgc.grabFrame();
                System.out.println("entrei na captura**3*********************");
                BufferToImage bti = new BufferToImage((VideoFormat) buffer.getFormat());
                Image image = bti.createImage(buffer);
                bi = (BufferedImage) image;

                Date date1 = new Date(System.currentTimeMillis());
                SimpleDateFormat hora = new SimpleDateFormat("HH mm ss");
                long lDateTime = new Date().getTime();
                FileDialog fd = new FileDialog(new Frame(), " ", FileDialog.SAVE);

                if (i < 10) {
                    imagem = "[" + lDateTime + "]";
                    System.out.println("[0" + i + "-" + lDateTime + "]");
                } else {
                    imagem = "[" + lDateTime + "]";
                    System.out.println("[" + i + "-" + lDateTime + "]");
                }

                fd.setDirectory(diretorio + observacao);

                fd.setFile(imagem);

                if (fd.getFile() != null) {
                    nfile = fd.getDirectory() + fd.getFile() + ".jpg";
                    try {

                        File make = new File(nfile);
                        try {

                            ImageIO.write(bi, "jpg", make);
                        } catch (IOException ex) {

                        }
                    } catch (Exception ex) {
                    }
                }
            }
        } else {
            System.out.println("Nao foi possivel criar o diretorio");
        }

    }

    private LocalDate localDate() {

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd MM yyyy");
        hoje.format(formatador); // exemplo 08/04/2014
        return hoje;
    }

}
