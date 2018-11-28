package ca.mcgill.ecse420.a3bis;

import ca.mcgill.ecse420.a1.MatrixMultiplication;

import java.util.Arrays;
import java.util.concurrent.*;

public class MatrixVectorMultiplicationV2 {
  private static final int MATRIX_SIZE = 4;

  // To make it easier to use inside the nested ParallelMatrixMultiplication class
  protected static double[][] matrix;
  protected static double[] vector;
  protected static ExecutorService executor = Executors.newCachedThreadPool();

  public static void main(String[] args) {

    // Generate two random matrices, same size
    MatrixVectorMultiplicationV2.matrix = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
    MatrixVectorMultiplicationV2.vector = generateRandomVector(MATRIX_SIZE);
    // measureSequentialTime();
    // measureParallelTime();
    printMatrix(matrix);
    System.out.println();
    printVector(vector);
    System.out.println();
    printVector(sequentialMultiplyMatrix(matrix, vector));
    System.out.println();
    Future<double[]> v =
        executor.submit(new sumVector.multiplyMatrixVector(matrix, vector));
    try {
      printVector(v.get());
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  public static void measureSequentialTime() {
    long startTime = System.currentTimeMillis();
    sequentialMultiplyMatrix(matrix, vector);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (sequential) : " + runTime);
  }

  public static void measureParallelTime() {
    long startTime = System.currentTimeMillis();
    parallelMultiplyMatrix(matrix, vector);
    long endTime = System.currentTimeMillis();
    long runTime = endTime - startTime;
    System.out.println("Runtime (parallel) : " + runTime);
  }

  /**
   * Returns the result of a sequential matrix multiplication The two matrices are randomly
   * generated
   *
   * @param matrix is the matrix
   * @param vector is the vector
   * @return the result of the multiplication
   */
  public static double[] sequentialMultiplyMatrix(double[][] matrix, double[] vector) {
    double[] result = new double[MATRIX_SIZE];
    for (int i = 0; i < MATRIX_SIZE; i++) {
      for (int j = 0; j < MATRIX_SIZE; j++) {
        result[i] += matrix[i][j] * vector[j];
      }
    }
    return result;
  }

  /**
   * Returns the result of a concurrent matrix multiplication The two matrices are randomly
   * generated
   *
   * @param matrix is the matrix
   * @param vector is the vector
   * @return the result of the multiplication
   */
  public static double[] parallelMultiplyMatrix(double[][] matrix, double[] vector) {
    return null;
  }

  /**
   * Populates a matrix of given size with randomly generated integers between 0-10.
   *
   * @param numRows number of rows
   * @param numCols number of cols
   * @return matrix
   */
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

  public static void printMatrix(double[][] matrix) {
    for (double[] r : matrix) {
      for (int i = 0; i < r.length; i++) {
        System.out.print(r[i] + "\t");
      }
      System.out.println();
    }
  }

  public static void printVector(double[] vector) {
    for (int i = 0; i < vector.length; i++) {
      System.out.print(vector[i] + "\t");
    }
    System.out.println();
  }

  public static class sumVector extends MatrixMultiplication implements Runnable {
    private int start;
    private int end;
    private double[] firstVector;
    private double[] secondVector;
    protected double[] answerVector;

    public sumVector(
        double[] firstVector, double[] secondVector, int start, int end, double[] answerVector) {
      this.start = start;
      this.end = end;
      this.firstVector = firstVector;
      this.secondVector = secondVector;
      this.answerVector = answerVector;
    }

    @Override
    public void run() {
      if (start == end - 1) {
        answerVector[start] = firstVector[start] + secondVector[start];
      }
      int newStart = end / 2;
      Future<?> top =
          executor.submit(new sumVector(firstVector, secondVector, start, newStart, answerVector));
      Future<?> bottom =
          executor.submit(new sumVector(firstVector, secondVector, newStart, end, answerVector));
      try {
        top.get();
        bottom.get();
      } catch (Exception e) {
        System.out.println("Failed to add properly");
      }
    }

    public static class multiplyMatrixVector implements Callable<double[]> {
      private double[][] personalMatrix;
      private double[] personalVector;
      protected double[] answerVector;

      public multiplyMatrixVector(double[][] matrix, double[] vector) {
        this.personalMatrix = matrix;
        this.personalVector = vector;
        if (vector != null) {
          this.answerVector = new double[vector.length];
        }
      }

      @Override
      public double[] call() throws Exception {
        if (answerVector == null) {
          double[] ans = {0.0};
          return ans;
        }
        if(answerVector.length == 1) {
          answerVector[0] = personalMatrix[0][0]*personalVector[0];
          return answerVector;
        }
        double[][] upLeftMatrix = Arrays.copyOfRange(personalMatrix, 0, personalMatrix.length/2);
        double[][] upRightMatrix = Arrays.copyOfRange(personalMatrix, personalMatrix.length/2, matrix.length);
        double[][] downLeftMatrix = Arrays.copyOfRange(personalMatrix, 0, matrix.length/2);
        double[][] downRightMatrix = Arrays.copyOfRange(personalMatrix, personalMatrix.length/2, matrix.length);
        double[] leftVector = Arrays.copyOfRange(personalVector, 0, personalVector.length/2);
        double[] rightVector = Arrays.copyOfRange(personalVector, personalVector.length/2,personalVector.length);
        for (int i = 0; i < upLeftMatrix.length ; i++) {
          upLeftMatrix[i] = Arrays.copyOfRange(personalMatrix[i], 0, personalMatrix[i].length/2);
          downLeftMatrix[i] = Arrays.copyOfRange(personalMatrix[i], personalMatrix[i].length/2, personalMatrix[i].length);
        }
        for (int i = 0; i < upRightMatrix.length ; i++) {
          upRightMatrix[i] = Arrays.copyOfRange(personalMatrix[i], 0, personalMatrix[i].length/2);
          downRightMatrix[i] = Arrays.copyOfRange(personalMatrix[i], personalMatrix[i].length/2, personalMatrix[i].length);
        }
          Future<double[]> upLeftEx = executor.submit(new multiplyMatrixVector(upLeftMatrix, leftVector));
          Future<double[]> upRightEx = executor.submit(new multiplyMatrixVector(upRightMatrix, rightVector));
          Future<double[]> downLeftEx = executor.submit(new multiplyMatrixVector(downRightMatrix, rightVector));
          Future<double[]> downRightEx = executor.submit(new multiplyMatrixVector(downLeftMatrix, leftVector));
        double[] topLeftAr = upLeftEx.get();
        double[] topRightAr = upRightEx.get();
        double[] bottomLeftAr = downLeftEx.get();
        double[] bottomRightAr = downRightEx.get();
        for (int i = 0; i < topLeftAr.length; i++) {
          answerVector[i] = topLeftAr[i] + topRightAr[i];
          answerVector[i+topLeftAr.length] = bottomLeftAr[i] + bottomRightAr[i];
        }
        return answerVector;
      }
    }
  }
}
