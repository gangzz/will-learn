package com.will.data.tree;

public interface IBinaryTree<T> extends ITree<T>
{

	public Node<T> addLeftNode(Node<T> parent, T value);
	
	public Node<T> addRightNode(Node<T> parent, T value);
	
	public Node<T> getLeftChild(Node<T> node);
	
	public Node<T> getRightSibling(Node<T> node);
}
