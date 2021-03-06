package gov.noaa.pmel.dashboard.programs;

import gov.noaa.pmel.dashboard.actions.OverlapChecker;
import gov.noaa.pmel.dashboard.dsg.DsgNcFile;
import gov.noaa.pmel.dashboard.handlers.DsgNcFileHandler;
import gov.noaa.pmel.dashboard.server.DashboardConfigStore;
import gov.noaa.pmel.dashboard.server.DashboardServerUtils;
import gov.noaa.pmel.dashboard.shared.DashboardUtils;
import gov.noaa.pmel.dashboard.shared.DatasetQCStatus;
import gov.noaa.pmel.dashboard.shared.Overlap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Finds overlaps within and between datasets
 *
 * @author Karl Smith
 */
public class ReportOverlaps {

    /**
     * @param args
     *         ExpocodesFile - a file of expocodes specifying the datasets to examine for overlaps
     */
    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.err.println("Arguments:  ExpocodesFile");
            System.err.println();
            System.err.println("Checks for overlaps within and between datasets.  Overlaps are duplications of ");
            System.err.println("location and time values.  Extensive overlaps are very likely to be erroneous ");
            System.err.println("duplication of data, although there is the rare possibility of two instruments ");
            System.err.println("on the same platform.  Data points with a WOCE-4 flag or missing fCO2_rec are ");
            System.err.println("ignored. ");
            System.err.println();
            System.err.println("ExpocodesFile");
            System.err.println("    a file of expocodes, one per line, specifying the datasets to examine ");
            System.err.println();
            System.exit(1);
        }
        String exposFilename = args[0];

        TreeSet<String> givenExpocodes = new TreeSet<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(exposFilename));
            try {
                String dataline = reader.readLine();
                while ( dataline != null ) {
                    dataline = dataline.trim().toUpperCase();
                    if ( !dataline.isEmpty() )
                        givenExpocodes.add(dataline);
                    dataline = reader.readLine();
                }
            } finally {
                reader.close();
            }
        } catch ( Exception ex ) {
            System.err.println("Problems reading the file of expocodes '" + exposFilename + "': " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        SimpleDateFormat dateFmtr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFmtr.setTimeZone(TimeZone.getTimeZone("UTC"));

        TreeSet<Overlap> orderedOverlaps = new TreeSet<Overlap>();

        DashboardConfigStore configStore = null;
        try {
            configStore = DashboardConfigStore.get(false);
        } catch ( Exception ex ) {
            System.err.println("Problems obtaining the default dashboard configuration: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        try {

            DsgNcFileHandler dsgHandler = configStore.getDsgNcFileHandler();
            OverlapChecker oerlapChecker = new OverlapChecker(dsgHandler);

            long startTime = System.currentTimeMillis();

            // Get the QC flags for the cruises from the DSG files
            double timeDiff = (System.currentTimeMillis() - startTime) / (60.0 * 1000.0);
            System.err.format("%.2fm - getting QC flags for the datasets\n", timeDiff);
            TreeSet<String> expoSet = new TreeSet<String>();
            for (String expo : givenExpocodes) {
                try {
                    String[] flagVersionStatus = dsgHandler.getDatasetQCFlagAndVersionStatus(expo);
                    DatasetQCStatus qcFlag = DatasetQCStatus.fromString(flagVersionStatus[0]);
                    if ( qcFlag.isAcceptable() || qcFlag.isAwaitingQC() ) {
                        expoSet.add(expo);
                    }
                    else {
                        throw new Exception("QC flag is " + flagVersionStatus[0]);
                    }
                } catch ( Exception ex ) {
                    System.err.println("Problems with expocode " + expo + ": " + ex.getMessage());
                }
            }

            // Get the time and latitude limits for all the cruises in the list
            // in order to narrow down the cruises to examine for overlaps
            TreeMap<String,double[]> timeMinMaxMap = new TreeMap<String,double[]>();
            TreeMap<String,double[]> latMinMaxMap = new TreeMap<String,double[]>();
            for (String expo : expoSet) {
                timeDiff = (System.currentTimeMillis() - startTime) / (60.0 * 1000.0);
                System.err.format("%.2fm - getting data limits for %s\n", timeDiff, expo);
                double[][] dataVals = null;
                try {
                    dataVals = oerlapChecker.getMaskedLonLatTimeSstFco2Vals(expo);
                } catch ( Exception ex ) {
                    System.err.println("Unexpected error rereading " + expo + ": " + ex.getMessage());
                    System.exit(1);
                }
                double[] timeMinMaxVals = DashboardServerUtils.getMinMaxValidData(dataVals[2]);
                if ( (timeMinMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                        (timeMinMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) ) {
                    System.err.println("No valid times for " + expo);
                    System.exit(1);
                }
                timeMinMaxMap.put(expo, timeMinMaxVals);
                double[] latMinMaxVals = DashboardServerUtils.getMinMaxValidData(dataVals[1]);
                if ( (latMinMaxVals[0] == DashboardUtils.FP_MISSING_VALUE) ||
                        (latMinMaxVals[1] == DashboardUtils.FP_MISSING_VALUE) ) {
                    System.err.println("No valid latitudes for " + expo);
                    System.exit(1);
                }
                latMinMaxMap.put(expo, latMinMaxVals);
            }

            for (String firstExpo : expoSet) {
                double[] firstTimeMinMax = timeMinMaxMap.get(firstExpo);
                double[] firstLatMinMax = latMinMaxMap.get(firstExpo);
                // Get the list of possibly-crossing datasets to check
                TreeSet<String> checkExpos = new TreeSet<String>();
                for (String secondExpo : expoSet) {
                    // Only those datasets preceding this one so not doing two checks on a pair
                    if ( secondExpo.equals(firstExpo) ) {
                        // Always check for overlaps within a dataset
                        checkExpos.add(secondExpo);
                        break;
                    }
                    // Check that there is some overlap in time
                    double[] secondTimeMinMax = timeMinMaxMap.get(secondExpo);
                    if ( (firstTimeMinMax[1] + DsgNcFile.MIN_TIME_DIFF < secondTimeMinMax[0]) ||
                            (secondTimeMinMax[1] + DsgNcFile.MIN_TIME_DIFF < firstTimeMinMax[0]) )
                        continue;
                    // Check that there is some overlap in latitude
                    double[] secondLatMinMax = latMinMaxMap.get(secondExpo);
                    if ( (firstLatMinMax[1] + DsgNcFile.MIN_LAT_DIFF < secondLatMinMax[0]) ||
                            (secondLatMinMax[1] + DsgNcFile.MIN_LON_DIFF < firstLatMinMax[0]) )
                        continue;
                    checkExpos.add(secondExpo);
                }

                // Find any overlaps with this data set with the selected set of data sets
                try {
                    orderedOverlaps.addAll(oerlapChecker.getOverlaps(firstExpo, checkExpos, System.err, startTime));
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }

        } finally {
            DashboardConfigStore.shutdown();
        }

        Iterator<Overlap> revIter = orderedOverlaps.descendingIterator();
        while ( revIter.hasNext() ) {
            Overlap oerlap = revIter.next();

            String[] expos = oerlap.getDatasetIds();
            if ( expos[0].equals(expos[1]) ) {
                System.out.println("InternalOverlap[ expocode=" + expos[0] + ",");
            }
            else {
                System.out.println("ExternalOverlap[ expocodes=[" + expos[0] + ", " + expos[1] + "],");
            }
            ArrayList<Integer>[] rowNums = oerlap.getRowNums();
            ArrayList<Double>[] lons = oerlap.getLons();
            ArrayList<Double>[] lats = oerlap.getLats();
            ArrayList<Double>[] times = oerlap.getTimes();
            System.out.println("    numRows=" + rowNums[0].size() + ",");
            System.out.println("    rowNums=[ " + rowNums[0].toString() + ",");
            System.out.println("              " + rowNums[1].toString() + " ],");
            System.out.println("    lons=[ " + lons[0].toString() + ",");
            System.out.println("           " + lons[1].toString() + " ],");
            System.out.println("    lats=[ " + lats[0].toString() + ",");
            System.out.println("           " + lats[1].toString() + " ],");
            System.out.print("    times=[[");
            for (int k = 0; k < times[0].size(); k++) {
                if ( k > 0 )
                    System.out.print(", ");
                System.out.print(dateFmtr.format(new Date(Math.round(times[0].get(k) * 1000.0))));
            }
            System.out.println("],");
            System.out.print("           [");
            for (int k = 0; k < times[1].size(); k++) {
                if ( k > 0 )
                    System.out.print(", ");
                System.out.print(dateFmtr.format(new Date(Math.round(times[1].get(k) * 1000.0))));
            }
            System.out.println("]]");
            System.out.println("]");
        }
        System.exit(0);
    }

}
