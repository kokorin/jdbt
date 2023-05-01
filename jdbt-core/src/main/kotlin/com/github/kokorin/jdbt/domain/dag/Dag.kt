package com.github.kokorin.jdbt.domain.dag

import com.github.kokorin.jdbt.exception.CyclicGraphException

// TODO decide if Dag should be data class or simple class
data class Dag<T>(
    val nodes: Set<T>,
    val edges: Map<T, Set<T>>
) {
    init {
        traverseDepthFirst(CycleDetectingVisitor())
    }

    fun nodesPostOrder(): List<T> {
        val visitor = PostOrderCollectingVisitor<T>()
        traverseDepthFirst(visitor)
        return visitor.nodes
    }

    fun inverse(): Dag<T> {
        val inverseEdges = edges
            .flatMap { (fromNode, toNodes) -> toNodes.map { it to fromNode } }
            .groupingBy { it.first }
            .fold(setOf<T>()) { acc, edge -> acc + edge.second }

        return Dag(nodes, inverseEdges)
    }

    private fun traverseDepthFirst(visitor: Visitor<T>): Unit {
        fun traverse(path: List<T>, nextNodes: Set<T>, visitedNodes: Set<T>): Set<T> =
            nextNodes.fold(visitedNodes) { accVisitedNodes, currentNode ->
                visitor.discover(path, currentNode)

                if (currentNode in accVisitedNodes) {
                    accVisitedNodes
                } else {
                    visitor.visitPreOrder(path, currentNode)
                    val result = traverse(
                        path + currentNode,
                        edges[currentNode] ?: emptySet(),
                        accVisitedNodes + currentNode
                    )
                    visitor.visitPostOrder(path, currentNode)
                    result
                }
            }

        traverse(emptyList(), nodes, emptySet())
    }
}

private interface Visitor<T> {
    fun discover(path: List<T>, node: T): Unit
    fun visitPreOrder(path: List<T>, node: T): Unit
    fun visitPostOrder(path: List<T>, node: T): Unit
}

private class CycleDetectingVisitor<T> : Visitor<T> {
    private val traversingNodes = mutableSetOf<T>()
    override fun discover(path: List<T>, node: T) {
        if (node in traversingNodes) {
            val cycle = path.dropWhile { it != node } + node
            throw CyclicGraphException(cycle)
        }
    }

    override fun visitPreOrder(path: List<T>, node: T) {
        traversingNodes += node
    }

    override fun visitPostOrder(path: List<T>, node: T) {
        traversingNodes -= node
    }
}

private class PostOrderCollectingVisitor<T> : Visitor<T> {
    val nodes: MutableList<T> = mutableListOf()
    override fun discover(path: List<T>, node: T) {}
    override fun visitPreOrder(path: List<T>, node: T) {}

    override fun visitPostOrder(path: List<T>, node: T) {
        nodes += node
    }
}

