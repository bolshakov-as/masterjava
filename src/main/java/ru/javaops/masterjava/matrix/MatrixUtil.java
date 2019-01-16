package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static Integer[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final Integer[][] matrixC = new Integer[matrixSize][matrixSize];

        final CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executor);
        final List<Future<Boolean>> futures = new ArrayList<>();

        final int bColumns = matrixB[0].length;
        final int aColumns = matrixA[0].length;
        final int aRows = matrixA.length;

        for (int bColumn = 0; bColumn < bColumns; bColumn++) {
            final int fbRowColumn = bColumn;
            futures.add(completionService.submit(
                    () -> {
                        int tempColumn[] = new int[matrixB.length];
                        for (int aColumn = 0; aColumn < aColumns; aColumn++) {
                            tempColumn[aColumn] = matrixB[aColumn][fbRowColumn];
                        }
                        for (int aRow = 0; aRow < aRows; aRow++) {
                            int thisRow[] = matrixA[aRow];
                            int sum = 0;
                            for (int aColumn = 0; aColumn < aColumns; aColumn++) {
                                sum += thisRow[aColumn] * tempColumn[aColumn];
                            }
                            matrixC[aRow][fbRowColumn] = sum;
                        }
                        return true;
                    }
            ));
        }

        return new Callable<Integer[][]>() {
            @Override
            public Integer[][] call() {
                while (!futures.isEmpty()) {
                    try {
                        Future<Boolean> future = completionService.poll(10, TimeUnit.SECONDS);
                        if (future == null) {
                            return cancelWithFail();
                        }
                        futures.remove(future);
                    } catch (InterruptedException e) {
                        return cancelWithFail();
                    }

                }
                return matrixC;
            }

            private Integer[][] cancelWithFail() {
                futures.forEach(f -> f.cancel(true));
                return new Integer[0][0];
            }
        }.call();
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        int tempColumn[] = new int[matrixB.length];
        final int bColumns = matrixB[0].length;
        final int aColumns = matrixA[0].length;
        final int aRows = matrixA.length;
        for (int bColumn = 0; bColumn < bColumns; bColumn++) {
            final int fbRowColumn = bColumn;
            for (int aColumn = 0; aColumn < aColumns; aColumn++) {
                tempColumn[aColumn] = matrixB[aColumn][fbRowColumn];
            }
            for (int aRow = 0; aRow < aRows; aRow++) {
                int thisRow[] = matrixA[aRow];
                int sum = 0;
                for (int aColumn = 0; aColumn < aColumns; aColumn++) {
                    sum += thisRow[aColumn] * tempColumn[aColumn];
                }
                matrixC[aRow][fbRowColumn] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
