package com.will.data.tree.binary;

public class TreeZip
{

	private static String constantId = "huffman";
	
	private static String constantValue = "#";
	
	/**
	 * 根据排序好的数组构建哈夫曼树
	 * @param sortedArray
	 */
	public static LinkedBinaryTree<HuffmanTreeNode> buildHuffmanTree(HuffmanTreeNode[] sortedArray)
	{
		BinaryNode<HuffmanTreeNode> root = null;
		
		for(HuffmanTreeNode node : sortedArray)
		{
			if(root == null)
			{
				 root = new BinaryNode<HuffmanTreeNode>(constantId, node);
			}
			else
			{
				root = buildNewNode(root, node);
			}
		}
		
		return new LinkedBinaryTree<HuffmanTreeNode>(root);
	}
	
	private static BinaryNode<HuffmanTreeNode> buildNewNode(BinaryNode<HuffmanTreeNode> currentRoot, HuffmanTreeNode newItem)
	{
		int currentValue = currentRoot.getValue().getWeight();
		int newValue = newItem.getWeight();
		BinaryNode<HuffmanTreeNode> newNode = new BinaryNode<HuffmanTreeNode>(constantId, newItem);
		BinaryNode<HuffmanTreeNode> newRoot = 
				new BinaryNode<HuffmanTreeNode>(constantId, new HuffmanTreeNode(constantValue, currentValue + newValue));
		if(currentValue >= newValue)
		{
			newRoot.setLeftChild(newNode);
			newNode.setRightSibling(currentRoot);
		}
		else
		{
			newRoot.setLeftChild(currentRoot);
			currentRoot.setRightSibling(newRoot);
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
