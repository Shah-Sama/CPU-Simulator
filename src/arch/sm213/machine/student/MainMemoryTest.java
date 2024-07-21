package arch.sm213.machine.student;

import machine.AbstractMainMemory;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainMemoryTest {
    private MainMemory memory;

    @BeforeEach
    public void setUp() {
        memory = new MainMemory(32); // Initialize memory with 8 bytes for testing
    }

    @Test
    public void testConstructor(){
        // testing the constructor to male sure the length of the byte array is 8 as specified
        assertEquals(32,memory.length());
    }

    @Test
    public void testIsNotAligned(){
        // testing the case where the function is alligned should return false when the modulus value is not 0
        assertFalse(memory.isAccessAligned(2,3));
        assertFalse(memory.isAccessAligned(32,3));
        assertFalse(memory.isAccessAligned(1,3));
        assertFalse(memory.isAccessAligned(1,-4));
        assertFalse(memory.isAccessAligned(-3,-4));
    }

    @Test
    public void testIsAligned(){
        // testing the case where the function should return true when the modulus value is 0 when address % length
        assertTrue(memory.isAccessAligned(4,2));
        assertTrue(memory.isAccessAligned(0,4));
        assertTrue(memory.isAccessAligned(6,3));
    }

    @Test
    public void testBytesToIntegerZero(){
        // test on the base case of the function 0
        assertEquals(0,memory.bytesToInteger((byte) 0,(byte) 0,(byte) 0,(byte) 0));
    }

    @Test
    public void testBytesToIntegerStandard(){
        // testing the standard case of the function on normal integers
        assertEquals(1,memory.bytesToInteger((byte) 0,(byte) 0,(byte) 0,(byte) 1));
        assertEquals(0x01020304,memory.bytesToInteger((byte) 0x01,(byte) 0x02,(byte) 0x03,(byte) 0x04));
    }

    @Test
    public void testBytesToIntegerNegative(){
        // testing on negative values to see the functionality of the sign extension
        assertEquals(-1,memory.bytesToInteger((byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF));

        // testing on the edge case of the most negative value possible for the function to handle
        assertEquals(Integer.MIN_VALUE,memory.bytesToInteger((byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00));
    }

    @Test
    public void testBytesToIntegerExtraEdgeCases(){
        // testing on a positive maximum value that can be made
        assertEquals(Integer.MAX_VALUE,memory.bytesToInteger((byte) 0x7F,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF));

        // testing on the edge case of the number right after the most negative number
        assertEquals(0x80000001,memory.bytesToInteger((byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x01));
    }

    @Test
    // Basic Case on a positive integer
    public void testStandardPositiveIntegerToBytes() {
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x04}, memory.integerToBytes(16909060));
        assertArrayEquals(new byte[]{0x7F, (byte)0xFF, (byte)0xFF, (byte)0xFF}, memory.integerToBytes(Integer.MAX_VALUE));
    }

    // Test conversion of zero
    @Test
    public void testZeroIntegerToBytes() {
        byte[] memoryArray = new byte[]{0x00, 0x00, 0x00, 0x00};
        assertArrayEquals(memoryArray, memory.integerToBytes(0));
    }

    // test for the maximum positive value 7fffffff
    @Test
    public void testMaxIntegerToBytes() {
        byte[] memoryArray = new byte[]{0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertArrayEquals(memoryArray, memory.integerToBytes(Integer.MAX_VALUE));
    }

    // Test conversion for ffffffff
    @Test
    public void testNegativeIntegerToBytes() {
        byte[] memoryArray = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertArrayEquals(memoryArray, memory.integerToBytes(-1));
    }

    // test for the min negative value
    @Test
    public void testMaxNegativeIntegerToBytes() {
        byte[] memoryArray = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        assertArrayEquals(memoryArray, memory.integerToBytes(Integer.MIN_VALUE));
    }

    // testing a valid version of the get and set function
    @Test
    public void testGetAndSetNormal() throws AbstractMainMemory.InvalidAddressException {
        byte[] memoryArray = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        try {
            memory.set(0,memoryArray);
            byte get[] = memory.get(0, 4);
            for(int i = 0; i < 4; i++)
                assertEquals(memoryArray[i], get[i]);
        } catch (AbstractMainMemory.InvalidAddressException e) {
            fail("Didnt expect to get here");
        }


    }

    // testing an invalid get and set function negative address
    @Test
    public void testGetAndSetInvalidAddressException() throws AbstractMainMemory.InvalidAddressException {
        byte[] memoryArray = new byte[]{(byte) 0x80};
        try {
            memory.set(0,memoryArray);
            byte get[] = memory.get(-4, 4);
            fail("Didnt expect to get here");
        } catch (AbstractMainMemory.InvalidAddressException e) {
            // expected to get here
        }


    }

    // testing an invalid get and set function for when it is valid then goes invalid out of range
    @Test
    public void testGetAndSetInvalidAddressExceptionTwo() throws AbstractMainMemory.InvalidAddressException {
        byte[] memoryArray = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        try {
            memory.set(0, memoryArray);
            memory.get(0, 99); // This should throw an exception
            fail("Should not get here: InvalidAddressException expected but not thrown");
        } catch (AbstractMainMemory.InvalidAddressException e) {
            // Test passes, exception was thrown as expected
        }
    }

    @Test
    public void testSetPartialByteArray() throws AbstractMainMemory.InvalidAddressException {
        byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
        memory.set(0, data);
        memory.set(2, new byte[]{0x05});
        assertArrayEquals(new byte[]{0x01, 0x02, 0x05, 0x04}, memory.get(0, 4));
    }

    @Test
    public void testSetOverlappingMemoryRanges() throws AbstractMainMemory.InvalidAddressException {
        memory.set(0, new byte[]{0x01, 0x02});
        memory.set(1, new byte[]{0x03});
        assertArrayEquals(new byte[]{0x01, 0x03}, memory.get(0, 2));
    }

    @Test
    public void testGetOutOfRange() {
        byte[] memoryArray = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        try {
            memory.set(0, memoryArray);
            memory.get(0, 99);
            fail("Expected InvalidAddressException to be thrown");
        } catch (AbstractMainMemory.InvalidAddressException e) {
            // Test passes if exception is caught
        }
    }

    @Test
    public void testSetPartialByteArrayOutOfRange() {
        byte[] memoryArray = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
        try {
            memory.set(30, memoryArray);
            fail("Expected InvalidAddressException to be thrown");
        } catch (AbstractMainMemory.InvalidAddressException e) {
            // Test passes if exception is caught
        }
    }







}
