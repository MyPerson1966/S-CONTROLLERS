package pns.kiam.sweb.controllers.satelites;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.print.Collation;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
//import pns.common.RemoverDuplicatesTimeTable;
//import pns.controllers.FileDownloadController;
//import pns.controllers.FileUploadController;

import pns.fileUtils.FReader;
import pns.fileUtils.FileActor;
import pns.fileUtils.FileSpecActor;
import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.entities.telescopes.Telescope;
import pns.kiam.filecontrol.FileMeasuredController;
import pns.kiam.sweb.controllers.AbstractController;

/**
 *
 * @author User
 */
@SessionScoped
public class ArchiveViewController extends AbstractController implements Serializable {

    private List<FileMeasured> fmList = new ArrayList<>();
    private FileMeasured fmCurr;
    protected CriteriaBuilder cb;
    private CriteriaQuery<FileMeasured> cq;

    private String tmpName = "";
    private String filterValue = "";

//    protected EntityManagerFactory emfA = Persistence.createEntityManagerFactory("satelliteBandlePU");
//    //@PersistenceContext
//    protected EntityManager emA = emfA.createEntityManager();
    @PostConstruct
    public void init() {
        loadFileList();
        // Collections.sort(fmList);
    }

    private List loadFileList() {

        String qlString = " SELECT fm FROM FileMeasured fm ";
        Query query = em.createQuery(qlString, FileMeasured.class);
        fmList = query.getResultList();
        System.out.println("     ");
        System.out.println("         fmList.size()   " + fmList.size());
//        try {
//            Root<FileMeasured> res = cq.from(FileMeasured.class);
//            cq.select(res);
//            cq.orderBy(cb.asc(res.get("id")));
//            TypedQuery<FileMeasured> Q = emA.createQuery(cq);
//            System.out.println("       NOTNULL");
//            return Q.getResultList();
//        } catch (NullPointerException e) {
//        }
//        System.out.println("NULL !!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return null;//Q.getResultList();
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<FileMeasured> q = cb.createQuery(FileMeasured.class);
//        Root<FileMeasured> c = q.from(FileMeasured.class);
//        q.select(c);
//        TypedQuery<FileMeasured> query = em.createQuery(q);
//        fmList = query.getResultList();
    }

    public List<FileMeasured> getFmList() {
        return fmList;
    }

    public void setFmList(List<FileMeasured> fmList) {
        this.fmList = fmList;
    }

    public FileMeasured getFmCurr() {
        return fmCurr;
    }

    public void setFmCurr(FileMeasured fmCurr) {
        this.fmCurr = fmCurr;
    }

    public void selectFile(FileMeasured fm) {
        this.fmCurr = fm;
        genFileFromRec();
        System.out.println("    fm  " + fmCurr.getFileName() + "    tmpName  " + tmpName);
    }

