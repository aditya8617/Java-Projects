/*
 *Class Prime has 2 methods one generates the next prime number for the given integer and the other checks f a given number is prime.
 * Miller Rabin method to identify a prime number is used. The accuracy of this methid can be changed by adjusting parameter ACCURACY.
 */
import java.math.BigInteger;
import java.util.Random;

public class Prime {
  
  private static int ACCURACY = 10; //Accuracy constant for Miller rabin method.
  private static BigInteger TWO = new BigInteger("2");
  private static BigInteger THREE = new BigInteger("3");
  
  // generates the next prime number for the given integer
  public BigInteger generatePrime(BigInteger n)
  { 
    if((n.mod(TWO)).equals(BigInteger.ZERO))
    {
      n = n.add(BigInteger.ONE);
    }else
    {
      n = n.add(TWO);
    }
    
    while(!isPrime(n))
    {
      n = n.add(TWO);
    }
    return n;
  }
  
  // checks if a given number is prime or not with accuracy of 4 e-ACCURACY.
  public boolean isPrime(BigInteger n)
  {
    if(n.equals(BigInteger.ZERO))
    {
      System.out.println("Negative number, please give a positive number");
      return false;
    } else if(n.compareTo(THREE) < 0)
    {
      return false;
    }
    
    BigInteger m = n.mod(TWO);
    if(m.equals(BigInteger.ZERO))
    {
      return false;
    }
    return millerRabin(n);
  }
  
  //Miller Rabin algorithm for identifying a prime number.
  
  private boolean millerRabin(BigInteger n)
  {
    BigInteger d = n.subtract(BigInteger.ONE);
    int s = 0;
    boolean prime = false;
    
    while((d.mod(TWO)).equals(BigInteger.ZERO))
    {
      d = d.divide(TWO);
      s++;
    }
    
    int k = 0;
    while(k <= ACCURACY) //Loop helps identify any strong liars that make a number prime.
    {
      BigInteger r = random(n.bitLength());
      while(r.compareTo(n.subtract(TWO)) > 0 || r.compareTo(TWO) < 0)
      {
        r = random(n.bitLength());
      }
      prime = helper(r, d, s, n);
      k++;
    }
    
    return prime;
  }
  
  //helper method for Miller Rabin.
  private boolean helper(BigInteger r, BigInteger d, int s, BigInteger n)
  {
    int exponent = 0;
    BigInteger x = TWO;
    while((exponent < s))
    {
      BigInteger uExpo = (TWO.pow(exponent)).multiply(d);
      x = r.modPow(uExpo, n);
      if(x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE)))
      {
        return true;
      }
      exponent++;
    }
    return false;
  }
  
  //random number generator for BigInteger.
  public BigInteger random(int bits)
  {
    Random rnd = new Random();
    BigInteger r = new BigInteger(bits, rnd);
    return r;
  }
}