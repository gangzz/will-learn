package com.will.data.tree;

import java.util.List;

public interface ITree<T>
{
	public Node<T> getParent(Node<T> node);
	
	public Node<T> insertChild(Node<T> parent, T value);
	
	public Node<T> appendChild(Node<T> parent, T value);
	
	public Node<T> getRoot();
	
	public List<Node<T>> getChildren(Node<T> node);
	
	public void deleteNode(Node<T> node);
	
	public void destory();

}
