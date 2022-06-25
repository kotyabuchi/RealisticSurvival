package com.github.kotyabuchi.RealisticSurvival.CustomPersistentDataType

import org.apache.commons.lang3.SerializationUtils
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

object PersistentDataTypeBoolean: PersistentDataType<ByteArray, Boolean> {
    override fun getPrimitiveType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    override fun getComplexType(): Class<Boolean> {
        return Boolean::class.java
    }

    override fun toPrimitive(complex: Boolean, context: PersistentDataAdapterContext): ByteArray {
        return SerializationUtils.serialize(complex)
    }

    override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): Boolean {
        try {
            val inputStream = ByteArrayInputStream(primitive)
            val objectInputStream = ObjectInputStream(inputStream)
            return objectInputStream.readObject() as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}