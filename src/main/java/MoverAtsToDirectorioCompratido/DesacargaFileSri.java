/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MoverAtsToDirectorioCompratido;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *
 * @author USUARIO
 */
public class DesacargaFileSri {

    private static final Logger LOGGER = Logger.getLogger(DesacargaFileSri.class.getName());

    public static void main(String[] args) {
        init();
    }
    
    public static void init(){
        
        Catastros();
        DescargaGeneral(Main.getPropiedad("urlAgenteRetencion"),Main.getPropiedad("rutaDescargaFileSriAgenteRetencion"));
        DescargaGeneral(Main.getPropiedad("urlEmpresaFantasma"),Main.getPropiedad("rutaDescargaFileSriEmpresaFantasma"));
        DescargaGeneral(Main.getPropiedad("urlEmpresaExportadoraBienes"),Main.getPropiedad("rutaDescargaFileSriEmpresaExportadoraBienes"));
    }
    
    public static void DescargaGeneral(String url, String ruta){
        try {
                int post = url.lastIndexOf("/") + 1;
                String nameFile = url.substring(post, url.length());                
                descargarZipFileTmpDirectorio(url, new File( ruta+ nameFile));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static void Catastros() {

        try {
            int tam = Integer.parseInt(Main.getPropiedad("urlCantidadProvincias"));
            for (int a = 1; a <= tam; a++) {
                String url = Main.getPropiedad("urlCatastro" + a);
                int post = url.lastIndexOf("/") + 1;
                String nameFile = url.substring(post, url.length());                
                descargarZipFileTmpDirectorio(url, new File(Main.getPropiedad("rutaDescargaFileSriCatastros") + nameFile));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static Boolean descargarZipFileTmpDirectorio(String url, File fileZip) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            try ( okhttp3.Response response = client.newCall(request).execute()) {
//                Configuracion.printConsoleLogInfo(logger_, " code status ===> " + response.code());
                if (response.code() == 200 || response.code() == 201) {
                    response.body().byteStream();
                    try ( BufferedInputStream bufferedInputStream = new BufferedInputStream(response.body().byteStream());  FileOutputStream out = new FileOutputStream(fileZip);  BufferedOutputStream bout = new BufferedOutputStream(out)) {
                        int temp;
                        while ((temp = bufferedInputStream.read()) != -1) {
                            bout.write(temp);
                        }
                        return true;
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, null, e);
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
