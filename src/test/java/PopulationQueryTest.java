import algorithm.AbstractBaseAlgo;
import algorithm.BaseAlgo;
import base.CensusData;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PopulationQueryTest {
    static final Map<String, String> algorithms = new HashMap<String, String>(){
        {
            put("-v1", "algorithm.V1SimpleAndSequential");
            put("-v2", "algorithm.V2SimpleAndParallel");
            put("-v3", "algorithm.V3SmarterAndSequential");
            put("-v4", "algorithm.V4SmarterAndParallel");
            put("-v5", "algorithm.V5SmarterAndLockedBased");
        }
    };

    public static void main(String[] args) {
        try {
            String absolutePath = Paths.get("src","main","resources").toFile().getAbsolutePath();
            CensusData censusData = PopulationQuery.parse(absolutePath+"/CenPop2010.txt");
            Class<?>   clazz = Class.forName(algorithms.get("-v5"));
            Constructor<?> constructor = clazz.getConstructor(Integer.class, Integer.class, CensusData.class);
            BaseAlgo algorithm = (BaseAlgo) constructor.newInstance(100, 500, censusData);
            long startProcessTimeV1 = System.nanoTime();
            algorithm.findUSRectangle();
            long endProcessTimeV1 = System.nanoTime();
            System.out.println("Total PreProcessing Time: " + (endProcessTimeV1 - startProcessTimeV1)+"ns");
            BaseAlgo.ReturnType returnType1;
            do {
                returnType1 = algorithm.takeInput();
                if(returnType1 ==  AbstractBaseAlgo.ReturnType.DONE) {
                    long startQueryTime = System.nanoTime();
                    algorithm.findPopulation();
                    long endQueryTime = System.nanoTime();
                    System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");

                }
            }while(returnType1 != AbstractBaseAlgo.ReturnType.EXIT);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
