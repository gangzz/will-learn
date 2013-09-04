package com.will.data.tree.binary;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.will.data.tree.Node;
import com.will.data.tree.binary.TreeZip.HuffmanTreeNode;

public class TreeZipTest
{

	Comparator<HuffmanTreeNode> comparator =  new Comparator<HuffmanTreeNode>()
	{

		public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2)
		{
			return o1.getWeight() - o2.getWeight();
		}

	};
	
	@Test
	public void testGetCode()
	{
		HuffmanTreeNode[] sortedArray = getTestData();
		LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(sortedArray, comparator);
		Map<Character, byte[]> codes = TreeZip.getHuffmanCode(tree);
		Iterator<Entry<Character, byte[]>> iter = codes.entrySet().iterator();
		Entry<Character, byte[]> entry = null;
		while(iter.hasNext())
		{
			entry = iter.next();
			System.out.println(entry.getKey() + Arrays.toString(entry.getValue()));
		}
	}
		
	@Test
	public void testBuildHuffman()
	{
		HuffmanTreeNode[] sortedArray = getTestData();
		LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(sortedArray, comparator);
		List<Node<HuffmanTreeNode>> nodes = new LinkedList<Node<HuffmanTreeNode>>();
		nodes.add(tree.getRoot());
		
		Node<HuffmanTreeNode> currentNode = null;
		while(!nodes.isEmpty())
		{
			int size = nodes.size();
			StringBuffer sb = new StringBuffer("[");
			for(int i = 0; i < size; i++)
			{
				currentNode = nodes.remove(0);
				if(i > 0)
				{
					sb.append(",");
				}
				sb.append(currentNode.getValue().getValue()).append("(")
					.append(currentNode.getValue().getWeight()).append(")");
				
				currentNode = tree.getLeftChild(currentNode);
				if(currentNode != null)
				{
					sb.append("*");
					nodes.add(currentNode);
					currentNode = tree.getRightSibling(currentNode);
					if(currentNode != null)
					{
						nodes.add(currentNode);
					}
				}
			}
			sb.append("]");
			System.out.println(sb.toString());
		}
	}
	
	private HuffmanTreeNode[] getTestData()
	{
		HuffmanTreeNode nodeA = null;
		int[] values = new int[]{5, 15, 40, 30, 10};
		HuffmanTreeNode[] sortedArray = new HuffmanTreeNode[values.length];
		for(int i = 0; i < values.length; i++)
		{
			nodeA = new HuffmanTreeNode(new String("" + (char)(i + 'A')), values[i]);
			sortedArray[i] = nodeA;
		}
		Arrays.sort(sortedArray, comparator);
		return sortedArray;
	}
}
