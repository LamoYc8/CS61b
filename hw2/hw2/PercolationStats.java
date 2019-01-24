package hw2;
/*
 * StdRandom generates random numbers
 * StdStats computes the sample mean and standard deviation
 * */


import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats {
    private int grid;
    private int experiments;
    private double[] singleThresholds;


    public PercolationStats(int N, int T, PercolationFactory pf)   // perform T independent experiments on an N-by-N grid
    {
        if ((N <= 0) || (T <= 0))
            throw new IllegalArgumentException("Illegal argument input");

        singleThresholds = new double[T];

        grid = N;

        experiments = T;

        while (T > 0) {

            Percolation percolation = pf.make(N);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);

                percolation.open(row, col);
            }

            singleThresholds[T - 1] = (double) percolation.numberOfOpenSites() / Math.pow(N, 2);
            T -= 1;

        }
    }

    public double mean()                                           // sample mean of percolation threshold
    {

        return StdStats.mean(singleThresholds);

    }

    public double stddev()                                         // sample standard deviation of percolation threshold
    {
        return StdStats.stddev(singleThresholds);
    }

    public double confidenceLow()                                  // low endpoint of 95% confidence interval
    {
        double result = 1.96 * stddev() / Math.sqrt(experiments);
        return mean() - result;
    }

    public double confidenceHigh()                                 // high endpoint of 95% confidence interval
    {
        double result = 1.96 * stddev() / Math.sqrt(experiments);
        return mean() + result;
    }

}