    public void deSelect() {
        this.fmCurr = null;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void recDownload(FileMeasured fm) {
        selectFile(fm);
        File f = new File(tmpName);
        System.out.println("Download: " + f.getAbsolutePath());
//        try {
//            fdc.downloadFile(f);
//        } catch (IOException ex) {
//            Logger.getLogger(ArchiveViewController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        f.delete();
    }

    private void genFileFromRec() {
        if (fmCurr != null) {
            String tmpdir = "tmp/";
            FileSpecActor fsa = new FileSpecActor();
            fsa.createDir(tmpdir);
            tmpName = tmpdir + pns.utils.strings.RStrings.rndString(3, 'a', 'z') + "_" + fmCurr.getFileName();
            fsa.setFullFileName(tmpName);
            //System.out.println("  fsa.getFullFileName() " + fsa.getFullFileName());
            fsa.fileWrite(fmCurr.getContent());
        }
    }

    /**
     * Generates an archive of uploaded file's content.
     * <br />
     * At first removes dubbed and then puts the file's content into the
     * database
     * <br />
     * The archived files are removing from the hard
     *
     * @return
     * @throws Exception
     */
    public String createArchiveREC() {
        System.out.println("--------------------- Creating Archiv/ Step 1: Removing dobbled ---------------" + new Date());
//        removeDuplTT.setFileAgeInDays(100);
//        removeDuplTT.removeDupleFiles();

        System.out.println("");
        System.out.println("--------------------- Creating Archiv/ Step 2: Collect the files to Archive ---------------" + new Date());
        String rroot = "/satdata";
        System.out.println("  Go across  " + rroot + " and collects existing file ");
        System.out.println(new Date());

//        fmc.setArchPath(rroot);
//        Set<FileMeasured> fml = fmc.readArchiveFileDir();
//        System.out.println("  Found " + fml.size() + "  files");
        int k = 0;
//
//        for (Iterator<FileMeasured> it = fml.iterator(); it.hasNext();) {
//            String tmpFName = rroot + "/";
//            FileMeasured tmpf = it.next();
//
//            if (!em.contains(tmpf)) {
//
//                String fileMonth = tmpf.getMonth() + "";
//                if (tmpf.getMonth() < 10) {
//                    fileMonth = "0" + tmpf.getMonth();
//                }
//                String fileDate = tmpf.getDate() + "";
//                if (tmpf.getDate() < 10) {
//                    fileDate = "0" + tmpf.getDate();
//                }
//                tmpFName += tmpf.getYear() + "/" + fileMonth + "-" + fileDate + "/" + tmpf.getFileName();
//                System.out.println("=======> Operation No " + k);
//
//                System.out.println(" Working with file " + tmpFName);
//                System.out.println(" File content size " + (1 + tmpf.getContent().length() / 1024) + " kB ");
//                System.out.println("  ************  ");
//                System.out.println("  IN DB   " + em.contains(tmpf));
//                System.out.println("  ************  ");
//
//                try {
//                    System.out.println("--------------------- Creating Archiv / Step 3: Adding file to Archive ---------------" + new Date());
//                    em.getTransaction().begin();
//                    em.persist(tmpf);
//                    em.getTransaction().commit();
//                    k++;
//                } catch (PersistenceException e) {
//                    System.out.println(new Date() + "  The record with hash " + tmpf.getIntHash() + "  already exists. This operation have been crashed.   ");
//                }
//                File f = new File(tmpFName);
//                boolean ex = f.exists();
//                System.out.println(" Exist File  " + tmpFName + " -- Result:   " + ex);
//                boolean del = f.delete();
//                System.out.println(" Delete File  " + tmpFName + " -- Result:   " + del);
//            }
//        }
        System.out.println("  Number of Archive Operations:   " + k);
//        fml.clear();
        init();
        return "/index.xhtml?redirect=true";
    }

    public String dataSize(FileMeasured fm) {
        double res = 0;
        String suf = " bytes";
        if (fm != null) {
            res = fm.getContent().length();
            if (res > 1024 && res < 1024 * 1024) {
                res = res / 1024;
                res = ((int) (100 * res)) / 100;
                suf = " Kb ";
            } else if (res > 1024 * 1024 && res < 1024 * 1024 * 1024) {
                res = res / 1024 / 1024;
                res = ((int) (100 * res)) / 100;
                suf = " Mb ";
            } else if (res > 1024 * 1024 * 1024 && res < 1024 * 1024 * 1024 * 1024) {
                res = res / 1024 / 1024 / 1024;
                res = ((int) (100 * res)) / 100;
                suf = " Gb ";
            }
        }
        return res + suf;
    }

    public String uploadMomentUTC(FileMeasured fm) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getDefault().getTimeZone("UTC"));
        Date d = new Date(fm.getUploadedMoment());
        return formatter.format(d);
    }

    public void filterOutput() {
        System.out.println("    public void filterOutput(): filterValue " + filterValue);
        if (filterValue.length() == 0 || filterValue == null) {
            init();
            return;
        }

        List<FileMeasured> result = new ArrayList<>();
        for (FileMeasured line : fmList) {
            if (line.getFileName().trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getDate() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getYear() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
            if ((line.getContent() + "").trim().contains(filterValue.trim() + "")) {
                result.add(line);
            }
        }
        fmList = result;
    }
}
