package pns.kiam.commonserrvice;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import pns.fileUtils.DirectoryDeepGo;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;
import pns.kiam.sweb.controllers.archvedb.ArchiveFileController;

/**
 *
 * @author User
 */
@Stateless
public class RemoverDuplicates {

    private FileSpecActor fsa = new FileSpecActor();
    private FileActor fa = new FileActor();
    private DirectoryDeepGo ddg = new DirectoryDeepGo();
    private String rootDir = "";
    private List<File> lastFL = new ArrayList<>();
    private long maxFileAge = 1000 * 3600 * 24;
    private int fileAgeInDays = 3;
    private List<File> dupl = new ArrayList<>();

    ;

    @Inject
    private ArchiveFileController fvc;

    public void removeDupleFiles() {
        lastFL.clear();
        dupl = new ArrayList<>();
        long d = System.currentTimeMillis();
        long d1 = d - maxFileAge;
        System.out.println(" ----->> Now: " + new Date() + "   " + maxFileAge + " milisec ago " + new Date(d1));

        // prepaering and creating the list of files,
        // which are candidates to removing
        //The argument here is number of days which we are observing
        prepareRemoveFiles(fileAgeInDays);

        // search dubbed content in filesand then remove that dubles
        dupl = getDuple(lastFL);
    }

    /**
     * Sets the age in days of file. if the file has a dubbed content and it is
     * lives less then fileAgeInDays it will be removed
     *
     * @param fileAgeInDays
     */
    public void setFileAgeInDays(int fileAgeInDays) {
        this.fileAgeInDays = fileAgeInDays;
    }

    /**
     * every 15 days we are investigate here the existence of possible
     * doublicate files and remove them. The time deep is 33 days
     */
    public void removeSuperOldfiles() {

        // prepaering and creating the list of files,
        // which are candidates to removing
        prepareRemoveFiles(33);
        // search dubbed content in filesand then remove that dubles
        dupl = getDuple(lastFL);
    }

    /**
     * prepares an age, from which we need to collect filelist and then generate
     * correspondent list of files
     *
     * @param numberOfDays - number of days before current moment
     */
    private void prepareRemoveFiles(int numberOfDays) {
        if (numberOfDays < 1) {
            numberOfDays = 2;
        }
        long d = System.currentTimeMillis();
        Date dd = new Date(d);

        DateFormat df = new SimpleDateFormat("dd");
        String ds = df.format(d);
        if (ds.equals("01") || ds.equals("15")) {
            numberOfDays = 33;
        }

        long d1 = d - maxFileAge * numberOfDays;
        dupl = new ArrayList<>();
        rootDir = fa.getAppRootPath(true);
        //ddg.setRootDir(rootDir + "/satdata");
        ddg.goDeep(rootDir + "/satdata", true);
        System.out.println("");
        System.out.println("    ************   Remove  prepare  ******* ");
        System.out.println("           Prepare to remove duple content files"
                + " in " + rootDir + "/satdata, "
                + " created  after " + new Date(d1) + "... ");
// refresh files, that older then dl
        getFilesAfter(d1);

    }

    /**
     * generate list of files, that older then a given age
     *
     * @param age
     * @return
     */
    private void getFilesAfter(long age) {
        lastFL.clear();
        List<File> fl = ddg.getFileList();
        Date dd = new Date(age);
        System.out.println(" ================== Files,  which are older then " + dd + "   ==================   ");
//        System.out.println("  fl.size " + fl.size());
        for (int k = 0; k < fl.size(); k++) {
            if (fl.get(k).lastModified() > age) {
                lastFL.add(fl.get(k));
                System.out.println(k + " **=====> File to Investigate " + fl.get(k).getAbsolutePath() + "  Modified " + new Date(fl.get(k).lastModified()));
            } else {
                System.out.println(k + "  <===== File omited Investigate " + fl.get(k).getAbsolutePath() + "  Modified " + new Date(fl.get(k).lastModified()));
            }
        }
    }

    /**
     * Generate a list of file, which have duple content As soon as the dubbed
     * found ( look for 'hasContent' method) the file removes from the hard
     *
     * @param fl
     * @return
     */
    private List<File> getDuple(List<File> fl) {

        List<File> res = new ArrayList<>();
        for (int k = 0; k < fl.size(); k++) {
            File f = fl.get(k);
            if (f.exists()) {
                if (f.isFile()) {
                    boolean exists = false;
                    exists = hasContent(f, fl);
                    if (exists) {
                        //System.out.println("   " + f.getAbsolutePath());
                        res.add(f);
                    }
                }
            }
        }

        System.out.println(" Found   " + res.size() + " of dubbed in " + fl.size() + " files ");

        return res;
    }

