package com.will.data.tree.binary;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.will.data.tree.Node;

public class TestLinkedBinaryTree
{

	private LinkedBinaryTree<String> tree;

	@Before
	public void buildTree()
	{
		tree = new LinkedBinaryTree<String>("A");
		int i = 0;
		BinaryNode<String> root = (BinaryNode<String>)tree.getRoot();
		Queue<BinaryNode<String>> queue = new ArrayBlockingQueue<BinaryNode<String>>(50);
		queue.add(root);
		while(i < 10)
		{
			BinaryNode<String> node = queue.poll();
			BinaryNode<String> left = (BinaryNode<String>)tree.addLeftNode(node, getCharacter(++i));
			BinaryNode<String> right = (BinaryNode<String>)tree.addRightNode(node, getCharacter(++i));
			queue.add(left);
			queue.add(right);
		}
	}
	
	@Test
	public void testDelete()
	{
		Node<String> nodeB = tree.getLeftChild(tree.getRoot());
		tree.deleteNode(nodeB);
		assertNull(tree.getLeftChild(tree.getRoot()));
	}
	
	@Test
	public void testDestory()
	{
		tree.destory();
	}
	
	@Test
	public void testAddNode()
	{
		Node<String> root = tree.getRoot();
		Node<String> nodeB = tree.getLeftChild(root);
		Node<String> nodeC = tree.getRightSibling(nodeB);
		tree.addRightNode(nodeC, "GG");
		Node<String> nodeF = tree.getLeftChild(nodeC);
		String expectValue = tree.getRightSibling(nodeF).getValue();
		assertEquals("GG", expectValue);
		tree.addLeftNode(nodeC, "FF");
		expectValue = tree.getLeftChild(nodeC).getValue();
		assertEquals(expectValue, "FF");
		nodeB = tree.addLeftNode(root, "BB");
		assertEquals("BB", nodeB.getValue());
		assertEquals("D", tree.getLeftChild(nodeB).getValue());
	}
	
	@Test
	public void testTraverse()
	{
		int i = 0;
		BinaryNode<String> root = new BinaryNode<String>(Integer.toString(i), "A");
		Queue<BinaryNode<String>> queue = new ArrayBlockingQueue<BinaryNode<String>>(50);
		queue.add(root);
		while(i < 10)
		{
			BinaryNode<String> node = queue.poll();
			BinaryNode<String> left = new BinaryNode<String>(Integer.toString(++i), getCharacter(i));
			BinaryNode<String> right = new BinaryNode<String>(Integer.toString(++i), getCharacter(i));
			node.setLeftChild(left);
			left.setRightSibling(right);
			queue.add(left);
			queue.add(right);
		}
		tree = new LinkedBinaryTree<String>("TEst");
		
		TraversalInvoker invoker = new TraversalInvoker()
		{

			@Override
			public boolean invoke(BinaryNode<?> node)
			{
				node.setRightSibling(null);
				node.setLeftChild(null);
				System.out.println(node.getValue());
				return true;
			}
			
		};
		tree.traverse(root, new int[]{2, 1, 0}, invoker);
	}
	
	private static String getCharacter(int i)
	{
		Stack<Integer> stack = new Stack<Integer>();
		int term = 26;
		stack.push(i % term);
		while((i = i / term) > 0)
		{
			stack.push(i % term - 1);
		}
		char[] chars = new char[stack.size()];
		for(int j = 0; j < chars.length; j++)
		{
			int num = stack.pop() + 65;
			char c = (char)num;
			chars[j] = c;
		}
		return new String(chars);
	}
	
}
