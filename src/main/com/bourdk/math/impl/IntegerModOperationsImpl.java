package com.bourdk.math.impl;

import com.bourdk.exceptions.ModuloException;
import com.bourdk.exceptions.OperationException;
import com.bourdk.math.IntegerModOperations;
import com.bourdk.types.IntegerMod;

public class IntegerModOperationsImpl implements IntegerModOperations {

    final static int HALF_WORD_PRIME_MOD = 65521; // mod is largest prime that fits in 16 bits
    final static int WORD_PRIME_MOD = Integer.MAX_VALUE; // mod is prime and fits in less than Integer.BYTES;
    final static long DOUBLE_WORD_PRIME_MOD = Long.MAX_VALUE; // mod is prime and fits in less than Long.BYTES;
    
    static int modulo;
    
    @Override
    public void initModulo(int m) {
        IntegerModOperationsImpl.modulo = m;
    }

    /**
     * Add two int arrays within Z mod modulo. Perform optimization based on size of modulo.
     */
    @Override
    public int[] add(int[] a, int[] b) throws ModuloException, OperationException {
        if(modulo <= 0){
            throw new ModuloException("Mod must be a positive integer");
        } else if (a.length != b.length){
            throw new OperationException("Two vector summands must be of the same length");
        }
        
        // perform addition based on mod size
        if(modulo <= HALF_WORD_PRIME_MOD){
            return addHelper(a, b, 16);
        } else if(modulo > HALF_WORD_PRIME_MOD && modulo <= WORD_PRIME_MOD){
            return addHelper(a, b, 32);
        } else {
            return addHelper(a, b, 64);
        }
    }
    
    // component-wise add a & b, whose elements don't exceed modSize in bits
    private int[] addHelper(int[] a, int[] b, int modSize){
        int numAdds = 0;  // track the number of additions performed in this call
        int[] res = new int[a.length];        
        
        // mod fits in less than 32 bits
        if(modSize < Integer.SIZE) {
            for(int i = 0; i < a.length; i++){
                res[i] = (a[i] + b[i]) % modulo;
            }
        }
        return res;
    }
    
    @Override
    public IntegerMod[] add(IntegerMod[] a, IntegerMod[] b) throws ModuloException, OperationException {
        // mod value of the arguments takes precedence over value in this class

        if(a.length != b.length || a.length <= 0){
            throw new OperationException("Two vector summands must be of the same length");
        } else if (a[0].getMod() != b[0].getMod() || a[0].getMod() <= 0){
            throw new ModuloException("Mod values of the arguments must be equal & positive");
        }
        
        int modulo = a[0].getMod();
        
        // perform addition based on mod size
        if(modulo <= HALF_WORD_PRIME_MOD){
            return addModHelper(a, b, modulo, 16);
        } else if(modulo > HALF_WORD_PRIME_MOD && modulo <= WORD_PRIME_MOD){
            return addModHelper(a, b, modulo, 32);
        } else {
            return addModHelper(a, b, modulo, 64);
        }
    }
    
    private IntegerMod[] addModHelper(IntegerMod[] a, IntegerMod[] b, int mod, int modSize){
        IntegerMod[] res = new IntegerMod[a.length];
        
        // mod fits in less than 32 bits
        if(modSize < Integer.SIZE) {
            for(int i = 0; i < a.length; i++){
                res[i] = new IntegerMod();
                res[i].setMod(mod);
                res[i] = new IntegerMod(a[i].intValue() + b[i].intValue(), mod);
            }
        }
    }

    // dot product of A and B, i.e., {for i in 0..A.length-1 do C += A[i] + B[i]}
    @Override
    public int dot(int[] a, int[] b) throws ModuloException, OperationException {
        if(modulo <= 0){
            throw new ModuloException("Mod must be a positive integer");
        } else if (a.length != b.length){
            throw new OperationException("Two operands must be of the same length");
        }
        
        // perform dot based on mod size
        if(modulo <= HALF_WORD_PRIME_MOD){
            return dotHelper(a, b, 16);
        } else if(modulo > HALF_WORD_PRIME_MOD && modulo <= WORD_PRIME_MOD){
            return dotHelper(a, b, 32);
        } else {
            return dotHelper(a, b, 64);
        }
    }
    
    private int dotHelper(int[] a, int[] b, int modSize){
        int res = 0;
        if(modSize <= Short.SIZE){ // half-word, can safely perform multiplication inside a word
            for(int i = 0; i < a.length; i++){
                int mul = (a[i] * b[i]) % modulo;
                res += mul;
                // can add up to 15 times, before result starts overflowing int size
                if(i != 0 && i % 15 == 0){
                    res = res % modulo;
                }
            }
        }
        
        return res;
    }

    @Override
    public int[][] mtxAdd(int[][] A, int[][] B) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[][] mtxMult(int[][] A, int[][] B) {
        // TODO Auto-generated method stub
        return null;
    }

   

    @Override
    public IntegerMod[] dot(IntegerMod[] a, IntegerMod[] b) {
        // TODO Auto-generated method stub
        return null;
    }

}
