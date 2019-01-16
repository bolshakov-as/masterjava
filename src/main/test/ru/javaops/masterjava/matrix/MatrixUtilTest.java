package ru.javaops.masterjava.matrix;

import org.junit.Test;

public class MatrixUtilTest {

    @Test
    public void testConcurrentMultiply() throws Exception {
        int[][] matrixA = MatrixUtil.create(1000);
        int[][] matrixB = MatrixUtil.create(1000);

        {
            long before = System.currentTimeMillis();
            int[][] ints = MatrixUtil.singleThreadMultiply(matrixA, matrixB);
            long after = System.currentTimeMillis();
            System.out.println("Single: " + (before - after));
        }


    }
}