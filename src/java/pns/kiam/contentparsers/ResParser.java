/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.contentparsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import pns.kiam.entities.satellites.Satellite;
import pns.kiam.entities.satellites.SatelliteMeasurement;

/**
 *
 * @author User
 */
@Stateless
@LocalBean
public class ResParser extends AbstractFileMeasureParser {

    @Override
    public List<SatelliteMeasurement> parseToSatellite(String content, String filename) {
        file = filename;
        List<SatelliteMeasurement> satMesList = new ArrayList<>();
        if (content.length() > 3) {
            Set<String> blocs = singleContent(content);
            List<String> lisblock = new ArrayList<>();
            lisblock.addAll(blocs);
            String tmpBlock = "";

            for (int k = 0; k < lisblock.size(); k++) {
                tmpBlock = lisblock.get(k);
                if (tmpBlock.trim().length() > 0) {
                    // List<String> stringDataList = createTMPDataBR(tmpBlock);
                    createTMPDataBR(tmpBlock);
                    //preData.addAll(stringDataList);

                }
            }
            //pns.GenerateLists.outputCollectionShort((Set) preData);

            List<LineConduct> lineConductList = createLineConduct(preData);
            for (int j = 0; j < lineConductList.size(); j++) {
                SatelliteMeasurement sm = conduct2Measurement(lineConductList.get(j));
                satMesList.add(sm);
            }
            pns.GenerateLists.outputCollectionShort((List) satMesList);

            System.out.println("-----------------------");
        }

        return satMesList;
    }

    @Override
    protected Set<String> singleContent(String content) {
        Set<String> single = new HashSet<>();
        String[] parts = new String[0];
        if (content.contains("END")) {
            parts = content.split("END");
        } else if (content.contains("СИСТ") && content.contains("КОНЕЦ")) {

            String[] subParts = content.split("СИСТ");
            for (int k = 0; k < subParts.length; k++) {

                subParts[k] = subParts[k].replace("КОНЕЦ", "").trim();
            }
            single = (Set) pns.GenerateLists.toSet(subParts);

        }
//        single = pns.GenerateLists.toList(parts);
        return (Set) single;
    }

    @Override
    protected SatelliteMeasurement conduct2Measurement(LineConduct lc) {
        SatelliteMeasurement sm = new SatelliteMeasurement();
        DateFormat format = new SimpleDateFormat("ddMMyyHHmmssSSS");
        try {
            String dd = lc.getObsDate();
            String tt = lc.getObsTime();
            Date date = format.parse(lc.getObsDate() + lc.getObsTime());
//            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date));
            sm.setDate(date.getTime());
        } catch (ParseException ex) {
            Logger.getLogger(ResParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        int telescopeID = Integer.parseInt(lc.getTelescopeNo());
        sm.setTelescopeID(telescopeID);

        int objectNo = Integer.parseInt(lc.getObjectNo());
        sm.setSatID(objectNo);

        int H = Integer.parseInt(lc.getAscensions().substring(0, 2));
        int M = Integer.parseInt(lc.getAscensions().substring(2, 4));
        double S = Double.parseDouble(lc.getAscensions().substring(4, 6) + "." + lc.getAscensions().substring(6));
        sm.setAscent_H(H);
        sm.setAscent_M(M);
        sm.setAscent_S(S);
        sm.setMandatory(lc.getDeclinations().charAt(0));

        H = Integer.parseInt(lc.getDeclinations().substring(1, 3));
        M = Integer.parseInt(lc.getDeclinations().substring(3, 4));
        S = Double.parseDouble(lc.getDeclinations().substring(4, 6) + "." + lc.getDeclinations().substring(6));
        sm.setDecline_H(H);
        sm.setDecline_M(M);
        sm.setDecline_S(S);

        double B = Double.parseDouble(lc.getBright()) / 10;
        sm.setBrightness(B);

        double E = Double.parseDouble(lc.getExact());
        sm.setAccuracy(E);

        sm.setFileAddress(file);
        return sm;

    }

    private List<LineConduct> createLineConduct(Set<String> preData) {
        List<String> preDataList = new ArrayList<>(preData);
        List<LineConduct> lineConductList = new ArrayList<>();
        for (String tmp : preDataList) {
            String[] tmpParts = tmp.split("\\s");
            LineConduct lc = new LineConduct();

            lc.setTelescopeNo(tmpParts[0].trim());
            lc.setObjectNo(tmpParts[1].trim());
            lc.setObsDate(tmpParts[2].trim());
            lc.setObsTime(tmpParts[3].trim());
            lc.setAscensions(tmpParts[4].trim());
            lc.setDeclinations(tmpParts[5].trim());
            String exact = "";
            String bright = "";
            if (tmpParts[6].length() == 6) {
                exact = tmpParts[6].substring(0, 3);
                bright = tmpParts[6].substring(3);
            }
            lc.setExact(exact);
            lc.setBright(bright);

            lineConductList.add(lc);
        }

        return lineConductList;
    }

    private void createTMPDataBR(String block) {
        String prefix = "";
        String[] blocStrings = block.split(System.lineSeparator());
        if (blocStrings.length > 0) {
            prefix = blocStrings[0];
        }
        System.out.println("    blocs: ");
        for (int p = 1; p < blocStrings.length; p++) {
            blocStrings[p] = prefix + " " + blocStrings[p];
            preData.add(blocStrings[p]);
        }
    }

    private Date getDateFromString(String startDateString) {

        DateFormat df = new SimpleDateFormat("MMddyy hhmmssSSS");
        Date startDate = null;
        try {
            startDate = df.parse(startDateString);
//            String newDateString = df.format(startDate);
//            System.out.println(newDateString);
            //System.out.println("StartDate:   "+startDate );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    private Collection<String> convertDataFromBlock(String ss) {
        return pns.GenerateLists.generateTokens(ss);
    }
}
