/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.satelites;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileSpecActor;
import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.controllers.app.XXParserSWEB;

/**
 *
 * @author User
 */
@Singleton
@Startup
public class FileMeasuredController extends AbstractController implements Serializable {

    private String archPath = "";
    private DirectoryDeepGo ddg = new DirectoryDeepGo();
    private FileSpecActor fsa = new FileSpecActor();

    public String getArchPath() {
        return archPath;
    }

    public void setArchPath(String archPath) {
        this.archPath = archPath;
    }

    public void readArchiveFileDir() {

        ddg.setRootDir(archPath);
        ddg.setDirToInvestigate("/");

        ddg.goDeep();
        System.out.println("  ddg.getDirToInvestigate()   " + ddg.getDirToInvestigate() + "  ddg.getSubDirList().size()  " + ddg.getSubDirList().size());
        for (int k = 0; k < ddg.getFileList().size(); k++) {
            File f = ddg.getFileList().get(k);
            long mm = f.lastModified();
            String tmp = ddg.getFileList().get(k).getAbsolutePath();
            tmp = tmp.replace('\\', '/');
            String[] pathPropers = tmp.split(archPath);
            System.out.println(tmp + "      pathPropers.length   " + pathPropers.length);
            String[] pathParts = pathPropers[1].split("/");
            //System.out.println("           +pathParts.length " + pathParts.length);
            String YYYY = pathParts[1];
            String DDDD = pathParts[2];
            String fileName = pathParts[pathParts.length - 1].split("\\.")[0];
            System.out.println("YYYY:  " + YYYY + "     DDDD:  " + DDDD);
            if (fsa.fileRead(tmp)) {
                String c = fsa.getFileContent().trim();
                int y = gettingIntFromSTR(YYYY);
                int m = gettingIntFromSTR(DDDD.split("-")[0]);
                int d = gettingIntFromSTR(DDDD.split("-")[1]);
                System.out.println((new Date()) + " ;   file Modified     " + new Date(mm));
                FileMeasured fm = new FileMeasured(y, m, d, c, fileName, mm);

//		System.out.println(k + " c==null " + c.length());
//		System.out.println(k + " y " + fm.getYear());
//		System.out.println(k + " m " + fm.getMonth());
//		System.out.println(k + " d " + fm.getDate());
                persist(fm);
            }
            //
            System.out.println(k + "     =====>>>==>> " + tmp + "   :    ");
        }

    }

    private int gettingIntFromSTR(String s) throws NumberFormatException {
        return Integer.parseInt(s);
    }
}
