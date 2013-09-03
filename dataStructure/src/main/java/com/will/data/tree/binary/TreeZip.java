package com.will.data.tree.binary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeZip
{

	private static String constantId = "huffman";
	
	private static String constantValue = "#";
	
	/**
	 * 根据排序好的数组构建哈夫曼树
	 * @param sortedArray
	 */
	public static LinkedBinaryTree<HuffmanTreeNode> buildHuffmanTree(HuffmanTreeNode[] sortedArray
			,final Comparator<HuffmanTreeNode> comparator)
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
		
		while(nodeArray.size() >= 2)
		{
				newRoot = buildNewNode(nodeArray.remove(0), nodeArray.remove(0));
				nodeArray.add(newRoot);
				Collections.sort(nodeArray, nodeComparator);
		}
		
		return new LinkedBinaryTree<HuffmanTreeNode>(newRoot);
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
	
	private static BinaryNode<HuffmanTreeNode> buildNewNode(BinaryNode<HuffmanTreeNode> nodeA, BinaryNode<HuffmanTreeNode> nodeB)
	{
		int valueA = nodeA.getValue().getWeight();
		int valueB = nodeB.getValue().getWeight();
		BinaryNode<HuffmanTreeNode> newRoot = 
				new BinaryNode<HuffmanTreeNode>(constantId, new HuffmanTreeNode(constantValue, valueA + valueB));
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
