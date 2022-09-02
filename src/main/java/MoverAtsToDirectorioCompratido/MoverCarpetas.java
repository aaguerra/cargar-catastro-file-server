package MoverAtsToDirectorioCompratido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

public class MoverCarpetas {

    private static final Logger LOGGER = Logger.getLogger(MoverCarpetas.class.getName());
    
    

    public static void copy(String sSourceLocation, String stargetLocation) throws IOException {
        File sourceLocation = new File(sSourceLocation);
        File targetLocation = new File(stargetLocation);

        if (sourceLocation.isDirectory()) { //Es directorio.
            copyDirectory(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
        } else { //Es archivo.
            copyFile(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
        }
    }

    private static Boolean copyDirectory(File source, File target, String sSourceLocation, String stargetLocation) throws IOException {
        if (!target.exists()) {
            //No existe directorio destino, lo crea.
            target.mkdir();
        }
        for (String f : source.list()) {
            boolean fileIsNotLocked = new File(source, f).renameTo(new File(source, f));
            if (fileIsNotLocked) {
                if (copy(new File(source, f), new File(target, f), sSourceLocation, stargetLocation)) {
                    System.out.println("copyDirectory El archivo se movio correctamente: " + f);
                    new File(source, f).delete();
                } else {
                    System.out.println("copyDirectory El archivo no se puede mover: " + f);

                }
            } else {
                System.out.println("copyDirectory El archivo no se puede abrir porque esta en uso: " + f);
            }
        }
        return true;
    }

    private static Boolean copy(File sourceLocation, File targetLocation, String sSourceLocation, String stargetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            return copyDirectory(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
        } else {
            return copyFile(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
        }
    }

    private static Boolean copyFile(File source, File target, String sSourceLocation, String stargetLocation) throws IOException {
        boolean fileIsNotLocked = source.renameTo(source);
        if (!fileIsNotLocked && source.getName().startsWith("~$")) {
            return false;
        } else {

            //if (SendFileRepository(source, sSourceLocation, stargetLocation)) {
                if (Integer.parseInt(Main.getPropiedad("urlServidorFileEnable")) == 1) {
                    System.out.println("copyDirectory El archivo se movio correctamente 22: " + source.getName());
                    try (
                            InputStream in = new FileInputStream(source);
                            OutputStream out = new FileOutputStream(target)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = in.read(buffer)) > 0) {
                            out.write(buffer, 0, length);
                        }
                        source.delete();
                        return true;
                    } catch (IOException e) {
                        System.out.println(e);
                        LOGGER.log(Level.SEVERE, null, e);
                        return false;
                    }
                } else {
                    return false;
                }
            /*} else {
                return false;
            }*/
        }

    }

    private static Boolean SendFileRepository(File source, String sSourceLocation, String stargetLocation) {
        try {
            // no subo el archivo al servidor
            if (Integer.parseInt(Main.getPropiedad("urlServidorFileEnable")) == 0) {
                return true;
            }
            UnirestInstance unirest = Unirest.primaryInstance();
            // It can be configured and used just like the static context
            unirest.config().socketTimeout(60000).connectTimeout(60000);
            String ruta = source.getAbsolutePath();
            ruta = ruta.replace(sSourceLocation, "");
            ruta = ruta.replace(source.getName(), "");
            ruta = ruta.substring(1, ruta.length() - 1);
            if (ruta.contains("\\")) {
                ruta = ruta.replace("\\", "/");
            }
            HttpResponse<String> response_ = unirest.post(Main.getPropiedad("urlServidorFile"))
                    .field("file", new File(ruta))
                    .field("fileName", source.getName())
                    .field("filePath", ruta)
                    .asString();
            if (response_.getStatus() == 200 || response_.getStatus() == 201) {
                return true;
            }
            return false;
        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, null, e);
            return false;
        }
    }

}
