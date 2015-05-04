/*
 * RSA function implements RSA algorithm.
 * It generates a public & private key when the RSA object is created.
 * encrypt & decrypt methods can be used to encrypt or decrypt single characters.
 * To create RSA object 2 numbers have to be provided.
 * */
import java.math.BigInteger;

public class RSA extends Prime{ //extends Prime to use its generatePrime and isPrime functions.
  
  private BigInteger e; //public key
  private BigInteger d; //private key
  private BigInteger n;
  
  //constructors
  public RSA(BigInteger p, BigInteger q)
  {
    if(!isPrime(p))
    {
      p = generatePrime(p);
    }
    
    if(!isPrime(q))
    {
      q = generatePrime(q);
    }
    
    keygen(p,q); //ensures that p & q are prime.
  }
  
  //directly set e,d & n.
  public RSA(BigInteger x, BigInteger y, BigInteger z)
  { 
    e = x;
    d = y;
    n = z;
  }
  
  public String getPrivateKey()
  {
    return d.toString();
  }
  
  public String getPublicKey()
  {
    return e.toString();
  }
  
  public String getN()
  {
    return n.toString();
  }
  
  //generates public and private keys given p & q, random integers.
  public void keygen(BigInteger p, BigInteger q)
  {
    if(isPrime(p) && isPrime(q))
    {
      n = p.multiply(q);
      BigInteger nTotient = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
      e = random(nTotient.bitLength());

      while(!(e.gcd(nTotient).equals(BigInteger.ONE))|| e.compareTo(nTotient) > -1 || e.equals(BigInteger.ZERO))
      {
        e = random(n.bitLength());
      }
      BigInteger[] temp = extendedEuclideanMethod(nTotient, e);
      
      d = temp[1];
      if(d.compareTo(BigInteger.ZERO) < 0)
        d = nTotient.add(d);
    }
  }
  
  //encrypts a character.
  public BigInteger encrypt(char c)
  {
    int i = c;
    String s = "" + i;
    BigInteger m = new BigInteger(s);
    m = m.modPow(e, n);
    return m;
  }
  
  // decrypts a cipher of character back to teh character.
  public char decrypt(BigInteger m)
  { 
    m = m.modPow(d, n);
    int i = m.intValue();
    char c = (char) i;
    return c;
  }
  
  // private method.
  // Best and fastest way to calculate a private key(d) when public key(e) and n are known.
  private BigInteger[] extendedEuclideanMethod(BigInteger totient, BigInteger publicKey)
  {
    BigInteger[] data = new BigInteger[2];

    if(publicKey.equals(BigInteger.ZERO))
    {
      data[0] = BigInteger.ONE;
      data[1] = BigInteger.ZERO;
      return data;
    }else
    {
      data = totient.divideAndRemainder(publicKey);
      BigInteger q = data[0];
      BigInteger r = data[1];
      data = extendedEuclideanMethod(publicKey, r);
      
      BigInteger x = data[0];
      BigInteger y = data[1];
      x = x.subtract(y.multiply(q));
      data[0] = y;
      data[1] = x;
      return data;
    }
  }
}