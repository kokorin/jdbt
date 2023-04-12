package com.github.kokorin.jdbt.exception

class CyclicGraphException(val cycle: List<Any?>) :
    RuntimeException("Graph cycle: ${cycle.joinToString(" -> ")}")