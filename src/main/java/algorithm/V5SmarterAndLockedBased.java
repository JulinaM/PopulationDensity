package algorithm;

import base.CensusData;
import base.CensusGroup;

import java.util.concurrent.locks.ReentrantLock;

public class V5SmarterAndLockedBased extends V2SimpleAndParallel {
    // Additional grid to maintain locks for threads
    private final ReentrantLock[][] locks;
    int[][] grid;


    public V5SmarterAndLockedBased(Integer x, Integer y, CensusData censusData) {
        super(x, y, censusData);
        this.grid = new int[x][y];
        this.locks = new ReentrantLock[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                this.locks[i][j] = new ReentrantLock();
            }
        }
    }

    @Override
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
