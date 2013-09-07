package com.will.data.tree.binary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.will.data.tree.Node;
import com.will.data.tree.binary.TreeZip.HuffmanTreeNode;
import com.will.data.tree.binary.model.CodeEntry;

public class TreeZipTest
{

	Comparator<HuffmanTreeNode> comparator = new Comparator<HuffmanTreeNode>()
	{

		public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2)
		{
			return o1.getWeight() - o2.getWeight();
		}

	};
	
	@Test
	/*
	 * [1byte 魔数][4byte 长度N][N*3*4 byte 树数据][压缩数据]
	 */
	public void testToZip() throws FileNotFoundException
	{
		String src = "E:\\zipTest\\src.txt", target = "E:\\zipTest\\target.zip";
//		TreeZip.toZip(src, target);
		
		InputStream ins = new FileInputStream(target);
		byte[] head = new byte[5];
		try
		{
			ins.read(head);
			byte[] lengths = new byte[]{head[1], head[2], head[3], head[4]};
			byte magic = head[0];
			int length = TreeZip.byteToInt(lengths);
			System.out.println("Mag :" + magic);
			System.out.println("Length :" + length);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(ins);
		}
	}
	
	@Test
	public void testIntConvertion()
	{
		Random random = new Random(Integer.MAX_VALUE);
		for(int i = 0; i < 100; i++)
		{
			int number = 6281;
			//random.nextInt(10000);
			byte[] b = TreeZip.intToByte(number);
			Assert.assertEquals(number, TreeZip.byteToInt(b));
			System.out.println(number);
		}
	}
	
	public static void main(String[] args)
	{
		int a1 = 6144;
		byte b1 = -119;
		int a2 = (~(b1 - 1 ));
		int a3 = a1 | a2;
		System.out.println(a3);
	}

	@Test
	public void testGetCode()
	{
		HuffmanTreeNode[] sortedArray = getTestData();
		LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(
				sortedArray, comparator);
		Map<Character, CodeEntry> codes = TreeZip.getHuffmanCode(tree);
		Iterator<Entry<Character, CodeEntry>> iter = codes.entrySet().iterator();
		Entry<Character, CodeEntry> entry = null;
		while (iter.hasNext())
		{
			entry = iter.next();
			System.out.println(entry.getKey()
					+ entry.getValue().toString());
		}
	}

	@Test
	public void testBuildHuffman()
	{
		HuffmanTreeNode[] sortedArray = getTestData();
		LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(
				sortedArray, comparator);
		List<Node<HuffmanTreeNode>> nodes = new LinkedList<Node<HuffmanTreeNode>>();
		nodes.add(tree.getRoot());

		Node<HuffmanTreeNode> currentNode = null;
		while (!nodes.isEmpty())
		{
			int size = nodes.size();
			StringBuffer sb = new StringBuffer("[");
			for (int i = 0; i < size; i++)
			{
				currentNode = nodes.remove(0);
				if (i > 0)
				{
					sb.append(",");
				}
				sb.append(currentNode.getValue().getValue()).append("(")
						.append(currentNode.getValue().getWeight()).append(")");

				currentNode = tree.getLeftChild(currentNode);
				if (currentNode != null)
				{
					sb.append("*");
					nodes.add(currentNode);
					currentNode = tree.getRightSibling(currentNode);
					if (currentNode != null)
					{
						nodes.add(currentNode);
					}
				}
			}
			sb.append("]");
			System.out.println(sb.toString());
		}
	}

	// 测试byte和bit之间的转换
	@Test
	public void testBitAndByteConvertion()
	{
		for (byte i = 0; i < 127; i++)
		{
			boolean[] result = TreeZip.byteToBit(i);
			byte value = TreeZip.bitToByte(result);
			Assert.assertEquals(i, value);
		}
	}

	private HuffmanTreeNode[] getTestData()
	{
		HuffmanTreeNode nodeA = null;
		int[] values = new int[]
		{ 5, 15, 40, 30, 10 };
		HuffmanTreeNode[] sortedArray = new HuffmanTreeNode[values.length];
		for (int i = 0; i < values.length; i++)
		{
			nodeA = new HuffmanTreeNode(new String("" + (char) (i + 'A')),
					values[i]);
			sortedArray[i] = nodeA;
		}
		Arrays.sort(sortedArray, comparator);
		return sortedArray;
	}
}
