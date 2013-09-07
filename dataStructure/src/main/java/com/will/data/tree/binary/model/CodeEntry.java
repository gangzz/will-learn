package com.will.data.tree.binary.model;

import java.util.Arrays;

/**
 * Huffman Code Entry describes the bit code(use byte) and code array length.
 * @author Administrator
 *
 */
public class CodeEntry
{

	private boolean[] code;
	
	private int length;

	public CodeEntry(boolean[] code, int length)
	{
		super();
		this.code = code;
		this.length = length;
	}

	public boolean[] getCode()
	{
		return code;
	}

	public int getLength()
	{
		return length;
	}
	
	/**
	 * 获取头部的若干个字节
	 * @param length
	 * @return
	 */
//	public byte getTopbits(int length)
//	{
////		byte expectByte = 0, modeByte = (byte)(1 << length);
////		for(int i = 0; i < code.length; i++)
////		{
////			TreeZip.byteToBit(aByte);
////		}
////		return 0;
//	}
	
	@Override
	public String toString()
	{
		return "CodeEntry [code=" + Arrays.toString(code) + ", length="
				+ length + "]";
	}
	
	
}
