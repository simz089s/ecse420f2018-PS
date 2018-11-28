package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;

public class MergeMatrix {

  public static final int MATRIX_SIZE = 2000;

  public static ExecutorService execs = Executors.newCachedThreadPool();

  public static void sumUpRows(
      double[][] matrix, int top, int bot, int left, int midH, double[] resultMatrix) {

    if (bot - 1 == top) {
      matrix[top][left] += matrix[top][midH];
      return;
    }

    int midV = (bot - top) / 2 + top;

    Future<?> sumTop = execs.submit(() -> sumUpRows(matrix, top, midV, left, midH, resultMatrix));
    Future<?> sumBot = execs.submit(() -> sumUpRows(matrix, midV, bot, left, midH, resultMatrix));
    try {
      sumTop.get();
      sumBot.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

  public static void mergeMatrixProductSums(
      double[][] matrix,
      double[] vector,
      int top,
      int bot,
      int left,
      int right,
      double[] resultMatrix) {

    if (bot - 1 == top && right - 1 == left) {
      matrix[top][left] *= vector[left];
      return;
    } else if (bot - 1 == top) {
      matrix[top][left] *= vector[left];
      matrix[top][left + 1] *= vector[left + 1];
      matrix[top][left] += matrix[top][left + 1];
      return;
    } else if (right - 1 == left) {
      matrix[top][left] *= vector[left];
      matrix[top + 1][left] *= vector[left];
      return;
    }

    int midV = (bot - top) / 2 + top;
    int midH = (right - left) / 2 + left;

    Future<?> mergeTopLeft =
        execs.submit(
            () -> mergeMatrixProductSums(matrix, vector, top, midV, left, midH, resultMatrix));
    Future<?> mergeTopRight =
        execs.submit(
            () -> mergeMatrixProductSums(matrix, vector, top, midV, midH, right, resultMatrix));
    Future<?> mergeBottomLeft =
        execs.submit(
            () -> mergeMatrixProductSums(matrix, vector, midV, bot, left, midH, resultMatrix));
    Future<?> mergeBottomRight =
        execs.submit(
            () -> mergeMatrixProductSums(matrix, vector, midV, bot, midH, right, resultMatrix));
    try {
      mergeTopLeft.get();
      mergeTopRight.get();
      mergeBottomLeft.get();
      mergeBottomRight.get();
      sumUpRows(matrix, top, bot, left, midH, resultMatrix);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

  public static double[] multiplyMatrix(double[][] matrix, double[] vector) {

    double[][] matrixCopy = Arrays.copyOf(matrix, matrix.length);
    double[] resultMatrix = new double[vector.length];
    mergeMatrixProductSums(matrixCopy, vector, 0, matrix.length, 0, matrix[0].length, resultMatrix);
    for (int i = 0; i < matrix.length; i++) {
      resultMatrix[i] = matrix[i][0];
    }
    return resultMatrix;
  }

  public static void main(String[] args) {

    double[] v = generateRandomVector(MATRIX_SIZE);
    double[][] m = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    System.out.println(
        Arrays.deepToString(m)
            .replace("], [", "],\n\t[")
            .replace("[[", "{\n\t[")
            .replace("]]", "]\n}"));
    System.out.println(Arrays.toString(v));
    System.out.println(Arrays.toString(sequentialMultiplyMatrix(m, v)));
    System.out.println(Arrays.toString(multiplyMatrix(m, v)));

    measureParallelmultiplyMatrix(m, v);
    measureSequentialTime(m, v);

    execs.shutdown();
    while (!execs.isTerminated()) {}
  }

  private static double[][] generateRandomMatrix(int numRows, int numCols) {
    double matrix[][] = new double[numRows][numCols];
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        matrix[row][col] = (double) ((int) (Math.random() * 10.0));
      }
    }
    return matrix;
  }

  private static double[] generateRandomVector(int num) {
    double vector[] = new double[num];
    for (int row = 0; row < num; row++) {
      vector[row] = (double) ((int) (Math.random() * 10.0));
    }
    return vector;
  }

  public static double[] sequentialMultiplyMatrix(double[][] matrix, double[] vector) {
    double[] result = new double[MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  public static void measureSequentialTime(double[][] matrix, double[] vector) {
    long startTime = System.nanoTime();
    sequentialMultiplyMatrix(matrix, vector);
    long endTime = System.nanoTime();
    long runTime = endTime - startTime;
    System.out.println("Runtime (sequential) : " + runTime + " ns");
  }

  public static void measureParallelmultiplyMatrix(double[][] matrix, double[] vector) {
    long startTime = System.nanoTime();
    multiplyMatrix(matrix, vector);
    long endTime = System.nanoTime();
    long runTime = endTime - startTime;
    System.out.println("Runtime ( parallel ) : " + runTime + " ns");
  }
}
