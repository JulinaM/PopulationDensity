package algorithm;

import base.CensusData;
import base.CensusGroup;
import base.Rectangle;

public class V1SimpleAndSequential extends AbstractBaseAlgo {

    public V1SimpleAndSequential(Integer x, Integer y, CensusData censusData) {
        super(x, y, censusData);
    }

    public void findUSRectangle() {
        this.usRectangle = new Rectangle(censusData.data[0].longitude, censusData.data[0].longitude,
                censusData.data[0].latitude, censusData.data[0].latitude);
        Rectangle temp;
        this.totalPopulation += censusData.data[0].population;
        for (int i = 1; i < censusData.data_size; i++) {
            CensusGroup censusGroup = censusData.data[i];
            if(censusGroup != null) {
                temp = new Rectangle(censusGroup.longitude, censusGroup.longitude, censusGroup.latitude, censusGroup.latitude);
                usRectangle = usRectangle.encompass(temp);
                this.totalPopulation += censusGroup.population;
            }
        }
    }

    public void findPopulation() {
        long popInArea = queryPopulation();
        System.out.println("Total Population in the Area: " + popInArea);
        System.out.println("Total Population: " + totalPopulation);
        float percent = ((float) popInArea * 100)/totalPopulation.floatValue();
        System.out.printf("Percent of total population: %.2f \n",percent);

    }

    public long calculatePopulation(double westBound, double eastBound, double northBound, double southBound) {
        long totalPopulationInArea = 0L;
        for (int i = 0; i < censusData.data_size; i++) {
            CensusGroup censusGroup = censusData.data[i];

            float groupLong = censusGroup.longitude;
            float groupLat = censusGroup.latitude;
            // Defaults to North and/or East in case of tie
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
