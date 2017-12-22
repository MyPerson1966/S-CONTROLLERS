/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.archvedb;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileSpecActor;
import pns.kiam.commonserrvice.RemoverDuplicates;
import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.filecontrol.FileMeasuredController;
import pns.kiam.sweb.controllers.app.XXParserSWEB;

/**
 *
 * @author User
 */
//@Stateful
@LocalBean
public class ArchiveGenerator extends ArchiveFileController {

    @EJB
    private XXParserSWEB xxparser;
    @EJB
    private RemoverDuplicates removerDuplicates;
    @EJB
    private FileMeasuredController fmc;

    private FileSpecActor fsa = new FileSpecActor();
    private DirectoryDeepGo ddg = new DirectoryDeepGo();
    private String tmpFilePath = "";

    /**
     *
     * Generates an archive of uploaded file's content.
     * <br />
     * At first removes dubbed and then puts the rest to a collection to work
     * <br />
     *
     * @return
     */
    protected List<File> createArchiveCollection() {

        System.out.println("--------------------- Creating Archiv/ Step 1: Removing dobbled ---------------" + new Date());
//        System.out.println("  xxparser.getArchivePath() " + xxparser.getArchivePath() + "    xxparser.getMaxDaysFileLive() " + xxparser.getMaxDaysFileLive());

        long ts = System.currentTimeMillis();
        removerDuplicates.setFileAgeInDays(40);
        removerDuplicates.setRootDir(xxparser.getArchivePath());
        removerDuplicates.removeDupleFiles();
        long te = System.currentTimeMillis();
        System.out.println("    " + (te - ts) + " ms");
        List<File> fl = removerDuplicates.getFileList();
        System.out.println("  Number of  Files To Collect :  " + fl.size());
        return fl;
    }

    /**
     * Creating a Collection of FileMeasured
     *
     * @param fl
     * @return
     */
    protected List<FileMeasured> fileListToFileMeasuredSet(List<File> fl) {
        System.out.println("--------------------- Creating Archiv/ Step 2: Collect the files to Archive ---------------" + new Date());

        int k = 0;
        List<FileMeasured> fml = null;
        fml = new ArrayList<>();

        for (File f : fl) {
            long mm = f.lastModified();
            String tmp = f.getAbsolutePath();
            tmp = tmp.replace('\\', '/');
            String[] pathPropers = tmp.split(xxparser.getArchivePath());
//            System.out.println(tmp + "      pathPropers.length   " + pathPropers.length);
            String[] pathParts = pathPropers[1].split("/");

            String YYYY = pathParts[1].trim();
            String DDDD = pathParts[2].trim();
            String fileName = pathParts[pathParts.length - 1];
            System.out.println("YYYY:  " + YYYY + "     DDDD:  " + DDDD + "    fileName " + fileName);
            if (fsa.fileRead(tmp)) {
                String c = fsa.getFileContent().trim();

                System.out.println("                        YYYY: " + YYYY);
                int y = gettingIntFromSTR(YYYY);
                int m = -1;
                int d = -1;
                if (DDDD.split("-").length == 2) {
                    m = gettingIntFromSTR(DDDD.split("-")[0]);
                    d = gettingIntFromSTR(DDDD.split("-")[1]);
                }
                System.out.println((new Date()) + " ;   file Modified     " + new Date(mm));

                FileMeasured fm = new FileMeasured();
                fm.setContent(c);

                fm.setFields(y, m, d, c, fileName, mm);

                fml.add(fm);
            }
            k++;
        }

        return fml;
    }

    protected FileMeasured prepareToTnsertInArchive(FileMeasured fm) {
        System.out.println(""
                + "++++++++++++++++++++++++++++++++++++++++++" + System.lineSeparator()
                + " file:   " + fm.getFileName() + System.lineSeparator()
                + "  :   =========>>>   " + fm.getStrHash() + "        " + fm.getIntHash() + System.lineSeparator() + ""
                + "   fm.getFileName() " + fm.getFileName() + System.lineSeparator()
                + "    fm.getDate()" + fm.getDate() + System.lineSeparator()
                + "  fm.getMonth()" + fm.getMonth() + System.lineSeparator()
                + "  fm.getYear() " + fm.getYear()
                + ""
                + "");
        String tmpMonth = "" + fm.getMonth();
        String tmpDate = "" + fm.getDate();
        if (fm.getMonth() < 10) {
            tmpMonth = "0" + tmpMonth;
        }
        if (fm.getDate() < 10) {
            tmpDate = "0" + tmpDate;
        }
        StringBuilder sbf = new StringBuilder();
        sbf.append(xxparser.getArchivePath() + '/');
        sbf.append(fm.getYear() + "/");
        sbf.append(tmpMonth + "-" + tmpDate + "/");
        sbf.append(fm.getFileName());
        getType(fm);
        fm.setFileType(fileType);
        tmpFilePath = sbf.toString();
        return fm;
    }

    protected void removeArchivedFileFromHard() {

        System.out.println("                      *******");
        System.out.println("    Try To Remove  aFile:  " + tmpFilePath);
        System.out.println("                         ******");
        File ff = new File(tmpFilePath);
        if (ff.exists()) {
            if (ff.delete()) {
                System.out.println("  Deleting work have done! ");
            }
        }
    }

    private int gettingIntFromSTR(String s) throws NumberFormatException {
        int res = 0;
        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        return res;
    }
}
