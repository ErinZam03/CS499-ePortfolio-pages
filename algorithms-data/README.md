# Algorithms and Data Structures Enhancement

## Artifact: InventoryApp – List Filtering and Sorting

This enhancement focuses on implementing efficient list filtering, sorting, and display updates using algorithmic techniques and optimized data structure practices.

### What’s Included
- `InventoryAdapter.java`: Upgraded to use `ListAdapter` and `DiffUtil.ItemCallback` for optimized UI updates
- Filtering and sorting are performed in a single pass using Java Streams
- Items are sorted alphabetically on load and whenever filtered

### Enhancement Goals
- Add category filtering and name-based search
- Apply alphabetical and stock-level sorting with Comparator logic
- Use `DiffUtil.ItemCallback` to update only changed items, improving performance
- Ensure smooth user experience and instant feedback on edits

### Algorithm & Data Structure Concepts Used
- Stream filtering with multiple constraints
- Comparator-based sorting
- `DiffUtil.ItemCallback` for efficient RecyclerView diffing
