package com.will.data.tree.binary;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.will.data.tree.binary.TreeZip.HuffmanTreeNode;
import com.will.data.tree.binary.model.CodeEntry;

/**
 * 负责一个压缩的流程实体
 * @author Administrator
 *
 */
public class ToZipProcess 
{
	
	private File tempFiles = null;
	private Reader buffReader = null;
	private OutputStream outs = null;
	char[] cbuffer = new char[4096];
	private static final byte ConstantHead = 15;
	
	private static Comparator<HuffmanTreeNode> comparator = new Comparator<HuffmanTreeNode>()
	{

		public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2)
		{
			
			return o1.getWeight() - o2.getWeight();
		}

	};
	
	
	public ToZipProcess(Reader reader, OutputStream outs)
	{
		super();
		boolean isReaderBuffered = reader instanceof BufferedReader;
		boolean isOutBuffered = outs instanceof BufferedOutputStream;
		buffReader = isReaderBuffered ? reader : new BufferedReader(reader);
		this.outs = isOutBuffered ? outs : new BufferedOutputStream(outs);
	}

	public void toZip()
	{
		try
		{
			
			HuffmanTreeNode[] sortedArray = generateCharWeightList();
			Arrays.sort(sortedArray, comparator);
			LinkedBinaryTree<HuffmanTreeNode> tree = TreeZip.buildHuffmanTree(sortedArray, comparator);
			Map<Character, CodeEntry> codes = TreeZip.getHuffmanCode(tree);
			outputTree(tree);
			outputZipFile(codes);
			clearTempFiles();
		}
		finally
		{
			IOUtils.closeQuietly(outs);
			IOUtils.closeQuietly(buffReader);
		}
	}
	
	/**
	 * 输出一个树
	 * @param tree
	 */
	private void outputTree(LinkedBinaryTree<HuffmanTreeNode>  tree)
	{
		try
		{
			outs.write(ConstantHead);
			int length = tree.size();
			outs.write(TreeZip.intToByte(length));
			OutputTreeInvoker invoker = new OutputTreeInvoker();
			tree.postorderTraverse((BinaryNode)tree.getRoot(), invoker);
			List<int[]> datas = invoker.getData();
			for(int[] data : datas)
			{
				for(int num : data)
				{
					outs.write(TreeZip.intToByte(num));
				}
			}
		} catch (IOException e)
		{
			throw new RuntimeException("Output Tree faild.", e);
		}
	}
	
	/**
	 * 根据最终生成的字符码对照表输出
	 * @param codes
	 */
	private void outputZipFile(Map<Character, CodeEntry> codes)
	{
		try
		{
			int bufferLength = buffReader.read(cbuffer);
			CodeEntry code = null;
			
			while(bufferLength > 0)
			{
				
				for(int i =0; i < bufferLength; i++)
				{
					code = codes.get(cbuffer[i]);
					
					//TODO 预读、快捷表
					mergeAndOutput(code);
				}
				bufferLength = buffReader.read(cbuffer);
			}
			if(leftLength > 0)
			{
				outs.write(TreeZip.bitToByte(mergingByte));
			}
		} catch (IOException e)
		{
			throw new RuntimeException("ZipFile write faild.", e);
		}
	}
	
	/**
	 * 融合并输出
	 * @param code
	 */
	private int leftLength = 8;
	private boolean[] mergingByte = new boolean[8];
	private void mergeAndOutput(CodeEntry code) throws IOException
	{
		int codeLeftLength = code.getLength();
		int moveLength = 0;
		for(int i = 0; i < code.getLength(); i += moveLength)
		{
			moveLength = (leftLength > codeLeftLength) ? codeLeftLength : leftLength;
			System.arraycopy(code.getCode(), i, mergingByte, (8 - leftLength), moveLength);
			codeLeftLength -= moveLength; 
			leftLength -= moveLength;
			if(leftLength == 0)
			{
				outs.write(TreeZip.bitToByte(mergingByte));
				mergingByte = new boolean[8];
				leftLength = 8;
			}
		}
	}
	
	/**
	 * 生成未排序的所有字符的权重表
	 * @return
	 */
	private  HuffmanTreeNode[] generateCharWeightList( )
	{
		Writer tempWriter = null;
		Map<Character, Integer> counter = new HashMap<Character, Integer>();
		
		try
		{
			
			tempFiles = File.createTempFile("wtemp", TreeZip.TempFileSuffix);
			FileWriter fwriter = new FileWriter(tempFiles);
			tempWriter = new BufferedWriter(fwriter);
			
			int readLength = buffReader.read(cbuffer);
			while(readLength > 0)
			{
				markReader(counter, cbuffer, readLength);
				tempWriter.write(cbuffer, 0, readLength);
				readLength = buffReader.read(cbuffer);
			}
		}
		catch(IOException ie)
		{
			throw new RuntimeException("File Read Error!", ie);
		}
		finally
		{
			try
			{
				
				IOUtils.closeQuietly(buffReader);
				buffReader = new BufferedReader(new FileReader(tempFiles));
				
			}
			catch(IOException e)
			{
				throw new RuntimeException("File Reader reset Error!", e);
			}
			IOUtils.closeQuietly(tempWriter);
		}
		
		Iterator<Entry<Character, Integer>> iter = counter.entrySet().iterator();
		HuffmanTreeNode[] nodeArray = new HuffmanTreeNode[counter.size()];
		Entry<Character, Integer> entry = null;
		int index = 0;
		while(iter.hasNext())
		{
			entry = iter.next();
			nodeArray[index] = new HuffmanTreeNode(Character.toString(entry.getKey()), entry.getValue());
			index++;
		}
		
		return nodeArray;
	}
	
	private static void markReader(Map<Character, Integer> counter, char[] cbuffer, int length)
	{
		for(int i = 0; i < length; i++)
		{
			Integer value = counter.get(cbuffer[i]);
			counter.put(cbuffer[i], (value == null) ? 1 : ++value);
		}
	}
	
	private void clearTempFiles()
	{
		if(tempFiles != null)
		{
			try
			{
				tempFiles.delete();
			}
			catch(Exception e)
			{
				System.out.println("File delete faild, file is " + tempFiles.getAbsolutePath());
			}
		}
		cbuffer = null;
	}
	
	public static void main(String[] args)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(os);
		try
		{
//			writer.write(Integer.MAX_VALUE);
			os.write(255);
			writer.flush();
			byte[] array = os.toByteArray();
			System.out.println(os.size());
			System.out.println(Arrays.toString(array));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private class OutputTreeInvoker extends TraversalInvoker<HuffmanTreeNode>
	{

		Map<String, Integer> positions = new HashMap<String, Integer>();
		List<int[]> nodes = new ArrayList<int[]>();
		int position = 0;
		
		@Override
		public boolean invoke(BinaryNode<HuffmanTreeNode> node)
		{
			int[] data = new int[3];
			HuffmanTreeNode nodeValue = node.getValue();
			String value = nodeValue.getValue();
			int leftPosition = -1, rightPosition = -1;
			BinaryNode<HuffmanTreeNode> leftNode = node.getLeftChild(), rightNode = null;
			if(leftNode != null)
			{
				rightNode = leftNode.getRightSibling();
				leftPosition = positions.get(leftNode.getValue().getValue());
				if(rightNode != null)
				{
					rightPosition = positions.get(rightNode.getValue().getValue());
				}
			}
			data[0] = value.length() > 1 ? '#' : value.charAt(0);
			data[1] = leftPosition;
			data[2] = rightPosition;
			nodes.add(data);
			positions.put(value, position);
			position ++;
				
			return true;
		}
		
		public List<int[]> getData()
		{
			return nodes;
		}
		
	}
	
}
