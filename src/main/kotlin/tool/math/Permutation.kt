package tool.math

/* This code is contributed by Devesh Agrawal (and altered by myself) */
private fun combinationUtil(arr: IntArray, data: IntArray, start: Int, end: Int, index: Int, r: Int, allCombinationsList: MutableList<List<Int>>) {
    if (index == r) {
        allCombinationsList.add(data.toList())
        return
    }

    var i = start
    while (i <= end && end - i + 1 >= r - index) {
        data[index] = arr[i]
        combinationUtil(arr, data, i + 1, end, index + 1, r, allCombinationsList)
        i++
    }
}

// return all combinations of split in (split, splitSize)
//   (   split   )
//   ( splitSize )
//
fun getCombinationList(size: Int, splitSize: Int): List<List<Int>> {
    val allCombinationsList = mutableListOf<List<Int>>()
    val data = IntArray(splitSize)
    val arr = IntArray(size){it}
    allCombinationsList.clear()
    combinationUtil(arr, data, 0, size - 1, 0, splitSize, allCombinationsList)
    return allCombinationsList
}

private fun makeList(elements: IntArray): List<Int> {
    return elements.map { it }
}

private fun swap(elements: IntArray, a: Int, b: Int) {
    val tmp = elements[a]
    elements[a] = elements[b]
    elements[b] = tmp
}

// returns all permutations of the given list.
// each 'list' in the returned list of 'list's, conatins one of the permutations.
// be aware of the combinatoral explosion!
//
fun makeAllPermutations(elements: List<Int>): List<List<Int>> {
    return makeAllPermutations(elements.size, elements.toIntArray())
}

private fun makeAllPermutations(n: Int, elements: IntArray): List<List<Int>> {
    if (n == 1) {
        return listOf(makeList(elements))
    } else {
        val localList = mutableListOf<List<Int>>()
        for (i in 0 until n - 1) {
            localList.addAll(makeAllPermutations(n - 1, elements))
            if (n % 2 == 0) {
                swap(elements, i, n - 1)
            } else {
                swap(elements, 0, n - 1)
            }
        }
        localList.addAll(makeAllPermutations(n - 1, elements))
        return localList
    }
}
