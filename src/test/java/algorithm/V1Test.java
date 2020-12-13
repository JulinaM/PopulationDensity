package algorithm;

import base.CensusData;
import base.Rectangle;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

public class V1Test {
    private AbstractBaseAlgo v1 ;

    @Before
    public void init(){
        System.out.println("Initializing . . .");
        String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
        CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
        v1 = new V1SimpleAndSequential(100, 500, censusData);
    }

    @Test
    public void findUSRectangle(){
        v1.findUSRectangle();
        System.out.println("Total US Population: "+ v1.totalPopulation);
        System.out.println("US Rectangle: " + v1.usRectangle + "\n");
        v1.inputRecBoundary = new Rectangle(1,50,500,1);
        v1.findPopulation();

        assert v1.totalPopulation == 312471327;
        assert v1.popInArea == 27820072;
        assert v1.popPercent == 8.90324f;

    }
}
