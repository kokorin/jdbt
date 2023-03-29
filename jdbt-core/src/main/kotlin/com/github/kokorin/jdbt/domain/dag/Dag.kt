package com.github.kokorin.jdbt.domain.dag

import com.github.kokorin.jdbt.exception.CyclicGraphException

class Dag(
    private val nodes: Set<String>,
    private val edges: Map<String, Set<String>>
) {
    init {
        traverseDepthFirst(CycleDetectingVisitor())
    }

    fun nodesPostOrder(): List<String> {
        val visitor = PostOrderCollectingVisitor()
        traverseDepthFirst(visitor)
        return visitor.nodes
    }

    private fun traverseDepthFirst(visitor: Visitor): Unit {
        fun traverse(path: List<String>, nextNodes: Collection<String>, visitedNodes: Set<String>): Set<String> =
            nextNodes.fold(visitedNodes) { accVisitedNodes, currentNode ->
                visitor.discover(path, currentNode)

                if (currentNode in accVisitedNodes) {
                    accVisitedNodes
                } else {
                    visitor.visitPreOrder(path, currentNode)
                    val result = traverse(
                        path + currentNode,
                        edges[currentNode] ?: listOf(),
                        accVisitedNodes + currentNode
                    )
                    visitor.visitPostOrder(path, currentNode)
                    println("result: $result")
                    result
                }
            }

        traverse(emptyList(), nodes, emptySet())
    }
}

private interface Visitor {
    fun discover(path: List<String>, node: String): Unit
    fun visitPreOrder(path: List<String>, node: String): Unit
    fun visitPostOrder(path: List<String>, node: String): Unit
}

private class CycleDetectingVisitor : Visitor {
    private val traversingNodes = mutableSetOf<String>()
    override fun discover(path: List<String>, node: String) {
        if (node in traversingNodes) {
            val cycle = path.dropWhile { it != node } + node
            throw CyclicGraphException(cycle)
        }
    }

    override fun visitPreOrder(path: List<String>, node: String) {
        traversingNodes += node
    }

    override fun visitPostOrder(path: List<String>, node: String) {
        traversingNodes -= node
    }
}

private class PostOrderCollectingVisitor : Visitor {
    val nodes: MutableList<String> = mutableListOf()
    override fun discover(path: List<String>, node: String) {}
    override fun visitPreOrder(path: List<String>, node: String) {}

    override fun visitPostOrder(path: List<String>, node: String) {
        nodes += node
    }
}

