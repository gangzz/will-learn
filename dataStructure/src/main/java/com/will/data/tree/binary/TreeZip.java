package com.will.data.tree.binary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.will.data.tree.Node;
import com.will.data.tree.binary.model.CodeEntry;

public class TreeZip
{

	private static String constantId = "huffman";
	
	private static String constantValue = "#%s";
	
	static final String TempFileSuffix = ".wmp";
	
	public static void toZip(String sourceFilePath, String targetFilePath)  
	{
		toZip(new File(sourceFilePath), new File(targetFilePath));
		
	}
	
	public static void toZip(File sourceFile, File targetFile)  
	{
		InputStream ins = null;
		OutputStream outs = null;
		try
		{
			
			if(targetFile.exists())
			{
				targetFile.mkdirs();
				targetFile.createNewFile();
			}
			ins = new FileInputStream(sourceFile);
			outs = new FileOutputStream(targetFile);
			toZip(ins, outs);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			IOUtils.closeQuietly(ins);
			IOUtils.closeQuietly(outs);
		}
		
		
	}
	
	public static void toZip(InputStream ins, OutputStream outs)
	{
		Reader reader = new InputStreamReader(ins);
		
		toZip(reader, outs);
		
	}
	
	public static void toZip(Reader reader, OutputStream outs)
	{
		
		ToZipProcess toZipProcess = new ToZipProcess(reader, outs);
		toZipProcess.toZip();
	}
	
	
	/**
	 * left is zero and right child is one
	 * @param tree
	 * @return
	 */
	public static Map<Character, CodeEntry> getHuffmanCode(LinkedBinaryTree<HuffmanTreeNode> tree)
	{
		Map<String, boolean[]> codesToReturn = new HashMap<String, boolean[]>();
		List<Node<HuffmanTreeNode>> nodes = new LinkedList<Node<HuffmanTreeNode>>();
		nodes.add(tree.getRoot());
		codesToReturn.put(tree.getRoot().getValue().getValue(), new boolean[0]);
		Node<HuffmanTreeNode> node = null, leftChild, rightChild;
		boolean[] parentCode = null;
		while(!nodes.isEmpty())
		{
			int size = nodes.size();
			for(int i = 0; i < size; i++)
			{
				node = nodes.remove(0);
				parentCode = codesToReturn.get(node.getValue().getValue());
				boolean[] subCode = new boolean[parentCode.length + 1];
				leftChild = tree.getLeftChild(node);
				if(leftChild != null)
				{
					rightChild = tree.getRightSibling(leftChild);
					nodes.add(leftChild);
					System.arraycopy(parentCode, 0, subCode, 0, parentCode.length);
					subCode[parentCode.length] = false;
					codesToReturn.put(leftChild.getValue().getValue(), subCode.clone());
					if(rightChild != null)
					{
						nodes.add(rightChild);
						subCode[parentCode.length] = true;
						codesToReturn.put(rightChild.getValue().getValue(), subCode.clone());
					}
				}
			}
		}
		
		return trimCodeList(codesToReturn);
	}
	
	private static Map<Character, CodeEntry> trimCodeList(Map<String, boolean[]> map)
	{
		String[] keyArray = map.keySet().toArray(new String[map.size()]);
		for(String key : keyArray)
		{
			if(key.length() > 1)
			{
				map.remove(key);
			}
		}
		Map<Character, CodeEntry> result = new HashMap<Character, CodeEntry>(map.size());
		Iterator<Entry<String, boolean[]>> iterator = map.entrySet().iterator();
		Entry<String, boolean[]> entry = null;
		while(iterator.hasNext())
		{
			entry = iterator.next();
			result.put(entry.getKey().charAt(0), convertToEntry(entry.getValue()));
		}
		map.clear();
		return result;
	}
	
	/*
	 * 转换bit数组为byte值，需要考虑超过8位的情况
	 */
	private static CodeEntry convertToEntry(boolean[] value)
	{
//		int maxLength = (value.length + 7)/8, srcPos = 1;
//		byte byteLength = 8;
//		byte[] byteCode = new byte[maxLength];
//		boolean[] nextByte = new boolean[byteLength];
//		
//		for(int i = 1; i <= maxLength; i ++)
//		{
//			System.arraycopy(value, srcPos - 1, nextByte, 0, byteLength);
//			srcPos = srcPos << 3;
//			byteCode[i] = bitToByte(nextByte);
//			
//		}
		return new CodeEntry(value, value.length);
	}
	
