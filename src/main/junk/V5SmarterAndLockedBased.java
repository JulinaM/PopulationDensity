import java.util.concurrent.locks.ReentrantLock;

public class V5SmarterAndLockedBased extends V2SimpleAndParallel {
    // Additional grid to maintain locks for threads
    private final ReentrantLock[][] locks;
	int[][] grid;
    
    public V5SmarterAndLockedBased(int x, int y, CensusData data,int cutoff) {
        super(x, y, data,cutoff);
        grid = new int[x][y];
        locks = new ReentrantLock[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                locks[i][j] = new ReentrantLock();
            }
        }
    }
    
    public void findUSRectangle() {
		super.findUSRectangle();
		
		SmarterPreprocessor sp = new SmarterPreprocessor(0, censusData.data_size);
        sp.run();

        // sum top edge (of graph)
        for (int i = 1; i < grid.length; i++) {
            grid[i][grid[0].length - 1] += grid [i - 1][grid[0].length - 1];
        }

        // sum left edge (of graph)
        for (int i = grid[0].length - 2; i >= 0; i--) {
            grid[0][i] += grid [0][i + 1];
        }

        //  second step of grid addition
        for (int j = grid[0].length - 1 - 1; j >= 0; j--) {
            for (int i = 1; i < grid.length; i++) {
                grid[i][j] += (grid[i-1][j] + grid[i][j+1] - grid[i-1][j+1]);
            }
        }
	}
    
    public long queryPopulation() {
    		Long totalPopulationInArea = new Long(0);
		
		totalPopulationInArea += grid[(int) (inputRecBoundary.right - 1)][(int) (inputRecBoundary.bottom - 1)];
		totalPopulationInArea -= (inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.right- 1)][(int) inputRecBoundary.top]); // top right
		totalPopulationInArea -= (inputRecBoundary.left == 1 ? 0 : grid[(int) (inputRecBoundary.left- 1 - 1)][(int) (inputRecBoundary.bottom - 1)]); // bottom left
		totalPopulationInArea += (inputRecBoundary.left == 1 || inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.left - 1 - 1)][(int) inputRecBoundary.top]); // top left

		return totalPopulationInArea;
    	
    }
    
	public void findPopulation() {
		Long popInArea = queryPopulation();
		System.out.println("Total Population in the Area: " + popInArea);
		System.out.println("Total Population: " + totalPopulation);
		float percent = (popInArea.floatValue() * 100)/totalPopulation.floatValue();
		System.out.printf("Percent of total population: %.2f \n",percent);
	}
    
 // An internal class for preprocessing
    class SmarterPreprocessor extends java.lang.Thread {
        int hi, lo;
        // Look at data from lo (inlcusive) to hi (exclusive)
        SmarterPreprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }

        /** {@inheritDoc} */
        @Override
        public void run() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int row, col;
                
                float yAxis = usRectangle.left;
                float xAxis = usRectangle.bottom;
                float gridSquareWidth = (usRectangle.right - usRectangle.left) / x;
                float gridSquareHeight = (usRectangle.top - usRectangle.bottom) / y;
                		
                for (int i = lo; i < hi; i++) {
                    group = censusData.data[i];
                    col = (int) ((group.latitude - xAxis) / gridSquareHeight);
                    // Default to North
                    if (group.latitude >= (col + 1) * gridSquareHeight + xAxis)
                        col++;
                    col = (col == y ?  y - 1: col); // edge case due to rounding
                    row = (int) ((group.longitude - yAxis) / gridSquareWidth);
                    // Default to East
                    if (group.longitude >= (row + 1) * gridSquareWidth + yAxis)
                        col++;
                    row = (row == x ? x - 1 : row); // edge case due to rounding

                    // lock it up
                    locks[row][col].lock();
                    try {
                        grid[row][col] += group.population;
                    } finally {
                        locks[row][col].unlock();
                    }
                }

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.start(); // fork a thread and calls compute
                right.run(); //call compute directly
                try {
                    left.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
