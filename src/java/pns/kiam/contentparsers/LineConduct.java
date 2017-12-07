/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.contentparsers;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class LineConduct {

    private String telescopeNo = "";
    private String objectNo = "";
    private String obsDate = "";
    private String obsTime = "";
    private String ascensions = "";
    private String declinations = "";
    private String exact = "";
    private String bright = "";

    public String getTelescopeNo() {
        return telescopeNo;
    }

    public void setTelescopeNo(String telescopeNo) {
        this.telescopeNo = telescopeNo;
    }

    public String getObjectNo() {
        return objectNo;
    }

    public void setObjectNo(String objectNo) {
        this.objectNo = objectNo;
    }

    public String getAscensions() {
        return ascensions;
    }

    public void setAscensions(String ascensions) {
        this.ascensions = ascensions;
    }

    public String getDeclinations() {
        return declinations;
    }

    public void setDeclinations(String declinations) {
        this.declinations = declinations;
    }

    public String getObsDate() {
        return obsDate;
    }

    public void setObsDate(String obsDate) {
        this.obsDate = obsDate;
    }

    public String getObsTime() {
        return obsTime;
    }

    public void setObsTime(String obsTime) {
        this.obsTime = obsTime;
    }

    public String getExact() {
        return exact;
    }

    public void setExact(String exact) {
        this.exact = exact;
    }

    public String getBright() {
        return bright;
    }

    public void setBright(String bright) {
        this.bright = bright;
    }

    @Override
    public String toString() {
        return "LineConduct{" + "telescopeNo=" + telescopeNo + ", objectNo=" + objectNo + ", obsDate=" + obsDate + ", obsTime=" + obsTime + ", ascensions=" + ascensions + ", declinations=" + declinations + ", exact=" + exact + ", bright=" + bright + '}';
    }

}
