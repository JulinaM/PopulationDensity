
public class V1SimpleAndSequential extends QueryAlgorithm{
	
	public V1SimpleAndSequential(int x, int y, CensusData censusData) {
		this.x = x;
		this.y = y;
		this.censusData = censusData;
	}

	public void findUSRectangle() {
		usRectangle = new Rectangle(censusData.data[0].longitude, censusData.data[0].longitude, censusData.data[0].latitude, censusData.data[0].latitude);
		Rectangle temp;
		totalPopulation += censusData.data[0].population;
		for (int i = 1; i < censusData.data_size; i++) {
			CensusGroup censusGroup = censusData.data[i];
			if(censusGroup != null) {
				temp = new Rectangle(censusGroup.longitude, censusGroup.longitude, censusGroup.latitude, censusGroup.latitude);
				usRectangle = usRectangle.encompass(temp);
				totalPopulation += censusGroup.population;
			}
		}
	}
	
	public long queryPopulation() {
		Long totalPopulationInArea = new Long(0);
		float width = (usRectangle.right - usRectangle.left) / x;
		float height = (usRectangle.top - usRectangle.bottom) / y;
		
		float westBound = (usRectangle.left + (inputRecBoundary.left - 1) * (width));
		float eastBound = (usRectangle.left + (inputRecBoundary.right) * (width));
		float northBound = (usRectangle.bottom + (inputRecBoundary.top) * (height));
		float southBound = (usRectangle.bottom + (inputRecBoundary.bottom - 1) * (height));
		
		for (int i = 0; i < censusData.data_size; i++) {
			CensusGroup censusGroup = censusData.data[i];
			
            float groupLong = censusGroup.longitude;
            float groupLat = censusGroup.latitude;
            // Defaults to North and/or East in case of tie
            if (groupLat >= southBound &&
                    (groupLat < northBound ||
                            (northBound - usRectangle.top) >= 0) &&
                            (groupLong < eastBound ||
                                    (eastBound - usRectangle.right) >= 0) &&
                                    groupLong >= westBound) {
            		totalPopulationInArea += censusGroup.population;
            }
		}
		return totalPopulationInArea;	
	}
	public void findPopulation() {
		Long popInArea = queryPopulation();
		System.out.println("Total Population in the Area: " + popInArea);
		System.out.println("Total Population: " + totalPopulation);
		float percent = (popInArea.floatValue() * 100)/totalPopulation.floatValue();
		System.out.printf("Percent of total population: %.2f \n",percent);
	}
}
