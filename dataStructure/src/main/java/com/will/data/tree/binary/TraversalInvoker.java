package com.will.data.tree.binary;

public abstract class TraversalInvoker<T>
{
	public abstract boolean invoke(BinaryNode<T> node);

}