package algorithm;

import base.CensusData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PopulationQuery {
	// next four constants are relevant to parsing
	private static final int TOKENS_PER_LINE  = 7;
	private static final int POPULATION_INDEX = 4; // zero-based indices
	private static final int LATITUDE_INDEX   = 5;
	private static final int LONGITUDE_INDEX  = 6;
	
	// parse the input file into a large array held in a base.CensusData object
	static CensusData parse(String filename) {
		CensusData result = new CensusData();
		
        try {
            BufferedReader fileIn = new BufferedReader(new FileReader(filename));
            
            // Skip the first line of the file
            // After that each line has 7 comma-separated numbers (see constants above)
            // We want to skip the first 4, the 5th is the population (an int)
            // and the 6th and 7th are latitude and longitude (floats)
            // If the population is 0, then the line has latitude and longitude of +.,-.
            // which cannot be parsed as floats, so that's a special case
            //   (we could fix this, but noisy data is a fact of life, more fun
            //    to process the real data as provided by the government)
            
            String oneLine = fileIn.readLine(); // skip the first line

            // read each subsequent line and add relevant data to a big array
            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if(tokens.length != TOKENS_PER_LINE)
                	throw new NumberFormatException();
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if(population != 0)
                	result.add(population,
                			   Float.parseFloat(tokens[LATITUDE_INDEX]),
                		       Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }

            fileIn.close();
        } catch(IOException ioe) {
            System.err.println("Error opening/reading/writing input or output file.");
            System.exit(1);
        } catch(NumberFormatException nfe) {
            System.err.println(nfe.toString());
            System.err.println("Error in file format");
            System.exit(1);
        }
        return result;
	}

    private static final Map<String, String> algorithms = new HashMap<String, String>(){
        {
            put("-v1", "algorithm.V1SimpleAndSequential");
            put("-v2", "algorithm.V2SimpleAndParallel");
            put("-v3", "algorithm.V3SmarterAndSequential");
            put("-v4", "algorithm.V4SmarterAndParallel");
            put("-v5", "algorithm.V5SmarterAndLockedBased");
        }
    };

	public static void main(String[] args) {
		// FOR YOU

        // argument 1: file name for input data: pass this to parse
        // argument 2: number of x-dimension buckets
        // argument 3: number of y-dimension buckets
        // argument 4: -v1, -v2, -v3, -v4, or -v5

        String fileName = args[0];
        CensusData censusData = PopulationQuery.parse(fileName);
        int x = Integer.valueOf(args[1]);
        int y = Integer.valueOf(args[2]);
        String version = args[3];
        try {
            System.out.println(algorithms.get(version));
            Class<?> clazz = Class.forName(algorithms.get(version));
            Constructor<?> constructor = clazz.getConstructor(Integer.class, Integer.class, CensusData.class);
            BaseAlgo algorithm = (BaseAlgo) constructor.newInstance(x, y, censusData);
            long startProcessTime = System.nanoTime();
            algorithm.findUSRectangle();
            long endProcessTime = System.nanoTime();
            System.out.println("Total PreProcessing Time: " + (endProcessTime - startProcessTime)+"ns");
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

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
