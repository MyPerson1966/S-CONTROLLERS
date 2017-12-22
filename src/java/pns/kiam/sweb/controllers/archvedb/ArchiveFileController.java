package pns.kiam.sweb.controllers.archvedb;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import pns.fileUtils.FileSpecActor;
import pns.kiam.Utils.FormatClassificator;

import pns.kiam.commonserrvice.DownloadContentController;
import pns.kiam.commonserrvice.FormatCoordinator;
import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.entities.satellites.SatelliteMeasurement;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.controllers.app.XXParserSWEB;

/**
 *
 * @author User
 */
@LocalBean
public class ArchiveFileController extends AbstractController implements Serializable {

    private FileMeasured fileMeasured;
    List<FileMeasured> fileMeasuredList = new ArrayList<>();

    protected CriteriaQuery<FileMeasured> cq;

    private String tmpName = "";
    private String filterValue = "";
    protected String fileType = "";
    private FormatCoordinator formatCoordinator;

    @EJB
    private XXParserSWEB xxparser;
    @EJB
    private DownloadContentController downloadContentController;

    @PostConstruct
    public void initial() {
        try {
            abstractInit();
            cq = cb.createQuery(FileMeasured.class);
            fileMeasuredList = loadAllArchMeas();
        } catch (NullPointerException e) {
        }
    }

    private List loadAllArchMeas() {

        Root<FileMeasured> res = cq.from(FileMeasured.class);
        cq.select(res);

        cq.orderBy(cb.asc(res.get("id")));
        TypedQuery<FileMeasured> Q = em.createQuery(cq);
        rowDeselect();
        return Q.getResultList();
    }

    private void rowDeselect() {

    }

    public FileMeasured getFileMeasured() {
        return fileMeasured;
    }

    public List<FileMeasured> getFileMeasuredList() {
        return fileMeasuredList;
    }

    public String getTmpName() {
        return tmpName;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFileMeasured(FileMeasured fileMeasured) {
        this.fileMeasured = fileMeasured;
    }

    public void setFileMeasuredList(List<FileMeasured> fileMeasuredList) {
        this.fileMeasuredList = fileMeasuredList;
    }

    public void setCq(CriteriaQuery<FileMeasured> cq) {
        this.cq = cq;
    }

    public void setTmpName(String tmpName) {
        this.tmpName = tmpName;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void selectFile(FileMeasured fm) {
        this.fileMeasured = fm;
        genFileFromRec();
        System.out.println("    fm  " + fileMeasured.getFileName() + "    tmpName  " + tmpName);
    }

    private void genFileFromRec() {
        if (fileMeasured != null) {
            String tmpdir = "tmp/";
            FileSpecActor fsa = new FileSpecActor();
            fsa.createDir(tmpdir);
            tmpName = tmpdir + pns.utils.strings.RStrings.rndString(3, 'a', 'z') + "_" + fileMeasured.getFileName();
            fsa.setFullFileName(tmpName);
            //System.out.println("  fsa.getFullFileName() " + fsa.getFullFileName());
            fsa.fileWrite(fileMeasured.getContent());
        }
    }

    public void recDownload(FileMeasured fm) {
        selectFile(fm);
        if (fileMeasured != null) {
            System.out.println("  --------->> FN name " + fileMeasured.getFileName());
            try {
                downloadContentController.downloadData(fileMeasured.getContent(), fileMeasured.getFileName());
            } catch (IOException ex) {
                Logger.getLogger(ArchiveFileController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//        File f = new File(tmpName);
//        System.out.println("`````````````````>>>Download: " + f.getAbsolutePath());
////        try {
////            fdc.downloadFile(f);
////        } catch (IOException ex) {
////            Logger.getLogger(FileViewController.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        f.delete();
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
            return;
        }

        List<FileMeasured> result = new ArrayList<>();
        for (FileMeasured line : fileMeasuredList) {
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
        fileMeasuredList = result;
    }

    protected void getType(FileMeasured fm) {
        formatCoordinator = new FormatCoordinator();
        formatCoordinator.classificate(fm.getContent());
        fileType = formatCoordinator.getFormatType();

    }
}