    /**
     * Tests, has the file f the same content as in some file of list of files
     * fl. if so, the file removes from the hard
     *
     * @param f
     * @param fl
     * @return
     */
    private boolean hasContent(File f, List<File> fl) {
        System.out.println("");
        System.out.println("     Method " + this.getClass().getCanonicalName() + ".hasContent(File f, List<File> fl)");
        System.out.println("     Checking, has the file f the same content as in some file of list of files. if so, the file removes from the hard");
        if (f.isFile()) {

            if (f.length() < 2) {
                System.out.println(" The size of file " + f.getAbsolutePath() + " is too small. This file removes from the hard");
                f.delete();
                return false;
            }
            FileActor testFA = new FileActor();
            String testContent = "";
            if (f.exists()) {
                testFA.fileRead(f.getAbsolutePath());
            }
            String tss = " ";
            testContent = testFA.getFileContent();
            tss += " TEST CONTENT " + testContent;
////            System.out.println(tss);
            tss = "";
            testContent = pns.utils.strings.RStrings.removeSpaces(testContent);
            for (int k = 0; k < fl.size(); k++) {
                File tmpf = fl.get(k);
                boolean sameFile = f.getAbsolutePath().trim().equals(tmpf.getAbsolutePath().trim());
//                tss += " --> f: " + f.getAbsolutePath();
//                tss += System.lineSeparator() + "  same: "
//                        + "       " + sameFile + " ; " + tmpf.getAbsolutePath();
//                tss += System.lineSeparator() + "  TEST content " + System.lineSeparator()
//                        + testContent
//                        + System.lineSeparator();

                //System.out.println(tss);
                if (!sameFile) {
                    FileActor tmpFA = new FileActor();
                    tmpFA.fileRead(tmpf.getAbsolutePath());
                    String tmpContent = tmpFA.getFileContent();
//                    String ttt = "asd vfr  ttr jhihui jjghju  jgu  jhuihu" + System.lineSeparator() + "L  OO 987  77 gEEE ";
//                    System.out.println(" ttt Str " + ttt);
//                    ttt = pns.utils.RStrings.removeSpaces(ttt);
//                    System.out.println("    ttt REMOVE SPACES  " + ttt);
                    tss += "     TMP content" + System.lineSeparator() + tmpContent + System.lineSeparator();

                    tmpContent = pns.utils.strings.RStrings.removeSpaces(tmpContent);

                    String[] tmpParts = tmpContent.split(testContent);
                    String[] testParts = testContent.split(tmpContent);
                    int has = testParts.length + tmpParts.length;
                    boolean res = testContent.equals(tmpContent);

//                    System.out.println(k + System.lineSeparator() + tss + System.lineSeparator() + " result " + res + "  has: " + has);
                    if (res) {
                        if (tmpf.exists()) {
                            String tmpfParentName = tmpf.getParentFile().getAbsolutePath();
                            if (tmpf.delete()) {
                                dupl.remove(tmpf);
                                System.out.println(" The file " + tmpf.getName() + " has the dubbed content and then  deleted");
                            }

                        }
                        return true;
                    }
                }

            }
//            System.out.println("");
//            System.out.println("");
//            System.out.println("**************************");
        } else {
        }
        return false;
    }

    /**
     * Search the equal content of given file f in the file list fl
     *
     * @param f
     * @param fl
     * @return
     */
    private boolean hasContent0(File f, List<File> fl) {
        //    System.out.println("  f: " + f.getAbsolutePath());
        if (f.isFile()) {

            FileActor testFA = new FileActor();
            String testContent = "";
            testFA.fileRead(f.getAbsolutePath());
            testContent = testFA.getFileContent();
//            System.out.println(" **********>>>testFA.getFileContent().length() " + testFA.getFileContent().length() + "    fl.size() " + fl.size());
//            System.out.println(f.getAbsoluteFile() + System.lineSeparator() + "   TEST CONTENT " + testContent);
            for (int k = 0; k < fl.size(); k++) {
                System.out.println("     %%%---->>>>>! >> " + k);
                String tmpContent = "";
                File tmpf = fl.get(k);
                if (!f.getAbsoluteFile().equals(tmpf.getAbsoluteFile())) {
//                    System.out.println(k + "$$$ f.getAbsoluteFile()  " + f.getAbsoluteFile());
//                    System.out.println(k + "### tmpf.getAbsoluteFile()  " + tmpf.getAbsoluteFile());
                    FileActor tmpFA = new FileActor();
                    tmpFA.fileRead(tmpf.getAbsolutePath());
                    tmpContent = tmpFA.getFileContent();
//                    tmpContent = "asd";
//                    testContent = "asd asd gfr dew asd";

                    String[] tmpParts = tmpContent.split(testContent);
                    String[] testParts = testContent.split(tmpContent);
                    int has = testParts.length + tmpParts.length;
                    boolean ravno = tmpContent.equals(testContent);
//                    if (k > 2) {
//
//                        System.out.println("");
//                        System.out.println("");
//                        System.out.println(
//                                k + "=====>>f " + f.getAbsolutePath() + System.lineSeparator()
//                                + "==>>tmpf " + tmpf.getAbsolutePath() + System.lineSeparator()
//                                + " testContent " + testContent
//                                + System.lineSeparator() + " ******************** "
//                                + System.lineSeparator()
//                                + " tmpContent  " + tmpContent
//                                + System.lineSeparator() + "******************* " + System.lineSeparator()
//                                + ravno + "   tmpParts.length " + tmpParts.length + "  testParts.length " + testParts.length);
//
//                        System.out.println("");
//                        System.out.println("");
//                    }
                    return ravno;
                }

            }

        }
        return false;
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
