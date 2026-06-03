package me.lian.hsc.ktypst.structures.tree.b

fun emptyBTree(depth: Int, keys: Int = 3) = btree(renderEngine = EmptyContentBTreeRenderEngine) {
    emptyBTree(depth, startIndex = 1, currentDepth = 1, keys = keys)
}

fun BTreeDsl.emptyBTree(
    depth: Int,
    startIndex: Int = 1,
    currentDepth: Int = 1,
    keys: Int = 3
) {
    for (i in 0..<keys) {
        val index = startIndex + i

        if (currentDepth < depth) {
            child {
                emptyBTree(
                    depth = depth,
                    startIndex = (index - 1) * (keys + 1) + 1,
                    currentDepth = currentDepth + 1,
                    keys = keys
                )
            }
        }

        if (i == 0) key("$currentDepth-${startIndex + i}")
        else key("${startIndex + i}")
    }

    if (currentDepth < depth) {
        child {
            emptyBTree(
                depth = depth,
                startIndex = keys * (keys + 1) + 1,
                currentDepth = currentDepth + 1,
                keys = keys
            )
        }
    }
}
