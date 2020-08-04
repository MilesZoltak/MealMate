package com.example.mealmate

import android.util.Log

private const val TAG = "Trie_AC"
class Trie_AC {
    data class Node(var end: Boolean = false, var word: String = "", val childNodes: MutableMap<Char, Node> = mutableMapOf(), var fLink: Node? = null, var oLink: Node? = null)

    private val root = Node()

    fun insert(word: String) {
        val lWord = word.decapitalize()
        var currentNode = root
        var depth = 0
        for (char in lWord) {
            if (currentNode.childNodes[char] == null) {
                currentNode.childNodes[char] = Node()
            }
            currentNode = currentNode.childNodes[char]!!
            depth++
            currentNode.word = lWord.subSequence(0, depth).toString()

        }
        currentNode.word = lWord
        currentNode.end = true
    }

    //you pass in any given node and it will return the parent of that node
    fun getParent(curr: Node) : Node? {
        val word = curr.word
        if (curr == root) {
            return null
        }
        var parent = root
        for (c in word.subSequence(0, word.length - 1)) {
            parent = parent.childNodes[c]!!
        }
        return parent
    }

    //this turns a regular trie into an Aho-Corasick trie so we can do string pattern matching
    fun buildAhoCorasick() {
        Log.i(TAG, "starting to build Aho-Corasick Trie")
        val queue: MutableList<Node> = mutableListOf()
        var temp: Node? = null  //TODO: figure out if i can get by like this, or if i need a parent node... and then if so do i need temp?  can i even sustain a parent node?
        var curr = root

        for (child in curr.childNodes) {
            queue.add(child.value)
        }
        while (queue.size > 0) {
            curr = queue[0]
            temp = getParent(curr)
            //add curr's children to queue
            if (curr.childNodes.isNotEmpty()) {
                for (child in curr.childNodes) {
                    queue.add(child.value)
                }
            }
            //process next item in queue (at index 0) --> this means first adding failure links, then adding output links (if necessary)
            if (temp == root) {
                curr.fLink = root
                Log.i(TAG, "curr string = ${curr.word} and fLink points to ${curr.fLink!!.word}")
            }
            else {
                val letter = curr.word[curr.word.length - 1]  //converting single letter string to character for childnodes map
                temp = temp?.fLink //parent is kinda disposable, so we can just use it to follow it's own fLink pointer
                while (curr.fLink == null) {
                    //we need to make curr.fLink non-null

                    //if temp contains the key we are looking for
                    if (temp?.childNodes != null) {
                        if (temp?.childNodes?.containsKey(letter)!!) {
                            curr.fLink = temp?.childNodes[letter]
                            break
                        }
                    }
                    if (temp == root) {    //this says, ok temp didn't have the key AND we are at the root so just set curr.fLink to temp bc it is the root
                        curr.fLink = temp
                        break
                    }
                    temp = temp?.fLink
                }
                Log.i(TAG, "curr string = ${curr.word} and fLink points to ${curr.fLink!!.word}")
            }

            if (curr != root) {
                temp = curr.fLink!!
                if (temp.end) {
                    curr.oLink = temp
                } else {
                    curr.oLink = temp.oLink
                }
            }

            queue.removeAt(0)
        }
        Log.i(TAG, "finished building Aho-Corasick Trie")
    }

    fun searchAC(haystack: String): List<String> {
        Log.i(TAG, "starting to search AC trie")
        val outList: MutableList<String> = mutableListOf()
        var curr = root
        var temp = root
        for (straw in haystack) {
            while (!curr.childNodes.containsKey(straw)) {
                if (curr == root) {
                    break
                } else {
                    curr = curr.fLink!!
                }
            }
            if (curr.childNodes.containsKey(straw)) {
                curr = curr.childNodes[straw]!!
            }

            temp = curr

            if (curr.end) {
                outList.add(curr.word.toString())
            }

            while (curr.oLink != null) {
                outList.add(curr.word.toString())
                temp = temp.oLink!!
            }
        }

        Log.i(TAG, "finished searching AC trie")
        return outList
    }

    fun search(word: String): Boolean {
        var currentNode = root
        for (char in word) {
            if (currentNode.childNodes[char] == null) {
                return false
            }
            currentNode = currentNode.childNodes[char]!!
        }
        return currentNode.word != null
    }
}