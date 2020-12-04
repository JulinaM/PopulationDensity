import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class V2SimpleAndParallel extends QueryAlgorithm{

	// for parallel programming!

	int cutoff = 5000;
	
	public V2SimpleAndParallel(int x, int y, CensusData censusData, int cutOff) {
		this.x = x;
		this.y = y;
		this.censusData = censusData;
		this.cutoff = cutOff;
	}
	
	public void findUSRectangle() {
        if (censusData.data_size == 0)
            return;

        Result res = ForkJoinPool.commonPool().invoke(new Preprocessor(0, censusData.data_size));
        usRectangle = res.rec;
        
        totalPopulation = (long) res.population;
        
	}
	
	public long queryPopulation() {
        double westBound = (usRectangle.left + (inputRecBoundary.left - 1) * (usRectangle.right - usRectangle.left) / x);
		double eastBound = (usRectangle.left + (inputRecBoundary.right) * (usRectangle.right - usRectangle.left) / x);
		double northBound = (usRectangle.bottom + (inputRecBoundary.top) * (usRectangle.top - usRectangle.bottom) / y);
		double southBound = (usRectangle.bottom + (inputRecBoundary.bottom - 1) * (usRectangle.top - usRectangle.bottom) / y);
		return (long) ForkJoinPool.commonPool().invoke(new Query(0, censusData.data_size, westBound, eastBound, northBound, southBound));
	}
	
	public void findPopulation() {
		Long popInArea = queryPopulation();
		System.out.println("Total Population in the Area: " + popInArea);
		System.out.println("Total Population: " + totalPopulation);
		float percent = (popInArea.floatValue() * 100)/totalPopulation.floatValue();
		System.out.printf("Percent of total population: %.2f \n",percent);
	}
	
    class Result {
        Rectangle rec;
        int population;
        Result(Rectangle rec, int pop) {
            this.rec = rec;
            population = pop;
        }
    }
    
	@SuppressWarnings("serial")
	class Preprocessor extends RecursiveTask<Result> {
        int hi, lo;

        // Look at data from lo (inclusive) to hi (exclusive)
        Preprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }

        /** {@inheritDoc} */
        @Override
        protected Result compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group = censusData.data[lo];
                int pop = group.population;
                Rectangle rec = new Rectangle(group.longitude, group.longitude,
                        group.latitude, group.latitude), temp;
                for (int i = lo + 1; i < hi; i++) {
                    group = censusData.data[i];
                    temp = new Rectangle(group.longitude, group.longitude,
                            group.latitude, group.latitude);
                    rec = rec.encompass(temp);
                    pop += group.population;
                }
                return new Result(rec, pop);
            } else {
                Preprocessor left = new Preprocessor(lo, (hi+lo)/2);
                Preprocessor right = new Preprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                Result rightAns = right.compute();//call compute directly
                Result leftAns = left.join();
                return new Result(rightAns.rec.encompass(leftAns.rec),
                        rightAns.population + leftAns.population);
            }

        }
    }
	
	@SuppressWarnings("serial")
	class Query extends RecursiveTask<Integer> {
        int hi, lo;
        double leftBound, rightBound, topBound, bottomBound;

        // Look at data from lo (inclusive) to hi (exclusive)
        // Query bounded by *Bound fields
        Query(int lo, int hi, double leftBound, double rightBound, double topBound, double bottomBound) {
            this.lo  = lo;
            this.hi = hi;
            this.leftBound = leftBound;
            this.rightBound = rightBound;
            this.topBound = topBound;
            this.bottomBound = bottomBound;
        }

        /** {@inheritDoc} */
        @Override
        protected Integer compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int population = 0;
                double groupLong, groupLat;

                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    groupLong = group.longitude;
                    groupLat = group.latitude;
                    // Defaults to North and/or East in case of tie
                    if (groupLat >= bottomBound &&
                            (groupLat < topBound ||
                                    (topBound - usRectangle.top) >= 0) &&
                                    (groupLong < rightBound ||
                                            (rightBound - usRectangle.right) >= 0) &&
                                            groupLong >= leftBound)
                        population += group.population;
                }

                return population;
            } else {
                Query left =
                        new Query(lo, (hi+lo)/2, leftBound, rightBound, topBound, bottomBound);
                Query right =
                        new Query((hi+lo)/2, hi, leftBound, rightBound, topBound, bottomBound);

                left.fork(); // fork a thread and calls compute
                Integer rightAns = right.compute(); // call compute directly
                Integer leftAns = left.join();
                return rightAns + leftAns;
            }

        }
    }
}