	/**
	 * 根据排序好的数组构建哈夫曼树
	 * @param sortedArray
	 */
	public static LinkedBinaryTree<HuffmanTreeNode> buildHuffmanTree(HuffmanTreeNode[] sortedArray
			, final Comparator<HuffmanTreeNode> comparator)
	{
		BinaryNode<HuffmanTreeNode> newRoot = null;
		List<BinaryNode<HuffmanTreeNode>> nodeArray = buildNodes(sortedArray);
		Comparator<BinaryNode<HuffmanTreeNode>> nodeComparator = new Comparator<BinaryNode<HuffmanTreeNode>>()
				{

					public int compare(BinaryNode<HuffmanTreeNode> o1,
							BinaryNode<HuffmanTreeNode> o2) {
						return comparator.compare(o1.getValue(), o2.getValue());
					}
			
				};
		int count = 1;
		while(nodeArray.size() >= 2)
		{
				newRoot = buildNewNode(nodeArray.remove(0), nodeArray.remove(0), ++count);
				nodeArray.add(newRoot);
				Collections.sort(nodeArray, nodeComparator);
		}
		
		return new LinkedBinaryTree<HuffmanTreeNode>(newRoot);
	}
	
	private static BinaryNode<HuffmanTreeNode> buildNewNode(BinaryNode<HuffmanTreeNode> nodeA, BinaryNode<HuffmanTreeNode> nodeB, int count)
	{
		int valueA = nodeA.getValue().getWeight();
		int valueB = nodeB.getValue().getWeight();
		BinaryNode<HuffmanTreeNode> newRoot = 
				new BinaryNode<HuffmanTreeNode>(constantId, new HuffmanTreeNode(String.format(constantValue, count), valueA + valueB));
		if(valueA >= valueB)
		{
			newRoot.setLeftChild(nodeB);
			nodeB.setRightSibling(nodeA);
		}
		else
		{
			newRoot.setLeftChild(nodeA);
			nodeA.setRightSibling(nodeB);
		}
		
		return newRoot;
	}
	
	private static List<BinaryNode<HuffmanTreeNode>> buildNodes(HuffmanTreeNode[] sortedArray)
	{
		ArrayList<BinaryNode<HuffmanTreeNode>> array 
				= new ArrayList<BinaryNode<HuffmanTreeNode>>(sortedArray.length);
		for(HuffmanTreeNode node : sortedArray)
		{
			array.add(new BinaryNode<HuffmanTreeNode>(constantId, node));
		}
		
		return array;
	}
	
	
	public static class HuffmanTreeNode
	{
		String value;
		int weight;
		
		public HuffmanTreeNode(String value, int weight)
		{
			super();
			this.value = value;
			this.weight = weight;
		}

		public String getValue()
		{
			return value;
		}
		
		public int getWeight()
		{
			return weight;
		}

		@Override
		public String toString()
		{
			return "[value=" + value + ", weight=" + weight
					+ "]";
		}
	}
	
	public static byte bitToByte(boolean[] values)
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
	
	public static boolean[] byteToBit(byte aByte)
	{
		boolean[] result = new boolean[8];
		byte constantMode = 1;
		for(int i = 7; i >= 0; i--)
		{
			result[i] = (aByte & constantMode) == 1;
			aByte = (byte)(aByte >> 1);
		}
		return result;
	}
	
	public static byte[] intToByte(int num)
	{
		byte[] array = new byte[4];
		for(int i = 3; i >= 0; i--)
		{
			array[i] = (byte)num;
			num = num >> 8;
		}
		return array;
	}
	
	public static int byteToInt(byte[] bytes)
	{
		int a1 = (bytes[0] << 24) , a2 = (bytes[1] << 16), a3 = (bytes[2] << 8) , a4 = bytes[3];
		int i = a1 | a2 | a3 | a4;
		return i;
	}
	
	public static void main(String[] args)
	{
		int[] arr = new int[3];
		arr[0] = 'A';
		arr[1] = 'B';
		arr[2] = 'C';
		for(int a : arr)
		{
			System.out.println((char)a);
		}
	}
}
