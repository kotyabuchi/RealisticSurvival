package com.github.kotyabuchi.RealisticSurvival.Utility

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import kotlin.math.min

fun PlayerInventory.addItemOrDrop(player: Player, vararg items: ItemStack): Boolean {
    val addedItems = mutableMapOf<Int, ItemStack>()
    val contents = this.storageContents

    items.forEach { item ->
        var amount = item.amount

        for ((index, itemStack) in contents.withIndex()) {
            if (itemStack == null) {
                this.setItem(index, item)
                addedItems[index] = item
                amount = 0
            } else {
                val addItemClone = item.clone()
                val checkItemClone = itemStack.clone()
                addItemClone.amount = 1
                checkItemClone.amount = 1
                if (addItemClone == checkItemClone) {
                    val canAddAmount = min(amount, itemStack.maxStackSize - itemStack.amount)
                    itemStack.amount += canAddAmount
                    addItemClone.amount = canAddAmount
                    addedItems[index] = addItemClone
                    amount -= canAddAmount
                }
            }
            if (amount <= 0) break
        }

        if (amount > 0) {
            item.amount = amount
            player.world.dropItem(player.location, item)
        }
    }
    return addedItems.isNotEmpty()
}

fun Inventory.findFirst(searchItem: ItemStack): FindItemResult? {
    this.contents.forEachIndexed { index, itemStack ->
        if (itemStack != null && searchItem.isSimilar(itemStack)) return FindItemResult(index, itemStack)
    }
    return null
}

fun Inventory.findLast(searchItem: ItemStack): FindItemResult? {
    this.contents.reversed().forEachIndexed { index, itemStack ->
        if (itemStack != null && searchItem.isSimilar(itemStack)) return FindItemResult(index, itemStack)
    }
    return null
}

fun Inventory.findAll(searchItem: ItemStack): List<FindItemResult> {
    val result = mutableListOf<FindItemResult>()
    this.contents.forEachIndexed { index, itemStack ->
        if (itemStack != null && searchItem.isSimilar(itemStack)) result.add(FindItemResult(index, itemStack))
    }
    return result
}

fun Inventory.getFirstItem(): FindItemResult? {
    this.contents.forEachIndexed { index, itemStack ->
        if (itemStack != null && !itemStack.type.isAir) return FindItemResult(index, itemStack)
    }
    return null
}

fun Inventory.consume(itemStack: ItemStack, amount: Int = itemStack.amount, reverse: Boolean = false): Boolean {
    var result = false
    val contents = this.storageContents.clone()
    if (reverse) contents.reverse()

    var foundAmount = 0
    for (item in contents) {
        if (itemStack.isSimilar(item)) {
            val useAmount = min(item.amount, amount - foundAmount)
            foundAmount += useAmount
            item.amount -= useAmount
        }
        if (foundAmount >= amount) {
            result = true
            break
        }
    }
    if (result) {
        this.storageContents = contents
    }
    return result
}

fun Inventory.findItemAmount(itemStack: ItemStack): Int {
    var foundAmount = 0
    for (item in contents) {
        if (itemStack.isSimilar(item)) {
            foundAmount += item.amount
        }
    }
    return foundAmount
}

data class FindItemResult(val slot: Int, val itemStack: ItemStack)