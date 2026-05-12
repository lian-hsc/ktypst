package me.lian.hsc.ktypst.structures.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible

internal class NotNullable<T : Any>(
    val reference: KMutableProperty<T?>,
    val singleSet: Boolean = false
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val actualPropertyValue = reference.getter.also { if (!it.isAccessible) it.isAccessible = true }.call()
        return checkNotNull(actualPropertyValue) { "${property.name} is not defined." }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (singleSet) {
            val currentValue = reference.getter.also { if (!it.isAccessible) it.isAccessible = true }.call()
            check(currentValue == null) { "${property.name} has already been set" }
        }

        reference.setter.also { if (!it.isAccessible) it.isAccessible = true }.call(value)
    }

}
