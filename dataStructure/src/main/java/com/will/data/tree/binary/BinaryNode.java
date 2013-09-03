package com.will.data.tree.binary;

import com.will.data.tree.Node;

public class BinaryNode<T> extends Node<T>
{

	private BinaryNode<T> leftChild;
	
	private BinaryNode<T> rightSibling;
	
	public BinaryNode(String id, T value)
	{
		super(id, value);
	}

	public BinaryNode<T> getLeftChild()
	{
		return leftChild;
	}

	public BinaryNode<T> getRightSibling()
	{
		return rightSibling;
	}

	public void setLeftChild(BinaryNode<T> leftChild)
	{
		this.leftChild = leftChild;
	}

	public void setRightSibling(BinaryNode<T> rightSibling)
	{
		this.rightSibling = rightSibling;
	}
	
	
}
