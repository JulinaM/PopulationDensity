package algorithm;

import base.CensusData;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class V2Test {

    private AbstractBaseAlgo v2 ;

    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
        v2 = new V2SimpleAndParallel(100, 500, censusData);
    }

    @Test
    public void findUSRectangle(){
        v2.findUSRectangle();
        System.out.println("Total US Population: "+ v2.totalPopulation);
        System.out.println("US Rectangle: " + v2.usRectangle + "\n");
        v2.inputRecBoundary = new Rectangle(1,50,500,1);
        v2.findPopulation();

        assert v2.totalPopulation == 312471327;
        assert v2.popInArea == 27820072;
        assert v2.popPercent == 8.90324f;
    }

}