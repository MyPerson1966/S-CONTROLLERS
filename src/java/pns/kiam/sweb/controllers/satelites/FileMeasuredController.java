/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.sweb.controllers.satelites;

import java.io.ByteArrayOutputStream;
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
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import pns.kiam.entities.satellites.FileMeasured;
import pns.kiam.entities.satellites.SatelliteMeasurement;
import pns.kiam.fileDuplecontrols.DupRemover;
import pns.kiam.sweb.controllers.AbstractController;
import pns.kiam.sweb.controllers.app.XXParserSWEB;

/**
 *
 * @author User
 */
@LocalBean
//@Stateless
public class FileMeasuredController extends AbstractController {

    private List<FileMeasured> fileMeasuredList = new ArrayList<>();
    private FileMeasured fileMeasured;
    private CriteriaQuery<FileMeasured> cq;
    private String filterValue;
    protected String archivePath;
    protected int maxDaysFileLive = 10;

    @EJB
    private DupRemover dupRemover;
    @EJB
    private XXParserSWEB xxparser;

//    @Inject
//    private pns.kiam.filecontrol.FileMeasuredController fmc;
//    @Inject
//    private RemoverDuplicatesTimeTable removeDuplTT;
    @PostConstruct
    public void initial() {

        try {
            cb = em.getCriteriaBuilder();
            cq = cb.createQuery(FileMeasured.class);
            fileMeasuredList = loadAllMeas();
            System.out.println("");
            infMeasured();
        } catch (NullPointerException e) {
        }
    }

    public void infMeasured() {
        fileMeasuredList = loadAllMeas();
        System.out.println(fileMeasuredList.size());
    }

    private List loadAllMeas() {
        System.out.println(" 0  ===============loadAllSatMeas===================");
        Root<FileMeasured> res = cq.from(FileMeasured.class);
        cq.select(res);
        System.out.println(" 1  ===============loadAllSatMeas===================");

        cq.orderBy(cb.asc(res.get("id")));
        TypedQuery<FileMeasured> Q = em.createQuery(cq);
        rowDeselect();
        return Q.getResultList();
    }

    public void rowDeselect() {
        this.fileMeasured = null;
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

    public void recDownload(FileMeasured fl) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        String[] fnParts = fl.getFileName().split("_");
        fnParts[0] = "";
        String filename = String.join("_", fnParts);
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setContentLength((int) fl.getContent().length());
        try {
            ServletOutputStream out = response.getOutputStream();
//        ServletOutputStream out = null;
            System.out.println("    recDownload " + filename);
            byte[] bts = fl.getContent().getBytes();
            for (int k = 0; k < bts.length; k++) {
                out.write(bts[k]);
                out.flush();
            }
            FacesContext.getCurrentInstance().getResponseComplete();

        } catch (IOException ex) {
            Logger.getLogger(FileMeasuredController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void selectFile(FileMeasured fl) {
        this.fileMeasured = fl;
    }

    public FileMeasured getFileMeasured() {
        return fileMeasured;
    }

    public List<FileMeasured> getFileMeasuredList() {
        return fileMeasuredList;
    }

    public void filterOutput() {
        System.out.println("    public void filterOutput(): filterValue " + filterValue);
        if (filterValue.length() == 0 || filterValue == null) {
            fileMeasuredList = loadAllMeas();
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
        String rroot = archivePath;
        dupRemover.setMaxDaysFileLive(100);
        dupRemover.removeDupleFiles();

//        removeDuplTT.setFileAgeInDays(100);
//        removeDuplTT.removeDupleFiles();
//
        System.out.println("    xxparser.getMaxDaysFileLive() " + xxparser.getMaxDaysFileLive());
        System.out.println("--------------------- Creating Archiv/ Step 2: Collect the files to Archive ---------------" + new Date());
        //fuc.getRooot() + "/satdata";
//        System.out.println("  Go across  " + rroot + " and collects existing file ");
//        System.out.println(new Date());
//
//        fmc.setArchPath(rroot);
//        Set<FileMeasured> fml = fmc.readArchiveFileDir();
//
//        System.out.println("  Found " + fml.size() + "  files");
//
//        int k = 0;
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
//        System.out.println("  Number of Archive Operations:   " + k);
//        fml.clear();
        return "/index.xhtml?redirect=true";
    }

}
