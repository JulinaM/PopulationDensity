

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PopulationQuery {
	// next four constants are relevant to parsing
	public static final int TOKENS_PER_LINE  = 7;
	public static final int POPULATION_INDEX = 4; // zero-based indices
	public static final int LATITUDE_INDEX   = 5;
	public static final int LONGITUDE_INDEX  = 6;

	// argument 1: file name for input data: pass this to parse
	// argument 2: number of x-dimension buckets
	// argument 3: number of y-dimension buckets
	// argument 4: -v1, -v2, -v3, -v4, or -v5
	public static void main(String[] args) {
		// FOR YOU
		String fileName = args[0];
		CensusData censusData = PopulationQuery.parse(fileName);
		int x = Integer.valueOf(args[1]);
		int y = Integer.valueOf(args[2]);

		String version = args[3];

		switch(version){
		case "-v1":
			V1SimpleAndSequential v1 = new V1SimpleAndSequential(x, y, censusData);
			long startProcessTimeV1 = System.nanoTime();
			v1.findUSRectangle();
			long endProcessTimeV1 = System.nanoTime();
			System.out.println("Total PreProcessing Time: " + (endProcessTimeV1 - startProcessTimeV1)+"ns");
			int returnType1 = QueryAlgorithm.DONE;
			do {
				returnType1 = v1.takeInput();
				if(returnType1 ==  QueryAlgorithm.DONE) {
					long startQueryTime = System.nanoTime();
					v1.findPopulation();
					long endQueryTime = System.nanoTime();
					System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");
					
				}
			}while(returnType1 != QueryAlgorithm.EXIT);

			break;
		case "-v2":
			V2SimpleAndParallel v2 = new V2SimpleAndParallel(x, y, censusData, 5000);
			long startProcessTimeV2 = System.nanoTime();
			v2.findUSRectangle();
			long endProcessTimeV2 = System.nanoTime();
			System.out.println("Total PreProcessing Time: " + (endProcessTimeV2 - startProcessTimeV2)+"ns");
			
			int returnType2 = QueryAlgorithm.DONE;
			do {
				returnType2 = v2.takeInput();
				if(returnType2 ==  QueryAlgorithm.DONE) {
					long startQueryTime = System.nanoTime();
					v2.findPopulation();
					long endQueryTime = System.nanoTime();
					System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");
				}
			}while(returnType2 != QueryAlgorithm.EXIT);
			break;
		case "-v3":
			V3SmarterAndSequential v3 = new V3SmarterAndSequential(x, y, censusData);
			long startProcessTimeV3 = System.nanoTime();
			v3.findUSRectangle();
			long endProcessTimeV3 = System.nanoTime();
			System.out.println("Total PreProcessing Time: " + (endProcessTimeV3 - startProcessTimeV3)+"ns");
			int returnType3 = QueryAlgorithm.DONE;
			do {
				returnType3 = v3.takeInput();
				if(returnType3 ==  QueryAlgorithm.DONE) {
					long startQueryTime = System.nanoTime();
					v3.findPopulation();
					long endQueryTime = System.nanoTime();
					System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");
				}
			}while(returnType3 != QueryAlgorithm.EXIT);
			break;
		case "-v4":
			V4SmarterAndParallel v4 = new V4SmarterAndParallel(x, y, censusData, 5000);
			long startProcessTimeV4 = System.nanoTime();
			v4.findUSRectangle();
			long endProcessTimeV4 = System.nanoTime();
			System.out.println("Total PreProcessing Time: " + (endProcessTimeV4 - startProcessTimeV4)+"ns");
			int returnType4 = QueryAlgorithm.DONE;
			do {
				returnType4 = v4.takeInput();
				if(returnType4 ==  QueryAlgorithm.DONE) {
					long startQueryTime = System.nanoTime();
					v4.findPopulation();
					long endQueryTime = System.nanoTime();
					System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");
				}
			}while(returnType4 != QueryAlgorithm.EXIT);
			break;
		case "-v5":
			V5SmarterAndLockedBased v5 = new V5SmarterAndLockedBased(x, y, censusData,5000);
			long startProcessTimeV5 = System.nanoTime();
			v5.findUSRectangle();
			long endProcessTimeV5 = System.nanoTime();
			System.out.println("Total PreProcessing Time: " + (endProcessTimeV5 - startProcessTimeV5)+"ns");
			int returnType5 = QueryAlgorithm.DONE;
			do {
				returnType5 = v5.takeInput();
				if(returnType5 ==  QueryAlgorithm.DONE) {
					long startQueryTime = System.nanoTime();
					v5.findPopulation();
					long endQueryTime = System.nanoTime();
					System.out.println("Total Query Time: " + (endQueryTime - startQueryTime)+"ns");
				}
			}while(returnType5 != QueryAlgorithm.EXIT);
			break;
		case "-c1":
			V1SimpleAndSequential simple = new V1SimpleAndSequential(x, y, censusData);
			V2SimpleAndParallel simPar = new V2SimpleAndParallel(x, y, censusData, 0);
			long startTime = System.nanoTime();
			simple.findUSRectangle();
			long endTime = System.nanoTime();
			long seqDuration = endTime - startTime;
			System.out.println("Total PreProcessing Time for Simple and Sequential: " + seqDuration +"ns");
			long parDuration = 0;
			int initialCutoff = 1;
			
			do {
				initialCutoff += 1;
				simPar = new V2SimpleAndParallel(x, y, censusData, (initialCutoff));
				long startTime1 = System.nanoTime();
				simPar.findUSRectangle();
				long endTime1 = System.nanoTime();
				parDuration = endTime1 - startTime1;
				System.out.println("Cutoff:  " + initialCutoff + " Total Time : " + parDuration);
			}while(parDuration > seqDuration);
			System.out.println("Sequential Cutoff:  " + initialCutoff);
			break;
			
		case "-c2":
			V3SmarterAndSequential smaSeq = new V3SmarterAndSequential(x, y, censusData);
			V4SmarterAndParallel smaPar = new V4SmarterAndParallel(x, y, censusData, 0);
			long startTime3 = System.nanoTime();
			smaSeq.findUSRectangle();
			long endTime3 = System.nanoTime();
			long smaSeqDuration = endTime3 - startTime3;
			System.out.println("Total PreProcessing Time for Smarter and Sequential: " + smaSeqDuration +"ns");
			long smaParDuration = 0;
			int cutoff = 0;
			
			do {
				cutoff += 500;
				smaPar = new V4SmarterAndParallel(x, y, censusData, (cutoff));
				long startTime1 = System.nanoTime();
				smaPar.findUSRectangle();
				long endTime1 = System.nanoTime();
				smaParDuration = endTime1 - startTime1;
				System.out.println("Cutoff:  " + cutoff + " Total Time : " + smaParDuration);
			}while(smaParDuration > smaSeqDuration);
			System.out.println("Sequential Cutoff:  " + cutoff);
			break;
			
		case "-p1":
			
			int grid = 10;
			do {
				V4SmarterAndParallel spV4 = new V4SmarterAndParallel(grid,grid, censusData,1000);
				V5SmarterAndLockedBased slV5 = new V5SmarterAndLockedBased(grid,grid, censusData,1000);
				
				long st1 = System.nanoTime();
				spV4.findUSRectangle();
				long et1 = System.nanoTime();
				
				
				long st2 = System.nanoTime();
				slV5.findUSRectangle();
				long et2 = System.nanoTime();
				
				System.out.println("Grid: "+ grid + " V4 Time : " + (et1-st1)+ " V5 Time : " + (et2-st2));
				if(grid == 10) {
					grid = 50;
				}else {
					grid += 50;
				}
			}while(grid <= 500);
			
			break;
			
		case "-p2":
			V1SimpleAndSequential ssV1 = new V1SimpleAndSequential(x, y, censusData);
			V3SmarterAndSequential ssV3 = new V3SmarterAndSequential(x, y, censusData);
			long preSt1 = System.nanoTime();
			ssV1.findUSRectangle();
			long preEt1 = System.nanoTime();
			System.out.println("V1 Processing time: " + (preEt1 - preSt1));
			
			long preSt2 = System.nanoTime();
			ssV3.findUSRectangle();
			long preEt2 = System.nanoTime();
			System.out.println("V3 Processing time: " + (preEt2 - preSt2));
			
			int querySize = 50;
			do {
				ssV1.inputRecBoundary =  new Rectangle(1, querySize, querySize, 1);
				ssV3.inputRecBoundary =  new Rectangle(1, querySize, querySize, 1);
				
				long st1 = System.nanoTime();
				ssV1.queryPopulation();
				long et1 = System.nanoTime();
				
				
				long st2 = System.nanoTime();
				ssV3.queryPopulation();
				long et2 = System.nanoTime();
				
				System.out.println("Query Size: "+ querySize + " V1 Time : " + (et1-st1)+ " V3 Time : " + (et2-st2));
				
				
				querySize += 50;
			}while(querySize < x);
			break;
			
		case "-p3":
			V2SimpleAndParallel spV2 = new V2SimpleAndParallel(x, y, censusData,7000);
			V4SmarterAndParallel spV4 = new V4SmarterAndParallel(x, y, censusData,7000);
			long preSt3 = System.nanoTime();
			spV2.findUSRectangle();
			long preEt3 = System.nanoTime();
			System.out.println("V2 Processing time: " + (preEt3 - preSt3));
			
			long preSt4 = System.nanoTime();
			spV4.findUSRectangle();
			long preEt4 = System.nanoTime();
			System.out.println("V4 Processing time: " + (preEt4 - preSt4));
			
			int querySize1 = 50;
			do {
				spV2.inputRecBoundary =  new Rectangle(1, querySize1, querySize1, 1);
				spV4.inputRecBoundary =  new Rectangle(1, querySize1, querySize1, 1);
				
				long st1 = System.nanoTime();
				spV2.queryPopulation();
				long et1 = System.nanoTime();
				
				
				long st2 = System.nanoTime();
				spV4.queryPopulation();
				long et2 = System.nanoTime();
				
				System.out.println("Query Size: "+ querySize1 + " V2 Time : " + (et1-st1)+ " V4 Time : " + (et2-st2));
				
				
				querySize1 += 50;
			}while(querySize1 < x);
			break;

		}
		


	}
	
	// parse the input file into a large array held in a base.CensusData object
		public static CensusData parse(String filename) {
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
				System.err.println(ioe.getMessage());
				System.err.println("Error opening/reading/writing input or output file.");
				System.exit(1);
			} catch(NumberFormatException nfe) {
				System.err.println(nfe.toString());
				System.err.println("Error in file format");
				System.exit(1);
			}
			return result;
		}

}
