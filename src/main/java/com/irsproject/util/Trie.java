package com.irsproject.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trie 
{
	private class Node
	{
		private char character;
		private HashMap<Character, Node> children = new HashMap<Character, Node>();
		private boolean isEndOfWord;
		
		public Node(char character)
		{
			this.character = character;
		}
		
		public boolean hasChild(char character) 
		{
            return children.containsKey(character);
        }

        public void addChild(char character) 
        {
            children.put(character, new Node(character));
        }

        public Node getChild(char character) 
        {
            return children.get(character);
        }

		public Node[] getChildren()
		{
			return children.values().toArray(new Node[0]);
		}
        
        @Override
        public String toString() 
        {
        	return "Value" + character;
        }

	}
	
	Node root = new Node(' ');

	public void insert(String word)
	{
		insert(root,word,0);
	}

	private void insert(Node root, String word, int index)
	{
		if (word.length() == index)
		{
			root.isEndOfWord = true;
			return;
		}

		if (!root.hasChild(word.charAt(index)))
			root.addChild(word.charAt(index));

		insert(root.getChild(word.charAt(index)), word, index +1);
	}

	public boolean contains(String word)
	{
		return contains(root,word,0);
	}

	private boolean contains(Node root,String word,int index)
	{
		if (index == word.length())
		{
			return root.isEndOfWord ? true : false;
		}

		if (!root.hasChild(word.charAt(index)))
			return false;

		root = root.getChild(word.charAt(index));
		return contains(root,word,index + 1);
	}

	public List<String> findWord(String word)
	{
		List<String> words = new ArrayList<>();

		if (word == null)
			return words;

		if (word.length() == 0)
			return words;

		Node lastNode = findLastNode(word);

		findWords(lastNode,word,words);

		return words;
	}

	private void findWords(Node root,String prefix,List<String> words)
	{
		if (root == null)
			return;

		if (root.isEndOfWord)
			words.add(prefix);

		for (Node child: root.getChildren())
			findWords(child,prefix + child.character,words);
	}

	private Node findLastNode(String prefix)
	{
		Node currentNode = root;

		for(char character: prefix.toCharArray())
		{
			Node child = currentNode.getChild(character);
			if (child == null)
				return null;
			currentNode = child;
		}
		return currentNode;
	}
}
