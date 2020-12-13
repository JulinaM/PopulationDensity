package algorithm;

import base.CensusData;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class V3Test {

    private AbstractBaseAlgo v3;

    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
        v3 = new V3SmarterAndSequential(100, 500, censusData);
    }

    @Test
    public void findUSRectangle(){
        v3.findUSRectangle();
        System.out.println("Total US Population: "+ v3.totalPopulation);
        System.out.println("US Rectangle: " + v3.usRectangle + "\n");
        v3.inputRecBoundary = new Rectangle(1,50,500,1);
        v3.findPopulation();

        assert v3.totalPopulation == 312471327;
        assert v3.popInArea == 27820072;
        assert v3.popPercent == 8.90324f;
    }
}
