package arch.sm213.machine.student;

import machine.AbstractMainMemory;


/**
 * Main Memory of Simple CPU.
 *
 * Provides an abstraction of main memory (DRAM).
 */

public class MainMemory extends AbstractMainMemory {
  private byte [] mem;
  
  /**
   * Allocate memory.
   * @param byteCapacity size of memory in bytes.
   */
  public MainMemory (int byteCapacity) {
    mem = new byte [byteCapacity];
  }
  
  /**
   * Determine whether an address is aligned to specified length.
   * @param address memory address.
   * @param length byte length.
   * @return true iff address is aligned to length.
   */
  @Override public boolean isAccessAligned (int address, int length) {
      return address % length == 0;
  }
  
  /**
   * Convert an sequence of four bytes into a Big Endian integer.
   * @param byteAtAddrPlus0 value of byte with lowest memory address (base address).
   * @param byteAtAddrPlus1 value of byte at base address plus 1.
   * @param byteAtAddrPlus2 value of byte at base address plus 2.
   * @param byteAtAddrPlus3 value of byte at base address plus 3 (highest memory address).
   * @return Big Endian integer formed by these four bytes.
   */
  @Override public int bytesToInteger (byte byteAtAddrPlus0, byte byteAtAddrPlus1, byte byteAtAddrPlus2, byte byteAtAddrPlus3) {
    int b = 0xFF & (byteAtAddrPlus0);
    b = (b << 24);
    int c = 0xFF & (byteAtAddrPlus1);
    c = c << 16;
    int d = 0xFF & (byteAtAddrPlus2);
    d = d << 8;
    int e = 0xFF & (byteAtAddrPlus3);
    return b+c+d+e;
  }
  
  /**
   * Convert a Big Endian integer into an array of 4 bytes organized by memory address.
   * @param  i an Big Endian integer.
   * @return an array of byte where [0] is value of low-address byte of the number etc.
   */
  @Override public byte[] integerToBytes (int i) {
      byte[] byteArray = new byte[4];
      int count = 24;
      for (int j = 0; j < 4; j++){
          int b = 0xFF & (i >> count);
          byte a = (byte) b;
          count = count - 8;
          byteArray[j] = a;
      }

      return byteArray;
  }
  
  /**
   * Fetch a sequence of bytes from memory.
   * @param address address of the first byte to fetch.
   * @param length  number of bytes to fetch.
   * @throws InvalidAddressException  if any address in the range address to address+length-1 is invalid.
   * @return an array of byte where [0] is memory value at address, [1] is memory value at address+1 etc.
   */
  @Override public byte[] get (int address, int length) throws InvalidAddressException {
      byte[] byteArr = new byte[length];
      for(int i = 0; i < length; i++){
          if(address + i >= mem.length || address + i < 0){
              throw new InvalidAddressException();
          }else{
              byteArr[i] = mem[address+ i];
          }
          }
      return byteArr;

  }
  
  /**
   * Store a sequence of bytes into memory.
   * @param  address                  address of the first byte in memory to recieve the specified value.
   * @param  value                    an array of byte values to store in memory at the specified address.
   * @throws InvalidAddressException  if any address in the range address to address+value.length-1 is invalid.
   */
  @Override public void set (int address, byte[] value) throws InvalidAddressException {
    int length = value.length;
    for (int i = 0; i < length; i++){
        if(address + i < 0 || address + i >= mem.length){
            throw new InvalidAddressException();
        }else{
            mem[address+ i] = value[i];
        }
    }
  }
  
  /**
   * Determine the size of memory.
   * @return the number of bytes allocated to this memory.
   */
  @Override public int length () {
    return mem.length;
  }
}
