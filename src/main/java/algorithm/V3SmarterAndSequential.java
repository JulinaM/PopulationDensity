package algorithm;

import base.CensusData;
import base.CensusGroup;
import base.Rectangle;

public class V3SmarterAndSequential extends AbstractBaseAlgo {

    private int[][] grid;


    public V3SmarterAndSequential(Integer x, Integer y, CensusData censusData) {
        super(x, y, censusData);
        this.grid = new int[x][y];

    }

    @Override
    public void findUSRectangle() {
        if (censusData.data_size == 0)
            return;

        long pop = 0;
        CensusGroup group = censusData.data[0];

        Rectangle rec = new Rectangle(group.longitude, group.longitude, group.latitude, group.latitude);
        Rectangle temp;
        pop += group.population;

        for (int i = 1; i < censusData.data_size; i++) {
            group = censusData.data[i];
            temp = new Rectangle(group.longitude, group.longitude, group.latitude, group.latitude);
            rec = rec.encompass(temp);
            pop += group.population;
        }

        usRectangle = rec;

        float yAxis = usRectangle.left;
        float xAxis = usRectangle.bottom;
        float gridSquareWidth = (usRectangle.right - usRectangle.left) / x;
        float gridSquareHeight = (usRectangle.top - usRectangle.bottom) / y;

        totalPopulation = pop;
        int row, col;

        for (int i = 0; i < censusData.data_size; i++) {
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
            grid[row][col] += group.population;

        }

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

    @Override
    public void findPopulation() {
        this.popInArea = _queryPopulation();

        System.out.println("Total Population in the Area: " + popInArea);
        System.out.println("Total Population: " + totalPopulation);
        this.popPercent = ((float) popInArea * 100)/totalPopulation.floatValue();
        System.out.printf("Percent of total population: %.2f \n", popPercent);

    }

    private long _queryPopulation() {
        long totalPopulationInArea = 0L;
        totalPopulationInArea += grid[(int) (inputRecBoundary.right - 1)][(int) (inputRecBoundary.bottom - 1)];
        totalPopulationInArea -= (inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.right- 1)][(int) inputRecBoundary.top]); // top right
        totalPopulationInArea -= (inputRecBoundary.left == 1 ? 0 : grid[(int) (inputRecBoundary.left- 1 - 1)][(int) (inputRecBoundary.bottom - 1)]); // bottom left
        totalPopulationInArea += (inputRecBoundary.left == 1 || inputRecBoundary.top == y ? 0 : grid[(int) (inputRecBoundary.left - 1 - 1)][(int) inputRecBoundary.top]); // top left
        return totalPopulationInArea;
    }

}
