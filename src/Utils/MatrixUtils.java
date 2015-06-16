/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

/**
 *
 * @author Franklin
 */
public class MatrixUtils {
    public static void printNumberMatrix(double [][] matrix ){
        int row = matrix.length;
        for(int i=0; i<row; i++){
            int column = matrix[i].length;
            for(int j=0; j<column; j++){
               System.out.format("  %2f  ", matrix[i][j]);
            }
            System.out.println();  // To move to the next line.
        }
    }
}
