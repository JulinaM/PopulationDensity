
import base.CensusData;
import base.CensusGroup;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;


public class FindUSRectangle {
    CensusData censusData = null;
    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");

    }

    @Test
    public void queryPop(){

        Rectangle usRectangle = new Rectangle(1,100,500,1);
        Rectangle inputRecBoundary = new Rectangle(1,50,500,1);

        calculateBoundary(usRectangle, inputRecBoundary, 100, 500);
        System.out.println(queryPopulation(usRectangle, inputRecBoundary,
                censusData,10, 50 ));
    }

    void calculateBoundary(Rectangle usRectangle, Rectangle inputRecBoundary,
                         Integer x , Integer y ){

        double westBound = (usRectangle.left + (inputRecBoundary.left - 1) * (usRectangle.right - usRectangle.left) / x);
        double eastBound = (usRectangle.left + (inputRecBoundary.right) * (usRectangle.right - usRectangle.left) / x);
        double northBound = (usRectangle.bottom + (inputRecBoundary.top) * (usRectangle.top - usRectangle.bottom) / y);
        double southBound = (usRectangle.bottom + (inputRecBoundary.bottom - 1) * (usRectangle.top - usRectangle.bottom) / y);
        assert westBound >= 1;
        assert eastBound >=50;
        assert northBound >=500;
        assert southBound >=1;
    }



    long queryPopulation(Rectangle usRectangle, Rectangle inputRecBoundary,
                         CensusData censusData,
                         Integer x , Integer y ){

        double westBound = (usRectangle.left + (inputRecBoundary.left - 1) * (usRectangle.right - usRectangle.left) / x);
        double eastBound = (usRectangle.left + (inputRecBoundary.right) * (usRectangle.right - usRectangle.left) / x);
        double northBound = (usRectangle.bottom + (inputRecBoundary.top) * (usRectangle.top - usRectangle.bottom) / y);
        double southBound = (usRectangle.bottom + (inputRecBoundary.bottom - 1) * (usRectangle.top - usRectangle.bottom) / y);

        long totalPopulationInArea = 0L;
        for (int i = 0; i < censusData.data_size; i++) {
            CensusGroup censusGroup = censusData.data[i];

            float groupLong = censusGroup.longitude;
            float groupLat = censusGroup.latitude;
            if (groupLat >= southBound &&
                    (groupLat < northBound || (northBound - usRectangle.top) >= 0) &&
                    (groupLong < eastBound || (eastBound - usRectangle.right) >= 0) &&
                    groupLong >= westBound) {
                totalPopulationInArea += censusGroup.population;
            }
        }
        return totalPopulationInArea;
    }

}
