/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MoverAtsToDirectorioCompratido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author USUARIO
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final Properties prop;

    static {
        String filePath = "";
        prop = new Properties();
        InputStream entrada = null;
        try {
            if (filePath != null) {
                if (!filePath.isEmpty()) {
                    entrada = new FileInputStream((!filePath.isEmpty()) ? filePath : "application.properties");
                } else {
                    entrada = getFileResource("application.properties");
                }
            } else {
                entrada = new FileInputStream("application.properties");
            }
            prop.load(entrada);

        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.log(Level.SEVERE, null, fileNotFoundException);
        } catch (Exception iOException) {
            LOGGER.log(Level.SEVERE, null, iOException);
        }
    }

    public static InputStream getFileResource(String name_file) {
        try {
            InputStream inputStream = Resources.getResourceAsStream(name_file);
            return inputStream;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static String getPropiedad(String propertie) {
        return prop.getProperty(propertie);
    }

    public static void main(String[] args) {

        init(
                Main.getPropiedad("rutaFuenteCatastros"),
                Main.getPropiedad("rutaPorProcesarCatastros"),
                Main.getPropiedad("rutaProcesadoCatastros"),
                Main.getPropiedad("urlServidorFileCatastros"),
                Integer.parseInt(Main.getPropiedad("urlApiDataTipoCatastro"))
        );

        init(
                Main.getPropiedad("rutaFuenteAgenteRetencion"),
                Main.getPropiedad("rutaPorProcesarAgenteRetencion"),
                Main.getPropiedad("rutaProcesadoAgenteRetencion"),
                Main.getPropiedad("urlServidorFileAgenteRetencion"),
                Integer.parseInt(Main.getPropiedad("urlApiDataTipoAgenteRetencion"))
        );
        
        init(
                Main.getPropiedad("rutaFuenteEmpresaExportadoraBienes"),
                Main.getPropiedad("rutaPorProcesarEmpresaExportadoraBienes"),
                Main.getPropiedad("rutaProcesadoEmpresaExportadoraBienes"),
                Main.getPropiedad("urlServidorFileEmpresaExportadoraBienes"),
                Integer.parseInt(Main.getPropiedad("urlApiDataTipoEmpresaExportadoraBienes"))
        );
        
        init(
                Main.getPropiedad("rutaFuenteEmpresaFantasma"),
                Main.getPropiedad("rutaPorProcesarEmpresaFantasman"),
                Main.getPropiedad("rutaProcesadoEmpresaFantasma"),
                Main.getPropiedad("urlServidorFileEmrpesaFantasma"),
                Integer.parseInt(Main.getPropiedad("urlApiDataTipoEmpresaFantasta"))
        );

    }

    public static void init(
            String rutaFuente,
            String rutaPorProcesar,
            String rutaProcesado,
            String rutaFileServer,
            Integer tipoFileSri
    ) {

        System.out.println("rutaFuente=" + rutaFuente);
        System.out.println("rutaPorProcesar=" + rutaPorProcesar);
        System.out.println("rutaProcesado=" + rutaProcesado);
        System.out.println("rutaFileServer=" + rutaFileServer);
        System.out.println("tipoFileSri=" + tipoFileSri);
        LOGGER.info("rutaFuente=" + rutaFuente);
        LOGGER.info("rutaPorProcesar=" + rutaPorProcesar);
        LOGGER.info("rutaProcesado=" + rutaProcesado);
        LOGGER.info("rutaFileServer=" + rutaFileServer);
        LOGGER.info("tipoFileSri=" + tipoFileSri);
        procesarDirectorio(
                rutaFuente,
                rutaPorProcesar
        );
        loadFile(
                rutaPorProcesar,
                rutaProcesado,
                rutaFileServer,
                tipoFileSri
        );
    }

    public static void loadFile(String rutaPorProcesar, String rutaProcesado, String rutaFileServer, Integer tipoFileSri ) {
        try {
            Set<String> lista = listFilesUsingFileWalkAndVisitor(rutaPorProcesar);

            lista.forEach(a -> {
                System.out.println(a);
                try {
                    String[] data = a.split("_");
                    if (data.length >= 2) {
                        String name = data[0] + a.substring(a.lastIndexOf("."), a.length());
                        if (SendFileRepository(
                                new File(rutaPorProcesar + File.separator + a),
                                rutaFileServer,
                                name
                        )) {                            
                            if(SendDataFileRepository(data[0],rutaFileServer+"/"+data[0]+".zip",tipoFileSri)){
                                moveFile(rutaPorProcesar + File.separator + a, rutaProcesado + File.separator + a);
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            });

//            copyFile(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
//            MoverCarpetas.copy(rutaFuente, rutaProcesado);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            System.out.println(e);
        }
    }

    public static void procesarDirectorio(String rutaFuente, String rutaPorProcesar) {

        try {
            Set<String> lista = listFilesUsingFileWalkAndVisitor(rutaFuente);

            lista.forEach(a -> {
                System.out.println(a);
                try {
                    moveFile(rutaFuente + File.separator + a, rutaPorProcesar + File.separator + UUID.randomUUID().toString() + "_" + a);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            });

//            copyFile(sourceLocation, targetLocation, sSourceLocation, stargetLocation);
//            MoverCarpetas.copy(rutaFuente, rutaProcesado);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            System.out.println(e);
        }
    }

    private static Boolean SendFileRepository(File source, String sSourceLocation, String namefile) {
        try ( UnirestInstance unirest = Unirest.primaryInstance();) {
            // no subo el archivo al servidor
            if (Integer.parseInt(Main.getPropiedad("urlServidorFileEnable")) == 0) {
                return true;
            }

            // It can be configured and used just like the static context
            unirest.config().socketTimeout(60000).connectTimeout(60000);

            System.out.println("---------------" + source.getName());
            System.out.println("---------------" + Main.getPropiedad("urlServidorFile"));
            System.out.println("---------------" + sSourceLocation);
            System.out.println("---------------" + namefile);

            HttpResponse<String> response_ = unirest.post(Main.getPropiedad("urlServidorFile"))
                    .field("file", source)
                    //.field("file", new File("/C:/Users/USUARIO/Downloads/AZUAY/AZUAY-v1.txt"))
                    .field("fileName", namefile)
                    .field("filePath", sSourceLocation)
                    .asString();

            System.out.println("---------------" + response_.getStatus());
            if (response_.getStatus() == 200 || response_.getStatus() == 201) {
                return true;
            }
            return false;
        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, null, e);
            return false;
        }
    }

    private static Boolean SendDataFileRepository(String idFile, String namefile, Integer tipoFileSri) {
        try ( UnirestInstance unirest = Unirest.primaryInstance();) {

            // It can be configured and used just like the static context
            unirest.config().socketTimeout(60000).connectTimeout(60000);

            System.out.println("---------------" + idFile);
            System.out.println("---------------" + Main.getPropiedad("urlApiData"));
            System.out.println("---------------" + tipoFileSri);
            System.out.println("---------------" + namefile);

            HttpResponse<String> response_ = unirest.post(Main.getPropiedad("urlApiData"))
                    .header("Content-Type", "application/json")
                    .body("{ \"idFileSri\": \""+idFile+"\", \"nameFile\": \""+namefile+"\",  \"tipoFileSriId\": "+tipoFileSri+"}")
                    .asString();

            System.out.println("---------------" + response_.getStatus());
            if (response_.getStatus() == 200 || response_.getStatus() == 201) {
                return true;
            }
            return false;
        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, null, e);
            return false;
        }
    }

    public static boolean moveFile(String sourcePath, String targetPath) throws IOException {
        boolean fileMoved = true;

        Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);

        return fileMoved;
    }

    public static Set<String> listFilesUsingFileWalkAndVisitorV2(String dir) throws IOException {
        Set<String> fileList = new HashSet<>();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {
                    fileList.add(file.getFileName().toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    public static Set<String> listFilesUsingFileWalkAndVisitor(String dir) throws IOException {
        Set<String> fileList = new HashSet<>();
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (!Files.isDirectory(file)) {
                    fileList.add(file.getFileName().toString());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }
}
