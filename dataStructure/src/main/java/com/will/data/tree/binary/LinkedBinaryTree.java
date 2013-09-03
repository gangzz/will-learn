package com.will.data.tree.binary;

import java.util.Arrays;
import java.util.List;

import com.will.data.tree.IBinaryTree;
import com.will.data.tree.Node;

public class LinkedBinaryTree<T> implements IBinaryTree<T>
{

	private BinaryNode<T> root;
	private int id = 0;
	
	public LinkedBinaryTree(T rootValue)
	{
		super();
		this.root = new BinaryNode<T>(Integer.toString(id), rootValue);
	}
	
	public LinkedBinaryTree(BinaryNode<T> root)
	{
		super();
		this.root = root;
	}

	public Node<T> getRoot()
	{
		return root;
	}
	
	@SuppressWarnings("unchecked")
	public Node<T> getParent(final Node<T> node)
	{
		final BinaryNode<T>[] parent = new BinaryNode[1];
		TraversalInvoker invoker = new TraversalInvoker()
		{

			@Override
			public boolean invoke(@SuppressWarnings("rawtypes") BinaryNode aNode)
			{
				if(node.equals(aNode.getLeftChild()) || node.equals(aNode.getLeftChild().getRightSibling()))
				{
					parent[0] = aNode;
					return false;
				}
				return true;
			}
			
		};
		preorderTraverse((BinaryNode<T>)root, invoker);
		return parent[0];
	}
	
	public Node<T> getLeftChild(Node<T> node)
	{
		return ((BinaryNode<T>)node).getLeftChild();
	}

	public Node<T> getRightSibling(Node<T> node)
	{
		return ((BinaryNode<T>)node).getRightSibling();
	}

	public void deleteNode(Node<T> node)
	{
		destorySubTree((BinaryNode<T>)node);
		BinaryNode<T> parent = (BinaryNode<T>)getParent(node);
		BinaryNode<T> leftChild = parent.getLeftChild();
		if(leftChild.equals(node))
		{
			parent.setLeftChild(null);
		}
		else
		{
			leftChild.setRightSibling(null);
		}
		
	}
	
	public void destory()
	{
		destorySubTree(root);
		root = null;
	}

	public Node<T> addLeftNode(Node<T> parent, T value)
	{
		BinaryNode<T> parentNode = (BinaryNode<T>)parent;
		BinaryNode<T> child = new BinaryNode<T>(Integer.toString(++id), value);
		BinaryNode<T> old = parentNode.getLeftChild();
		if(old != null)
		{
			child.setRightSibling(old.getRightSibling());
			child.setLeftChild(old.getLeftChild());
		}
		parentNode.setLeftChild(child);
		
		return child;
	}

	public Node<T> addRightNode(Node<T> parent, T value)
	{
		BinaryNode<T> parentNode = (BinaryNode<T>)parent;
		BinaryNode<T> child = new BinaryNode<T>(Integer.toString(++id), value);
		BinaryNode<T> left = parentNode.getLeftChild();
		if(left.getRightSibling() != null)
		{
			child.setLeftChild(left.getRightSibling().getLeftChild());
		}
		left.setRightSibling(child);
		
		return child;
		
	}
	
	public Node<T> insertChild(Node<T> parent, T value)
	{
		return addLeftNode(parent, value);
	}

	public Node<T> appendChild(Node<T> parent, T value)
	{
		return addRightNode(parent, value);
	}

	public List<Node<T>> getChildren(Node<T> node)
	{
		BinaryNode<T> parent = (BinaryNode<T>)node;
		Node<T> left = parent.getLeftChild(), right = parent.getRightSibling();
		Node<T>[] array = null;
		if(left != null)
		{
			if(right != null)
			{
				array = new Node[2];
				array[1] = right;
			}
			else
			{
				array = new Node[1];
			}
			array[0] = left;
		}
		return array == null ? null : Arrays.asList(array);
	}

	void destorySubTree(BinaryNode<T> root)
	{
		TraversalInvoker invoker = new TraversalInvoker()
		{
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public boolean invoke(BinaryNode aNode)
			{
				aNode.setLeftChild(null);
				aNode.setRightSibling(null);
				aNode.setValue(null);
				return true;
			}
		};
		traverse(root, new int[]{Traverse.RIGHT, Traverse.LEFT, Traverse.SELF}, invoker);
		
	}
	
	void inorderTraverse(BinaryNode<T> node, TraversalInvoker invoker)
	{
		traverse(node, new int[]{1, 0, 2}, invoker);
	}
	
	void preorderTraverse(BinaryNode<T> node, TraversalInvoker invoker)
	{
		traverse(node, new int[]{0, 1, 2}, invoker);
	}
	
	void postorderTraverse(BinaryNode<T> node, TraversalInvoker invoker)
	{
		traverse(node, new int[]{1, 2, 0}, invoker);
	}
	
	
	boolean traverse(BinaryNode<T> node, int[] orders, TraversalInvoker invoker)
	{
		if(node == null)
		{
			return true;
		}
		boolean isContinue = true;
		for(int i = 0; i < 3 && isContinue; i++)
		{
			switch(orders[i])
			{
			
			case Traverse.SELF:
				isContinue = invoker.invoke(node);
				break;
			case Traverse.LEFT:
				isContinue = traverse(node.getLeftChild(), orders, invoker);
				break;
				
			case Traverse.RIGHT:
				if(node.getLeftChild() != null)
				{
					isContinue = traverse(node.getLeftChild().getRightSibling(), orders, invoker);
				}
				break;
			}
		}
		return isContinue;
	}
	
	abstract class Traverse
	{
		private static final int SELF = 0;
		private static final int LEFT = 1;
		private static final int RIGHT = 2;
		public abstract int[] getOrders();
	}

}
