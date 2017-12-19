/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.commonserrvice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author User
 */
//@Named
@Stateful
public class DownloadContentController implements Serializable {

    /**
     * Creates a new instance of DownloadContentController
     */
    public DownloadContentController() {
    }

    /**
     * Download given file.
     */
    public void downloadFile(File file) throws IOException {
        InputStream fis = new FileInputStream(file);
        byte[] buf = new byte[1024];
        int offset = 0;
        int numRead = 0;
        while ((offset < buf.length) && ((numRead = fis.read(buf, offset, buf.length - offset)) >= 0)) {
            offset += numRead;
        }
        fis.close();
        HttpServletResponse response
                = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        response.getOutputStream().write(buf);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     * Download given file.
     */
    public void downloadData(String data, String target) throws IOException {
//        InputStream fis = new FileInputStream(file);
//        byte[] buf = new byte[1024];
//        int offset = 0;
//        int numRead = 0;
//        while ((offset < buf.length) && ((numRead = fis.read(buf, offset, buf.length - offset)) >= 0)) {
//            offset += numRead;
//        }
//        fis.close();
//        InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8.name()));

        byte buf[] = data.getBytes();
        HttpServletResponse response
                = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + target);
        response.getOutputStream().write(buf);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        FacesContext.getCurrentInstance().responseComplete();
    }
}
