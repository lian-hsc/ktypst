package me.lian.hsc.ktypst.structures.tree.simple

fun emptyTree(depth: Int, order: Int = 2) = tree {
    key = "1-1"
    content = ""

    if (depth > 1) {
        emptyTree(depth, startIndex = 1, currentDepth = 2, order = order)
    }
}

fun <Type : TreeDsl<Type>> TreeDsl<Type>.emptyTree(
    depth: Int,
    startIndex: Int = 1,
    currentDepth: Int = 1,
    order: Int = 2
) {
    for (i in 0..<order) {
        val index = startIndex + i

        child("$currentDepth-${startIndex + i}") {
            content = ""

            if (currentDepth < depth) {
                emptyTree(
                    depth = depth,
                    startIndex = (index - 1) * order + 1,
                    currentDepth = currentDepth + 1,
                    order = order
                )
            }
        }
    }
}
