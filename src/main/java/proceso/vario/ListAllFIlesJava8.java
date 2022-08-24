/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proceso.vario;

/**
 *
 * @author USUARIO
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListAllFIlesJava8 {

    public static void main(String[] args) {

        String directory = "";
        String directorySalida = "";
        try {
            directory = args[0] == null ? args[0] : "C:\\Users\\USUARIO\\Downloads\\FEBRERO_SOLO_XML";
            directorySalida = args[1] == null ? args[1] : "C:\\ts\\5";
        } catch (Exception ex) {
            Logger.getLogger(ListAllFIlesJava8.class.getName()).log(Level.SEVERE, null, ex);
            
            directory =  "C:\\Users\\USUARIO\\Downloads\\FEBRERO_SOLO_XML";
            directorySalida = "C:\\ts\\5";
        }
        // Finding only files.
        System.out.println("File Names : ");
        printFileNames(directory, directorySalida);

//        // Finding only directories
//        System.out.println("Folder Names : ");
//        printFolderNames(directory);
        // Filtering file names by a pattern.
        System.out.println("Filtering name by a pattern \"Line\": ");
        filterByPattern(directory);

    }

    private static void printFileNames(String directory, String directorySalida) {

        // Reading the folder and getting Stream.
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            // Filtering the paths by a regualr file and adding into a list.
            List<String> fileNamesList = walk.filter(Files::isRegularFile).map(x -> {
                try {
                    
                    //System.out.println(x.toString());
                    Path path = Paths.get(x.toString());

                    String read  = new String ( Files.readAllBytes( path ) );
                    //System.out.println(read);
                    //System.out.println(obtenerTagXml(read, "claveAcceso"));
                    Path targetPath = Paths.get(directorySalida + File.separator + obtenerTagXml(read, "claveAcceso") + ".xml");

                    Files.move(path, targetPath);

                } catch (Exception ex) {
                    Logger.getLogger(ListAllFIlesJava8.class.getName()).log(Level.SEVERE, null, ex);
                }
                return x.toString();
            }).collect(Collectors.toList());

//            // printing the file nams
//            fileNamesList.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String obtenerTagXml(String pDato, String pTag) {
        String lIniTag = "<" + pTag + ">";
        String lFinTag = "</" + pTag + ">";
        if (pDato.contains(lIniTag)) {
            int idxof = pDato.indexOf(lIniTag);
            int idxofFin = pDato.indexOf(lFinTag);
            return pDato.substring(idxof + lIniTag.length(), idxofFin);
        }
        String lIniTagR = "&lt;" + pTag + "&gt;";
        String lFinTagR = "&lt;/" + pTag + "&gt";
        if (pDato.contains(lIniTagR)) {
            int idxof = pDato.indexOf(lIniTagR);
            int idxofFin = pDato.indexOf(lFinTagR);
            return pDato.substring(idxof + lIniTagR.length(), idxofFin);
        }
        return "";
    }

//    private static void printFolderNames(String directory) {
//
//        // Reading the folder and getting Stream.
//        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {
//
//            // Filtering the paths by a folder and adding into a list.
//            List<String> folderNamesList = walk.filter(Files::isDirectory).map(x -> x.toString())
//                    .collect(Collectors.toList());
//
//            // printing the folder names
//            folderNamesList.forEach(System.out::println);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private static void filterByPattern(String directory) {

        // Reading the folder and getting Stream.
        try (Stream<Path> walk = Files.walk(Paths.get(directory))) {

            // Filtering the paths by a folder and adding into a list.
            List<String> fileNamesList = walk.map(x -> x.toString()).filter(f -> f.contains("Line"))
                    .collect(Collectors.toList());

            // printing the folder names
            fileNamesList.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
