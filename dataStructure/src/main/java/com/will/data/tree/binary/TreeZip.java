package com.will.data.tree.binary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
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

public class TreeZip
{

	private static String constantId = "huffman";
	
	private static String constantValue = "#%s";
	
	static final String TempFileSuffix = ".wmp";
	
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
	public static Map<Character, byte[]> getHuffmanCode(LinkedBinaryTree<HuffmanTreeNode> tree)
	{
		Map<String, byte[]> codes = new HashMap<String, byte[]>();
		List<Node<HuffmanTreeNode>> nodes = new LinkedList<Node<HuffmanTreeNode>>();
		nodes.add(tree.getRoot());
		codes.put(tree.getRoot().getValue().getValue(), new byte[0]);
		Node<HuffmanTreeNode> node = null, leftChild, rightChild;
		byte[] parentCode = null;
		while(!nodes.isEmpty())
		{
			int size = nodes.size();
			for(int i = 0; i < size; i++)
			{
				node = nodes.remove(0);
				parentCode = codes.get(node.getValue().getValue());
				byte[] subCode = new byte[parentCode.length + 1];
				leftChild = tree.getLeftChild(node);
				if(leftChild != null)
				{
					rightChild = tree.getRightSibling(leftChild);
					nodes.add(leftChild);
					System.arraycopy(parentCode, 0, subCode, 0, parentCode.length);
					subCode[parentCode.length] = 0;
					codes.put(leftChild.getValue().getValue(), subCode.clone());
					if(rightChild != null)
					{
						nodes.add(rightChild);
						subCode[parentCode.length] = 1;
						codes.put(rightChild.getValue().getValue(), subCode.clone());
					}
				}
			}
		}
		
		
		return trimCodeList(codes);
	}
	
	private static Map<Character, byte[]> trimCodeList(Map<String, byte[]> map)
	{
		String[] keyArray = map.keySet().toArray(new String[map.size()]);
		for(String key : keyArray)
		{
			if(key.length() > 1)
			{
				map.remove(key);
			}
		}
		Map<Character, byte[]> result = new HashMap<Character, byte[]>(map.size());
		Iterator<Entry<String, byte[]>> iterator = map.entrySet().iterator();
		Entry<String, byte[]> entry = null;
		while(iterator.hasNext())
		{
			entry = iterator.next();
			result.put(entry.getKey().charAt(0), entry.getValue());
		}
		map.clear();
		return result;
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
		int i = 0;
		for(HuffmanTreeNode node : sortedArray)
		{
			array.add(new BinaryNode<HuffmanTreeNode>(constantId, node));
			i++;
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
