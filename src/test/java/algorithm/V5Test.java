package algorithm;

import base.CensusData;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class V5Test {

    private AbstractBaseAlgo v5;

    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
        v5 = new V5SmarterAndLockedBased(100, 500, censusData);
    }

    @Test
    public void findUSRectangle(){
        v5.findUSRectangle();
        System.out.println("Total US Population: "+ v5.totalPopulation);
        System.out.println("US Rectangle: " + v5.usRectangle + "\n");
        v5.inputRecBoundary = new Rectangle(1,50,500,1);
        v5.findPopulation();

        assert v5.totalPopulation == 312471327;
        assert v5.popInArea == 27820072;
        assert v5.popPercent == 8.90324f;
    }
}
