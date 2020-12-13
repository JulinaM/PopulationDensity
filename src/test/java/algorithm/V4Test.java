package algorithm;

import base.CensusData;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class V4Test {

    private AbstractBaseAlgo v4;

    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
        v4 = new V4SmarterAndParallel(100, 500, censusData);
    }

    @Test
    public void findUSRectangle(){
        v4.findUSRectangle();
        System.out.println("Total US Population: "+ v4.totalPopulation);
        System.out.println("US Rectangle: " + v4.usRectangle + "\n");
        v4.inputRecBoundary = new Rectangle(1,50,500,1);
        v4.findPopulation();

        assert v4.totalPopulation == 312471327;
        assert v4.popInArea == 27820072;
        assert v4.popPercent == 8.90324f;
    }
}
