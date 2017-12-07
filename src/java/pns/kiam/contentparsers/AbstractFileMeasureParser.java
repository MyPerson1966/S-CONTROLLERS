/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pns.kiam.contentparsers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pns.kiam.entities.satellites.SatelliteMeasurement;

/**
 *
 * @author User
 */
public abstract class AbstractFileMeasureParser {

    protected Set<String> preData = new HashSet<>();
    protected String file = "";

    public abstract List<SatelliteMeasurement> parseToSatellite(String content, String filename);

    protected abstract Set<String> singleContent(String content);

    protected abstract SatelliteMeasurement conduct2Measurement(LineConduct lc);

}
