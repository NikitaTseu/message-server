package com.bsu.security.server.controllers;

import com.bsu.security.server.EncryptionService;
import com.bsu.security.server.encryption.Pair;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping
public class startController {
    private static String pathBase = "C:\\Users\\Nikita\\Desktop\\server_storage\\";
    private static Charset defaultCharset = StandardCharsets.UTF_8;
    private static EncryptionService encryptionService = new EncryptionService();

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @GetMapping("/test")
    public String sendHello(){
        return "hello";
    }

    @PostMapping("/key")
    public byte[] sendKey(@RequestBody byte[] rsaOpenKeyBytes){
        String s = new String(rsaOpenKeyBytes, defaultCharset);
        String tmp[] = s.split(",", 2);
        Pair publicKey = new Pair(Long.valueOf(tmp[0]), Long.valueOf(tmp[1]));

        return this.encryptionService.encryptRSA(publicKey, this.encryptionService.getSessionKey());
        //return this.encryptionService.getSessionKey();
    }

    @GetMapping("/filenames")
    public String getFilenames(){
        File folder = new File(pathBase);
        File[] listOfFiles = folder.listFiles();
        String filenames = "";

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                filenames = filenames.concat(listOfFiles[i].getName());
                filenames = filenames.concat(",");
            }
        }

        return filenames;
    }

    @PostMapping("/load")
    public byte[] loadText(@RequestBody String filename){
        String text = "";
        byte[] bytes = null;
        try {
            text = readFile(pathBase + filename, defaultCharset);
            bytes = encryptionService.encrypt(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @PostMapping("/delete")
    public String deleteText(@RequestBody String filename){
        File file = new File(pathBase + filename);

        if(file.delete()) {
            return "deleted";
        }
        else {
            return "error";
        }
    }

    @PostMapping("/create")
    public String createText(@RequestBody String filename){
        File file = new File(pathBase + filename);

        try {
            if(file.createNewFile()) {
                return "created";
            }
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
            e.printStackTrace();
        }

        return "error";
    }

    @PostMapping("/save")
    public String saveText(@RequestBody byte[] dataBytes){
        String data = encryptionService.decrypt(dataBytes);
        String tmp[] = data.split("\n", 2);
        String filename = tmp[0];
        String text = tmp[1];

        File file = new File(pathBase + filename);
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getStackTrace());
            e.printStackTrace();
            return "error";
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(pathBase + filename);
            byte[] strToBytes = text.getBytes(defaultCharset);
            outputStream.write(strToBytes);
            outputStream.close();
            return "saved";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
