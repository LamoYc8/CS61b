package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
/*
Part of the goal of this assignment is to
learn how to cast one problem (Percolation)
in terms of an already solved problem (Disjoint Sets, a.k.a Union Find).
*/

public class Percolation {
    private boolean[][] underground;
    private int openSiteN;
    private WeightedQuickUnionUF wquuf; //Instance class of WQUF class
    private WeightedQuickUnionUF nobackWash; //Aim to solve backwash problem
    private int grid;

    //Using virtual site to speed up percolation and isFull operations
    //Idea comes from HW2 slides
    private int topVirtual;
    private int bottomVirtual;


    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {

        if (N < 0)
            throw new IllegalArgumentException("N is not illegal!");

        grid = N;
        openSiteN = 0;

        underground = new boolean[N][N];
        for (int i = 0; i < underground.length; i++) {
            for (int j = 0; j < underground[i].length; j++) {
                underground[i][j] = false;
            }
        }

        //Math.pow(base, exponent)
        wquuf = new WeightedQuickUnionUF((int) Math.pow(N, 2) + 2);
        nobackWash = new WeightedQuickUnionUF((int) Math.pow(N, 2) + 1);

        topVirtual = (int) Math.pow(N, 2);
        bottomVirtual = topVirtual + 1;

    }

    /*
       Coding mistakes: 转换算法仔细检查
       Index starts from 0 to N * N -1;
       Idea comes from HW2 slides
     */
    private int xyTo1D(int row, int col) {
        return underground.length * row + col;
    }


    // open the site (row, col) if it is not open already
    public void open(int row, int col) {
        if ((row < 0) || (row >= grid) || (col < 0) || (col >= grid))
            throw new ArrayIndexOutOfBoundsException("Bad inputs");

        if (underground[row][col])
            return;

        underground[row][col] = true;

        openSiteN += 1;

        checkAndUnion(row, col);

        if (row == 0) {
            wquuf.union(xyTo1D(row, col), topVirtual);
            nobackWash.union(xyTo1D(row, col), topVirtual);
        }


        if (row == grid - 1)
            wquuf.union(xyTo1D(row, col), bottomVirtual);


        //Avoid backwash issue
        //Pay attention to the order when unions the top and bottom virtual sites
        /*if((row == grid -1 ) && isFull(row, col))
            wquuf.union(xyTo1D(row, col), bottomVirtual);*/


        /* Using checkAndUnion function instead, reducing code
        if((row > 0)&&(row < grid -1) && (col >0)&&(col < grid -1))
        {
            if(isOpen(row-1,col))
                wquuf.union(xyTo1D(row,col),xyTo1D(row-1, col));
            if(isOpen(row+1,col))
                wquuf.union(xyTo1D(row,col),xyTo1D(row+1, col));
            if(isOpen(row,col-1))
                wquuf.union(xyTo1D(row,col),xyTo1D(row, col-1));
            if(isOpen(row,col+1))
                wquuf.union(xyTo1D(row,col),xyTo1D(row, col+1));

        }else if(row == 0)  {
            if(isOpen(row + 1, col))
                wquuf.union(xyTo1D(row, col), xyTo1D(row + 1, col));

        }else if(row == grid -1){
            if(isOpen(row-1, col))
                wquuf.union(xyTo1D(row, col), xyTo1D(row -1, col));
            if(col == 0){
                if(isOpen(row, col + 1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col + 1));
            }else if(col == grid - 1){
                if(isOpen(row , col - 1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col -1));
            }else {
                if(isOpen(row, col + 1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col + 1));
                if(isOpen(row , col - 1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col -1));
            }
        }
        else {

            if(isOpen(row +1,col))
                wquuf.union(xyTo1D(row, col), xyTo1D(row +1, col));
            if(isOpen(row -1, col))
                wquuf.union(xyTo1D(row, col), xyTo1D(row -1, col));
            if(col == 0) {
                if (isOpen(row, col + 1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col + 1));
            }
            else {
                if(isOpen(row, col -1))
                    wquuf.union(xyTo1D(row, col), xyTo1D(row, col-1));


            }
        }*/


    }

    //Using coordination system and radius(distance) to reduce pre-union checking operation code
    //Based on this project, ignore those items located at the center(itself) and across corners
    private void checkAndUnion(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (tPointD(row, col, i, j) == 1) {
                    if ((0 <= i) && (i <= grid - 1) && (0 <= j) && (j <= grid - 1))
                        if (isOpen(i, j)) {
                            wquuf.union(xyTo1D(row, col), xyTo1D(i, j));
                            nobackWash.union(xyTo1D(row, col), xyTo1D(i, j));
                        }
                }
            }
        }
    }

    //Calculate the distance between two points
    private double tPointD(int x1, int y1, int x2, int y2) {
        double xT = Math.pow(x1 - x2, 2);
        double yT = Math.pow(y1 - y2, 2);

        return Math.sqrt(xT + yT);

    }

    public boolean isOpen(int row, int col)  // is the site (row, col) open?
    {
        if ((row < 0) || (row >= grid) || (col < 0) || (col >= grid))
            throw new ArrayIndexOutOfBoundsException("Bad inputs");

        if (underground[row][col])
            return true;
        return false;
    }

    public boolean isFull(int row, int col)  // is the site (row, col) full?
    {
        if ((row < 0) || (row >= grid) || (col < 0) || (col >= grid))
            throw new ArrayIndexOutOfBoundsException("Bad inputs");

        //check isOpen at first, otherwise isFull makes no sense here
        if (isOpen(row, col) && nobackWash.connected(xyTo1D(row, col), topVirtual)) {
            return true;

            //Using virtual top site to speed up this operation into constant time rather than linear time
            /*
            if (row == 0)
                return true;

            for (int i = 0; i < underground.length; i++) {
                if (wquuf.connected(i, xyTo1D(row, col)) && isOpen(0,i))
                    return true;

            }*/


        }

        return false;
    }

    public int numberOfOpenSites()           // number of open sites
    {
        return openSiteN;
    }

    public boolean percolates()              // does the system percolate?
    {
        /*for (int i = 0; i < grid; i++) {
            for (int j = 0; j < grid; j++) {
                if(wquuf.connected(xyTo1D(0,i), xyTo1D(grid-1, j))){
                    return true;
                }
            }
        }*/

        if (wquuf.connected(topVirtual, bottomVirtual))
            return true;

        return false;
    }

    public static void main(String[] args)   // use for unit testing (not required)
    {


    }

}
