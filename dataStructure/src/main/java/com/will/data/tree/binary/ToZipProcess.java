package com.will.data.tree.binary;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.will.data.tree.binary.TreeZip.HuffmanTreeNode;

/**
 * 负责一个压缩的流程实体
 * @author Administrator
 *
 */
public class ToZipProcess 
{
	
	private File tempFiles = null;
	private Reader buffReader = null;
	private OutputStream outs = null;
	char[] cbuffer = new char[4096];
	private static Comparator<HuffmanTreeNode> comparator = new Comparator<HuffmanTreeNode>()
	{

		public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2)
		{
			
			return o1.getWeight() - o2.getWeight();
		}

	};
	
	
	public ToZipProcess(Reader reader, OutputStream outs)
	{
		super();
		boolean isReaderBuffered = reader instanceof BufferedReader;
		boolean isOutBuffered = outs instanceof BufferedOutputStream;
		buffReader = isReaderBuffered ? reader : new BufferedReader(reader);
		this.outs = isOutBuffered ? outs : new BufferedOutputStream(outs);
	}

	public void toZip()
	{
		try
		{
			
			HuffmanTreeNode[] sortedArray = generateCharWeightList();
			Arrays.sort(sortedArray, comparator);
			LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(sortedArray, comparator);
			Map<Character, byte[]> codes = TreeZip.getHuffmanCode(tree);
			outputTree(tree);
			outputZipFile(codes);
			clearTempFiles();
		}
		finally
		{
			IOUtils.closeQuietly(outs);
			IOUtils.closeQuietly(buffReader);
		}
	}
	
	/**
	 * 输出一个树
	 * @param tree
	 */
	private void outputTree(LinkedBinaryTree<HuffmanTreeNode>  tree)
	{
		
	}
	
	/**
	 * 根据最终生成的字符码对照表输出
	 * @param codes
	 */
	private void outputZipFile(Map<Character, byte[]> codes)
	{
		try
		{
			int bufferLength = buffReader.read(cbuffer);
			byte[] code = null;
			
			while(bufferLength > 0)
			{
				
				for(int i =0; i < bufferLength; i++)
				{
					code = codes.get(i);
					
					//TODO 预读、快捷表
					outs.write(code);
				}
			}
		} catch (IOException e)
		{
			throw new RuntimeException("ZipFile write faild.", e);
		}
	}
	
	/**
	 * 生成未排序的所有字符的权重表
	 * @return
	 */
	private  HuffmanTreeNode[] generateCharWeightList( )
	{
		Writer tempWriter = null;
		Map<Character, Integer> counter = new HashMap<Character, Integer>();
		
		try
		{
			
			if(buffReader.markSupported())
			{
				buffReader.mark(Integer.MAX_VALUE);
			}
			else
			{
				tempFiles = File.createTempFile(null, TreeZip.TempFileSuffix);
				FileWriter fwriter = new FileWriter(tempFiles);
				tempWriter = new BufferedWriter(fwriter);
			}
			
			int readLength = buffReader.read(cbuffer);
			while(readLength > 0)
			{
				markReader(counter, cbuffer, readLength);
				if(!buffReader.markSupported())
				{
					tempWriter.write(cbuffer, 0, readLength);
				}
				readLength = buffReader.read(cbuffer);
			}
		}
		catch(IOException ie)
		{
			throw new RuntimeException("File Read Error!", ie);
		}
		finally
		{
			try
			{
				if(buffReader.markSupported())
				{
					buffReader.reset();
				}
				else
				{
					IOUtils.closeQuietly(buffReader);
					buffReader = new BufferedReader(new FileReader(tempFiles));
				}
			}
			catch(IOException e)
			{
				throw new RuntimeException("File Reader reset Error!", e);
			}
			IOUtils.closeQuietly(tempWriter);
		}
		
		Iterator<Entry<Character, Integer>> iter = counter.entrySet().iterator();
		HuffmanTreeNode[] nodeArray = new HuffmanTreeNode[counter.size()];
		Entry<Character, Integer> entry = null;
		int index = 0;
		while(iter.hasNext())
		{
			entry = iter.next();
			nodeArray[index] = new HuffmanTreeNode(Character.toString(entry.getKey()), entry.getValue());
			index++;
		}
		
		return nodeArray;
	}
	
	private static void markReader(Map<Character, Integer> counter, char[] cbuffer, int length)
	{
		for(int i = 0; i < length; i++)
		{
			Integer value = counter.get(cbuffer[i]);
			counter.put(cbuffer[i], (value == null) ? 1 : ++value);
		}
	}
	
	private void clearTempFiles()
	{
		if(tempFiles != null)
		{
			try
			{
				tempFiles.delete();
			}
			catch(Exception e)
			{
				System.out.println("File delete faild, file is " + tempFiles.getAbsolutePath());
			}
		}
		cbuffer = null;
	}
	
	public static void main(String[] args)
	{
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		Writer writer = new OutputStreamWriter(os);
//		try
//		{
//			writer.write(new char[]{'B','国'});
//			writer.flush();
//			System.out.println(os.size());
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		boolean one = true;
		boolean zero = false;
		boolean[] values = new boolean[]{one, zero, one};
		System.out.println(toByte(values));
	}
	
	private static byte toByte(boolean[] values)
	{
		boolean b = false;
		byte value = 0, constant = 1;
		for(int i = 0; i < values.length ; i++)
		{
			b = values[i];
			if(b)
			{
				value = (byte)(value | (constant << (values.length - i - 1)));
			}
		}
		return value;
	}
	
}
