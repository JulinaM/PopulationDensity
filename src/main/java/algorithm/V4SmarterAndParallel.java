package algorithm;

import base.CensusData;
import base.CensusGroup;

import javax.management.remote.rmi.RMIConnectionImpl;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class V4SmarterAndParallel extends V2SimpleAndParallel {

    private int[][] grid;

    public V4SmarterAndParallel(Integer x, Integer y, CensusData censusData) {
        super(x, y, censusData);
        this.grid = new int[x][y];
    }

    @Override
    public void findUSRectangle() {
        super.findUSRectangle();
        _createGrid();
    }

    private void _createGrid(){
        grid = ForkJoinPool.commonPool().invoke(new SmarterPreprocessor(0, censusData.data_size));

        // sum top edge (of graph)
        for (int i = 1; i < grid.length; i++) {
            grid[i][grid[0].length - 1] += grid [i - 1][grid[0].length - 1];
        }

        // sum left edge (of graph)
        for (int i = grid[0].length - 2; i >= 0; i--) {
            grid[0][i] += grid [0][i + 1];
        }

        for (int j = grid[0].length - 1 - 1; j >= 0; j--) {
            for (int i = 1; i < grid.length; i++) {
                grid[i][j] += (grid[i-1][j] + grid[i][j+1] - grid[i-1][j+1]);
            }
        }
    }

    @Override
    public void findPopulation() {
        this.popInArea = _queryPopulation();
        System.out.println("Total Population in the Area: " + popInArea);
        System.out.println("Total Population: " + totalPopulation);
        this.popPercent = ((float) popInArea * 100)/totalPopulation.floatValue();
        System.out.printf("Percent of total population: %.2f \n",this.popPercent);

    }

    private long _queryPopulation() {
        long totalPopulationInArea = 0L;
        totalPopulationInArea += grid[(int) (inputRecBoundary.right - 1)][(int) (inputRecBoundary.bottom - 1)];
        totalPopulationInArea -= (inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.right- 1)][(int) inputRecBoundary.top]); // top right
        totalPopulationInArea -= (inputRecBoundary.left == 1 ? 0 : grid[(int) (inputRecBoundary.left- 1 - 1)][(int) (inputRecBoundary.bottom - 1)]); // bottom left
        totalPopulationInArea += (inputRecBoundary.left == 1 || inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.left - 1 - 1)][(int) inputRecBoundary.top]); // top left
        return totalPopulationInArea;
    }

    // An internal class for preprocessing
    @SuppressWarnings("serial")
    class SmarterPreprocessor extends RecursiveTask<int[][]> {
        int hi, lo;

        // Look at data from lo (inlcusive) to hi (exclusive)
        SmarterPreprocessor(int lo, int hi) {
            this.lo  = lo;
            this.hi = hi;
        }

        /** {@inheritDoc} */
        @Override
        protected int[][] compute() {
            if(hi - lo <  cutoff) {
                CensusGroup group;
                int row, col;
                int[][] g = new int[x][y];

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
                    g[row][col] += group.population;

                }
                return g;

            } else {
                SmarterPreprocessor left = new SmarterPreprocessor(lo, (hi+lo)/2);
                SmarterPreprocessor right = new SmarterPreprocessor((hi+lo)/2, hi);

                left.fork(); // fork a thread and calls compute
                int[][] gRight = right.compute();//call compute directly
                int[][] gLeft = left.join();

                // add the two grids together in parallel
                RMIConnectionImpl fjPool;
                ForkJoinPool.commonPool().invoke(new AddGrids(0, x, gLeft, gRight));
                return gRight;
            }

        }
    }

    @SuppressWarnings("serial")
    class AddGrids extends RecursiveAction {
        int xhi, xlo;
        int[][] l, r;

        /*
         * examines data from two grids in the range:
         *
         * (xlo,yhi)    ...    (xhi,yhi)
         *     .                   .
         *     .                   .
         *     .                   .
         * (xlo,ylo)    ...    (xhi,ylo)
         */
        AddGrids(int xlo, int xhi, int[][] l, int[][] r) {
            this.xlo  = xlo;
            this.xhi = xhi;
            this.l = l;
            this.r = r;
        }

        /** {@inheritDoc} */
        @Override
        protected void compute() {
            // find the optimal cutoff, based on grid size
            //			int cutoff = (int) Math.pow(10, -1 + Math.floor(Math.log(x*y))) + 10;

            if((xhi-xlo) <  cutoff) {
                for(int i = xlo; i < xhi; i++) {
                    for(int j = 0; j < y; j++)
                        r[i][j] += l[i][j];
                }
            } else {
                AddGrids left = new AddGrids(xlo, (xhi+xlo)/2, l, r);
                AddGrids right = new AddGrids((xhi+xlo)/2, xhi, l, r);

                left.fork(); // fork a thread and calls compute
                right.compute();//call compute directly
                left.join();
            }

        }
    }


}
