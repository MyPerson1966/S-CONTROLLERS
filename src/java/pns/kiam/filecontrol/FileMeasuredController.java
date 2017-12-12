/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.filecontrol;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileSpecActor;
import pns.kiam.entities.satellites.FileMeasured;

/**
 *
 * @author User
 */
@Stateless
public class FileMeasuredController implements Serializable {

    private String archPath = "";
    private DirectoryDeepGo ddg = new DirectoryDeepGo();
    private FileSpecActor fsa = new FileSpecActor();

    public String getArchPath() {
        return archPath;
    }

    /**
     *
     * @param archPath
     */
    public void setArchPath(String archPath) {
        this.archPath = archPath.replace('\\', '/');
    }

    public Set<FileMeasured> readArchiveFileDir() {

        Set<FileMeasured> fml = null;
        fml = new HashSet<>();

        ddg.setRootDir(archPath);
        ddg.setDirToInvestigate("/");
        ddg.getFileList().clear();

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
            String fileName = pathParts[pathParts.length - 1];
            System.out.println("YYYY:  " + YYYY + "     DDDD:  " + DDDD);
            if (fsa.fileRead(tmp)) {
                String c = fsa.getFileContent().trim();

                int y = gettingIntFromSTR(YYYY);
                int m = gettingIntFromSTR(DDDD.split("-")[0]);
                int d = gettingIntFromSTR(DDDD.split("-")[1]);
                System.out.println((new Date()) + " ;   file Modified     " + new Date(mm));
                FileMeasured fm = new FileMeasured(y, m, d, c, fileName, mm);
                fml.add(fm);
//		System.out.println(k + " c==null " + c.length());
//		System.out.println(k + " y " + fm.getYear());
//		System.out.println(k + " m " + fm.getMonth());
//		System.out.println(k + " d " + fm.getDate());
            }

        }
        return fml;
    }

    private int gettingIntFromSTR(String s) throws NumberFormatException {
        return Integer.parseInt(s);
    }
}
