package Server;

import java.util.Arrays;

public class mergeSort implements Runnable{
   private Integer [] array;
   private int [] intermediate;
   
    mergeSort(Integer [] array)
    {   this.array=array;
        this.intermediate=  new int [array.length];
        for(int j=0;j<this.intermediate.length;j++){
            this.intermediate[j] = array[j];
        }
    }


    @Override
    public void run() {
        sort(intermediate, array.length);
        for(int j=0;j<this.array.length;j++){
            this.array[j] = intermediate[j];
        }
    }

    private void sort(int[] A, int n){
        // Set sizes for left and right lists
        int n1 = n/2;
        int n2 = n/2;
        // Account for arrays with odd length
        if(n%2 != 0){
            n2 = (n/2)+1;
        }
        // Call recurrence while the current sub array is not one element
        if(n > 1){
            // Create sub lists
            int[] B = new int[n1];
            int[] C = new int[n2];
            // Copy values over to sub lists
            copy(A, B, 0, (n/2)-1);
            copy(A, C, (n/2), n-1);
            // Pass sub lists recursively
            sort(B, B.length);
            sort(C, C.length);
            // Merge sorted sub lists back to original
            merge(B, C, A);
        }
    }

    private void merge(int[] B, int[] C, int[] A){
        // Initialize three pointers for each list
        int i = 0;
        int j = 0;
        int k = 0;
        // Break pointer when the end of one sub list is reached
        while(i < B.length && j < C.length){
            // Compare both sublists and take lower value
            if(B[i] <= C[j]){
                A[k] = B[i];
                i++;
            }else{
                A[k] = C[j];
                j++;
            }
            // Increment pointer for merged list
            k++;
        }
        // Copy any extra values from list that is not empty
        if(i == B.length){
            for(int l = j; l < C.length; l++){
                A[k] = C[l];
                k++;
            }
        }else{
            for(int l = i; l < B.length; l++){
                A[k] = B[l];
                k++;
            }
        }
        for (int p=0;p< array.length;p++){
            array[i] = A[i];
        }
    }

    // Helper method to copy list to new list
    private void copy(int[] A, int[] B, int start, int end){
        int j = start;
        for(int i = 0; i <= (end-start); i++){
            B[i] = A[j];
            j++;
        }
    }
}
